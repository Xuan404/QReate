package com.example.qreate.attendee;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.qreate.EditProfileScreenFragment;
import com.example.qreate.HomeScreenFragment;
import com.example.qreate.R;
import com.example.qreate.WelcomeScreenFragment;
import com.example.qreate.attendee.AttendeeEventDetailsFragment;
import com.example.qreate.attendee.AttendeeNotificationsFragment;
import com.example.qreate.attendee.AttendeeScanFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendeeActivity extends AppCompatActivity implements EditProfileScreenFragment.OnFragmentInteractionListener {
    /*
    This class is used as the MainActivity class for the Administrator UI
     */

    private FirebaseFirestore db;
    private BottomNavigationView bottomNavigationView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_handler);

        db = FirebaseFirestore.getInstance();

        bottomNavigationView = findViewById(R.id.attendee_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.defaultNavPlaceholderAttendee);


        // authenticateUser(this);


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


    public void onFragmentDestroyed() {
        //After filling in user info from edit profile screen, this function is called

        bottomNavigationView.setVisibility(View.VISIBLE);
        homeScreenAttendee();
    }



    public void firstTimeLoginAttendee() {
        // Inflates the welcomescreen fragment if its the user's first time logging in

        bottomNavigationView.setVisibility(View.INVISIBLE);
        WelcomeScreenFragment welcomeScreenFragment = new WelcomeScreenFragment("attendee");

        getSupportFragmentManager().beginTransaction()
                .add(R.id.attendee_handler_frame,welcomeScreenFragment).commit();
    }

    public void homeScreenAttendee() {

        //sendUserIdToFirestore(this); //Sends user android id to database

        bottomNavigationView.setVisibility(View.VISIBLE);
        HomeScreenFragment homeScreenFragment = new HomeScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.attendee_handler_frame,homeScreenFragment).commit();

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
