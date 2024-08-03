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
 * is a Java class that represents a sample data point with timestamp, value, and
 * location information. The class implements the Parcelable interface for serialization
 * and deserialization purposes. It also provides methods for getting and setting the
 * timestamp, value, and location fields.
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
     * returns the value of a `timestamp` field.
     * 
     * @returns a long value representing the current timestamp.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * sets the value of a class instance field named `timestamp`.
     * 
     * @param timestamp 64-bit value of the timestamp that is used to update the value
     * of the `timestamp` field of the current object.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * returns the value of a variable named `value`.
     * 
     * @returns a double value representing the variable `value`.
     */
    public double getValue() {
        return value;
    }

    /**
     * sets the value field of its instance to the given double value.
     * 
     * @param value double value that will be assigned to the `value` field of the
     * function's caller.
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * returns an integer value of 0, indicating that no contents are present or needed.
     * 
     * @returns an integer value of 0.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * writes the `timestamp` and `value` parameters to a Parcel object, using the specified
     * flags for formatting.
     * 
     * @param dest parcel that will be written to.
     * 
     * 	- `dest`: The Parcel object that represents the destination buffer for writing
     * data. It has a `writeLong()` and `writeDouble()` method for writing timestamp and
     * value respectively in this case.
     * 
     * @param flags 32-bit integer value that specifies the type of data being written
     * to the Parcel, with possible values ranging from 0 to 7, and it is used to determine
     * the format of the data being written.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timestamp);
        dest.writeDouble(value);
    }

    public static final Parcelable.Creator<Sample> CREATOR
            = new Parcelable.Creator<Sample>() {
        /**
         * creates a new instance of the `Sample` class from a provided Parcel object.
         * 
         * @param in `Parcel` object that contains data to be used in creating a new instance
         * of the `Sample` class.
         * 
         * The `Parcel in` received is a container for a `Sample` object, which contains
         * details about a sample such as its ID, name, and dimensions. The `in` parameter
         * can be decoded or deserialized to create an instance of the `Sample` class.
         * 
         * @returns a new instance of the `Sample` class initialized with the values from the
         * provided Parcel.
         * 
         * 	- The input Parcel is used to create a new instance of the `Sample` class.
         * 	- The `Sample` class represents a sample object with unspecified attributes and
         * methods.
         * 	- The `new Sample(in)` syntax creates an instance of the `Sample` class using the
         * values from the input Parcel.
         */
        public Sample createFromParcel(Parcel in) {
            return new Sample(in);
        }

        /**
         * returns an array of `Sample` objects with the specified size.
         * 
         * @param size amount of space to be allocated for an array of `Sample` objects, which
         * is then returned as a new array.
         * 
         * @returns an array of `Sample` objects with the specified size.
         * 
         * 	- The `Sample` array returned by the function has a fixed size specified by the
         * `size` parameter passed to the function.
         * 	- The array is initialized with a default value for each element, which is `null`
         * in this case.
         * 	- The array is immutable once it is created and cannot be modified after initialization.
         * 	- The function does not perform any error handling or validation of the input
         * parameters, so it is possible for the function to create an invalid or incomplete
         * array if the input parameters are incorrect.
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
     * returns a string representation of an object by combining its `timestamp` and `value`.
     * 
     * @returns a string representation of the object, consisting of the current timestamp
     * and the value of the object.
     */
    @Override
    public String toString() {
        return this.getTimestamp() + ": " + this.getValue();
    }

    /**
     * converts an object of type `MyObject` into a JSON object, including the timestamp
     * and value properties.
     * 
     * @returns a JSONObject containing the current timestamp and value of the given object.
     * 
     * 	- `json`: A JSONObject representing the timestamp and value of the object in a
     * JSON format.
     * 	- `timestamp`: The timestamp value represented as a string.
     * 	- `value`: The value of the object represented as a string.
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
     * returns an array of doubles representing the location of an object.
     * 
     * @returns a double array containing the location of an object.
     */
    public double[] getLocation() {
        return location;
    }

    /**
     * sets the location of an object to a provided double array.
     * 
     * @param location 3D coordinates of an object or entity in a program, and by assigning
     * it to the `this.location` field, the function sets the location of the object or
     * entity.
     * 
     * 	- This parameter is of type double[], indicating an array of double values.
     * 	- The function assigns the input `location` to the field `location` of this class.
     */
    public void setLocation(double[] location) {
        this.location = location;
    }
}