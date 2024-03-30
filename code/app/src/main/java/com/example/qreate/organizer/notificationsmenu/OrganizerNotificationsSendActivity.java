package com.example.qreate.organizer.notificationsmenu;

import static java.security.AccessController.getContext;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qreate.R;
import com.example.qreate.organizer.qrmenu.OrganizerEvent;
import com.example.qreate.organizer.qrmenu.OrganizerEventSpinnerArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * The following class is responsible for allowing organizers to send notifications
 *
 * Outstanding Issue: Event spinner is set up but not pulling from firebase yet code for sending the notification has also yet to be done
 * @author Denis Soh
 */
public class OrganizerNotificationsSendActivity extends AppCompatActivity {
    ArrayList<OrganizerEvent> events;
    private FirebaseFirestore db;
    private Button testButton;
    private OrganizerEvent selectedEvent;
    Spinner eventsSpinner;
    OrganizerEventSpinnerArrayAdapter eventSpinnerArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_send_notification_screen);
        db = FirebaseFirestore.getInstance();
        events = new ArrayList<OrganizerEvent>();

        addEventsInit();

        //eventSpinnerArrayAdapter = new OrganizerEventSpinnerArrayAdapter(this, events);

        //NEED TO GRAB THE ARRAY FROM FIREBASE THEN PARSE IT INTO THIS

        /*eventsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            *//**
             * changes the string in the spinner
             *
             * @param parent the adapter view of the item
             * @param view the current view
             * @param position the current position in spinner
             * @param id the id
             *
             * @return
             *//*
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        testButton = findViewById(R.id.send_notifications_screen_spinner);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();
            }
        });

        /*eventSpinnerArrayAdapter.setDropDownViewResource(R.layout.organizer_event_list_recycler_row_layout);

        eventsSpinner.setAdapter(eventSpinnerArrayAdapter);*/

        //Back Button
        ImageButton backButton = findViewById(R.id.send_notifications_screen_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//      // Dummy code (modify it after dennis has completed events)
//    public void sendNotificationToAllUsers(String title, String message) {
//        store db = FirestoreClient.getFirestore();
//        ApiFuture<QuerySnapshot> query = db.collection("Attendees").get();
//        List<String> fcmTokens = new ArrayList<>();
//
//        // Retrieve all documents from the "Attendees" collection
//        QuerySnapshot querySnapshot = query.get();
//        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
//        for (QueryDocumentSnapshot document : documents) {
//            String fcmToken = document.getString("fcmToken");
//            if (fcmToken != null && !fcmToken.isEmpty()) {
//                fcmTokens.add(fcmToken);
//            }
//        }
//
//        // Prepare a message to be sent to all FCM tokens
//        MulticastMessage message = MulticastMessage.builder()
//                .putData("title", title)
//                .putData("message", message)
//                .addAllTokens(fcmTokens)
//                .build();
//
//        // Send the message
//        BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
//        // You can log or handle the response if needed
//    }


    //Temporary to test swap this with the firebase data
    private void addEventsInit(){


        // TODO THIS CODE CRASHES IF THERES NO DETAIL OR DATE SO I COMMENTED IT OUT UNCOMMENT WHEN DATA IS FIXED
        String device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        db.collection("Organizers")
                .whereEqualTo("device_id", device_id)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {

                                // Since the unique ID is unique, we only expect one result
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                                List<DocumentReference> referenceArray = (List<DocumentReference>) document.get("events_list");

                                //assert createdEvents != null;
                                for (DocumentReference reference : referenceArray) {
                                    reference.get().addOnCompleteListener(referencedTask -> {
                                        if (referencedTask.isSuccessful()) {
                                            DocumentSnapshot referencedDocument = referencedTask.getResult();
                                            if (referencedDocument.exists()) {
                                                //TODO description/dates are not set in most firebase stuff this will cause it to crash
                                                String eventName = referencedDocument.getString("name");
                                                //String eventDetails = document.getString("description");
                                                //String eventDate = document.getString("date");
                                                String eventOrganizer = referencedDocument.getString("organizer");
                                                String eventID = referencedDocument.getId();
                                                events.add(new OrganizerEvent(eventName, "details", "date", eventOrganizer, eventID));
                                            } else {
                                                System.out.println("Referenced document does not exist");
                                            }
                                        } else {
                                            System.out.println("Error fetching referenced document: " + referencedTask.getException());
                                        }
                                    });
                                }

                            } else {
                                Log.d("Firestore", "No such document");
                            }
                        } else {
                            Log.d("Firestore", "get failed with ", task.getException());
                        }
                    }
                });
    }
    private void showOptionsDialog() {
        final String[] items = new String[events.size()];
        for (int i = 0; i < events.size(); i++) {
            items[i] = events.get(i).getEvent();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Events");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                testButton.setText(items[which]);
                selectedEvent = events.get(which);
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();

        // Make sure the dialog has a window
        if (dialog.getWindow() != null) {
            // Create a new GradientDrawable with rounded corners
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(50f); // Set the corner radius
            drawable.setColor(Color.WHITE); // Set the background color (change if needed)

            // Set the GradientDrawable as the background of the dialog's window
            dialog.getWindow().setBackgroundDrawable(drawable);
        }

        dialog.show();
    }
}
