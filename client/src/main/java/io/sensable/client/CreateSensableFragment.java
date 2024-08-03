package io.sensable.client;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import io.sensable.SensableService;
import io.sensable.client.scheduler.ScheduleHelper;
import io.sensable.client.sqlite.SavedSensablesTable;
import io.sensable.client.sqlite.SensableContentProvider;
import io.sensable.model.Sample;
import io.sensable.model.SampleResponse;
import io.sensable.model.ScheduledSensable;
import io.sensable.model.Sensable;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;


/**
 * is responsible for creating and scheduling a sensory data object to be created by
 * the Sensable API. The fragment includes a spinner for selecting the sensor type,
 * a button for creating the scheduled sensory object, and a function for saving the
 * sensory data to the local bookmark after it has been created and scheduled.
 */
public class CreateSensableFragment extends DialogFragment {

    private static final String TAG = CreateSensableFragment.class.getSimpleName();

    private List<Sensor> sensorList;
    private Spinner sensorSpinner;
    private EditText sensableId;
    private Button submitButton;
    private CreateSensableListener createSensableListener;

    /**
     * allows for listeners to receive notifications when a scheduled sensable is confirmed.
     */
    public static interface CreateSensableListener {
        public void onConfirmed(ScheduledSensable scheduledSensable);
    }

    public CreateSensableFragment() {
    }

    /**
     * inflates a layout, sets up a spinner to display sensor types, creates an EditText
     * for entering sensable ID, and adds a button listener for creating a new sensable.
     * 
     * @param inflater inflater object that is used to inflate the layout for the fragment's
     * user interface.
     * 
     * 	- `inflater`: An instance of the `LayoutInflater` class, which is used to inflate
     * layout files into views.
     * 	- `container`: A reference to a `ViewGroup` object that represents the parent
     * container for the view being created.
     * 	- `savedInstanceState`: A `Bundle` object that contains any saved state from a
     * previous activity session, or null if this is the first time the activity is being
     * created.
     * 
     * @param container ViewGroup that will hold the created view.
     * 
     * 	- `inflater`: The `LayoutInflater` object used to inflate the layout.
     * 	- `container`: A `ViewGroup` object that holds the views created by the `inflate`
     * method.
     * 	- `savedInstanceState`: A `Bundle` object containing any saved state from a
     * previous activity or fragment.
     * 
     * The properties of the input `container` are not explicitly mentioned, as it is
     * assumed to be a standard `ViewGroup` with the usual properties and attributes.
     * 
     * @param savedInstanceState saved state of the activity, including any data or values
     * that were previously stored and can be used to restore the activity's state when
     * it is recreated.
     * 
     * 	- `getActivity()`: This is a reference to the activity that the fragment belongs
     * to.
     * 	- `getString(R.string.dialogTitleCreateSensable)`: This is a string resource ID
     * that represents the title of the dialog box.
     * 	- `sensorList`: This is a list of sensor objects that are returned by the
     * `getSensorList()` method of the `SensorManager`.
     * 	- `sensorSpinner`: This is a Spinner widget that displays a list of sensors
     * available on the device.
     * 	- `sensableId`: This is an EditText field where the user can enter the ID of the
     * sensable they want to create.
     * 	- `addListenerOnButton()`: This is a method that adds an listener to a button in
     * the layout.
     * 
     * Note: The `savedInstanceState` object may contain additional properties or attributes
     * depending on how the fragment was saved and restored.
     * 
     * @returns a layout with a spinner to select a sensor type and an edit text field
     * to enter the sensable ID.
     * 
     * 	- `sensorManager`: A reference to the `SensorManager` object that provides access
     * to the sensors on the device.
     * 	- `sensorList`: A list of `Sensor` objects that represent the available sensors
     * on the device.
     * 	- `view`: The inflated view from the layout file specified in the `inflate()` method.
     * 	- `getDialog().setTitle()`: Sets the title of the dialog box displayed when the
     * user creates a new sensable.
     * 	- `listSensorType`: A list of sensor types that are available on the device.
     * 	- `sensorSpinner`: A Spinner component that allows the user to select a sensor
     * type from the list of available sensors.
     * 	- `sensableId`: An EditText field where the user can enter the ID of the newly
     * created sensable.
     * 	- `addListenerOnButton()`: Adds an event listener to a button on the screen to
     * handle button clicks.
     * 
     * In summary, the `onCreateView` function returns a view that provides a user interface
     * for creating a new sensable, including a list of available sensors, an EditText
     * field for entering the ID of the sensable, and a button with an event listener to
     * handle button clicks.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        View view = inflater.inflate(R.layout.create_sensable_layout, null);

        getDialog().setTitle(getActivity().getString(R.string.dialogTitleCreateSensable));

        List<String> listSensorType = new ArrayList<String>();
        for (int i = 0; i < sensorList.size(); i++) {
            listSensorType.add(sensorList.get(i).getName());
        }

        sensorSpinner = (Spinner) view.findViewById(R.id.sensor_spinner);

        sensableId = (EditText) view.findViewById(R.id.create_sensable_id);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, listSensorType);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sensorSpinner.setAdapter(spinnerArrayAdapter);

        addListenerOnButton(view);

        return view;
    }

    /**
     * sets a reference to a `CreateSensableListener` object, which allows the caller to
     * receive notifications when sensory data is created.
     * 
     * @param createSensableListener CreateSensableListener object that will receive
     * notifications when the sensors are created or removed.
     * 
     * 	- This method sets the `createSensableListener` field of the current object to
     * the provided `CreateSensableListener` object.
     * 	- The `CreateSensableListener` class is a reference type that represents an object
     * with properties and methods related to creating sensational experiences.
     * 	- The `createSensableListener` field is of type `CreateSensableListener`, indicating
     * that it holds an instance of this class.
     * 	- The field can be accessed and modified through this method, allowing the object
     * to update its internal state based on the provided listener.
     */
    public void setCreateSensableListener(CreateSensableListener createSensableListener) {
        this.createSensableListener = createSensableListener;
    }

