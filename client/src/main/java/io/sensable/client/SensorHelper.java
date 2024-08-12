package io.sensable.client;

import android.hardware.Sensor;

/**
 * Created by madine on 16/07/14.
 */
/**
 * Provides utility functions to determine the unit of measurement for various sensors
 * and retrieve corresponding image resources based on sensor types. It contains three
 * main functions: `determineUnit` which returns the unit of measurement,
 * `determineImage(int)` which retrieves an integer representing a drawable resource
 * ID for a given sensor type, and `determineImage(String)` which does the same but
 * takes a string parameter.
 */
public class SensorHelper {

    /**
     * Takes an integer `sensorType` as input and returns a corresponding unit string for
     * the sensor type. The function uses a switch statement to map the sensor type to
     * its respective unit, covering various types of sensors such as accelerometer,
     * magnetometer, gyroscope, and others.
     *
     * @param sensorType type of sensor being used, which determines the corresponding
     * unit to be returned by the function.
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
     * Determines which image resource to display based on the type of sensor specified
     * by the input parameter `sensorType`. It uses a switch statement to map each sensor
     * type to a corresponding image resource and returns the selected image.
     *
     * @param sensorType type of sensor and determines which corresponding image resource
     * to be returned based on its value.
     *
     * @returns an integer value representing a specific sensor type.
     *
     * The output is an integer value representing a drawable image resource ID. The value
     * can be one of several predefined constants such as R.drawable.type_lux,
     * R.drawable.type_accelerometer, and so on.
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
     * Determines an image based on a given sensor type. It converts the sensor type to
     * lowercase and checks for various conditions, assigning a corresponding image
     * resource ID. The function returns the determined image ID as an integer.
     *
     * @param sensorType type of sensor that is being used, and its value determines which
     * image to return based on a series of conditional checks.
     *
     * @returns an integer value representing a drawable resource ID.
     *
     * The output is an integer representing a drawable image resource ID from R.drawable.
     * The value of this integer determines which image is displayed based on the input
     * sensorType parameter.
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
