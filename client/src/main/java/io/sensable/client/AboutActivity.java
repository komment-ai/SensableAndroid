package io.sensable.client;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import io.sensable.SensableService;
import io.sensable.model.Statistics;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Displays information about the application, including a formatted text view and
 * retrieves sample count from sensable.io via Rest API.
 */
public class AboutActivity extends Activity {
    private static final String TAG = AboutActivity.class.getSimpleName();

    private TextView statistics;

    /**
     * Initializes the activity's layout and displays formatted text from a string resource,
     * along with loading additional statistics.
     *
     * @param savedInstanceState bundle of information previously saved by the activity,
     * allowing it to restore its state after being destroyed.
     *
     * Destructure `savedInstanceState` into its main properties:
     * - `boolean isRestored`
     * - `Bundle` `state`
     * - `ClassLoader` `loader`
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        statistics = (TextView) findViewById(R.id.about_statistics);


        TextView view = (TextView)findViewById(R.id.about_text);
        String formattedText = getString(R.string.about_text);
        Spanned result = Html.fromHtml(formattedText);
        view.setText(result);

        loadStatistics();

    }

    /**
     * retrieves sample count from sensable.io via Rest API and updates a label with the
     * count.
     */
    private void loadStatistics() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("http://sensable.io")
                .build();

        SensableService service = restAdapter.create(SensableService.class);

        service.getStatistics(new Callback<Statistics>() {
            /**
             * is called when the Statistics callback is successful. It logs the number of samples
             * retrieved to the log cat and sets the text view to display the number of samples.
             * 
             * @param statisticsResponse count of samples successfully processed by the Statistics
             * service, which is returned as an integer value in the success callback function.
             * 
             * * `getCount()`: This method returns the total number of samples in the response.
             * * `Statistics`: This class represents the statistics of the samples, including the
             * total count and other relevant information.
             * 
             * @param response response object that is passed to the `success` method as an
             * argument, providing additional information about the callback execution result.
             * 
             * The input `response` is of type `Response`, which contains information about the
             * success or failure of the API call.
             * The `Statistics` object `statisticsResponse` returned by the API is stored in a
             * variable named `statistics`.
             * The `count` property of `statisticsResponse` is accessed using the dot notation,
             * and its value is formatted using `NumberFormat.getInstance()` and appended to a
             * text view named `textView`.
             */
            @Override
            public void success(Statistics statisticsResponse, Response response) {
                Log.d(TAG, "Statistics callback Success: " + statisticsResponse.getCount());

                statistics.setText(NumberFormat.getInstance().format(statisticsResponse.getCount()) + " samples on sensable.io");
            }

            /**
             * handles callback failures by logging an error message and making the `statistics`
             * view invisible.
             * 
             * @param retrofitError error message that occurs when a callback fails, and it is
             * logged with a tag and its toString method is called to obtain a human-readable
             * string representation of the error.
             * 
             * * `toString()`: Returns a string representation of the error object, which can be
             * used for debugging or logging purposes.
             */
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Callback failure" + retrofitError.toString());
                statistics.setVisibility(View.GONE);
            }
        });
    }

}




