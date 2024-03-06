package com.example.qreate.organizer;

import android.os.Bundle;
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

public class OrganizerActivity extends AppCompatActivity implements EditProfileScreenFragment.OnFragmentInteractionListener {

    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_handler);

        bottomNavigationView = findViewById(R.id.organizer_handler_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.defaultNavPlaceholder); //This line is here so that there is no default item selected, it selects a menu item that is invisible

        // An if statement needs to be implemented here to check if user has logged in previously.
        firstTimeLoginOrganizer();
        // Else the user goes directly to the home screen.


        //Used if/else to check for selected id because switch was being a bitch
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.qr_menu) {
                    selectedFragment = new QRmenuFragment();
                } else if (itemId == R.id.notifications_menu) {
                    selectedFragment = new NotificationsMenuFragment();
                } else if (itemId == R.id.attendee_list_menu) {
                    selectedFragment = new AttendeeListMenuFragment();
                } else if (itemId == R.id.geolocation_menu) {
                    selectedFragment = new GeolocationMenuFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.organizer_handler_frame, selectedFragment).commit();
                    return true;
                }
                return true;
            }
        });


    }

    public void firstTimeLoginOrganizer() {

        // Inflates the welcomescreen fragment if its the user's first time logging in
        bottomNavigationView.setVisibility(View.INVISIBLE);
        WelcomeScreenFragment welcomeScreenFragment = new WelcomeScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.organizer_handler_frame,welcomeScreenFragment).commit();


    }

    public void homeScreenOrganizer() {

        HomeScreenFragment homeScreenFragment = new HomeScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.organizer_handler_frame,homeScreenFragment).commit();

    }

    public void onFragmentDestroyed() {
        //After filling in user info from edit profile screen, this function is called
        bottomNavigationView.setVisibility(View.VISIBLE);
        homeScreenOrganizer();

    }

}
