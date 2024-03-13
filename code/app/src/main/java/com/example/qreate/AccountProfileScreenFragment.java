package com.example.qreate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.qreate.administrator.AdministratorActivity;
import com.example.qreate.attendee.AttendeeActivity;
import com.example.qreate.organizer.OrganizerActivity;

import java.util.Objects;

public class AccountProfileScreenFragment extends Fragment {

    String current_activity;

    /**
     *
     * @param activity
     */
    public AccountProfileScreenFragment(String activity) {
        current_activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.edit_profile_info, container, false);
        //This one goes to the next screen, wherease the class AccountProfileScreenFragment destroys itself and returns to previous fragment
        // so two classes, same fragment layout but different behaviour
        // on pressing confirm, validates user details and returns

        Button confirmDataButton = view.findViewById(R.id.edit_profile_confirm_button);

        confirmDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //authenticates and then destroys itself
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getSupportFragmentManager() in an Activity
                fragmentManager.popBackStack();
            }
        });

        return view;
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {

            if (Objects.equals(current_activity, "organizer")){
                ((OrganizerActivity)getActivity()).showBottomNavigationBar();
            } else if (Objects.equals(current_activity, "attendee")) {
                //((AttendeeActivity)getActivity()).showBottomNavigationBar();
            }else if (Objects.equals(current_activity, "administrator")) {
                //((AdministratorActivity)getActivity()).showBottomNavigationBar();
            }
        }
    }
}
