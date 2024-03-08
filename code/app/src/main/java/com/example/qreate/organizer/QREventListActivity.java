package com.example.qreate.organizer;

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
public class QREventListActivity extends AppCompatActivity implements CreateEventFragment.AddEventDialogListener {
    ArrayList<Event> events;
    RecyclerView eventsView;
    EventListArrayAdapter eventListArrayAdapter;
    //Not finished recycler view is more annoying than i though gonna probably swap to list view

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
        events = new ArrayList<Event>();

        addEventsInit();

        eventListArrayAdapter = new EventListArrayAdapter(this, events);

        eventsView = findViewById(R.id.event_list_screen_eventlist);
        eventsView.setAdapter(eventListArrayAdapter);

        //Create Event Button
        Button createEventButton = findViewById(R.id.event_list_screen_confirmbutton);

        createEventButton.setOnClickListener(v -> {
            new CreateEventFragment().show(getSupportFragmentManager(), "Create Event");
        });

        //Back Button
        ImageButton backButton = findViewById(R.id.event_list_screen_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //Temporary to test swap this with the firebase data
    private void addEventsInit(){
        String []cities ={"Edmonton", "Vancouver", "Toronto", "Hamilton", "Denver", "Los Angeles"};
        String []provinces = {"AB", "BC", "ON", "ON", "CO", "CA"};
        for(int i=0;i<cities.length;i++){
            events.add((new Event(cities[i], provinces[i])));
        }
    }

    /**
     *
     * @param event
     */
    @Override
    public void addEvent(Event event) {
        //add event stuff into the database TODO
    }
}
