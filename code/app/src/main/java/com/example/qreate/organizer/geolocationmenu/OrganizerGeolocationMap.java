package com.example.qreate.organizer.geolocationmenu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qreate.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class OrganizerGeolocationMap extends AppCompatActivity implements OnMapReadyCallback {

    private String eventDocId;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_geolocation_map_screen);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        eventDocId = intent.getStringExtra("eventDocId");

        //Back Button
        ImageButton backButton = findViewById(R.id.geolocation_map_screen_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.geolocation_map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

//        //Change "Users" collection to the collection for attendees of a specific event afterwards
//        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        GeoPoint geoPoint = document.getGeoPoint("coordinates");
//                        if (geoPoint != null) {
//                            // Add a marker for each user's location
//                            LatLng userLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
//                            googleMap.addMarker(new MarkerOptions().position(userLocation).title(document.getId()));
//                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
//                        }
//                    }
//                } else {
//                    Log.w("locationRetrievel", "Error getting documents.", task.getException());
//                }
//            }
//        });


        DocumentReference eventDocRef = db.collection("Events").document(eventDocId);
        //List<GeoPoint> coordinatesList = new ArrayList<>();

        eventDocRef.get().addOnSuccessListener(eventDocument -> {
            // Extract the checkedin_attendees list, which contains document references
            List<DocumentReference> checkedInAttendees = (List<DocumentReference>) eventDocument.get("checkedin_attendees");
            if (checkedInAttendees != null) {
                for (DocumentReference attendeeRef : checkedInAttendees) {
                    // Retrieve the attendee document
                    attendeeRef.get().addOnSuccessListener(attendeeDocument -> {
                        // Extract the user_document_id reference
                        DocumentReference userDocRef = (DocumentReference) attendeeDocument.get("user_document_id");
                        // Retrieve the user document
                        userDocRef.get().addOnSuccessListener(userDocument -> {
                            if (userDocument.exists()) {
                                // Extract the coordinates field
                                GeoPoint coordinates = userDocument.getGeoPoint("coordinates");
                                if (coordinates != null) {
                                    //coordinatesList.add(coordinates);
                                    // Add a marker for each user's location
                                    LatLng userLocation = new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
                                    googleMap.addMarker(new MarkerOptions().position(userLocation).title(userDocument.getId()));
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                                }
                            }
                        }).addOnFailureListener(e -> {
                            // Handle failure to retrieve the user document
                        });

                    }).addOnFailureListener(e -> {
                        // Handle failure to retrieve the attendee document
                    });
                }
            }

        }).addOnFailureListener(e -> {
            // Handle failure to retrieve the event document
        });




    }






}
