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
import io.sensable.client.sqlite.SensableContentProvider;
import io.sensable.model.Sensable;

/**
 * Created by simonmadine on 19/07/2014.
 */
/**
 * Is a part of an Android application that displays a list of favourite sensors in
 * a Fragment. It uses a CursorLoader to load data from a SQLite database and populates
 * a ListView with the retrieved data. The fragment also provides methods for
 * initializing the list, attaching a cursor loader, and handling item clicks.
 */
public class FavouriteSensablesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = FavouriteSensablesFragment.class.getSimpleName();
    public final static String EXTRA_SENSABLE = "io.sensable.sensable";

    SensableListAdapter mAdapter;

    /**
     * Inflates a layout for a fragment from the resource R.layout.favourite_sensables_fragment
     * into a given ViewGroup. The inflated view is then returned as the result of the
     * onCreateView method. This function is part of the Fragment class and is responsible
     * for creating the initial layout of a fragment.
     *
     * @param layoutInflater LayoutInflater object used to inflate the layout file
     * R.layout.favourite_sensables_fragment into a View object.
     *
     * @param container 2D array of view roots that is passed to the inflated view's
     * `setLayoutParams()` method.
     *
     * @param savedInstanceState Bundle object containing the activity's previously saved
     * state.
     *
     * @returns an inflated view of the "favourite_sensables_fragment" layout.
     */
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        return layoutInflater.inflate(R.layout.favourite_sensables_fragment, container, false);
    }

    /**
     * Initializes the application by executing its superclass's `onStart` method and
     * then calling the `initialiseList` method to set up a list or other data structure.
     * This method is typically used during the initialization of an Android activity.
     */
    @Override
    public void onStart() {
        super.onStart();
        initialiseList();
    }

    /**
     * Initializes a list view by setting up a cursor loader and an empty view for it,
     * then sets up an on-click listener for the list view. Additionally, it starts a
     * scheduler for scheduling tasks.
     */
    private void initialiseList() {
        ScheduleHelper scheduleHelper = new ScheduleHelper(getActivity());
        scheduleHelper.startScheduler();

        final ListView sensableList = (ListView) getView().findViewById(R.id.saved_sensable_list);
        attachCursorLoader(sensableList);

        final TextView emptyFavouriteText = (TextView) getView().findViewById(R.id.text_no_favourite);
        sensableList.setEmptyView(emptyFavouriteText);

        //add onclick to ListView
        sensableList.setOnItemClickListener(getSavedSensableListener());

    }

    /**
     * Returns an AdapterView.OnItemClickListener object that is called when an item in
     * a list is clicked. It creates an intent to open the SensableActivity class, passing
     * sensable data as an extra through the EXTRA_SENSABLE field.
     *
     * @returns an AdapterView.OnItemClickListener.
     */
    private AdapterView.OnItemClickListener getSavedSensableListener() {
        return new AdapterView.OnItemClickListener() {
            /**
             * Handles an item click event from an adapter view. It creates a new intent to start
             * the SensableActivity class, retrieves a sensable object from the SavedSensablesTable
             * using the selected position, adds the object as an extra to the intent, and starts
             * the activity with the intent.
             *
             * @param parent AdapterView that generated the event, allowing access to its state
             * and properties such as the selected item.
             *
             * @param view View associated with the selected item in the AdapterView.
             *
             * @param position 0-based index of the item that has been clicked in the AdapterView.
             *
             * @param id 64-bit integer row ID of the item selected from the adapter.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), SensableActivity.class);
                Sensable sensable = SavedSensablesTable.getSensable((Cursor) parent.getItemAtPosition(position));
                intent.putExtra(EXTRA_SENSABLE, sensable);
                startActivity(intent);
            }
        };
    }

    private static final String[] SENSABLE_PROJECTION = new String[]{
            SavedSensablesTable.COLUMN_ID,
            SavedSensablesTable.COLUMN_SENSOR_ID,
            SavedSensablesTable.COLUMN_SENSOR_TYPE,
            SavedSensablesTable.COLUMN_NAME,
            SavedSensablesTable.COLUMN_LOCATION_LATITUDE,
            SavedSensablesTable.COLUMN_LOCATION_LONGITUDE,
            SavedSensablesTable.COLUMN_LAST_SAMPLE,
            SavedSensablesTable.COLUMN_UNIT
    };

    /**
     * Initializes a SensableListAdapter and sets it to a ListView. It also prepares a
     * loader manager to fetch data from the database, specifically from the SavedSensablesTable.
     * The loader is initialized with an ID of 0 and a null bundle.
     *
     * @param listView ListView to which the SensableListAdapter is set as its adapter.
     *
     * Set: list view
     * Properties: List view set adapter.
     */
    private void attachCursorLoader(ListView listView) {
        SensableListAdapter.AdapterHolder projection = new SensableListAdapter.AdapterHolder();
        projection.ID = SavedSensablesTable.COLUMN_ID;
        projection.NAME = SavedSensablesTable.COLUMN_NAME;
        projection.SENSOR_ID = SavedSensablesTable.COLUMN_SENSOR_ID;
        projection.VALUE = SavedSensablesTable.COLUMN_LAST_SAMPLE;
        projection.TYPE = SavedSensablesTable.COLUMN_SENSOR_TYPE;
        projection.UNIT = SavedSensablesTable.COLUMN_UNIT;

        mAdapter = new SensableListAdapter(getActivity(), R.id.row_sensable_id, null, projection);
        listView.setAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Creates and returns a `CursorLoader`. The loader retrieves data from the
     * `SensableContentProvider` using its `CONTENT_URI`, a set of projection columns
     * (`SENSABLE_PROJECTION`), and optional selection, selection arguments, and sort order.
     *
     * @param id 0-based identifier of the loader being created and is used to differentiate
     * between multiple loaders that may be simultaneously running in an activity or fragment.
     *
     * @param args Bundle object that contains any arguments passed to the LoaderManager
     * when it was created and can be used to initialize the loader's data retrieval process.
     *
     * @returns a CursorLoader instance.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(getActivity(), SensableContentProvider.CONTENT_URI,
                SENSABLE_PROJECTION, null, null, null);
    }

    /**
     * Swaps a new cursor into a specified adapter, replacing any existing cursor. This
     * is done when a loader finishes loading data, and ensures that the adapter is updated
     * with the latest information once it becomes available.
     *
     * @param loader Loader instance that manages the loading process, providing access
     * to the loaded data.
     *
     * @param data Cursor object returned by the Loader, which is used to update the
     * adapter with new data for display.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)

        mAdapter.swapCursor(data);
    }

    /**
     * Resets the adapter's cursor when it's being destroyed, ensuring that the previous
     * cursor is closed and not used anymore. This is necessary to prevent memory leaks
     * or errors due to stale data. The function swaps the cursor with null, effectively
     * resetting the adapter.
     *
     * @param loader Loader that is being reset, which provides access to the Cursor
     * object that is being closed.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }
}
