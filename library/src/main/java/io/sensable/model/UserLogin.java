package io.sensable.model;

/**
 * Created by simonmadine on 12/07/2014.
 */
/**
 * Represents user login functionality, encapsulating username and password details.
 * It provides getter and setter methods for these attributes, allowing manipulation
 * of the data. The class supports default constructor and a parameterized constructor
 * for initializing instances with or without provided username and password values.
 */
public class UserLogin {
    private String username;
    private String password;

    public UserLogin() {
    }

    public UserLogin(String username, String password) {
        this.username = username;
        this.password = password;

    }
    /**
     * Returns the value of a variable named `password`. This suggests that the function
     * is providing access to the stored password, likely for retrieval or display purposes.
     * The returned value is a string representation of the password.
     *
     * @returns a string value representing the stored password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Updates the value of an internal variable named `password` with a provided string
     * input. The new password replaces any previously set password, effectively changing
     * the stored password. This function has no return value and operates on the object's
     * internal state.
     *
     * @param password string value to be set as the new password of the object, which
     * is then stored in the `this.password` field.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Retrieves and returns a string value representing the current username. It does
     * not modify or manipulate the username in any way, simply providing access to its
     * value. The returned string is based on the instance variable `username`.
     *
     * @returns a string representing the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Assigns a given string value to the object's `username` attribute. This attribute
     * is likely used to store and retrieve user credentials or identities within the
     * program. The assigned value can be retrieved or manipulated by other parts of the
     * code as needed.
     *
     * @param username new value to be assigned to the instance variable `username`.
     */
    public void setUsername(String username) {
        this.username = username;
    }

}
