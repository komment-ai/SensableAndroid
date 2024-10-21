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
 * Is a fragment that displays a list of sensibles retrieved from an API endpoint
 * using Retrofit. It initializes a ListView with an ArrayAdapter to display the list
 * and adds an OnItemClickListener to handle item clicks, launching the SensableActivity
 * with extra data when an item is clicked.
 */
public class RemoteSensablesFragment extends Fragment {

    private static final String TAG = RemoteSensablesFragment.class.getSimpleName();
    public final static String EXTRA_SENSABLE = "io.sensable.sensable";

    private ArrayList<Sensable> mSensables;
    private ArrayAdapter<Sensable> mListArrayAdapter;


    /**
     * Inflates a view from an XML layout file and sets it as the content for the fragment.
     * It uses the `LayoutInflater` class to inflate the layout and returns the inflated
     * view. The view is then displayed on the screen within the container provided.
     *
     * @param layoutInflater factory that creates instances of layouts and returns a view
     * hierarchy from an XML resource file.
     *
     * @param container 2D array of views that will hold the inflated layout.
     *
     * @param savedInstanceState bundle containing saved key-value pairs to be used when
     * restoring the fragment's state after being destroyed and recreated.
     *
     * @returns a view instance created from the specified layout resource.
     */
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        return layoutInflater.inflate(R.layout.remote_sensables_fragment, container, false);
    }

    /**
     * Initializes a list and retrieves data from an API using Retrofit. It handles
     * successful and failed callbacks by updating the UI with the retrieved data or
     * logging errors, respectively.
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
             * Handles a callback response by logging a message, updating a local list of sensables,
             * and notifying an adapter to refresh its display of the updated data. It does so
             * when a list of sensables is received with a successful response.
             *
             * @param sensables list of Sensable objects returned by the API, which is then cleared
             * and replaced with the new list in the local cache.
             *
             * @param response response data from the API call and is not utilized within the
             * scope of this method.
             */
            @Override
            public void success(List<Sensable> sensables, Response response) {
                Log.d(TAG, "Callback Success " + sensables.size());
                mSensables.clear();
                mSensables.addAll(sensables);
                mListArrayAdapter.notifyDataSetChanged();
            }

            /**
             * Handles a Retrofit error by logging an error message to the console with the
             * provided `RetrofitError`. The error is logged at the `ERROR` level, indicating a
             * serious problem occurred.
             *
             * @param retrofitError exception or error that occurred during the Retrofit request
             * and provides details about the failure.
             */
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Callback failure " + retrofitError.toString());
            }
        });

    }

    /**
     * Initializes a list view with a list of Sensables and sets an on-click listener to
     * handle item selection events. When an item is clicked, it starts a new activity,
     * passing the selected Sensable object as an extra through an intent.
     */
    private void initialiseList() {
        final ListView sensableList = (ListView) getView().findViewById(R.id.sensable_list);

        mSensables = new ArrayList<Sensable>();
        mListArrayAdapter = new ArrayAdapter<Sensable>(getActivity(), android.R.layout.simple_list_item_1, mSensables);
        sensableList.setAdapter(mListArrayAdapter);

        //add onclick to listview
        sensableList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Processes an item selection event from an adapter view. It creates an intent to
             * start a new activity and passes the selected item as an extra parameter. The
             * activity is launched with the specified item information.
             *
             * @param parent adapter view that holds the item at the specified position and is
             * used to retrieve the item using the `getItemAtPosition()` method.
             *
             * @param view View object associated with the item at the specified position in the
             * AdapterView that receives the click event.
             *
             * @param position 0-based index of the item selected from the adapter that triggered
             * the onItemClick event.
             *
             * @param id 64-bit unique identifier of the item at the specified `position` in the
             * adapter.
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
