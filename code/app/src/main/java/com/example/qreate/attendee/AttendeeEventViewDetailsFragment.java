package com.example.qreate.attendee;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qreate.R;
import com.example.qreate.administrator.AdministratorEventDetailsFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AttendeeEventViewDetailsFragment extends Fragment {
    private ImageView poster;
    private TextView eventName;
    private TextView eventOrganizer;
    private TextView eventDescription;
    private TextView eventDate;
    private TextView eventTime;
    private TextView eventLocation;

    private FirebaseFirestore db;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.attendee_event_view_details, container, false);
        db = FirebaseFirestore.getInstance();
        poster = view.findViewById(R.id.event_details_poster);
        eventName = view.findViewById(R.id.event_details_name);
        eventOrganizer = view.findViewById(R.id.event_details_organizer);
        eventDescription = view.findViewById(R.id.event_details_description);
        eventDate = view.findViewById(R.id.event_details_date);
        eventTime = view.findViewById(R.id.event_details_time);
        eventLocation = view.findViewById(R.id.event_details_location);

        // Retrieve the event ID passed from the previous fragment
        Bundle args = getArguments();
        String eventId = null;
        if (args != null) {
            eventId = args.getString("eventId");
        }

        Button cancelButton = view.findViewById(R.id.event_details_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomNavigationBar();
                // Pop the current fragment off the stack to return to the previous one
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                }
            }
        });

        if (eventId != null) {
            db.collection("Events").document(eventId)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Extract event details from the document and update the UI
                                eventName.setText(document.getString("name"));
                                // eventOrganizer.setText(document.getString("organizer"));
                                eventDescription.setText(document.getString("description"));
                                Timestamp dateTimestamp = document.getTimestamp("date");
                                if (dateTimestamp != null) {
                                    // Format the Timestamp as a String to include only the date part in dd-MM-yyyy format
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                    String formattedDate = dateFormat.format(dateTimestamp.toDate());
                                    eventDate.setText(formattedDate);
                                }
                                // poster.setText(document.getString("poster"));
                                // eventTime.setText(document.getString("time"));
                                // eventLocation.setText(document.getString("location"));
                                // Ensure you have fields named accordingly in your Firestore document
                            } else {
                                Log.d("Firestore", "Error getting documents: ", task.getException());
                            }
                        } else {
                            Log.d("Firestore", "Task Failure: ", task.getException());
                        }
                    });
        }

        return view;

    }

    public static AttendeeEventViewDetailsFragment newInstance(String eventId) {
        AttendeeEventViewDetailsFragment fragment = new AttendeeEventViewDetailsFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    private void showBottomNavigationBar() {
        // Find the BottomNavigationView and set its visibility to GONE
        ((AttendeeActivity)getActivity()).showBottomNavigationBar();
    }
}
