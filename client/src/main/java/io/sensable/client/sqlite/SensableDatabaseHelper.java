package io.sensable.client.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import io.sensable.client.settings.Config;

/**
 * Created by madine on 03/07/14.
 */
/**
 * is a SQLiteOpenHelper that provides a singleton connection to a SQLite database
 * for content providers. The class creates and manages the database schema and
 * provides a way to access the database through a single instance.
 */
public class SensableDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = SensableDatabaseHelper.class.getCanonicalName();

    private static SensableDatabaseHelper sInstance = null;

    /**
     * The Internet and common sense say that having multiple open
     * connections to a database is bad for performance and it is
     * a bad practice.
     * <p/>
     * {@link SensableDatabaseHelper} connects Content Providers to a
     * SQLite DB that is stored on the filesystem. Therefore it
     * is best to use a singleton for that and use it every time
     * a some class or content provider needs to read/write to a
     * database table.
     * {@link SensableDatabaseHelper#onCreate(android.database.sqlite.SQLiteDatabase)}
     *
     * @param context
     * @return
     */
    /**
     * provides a single instance of `SensableDatabaseHelper` for a given `Context`. If
     * the instance is null, it creates a new one using the context's application context.
     * 
     * @param context Android application context, which is used to create a new instance
     * of the `SensableDatabaseHelper` class.
     * 
     * 	- `Context context`: This is an object that represents the Android application
     * environment. It provides access to various components and resources within the
     * app, such as the main activity, the application context, and other utility classes.
     * 	- `sInstance`: A static instance variable that stores a reference to a
     * `SensableDatabaseHelper` object. This variable is used to store a single instance
     * of the helper class, which can be retrieved by calling the `getHelper` function.
     * 
     * @returns a `SensableDatabaseHelper` object instance.
     * 
     * 	- The output is a `SensableDatabaseHelper`, which is an instance of a class that
     * provides access to a database.
     * 	- The `SensableDatabaseHelper` instance is created by calling the
     * `SensableDatabaseHelper(Context)` constructor, passing in the `Context` object
     * representing the Android application context.
     * 	- The instance is stored in a variable called `sInstance`, which is initially set
     * to `null`. When the function is called, it sets `sInstance` to the newly created
     * `SensableDatabaseHelper` instance.
     */
    public static synchronized SensableDatabaseHelper getHelper(Context context) {
        if (sInstance == null) {
            sInstance = new SensableDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private SensableDatabaseHelper(Context context) {
        super(context, Config.SENSABLE_STORAGE_DB, null, Config.SENSABLE_STORAGE_DB_VERSION);
    }

    /**
     * is responsible for creating the `SavedSensablesTable` and `ScheduledSensablesTable`
     * in a SQLite database.
     * 
     * @param db SQLiteDatabase object that is used to perform database operations.
     * 
     * 	- SavedSensablesTable: The table for saved sensibles is created with the `onCreate`
     * method.
     * 	- ScheduledSensablesTable: The table for scheduled sensibles is also created using
     * the `onCreate` method.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        SavedSensablesTable.onCreate(db);
        ScheduledSensablesTable.onCreate(db);

    }

    /**
     * updates the SavedSensables and ScheduledSensables tables when upgrading the database
     * version.
     * 
     * @param db SQLiteDatabase object that is being upgraded.
     * 
     * 	- `SavedSensablesTable`: The table containing sensors' historical data is upgraded
     * by calling its `onUpgrade` method.
     * 	- `ScheduledSensablesTable`: The scheduled sensors' data in another table is also
     * upgraded using the same method.
     * 
     * @param oldVersion previous version of the database schema.
     * 
     * @param newVersion latest version of the SQLite database schema, which is used to
     * determine what actions need to be taken during the upgrade process.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        SavedSensablesTable.onUpgrade(db, oldVersion, newVersion);
        ScheduledSensablesTable.onUpgrade(db, oldVersion, newVersion);
    }

}
