package io.sensable.model;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by madine on 01/07/14.
 */
/**
 * appears to be a data model for managing sensor readings and samples. It has various
 * fields such as ID, sensor ID, name, internal sensor ID, sensortype, unit, pending,
 * sample, private sensor, access token, and location (which is a double array). The
 * class also has methods for getting or setting values for these fields, as well as
 * a method for generating a JSON string representation of the sample data.
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
     * retrieves a predefined string value, `sensorid`.
     * 
     * @returns a string representing the sensor ID.
     */
    public String getSensorid() {
        return sensorid;
    }

    /**
     * sets a field named `sensorid` to a given string value, providing a new value for
     * the object's sensor identification.
     * 
     * @param sensorid identifier of a specific sensor that this method is related to.
     */
    public void setSensorid(String sensorid) {
        this.sensorid = sensorid;
    }

    /**
     * retrieves a string representing the caller's name.
     * 
     * @returns a string representing the name of an object.
     */
    public String getName() {
        return name;
    }

    /**
     * sets the value of the object's `name` field to the provided String argument.
     * 
     * @param name new value of the class's `name` field.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * returns the internal sensor ID of an object.
     * 
     * @returns an integer representing the internal sensor ID.
     */
    public int getInternalSensorId() {
        return internalSensorId;
    }

    /**
     * sets the value of the internal sensor ID field of a class instance to the argument
     * passed as an integer.
     * 
     * @param sensorId internal sensor ID that is being set for the object.
     */
    public void setInternalSensorId(int sensorId) {
        this.internalSensorId = sensorId;
    }

    /**
     * returns a string representing the sensortype of an object.
     * 
     * @returns a string representing the sensory type.
     */
    public String getSensortype() {
        return sensortype;
    }

    /**
     * sets a string value for the class member `sensortype`.
     * 
     * @param sensortype sensor type for which the method is setting the value of the
     * `sensortype` field.
     */
    public void setSensortype(String sensortype) {
        this.sensortype = sensortype;
    }

    /**
     * retrieves a `String` value representing the unit of measurement for the current context.
     * 
     * @returns a string representing the unit of measurement.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * sets a reference to a `String` variable `unit` within the function's scope,
     * effectively updating the value of the class-level variable `this.unit`.
     * 
     * @param unit value of the unit of measurement for the object being manipulated by
     * the `setUnit()` method.
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * retrieves the value of the `pending` field, which represents the number of tasks
     * that are pending to be executed.
     * 
     * @returns the value of the `pending` variable, which is an integer representing the
     * number of tasks that are pending to be executed.
     */
    public int getPending() {
        return pending;
    }

    /**
     * sets the value of the class's `pending` field to the provided integer argument.
     * 
     * @param pending integer value that will be stored in the `this.pending` field of
     * the class.
     */
    public void setPending(int pending) {
        this.pending = pending;
    }

    /**
     * returns a reference to an `sample` object.
     * 
     * @returns a reference to an `Sample` object named `sample`.
     * 
     * 	- The returned object is of type `sample`, indicating that it belongs to a class
     * named `sample`.
     * 	- The object contains an instance variable named `sample`, which holds some value.
     * 	- The exact value of the `sample` instance variable is not specified in the
     * function, as it depends on the implementation of the `sample` class.
     */
    public Sample getSample() {
        return sample;
    }

    /**
     * sets the `sample` field of an object to the provided `Sample` object.
     * 
     * @param sample Sample object that is assigned to the `this.sample` field within the
     * function.
     * 
     * 	- The field `this.sample` is assigned with the given `sample` object as a reference.
     * 	- `sample` is an instance of the class `Sample`, which has no explicit properties
     * or attributes mentioned in the code snippet provided.
     */
    public void setSample(Sample sample) {
        this.sample = sample;
    }

    /**
     * returns a boolean value indicating whether the sensor is private or not.
     * 
     * @returns a boolean value indicating whether the sensor is private or not.
     */
    public boolean isPrivateSensor() {
        return privateSensor;
    }

    /**
     * sets the value of a field named `privateSensor` to a provided boolean value,
     * updating the state of the object.
     * 
     * @param privateSensor boolean value that determines whether the sensor is private
     * or not, which is then stored as an attribute of the class instance represented by
     * the `this` reference.
     */
    public void setPrivateSensor(boolean privateSensor) {
        this.privateSensor = privateSensor;
    }

    /**
     * retrieves a pre-defined access token from storage and returns it as a string.
     * 
     * @returns a string representing the access token.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * sets a reference to an `accessToken` variable, which stores a string value
     * representing an authentication token for API calls.
     * 
     * @param accessToken token that grants access to the protected resources.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * retrieves a double array representing the location of an object from its associated
     * sample object, if available; otherwise, it returns a default location array.
     * 
     * @returns a double array containing the location of the sample.
     */
    public double[] getLocation() {
        if (this.sample == null) {
            this.sample = new Sample();
        }
        return this.sample.getLocation();
    }

    /**
     * sets the location of a `Sample` object associated with an instance of the `JavaClass`.
     * 
     * @param location 2D location of an object in a sample, which is then set to the
     * corresponding field in the internal `Sample` class.
     * 
     * 	- `this.sample`: A reference to an instance variable named `sample`.
     * 	- `location`: An array of double values representing the location data.
     */
    public void setLocation(double[] location) {
        if (this.sample == null) {
            this.sample = new Sample();
        }
        this.sample.setLocation(location);

    }

    /**
     * returns the `id` field of an object.
     * 
     * @returns an integer representing the value of the `id` field.
     */
    public int getId() {
        return id;
    }

    /**
     * sets the `id` field of an object to a specified value.
     * 
     * @param id integer value that will be assigned to the `id` field of the class
     * instance being updated by the `setId()` method.
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
     * converts a `Sample` object to a JSON string if it is not null, otherwise it creates
     * a new `Sample` object and returns its JSON representation.
     * 
     * @returns a JSON string representing a sample object.
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