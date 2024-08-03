package io.sensable.client.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import io.sensable.client.sqlite.SavedSensablesTable;
import io.sensable.client.sqlite.ScheduledSensableContentProvider;
import io.sensable.client.sqlite.ScheduledSensablesTable;
import io.sensable.client.sqlite.SensableContentProvider;
import io.sensable.model.ScheduledSensable;
import io.sensable.model.Sensable;

/**
 * Created by madine on 15/07/14.
 */
/**
 * is a utility class that assists in managing scheduled tasks for a sensory system.
 * It provides methods for creating and removing scheduled tasks, as well as querying
 * the number of scheduled tasks and pending tasks. Additionally, it includes a method
 * for updating the favourite object if available.
 */
public class ScheduleHelper {

    private static final String TAG = ScheduleHelper.class.getSimpleName();
    private Context context;
    private AlarmManager scheduler;
    private static final int PENDING_INTENT_ID = 12345;

    public ScheduleHelper(Context context) {
        this.context = context.getApplicationContext();
        scheduler = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * sets up a scheduled task using AlarmManager if it doesn't already exist, or updates
     * the existing one if it does.
     */
    public void startScheduler() {
        Intent intent = new Intent(context.getApplicationContext(), ScheduledSensableService.class);
        boolean alarmUp = (PendingIntent.getBroadcast(context.getApplicationContext(), PENDING_INTENT_ID, intent, PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp) {
            Log.d(TAG, "AlarmManager already running. Exit without recreating it.");
        } else {
            // Create scheduled task if it doesn't already exist.
            Log.d(TAG, "AlarmManager not running. Create it now.");
            PendingIntent scheduledIntent = PendingIntent.getService(context.getApplicationContext(), PENDING_INTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            scheduler.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, scheduledIntent);
        }
    }

    /**
     * queries the content resolver for scheduled tasks and returns a cursor containing
     * the results.
     * 
     * @returns a cursor object containing the scheduled tasks.
     * 
     * 	- `Cursor count`: This is a cursor object that represents a query result set
     * containing information about scheduled tasks.
     * 	- `context`: The context object refers to the application context in which the
     * function was called.
     * 	- `ContentResolver`: The content resolver is an interface used to interact with
     * the device's data storage, and it is being used to query the ScheduledSensableContentProvider
     * for information about scheduled tasks.
     * 	- `Uri.parse(ScheduledSensableContentProvider.CONTENT_URI.toString())`: This line
     * of code constructs a Uri object that represents the content provider's URI for
     * querying scheduled tasks.
     * 	- `new String[]{"*"}`: This line of code defines an array of strings containing
     * the fields to be queried in the result set returned by the content provider. The
     * `"*"` field is used to indicate that all fields should be queried.
     */
    public Cursor getScheduledTasks() {
        Cursor count = context.getApplicationContext().getContentResolver().query(Uri.parse(ScheduledSensableContentProvider.CONTENT_URI.toString()), new String[]{"*"}, null, null, null, null);
        return count;
    }

    /**
     * retrieves the number of scheduled tasks from a `ScheduledTasks` collection and
     * returns it as an integer value.
     * 
     * @returns the number of scheduled tasks.
     */
    public int countScheduledTasks() {
        return getScheduledTasks().getCount();
    }

    /**
     * queries the ContentResolver for the number of pending scheduled tasks stored in
     * the ScheduledSensableContentProvider and returns that count.
     * 
     * @returns the number of pending scheduled tasks.
     */
    public int countPendingScheduledTasks() {
        Cursor count = context.getApplicationContext().getContentResolver().query(Uri.parse(ScheduledSensableContentProvider.CONTENT_URI + "/pending"), new String[]{"*"}, null, null, null, null);
        return count.getCount();
    }

    // Call this when removing a scheduled task to find out if we can remove the scheduler
    /**
     * cancels a scheduler's pending intent if no tasks are scheduled, effectively stopping
     * the scheduler.
     * 
     * @returns a boolean value indicating whether the scheduler was successfully canceled.
     */
    public boolean stopSchedulerIfNotNeeded() {
        if (countScheduledTasks() == 0) {
            Intent intent = new Intent(context, ScheduledSensableService.class);
            PendingIntent scheduledIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            scheduler.cancel(scheduledIntent);
        }
        return true;
    }

    /**
     * inserts a new scheduled sensory data into the database through Content Resolver.
     * 
     * @param scheduledSensable sensable that will be added to the scheduler, and it is
     * used to create a ContentValues object that contains the data to be inserted into
     * the database.
     * 
     * 	- `context`: This represents the context in which the function is being executed,
     * and is used to get a content resolver for inserting the scheduled sensable into
     * the database.
     * 	- `ScheduledSensablesTable`: This represents the table where the scheduled sensable
     * will be inserted into the database.
     * 	- `serializeScheduledSensableForSqlLite`: This function serializes the input
     * `scheduledSensable` into a ContentValues object, which is then used to insert the
     * sensable into the database.
     * 	- `mNewUri`: This represents the Uri object that contains the new scheduled
     * sensable data after insertion.
     * 	- `getContentResolver()`: This function gets a content resolver for inserting the
     * scheduled sensable into the database.
     * 
     * @returns a boolean value indicating whether the scheduled sensable was successfully
     * added to the scheduler.
     */
    public boolean addSensableToScheduler(ScheduledSensable scheduledSensable) {
        ContentValues mNewValues = ScheduledSensablesTable.serializeScheduledSensableForSqlLite(scheduledSensable);

        Uri mNewUri = context.getContentResolver().insert(
                ScheduledSensableContentProvider.CONTENT_URI,   // the user dictionary content URI
                mNewValues                          // the values to insert
        );
        return true;
    }

    /**
     * deletes a ScheduledSensable object from the database by calling the `delete` method
     * on the content resolver with the ScheduledSensable's ID as the URI and null as the
     * selection and selection arguments. It returns `true` if any rows were deleted,
     * otherwise `false`.
     * 
     * @param scheduledSensable ScheduledSensable object to be removed from the scheduler.
     * 
     * 	- `Context`: This is an instance of `android.content.Context`, which is used to
     * access the content resolver for deleting rows from the database.
     * 	- `Uri`: This is a `Uri` object that represents the content provider for schedules,
     * with the path `/$1`. The number after the `$` is the ID of the scheduled sensable.
     * 	- `ScheduledSensableContentProvider`: This is a class that provides access to the
     * content resolver for deleting rows from the database.
     * 	- `getContentResolver()`: This is a method that returns an instance of
     * `android.content.ContentResolver`, which is used to access the content provider
     * for deleting rows from the database.
     * 	- `delete()`: This is a method that deletes rows from the database based on the
     * provided `Uri`.
     * 	- `null`: This is a null object that is passed as the first parameter to the
     * `delete()` method.
     * 	- `null`: This is a null object that is passed as the second parameter to the
     * `delete()` method.
     * 
     * The function returns a boolean value indicating whether the rows were deleted successfully.
     * 
     * @returns a boolean value indicating whether the scheduled sensable was successfully
     * deleted.
     */
    public boolean removeSensableFromScheduler(ScheduledSensable scheduledSensable) {
        int rowsDeleted = context.getContentResolver().delete(Uri.parse(ScheduledSensableContentProvider.CONTENT_URI + "/" + scheduledSensable.getId()), null, null);
        return (rowsDeleted > 0);
    }

    /**
     * sets the "pending" status of a `ScheduledSensable` object to 1 and then updates
     * the sensors sender with the new status.
     * 
     * @param scheduledSensable sensable that is marked as pending to be sent, and its
     * value of 1 indicates that it is currently pending.
     * 
     * 	- `setPending(1)`: Sets the `pending` attribute of the object to `1`.
     * 	- `updateSensableSender()`: Calls the `updateSensableSender` function, which is
     * not provided in the code snippet.
     * 
     * @returns a boolean value indicating whether the scheduled sensable was successfully
     * updated.
     */
    public boolean setSensablePending(ScheduledSensable scheduledSensable) {
        scheduledSensable.setPending(1);
        return updateSensableSender(scheduledSensable);
    }

    /**
     * updates a scheduled sensory and sets its pending to zero, after which it sends the
     * update to the sensory sender.
     * 
     * @param scheduledSensable sensibility that is being unset, and it is passed to the
     * `updateSensableSender()` method for further processing.
     * 
     * 	- `scheduledSensable`: This is an instance of the class `ScheduledSensable`, which
     * contains a single attribute called `pending`. The value of this attribute can be
     * either 0 or 1, representing whether the sensation is pending or not.
     * 	- `setPending(int newValue)`: This method sets the value of the `pending` attribute
     * to the argument `newValue`.
     * 
     * @returns a boolean value indicating whether the sensable was successfully updated.
     */
    public boolean unsetSensablePending(ScheduledSensable scheduledSensable) {
        scheduledSensable.setPending(0);
        return updateSensableSender(scheduledSensable);
    }

    /**
     * updates a scheduled sensation's row in a SQL Lite database and also updates a
     * related favourite object if available.
     * 
     * @param scheduledSensable content to be updated in the database, which is serialized
     * and passed as a ContentValues object to the update method.
     * 
     * 	- `Uri updateUri`: The content URI for the scheduled sensables table, which is
     * used as the update target in the database.
     * 	- `ContentValues mNewValues`: A serialized representation of the `scheduledSensable`
     * object, created using `ScheduledSensablesTable.serializeScheduledSensableForSqlLite()`.
     * This value is used to update the scheduled sensable record in the database.
     * 	- `int rowsUpdated`: The number of rows updated in the database after executing
     * the `update()` method.
     * 	- `context`: A reference to the context object, which provides access to the
     * content resolver and other resources needed for the update operation.
     * 
     * @returns a boolean value indicating whether the scheduled sensable was successfully
     * updated.
     */
    private boolean updateSensableSender(ScheduledSensable scheduledSensable) {
        ContentValues mNewValues = ScheduledSensablesTable.serializeScheduledSensableForSqlLite(scheduledSensable);

        Uri updateUri = Uri.parse(ScheduledSensableContentProvider.CONTENT_URI + "/" + scheduledSensable.getId());

        int rowsUpdated = context.getContentResolver().update(
                updateUri,   // the user dictionary content URI
                mNewValues,                          // the values to insert
                null,
                new String[]{}
        );
        // Copy this sample over to the favourite object if there is one
        updateFavouriteIfAvailable(scheduledSensable);

        return rowsUpdated > 0;
    }

    /**
     * updates a scheduled sensable's favorite status by checking if it is already
     * favourited and updating its favorite sample if necessary.
     * 
     * @param scheduledSensable sensable that needs to be updated as the user's favourite.
     * 
     * 	- `getSensorid()`: returns the sensor ID of the scheduled sensing event
     * 	- `getSample()`: returns the sample data associated with the scheduled sensing event
     * 
     * The function then performs the following operations:
     * 
     * 1/ Retrieves the count of favourited samples for the specified sensor ID using a
     * query on the `SavedSensablesTable` content resolver.
     * 2/ If the count is greater than 0, it means that the sensor ID is already favourited.
     * 3/ Creates a new `Sensable` object with the same sensor ID and sample data as the
     * input `scheduledSensable`.
     * 4/ Serializes the `Sensable` object into a `ContentValues` object for storage in
     * the SQL Lite database.
     * 5/ Updates the favourite sample using the `update()` method of the content resolver,
     * passing in the favourite URI, the serialized `ContentValues` object, and the null
     * values array.
     * 6/ Returns the number of rows updated successfully.
     * 
     * @returns a boolean value indicating whether the specified sensor was updated as a
     * favourite.
     */
    private boolean updateFavouriteIfAvailable(ScheduledSensable scheduledSensable) {

        Uri favouriteUri = Uri.parse(SensableContentProvider.CONTENT_URI + "/" + scheduledSensable.getSensorid());
        Cursor count = context.getContentResolver().query(favouriteUri, new String[]{"*"}, null, null, null, null);

        // If this is also favourited
        if(count.getCount() > 0) {
            Sensable sensable = new Sensable();
            sensable.setSensorid(scheduledSensable.getSensorid());
            sensable.setSample(scheduledSensable.getSample());
            ContentValues mNewValues = SavedSensablesTable.serializeSensableWithSingleSampleForSqlLite(sensable);
            //Update the favourite sample
            int rowsUpdated = context.getContentResolver().update(
                    favouriteUri,   // the user dictionary content URI
                    mNewValues,                          // the values to insert
                    null,
                    new String[]{}
            );
            return rowsUpdated > 0;
        } else {
            return false;
        }

    }

}
