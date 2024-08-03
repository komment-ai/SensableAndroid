package io.sensable.client.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import io.sensable.client.views.FavouriteSensablesFragment;
import io.sensable.client.views.LocalSensablesFragment;
import io.sensable.client.views.RemoteSensablesFragment;

/**
 * Created by simonmadine on 20/07/2014.
 */
/**
 * is an extension of FragmentPagerAdapter that provides fragments for a tabbed
 * interface with three tabs: Top Rated, Games, and Movies. The adapter receives the
 * index of the current tab and returns the corresponding fragment activity.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * determines the type of fragment to display based on its index and returns a
     * `Fragment` instance accordingly.
     * 
     * @param index 0-based index of the fragment to be returned, with values 0, 1, and
     * 2 corresponding to different fragments: `FavouriteSensablesFragment`,
     * `LocalSensablesFragment`, and `RemoteSensablesFragment`, respectively.
     * 
     * @returns a reference to a `Fragment` object that represents one of four different
     * activity types based on the input index.
     * 
     * The return type of the function is `Fragment`, which means that the function returns
     * an instance of a fragment class.
     * 
     * The variable `index` passed as an argument to the function takes on the values 0,
     * 1, or 2, indicating the type of fragment to be returned.
     * 
     * The code inside the `switch` statement defines three different fragments:
     * `FavouriteSensablesFragment`, `LocalSensablesFragment`, and `RemoteSensablesFragment`.
     * Each of these fragments represents a specific type of content, such as top-rated
     * sensibles, local sensibles, or remote sensibles.
     * 
     * The `return` statement at the end of the `switch` statement returns an instance
     * of one of these fragments based on the value of `index`.
     */
    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new FavouriteSensablesFragment();
            case 1:
                // Games fragment activity
                return new LocalSensablesFragment();
            case 2:
                // Movies fragment activity
                return new RemoteSensablesFragment();
        }

        return null;
    }

    /**
     * retrieves the count of items by calculating the number of tabs. The count is
     * returned as an integer value, equal to the number of tabs.
     * 
     * @returns the number of tabs.
     * 
     * 	- The output is an integer value representing the number of tabs.
     * 	- The value is equal to 3 in this case, indicating that there are three tabs
     * present in the system.
     * 	- The output does not provide any information about the contents or organization
     * of the tabs, only their total count.
     */
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}