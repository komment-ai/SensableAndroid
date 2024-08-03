package io.sensable.client.views;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import io.sensable.client.R;
import io.sensable.client.SensorHelper;
import io.sensable.client.sqlite.SavedSensablesTable;
import io.sensable.model.Sample;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by simonmadine on 23/07/2014.
 */
/**
 * is an extension of CursorAdapter that provides a custom layout for displaying
 * sensor data from a database. It takes in a context, layout resource ID, cursor,
 * and AdapterHolder projection, and uses these inputs to inflate a custom layout and
 * bind the data from the cursor to the appropriate UI elements. The AdapterHolder
 * class contains the data from the cursor, including the sensor ID, name, type,
 * value, and unit.
 */
public class SensableListAdapter extends CursorAdapter {
    private static final String TAG = SensableListAdapter.class.getSimpleName();

    Context context;
    int layoutResourceId;
    Cursor cursor;
    private final LayoutInflater mInflater;
    private AdapterHolder projection;


    public SensableListAdapter(Context context, int layoutResourceId, Cursor cursor, AdapterHolder projection) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);

        this.mInflater = LayoutInflater.from(context);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.cursor = cursor;
        this.projection = projection;
    }

    /**
     * inflates a layout for a list row based on the provided cursor and parent view group.
     * 
     * @param context Android framework's current context, which is used to inflate the
     * layout for the list row.
     * 
     * 	- `Context`: This is an object that represents the current application context.
     * It provides access to various resources and services, such as the user interface,
     * data storage, and network connections.
     * 	- `Cursor`: A cursor object represents a database result set, which can be retrieved
     * through various means such as SQL queries or web services. The cursor contains
     * information about the rows in the result set, including column values for each row.
     * 
     * @param cursor Cursor object containing the data to be displayed in the list row.
     * 
     * 	- `Context context`: The application's current context, which contains essential
     * resources and services for rendering the view.
     * 	- `Cursor cursor`: A cursor object representing a database row, containing
     * attributes such as ID, title, and other relevant data.
     * 	- `ViewGroup parent`: The parent view group that will hold the new view once created.
     * 
     * @param parent ViewGroup that will hold or contain the newly inflated View.
     * 
     * 	- `Context`: The context of the application that initialized the view.
     * 	- `Cursor`: A cursor object representing the data to be displayed in the view.
     * 	- `ViewGroup`: The parent view group that contains the newly inflated view.
     * 
     * @returns a fully inflated View object representing a list row layout with customizable
     * elements.
     * 
     * 	- `mInflater`: This is an instance of `LayoutInflater`, which is used to inflate
     * the layout for the list row.
     * 	- `R.layout.sensable_list_row`: This is the resource ID for the layout file that
     * is inflated by `mInflater`.
     * 	- `Context`: This is the context object, which contains information about the
     * device and operating system on which the app is running.
     * 	- `Cursor cursor`: This is an instance of `Cursor`, which contains the data that
     * will be displayed in the list row.
     * 	- `ViewGroup parent`: This is the parent view group, which is used to inflate the
     * layout for the list row.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.sensable_list_row, parent, false);
        return view;
    }

    /**
     * binds a view to a sensor data, displaying the sensor name, ID, type, value and
     * unit. It also sets the background color of the view based on the sensor ID and ID.
     * 
     * @param view View object that will be populated with data from the cursor.
     * 
     * 	- `view`: The View object to be populated with data from the cursor.
     * 	- `context`: The Context object that provides access to resources and information
     * necessary for the function.
     * 	- `cursor`: The Cursor object containing the data to be bound to the View.
     * 
     * The View object has several properties:
     * 
     * 	- `findViewById(int id)`: Allows to find and retrieve a view by its ID. In this
     * case, it is used to retrieve the `TextView` objects with IDs `R.id.row_sensable_name`,
     * `R.id.row_sensable_id`, `R.id.row_sensable_type`, and `R.id.row_sensable_sample_value`.
     * 	- `setImageResource(int resId)`: Sets the image resource of an ImageView object.
     * In this case, it is used to set the image resource based on the value of the `TYPE`
     * column in the cursor.
     * 	- `setText(CharSequence text)`: Sets the text of a TextView object. In this case,
     * it is used to set the text of the `name`, `sensorId`, and `unit` TextViews based
     * on the values in the corresponding columns of the cursor.
     * 	- `setBackgroundColor(int color)`: Sets the background color of a View object.
     * In this case, it is used to set the background color of the View based on the value
     * of the `SENSOR_ID` column in the cursor.
     * 
     * @param context Android context of the view being bound, which is used to obtain
     * resources and information necessary for binding the view to the sensor data.
     * 
     * 	- `Context context`: This is the application context, which provides access to
     * various resources and functionality within the application.
     * 	- `Cursor cursor`: This is the result set from a database query, containing the
     * data that will be displayed in the fragment's list view.
     * 	- `projection`: This is an instance of `SimpleCursorAdapter.SelectionKey`,
     * representing the columns and values in the cursor that are to be displayed in the
     * list view.
     * 
     * The function then processes the cursor data as follows:
     * 
     * 	- It retrieves the text value from the `NAME` column, and sets it as the text for
     * a `TextView` with the ID `R.id.row_sensable_name`.
     * 	- It retrieves the `SENSOR_ID`, `TYPE`, and `VALUE` columns, and sets their
     * corresponding values as text for `TextView`s with IDs `R.id.row_sensable_id`,
     * `R.id.row_sensable_type`, and `R.id.row_sensable_sample_value`, respectively.
     * 	- It retrieves the `UNIT` column, and sets its value as text for a `TextView`
     * with the ID `R.id.row_sensable_sample_unit`.
     * 	- It sets the background color of the list view item to a color determined by the
     * combination of the `SENSOR_ID` and `ID` values.
     * 
     * Note that the `Context` object provides access to various resources and functionality
     * within the application, but its specific properties or attributes are not mentioned
     * in this function.
     * 
     * @param cursor Cursor object returned by the database query, which contains the
     * data to be displayed in each row of the layout.
     * 
     * 	- `projection`: The projection used to extract the data from the cursor.
     * 	- `Name`: A column index representing the name of the sensor.
     * 	- `SensorId`: A column index representing the ID of the sensor.
     * 	- `Type`: A column index representing the type of sensor (e.g., temperature,
     * humidity, etc.).
     * 	- `Value`: A column index representing the sample value.
     * 	- `Unit`: A column index representing the unit of measurement for the sample value.
     * 	- `JSONObject`: A JSON object containing the raw data from the Value column.
     * 	- `Sample`: An instance of the Sample class, which contains the parsed JSON data.
     * 	- `DecimalFormat`: An instance of the DecimalFormat class, used to format the
     * sample value as a decimal number.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = (TextView) view.findViewById(R.id.row_sensable_name);
        TextView sensorId = (TextView) view.findViewById(R.id.row_sensable_id);
        ImageView sensorType = (ImageView) view.findViewById(R.id.row_sensable_type);
        TextView value = (TextView) view.findViewById(R.id.row_sensable_sample_value);
        TextView unit = (TextView) view.findViewById(R.id.row_sensable_sample_unit);

        if(cursor.getColumnIndex(projection.NAME) == -1) {
            name.setText("");
        } else {
            name.setText(cursor.getString(cursor.getColumnIndex(projection.NAME)));
        }

        sensorId.setText(cursor.getString(cursor.getColumnIndex(projection.SENSOR_ID)));
        sensorType.setImageResource(SensorHelper.determineImage(cursor.getString(cursor.getColumnIndex(projection.TYPE))));

        try {
            JSONObject json = new JSONObject(cursor.getString(cursor.getColumnIndex(projection.VALUE)));
            Sample sample = new Sample(json);
            DecimalFormat df = new DecimalFormat("#.##");
            value.setText(df.format(sample.getValue()));
        } catch (JSONException e) {
            value.setText("?");
            e.printStackTrace();
        }

        unit.setText(cursor.getString(cursor.getColumnIndex(projection.UNIT)));

        view.setBackgroundColor(getColour(cursor.getString(cursor.getColumnIndex(projection.SENSOR_ID)) + cursor.getString(cursor.getColumnIndex(projection.ID))));

    }

    /**
     * generates a random color based on the hash code of a given string, using an array
     * of predefined colors as a probability distribution.
     * 
     * @param name 8-bit hash code of the name, which is used to generate a random index
     * into an array of colors.
     * 
     * @returns an integer representing a random color between 0 and 255, determined by
     * hash code of the input string.
     */
    private int getColour(String name) {
        Random rnd = new Random(name.hashCode());
        int[] colors = new int[]{
                Color.argb(255, 26, 188, 156),
                Color.argb(255, 241, 196, 15),
                Color.argb(255, 231, 76, 60),
                Color.argb(255, 46, 204, 113),
                Color.argb(255, 52, 152, 219),
                Color.argb(255, 155, 89, 182),
                Color.argb(255, 52, 73, 94),
                Color.argb(255, 243, 156, 18),
                Color.argb(255, 211, 84, 0)
        };
        return colors[rnd.nextInt(colors.length-1)];

    }

    /**
     * represents a holder for sensor data adapter attributes.
     * Fields:
     * 	- ID (String): a unique identifier for an object or data point.
     * 	- NAME (String): a name associated with an ID, sensor ID, type, value, and unit.
     * 	- SENSOR_ID (String): a unique identifier for each sensor.
     * 	- TYPE (String): a string value indicating the type of sensable being displayed,
     * with possible values being "temperature", "humidity", "pressure", etc.
     * 	- VALUE (String): a string value with no additional context or information beyond
     * what is explicitly defined in the class definition.
     * 	- UNIT (String): a textual representation of the unit of measurement associated
     * with a sample value.
     */
    static class AdapterHolder {
        String ID;
        String NAME;
        String SENSOR_ID;
        String TYPE;
        String VALUE;
        String UNIT;
    }
}
