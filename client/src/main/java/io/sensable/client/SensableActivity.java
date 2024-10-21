package io.sensable.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import io.sensable.SensableService;
import io.sensable.client.adapter.ExpandableListAdapter;
import io.sensable.client.scheduler.ScheduleHelper;
import io.sensable.client.sqlite.SavedSensablesTable;
import io.sensable.client.sqlite.ScheduledSensableContentProvider;
import io.sensable.client.sqlite.ScheduledSensablesTable;
import io.sensable.client.sqlite.SensableContentProvider;
import io.sensable.model.Sample;
import io.sensable.model.ScheduledSensable;
import io.sensable.model.Sensable;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.*;



/**
 * Is responsible for managing sensory data from a device and updating its corresponding
 * views in an Android application. It fetches data from a REST API, updates the
 * sensable object and view, and allows users to save or remove the sensable data
 * from a local database.
 */
public class SensableActivity extends Activity {

    private static final String TAG = SensableActivity.class.getSimpleName();

    private Sensable sensable;
    private TextView sensableId;
    private TextView sensableUnit;
    private TextView sensableLocation;

    ExpandableListAdapter mExpandableListAdapter;
    ExpandableListView sensableSamples;
    List<String> listDataHeader = new ArrayList<String>();
    HashMap<String, List<String>> listDataChild = new HashMap<String, List<String>>();

    private Boolean savedLocally;

    private Button favouriteButton;

    private Button unFavouriteButton;

    private ArrayList<Sample> mSamples;

