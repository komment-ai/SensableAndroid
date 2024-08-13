package io.sensable.client.sqlite;

/**
 * Created by madine on 03/07/14.
 */

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Is a content provider that manages data in a SQLite database. It provides CRUD
 * (Create, Read, Update, Delete) operations for data retrieval and manipulation
 * through the Android ContentResolver API. The class handles URIs, columns, and
 * selection criteria to interact with the database efficiently.
 */
public class SensableContentProvider extends ContentProvider {

    private static final String TAG = SensableContentProvider.class.getSimpleName();
    private static final String AUTHORITY = "io.sensable.client.contentprovider";

    // Used for the UriMacher
    private static final int SENSABLES = 10;
    private static final int SENSABLE_ID = 20;

    private static final String BASE_PATH = "sensables";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, SENSABLES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/*", SENSABLE_ID);
    }

    private SensableDatabaseHelper dbHelper;

    /**
     * Initializes a database helper object and logs its string representation to the
     * debug log. It also returns a boolean value of `false`.
     *
     * @returns a string representation of the database helper object.
     */
    @Override
    public boolean onCreate() {
        dbHelper = SensableDatabaseHelper.getHelper(getContext());
        Log.d(TAG, dbHelper.toString());
        return false;
    }

    /**
     * Executes a SQL query on a SQLite database and returns a Cursor object containing
     * the results. It handles different types of URIs, including the base URI for all
     * sensables and a specific sensor ID URI, and logs the executed SQL statement.
     *
     * @param uri URI of the query operation and is used to determine the specific table
     * or record to retrieve data from based on its path segment.
     *
     * Matched to one of the cases SENSABLES or SENSABLE_ID using the URI matcher
     * sURIMatcher. The `getLastPathSegment()` returns a part of the path of the URI and
     * is used as a selection argument in case SENSABLE_ID.
     *
     * @param projection list of columns that are to be returned in the result set of the
     * query, which can be used to select specific fields from the database.
     *
     * @param selection WHERE clause of the SQL query that is used to filter the results,
     * allowing for conditional selection of specific rows from the database table.
     *
     * @param selectionArgs values to bind into the SQL selection string for the query,
     * which can contain placeholders such as '?' or ':name' that are replaced by these
     * values.
     *
     * * It is an array of strings.
     * * The length of this array matches the number of question marks in the selection
     * string.
     *
     * @param sortOrder constraint used to sort the query results, specifying how the
     * rows should be ordered and returned from the database.
     *
     * @returns a SQLite Cursor containing the query results.
     *
     * The output is a Cursor object. It contains query results as rows, each represented
     * by a RowSet. Each row can be accessed using the moveToNext() method to fetch the
     * data.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(SavedSensablesTable.NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case SENSABLES:
                break;
            case SENSABLE_ID:
                queryBuilder.appendWhere(SavedSensablesTable.COLUMN_SENSOR_ID + "='" + uri.getLastPathSegment() + "'");
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = queryBuilder.buildQuery(projection, selection, null, null, null, sortOrder); // API 11 and later
        Log.d(TAG, sql);
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Returns a string value representing the type of a given URI. It overrides the
     * default implementation to provide custom URI type determination logic. The returned
     * value is always `null`.
     *
     * @param uri Uri object that contains the authority and path of the requested content.
     *
     * @returns a null value as it always returns `null`.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * Inserts a new row into the database based on the provided URI and ContentValues.
     * It matches the URI to determine the table to insert into, logs the insertion, and
     * notifies the content resolver of the change.
     *
     * @param uri URI of the content provider for which data is being inserted and is
     * used to determine the specific table to insert into.
     *
     * Matches to a specific URI type through `sURIMatcher.match(uri)`. The matched URI
     * type is an integer represented by `uriType`.
     *
     * @param values key-value pairs to be inserted into the database table associated
     * with the specified URI.
     *
     * It is an instance of `ContentValues`, which contains key-value pairs representing
     * columns and their corresponding values for insertion into the database. The keys
     * are column names as strings, while the values can be any type of data that can be
     * stored in a SQLite database.
     *
     * @returns a `Uri` object with an ID of the newly inserted row.
     *
     * The output is an instance of `Uri`, which represents a uniform resource identifier
     * (URI). It contains a parsed URI string and has methods to extract its components.
     * The Uri object identifies a specific record in the database after insertion.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "Inserting: " + values.toString());
        int uriType = sURIMatcher.match(uri);
        if (dbHelper == null) {
            dbHelper = SensableDatabaseHelper.getHelper(getContext());
        }

        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case SENSABLES:
                id = sqlDB.insert(SavedSensablesTable.NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(CONTENT_URI + "/" + id);
    }

    /**
     * Deletes data from a SQLite database based on a provided selection and selection
     * arguments. It supports two types of URIs: SENSABLES for deleting multiple records
     * and SENSABLE_ID for deleting a single record by its ID, or multiple records matching
     * specific conditions.
     *
     * @param uri URI of the data being deleted and is used to determine which table in
     * the database to delete from based on the matching result with a predefined set of
     * URI patterns.
     *
     * Matches an Uri path to identify the type of data being deleted, such as SENSABLES
     * or SENSABLE_ID.
     *
     * @param selection WHERE clause of the SQL query to be executed on the SQLite database
     * for deletion operations.
     *
     * @param selectionArgs 0-indexed array of values to bind as placeholders within the
     * selection string.
     *
     * Array containing arguments to be replaced within the selection clause.
     *
     * @returns the number of rows deleted from the database.
     *
     * The output is an integer representing the number of rows deleted from the database,
     * which defaults to 0 if no deletions occur. This count may be negative if the delete
     * operation throws a SQLException. The output value is then passed back to the caller
     * through the `return` statement.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case SENSABLES:
                rowsDeleted = sqlDB.delete(SavedSensablesTable.NAME, selection,
                        selectionArgs);
                break;
            case SENSABLE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(SavedSensablesTable.NAME,
                            SavedSensablesTable.COLUMN_SENSOR_ID + "='" + id + "'", null);
                } else {
                    rowsDeleted = sqlDB.delete(SavedSensablesTable.NAME, SavedSensablesTable.COLUMN_SENSOR_ID + "='" + id + "' and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    /**
     * Updates data in a SQLite database based on a given URI and ContentValues object.
     * It handles different types of URIs by selecting specific records to update and
     * returns the number of updated rows.
     *
     * @param uri URI of the data being updated and is used to determine which table in
     * the database should be updated and what conditions should be applied for the update
     * operation.
     *
     * Matches with URI type SENSABLES or SENSABLE_ID using the `sURIMatcher`.
     *
     * @param values Map of column names and new values to be inserted into or updated
     * in the database table.
     *
     * Contains key-value pairs to be updated in the database; each value is an object
     * that may contain more complex data structures.
     *
     * @param selection WHERE clause for the SQL update operation, allowing the function
     * to filter and update specific rows in the database based on certain conditions.
     *
     * @param selectionArgs values to be bound to the question mark placeholders in the
     * selection string.
     *
     * Array of String values representing the actual selection arguments. It may be empty
     * or contain one or more strings.
     *
     * @returns an integer representing the number of rows updated.
     *
     * The function returns an integer value representing the number of rows updated in
     * the database. It also notifies the content resolver to notify any registered
     * listeners that the data has changed. The output is a count of affected rows.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case SENSABLES:
                rowsUpdated = sqlDB.update(SavedSensablesTable.NAME, values,
                        selection, selectionArgs);
                break;
            case SENSABLE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(SavedSensablesTable.NAME, values,
                            SavedSensablesTable.COLUMN_SENSOR_ID + "='" + id + "'", null);
                } else {
                    rowsUpdated = sqlDB.update(SavedSensablesTable.NAME, values,
                            SavedSensablesTable.COLUMN_SENSOR_ID + "='" + id + "' and "
                                    + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

}