    /**
     * schedules a sensable object for creation and bookmarking on the Sensable API, using
     * the sensor ID selected from the spinner and the last known location of the device.
     * 
     * @param view View object that was clicked, and it is used to identify the specific
     * action to be taken based on the type of view that was clicked.
     * 
     * 	- `sensableId`: A string containing the text entered by the user in the `Sensable
     * ID` field.
     * 	- `sensorSpinner`: An item from a spinner representing the sensor type selected
     * by the user.
     * 	- `lastKnownLocation`: The last known location of the device, represented as a
     * `Location` object.
     * 	- `getUserAccessToken()`: A method that returns an access token for the current
     * user.
     * 
     * The function takes the input `view` and processes it accordingly to create a new
     * `Sensable` object, schedule it, save it in the database, and display a notification
     * to the user.
     */
    public void addListenerOnButton(View view) {

        sensorSpinner = (Spinner) view.findViewById(R.id.sensor_spinner);

        submitButton = (Button) view.findViewById(R.id.create_sensable_button);

        submitButton.setOnClickListener(new View.OnClickListener() {

            /**
             * creates a new scheduled sensable object and sets its sensor ID to the same value
             * as the sensable object. It then schedules the sensable using a listener, creates
             * a bookmark for the sensable, and dismisses the fragment.
             * 
             * @param v View that was clicked, providing the event trigger for the function to execute.
             * 
             * 	- `sensableId`: The text value of the `sensableId` field in the layout.
             * 	- `sensorSpinner`: A reference to the `SensorSpinner` view in the layout.
             * 	- `getLocation()`: A method that returns the current location of the device, which
             * is used to create a bookmark for the scheduled sensable.
             * 	- `getUserAccessToken()`: A method that returns the access token for the user,
             * which is used to authenticate the API call to create the sensable.
             * 	- `restAdapter`: An instance of `RestAdapter` that builds the service client for
             * the Sensable API.
             * 	- `SensableService`: An interface for interacting with the Sensable API.
             * 	- `createSensableListener`: An instance of `CreateSensableListener` responsible
             * for handling the creation of the favorite bookmark.
             * 	- `scheduledSensable`: A `Sensable` object representing the scheduled sensable
             * that is to be created.
             * 	- `sensable`: A `Sensable` object representing the sensable that is being created.
             */
            @Override
            public void onClick(View v) {
                if (sensableId.getText().toString().length() > 0) {

                    int sensorId = getSensorId(sensorSpinner.getSelectedItem().toString());

                    // Create the object for scheduling
                    final ScheduledSensable scheduledSensable = new ScheduledSensable();
                    scheduledSensable.setSensorid(sensableId.getText().toString());
                    scheduledSensable.setInternalSensorId(sensorId);
                    scheduledSensable.setName(sensableId.getText().toString());
                    scheduledSensable.setSensortype(sensorSpinner.getSelectedItem().toString());
                    scheduledSensable.setUnit(SensorHelper.determineUnit(sensorId));
                    scheduledSensable.setPending(0);

                    Location lastKnownLocation = getLocation();

                    //Create the bookmarkable object
                    final Sensable sensable = new Sensable();
                    sensable.setSensorid(sensableId.getText().toString());
                    sensable.setUnit(scheduledSensable.getUnit());
                    sensable.setName(sensableId.getText().toString());
                    sensable.setSensortype(sensorSpinner.getSelectedItem().toString());
                    sensable.setLocation(new double[]{lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude()});
                    sensable.setSamples(new Sample[]{});
                    sensable.setAccessToken(getUserAccessToken());

                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setLogLevel(RestAdapter.LogLevel.FULL)
                            .setEndpoint("http://sensable.io")
                            .build();

                    SensableService service = restAdapter.create(SensableService.class);

                    service.createSensable(sensable, new Callback<SampleResponse>() {
                        /**
                         * handles a callback from the service, which returns the canonical sensable ID. The
                         * ID is set on the `sensable` and `scheduledSensable` objects before saving them,
                         * and then a favorite bookmark is created for the scheduled sensable.
                         * 
                         * @param sampleResponse response from the service call, which contains the canonical
                         * sensable ID that is set to the corresponding object before saving and scheduling
                         * it.
                         * 
                         * 	- `getMessage()`: The callback message returned by the service.
                         * 	- `sensableid`: The canonical sensable ID returned by the service, set in the
                         * `success` function.
                         * 	- `scheduledSensable`: A scheduled sensable object, which is created and set in
                         * the `success` function.
                         * 	- `createSensableListener`: An object that implements `OnConfirmed`, which is
                         * called after scheduling the sensable.
                         * 
                         * @param response response from the service, which contains the canonical sensable
                         * ID that is used to set the sensor ID of the scheduled sensable and the favourite
                         * bookmark.
                         * 
                         * 	- `response`: The response object contains the result of the callback, including
                         * the sensable ID in the `sensableid` attribute.
                         * 	- `sampleResponse`: The deserialized response from the service, containing the
                         * sensor ID in the `sensableid` attribute.
                         * 	- `scheduledSensable`: A scheduled sensable object that will be created and saved
                         * with the correct sensor ID.
                         * 	- `createScheduledSensable()`: A function that creates a scheduled sensable object
                         * and saves it along with the original sensable object.
                         * 	- `createSensableListener()`: An object that will receive confirmation when the
                         * scheduled sensable is created.
                         */
                        @Override
                        public void success(SampleResponse sampleResponse, Response response) {
                            Log.d(TAG, "Callback Success: " + sampleResponse.getMessage());

                            // The service returns the canonical sensableID. Set that before saving the objects.
                            sensable.setSensorid(sampleResponse.getSensorid());
                            scheduledSensable.setSensorid(sampleResponse.getSensorid());

                            // Schedule the sensable then create the favourite bookmark
                            createScheduledSensable(scheduledSensable, sensable);
                            createSensableListener.onConfirmed(scheduledSensable);
                            dismiss();
                        }

                        /**
                         * is called when a Retrofit error occurs during the creation of an object. It logs
                         * the error message and displays a toast notification to the user indicating that
                         * the object could not be created.
                         * 
                         * @param retrofitError error message resulting from the failure of the Retrofit call.
                         * 
                         * 	- `toString()` returns a human-readable string representation of the error object.
                         * 	- `Log.e(TAG, "Callback failure: " + retrofitError.toString());` logs a message
                         * with the error details to the Android LogCat.
                         * 	- `Toast.makeText(getActivity(), "Could not create that Sensable",
                         * Toast.LENGTH_SHORT).show();"` displays an informative message to the user via a
                         * Toast notification.
                         */
                        @Override
                        public void failure(RetrofitError retrofitError) {
                            Log.e(TAG, "Callback failure: " + retrofitError.toString());
                            Toast.makeText(getActivity(), "Could not create that Sensable", Toast.LENGTH_SHORT).show();
                        }
                    });


                } else {
                    Toast.makeText(getActivity(), "Sensable ID is required", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    /**
     * retrieves and returns an access token for a user based on their saved login
     * credentials. If the user is logged in, it checks if they have an access token saved
     * and returns it, otherwise it returns an empty string.
     * 
     * @returns a non-empty access token for a user who has been authenticated and has
     * an access token saved in the application's shared preferences.
     * 
     * 	- `accessToken`: This is the access token retrieved from the shared preferences
     * file. It is a string variable that contains the access token for the user.
     * 	- `loggedIn`: This is a boolean variable that indicates whether the user is logged
     * in or not. If the user is logged in, the `accessToken` variable will be non-empty.
     * 	- `username`: This is the username of the user retrieved from the shared preferences
     * file. It is also a string variable.
     * 
     * The function first checks if the user is logged in by checking the `loggedIn`
     * variable. If the user is not logged in, the function returns an empty string.
     * Otherwise, it retrieves the access token and username from the shared preferences
     * file using the `getString` method, and then returns the access token as a string.
     */
    private String getUserAccessToken() {
        SensableUser user = new SensableUser(getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE), getActivity());
        if (user.loggedIn) {
            if (user.hasAccessToken) {
                Log.d(TAG, "Loading Access token");
                Log.d(TAG, getString(R.string.preference_file_key));
                Log.d(TAG, getString(R.string.saved_access_token));
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
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
     * creates a scheduled entry for a sensable in a scheduler and saves it locally.
     * 
     * @param scheduledSensable sensable that is to be added to the scheduler for repeated
     * execution at a later time.
     * 
     * 	- `ScheduleHelper scheduleHelper`: A new instance of `ScheduleHelper`, created
     * with the current `Activity`.
     * 	- `addSensableToScheduler(scheduledSensable)`: An attempt to add the sensable to
     * the scheduler. Returns `false` if the addition fails.
     * 	- `startScheduler()`: Starts the scheduler, if it is not already running.
     * 	- `saveSensable(sensable)`: Tries to save the sensable to the local bookmark.
     * Returns `true` if the save succeeds.
     * 
     * @param sensable sensable data that is to be scheduled and saved locally by the function.
     * 
     * 	- `sensable`: This is an instance of the `Sensable` class, which has attributes
     * such as `id`, `type`, `name`, and `data`. These attributes are used to store
     * information about the sensory input.
     * 	- `ScheduleHelper`: This is an instance of a custom class called `ScheduleHelper`,
     * which provides utility methods for working with scheduling functionality in the activity.
     * 	- `addSensableToScheduler()`: This method is part of the `ScheduleHelper` class
     * and is used to add the `sensable` instance to the scheduler. It takes the
     * `scheduledSensable` instance as a parameter and adds it to the scheduler if successful.
     * 	- `startScheduler()`: This method is also part of the `ScheduleHelper` class and
     * is used to start the scheduler. It is called after adding the `sensable` instance
     * to the scheduler to ensure that the scheduler is running.
     * 	- `saveSensable(sensable)`: This method is used to save the `sensable` instance
     * to the activity's database. It returns `true` if the sensory input was saved
     * successfully, otherwise it returns `false`.
     * 
     * @returns a boolean value indicating whether the scheduled sensable was successfully
     * created and added to the scheduler.
     */
    private boolean createScheduledSensable(ScheduledSensable scheduledSensable, Sensable sensable) {
        ScheduleHelper scheduleHelper = new ScheduleHelper(getActivity());

        Log.d(TAG, "Creating Scheduler");

        // Try to create schedule entry
        if (!scheduleHelper.addSensableToScheduler(scheduledSensable)) {
            return false;
        }

        //Give the scheduler a kick in case it isn't already running.
        scheduleHelper.startScheduler();

        // Try to create local bookmark to this sensable
        return saveSensable(sensable);

    }

    /**
     * Serializes a given `Sensable` object using the `SavedSensablesTable` class, inserts
     * its values into a SQLite database through the `ContentResolver`, and returns `true`
     * upon successful insertion.
     * 
     * @param sensable Sensable object that contains the data to be saved in the SQLite
     * database.
     * 
     * 	- `ContentValues`: This is an Android class that serializes data into a form
     * suitable for database storage. The input `mNewValues` is an instance of this class.
     * 	- `getActivity().getContentResolver()`: This method returns the content resolver
     * associated with the current activity, which is used to interact with the database.
     * 	- `insert()`: This method inserts new data into a database table. In this case,
     * it inserts the deserialized `sensable` data into the `SavedSensablesTable`.
     * 	- `Uri`: This is a type-safe identifier for a database table or view. The output
     * of the `insert()` method is a `Uri` object that represents the newly inserted row
     * in the database.
     * 
     * @returns a boolean value indicating whether the sensable was successfully saved
     * to the database.
     */
    private boolean saveSensable(Sensable sensable) {
        ContentValues mNewValues = SavedSensablesTable.serializeSensableForSqlLite(sensable);

        Uri mNewUri = getActivity().getContentResolver().insert(
                SensableContentProvider.CONTENT_URI,   // the user dictionary content URI
                mNewValues                          // the values to insert
        );
        return true;
    }

    /**
     * retrieves a sensor ID based on its name, searching a list of sensors and returning
     * -1 if the sensor is not found.
     * 
     * @param sensorName name of the sensor for which the sensor ID is to be retrieved.
     * 
     * @returns an integer representing the sensor ID of the chosen sensor.
     */
    private int getSensorId(String sensorName) {
        Sensor chosenSensor = null;
        for (Sensor aSensorList : sensorList) {
            if (aSensorList.getName().equals(sensorName)) {
                chosenSensor = aSensorList;
            }
        }

        if (chosenSensor == null) {
            return -1;
        }
        return chosenSensor.getType();
    }

    /**
     * retrieves the last known location of a device using the `LocationManager`. It
     * returns the obtained location.
     * 
     * @returns a `Location` object containing the most recent location data from the
     * network provider.
     * 
     * 	- The Location object represents the current location of the device, as determined
     * by the device's location providers.
     * 	- The `getLastKnownLocation` method returns the last known location of the device
     * for the specified provider.
     * 	- The `locationProvider` parameter is a string that identifies the location
     * provider to use for determining the location (e.g., `LocationManager.GPS_PROVIDER`).
     * 	- The returned Location object has various attributes, such as latitude, longitude,
     * altitude, and accuracy, which can be accessed through methods like `getLatitude()`,
     * `getLongitude()`, `getAltitude()`, and `getAccuracy()`.
     */
    private Location getLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        return locationManager.getLastKnownLocation(locationProvider);
    }


}

