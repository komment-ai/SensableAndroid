package io.sensable.client;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import io.sensable.SensableService;
import io.sensable.client.adapter.TabsPagerAdapter;
import io.sensable.model.ScheduledSensable;
import io.sensable.model.Sensable;
import io.sensable.model.Statistics;
import io.sensable.model.UserLogin;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by simonmadine on 19/07/2014.
 */
/**
 * Is an Android application that manages user login and sensable operations. It
 * extends FragmentActivity and implements ActionBar.TabListener to handle tabs
 * navigation. The class initializes a ViewPager and ActionBar, sets up a TabsPagerAdapter,
 * and handles menu item selections for login, logout, create sensable, and about actions.
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public final static String EXTRA_SENSABLE = "io.sensable.sensable";

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;

    // Tab titles
    private String[] tabs = {"Favourites", "Local", "Remote"};

    public SensableUser sensableUser;

    //define callback interface
    /**
     * Defines a method to update the login status and display a toast message based on
     * whether the user is logged in or not.
     */
    public interface CallbackInterface {
        void loginStatusUpdate(Boolean loggedIn);
    }

    /**
     * Initializes the main activity by setting its content view and retrieving shared
     * preferences. It checks if a user is logged in, displaying a toast message accordingly.
     * Finally, it calls the `initialiseTabs` method to set up the tabs.
     *
     * @param savedInstanceState Bundle object that contains the activity's previously
     * saved state, allowing for restoration after rotation or other configuration changes.
     *
     * Bundle is an interface for classes that manage a set of key-value pairs. It provides
     * methods to retrieve values by key and to associate values with keys.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sensableUser = new SensableUser(sharedPref, this);
        if (sensableUser.loggedIn) {
            Toast.makeText(MainActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Not logged In", Toast.LENGTH_SHORT).show();
        }

        initialiseTabs();
    }

    /**
     * Initializes a ViewPager and ActionBar with tabs, sets an adapter for the ViewPager,
     * and sets up listeners for the ViewPager's page changes to update the selected tab
     * in the ActionBar.
     */
    private void initialiseTabs() {
        // Initialization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            /**
             * Sets a navigation item as selected based on the provided position, typically used
             * to update the action bar navigation after changing pages.
             *
             * @param position 0-based index of the selected item in the action bar's navigation
             * items, used to select the corresponding tab.
             */
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            /**
             * Is overridden to handle page scrolling events. It takes three arguments: the current
             * page number, the scroll distance as a fraction of the total distance, and the
             * scroll state (SCROLL_STATE_IDLE, SCROLL_STATE_TOUCH_SCROLL, or SCROLL_STATE_FLING).
             *
             * @param arg0 0-based index of the current page.
             *
             * @param arg1 fractional page offset from the start of the current page.
             *
             * @param arg2 x-coordinate of the view which is being scrolled horizontally.
             */
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            /**
             * Overrides a method to handle state changes of page scrolling. It is triggered when
             * the scroll state of the current page changes, allowing for actions based on the
             * new state. The function does not contain any specific implementation, leaving it
             * empty.
             *
             * @param arg0 0-based index of the current scroll state, indicating whether the page
             * is idle (0), scrolling (1), or at the top (2).
             */
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }

    /**
     * Overrides the default onStart method inherited from its superclass. It ensures
     * that the current object's lifecycle is properly initialized, and it calls the
     * `super.onStart()` method to propagate the call up the inheritance chain. This
     * function handles the start event of an application component.
     */
    @Override
    public void onStart() {
        super.onStart();
    }


    /**
     * Handles changes to the device's configuration, such as screen orientation or
     * language settings. It calls the superclass's implementation and does not perform
     * any specific actions.
     *
     * @param newConfig Configuration object that contains information about changes made
     * to the device's screen configuration, such as orientation or language.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    /**
     * Inflates a menu into an action bar, if present. It sets visibility for items based
     * on user login status: login and logout buttons appear when the user is logged in
     * or out, respectively; create button appears only when the user is logged in.
     *
     * @param menu action bar's menu that is being populated with items from the
     * `R.menu.saved_sensables` resource.
     *
     * @returns a boolean value indicating whether options are successfully created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.saved_sensables, menu);
        menu.findItem(R.id.action_login).setVisible(!sensableUser.loggedIn);
        menu.findItem(R.id.action_logout).setVisible(sensableUser.loggedIn);
        menu.findItem(R.id.action_create).setVisible(sensableUser.loggedIn);
        return true;
    }

    /**
     * Handles clicks on action bar items in a menu. It identifies the clicked item and
     * performs corresponding actions, such as launching about dialog, logging out user,
     * or creating sensable content. It also updates the login status and displays toast
     * messages accordingly.
     *
     * @param item MenuItem that was clicked and triggered the onOptionsItemSelected
     * method call.
     *
     * The item has an `id`, which is checked against specific IDs for different actions.
     * The item does not have any other properties mentioned in this code snippet.
     *
     * @returns a boolean indicating whether the menu item was handled or not.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_about) {
            launchAbout();
        } else if (id == R.id.action_login) {
            loginDialog();
        } else if (id == R.id.action_logout) {
            sensableUser.deleteSavedUser(new CallbackInterface() {
                /**
                 * Updates the login status and displays a corresponding toast message based on whether
                 * the user is logged in or out. If the user is not logged in, it shows a "Logged
                 * out" message; otherwise, it shows a "Logout failed" message.
                 *
                 * @param loggedIn status of user login, determining whether to display a "Logged
                 * out" or "Logout failed" toast message accordingly.
                 */
                @Override
                public void loginStatusUpdate(Boolean loggedIn) {
                    if (!loggedIn) {
                        Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Logout failed", Toast.LENGTH_SHORT).show();
                    }
                    invalidateOptionsMenu();
                }
            });
        } else if (id == R.id.action_create) {
            createSensable(this.findViewById(android.R.id.content));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Launches an instance of the `AboutActivity` class. It creates a new `Intent` object
     * with the current context and specifies the target activity to be launched as
     * `AboutActivity`. The function then starts the specified intent using the `startActivity`
     * method.
     */
    private void launchAbout() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    /**
     * Called when the user clicks the Send button
     */
    /**
     * Initiates a login dialog for a user to log in with their credentials. It updates
     * the UI and menu options based on the login status, displaying a toast message
     * indicating success or failure of the login operation.
     */
    public void loginDialog() {
        FragmentManager fm = getFragmentManager();
        SensableLoginFragment sensableLoginFragment = new SensableLoginFragment();
        sensableLoginFragment.setSensableLoginListener(new SensableLoginFragment.SensableLoginListener() {
            /**
             * Updates the login status of a user and displays a toast message based on whether
             * the login operation is successful or not. It invokes the `login` method with a
             * callback interface, which triggers the `loginStatusUpdate` method to display success
             * or failure messages accordingly.
             *
             * @param userLogin UserLogin object that contains login credentials, which is passed
             * to the sensableUser for login processing.
             *
             * The object has a type and an ID property.
             */
            @Override
            public void onConfirmed(UserLogin userLogin) {
                sensableUser.login(userLogin, new CallbackInterface() {
                    /**
                     * Updates the login status by displaying a toast message indicating whether the login
                     * was successful or not. If logged in, it displays "Successfully logged In" and if
                     * failed, it displays "Login failed". It then calls the `invalidateOptionsMenu()`
                     * method to update the menu options.
                     *
                     * @param loggedIn boolean value indicating whether the login operation was successful
                     * or not.
                     */
                    @Override
                    public void loginStatusUpdate(Boolean loggedIn) {

                        if (loggedIn) {
                            Toast.makeText(MainActivity.this, "Successfully logged In", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                        invalidateOptionsMenu();
                    }
                });
            }
        });
        sensableLoginFragment.show(fm, "sensable_login_name");
    }

    /**
     * Called when the user clicks the Create Sensable menu item
     */
    /**
     * Displays a fragment that allows user confirmation of scheduled sensing actions.
     * When confirmed, it shows a toast message with the sensor ID using the `onConfirmed`
     * method. The function uses the `FragmentManager` to display the fragment and sets
     * a listener for confirmation events.
     *
     * @param view View that is hosting the method call and is not used within the method
     * itself.
     *
     * View is an object representing the user interface element to be displayed in the
     * activity. It does not have any specific properties mentioned in this code snippet.
     */
    public void createSensable(View view) {
        FragmentManager fm = getFragmentManager();
        CreateSensableFragment createSensableFragment = new CreateSensableFragment();
        createSensableFragment.setCreateSensableListener(new CreateSensableFragment.CreateSensableListener() {
            /**
             * Displays a toast message on the main activity with the sensor ID retrieved from a
             * `ScheduledSensable` object when a confirmation event occurs.
             *
             * @param scheduledSensable object that triggered the event and provides access to
             * its sensor ID, which is then displayed as a toast message.
             */
            @Override
            public void onConfirmed(ScheduledSensable scheduledSensable) {
                Toast.makeText(MainActivity.this, scheduledSensable.getSensorid(), Toast.LENGTH_SHORT).show();
            }
        });
        createSensableFragment.show(fm, "create_sensable_name");
    }


    /**
     * Sets the current item of a `ViewPager` to the position corresponding to the selected
     * tab, effectively displaying the associated fragment view. This function is triggered
     * when a tab is selected. The selection is updated through the provided `ActionBar.Tab`
     * and `FragmentTransaction`.
     *
     * @param tab selected tab from which this method is called, and its position is
     * accessed through the `getPosition()` method to determine the corresponding fragment
     * view.
     *
     * @param ft FragmentTransaction object that is used to commit the transaction and
     * replace the current fragment with the new one.
     */
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    /**
     * Handles the event when a tab is unselected from an ActionBar. It is called when a
     * different tab is selected, and it updates the related FragmentTransaction accordingly.
     * The actual functionality within the function is empty, as it only overrides the
     * default implementation.
     *
     * @param tab currently unselected action bar tab that is being updated or removed
     * from the action bar.
     *
     * @param ft FragmentTransaction object that is responsible for managing the addition
     * or removal of fragments from the activity's layout during the onTabUnselected
     * method execution.
     */
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    /**
     * Handles an event when a tab is reselected in an Android application's action bar.
     * It receives the selected tab and a fragment transaction as parameters, indicating
     * that the user has chosen to revisit the same tab.
     *
     * @param tab selected ActionBar Tab that is being reselected.
     *
     * @param ft fragment transaction to be used for replacing or adding fragments.
     */
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

}