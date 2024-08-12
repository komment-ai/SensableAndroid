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
 * Provides content management for a SQLite database that stores sensable data. It
 * defines four main functions: query, insert, delete, and update, which allow clients
 * to interact with the database. These functions use a Uri to determine which table
 * in the database to access and perform CRUD operations accordingly.
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
     * Initializes a database helper instance for the current context and logs its string
     * representation. It then returns `false`. This function is likely part of an Android
     * activity's lifecycle, specifically called during initialization or creation.
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
     * Retrieves data from a SQLite database based on the given URI, projection, selection
     * criteria, and sort order. It handles two types of URIs: SENSABLES and SENSABLE_ID,
     * which correspond to retrieving all or specific sensor data, respectively.
     *
     * @param uri URI of the data to be queried, which determines whether the query is
     * for all saved sensables or for a specific sensor ID.
     *
     * Matched by SQLiteQueryBuilder with URI matcher sURIMatcher;
     * URI type (SENSABLES or SENSABLE_ID) is obtained through matching.
     *
     * @param projection list of columns to be returned by the query, allowing for selective
     * retrieval of specific data from the database table.
     *
     * Extracts an array of column names from the table. It contains zero or more strings
     * that specify the columns to include in the result set.
     *
     * @param selection WHERE clause of the SQL query that is used to filter the results
     * returned by the database query.
     *
     * @param selectionArgs values to be substituted into the selection string for the
     * query, allowing for dynamic filtering and search criteria.
     *
     * Array of strings with values to replace placeholders in selection SQL statement.
     * May be null if no arguments.
     *
     * @param sortOrder query sort order, which is used to specify how the result set
     * should be sorted when it is returned by the database.
     *
     * @returns a SQLite database cursor containing selected data from the SavedSensablesTable.
     *
     * Returns a Cursor object that contains data queried from the database table SavedSensablesTable.
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
     * Returns a string representing the type of a given Uri. The function overrides the
     * default implementation and currently always returns `null`. It does not appear to
     * utilize the provided Uri parameter.
     *
     * @param uri URI of the resource being requested, and its value is passed to the
     * function as an argument.
     *
     * @returns a null value of type `String`.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * Inserts a new row into the database based on the provided URI and ContentValues.
     * It matches the URI with predefined constants to determine the table to insert data
     * into, then uses a content provider to notify listeners of the change and returns
     * the URI of the newly inserted item.
     *
     * @param uri URI of the content provider being accessed and is used to determine
     * which table in the database to insert into based on its type, matched against a
     * set of predefined constants.
     *
     * Matched with sURIMatcher, the URI type is identified as SENSABLES or unknown. Uri
     * contains information about database table and its content. The Uri path represents
     * a specific data record in the specified table.
     *
     * @param values key-value pairs to be inserted into the database table specified by
     * the URI.
     *
     * Values is an instance of `ContentValues`, a container that holds key-value pairs.
     * It contains data to be inserted into the database. The keys and values are obtained
     * from its string representations using `toString()`.
     *
     * @returns a URI representing the newly inserted record.
     *
     * The output is an instance of Uri type which is parsed from the CONTENT_URI
     * concatenated with the inserted record's id. It represents the URI for the newly
     * inserted data and can be used to access or retrieve that data later.
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
     * Deletes records from a SQLite database based on provided selection and selection
     * arguments for specified URIs. It returns the number of deleted rows. If URI is not
     * recognized, it throws an exception. After deletion, it notifies registered observers
     * that data has changed.
     *
     * @param uri Uniform Resource Identifier (URI) of the data to be deleted and is used
     * to determine which table in the database to delete from and how to construct the
     * SQL deletion query.
     *
     * Matches the URI against various patterns using `sURIMatcher.match(uri)` to identify
     * its type. The matched type is stored in `uriType`.
     *
     * @param selection SQL WHERE clause for the deletion operation, allowing for filtering
     * of the rows to be deleted based on specific conditions.
     *
     * @param selectionArgs values to be substituted into the selection statement when
     * querying or deleting data from the database.
     *
     * An array of strings that replace the ? wildcards in the selection string. Its
     * elements match the corresponding ? wildcard in the selection string.
     *
     * @returns an integer representing the number of rows deleted from the database.
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
     * Updates data in a SQLite database based on a provided URI and selection criteria.
     * It handles two types of URIs: one for updating all sensables and another for
     * updating sensable by ID, then notifies content resolver about the changes.
     *
     * @param uri Uniform Resource Identifier of the data being updated and is used to
     * determine which type of update operation should be performed based on its match
     * with a predefined set of URIs.
     *
     * Matches a URI pattern using sURIMatcher and determines its type through uriType.
     * It can either be SENSABLES or SENSABLE_ID. The uri's last path segment is extracted
     * as an ID if it matches SENSABLE_ID.
     *
     * @param values key-value pairs to be updated or inserted into the database table
     * specified by the URI.
     *
     * The `values` parameter is an instance of `ContentValues`, which contains key-value
     * pairs that represent data to be updated in the database table. These key-value
     * pairs have a String key and a primitive value or an Object value.
     *
     * @param selection WHERE clause of the SQL query used to filter the rows to be updated
     * based on specific conditions specified by the caller.
     *
     * @param selectionArgs arguments to replace the placeholders in the selection string
     * to filter the data that is updated in the database table.
     *
     * Array of strings providing values for selection and selection arguments.
     *
     * @returns the number of rows updated in the database.
     *
     * The returned value is an integer indicating the number of rows updated in the
     * database. It may have a value greater than zero if any rows were successfully
     * updated, or zero if no rows were affected. In case of failure, it throws an exception.
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