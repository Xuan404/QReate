package com.example.qreate.organizer.attendeesmenu;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qreate.R;
import com.example.qreate.administrator.AdministratorEvent;
import com.example.qreate.organizer.qrmenu.OrganizerEventArrayAdapter;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrganizerAttendeeSignupListActivity extends AppCompatActivity {

    OrganizerAttendeeSignupArrayAdapter attendeeArrayAdapter;
    private String eventDocId;
    private ListView list;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_attendee_list_attendee_signups);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        eventDocId = intent.getStringExtra("eventDocId");

        list = findViewById(R.id.attendee_signup_list_screen_attendeelist);
        ImageButton backButton = findViewById(R.id.attendee_signup_list_screen_backbutton);

        loadEvents();

        backButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }));

    }

    public void loadEvents(){

        ArrayList<OrganizerAttendeeSignup> attendees = new ArrayList<>();
        attendeeArrayAdapter = new OrganizerAttendeeSignupArrayAdapter(this, attendees);
        list.setAdapter(attendeeArrayAdapter);

        DocumentReference eventDocRef = db.collection("Events").document(eventDocId);

        eventDocRef.get().addOnSuccessListener(eventDocument -> {
            if (eventDocument.exists()) {
                List<DocumentReference> checkedInAttendees = (List<DocumentReference>) eventDocument.get("checkedin_attendees");
                if (checkedInAttendees != null) {

                    for (DocumentReference attendeeRef : checkedInAttendees) {
                        // Now retrieve each Attendee document to get the User document reference
                        attendeeRef.get().addOnSuccessListener(attendeeDocument -> {
                            if (attendeeDocument.exists()) {
                                DocumentReference userDocRef = attendeeDocument.getDocumentReference("user_document_id");
                                // Finally, retrieve each User document to get the name
                                userDocRef.get().addOnSuccessListener(userDocument -> {
                                    if (userDocument.exists()) {
                                        String name = userDocument.getString("name");
                                        String eventId = userDocument.getId();
                                        attendeeArrayAdapter.add(new OrganizerAttendeeSignup(name, eventId));
                                        attendeeArrayAdapter.notifyDataSetChanged();

                                    }
                                });
                            }
                        });
                    }
                }
            }
        }).addOnFailureListener(e -> {
            // Handle any errors in fetching the Event document
        });


    }

}
