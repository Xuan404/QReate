package com.example.qreate.administrator;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    String profileDeviceId;


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
        // check if the admin is trying to delete his own profile
        checkIfOwnProfile(profileId, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // after checking if the profile is not the admin's

            }
        });
    }


    private void checkIfOwnProfile(String profileId, OnCompleteListener<DocumentSnapshot> completionListener) {
        String device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        db.collection("Users").document(profileId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                profileDeviceId = document.getString("device_id");
                                Log.d("Firebase", "device_id of the profile has been retrieved ");
                                if (profileDeviceId.equals(device_id)) {
                                    Toast.makeText(AdministratorActivity.this,"Admin cannot delete his own profile", Toast.LENGTH_SHORT).show();
                                } else {
                                    completionListener.onComplete(task);
                                }
                            } else {
                                Log.d("Firebase", "Profile document does not exist");
                            }
                        } else {
                            Log.d("Firebase", "Task was unsuccessful ");
                        }
                    }
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