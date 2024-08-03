package io.sensable.client.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import io.sensable.model.Sample;
import io.sensable.model.Sensable;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by madine on 03/07/14.
 */
/**
 * is used to store and manage sensor data in a SQLite database. It has various columns
 * for storing location, sensor ID, sensor type, name, and last sample information.
 * The class also provides methods for serializing and deserializing the sensor data
 * into JSON format for efficient storage and retrieval.
 */
public class SavedSensablesTable {

    public static final String NAME = "saved_sensables";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LOCATION_LATITUDE = "sensable_latitude";
    public static final String COLUMN_LOCATION_LONGITUDE = "sensable_longitude";
    public static final String COLUMN_SENSOR_ID = "sensable_sensor_id";
    public static final String COLUMN_SENSOR_TYPE = "sensable_sensor_type";
    public static final String COLUMN_NAME = "sensable_sensor_name";
    public static final String COLUMN_LAST_SAMPLE = "sensable_last_sample";
    public static final String COLUMN_UNIT = "sensable_unit";

    private static final String DATABASE_CREATE = "create table " + NAME + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_LOCATION_LATITUDE + " real not null, "
            + COLUMN_LOCATION_LONGITUDE + " real not null, "
            + COLUMN_SENSOR_ID + " text unique not null, "
            + COLUMN_SENSOR_TYPE + " text, "
            + COLUMN_NAME + " text, "
            + COLUMN_LAST_SAMPLE + " text, "
            + COLUMN_UNIT + " text not null"
            + ");";


