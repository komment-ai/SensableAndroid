package io.sensable.model;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by madine on 01/07/14.
 */
/**
 * Is a data model for managing sensor readings and samples. It encapsulates various
 * attributes such as ID, sensor ID, name, internal sensor ID, type, unit, pending
 * status, sample data, access token, and location. The class provides getter and
 * setter methods to manipulate these attributes.
 */
public class ScheduledSensable {
    private int id;                 // Internal DB ID
    private String sensorid;        // Sensables ID
    private String name;            // Sensables name
    private int internalSensorId;   // Reference to sensor hardware
    private String sensortype;      // Type of Sensor
    private String unit;            // Unit of Sensor
    private int pending;            // Are we waiting for a sample to be taken?
    private Sample sample;          // Latest Sample
    private boolean privateSensor;
    private String accessToken;

    // Remove when location is part of sample
    private double[] location;

    public ScheduledSensable() {
    }

    /**
     * Returns a value. It retrieves and provides access to the private variable `sensorid`,
     * which is likely a unique identifier for a sensor, allowing other parts of the
     * program to utilize its information. This function serves as a simple getter method
     * for the sensor ID.
     *
     * @returns a string value representing the sensor ID.
     */
    public String getSensorid() {
        return sensorid;
    }

    /**
     * Sets a string value to an instance variable named `sensorid`. It takes one parameter,
     * `sensorid`, which is used to update the object's state. This variable can be
     * accessed and modified through this method.
     *
     * @param sensorid value to be assigned to the instance variable `this.sensorid`.
     */
    public void setSensorid(String sensorid) {
        this.sensorid = sensorid;
    }

    /**
     * Retrieves a string value named `name`. The returned value is a reference to an
     * existing instance variable, indicating that it does not create or modify any data.
     * This method provides read-only access to the stored name.
     *
     * @returns a string representation of the object's name attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * Assigns a given string value to the instance variable `name`. It sets the attribute
     * `name` with the provided input `String name`, allowing external modification of
     * the object's internal state.
     *
     * @param name new value to be assigned to the instance variable `name`.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns an integer value representing the internal sensor ID. It does not accept
     * any parameters and simply retrieves the value stored in the `internalSensorId`
     * variable. This function allows external access to the internal sensor ID for further
     * processing or usage.
     *
     * @returns an integer value representing the internal sensor ID.
     */
    public int getInternalSensorId() {
        return internalSensorId;
    }

    /**
     * Sets an internal sensor ID to a specified value. It assigns an integer value to
     * the instance variable `internalSensorId`. The assigned value is used to identify
     * the internal sensor within the program.
     *
     * @param sensorId internal sensor identifier, which is assigned to the instance
     * variable `internalSensorId`.
     */
    public void setInternalSensorId(int sensorId) {
        this.internalSensorId = sensorId;
    }

    /**
     * Retrieves and returns a string value representing the sensor type. This method
     * does not modify any data; it simply provides access to an existing property,
     * allowing other parts of the program to use its value as needed.
     *
     * @returns a string representing the sensor type.
     */
    public String getSensortype() {
        return sensortype;
    }

    /**
     * Assigns a specified string value to an instance variable named `sensortype`. This
     * variable is likely used to store and manage sensor type information within an
     * object. The assigned value can be retrieved later for further processing or display.
     *
     * @param sensortype type of sensor to be set, which is stored as an instance variable
     * within the object.
     */
    public void setSensortype(String sensortype) {
        this.sensortype = sensortype;
    }

    /**
     * Retrieves a string value representing a unit and returns it. It appears to be a
     * getter method, accessing an instance variable named `unit`. The returned value is
     * used elsewhere in the program likely for display or further processing.
     *
     * @returns a `String` value representing the `unit`.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Assigns a specified string value to the instance variable `unit`. This method
     * updates the internal state of an object, allowing it to store and retain a unit
     * string representation. The updated value is stored in the object's memory for
     * future reference.
     *
     * @param unit value to be assigned to the `this.unit` field, updating its current
     * state with the new string value provided.
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Returns an integer value representing a variable named `pending`. This function
     * does not perform any operation, it simply retrieves and returns the current state
     * of the `pending` variable. The returned value is accessible to the caller.
     *
     * @returns an integer representing the value of the `pending` variable.
     */
    public int getPending() {
        return pending;
    }

