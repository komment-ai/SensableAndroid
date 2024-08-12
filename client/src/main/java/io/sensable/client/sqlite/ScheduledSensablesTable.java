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
 * Is responsible for managing a SQLite database table that stores scheduled sensor
 * data. It provides methods for creating and upgrading the database schema, serializing
 * and deserializing scheduled sensors to and from ContentValues objects, and retrieving
 * scheduled sensor data from a cursor.
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
     * Executes a SQL command on a SQLite database to create tables. The `execSQL` method
     * is used to execute the SQL command specified by the constant `DATABASE_CREATE`.
     * This function is typically called when the database is created for the first time.
     *
     * @param database SQLiteDatabase object that is being used to execute the SQL command
     * defined by the `DATABASE_CREATE` constant.
     */
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    /**
     * Drops an existing table with name `NAME` from a SQLite database and then creates
     * it again by calling the `onCreate` function when the database version is upgraded.
     *
     * @param database SQLiteDatabase instance that is being upgraded to the new version
     * specified by the `newVersion`.
     *
     * @param oldVersion current version of the database before any upgrade operations
     * are performed.
     *
     * @param newVersion current version of the SQLite database schema, indicating when
     * changes or updates are made to the table structure.
     */
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + NAME);
        onCreate(database);
    }

    /**
     * Converts a `ScheduledSensable` object into a SQL-compatible format using a
     * `ContentValues` object. It extracts various properties from the input object and
     * stores them as key-value pairs, preparing the data for storage in a SQLite database.
     *
     * @param scheduledSensable data to be serialized into a ContentValues object for
     * storage in a SQLite database.
     *
     * Serialize its sensor ID, internal sensor ID, name, sensor type, last sample as
     * JSON string, unit and pending status.
     *
     * @returns a ContentValues object.
     *
     * Serialized content contains sensable ID, internal sensor ID, name, sensor type,
     * last sample in JSON string format, unit and a pending flag set to false. It is
     * stored as a ContentValues object.
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
     * Retrieves data from a database cursor and populates a `ScheduledSensable` object
     * with its properties, including ID, sensor ID, name, type, unit, and pending status.
     * It also extracts the last sample from the database as a JSON string and sets it
     * in the `ScheduledSensable` object.
     *
     * @param cursor cursor that is used to retrieve data from the database table and
     * populate the `ScheduledSensable` object with relevant information.
     *
     * Cursor has columns: COLUMN_ID, COLUMN_SENSABLE_ID, COLUMN_SENSOR_ID, COLUMN_SENSOR_NAME,
     * COLUMN_SENSOR_TYPE, COLUMN_UNIT, COLUMN_PENDING. Additionally, it contains a column
     * with key COLUMN_LAST_SAMPLE and value type String.
     *
     * @returns a populated `ScheduledSensable` object.
     *
     * The ScheduledSensable object contains an ID, sensor id, internal sensor ID, name,
     * sensor type, unit, and pending status. Additionally, it has a sample attribute
     * that is either populated from JSON data or initialized as empty if no JSON data
     * is present.
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
