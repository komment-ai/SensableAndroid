package io.sensable.client;

import android.app.ListActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Disables the default list layout and replaces it with a list of available sensors
 * on the device.
 * It enables text filtering on the list view to allow users to search for specific
 * sensors.
 * The list of sensors is retrieved from the device's SensorManager.
 */
public class SensorListActivity extends ListActivity {
    /**
     * Called when the activity is first created.
     */
    /**
     * sets up a list adapter for displaying sensor types and enables text filtering on
     * the ListView.
     * 
     * @param savedInstanceState state of the activity that was previously saved, which
     * can be used to restore the state of the activity if it is restarted or recreated.
     * 
     * The `super.onCreate(savedInstanceState)` line is executed first to call the parent
     * class's `onCreate` method and perform any necessary initialization. The
     * `getSystemService()` method calls the `SensorManager` class to get an instance of
     * the sensor manager, which is then stored in the variable `sensorManager`.
     * 
     * The `getSensorList()` method of the `SensorManager` class returns a list of all
     * available sensors on the device. The list is stored in the variable `listSensor`.
     * 
     * The `List<String>` variable `listSensorType` is created to store the names of the
     * sensors in the list returned by `getSensorList()`.
     * 
     * Finally, a new `ArrayAdapter` object is created to display the sensor names in a
     * list view. The adapter takes three arguments: the context of the activity (`this`),
     * a resource ID for the layout to use for each item in the list
     * (`android.R.layout.simple_list_item_1`), and the list of sensor names stored in `listSensorType`.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> listSensor = sensorManager.getSensorList(Sensor.TYPE_ALL);

        List<String> listSensorType = new ArrayList<String>();
        for (int i = 0; i < listSensor.size(); i++) {
            listSensorType.add(listSensor.get(i).getName());
        }

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listSensorType));
        getListView().setTextFilterEnabled(true);
    }
}
