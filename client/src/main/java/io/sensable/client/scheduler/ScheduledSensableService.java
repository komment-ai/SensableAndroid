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
 * is an Android service that extends Service and is responsible for sampling sensors
 * based on a schedule. The service gets the sensor manager and sensor from the system,
 * registers a listener to receive sensor data, and schedules the sensor to be sampled
 * at a later time using a scheduler. When the sensor data is received, it creates a
 * new sample object with the timestamp, sensor value, and location (if available).
 * The service then updates the scheduled sensable object with the new sample and
 * saves it to the database. Finally, the service stops the sensor listener and
 * scheduler if no more samples are needed.
 */
public class ScheduledSensableService extends Service {

    private static final String TAG = ScheduledSensableService.class.getSimpleName();

    private SensorManager sensorManager = null;
    private Sensor sensor = null;

    /**
     * starts a service, initializes a sensor manager, and registers listeners on sensors
     * to collect data from scheduled sensables.
     * 
     * @param intent start command for the service, which is used to initiate the startup
     * process.
     * 
     * 	- `intent`: The Intent object that starts the service. It contains information
     * about why the service is being started and what it should do when it is started.
     * 	- `flags`: An integer value that represents the reason why the service was started.
     * This can be one of the following values: `START_STICKY`, `START_NOT_COOKIE`, or `START_COOKIE`.
     * 	- `startId`: A unique identifier for the start request, used to identify the
     * request and handle it appropriately.
     * 
     * @param flags 3-bit value that indicates whether the service should be started in
     * the foreground or background, with possible values of 0 (background), 1 (foreground),
     * or 2 (persistent).
     * 
     * @param startId ID of the service that started the `onStartCommand()` method execution
     * and is used to identify the service instance for further operations.
     * 
     * @returns a sticky start result of START_STICKY.
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
     * creates a SensorEventListener instance that listens for sensor changes and saves
     * them to a ScheduledSensable object. It also stops the sensor and service when no
     * more samples are pending.
     * 
     * @param scheduledSensable ScheduledSensable object that contains the sensor data
     * to be processed and saved, and is used to update its location and sample fields
     * before saving it to the service.
     * 
     * 	- `scheduledSensable`: This is the scheduled sensable object that contains
     * information about the sensor, location, and sample data. It has various
     * attributes/properties such as `sensorid`, `location`, `sample`, `sensortype`,
     * `unit`, `privatesensor`, `accesstoken`, and `scheduleid`.
     * 	- `sensorid`: This is the unique identifier of the sensor associated with the
     * scheduled sensable task.
     * 	- `location`: This is the current location of the device, which can be a latitude
     * and longitude pair.
     * 	- `sample`: This is the sample data generated by the sensor, which includes the
     * timestamp, value, and other metadata.
     * 	- `sensortype`: This is the type of sensor associated with the scheduled sensable
     * task.
     * 	- `unit`: This is the unit of measurement for the sensor data.
     * 	- `privatesensor`: This is a boolean flag indicating whether the sensor is private
     * or not.
     * 	- `accesstoken`: This is the access token required to post the sample data to the
     * server.
     * 	- `scheduleid`: This is the unique identifier of the scheduled sensable task.
     * 
     * @returns a SensorEventListener that listens to sensor changes and saves them to a
     * remote service.
     * 
     * 	- `scheduledSensable`: This is an instance of the `ScheduledSensable` class, which
     * contains information about the scheduled sampling task.
     * 	- `sample`: This is a sample object that represents the sensor data. It has several
     * attributes, including `timestamp`, `value`, `location`, and `sensortype`.
     * 	- `lastKnownLocation`: This is a `Location` object that represents the last known
     * location of the device.
     * 	- `getUserAccessToken`: This is a method that returns a token used to authenticate
     * requests to the Sensable API.
     * 	- `service`: This is an instance of the `RestAdapter` class, which provides a way
     * to interact with the Sensable API.
     * 	- `sampleSender`: This is an object that encapsulates the logic for sending sensor
     * data to the Sensable API. It has methods for setting the access token and sample
     * data.
     * 	- `scheduleHelper`: This is an instance of the `ScheduleHelper` class, which
     * provides utilities for managing scheduling tasks.
     * 	- `countPendingScheduledTasks`: This is a method that returns the number of pending
     * scheduled tasks.
     * 	- `stopSelf`: This is a method that stops the sampling task and service.
     * 	- `scheduleHelper.stopSchedulerIfNotNeeded`: This is a method that stops the
     * scheduler if it is not needed.
     */
    private SensorEventListener getListener(final ScheduledSensable scheduledSensable) {
        return new SensorEventListener() {
            /**
             * handles sensor data received from a listener and saves it to a remote service for
             * analysis. It creates a sample object, formats its values, and attaches location
             * data before sending it to the service for storage. The function also updates a
             * Scheduled object with the sensor ID, sample values, and other metadata before
             * stopping the sensor and service if no more tasks are pending.
             * 
             * @param event event that triggered the onSensorChanged() method and contains
             * information about the sensor value change, which is used to create a sample object
             * and send it to the Sensable service for processing.
             * 
             * 	- `sensor`: The sensor that triggered the callback, providing information about
             * the changed value.
             * 	- `values`: An array of values representing the raw data from the sensor.
             * 	- `timestamp`: The timestamp when the sensor was last updated.
             * 
             * Note that the `event` object may also contain other properties or attributes
             * depending on the specific sensor and implementation used.
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
                     * is called when a sample is posted successfully. It logs a message to the debug log
                     * indicating that the sample was successfully posted.
                     * 
                     * @param success successful posting of a sample.
                     * 
                     * 	- `success`: A `SampleResponse` object that represents the successful post request.
                     * It contains information about the posted sample, such as its ID and name.
                     * 
                     * @param response result of the API call made by the `postSample` method, which
                     * contains information about the success or failure of the sample posting operation.
                     * 
                     * 	- `success`: A boolean indicating whether the posting was successful (true) or
                     * not (false).
                     * 	- `response`: A JSON object representing the response from the server, containing
                     * various attributes such as error messages or HTTP status codes.
                     */
                    @Override
                    public void success(SampleResponse success, Response response) {
                        Log.d(TAG, "Success posting sample");
                    }

                    /**
                     * is called when a failure occurs during the posting of a sample. It logs an error
                     * message with the tag `TAG`.
                     * 
                     * @param retrofitError error that occurred during the API call, which is logged to
                     * the app's logcat using the `Log.e()` method.
                     * 
                     * 	- `toString()` returns a string representation of the error object, which can be
                     * used for logging or further analysis.
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
             * is called when the accuracy of a sensor changes. It does not provide any information
             * or take any action related to the changed accuracy.
             * 
             * @param sensor sensor that has its accuracy changed through the call to the
             * `onAccuracyChanged()` method.
             * 
             * 	- `sensor`: A class that represents a sensor, providing information about its accuracy.
             * 	- `accuracy`: An integer representing the level of accuracy of the sensor's readings.
             * 
             * @param accuracy level of accuracy achieved by the sensor after filtering or correction.
             */
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    /**
     * returns a null `IBinder`, indicating that no binding is required for the specified
     * `Intent`.
     * 
     * @param intent binding request from an activity or service that is being handled
     * by the `onBind()` method.
     * 
     * - It is an instance of `Intent`, which is a class representing an intent.
     * - The `intent` object contains information about the activity or action that
     * triggered its creation, as well as any data to be passed to the new activity or
     * service when it is started using the `startActivity()` or `startService()` method.
     * - `onBind` is called when the system needs to bind an `IBinder` instance to a
     * specific component (e.g., an activity or service) so that it can communicate with
     * that component.
     * 
     * @returns `null`.
     * 
     * 	- The output is an `IBinder` object, which represents a binding between a client
     * and a service.
     * 	- The `onBind` method returns null, indicating that no binder is provided for
     * this component.
     * 	- This means that there is no way to bind to the service offered by this component.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * retrieves and returns an access token for a user based on their logged-in status
     * and saved credentials.
     * 
     * @returns a valid access token for the user.
     * 
     * 	- `accessToken`: This is the access token obtained from the user's preferences.
     * It is a string value that represents the user's access token.
     * 	- `hasAccessToken`: This is a boolean value that indicates whether an access token
     * is available for the user. If it is set to true, then an access token is available,
     * and if it is set to false, then no access token is available.
     * 	- `loggedIn`: This is a boolean value that indicates whether the user is logged
     * in or not. If it is set to true, then the user is logged in, and if it is set to
     * false, then the user is not logged in.
     * 	- `username`: This is the username associated with the user's access token. It
     * is a string value that represents the user's username.
     * 
     * These properties provide information about the user's access token and their login
     * status, which can be used to determine whether the user has access to certain
     * resources or features.
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
     * retrieves the user's current location using the Android Location Manager. It returns
     * the last known location obtained from the network provider.
     * 
     * @returns a `Location` object representing the device's current location, obtained
     * from the system-provided Location Manager.
     * 
     * The function returns a `Location` object representing the current location of the
     * device.
     * The `LocationManager` is used to retrieve the last known location of the device
     * using the `NETWORK_PROVIDER`.
     * The returned `Location` object contains information such as latitude, longitude,
     * altitude, and accuracy.
     */
    private Location getLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        return locationManager.getLastKnownLocation(locationProvider);
    }


}
