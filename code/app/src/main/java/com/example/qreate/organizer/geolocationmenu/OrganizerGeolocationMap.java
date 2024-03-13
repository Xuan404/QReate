package com.example.qreate.organizer.geolocationmenu;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qreate.R;


public class OrganizerGeolocationMap extends AppCompatActivity {

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
    }
}
