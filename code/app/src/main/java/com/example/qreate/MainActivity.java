package com.example.qreate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.qreate.administrator.AdministratorActivity;
import com.example.qreate.attendee.AttendeeActivity;
import com.example.qreate.organizer.OrganizerActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * This Screen allows the user to login either as an Attendee/Organizer/Administrator
 * Depending on what button the user clicks, he is takes to the screen
 *
 * @author Akib Zaman Choudhury
 */
public class MainActivity extends AppCompatActivity{

    /**
     * Creates and Inflates the view
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Change this to the appropriate layout after Ryan and Shradha have created the xml file
        setContentView(R.layout.activity_main);

        // The ids to the button will change as well after the layout has been created
        Button attendeeButton = findViewById(R.id.test_Attendee);
        Button organizerButton = findViewById(R.id.test_Organizer);
        Button administratorButton = findViewById(R.id.test_Administrator);



        //***************** IMPORTANT!!!!! ***********************************************************************
        // Each interface will have its own Activity class that will handle everything
        // This is so that we don't unnecessarily populate the main activity and create merge conflicts
        // A package directory has been created for each UI
        // Do Whatever you want to do with the other classes but DON'T TOUCH MainActivity for the time being
        // You may change the .setOnClickListener stuff in regards to the UI that you are implementing
        //*********************************************************************************************************

        attendeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AttendeeActivity.class);
                startActivity(intent);

            }
        });

        organizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrganizerActivity.class);
                startActivity(intent);

            }
        });

        administratorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminPasswordFragment adminLoginPopupFragment = new AdminPasswordFragment();
                adminLoginPopupFragment.show(getSupportFragmentManager(), "admin_login_popup");
            }
        });

    }


}


