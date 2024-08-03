package io.sensable.client.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by simonmadine on 19/07/2014.
 */
/**
 * is a broadcast receiver that handles events related to the system booting up. When
 * the device boots, the receiver starts an AlarmManager to schedule tasks using the
 * ScheduleHelper class.
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getSimpleName();

    /**
     * starts AlarmManager at boot and initiates the Schedule Helper to begin scheduling
     * tasks.
     * 
     * @param context Android app's context, which provides access to resources and APIs
     * needed to perform the function.
     * 
     * 	- `TAG`: a String variable representing the logging tag for the function.
     * 	- `intent`: an Intent object passed as an argument to the function, containing
     * data relevant to the function's execution.
     * 
     * @param intent Android AlarmManager's intent, which triggers the start of the
     * ScheduleHelper's scheduler when the device boots.
     * 
     * 	- `context`: The context of the application, which is a crucial component in
     * Android applications.
     * 	- `intent`: An intent object representing the purpose and data associated with
     * the event being handled.
     */
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Starting AlarmManager at Boot (onReceive)");
        ScheduleHelper scheduleHelper = new ScheduleHelper(context);
        scheduleHelper.startScheduler();
    }
}
