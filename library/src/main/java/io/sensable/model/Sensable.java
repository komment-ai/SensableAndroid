package io.sensable.model;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by madine on 01/07/14.
 */
/**
 * Is a Parcelable object that represents a sensory data point with various metadata
 * such as location, sensor ID, name, type, and unit. It also contains sample values
 * and can serialize its state to a Parcel object for efficient storage or transmission.
 * The class provides methods for accessing and manipulating these properties.
 */
public class Sensable implements Parcelable {
    private double[] location;
    private String sensorid;
    private String name;
    private String sensortype;
    private Sample[] samples;
    private Sample sample;
    private String unit;
    private String accessToken;

    public Sensable() {
    }

    /**
     * Returns an array of doubles representing a location. The returned array contains
     * the coordinates of a specific point or object. This function does not perform any
     * calculations, it simply retrieves and exposes the pre-defined location data.
     *
     * @returns an array of double values representing a geographic location.
     */
    public double[] getLocation() {
        return location;
    }

    /**
     * Assigns a specified double array as the value for an instance variable `location`.
     * This variable is expected to hold a specific set of geographic coordinates. The
     * method sets or updates the current location with the provided values.
     *
     * @param location 2D array of double values that sets the location attribute for the
     * object.
     */
    public void setLocation(double[] location) {
        this.location = location;
    }

    /**
     * Retrieves and returns a string value representing a sensor ID. This value is stored
     * as an instance variable named `sensorid`. The function does not modify any data
     * or perform complex operations, simply returning the current state of the `sensorid`
     * variable.
     *
     * @returns a string representing the value of the `sensorid`.
     */
    public String getSensorid() {
        return sensorid;
    }

    /**
     * Assigns a specified `sensorId` to an instance variable. The function takes a
     * `String` parameter and updates the value of the `sensorid` attribute within the
     * class. This allows external code to modify the sensor ID associated with an object.
     *
     * @param sensorId identifier of a sensor and is used to set the value of the
     * corresponding instance variable `this.sensorid`.
     */
    public void setSensorid(String sensorId) {
        this.sensorid = sensorId;
    }

    /**
     * Returns a string value named `name`. This suggests that it retrieves or accesses
     * a predefined string variable, likely a property or attribute of an object or class.
     *
     * @returns a string value stored in the `name` variable.
     */
    public String getName() {
        return name;
    }

    /**
     * Assigns a specified string value to the instance variable `name`. This method sets
     * the property of an object with a new value, effectively updating its state. It
     * takes one parameter, a string that represents the new name.
     *
     * @param name value to be assigned to the instance variable `this.name`, allowing
     * it to be set or updated by the method.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a string value representing the sensor type. It retrieves and provides
     * access to an internal variable named `sensortype`. The retrieved value is passed
     * back as a string, allowing external code to utilize it for further processing or
     * display purposes.
     *
     * @returns a string value of type `sensortype`.
     */
    public String getSensortype() {
        return sensortype;
    }

    /**
     * Sets a string value to an instance variable named `sensortype`. This method allows
     * the assignment of a new sensor type to an object, which can be accessed and used
     * elsewhere in the program through the public property.
     *
     * @param sensortype value to be assigned to the instance variable `sensortype`.
     */
    public void setSensortype(String sensortype) {
        this.sensortype = sensortype;
    }

    /**
     * Retrieves a collection of `Sample` objects and returns it as an array. The retrieved
     * samples are stored in the `samples` variable, which is accessible within the scope
     * of this method.
     *
     * @returns an array of `Sample` objects named `samples`.
     */
    public Sample[] getSamples() {
        return samples;
    }

    /**
     * Sets a given array of `Sample` objects as the new sample data for an instance. If
     * the array is not empty, it sets the last sample from the array using another method
     * `setSample`. Otherwise, it sets the sample to null.
     *
     * @param samples array of Sample objects to be processed by the function, which then
     * sets the last sample or null depending on its length and updates the internal state
     * with the provided samples.
     */
    public void setSamples(Sample[] samples) {
        if(samples.length > 0) {
            setSample(samples[samples.length-1]);
        } else {
            setSample(null);
        }
        this.samples = samples;
    }

    /**
     * Retrieves a reference to an object of type `Sample`. It returns the current value
     * of the `sample` variable. This allows external code to access and utilize the
     * internal state of the class.
     *
     * @returns an instance of class `Sample`.
     */
    public Sample getSample() {
        return sample;
    }

