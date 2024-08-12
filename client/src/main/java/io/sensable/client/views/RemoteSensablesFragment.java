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
 * Extends Fragment and provides a list of sensibles retrieved from an API endpoint.
 * It initializes a RestAdapter to make API calls and uses a ListView to display the
 * list of sensibles, with an OnItemClickListener handling item clicks and launching
 * the SensableActivity.
 */
public class RemoteSensablesFragment extends Fragment {

    private static final String TAG = RemoteSensablesFragment.class.getSimpleName();
    public final static String EXTRA_SENSABLE = "io.sensable.sensable";

    private ArrayList<Sensable> mSensables;
    private ArrayAdapter<Sensable> mListArrayAdapter;


    /**
     * Inflates a layout file and returns it as the root view for a fragment. The inflated
     * layout is specified by the resource ID `R.layout.remote_sensables_fragment`, and
     * the container is provided as part of the method call.
     *
     * @param layoutInflater LayoutInflater object that is used to inflate or create an
     * instance of the layout specified by R.layout.remote_sensables_fragment and returns
     * the View object representing the inflated view hierarchy.
     *
     * @param container ViewGroup to which the View returned by the method will be added,
     * and it is used as the parent for the inflated view.
     *
     * @param savedInstanceState bundle of data previously saved by the host activity
     * through the `onSaveInstanceState` method, and is passed to the fragment for
     * restoration after its process is recreated.
     *
     * @returns an inflated view of a specific layout from the given resource.
     */
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        return layoutInflater.inflate(R.layout.remote_sensables_fragment, container, false);
    }

    /**
     * Initializes a list and makes a REST call to retrieve a list of sensibles from an
     * API. The response is processed and added to an internal list, then notifies an
     * adapter to update the UI. If the request fails, it logs the error to the console.
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
             * Processes a list of `sensables`. It logs a message, clears an existing list, updates
             * it with new elements, and notifies a data adapter to refresh its display.
             *
             * @param sensables list of Sensable objects returned by an API or similar operation,
             * which is then cleared and updated in the local cache.
             *
             * @param response result of an operation, which is not utilized in this callback method.
             */
            @Override
            public void success(List<Sensable> sensables, Response response) {
                Log.d(TAG, "Callback Success " + sensables.size());
                mSensables.clear();
                mSensables.addAll(sensables);
                mListArrayAdapter.notifyDataSetChanged();
            }

            /**
             * Logs an error message to the console when a Retrofit request fails. The error
             * message includes the details of the RetrofitError object. This allows for debugging
             * and troubleshooting failed requests.
             *
             * @param retrofitError error that occurred during the execution of a Retrofit request,
             * and its value is passed to the `Log.e` method for debugging purposes.
             */
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Callback failure " + retrofitError.toString());
            }
        });

    }

    /**
     * Initializes a list view with an array adapter and sets an on-click listener to
     * handle item selection events. When an item is clicked, it creates an intent for
     * the SensableActivity class and passes the selected item as an extra parameter.
     */
    private void initialiseList() {
        final ListView sensableList = (ListView) getView().findViewById(R.id.sensable_list);

        mSensables = new ArrayList<Sensable>();
        mListArrayAdapter = new ArrayAdapter<Sensable>(getActivity(), android.R.layout.simple_list_item_1, mSensables);
        sensableList.setAdapter(mListArrayAdapter);

        //add onclick to listview
        sensableList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Handles item clicks in an adapter view. When an item is clicked, it creates an
             * intent to start a new activity (`SensibleActivity`) with a specific extra parameter
             * (`EXTRA_SENSABLE`) set to the selected item from the parent adapter view. The
             * intent then starts the new activity.
             *
             * @param parent AdapterView whose item has been clicked, providing information about
             * the adapter that is being used to populate the view.
             *
             * @param view View object that was clicked by the user, providing access to its
             * properties and methods for further processing.
             *
             * @param position 0-based index of the item that was clicked within the AdapterView.
             *
             * @param id 64-bit unique identifier of the item at the specified position in the adapter.
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
