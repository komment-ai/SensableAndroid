package io.sensable.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by simonmadine on 12/07/2014.
 */
/**
 * Represents an entity with personal information and an access token, allowing for
 * serialization and transmission. It implements Parcelable to facilitate efficient
 * data transfer. The class provides getter and setter methods for accessing user details.
 */
public class User implements Parcelable {

    private String username;
    private String email;
    private String accessToken;

    public User() {
    }

    /**
     * Returns a string representing a username. It retrieves and exposes the internal
     * value of the `username` variable, allowing external access to the stored username
     * data. The returned string can be used by other parts of the program for various purposes.
     *
     * @returns a string value representing the user's username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets a specified string value as the object's username attribute. It assigns the
     * given `username` parameter to the `this.username` field, effectively updating the
     * object's username property.
     *
     * @param username value to be assigned to the instance variable `this.username`,
     * updating its current state.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Retrieves and returns a string value representing an email address. The email
     * address is stored as an instance variable named `email`.
     *
     * @returns a string representing an email address stored as an instance variable.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Assigns a new value to the `email` attribute of an object. It accepts a string as
     * input and updates the internal state of the object with the provided email address.
     * This allows the object's email property to be modified dynamically.
     *
     * @param email value to be assigned to the instance variable `email`.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns a string value representing an access token. It retrieves and provides
     * access to the stored access token, allowing it to be used for subsequent operations
     * or API calls. The returned token is likely used to authenticate and authorize requests.
     *
     * @returns a string containing the stored access token.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Assigns a specified `accessToken` to an instance variable with the same name,
     * updating its value. This allows the object to store and manage access tokens for
     * authentication purposes. The new access token replaces any previously stored one.
     *
     * @param accessToken value to be assigned to the instance variable `this.accessToken`,
     * updating its state with the provided string.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Returns an integer indicating the type of contents contained within an object,
     * which is used by the Android operating system for debugging and serialization
     * purposes. The returned value is typically a constant that indicates the type of
     * data stored in the object.
     *
     * @returns an integer value indicating the type of parcelable object.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes the values of `username`, `email`, and `accessToken` to a parcel object
     * using `Parcelable` interface for serialization. It overrides the default implementation
     * to customize how objects are written to a parcel.
     *
     * @param dest parcel that is being written to, allowing the object's properties to
     * be serialized and stored within it.
     *
     * @param flags optional settings that control how Parcel writes and reads its data,
     * such as whether to include metadata or not.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(accessToken);

    }
}