    /**
     * Assigns a value to the instance variable `pending`. It updates the state of an
     * object by setting its `pending` attribute to the provided integer value. The
     * function does not return any value, as it is declared as `void`.
     *
     * @param pending value to be assigned to the instance variable `this.pending`.
     */
    public void setPending(int pending) {
        this.pending = pending;
    }

    /**
     * Returns a reference to an instance variable `sample` of type `Sample`. This function
     * is used to access or retrieve the value stored in `sample`. It provides read-only
     * access to the sample object.
     *
     * @returns a reference to an object of type `Sample`.
     */
    public Sample getSample() {
        return sample;
    }

    /**
     * Assigns a new `Sample` object to the `sample` field of the class, replacing any
     * previously assigned value. This function takes an instance of the `Sample` class
     * as input and updates the internal state of the class accordingly.
     *
     * @param sample Sample object to be assigned to the instance variable `this.sample`.
     */
    public void setSample(Sample sample) {
        this.sample = sample;
    }

    /**
     * Determines whether a sensor is private or not. It returns a boolean value indicating
     * the status of the sensor, which is stored in the `privateSensor` variable. This
     * function simply retrieves and returns the current state of the sensor without
     * performing any modifications or computations.
     *
     * @returns a boolean value indicating whether the `privateSensor` variable is true
     * or false.
     */
    public boolean isPrivateSensor() {
        return privateSensor;
    }

    /**
     * Updates the value of a boolean variable `privateSensor`. It takes a single argument
     * of type `boolean`, which represents the new state of the sensor, and assigns it
     * to the corresponding instance variable. This allows the object's internal state
     * to be modified.
     *
     * @param privateSensor value to be assigned to the instance variable `this.privateSensor`,
     * effectively setting it to either true or false based on the provided boolean value.
     */
    public void setPrivateSensor(boolean privateSensor) {
        this.privateSensor = privateSensor;
    }

    /**
     * Retrieves and returns an access token as a string value.
     *
     * @returns a string representing an access token.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Assigns a specified value to an instance variable named `accessToken`. The input
     * parameter is a string representing the access token. This variable stores the
     * provided access token for later use.
     *
     * @param accessToken value to be assigned to the instance variable `this.accessToken`.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Retrieves a location from an internal `Sample` object. If the `sample` object is
     * null, it creates a new one first. The function returns an array of double values
     * representing the location.
     *
     * @returns an array of doubles representing a location.
     */
    public double[] getLocation() {
        if (this.sample == null) {
            this.sample = new Sample();
        }
        return this.sample.getLocation();
    }

    /**
     * Sets a double array location for an existing or newly created instance of the
     * class's internal `Sample` object if it is null, and then assigns the location to
     * the Sample object.
     *
     * @param location 2D coordinates to be set as the location of an instance of the
     * `Sample` class, which is initialized if it is null.
     */
    public void setLocation(double[] location) {
        if (this.sample == null) {
            this.sample = new Sample();
        }
        this.sample.setLocation(location);

    }

    /**
     * Retrieves and returns the value of a private variable `id`. This variable likely
     * represents an identifier or unique key for an object, and this method provides
     * access to its current state. The returned value is an integer representing the id.
     *
     * @returns an integer value representing the identifier of a specific object.
     */
    public int getId() {
        return id;
    }

    /**
     * Assigns a given integer value to an instance variable named `id`. This instance
     * variable is presumably declared within the same class and has been initialized
     * previously. The function takes an `int` parameter `id` that represents the new
     * value for the `id` variable.
     *
     * @param id identifier to be set for an object, which is stored in the instance
     * variable `this.id`.
     */
    public void setId(int id) {
        this.id = id;
    }

//    public double[] getLocation() {
//        return location;
//    }

//    public void setLocation(double[] location) {
//        this.location = location;
//    }

    /**
     * Returns a JSON representation of a sample object. If the internal sample is not
     * null, it converts and logs the internal sample; otherwise, it creates a new sample,
     * converts and logs it, then returns the result.
     *
     * @returns a JSON string representation of either `this.sample` or a new `Sample` object.
     */
    public String getSampleAsJsonString() {
        if(this.sample != null) {
            Log.d("ScheduledSensable", this.sample.toJson().toString());
            return this.sample.toJson().toString();
        } else {
            Log.d("ScheduledSensable", new Sample().toJson().toString());
            return new Sample().toJson().toString();
        }
    }

}