package com.example.qreate.organizer.attendeesmenu;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qreate.R;

public class OrganizerAttendeeSignupListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_attendee_list_attendee_signups);

        ImageButton backButton = findViewById(R.id.attendee_signup_list_screen_backbutton);

        backButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }));

    }

}
