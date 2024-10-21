package io.sensable.model;

/**
 * Created by simonmadine on 18/07/2014.
 */
/**
 * Encapsulates two attributes: message and sensorid. It provides getter and setter
 * methods for these attributes, allowing access and modification of their values.
 * The class is designed to store and manipulate string data related to messages and
 * sensor IDs.
 */
public class SampleResponse {
    private String message;
    private String sensorid;

    public SampleResponse() {
    }

    /**
     * Returns a string value stored in the `message` variable. It is likely to be part
     * of a class that encapsulates some kind of messaging system, providing a way to
     * retrieve the current message. The returned message can then be used by the calling
     * code as needed.
     *
     * @returns a string representing the current value of the `message` variable.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Assigns a given string to the `message` field of the object. This allows the
     * object's message property to be updated with a new value. The new message replaces
     * any previously stored message.
     *
     * @param message value to be assigned to the instance variable `this.message`,
     * updating its value within the class.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Retrieves and returns a string value representing the `sensorid`. The returned
     * value is stored in the instance variable `sensorid`. This function allows access
     * to the `sensorid` value for use elsewhere in the program.
     *
     * @returns a string representing the value of the `sensorid` variable.
     */
    public String getSensorid() {
        return sensorid;
    }

    /**
     * Assigns a specified `sensorid` value to an instance variable. The `sensorid`
     * parameter is passed as a string, and it updates the internal state of the object
     * with the provided ID. This method sets the sensor ID for subsequent use within the
     * class.
     *
     * @param sensorid identifier of a sensor, which is assigned to the instance variable
     * `this.sensorid` in the method.
     */
    public void setSensorid(String sensorid) {
        this.sensorid = sensorid;
    }
}
