package com.example.qreate.organizer.notificationsmenu;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.qreate.R;
import com.example.qreate.organizer.qrmenu.OrganizerEvent;
import com.example.qreate.organizer.qrmenu.OrganizerEventSpinnerArrayAdapter;
import com.example.qreate.organizer.qrmenu.OrganizerQRGeneratorActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * The following class is responsible for allowing organizers to send notifications
 *
 * Outstanding Issue: Event spinner is set up but not pulling from firebase yet code for sending the notification has also yet to be done
 * @author Denis Soh
 */
public class OrganizerNotificationsSendActivity extends AppCompatActivity {

    private String documentId;
    private String eventName;
    private String eventMessage;
    private final String SERVER_KEY = "AAAAt7_rZSU:APA91bGBInjDrDS8HBa2NTqp6YSiczU_GaR5ejD59JlmFB4RjwMOHEAIJ_HDY-BZ8MdUms7PrQVB_yjO_ja7ThicTwlCkwxdxXAWtWkUhsXJvwJPgWAgkZEm1QFQjHc2UOOJ1uEZI7Oi";
    ArrayList<OrganizerEvent> events;
    private FirebaseFirestore db;
    private Button eventSelectButton;
    private OrganizerEvent selectedEvent;
    private ExecutorService executorService; // An ExecutorService that can schedule commands to run after a given delay, or to execute periodically.


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_send_notification_screen);

        executorService = Executors.newSingleThreadExecutor();

        db = FirebaseFirestore.getInstance();

        events = new ArrayList<OrganizerEvent>();
        addEventsInit();


        eventSelectButton = findViewById(R.id.send_notifications_screen_spinner);
        eventSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();
            }
        });
        
        //Back Button
        ImageButton backButton = findViewById(R.id.send_notifications_screen_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Button confirmButton = findViewById(R.id.send_notifications_screen_condirmbutton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendPushNotification(FCMtoken);

                if (documentId == null) {
                    Toast.makeText(OrganizerNotificationsSendActivity.this, "Please select an event", Toast.LENGTH_SHORT).show();
                    return;
                }

                eventName = selectedEvent.getEvent();
                EditText editTextMessage = findViewById(R.id.send_notifications_screen_text_box);
                eventMessage = editTextMessage.getText().toString();

                retrieveFcmTokens(documentId);
                createAnnouncement(eventName, eventMessage, documentId);

                //sendFcmMessage(SERVER_KEY, FCMtoken, eventName, eventMessage);


            }
        });


    }

    public void createAnnouncement(String title, String description, String documentId) {

        // Create a new document with title and description
        Map<String, Object> announcement = new HashMap<>();
        announcement.put("title", title);
        announcement.put("description", description);
        announcement.put("event_doc_id", documentId);

        // Add a new document with a generated ID
        db.collection("Announcements").add(announcement);
    }



    // Retrieves the fcm token and sends a notification
    public void retrieveFcmTokens(String eventId) {

        DocumentReference eventDocRef = db.collection("Events").document(eventId);
        //List<String> fcmTokens = new ArrayList<>();

        eventDocRef.get().addOnSuccessListener(eventDocument -> {
            // Extract the signedup_attendees list, which contains maps
            List<Map<String, Object>> signedUpAttendees = (List<Map<String, Object>>) eventDocument.get("signedup_attendees");
            if (signedUpAttendees != null) {
                for (Map<String, Object> attendeeInfo : signedUpAttendees) {
                    // The attendeeRef is a DocumentReference
                    DocumentReference attendeeRef = (DocumentReference) attendeeInfo.get("attendeeRef");

                    // Retrieve the attendee document by the reference
                    attendeeRef.get().addOnSuccessListener(attendeeDocument -> {
                        if (attendeeDocument.exists()) {
                            String fcmToken = attendeeDocument.getString("fcm_token");
                            //fcmTokens.add(fcmToken);
                            sendFcmMessage(SERVER_KEY, fcmToken, eventName, eventMessage);

                        }
                    }).addOnFailureListener(e -> {
                            // Handle failure to retrieve the attendee document
                    });
                }
            }

        }).addOnFailureListener(e -> {
            // Handle failure to retrieve the event document
        });

    }



    // Sends notification to the fcm tokens passed
    public void sendFcmMessage(String serverKey, String deviceToken, String messageTitle, String messageBody) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://fcm.googleapis.com/fcm/send");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Authorization", "key=" + serverKey);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    // JSON payload
                    String jsonPayload = "{\"to\":\"" + deviceToken + "\","
                            + "\"notification\":{"
                            + "\"title\":\"" + messageTitle + "\","
                            + "\"body\":\"" + messageBody + "\""
                            + "},"
                            + "\"data\":{"
                            + "\"customKey1\":\"value1\","
                            + "\"customKey2\":\"value2\""
                            + "}}";

                    // Sending the request
                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(jsonPayload.getBytes("UTF-8"));
                    outputStream.close();


                    int responseCode = conn.getResponseCode();

                    InputStream inputStream = responseCode == HttpURLConnection.HTTP_OK
                            ? conn.getInputStream()
                            : conn.getErrorStream();

                    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Log the response code and response message
                    Log.d("FCM_RESPONSE", "Response Code: " + responseCode);
                    Log.d("FCM_RESPONSE_BODY", "Response: " + response.toString());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle the error on the UI thread if needed
                    Log.e("FCM_ERROR", "Exception sending FCM message", e);
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }


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
                eventSelectButton.setText(items[which]);
                selectedEvent = events.get(which);
                documentId = selectedEvent.getDocumentID();
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
