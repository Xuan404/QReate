package com.example.qreate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.qreate.administrator.AdministratorActivity;
import com.example.qreate.attendee.AttendeeActivity;
import com.example.qreate.organizer.OrganizerActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import android.content.Context;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String device_id;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Change this to the appropriate layout after Ryan and Shradha have created the xml file
        setContentView(R.layout.activity_main);

        // initializes firestore db and sends the id to db
        sendUniqueIdToFirestore(this);


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
                Intent intent = new Intent(MainActivity.this, AdministratorActivity.class);
                startActivity(intent);
            }
        });

    }


    private void sendUniqueIdToFirestore(Context context) {

        // Get a Firestore instance
        db = FirebaseFirestore.getInstance();
        Log.d("FirestoreConnection", "Firestore has been initialized.");
        // Get the unique Android ID
        device_id = "lola";//Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        // Prepare the data to send
        Map<String, String> device = new HashMap<>();
        device.put("device_id", device_id);

        // Send the unique ID to Firestore
        db.collection("Users").document(device_id).set(device)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestorelola", "DocumentSnapshot successfully written!");
                    // Show a Toast message
                    Toast.makeText(context, "It worked", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error writing document", e);
                    // Optionally, you could also show a Toast on failure
                    Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
                });
    }
}


