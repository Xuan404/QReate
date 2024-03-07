package com.example.qreate;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.qreate.R;
import com.example.qreate.organizer.QRGeneratorActivity;

import java.util.Objects;

public class WelcomeScreenFragment extends Fragment {
    String current_activity;
    public WelcomeScreenFragment(String activity) {
        current_activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.welcome_screen, container, false);

        Button updateDetails = view.findViewById(R.id.welcome_screen_updatebutton);

        updateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Takes user to edit profile page
                Fragment editProfile = new EditProfileScreenFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                if (Objects.equals(current_activity, "organizer")){
                    transaction.replace(R.id.organizer_handler_frame, editProfile);
                } else if (Objects.equals(current_activity, "attendee")) {
                    transaction.replace(R.id.attendee_handler_frame, editProfile);
                }

                transaction.commit();
            }
        });

        return view;

    }
}
