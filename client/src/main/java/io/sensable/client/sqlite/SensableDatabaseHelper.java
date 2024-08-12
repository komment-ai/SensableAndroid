package io.sensable.client.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import io.sensable.client.settings.Config;

/**
 * Created by madine on 03/07/14.
 */
/**
 * Is a SQLiteOpenHelper that provides a singleton connection to a SQLite database
 * for content providers. It creates and manages the database schema and provides
 * access to the database through a single instance, ensuring only one active connection
 * at a time. The class handles database operations such as creating and upgrading tables.
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
     * Creates a single instance of `SensableDatabaseHelper` if it does not exist, using
     * the provided `Context`. The instance is stored in a static field for reuse. The
     * function returns the existing or newly created instance, ensuring thread safety
     * with the use of the `synchronized` keyword.
     *
     * @param context application context that is used to create an instance of the
     * SensableDatabaseHelper class when it has not been initialized yet.
     *
     * @returns a synchronized instance of the `SensableDatabaseHelper`.
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
     * Executes tables creation for `SavedSensablesTable` and `ScheduledSensablesTable`
     * in a database, ensuring their schema is established when the application is initially
     * installed or updated. This allows for data storage and retrieval operations to be
     * performed.
     *
     * @param db SQLiteDatabase object that is used to create or modify tables and their
     * structures, such as SavedSensablesTable and ScheduledSensablesTable.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        SavedSensablesTable.onCreate(db);
        ScheduledSensablesTable.onCreate(db);

    }

    /**
     * Is overridden to handle database schema changes when upgrading from an older version
     * to a newer one. It calls separate upgrade methods for two tables: `SavedSensablesTable`
     * and `ScheduledSensablesTable`, indicating that these tables require migration upon
     * upgrading the database schema.
     *
     * @param db SQLiteDatabase object that is being upgraded to a newer version.
     *
     * @param oldVersion previous version of the database schema before the upgrade
     * operation is performed.
     *
     * @param newVersion newest database schema version being installed or upgraded to.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        SavedSensablesTable.onUpgrade(db, oldVersion, newVersion);
        ScheduledSensablesTable.onUpgrade(db, oldVersion, newVersion);
    }

}
