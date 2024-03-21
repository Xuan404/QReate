package com.example.qreate.administrator;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.qreate.EditProfileScreenFragment;
import com.example.qreate.R;
import com.example.qreate.HomeScreenFragment;
import com.example.qreate.WelcomeScreenFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
/**
 * The following class is responsible for all activities related to the Administrator
 *
 * Outstanding Issue: AdministratorActivityTest does not PASS unless line 48: authenticateUser(this); is commented.
 */
public class AdministratorActivity extends AppCompatActivity implements EditProfileScreenFragment.OnFragmentInteractionListener {
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;

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

                if (itemId == R.id.events_icon) {
                    selectedFragment = new AdministratorDashboardFragment();
                } else if (itemId == R.id.profiles_icon) {
                    selectedFragment = new AdministratorDashboardFragment();
                } else if (itemId == R.id.images_icon) {
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
    }

    public void hideDeleteNavigationBar() {
        BottomNavigationView navBar = findViewById(R.id.administrator_delete_navigation_bar);
        navBar.setVisibility(View.INVISIBLE); // Make the bottom navigation bar reappear
    }

    public void setupDetailsNavigationBar() {
        BottomNavigationView detailsNavBar = findViewById(R.id.administrator_view_details_navigation_bar);
        detailsNavBar.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.cancel_icon) {
                    hideDetailsNavigationBar();
                    showMainBottomNavigationBar();


                } else if (itemId == R.id.view_details_icon) {
                    hideDetailsNavigationBar();
                    showDeleteNavigationBar();
                    selectedFragment = new AdministratorEventDetailsFragment();
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.administrator_handler, selectedFragment).commit();
                }
                return true;
            }
        });
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