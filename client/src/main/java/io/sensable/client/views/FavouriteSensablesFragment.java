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
 * Is a fragment in an Android application that displays a list of favourite sensors
 * and allows users to view detailed information about each sensor by clicking on it.
 * The class uses a CursorLoader to load data from a SQLite database and display it
 * in a ListView.
 */
public class FavouriteSensablesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = FavouriteSensablesFragment.class.getSimpleName();
    public final static String EXTRA_SENSABLE = "io.sensable.sensable";

    SensableListAdapter mAdapter;

    /**
     * Inflates a view from an XML layout file into a new instance of the Fragment class.
     * It specifies the ID of the layout to inflate and returns it as the root view for
     * the fragment. The container is the parent view group where the inflated view will
     * be placed.
     *
     * @param layoutInflater LayoutInflater object that is used to inflate a view hierarchy
     * from a XML layout resource file.
     *
     * @param container parent view group where the layout is inflated and returned,
     * allowing for the new view hierarchy to be added to the activity's view tree.
     *
     * @param savedInstanceState bundle object that holds the UI state of the fragment
     * being restored after its process is killed by the Android system.
     *
     * @returns a view inflated from the `R.layout.favourite_sensables_fragment` XML layout.
     */
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        return layoutInflater.inflate(R.layout.favourite_sensables_fragment, container, false);
    }

    /**
     * Initializes a list when an application starts. It overrides the `onStart` method
     * from its superclass, ensuring that the list is set up at the beginning of the
     * program's lifecycle. The `initialiseList` method is called to complete the
     * initialization process.
     */
    @Override
    public void onStart() {
        super.onStart();
        initialiseList();
    }

    /**
     * Initializes a list view and its associated components for displaying sensable data.
     * It starts a scheduler, attaches a cursor loader to the list view, sets an empty
     * view, and adds an on-click listener to the list view.
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
     * Returns an event listener for a list view item click. When an item is clicked, it
     * creates an intent to open the `SensableActivity`, retrieves the corresponding
     * sensable object from the database, and passes it as an extra in the intent.
     *
     * @returns an `AdapterView.OnItemClickListener`.
     *
     * The returned object is an instance of `AdapterView.OnItemClickListener`, which
     * implements the `onItemClick` method. This method takes four parameters: `parent`,
     * `view`, `position`, and `id`.
     */
    private AdapterView.OnItemClickListener getSavedSensableListener() {
        return new AdapterView.OnItemClickListener() {
            /**
             * Handles an item click event from an adapter view. It creates an intent to start a
             * new activity, retrieves the corresponding sensable object from a table using a
             * cursor, adds it as an extra to the intent, and then starts the activity with the
             * specified sensable.
             *
             * @param parent AdapterView that generated the click event and provides access to
             * the item at the given position.
             *
             * @param view View object that is clicked, allowing for manipulation of its properties
             * or actions to be performed on it.
             *
             * @param position 1-based index of the item within the AdapterView that was clicked,
             * used to retrieve the corresponding data from the adapter.
             *
             * @param id 64-bit row ID of the item at the given position in the adapter's data set.
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
     * Initializes and sets a list view adapter to display data from a database table.
     * It prepares a loader to retrieve data and attaches it to the list view for display.
     *
     * @param listView ListView to which an adapter is set, displaying data retrieved
     * from the database via a CursorLoader.
     *
     * Gets or sets the list view's adapter. It is an AdapterView that shows a list of items.
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
     * Creates and returns a CursorLoader that retrieves data from the Sensable Content
     * Provider using its CONTENT_URI, SENSABLE_PROJECTION, and query parameters. The
     * retrieved cursor is then used to display the data.
     *
     * @param id 0-based identifier of the Loader to be created or replaced.
     *
     * @param args Bundle object that contains additional data and is passed from an
     * Activity to the LoaderManager for further processing.
     *
     * @returns a CursorLoader instance for retrieving data from Sensable Content Provider.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(getActivity(), SensableContentProvider.CONTENT_URI,
                SENSABLE_PROJECTION, null, null, null);
    }

    /**
     * Swaps the loaded cursor into the adapter, replacing any existing cursor. The
     * framework automatically closes the old cursor after the new one is set. This allows
     * the data to be displayed in the UI once it has been successfully loaded.
     *
     * @param loader Loader object that triggered this callback, providing the Cursor
     * data as an argument for further processing.
     *
     * @param data Cursor object containing the query results, which is swapped with the
     * existing cursor by calling `mAdapter.swapCursor(data)`.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)

        mAdapter.swapCursor(data);
    }

    /**
     * Resets the cursor provided by the Loader and swaps it with null, ensuring the
     * previous cursor is closed and not used further. This is necessary when the last
     * cursor supplied to `onLoadFinished` is about to be closed.
     *
     * @param loader Loader that is being reset, providing information about the previous
     * cursor data.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }
}
