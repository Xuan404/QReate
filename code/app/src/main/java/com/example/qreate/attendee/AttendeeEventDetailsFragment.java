package com.example.qreate.attendee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qreate.R;

/**
 * A Fragment representing the event details page for attendees.
 * This fragment is used to display the details of an event to the attendee, including
 * information such as event title, description, date, time, and location.
 * Attendees can view  details about the event they are interested in or planning to attend.
 * This fragment is accessed from the attendee's main navigation, typically through selecting an event
 * in a list.
 *
 * @author Akib Zaman Choudhury
 */

public class AttendeeEventDetailsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.attendee_event_menu_screen, container, false);
        return view;
    }
}
