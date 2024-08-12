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
 * Is a representation of a sensory data point with metadata such as location, sensor
 * ID, name, type, and unit. It also contains sample values that can be retrieved or
 * set through methods. The class implements Parcelable for efficient serialization
 * and deserialization.
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
     * Returns a double array representing the location.
     *
     * @returns an array of doubles representing a geographical location.
     */
    public double[] getLocation() {
        return location;
    }

    /**
     * Sets a new value for an object's `location` attribute, which is expected to be an
     * array of double values representing coordinates. It updates the internal state of
     * the object with the provided location data. The updated location can then be
     * accessed or used by other parts of the program.
     *
     * @param location 2D array that is assigned to the instance variable `this.location`.
     */
    public void setLocation(double[] location) {
        this.location = location;
    }

    /**
     * Retrieves and returns a string value representing the sensor ID. This method simply
     * accesses the existing `sensorid` variable without performing any operations on it,
     * effectively returning its current state.
     *
     * @returns a string value representing the sensor ID.
     */
    public String getSensorid() {
        return sensorid;
    }

    /**
     * Updates the internal state of an object by assigning a given string value to the
     * `sensorid` attribute. This attribute represents a unique identifier for a sensor.
     * The update operation is performed directly on the object's internal storage.
     *
     * @param sensorId identifier of a sensor and assigns it to an instance variable with
     * the same name within the class.
     */
    public void setSensorid(String sensorId) {
        this.sensorid = sensorId;
    }

    /**
     * Retrieves a string value and returns it. The returned value is stored in the `name`
     * variable, which is likely an instance or class-level attribute. This function
     * allows external access to the name property without modifying its internal state.
     *
     * @returns a string value representing the object's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Assigns a new value to the instance variable `name`. It accepts a `String` parameter,
     * which is stored as the object's `name` property. This allows the object's name
     * attribute to be updated or changed.
     *
     * @param name string value to be assigned to the instance variable `this.name`,
     * updating its state.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves and returns a string value representing the sensor type. The returned
     * value is stored in the variable `sensortype`. This method simply provides access
     * to the pre-existing sensor type information without performing any computations
     * or modifications.
     *
     * @returns a string value representing the sensor type.
     */
    public String getSensortype() {
        return sensortype;
    }

    /**
     * Assigns a value to an instance variable `sensortype`. It takes a `String` parameter,
     * which is used to update the state of the object. The updated value can be accessed
     * later through the same property.
     *
     * @param sensortype value to be assigned to the instance variable `this.sensortype`.
     */
    public void setSensortype(String sensortype) {
        this.sensortype = sensortype;
    }

    /**
     * Returns an array of sample objects. The returned array is stored in a variable
     * named `samples`. This method allows access to the internal state of the object,
     * providing a way for external code to retrieve the samples.
     *
     * @returns an array of `Sample` objects named `samples`.
     */
    public Sample[] getSamples() {
        return samples;
    }

    /**
     * Sets an array of `Sample` objects to a private field `samples`. If the input array
     * is not empty, it calls another function `setSample` with the last element of the
     * array; otherwise, it sets `samples` to null.
     *
     * @param samples 2D array of `Sample` objects to be assigned to the current object's
     * internal `samples` field.
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
     * Returns a reference to an object of type `Sample`. The object's value is stored
     * in a variable named `sample`. This method provides access to the `sample` object
     * from outside the class where it is defined.
     *
     * @returns an instance of the `Sample` class named `sample`.
     */
    public Sample getSample() {
        return sample;
    }

    /**
     * Assigns a new value to the `sample` attribute of its object, replacing any previous
     * value it held. The `sample` parameter is an instance of the `Sample` class and
     * becomes the new state of the object's `sample` attribute.
     *
     * @param sample object to be assigned to the instance variable `this.sample`.
     */
    public void setSample(Sample sample) {

        this.sample = sample;
    }

    /**
     * Retrieves and returns a string value stored in the `unit` variable. It does not
     * modify any external state, instead, it provides access to the internal state by
     * returning its value. This allows other parts of the program to utilize the unit
     * information as needed.
     *
     * @returns a string value representing the unit property.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Assigns a new value to the `unit` field. It takes a string parameter representing
     * the unit and updates the internal state of the object with the provided value.
     * This allows external code to modify the unit associated with the object.
     *
     * @param unit value to be assigned to the instance variable `this.unit`.
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Retrieves and returns an access token. The access token is stored in the `accessToken`
     * variable, which is presumably set elsewhere in the code. This function provides a
     * getter method for accessing the access token from outside the class.
     *
     * @returns a string value representing an access token.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Sets a new value for the `accessToken` variable, updating its internal state. It
     * takes a `String` parameter representing the new access token to be assigned. This
     * updated token is stored within the class instance.
     *
     * @param accessToken value to be assigned to the instance variable `accessToken`.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Generates a string representation of an object. It combines the sensor ID, first
     * sample value (if available), and unit of measurement into a single string. If no
     * samples are present, it returns the sensor ID and unit only.
     *
     * @returns a string describing the sensor ID and its value or unit.
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
     * Specifies the type of serialization used for the object. It returns an integer
     * value indicating the type, in this case, 0, which indicates no special handling
     * is required during serialization. This method is typically overridden in classes
     * that implement Parcelable interface.
     *
     * @returns an integer value representing the serializable contents of the object.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes a parcelable object to a Parcel, containing location data as a double array,
     * sensor ID and name as strings, sensor type as a string, sample data as an array
     * of parcelables, and unit as a string.
     *
     * @param dest Parcel where the object's state is being written, allowing its attributes
     * to be serialized and stored for later use.
     *
     * @param flags 32-bit integer that controls how the parcel is marshaled and unmarshaled.
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
         * Recreates an instance of the `Sensible` class from a Parcel object passed as a
         * parameter. It instantiates a new object using the provided parcel and returns it.
         * The object is initialized with data retrieved from the parcel.
         *
         * @param in Parcel object from which data is to be read and used to construct a new
         * instance of the Sensable class.
         *
         * @returns an instance of the `Sensible` class.
         */
        public Sensable createFromParcel(Parcel in) {
            return new Sensable(in);
        }

        /**
         * Creates an array of type `Sensible` with a specified `size`. It initializes an
         * empty array of the specified length, ready for use to store objects of type
         * `Sensible`. The returned array is filled with default values.
         *
         * @param size umber of elements to be allocated for an array of type `Sensible`.
         *
         * @returns an array of `Sensible` objects with a specified `size`.
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
     * Returns a JSON representation of an object named "sample". If the sample object
     * is null, it creates a new one. The returned string is generated by calling the
     * `toJson` method on the sample object and then converting the result to a string
     * using `toString`.
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
