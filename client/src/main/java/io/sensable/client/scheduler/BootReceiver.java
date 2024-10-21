package io.sensable.client.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by simonmadine on 19/07/2014.
 */
/**
 * Is a broadcast receiver that handles system boot events in Android applications.
 * It starts an AlarmManager and initiates the Schedule Helper to begin scheduling
 * tasks when the device boots up. This ensures timely execution of scheduled tasks.
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getSimpleName();

    /**
     * Schedules a task using an AlarmManager when the device boots up. It creates a
     * `ScheduleHelper` object and calls its `startScheduler` method to initiate the
     * scheduling process. The scheduled task is logged with debug information.
     *
     * @param context application environment and provides access to various system
     * resources, services, and functionality within the code.
     *
     * @param intent Intent that triggered the onReceive() method and is not utilized
     * within the provided code snippet.
     */
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Starting AlarmManager at Boot (onReceive)");
        ScheduleHelper scheduleHelper = new ScheduleHelper(context);
        scheduleHelper.startScheduler();
    }
}
