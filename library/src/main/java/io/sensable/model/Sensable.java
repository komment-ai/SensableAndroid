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
 * is a Java class that represents a sensory data point with various metadata such
 * as location, sensor ID, name, type, and unit. The class also contains a sample
 * value, which can be retrieved through the `getSample()` method or set through the
 * `setSample()` method. Additionally, the class has a `writeToParcel()` method for
 * serializing the data to a Parcel object.
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
     * returns an array of doubles representing the location of an object.
     * 
     * @returns a double-valued array containing the location of the object.
     */
    public double[] getLocation() {
        return location;
    }

    /**
     * sets the location of an object, represented by a double array, to the input argument
     * provided.
     * 
     * @param location 3D coordinates of an object or entity, which is assigned to the
     * function's local variable `this.location`.
     * 
     * 	- The `double[]` parameter `location` represents an array of double values
     * containing the location coordinates.
     * 	- Each element in the array corresponds to a particular coordinate (longitude and
     * latitude) for the location.
     */
    public void setLocation(double[] location) {
        this.location = location;
    }

    /**
     * retrieves the value of a static variable `sensorid`.
     * 
     * @returns a string representing the sensor ID.
     */
    public String getSensorid() {
        return sensorid;
    }

    /**
     * sets the value of a field named `sensorid` to a provided string parameter.
     * 
     * @param sensorId ID of the sensor to which the method is applying the specified
     * action, and it is assigned to the `sensorid` field of the class instance.
     */
    public void setSensorid(String sensorId) {
        this.sensorid = sensorId;
    }

    /**
     * returns a string representing the name of an object.
     * 
     * @returns a string representing the name of the object.
     */
    public String getName() {
        return name;
    }

    /**
     * sets the value of the class member variable `name`.
     * 
     * @param name name to be assigned to the object, and by assigning a value to it
     * within the function, the name of the object is updated.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * returns a string representing the sensory type of an object.
     * 
     * @returns a string representing the sensory type.
     */
    public String getSensortype() {
        return sensortype;
    }

    /**
     * sets a object's `sensortype` field to the provided String value.
     * 
     * @param sensortype sensor type to be used by the `setSensortype()` method, providing
     * a means for setting the specific type of sensor that will be used by the method.
     */
    public void setSensortype(String sensortype) {
        this.sensortype = sensortype;
    }

    /**
     * returns an array of sample objects, `samples`.
     * 
     * @returns an array of `Sample` objects.
     * 
     * 	- `samples`: A sample array of objects that contains information about the samples.
     * The length of this array indicates the total number of samples available. Each
     * object within the array has fields for the sample name, description, and other
     * relevant details.
     */
    public Sample[] getSamples() {
        return samples;
    }

    /**
     * updates the reference to the last sample in a collection and sets the value of the
     * `samples` field to that collection.
     * 
     * @param samples sample data to be stored in the `this.samples` member variable.
     * 
     * 	- Length greater than 0: The `samples` array has at least one element, indicating
     * that the last element in the array can be used for setting a sample.
     * 	- Length equal to 0: The `samples` array is empty, meaning no samples are present
     * and the last call to `setSample` resulted in null.
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
     * returns a `Sample` object, which is its own instance variable `sample`.
     * 
     * @returns a reference to an object of type `Sample`.
     * 
     * The `sample` variable is of type `public Sample`, indicating that it is a class
     * member and can be accessed within the same class or package.
     * 
     * The `return` statement indicates that the function will return the `sample` object
     * to the caller.
     * 
     * The `sample` object represents an instance of the `Sample` class, which contains
     * properties and methods related to sample data.
     */
    public Sample getSample() {
        return sample;
    }

    /**
     * sets the value of the class member variable `sample`.
     * 
     * @param sample Sample object that is assigned to the field `this.sample` within the
     * function, effectively storing the value of the input parameter.
     * 
     * 	- `this.sample`: This field assigns the input `sample` to a class member variable
     * named `sample`.
     */
    public void setSample(Sample sample) {

        this.sample = sample;
    }

    /**
     * returns a string representing the unit associated with a given value.
     * 
     * @returns a string representation of the variable `unit`.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * sets the `unit` field of the current object to the provided `String`.
     * 
     * @param unit unit of measurement for the object being modified by the `setUnit()`
     * method.
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * retrieves an access token for use in authenticated API calls.
     * 
     * @returns a string representing the access token.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * sets the instance variable `accessToken` to a given string value.
     * 
     * @param accessToken token that grants access to the protected resources.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * generates a string representation of an object by combining its `sensorID`,
     * `samples`, and unit into a single string.
     * 
     * @returns a concise representation of the sensor ID and value, including the unit.
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
     * returns an integer value indicating that no contents are present.
     * 
     * @returns an integer value of 0.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * writes the location, sensor ID, name, sensor type, and samples to a Parcel object,
     * along with the unit of measurement.
     * 
     * @param dest Parcel object that will be used to write the instance's state to.
     * 
     * 1/ `dest`: A `Parcel` object that represents the output stream for writing the
     * data to a Parcel. It has various attributes such as `writeDoubleArray()`,
     * `writeString()`, `writeString()`, `writeParcelableArray()`, and `writeString()`.
     * 2/ `location`: An array of double values representing the location of the sensor.
     * 3/ `sensorid`: A string value representing the ID of the sensor.
     * 4/ `name`: A string value representing the name of the sensor.
     * 5/ `sensortype`: A string value representing the type of sensor (e.g., "GPS",
     * "Accelerometer", etc.).
     * 6/ `samples`: An array of Parcelable objects representing the sensor readings. The
     * `Parcelable` interface allows for efficient serialization and deserialization of
     * complex data structures.
     * 7/ `unit`: A string value representing the unit of measurement for the sensor
     * readings (e.g., " meters", "degrees", etc.).
     * 
     * @param flags 32-bit integer value that specifies the type of data being written
     * to the Parcel, with possible values including `PARCEL_WRITE_REusable`,
     * `PARCEL_WRITE_TRANSIENT`, and `PARCEL_WRITE_CANCELABLE`.
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
         * creates a new instance of `Sensable` from a given `Parcel`.
         * 
         * @param in parcel that contains the data to be converted into a `Sensable` object.
         * 
         * 	- `in`: A Parcel object containing the data to be deserialized into a `Sensable`
         * object.
         * 	- `Sensable`: The class that represents the type of data being deserialized, with
         * properties and attributes defined by its definition.
         * 
         * @returns a newly created `Sensable` object initialized from the provided `Parcel`
         * input.
         * 
         * The `Sensable` object created is an instance of a class that represents a sensory
         * input capable of being processed by the system. It contains information regarding
         * its origin and format, as well as any relevant data itself. The format could be
         * audio, video, image, or another type of sensory input altogether. The class likely
         * has fields for these various parameters, which are populated with values provided
         * in the `in` parameter passed to the function.
         */
        public Sensable createFromParcel(Parcel in) {
            return new Sensable(in);
        }

        /**
         * creates a new array of `Sensable` objects with the specified size.
         * 
         * @param size number of elements to be created and allocated in the newly returned
         * array.
         * 
         * @returns an array of `Sensable` objects with the specified size.
         * 
         * 	- The `Sensable[]` array is created with the specified `size` parameter.
         * 	- The array contains zero or more instances of the `Sensable` class, which has
         * not been provided in the code snippet.
         * 	- The exact behavior and attributes of the `Sensable` class are unknown without
         * further information.
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
     * takes a `Sample` object and returns its JSON representation as a string.
     * 
     * @returns a JSON string representation of the `Sample` object.
     */
    public String getSampleAsJsonString() {
        if(sample == null) {
            this.sample = new Sample();
        }
        return this.sample.toJson().toString();
    }
}
