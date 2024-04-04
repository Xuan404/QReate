package com.example.qreate.attendee;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.qreate.AccountProfileScreenFragment;
import com.example.qreate.R;
import com.example.qreate.organizer.OrganizerActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A Fragment that displays notifications to the attendee. This class is responsible for
 * fetching notification data from Firestore and displaying it in a ListView. Each notification
 * consists of a message and details, encapsulated in a Notif object. The fragment uses a custom
 * ArrayAdapter (NotifArrayAdapter) to display the notifications in the ListView.
 *
 * @author Shraddha Mehta
 */

public class AttendeeNotificationsFragment extends Fragment {

    private ListView notificationsListView;
    private ArrayList<Notif> notificationsArrayList;
    private NotifArrayAdapter notifArrayAdapter;
    private FirebaseFirestore db;
    private profilePicViewModel profilePicViewModel;

    /**
     * This method inflates the layout for the notifications page and sets up the
     * ListView with a custom ArrayAdapter.
     * It also initiates fetching of notification data from Firestore.
     *
     * @param inflater LayoutInflater: The LayoutInflater object that can be used to inflate
     *                 any views in the fragment.
     * @param container ViewGroup: If non-null, this is the parent view that the fragment's
     *                 UI should be attached to.
     * @param savedInstanceState Bundle: If non-null, this fragment is being re-constructed
     *                 from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.attendee_notifications_page, container, false);

        ImageButton profileButton = view.findViewById(R.id.profile);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

        //initialize data
        notificationsArrayList = new ArrayList<>();
        //set up adapter
        notifArrayAdapter = new NotifArrayAdapter(getContext(), notificationsArrayList);
        //set up the ListView
        notificationsListView = view.findViewById(R.id.notif_list_view);
        initializePicViewModel(view);
        notificationsListView.setAdapter(notifArrayAdapter);

        //set up item click listener
        notificationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


        fetchNotificationsFromFireStore();

        return view;
    }

    /**
     * Fetches notification data from Firestore, parses the data into Notif objects, and adds
     * them to the notificationsArrayList. It then notifies the notifArrayAdapter of the data
     * change to refresh the ListView. If there is an error fetching data, it logs the error.
     */
    private void fetchNotificationsFromFireStore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        //get attendee doc to get list of signed up events
        db.collection("Attendees").whereEqualTo("device_id", userId).limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshot -> {
                    if(!queryDocumentSnapshot.isEmpty()){
                        DocumentSnapshot attendeeDoc = queryDocumentSnapshot.getDocuments().get(0);
                        List<DocumentReference> signedUpEvent = (List<DocumentReference>) attendeeDoc.get("signup_event_list");


                        if(signedUpEvent!= null && !signedUpEvent.isEmpty()) {
                            List<String> signedUpEventIds = signedUpEvent.stream().map(DocumentReference::getId).collect(Collectors.toList());

                            //fetch notifs for these events
                            db.collection("Announcements")
                                    .whereIn("event_doc_id",signedUpEventIds)
                                    .get()
                                    .addOnSuccessListener(announcementQuerySnapshot -> {
                                        notificationsArrayList.clear();

                                        for(QueryDocumentSnapshot announcementsDoc : announcementQuerySnapshot){
                                            String notifDescription = announcementsDoc.getString("description");
                                            String notifTitle = announcementsDoc.getString("title");

                                            Notif notif = new Notif(notifDescription, notifTitle);
                                            notificationsArrayList.add(notif);
                                        }
                                        notifArrayAdapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e-> Log.e("Firestore", "error fetching notifications", e));

                        }
                    }
                })
                .addOnFailureListener(e-> Log.e("Firestore", "Error fetching attendee info ", e));

    }

    private void initializePicViewModel(View view){
        profilePicViewModel = new ViewModelProvider(requireActivity()).get(com.example.qreate.attendee.profilePicViewModel.class);
        profilePicViewModel.getGeneratedProfilePic().observe(getViewLifecycleOwner(), bitmap -> {
            ImageButton profileButton = view.findViewById(R.id.profile);
            profileButton.setImageBitmap(bitmap);
        });

        String deviceId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        profilePicViewModel.fetchGeneratedPic(getContext(), deviceId);

    }


    /**
     * To make the drop down dashboard button functional
     * @param view
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
     * Switching views when user needs to update their profile
     */
    private void accountProfile() {
        //Handles fragment transaction related to the account profile

        ((AttendeeActivity)getActivity()).hideBottomNavigationBar();

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.attendee_handler_frame, new AccountProfileScreenFragment("attendee"));
        transaction.addToBackStack(null); // Add this transaction to the back stack
        transaction.commit();
    }



}
