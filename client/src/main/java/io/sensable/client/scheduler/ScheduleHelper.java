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
 * Manages scheduled tasks for a sensory system, allowing for creation, deletion, and
 * updating of scheduled sensables. It utilizes Android's AlarmManager to schedule
 * tasks at specific intervals and ContentResolver to interact with the device's data
 * storage. The class provides methods for checking the status of scheduled tasks,
 * stopping or canceling schedules, and updating favourite objects.
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
     * Starts or resumes a recurring task on an Android device using AlarmManager. If an
     * existing alarm is found, it exits without recreating it; otherwise, it creates and
     * schedules the task to run every 15 minutes.
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
     * Queries the content resolver for a cursor containing all scheduled tasks, returning
     * it as a result. It uses the `Uri.parse` method to obtain the URI for the
     * ScheduledSensableContentProvider and then passes this URI along with an empty
     * projection, null selection criteria, and null sort order to the query method.
     *
     * @returns a cursor object representing scheduled tasks.
     */
    public Cursor getScheduledTasks() {
        Cursor count = context.getApplicationContext().getContentResolver().query(Uri.parse(ScheduledSensableContentProvider.CONTENT_URI.toString()), new String[]{"*"}, null, null, null, null);
        return count;
    }

    /**
     * Retrieves and returns the count of scheduled tasks from a designated source using
     * the `getSchedueldTasks` method. The returned value represents the number of tasks
     * that are currently scheduled for execution. This function encapsulates the logic
     * to retrieve task count in a concise manner.
     *
     * @returns an integer value representing the number of scheduled tasks.
     */
    public int countScheduledTasks() {
        return getScheduledTasks().getCount();
    }

    /**
     * Queries a content provider for a URI related to pending scheduled tasks. It returns
     * the number of rows in the result set, representing the count of pending scheduled
     * tasks. The query uses a cursor to retrieve data from the content resolver.
     *
     * @returns an integer value representing the number of pending scheduled tasks.
     */
    public int countPendingScheduledTasks() {
        Cursor count = context.getApplicationContext().getContentResolver().query(Uri.parse(ScheduledSensableContentProvider.CONTENT_URI + "/pending"), new String[]{"*"}, null, null, null, null);
        return count.getCount();
    }

    // Call this when removing a scheduled task to find out if we can remove the scheduler
    /**
     * Cancels a scheduled task if no tasks are pending. It checks the count of scheduled
     * tasks and, if zero, stops the scheduler by canceling a pending intent that triggers
     * the ScheduledSensableService class. The function then returns true.
     *
     * @returns a boolean value indicating successful cancellation of scheduled tasks.
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
     * Inserts a new scheduled sensable into the SQLite database, using the provided
     * ScheduledSensableContentProvider and ContentValues for serialization. It returns
     * true upon successful insertion.
     *
     * @param scheduledSensable ScheduledSensable object that needs to be added to the
     * SQLite database through the content provider.
     *
     * @returns a boolean value indicating successful insertion of data.
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
     * Removes a scheduled sensable from the scheduler by deleting its corresponding entry
     * from the content provider. It returns true if the deletion is successful, and false
     * otherwise.
     *
     * @param scheduledSensable ScheduledSensable object whose ID is used to delete the
     * corresponding row from the content provider.
     *
     * @returns a boolean value indicating deletion success.
     */
    public boolean removeSensableFromScheduler(ScheduledSensable scheduledSensable) {
        int rowsDeleted = context.getContentResolver().delete(Uri.parse(ScheduledSensableContentProvider.CONTENT_URI + "/" + scheduledSensable.getId()), null, null);
        return (rowsDeleted > 0);
    }

    /**
     * Sets the `pending` field of a `ScheduledSensable` object to 1 and updates a `sensable
     * sender`. It returns a boolean value indicating whether the update was successful.
     *
     * @param scheduledSensable object that needs to have its pending status set and then
     * updated by calling the `updateSensableSender` method.
     *
     * @returns a boolean value indicating successful or unsuccessful processing.
     */
    public boolean setSensablePending(ScheduledSensable scheduledSensable) {
        scheduledSensable.setPending(1);
        return updateSensableSender(scheduledSensable);
    }

    /**
     * Sets the pending value of a ScheduledSensable object to 0 and then calls the
     * `updateSensableSender` method with the updated object, returning its result.
     *
     * @param scheduledSensable object whose pending status is being unset and updated
     * for sending to a sensable sender.
     *
     * @returns a boolean value indicating the result of updating the sensable sender.
     */
    public boolean unsetSensablePending(ScheduledSensable scheduledSensable) {
        scheduledSensable.setPending(0);
        return updateSensableSender(scheduledSensable);
    }

    /**
     * Updates a scheduled sensable record in a SQLite database by serializing the object
     * into ContentValues, then updating the corresponding Uri with these values. The
     * function returns true if the update is successful and false otherwise.
     *
     * @param scheduledSensable scheduled sensable object to be updated in the SQLite
     * database and its serialized values are used to update the corresponding record in
     * the database.
     *
     * SerializeScheduledSensableForSqlLite returns ContentValues with serialized data
     * from ScheduledSensable.
     *
     * @returns a boolean indicating whether any rows were updated.
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
     * Checks if a scheduled sensable is also favourited. If it is, updates the favourite
     * sample and returns true; otherwise, returns false.
     *
     * @param scheduledSensable sensor data to be processed and potentially updated as a
     * favourite sample in the system.
     *
     * Extracted from `scheduledSensable`:
     * - Sensorid
     * - Sample
     *
     * @returns a boolean value indicating update success.
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