    /**
     * executes a SQL statement to create the database schema when the application starts.
     * 
     * @param database SQLite Database object that is being executed by the function.
     * 
     * 	- `SQLiteDatabase`: The database class that provides methods for managing SQLite
     * databases.
     * 	- `execSQL()`: A method that executes SQL commands on the database.
     * 	- `DATABASE_CREATE`: The SQL command that is executed, which creates the database
     * if it does not already exist.
     */
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    // TODO: make it smarter
    /**
     * drops an existing table named `NAME` and then calls the `onCreate` function to
     * create a new table with the same name.
     * 
     * @param database SQLiteDatabase object that is being upgraded, and it is used to
     * execute SQL commands on the database.
     * 
     * 	- `database`: A SQLiteDatabase object that represents the database to be upgraded.
     * 	- `oldVersion`: An integer representing the previous version number of the database.
     * 	- `newVersion`: An integer representing the current version number of the database.
     * 
     * @param oldVersion previous version of the database, which is used to determine
     * whether an upgrade is necessary.
     * 
     * @param newVersion version of the application that is being upgraded, and it is
     * used to determine whether the database schema needs to be updated or not.
     */
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + NAME);
        onCreate(database);
    }

    /**
     * converts a `Sensable` object into a `ContentValues` instance, which can be used
     * for storing or retrieving data from a SQLite database. It sets values for columns
     * such as `COLUMN_LOCATION_LATITUDE`, `COLUMN_LOCATION_LONGITUDE`, `COLUMN_SENSOR_ID`,
     * `COLUMN_SENSOR_TYPE`, `COLUMN_NAME`, `COLUMN_LAST_SAMPLE`, and `COLUMN_UNIT`.
     * 
     * @param sensable Sensible object that contains information about the sensor, including
     * its location, sensor ID, sensor type, name, last sample, and unit of measurement.
     * 
     * 	- ` COLUMN_LOCATION_LATITUDE`: The latitude value of the location associated with
     * the sensable.
     * 	- `COLUMN_LOCATION_LONGITUDE`: The longitude value of the location associated
     * with the sensable.
     * 	- `COLUMN_SENSOR_ID`: The ID of the sensor that generated the sensable data.
     * 	- `COLUMN_SENSOR_TYPE`: The type of sensor that generated the sensable data (e.g.,
     * accelerometer, gyroscope, etc.).
     * 	- `COLUMN_NAME`: The name of the sensable.
     * 	- `COLUMN_LAST_SAMPLE`: The last sample value of the sensable as a JSON string.
     * 	- `COLUMN_UNIT`: The unit of measurement for the sensable data.
     * 
     * @returns a ContentValues object containing the serialized sensable data.
     * 
     * 	- `ContentValues serializedSensable`: This is an instance of the `ContentValues`
     * class, which represents a set of key-value pairs for a SQLite database.
     * 	- `put()` methods: The `put()` methods are used to associate a key with a value
     * in the `ContentValues` object. In this case, the keys are the column names in the
     * SQLite table, and the values are the serialized values of the `Sensable` object.
     * 	- `COLUMN_LOCATION_LATITUDE`, `COLUMN_LOCATION_LONGITUDE`, `COLUMN_SENSOR_ID`,
     * `COLUMN_SENSOR_TYPE`, `COLUMN_NAME`, `COLUMN_LAST_SAMPLE`, and `COLUMN_UNIT`: These
     * are the column names in the SQLite table, representing the different attributes
     * of the `Sensable` object.
     * 	- `sensable.getLocation()[0]`, `sensable.getLocation()[1]`: These are methods
     * that return the latitude and longitude values of the `Sensable` object, respectively.
     * 	- `sensable.getSampleAsJsonString()`: This is a method that returns the sample
     * value of the `Sensable` object as a JSON string.
     * 	- `sensable.getSensorid()`: This is a method that returns the sensor ID of the
     * `Sensable` object.
     * 	- `sensable.getSensortype()`: This is a method that returns the sensor type of
     * the `Sensable` object.
     * 	- `sensable.getName()`: This is a method that returns the name of the `Sensable`
     * object.
     */
    public static ContentValues serializeSensableForSqlLite(Sensable sensable) {
        ContentValues serializedSensable = new ContentValues();
        serializedSensable.put(COLUMN_LOCATION_LATITUDE, sensable.getLocation()[0]);
        serializedSensable.put(COLUMN_LOCATION_LONGITUDE, sensable.getLocation()[1]);
        serializedSensable.put(COLUMN_SENSOR_ID, sensable.getSensorid());
        serializedSensable.put(COLUMN_SENSOR_TYPE, sensable.getSensortype());
        serializedSensable.put(COLUMN_NAME, sensable.getName());
        serializedSensable.put(COLUMN_LAST_SAMPLE, sensable.getSampleAsJsonString());
        serializedSensable.put(COLUMN_UNIT, sensable.getUnit());
        return serializedSensable;
    }

    /**
     * serializes a `Sensable` object into a ContentValues format for storage in a SQLite
     * database, including the sensor ID and a single sample as a JSON string.
     * 
     * @param sensable sensory data that is to be serialized and stored in a SQLite database.
     * 
     * 	- `sensable`: This is an instance of the `Sensable` class, which has a single
     * property - `sensorid`.
     * 	- `sensorid`: A column name in the serialized data, representing the unique
     * identifier of the sensor.
     * 
     * @returns a ContentValues object containing the sensor ID and the last sample as a
     * JSON string.
     * 
     * 	- `ContentValues serializedSensable`: This is an immutable map of key-value pairs
     * representing the sensable data to be stored in a SQLite database. The keys are
     * defined by the `COLUMN_` constants, and the values are the corresponding sensor data.
     * 	- `serializedSensable.put(COLUMN_SENSOR_ID, sensable.getSensorid())`: This line
     * adds a column named "sensor_id" to the serialized data with the value of `sensable.getSensorid()`.
     * 	- `serializedSensable.put(COLUMN_LAST_SAMPLE, sensable.getSampleAsJsonString())`:
     * This line adds a column named "last_sample" to the serialized data with the value
     * of `sensable.getSampleAsJsonString()`.
     */
    public static ContentValues serializeSensableWithSingleSampleForSqlLite(Sensable sensable) {
        ContentValues serializedSensable = new ContentValues();
        serializedSensable.put(COLUMN_SENSOR_ID, sensable.getSensorid());
        serializedSensable.put(COLUMN_LAST_SAMPLE, sensable.getSampleAsJsonString());
        return serializedSensable;
    }

    /**
     * retrieves a Sensable object from a cursor and populates its fields with values
     * from the cursor, including location, sensor ID, unit, sensor type, last sample (if
     * available), name, and samples.
     * 
     * @param cursor Cursor object that contains the data to be retrieved and manipulated
     * by the `getSensable()` method.
     * 
     * 	- `Cursor cursor`: A `Cursor` object that contains the data for the sensable to
     * be constructed.
     * 	+ `getDouble(int columnIndex)`: Retrieves a double value from the specified column
     * index in the cursor.
     * 	+ `getString(int columnIndex)`: Retrieves a string value from the specified column
     * index in the cursor.
     * 	+ `getColumnIndex()`: Returns the index of the column containing the data being
     * retrieved.
     * 	+ `getLastSampleColumnIndex()`: Returns the index of the column containing the
     * last sample data, or -1 if no such column exists.
     * 	+ `getString(int columnIndex)`: Retrieves a string value from the specified column
     * index in the cursor.
     * 
     * The function then constructs a new `Sensable` object with the retrieved data,
     * setting the following properties:
     * 
     * 	- `setLocation(double[] location)`: Sets the location of the sensable using the
     * double values retrieved from the cursor.
     * 	- `setSensorid(String sensorId)`: Sets the ID of the sensor associated with this
     * sensable.
     * 	- `setUnit(String unit")`: Sets the unit of measurement for this sensable.
     * 	- `setSensortype(String sensorType")`: Sets the type of sensor associated with
     * this sensable.
     * 	- `setName(String name)`: Sets the name of the sensable.
     * 
     * Finally, the function returns the constructed `Sensable` object.
     * 
     * @returns a Sensable object containing location, sensor ID, unit, sensor type, and
     * samples data.
     * 
     * 	- `sensable`: A `Sensable` object representing the sensory data.
     * 	+ `setLocation()` sets the location of the sensory data using two doubles
     * representing latitude and longitude.
     * 	+ `setSensorid()`, `setUnit()`, and `setSensortype()` set the sensor ID, unit,
     * and sensor type, respectively, using string values.
     * 	+ `setLastSample()` sets a JSON object containing the last sample of sensory data
     * using a string value.
     * 	+ `getSamples()` returns an array of `Sample` objects representing the sensory data.
     * 	+ `getName()` returns the name of the sensory data using a string value.
     */
    public static Sensable getSensable(Cursor cursor) {
        Sensable sensable = new Sensable();
        sensable.setLocation(new double[]{cursor.getDouble(cursor.getColumnIndex(SavedSensablesTable.COLUMN_LOCATION_LATITUDE)), cursor.getDouble(cursor.getColumnIndex(SavedSensablesTable.COLUMN_LOCATION_LONGITUDE))});
        sensable.setSensorid(cursor.getString(cursor.getColumnIndex(SavedSensablesTable.COLUMN_SENSOR_ID)));
        sensable.setUnit(cursor.getString(cursor.getColumnIndex(SavedSensablesTable.COLUMN_UNIT)));
        sensable.setSensortype(cursor.getString(cursor.getColumnIndex(SavedSensablesTable.COLUMN_SENSOR_TYPE)));
        if(cursor.getColumnIndex(SavedSensablesTable.COLUMN_LAST_SAMPLE) != -1) {
            String jsonSample = cursor.getString(cursor.getColumnIndex(SavedSensablesTable.COLUMN_LAST_SAMPLE));
            try {
                JSONObject json = new JSONObject(jsonSample);
                Sample sample = new Sample(json);
                sensable.setSamples(new Sample[]{sample});
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            sensable.setSamples(new Sample[]{});
        }
        sensable.setName(cursor.getString(cursor.getColumnIndex(SavedSensablesTable.COLUMN_NAME)));

        return sensable;
    }

}
