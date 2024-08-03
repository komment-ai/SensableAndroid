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
 * is responsible for managing data related to scheduled sensables. It provides
 * functions for querying, inserting, updating, and deleting data in the database.
 * The provider also handles notifications of changes to the data.
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
     * retrieves a database helper instance and logs its details to the console.
     * 
     * @returns a message indicating the successful creation of a database helper object
     * and its associated log entry.
     */
    @Override
    public boolean onCreate() {
        dbHelper = SensableDatabaseHelper.getHelper(getContext());
        Log.d(TAG, dbHelper.toString());
        return false;
    }

    /**
     * queries the scheduled sensables table based on a given URI and filters the results
     * using a selection clause. It returns a cursor object representing the result set.
     * 
     * @param uri URI that the query will be executed for, and it is used to determine
     * which table to query and which rows to retrieve.
     * 
     * 	- `sURIMatcher.match(uri)` returns an integer value that identifies the URI type,
     * which is used to switch between different queries.
     * 	- `ScheduledSensablesTable.NAME` refers to the name of the table in the database
     * where scheduled sensibles are stored.
     * 	- `ScheduledSensablesTable.COLUMN_SENSABLE_ID` and `ScheduledSensablesTable.COLUMN_PENDING`
     * refer to specific columns in the `ScheduledSensablesTable`.
     * 	- `projection` is an array of strings that specify which columns from the table
     * are needed in the cursor returned by the function.
     * 	- `selection` is a string that specifies a condition for filtering the rows in
     * the table, based on the values in the specified columns.
     * 	- `selectionArgs` is an array of strings that contain the actual values to be
     * used in the selection clause.
     * 	- `sortOrder` is a string that specifies the sort order of the results returned
     * by the function.
     * 
     * @param projection list of columns to be returned by the query.
     * 
     * 	- `ScheduledSensablesTable.NAME`: The table name where the data is stored.
     * 	- `COLUMN_SENSABLE_ID`: The column name for the sensable ID.
     * 	- `COLUMN_PENDING`: The column name for the pending status.
     * 	- `selection`: An optional selection clause that filters the rows based on a condition.
     * 	- `selectionArgs`: An array of selection arguments that are used in the selection
     * clause.
     * 	- `sortOrder`: The sort order of the query results, which determines the order
     * in which the results are returned.
     * 
     * @param selection condition for which rows to retrieve from the database, and it
     * is appended to the WHERE clause of the SQL query built by the `SQLiteQueryBuilder`.
     * 
     * @param selectionArgs values that are used to modify the selection clause of the
     * SQL query, allowing for more complex filtering of data based on the value of the
     * `uri` parameter.
     * 
     * 	- `selectionArgs`: A string array containing the arguments for the SQL query's
     * WHERE clause. The values in this array will be used as the parameters in the WHERE
     * clause, separated by commas.
     * 	- `sortOrder`: A string representing the sort order for the returned data. This
     * can be one of the following: "ASC" (ascending) or "DESC" (descending).
     * 
     * @param sortOrder 1-based index of the sort column, which determines the order of
     * the sorted results returned by the query.
     * 
     * @returns a Cursor object containing data from the scheduled sensables table based
     * on the provided Uri and selection criteria.
     * 
     * 	- `cursor`: A Cursor object that represents a result set from the query. It
     * contains information about the rows in the result set, such as the column names
     * and values.
     * 	- `projection`: An array of strings that specify which columns to include in the
     * result set.
     * 	- `selection`: A string that specifies a filter or condition for selecting rows
     * from the table.
     * 	- `selectionArgs`: An array of strings that contain values for the selection criteria.
     * 	- `sortOrder`: A string that specifies the order of the results, such as "ascending"
     * or "descending".
     * 
     * The function first constructs a SQLiteQueryBuilder object to build the query. It
     * then sets the tables to be queried and appends a filter clause based on the `uriType`
     * variable. The filter clause is constructed using the `ScheduledSensablesTable`
     * columns and the value of `uri.getLastPathSegment()`.
     * 
     * Next, the function creates a writable SQLite database instance and builds the query
     * using the query builder. The resulting SQL query is logged to the debug log.
     * Finally, the function returns a Cursor object that represents the result set of
     * the query. The Cursor has a notification URI set to the content resolver of the
     * activity, which allows it to be notified when the data changes.
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
     * returns a `String` representing the type of a given `Uri`. It returns `null` if
     * the `Uri` cannot be determined.
     * 
     * @param uri Android package's Uri object that is passed to the function, providing
     * the information for the getType method to return the appropriate type.
     * 
     * 	- `Uri`: This is the input parameter for this function. It represents a Uri object
     * containing information about a resource on the web or in a specific protocol.
     * 
     * @returns null.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * inserts new data into a database table based on the URI provided. It first determines
     * the type of the URI, then inserts the data into the appropriate table using a
     * SQLiteDatabase. Finally, it notifies the content resolver of the change and returns
     * the inserted ID as a Uri.
     * 
     * @param uri URI to be inserted into the database.
     * 
     * 	- `sURIMatcher.match(uri)`: This method returns the match type of the given `Uri`,
     * which can be one of the constants defined in the `SENSER_URI_MATCHER` class.
     * 	- `dbHelper`: This is an instance of `SensableDatabaseHelper`, which is a subclass
     * of `ContentObserver`. It is used to interact with the database.
     * 	- `sqlDB`: This is an instance of `SQLiteDatabase`, which is used to perform
     * database operations.
     * 	- `ScheduledSensablesTable.NAME`: This is the name of the table in the database
     * where scheduled sensables are stored.
     * 	- `id`: This variable holds the unique identifier of the newly inserted row in
     * the database.
     * 	- `getContext()`: This method returns an instance of `Context`, which provides
     * access to the application's resources and functionality.
     * 	- `CONTENT_URI`: This is a constant that represents the content provider's base
     * URI. It is used to construct the final insertion result.
     * 
     * @param values content of the sensable to be inserted, which contains the data for
     * the specific field types defined in the SQLite database table.
     * 
     * 	- `values`: A `ContentValues` object containing data to be inserted into the
     * database. The values in the object correspond to columns in the database table.
     * 	- `uri`: The original URI of the insertion request, which serves as a reference
     * point for the operation.
     * 	- `dbHelper`: An instance of `SensableDatabaseHelper`, providing access to the
     * underlying database.
     * 	- `sqlDB`: A SQLite Database object representing the writable database for the
     * specified table.
     * 	- `id`: The unique identifier generated by the database upon successful insertion,
     * indicating the newly created row in the table.
     * 
     * @returns a new Uri object representing the inserted data.
     * 
     * 	- `Uri`: The inserted Uri is returned as a `Uri` object.
     * 	- `id`: The unique identifier of the newly inserted row in the database is returned
     * as a long value.
     * 	- `getContext()`: A reference to the current activity's `Context` object is
     * returned, which can be used to access various resources and functionality within
     * the application.
     * 	- `CONTENT_URI`: The content resolver's Uri for the inserted row is returned as
     * a string.
     * 
     * The properties of the output are explained as follows:
     * 
     * 	- `Uri`: The inserted Uri is returned as a `Uri` object, which can be used to
     * represent the newly inserted data in the application.
     * 	- `id`: The unique identifier of the newly inserted row in the database is returned
     * as a long value, which can be used to identify the specific row that was inserted.
     * 	- `getContext()`: A reference to the current activity's `Context` object is
     * returned, which can be used to access various resources and functionality within
     * the application.
     * 	- `CONTENT_URI`: The content resolver's Uri for the inserted row is returned as
     * a string, which can be used to locate the specific row in the database using the
     * content resolver.
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
     * deletes data from a SQLite database based on a given URI and selection criteria.
     * It matches the URI to a specific table name and column(s), then deletes rows based
     * on the selection and selection arguments.
     * 
     * @param uri Android content provider's Uri object that identifies the table and row
     * to be deleted.
     * 
     * 	- `sURIMatcher.match(uri)` - This method returns the type of the URI based on its
     * structure and contents.
     * 	- `ScheduledSensablesTable.NAME` - The name of the table where the data is stored.
     * 	- `ScheduledSensablesTable.COLUMN_ID` - The column in the table that stores the
     * ID of each row.
     * 	- `selection` and `selectionArgs` - These are the selection criteria and any
     * arguments to use when deleting rows from the table.
     * 
     * @param selection condition for which rows to delete from the `ScheduledSensablesTable`.
     * 
     * @param selectionArgs 0th column of the selected rows in the `SQLiteDatabase`, which
     * is used to filter the rows that are deleted based on the selection criteria.
     * 
     * 	- `selectionArgs`: An array of strings containing additional criteria for filtering
     * or sorting the data in the database.
     * 	- `ScheduledSensablesTable.NAME`: The name of the table in the database where the
     * data is stored.
     * 	- `ScheduledSensablesTable.COLUMN_ID`: The column name for the primary key id in
     * the `ScheduledSensablesTable`.
     * 	- `ScheduledSensablesTable.COLUMN_ID + "=" + id`: The SQL syntax for filtering
     * rows based on the value of the `id` column.
     * 
     * @returns the number of rows deleted from the database.
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
     * updates the values in a database table based on the given URI and selection criteria.
     * It returns the number of rows updated.
     * 
     * @param uri URI of the row to be updated in the database.
     * 
     * 	- `sURIMatcher.match(uri)` returns an integer representing the match type of `uri`.
     * 	- `ScheduledSensablesTable.NAME` is the name of the table where data is updated.
     * 	- `selection` and `selectionArgs` are used to filter or update specific rows in
     * the table based on their ID or other attributes.
     * 	- `id` is a column in the table that represents the unique identifier for each row.
     * 
     * @param values update data to be applied to the database.
     * 
     * 	- `ScheduledSensablesTable.NAME`: The table name where the data will be updated.
     * 	- `ScheduledSensablesTable.COLUMN_ID`: The column name for the unique identifier
     * of the sensable.
     * 	- `ScheduledSensablesTable.COLUMN_ID + "=" + id`: The selection criteria for
     * updating the data, where `id` is the unique identifier of the sensable.
     * 	- `selectionArgs`: An array of strings containing additional selection criteria
     * for the update operation.
     * 
     * Note that `values` may contain other properties or attributes depending on how it
     * was deserialized from the input.
     * 
     * @param selection condition that determines which rows to update in the database
     * based on the provided `values`.
     * 
     * @param selectionArgs 2nd argument of the `update()` method and is used to specify
     * additional arguments for the WHERE clause of the SQL query when updating rows in
     * the database based on the specified URI type.
     * 
     * 	- `selectionArgs`: an array of String values that represent the selection criteria
     * for the database query. Each element in the array corresponds to a separate condition
     * in the WHERE clause of the query.
     * 
     * @returns the number of rows updated in a database table based on a given URI and
     * parameters.
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