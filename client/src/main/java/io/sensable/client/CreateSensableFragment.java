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
 * Manages the creation of new sensables and their scheduling for repeated execution
 * at a later time. It sets up a UI with a spinner to select sensor types, a text
 * field for inputting sensable IDs, and a button to create new sensables. Upon
 * clicking the button, it creates a new ScheduledSensable object, schedules it, saves
 * it locally, and displays a notification to the user.
 */
public class CreateSensableFragment extends DialogFragment {

    private static final String TAG = CreateSensableFragment.class.getSimpleName();

    private List<Sensor> sensorList;
    private Spinner sensorSpinner;
    private EditText sensableId;
    private Button submitButton;
    private CreateSensableListener createSensableListener;

    /**
     * Provides a notification callback for when a scheduled sensable is confirmed.
     */
    public static interface CreateSensableListener {
        public void onConfirmed(ScheduledSensable scheduledSensable);
    }

    public CreateSensableFragment() {
    }

    /**
     * Inflates a layout and initializes UI components for creating a sensable entity,
     * including a spinner listing all available sensors and an edit text field for
     * entering a unique ID. It also sets up an adapter for the sensor spinner and adds
     * an event listener to a button.
     *
     * @param inflater LayoutInflater object that inflates (or creates) the layout from
     * the resource file R.layout.create_sensable_layout into a View object.
     *
     * Inflate. The inflater is an instance of LayoutInflater.
     *
     * @param container 2D ViewGroup that this new View is to be added to, and serves as
     * the parent ViewGroup for the inflated layout.
     *
     * Passed as a `ViewGroup`, containing the view hierarchy to be inflated into.
     *
     * @param savedInstanceState Bundle object that contains the activity's previously
     * frozen state from an earlier lifecycle, which is not utilized in this method.
     *
     * Bundle object containing the activity's state at the time that the user touches
     * back button or presses Home key to save its state and restore later. It does not
     * contain any specific information in this case.
     *
     * @returns a View object representing the create sensable layout.
     *
     * The output is a `View`, which represents the layout of the dialog. The view consists
     * of a sensor spinner and an edit text field for creating sensable objects. It also
     * contains a title set to "dialogTitleCreateSensable" from the string resource.
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
     * Assigns a specified `CreateSensableListener` object to an instance variable,
     * allowing other parts of the program to interact with it as needed. This enables
     * event handling or notification mechanisms within the class. The assigned listener
     * is responsible for responding to events triggered by this class.
     *
     * @param createSensableListener listener to be set for creating sensable entities,
     * which is stored as an instance variable of the class.
     */
    public void setCreateSensableListener(CreateSensableListener createSensableListener) {
        this.createSensableListener = createSensableListener;
    }

    /**
     * Creates a new scheduled sensable object based on user input and schedules it using
     * a listener, creates a bookmark for the sensable, and dismisses the fragment. It
     * also handles Retrofit errors and displays error messages to the user.
     *
     * @param view View object that contains the submit button and sensor spinner, allowing
     * the code to find and manipulate these views within the method.
     *
     * Find: `sensorSpinner`, `submitButton`.
     */
    public void addListenerOnButton(View view) {

        sensorSpinner = (Spinner) view.findViewById(R.id.sensor_spinner);

        submitButton = (Button) view.findViewById(R.id.create_sensable_button);

        submitButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Creates a scheduled sensable object and schedules it, then creates a favorite
             * bookmark. It retrieves sensor ID from spinner, location from last known location,
             * and unit from SensorHelper. The function uses Retrofit to create the sensable
             * object and schedule it.
             *
             * @param v View object that triggered the onClick event, which is not used in this
             * function.
             *
             * View v - An object representing the View that was clicked. The main properties include:
             *
             * - getId(): Returns the unique integer ID of the view.
             * - getClass(): Returns the runtime class of this Object.
             * - hashCode(): Returns a hash code value for the object.
             * - toString(): Returns a string representation of the object.
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
                         * Logs a callback success message and updates sensor IDs for two objects. It then
                         * schedules an object creation, notifies a listener about confirmation, and dismisses
                         * the current state.
                         *
                         * @param sampleResponse response returned by the service that contains the sensor
                         * ID and message, which is then used to set the sensor ID for the sensable objects
                         * and log the callback success.
                         *
                         * Get the message from the response and log it;
                         * Extract sensorid from sampleResponse.
                         *
                         * @param response response from the service call and is not used explicitly within
                         * the method.
                         *
                         * The `response` has no direct explanation, as it is not explicitly utilized within
                         * this method.
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
                         * Handles an error occurred during a Retrofit request, logs the error and displays
                         * a toast message to the user with a failure message "Could not create that Sensable".
                         *
                         * @param retrofitError error that occurred during the Retrofit request and provides
                         * information about the failure.
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
     * Retrieves a user's access token from shared preferences if they are logged in and
     * have an existing token. If not, it returns an empty string. It logs debug messages
     * for the preference file key, saved username, and access token to the console.
     *
     * @returns either an existing user's access token or an empty string.
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
     * Creates a scheduled entry for a given `sensable` object using an instance of
     * `ScheduleHelper`. It adds the sensable to the scheduler, starts it if necessary,
     * and saves a local bookmark to the sensable. The function returns a boolean indicating
     * success or failure.
     *
     * @param scheduledSensable object that is being scheduled and added to the scheduler,
     * facilitating the creation of a schedule entry.
     *
     * Has properties of type.
     *
     * @param sensable object that is being saved and added to the scheduler, which
     * requires a corresponding local bookmark to be created.
     *
     * Deserialized and stored in a Sensable object, its main properties include:
     *
     * @returns a boolean value indicating success or failure.
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
     * Inserts a new Sensable object into the SQLite database using the ContentResolver's
     * insert method. It serializes the Sensable object into ContentValues and then inserts
     * these values into the user dictionary content URI, returning true upon successful
     * insertion.
     *
     * @param sensable object that is being serialized and inserted into a SQLite database
     * via the `ContentResolver` interface.
     *
     * @returns a boolean value indicating successful insertion of data into SQLite database.
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
     * Retrieves an object representing a sensor from a list based on its name, and then
     * returns the type of that sensor if found; otherwise, it returns -1. It iterates
     * through the list to find a match for the given sensor name.
     *
     * @param sensorName name of the sensor to be searched for in the `sensorList`,
     * allowing the function to retrieve its corresponding type.
     *
     * @returns an integer value representing the sensor type.
     *
     * The output is an integer value representing the sensor ID or -1 if no matching
     * sensor name is found in the list.
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
     * Retrieves the last known location from a network provider using a location manager,
     * which is obtained through the activity's system service.
     *
     * @returns a `Location` object representing the last known location.
     */
    private Location getLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        return locationManager.getLastKnownLocation(locationProvider);
    }


}

