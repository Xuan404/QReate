package com.example.qreate.attendee;

import android.os.Bundle;
import android.provider.Settings;
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
public class AttendeeActivity extends AppCompatActivity implements EditProfileScreenFragment.OnFragmentInteractionListener {
    /*
    This class is used as the MainActivity class for the Administrator UI
     */

    private BottomNavigationView bottomNavigationView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_handler);

        bottomNavigationView = findViewById(R.id.attendee_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.defaultNavPlaceholder);

        firstTimeLoginOrganizer();

        //inflates the homescreen fragment automatically
//        HomeScreenFragment homeScreenFragment = new HomeScreenFragment();
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.attendee_handler_frame,homeScreenFragment).commit();
    }
//    Button update_profile = findViewById(R.id.welcome_screen_updatebutton);

    public void firstTimeLoginOrganizer() {

        // Inflates the welcomescreen fragment if its the user's first time logging in
        bottomNavigationView.setVisibility(View.INVISIBLE);
        WelcomeScreenFragment welcomeScreenFragment = new WelcomeScreenFragment("attendee");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.attendee_handler_frame,welcomeScreenFragment).commit();


    }
    public void homeScreenOrganizer() {

        HomeScreenFragment homeScreenFragment = new HomeScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.attendee_handler_frame,homeScreenFragment).commit();

    }


    @Override
    public void onFragmentDestroyed() {
        bottomNavigationView.setVisibility(View.VISIBLE);
        homeScreenOrganizer();
    }
}