    /**
     * Initializes a Sensable activity by setting up UI elements, retrieving data from
     * intent and updating views. It also sets up buttons for saving, unfavouriting and
     * deleting sensables, and handles their click events.
     *
     * @param savedInstanceState Bundle object that contains the data to be used when the
     * activity is recreated, such as its previous state and data.
     *
     * Bundle object with data saved previously when the activity was paused or stopped.
     * It contains key-value pairs where keys are strings and values can be any type of
     * serializable objects (e.g., primitive types, arrays, lists).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensable);
        Intent intent = getIntent();
        sensable = (Sensable) intent.getParcelableExtra(MainActivity.EXTRA_SENSABLE);

        sensableId = (TextView) findViewById(R.id.sensable_id_field);
        sensableUnit = (TextView) findViewById(R.id.sensable_unit_field);
        sensableLocation = (TextView) findViewById(R.id.sensable_location_field);

        if (sensable.getLocation() == null) {
            sensable.setLocation(new double[]{0, 0});
        }

        if (sensable.getSamples() == null) {
            sensable.setSamples(new Sample[]{});
        }
        mSamples = new ArrayList<Sample>(Arrays.asList(sensable.getSamples()));

        setTitle(sensable.getName());

        // get the listview
        sensableSamples = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        mExpandableListAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        sensableSamples.setAdapter(mExpandableListAdapter);

        savedLocally = checkSavedLocally();

        favouriteButton = (Button) findViewById(R.id.favourite_sensables_button);
        unFavouriteButton = (Button) findViewById(R.id.unfavourite_sensables_button);

        favouriteButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Is overridden and triggered when a View is clicked. It calls the `saveThisSensable()`
             * method, which saves or updates data related to sensory information. This action
             * is performed when a user interacts with a GUI element.
             *
             * @param v View that was clicked, allowing the function to identify and respond accordingly.
             */
            @Override
            public void onClick(View v) {
                saveThisSensable();
            }
        });

        unFavouriteButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles a click event on a View object, triggering an execution of the
             * `unsaveThisSensable` method when invoked. This method appears to undo or reverse
             * some action related to saving data or information that is considered sensible. The
             * purpose of this functionality is unknown without further context.
             *
             * @param v View that triggered the click event, allowing the method to access and
             * manipulate the view's properties or actions.
             */
            @Override
            public void onClick(View v) {
                unsaveThisSensable();
            }
        });

        final Cursor localSender = getSensableSender();
        if (localSender.getCount() > 0) {
            final Button deleteLocal = (Button) findViewById(R.id.delete_local_button);
            deleteLocal.setVisibility(View.VISIBLE);
            deleteLocal.setOnClickListener(new View.OnClickListener() {
                /**
                 * Creates a confirmation dialog to stop sampling with a sensable when a button is
                 * clicked. If the user confirms, it removes the sensable from the scheduler and hides
                 * a delete local view; otherwise, it cancels the dialog. A toast message indicates
                 * "Sensable stopped" after removal.
                 *
                 * @param v View that was clicked, triggering the execution of the function's code.
                 *
                 * View: The primary class for working with views and view hierarchies.
                 * Has no other main properties.
                 */
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SensableActivity.this);
                    builder.setMessage("Stop sampling with this sensable?");
                    builder.setTitle("Confirmation Dialog");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        /**
                         * Stops a sensable by removing it from a scheduler and hiding a delete button. It
                         * shows a toast message indicating the successful stop. The function uses a helper
                         * class, a table to retrieve scheduled sensables, and a cursor to access local data.
                         *
                         * @param dialog DialogInterface that triggered the execution of this code when an
                         * item is selected from its list.
                         *
                         * @param which 0-based index of the item that was clicked or selected from a list
                         * of items displayed by a DialogFragment.
                         */
                        public void onClick(DialogInterface dialog, int which) {
                            ScheduleHelper scheduleHelper = new ScheduleHelper(SensableActivity.this);

                            localSender.moveToFirst();
                            ScheduledSensable scheduledSensable = ScheduledSensablesTable.getScheduledSensable(localSender);
                            scheduleHelper.removeSensableFromScheduler(scheduledSensable);
                            deleteLocal.setVisibility(View.GONE);
                            Toast.makeText(SensableActivity.this, "Sensable stopped", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        /**
                         * Cancels a dialog when an event occurs. It receives a `DialogInterface` and an
                         * integer representing the selected item as parameters. When called, it immediately
                         * stops the execution of the dialog's remaining tasks.
                         *
                         * @param dialog DialogInterface that initiated the callback, allowing the function
                         * to interact with it and cancel its instance.
                         *
                         * @param which 0-based index of the selected item from the DialogInterface if it's
                         * a ListView or Spinner, but it is not used in this specific implementation.
                         */
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.create().show();


                }
            });
        }

        updateView(sensable);
    }

    /**
     * Initializes a REST adapter to interact with a Sensable service and makes a request
     * for sensor data using the provided sensor ID. On successful response, it updates
     * a Sensable object and a view component; otherwise, logs an error message if a
     * Retrofit error occurs.
     */
    @Override
    public void onStart() {
        super.onStart();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://sensable.io")
                .build();

        SensableService service = restAdapter.create(SensableService.class);

        service.getSensorData(sensable.getSensorid(), new Callback<Sensable>() {
            /**
             * Processes a successful callback by logging a message, updating the `sensable`
             * object through `updateSensable`, and refreshing the view with `updateView`. This
             * implies that it handles a response from an asynchronous operation or API call successfully.
             *
             * @param sensable object being updated by the function, which is then used to trigger
             * updates in both the UI and the data model.
             *
             * @param response response received from the server after a successful operation and
             * is not utilized within the method.
             */
            @Override
            public void success(Sensable sensable, Response response) {
                Log.d(TAG, "Callback Success - Sensable");
                updateSensable(sensable);
                updateView(sensable);
            }

            /**
             * Handles errors occurred during a Retrofit request and logs the error message with
             * the specified tag `TAG`. It takes a `RetrofitError` object as a parameter, which
             * contains information about the failure. The function logs the error message to the
             * Android logcat for debugging purposes.
             *
             * @param retrofitError error that occurred during the execution of the Retrofit API
             * request and is passed to the callback method for processing.
             */
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Callback failure" + retrofitError.toString());
            }
        });
    }

    /**
     * Updates the GUI components by setting text fields with sensor ID, unit, and location
     * information from a given `Sensable` object. It also clears and replenishes a list
     * of samples, notifies the adapter to update the list, and triggers another method
     * to update a save button.
     *
     * @param sensable object whose properties are updated and displayed in the view,
     * providing sensor ID, unit, location, and sample data.
     *
     * Get sensor ID, unit and location from sensable object.
     * Sensable contains array of samples which is cleared, updated and then notified to
     * adapter.
     */
    public void updateView(Sensable sensable) {

        sensableId.setText(sensable.getSensorid());

        sensableUnit.setText(sensable.getUnit());

        sensableLocation.setText(sensable.getLocation()[0] + ", " + sensable.getLocation()[1]);

        mSamples.clear();
        mSamples.addAll(new ArrayList<Sample>(Arrays.asList(sensable.getSamples())));
        prepareListData();
        mExpandableListAdapter.notifyDataSetChanged();
        updateSaveButton();
    }

    /**
     * Updates an object's properties. It sets the name, sensor ID, location, sensor type,
     * samples, and unit of a `Sensable` object based on the provided parameters. The
     * updated object is then stored in the database using the `updateSensableInDatabase`
     * method.
     *
     * @param sensable object to be updated, whose properties such as name, sensor ID,
     * location, sensor type, samples, and unit are copied into the corresponding fields
     * of the class instance.
     */
    private void updateSensable(Sensable sensable) {
        this.sensable.setName(sensable.getName());
        this.sensable.setSensorid(sensable.getSensorid());
        this.sensable.setLocation(sensable.getLocation());
        this.sensable.setSensortype(sensable.getSensortype());
        this.sensable.setSamples(sensable.getSamples());
        this.sensable.setUnit(sensable.getUnit());
        updateSensableInDatabase();
    }


    /**
     * Inserts a sensable object into the SQLite database if it has not been saved locally.
     * It uses a ContentValues object to store the data and the getContentResolver's
     * insert method to execute the insertion.
     */
    private void saveThisSensable() {
        savedLocally = checkSavedLocally();

        if (!savedLocally) {
            Uri mNewUri;
            ContentValues mNewValues = SavedSensablesTable.serializeSensableForSqlLite(sensable);

            mNewUri = getContentResolver().insert(
                    SensableContentProvider.CONTENT_URI,   // the user dictionary content URI
                    mNewValues                          // the values to insert
            );
            savedLocally = true;
            updateSaveButton();
        }
    }

    /**
     * Updates a sensable's information in the database by checking if it is saved locally,
     * serializing it for SQLite, and then updating the corresponding database rows using
     * the content resolver.
     *
     * @returns a boolean value indicating successful update or failure.
     */
    private boolean updateSensableInDatabase() {
        savedLocally = checkSavedLocally();
        if (savedLocally) {
            ContentValues mNewValues = SavedSensablesTable.serializeSensableForSqlLite(sensable);

            int rowsUpdated = getContentResolver().update(
                    getDatabaseUri(),   // the user dictionary content URI
                    mNewValues,                          // the values to insert
                    null,
                    new String[]{}
            );
            return rowsUpdated > 0;
        } else {
            return false;
        }
    }

    /**
     * Checks if data is already saved locally and, if so, deletes it from a database
     * using a Uri object. It updates the state of the `savedLocally` variable based on
     * the deletion result and triggers an update to the save button's state accordingly.
     */
    private void unsaveThisSensable() {
        // Defines a new Uri object that receives the result of the insertion
        int rowsDeleted;

        savedLocally = checkSavedLocally();
        if (savedLocally) {
            rowsDeleted = getContentResolver().delete(getDatabaseUri(), null, null);
            savedLocally = !(rowsDeleted > 0);
            updateSaveButton();
        }
    }

    /**
     * Queries a database using a cursor to retrieve the number of rows stored locally.
     * It then checks if the retrieved count is greater than zero, returning a boolean
     * indicating whether data has been saved locally.
     *
     * @returns a boolean value indicating database presence.
     */
    private boolean checkSavedLocally() {
        Cursor count = getContentResolver().query(getDatabaseUri(), new String[]{"*"}, null, null, null, null);
        return count.getCount() > 0;
    }

    /**
     * Queries a database using its content resolver and returns a cursor containing data
     * from the specified URI. The query retrieves all records (`"*"`) without any filtering
     * conditions or sorting.
     *
     * @returns a cursor object that queries scheduled database.
     */
    private Cursor getSensableSender() {
        Cursor count = getContentResolver().query(getScheduledDatabaseUri(), new String[]{"*"}, null, null, null, null);
        return count;
    }

    // Returns the DB URI for this sensable
    /**
     * Parses a uniform resource identifier (URI) by concatenating a constant URI string
     * with a sensor ID retrieved from the `sensable` object, forming a database URI for
     * specific sensor data retrieval.
     *
     * @returns a parsed URI for accessing a specific sensor's data.
     */
    private Uri getDatabaseUri() {
        return Uri.parse(SensableContentProvider.CONTENT_URI + "/" + sensable.getSensorid());
    }

    /**
     * Parses a URI to retrieve data from a scheduled database, combining a predefined
     * constant URI with a sensor ID obtained from an object called `sensable`. The
     * resulting URI is then returned as a Uri object.
     *
     * @returns a parsed URI combining CONTENT_URI and sensor ID.
     */
    private Uri getScheduledDatabaseUri() {
        return Uri.parse(ScheduledSensableContentProvider.CONTENT_URI + "/" + sensable.getSensorid());
    }


    /**
     * Toggles the visibility of two buttons based on a boolean flag `savedLocally`. If
     * `savedLocally` is true, it hides the favourite button and shows the unFavourite
     * button. Otherwise, it shows the favourite button and hides the unFavourite button.
     */
    public void updateSaveButton() {
        if (savedLocally) {
            favouriteButton.setVisibility(View.GONE);
            unFavouriteButton.setVisibility(View.VISIBLE);
        } else {
            favouriteButton.setVisibility(View.VISIBLE);
            unFavouriteButton.setVisibility(View.GONE);
        }
    }

    /*
     * Preparing the list data
     */
    /**
     * Sorts a list of samples based on their timestamps and groups them by day. It then
     * creates a hierarchical data structure to store the grouped samples, with each
     * group's header representing the date.
     */
    private void prepareListData() {
        listDataHeader.clear();//
        listDataChild.clear();//

        // Reverse the order of samples so they are by timestamp desc
        Collections.sort(mSamples, new Comparator<Sample>() {
            /**
             * Returns an integer value representing the difference between the timestamps of two
             * `Sample` objects, with a larger value indicating that the second object has an
             * earlier timestamp. The comparison is done by subtracting the first object's timestamp
             * from the second object's timestamp.
             *
             * @param a first `Sample` object to be compared with the second `Sample` object `b`.
             *
             * @param b second object being compared to the first object `a`, with its timestamp
             * subtracted from `a`'s timestamp to determine the comparison result.
             *
             * @returns an integer representing the timestamp difference between two samples.
             */
            @Override
            public int compare(Sample a, Sample b) {
                return (int) (b.getTimestamp() - a.getTimestamp());
            }
        });

        List<ArrayList<String>> currentListList = new ArrayList<ArrayList<String>>();
        currentListList.add(new ArrayList<String>());

        String thisDayName;
        Calendar cal = Calendar.getInstance();
        int i = 0;
        while (i < mSamples.size()) {
            int nextDay = -1;
            int currentIndex = currentListList.size() - 1;
            Date thisSampleDate = new Date(mSamples.get(i).getTimestamp());
            if ((i + 1) < mSamples.size()) {
                Date nextSampleDate = new Date(mSamples.get(i + 1).getTimestamp());
                cal.setTime(nextSampleDate);
                nextDay = cal.get(Calendar.DAY_OF_YEAR);
            }
            cal.setTime(thisSampleDate);
            int thisDay = cal.get(Calendar.DAY_OF_YEAR);
            int month = (cal.get(Calendar.MONTH) + 1);
            thisDayName = cal.get(Calendar.YEAR) + "-" + month + "-" + cal.get(Calendar.DAY_OF_MONTH);
            String sampleRepresentation = cal.get(Calendar.YEAR)
                    + "-" + month
                    + "-" + cal.get(Calendar.DAY_OF_MONTH)
                    + " " + String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
                    + " | " + mSamples.get(i).getValue()
                    + "  " + sensable.getUnit();
            currentListList.get(currentIndex).add(sampleRepresentation);

            if (thisDay != nextDay) {
                listDataHeader.add(thisDayName);
                listDataChild.put(thisDayName, currentListList.get(currentIndex)); // Header, Child data
                currentListList.add(new ArrayList<String>());
            }
            i++;
        }

    }
}
