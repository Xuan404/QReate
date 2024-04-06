package com.example.qreate.organizer.qrmenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qreate.R;
import com.example.qreate.administrator.AdministratorActivity;
import com.example.qreate.administrator.AdministratorEvent;
import com.example.qreate.administrator.EventArrayAdapter;
import com.example.qreate.organizer.OrganizerActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Show the User a list of Events that he has created
 */
public class OrganizerQREventListActivity extends AppCompatActivity implements OrganizerQREventListPopupWindow.EventCreationListener, OrganizerEventArrayAdapter.EventSelectionListener{
    private OrganizerQREventListPopupWindow popupWindow;
    private ListView list;
    private FirebaseFirestore db;
    private String device_id;
    private Button createEventButton;

    OrganizerEventArrayAdapter eventArrayAdapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_list_screen);

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                // No more fragments on the stack, make the button visible
                createEventButton.setVisibility(View.VISIBLE);
            }
        });

        //Create Event Button
        createEventButton = findViewById(R.id.event_list_screen_confirmbutton);
        list = findViewById(R.id.event_list_screen_eventlist);
        db = FirebaseFirestore.getInstance();

        loadEvents();

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creating instance of the CustomPopupWindow
                popupWindow = new OrganizerQREventListPopupWindow(OrganizerQREventListActivity.this);
                popupWindow.setEventCreationListener(OrganizerQREventListActivity.this); // Fix applied here
                // Showing the popup window
                popupWindow.showPopupWindow();
            }
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


    @Override
    public void onEventCreated() {
        // Refresh events list
        loadEvents();
    }

    public void onEventSelected(String eventId){
        createEventButton.setVisibility(View.INVISIBLE);
        OrganizerEventDetailsFragment detailsFragment = OrganizerEventDetailsFragment.newInstance(eventId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.event_list_fragment_container, detailsFragment)
                .addToBackStack(null)
                .commit();
    }


    public void loadEvents(){
        ArrayList<AdministratorEvent> events = new ArrayList<>();
        eventArrayAdapter = new OrganizerEventArrayAdapter(this,events);
        eventArrayAdapter.setEventSelectionListener(this);
        list.setAdapter(eventArrayAdapter);
        device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        db.collection("Events")
                .whereEqualTo("org_device_id", device_id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<AdministratorEvent> eventsList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String eventName = document.getString("name");
                            String eventId = document.getId();
                            eventsList.add(new AdministratorEvent(eventName,eventId));
                        }
                        // Update the adapter with the new list
                        eventArrayAdapter.clear();
                        eventArrayAdapter.addAll(eventsList);
                        eventArrayAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }

                });
    }

    // Handle the result from the poster selection when creating an event
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OrganizerQREventListPopupWindow.IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            popupWindow.setImageUri(imageUri);
            String imageName = popupWindow.getFileNameByUri(this, imageUri);
            popupWindow.setImageName(imageName);
        }
    }

}
