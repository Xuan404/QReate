package com.example.qreate.attendee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qreate.R;

/**
 * The AttendeeHomePage fragment serves as the main dashboard for attendees upon successful login.
 * It presents the welcome screen layout, which typically includes quick access to events,
 * notifications or scanning QR code.
 *
 *
 * @author Shraddha Mehta
 */

public class AttendeeHomePage extends Fragment {

    /**
     * This method inflates the welcome screen layout for the attendee's home page,
     * which serves as the landing page after an attendee logs in or navigates back to the home screen.
     *
     * @param inflater LayoutInflater: The LayoutInflater object that can be used to inflate
     *                 any views in the fragment.
     * @param container ViewGroup: If non-null, this is the parent view that the fragment's
     *                 UI should be attached to.
     * @param savedInstanceState Bundle: If non-null, this fragment is being re-constructed
     *                 from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.welcome_screen, container, false);
    }

}
