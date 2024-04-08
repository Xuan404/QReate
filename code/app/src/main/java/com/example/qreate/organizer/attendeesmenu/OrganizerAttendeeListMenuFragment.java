package com.example.qreate.organizer.attendeesmenu;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import android.content.DialogInterface;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.qreate.AccountProfileScreenFragment;
import com.example.qreate.R;
import com.example.qreate.organizer.OrganizerActivity;
import com.example.qreate.organizer.OrganizerEvent;
import com.example.qreate.organizer.OrganizerEventSpinnerArrayAdapter;
import com.example.qreate.organizer.geolocationmenu.OrganizerGeolocationMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
*   Handles all activities related to the Attendee List menu
 *   @author Akib Zamn Choudhury
*/
public class OrganizerAttendeeListMenuFragment extends Fragment {
    private com.example.qreate.attendee.profilePicViewModel profilePicViewModel;
    private String documentId;
    ArrayList<OrganizerEvent> events;
    private OrganizerEvent selectedEvent;
    private FirebaseFirestore db;
    private Button testButton;

    private View view;

    OrganizerEventSpinnerArrayAdapter eventSpinnerArrayAdapter;

    /**
     * Creates the view and inflates the organizer_attendee_list_menu_screen layout
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        db = FirebaseFirestore.getInstance();
        view = inflater.inflate(R.layout.organizer_attendee_list_menu_screen, container, false);
        ImageButton profileButton = view.findViewById(R.id.attendee_list_menu_screen_profile_button);
        testButton = view.findViewById(R.id.attendee_list_menu_screen_spinner);
        events = new ArrayList<OrganizerEvent>();
        addEventsInit();
        initializePicViewModel(view);


        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();
            }
        });

        Button attendeeCheckinListButton = view.findViewById(R.id.attendee_list_menu_screen_attendee_checkins);
        Button attendeeSignupListButton = view.findViewById(R.id.attendee_list_menu_screen_attendee_signups);

        attendeeCheckinListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (documentId == null) {
                    Toast.makeText(getContext(), "Please select an event", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getActivity(), OrganizerAttendeeCheckinListActivity.class);
                intent.putExtra("eventDocId", documentId);
                startActivity(intent);
            }
        });
        attendeeSignupListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (documentId == null) {
                    Toast.makeText(getContext(), "Please select an event", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getActivity(), OrganizerAttendeeSignupListActivity.class);
                intent.putExtra("eventDocId", documentId);
                startActivity(intent);
            }
        });


        return view;
    }

    /**
     * Sends local notification to the user based on milestone reached
     * @param notificationId
     * @param title
     * @param content
     */
    public void sendLocalNotification(int notificationId, String title, String content) {
        String channelId = "local_channel_id";

        // Create a notification manager
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

        // For Android 8.0 and higher, register the notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Local Notifications";
            String channelDescription = "This channel is used for local notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            // Register the channel with the system
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification and set the notification content
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), channelId)
                .setSmallIcon(R.mipmap.ic_launcher) // Replace with your own drawable icon
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true); // Remove the notification when tapped


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // Notify the notification manager to trigger the notification
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(notificationId, builder.build());

    }


    /**
     * Updates Stats and checks if milestone has been reached
     */
    private void updateStats() {

        DocumentReference eventDocRef = db.collection("Events").document(documentId);

        eventDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String eventName = documentSnapshot.getString("name");
                // Retrieve and store the checkin_count value
                Number checkinCount = documentSnapshot.getLong("checkin_count");
                // Retrieve and store the signup_count value
                Number signupCount = documentSnapshot.getLong("signup_count");

                TextView checkinTextView = view.findViewById(R.id.attendee_list_menu_screen_checkedin_text);
                TextView totalTextView = view.findViewById(R.id.attendee_list_menu_screen_total_text);
                String checkinText = "Checked in:  " + checkinCount;
                String totalText = "Total:  " + signupCount;
                checkinTextView.setText(checkinText);
                totalTextView.setText(totalText);

                if (checkinCount.intValue() > 0) {
                    sendLocalNotification(1, eventName, "An Attendee has Checked in!");
                } else if (checkinCount.intValue() > 10){
                    sendLocalNotification(1, eventName, "More than 10 Attendee have Checked in !");
                } else if (checkinCount.intValue() > 20){
                    sendLocalNotification(1, eventName, "More than 20 Attendee have Checked in !!");
                } else if (checkinCount.intValue() > 30){
                    sendLocalNotification(1, eventName, "More than 30 Attendee has Checked in !!");

                }


            } else {
                // Handle case where document does not exist
            }
        }).addOnFailureListener(e -> {
            // Handle any errors
        });




    }

    /**
     *  Dialog box for choosing event
     */
    private void showOptionsDialog() {
        final String[] items = new String[events.size()];
        for (int i = 0; i < events.size(); i++) {
            items[i] = events.get(i).getEvent();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Events");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                testButton.setText(items[which]);
                selectedEvent = events.get(which);
                documentId = selectedEvent.getDocumentID();
                updateStats();
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


    /**
     * Initializes the profile picture ViewModel and sets up the observer for profile picture changes.
     *
     * @param view The fragment's root view.
     */

    private void initializePicViewModel(View view){
        profilePicViewModel = new ViewModelProvider(requireActivity()).get(com.example.qreate.attendee.profilePicViewModel.class);

        ImageButton profileButton = view.findViewById(R.id.attendee_list_menu_screen_profile_button);
        String deviceId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        profilePicViewModel.fetchProfilePicUrl(deviceId);

        profilePicViewModel.getProfilePicUrl().observe(getViewLifecycleOwner(), profilePicUrl -> {
            if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                Glide.with(this)
                        .load(profilePicUrl)
                        .apply(new RequestOptions().circleCrop())
                        .into(profileButton);
            } else {
                profilePicViewModel.getGeneratedProfilePic().observe(getViewLifecycleOwner(), bitmap -> {
                    profileButton.setImageBitmap(bitmap);
                });
                profilePicViewModel.fetchGeneratedPic(getContext(), deviceId);
            }
        });

    }


    /**
     * Sets up event inside dialog box
     */
    private void addEventsInit(){
        // TODO THIS CODE CRASHES IF THERES NO DETAIL OR DATE SO I COMMENTED IT OUT UNCOMMENT WHEN DATA IS FIXED
        String device_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
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



    /**
     * Displays a popup menu with options for the user, such as account profile and logout.
     *
     * @param view The view on which the popup menu will be anchored.
     */
    private void showPopupMenu(View view) {
        // Initialize the PopupMenu
        PopupMenu popupMenu = new PopupMenu(getActivity(), view); // For Fragment, use getActivity() instead of this
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());

        //Sets text color to white
        for (int i = 0; i < popupMenu.getMenu().size(); i++) {
            MenuItem item = popupMenu.getMenu().getItem(i);
            SpannableString spanString = new SpannableString(popupMenu.getMenu().getItem(i).getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanString.length(), 0); // Set color to white
            item.setTitle(spanString);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.profile_account) {
                    accountProfile();
                    return true;

                } else if (id == R.id.profile_logout) {
                    getActivity().finish();
                    return true;
                } else {
                    return false;
                }
            }
        });

        popupMenu.show();
    }

    /**
     * Handles the fragment transaction related to the account profile.
     * Hides the bottom navigation bar if the activity is an OrganizerActivity.
     * Replaces the current fragment with an AccountProfileScreenFragment and adds the transaction to the back stack.
     */
    private void accountProfile() {
        //Handles fragment transaction related to the account profile

        ((OrganizerActivity)getActivity()).hideBottomNavigationBar();

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.organizer_handler_frame, new AccountProfileScreenFragment("organizer"));
        transaction.addToBackStack(null); // Add this transaction to the back stack
        transaction.commit();
    }

}
