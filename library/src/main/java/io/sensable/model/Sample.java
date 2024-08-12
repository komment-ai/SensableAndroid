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
 * Represents a sample data point with timestamp, value, and location information.
 * It implements Parcelable interface for serialization and deserialization purposes.
 * The class provides getter and setter methods for its fields, as well as methods
 * to convert the object into JSON format and represent it as a string.
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
     * Returns a value of type `long`. The returned value represents a timestamp, which
     * is stored in the variable `timestamp`.
     *
     * @returns a `long` value representing a timestamp.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Assigns a long integer value to the `timestamp` field of the object, allowing
     * external modification of its internal state. This change affects the object's
     * properties and potentially influences subsequent operations or calculations. The
     * timestamp can be retrieved and used for various purposes within the program.
     *
     * @param timestamp 64-bit long value to be assigned as the current timestamp for the
     * object.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Retrieves and returns a stored value, which is represented by the variable `value`.
     * This value can be accessed through an instance of the class containing this method.
     * The returned value is of type double.
     *
     * @returns a double-precision floating-point number representing the current value.
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets a new value for an object's internal state variable. It takes a `double`
     * parameter representing the desired value and assigns it to the object's `value`
     * field. This updates the object's internal state with the specified value.
     *
     * @param value new value to be assigned to the instance variable with the same name,
     * updating its current state.
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Returns an integer indicating the type and complexity of the object's contents.
     * The value typically represents a bitwise combination of content types, such as
     * primitive values or complex objects. In this case, the function always returns 0,
     * suggesting that the object contains no complex data structures.
     *
     * @returns an integer value of 0.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes the timestamp and value to a Parcel object. It uses the `dest.writeLong`
     * and `dest.writeDouble` methods to serialize these values into the parcel. The
     * parcel can then be used to transmit or store these data values.
     *
     * @param dest Parcel to which data is written.
     *
     * @param flags bit mask of options for the write operation, which can be used to
     * specify additional behavior such as whether the parcel should be compressed or encrypted.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timestamp);
        dest.writeDouble(value);
    }

    public static final Parcelable.Creator<Sample> CREATOR
            = new Parcelable.Creator<Sample>() {
        /**
         * Instantiates a new instance of the `Sample` class using the provided `Parcel`
         * object as an argument, effectively creating a sample from parcelled data.
         *
         * @param in Parcel object from which to read and deserialize data, allowing creation
         * of a new instance of the Sample class.
         *
         * @returns a newly created instance of the `Sample` class.
         */
        public Sample createFromParcel(Parcel in) {
            return new Sample(in);
        }

        /**
         * Creates an array of a specified size and returns it as an instance of the `Sample`
         * class. The function takes an integer parameter, `size`, which determines the number
         * of elements in the created array.
         *
         * @param size umber of elements to be included in the newly created array.
         *
         * @returns an array of type `Sample`, with a specified size.
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
     * Concatenates a timestamp and value strings, returning the resulting string. The
     * timestamp is retrieved from the `getTimestamp` method, while the value is obtained
     * through the `getValue` method. This allows for a human-readable representation of
     * the object.
     *
     * @returns a string combining timestamp and value.
     */
    @Override
    public String toString() {
        return this.getTimestamp() + ": " + this.getValue();
    }

    /**
     * Converts an object into a JSON format string, which includes timestamp and value
     * fields. It uses a JSONObject to create the JSON string from the object's timestamp
     * and value properties. The function returns the resulting JSON object.
     *
     * @returns a JSON object with timestamp and value attributes.
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
     * Returns an array of doubles representing a location. The function does not perform
     * any calculations or operations, it simply retrieves and returns a pre-existing
     * value. The returned value is stored in a variable named `location`.
     *
     * @returns an array of doubles representing a location's coordinates.
     */
    public double[] getLocation() {
        return location;
    }

    /**
     * Assigns a new double array representing a location to an instance variable `location`.
     * This allows for changing the current location of an object at runtime. The assigned
     * value replaces any previously set location.
     *
     * @param location 2D array of double values that sets the new location for the object.
     */
    public void setLocation(double[] location) {
        this.location = location;
    }
}