package io.sensable.model;

/**
 * Created by simonmadine on 18/07/2014.
 */
/**
 * is a Java class with four attributes and three methods. The attributes include a
 * message and sensorid, while the methods allow for setting and retrieving these values.
 */
public class SampleResponse {
    private String message;
    private String sensorid;

    public SampleResponse() {
    }

    /**
     * returns a string representing a message.
     * 
     * @returns a string representing the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * sets the value of the object's `message` field to the provided string argument.
     * 
     * @param message message that will be stored in the `message` field of the class
     * instance, which is being modified by the `setMessage()` method call.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * returns a `String` object representing the `sensorid`.
     * 
     * @returns a string representing the sensor ID.
     */
    public String getSensorid() {
        return sensorid;
    }

    /**
     * sets the value of the `sensorid` field within a Java object, assigning the given
     * string value to it.
     * 
     * @param sensorid ID of a sensor that is being set within the function.
     */
    public void setSensorid(String sensorid) {
        this.sensorid = sensorid;
    }
}
