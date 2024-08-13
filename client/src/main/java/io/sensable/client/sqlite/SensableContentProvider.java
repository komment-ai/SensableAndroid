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
 * Provides CRUD (Create, Read, Update, Delete) operations for managing sensor data
 * stored in a SQLite database. It uses a URI-based approach to handle requests and
 * supports three types of URIs: SENSABLES, SENSABLE_ID, and unknown. The class extends
 * the ContentProvider abstract class and overrides its methods to implement the
 * desired functionality.
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
     * Initializes a database helper by calling the `getHelper` method and logs its string
     * representation to the debug log. The method returns false, indicating that the
     * creation process was not successful.
     *
     * @returns a boolean value set to `false`.
     */
    @Override
    public boolean onCreate() {
        dbHelper = SensableDatabaseHelper.getHelper(getContext());
        Log.d(TAG, dbHelper.toString());
        return false;
    }

    /**
     * Retrieves data from a SQLite database based on a given URI and query parameters.
     * It constructs an SQL query using a SQLiteQueryBuilder, executes it against the
     * database, and returns the resulting Cursor object. The query can be filtered by
     * sensor ID if the URI specifies an ID.
     *
     * @param uri Uniform Resource Identifier that identifies the specific data requested
     * by the caller, and its type determines whether to query all sensables or retrieve
     * sensable data based on a sensor ID.
     *
     * Matches a URI scheme and path segment with the provided patterns. The result
     * specifies the type of the URI. It can be one of SENSABLES or SENSABLE_ID.
     *
     * @param projection columns to be returned in the resulting Cursor, allowing for
     * selective retrieval of specific data from the database table.
     *
     * array of strings containing column names from which to return results.
     *
     * @param selection WHERE clause of the SQL query, which is used to filter the results
     * based on specific conditions.
     *
     * @param selectionArgs values to be substituted into the SQL selection string,
     * allowing for more dynamic and flexible querying of the database.
     *
     * Arrays of strings containing values to be substituted into the selection and
     * selection args. Contains 0 or more elements corresponding to the number of
     * placeholders in the SQL WHERE clause. Each element is a string value to be inserted
     * as a literal in the SQL statement.
     *
     * @param sortOrder sorting criteria for the query result, allowing the data to be
     * returned in a specific order based on one or more columns.
     *
     * @returns a `Cursor` object.
     *
     * The result is a `Cursor` object that encapsulates a set of rows and columns from
     * the database table SavedSensablesTable with the specified projection, selection,
     * and sort order. The cursor's content URI is notified for changes.
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
     * Returns a null string value for a given URI. The override annotation indicates
     * that this method is specific to the class implementing it, possibly overriding a
     * default implementation from a superclass. This method appears to be incomplete or
     * intentionally returning no result.
     *
     * @param uri Uniform Resource Identifier of the resource being requested, passed to
     * the method for processing.
     *
     * @returns a null value, indicating no type information available for the given Uri.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * Inserts data into a SQLite database based on the provided URI and ContentValues.
     * It matches the URI with a specific table, writes the values to the table, and
     * returns a new Uri containing the ID of the inserted row.
     *
     * @param uri URI of the content provider and determines which table to insert data
     * into based on its match with a predefined pattern.
     *
     * Parsed into uriType by sURIMatcher and matched with predefined constants such as
     * SENSABLES. Uri is expected to be in CONTENT_URI format.
     *
     * @param values key-value pairs of data to be inserted into the SQLite database.
     *
     * Values contains key-value pairs, represented as a map of Strings to objects, which
     * can be any type that implements the Parcelable or ContentValues interface.
     *
     * @returns a Uri that represents the newly inserted row.
     *
     * The returned value is an instance of Uri that represents the newly inserted record
     * in the database. This Uri can be used to retrieve or update the inserted data.
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
     * Deletes data from a SQLite database based on a given Uri and selection criteria.
     * It handles two types of Uris: SENSABLES and SENSABLE_ID, and uses different queries
     * to delete data accordingly. The number of rows deleted is returned and the Content
     * Resolver is notified of changes.
     *
     * @param uri Uniform Resource Identifier of the data to be deleted and is used to
     * determine which database table to access and what specific row to delete, if applicable.
     *
     * The `uri` represents a path to a database table within a specific SQLite database.
     * It matches one of two types: SENSABLES or SENSABLE_ID. Depending on its type, it
     * either corresponds to a table name or an ID value.
     *
     * @param selection WHERE clause of an SQL query to filter which records to delete
     * from the database based on specific conditions.
     *
     * @param selectionArgs 0-based array of values that are bound to the question mark
     * placeholders in the selection statement for the database operation, facilitating
     * dynamic query execution.
     *
     * Array-like structure containing arguments to replace `%?` placeholders in the
     * selection string.
     *
     * @returns the number of deleted database rows.
     *
     * The function returns an integer value representing the number of rows deleted from
     * the database.
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
     * Updates data in a database based on the provided URI and ContentValues. It matches
     * the URI with a predefined set of cases and then performs an update operation
     * accordingly, either by matching the sensor ID or using the selection clause.
     *
     * @param uri Uniform Resource Identifier of the data to be updated, which is used
     * to determine the specific table or row to update within the database.
     *
     * Matched with a specific URI type through `sURIMatcher.match(uri)`, indicating its
     * purpose and functionality. It is then checked for two distinct types - `SENSABLES`
     * or `SENSABLE_ID`.
     *
     * @param values ContentValues object containing the new column values to be updated
     * in the database.
     *
     * It is a ContentValues object, containing key-value pairs representing data to be
     * updated in the database. The keys and values are represented as Strings. The
     * function does not inspect or manipulate these key-value pairs further.
     *
     * @param selection WHERE clause of the SQL query used to filter the rows updated in
     * the database.
     *
     * @param selectionArgs values to be substituted into the SQL selection clause,
     * allowing for secure and efficient query execution.
     *
     * Array of strings used to bind arguments for the SQL query. Its elements will be
     * replaced with actual values in the SQL query.
     *
     * @returns an integer indicating the number of rows updated.
     *
     * The output is an integer value representing the number of rows updated in the
     * database as a result of the update operation. This integer value indicates the
     * success or failure of the update process.
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