package io.sensable.client.views;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import io.sensable.SensableService;
import io.sensable.client.R;
import io.sensable.client.SensableActivity;
import io.sensable.model.Sensable;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simonmadine on 20/07/2014.
 */
/**
 * is a Java file that extends Fragment and provides a list of sensibles retrieved
 * from an API endpoint. The class has methods for initializing the list and adding
 * an onclick listener to the listview.
 */
public class RemoteSensablesFragment extends Fragment {

    private static final String TAG = RemoteSensablesFragment.class.getSimpleName();
    public final static String EXTRA_SENSABLE = "io.sensable.sensable";

    private ArrayList<Sensable> mSensables;
    private ArrayAdapter<Sensable> mListArrayAdapter;


    /**
     * inflates a layout from a resource file and returns the resulting view instance.
     * 
     * @param layoutInflater inflation factory that is used to inflate the remote sensables
     * fragment layout from the resource file R.layout.remote_sensables_fragment.
     * 
     * The `inflate` method is used to inflate a layout from an XML file specified by the
     * `R.layout.remote_sensables_fragment` constant. The third argument, `container`,
     * represents the parent view group in which the inflated layout will be added. Setting
     * `false` as the fourth argument indicates that the parent view group should not be
     * modified.
     * 
     * @param container ViewGroup that the inflated layout will be added to.
     * 
     * Returning the inflated layout from `R.layout.remote_sensables_fragment`. The
     * `inflate` method takes three parameters - the first is the layout resource ID, the
     * second is the parent view group, and the third is a boolean value indicating whether
     * the layout should be recreated or not. In this case, `false` means that the existing
     * layout in the `container` view should be reused instead of recreating a new one.
     * 
     * @param savedInstanceState saved state of the fragment, which can be useful in case
     * the fragment is restored from a previous configuration.
     * 
     * The `savedInstanceState` argument is a Bundle object that contains additional
     * information about the fragment when it is recreated or restored. It may contain
     * various properties such as the fragment's original state before being saved, the
     * ID of the fragment, and other attributes specific to the fragment.
     * 
     * @returns a View object representing the inflated layout from the
     * `R.layout.remote_sensables_fragment` file.
     * 
     * 	- The output is an inflated layout, specifically one from R.layout.remote_sensables_fragment.
     * 	- The layout is inflated into the container view group specified in the function
     * call.
     * 	- The layout is inflated with false as the third parameter, indicating that it
     * should not be treated as a new instance of the layout.
     * 
     * The properties of the output are:
     * 
     * 	- It is an instance of the View class.
     * 	- It has a reference to the layout defined in R.layout.remote_sensables_fragment.
     * 	- It has a reference to the container view group, which is used to display the
     * inflated layout.
     */
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        return layoutInflater.inflate(R.layout.remote_sensables_fragment, container, false);
    }

    /**
     * initializes a `RestAdapter` to make API calls to retrieve a list of sensors, and
     * then uses the `create` method to create an instance of the `SensableService` class.
     * The service is then called with a callback to receive the list of sensors in response.
     */
    @Override
    public void onStart() {
        super.onStart();
        initialiseList();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("http://sensable.io")
                .build();

        SensableService service = restAdapter.create(SensableService.class);

        service.listSensables(new Callback<List<Sensable>>() {
            /**
             * is called when a callback operation completes successfully. It adds the list of
             * sensibles to a internal list and notifies the ListArrayAdapter to update the UI.
             * 
             * @param sensables list of sensors that have successfully received data, which is
             * added to the existing list of sensors stored in the variable `mSensables`.
             * 
             * 	- `sensables`: A list of `Sensable` objects, which contain attributes such as
             * `id`, `name`, `description`, and `value`.
             * 
             * @param response response from the API call, which contains the list of sensibles
             * that will be processed and added to the adapter.
             * 
             * 	- `sensables`: A list of `Sensable` objects representing the successful callback
             * data.
             * 	- `size`: The number of elements in the `sensables` list.
             */
            @Override
            public void success(List<Sensable> sensables, Response response) {
                Log.d(TAG, "Callback Success " + sensables.size());
                mSensables.clear();
                mSensables.addAll(sensables);
                mListArrayAdapter.notifyDataSetChanged();
            }

            /**
             * is called when a callback fails, logging the error to the console with a tag and
             * including the error details in the log message.
             * 
             * @param retrofitError error that occurred during the callback, and its toString()
             * method is called to log the error message.
             * 
             * 	- `toString()`: returns a human-readable representation of the error object in
             * string format.
             */
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Callback failure " + retrofitError.toString());
            }
        });

    }

    /**
     * initializes a `ListView` by creating an adapter to display a list of `Sensable`
     * objects and adding an `OnItemClickListener` to handle item clicks and launch the
     * `SensableActivity`.
     */
    private void initialiseList() {
        final ListView sensableList = (ListView) getView().findViewById(R.id.sensable_list);

        mSensables = new ArrayList<Sensable>();
        mListArrayAdapter = new ArrayAdapter<Sensable>(getActivity(), android.R.layout.simple_list_item_1, mSensables);
        sensableList.setAdapter(mListArrayAdapter);

        //add onclick to listview
        sensableList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * is called when an item is clicked on a list, it creates an intent with the Sensable
             * activity and passes the extra data as a (Sensable) object.
             * 
             * @param parent AdapterView object from which the click event occurred.
             * 
             * 	- `AdapterView<?>`: This is an interface that represents an adapter view. The
             * type parameter `<?>` indicates that the view can be any subclass of `AdapterView`.
             * 	- `parent`: This is the current view being processed, which is an instance of `AdapterView`.
             * 	- `position`: This is an integer value representing the position of the item in
             * the list or grid that was clicked.
             * 	- `id`: This is a long value representing the ID of the item that was clicked.
             * 
             * @param view view that was clicked and is used to identify the position of the item
             * in the adapter.
             * 
             * 	- `parent`: The parent `AdapterView` that triggered the click event.
             * 	- `position`: The position of the item in the adapter.
             * 	- `id`: The unique identifier for the item.
             * 
             * @param position 0-based index of the selected item within the adapter's dataset,
             * which is passed to the corresponding `Sensable` object as an extra through the
             * `putExtra()` method when the user clicks on the item.
             * 
             * @param id identifier of the selected item.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), SensableActivity.class);
                intent.putExtra(EXTRA_SENSABLE, (Sensable) parent.getItemAtPosition(position));
                startActivity(intent);
            }
        });
    }

}
