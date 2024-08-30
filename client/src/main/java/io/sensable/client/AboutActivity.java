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
 * Is designed to display about page information and load statistics from a Rest API
 * endpoint. It extends Android's Activity class and uses Retrofit for making HTTP
 * requests. The activity loads text from HTML strings and displays it in a TextView,
 * while also loading sample count data that is displayed on the same view.
 */
public class AboutActivity extends Activity {
    private static final String TAG = AboutActivity.class.getSimpleName();

    private TextView statistics;

    /**
     * Initializes the "About" activity by inflating its layout, populating a text view
     * with formatted HTML content, and loading statistics data from an unspecified method.
     * The function also retrieves a string resource for display in the text view.
     *
     * @param savedInstanceState Bundle object that contains the data of all the saved
     * instances when an activity is recreated, typically after a configuration change
     * or process death.
     *
     * Bundle savedInstanceState has one primary property - its key-value pair mapping.
     * This mapping associates keys with saved values such as states or data. The Bundle's
     * primary purpose is to persist and restore state information across application
     * lifecycle events.
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
     * Retrieves statistics from a remote service via HTTP requests, logging successful
     * responses and displaying the count to the user, while handling failures by logging
     * errors and hiding the view.
     */
    private void loadStatistics() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("http://sensable.io")
                .build();

        SensableService service = restAdapter.create(SensableService.class);

        service.getStatistics(new Callback<Statistics>() {
            /**
             * Displays a message and updates a UI component with data from a statistics response.
             * The message logs the count to the console, while the component displays the formatted
             * count alongside a string. The formatting uses the locale's default number format.
             *
             * @param statisticsResponse response from a server call, containing statistical data
             * such as the count of samples.
             *
             * Retrieves statistical data from the server and updates UI with the count of samples.
             * Formats the count of samples for display using NumberFormat.
             *
             * @param response response object from an API call, but its value is not used within
             * the function.
             */
            @Override
            public void success(Statistics statisticsResponse, Response response) {
                Log.d(TAG, "Statistics callback Success: " + statisticsResponse.getCount());

                statistics.setText(NumberFormat.getInstance().format(statisticsResponse.getCount()) + " samples on sensable.io");
            }

            /**
             * Logs an error message when a Retrofit request fails, including details about the
             * error. It then hides a view named `statistics`. The failure handling is triggered
             * by a callback mechanism in the Retrofit library.
             *
             * @param retrofitError error that occurred during the Retrofit request and provides
             * information about the failure.
             */
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Callback failure" + retrofitError.toString());
                statistics.setVisibility(View.GONE);
            }
        });
    }

}




