package com.example.qreate.organizer.notificationsmenu;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qreate.R;
import com.example.qreate.organizer.qrmenu.OrganizerEvent;
import com.example.qreate.organizer.qrmenu.OrganizerEventSpinnerArrayAdapter;

import java.util.ArrayList;


/**
 * The following class is responsible for allowing organizers to send notifications
 *
 * Outstanding Issue: Event spinner is set up but not pulling from firebase yet code for sending the notification has also yet to be done
 * @author Denis Soh
 */
public class OrganizerNotificationsSendActivity extends AppCompatActivity {
    ArrayList<OrganizerEvent> events;
    Spinner eventsSpinner;
    OrganizerEventSpinnerArrayAdapter eventSpinnerArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_send_notification_screen);
        events = new ArrayList<OrganizerEvent>();

        addEventsInit();

        eventSpinnerArrayAdapter = new OrganizerEventSpinnerArrayAdapter(this, events);

        //NEED TO GRAB THE ARRAY FROM FIREBASE THEN PARSE IT INTO THIS
        eventsSpinner = findViewById(R.id.send_notifications_screen_spinner);

        eventsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * changes the string in the spinner
             *
             * @param parent the adapter view of the item
             * @param view the current view
             * @param position the current position in spinner
             * @param id the id
             *
             * @return
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        addEventsInit();

        eventSpinnerArrayAdapter.setDropDownViewResource(R.layout.organizer_event_list_recycler_row_layout);

        eventsSpinner.setAdapter(eventSpinnerArrayAdapter);

        //Back Button
        ImageButton backButton = findViewById(R.id.send_notifications_screen_backbutton);
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
            events.add((new OrganizerEvent(cities[i], provinces[i])));
        }
    }
}
