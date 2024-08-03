package io.sensable.model;

/**
 * Created by simonmadine on 26/07/2014.
 */
/**
 * is a simple Java class that represents a count value and provides methods for
 * accessing and modifying that value. The class has a private variable `count` and
 * three public methods: `getCount()`, `setCount()`, and `getCount()` which returns
 * the current value of the `count` variable, allows it to be set to a new value, and
 * does not allow it to be modified in any other way.
 */
public class Statistics {
    private int count;

    public Statistics() {
    }

    /**
     * returns the value of the `count` field, which represents the number of elements
     * in a collection.
     * 
     * @returns the value of the `count` field.
     */
    public int getCount() {
        return count;
    }

    /**
     * sets the value of the `count` field to the input argument.
     * 
     * @param count integer value that will be assigned to the `count` field of the class
     * instance being manipulated by the function.
     */
    public void setCount(int count) {
        this.count = count;
    }
}
