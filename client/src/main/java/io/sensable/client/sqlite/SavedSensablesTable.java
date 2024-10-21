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
 * Is responsible for managing sensor data in a SQLite database. It provides methods
 * to create and upgrade the database schema, serialize and deserialize Sensable
 * objects, and retrieve Sensable objects from a cursor.
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
     * Executes a SQL statement to create a table in a SQLite database when it is first
     * created. The SQL statement is stored in the `DATABASE_CREATE` variable and is
     * executed on the provided `SQLiteDatabase`. This function initializes the database
     * with the specified schema.
     *
     * @param database SQLiteDatabase object that is passed to the onCreate method,
     * allowing the method to execute SQL commands on it.
     */
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    // TODO: make it smarter
    /**
     * Drops an existing table and recreates it if a schema change occurs during the
     * upgrade process of a SQLite database. It uses the `execSQL` method to execute SQL
     * statements for dropping the table and then calls the `onCreate` function to recreate
     * the table structure.
     *
     * @param database SQLiteDatabase object that is being upgraded or created, allowing
     * for execution of SQL statements and other database operations.
     *
     * @param oldVersion previous version of the database schema that was used prior to
     * the current upgrade operation, allowing the function to determine what changes
     * need to be made to bring it up to date with the new schema specified by `newVersion`.
     *
     * @param newVersion current version of the database schema, which is used to determine
     * whether any changes have been made and if so, the necessary actions are taken accordingly.
     */
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + NAME);
        onCreate(database);
    }

    /**
     * Creates a ContentValues object to store sensor data. It populates the object with
     * various properties of a Sensable object, including location coordinates, sensor
     * ID, type, name, last sample, and unit, transforming JSON-formatted samples into
     * SQL-compatible values.
     *
     * @param sensable object being serialized into a ContentValues object for storage
     * in SQLite database.
     *
     * The object has location with latitude and longitude coordinates, sensor ID, sensor
     * type, name, last sample as JSON string, and unit. These properties are serialized
     * into a ContentValues object.
     *
     * @returns a ContentValues object with various sensor details.
     *
     * The output is an instance of `ContentValues`, which is a key-value pair container
     * used for storing and retrieving data in SQLite databases. The keys are column
     * names, and the values are corresponding data elements.
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
     * Converts a `Sensable` object into a `ContentValues` object for SQLite database
     * storage. It takes a `sensable` object as input and populates the `ContentValues`
     * with its sensor ID and a single sample in JSON format.
     *
     * @param sensable object being serialized into a ContentValues instance.
     *
     * @returns a ContentValues object.
     */
    public static ContentValues serializeSensableWithSingleSampleForSqlLite(Sensable sensable) {
        ContentValues serializedSensable = new ContentValues();
        serializedSensable.put(COLUMN_SENSOR_ID, sensable.getSensorid());
        serializedSensable.put(COLUMN_LAST_SAMPLE, sensable.getSampleAsJsonString());
        return serializedSensable;
    }

    /**
     * Extracts data from a database cursor and populates a `Sensable` object with location,
     * sensor ID, unit, sensor type, last sample, and name information. It also handles
     * parsing a JSON string to create a `Sample` object if available in the database.
     *
     * @param cursor ursor object that retrieves data from the database table and provides
     * values to populate the `Sensable` object properties.
     *
     * Moves to a specific column by index or name. Returns -1 if the column does not exist.
     *
     * @returns an instance of class `Sensable`.
     *
     * The output is an object of type `Sensable`. It has a location property with latitude
     * and longitude values, a sensor ID string, a unit string, a sensor type string, a
     * sample array containing a single `Sample` object or an empty array if no sample
     * exists, and a name string.
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
