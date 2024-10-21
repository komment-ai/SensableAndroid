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
 * Is an extension of CursorAdapter that provides a custom layout for displaying
 * sensor data from a database. It uses a cursor to fetch data and binds it to a view,
 * which can be customized with various elements such as text views, image views, and
 * colors based on the sensor ID and value. The class also generates random colors
 * based on the hash code of the sensor name.
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
     * Inflates a layout resource into a new view, specifically an instance of
     * `R.layout.sensable_list_row`, which is then returned for use. It takes three
     * parameters: the context, a cursor object, and a parent ViewGroup. The `false`
     * parameter indicates that the inflated view should not be attached to the parent immediately.
     *
     * @param context application environment and provides access to application-specific
     * resources, such as inflated views.
     *
     * @param cursor data to be displayed in the list row, allowing for the retrieval and
     * processing of specific column values within the cursor object.
     *
     * @param parent ViewGroup that the inflated layout will be added to and helps determine
     * the measurement and positioning of the inflated view.
     *
     * @returns a newly inflated view from the "sensable_list_row" layout.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.sensable_list_row, parent, false);
        return view;
    }

    /**
     * Retrieves data from a Cursor and binds it to various views within a row of a list.
     * It populates text views with sensor name, ID, type, value, and unit, sets an image
     * based on the sensor type, and changes the background color based on the sensor ID
     * and row ID.
     *
     * @param view View that is being populated with data from the Cursor, allowing the
     * function to interact with and modify its properties.
     *
     * FindViewById: Returns a View object with the given id or null if no such view
     * exists. The findViewById() method searches for a child view that has been assigned
     * this ID in its layout description.
     *
     * @param context context of the activity or fragment, used to access resources and
     * perform operations on the UI components within the view.
     *
     * Context contains the application's resources and local class loader, as well as
     * its base directory.
     *
     * @param cursor data source from which to retrieve data, providing access to the
     * columns of the underlying database table through its getter methods.
     *
     * Contains columns named NAME, SENSOR_ID, TYPE, VALUE and UNIT.
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
     * Generates a random color based on the input string's hash code. It creates an array
     * of predefined colors and selects one randomly using the hash code to generate a
     * random index within the array. The selected color is then returned as an integer
     * value representing an RGB color.
     *
     * @param name seed value used to generate a random instance of the Random class,
     * which determines the selection of a color from the predefined array.
     *
     * @returns a random color from an array of predefined colours.
     *
     * The output is an integer value representing a color in ARGB (Alpha, Red, Green,
     * Blue) format. The value is a combination of alpha channel transparency and RGB
     * colors, with each component ranging from 0 to 255.
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
     * Represents a data holder for sensor data adapter attributes.
     *
     * - ID (String): represents a unique identifier for an object or data point.
     *
     * - NAME (String): represents a name associated with an ID.
     *
     * - SENSOR_ID (String): represents a unique identifier for each sensor.
     *
     * - TYPE (String): represents a string value indicating the type of sensable being
     * displayed.
     *
     * - VALUE (String): in the AdapterHolder class represents a string value with no
     * additional context or information beyond what is explicitly defined.
     *
     * - UNIT (String): represents a textual representation of the unit of measurement
     * associated with a sample value.
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
