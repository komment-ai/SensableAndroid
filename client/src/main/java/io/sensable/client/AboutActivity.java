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
 * Extends Activity and provides functionality to retrieve sample count from sensable.io
 * via Rest API and display it in a text view. It also sets formatted text from a
 * resource string in another text view. The loadStatistics method is responsible for
 * fetching the statistics.
 */
public class AboutActivity extends Activity {
    private static final String TAG = AboutActivity.class.getSimpleName();

    private TextView statistics;

    /**
     * Sets the layout, retrieves a TextView by ID, formats HTML text, and displays it
     * on the screen. It also loads statistics and assigns them to another TextView. This
     * is a typical initialization method for an Android activity.
     *
     * @param savedInstanceState Bundle object that contains the activity's saved state
     * from a previous invocation of its lifecycle methods.
     *
     * Bundle contains key-value pairs to preserve instance state across process death.
     * It includes data about fragments that were previously added and removed, as well
     * as other relevant details.
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
     * Loads statistics from a REST API and updates a text view with the count of samples
     * processed. It handles successful responses by logging the count and updating the
     * text view, and failure cases by logging errors and hiding the text view.
     */
    private void loadStatistics() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("http://sensable.io")
                .build();

        SensableService service = restAdapter.create(SensableService.class);

        service.getStatistics(new Callback<Statistics>() {
            /**
             * Logs a debug message and updates the UI component `statistics` with the count from
             * the received `Statistics` object, formatted as a string. The format includes the
             * count and a text describing the data source "sensable.io".
             *
             * @param statisticsResponse response object that contains the count of samples from
             * the API, which is then used to set the text of a UI component named `statistics`.
             *
             * @param response response object that contains the result of the request, but its
             * content is not used within the method.
             */
            @Override
            public void success(Statistics statisticsResponse, Response response) {
                Log.d(TAG, "Statistics callback Success: " + statisticsResponse.getCount());

                statistics.setText(NumberFormat.getInstance().format(statisticsResponse.getCount()) + " samples on sensable.io");
            }

            /**
             * Logs an error message to the console when a callback request fails. It also hides
             * a UI component named `statistics`. The function is part of a class that handles
             * API requests using Retrofit, and it deals with exceptions or errors that occur
             * during the request process.
             *
             * @param retrofitError error that occurred during the network request, providing
             * information about the failure such as the HTTP status code and any error message.
             */
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Callback failure" + retrofitError.toString());
                statistics.setVisibility(View.GONE);
            }
        });
    }

}