    /**
     * Assigns a new value to the `sample` field. It takes an object of type `Sample` as
     * input and updates the internal state of the class by setting it equal to the
     * provided sample object. This allows the class to manage external samples internally.
     *
     * @param sample object to be assigned to the instance variable `this.sample`.
     */
    public void setSample(Sample sample) {

        this.sample = sample;
    }

    /**
     * Retrieves the value stored in the `unit` variable and returns it as a string. This
     * suggests that the function is designed to provide access to a unit or measurement
     * quantity for some purpose. The returned value can then be used by the calling code.
     *
     * @returns a string value stored in the `unit` variable.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Updates the internal state of an object by assigning a new value to the `unit`
     * property, which is likely a variable or field of the class. This allows external
     * code to modify the unit associated with the object programmatically. The update
     * is made directly on the object's instance variables.
     *
     * @param unit value to be assigned to the instance variable `this.unit`, updating
     * its state.
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Returns a string representing the access token. This accessor method allows external
     * classes to retrieve the current value of the `accessToken` variable.
     *
     * @returns a `String` value representing an access token.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Assigns a specified `accessToken` to the current instance's corresponding attribute,
     * effectively updating the stored access token value. This modification is made by
     * assigning the provided string value directly to the attribute with the same name.
     * The updated value becomes accessible for further use.
     *
     * @param accessToken token to be assigned to the object's `accessToken` field.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Returns a string representation of an object, combining its sensor ID with either
     * the value and unit of the first sample if available, or just the unit if no samples
     * are present. The result is formatted as "sensor_id - value_unit".
     *
     * @returns a string representation of sensor ID and its value or unit.
     */
    @Override
    public String toString() {
        if(this.getSamples().length > 0) {
            return this.getSensorid() + " - " + this.getSamples()[0].getValue() + this.getUnit();
        } else {
            return this.getSensorid() + " - " + this.getUnit();
        }
    }

    /**
     * Returns an integer value representing the type and size of the Parcelable object's
     * content. It is a method required by the Parcelable interface, used to specify the
     * number of elements that need to be written toParcel(). The returned value typically
     * indicates the type of data being transferred.
     *
     * @returns an integer value representing the type of data contained.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Serializes the data to a parcel for storage. It writes an array of doubles
     * representing location coordinates, four strings (sensor ID, name, sensor type, and
     * unit), and an array of Parcelable samples to the parcel using Parcel API methods.
     *
     * @param dest parcel that holds the data to be written by the function, allowing it
     * to store and send complex data objects efficiently.
     *
     * @param flags 32-bit integer that controls the behavior of various operations such
     * as data serialization and deserialization using Parcel.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDoubleArray(location);
        dest.writeString(sensorid);
        dest.writeString(name);
        dest.writeString(sensortype);
        dest.writeParcelableArray(samples, flags);
        dest.writeString(unit);

    }

    public static final Parcelable.Creator<Sensable> CREATOR = new Parcelable.Creator<Sensable>() {
        /**
         * Creates a new instance of the `Sensible` class from a parcel object. It uses the
         * provided parcel to initialize the newly created object. The function returns the
         * newly created object as an instance of the `Sensible` class.
         *
         * @param in Parcel object that contains the data to be deserialized into an instance
         * of the Sensable class.
         *
         * @returns a new instance of `Sensable`.
         */
        public Sensable createFromParcel(Parcel in) {
            return new Sensable(in);
        }

        /**
         * Creates a new array of type `Sensible` with the specified `size`. It returns a
         * reference to the newly created array, allowing it to be used in the calling code.
         * The array is initialized without any values.
         *
         * @param size umber of elements to be allocated for an array of type `Sensible`.
         *
         * @returns an array of type `Sensible` with a specified `size`.
         */
        public Sensable[] newArray(int size) {
            return new Sensable[size];
        }
    };

    private Sensable(Parcel in) {
        location = in.createDoubleArray();
        sensorid = in.readString();
        name = in.readString();
        sensortype = in.readString();

        Parcelable[] parcelableArray = in.readParcelableArray(Sample.class.getClassLoader());
        samples = null;
        if (parcelableArray != null) {
            samples = Arrays.copyOf(parcelableArray, parcelableArray.length, Sample[].class);
        }

        unit = in.readString();
    }


    /**
     * Returns a string representation of a sample object as JSON. If the sample is null,
     * it initializes a new instance before conversion to JSON.
     *
     * @returns a JSON string representation of the sample object.
     */
    public String getSampleAsJsonString() {
        if(sample == null) {
            this.sample = new Sample();
        }
        return this.sample.toJson().toString();
    }
}
