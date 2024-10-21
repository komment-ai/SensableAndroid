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
 * Is responsible for displaying scheduled sensors in a list view, where each sensor
 * represents a sensory input to be taken at a specific time. It initializes the list
 * view and attaches a cursor loader to display data from a SQLite database. The
 * fragment also handles user clicks on list items by starting an activity with sensor
 * data for the selected sensable.
 */
public class LocalSensablesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = LocalSensablesFragment.class.getSimpleName();
    public final static String EXTRA_SENSABLE = "io.sensable.sensable";

    SensableListAdapter mAdapter;

    /**
     * Inflates a local sensables fragment view from an XML layout and returns it. The
     * inflated view is added to a container with the specified parent and attaches the
     * layout parameters. This function overrides the default onCreateView method in a
     * Fragment class.
     *
     * @param layoutInflater LayoutInflater instance that is used to inflate the
     * local_sensables_fragment layout resource into a View object.
     *
     * @param container 2D array to which the View returned by the method will be added
     * and is used to determine the layout parameters of the inflated view.
     *
     * @param savedInstanceState Bundle object passed from the previous instance of the
     * same fragment and provides state information that needs to be restored.
     *
     * @returns an inflated view from the local_sensables_fragment layout resource.
     */
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        return layoutInflater.inflate(R.layout.local_sensables_fragment, container, false);
    }

    /**
     * Initiates a list by calling the `initialiseList` method when the application starts.
     * It overrides the default implementation and includes an additional step to prepare
     * the list for use. The `super.onStart()` call ensures that the parent class's
     * start-up sequence is executed first.
     */
    @Override
    public void onStart() {
        super.onStart();
        initialiseList();
    }

    /**
     * Initializes a schedule helper and starts a scheduler, sets up a list view with an
     * empty text view as its empty view, and attaches a cursor loader to it. It also
     * adds an on-click listener to the list view.
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
     * selected sensable by retrieving the ScheduledSensable object from the database
     * based on the clicked position and then creating an Intent to launch the SensableActivity.
     *
     * @returns an AdapterView.OnItemClickListener instance.
     *
     * The output is an instance of `AdapterView.OnItemClickListener`, implementing a
     * listener interface for handling user clicks on list items in an adapter view. It
     * includes methods to handle click events with parameters `parent`, `view`, `position`,
     * and `id`.
     */
    private AdapterView.OnItemClickListener getScheduledSensableListener() {
        return new AdapterView.OnItemClickListener() {
            /**
             * Retrieves a scheduled sensable item from a table based on the selected position.
             * It then creates an intent to start the SensableActivity with the retrieved sensable
             * item's sensor ID and unit as extras, and starts the activity.
             *
             * @param parent AdapterView that holds the item being clicked, providing access to
             * its contents through methods like `getItemAtPosition`.
             *
             * AdapterView parent - an abstract class for controlling layout and drawing of items.
             * It provides methods to interact with child views such as adding or removing them
             * from a container. Its main properties include its adapter, which determines the
             * data it should display; the ID of the context view; and the number of items in the
             * list.
             *
             * @param view View object associated with the item that was clicked within the
             * AdapterView, which is used to start an Activity.
             *
             * View's type is `View`, which is an interface representing a UI component. Its
             * properties include its parent, layout parameters, tag, and focusable state.
             *
             * @param position 0-based index of the item selected in the AdapterView at which the
             * view is located.
             *
             * @param id 0-based index of the item that was clicked within the adapter.
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
     * Prepares and sets up a list view by creating an adapter with specific columns from
     * a database table, attaching it to the list view, and initializing a loader manager
     * for retrieving data.
     *
     * @param listView ListView object to which an adapter is set and managed by the
     * `attachCursorLoader` method.
     *
     * • It is an instance of ListView.
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
     * Creates and returns a `CursorLoader` instance to manage data retrieval from the
     * `ScheduledSensableContentProvider`. It takes care of creating a `Cursor` for
     * displaying data, using the provider's content URI, projection, and query parameters.
     *
     * @param id 0-based identifier of the Loader being created and is used to identify
     * the loader's state across configuration changes.
     *
     * @param args Bundle of arguments passed to the LoaderManager when it is created or
     * restarted, which are not used in this implementation.
     *
     * @returns a `CursorLoader` instance.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(getActivity(), ScheduledSensableContentProvider.CONTENT_URI,
                SCHEDULED_SENSABLE_PROJECTION, null, null, null);
    }

    /**
     * Loads data from a Loader into a Cursor and passes it to an adapter, which swaps
     * the new cursor with the existing one. The old cursor is automatically closed by
     * the framework once the function returns.
     *
     * @param loader Loader object that has finished its load operation, providing the
     * Cursor data to be used for updating the adapter's content.
     *
     * @param data Cursor object returned by the Loader, which is swapped into the adapter
     * to update the data displayed in the UI.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)

        mAdapter.swapCursor(data);
    }

    /**
     * Resets the cursor provided by a loader, ensuring its safe closure. It prevents
     * further use of the cursor and updates an adapter to swap its cursor with null,
     * effectively resetting its data source.
     *
     * @param loader Loader that is about to reset its cursor, which allows the implementation
     * to release any resources associated with it.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }
}
