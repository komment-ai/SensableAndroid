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
 * manages data display and sensory data saving for Android applications. It includes
 * functions like retrieving sensor data, preparing list data, updating user interface
 * components, calling the callback function when an API call fails, updating a
 * sensable object's fields and database, and updating a save button based on the
 * number of samples in the list. Additionally, it provides methods to update a sensory
 * device in a database by serializing and inserting it into the Sensables table using
 * content resolvers and to retrieve a sensor sender from a scheduled database through
 * the getContentResolver method.
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
     * sets up the UI for the sensable activity, including displaying a list of sensables
     * and their locations, and adding an button to save or unsave a sensable.
     * 
     * @param savedInstanceState previous saved state of the activity, which can be used
     * to restore the activity's UI and data if it was launched from a different task or
     * process.
     * 
     * * `sensable`: A `Sensable` object containing information about the sensable that
     * was saved previously. It has fields for `name`, `location`, `samples`, and `id`.
     * * `listDataHeader`: An array of headers representing the columns of data to be
     * displayed in the expandable list. Each header is a `String` object.
     * * `listDataChild`: An array of child objects representing the rows of data to be
     * displayed in the expandable list. Each child object is a `String` object.
     * * `localSender`: A `Cursor` object representing the local sender of the sensable.
     * It has fields for `id`, `name`, and `count`.
     * 
     * Note that these properties are not necessarily present in every instance of the
     * `SensableActivity`, as they depend on the specific input data provided in `savedInstanceState`.
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
             * saves unspecified data, likely crucial to the application's operation, using the
             * `saveThisSensable()` method.
             * 
             * @param v view that was clicked, and it is passed to the `saveThisSensable()` method
             * as a way to trigger the saving of data.
             * 
             * * `View v`: The object that was clicked.
             * * `saveThisSensable()`: A method that is called when the view is clicked, which
             * performs some action related to saving something sensable.
             */
            @Override
            public void onClick(View v) {
                saveThisSensable();
            }
        });

        unFavouriteButton.setOnClickListener(new View.OnClickListener() {
            /**
             * calls the `unsaveThisSensable()` method, which is not specified in the provided
             * code snippet. Therefore, the exact functionality of this function cannot be
             * determined with certainty.
             * 
             * @param v button that was clicked and triggers the `unsaveThisSensable()` method
             * to perform its functionality.
             * 
             * * `v`: This is an instance of the `View` class, which contains information about
             * the widget that was clicked.
             * * `unsaveThisSensable()`: This is a method that has been overridden in a subclass
             * and does not have any meaning on its own.
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
                 * builds an alert dialog asking if the user wants to stop sampling a sensable. It
                 * removes the sensable from the scheduler and makes the delete local button invisible
                 * when the positive button is clicked, or cancels the dialog when the negative button
                 * is clicked.
                 * 
                 * @param v view that was clicked, and it is used to identify which button was pressed
                 * within the dialog box.
                 * 
                 * * `v`: This is the View object that triggered the `onClick` method. It represents
                 * the button that was clicked by the user.
                 * * `DialogInterface dialog`: This is an Android Dialog Interface object that contains
                 * information about the button that was clicked within the dialog. The `dialog`
                 * parameter is passed as a reference to the `onClick` method, allowing for easy
                 * access to its properties and methods.
                 * * `which`: This is an integer value representing the button that was clicked within
                 * the dialog. It can take on values between 0 and -1, with 0 indicating the positive
                 * button and -1 indicating the negative button.
                 */
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SensableActivity.this);
                    builder.setMessage("Stop sampling with this sensable?");
                    builder.setTitle("Confirmation Dialog");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        /**
                         * removes a sensable from a scheduler when the button is clicked.
                         * 
                         * @param dialog dialog window that triggered the event and whose options should be
                         * processed by the function.
                         * 
                         * * `dialog`: A DialogInterface object representing the click event.
                         * * `which`: An integer value representing the position of the click event within
                         * the dialog (optional).
                         * 
                         * @param which button index that was clicked and is used to identify the specific
                         * sensable to be removed from the scheduler.
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
                         * cancels a dialog box when the user clicks on it.
                         * 
                         * @param dialog cancelled dialog box that the function is called on.
                         * 
                         * * `dialog`: This is an instance of the `DialogInterface` class, which represents
                         * a dialog box or other UI component that can be shown to the user.
                         * * `which`: This is an integer value representing the position of the button in the
                         * dialog box that was clicked.
                         * 
                         * @param which event that triggered the cancelation of the dialog.
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
     * sets up a REST API client to fetch sensory data from a server, and calls the
     * `getSensorData` method to retrieve the data. It then updates the `sensable` object
     * and related views with the obtained data.
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
             * updates a sensable object and a view after a callback success response.
             * 
             * @param sensable sensory data received from the device and is updated in the
             * `updateSensable()` and `updateView()` methods.
             * 
             * * `Log.d(TAG, "Callback Success - Sensable");`: This line logs a message to the
             * console with the tag `TAG`.
             * * `updateSensable(sensable);`: This line updates the `sensable` object with some
             * changes made to its properties or attributes.
             * * `updateView(sensable);`: This line updates the view by applying changes to the
             * `sensable` object.
             * 
             * @param response response from the API call, which is used to update the sensable
             * and view components in the application.
             * 
             * * `sensable`: A `Sensable` object, representing the user's input.
             * * `response`: A JSON response from the server, containing the user's input and
             * other data.
             */
            @Override
            public void success(Sensable sensable, Response response) {
                Log.d(TAG, "Callback Success - Sensable");
                updateSensable(sensable);
                updateView(sensable);
            }

            /**
             * is called when a Retrofit error occurs during callback processing. It logs an error
             * message with the RetrofitError object's toString() value and its TAG.
             * 
             * @param retrofitError error that occurred during the callback process and is logged
             * along with its message using the `Log.e()` method.
             * 
             * * `toString()` returns a string representation of the error object.
             */
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Callback failure" + retrofitError.toString());
            }
        });
    }

    /**
     * updates the view by setting the sensor ID, unit, and location to the corresponding
     * values from a `Sensable` object, clears any existing samples, adds the samples
     * from the `Sensable` object to a list, and notifies the adapter of the changes.
     * 
     * @param sensable sensory data to be displayed in the UI, which includes the sensor
     * ID, unit, and location information.
     * 
     * * `sensableId`: A text field that displays the sensor ID of `sensable`.
     * * `sensableUnit`: A text field that displays the unit of measurement for `sensable`.
     * * `sensableLocation`: A text field that displays the location of `sensable` in a
     * comma-separated format (longitude, latitude).
     * * `mSamples`: A List<Sample> object that stores the samples of `sensable`.
     * * `mExpandableListAdapter`: An adapter object for an expandable list view that is
     * used to display the samples of `sensable`.
     * * `prepareListData()`: This method prepares the data to be displayed in the
     * expandable list view by adding the samples of `sensable` to a List<Sample>.
     * * `updateSaveButton()`: This method updates the state of a button in the UI to
     * reflect whether the data has been saved successfully or not.
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
     * updates a sensable object's fields with values from the provided sensable object,
     * then saves the updated object to the database.
     * 
     * @param sensable sensory data to be updated, and its properties are copied to the
     * corresponding fields of the `Sensable` object.
     * 
     * * `name`: A string property representing the name of the sensable device.
     * * `sensorid`: An integer property representing the unique identifier of the sensable
     * device.
     * * `location`: A string property representing the location of the sensable device.
     * * `sensortype`: An integer property representing the type of sensable device.
     * * `samples`: An array of data samples represented as a list of floating-point numbers.
     * * `unit`: A string property representing the unit of measurement for the sensable
     * device.
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
     * saves a sensable data to local storage or database, depending on whether it has
     * been saved previously. It first checks if the sensable has been saved locally and
     * then inserts it into the database if not saved before.
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
     * updates a sensable data in the database by checking if it was saved locally,
     * serializing it for SQL Lite, and then updating the content URI with the new values.
     * It returns true if at least one row was updated or false otherwise.
     * 
     * @returns a boolean value indicating whether the sensable data was successfully
     * updated in the database.
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
     * deletes rows from a local database based on a check of whether the data was saved
     * locally or not, and updates a save button accordingly.
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
     * queries the local database using a `ContentResolver` and counts the number of rows
     * returned. If the count is greater than zero, the function returns `true`.
     * 
     * @returns a boolean value indicating whether any data is saved locally.
     * 
     * * The function returns a boolean value indicating whether any data is saved locally
     * in the database.
     * * The count returned by the `getCount()` method of the `Cursor` object represents
     * the number of rows in the result set.
     * * The `getContentResolver()` method is used to query the database, and the `new
     * String[]{"*"}` argument specifies that all columns are needed in the result set.
     * * The `null` arguments for the `where` and `groupby` methods indicate that no
     * filtering or grouping is performed on the data.
     */
    private boolean checkSavedLocally() {
        Cursor count = getContentResolver().query(getDatabaseUri(), new String[]{"*"}, null, null, null, null);
        return count.getCount() > 0;
    }

    /**
     * retrieves a Cursor object containing data from a scheduled database using a query
     * executed by the device's content resolver.
     * 
     * @returns a cursor object containing data from a query on the scheduled database.
     * 
     * The Cursor object returned by the function represents the results of querying the
     * scheduled database with the specified criteria. The cursor contains information
     * about the sender objects in the database, including their ID, name, and status.
     */
    private Cursor getSensableSender() {
        Cursor count = getContentResolver().query(getScheduledDatabaseUri(), new String[]{"*"}, null, null, null, null);
        return count;
    }

    // Returns the DB URI for this sensable
    /**
     * parses a URI string to obtain a database connection for Sensable content provider.
     * 
     * @returns a Uri object representing a content provider entry point for a specific
     * sensor ID.
     * 
     * * `Uri.parse(SensableContentProvider.CONTENT_URI + "/" + sensable.getSensorid())`:
     * This returns a Uri object representing the database location for the given sensor
     * ID. The Uri object has various attributes such as scheme, authority, path, and query.
     * * Scheme: The scheme of the Uri object represents the protocol used to access the
     * database, which is typically "content".
     * * Authority: The authority component of the Uri object specifies the domain name
     * or IP address of the server hosting the database.
     * * Path: The path component of the Uri object specifies the location within the
     * server where the database is stored. In this case, it is "/" + sensable.getSensorid(),
     * which represents the specific sensor ID for which the database is being retrieved.
     * * Query: The query component of the Uri object can contain additional information
     * such as filters or sorting criteria used to narrow down the data retrieved from
     * the database. However, in this case, there are no query parameters provided.
     */
    private Uri getDatabaseUri() {
        return Uri.parse(SensableContentProvider.CONTENT_URI + "/" + sensable.getSensorid());
    }

    /**
     * parses a Uri based on a content provider and sensor ID to return a scheduled
     * database Uri.
     * 
     * @returns a Uri object representing the scheduled database location for a specific
     * sensor ID.
     * 
     * * The output is a `Uri` object representing a database URL.
     * * The `Uri.parse()` method is used to create the `Uri` object from a string
     * representation of the database URL.
     * * The string representation consists of the `ScheduledSensableContentProvider.CONTENT_URI`
     * scheme and path, followed by the sensor ID as a String.
     * 
     * The ` Uri` object represents a database URL that can be used to access the sensory
     * data stored in the database.
     */
    private Uri getScheduledDatabaseUri() {
        return Uri.parse(ScheduledSensableContentProvider.CONTENT_URI + "/" + sensable.getSensorid());
    }


    /**
     * determines the visibility of two buttons based on whether a location is saved
     * locally or not, hiding one button and showing the other.
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
     * prepares a list of data by reversing the order of sample timestamps, sorting them
     * based on their timestamps, and then adding the samples to a new list with headers
     * and child data.
     */
    private void prepareListData() {
        listDataHeader.clear();//
        listDataChild.clear();//

        // Reverse the order of samples so they are by timestamp desc
        Collections.sort(mSamples, new Comparator<Sample>() {
            /**
             * compares two `Sample` objects based on their timestamps, returning an integer value
             * indicating the difference between the two timestamps.
             * 
             * @param a 1st sample being compared to the 2nd sample `b`.
             * 
             * * `a` is a `Sample` object containing attributes such as `timestamp`, which is a
             * long value representing the time of creation of the sample.
             * 
             * @param b 2nd sample object to be compared with the `a` parameter, and its `timestamp`
             * field is used for the comparison.
             * 
             * * `getTimestamp()`: returns the timestamp value in milliseconds since the epoch
             * (January 1, 1970, 00:00:00 GMT) for object `b`.
             * 
             * @returns an integer value representing the difference between the timestamps of
             * the two input samples.
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
