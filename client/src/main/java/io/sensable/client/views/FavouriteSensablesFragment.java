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
 * is responsible for displaying a list of favourite sensors in a Fragment. It utilizes
 * a CursorLoader to load the data from a content provider and an adapter to display
 * the data in a ListView. The fragment also provides methods for initializing the
 * list and attaching a cursor loader to the ListView.
 */
public class FavouriteSensablesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = FavouriteSensablesFragment.class.getSimpleName();
    public final static String EXTRA_SENSABLE = "io.sensable.sensable";

    SensableListAdapter mAdapter;

    /**
     * inflates a fragment's layout from a resource file and returns the resulting view.
     * 
     * @param layoutInflater inflation API that is used to inflate the layout of the
     * fragment based on the provided resource ID.
     * 
     * 	- `LayoutInflater layoutInflater`: This is an instance of the `LayoutInflater`
     * class, which is used to inflate XML layouts into Android views.
     * 	- `ViewGroup container`: This is the parent view group that will contain the newly
     * inflated view.
     * 	- `Bundle savedInstanceState`: This is an instance of the `Bundle` class, which
     * contains any saved state information for this fragment.
     * 
     * @param container parent view group where the inflated layout will be added as a child.
     * 
     * 	- `R.layout.favourite_sensables_fragment`: The layout file that contains the
     * elements for this fragment.
     * 	- `LayoutInflater`: An object used to inflate the layout from the resource file.
     * 	- `ViewGroup`: A container view group that holds other views.
     * 	- `Bundle`: A data storage container used to store saved state.
     * 
     * @param savedInstanceState saved state of the fragment, which can be used to restore
     * the fragment's properties if it is recreated due to a configuration change or an
     * error.
     * 
     * 	- `savedInstanceState`: This is a bundle that contains arbitrary data from the
     * previous state of this fragment, if it was saved by the system. The bundle's
     * properties include the values of its fields and the references to any objects that
     * were stored in it.
     * 
     * @returns an inflated view of the `R.layout.favourite_sensables_fragment` layout.
     * 
     * 	- `LayoutInflater`: The LayoutInflater object that is used to inflate the layout
     * from the resource file.
     * 	- `ViewGroup`: The ViewGroup container that holds the fragment's layout.
     * 	- `Bundle`: The Bundle object that contains any saved instance state, if available.
     */
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        return layoutInflater.inflate(R.layout.favourite_sensables_fragment, container, false);
    }

    /**
     * initializes a list upon calling the superclass's `onStart` method.
     */
    @Override
    public void onStart() {
        super.onStart();
        initialiseList();
    }

    /**
     * initializes a list view with saved sensables and sets an empty view, on item click
     * listener for the list view.
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
     * creates an `AdapterView.OnItemClickListener` that, when an item is clicked, starts
     * an intent to display the `SensableActivity` with the corresponding sensable data
     * extracted from a savable table.
     * 
     * @returns an `OnItemClickListener` that handles clicks on list items and starts an
     * intent to display the `SensableActivity`.
     * 
     * 	- `AdapterView.OnItemClickListener`: This interface represents an adapter view's
     * click listener. It is implemented to handle item clicks on the adapter view.
     * 	- `onItemClick()`: This method is called when an item in the adapter view is
     * clicked. It takes four parameters: `parent`, `view`, `position`, and `id`.
     * 	- `getActivity()`: This method returns the current activity, which is used to
     * start a new activity when the sensable is clicked.
     * 	- `SensableActivity.class`: This class represents the activity that is started
     * when an item in the adapter view is clicked. It takes a single extra parameter,
     * `sensable`, which is a `Sensable` object obtained from the `getSensable()` method
     * of the `SavedSensablesTable`.
     * 	- `Sensable`: This class represents a sensable data object, which contains
     * information about a specific sensation. It has several attributes, including `id`,
     * `name`, `description`, and `value`.
     */
    private AdapterView.OnItemClickListener getSavedSensableListener() {
        return new AdapterView.OnItemClickListener() {
            /**
             * is called when an item in a list is clicked. It creates an intent to open the
             * `SensableActivity` class, passing the sensable data as an extra throughput the
             * `EXTRA_SENSABLE` field.
             * 
             * @param parent AdapterView whose item has been clicked and is being passed to the
             * function as an argument.
             * 
             * 	- `AdapterView`: This is the type of the view that triggered the event. It provides
             * information about the adapter that was used to display the items in the list and
             * the position of the item that was clicked.
             * 	- `view`: This is the view that was clicked, which is a `View` object.
             * 	- `position`: This is the position of the item in the list, which can be used to
             * determine which item was clicked.
             * 	- `id`: This is the ID of the item that was clicked, which can be used to identify
             * the item in the list.
             * 
             * @param view View object that was clicked and triggered the onItemClick() method.
             * 
             * 1/ `parent`: The parent AdapterView instance that triggered the click event.
             * 2/ `position`: The position of the item in the adapter view.
             * 3/ `id`: The ID of the selected item.
             * 4/ `intent`: An Intent object used to launch the next activity.
             * 
             * @param position 0-based index of the selected item in the adapter view.
             * 
             * @param id 4-byte unique identifier of the item that was clicked, which is used to
             * identify the corresponding sensable object in the SavedSensablesTable.
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
     * attaches a CursorLoader to a ListView, allowing the display of sensable data from
     * a SQLite database.
     * 
     * @param listView ListView that will be populated with data by the adapter created
     * and set by the function.
     * 
     * 	- `listView`: A reference to a ListView object in the activity. Its ID is specified
     * by `R.id.row_sensable_id`.
     * 	- `mAdapter`: An instance of SensableListAdapter, which is responsible for
     * displaying the data in the ListView. The adapter's constructor takes the activity
     * as an argument and specifies null as the second parameter.
     * 
     * The function then sets the adapter for the ListView using the `setAdapter` method.
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
     * creates a `CursorLoader` instance that fetches data from the `SensableContentProvider`
     * and returns a `Cursor` object for display.
     * 
     * @param id identifier of the loader, which is used to uniquely identify the loader
     * instance across multiple calls to the `onCreateLoader()` method.
     * 
     * @param args application-specific data passed to the `onCreateLoader` method by the
     * caller.
     * 
     * The input `args` bundle has a single non-null component named `CONTENT_URI`, which
     * represents the content provider's URI for displaying data. The `SENSABLE_PROJECTION`
     * string is defined in the function, and it specifies the columns to be retrieved
     * from the content provider. Additionally, there are three nullable reference
     * parameters: `null`, `null`, and `null`.
     * 
     * @returns a `CursorLoader` instance that loads data from a content provider.
     * 
     * 	- `id`: The unique identifier for the loader, which is an integer value.
     * 	- `args`: A Bundle object that contains additional data that can be used by the
     * loader.
     * 	- `SensableContentProvider.CONTENT_URI`: The content URI of the data being
     * displayed, which is a string value.
     * 	- `SENSABLE_PROJECTION`: An array of strings representing the fields to be queried
     * in the Cursor object returned by the loader, which can be modified or extended
     * depending on the requirements of the application.
     * 	- `null`: A null reference that indicates no data is available for the specified
     * URI.
     * 	- `null`: A null reference that indicates no additional data is available beyond
     * what is included in the Cursor object.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(getActivity(), SensableContentProvider.CONTENT_URI,
                SENSABLE_PROJECTION, null, null, null);
    }

    /**
     * swaps a new cursor with the existing one in a `Loader` object, allowing the adapter
     * to display the new data.
     * 
     * @param loader Loader object that is responsible for loading the data into the
     * Cursor that is passed as the second parameter to the `onLoadFinished()` method.
     * 
     * 	- `loader`: A `Loader<Cursor>` instance that represents the result of a load
     * operation. It provides access to the data in the form of a `Cursor`.
     * 
     * @param data cursor containing the data to be displayed by the adapter after the
     * load operation has completed.
     * 
     * 	- `mAdapter`: The adapter associated with the loader, which is passed as an
     * argument to the function.
     * 	- `data`: The cursor containing the new data, which is swapped in place of the
     * previous cursor by calling `mAdapter.swapCursor(data)`.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)

        mAdapter.swapCursor(data);
    }

    /**
     * is called when the last Cursor provided to `onLoadFinished()` is about to be closed,
     * and it ensures that the adapter is no longer using the Cursor by swapping it with
     * null.
     * 
     * @param loader Loader object that provides the Cursor data to be processed by the
     * onLoadFinished() method.
     * 
     * The `onLoaderReset()` method is called when the last Cursor provided to
     * `onLoadFinished()` is about to be closed. It ensures that the `mAdapter` object
     * no longer uses the cursor by calling its `swapCursor()` method and passing in a
     * null value.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }
}
