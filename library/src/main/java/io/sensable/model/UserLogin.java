package io.sensable.model;

/**
 * Created by simonmadine on 12/07/2014.
 */
/**
 * is a Java class that represents a user login functionality. It has various fields
 * and methods for managing username and password details.
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
     * returns a string representing the password.
     * 
     * @returns a string representing the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * sets the password field of the current object to the given string value.
     * 
     * @param password password to be stored in the `this.password` field of the class.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * returns a string representing the user's username.
     * 
     * @returns the value of the `username` field.
     */
    public String getUsername() {
        return username;
    }

    /**
     * sets the value of the class instance variable `username` to the provided String argument.
     * 
     * @param username username for the current user account, which is assigned to the
     * class instance variable `username`.
     */
    public void setUsername(String username) {
        this.username = username;
    }

}
