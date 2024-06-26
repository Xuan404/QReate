package com.example.qreate.attendee;

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
import com.example.qreate.HomeScreenFragment;
import com.example.qreate.R;
import com.example.qreate.WelcomeScreenFragment;
import com.example.qreate.administrator.EventArrayAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AttendeeActivity manages the main UI for attendee users within the application,
 * providing navigation through a bottom navigation bar to various fragments such as
 * notifications, event scanning, and event details.
 * It is responsible for initializing and managing the user interface related to attendee operations,
 * handling navigation between different sections of the app, and performing initial user authentication.
 * This class also implements the OnFragmentInteractionListener interface from the EditProfileScreenFragment
 * to handle interaction events in the user profile editing scenario.
 *
 * @author Akib Zaman Choudhury
 */

public class AttendeeActivity extends AppCompatActivity implements EditProfileScreenFragment.OnFragmentInteractionListener{
    /*
    This class is used as the MainActivity class for the Administrator UI
     */

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();;
    private BottomNavigationView bottomNavigationView;
    private String retrievedDocumentId;
    private String tokenFCM;
    private String selectedEventId;
    private String selectedNotifId;
    String device_id;
    private EventArrayAdapter eventArrayAdapter;
    private NotifArrayAdapter notifArrayAdapter;

    /**
     * Called when the activity is starting. This is where most initialization should go:
     * calling setContentView(int) to inflate the activity's UI, using findViewById(int)
     * to programmatically interact with widgets in the UI, setting up the bottom navigation
     * to handle fragment switching, and authenticating the user.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                          Otherwise it should be null.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_handler);

        device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        bottomNavigationView = findViewById(R.id.attendee_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.defaultNavPlaceholderAttendee);


        authenticateUser(this);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.notifications_icon) {
                    selectedFragment = new AttendeeNotificationsFragment();
                } else if (itemId == R.id.qr_icon) {
                    selectedFragment = new AttendeeScanFragment();
                } else if (itemId == R.id.events_icon_nav) {
                    selectedFragment = new AttendeeEventDetailsFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.attendee_handler_frame, selectedFragment).commit();
                    return true;
                }

                return false;
            }
        });
    }

    /**
     *  onFragmentDestroyed() is called when an interaction in the EditProfileScreenFragment
     *  is detected that requires the parent activity (AttendeeActivity) to perform a
     *  subsequent action, such as returning to the home screen.
     */
    public void onFragmentDestroyed() {
        //After filling in user info from edit profile screen, this function is called

        bottomNavigationView.setVisibility(View.VISIBLE);
        createAttendeeCollection();
        homeScreenAttendee();
    }

    /**
     * Initiates the first-time login process for a new attendee, inflating the WelcomeScreenFragment
     * and making the bottom navigation bar invisible to focus the user's attention on the welcome
     * content.
     */

    public void firstTimeLoginAttendee() {
        // Inflates the welcomescreen fragment if its the user's first time logging in

        bottomNavigationView.setVisibility(View.INVISIBLE);
        WelcomeScreenFragment welcomeScreenFragment = new WelcomeScreenFragment("attendee");

        getSupportFragmentManager().beginTransaction()
                .add(R.id.attendee_handler_frame,welcomeScreenFragment).commit();
    }

    /**
     * Transitions the user interface to display the HomeScreenFragment for an attendee,
     * making the bottom navigation bar visible and replacing the current fragment container
     * content with the home screen fragment.
     */

