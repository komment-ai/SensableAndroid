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
 * in Android provides a framework for managing user interactions with sensors,
 * including login and create sensable functionality. The class implements the
 * ActionBar.TabListener interface to handle tab selection events and initializes
 * tabs using a TabsPagerAdapter. Additionally, it provides methods for handling
 * options menu items and action bar item clicks.
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
     * acts as a callback mechanism for receiving updates on the login status of a user
     * after they have logged in or out.
     */
    public interface CallbackInterface {
        void loginStatusUpdate(Boolean loggedIn);
    }

    /**
     * sets up the user interface and initializes the SensableUser class, which manages
     * the user's login state. If the user is logged in, it displays a toast message;
     * otherwise, it displays another toast message.
     * 
     * @param savedInstanceState saved state of the activity, which can be used to restore
     * the activity's state in case it is restarted or recreated.
     * 
     * 	- `super.onCreate(savedInstanceState)` - Calls the superclass's `onCreate` method
     * to perform any necessary initialization before proceeding with the current method's
     * code.
     * 	- `setContentView(R.layout.main_activity)` - Sets the content view of the activity
     * to the R.layout.main_activity file, which is assumed to contain the main layout
     * and widgets for the activity.
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
     * initializes a ViewPager and ActionBar, sets an adapter for the ViewPager, and adds
     * tabs to the ActionBar using a tab listener. When the user swipes the ViewPager,
     * the selected tab is updated in the ActionBar.
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
             * updates the selected item in the action bar based on the page number passed as an
             * argument.
             * 
             * @param position 0-based index of the selected tab in the navigation menu, which
             * is used to set the selected tab in the action bar.
             */
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            /**
             * is a callback method called when the user scrolls a web page. It receives the
             * current position of the scroll, the scrolling velocity, and the total scroll range
             * as arguments.
             * 
             * @param arg0 current scroll position of the web page within the viewport.
             * 
             * @param arg1 current scroll position of the view in fractional units, such as a
             * percentage of the total view size.
             * 
             * @param arg2 current position of the scrollview, measured in pixels from the top
             * of the view.
             */
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            /**
             * is called when the scrolling state of a page changes, and it performs an unspecified
             * action in response.
             * 
             * @param arg0 scroll state of the view pager, which is used to determine the appropriate
             * action to take in response to changes in the scroll position.
             */
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }

    /**
     * is a override of the superclass `onStart` method, indicating that it performs
     * additional actions beyond those of the parent class.
     */
    @Override
    public void onStart() {
        super.onStart();
    }


    /**
     * is a method in Android that responds to changes in the device's configuration. It
     * is called when the device's screen orientation, display density, or other
     * configurations change. The method updates the UI and other components of the app
     * based on the new configuration.
     * 
     * @param newConfig updated configuration of the device or environment that triggered
     * the `onConfigurationChanged()` method call, and it is passed to the superclass's
     * `onConfigurationChanged()` method for further processing.
     * 
     * 1/ Screen orientation: The `newConfig` object contains information about the current
     * screen orientation, including whether it is portrait or landscape, and the specific
     * orientation (e.g., "portrait-primary").
     * 2/ Display metric: The `displayMetrics` property of `newConfig` provides information
     * about the display size and density, such as the width and height of the screen in
     * pixels, and the pixel density in dots per inch (dpi).
     * 3/ Resource IDs: The `resources` property of `newConfig` contains a list of resource
     * IDs that are associated with the configuration change. These resources may include
     * drawable icons, layout files, or other assets that are relevant to the new configuration.
     * 4/ Layout: The `layout` property of `newConfig` specifies the current layout of
     * the application, including the orientation and size of the screen, as well as any
     * additional layout parameters such as margins or gravity.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    /**
     * inflates a menu with options based on the logged-in status of the sensable user
     * and sets the visibility of those options accordingly.
     * 
     * @param menu menu that will be inflated with additional items based on the logic
     * within the function.
     * 
     * 	- `R.menu.saved_sensables`: The menu resource ID that contains the menu items to
     * be inflated.
     * 	- `R.id.action_login`: The resource ID of the login item in the menu.
     * 	- `R.id.action_logout`: The resource ID of the logout item in the menu.
     * 	- `R.id.action_create`: The resource ID of the create item in the menu.
     * 
     * These properties are used to set the visibility of menu items based on the logged-in
     * status of the sensable user.
     * 
     * @returns a menu with items for logging in, logging out, and creating new sensables,
     * depending on the user's logged-in status.
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
     * handles menu item selections, such as opening the about screen or login dialog,
     * logging out, and creating a new sensable user. It calls parent class methods and
     * performs callback interface actions based on menu item IDs.
     * 
     * @param item selected menu item and provides its item ID, which is used to determine
     * the appropriate action to take based on the selected item.
     * 
     * 	- `id`: The unique identifier of the menu item, represented as an integer value.
     * 	- `item.getItemId()`: Returns the identifier of the menu item, which can be used
     * to determine its type and functionality.
     * 
     * The `if` statement checks for specific values of `id`, and performs different
     * actions based on the selected menu item.
     * 
     * @returns a handling of action bar item clicks, including launching an about dialog,
     * displaying a login dialog, logging out, and creating a new sensable.
     * 
     * 	- `id`: This is an integer that represents the ID of the selected item in the
     * action bar. It takes on the value of the item's ID defined in the `android:id`
     * attribute in the `menu_item` XML file.
     * 	- `super.onOptionsItemSelected(item)`: This is a call to the superclass's
     * implementation of `onOptionsItemSelected`, which handles the default action for
     * the selected item.
     * 
     * The output of the function is a boolean value indicating whether the action bar
     * item was handled successfully or not. If the item was handled, the method returns
     * `true`; otherwise, it returns `false`.
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
                 * updates the login status and displays a toast message based on whether the user
                 * is logged in or not, also invoking the `invalidateOptionsMenu()` method.
                 * 
                 * @param loggedIn login status of the user, with a value of `true` indicating that
                 * the user is logged in and `false` indicating otherwise.
                 * 
                 * 	- `loggedIn`: A Boolean value indicating whether the user is logged in or not.
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
     * starts an activity called `AboutActivity`.
     */
    private void launchAbout() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    /**
     * Called when the user clicks the Send button
     */
    /**
     * creates a new instance of the `SensableLoginFragment` and sets a listener to handle
     * the login confirmation event. When the user confirms the login, the fragment's
     * listener calls the callback interface with the logged-in status, which in turn
     * updates the UI and invalidates the options menu.
     */
    public void loginDialog() {
        FragmentManager fm = getFragmentManager();
        SensableLoginFragment sensableLoginFragment = new SensableLoginFragment();
        sensableLoginFragment.setSensableLoginListener(new SensableLoginFragment.SensableLoginListener() {
            /**
             * is called when a user logs in successfully or fails to log in. It passes the login
             * status to the CallbackInterface, which updates the UI and invalidates the options
             * menu.
             * 
             * @param userLogin user login details that are being confirmed.
             * 
             * 	- `sensableUser`: A reference to an object of class `SensableUser`.
             * 	- `login`: A method that takes a `CallbackInterface` as its parameter and logs
             * in the user with the provided callback interface.
             * 	- `invalidateOptionsMenu`: A method that is called when the user's login status
             * changes, which is used to update the menu options accordingly.
             */
            @Override
            public void onConfirmed(UserLogin userLogin) {
                sensableUser.login(userLogin, new CallbackInterface() {
                    /**
                     * updates the login status of a user and displays a toast message based on whether
                     * the login was successful or not.
                     * 
                     * @param loggedIn success of the login operation, with a value of `true` indicating
                     * successful login and `false` indicating failed login.
                     * 
                     * 	- `loggedIn`: A boolean value representing whether the user has successfully
                     * logged in or not.
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
     * creates a fragment to display a scheduled sensable and sets up a listener for when
     * the sensable is confirmed. When the sensable is confirmed, it displays a toast
     * message with the sensable ID.
     * 
     * @param view `View` object that contains the sensor ID to be confirmed and is used
     * to display the resulting sensor ID in a toast message.
     * 
     * 	- `getFragmentManager()` returns the Fragment Manager instance for the current activity.
     * 	- `setCreateSensableListener()` sets a listener interface for the Create Sensable
     * fragment to receive updates from the sensor.
     * 	- `getSensorid()` returns the ID of the sensor that was created.
     */
    public void createSensable(View view) {
        FragmentManager fm = getFragmentManager();
        CreateSensableFragment createSensableFragment = new CreateSensableFragment();
        createSensableFragment.setCreateSensableListener(new CreateSensableFragment.CreateSensableListener() {
            /**
             * is called when a scheduled sensing action is confirmed. It displays a toast message
             * with the sensor ID.
             * 
             * @param scheduledSensable sensor ID that will be displayed as a toast message when
             * the `onConfirmed()` method is called.
             * 
             * 	- `scheduledSensable`: A `ScheduledSensable` object containing information about
             * the sensor and its scheduled time.
             * 	- `sensorid`: The unique identifier of the sensor.
             */
            @Override
            public void onConfirmed(ScheduledSensable scheduledSensable) {
                Toast.makeText(MainActivity.this, scheduledSensable.getSensorid(), Toast.LENGTH_SHORT).show();
            }
        });
        createSensableFragment.show(fm, "create_sensable_name");
    }


    /**
     * updates the current item displayed by a `ViewPager` when a tab is selected in an
     * `ActionBar`.
     * 
     * @param tab selected tab that triggered the event, providing information about its
     * position within the tabs list.
     * 
     * 	- `tab`: The selected tab object, containing properties such as its position
     * (`getPosition()`), and the name or label of the tab (`getTitle()`).
     * 
     * @param ft FragmentTransaction object, which is used to manage the fragments displayed
     * by the ViewPager control.
     * 
     * 	- `tab`: The selected tab object containing information about the current tab
     * selection, such as its position and title.
     * 	- `viewPager`: A reference to the view pager widget that displays fragments based
     * on the selected tab.
     */
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    /**
     * is called when a tab is unselected from an action bar. It does not perform any
     * specific task and is typically used for cleanup or customization purposes.
     * 
     * @param tab Tab that was just selected or deselected, providing information about
     * its current state.
     * 
     * 	- `tab`: The unselected tab for which the onTabUnselected method was called. It
     * has various attributes such as `getTag()`, `getTitle()`, and `getIcon()`.
     * 
     * @param ft FragmentTransaction object that provides the necessary methods for
     * managing and updating the fragments associated with the action bar.
     * 
     * 	- `tab`: Represents the tab that was unselected.
     * 	- `ft`: Stands for Fragment Transaction, which contains information regarding the
     * fragments involved in the transaction.
     */
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    /**
     * is called when a previously selected tab is re-selected in the action bar. It has
     * no opemon functionality and simply ignores the event.
     * 
     * @param tab Tab that was reselected.
     * 
     * 	- `tab`: A `Tab` object that represents the tab being reselected. It has various
     * attributes such as its label, icon, and fragment.
     * 
     * @param ft transaction that is being performed when the tab is reselected.
     * 
     * 	- `tab`: The selected tab that triggered the event.
     * 	- `ft`: A fragment transaction object containing information about the fragments
     * involved in the transaction, such as their IDs and the current state of the fragments.
     */
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

}