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
 * Is a custom Dialog Fragment that allows users to log in to a sensable system. It
 * has an EditText field for username and password, and a submit button that triggers
 * the login process when clicked. The fragment also provides an interface for receiving
 * confirmed UserLogin objects.
 */
public class SensableLoginFragment extends DialogFragment {

    private EditText loginUsername;
    private EditText loginPassword;
    private Button submitButton;
    private SensableLoginListener sensableLoginListener;

    /**
     * Defines a method to receive confirmed UserLogin objects from login operations.
     */
    public static interface SensableLoginListener {
        public void onConfirmed(UserLogin userLogin);
    }

    public SensableLoginFragment() {
    }

    /**
     * Inflates a layout, sets a title to the dialog, finds and initializes UI components
     * such as EditTexts for username and password fields, and adds an event listener to
     * a button. It returns the inflated view.
     *
     * @param inflater LayoutInflater object that is used to inflate or load the layout
     * resource file into a View object.
     *
     * Inflate an instance of type layout from the given resource. It uses null as a root
     * view, and attach to the parent ViewGroup if flag is specified. The inflater is
     * used for inflating layouts and views.
     *
     * @param container 2D ViewGroup that this view is to be added to, and it is used by
     * LayoutInflater to set the parent of the inflated view.
     *
     * Is null or zero-sized if the parent view group is an invalid type.
     * Has layout parameters set to wrap its content.
     * Is passed as part of the inflate method's arguments.
     *
     * @param savedInstanceState bundle of data previously saved by onSaveInstanceState
     * and is not used in this function.
     *
     * Bundle object containing saved key-value pairs. Bundle stores data that can be
     * saved across process death.
     *
     * @returns a view of the sensable login layout.
     *
     * The returned object is a View, which represents a UI component in Android's layout
     * hierarchy. The View is inflated from the sensable_login_layout resource and has
     * two child views: an EditText for login username and another EditText for login password.
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
     * Establishes a connection between an object and its login listener, storing the
     * listener in the `sensableLoginListener` field for potential future use. This enables
     * the object to notify the listener when certain login-related events occur. The
     * function sets the listener without any validation or processing.
     *
     * @param sensableLoginListener listener that is assigned to handle login-related events.
     */
    public void setSensableLoginListener(SensableLoginListener sensableLoginListener) {
        this.sensableLoginListener = sensableLoginListener;
    }


    /**
     * Sets a click listener for a submit button, verifying if login credentials are
     * provided. If valid, it creates a `UserLogin` object and passes it to a listener,
     * then dismisses the dialog. If invalid, it displays a toast message requiring
     * username and password.
     *
     * @param view view that was clicked, which is then used to find the submit button
     * within it by calling `findViewById(R.id.login_submit_button)`.
     *
     * View represents a base class for widgets and other graphical user interface
     * components. It implements the Component interface to provide methods for handling
     * events such as clicks, touches, and other gestures.
     */
    public void addListenerOnButton(View view) {

        submitButton = (Button) view.findViewById(R.id.login_submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Triggers when a view is clicked. It checks if username and password are provided,
             * then creates a `UserLogin` object with the input data, notifies the listener, and
             * dismisses the dialog if valid; otherwise, it displays an error message.
             *
             * @param v View that was clicked, triggering the execution of the `onClick` method.
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
