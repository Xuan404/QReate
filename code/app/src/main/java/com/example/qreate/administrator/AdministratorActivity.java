package com.example.qreate.administrator;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.qreate.EditProfileScreenFragment;
import com.example.qreate.R;
import com.example.qreate.HomeScreenFragment;
import com.example.qreate.WelcomeScreenFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import android.widget.Toast;

/**
 * The following class is responsible for all activities related to the Administrator
 *
 * Outstanding Issue: AdministratorActivityTest does not PASS unless line 48: authenticateUser(this); is commented.
 */
public class AdministratorActivity extends AppCompatActivity implements EditProfileScreenFragment.OnFragmentInteractionListener{
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;
    private String selectedEventId;
    private String selectedProfileId;
    private String selectedImageId;
    private String selectedImageType;
    private String profileDeviceId;
    private String attendeeId;
    private String organizerId;


    /**
     * Creates the view and sets activity to the administrator_handler layout
     * Authenticates user
     * set up the navigation bar
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrator_handler);

        db = FirebaseFirestore.getInstance();

        bottomNavigationView = findViewById(R.id.administrator_handler_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.defaultNavPlaceholder);

        authenticateUser(this);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if ((itemId == R.id.events_icon) || (itemId == R.id.profiles_icon) || (itemId == R.id.images_icon)) {
                    selectedFragment = new AdministratorDashboardFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.administrator_handler_frame, selectedFragment).commitNow();

                    if (selectedFragment instanceof AdministratorDashboardFragment) {
                        if (itemId == R.id.events_icon) {
                            ((AdministratorDashboardFragment) selectedFragment).loadEvents();
                        } else if (itemId == R.id.profiles_icon) {
                            ((AdministratorDashboardFragment) selectedFragment).loadProfiles();
                        } else if (itemId == R.id.images_icon) {
                            ((AdministratorDashboardFragment) selectedFragment).loadImages();
                        }
                    }
                }
                return true;
            }
        });
    }


    public void hideMainBottomNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.administrator_handler_navigation_bar);
        navBar.setVisibility(View.INVISIBLE); // Make the bottom navigation bar disappear
    }

    public void showMainBottomNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.administrator_handler_navigation_bar);
        navBar.setVisibility(View.VISIBLE); // Make the bottom navigation bar reappear
    }

    public void showDetailsNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.administrator_view_details_navigation_bar);
        navBar.setVisibility(View.VISIBLE); // Make the bottom navigation bar reappear
        navBar.setSelectedItemId(R.id.defaultNavPlaceholder);
        setupDetailsNavigationBar();
    }

    public void hideDetailsNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.administrator_view_details_navigation_bar);
        navBar.setVisibility(View.INVISIBLE); // Make the bottom navigation bar reappear
    }

    public void showDeleteNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.administrator_delete_navigation_bar);
        navBar.setVisibility(View.VISIBLE); // Make the bottom navigation bar reappear
        navBar.setSelectedItemId(R.id.defaultNavPlaceholder);
        setupDeleteNavigationBar();
    }

    public void hideDeleteNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.administrator_delete_navigation_bar);
        navBar.setVisibility(View.INVISIBLE); // Make the bottom navigation bar reappear
    }

    public void setupDeleteNavigationBar(){
        BottomNavigationView deleteNavBar = findViewById(R.id.administrator_delete_navigation_bar);
        BottomNavigationView navBar = findViewById(R.id.administrator_handler_navigation_bar);
        int navBarItemId = navBar.getSelectedItemId();

        deleteNavBar.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                AdministratorDashboardFragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.cancel_icon_two) {
                    hideDeleteNavigationBar();
                    showMainBottomNavigationBar();
                    getSupportFragmentManager().popBackStackImmediate();

                    if ((navBarItemId == R.id.events_icon) || (navBarItemId == R.id.profiles_icon) || (navBarItemId == R.id.images_icon)) {
                        selectedFragment = new AdministratorDashboardFragment();
                    }
                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.administrator_handler_frame, selectedFragment).commitNow();

                        if (selectedFragment instanceof AdministratorDashboardFragment) {
                            if (navBarItemId == R.id.events_icon) {
                                selectedFragment.loadEvents();
                            } else if (navBarItemId == R.id.profiles_icon) {
                                selectedFragment.loadProfiles();
                            } else if (navBarItemId == R.id.images_icon) {
                                selectedFragment.loadImages();
                            }
                        }
                    }
                }
                else if (itemId == R.id.delete_icon) {
                    if (navBarItemId == R.id.events_icon && selectedEventId != null) {
                        deleteEvent(selectedEventId);
                    }
                    if ((navBarItemId == R.id.profiles_icon) && (selectedProfileId != null)) {
                        deleteProfile(selectedProfileId);
                    }
                    if ((navBarItemId==R.id.images_icon) && (selectedImageId != null)) {
                        deleteImage(selectedImageId);
                    }
                    // DO NOT UNCOMMENT THIS. DELETION OF PROFILES AND IMAGES WONT WORK TEMPORARILY BUT NEED TO CREATE NEW FUNCTIONS FOR IT SO DONT TRY THAT
//                    hideDeleteNavigationBar();
//                    showMainBottomNavigationBar();
//                    getSupportFragmentManager().popBackStackImmediate();
//
//                    if ((navBarItemId == R.id.events_icon) || (navBarItemId == R.id.profiles_icon) || (navBarItemId == R.id.images_icon)) {
//                        selectedFragment = new AdministratorDashboardFragment();
//                    }
//                    if (selectedFragment != null) {
//                        getSupportFragmentManager().beginTransaction().replace(R.id.administrator_handler_frame, selectedFragment).commitNow();
//
//                        if (selectedFragment instanceof AdministratorDashboardFragment) {
//                            if (navBarItemId == R.id.events_icon) {
//                                selectedFragment.loadEvents();
//                            } else if (navBarItemId == R.id.profiles_icon) {
//                                selectedFragment.loadProfiles();
//                            } else if (navBarItemId == R.id.images_icon) {
//                                selectedFragment.loadImages();
//                            }
//                        }
//                    }



                }
                return true;
            }
        });

    }


    private void deleteEvent(String eventId) {
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
                                //now that all deletion steps are complete, switch back to listview
                                hideDeleteNavigationBar();
                                showMainBottomNavigationBar();
                                getSupportFragmentManager().popBackStackImmediate();
                                AdministratorDashboardFragment selectedFragment = new AdministratorDashboardFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.administrator_handler_frame, selectedFragment).commitNow();
                                selectedFragment.loadEvents();

                            }
                        });
                    }
                });
            }
        });
    }

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


    private void deleteProfile(String profileId) {
        String device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        checkIfOwnProfile(profileId, device_id, new ProfileDeletionCallback() {
            @Override
            public void onOwnProfileChecked(boolean isOwnProfile) {
                if (!isOwnProfile) {
                    removeAttendeeFromEvents(profileDeviceId, new ProfileDeletionCallback() {
                        @Override
                        public void onAttendeeEventsRemoved(String attendeeId) {
                            deleteAttendee(attendeeId, new ProfileDeletionCallback() {
                                @Override
                                public void onAttendeeDeleted() {
                                    removeEventsForOrganizer(profileDeviceId, new ProfileDeletionCallback() {
                                        @Override
                                        public void onOrganizerEventsRemoved(String organizerId) {
                                            deleteOrganizer(organizerId, new ProfileDeletionCallback() {
                                                @Override
                                                public void onOrganizerDeleted() {
                                                    deleteUser(profileId, new ProfileDeletionCallback() {
                                                        @Override
                                                        public void onUserDeleted() {
                                                            hideDeleteNavigationBar();
                                                            showMainBottomNavigationBar();
                                                            getSupportFragmentManager().popBackStackImmediate();
                                                            AdministratorDashboardFragment selectedFragment = new AdministratorDashboardFragment();
                                                            getSupportFragmentManager().beginTransaction().replace(R.id.administrator_handler_frame, selectedFragment).commitNow();
                                                            selectedFragment.loadProfiles();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {
                    Log.d("deleteProfile", "Admin cannot delete his own profile");
                }
            }
        });
    }

    private interface ProfileDeletionCallback {
        default void onOwnProfileChecked(boolean isOwnProfile) {}
        default void onAttendeeEventsRemoved(String attendeeId) {}
        default void onAttendeeDeleted() {}
        default void onOrganizerEventsRemoved(String organizerId) {}
        default void onOrganizerDeleted() {}
        default void onUserDeleted() {}
    }



    private void checkIfOwnProfile(String profileId, String device_id, ProfileDeletionCallback callback) {
        db.collection("Users").document(profileId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                profileDeviceId = document.getString("device_id");
                                Log.d("Firestore", "Device ID of the profile has been retrieved: " + profileDeviceId);
                                // Invoke the callback, passing whether the profile device ID matches the admin's device ID
                                if (profileDeviceId.equals(device_id)) {
                                    Toast.makeText(AdministratorActivity.this,"Admin cannot delete his own profile", Toast.LENGTH_SHORT).show();
                                }
                                callback.onOwnProfileChecked(profileDeviceId.equals(device_id));
                            } else {
                                Log.d("Firestore", "Profile document does not exist");
                                // If document does not exist, invoke the callback with false to continue deletion
                                callback.onOwnProfileChecked(false);
                            }
                        } else {
                            Log.d("Firestore", "Task was unsuccessful ", task.getException());
                            // Handle the error scenario, you may want to stop deletion or retry
                        }
                    }
                });
    }

    private void removeAttendeeFromEvents(String profileDeviceId, ProfileDeletionCallback callback) {
        Log.d("Firestore", "Starting to remove attendee from events for device_id: " + profileDeviceId);

        db.collection("Attendees").whereEqualTo("device_id", profileDeviceId)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot attendeeSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String attendeeId = attendeeSnapshot.getId();
                        Log.d("Firestore", "Attendee document acquired with ID: " + attendeeId);

                        db.collection("Events")
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        Log.d("Firestore", "Event Documents Retrieved");

                                        if (querySnapshot != null) {
                                            for (DocumentSnapshot document : querySnapshot) {
                                                Log.d("Firestore", "Processing Event Document: " + document.getId());
                                                DocumentReference eventRef = document.getReference();
                                                eventRef.get().addOnSuccessListener(eventDocumentSnapshot -> {
                                                    List<Map<String, Object>> signedUpAttendees = (List<Map<String, Object>>) eventDocumentSnapshot.get("signedup_attendees");
                                                    if (signedUpAttendees != null) {
                                                        boolean isAttendeeRemoved = false;
                                                        for (Map<String, Object> attendee : new ArrayList<>(signedUpAttendees)) {
                                                            DocumentReference ref = (DocumentReference) attendee.get("attendeeRef");
                                                            if (attendeeId.equals(ref.getId())) {
                                                                Long currentCount = eventDocumentSnapshot.getLong("signup_count");
                                                                if (currentCount != null) {
                                                                    eventRef.update("signup_count", currentCount - 1);
                                                                    Log.d("Firestore", "Decreased sign-up count for event: " + document.getId());
                                                                }
                                                                isAttendeeRemoved = true;
                                                            }
                                                        }
                                                        if (isAttendeeRemoved) {
                                                            // remove attendee from signedup_attendees list in each event
                                                            signedUpAttendees.removeIf(attendee -> attendeeId.equals(((DocumentReference) attendee.get("attendeeRef")).getId()));
                                                            eventRef.update("signedup_attendees", signedUpAttendees)
                                                                    .addOnSuccessListener(aVoid -> Log.d("UpdateEvent", "Attendee removed from event's signedup_attendees for event: " + document.getId()))
                                                                    .addOnFailureListener(e -> Log.e("UpdateEvent", "Error removing attendee from event: " + document.getId(), e));
                                                        }
                                                    }
                                                }).addOnFailureListener(e -> Log.e("FetchEvent", "Error fetching event document: " + document.getId(), e));
                                            }
                                            callback.onAttendeeEventsRemoved(attendeeId);
                                        } else {
                                            Log.d("Firestore", "No Event documents exist");
                                            callback.onAttendeeEventsRemoved(null);
                                        }
                                    } else {
                                        Log.d("Firestore", "Failed to retrieve Event documents");
                                        callback.onAttendeeEventsRemoved(null);
                                    }
                                });
                    } else {
                        Log.d("Firestore", "No Attendee document exists for device_id: " + profileDeviceId);
                        callback.onAttendeeEventsRemoved(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error getting Attendee document for device_id: " + profileDeviceId, e);
                    callback.onAttendeeEventsRemoved(null);
                });
    }

    private void deleteAttendee(String attendeeId, ProfileDeletionCallback callback) {
        if (attendeeId == null || attendeeId.isEmpty()) {
            Log.d("deleteAttendee", "No attendeeId provided, skipping deleteAttendee.");
            callback.onAttendeeDeleted(); // Proceed as there is no attendee to delete.
            return;
        }

        Log.d("Firestore", "Attempting to delete attendee with ID: " + attendeeId);

        db.collection("Attendees").document(attendeeId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("deleteAttendee", "Attendee document with ID: " + attendeeId + " has been successfully deleted");
                        callback.onAttendeeDeleted();
                    } else {
                        Log.e("deleteAttendee", "Failed to delete attendee document with ID: " + attendeeId, task.getException());
                        // You may want to handle this situation or retry the deletion.
                        // For now, we'll call the callback to proceed with the flow.
                        callback.onAttendeeDeleted();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("deleteAttendee", "Error deleting attendee document with ID: " + attendeeId, e);
                    // Even in case of failure, we call the callback to continue the process.
                    callback.onAttendeeDeleted();
                });
    }


    private void removeEventsForOrganizer(String profileDeviceId, ProfileDeletionCallback callback) {
        Log.d("FirestoreOperation", "Starting to delete events for organizer with device ID: " + profileDeviceId);
        db.collection("Organizers")
                .whereEqualTo("device_id", profileDeviceId)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot organizerSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String organizerId = organizerSnapshot.getId();
                        Log.d("FirestoreOperation", "Organizer document found: " + organizerId);
                        List<DocumentReference> eventsList = (List<DocumentReference>) organizerSnapshot.get("events_list");
                        if (eventsList != null && !eventsList.isEmpty()) {
                            Log.d("FirestoreOperation", "Processing " + eventsList.size() + " events for organizer.");
                            AtomicInteger eventsToProcess = new AtomicInteger(eventsList.size());

                            for (DocumentReference eventRef : eventsList) {
                                eventRef.get().addOnSuccessListener(eventDoc -> {
                                    if (eventDoc.exists()) {
                                        Log.d("FirestoreOperation", "Processing event document: " + eventDoc.getId());
                                        List<Map<String, Object>> signedUpAttendees = (List<Map<String, Object>>) eventDoc.get("signedup_attendees");
                                        if (signedUpAttendees != null && !signedUpAttendees.isEmpty()) {
                                            Log.d("FirestoreOperation", "Processing " + signedUpAttendees.size() + " attendees for event: " + eventDoc.getId());
                                            AtomicInteger attendeesToProcess = new AtomicInteger(signedUpAttendees.size());

                                            for (Map<String, Object> attendeeMap : signedUpAttendees) {
                                                DocumentReference attendeeRef = (DocumentReference) attendeeMap.get("attendeeRef");
                                                attendeeRef.get().addOnSuccessListener(attendeeDoc -> {
                                                    List<DocumentReference> signupEventList = (List<DocumentReference>) attendeeDoc.get("signup_event_list");
                                                    if (signupEventList != null && signupEventList.contains(eventRef)) {
                                                        attendeeRef.update("signup_event_list", FieldValue.arrayRemove(eventRef))
                                                                .addOnSuccessListener(aVoid -> {
                                                                    Log.d("FirestoreOperation", "Event reference removed from attendee's signup_event_list: " + attendeeRef.getId());
                                                                    if (attendeesToProcess.decrementAndGet() == 0) {
                                                                        deleteEventDocument(eventRef, eventsToProcess, organizerSnapshot, callback);
                                                                    }
                                                                })
                                                                .addOnFailureListener(e -> Log.e("FirestoreOperation", "Failed to remove event reference from attendee's signup_event_list: " + attendeeRef.getId(), e));
                                                    } else if (attendeesToProcess.decrementAndGet() == 0) {
                                                        deleteEventDocument(eventRef, eventsToProcess, organizerSnapshot, callback);
                                                    }
                                                });
                                            }
                                        } else {
                                            deleteEventDocument(eventRef, eventsToProcess, organizerSnapshot, callback);
                                        }
                                    } else if (eventsToProcess.decrementAndGet() == 0) {
                                        clearOrganizerEventsList(organizerSnapshot, callback);
                                    }
                                });
                            }
                        } else {
                            Log.d("FirestoreOperation", "No events found for organizer: " + organizerId);
                            callback.onOrganizerEventsRemoved(organizerId);
                        }
                    } else {
                        Log.d("FirestoreOperation", "No organizer found with the provided device ID: " + profileDeviceId);
                        callback.onOrganizerEventsRemoved(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreOperation", "Failed to query Organizers collection", e);
                    callback.onOrganizerEventsRemoved(null);
                });
    }

    private void deleteEventDocument(DocumentReference eventRef, AtomicInteger eventsToProcess, DocumentSnapshot organizerSnapshot, ProfileDeletionCallback callback) {
        eventRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirestoreOperation", "Event document deleted successfully: " + eventRef.getId());
                    // Check if this is the last event to process
                    if (eventsToProcess.decrementAndGet() == 0) {
                        // All events deleted, proceed to clear the organizer's events list
                        clearOrganizerEventsList(organizerSnapshot, callback);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreOperation", "Failed to delete event document: " + eventRef.getId(), e);
                    // Even in case of failure, check if it's the last event to process
                    if (eventsToProcess.decrementAndGet() == 0) {
                        // Attempt to clear the organizer's events list regardless of individual event deletion success
                        clearOrganizerEventsList(organizerSnapshot, callback);
                    }
                });
    }


    private void clearOrganizerEventsList(DocumentSnapshot organizerSnapshot, ProfileDeletionCallback callback) {
        organizerSnapshot.getReference().update("events_list", FieldValue.delete())
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirestoreOperation", "Events list cleared from organizer document: " + organizerSnapshot.getId());
                    // Successfully cleared the events list, invoke the callback with the organizer's ID
                    callback.onOrganizerEventsRemoved(organizerSnapshot.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreOperation", "Failed to clear events list from organizer document: " + organizerSnapshot.getId(), e);
                    // Even in case of failure, invoke the callback to continue the flow
                    callback.onOrganizerEventsRemoved(organizerSnapshot.getId());
                });
    }




    private void deleteOrganizer(String organizerId, ProfileDeletionCallback callback) {
        if (organizerId == null || organizerId.isEmpty()) {
            Log.d("deleteOrganizer", "No organizerId provided, skipping deleteOrganizer operation.");
            callback.onOrganizerDeleted(); // Proceed as there is no organizer to delete.
            return;
        }

        Log.d("Firestore", "Attempting to delete organizer with ID: " + organizerId);

        db.collection("Organizers").document(organizerId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Successfully deleted organizer with ID: " + organizerId);
                    callback.onOrganizerDeleted();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error deleting organizer with ID: " + organizerId, e);
                    // Even in case of failure, we proceed with the callback to continue the process.
                    callback.onOrganizerDeleted();
                });
    }


    private void deleteUser(String profileId, ProfileDeletionCallback callback) {
        if (profileId == null || profileId.isEmpty()) {
            Log.d("deleteUser", "No profileId provided, skipping deleteUser operation.");
            callback.onUserDeleted(); // Proceed as there is no user profile to delete.
            return;
        }

        Log.d("Firestore", "Attempting to delete user with ID: " + profileId);

        db.collection("Users").document(profileId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Successfully deleted user with ID: " + profileId);
                    callback.onUserDeleted();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error deleting user with ID: " + profileId, e);
                    // Even in case of failure, we proceed with the callback to continue the process.
                    callback.onUserDeleted();
                });
    }



    private void deleteImage(String imageId) {
        DocumentReference eventDoc = db.collection("Events").document(imageId);
        eventDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // The task is successful, now check if the document exists
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        HashMap<String,Object> data = new HashMap<>();
                        data.put("poster", FieldValue.delete());
                        eventDoc.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Field successfully deleted
                                Log.d("Firestore", "Field successfully deleted");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error deleting the field
                                Log.w("Firestore", "Error deleting field", e);
                            }
                        });

                        Log.d("Events Document", "Events Document exists!");
                    } else {
                        Log.d("Events Document", "Events Document does not exist!");
                    }
                } else {
                    // The task failed with an exception
                    Log.d("Events Document", "Failed with: ", task.getException());
                }
            }
        });


    }


    public void setupDetailsNavigationBar() {
        BottomNavigationView detailsNavBar = findViewById(R.id.administrator_view_details_navigation_bar);
        BottomNavigationView navBar = findViewById(R.id.administrator_handler_navigation_bar);
        int navBarItemId = navBar.getSelectedItemId();

        detailsNavBar.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.cancel_icon) {
                    hideDetailsNavigationBar();
                    showMainBottomNavigationBar();

                } else if (itemId == R.id.view_details_icon) {
                    hideDetailsNavigationBar();
                    showDeleteNavigationBar();
                    if (navBarItemId==R.id.events_icon) {
                        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.administrator_handler_frame);
                        if (currentFragment instanceof AdministratorDashboardFragment) {
                            selectedEventId = ((AdministratorDashboardFragment) currentFragment).getSelectedEventId();
                            if (selectedEventId != null) {
                                navigateToEventDetails(selectedEventId);
                            }
                        }
                    }

                    else if (navBarItemId==R.id.profiles_icon) {
                        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.administrator_handler_frame);
                        if (currentFragment instanceof AdministratorDashboardFragment) {
                            selectedProfileId = ((AdministratorDashboardFragment) currentFragment).getSelectedProfileId();
                            if (selectedProfileId != null) {
                                navigateToProfileDetails(selectedProfileId);
                            }
                        }
                    }
                    else if (navBarItemId==R.id.images_icon) {
                        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.administrator_handler_frame);
                        if (currentFragment instanceof AdministratorDashboardFragment) {
                            selectedImageId = ((AdministratorDashboardFragment) currentFragment).getSelectedImageId();
                            selectedImageType = ((AdministratorDashboardFragment) currentFragment).getSelectedImageType();
                            if (selectedImageId != null) {
                                navigateToImageDetails(selectedImageId, selectedImageType);
                            }
                        }
                    }
                }
                return true;
            }
        });
    }

    private void navigateToEventDetails(String eventId) {
        AdministratorEventDetailsFragment detailsFragment = AdministratorEventDetailsFragment.newInstance(eventId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.administrator_handler_frame, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToImageDetails(String imageId, String imageType) {
        AdministratorImageDetailsFragment detailsFragment = AdministratorImageDetailsFragment.newInstance(imageId, imageType);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.administrator_handler_frame, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToProfileDetails(String profileId) {
        AdministratorProfileDetailsFragment detailsFragment = AdministratorProfileDetailsFragment.newInstance(profileId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.administrator_handler_frame, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    /**
     * Interface method implemented for when the edit menu fragment is destroyed
     */
    public void onFragmentDestroyed() {
        //After filling in user info from edit profile screen, this function is called.
        bottomNavigationView.setVisibility(View.VISIBLE);
        homeScreenAdministrator();
    }