    public void homeScreenAttendee() {

        authenticateAttendee(); // checks if attendee collection exists, if not then creates one

        bottomNavigationView.setVisibility(View.VISIBLE);
        HomeScreenFragment homeScreenFragment = new HomeScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.attendee_handler_frame,homeScreenFragment).commit();

    }

    /**
     * Checks if the user's unique ID exists in the specified Firestore collection,
     * directing the user either to the home screen or to the first-time login process
     * based on whether the ID is found.
     *
     * @param collectionName The name of the Firestore collection to search.
     * @param fieldName The document field name expected to contain the user's unique ID.
     * @param uniqueId The unique ID of the device/user being authenticated.
     */


    private void checkIfUserExists(String collectionName, String fieldName, String uniqueId) {

        db.collection("Users")
                .whereEqualTo(fieldName, uniqueId)
                .limit(1) // Optimizes the query by limiting to the first match
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            homeScreenAttendee(); // Takes user directly to the home screen
                            Log.d("UniqueIDCheck", "The unique ID exists in the collection.");
                        } else {
                            firstTimeLoginAttendee(); // Takes user to the profile page
                            Log.d("UniqueIDCheck", "The unique ID does not exist in the collection.");
                        }
                    } else {
                        Log.e("UniqueIDCheck", "Failed to perform the query.", task.getException());
                    }
                });
    }

    /**
     * Prepares for user authentication by making the bottom navigation bar invisible and
     * calling the checkIfUserExists method with the user's device ID and the relevant Firestore
     * collection details. This is an initial step in the app's security and personalization
     * process.
     *
     * @param context The Context in which the authentication method is called, typically the current Activity.
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

    ////////////// FUNCTIONS FOR HANDLING ATTENDEE EVENTS PAGE //////////////////////////////////

    /**
     * Hides the primary bottom navigation bar, typically to prioritize displaying another navigation mechanism or content view.
     */
    public void hideBottomNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.attendee_navigation_bar);
        navBar.setVisibility(View.INVISIBLE); // Make the bottom navigation bar disappear
    }

    /**
     * Shows the primary bottom navigation bar, making it visible to the user after being hidden.
     */
    public void showBottomNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.attendee_navigation_bar);
        navBar.setVisibility(View.VISIBLE); // Make the bottom navigation bar reappear
    }

    /**
     * Displays the details navigation bar which is specific for viewing detailed content such as notifications or event details.
     * It sets a default selected item and initializes further setup for detailed navigation.
     */
    public void showDetailsNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.attendee_view_details_navigation_bar);
        navBar.setVisibility(View.VISIBLE);
        navBar.setSelectedItemId(R.id.defaultNavPlaceholder);
        setupDetailsNavigationBar();
    }

    /**
     * Hides the details navigation bar to revert back to a more general navigation state or to prioritize other content.
     */
    public void hideDetailsNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.attendee_view_details_navigation_bar);
        navBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Displays the deletion navigation bar, which includes options related to deleting or canceling the deletion of items.
     * It sets a default selected item and initializes the deletion navigation setup.
     */
    public void showDeleteNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.attendee_delete_navigation_bar);
        navBar.setVisibility(View.VISIBLE); // Make the bottom navigation bar reappear
        navBar.setSelectedItemId(R.id.defaultNavPlaceholder);
        setupDeleteNavigationBar();
    }

    /**
     * Hides the deletion navigation bar, typically after a deletion action is completed or canceled.
     */
    public void hideDeleteNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.attendee_delete_navigation_bar);
        navBar.setVisibility(View.INVISIBLE); // Make the bottom navigation bar reappear
    }


    /**
     * Configures the details navigation bar, setting up item selection handling to navigate to more specific content views
     * based on the current navigation state or selected items.
     */
    public void setupDetailsNavigationBar() {
        BottomNavigationView detailsNavBar = findViewById(R.id.attendee_view_details_navigation_bar);
        BottomNavigationView navBar = findViewById(R.id.attendee_navigation_bar);
        int navBarItemId = navBar.getSelectedItemId();

        detailsNavBar.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.cancel_icon) {
                    hideDetailsNavigationBar();
                    showBottomNavigationBar();

                } else if (itemId == R.id.view_details_icon) {
                    hideDetailsNavigationBar();
                    if (navBarItemId==R.id.notifications_icon) {
                        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.attendee_handler_frame);
                        if (currentFragment instanceof AttendeeNotificationsFragment) {
                            selectedNotifId = ((AttendeeNotificationsFragment) currentFragment).getSelectedNotifId();
                            if(selectedNotifId != null) {
                                navigateToNotifDetails(selectedNotifId);
                            }
                        }
                    } else if (navBarItemId==R.id.events_icon_nav) {
                        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.attendee_handler_frame);
                        if (currentFragment instanceof OtherEventsFragment) {
                            selectedEventId = ((OtherEventsFragment) currentFragment).getSelectedEventId();
                            if (selectedEventId != null) {
                                navigateToOtherEventDetails(selectedEventId);
                            }
                        } else if (currentFragment instanceof CurrentEventsFragment) {
                            selectedEventId = ((CurrentEventsFragment) currentFragment).getSelectedEventId();
                            if (selectedEventId != null) {
                                navigateToCurrentAndUpcomingEventDetails(selectedEventId);
                            }
                            showDeleteNavigationBar();
                        } else if (currentFragment instanceof UpcomingEventsFragment) {
                            selectedEventId = ((UpcomingEventsFragment) currentFragment).getSelectedEventId();
                            if (selectedEventId != null) {
                                navigateToCurrentAndUpcomingEventDetails(selectedEventId);
                            }
                            showDeleteNavigationBar();
                        }
                    }

                }
                return true;
            }
        });
    }

    /**
     * Configures the delete navigation bar, setting up item selection handling for deletion actions and cancellation.
     */
    public void setupDeleteNavigationBar(){
        BottomNavigationView deleteNavBar = findViewById(R.id.attendee_delete_navigation_bar);
        BottomNavigationView navBar = findViewById(R.id.attendee_navigation_bar);
        int navBarItemId = navBar.getSelectedItemId();
        deleteNavBar.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.cancel_icon_two) {
                    hideDeleteNavigationBar();
                    showBottomNavigationBar();
                    getSupportFragmentManager().popBackStackImmediate();
                } else if (itemId == R.id.delete_icon) {
                    String device_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    if (navBarItemId==R.id.notifications_icon && selectedNotifId != null) {
                        if (notifArrayAdapter != null) {
                            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.attendee_handler_frame);
                            if (currentFragment instanceof AttendeeNotificationsFragment) {
                                String selectedNotifId = ((AttendeeNotificationsFragment) currentFragment).getSelectedNotifId();
                                ((AttendeeNotificationsFragment) currentFragment).deleteNotification(selectedNotifId);
                            }
                        }
                    } else if (navBarItemId==R.id.events_icon_nav) {
                        db.collection("Attendees")
                                .whereEqualTo("device_id", device_id)
                                .limit(1)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (!querySnapshot.isEmpty()) {
                                            DocumentSnapshot attendeeDocument = querySnapshot.getDocuments().get(0);
                                            String attendeeId = attendeeDocument.getId();

                                            removeEventFromAttendee(selectedEventId, attendeeId);
                                        } else {
                                            Log.d("FetchAttendee", "No attendee found with the given device ID");
                                        }
                                    } else {
                                        Log.e("FetchAttendee", "Error fetching attendee document", task.getException());
                                    }
                                });
                        decrementSignupCount(selectedEventId);
                    }
                    hideDeleteNavigationBar();
                    showBottomNavigationBar();
                    getSupportFragmentManager().popBackStackImmediate();
                }
                return true;
            }
        });

    }

    /**
     * Navigates to a detailed view of a specific event that is not part of the current attendee's list of signed-up events.
     * @param eventId The ID of the event to navigate to.
     */
    private void navigateToOtherEventDetails(String eventId) {
        AttendeeEventViewDetailsFragment detailsFragment = AttendeeEventViewDetailsFragment.newInstance(eventId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.attendee_handler_frame, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Navigates to detailed view of a current or upcoming event that the attendee has signed up for.
     * @param eventId The ID of the event to navigate to.
     */
    private void navigateToCurrentAndUpcomingEventDetails(String eventId) {
        AttendeeSignedUpEventsDetailsFragment detailsFragment = AttendeeSignedUpEventsDetailsFragment.newInstance(eventId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.attendee_handler_frame, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    /**
     * Removes an event from the attendee's list of signed-up events and updates the Firestore document accordingly.
     * @param eventId The ID of the event to be removed.
     * @param attendeeId The ID of the attendee from whose list the event is to be removed.
     */
    private void removeEventFromAttendee(String eventId, String attendeeId) {
        DocumentReference attendeeRef = db.collection("Attendees").document(attendeeId);

        // Remove the event reference from the attendee's signedup_events_list
        attendeeRef.update("signup_event_list", FieldValue.arrayRemove(db.collection("Events").document(eventId)))
                .addOnSuccessListener(aVoid -> {
                    Log.d("UpdateAttendee", "Event removed from attendee's signup_event_list");
                    // Proceed to remove attendee from the event
                    removeAttendeeFromEvent(eventId, attendeeId);
                })
                .addOnFailureListener(e -> Log.e("UpdateAttendee", "Error removing event from attendee's list", e));
    }

    /**
     * Removes an attendee from an event's list of signed-up attendees and updates the Firestore document.
     * @param eventId The ID of the event from which the attendee is to be removed.
     * @param attendeeId The ID of the attendee to be removed from the event.
     */
    private void removeAttendeeFromEvent(String eventId, String attendeeId) {
        DocumentReference eventRef = db.collection("Events").document(eventId);

        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            List<Map<String, Object>> signedUpAttendees = (List<Map<String, Object>>) documentSnapshot.get("signedup_attendees");
            if (signedUpAttendees != null) {
                signedUpAttendees.removeIf(attendee -> attendeeId.equals(((DocumentReference) attendee.get("attendeeRef")).getId()));
                eventRef.update("signedup_attendees", signedUpAttendees)
                        .addOnSuccessListener(aVoid -> Log.d("UpdateEvent", "Attendee removed from event's signedup_attendees"))
                        .addOnFailureListener(e -> Log.e("UpdateEvent", "Error removing attendee from event", e));
            }
        }).addOnFailureListener(e -> Log.e("FetchEvent", "Error fetching event document", e));

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.attendee_handler_frame);
        if (currentFragment instanceof CurrentEventsFragment) {
            ((CurrentEventsFragment) currentFragment).removeEventFromList(eventId);
        } else if (currentFragment instanceof UpcomingEventsFragment) {
            ((UpcomingEventsFragment) currentFragment).removeEventFromList(eventId);
        }
    }

    /**
     * Decrements the signup count for an event in Firestore, reflecting one less attendee.
     * @param eventId The ID of the event for which the signup count is to be decremented.
     */
    private void decrementSignupCount(String eventId) {
        final DocumentReference eventRef = db.collection("Events").document(eventId);
        db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot eventSnapshot = transaction.get(eventRef);
                    Long currentCount = eventSnapshot.getLong("signup_count");
                    if (currentCount == null || currentCount <= 0) {
                        return null;
                    }
                    transaction.update(eventRef, "signup_count", currentCount - 1);
                    return null;
                }).addOnSuccessListener(aVoid -> Log.d("Transaction", "Transaction success! signup_count incremented"))
                .addOnFailureListener(e -> Log.e("Transaction", "Transaction failure.", e));
    }

    /**
     * Navigates to a detailed view of a specific notification.
     * @param notifId The ID of the notification to navigate to.
     */
    private void navigateToNotifDetails(String notifId) {
        NotifViewDetailsFragment detailsFragment = NotifViewDetailsFragment.newInstance(notifId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.attendee_handler_frame, detailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


////////////// FUNCTIONS FOR HANDLING ORGANIZER COLLECTION CREATION //////////////////////////////////
    /**
     * Interface for handling the retrieval of a document's unique ID from Firestore.
     */
    public interface DocumentIdCallback {
        /**
         * Called when the document ID has been successfully retrieved.
         *
         * @param documentId The retrieved document ID.
         */
        void onDocumentIdRetrieved(String documentId);
        /**
         * Called when there is an error during the document ID retrieval process.
         *
         * @param e The exception thrown during the retrieval process.
         */
        void onError(Exception e);
    }

    /**
     * Interface for handling the reception of a Firebase Cloud Messaging (FCM) token.
     */
    public interface FCMTokenCallback {
        void onTokenReceived(String tokenFCM);
    }

    /**
     * Creates a new attendee record in Firestore's "Attendees" collection. The method first retrieves the
     * user's document ID and then creates an FCM token. Using both pieces of data, it assembles a record to
     * be added to the "Attendees" collection.
     */
    public void createAttendeeCollection() {

        retrieveUserDocument(new DocumentIdCallback() {
            @Override
            public void onDocumentIdRetrieved(String documentId) {
                createFCMToken(new FCMTokenCallback() {
                    @Override
                    public void onTokenReceived(String tokenFCM) {

                        DocumentReference docRef = db.collection("Users").document(documentId);
                        List<String> eventList = new ArrayList<>();

                        Map<String, Object> device = new HashMap<>();
                        device.put("device_id", device_id);
                        device.put("user_document_id", docRef);
                        device.put("signup_events_list", eventList);
                        device.put("fcm_token", tokenFCM);
                        device.put("currently_checkedin", "");

                        db.collection("Attendees").add(device);
                    }
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }


    /**
     * Authenticates the attendee by checking if their document exists in the "Attendees" collection based
     * on their device ID. If the document does not exist, a new attendee collection is created.
     */
    private void authenticateAttendee() {
        //Function to help set up checkIfOrganizerExists

        retrieveUserDocument(new DocumentIdCallback() {
            @Override
            public void onDocumentIdRetrieved(String documentId) {

                DocumentReference docRef = db.collection("Users").document(documentId);
                String collectionName = "Attendees";
                String fieldName = "user_document_id";

                checkIfAttendeesExist(collectionName, fieldName, docRef);

            }

            @Override
            public void onError(Exception e) {
                // Handle the error, such as by logging or displaying a message
            }
        });

    }


    /**
     * Checks if an attendee already exists in the specified collection by matching a document reference.
     *
     * @param collectionName The name of the Firestore collection to search within.
     * @param fieldName The document field name to match against the provided document reference.
     * @param documentReference The document reference to search for in the collection.
     */
    public void checkIfAttendeesExist(String collectionName, String fieldName, DocumentReference documentReference) {

        db.collection(collectionName)
                .whereEqualTo(fieldName, documentReference)
                .limit(1) // Optimizes the query by limiting to the first match
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            Log.d("UniqueIDCheck", "The unique ID exists in the collection.");
                        } else {
                            createAttendeeCollection(); // creates organizer collection
                            Log.d("UniqueIDCheck", "The unique ID does not exist in the collection.");
                        }
                    } else {
                        Log.e("UniqueIDCheck", "Failed to perform the query.", task.getException());
                    }
                });


    }


    /**
     * Retrieves the Firestore document ID for the current user based on their device ID. This is used
     * to authenticate and manage attendee data in the database.
     *
     * @param callback The callback to handle the outcome of the document ID retrieval process.
     */
    public void retrieveUserDocument(DocumentIdCallback callback) {

        db.collection("Users")
                .whereEqualTo("device_id", device_id)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String documentId = document.getId();
                        callback.onDocumentIdRetrieved(documentId);
                    } else {
                        callback.onError(new Exception("Document not found or error in fetching document."));
                    }
                });
    }

    /**
     * Creates a Firebase Cloud Messaging (FCM) token for the device. This token is used for sending
     * notifications to the device.
     *
     * @param callback The callback to handle the reception of the FCM token.
     */
    public void createFCMToken(FCMTokenCallback callback) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        //Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    String tokenFCM = task.getResult();
                    callback.onTokenReceived(tokenFCM);
                });
    }

//////////////////////////////////////////////// END //////////////////////////////////////////////////

}
