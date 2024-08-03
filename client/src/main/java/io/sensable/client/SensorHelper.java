package io.sensable.client;

import android.hardware.Sensor;

/**
 * Created by madine on 16/07/14.
 */
/**
 * provides a way to determine the type of sensor based on its name, and return an
 * appropriate image resource for that sensor. The class includes methods to determine
 * the image for different types of sensors, such as accelerometer, magnetic field,
 * orientation, light, pressure, proximity, gravity, linear acceleration, rotation,
 * humidity, temperature, and CO2.
 */
public class SensorHelper {

    /**
     * takes an integer input parameter representing a sensor type, and returns a string
     * indicating the appropriate unit for that sensor type.
     * 
     * @param sensorType 16-bit value of the sensor type, which determines the unit of
     * measurement for the accelerometer, magnetic field, gyroscope, light, pressure,
     * proximity, gravity, linear acceleration, rotation vector, orientation, relative
     * humidity, ambient temperature, or uncalibrated magnetic field, gyroscope, or game
     * rotation vector output.
     * 
     * @returns a string representing the unit of measurement for a given sensor type.
     */
    public static String determineUnit(int sensorType) {

        String unit = "";
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                unit = "m/s^2";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                unit = "uT";
                break;
            case Sensor.TYPE_GYROSCOPE:
                unit = "rad/s";
                break;
            case Sensor.TYPE_LIGHT:
                unit = "lux";
                break;
            case Sensor.TYPE_PRESSURE:
                unit = "hPa";
                break;
            case Sensor.TYPE_PROXIMITY:
                unit = "cm";
                break;
            case Sensor.TYPE_GRAVITY:
                unit = "m/s^2";
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                unit = "m/s^2";
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                unit = "<θx, θy, θz>";
                break;
            case Sensor.TYPE_ORIENTATION:
                unit = "<degx, degy, degz>";
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                unit = "%";
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                unit = "°C";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                unit = "uT";
                break;
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                unit = "<θx, θy, θz>";
                break;
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                unit = "rad/s";
                break;
        }

        return unit;
    }

    /**
     * determines an image resource based on a sensor type, using a switch statement to
     * map the sensor type to a corresponding image resource ID.
     * 
     * @param sensorType 16-bit integer value returned by the Android sensor API, which
     * determines the type of sensor data to be fetched and displayed in the `determineImage()`
     * function.
     * 
     * @returns an integer representing the drawable resource ID for the corresponding
     * sensor type.
     */
    public static int determineImage(int sensorType) {

        int image = R.drawable.type_lux;
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                image = R.drawable.type_accelerometer;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                image = R.drawable.type_magnetic;
                break;
            case Sensor.TYPE_GYROSCOPE:
                image = R.drawable.type_gyroscope;
                break;
            case Sensor.TYPE_LIGHT:
                image = R.drawable.type_lux;
                break;
            case Sensor.TYPE_PRESSURE:
                image = R.drawable.type_pressure;
                break;
            case Sensor.TYPE_PROXIMITY:
                image = R.drawable.type_lux;
                break;
            case Sensor.TYPE_GRAVITY:
                image = R.drawable.type_gravity;
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                image = R.drawable.type_accelerometer;
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                image = R.drawable.type_gyroscope;
                break;
            case Sensor.TYPE_ORIENTATION:
                image = R.drawable.type_gyroscope;
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                image = R.drawable.type_humidity;
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                image = R.drawable.type_temperature;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                image = R.drawable.type_magnetic;
                break;
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                image = R.drawable.type_gyroscope;
                break;
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                image = R.drawable.type_gyroscope;
                break;
        }

        return image;
    }

    /**
     * determines the appropriate drawable resource for a given sensor type based on a
     * case-insensitive match of the sensor name.
     * 
     * @param sensorType type of sensor, and based on its value, the function returns a
     * different drawable resource ID for the image to be displayed.
     * 
     * @returns an integer representing the drawable resource ID for the corresponding
     * sensor type.
     */
    public static int determineImage(String sensorType) {

        int image = R.drawable.type_lux;
        sensorType = sensorType.toLowerCase();
        if (sensorType.equals("accelerometer") || sensorType.contains("accelerometer")) {
            image = R.drawable.type_accelerometer;

        } else if (sensorType.equals("magnetic") || sensorType.contains("magnetometer")) {
            image = R.drawable.type_magnetic;

        } else if (sensorType.equals("orientation")) {
            image = R.drawable.type_gyroscope;

        } else if (sensorType.equals("light") || sensorType.contains("light")) {
            image = R.drawable.type_lux;

        } else if (sensorType.equals("pressure") || sensorType.contains("barometer")) {
            image = R.drawable.type_pressure;

        } else if (sensorType.equals("proximity")) {
            image = R.drawable.type_lux;

        } else if (sensorType.equals("gravity")) {
            image = R.drawable.type_gravity;

        } else if (sensorType.equals("linear_acceleration")) {
            image = R.drawable.type_accelerometer;

        } else if (sensorType.equals("rotation") || sensorType.contains("gyroscope")) {
            image = R.drawable.type_gyroscope;

        } else if (sensorType.equals("humidity")) {
            image = R.drawable.type_humidity;

        } else if (sensorType.equals("noise")) {
            image = R.drawable.type_noise;

        } else if (sensorType.equals("temperature")) {
            image = R.drawable.type_temperature;

        } else if (sensorType.equals("co2")) {
            image = R.drawable.type_molecule;

        }

        return image;
    }

}
