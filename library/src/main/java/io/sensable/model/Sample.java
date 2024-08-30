package io.sensable.model;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by madine on 01/07/14.
 */
/**
 * Represents a data point with timestamp, value, and location information for
 * serialization and deserialization purposes. It implements the Parcelable interface
 * to facilitate data transfer between applications. The class also provides methods
 * for converting instances to JSON objects and string representations.
 */
public class Sample implements Parcelable {
    private long timestamp;
    private double value;
    private double[] location;

    public Sample() {
    }

    public Sample(JSONObject json) {
        try {
            this.timestamp = json.getLong("timestamp");
            this.value = json.getDouble("value");

            JSONArray jsonArray = json.getJSONArray("location");
            this.location = new double[]{jsonArray.getDouble(0), jsonArray.getDouble(1)};
        } catch (JSONException e) {
//            e.printStackTrace();
        }
    }

    /**
     * Returns a value representing time. The returned value is accessed through a variable
     * named `timestamp`. It is possible to obtain the current system time using this method.
     *
     * @returns a non-negative integer value representing seconds since epoch.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Assigns a specified value to the object's `timestamp` field. This method takes a
     * single long integer parameter representing the new timestamp and stores it within
     * the object. The timestamp is then available for retrieval or further processing
     * as required.
     *
     * @param timestamp 64-bit long value that is being assigned to the class's internal
     * `timestamp` variable, updating its current value.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns a double-precision floating-point number representing the current state
     * of the object's value attribute. This method provides read-only access to the
     * stored value. It does not modify the state of the object.
     *
     * @returns a double representing an internal state variable known as `value`.
     */
    public double getValue() {
        return value;
    }

    /**
     * Accepts a double data type as an argument and assigns it to the instance variable
     * 'value' within the class. This allows the property to be set or modified with any
     * valid numeric value. The assigned value is then stored internally for further use.
     *
     * @param value new double value to be assigned to the object's property with the
     * same name.
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Returns an integer value indicating which types of data are contained within the
     * object being serialized or de-serialized by a Parcelable implementation. The method
     * is called internally during serialization and deserialization processes. A return
     * value of 0 indicates that there are no file descriptors, file paths, or other
     * file-related resources.
     *
     * @returns an integer indicating the object's serializable state, which is always 0.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Serializes object data into a Parcel for storage or transmission. It writes two
     * values to the parcel: a long integer timestamp and a double value, both retrieved
     * from the object's internal state. The data is written in a format that can be
     * reconstructed later.
     *
     * @param dest parcel object where the data is being written to.
     *
     * @param flags bitmask of parceling options and is used to specify additional
     * instructions for parceling objects, such as whether to use its own class loader
     * or not.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timestamp);
        dest.writeDouble(value);
    }

    public static final Parcelable.Creator<Sample> CREATOR
            = new Parcelable.Creator<Sample>() {
        /**
         * Recreates an instance of `Sample` from a parcel, which is a data container used
         * for inter-process communication. It does so by creating a new `Sample` object and
         * passing the parcel to its constructor. The resulting object contains the deserialized
         * data from the parcel.
         *
         * @param in Parcel object from which the Sample object is being reconstructed or
         * created from serialized data.
         *
         * @returns a new instance of the `Sample` class.
         */
        public Sample createFromParcel(Parcel in) {
            return new Sample(in);
        }

        /**
         * Creates a new array of objects, each instance being an instance of the `Sample`
         * class, with the specified `size`. The returned array is initialized but not populated
         * with any data. It has no bounds checking, which means it can throw an exception
         * if passed an invalid size.
         *
         * @param size umber of elements to be allocated in the newly created array of type
         * `Sample`.
         *
         * @returns an array of `Sample` objects with a specified length.
         */
        public Sample[] newArray(int size) {
            return new Sample[size];
        }
    };

    private Sample(Parcel in) {
        timestamp = in.readLong();
        value = in.readDouble();
    }

    /**
     * Returns a string representation of an object, concatenating its timestamp and
     * value. The returned string is in the format "timestamp: value". It appears to be
     * used for debugging or logging purposes.
     *
     * @returns a string combining timestamp and value.
     */
    @Override
    public String toString() {
        return this.getTimestamp() + ": " + this.getValue();
    }

    /**
     * Creates a JSONObject representing data, including timestamp and value. The JSONObject
     * is populated with values obtained from the object's `getTimestamp` and `getValue`
     * methods. If an error occurs during creation, it is silently discarded and the
     * function returns null.
     *
     * @returns a JSONObject containing timestamp and value properties.
     */
    public JSONObject toJson() {
        JSONObject json = null;
        try {
            json = new JSONObject("{\"timestamp\": " + this.getTimestamp() + ", \"value\": " + this.getValue() + "}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Returns an array of doubles representing a geographical location. The returned
     * value is retrieved from a previously initialized variable named `location`. This
     * allows other parts of the program to access and utilize the stored location data.
     *
     * @returns a double array representing the current location.
     */
    public double[] getLocation() {
        return location;
    }

    /**
     * Assigns a new location to the object's internal state. It takes an array of double
     * values representing coordinates as input and updates the corresponding instance
     * variable. This allows the object's location to be changed dynamically.
     *
     * @param location 1D array of doubles that is assigned to the instance variable `location`.
     */
    public void setLocation(double[] location) {
        this.location = location;
    }
}