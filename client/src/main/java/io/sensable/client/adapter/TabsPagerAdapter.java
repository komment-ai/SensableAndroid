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
 * Extends FragmentPagerAdapter, providing fragments for a tabbed interface with three
 * tabs: Top Rated, Games, and Movies. It determines the type of fragment to display
 * based on its index and returns a corresponding `Fragment` instance. The class has
 * a fixed count of 3 tabs.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Returns a Fragment instance based on the specified index, which determines the
     * type of content to display. It uses a switch statement to handle three cases:
     * FavouriteSensablesFragment for index 0, LocalSensablesFragment for index 1, and
     * RemoteSensablesFragment for index 2.
     *
     * @param index 0-based index of a tab within a tab layout, determining which fragment
     * to return from the switch statement.
     *
     * @returns a Fragment object, depending on the specified index.
     *
     * The output is an instance of a fragment class. The type of fragment depends on the
     * input index. If the index is 0, the output is an instance of `FavouriteSensablesFragment`.
     * If the index is 1, the output is an instance of `LocalSensablesFragment`. If the
     * index is 2, the output is an instance of `RemoteSensablesFragment`.
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
     * Returns an integer representing the number of items, specifically the number of
     * tabs. The method overrides a parent class's implementation and always returns a
     * fixed value of 3.
     *
     * @returns an integer value, specifically `3`.
     */
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}