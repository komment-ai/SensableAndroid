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
 * application. It provides functionality to login, logout, retrieve user settings,
 * save user information to preferences, and delete saved user information. The class
 * utilizes Retrofit library for making HTTP requests and managing user data.
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
     * Retrieves username, email, and access token from shared preferences and sets them
     * on a user object. If any of these values are missing, it returns `false`, otherwise,
     * it returns `true`.
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
     * Initiates a login process using an authentication service and notifies a callback
     * interface when completed. It sets user preferences, updates the login status, and
     * persists user data if successful, or logs an error message if not.
     *
     * @param userLogin user's login credentials to be sent to the authentication service
     * for processing and verification.
     *
     * - The object `UserLogin` has a username and a password as its main properties.
     *
     * @param cb CallbackInterface of the MainActivity, which is notified about the login
     * status update using the `loginStatusUpdate(loggedIn)` method.
     *
     * - `CallbackInterface`: This is an interface with a single method `loginStatusUpdate(loggedIn)`.
     */
    public void login(UserLogin userLogin, final MainActivity.CallbackInterface cb) {

        service.login(userLogin, new Callback<User>() {
            /**
             * Updates a local user object with username and email from a response, sets login
             * status to true, retrieves an access token if present, saves it to preferences or
             * proceeds to settings otherwise, and notifies a callback about the login status update.
             *
             * @param user authenticated user, providing access to their username, email, and
             * access token, which are then used to update the local user object and preferences.
             *
             * Decompose user into:
             * username and email.
             *
             * @param response callback response from the login request, which is not utilized
             * within the provided method implementation.
             *
             * GetContent: The content returned by the request; getRawBody: Returns an input
             * stream for reading the raw response body.
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
             * Handles failures when making a request using Retrofit. It logs an error message
             * to the console with the provided Retrofit error, indicating that the login attempt
             * has failed. The error details are included in the log output.
             *
             * @param retrofitError error that occurred during the execution of the Retrofit API
             * request.
             */
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Login callback failure" + retrofitError.toString());
            }
        });
    }

    /**
     * Retrieves user settings from a service and updates a local user object with the
     * received information. It saves the updated user details to preferences if an access
     * token is provided, indicating successful login. It also handles error cases by
     * logging error messages to the console.
     */
    public void userSettings() {

        service.settings(mUser.getUsername(), new Callback<User>() {
            /**
             * Updates local user information with data from a successful login response, sets
             * the `loggedIn` flag to `true`, and saves the user's access token if present. It
             * also logs various success messages using `Log.d`. The updated user details are
             * then saved to preferences.
             *
             * @param user authenticated user, providing access to their username, email, and
             * access token, which are then used to update local user data and preferences.
             *
             * User has username, email and access token.
             *
             * @param response result of an API request and is ignored in this method, as it is
             * not utilized for any operations.
             *
             * * The response object has no further decomposition.
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
             * Logs an error message with a Retrofit error and the tag `TAG`. It is called when
             * a login request fails. The error message includes information about the Retrofit
             * error, which can be used for debugging purposes.
             *
             * @param retrofitError exception that occurred during the Retrofit call execution
             * and is passed to the `failure` method for logging or further error handling purposes.
             */
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Login callback failure" + retrofitError.toString());
            }
        });
    }

    /**
     * Logs user information to the debug log and saves it to shared preferences,
     * specifically storing username, email, and access token.
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
     * Removes stored user data from SharedPreferences, resets `mUser`, `loggedIn`, and
     * `hasAccessToken` variables to default values, and notifies the callback interface
     * about the updated login status by calling `loginStatusUpdate(loggedIn)`.
     *
     * @param cb CallbackInterface, which is used to update the login status with the
     * callback method `loginStatusUpdate(loggedIn)`.
     *
     * Cb is an instance of `MainActivity.CallbackInterface`. It has a method named
     * `loginStatusUpdate` with one argument of boolean type.
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
