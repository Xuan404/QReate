package com.example.qreate.organizer.qrmenu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qreate.R;

import java.util.ArrayList;

/**
 * Show the User a list of Events that he has created
 */
public class OrganizerQREventListActivity extends AppCompatActivity  {


    /**
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_list_screen);

        //Create Event Button
        Button createEventButton = findViewById(R.id.event_list_screen_confirmbutton);



        //Back Button
        ImageButton backButton = findViewById(R.id.event_list_screen_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
