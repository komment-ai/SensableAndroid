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
 * Is responsible for displaying scheduled sensors in a list view and handling user
 * interactions with the list items. It uses a CursorLoader to retrieve data from a
 * SQLite database and displays the data in a SensableListAdapter. When an item is
 * clicked, it starts an activity with sensor data for the selected sensable.
 */
public class LocalSensablesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = LocalSensablesFragment.class.getSimpleName();
    public final static String EXTRA_SENSABLE = "io.sensable.sensable";

    SensableListAdapter mAdapter;

    /**
     * Inflates a view from an XML layout file into a container view group. The inflated
     * view is a local sensables fragment with ID R.layout.local_sensables_fragment, and
     * it does not attach the layout to the parent or add it to the child list of the container.
     *
     * @param layoutInflater LayoutInflater object that inflates the layout resource file
     * specified by R.id.local_sensables_fragment into a View object.
     *
     * @param container parent view group where the inflated view will be added.
     *
     * @param savedInstanceState bundle that contains data saved from the previous state
     * of the fragment, allowing for restoration after configuration changes or orientation
     * changes.
     *
     * @returns a view object inflated from `R.layout.local_sensables_fragment`.
     */
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        return layoutInflater.inflate(R.layout.local_sensables_fragment, container, false);
    }

    /**
     * Initiates the execution sequence by first calling its superclass's `onStart` method,
     * and then it invokes the `initialiseList` method to set up the list for further
     * processing. This function is a crucial part of the initialization process in an
     * Android application.
     */
    @Override
    public void onStart() {
        super.onStart();
        initialiseList();
    }

    /**
     * Initializes a list view by retrieving a ScheduleHelper instance, starting its
     * scheduler, and attaching a cursor loader to the list view. It also sets an empty
     * view for the list view when it is empty and adds an on-click listener to the list
     * view.
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
     * Handles user clicks on a list item and starts an activity with sensor data for the
     * selected ScheduledSensable. It retrieves the corresponding ScheduledSensable object
     * from the database based on the clicked position, creates an Intent to launch
     * SensableActivity, and passes necessary data to it.
     *
     * @returns an AdapterView.OnItemClickListener.
     *
     * Returns an AdapterView.OnItemClickListener that handles user clicks on a list item
     * and starts an activity with sensor data for the selected sensable.
     * It takes four parameters in its onItemClick method: parent AdapterView, View object
     * that was clicked, int position, and long id.
     */
    private AdapterView.OnItemClickListener getScheduledSensableListener() {
        return new AdapterView.OnItemClickListener() {
            /**
             * Retrieves a scheduled sensable from a table based on user selection, creates an
             * intent to start SensableActivity with selected sensable data, and starts the
             * activity with the intent.
             *
             * @param parent `AdapterView` that contains the view at the specified position, which
             * is used to retrieve the item at that position and get its corresponding data from
             * the database.
             *
             * Parent is an instance of AdapterView, which extends View. Its main properties
             * include position and id. The position indicates the current selected item in the
             * adapter view, while id is a unique identifier for each row.
             *
             * @param view View object that is associated with the selected item at the given position.
             *
             * View: represents a UI component. Properties include: parent (the AdapterView that
             * generated this view), id (a unique identifier for this view), and layoutResource
             * (the resource ID for this view's layout).
             *
             * @param position 0-based index of the item selected from the AdapterView, used to
             * retrieve the corresponding Cursor object.
             *
             * @param id 64-bit integer row ID of the item selected from the adapter.
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
     * Initializes a list view adapter and sets it to a list view. It also prepares a
     * loader manager to load data into the adapter.
     *
     * @param listView ListView object to which the adapter and cursor loader are assigned,
     * allowing for data population and rendering within the list.
     *
     * Define. Has no ID attribute defined.
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
     * Creates and returns a `CursorLoader` instance that retrieves data from the
     * `ScheduledSensableContentProvider`. It takes care of creating a cursor for displaying
     * the scheduled sensable content, using the specified URI, projection, selection
     * criteria, and sort order.
     *
     * @param id 0-based identifier of the loader being created and is used to determine
     * which loader should be recreated if the data changes.
     *
     * @param args Bundle object that contains additional data or arguments to be passed
     * to the Loader's operation.
     *
     * @returns a CursorLoader instance.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(getActivity(), ScheduledSensableContentProvider.CONTENT_URI,
                SCHEDULED_SENSABLE_PROJECTION, null, null, null);
    }

    /**
     * Swaps a new cursor with an existing one, updating data for a specified adapter.
     * The framework automatically closes the old cursor upon returning from this method.
     *
     * @param loader Loader that triggered the onLoadFinished callback, providing information
     * about the loaded data.
     *
     * @param data Cursor object returned by the Loader which contains the query results
     * that are then swapped into the adapter to update the UI.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)

        mAdapter.swapCursor(data);
    }

    /**
     * Resets the loader by clearing its cursor and stopping its operation when the last
     * cursor provided to `onLoadFinished` is about to be closed. This ensures that no
     * further references are maintained to the cursor. The adapter's cursor is set to null.
     *
     * @param loader Loader object that provided the Cursor to be reset, which is about
     * to be closed and needs to be handled properly.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }
}
