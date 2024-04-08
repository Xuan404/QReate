package com.example.qreate.attendee;

import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.qreate.R;
import com.example.qreate.administrator.AdministratorEventDetailsFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Fragment for displaying the detailed view of an event to an attendee. It includes
 * functionality for viewing event details and signing up for the event.
 */
public class AttendeeEventViewDetailsFragment extends Fragment {
    private ImageView poster;
    private TextView eventName;
    private TextView eventOrganizer;
    private TextView eventDescription;
    private TextView eventDate;
    private TextView eventTime;
    private TextView eventLocation;

    private FirebaseFirestore db;

    /**
     * Inflates the layout for the event details view, initializes UI components, and sets up event handlers
     * for interaction elements like the back and sign-up buttons.
     *
     * @param inflater           The LayoutInflater object to inflate views in the fragment.
     * @param container          If non-null, this is the parent view to which the fragment's UI is attached.
     * @param savedInstanceState If non-null, the fragment is being re-constructed from a previous saved state.
     * @return view
     */
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
        String eventId;
        if (args != null) {
            eventId = args.getString("eventId");
        } else {
            eventId = null;
        }

        Button backButton = view.findViewById(R.id.event_details_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomNavigationBar();
                // Pop the current fragment off the stack to return to the previous one
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                }
            }
        });

        Button signUpButton = view.findViewById(R.id.event_details_signup_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String device_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Step 1: Find the user document by device_id
                db.collection("Users").whereEqualTo("device_id", device_id).limit(1).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);

                        // Step 2: Find the corresponding attendee document
                        db.collection("Attendees").whereEqualTo("user_document_id", userDoc.getReference()).limit(1).get().addOnSuccessListener(attendees -> {
                            if (!attendees.isEmpty()) {
                                DocumentSnapshot attendeeDoc = attendees.getDocuments().get(0);
                                DocumentReference attendeeRef = attendeeDoc.getReference();

                                // Step 3: Update the event document
                                DocumentReference eventRef = db.collection("Events").document(eventId);
                                eventRef.get().addOnSuccessListener(eventDoc -> {
                                    if (eventDoc.exists()) {
                                        List<Map<String, Object>> signedupAttendeesList = (List<Map<String, Object>>) eventDoc.get("signedup_attendees");
                                        boolean alreadySignedUp = false;
                                        String signupLimitStr = eventDoc.getString("signup_limit");
                                        Long sign_up_limit = null;

                                        if (signupLimitStr != null && !signupLimitStr.isEmpty()) {
                                            try {
                                                sign_up_limit = Long.parseLong(signupLimitStr);
                                            } catch (NumberFormatException e) {
                                                // Handle the case where signup_limit is not a valid number
                                                Log.e("SignUpEvent", "signup_limit is not a valid number", e);
                                                sign_up_limit = null; // Ensures no restriction if the string was not numeric
                                            }
                                        }

                                        if (signedupAttendeesList != null) {
                                            for (Map<String, Object> attendee : signedupAttendeesList) {
                                                DocumentReference signedUpAttendeeRef = (DocumentReference) attendee.get("attendeeRef");
                                                if (signedUpAttendeeRef.getId().equals(attendeeRef.getId())) {
                                                    alreadySignedUp = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (sign_up_limit == null) {
                                            // Assuming null means no limit
                                            sign_up_limit = Long.MAX_VALUE; // This effectively means no limit
                                        }
                                        if (alreadySignedUp) {
                                            if (isAdded()) { // Check if the fragment is still added
                                                Toast.makeText(getContext(), "You have already signed up for this event!", Toast.LENGTH_SHORT).show();
                                            }
                                        } else if (sign_up_limit != null && (signedupAttendeesList != null && signedupAttendeesList.size() >= sign_up_limit) || sign_up_limit == 0) {
                                            // Check if the sign-up limit has been reached
                                            if (isAdded()) {
                                                Toast.makeText(getContext(), "The sign-up limit for this event has been reached.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            if (signedupAttendeesList == null) {
                                                signedupAttendeesList = new ArrayList<>();
                                            }



                                            Map<String, Object> newEntry = new HashMap<>();
                                            newEntry.put("attendeeRef", attendeeDoc.getReference());
                                            newEntry.put("checkInCount", 0);

                                            signedupAttendeesList.add(newEntry);
                                            incrementSignupCount(eventId);

                                            eventRef.update("signedup_attendees", signedupAttendeesList)
                                                    .addOnSuccessListener(aVoid -> {
                                                        if (isAdded()) {
                                                            Toast.makeText(getContext(), "You are signed up for the event!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(e -> Log.e("SignUpEvent", "Error updating document", e));

                                            attendeeRef.get().addOnSuccessListener(attendeeDocument -> {
                                                List<DocumentReference> signedUpEventsList;
                                                Object signedUpEventsObj = attendeeDocument.get("signup_event_list");
                                                if (signedUpEventsObj instanceof List<?>) {
                                                    signedUpEventsList = (List<DocumentReference>) signedUpEventsObj;
                                                } else {
                                                    signedUpEventsList = new ArrayList<>();
                                                }

                                                // Add the current event reference to the list, if it's not already there
                                                if (!signedUpEventsList.contains(eventRef)) {
                                                    signedUpEventsList.add(eventRef);
                                                    attendeeRef.update("signup_event_list", signedUpEventsList)
                                                            .addOnSuccessListener(aVoid -> Log.d("UpdateAttendee", "Attendee signed_up_events updated successfully"))
                                                            .addOnFailureListener(e -> Log.e("UpdateAttendee", "Error updating attendee document", e));
                                                }
                                            }).addOnFailureListener(e -> {
                                                if (isAdded()) { // Ensure fragment is still attached
                                                    Toast.makeText(getContext(), "Error retrieving attendee document", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                });


                            }
                        });
                    }
                }).addOnFailureListener(e -> {
                    if (isAdded()) { // Check if the fragment is still added
                        Toast.makeText(getContext(), "Error finding user or attendee", Toast.LENGTH_SHORT).show();
                    }
                });
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

    /**
     * Factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param eventId The unique ID of the event to display in detail.
     * @return A new instance of fragment AttendeeEventViewDetailsFragment.
     */
    public static AttendeeEventViewDetailsFragment newInstance(String eventId) {
        AttendeeEventViewDetailsFragment fragment = new AttendeeEventViewDetailsFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Shows the bottom navigation bar, typically called when navigating away from the details view.
     */
    private void showBottomNavigationBar() {
        // Find the BottomNavigationView and set its visibility to GONE
        ((AttendeeActivity)getActivity()).showBottomNavigationBar();
    }

    /**
     * Increments the signup count for the event in the Firestore database.
     *
     * @param eventId The unique ID of the event for which the signup count is incremented.
     */
    private void incrementSignupCount(String eventId) {
        final DocumentReference eventRef = db.collection("Events").document(eventId);
        db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot eventSnapshot = transaction.get(eventRef);
                    Long currentCount = eventSnapshot.getLong("signup_count");
                    if (currentCount == null) currentCount = 0L;
                    transaction.update(eventRef, "signup_count", currentCount + 1);
                    return null;
                }).addOnSuccessListener(aVoid -> Log.d("Transaction", "Transaction success! signup_count incremented"))
                .addOnFailureListener(e -> Log.e("Transaction", "Transaction failure.", e));
    }

    /**
     * Loads the poster image for the event from Firebase Storage and displays it in the UI.
     *
     * @param posterPath The storage path of the poster image in Firebase Storage.
     */
    private void loadPosterImage(String posterPath) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference posterRef = storage.getReference(posterPath);

        posterRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(AttendeeEventViewDetailsFragment.this)
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
