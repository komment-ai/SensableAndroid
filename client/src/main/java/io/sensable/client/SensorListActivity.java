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
 * Extends ListActivity to display a list of available sensors on a device. It retrieves
 * a list of all sensors using the SensorManager and then creates an ArrayAdapter to
 * display their names in a ListView.
 */
public class SensorListActivity extends ListActivity {
    /**
     * Called when the activity is first created.
     */
    /**
     * Initializes a SensorManager and retrieves a list of all available sensors. It then
     * creates an ArrayList to store sensor names, adds these names to the list, and sets
     * a list adapter for a ListView. The adapter enables text filtering on the ListView.
     *
     * @param savedInstanceState Bundle object that contains the activity's saved state
     * at the moment it is being restored by the system.
     *
     * Bundle object holding the activity's state. It contains key-value pairs representing
     * the application-specific data saved by the activity when its process is killed.
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
