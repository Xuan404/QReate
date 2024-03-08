package com.example.qreate.organizer;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.qreate.EditProfileScreenFragment;
import com.example.qreate.HomeScreenFragment;
import com.example.qreate.R;
import com.example.qreate.WelcomeScreenFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * The following class is responsible for all activities related to the Organizer
 *
 * Outstanding Issue: OrganizerActivityTest does not PASS unless line 53: authenticateUser(this); is commented.
 * @author Akib Zaman Choudhury
 */
public class OrganizerActivity extends AppCompatActivity implements EditProfileScreenFragment.OnFragmentInteractionListener {

    private FirebaseFirestore db;
    private BottomNavigationView bottomNavigationView;

    /**
     * Creates the view and sets activity to the organizer_handler layout
     * Authenticates user
     * set up the navigation bar
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_handler);

        bottomNavigationView = findViewById(R.id.organizer_handler_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.defaultNavPlaceholder); //This line is here so that there is no default item selected, it selects a menu item that is invisible

        // Authenticates if user exists and sends them to the appropriate page
        authenticateUser(this);

        //Used if/else to check for selected id because switch was being a bitch
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.qr_menu) {
                    selectedFragment = new OrganizerQRmenuFragment();
                } else if (itemId == R.id.notifications_menu) {
                    selectedFragment = new OrganizerNotificationsMenuFragment();
                } else if (itemId == R.id.attendee_list_menu) {
                    selectedFragment = new OrganizerAttendeeListMenuFragment();
                } else if (itemId == R.id.geolocation_menu) {
                    selectedFragment = new OrganizerGeolocationMenuFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.organizer_handler_frame, selectedFragment).commit();
                    return true;
                }
                return true;
            }
        });


    }

    /**
     * Interface method implemented for when the edit menu fragment is destroyed
     */
    public void onFragmentDestroyed() {
        //After filling in user info from edit profile screen, this function is called

        bottomNavigationView.setVisibility(View.VISIBLE);
        homeScreenOrganizer();

    }

    /**
     * Sets bottom menu bar to be invisible and open up the welcome screen
     */
    public void firstTimeLoginOrganizer() {
        // Inflates the welcomescreen fragment if its the user's first time logging in

        bottomNavigationView.setVisibility(View.INVISIBLE);
        WelcomeScreenFragment welcomeScreenFragment = new WelcomeScreenFragment("organizer");

        getSupportFragmentManager().beginTransaction()
                .add(R.id.organizer_handler_frame,welcomeScreenFragment).commit();


    }

    /**
     * Sets buttom menu bar to be visible and open up the home screen
     */
    public void homeScreenOrganizer() {

        //sendUserIdToFirestore(this); //Sends user android id to database

        bottomNavigationView.setVisibility(View.VISIBLE);
        HomeScreenFragment homeScreenFragment = new HomeScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.organizer_handler_frame,homeScreenFragment).commit();

    }


    /**
     * Sends a query to check if user's android id already exists within the database
     * If android id exists, then it send the user directly to the home page,
     * else the app sends the user to the welcome page
     *
     * @param collectionName
     * @param fieldName
     * @param uniqueId
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
                            homeScreenOrganizer(); // Takes user directly to the home screen
                            Log.d("UniqueIDCheck", "The unique ID exists in the collection.");
                        } else {
                            firstTimeLoginOrganizer(); // Takes user to the profile page
                            Log.d("UniqueIDCheck", "The unique ID does not exist in the collection.");
                        }
                    } else {
                        Log.e("UniqueIDCheck", "Failed to perform the query.", task.getException());
                    }
                });
    }

    /**
     * This method sets up all necessary parameters for checkIfUserExists() method.
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
