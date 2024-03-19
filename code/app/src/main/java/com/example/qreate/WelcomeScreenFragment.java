package com.example.qreate;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

/**
 * This is the Java class for the welcome screen fragment.
 * The User can only see this screen if he is logging into the app for the first time.
 * Furthermore clicking on the update datails button takes the user to the Edit Screen menu
 * where he can update his details and customize his profile.
 * @author Akib Zaman Choudhury
 */
public class WelcomeScreenFragment extends Fragment {
    String current_activity;

    /**
     *
     * @param activity
     */
    public WelcomeScreenFragment(String activity) {
        current_activity = activity;
    }

    /**
     * Creates the view and inflates the welcome_screen layout
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.welcome_screen, container, false);
        TextView userRole = view.findViewById(R.id.welcome_screen_role);
        userRole.setText("You are now an " + current_activity);



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
                }else if (Objects.equals(current_activity, "administrator")) {
                    transaction.replace(R.id.administrator_handler_frame, editProfile);
                }

                transaction.commit();
            }
        });

        return view;

    }
}
