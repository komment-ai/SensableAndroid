package io.sensable.client.scheduler;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import io.sensable.SensableService;
import io.sensable.client.R;
import io.sensable.client.SensableUser;
import io.sensable.client.SensorHelper;
import io.sensable.client.sqlite.ScheduledSensablesTable;
import io.sensable.model.Sample;
import io.sensable.model.SampleResponse;
import io.sensable.model.SampleSender;
import io.sensable.model.ScheduledSensable;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.List;

/**
 * Is an Android Service that schedules and collects sensor data from various sensors
 * in the device based on a schedule. It retrieves scheduled tasks from a database,
 * registers listeners for each task, and sends the collected data to a remote service
 * for analysis. The service also handles errors, updates the scheduled sensables
 * with new sample values, and stops itself when there are no more pending tasks.
 */
public class ScheduledSensableService extends Service {

    private static final String TAG = ScheduledSensableService.class.getSimpleName();

    private SensorManager sensorManager = null;
    private Sensor sensor = null;

    /**
     * Registers sensor listeners for scheduled sensables, retrieves and processes data
     * from the database, and marks sensables as pending or stops scheduling if necessary.
     * It also logs messages and returns a sticky service start result.
     *
     * @param intent Intent object that was used to start the service and contains any
     * data or commands intended for the service.
     *
     * intent is an instance of Intent class that contains information about action to
     * be performed. It has three parameters - intent, flags and startId.
     *
     * @param flags flags used to start the service, which can be one or more of the
     * following: START_FLAG_REDELIVERY, START_FLAG_RETRY, or other flags that are not
     * yet documented.
     *
     * @param startId identifier of the service instance, which can be used to restart
     * the service if it is terminated by the system.
     *
     * @returns a service start state code of START_STICKY.
     *
     * Returns START_STICKY as an integer, indicating that the service should restart
     * itself after being stopped.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Starting Service");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        ScheduleHelper scheduleHelper = new ScheduleHelper(this);
        Cursor cursor = scheduleHelper.getScheduledTasks();

        while (cursor.moveToNext()) {
            Log.d(TAG, "Adding one sampler");
            // Load the sensable from the DB
            final ScheduledSensable scheduledSensable = ScheduledSensablesTable.getScheduledSensable(cursor);

            // Register the listener on the sensor
            List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
            Sensor sensor = sensorManager.getDefaultSensor(scheduledSensable.getInternalSensorId());
            sensorManager.registerListener(getListener(scheduledSensable), sensor, SensorManager.SENSOR_DELAY_NORMAL);

            // Mark this sensable as pending
            scheduleHelper.setSensablePending(scheduledSensable);
        }
        scheduleHelper.stopSchedulerIfNotNeeded();

        return START_STICKY;
    }

    /**
     * Returns a SensorEventListener that handles sensor data by creating a sample object
     * from the sensor values and attaching location data. It then sends the sample to a
     * remote service for analysis, updates a Scheduled object with sensor metadata, and
     * stops the sensor and service if no more tasks are pending.
     *
     * @param scheduledSensable Scheduled object that is updated with the sensor ID,
     * sample values, and other metadata before stopping the sensor and service if no
     * more tasks are pending.
     *
     * Set internal sensor ID.
     * Set unit based on sensor type.
     * Set private sensor status to false.
     * Set access token.
     *
     * @returns a `SensorEventListener` object.
     *
     * Returns an instance of SensorEventListener with methods to handle sensor data
     * received from a listener and save it to a remote service for analysis. The
     * onSensorChanged method creates a sample object, formats its values, attaches
     * location data before sending it to the service for storage.
     */
    private SensorEventListener getListener(final ScheduledSensable scheduledSensable) {
        return new SensorEventListener() {
            /**
             * Handles changes in sensor values, updates a scheduled sensable object with sensor
             * data and location information, and sends the updated data to a RESTful API using
             * Retrofit for further processing or storage.
             *
             * @param event SensorEvent that has been changed, containing information about the
             * sensor type and its values, which is used to update the scheduled object and send
             * samples to the server.
             *
             * Event has sensor type and name, which are accessed using event.sensor.getType()
             * and event.sensor.getName(). It also contains an array of values, accessible via event.values[0].
             */
            @Override
            public void onSensorChanged(SensorEvent event) {
                Log.d(TAG, "Sensor Value Changed");
                ScheduleHelper scheduleHelper = new ScheduleHelper(ScheduledSensableService.this);

                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setEndpoint("http://sensable.io")
                        .build();
                SensableService service = restAdapter.create(SensableService.class);

                // Create the sample object
                Sample sample = new Sample();
                sample.setTimestamp((System.currentTimeMillis()));

                // TODO: This should parse the sensor type and format the values array differently
                sample.setValue(event.values[0]);

                /* Location needs to be attached to samples once the service supports it */
                Location lastKnownLocation = getLocation();
                Log.d(TAG, "Location: " + lastKnownLocation.toString());
                sample.setLocation(new double[]{lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude()});

                SampleSender sampleSender = new SampleSender();
                sampleSender.setAccessToken(getUserAccessToken());
                sampleSender.setSample(sample);

                // Update the Scheduled object
                scheduledSensable.setLocation(new double[]{lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude()});
                scheduledSensable.setSample(sample);
                scheduledSensable.setSensortype(event.sensor.getName());

                scheduledSensable.setInternalSensorId(event.sensor.getType());
                scheduledSensable.setUnit(SensorHelper.determineUnit(event.sensor.getType()));
                scheduledSensable.setPrivateSensor(false);
                scheduledSensable.setAccessToken(getUserAccessToken());

                Log.d(TAG, "Saving sample: " + event.sensor.getName() + " : " + event.values[0]);
                service.saveSample(scheduledSensable.getSensorid(), sampleSender, new Callback<SampleResponse>() {
                    /**
                     * Logs a debug message to the console with the tag `TAG`, indicating that a successful
                     * post operation has been completed for a `SampleResponse`. The response object is
                     * also passed as an argument, but not used within the function.
                     *
                     * @param success response returned by the server when the request is successful.
                     *
                     * @param response HTTP response received from the server after successfully posting
                     * the sample.
                     */
                    @Override
                    public void success(SampleResponse success, Response response) {
                        Log.d(TAG, "Success posting sample");
                    }

                    /**
                     * Captures and logs an error that occurs when a request fails. It takes a `RetrofitError`
                     * object as input, converts it to a string, and logs the resulting error message at
                     * the error level with the tag `TAG`.
                     *
                     * @param retrofitError error occurred during the request processing and provides
                     * details about the failure, which is then logged by the function.
                     */
                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Log.e(TAG, "Failed to post sample: " + retrofitError.toString());
                    }
                });

                scheduleHelper.unsetSensablePending(scheduledSensable);

                // stop the sensor and service
                sensorManager.unregisterListener(this);
                if (scheduleHelper.countPendingScheduledTasks() == 0) {
                    // Stop this service from sampling as we are not waiting for any more samples to come in
                    stopSelf();
                    scheduleHelper.stopSchedulerIfNotNeeded();
                }
            }

            /**
             * Is called when the accuracy of a sensor changes. It takes two parameters: the
             * sensor that has changed accuracy and the new accuracy value. The function does not
             * perform any specific actions, but it provides information about the change in
             * sensor accuracy.
             *
             * @param sensor Sensor object that is being monitored for changes in its accuracy.
             *
             * @param accuracy current accuracy level of the sensor, which can be one of three
             * values: SensorManager.SENSOR_STATUS_UNRELIABLE, SensorManager.SENSOR_STATUS_ACCURACY_HIGH,
             * or SensorManager.SENSOR_STATUS_ACCURACY_LOW.
             */
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    /**
     * Returns an instance of `IBinder`, which is a reference to the interface that can
     * be used by clients to communicate with the service. The returned object is null,
     * indicating that no binding is being established between the client and the service.
     *
     * @param intent Intent object that is being bound to the Service, providing information
     * about the action requested by the client application.
     *
     * @returns a null instance of `IBinder`.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * Retrieves a user's access token from the device's shared preferences if they are
     * logged in and have an access token stored. If not, it returns an empty string. It
     * also logs various messages to the debug log for debugging purposes.
     *
     * @returns a string representing the user's access token or an empty string.
     */
    private String getUserAccessToken() {
        SensableUser user = new SensableUser(this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE), this);
        if (user.loggedIn) {
            if (user.hasAccessToken) {
                Log.d(TAG, "Loading Access token");
                Log.d(TAG, getString(R.string.preference_file_key));
                Log.d(TAG, getString(R.string.saved_access_token));
                SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                String username = sharedPreferences.getString(getString(R.string.saved_username), "");
                Log.d(TAG, "Username: " + username);
                String accessToken = sharedPreferences.getString(getString(R.string.saved_access_token), "");
                Log.d(TAG, "Access Token: " + accessToken);
                return accessToken;
            } else {
                Log.d(TAG, "No access Token");
                return "";
            }
        } else {
            Log.d(TAG, "Not logged in");
            return "";
        }

    }

    /**
     * Retrieves the last known location from a network provider using a LocationManager.
     * It obtains a reference to the current application context and a location manager,
     * then requests the last known location from the network provider.
     *
     * @returns a `Location` object representing the device's last known geographical position.
     */
    private Location getLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        return locationManager.getLastKnownLocation(locationProvider);
    }


}
