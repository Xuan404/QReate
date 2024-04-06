package com.example.qreate.organizer.attendeesmenu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qreate.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class OrganizerAttendeeCheckinListActivity extends AppCompatActivity {

    OrganizerAttendeeCheckinArrayAdapter attendeeArrayAdapter;
    private String eventDocId; // Dummy data
    private ListView list;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_attendee_list_attendee_checkins);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        eventDocId = intent.getStringExtra("eventDocId");

        list = findViewById(R.id.attendee_checkin_list_screen_attendeelist);
        ImageButton backButton = findViewById(R.id.attendee_checkin_list_screen_backbutton);

        loadEvents();

        backButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }));

    }

    public void loadEvents(){

        ArrayList<OrganizerAttendeeCheckin> attendees = new ArrayList<>();
        attendeeArrayAdapter = new OrganizerAttendeeCheckinArrayAdapter(this, attendees);
        list.setAdapter(attendeeArrayAdapter);
        DocumentReference eventDocRef = db.collection("Events").document(eventDocId);

        eventDocRef.get().addOnSuccessListener(eventDocument -> {

            if (eventDocument.exists()) {

                // Extract the signedup_attendees list, which contains Maps
                List<Map<String, Object>> signedUpAttendees = (List<Map<String, Object>>) eventDocument.get("signedup_attendees");
                if (signedUpAttendees != null) {

                    for (Map<String, Object> attendeeInfo : signedUpAttendees) {
                        // Extract the attendeeRef which is a DocumentReference
                        DocumentReference attendeeRef = (DocumentReference) attendeeInfo.get("attendeeRef");
                        Integer checkInCount = ((Number) attendeeInfo.get("checkInCount")).intValue();

                        // Retrieve the user_document_id from the Attendee document
                        attendeeRef.get().addOnSuccessListener(attendeeDocument -> {
                            if (attendeeDocument.exists()) {
                                DocumentReference userRef = (DocumentReference) attendeeDocument.get("user_document_id");

                                // Retrieve the User document and extract the name field
                                userRef.get().addOnSuccessListener(userDocument -> {
                                    if (userDocument.exists()) {
                                        String name = userDocument.getString("name");
                                        String eventId = userDocument.getId();
                                        attendeeArrayAdapter.add(new OrganizerAttendeeCheckin(name, eventId, checkInCount));
                                        attendeeArrayAdapter.notifyDataSetChanged();
                                    }
                                }).addOnFailureListener(e -> {
                                    // Handle failure in retrieving the User document
                                });
                            }
                        }).addOnFailureListener(e -> {
                            // Handle failure in retrieving the Attendee document
                        });
                    }


                }
            }
        }).addOnFailureListener(e -> {
            // Handle failure in retrieving the Event document
        });


    }




}
