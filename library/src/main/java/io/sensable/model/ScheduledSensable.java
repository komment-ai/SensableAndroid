package io.sensable.model;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by madine on 01/07/14.
 */
/**
 * Represents a data model for managing sensor readings and samples. It encapsulates
 * various properties such as ID, sensor type, unit, sample data, and access token,
 * allowing for CRUD operations on these values. The class also provides methods for
 * generating JSON representations of the sample data.
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
     * Returns a string value representing the sensor ID. It retrieves and returns the
     * stored `sensorid` variable. This function provides read-only access to the sensor
     * ID, allowing it to be used by other parts of the program for identification purposes.
     *
     * @returns a string representing the value of the `sensorid`.
     */
    public String getSensorid() {
        return sensorid;
    }

    /**
     * Assigns a specified string value to the `sensorid` attribute of an object. This
     * method is used to set or update the sensor ID, which can be used for identification
     * purposes. The new value replaces any existing value stored in the `sensorid` attribute.
     *
     * @param sensorid identifier of a sensor, which is assigned to an instance variable
     * `this.sensorid`.
     */
    public void setSensorid(String sensorid) {
        this.sensorid = sensorid;
    }

    /**
     * Returns a string representing the current value of the `name` variable. This
     * suggests that it is a getter method, allowing external access to the internal state
     * of an object. It does not modify any data, but simply retrieves and returns the
     * existing information.
     *
     * @returns a string representing the value of the variable `name`.
     */
    public String getName() {
        return name;
    }

    /**
     * Assigns a given string value to the `name` attribute of an object. It updates the
     * current value with the new one, replacing any previous content. This modification
     * affects the object's state, allowing its `name` property to be modified externally.
     *
     * @param name string value to be assigned to the `this.name` variable, updating its
     * current state.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves an integer value representing the internal sensor ID and returns it
     * directly to the caller without any modification or processing. The returned value
     * is stored in a variable called `internalSensorId`. This function provides read-only
     * access to the internal sensor ID.
     *
     * @returns an integer representing the internal sensor ID.
     */
    public int getInternalSensorId() {
        return internalSensorId;
    }

    /**
     * Assigns a specified integer value to an internal variable `internalSensorId`. This
     * variable is presumably a property of the class, allowing its state to be modified
     * externally. The assigned value becomes the new value of `internalSensorId`, which
     * can then be accessed or used within the class.
     *
     * @param sensorId integer value to be assigned to the internalSensorId field of the
     * class.
     */
    public void setInternalSensorId(int sensorId) {
        this.internalSensorId = sensorId;
    }

    /**
     * Returns a string value representing the sensor type. It simply retrieves and returns
     * the value of the `sensortype` variable without performing any operations or
     * calculations. The returned value can be used for further processing or display purposes.
     *
     * @returns a string value of the `sensortype` variable.
     */
    public String getSensortype() {
        return sensortype;
    }

    /**
     * Assigns a string value to the instance variable `sensortype`. This allows an
     * external entity to modify the internal state of the object by setting its sensor
     * type. The new value replaces any previous assignment to this attribute.
     *
     * @param sensortype sensor type to be assigned to an object's internal variable,
     * which is then stored and accessible through the object.
     */
    public void setSensortype(String sensortype) {
        this.sensortype = sensortype;
    }

    /**
     * Returns a string value representing the unit.
     *
     * @returns a string value of the `unit` variable.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Assigns a given string value to the `unit` field of its class. It takes a `String`
     * parameter, which represents the unit to be set. This function updates the internal
     * state of the object with the provided unit information.
     *
     * @param unit string value that is assigned to the instance variable `this.unit`.
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Retrieves and returns an integer value representing a pending state or status.
     * This value is likely stored in a variable named `pending`. The function provides
     * a read-only access to this value, allowing other parts of the program to obtain
     * its current state.
     *
     * @returns an integer value representing the pending status.
     */
    public int getPending() {
        return pending;
    }

    /**
     * Assigns a value to the `pending` field. The function accepts an integer parameter,
     * `pending`, and sets it as the new value for the field with the same name. This
     * allows the state of the object to be modified dynamically.
     *
     * @param pending integer value that is assigned to the instance variable `this.pending`.
     */
    public void setPending(int pending) {
        this.pending = pending;
    }

    /**
     * Returns an instance of the `Sample` class represented by the `sample` object. This
     * suggests a getter method, providing access to the encapsulated `sample` object
     * without modifying its state. The returned `Sample` object can then be used for
     * further processing or inspection.
     *
     * @returns an instance of the `Sample` class, referenced by `sample`.
     */
    public Sample getSample() {
        return sample;
    }

    /**
     * Assigns a new value to the `sample` field, replacing any existing value with the
     * provided `sample` object. This method does not perform any validation or processing
     * on the input data. It simply updates the internal state of the class with the new
     * sample object.
     *
     * @param sample object to be assigned to the `sample` field of the class, replacing
     * its previous value.
     */
    public void setSample(Sample sample) {
        this.sample = sample;
    }

    /**
     * Returns a boolean value indicating whether a sensor is private or not. It simply
     * retrieves the value of a variable `privateSensor`, which presumably holds information
     * about the sensor's privacy status. The returned value can be used by other parts
     * of the program to determine how to handle the sensor.
     *
     * @returns a boolean value indicating whether the sensor is private or not.
     */
    public boolean isPrivateSensor() {
        return privateSensor;
    }

    /**
     * Sets a boolean value for the instance variable `privateSensor`. This method takes
     * a single boolean parameter and updates the internal state of the object to reflect
     * the new value. The modified state is accessible through other parts of the program.
     *
     * @param privateSensor boolean value that is assigned to the `privateSensor` instance
     * variable, updating its state accordingly.
     */
    public void setPrivateSensor(boolean privateSensor) {
        this.privateSensor = privateSensor;
    }

    /**
     * Retrieves and returns the value of the `accessToken` variable. This method does
     * not perform any calculations or operations, instead, it simply provides access to
     * the stored token value. The returned token can be used for authentication purposes.
     *
     * @returns a string value of an access token.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Sets the value of an instance variable `accessToken`. It takes a `String` parameter
     * representing the access token and assigns it to the instance variable. This variable
     * can be accessed later by other parts of the program or class.
     *
     * @param accessToken value to be assigned to the instance variable `this.accessToken`,
     * effectively setting it with a new access token.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Returns an array of doubles representing a location when a sample object is not
     * already initialized, it creates a new sample object and retrieves its location.
     *
     * @returns an array of double values representing a location.
     */
    public double[] getLocation() {
        if (this.sample == null) {
            this.sample = new Sample();
        }
        return this.sample.getLocation();
    }

    /**
     * Initializes or updates the location of a sample object when provided with a double
     * array representing the coordinates. If the sample does not exist, it creates one
     * before setting its location. The updated sample is stored for future use.
     *
     * @param location 2D array of coordinates that is set as the location for the current
     * sample object within the class.
     */
    public void setLocation(double[] location) {
        if (this.sample == null) {
            this.sample = new Sample();
        }
        this.sample.setLocation(location);

    }

    /**
     * Returns an integer value representing a unique identifier. It retrieves and provides
     * access to the internal state variable `id`. This method allows external code to
     * obtain and use the ID for various purposes.
     *
     * @returns an integer representing a unique identifier.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of a private instance variable `id` with the provided integer
     * parameter, updating its current state to match the new input value. This variable
     * is likely used to uniquely identify an object or entity within the program. The
     * change is reflected within the class.
     *
     * @param id identifier to be assigned to the object, which is stored in the instance
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
     * Returns a JSON string representation of a sample object if it is not null, otherwise
     * it creates a new sample object and returns its JSON string representation. The
     * function logs the JSON string to the debug log with a respective tag.
     *
     * @returns a JSON representation of either `this.sample` or a default `Sample` object.
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