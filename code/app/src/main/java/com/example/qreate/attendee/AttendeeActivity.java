package com.example.qreate.attendee;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qreate.R;
import com.example.qreate.organizer.HomeScreenFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AttendeeActivity extends AppCompatActivity {

    /*
     This class is used as a MainActivity class for the Attendee UI
     */

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_tap_to_scan_qr);

        BottomNavigationView bottomNavigationView = findViewById(R.id.attendee_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.defaultNavPlaceholder);

        //inflates the homescreen fragment automatically
        HomeScreenFragment homeScreenFragment = new HomeScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.attendee_nav_frame,homeScreenFragment).commit();
    }
}
