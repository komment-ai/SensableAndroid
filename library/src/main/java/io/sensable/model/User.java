package io.sensable.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by simonmadine on 12/07/2014.
 */
/**
 * in the provided Java file represents a user with personal information and an access
 * token. The class implements Parcelable, allowing it to be serialized and transmitted
 * efficiently.
 */
public class User implements Parcelable {

    private String username;
    private String email;
    private String accessToken;

    public User() {
    }

    /**
     * retrieves the user name from a designated variable `username`.
     * 
     * @returns a string representing the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * sets the value of the `username` field within an object to the provided `String`.
     * 
     * @param username user's username which is assigned to the `this.username` field of
     * the function.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * retrieves a string representing the user's email address.
     * 
     * @returns a string representing the email address of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * sets the `email` field of an object to a given String value.
     * 
     * @param email email address to be associated with the object instance.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * retrieves a pre-defined access token for use in API requests.
     * 
     * @returns a string representing the access token.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * sets the `accessToken` field of an object to a provided string value.
     * 
     * @param accessToken token that grants access to a resource or service, and it is
     * assigned to the `this.accessToken` field within the function.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * returns an integer value of 0, indicating that no contents are described.
     * 
     * @returns 0.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * writes the username, email and access token to a Parcel object.
     * 
     * @param dest parcel that will receive the data written by the `writeToParcel()` method.
     * 
     * 	- `dest`: An instance of `Parcel`, which is a generic class in Android for storing
     * and transmitting data between objects. It has various attributes such as `writeReason`
     * (an integer), `nativeObject` (a native object), and `ref()` (a reference to an object).
     * 
     * @param flags 16-bit flag value that determines how the data is being written to
     * the parcel, with possible values including 0x0000 for a standard write or 0x0001
     * for a writable subset of the data.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(accessToken);

    }
}