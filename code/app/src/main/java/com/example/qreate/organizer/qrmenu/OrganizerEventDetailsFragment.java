package com.example.qreate.organizer.qrmenu;

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
import com.example.qreate.attendee.AttendeeSignedUpEventsDetailsFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class OrganizerEventDetailsFragment extends Fragment {
    private ImageView poster;
    private TextView eventName;
    private TextView eventOrganizer;
    private TextView eventDescription;
    private TextView eventDate;
    private TextView eventTime;
    private TextView eventLocation;
    private Button backButton;
    private Button deleteButton;
    private FirebaseFirestore db;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attendee_event_view_details, container, false);
        db = FirebaseFirestore.getInstance();
        poster = view.findViewById(R.id.event_details_poster);
        eventName = view.findViewById(R.id.event_details_name);
        eventOrganizer = view.findViewById(R.id.event_details_organizer);
        eventDescription = view.findViewById(R.id.event_details_description);
        eventDate = view.findViewById(R.id.event_details_date);
        eventTime = view.findViewById(R.id.event_details_time);
        eventLocation = view.findViewById(R.id.event_details_location);

        backButton = view.findViewById(R.id.event_details_back_button);
        deleteButton = view.findViewById(R.id.event_details_delete_button);
        deleteButton.setVisibility(View.VISIBLE);
        Button signUpButton = view.findViewById(R.id.event_details_signup_button);
        signUpButton.setVisibility(View.INVISIBLE);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pop the current fragment off the stack to return to the previous one
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                }
            }
        });

        // Retrieve the event ID passed from the previous fragment
        Bundle args = getArguments();
        String eventId;
        if (args != null) {
            eventId = args.getString("eventId");
        } else {
            eventId = null;
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // delete event reference from each attendee's signup_event_list
                removeEventFromAttendee(eventId);

                // delete event reference from organizer's event_list


                // delete event document from event collection

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
                                // poster.setText(document.getString("poster"));
                                // eventTime.setText(document.getString("time"));
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


    public static OrganizerEventDetailsFragment newInstance(String eventId) {
        OrganizerEventDetailsFragment fragment = new OrganizerEventDetailsFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    private void removeEventFromAttendee(String eventId) {
        DocumentReference eventRef = db.collection("Events").document(eventId);
        db.collection("Attendees")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            List<DocumentReference> signUpEventList = (List <DocumentReference>) document.get("signup_event_list");
                            if (signUpEventList != null && signUpEventList.contains(eventRef)) {
                                db.collection("Attendees")
                                        .document(document.getId())
                                        .update("signup_event_list", eventRef)
                                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event reference successfully removed from signup_event_list"))
                                        .addOnFailureListener(e -> Log.w("Firestore", "Error removing event reference from signup_event_list", e));
                            }
                        }
                    } else {
                        Log.w("Firestore", "Error querying documents: ", task.getException());
                    }

                });
    }


}
