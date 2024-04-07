package com.example.qreate.administrator;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qreate.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdministratorEventDetailsFragment extends Fragment {
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
        View view = inflater.inflate(R.layout.administrator_event_details_page, container, false);
        db = FirebaseFirestore.getInstance();
        eventName = view.findViewById(R.id.event_details_event_name);
        eventOrganizer = view.findViewById(R.id.event_details_event_organizer);
        eventDescription = view.findViewById(R.id.event_details_event_description);
        eventDate = view.findViewById(R.id.event_details_event_date);
        eventTime = view.findViewById(R.id.event_details_event_time);
        eventLocation = view.findViewById(R.id.event_details_event_location);

        // Retrieve the event ID passed from the previous fragment
        Bundle args = getArguments();
        String eventId = null;
        if (args != null) {
            eventId = args.getString("eventId");
        }

        // Query Firestore for the event details using the event ID
        if (eventId != null) {
            db.collection("Events").document(eventId)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                // Extract event details from the document and update the UI
                                eventName.setText(document.getString("name"));

                                String device_id = document.getString("org_device_id");
                                db.collection("Users")
                                        .whereEqualTo("device_id", device_id)
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful() && !task1.getResult().isEmpty()) {
                                                        // Assuming device_id is unique, get the first document.
                                                        QueryDocumentSnapshot doc = (QueryDocumentSnapshot) task1.getResult().getDocuments().get(0);
                                                        String orgName = doc.getString("name");
                                                        eventOrganizer.setText(orgName);
                                                    }
                                                });
                                eventDescription.setText(document.getString("description"));
                                Timestamp dateTimestamp = document.getTimestamp("date");
                                if (dateTimestamp != null) {
                                    // Format the Timestamp as a String to include only the date part in dd-MM-yyyy format
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                    String formattedDate = dateFormat.format(dateTimestamp.toDate());
                                    eventDate.setText(formattedDate);
                                }
                                eventTime.setText(document.getString("timeOfEvent"));
                                // eventLocation.setText(document.getString("location"));
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

    public static AdministratorEventDetailsFragment newInstance(String eventId) {
        AdministratorEventDetailsFragment fragment = new AdministratorEventDetailsFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }
}


