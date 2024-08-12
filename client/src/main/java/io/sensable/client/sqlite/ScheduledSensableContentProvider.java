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
 * Is responsible for managing and providing access to scheduled sensables data through
 * ContentResolver API. It handles CRUD operations (Create, Read, Update, Delete) on
 * the ScheduledSensablesTable in a SQLite database.
 */
public class ScheduledSensableContentProvider extends ContentProvider {

    private static final String TAG = SensableContentProvider.class.getSimpleName();
    private static final String AUTHORITY = "io.sensable.client.scheduledcontentprovider";

    // Used for the UriMacher
    private static final int SENDERS = 10;
    private static final int SENDER_ID = 20;
    private static final int PENDING = 30;

    private static final String BASE_PATH = "senders";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, SENDERS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/pending", PENDING);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/*", SENDER_ID);
    }

    private SensableDatabaseHelper dbHelper;

    /**
     * Initializes a database helper and logs its details to the debug log. The function
     * returns false indicating that it does not create any UI elements.
     *
     * @returns a string representation of the `SensibleDatabaseHelper` object.
     */
    @Override
    public boolean onCreate() {
        dbHelper = SensableDatabaseHelper.getHelper(getContext());
        Log.d(TAG, dbHelper.toString());
        return false;
    }

    /**
     * Returns a `Cursor` object that represents data retrieved from the database. It
     * builds a SQL query based on the provided parameters (URI, projection, selection,
     * etc.) and executes it against a SQLite database. The query results are then returned
     * as a `Cursor`.
     *
     * @param uri URI of the data to be retrieved from the database and is used to determine
     * which specific data to query based on the matched URI type.
     *
     * Matches a URI to an integer value through the `sURIMatcher`, resulting in the type
     * of query (SENDERS, SENDER_ID or PENDING).
     *
     * @param projection list of columns to include in the query results.
     *
     * The array contains column names from the table to be returned in the result set.
     * It may contain "*" for all columns or specific column names. If null is provided,
     * all columns will be included.
     *
     * @param selection where clause of the SQL query that is used to filter the results,
     * allowing for more specific data retrieval based on specific conditions.
     *
     * @param selectionArgs values that are replaced into the selection string to filter
     * the result set, allowing for more secure and flexible query execution.
     *
     * It is an array of strings representing the values to be inserted into the selection
     * clause's parameters. The length of this array must match the number of '?' in the
     * selection string.
     *
     * @param sortOrder sorting criteria for the query results, allowing the database to
     * order the output according to specific columns or expressions.
     *
     * @returns a Cursor object that contains data from a SQLite database.
     *
     * The returned output is a Cursor object that contains data from the ScheduledSensablesTable
     * in the database. It provides methods for traversing and processing the query
     * results. The cursor includes sorting of the data based on the provided sortOrder
     * parameter.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ScheduledSensablesTable.NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case SENDERS:
                break;
            case SENDER_ID:
                queryBuilder.appendWhere(ScheduledSensablesTable.COLUMN_SENSABLE_ID + "='" + uri.getLastPathSegment() + "'");
                break;
            case PENDING:
                queryBuilder.appendWhere(ScheduledSensablesTable.COLUMN_PENDING + "=1");
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
     * Overrides a method from a superclass and returns a `String`. It takes a `Uri` as
     * input, but always returns `null`, indicating that it does not provide any type
     * information about the given Uri.
     *
     * @param uri URI (Uniform Resource Identifier) of the resource being requested, which
     * is passed to the function for processing.
     *
     * @returns a null string.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * Inserts a new record into the database based on the provided URI and ContentValues.
     * It matches the URI type to determine the table to insert into, and returns the Uri
     * of the newly inserted item.
     *
     * @param uri URI of the content provider for which data is being inserted and helps
     * determine the table to be updated based on its match with the `sURIMatcher`.
     *
     * Matched by sURIMatcher with an integer value representing the URI type.
     *
     * @param values ContentValues object that contains the data to be inserted into the
     * database table specified by the URI.
     *
     * Contains key-value pairs; each key-value pair represents a column-value pair for
     * the table to be inserted into. The values are stored as ContentValues objects which
     * can hold multiple rows' worth of data.
     *
     * @returns a parsed `Uri` object with an inserted record's ID.
     *
     * The output is an object of type Uri, which represents a unique identifier for a
     * data row in the database. This Uri contains the authority, path and query parameters.
     * In this case, the Uri is constructed by parsing the CONTENT_URI and appending the
     * inserted id to it.
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
            case SENDERS:
                id = sqlDB.insert(ScheduledSensablesTable.NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(CONTENT_URI + "/" + id);
    }

    /**
     * Deletes data from a database based on the provided selection criteria. It handles
     * two types of URIs: SENDERS and SENDER_ID, which correspond to different deletion
     * scenarios. The function returns the number of rows deleted and notifies the content
     * resolver to update the affected URI.
     *
     * @param uri URI of the content provider and is used to determine which table or
     * record in the database should be deleted based on its match with the defined
     * patterns in the `sURIMatcher`.
     *
     * Matched against a regular expression pattern using `sURIMatcher.match(uri)` to
     * determine its type, which can be either SENDERS or SENDER_ID. The matched URI type
     * is then used for further processing.
     *
     * @param selection WHERE clause to be used for the deletion operation on the database
     * table, allowing for conditional deletions based on specific criteria.
     *
     * @param selectionArgs 0-indexed array of values to bind as the replacement for each
     * `?` found in the `selection` string.
     *
     * An array of strings containing values to replace the `?` wildcards in the selection
     * clause. If there are no wildcards, this array may be empty or null. The number and
     * types of values in the array must match the number and types expected by the SQL
     * statement.
     *
     * @returns the number of rows deleted from the database.
     *
     * The function returns an integer value representing the number of rows deleted from
     * the database table.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case SENDERS:
                rowsDeleted = sqlDB.delete(ScheduledSensablesTable.NAME, selection,
                        selectionArgs);
                break;
            case SENDER_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(ScheduledSensablesTable.NAME,
                            ScheduledSensablesTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(ScheduledSensablesTable.NAME, ScheduledSensablesTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    /**
     * Updates data in a SQLite database based on a specified URI and ContentValues. It
     * uses a switch statement to determine the type of update (by sender ID or by all
     * senders) and returns the number of rows updated.
     *
     * @param uri URI of the data to be updated and is used to determine the type of data
     * being updated and the corresponding database table.
     *
     * Matches with either SENDERS or SENDER_ID; last path segment can be an ID for
     * SENDER_ID and is empty otherwise.
     *
     * @param values new column values to be updated in the database table.
     *
     * Contains ContentValues object with key-value pairs. The keys and values are strings.
     * It represents data to be updated in the database.
     *
     * @param selection WHERE clause of an SQL query that filters the data to be updated
     * based on specific conditions.
     *
     * @param selectionArgs values to bind into the selection statement for the SQLite
     * update operation.
     *
     * Array of strings representing the values to be matched against the selection clause.
     * The length of this array should match the number of '?' or ':' in the selection string.
     *
     * @returns an integer representing the number of rows updated.
     *
     * The output is an integer representing the number of rows updated in the database
     * table. This value can be zero indicating no rows were updated or a positive integer
     * denoting the actual count of rows modified.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case SENDERS:
                rowsUpdated = sqlDB.update(ScheduledSensablesTable.NAME, values,
                        selection, selectionArgs);
                break;
            case SENDER_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(ScheduledSensablesTable.NAME, values,
                            ScheduledSensablesTable.COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(ScheduledSensablesTable.NAME, values,
                            ScheduledSensablesTable.COLUMN_ID + "=" + id + " and "
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