package io.sensable.client.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import io.sensable.model.Sample;
import io.sensable.model.ScheduledSensable;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by madine on 03/07/14.
 */
/**
 * is a SQLite database table for storing scheduled sensor data. It has various columns
 * for storing information such as sensor ID, internal sensor ID, sensor name, sensor
 * type, last sample, unit, and pending status. The class also provides methods for
 * serializing and deserializing the scheduled sensors for use in the app.
 */
public class ScheduledSensablesTable {

    public static final String NAME = "scheduled_sensables";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SENSABLE_ID = "scheduled_sensable_id";
    public static final String COLUMN_SENSOR_ID = "scheduled_sensor_id";
    public static final String COLUMN_SENSOR_NAME = "scheduled_sensor_name";
    public static final String COLUMN_SENSOR_TYPE = "scheduled_type";
    public static final String COLUMN_LAST_SAMPLE = "scheduled_last_sample";
    public static final String COLUMN_UNIT = "scheduled_unit";
    public static final String COLUMN_PENDING = "scheduled_pending";

    private static final String DATABASE_CREATE = "create table " + NAME + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SENSABLE_ID + " text unique not null, "
            + COLUMN_SENSOR_ID + " int not null, "
            + COLUMN_SENSOR_NAME + " text, "
            + COLUMN_SENSOR_TYPE + " text not null, "
            + COLUMN_LAST_SAMPLE + " text, "
            + COLUMN_UNIT + " text not null, "
            + COLUMN_PENDING + " int not null" + ");";


