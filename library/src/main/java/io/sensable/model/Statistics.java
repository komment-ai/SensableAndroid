package io.sensable.model;

/**
 * Created by simonmadine on 26/07/2014.
 */
/**
 * Encapsulates and manages an integer value representing a count of elements in a
 * collection. It provides methods to access and modify this count value through
 * getter and setter functions. The class maintains data integrity by restricting
 * direct modification of the count variable.
 */
public class Statistics {
    private int count;

    public Statistics() {
    }

    /**
     * Returns an integer value representing a count. The returned value is stored in the
     * `count` variable. This function provides access to the current count, allowing it
     * to be retrieved and used elsewhere in the program.
     *
     * @returns an integer value representing the current state of the `count` variable.
     */
    public int getCount() {
        return count;
    }

    /**
     * Assigns a new integer value to the `count` variable within the class. This allows
     * the external environment to modify the internal state of the object. The modified
     * value is then stored in the object for future reference.
     *
     * @param count new value to be assigned to the instance variable `this.count`.
     */
    public void setCount(int count) {
        this.count = count;
    }
}
