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
 * is an implementation of the ContentProvider interface for handling sensor data
 * stored in a SQLite database. It provides methods for querying, inserting, deleting,
 * and updating sensor data based on a given URI. The provider also includes a switch
 * statement to handle different types of URIs (e.g., sensables or sensor IDs).
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
     * in this code establishes a connection to a database and logs details about the
     * helper used for the connection.
     * 
     * @returns a log message indicating the database helper object has been created and
     * its details.
     */
    @Override
    public boolean onCreate() {
        dbHelper = SensableDatabaseHelper.getHelper(getContext());
        Log.d(TAG, dbHelper.toString());
        return false;
    }

    /**
     * generates a SQL query to retrieve data from a SQLite database based on a given URI
     * and selection criteria. It builds the query using a `SQLiteQueryBuilder`, executes
     * it against the database, and returns the resulting cursor object.
     * 
     * @param uri URI that is used to determine which data from the SavedSensables table
     * should be queried.
     * 
     * 	- `sURIMatcher.match(uri)`: This returns an integer that represents the type of
     * the `uri`. The possible values depend on the context in which this function is called.
     * 	- `SavedSensablesTable.NAME`: This is the name of the table where the data for
     * the `cursor` will be retrieved from.
     * 	- `SensorId`: This is a column in the `SavedSensablesTable` that represents the
     * sensor ID associated with the `uri`.
     * 	- `projection`: This is an array of strings that represent the columns to retrieve
     * from the table.
     * 	- `selection`: This is a string that specifies the condition for which rows to
     * include in the result set.
     * 	- `selectionArgs`: This is an array of strings that provide additional values for
     * the selection statement.
     * 	- `sortOrder`: This is a string that represents the sort order of the result set.
     * 
     * The function then builds a query using the `SQLiteQueryBuilder` class, which
     * constructs a SQL query based on the input parameters. The resulting query is then
     * executed against the database using the `query` method of the ` SQLiteDatabase`
     * class, and the results are returned in a `Cursor` object. Finally, the `Cursor`
     * is set as the notification URI for the `ContentResolver` associated with the context
     * of the app.
     * 
     * @param projection 0 or more columns of data to be retrieved from the SavedSensables
     * table in the SQLite database.
     * 
     * 	- `projection`: An array of strings representing the columns to be queried from
     * the SavedSensables table. The order and names of these columns match the column
     * names in the SavedSensables table.
     * 	- `selection`: A string representing the selection criteria for the query, which
     * is used to filter the results based on the sensor ID.
     * 	- `selectionArgs`: An array of strings containing the values to be substituted
     * into the selection expression. These values are used to further filter the results
     * based on the sensor ID.
     * 	- `sortOrder`: A string representing the sort order for the query results, which
     * determines the order in which the results will be returned.
     * 
     * @param selection condition that the query should satisfy, allowing for filtering
     * of the data retrieved from the database.
     * 
     * @param selectionArgs values for the selection criteria in the query, allowing the
     * user to customize the query based on their specific needs.
     * 
     * 	- `selection`: This is the condition applied to the table data based on the user's
     * query requirements. It can be a single field or an array of fields that define
     * which rows in the table satisfy the condition.
     * 	- `selectionArgs`: This is an array of values that are passed as arguments to the
     * ` selection` clause of the query. Each value in the array corresponds to a parameter
     * placeholders in the `SELECT` clause, allowing you to pass custom values for each
     * field in the condition.
     * 
     * In summary, `query` takes input parameters `uri`, `projection`, `selection`, and
     * `selectionArgs`. The `selection` parameter defines the condition applied to the
     * table data, while the `selectionArgs` parameter allows passing custom values for
     * each field in the condition.
     * 
     * @param sortOrder 1-based index of the sort key for the query result, which determines
     * the order of the sorted results returned by the `query()` method.
     * 
     * @returns a Cursor object containing data from the SavedSensables table based on
     * the specified selection and sort order.
     * 
     * 	- `Cursor cursor`: This is the result set returned by the query, which contains
     * the data from the database.
     * 	- `projection`: An array of strings representing the columns to include in the
     * result set.
     * 	- `selection`: A string representing the condition for selecting rows from the
     * database, based on the values in the `SavedSensablesTable`.
     * 	- `selectionArgs`: An array of strings representing the values to use as arguments
     * for the selection condition.
     * 	- `sortOrder`: A string representing the order in which the result set should be
     * sorted.
     * 
     * The function first determines the type of the URI provided, using the `sURIMatcher`
     * class, and then performs the appropriate query based on that type. For example,
     * if the URI is of type `SENSABLES`, the function simply returns the entire contents
     * of the `SavedSensablesTable`. If the URI is of type `SENSABLE_ID`, the function
     * constructs a more specific query by appending a condition to the `SELECT` statement,
     * where the value of the `Sensor ID` column in the table matches the value of the
     * last path segment of the URI. Finally, the function uses the `SQLiteDatabase` class
     * to execute the query and returns the result set as a `Cursor`. The `NotificationUri`
     * property of the `Cursor` is set to the content resolver of the activity, which
     * allows the user to navigate to the corresponding screen in the app.
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
     * returns a `String` representing the type of a given `Uri`. It does not return any
     * value for any `Uri`.
     * 
     * @param uri Android package's URI, which is used to determine the type of the package.
     * 
     * 	- Return value: The type of the Uri object is null.
     * 
     * @returns `null`.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * inserts data into a SQLite database. It takes a `Uri` and `ContentValues` as input,
     * and based on the value of the `uriType` parameter, it determines which table to
     * insert the data into. The function then uses the `SQLiteDatabase` class to insert
     * the data and returns the inserted `Uri`.
     * 
     * @param uri URI of the table where the data is to be inserted, and it is used to
     * determine the appropriate table to insert the data into based on the value of the
     * `uriType` variable.
     * 
     * 	- `sURIMatcher.match(uri)` - returns the match type of the provided `Uri` object
     * 	- `dbHelper` - an instance of `SensableDatabaseHelper`, used to interact with the
     * local database
     * 	- `sqlDB` - a SQLite database handle, obtained from the `dbHelper` instance
     * 	- `id` - a database ID generated by the `insert` function, for the new data
     * inserted into the database.
     * 
     * @param values content of the sensable data that will be inserted into the database.
     * 
     * 	- `SavedSensablesTable.NAME`: The name of the table where the data will be inserted.
     * 	- `null`: No column values are provided for insertion.
     * 	- `values`: A `ContentValues` object containing the data to be inserted into the
     * specified table.
     * 
     * @returns a new URI object representing the inserted data.
     * 
     * 	- `Uri`: The output is a `Uri` object representing the inserted data.
     * 	- `parse(String uriStr)`: This method parses a string representation of a `Uri`,
     * returning a `Uri` object if successful.
     * 	- `/" + id)`: This is the path segment of the `Uri`, representing the ID of the
     * inserted data.
     * 
     * The properties of the output are:
     * 
     * 	- `id`: The unique identifier of the inserted data, represented as a long value.
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
     * in Java is a method that deletes data from a SQLite database based on a given URI
     * and selection criteria. It retrieves the type of the URI, deletes data from the
     * appropriate table, and notifies the content resolver of any changes made to the data.
     * 
     * @param uri URI of the data that needs to be deleted from the database.
     * 
     * 	- `sURIMatcher.match(uri)` returns the type of URI that the given `uri` represents.
     * This information is not provided directly in the input.
     * 	- `dbHelper.getWritableDatabase()` provides access to a SQLite database.
     * 	- `int rowsDeleted = 0;` indicates the number of rows that will be deleted in the
     * next steps of the function.
     * 	- `switch (uriType)` defines the logic for deleting rows based on the type of URI
     * that was matched by `sURIMatcher`. The cases in the switch are:
     * 	+ `SENSABLES`: Deletes rows from the `SavedSensablesTable` with the matching
     * sensor ID.
     * 	+ `SENSABLE_ID`: Deletes rows from the `SavedSensablesTable` where the `Sensor
     * ID` column matches the value in the `uri`. If the `selection` parameter is empty,
     * the entire row will be deleted. Otherwise, only rows that match both the `selection`
     * and the `Sensor ID` columns will be deleted.
     * 	+ `default`: Throws an `IllegalArgumentException` if the given `uri` does not
     * have a known type.
     * 	- `getContext().getContentResolver().notifyChange(uri, null)` informs any observers
     * of the change to the database.
     * 
     * @param selection condition for selecting which rows to delete from the SavedSensablesTable
     * based on the UriType.
     * 
     * @param selectionArgs 0-length array of selection arguments that are used to further
     * filter the data to be deleted based on the selected column(s) and values.
     * 
     * 	- `selectionArgs`: An array of String objects containing the selection criteria
     * for the deletion operation. The length of this array must match the number of
     * selection columns in the database table.
     * 
     * The switch statement within the function handles different types of URIs based on
     * their matching pattern in the `sURIMatcher`. For each matched URI type, a delete
     * operation is performed on the corresponding database table using the `getWritableDatabase()`
     * method to obtain a reference to the SQLite database. The `delete()` method is
     * called on the database object with the selection criteria and arguments passed as
     * parameters. The number of rows deleted is returned as an integer value. Finally,
     * the `ContentResolver` is notified of the change through the `notifyChange()` method.
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
     * updates data in a SQLite database based on a given `Uri`, `values`, `selection`,
     * and `selectionArgs`. It switches between updating different tables (`SENSABLES`
     * and `SENSABLE_ID`) depending on the `uriType` and performs the update operation accordingly.
     * 
     * @param uri URI of the data that is being updated, and its type is used to determine
     * which table to update in the database.
     * 
     * 	- `uri`: This is an instance of `Uri`, which represents a network resource. It
     * has several properties such as `scheme`, `authority`, `path`, and `query`. In this
     * case, the scheme and authority of the `uri` are ignored, leaving only the path and
     * query components.
     * 	- `selection`: This is a string representing a filter for selecting rows to update
     * in the database. It may contain columns and operators used to match or exclude
     * specific rows.
     * 	- `selectionArgs`: This is an array of strings containing additional values to
     * be matched against the selection criteria. These values are used to further refine
     * the row selection process.
     * 	- `uriType`: This is an integer representing the type of `uri` passed as a
     * parameter. It is generated using the `sURIMatcher.match(uri)` method, which maps
     * the `uri` to one of three possible types: `SENSABLES`, `SENSABLE_ID`, or `UNKNOWN`.
     * The type determines which table in the database should be updated.
     * 
     * @param values update values for the specified sensor ID or sensor ID and selection
     * criteria, which are used to update the corresponding entries in the SavedSensables
     * table.
     * 
     * 	- `Uri`: The URI that is being updated.
     * 	- `ContentValues`: A class that contains a set of key-value pairs representing
     * the data to be updated in the database. In this case, it contains the values for
     * the sensors.
     * 	- `Selection`: A string representing the condition or filter under which the
     * update operation will be performed. It is used in conjunction with the `selectionArgs`
     * parameter to specify which rows to update.
     * 	- `SelectionArgs`: An array of strings representing the values of the selection
     * criteria. It is used in conjunction with the `selection` parameter to specify which
     * rows to update.
     * 
     * In the code, the `switch` statement determines which table to update based on the
     * value of `uriType`. The `update` method then updates the relevant table in the
     * database using the `SQLiteDatabase` object and the `ContentValues` object containing
     * the updated data. Finally, the method notifies any observers of the change by
     * calling the `notifyChange` method on the `Context` object.
     * 
     * @param selection condition for updating the data in the database based on the value
     * of the `SensorId` column in the `SavedSensablesTable`.
     * 
     * @param selectionArgs 2nd and 3rd parameters of the `ContentValues` constructor,
     * which are used to specify the values for the selection criteria when updating the
     * database with the `update()` method.
     * 
     * 	- `selectionArgs`: A string array representing the arguments for the `selection`
     * clause in the SQL query. It may contain any number of elements, each representing
     * a separate argument. The elements of this array are used to construct the `selection`
     * clause, separated by the `AND` operator. For example, if `selectionArgs` contains
     * the string `"key1='value1'", "key2='value2'"` as its only element, the `selection`
     * clause will be constructed as `"SELECT * FROM table_name WHERE key1='value1' AND
     * key2='value2'`.
     * 	- `getContext()`: A method of the enclosing class that returns a reference to the
     * context object associated with the current thread. This method is used to notify
     * change listeners in the UI thread when an update occurs.
     * 
     * @returns the number of rows updated in the database.
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