    /**
     * Sets bottom menu bar to be invisible and opens up the welcome screen if its the user's first time logging in
     */
    public void firstTimeLoginAdministrator() {
        // Inflates the welcome screen fragment if its the user's first time logging in

        bottomNavigationView.setVisibility(View.INVISIBLE);
        WelcomeScreenFragment welcomeScreenFragment = new WelcomeScreenFragment("administrator");

        getSupportFragmentManager().beginTransaction()
                .add(R.id.administrator_handler_frame, welcomeScreenFragment).commit();
    }

    /**
     * Sets bottom menu bar to be visible and open up the home screen
     */
    public void homeScreenAdministrator() {

        //sendUserIdToFirestore(this); //Sends user android id to database

        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setSelectedItemId(R.id.defaultNavPlaceholder);
        HomeScreenFragment homeScreenFragment = new HomeScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.administrator_handler_frame, homeScreenFragment).commit();

    }

    /**
     * Sends a query to check if user's android id already exists within the database
     * If android id exists, then it send the user directly to the home page,
     * else the app sends the user to the welcome page
     *
     * @param collectionName Collection name called 'Users' in the database
     * @param fieldName      Field name called 'device_id' in the Users collection
     * @param uniqueId       Unique android id of the user which is checked for in the database
     */
    private void checkIfUserExists(String collectionName, String fieldName, String uniqueId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .whereEqualTo(fieldName, uniqueId)
                .limit(1) // Optimizes the query by limiting to the first match
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            homeScreenAdministrator(); // Takes user directly to the home screen
                            Log.d("UniqueIDCheck", "The unique ID exists in the collection.");
                        } else {
                            firstTimeLoginAdministrator(); // Takes user to the profile page
                            Log.d("UniqueIDCheck", "The unique ID does not exist in the collection.");
                        }
                    } else {
                        Log.e("UniqueIDCheck", "Failed to perform the query.", task.getException());
                    }
                });
    }

    /**
     * This method sets up all necessary parameters for checkIfUserExists() method.
     *
     * @param context
     */
    private void authenticateUser(Context context) {
        //Function to help set up checkIfUserExists
        bottomNavigationView.setVisibility(View.INVISIBLE);
        // Get the unique Android ID
        String device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String collectionName = "Users";
        String fieldName = "device_id";

        //Checks to see if user exists
        checkIfUserExists(collectionName, fieldName, device_id);

    }
}