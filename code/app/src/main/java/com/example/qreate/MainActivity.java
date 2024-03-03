package com.example.qreate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //Intent intent = new Intent(MainActivity.this, QRMenuScreenActivity.class);
        //startActivity(intent);

        // Find the button by its ID
        Button attendeeButton = findViewById(R.id.test_Attendee);
        Button organizerButton = findViewById(R.id.test_Organizer);
        Button administratorButton = findViewById(R.id.test_Administrator);

        // Set a click listener on the button
        attendeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code to execute when the button is clicked

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
                // Code to execute when the button is clicked

            }
        });

    }
}