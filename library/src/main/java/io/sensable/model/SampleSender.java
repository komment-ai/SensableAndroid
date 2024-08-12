package io.sensable.model;

/**
 * Created by madine on 16/07/14.
 */
/**
 * Represents a sender of samples with two private fields: access token and sample.
 * It provides getter and setter methods to retrieve and set these fields. The class
 * allows the manipulation of an access token and a Sample object, which is likely a
 * custom class containing some data or values.
 */
public class SampleSender {
    private String accessToken;
    private Sample sample;

    /**
     * Retrieves and returns a stored access token as a string. The function does not
     * modify or process the access token; it simply provides direct access to its value.
     *
     * @returns a string value representing an access token.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Sets a specified access token as an attribute of the current object. The provided
     * string is assigned to the `accessToken` field, allowing it to be used by the object
     * for subsequent operations or authentication purposes.
     *
     * @param accessToken value that is assigned to the instance variable `this.accessToken`,
     * updating its state.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Retrieves a `Sample` object and returns it. The object is stored in the `sample`
     * variable. This method allows access to the `sample` object, making its value
     * available for use by other parts of the program.
     *
     * @returns an instance of the `Sample` class named `sample`.
     */
    public Sample getSample() {
        return sample;
    }

    /**
     * Assigns a new value to the instance variable `sample`, replacing its previous value
     * with the provided `sample` object. This method updates the internal state of the
     * class by setting the reference of the `sample` field to the given `sample` parameter.
     *
     * @param sample Sample object to be assigned to the instance variable `this.sample`.
     */
    public void setSample(Sample sample) {
        this.sample = sample;
    }

}
