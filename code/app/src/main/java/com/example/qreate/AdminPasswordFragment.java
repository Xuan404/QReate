package com.example.qreate;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.qreate.administrator.AdministratorActivity;

/**
 * A DialogFragment that prompts the user to enter an administrator password.
 * Upon entering the correct password, it grants access to the AdministratorActivity,
 * otherwise, it displays a toast message indicating that the password is incorrect.
 */
public class AdminPasswordFragment extends DialogFragment {
    private EditText passwordEditText;
    private Button cancelButton;
    private Button confirmButton;

    /**
     * Default constructor for AdminPasswordFragment.
     */
    public AdminPasswordFragment() {

    }

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     * Initializes the EditText for password input and the confirm button with its click listener.
     * If the entered password is correct, transitions to the AdministratorActivity.
     * If the password is incorrect, shows a toast message.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.administrator_password, container, false);

        passwordEditText = view.findViewById(R.id.admin_login_password);
        confirmButton = view.findViewById(R.id.button_confirm);

        confirmButton.setOnClickListener(v -> {
            String password = passwordEditText.getText().toString().trim();
            if ("uFHa2gkj".equals(password)) {
                Intent intent = new Intent(getActivity(), AdministratorActivity.class);
                startActivity(intent);
                dismiss();
            } else {
                Toast.makeText(getActivity(), "Password Incorrect!", Toast.LENGTH_SHORT).show();
            }

        });

        return view;
    }
}
