package io.sensable.client.views;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import io.sensable.client.R;
import io.sensable.client.SensableActivity;
import io.sensable.client.scheduler.ScheduleHelper;
import io.sensable.client.sqlite.SavedSensablesTable;
import io.sensable.client.sqlite.ScheduledSensableContentProvider;
import io.sensable.client.sqlite.ScheduledSensablesTable;
import io.sensable.client.sqlite.SensableContentProvider;
import io.sensable.model.Sensable;
import io.sensable.model.ScheduledSensable;

/**
 * Created by simonmadine on 19/07/2014.
 */
/**
 * is responsible for displaying scheduled sensors in a list view. It initializes the
 * list view and attaches a cursor loader to display data from a content provider.
 * The adapter is used to swap the cursor with the data provided by the loader when
 * it finishes loading or resets.
 */
public class LocalSensablesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = LocalSensablesFragment.class.getSimpleName();
    public final static String EXTRA_SENSABLE = "io.sensable.sensable";

    SensableListAdapter mAdapter;

    /**
     * inflates a fragment's layout from a resource file and returns the newly created
     * view object.
     * 
     * @param layoutInflater inflater class that is used to inflate the layout for the fragment.
     * 
     * 1/ `layoutInflater`: This is an instance of the `LayoutInflater` class, which is
     * used to inflate layout files. It has various properties such as `getContext()`,
     * `getLayoutInflater()` and `inflate()` methods.
     * 2/ `R.layout.local_sensables_fragment`: This is the layout file that is being
     * inflated by the `layoutInflater`. It defines the UI components of the fragment.
     * 3/ `container`: This is the parent view group that the inflated layout will be
     * added to.
     * 4/ `savedInstanceState`: This is an instance of the `Bundle` class, which contains
     * arbitrary data that was saved when the fragment was previously saved. It can be
     * used to restore the fragment's state.
     * 
     * @param container ViewGroup that will hold the inflated view.
     * 
     * 	- `R.layout.local_sensables_fragment`: This is the layout file that the `container`
     * represents.
     * 	- `false`: This indicates that the `container` is not a direct child of an activity.
     * 
     * @param savedInstanceState saved state of the fragment, which can be used to restore
     * the fragment's state in case it is recreated after being detroyed or when the user
     * returns to the fragment after navigating away from it.
     * 
     * 	- `savedInstanceState`: A Bundle object that contains additional data about the
     * fragment when it is recreated. It may contain arbitrary key-value pairs, but its
     * specific contents depend on the fragment's subclass and how it was saved.
     * 
     * @returns an inflated view of the specified layout.
     * 
     * The return statement inflates a layout from the resources file `R.layout.local_sensables_fragment`.
     * 
     * The value of `container` is the parent view group that the inflated layout will
     * be added to.
     * 
     * The value of `savedInstanceState` is a Bundle object containing any saved state
     * from previous views or fragments.
     */
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        return layoutInflater.inflate(R.layout.local_sensables_fragment, container, false);
    }

    /**
     * initializes a list upon call to the parent `onStart` method.
     */
    @Override
    public void onStart() {
        super.onStart();
        initialiseList();
    }

    /**
     * initializes a list view, attaches a cursor loader, sets an empty view, and adds
     * an on-item listener to handle clicks on list items.
     */
    private void initialiseList() {
        ScheduleHelper scheduleHelper = new ScheduleHelper(getActivity());
        scheduleHelper.startScheduler();

        final ListView sensableList = (ListView) getView().findViewById(R.id.scheduled_sensable_list);
        attachCursorLoader(sensableList);

        final TextView emptyFavouriteText = (TextView) getView().findViewById(R.id.text_no_local);
        sensableList.setEmptyView(emptyFavouriteText);

        //add onclick to ListView
        sensableList.setOnItemClickListener(getScheduledSensableListener());

    }

    /**
     * creates an `AdapterView.OnItemClickListener` that, when an item is clicked, opens
     * the `SensableActivity` with the sensor ID and unit of the scheduled sensable from
     * the database.
     * 
     * @returns an AdapterView.OnItemClickListener that handles click events on a list
     * of scheduled sensables, passing the selected sensor ID and unit to a new activity
     * for further processing.
     * 
     * 	- `ScheduledSensable scheduledSensable`: This class represents a scheduled sensable
     * object, which contains information about a sensory input that is to be taken at a
     * specific time.
     * 	- `Sensorid`: The unique identifier of the sensor for which the sensory input is
     * being scheduled.
     * 	- `Unit`: The unit of measurement for the sensory input.
     * 	- `Intent intent`: An intent object used to start the `SensableActivity` when an
     * item in the list is clicked.
     * 	- `EXTRA_SENSABLE`: A extra field in the intent that contains the sensable object.
     */
    private AdapterView.OnItemClickListener getScheduledSensableListener() {
        return new AdapterView.OnItemClickListener() {
            /**
             * handles user clicks on a list item and starts an activity with sensor data for the
             * selected sensable.
             * 
             * @param parent AdapterView from which the click event occurred.
             * 
             * 1/ `AdapterView<?> parent`: This is an adapter view object that represents the
             * list view from which the user clicked on an item. The type parameter `<?>` indicates
             * that the view can have any type of data as its elements, and therefore it cannot
             * be cast to a specific type directly.
             * 2/ `View view`: This is the view that was clicked by the user.
             * 3/ `int position`: This is the position of the item in the list view where the
             * click occurred.
             * 4/ `long id`: This is the ID of the item that was clicked.
             * 
             * @param view View object that was clicked, providing the position of the item within
             * the AdapterView.
             * 
             * 	- `parent`: The parent AdapterView object from which the clicked item was detected.
             * 	- `position`: The position of the clicked item in the list of items displayed by
             * the parent AdapterView.
             * 	- `id`: The unique identifier of the item that was clicked, as a long value.
             * 
             * @param position 0-based index of the selected item in the list, which is used to
             * retrieve the corresponding ScheduledSensable object from the database.
             * 
             * @param id 4-byte unique identifier of the scheduled sensable item that was clicked,
             * which is used to identify the sensable data to be displayed in the SensableActivity.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScheduledSensable scheduledSensable = ScheduledSensablesTable.getScheduledSensable((Cursor) parent.getItemAtPosition(position));

                Intent intent = new Intent(getActivity(), SensableActivity.class);
                Sensable sensable = new Sensable();
                sensable.setSensorid(scheduledSensable.getSensorid());
                sensable.setUnit(scheduledSensable.getUnit());

                intent.putExtra(EXTRA_SENSABLE, sensable);
                startActivity(intent);

            }
        };
    }

    private static final String[] SCHEDULED_SENSABLE_PROJECTION = new String[]{
            ScheduledSensablesTable.COLUMN_ID,
            ScheduledSensablesTable.COLUMN_SENSABLE_ID,
            ScheduledSensablesTable.COLUMN_SENSOR_ID,
            ScheduledSensablesTable.COLUMN_SENSOR_NAME,
            ScheduledSensablesTable.COLUMN_SENSOR_TYPE,
            ScheduledSensablesTable.COLUMN_PENDING,
            ScheduledSensablesTable.COLUMN_LAST_SAMPLE,
            ScheduledSensablesTable.COLUMN_UNIT
    };

    /**
     * attaches a cursor loader to a `ListView` in order to display data from a SQLite
     * database. The function prepares an adapter and sets it as the list view's adapter,
     * then initiates a new or existing loader with a given ID using the
     * `getLoaderManager().initLoader()` method.
     * 
     * @param listView ListView that will be populated with data by the adapter created
     * and initialized in the function.
     * 
     * 	- `listView`: This is an instance of the `ListView` class, which is used to display
     * a list of items in a user interface. It has various attributes and methods for
     * managing the display of the list.
     * 	- `projection`: This is an instance of the `AdapterHolder` class, which contains
     * the data to be displayed in each row of the list. The fields of the `projection`
     * object are:
     * 	+ `ID`: An integer representing the ID of the sensable.
     * 	+ `NAME`: A string representing the name of the sensable.
     * 	+ `SENSOR_ID`: An integer representing the ID of the sensor associated with the
     * sensable.
     * 	+ `VALUE`: A double representing the last sample value of the sensable.
     * 	+ `TYPE`: A string representing the type of the sensable.
     * 	+ `UNIT`: A string representing the unit of measurement for the sensable.
     */
    private void attachCursorLoader(ListView listView) {
        SensableListAdapter.AdapterHolder projection = new SensableListAdapter.AdapterHolder();
        projection.ID = ScheduledSensablesTable.COLUMN_ID;
        projection.NAME = ScheduledSensablesTable.COLUMN_SENSOR_NAME;
        projection.SENSOR_ID = ScheduledSensablesTable.COLUMN_SENSABLE_ID;
        projection.VALUE = ScheduledSensablesTable.COLUMN_LAST_SAMPLE;
        projection.TYPE = ScheduledSensablesTable.COLUMN_SENSOR_TYPE;
        projection.UNIT = ScheduledSensablesTable.COLUMN_UNIT;


        mAdapter = new SensableListAdapter(getActivity(), R.id.row_sensable_id, null, projection);
        listView.setAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * creates a `CursorLoader` that retrieves data from a Content Provider and returns
     * a Cursor object for display.
     * 
     * @param id identifier for the loader, which is used to identify the loader instance
     * and distinguish it from other instances created for different data sources.
     * 
     * @param args optional arguments that can be used to customize the loader's operation,
     * such as specifying the sort order or filtering the data.
     * 
     * 1/ `id`: An integer value representing the id of the loader.
     * 2/ `args`: A Bundle object containing additional data or parameters for the loader.
     * The properties of this object may vary depending on the context and requirements.
     * 
     * @returns a `CursorLoader` instance that fetches data from a content provider.
     * 
     * 	- `id`: An integer that identifies the loader, which is used to distinguish between
     * different loaders in the application.
     * 	- `Bundle args`: A bundle object that contains additional data that can be used
     * by the loader to customize its behavior.
     * 	- `Loader<Cursor>`: The actual loader object that will take care of creating a
     * Cursor for the data being displayed.
     * 	- `CursorLoader`: A subclass of `Loader` that provides a way to load a cursor
     * from a content provider.
     * 	- `SCHEDULED_SENSABLE_PROJECTION`: An array of strings that represents the
     * projection (i.e., the columns) of the data being displayed.
     * 	- `null`: A null value that indicates that no additional data should be included
     * in the Cursor.
     * 	- `null`: A null value that indicates that no additional information about the
     * Cursor should be included in the Loader.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(getActivity(), ScheduledSensableContentProvider.CONTENT_URI,
                SCHEDULED_SENSABLE_PROJECTION, null, null, null);
    }

    /**
     * swaps a new `Cursor` with the one previously loaded by the Loader, allowing the
     * adapter to display the new data.
     * 
     * @param loader Loader object that is used to load the data into the adapter.
     * 
     * 	- Loader: The `loader` is an instance of the class `Loader`, which holds information
     * about how to load data from a remote source.
     * 	- Cursor: The `cursor` parameter represents the data returned by the loader, which
     * can be used to populate a list view.
     * 
     * Swapping the cursor with `mAdapter` allows for efficient updates to the adapter's
     * data set without having to requery the database or network.
     * 
     * @param data new data that is being loaded into the adapter, which the method
     * `swapCursor` will swap with the previous cursor.
     * 
     * The `Cursor` object `data` is passed as an argument to the function, which indicates
     * that it contains data that has been loaded from a database or other data source.
     * The `Loader<Cursor>` class, which is the parameter for the function, loads data
     * from a database and returns a `Cursor` object representing the loaded data.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)

        mAdapter.swapCursor(data);
    }

    /**
     * is called when the last Cursor provided to `onLoadFinished()` is about to be closed.
     * It ensures that the cursor is no longer being used by swapping it with `null`.
     * 
     * @param loader Loader object that was used to load the Cursor, and its reset event
     * is triggered when the last Cursor provided by the Loader is about to be closed.
     * 
     * The `loader` argument is of type `Loader<Cursor>`, which means it provides access
     * to a Cursor object that represents the data to be displayed in the list. This
     * Cursor contains information about the items to be displayed in the list, such as
     * their names or other relevant details. The `onLoaderReset` function is called when
     * the last Cursor provided by the `onLoadFinished` function is about to be closed,
     * indicating that it is no longer needed and should be properly disposed of. To do
     * this, the `mAdapter.swapCursor(null)` line is used to swap the cursor with a null
     * value, which signals to the adapter that no more data is available for display.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }
}
