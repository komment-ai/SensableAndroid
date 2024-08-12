package io.sensable.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by simonmadine on 12/07/2014.
 */
/**
 * Represents a user with personal information and an access token, implementing
 * Parcelable for efficient serialization and transmission. It provides getter and
 * setter methods for username, email, and access token fields. The class also overrides
 * the writeToParcel method to write these fields to a Parcel object.
 */
public class User implements Parcelable {

    private String username;
    private String email;
    private String accessToken;

    public User() {
    }

    /**
     * Retrieves and returns the value stored in the `username` variable. It does not
     * perform any computations or modifications, simply providing access to the existing
     * data. The returned value is a string representing the username.
     *
     * @returns a string representing the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Assigns a value to the instance variable `username`. It takes a `String` parameter,
     * which is used to set the value of the `username` attribute within the class. This
     * method sets the username for an object.
     *
     * @param username user name to be set for an object, and its value is assigned to
     * the instance variable `this.username`.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns a string value representing an email address. It retrieves and exposes the
     * internal state of the object, providing access to the stored email information for
     * external use or processing. The returned value is a simple string representation
     * of the email address.
     *
     * @returns a string value representing an email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets a given string value as an instance variable named `email`. The provided email
     * address is assigned to the object's internal state, allowing the object to maintain
     * and store its email property. This property can be accessed and modified through
     * this method.
     *
     * @param email email address to be set for the object, which is then assigned to the
     * `email` field of the same object.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns a string value representing an access token. The returned value is likely
     * stored in an instance variable named `accessToken`. This function allows other
     * parts of the program to retrieve and utilize the access token.
     *
     * @returns a string representing an access token.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Assigns a specified string value to the `accessToken` property of an object. This
     * property is then updated with the provided access token. The function does not
     * return any value and has no effect on the program's execution flow beyond updating
     * the object's state.
     *
     * @param accessToken value to be assigned to the `this.accessToken` instance variable.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Returns an integer value indicating the type and complexity of the Parcelable
     * object's contents. In this case, it returns a constant value of 0, indicating that
     * the object has no complex or non-primitive data types. This is typically used for
     * debugging purposes in Android development.
     *
     * @returns an integer value indicating the type of bundle containing the Parcelable
     * object.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Serializes three string variables: `username`, `email`, and `accessToken`. It
     * writes these values to a Parcel object, allowing them to be stored or transmitted
     * as binary data.
     *
     * @param dest parcel where the object's data is being written, allowing the serialization
     * of the provided values to the parcelled output stream.
     *
     * @param flags bitwise OR of multiple values that specify how the parcel should be
     * written, such as whether to use compression or encryption.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(accessToken);

    }
}