package com.example.qreate.organizer.geolocationmenu;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class OrganizerGeolocationMap extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_geolocation_map_screen);

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

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Change "Users" collection to the collection for attendees of a specific event afterwards
        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        GeoPoint geoPoint = document.getGeoPoint("coordinates");
                        if (geoPoint != null) {
                            // Add a marker for each user's location
                            LatLng userLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                            googleMap.addMarker(new MarkerOptions().position(userLocation).title(document.getId()));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                        }
                    }
                } else {
                    Log.w("locationRetrievel", "Error getting documents.", task.getException());
                }
            }
        });


    }






}
