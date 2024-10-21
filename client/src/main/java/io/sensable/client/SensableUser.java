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
 * Is responsible for handling user authentication and settings retrieval in an Android
 * application. It provides functions to login, logout, retrieve user settings, and
 * save the user's information to preferences. The class also handles API calls using
 * Retrofit and logs user activity through a tag named TAG.
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
     * Retrieves user data from shared preferences, including username, email, and access
     * token, if available. It sets these values to a `mUser` object and returns a boolean
     * indicating whether the read operation was successful or not.
     *
     * @returns a boolean value indicating successful login or failure.
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
     * Authenticates a user by sending a request to the authentication service and receives
     * a response containing the user's information and access token. It updates the
     * user's preferences, notifies the login status update to the callback interface,
     * and logs error messages for failures.
     *
     * @param userLogin login credentials to be passed to the authentication service for
     * verification.
     *
     * - username: The username of the user.
     * - password: The password of the user.
     * - email: The email address of the user.
     *
     * @param cb CallbackInterface that notifies the login status update to the caller
     * when the login operation is successful.
     *
     * CallbackInterface cb:
     *
     * - has a method loginStatusUpdate(loggedIn): It updates the login status to the caller.
     */
    public void login(UserLogin userLogin, final MainActivity.CallbackInterface cb) {

        service.login(userLogin, new Callback<User>() {
            /**
             * Logs a message upon successful login, updates local user data, and checks for an
             * access token. If present, it saves the user to preferences; otherwise, it triggers
             * the user settings. It also notifies the callback with the logged-in status.
             *
             * @param user logged-in user, providing information such as username, email, and
             * access token to be used for further processing within the function.
             *
             * Gets username from user and sets to mUser's setUsername method.
             * Gets email from user and sets to mUser's setEmail method.
             * Get the access token from user if not null.
             *
             * @param response response from the login API call and is not used in the function,
             * as its value is not accessed or processed.
             *
             * Get a string response body from Response.
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
             * Logs an error message to the console when a Retrofit request fails. The error
             * message includes the exception details. It is used as a callback method for handling
             * failures in API calls, providing information about the failure for debugging purposes.
             *
             * @param retrofitError exception that occurred during the HTTP request execution and
             * is provided to the `failure` callback for further error handling.
             */
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Login callback failure" + retrofitError.toString());
            }
        });
    }

    /**
     * Retrieves user settings from a service using a Callback mechanism. It updates a
     * local user object and saves it to preferences if an access token is provided,
     * indicating successful login. In case of failure, it logs the error message.
     */
    public void userSettings() {

        service.settings(mUser.getUsername(), new Callback<User>() {
            /**
             * Logs user details and updates local variables with the received data, including
             * username, email, and access token. It also sets a flag indicating login success
             * and saves the updated user data to preferences.
             *
             * @param user authenticated user's details, which are then used to update local
             * variables and preferences.
             *
             * Extracts username and email from user object. The user object also contains an
             * access token property.
             *
             * @param response response from the server, which is currently not used within the
             * method.
             *
             * Response has no properties mentioned in this code snippet. The focus remains on
             * the User object provided as an argument to this method.
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
             * Handles errors that occur during a Retrofit API call. It logs an error message
             * with a tag and the error details to the Android logcat for debugging purposes,
             * indicating a failure in the login callback process.
             *
             * @param retrofitError error that occurred during the execution of the Retrofit
             * request, providing information about the failure.
             */
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Login callback failure" + retrofitError.toString());
            }
        });
    }

    /**
     * Saves user data, including username, email, and access token, to device storage
     * using SharedPreferences. The data is committed after being edited, allowing it to
     * be retrieved later for use by the application.
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
     * Removes saved user credentials from shared preferences, resets internal variables,
     * and triggers a login status update callback to notify the UI of the change.
     *
     * @param cb callback interface to notify about login status updates, which is invoked
     * with the updated login status when the saved user data is deleted.
     *
     * CallbackInterface cb: an interface with loginStatusUpdate method.
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
