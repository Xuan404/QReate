package com.example.qreate.organizer.qrmenu;

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
import com.example.qreate.administrator.AdministratorEventDetailsFragment;
import com.example.qreate.attendee.AttendeeSignedUpEventsDetailsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;


/**
 * Fragment that allows the user to delete and see event details
 *
 */
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
    private EventDeletionListener eventDeletionListener;


    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */
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

        // Retrieve the event ID passed from the previous fragment
        Bundle args = getArguments();
        String eventId;
        if (args != null) {
            eventId = args.getString("eventId");
        } else {
            eventId = null;
        }

        displayEventDetails(eventId);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pop the current fragment off the stack to return to the previous one
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete event reference from each attendee's signup_event_list, delete event reference from organizer's event_list & delete event document from event collection
                deleteEvent(eventId);
                notifyEventDeletion();
                Log.d("Interface", "notifyEventDeletion function called");

                // pop back fragment
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {

                    getParentFragmentManager().popBackStack();
                }
            }
        });

        return view;
    }


    /**
     * Static factory method to create a new instance of this fragment.
     *
     * @param eventId The ID of the event to display.
     * @return A new instance of OrganizerEventDetailsFragment.
     */
    public static OrganizerEventDetailsFragment newInstance(String eventId) {
        OrganizerEventDetailsFragment fragment = new OrganizerEventDetailsFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Displays event details.
     *
     * @param eventId The document ID of the event to display.
     */
    private void displayEventDetails(String eventId) {
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
    }

    /**
     * Deletes the event.
     *
     * @param eventId The document ID of the event to delete.
     */
    private void deleteEvent(String eventId) {
        // delete event reference from each attendee's signup_event_list
        removeEventFromAttendee(eventId, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // after removing from attendees, delete event reference from organizer's event_list
                removeEventFromOrganizer(eventId, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // after removing from attendee & organizer, delete event document from event collection
                        removeEvent(eventId, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //now that all deletion steps are complete, call notifyEventDeletion
                                notifyEventDeletion();
                            }
                        });
                    }
                });
            }
        });
    }


    /**
     * Removes the event from the signup_event_list of all attendees who signed up for it.
     *
     * @param eventId           The document ID of the event to remove from attendees' signup_event_list.
     * @param completionListener A listener to be called when the removal process is complete.
     */
    private void removeEventFromAttendee(String eventId, OnCompleteListener<Void> completionListener) {
        DocumentReference eventRef = db.collection("Events").document(eventId);
        db.collection("Attendees")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Create a list to hold all the update tasks
                        List<Task<Void>> updateTasks = new ArrayList<>();

                        for (DocumentSnapshot document : task.getResult()) {
                            List<DocumentReference> signUpEventList = (List <DocumentReference>) document.get("signup_event_list");
                            if (signUpEventList != null && signUpEventList.contains(eventRef)) {
                                // Add each update operation's Task to the list
                                Task<Void> updateTask = db.collection("Attendees")
                                        .document(document.getId())
                                        .update("signup_event_list", FieldValue.arrayRemove(eventRef));
                                updateTasks.add(updateTask);
                            }
                        }
                        // Wait for all update tasks to complete
                        Tasks.whenAllComplete(updateTasks)
                                .addOnCompleteListener(tasks -> {
                                    // All update operations are complete at this point
                                    Task<Void> completionTask = Tasks.forResult(null);
                                    completionListener.onComplete(completionTask);
                                });

                    } else {
                        Log.w("Firestore", "Error querying documents: ", task.getException());
                        // Signal completion with failure if the initial query failed
                        Task<Void> failureTask = Tasks.forException(task.getException());
                        completionListener.onComplete(failureTask);
                    }
                });
    }

    /**
     * Removes the event from the organizer's event_list.
     *
     * @param eventId           The ID of the event to remove from the organizer's event_list.
     * @param completionListener A listener to be called when the removal process is complete.
     */
    private void removeEventFromOrganizer(String eventId, OnCompleteListener<Void> completionListener) {
        DocumentReference eventRef = db.collection("Events").document(eventId);
       eventRef
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // retrieving the device_id of the organizer
                        String org_device_id = documentSnapshot.getString("org_device_id");
                        Log.d("Firestore", "Organizer Device ID: " + org_device_id);

                        // finding the event organizer of the event using the device_id and deleting the event from their event_list
                        db.collection("Organizers")
                                .whereEqualTo("device_id", org_device_id)
                                .limit(1)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        DocumentSnapshot organizerSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                        DocumentReference organizerRef = organizerSnapshot.getReference();
                                        Task<Void> updateTask = organizerRef.update("events_list", FieldValue.arrayRemove(eventRef));
                                        updateTask.addOnSuccessListener(aVoid -> {
                                            Log.d("Firestore", "Event successfully removed from organizer's event_list");
                                            // Notify the completion listener only after the update is successful
                                            completionListener.onComplete(null);
                                        }).addOnFailureListener(e -> {
                                            Log.w("Firestore", "Error updating organizer", e);
                                            // Notify the completion listener in case of failure as well
                                            completionListener.onComplete(null);
                                        });

                                    } else {
                                        Log.d("Firestore", "Organizer with given device ID not found");
                                        // If no organizer is found, consider it as operation completed
                                        completionListener.onComplete(null);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("Firestore", "Error finding organizer", e);
                                    // If there's an error fetching the organizer, notify completion listener
                                    completionListener.onComplete(null);
                                });
                    } else {
                        Log.d("Firestore", "Event document does not exist");
                        // If the event document does not exist, consider it as operation completed
                        completionListener.onComplete(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error getting document", e);
                    // If there's an error fetching the event document, notify completion listener
                    completionListener.onComplete(null);
                });
    }

    /**
     * Removes the event document from the Firestore database.
     *
     * @param eventId           The ID of the event document to be deleted.
     * @param completionListener A listener to be called when the deletion process is complete.
     */
    private void removeEvent(String eventId, OnCompleteListener<Void> completionListener) {
        db.collection("Events").document(eventId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // If the deletion is successful, invoke the completion listener with success
                            completionListener.onComplete(task);
                        }
                    }
                });
    }


    // Interface for callback
    /**
     * Interface for handling event deletion callbacks.
     */
    public interface EventDeletionListener {
        /**
         * Called when an event is successfully deleted.
         */
        void onEventDeleted();
    }

    // Method to set the listener
    /**
     * Sets the listener for event deletion callbacks.
     *
     * @param eventDeletionListener The listener to be set.
     */
    public void setEventDeletionListener(EventDeletionListener eventDeletionListener) {
        this.eventDeletionListener = eventDeletionListener;
        Log.d("Interface", "EventDeletionListener has been set in OrganizerEventDetailsFragment");
    }

    // Invoke this method when the event is successfully deleted
    /**
     * Notifies the registered listener that the event has been successfully deleted.
     * This method should be invoked when the event deletion process is complete.
     */
    private void notifyEventDeletion() {
        if (eventDeletionListener != null) {
            eventDeletionListener.onEventDeleted();
            Log.d("Interface", "EventDeletionListener (Activity) calls onEventDeleted function overidden in the activity");
        }
    }

    /**
     * Loads the poster image from Firebase Storage and displays it using Glide.
     *
     * @param posterPath The path of the poster image in Firebase Storage.
     */
    private void loadPosterImage(String posterPath) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference posterRef = storage.getReference(posterPath);

        posterRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(OrganizerEventDetailsFragment.this)
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
