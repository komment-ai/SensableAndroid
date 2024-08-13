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
 * Provides CRUD (Create, Read, Update, Delete) operations for managing scheduled
 * sensables in a SQLite database. It extends the ContentProvider class and implements
 * the necessary methods to interact with the database. The provider uses a UriMatcher
 * to match incoming URIs and perform the corresponding operations on the database.
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
     * Initializes a database helper object from the context and logs its string
     * representation to the debug log. It then returns `false`, indicating that the
     * activity has not been successfully created.
     *
     * @returns a log message with the string representation of the `SensibleDatabaseHelper`.
     */
    @Override
    public boolean onCreate() {
        dbHelper = SensableDatabaseHelper.getHelper(getContext());
        Log.d(TAG, dbHelper.toString());
        return false;
    }

    /**
     * Executes a SQL query on a SQLite database based on the provided URI and parameters.
     * It retrieves data from the `ScheduledSensablesTable` table, filters it according
     * to the given selection criteria, and returns the result as a `Cursor`.
     *
     * @param uri URI of the data to be queried and is used to determine which specific
     * rows to retrieve from the database based on its path segment, allowing for filtering
     * by sender ID or pending status.
     *
     * Matches a specific URI pattern based on the authority and path. The matched result
     * is stored in `uriType`.
     *
     * @param projection list of columns to include in the query results, which determines
     * what data is returned by the SQLite database.
     *
     * String array containing column names from the database that should be included in
     * the query result.
     *
     * @param selection WHERE clause of the SQL query that filters the data to be retrieved
     * from the database based on specific conditions or constraints.
     *
     * @param selectionArgs values to replace any '?' wildcard characters in the selection
     * string, allowing for flexible query conditions.
     *
     * It is an array of strings that can be used to fill-in '?' placeholders in the
     * selection string. The length of the array must match the number of '?' placeholders.
     *
     * @param sortOrder SQL statement used to sort the query results, allowing for
     * customized sorting of the data returned by the query.
     *
     * @returns a Cursor object containing data from the ScheduledSensablesTable.
     *
     * Cursor contains a query result set for ScheduledSensablesTable based on the specified
     * Uri, projection, selection, and sort order.
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
     * Returns a string representing the type of a given URI. It overrides an abstract
     * method from its superclass, implying it is part of a class implementing a specific
     * interface or abstract class. The function always returns null, suggesting it may
     * not be fully implemented or has no meaningful return value.
     *
     * @param uri Uniform Resource Identifier of the resource being queried, and its value
     * is not utilized within the provided implementation of the `getType` method as it
     * returns null regardless of the URI.
     *
     * @returns a null string.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * Inserts a new row into the database table corresponding to the provided `Uri`. It
     * retrieves the `ContentValues` from the `ContentResolver`, matches the `Uri` with
     * a specific table, and then inserts the values into the matched table.
     *
     * @param uri URI of the data to be inserted into the database, which is matched
     * against the defined URIs to determine the correct table for insertion.
     *
     * @param values key-value pairs to be inserted into the database's ScheduledSensablesTable
     * when matching the URI type to SENDERS.
     *
     * Contains key-value pairs represented as a ContentValues object.
     *
     * @returns a new Uri representing the inserted row with its ID.
     *
     * The output is an instance of Uri class representing the newly inserted data. It
     * contains the CONTENT_URI and id of the inserted data.
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
     * Deletes data from a SQLite database based on a provided Uri and selection criteria.
     * It matches the Uri to determine which table to delete from and executes the deletion
     * query using SQLiteDatabase's `delete` method.
     *
     * @param uri URI of the data to be deleted, which determines the type of deletion
     * operation to perform based on its match with predefined URI patterns.
     *
     * Matches with `sURIMatcher` and returns an integer value representing the type of
     * URI. It can be either SENDERS or SENDER_ID.
     *
     * @param selection SQL selection clause that is used to determine which rows to
     * delete from the database table when executing a DELETE query.
     *
     * @param selectionArgs replacement placeholders for the selection clause and is used
     * to bind actual values from the selection string to replace the placeholders.
     *
     * @returns an integer representing the number of rows deleted.
     *
     * The function returns an integer value representing the number of rows successfully
     * deleted from the database. If no rows are affected by the deletion operation, the
     * function returns zero.
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
     * Updates a specific row or all rows in a database table based on the provided URI
     * and selection criteria. It uses a switch statement to determine which type of
     * update operation is required, either updating all rows matching a selection or
     * updating a single row by its ID.
     *
     * @param uri URI of the data to be updated and is used to determine the type of
     * update operation based on its match with predefined URIs.
     *
     * Matches with `sURIMatcher` to identify the type of URI; contains a path segment
     * for SENDER_ID type URI.
     *
     * @param values ContentValues object containing new column values to be updated in
     * the database.
     *
     * Contains ContentValues object that holds key-value pairs.
     *
     * @param selection WHERE clause for the SQL update query, allowing to specify a
     * condition for which rows to update.
     *
     * @param selectionArgs values to be matched against the selection clause specified
     * by the `selection` parameter in the SQL query executed for updating data.
     *
     * An array of strings that contain values to be inserted into selection. The length
     * of this array must be equal to the number of '?' in the selection string. Each
     * value is bound with a '?' in the SQL query.
     *
     * @returns an integer representing the number of updated rows.
     *
     * The returned value is an integer representing the number of rows updated in the
     * database table. The update operation may involve matching specific conditions
     * specified by the `selection` and `selectionArgs` parameters or a single row with
     * a specific ID.
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