    /**
     * executes a SQL query to create the database structure when the app is launched for
     * the first time.
     * 
     * @param database SQLite Database object that is being manipulated by the function.
     * 
     * 	- `database`: an instance of SQLiteDatabase, which is a class that provides a way
     * to interact with a SQLite database.
     * 	- `execSQL`: a method that executes a SQL statement on the database.
     */
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    /**
     * drops an existing table and recreates it according to the new version's schema.
     * 
     * @param database SQLiteDatabase object that is being upgraded.
     * 
     * 	- `SQLiteDatabase`: This is an instance of the SQLite database class, which
     * provides methods for managing SQLite databases.
     * 	- `oldVersion`, `newVersion`: These are integers that represent the old and new
     * versions of the database, respectively.
     * 
     * @param oldVersion version of the database that is being upgraded, which is used
     * to determine the appropriate action to take during the upgrade process.
     * 
     * @param newVersion new version of the SQLite database, which is used to determine
     * the appropriate actions to take during the upgrade process.
     */
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + NAME);
        onCreate(database);
    }

    /**
     * converts a `ScheduledSensable` object into a `ContentValues` instance, which can
     * be used for database storage in SQLite. It serializes the `ScheduledSensable`
     * fields and puts them into the `ContentValues` with appropriate column names.
     * 
     * @param scheduledSensable sensory data that is scheduled to be saved for SQL Lite.
     * 
     * 	- `sensorid`: A unique identifier for the sensor.
     * 	- `internalSensorId`: The internal ID of the sensor.
     * 	- `name`: The name of the sensor.
     * 	- `sensortype`: The type of sensor (e.g., temperature, humidity, etc.).
     * 	- `lastSample`: The last sample value of the sensor, represented as a JSON string.
     * 	- `unit`: The unit of measurement for the sensor.
     * 	- `pending`: A boolean value indicating whether the sensor is pending or not.
     * 
     * @returns a ContentValues object containing the scheduled sensory data in a format
     * suitable for SQL Lite storage.
     * 
     * 	- `ContentValues serializedScheduledSensable`: This is an instance of the
     * `ContentValues` class, which is used to store and manipulate data in a SQLite database.
     * 	- `put(columnName, columnValue)`: This method is used to add or update a value
     * for a specific column name in the `serializedScheduledSensable` ContentValues
     * object. The `columnName` parameter specifies the name of the column, while the
     * `columnValue` parameter specifies the value to be stored in that column.
     * 	- `COLUMN_SENSABLE_ID`: This is the name of a column in the `serializedScheduledSensable`
     * ContentValues object that contains the ID of the sensable being serialized.
     * 	- `COLUMN_SENSOR_ID`: This is the name of a column in the `serializedScheduledSensable`
     * ContentValues object that contains the ID of the sensor associated with the sensable.
     * 	- `COLUMN_SENSOR_NAME`: This is the name of a column in the `serializedScheduledSensable`
     * ContentValues object that contains the name of the sensor associated with the sensable.
     * 	- `COLUMN_SENSOR_TYPE`: This is the name of a column in the `serializedScheduledSensable`
     * ContentValues object that contains the type of the sensor associated with the sensable.
     * 	- `COLUMN_LAST_SAMPLE`: This is the name of a column in the `serializedScheduledSensable`
     * ContentValues object that contains the last sample value for the sensable.
     * 	- `COLUMN_UNIT`: This is the name of a column in the `serializedScheduledSensable`
     * ContentValues object that contains the unit of measurement for the sensable.
     * 	- `COLUMN_PENDING`: This is the name of a column in the `serializedScheduledSensable`
     * ContentValues object that indicates whether the sensable is pending or not.
     */
    public static ContentValues serializeScheduledSensableForSqlLite(ScheduledSensable scheduledSensable) {
        ContentValues serializedScheduledSensable = new ContentValues();
        serializedScheduledSensable.put(COLUMN_SENSABLE_ID, scheduledSensable.getSensorid());
        serializedScheduledSensable.put(COLUMN_SENSOR_ID, scheduledSensable.getInternalSensorId());
        serializedScheduledSensable.put(COLUMN_SENSOR_NAME, scheduledSensable.getName());
        serializedScheduledSensable.put(COLUMN_SENSOR_TYPE, scheduledSensable.getSensortype());
        serializedScheduledSensable.put(COLUMN_LAST_SAMPLE, scheduledSensable.getSampleAsJsonString());
        serializedScheduledSensable.put(COLUMN_UNIT, scheduledSensable.getUnit());
        serializedScheduledSensable.put(COLUMN_PENDING, false);
        return serializedScheduledSensable;
    }

    /**
     * retrieves a scheduled sensors data from a cursor and creates a new ScheduledSensable
     * object with the retrieved data.
     * 
     * @param cursor result of a query on the `ScheduledSensablesTable`, providing a
     * cursor pointing to the current row being processed.
     * 
     * 	- `cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_ID)` - returns the index
     * of the column containing the sensor ID
     * 	- `cursor.getString(cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_SENSOR_ID))`
     * - retrieves the value of the sensor ID column as a string
     * 	- `cursor.getInt(cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_SENSOR_ID))`
     * - retrieves the value of the sensor ID column as an integer
     * 	- `cursor.getString(cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_NAME))`
     * - retrieves the value of the name column as a string
     * 	- `cursor.getString(cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_SENSOR_TYPE))`
     * - retrieves the value of the sensor type column as a string
     * 	- `cursor.getString(cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_UNIT))`
     * - retrieves the value of the unit column as a string
     * 	- `cursor.getInt(cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_PENDING))`
     * - retrieves the value of the pending column as an integer
     * 	- `cursor.getString(cursor.getColumnIndex(SavedSensablesTable.COLUMN_LAST_SAMPLE))`
     * - retrieves the value of the last sample column as a string (if appropriate)
     * 
     * Note: The properties of `cursor` may vary depending on the specific implementation
     * and usage of the `getScheduledSensable` function.
     * 
     * @returns a `ScheduledSensable` object containing the sensor data.
     * 
     * 	- `scheduledSensable`: An object of the `ScheduledSensable` class that contains
     * information about a scheduled sensor.
     * 	+ `setId()`: The id of the scheduled sensor.
     * 	+ `setSensorid()`: The ID of the sensor that the scheduled sensor is associated
     * with.
     * 	+ `setInternalSensorId()`: The internal ID of the sensor that the scheduled sensor
     * is associated with.
     * 	+ `setName()`: The name of the sensor.
     * 	+ `setSensortype()`: The type of the sensor.
     * 	+ `setUnit()`: The unit of measurement for the sensor.
     * 	+ `setPending()`: A boolean value indicating whether the scheduled sensor is
     * pending or not.
     * 	+ `setSample()`: A `Sample` object containing the latest sample data from the
     * associated sensor, or an empty `Sample` object if no sample data is available.
     */
    public static ScheduledSensable getScheduledSensable(Cursor cursor) {
        ScheduledSensable scheduledSensable = new ScheduledSensable();
        int currentIndex = cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_ID);
        if(currentIndex > -1) {
            scheduledSensable.setId(cursor.getInt(currentIndex));
            scheduledSensable.setSensorid(cursor.getString(cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_SENSABLE_ID)));
            scheduledSensable.setInternalSensorId(cursor.getInt(cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_SENSOR_ID)));
            scheduledSensable.setName(cursor.getString(cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_SENSOR_NAME)));
            scheduledSensable.setSensortype(cursor.getString(cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_SENSOR_TYPE)));
            scheduledSensable.setUnit(cursor.getString(cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_UNIT)));
            scheduledSensable.setPending(cursor.getInt(cursor.getColumnIndex(ScheduledSensablesTable.COLUMN_PENDING)));
            if(cursor.getColumnIndex(SavedSensablesTable.COLUMN_LAST_SAMPLE) != -1) {
                String jsonSample = cursor.getString(cursor.getColumnIndex(SavedSensablesTable.COLUMN_LAST_SAMPLE));
                try {
                    JSONObject json = new JSONObject(jsonSample);
                    Sample sample = new Sample(json);
                    scheduledSensable.setSample(sample);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Sample sample = new Sample();
                scheduledSensable.setSample(sample);
            }
        }

        return scheduledSensable;
    }

}
