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
 * Is an Android service responsible for sampling sensors based on scheduled tasks
 * and sending sensor data to a remote server for analysis. It uses the SensorManager
 * API to register listeners on sensors, retrieves user location using the LocationManager
 * API, and stores user access tokens in shared preferences.
 */
public class ScheduledSensableService extends Service {

    private static final String TAG = ScheduledSensableService.class.getSimpleName();

    private SensorManager sensorManager = null;
    private Sensor sensor = null;

    /**
     * Initializes and starts a service that schedules sensors to monitor specific tasks
     * based on data retrieved from a database. It registers listeners for each sensor,
     * marks scheduled tasks as pending, and stops unnecessary scheduling processes.
     *
     * @param intent Intent that was used to start the service, allowing it to retrieve
     * any data or commands that were included with the Intent when it was started.
     *
     * The `intent` object has no specific deconstruction as its usage is not relevant
     * in this context.
     *
     * @param flags flags associated with the start command, which can be used to control
     * the behavior of the service's lifecycle management and interaction with other components.
     *
     * @param startId 32-bit identifier that uniquely identifies the service, allowing
     * it to be restarted if terminated by the system.
     *
     * @returns a code indicating the service's status.
     *
     * The function returns an integer value indicating the state of the service's command.
     * The START_STICKY constant is used to indicate that this service will be restarted
     * if the system terminates it while it is in the foreground.
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
     * Returns a `SensorEventListener` that handles sensor data changes and saves them
     * to a remote service for analysis. It creates a sample object, formats values,
     * attaches location data, and updates a Scheduled object before stopping the sensor
     * and service if no more tasks are pending.
     *
     * @param scheduledSensable Scheduled object that is updated with the sensor ID,
     * sample values, and other metadata before stopping the sensor and service if no
     * more tasks are pending.
     *
     * - Location: double array representing longitude and latitude.
     * - Sample: an object with timestamp, values (event.values[0]), location, and other
     * metadata.
     * - Sensor type: name of the sensor.
     * - Internal Sensor ID: the type of internal sensor.
     * - Unit: determined by SensorHelper.
     * - Private Sensor: a boolean indicating whether it is private or not.
     * - Access token: obtained from getUserAccessToken.
     *
     * @returns a `SensorEventListener`.
     *
     * It returns an instance of SensorEventListener, which includes two methods:
     * onSensorChanged() and onAccuracyChanged(). The onSensorChanged() method handles
     * sensor data received from a listener and saves it to a remote service for analysis.
     * It creates a sample object, formats its values, and attaches location data before
     * sending it to the service for storage.
     */
    private SensorEventListener getListener(final ScheduledSensable scheduledSensable) {
        return new SensorEventListener() {
            /**
             * Handles sensor data changes by logging the event, creating a sample object and
             * sending it to a remote server using Retrofit, and then unregisters the sensor
             * listener and stops the service if there are no more pending tasks.
             *
             * @param event 3D sensor data changed event, which contains information about the
             * sensor type and its values.
             *
             * - `SensorEvent event`: The event contains sensor data and information about the
             * sensor that triggered the event. The main properties include:
             *   - `values`: An array of float values representing the raw sensor data.
             *   - `sensor`: A `Sensor` object providing metadata about the sensor that triggered
             * this event, such as its name and type.
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
                     * Logs a debug message indicating successful posting of a sample to the Android
                     * logcat with the specified TAG.
                     *
                     * @param success SampleResponse object returned as a result of successful posting operation.
                     *
                     * @param response HTTP response object that contains information about the HTTP
                     * request, including its status code and headers.
                     */
                    @Override
                    public void success(SampleResponse success, Response response) {
                        Log.d(TAG, "Success posting sample");
                    }

                    /**
                     * Logs an error message when a request made by Retrofit fails to complete successfully.
                     * It takes a `RetrofitError` object as input, which provides information about the
                     * failure. The error message includes the `TAG` and a string representation of the
                     * `retrofitError`.
                     *
                     * @param retrofitError exception or error that occurred during the execution of the
                     * Retrofit request, providing detailed information about the failure.
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
             * Monitors changes to the accuracy level of a sensor. It is triggered when the
             * accuracy level of a sensor varies, providing information about the sensor's current
             * accuracy status. The function takes two parameters: the sensor that has experienced
             * an accuracy change and the new accuracy level.
             *
             * @param sensor sensor whose accuracy has changed, providing information about the
             * specific sensor being monitored.
             *
             * @param accuracy level of accuracy reported by the sensor, with higher values
             * indicating better accuracy and lower values indicating worse accuracy.
             */
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    /**
     * Binds a client to a specific interface of a service and returns an `IBinder` object
     * representing that interface. This implementation returns null, indicating that the
     * service does not provide any binding interfaces. The client will be unable to bind
     * to this service.
     *
     * @param intent Intent object that contains the action to be performed and any
     * additional data required for processing.
     *
     * @returns a null object of type `IBinder`.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * Retrieves the user's access token from shared preferences if the user is logged
     * in and has an access token. If not, it returns an empty string. The function logs
     * debug messages with user credentials for verification purposes.
     *
     * @returns either an access token or an empty string.
     *
     * Returns a String representing the user's access token if logged in and has an
     * access token; otherwise, returns an empty string. The output can be further
     * deconstructed into two possible cases: either it contains the actual access token
     * or it is an empty string.
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
     * Retrieves the last known location from a network provider using a Location Manager
     * service provided by the Android system. It returns the current location, which may
     * not be accurate or up-to-date. The result is passed as an object of type `Location`.
     *
     * @returns a `Location` object containing the last known location.
     */
    private Location getLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        return locationManager.getLastKnownLocation(locationProvider);
    }


}
