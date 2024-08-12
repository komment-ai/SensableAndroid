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
 * Is responsible for creating and scheduling sensables (sensors) in a Sensable API,
 * allowing users to create favorite bookmarks and receive notifications when sensory
 * data is created. It utilizes a Retrofit service to interact with the Sensable API,
 * handles callbacks, and saves sensables locally using a SQLite database.
 */
public class CreateSensableFragment extends DialogFragment {

    private static final String TAG = CreateSensableFragment.class.getSimpleName();

    private List<Sensor> sensorList;
    private Spinner sensorSpinner;
    private EditText sensableId;
    private Button submitButton;
    private CreateSensableListener createSensableListener;

    /**
     * Allows for listeners to receive notifications when a scheduled sensable is confirmed.
     */
    public static interface CreateSensableListener {
        public void onConfirmed(ScheduledSensable scheduledSensable);
    }

    public CreateSensableFragment() {
    }

    /**
     * Inflates a layout, retrieves a list of sensors from the device's SensorManager,
     * and populates a Spinner with their names. It also initializes an EditText field
     * for user input and sets up a button listener. The returned View represents the UI
     * component of this fragment.
     *
     * @param inflater LayoutInflater object that is used to inflate the layout file
     * R.layout.create_sensable_layout into a View.
     *
     * Inflate is an object of type `LayoutInflater`, which takes three parameters -
     * `context`, `viewGroup`, and `savedInstanceState`. Its main property is that it
     * inflates a layout file into a `View` object.
     *
     * @param container 2D grid or stack that the newly created view is to be added to,
     * allowing it to be part of the user interface hierarchy.
     *
     * Container is an object of type `ViewGroup`. Its main properties include its layout
     * parameters and the children views attached to it.
     *
     * @param savedInstanceState Bundle object containing the activity's previous state,
     * which is not used in this code.
     *
     * Bundle object, contains data that was previously saved with ` onSaveInstanceState`.
     *
     * @returns a custom dialog with a spinner and text field.
     *
     * A View object is created by inflating the layout create_sensable_layout with null
     * parent. The title of the dialog is set to a string resource from the activity
     * context. A Spinner widget is populated with a list of sensor names retrieved from
     * a SensorManager instance. An EditText widget is used to input sensable ID.
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
     * Assigns a specified instance to a private field. It takes an object of type
     * `CreateSensableListener` as its parameter and updates the internal state by setting
     * it equal to the received value.
     *
     * @param createSensableListener object to be assigned as the listener for creating
     * sensable events.
     */
    public void setCreateSensableListener(CreateSensableListener createSensableListener) {
        this.createSensableListener = createSensableListener;
    }

    /**
     * Creates a new scheduled sensable object and sets its sensor ID based on user input
     * from a spinner. It then schedules the sensable using a listener, creates a bookmark
     * for it, and dismisses the fragment.
     *
     * @param view View that was clicked, providing the event trigger for the function
     * to execute.
     *
     * • sensorSpinner: A Spinner object that is found by its ID in the view.
     * • submitButton: A Button object that is found by its ID in the view.
     */
    public void addListenerOnButton(View view) {

        sensorSpinner = (Spinner) view.findViewById(R.id.sensor_spinner);

        submitButton = (Button) view.findViewById(R.id.create_sensable_button);

        submitButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Creates a new scheduled sensable and its corresponding bookmark when a user clicks
             * a button, provided that a sensable ID is entered. The function interacts with a
             * RESTful API to create the objects and save them, also handling errors if they occur.
             *
             * @param v View that triggered the onClick event, which is not used explicitly in
             * this function.
             *
             * View v: A View object passed as an argument to this method.
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
                         * Processes a successful callback response by logging the result and setting IDs for
                         * sensors. It schedules the sensable, creates a favourite bookmark, and triggers an
                         * onConfirmed event before dismissing the current view.
                         *
                         * @param sampleResponse response from the service that contains data to be processed,
                         * such as the sensor ID and message.
                         *
                         * Has a getter for message and sensorid.
                         *
                         * @param response response from the service and is not used within the method.
                         *
                         * It has no significant properties mentioned in the provided code snippet.
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
                         * Handles errors that occur during a Retrofit request. It logs an error message with
                         * a tag and displays a toast notification to the user with a custom error message
                         * when the request fails.
                         *
                         * @param retrofitError Retrofit error object that provides information about the
                         * failure of the request.
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
     * Retrieves a user's access token from preferences if they are logged in and have
     * an existing token. If not, it returns an empty string.
     *
     * @returns a valid access token or an empty string.
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
     * Attempts to create a scheduled entry for a `Sensable` object and a local bookmark.
     * It uses a `ScheduleHelper` to add the `Sensable` to the scheduler, starts the
     * scheduler if necessary, and then saves the `Sensable` locally.
     *
     * @param scheduledSensable scheduled entry to be added to the scheduler.
     *
     * ScheduledSensable has no separate explanation since there's nothing more to add
     * about its properties.
     *
     * @param sensable object to be created as a schedule entry and saved as a local bookmark.
     *
     * Sensable has no explicitly declared properties, as it appears to be an object that
     * is passed in through the method parameters. However, its type suggests it may have
     * properties related to sensing or sensibility.
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
     * Inserts a new sensable object into the SQLite database by serializing it and using
     * it as ContentValues, then inserting it into the SensableContentProvider's CONTENT_URI
     * through the ContentResolver.
     *
     * @param sensable object that needs to be saved, which is serialized into ContentValues
     * for subsequent insertion into the SQLite database.
     *
     * @returns a boolean value indicating successful insertion into the database.
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
     * Searches for a sensor with a given name in a list and returns its type if found,
     * or -1 if not. It iterates through the list and checks each sensor's name against
     * the provided name, returning the first match.
     *
     * @param sensorName name of a sensor for which an object from the `sensorList` needs
     * to be found and its type returned.
     *
     * @returns an integer value representing the type of a sensor or -1 if not found.
     *
     * The output is an integer, representing the type of the chosen sensor. If no matching
     * sensor name is found in the list, -1 is returned as a default value.
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
     * Retrieves the last known location using a network provider. It first obtains a
     * reference to the location manager service and then requests the last known location
     * from the specified provider. The obtained location is returned as an object of
     * type `Location`.
     *
     * @returns a `Location` object representing the last known location.
     */
    private Location getLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        return locationManager.getLastKnownLocation(locationProvider);
    }


}

