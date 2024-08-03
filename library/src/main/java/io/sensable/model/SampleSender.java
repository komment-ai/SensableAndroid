package io.sensable.model;

/**
 * Created by madine on 16/07/14.
 */
/**
 * is a Java class that represents a sender of samples. It has two private fields:
 * accessToken and sample, which can be set and retrieved using getter and setter
 * methods. The class also provides methods to retrieve the access token and sample.
 */
public class SampleSender {
    private String accessToken;
    private Sample sample;

    /**
     * returns a string representing an access token.
     * 
     * @returns a string representing the access token.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * sets the value of a class instance field `accessToken`.
     * 
     * @param accessToken access token to be stored in the class instance.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * returns a `sample` object, which contains some data or values that can be used for
     * various purposes in the program.
     * 
     * @returns a reference to an instance of the `Sample` class.
     * 
     * 	- The `sample` variable is of type `public Sample`. This indicates that it is an
     * instance of a class called `Sample`, which is likely to be a custom class defined
     * by the author of the code.
     * 	- The return statement simply returns the value of the `sample` variable, without
     * any additional processing or manipulation.
     * 	- There are no variables or methods defined within the function that could affect
     * the properties of the returned output. Therefore, the output is guaranteed to be
     * a complete and accurate representation of the `sample` variable at the time the
     * function was called.
     */
    public Sample getSample() {
        return sample;
    }

    /**
     * sets a `Sample` object to a class member called `sample`.
     * 
     * @param sample `Sample` data type and sets its associated field, `this.sample`, to
     * the input value provided in the function call.
     * 
     * 	- `this`: A reference to the current object instance, which is the receiver of
     * the method call.
     * 	- `sample`: A variable of type `Sample`, which holds the value passed to the
     * method as its argument.
     */
    public void setSample(Sample sample) {
        this.sample = sample;
    }

}
