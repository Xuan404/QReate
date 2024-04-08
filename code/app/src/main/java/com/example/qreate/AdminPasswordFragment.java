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

public class AdminPasswordFragment extends DialogFragment {
    private EditText passwordEditText;
    private Button cancelButton;
    private Button confirmButton;

    public AdminPasswordFragment() {

    }

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
