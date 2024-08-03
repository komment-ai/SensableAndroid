package io.sensable.client;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import io.sensable.model.UserLogin;

/**
 * Created by madine on 01/07/14.
 */
/**
 * is a custom Dialog Fragment that allows users to log in to a sensable system. It
 * has an EditText field for username and password, and a submit button that triggers
 * an listener when clicked. The listener can be set using the setSensableLoginListener
 * method, which will call the onConfirmed method of the listener when the login is
 * confirmed.
 */
public class SensableLoginFragment extends DialogFragment {

    private EditText loginUsername;
    private EditText loginPassword;
    private Button submitButton;
    private SensableLoginListener sensableLoginListener;

    /**
     * defines a method for receiving confirmed UserLogin objects.
     */
    public static interface SensableLoginListener {
        public void onConfirmed(UserLogin userLogin);
    }

    public SensableLoginFragment() {
    }

    /**
     * inflates a layout, sets the dialog title, and adds an event listener to a button.
     * 
     * @param inflater inflation tool that is used to inflate the layout from the resource
     * file.
     * 
     * 	- `inflater`: An instance of the `LayoutInflater` class, which provides a way to
     * inflate layouts from XML files into Android views.
     * 	- `view`: The View object that is being inflated and modified.
     * 	- `container`: The parent ViewGroup that will hold the newly inflated view.
     * 	- `savedInstanceState`: A Bundle object that contains any saved state information
     * about the fragment, which can be used to restore the state of the view when the
     * fragment is re-created.
     * 
     * In this function, the `inflater` instance is not modified or destroyed, as it is
     * a required parameter for the `onCreateView` method.
     * 
     * @param container parent view group that will contain the inflated layout.
     * 
     * 	- `inflater`: The `LayoutInflater` object used to inflate the layout of the view.
     * 	- `savedInstanceState`: A `Bundle` object containing any saved state from a
     * previous instance of the view.
     * 	- `container`: The parent view group that contains the view being created. It can
     * be used to access its properties and attributes, such as size, position, and visibility.
     * 
     * @param savedInstanceState saved state of the activity, which can be used to restore
     * the view and its components if the activity is recreated due to a configuration
     * change or app restart.
     * 
     * 	- `R.id.login_username_field`: An ID resource identifier for an EditText view.
     * 	- `R.id.login_password_field`: An ID resource identifier for an EditText view.
     * 	- `getActivity().getString(R.string.dialogTitleSensableLogin)`: The title of the
     * dialog shown when the user clicks on the login button.
     * 
     * @returns a View object representing the layout for the login screen.
     * 
     * 	- `View view`: This is the inflated layout for the login screen, which contains
     * several elements such as text fields for entering username and password, and a
     * button to initiate the login process.
     * 	- `getDialog().setTitle(getActivity().getString(R.string.dialogTitleSensableLogin))`:
     * This sets the title of the dialog window to "Sensable Login".
     * 	- `loginUsername = (EditText) view.findViewById(R.id.login_username_field)`: This
     * assigns a reference to an EditText field in the layout for entering the username.
     * 	- `loginPassword = (EditText) view.findViewById(R.id.login_password_field)`: This
     * assigns a reference to another EditText field in the layout for entering the password.
     * 	- `addListenerOnButton(view)`: This adds an listener to a button in the layout,
     * which will be triggered when clicked.
     * 
     * Overall, the `onCreateView` function is responsible for creating and inflating the
     * layout for the login screen, setting the title of the dialog window, and adding
     * listeners to the login fields and button.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sensable_login_layout, null);

        getDialog().setTitle(getActivity().getString(R.string.dialogTitleSensableLogin));

        loginUsername = (EditText) view.findViewById(R.id.login_username_field);
        loginPassword = (EditText) view.findViewById(R.id.login_password_field);

        addListenerOnButton(view);

        return view;
    }

    /**
     * sets a reference to an instance of `SensableLoginListener`. This listener will
     * receive events related to login functionality.
     * 
     * @param sensableLoginListener listener for authentication events, allowing the
     * method to assign and manage the listener object for further processing.
     * 
     * 	- This object's `sensableLoginListener` attribute or property is set to the
     * provided value.
     * 
     * The `sensableLoginListener` field or property is set to the specified value in
     * this method.
     */
    public void setSensableLoginListener(SensableLoginListener sensableLoginListener) {
        this.sensableLoginListener = sensableLoginListener;
    }


    /**
     * sets an event listener on a `Button` element in an Android activity, which upon
     * click will check if the login username and password fields have content and pass
     * the values to a `UserLogin` object. If both fields are empty, it displays a Toast
     * message.
     * 
     * @param view button that was clicked, and is used to obtain the Button object that
     * is being passed as an argument to the `OnClickListener`.
     * 
     * 1/ `submitButton`: The `Button` object found by calling `findViewById(R.id.login_submit_button)`.
     * 2/ `OnClickListener`: The `OnClickListener` interface implemented by a reference
     * to an anonymous inner class that contains the `onClick()` method.
     * 3/ `onClick()`: An instance method of the anonymous inner class that is called
     * when the button is clicked. It performs the following actions:
     * 		- Checks if the `loginUsername` and `loginPassword` fields have valid text content.
     * 		- Creates a new `UserLogin` object with the values of `loginUsername` and `loginPassword`.
     * 		- Calls the `onConfirmed()` method of the `sensableLoginListener` object.
     * 		- Dismisses the current `DialogFragment` instance.
     */
    public void addListenerOnButton(View view) {

        submitButton = (Button) view.findViewById(R.id.login_submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {

            /**
             * verifies if the login credentials are provided, then it passes the login information
             * to a listener and dismisses the dialog.
             * 
             * @param v view that was clicked, and is passed to the function as an argument when
             * the button is pressed.
             * 
             * 	- `v`: A `View` object that represents the button clicked to initiate the login
             * process.
             * 	- `loginUsername`: A `EditText` field containing the user's login username.
             * 	- `loginPassword`: A `EditText` field containing the user's login password.
             */
            @Override
            public void onClick(View v) {
                if (loginUsername.getText().toString().length() > 0 && loginPassword.getText().toString().length() > 0) {
                    UserLogin userLogin = new UserLogin(loginUsername.getText().toString(), loginPassword.getText().toString());
                    sensableLoginListener.onConfirmed(userLogin);
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), "Username and password required", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

}
