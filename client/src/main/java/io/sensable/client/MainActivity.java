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
 * Implements an ActionBar and ViewPager to manage multiple fragments and tabs,
 * handling login and logout functionality, displaying a toast message upon login
 * status update, and creating a new sensable user. It also provides callback interfaces
 * for login status updates and sensable creation confirmation.
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
     * Provides a callback mechanism for receiving updates on the login status of a user
     * after they have logged in or out.
     */
    public interface CallbackInterface {
        void loginStatusUpdate(Boolean loggedIn);
    }

    /**
     * Sets up the main activity layout, retrieves shared preferences, creates a SensableUser
     * object, checks login status and displays corresponding toast messages, and then
     * initializes tabs.
     *
     * @param savedInstanceState Bundle object that contains the data while user is
     * interacting with application and if user has pressed back button then it will show
     * previous state of activity.
     *
     * Bundle savedInstanceState contains key-value pairs representing the application's
     * state. Its main properties include the key-value pairs themselves and their
     * respective ordering, allowing for efficient storage and retrieval of data.
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
     * Sets up a tabbed interface with a ViewPager and ActionBar, creating tabs from an
     * array of tab names and setting their corresponding listeners. It also configures
     * the ViewPager to update the selected tab based on page changes.
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
             * Selects a navigation item in an action bar based on the given position. When a
             * page is changed, it makes the corresponding tab selected in the action bar.
             *
             * @param position 0-based index of the selected item or page, which is used to select
             * the corresponding navigation item on the action bar.
             */
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            /**
             * Is overridden to handle page scrolling events in a ViewPager. It takes three
             * arguments: an integer representing the new current item, a float representing the
             * position offset within the current item, and an integer representing the action
             * type (e.g., scrolled horizontally).
             *
             * @param arg0 0-based index of the current page being scrolled.
             *
             * @param arg1 scroll distance in pixels and is used to calculate the new offset of
             * the current page.
             *
             * @param arg2 position offset during a scroll operation.
             */
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            /**
             * Notifies the client about a change in the scrolling state of the page. It provides
             * an integer parameter, `arg0`, which indicates whether the scroll is idle, touching,
             * or flinging. The function does not have any implementation, indicating that it may
             * be intended to perform some action based on the changed state but has been left
             * blank for future development.
             *
             * @param arg0 new scroll state of the pager, which is an integer value indicating
             * whether the pager is idle (0), scrolling (1), or sliding to start (2).
             */
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }

    /**
     * Overrides the default onStart method inherited from a superclass and executes when
     * the activity starts. It calls the superclass's onStart method to perform necessary
     * initialization. This function is typically used for initialization tasks that
     * require the activity to be fully started.
     */
    @Override
    public void onStart() {
        super.onStart();
    }


    /**
     * Handles changes to the device's configuration, such as screen orientation or
     * language settings. It is called when a configuration change occurs and invokes its
     * superclass's implementation. The function takes a new configuration object as input
     * and performs no specific actions itself.
     *
     * @param newConfig updated configuration of the device, which is passed to the method
     * when the screen's orientation changes or other system settings are modified.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    /**
     * Inflates a menu from the `saved_sensables` resource and populates it with items
     * based on the login status of the `sensableUser`. It shows or hides specific menu
     * items depending on whether the user is logged in or not.
     *
     * @param menu action bar's menu that is being populated with items from the resource
     * file R.menu.saved_sensables.
     *
     * @returns a boolean value indicating the success of menu inflation.
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
     * Handles clicks on action bar items, including "About", "Login", and "Logout". It
     * performs corresponding actions such as launching a dialog or updating the login
     * status and displaying a toast message.
     *
     * @param item MenuItem object that was clicked, providing access to its identifier
     * and other properties.
     *
     * Extracted:
     * - `int id`: The unique ID of the action bar item that was selected.
     * - `MenuItem item`: Represents an action bar item in the context menu.
     *
     * @returns a boolean value indicating whether an action was selected or not.
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
                 * Updates the login status by displaying a toast message indicating whether the user
                 * is logged out or logout has failed, and then invalidates the options menu.
                 *
                 * @param loggedIn boolean status of user login, determining whether to display a
                 * logout or logout failure message.
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
     * Initializes an instance of the `Intent` class with a reference to the current
     * context and the `AboutActivity` class. The function then starts an activity specified
     * by the intent, displaying the about activity to the user.
     */
    private void launchAbout() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    /**
     * Called when the user clicks the Send button
     */
    /**
     * Displays a login dialog to the user, handles the login status update and displays
     * a toast message accordingly. It also updates the options menu when the user's login
     * status changes. The function sets up a callback interface for the login operation
     * to receive the login status update.
     */
    public void loginDialog() {
        FragmentManager fm = getFragmentManager();
        SensableLoginFragment sensableLoginFragment = new SensableLoginFragment();
        sensableLoginFragment.setSensableLoginListener(new SensableLoginFragment.SensableLoginListener() {
            /**
             * Updates the login status of a user and displays a toast message indicating whether
             * the login was successful or not. It calls a callback interface to perform this
             * operation, displaying success or failure messages based on the result.
             *
             * @param userLogin credentials of the user attempting to log in, which are passed
             * to the `sensableUser.login()` method for processing.
             *
             * UserLogin has the following attributes - username and password.
             */
            @Override
            public void onConfirmed(UserLogin userLogin) {
                sensableUser.login(userLogin, new CallbackInterface() {
                    /**
                     * Updates the login status of an application. When a user logs in successfully, it
                     * displays a toast message indicating successful login; otherwise, it displays a
                     * toast message indicating failed login. It also invalidates the options menu after
                     * updating the login status.
                     *
                     * @param loggedIn status of the user's login attempt, indicating whether it was
                     * successful or not.
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
     * Displays a fragment for creating sensable actions and sets a listener to confirm
     * scheduled sensing actions. When confirmed, it shows a toast message with the sensor
     * ID.
     *
     * @param view View object passed to the method, which is not utilized within the
     * method and can be removed.
     *
     * View: an object representing a user interface element that can receive events such
     * as clicks or touches.
     */
    public void createSensable(View view) {
        FragmentManager fm = getFragmentManager();
        CreateSensableFragment createSensableFragment = new CreateSensableFragment();
        createSensableFragment.setCreateSensableListener(new CreateSensableFragment.CreateSensableListener() {
            /**
             * Displays a toast message with the ID of a sensor when confirmed by the user. It
             * takes an instance of `ScheduledSensable` as input and uses the `getSensorid` method
             * to retrieve the sensor ID. The toast is displayed for a short duration using Toast's
             * `makeText` method.
             *
             * @param scheduledSensable object that triggered the confirmation event and provides
             * access to its sensor ID through the `getSensorid()` method.
             */
            @Override
            public void onConfirmed(ScheduledSensable scheduledSensable) {
                Toast.makeText(MainActivity.this, scheduledSensable.getSensorid(), Toast.LENGTH_SHORT).show();
            }
        });
        createSensableFragment.show(fm, "create_sensable_name");
    }


    /**
     * Switches to a specific fragment when an action bar tab is selected, based on the
     * tab's position. It sets the current item of a view pager to match the selected
     * tab, effectively displaying the corresponding fragment.
     *
     * @param tab selected ActionBar Tab, which is used to determine the position of the
     * tab and subsequently set the current item in the ViewPager.
     *
     * @param ft FragmentTransaction object that manages the addition or removal of
     * fragments from the activity's layout, allowing for dynamic changes to be made to
     * the UI.
     */
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    /**
     * Specifies actions to be performed when a tab is unselected in an Android application's
     * ActionBar. This method is part of a callback interface and overrides a default
     * implementation, allowing customization of the tab selection process. No specific
     * actions are defined for this tab.
     *
     * @param tab Action Bar Tab that is being unselected and provides information about
     * its configuration and state.
     *
     * @param ft FragmentTransaction object that is used to manage changes to the fragment's
     * state.
     */
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    /**
     * Handles the event when a tab is reselected within an action bar. It receives the
     * selected tab and a fragment transaction as parameters, but does not perform any
     * specific actions or operations.
     *
     * @param tab ActionBar.Tab object that was just reselected by the user and is passed
     * to this method for processing.
     *
     * @param ft FragmentTransaction object that allows to commit or reverse operations
     * on fragment's state.
     */
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

}