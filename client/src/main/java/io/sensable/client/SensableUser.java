package io.sensable.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import io.sensable.SensableService;
import io.sensable.model.User;
import io.sensable.model.UserLogin;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

/**
 * Created by simonmadine on 12/07/2014.
 */
/**
 * Manages user authentication and settings for an Android application. It provides
 * methods to login, logout, retrieve user settings, save user data to preferences,
 * and delete saved user information. The class utilizes a Retrofit API to interact
 * with the server and update the user's status accordingly.
 */
public class SensableUser {

    private static final String TAG = SensableUser.class.getSimpleName();

    private User mUser;
    public boolean loggedIn = false;
    public boolean hasAccessToken = false;

    private SensableService service;
    private SharedPreferences sharedPreferences;
    private Context context;

    public SensableUser(SharedPreferences sharedPreferences, Context context) {
        this.sharedPreferences = sharedPreferences;
        this.context = context;

        mUser = new User();
        loggedIn = readUserFromPreferences();

        //TODO: Remove this once API doesn't use cookies
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("http://sensable.io")
                .build();
        service = restAdapter.create(SensableService.class);

    }

    /**
     * Reads user data from shared preferences and populates a `mUser` object with username,
     * email, and access token. If any of these values are empty or missing, the function
     * returns false indicating that the user is not logged in.
     *
     * @returns a boolean value indicating user login status.
     */
    private boolean readUserFromPreferences() {
        String username = sharedPreferences.getString(context.getString(R.string.saved_username), "");
        if (username != "") {
            Log.d(TAG, "Username:" + username);
            // User is logged in
            mUser.setUsername(username);

            String email = sharedPreferences.getString(context.getString(R.string.saved_email), "");
            mUser.setEmail(email);

            String accessToken = sharedPreferences.getString(context.getString(R.string.saved_access_token), "");
            Log.d(TAG, "Access Token:" + accessToken);
            if (accessToken != "") {
                mUser.setAccessToken(accessToken);
                hasAccessToken = true;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Authenticates a user with an authentication service and updates the user's login
     * status. It sets user attributes from the response, saves them to preferences, and
     * notifies the callback interface when successful or failed.
     *
     * @param userLogin user credentials to be authenticated by sending them to the
     * authentication service for verification and updating the application's state accordingly.
     *
     * - It contains user's login credentials.
     *
     * @param cb CallbackInterface, which is used to notify the login status update from
     * the login operation to the caller after successful authentication or failure.
     *
     * CallbackInterface cb has one method - loginStatusUpdate(loggedIn).
     */
    public void login(UserLogin userLogin, final MainActivity.CallbackInterface cb) {

        service.login(userLogin, new Callback<User>() {
            /**
             * Updates the UI component's username, email and login status based on the received
             * user data. It also saves the access token if provided and notifies a callback about
             * the login status update.
             *
             * @param user logged-in user, providing access to their username, email, and access
             * token for subsequent processing.
             *
             * The object `user` has the following attributes - username, email and access token.
             *
             * @param response result of an asynchronous operation and is not utilized within
             * this method.
             *
             * Response has no fields.
             */
            @Override
            public void success(User user, Response response) {
                Log.d(TAG, "Login Callback Success:" + user.getUsername());

                mUser.setUsername(user.getUsername());
                mUser.setEmail(user.getEmail());



                loggedIn = true;

                String accessToken = user.getAccessToken();
                if (accessToken != null) {
                    mUser.setAccessToken(accessToken);
                    hasAccessToken = true;
                    saveUserToPreferences();
                } else {
                    userSettings();
                }

                cb.loginStatusUpdate(loggedIn);
            }

            /**
             * Logs an error message to the console with a tag identifying the source of the log
             * entry. The error message includes the RetrofitError object, providing details about
             * the failure that occurred during the login process. This function handles any
             * errors that occur while attempting to log in.
             *
             * @param retrofitError exception or error that occurred during the Retrofit request,
             * which is then logged using the Log.e method.
             */
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Login callback failure" + retrofitError.toString());
            }
        });
    }

    /**
     * Retrieves user settings from a service and updates the local user object with
     * received information. If an access token is provided, it saves the user details
     * to preferences. It also handles errors that may occur during the login callback process.
     */
    public void userSettings() {

        service.settings(mUser.getUsername(), new Callback<User>() {
            /**
             * Handles a successful login callback. It updates local user data, sets a login
             * status flag to true, retrieves and saves an access token if available, and saves
             * the updated user data to preferences.
             *
             * @param user logged-in user's details, which are used to update the local variables
             * and preferences with the username, email, and access token.
             *
             * Extracted properties include username, email and access token.
             *
             * @param response response received from the login operation, but its value is not
             * utilized within the method, serving only as a redundant parameter.
             *
             * The response object contains an error and a body with the user details. The error
             * property represents whether the request was successful or not, while the body holds
             * the result of the request in JSON format.
             */
            @Override
            public void success(User user, Response response) {
                Log.d(TAG, "Login Callback Success:" + user.getUsername());

                mUser.setUsername(user.getUsername());
                mUser.setEmail(user.getEmail());
                Log.d(TAG, "Login Callback Success:" + user.getEmail());

                loggedIn = true;

                String accessToken = user.getAccessToken();
                Log.d(TAG, "Login Callback Success:" + accessToken);
                if (accessToken != null) {
                    mUser.setAccessToken(accessToken);
                    hasAccessToken = true;
                }
                saveUserToPreferences();
            }

            /**
             * Handles a Retrofit error by logging an error message to the console with tag `TAG`.
             * The error message includes the string "Login callback failure" and the Retrofit
             * error object's string representation, indicating that login callback operation failed.
             *
             * @param retrofitError exception that occurred during the Retrofit request processing
             * and provides details about the error in string form.
             */
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Login callback failure" + retrofitError.toString());
            }
        });
    }

    /**
     * Saves user information, including username, email address, and access token, to
     * shared preferences. It uses a SharedPreferences.Editor object to make changes and
     * then commits them.
     */
    private void saveUserToPreferences() {
        Log.d(TAG, "Saving: " + mUser.getUsername() + ", " + mUser.getEmail() + ", " + mUser.getAccessToken());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.saved_username), mUser.getUsername());
        editor.putString(context.getString(R.string.saved_email), mUser.getEmail());
        editor.putString(context.getString(R.string.saved_access_token), mUser.getAccessToken());
        editor.commit();
    }

    /**
     * Removes saved username, email, and access token from SharedPreferences, resets the
     * `mUser`, `loggedIn`, and `hasAccessToken` variables, and updates the login status
     * through a callback interface.
     *
     * @param cb CallbackInterface, which triggers a login status update by calling its
     * `loginStatusUpdate` method with the updated value of `loggedIn`.
     *
     * Declares a CallbackInterface with a single method loginStatusUpdate().
     */
    public void deleteSavedUser(final MainActivity.CallbackInterface cb) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(context.getString(R.string.saved_username));
        editor.remove(context.getString(R.string.saved_email));
        editor.remove(context.getString(R.string.saved_access_token));
        editor.commit();
        mUser = new User();
        loggedIn = false;
        hasAccessToken = false;
        cb.loginStatusUpdate(loggedIn);
    }

}
