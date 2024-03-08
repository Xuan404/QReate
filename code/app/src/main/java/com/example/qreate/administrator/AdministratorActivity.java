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

import com.example.qreate.R;
import com.example.qreate.HomeScreenFragment;
import com.example.qreate.WelcomeScreenFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AdministratorActivity extends AppCompatActivity {
    /*
    This class is used as the MainActivity class for the Administrator UI
     */
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;
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

                if (itemId == R.id.dashboard_icon) {
                    selectedFragment = new AdministratorDashboardFragment();
                } else if (itemId == R.id.account_icon) {
                    selectedFragment = new HomeScreenFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.administrator_handler_frame, selectedFragment).commit();
                    return true;
                }
                return true;
            }
        });

    }

    public void firstTimeLoginAdministrator() {
        // Inflates the welcomescreen fragment if its the user's first time logging in

        bottomNavigationView.setVisibility(View.INVISIBLE);
        WelcomeScreenFragment welcomeScreenFragment = new WelcomeScreenFragment("administrator");

        getSupportFragmentManager().beginTransaction()
                .add(R.id.administrator_handler_frame,welcomeScreenFragment).commit();
    }

    public void homeScreenAdministrator() {

        //sendUserIdToFirestore(this); //Sends user android id to database

        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setSelectedItemId(R.id.defaultNavPlaceholderAttendee);
        HomeScreenFragment homeScreenFragment = new HomeScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.administrator_handler_frame,homeScreenFragment).commit();

    }


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
