package com.example.qreate.attendee;

import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.example.qreate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AttendeeSignedUpEventsDetailsFragment extends Fragment {
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

        Button backButton = view.findViewById(R.id.event_details_back_button);
        backButton.setVisibility(View.INVISIBLE);
        Button signUpButton = view.findViewById(R.id.event_details_signup_button);
        signUpButton.setVisibility(View.INVISIBLE);

        // Retrieve the event ID passed from the previous fragment
        Bundle args = getArguments();
        String eventId;
        if (args != null) {
            eventId = args.getString("eventId");
        } else {
            eventId = null;
        }

        if (eventId != null) {
            db.collection("Events").document(eventId)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Extract event details from the document and update the UI
                                eventName.setText(document.getString("name"));
                                String posterPath = document.getString("poster");
                                if (posterPath != null && !posterPath.isEmpty()) {
                                    loadPosterImage(posterPath);
                                }
                                String device_id = document.getString("org_device_id");
                                db.collection("Users")
                                        .whereEqualTo("device_id", device_id)
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful() && !task1.getResult().isEmpty()) {
                                                // Assuming device_id is unique, get the first document.
                                                QueryDocumentSnapshot doc = (QueryDocumentSnapshot) task1.getResult().getDocuments().get(0);
                                                String orgName = doc.getString("name");
                                                eventOrganizer.setText("Organizer: " + orgName);
                                            }
                                        });
                                eventDescription.setText("Description: " + document.getString("description"));
                                eventLocation.setText("Location: " + document.getString("location"));
                                Timestamp dateTimestamp = document.getTimestamp("date");
                                if (dateTimestamp != null) {
                                    // Format the Timestamp as a String to include only the date part in dd-MM-yyyy format
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                    String formattedDate = dateFormat.format(dateTimestamp.toDate());
                                    eventDate.setText("Date: " + formattedDate);
                                }
                                eventTime.setText("Time: " + document.getString("timeOfEvent"));
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

    public static AttendeeSignedUpEventsDetailsFragment newInstance(String eventId) {
        AttendeeSignedUpEventsDetailsFragment fragment = new AttendeeSignedUpEventsDetailsFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    private void loadPosterImage(String posterPath) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference posterRef = storage.getReference(posterPath);

        posterRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(AttendeeSignedUpEventsDetailsFragment.this)
                        .load(uri.toString())
                        .into(poster);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Firestore", "Error getting poster image: ", e);
            }
        });

    }
}
