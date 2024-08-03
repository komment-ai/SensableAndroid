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
 * is responsible for handling user authentication and settings retrieval in an Android
 * application. It provides functions to login, logout, retrieve user settings, and
 * save the user's information to preferences. Additionally, it offers methods to
 * delete the saved user information and update the login status.
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
     * retrieves user information from shared preferences and sets it to a `User` object,
     * returning `true` if successful or `false` otherwise.
     * 
     * @returns a boolean value indicating whether the user is logged in or not.
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
     * takes a `UserLogin` object and a `CallbackInterface` object as inputs, calls the
     * service's login method to authenticate the user, and updates the user's data and
     * saves it to preferences upon success.
     * 
     * @param userLogin UserLogin object containing the login credentials of the user to
     * be authenticated.
     * 
     * 	- `userLogin`: This is an instance of the `UserLogin` class, which contains
     * information about the user attempting to log in.
     * 	+ `username`: The username of the user attempting to log in.
     * 	+ `email`: The email address of the user attempting to log in.
     * 
     * The function call is made to the `service` object's `login` method, passing in
     * `userLogin` and a callback interface `cb`. The callback interface has two methods:
     * `success` and `failure`.
     * 
     * In the `success` method, if the login attempt is successful, the `User` object's
     * properties are set to the deserialized response data, including the `username` and
     * `email`. Additionally, the `loggedIn` flag is set to `true`, and the `accessToken`
     * property is set to the `accessToken` property of the `User` object. Finally, the
     * `saveUserToPreferences()` method is called to save the user's data to preferences.
     * 
     * In the `failure` method, an error message is logged to the console if the login
     * attempt fails.
     * 
     * @param cb `CallbackInterface` that will receive updates on the login status.
     * 
     * The `CallbackInterface cb` is an interface with two methods: `loginStatusUpdate(loggedIn)`
     * and `failure(RetrofitError retrofitError)`. The `loginStatusUpdate` method is
     * invoked when the login callback is successful, and it takes a boolean parameter
     * `loggedIn` indicating whether the user is logged in or not. The `failure` method
     * is invoked when there is an error during the login process, and it takes a
     * `RetrofitError` object as its parameter.
     */
    public void login(UserLogin userLogin, final MainActivity.CallbackInterface cb) {

        service.login(userLogin, new Callback<User>() {
            /**
             * handles a successful login callback from the authentication service. It sets the
             * user's username, email, and access token (if available), updates the user's
             * preferences, and notifies the login status update to the callback interface.
             * 
             * @param user login result, containing the user's username and email address.
             * 
             * 	- `username`: The username of the user.
             * 	- `email`: The email address of the user.
             * 	- `accessToken`: An access token for the user.
             * 
             * The `mUser` object is set to the deserialized `user` object, and various attributes
             * of `mUser` are updated. Additionally, a call to `saveUserToPreferences()` is made
             * to persist the user's data to preferences. Finally, the login status is updated
             * using `cb.loginStatusUpdate(loggedIn)`.
             * 
             * @param response response from the login API, which provides the user's authentication
             * information.
             * 
             * 	- `user`: A `User` object representing the authenticated user.
             * 	- `accessToken`: The access token obtained from the authentication process, which
             * can be used for further requests.
             * 
             * The function performs the following actions:
             * 
             * 1/ Logs a message to the debug log with the username of the successfully authenticated
             * user.
             * 2/ Sets the `username` and `email` properties of the `mUser` object to those of
             * the `user` object.
             * 3/ Sets the `loggedIn` property of the `mUser` object to `true`.
             * 4/ Checks if an access token was obtained and, if so, sets the `accessToken`
             * property of the `mUser` object to the obtained value.
             * 5/ Calls the `saveUserToPreferences()` function to save the `mUser` object to user
             * preferences.
             * 6/ Updates the login status in the caller with the newly set `loggedIn` property.
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
             * is called when a login request fails, and it logs an error message to the console
             * using the `Log.e()` method.
             * 
             * @param retrofitError error that occurred during the login callback, which is then
             * logged with a message indicating the nature of the error.
             * 
             * 	- `toString()`: Returns a string representation of the error object, which can
             * be used for logging or display to the user.
             */
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Login callback failure" + retrofitError.toString());
            }
        });
    }

    /**
     * sets user's settings by calling API with given username and returns access token
     * if successful.
     */
    public void userSettings() {

        service.settings(mUser.getUsername(), new Callback<User>() {
            /**
             * handles the callback response from the login API, updates the user object with the
             * received information, and saves it to preferences if an access token is provided.
             * 
             * @param user authenticated user returned by the authentication service, providing
             * the user's username and email address, as well as an access token if available.
             * 
             * 	- `username`: The username of the user.
             * 	- `email`: The email address of the user.
             * 	- `accessToken`: The access token of the user.
             * 
             * @param response result of the login API call, providing the user's authentication
             * status and other relevant information.
             * 
             * 	- `user`: A `User` object representing the user who has successfully logged in.
             * 	- `accessToken`: The access token obtained through the login process, which can
             * be used for further authenticated API requests.
             * 
             * The function then updates the `mUser` field with the values of `user`, sets
             * `loggedIn` to `true`, and saves the user details to the preferences file if
             * `hasAccessToken` is set to `true`.
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
             * is called when a Retrofit error occurs during the login callback. It logs an error
             * message to the console using the `Log.e()` method, including the error details in
             * the message.
             * 
             * @param retrofitError error object generated by the Retrofit API call, which contains
             * information about the failure of the login callback.
             * 
             * 	- `retrofitError.message`: String value representing the error message in
             * human-readable form.
             * 	- `retrofitError.statusCode`: Integer value representing the HTTP status code
             * associated with the error, if applicable.
             */
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e(TAG, "Login callback failure" + retrofitError.toString());
            }
        });
    }

    /**
     * saves user information to shared preferences, including username, email, and access
     * token.
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
     * removes saved user information from SharedPreferences and updates login status,
     * access token, and user data to empty values.
     * 
     * @param cb CallbackInterface, which is an interface that provides a method for
     * updating the login status of the user after the save operation is completed.
     * 
     * 	- `CallbackInterface cb`: This is an interface that provides methods for updating
     * the login status of the user. It has one method, `loginStatusUpdate`, which takes
     * a boolean parameter indicating whether the user is logged in or not.
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
