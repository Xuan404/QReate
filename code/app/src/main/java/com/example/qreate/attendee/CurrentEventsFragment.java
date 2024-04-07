package com.example.qreate.attendee;

import androidx.fragment.app.Fragment;

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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.qreate.AccountProfileScreenFragment;
import com.example.qreate.R;
import com.example.qreate.administrator.AdministratorEvent;
import com.example.qreate.administrator.EventArrayAdapter;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CurrentEventsFragment extends Fragment implements EventArrayAdapter.OnEventSelectedListener{
    private EventArrayAdapter eventArrayAdapter;
    private ListView eventList;
    private FirebaseFirestore db;
    private profilePicViewModel profilePicViewModel;
    /**
     * Creates and Inflates the current events view
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return current events view
     */
    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.current_events_listview,container,false);
        AppCompatButton backButton = view.findViewById(R.id.button_back_current_event_details);
        ImageButton profileButton = view.findViewById(R.id.current_events_profic_pic_icon);

        eventList = view.findViewById(R.id.current_event_list);
        db = FirebaseFirestore.getInstance();
        eventArrayAdapter = new EventArrayAdapter(getContext(), new ArrayList<>(), this);
        eventList.setAdapter(eventArrayAdapter);
        initializePicViewModel(view);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                goBackToAttendeeEventDetails();
            }
        });


        return view;

    }

    private void initializePicViewModel(View view){
        profilePicViewModel = new ViewModelProvider(requireActivity()).get(com.example.qreate.attendee.profilePicViewModel.class);
        profilePicViewModel.getGeneratedProfilePic().observe(getViewLifecycleOwner(), bitmap -> {
            ImageButton profileButton = view.findViewById(R.id.current_events_profic_pic_icon);
            profileButton.setImageBitmap(bitmap);

        });

        String deviceId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        profilePicViewModel.fetchGeneratedPic(getContext(), deviceId);

    }


    /**
     * Goes back to main event details page.
     */
    private void goBackToAttendeeEventDetails(){
        getParentFragmentManager().popBackStack();
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

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadAllEvents();
    }

    public void loadAllEvents() {
        String device_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // First, find the attendee document for the current user
        db.collection("Attendees")
                .whereEqualTo("device_id", device_id)
                .limit(1)
                .get()
                .addOnSuccessListener(attendeeQuerySnapshot -> {
                    if (!attendeeQuerySnapshot.isEmpty()) {
                        DocumentSnapshot attendeeDoc = attendeeQuerySnapshot.getDocuments().get(0);
                        List<DocumentReference> signedUpEventsRefs = (List<DocumentReference>) attendeeDoc.get("signup_event_list");
                        if (signedUpEventsRefs != null && !signedUpEventsRefs.isEmpty()) {
                            // Now fetch each event by its reference
                            fetchEventsByReferences(signedUpEventsRefs);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("CurrentEventsFragment", "Error fetching attendee info", e));
    }

    private void fetchEventsByReferences(List<DocumentReference> eventRefs) {
        // Prepare to fetch today's events
        Calendar startOfDay = Calendar.getInstance();
        startOfDay.set(Calendar.HOUR_OF_DAY, 0); // Set hour to midnight
        startOfDay.set(Calendar.MINUTE, 0);
        startOfDay.set(Calendar.SECOND, 0);
        startOfDay.set(Calendar.MILLISECOND, 0);
        Date today = startOfDay.getTime();

        Calendar endOfDay = Calendar.getInstance();
        endOfDay.set(Calendar.HOUR_OF_DAY, 23); // Set hour to almost midnight
        endOfDay.set(Calendar.MINUTE, 59);
        endOfDay.set(Calendar.SECOND, 59);
        endOfDay.set(Calendar.MILLISECOND, 999);
        Date tomorrow = endOfDay.getTime();

        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (DocumentReference ref : eventRefs) {
            tasks.add(ref.get());
        }

        // Wait for all event fetch tasks to complete
        Task<List<DocumentSnapshot>> allTasks = Tasks.whenAllSuccess(tasks);
        allTasks.addOnSuccessListener(documentSnapshots -> {
            ArrayList<AdministratorEvent> events = new ArrayList<>();
            for (DocumentSnapshot document : documentSnapshots) {
                Date eventDate = document.getDate("date");
                if (eventDate != null && eventDate.after(today) && eventDate.before(tomorrow)) {
                    String eventName = document.getString("name");
                    String eventId = document.getId();
                    events.add(new AdministratorEvent(eventName, eventId));
                }
            }
            // Update the ListView
            eventArrayAdapter.clear();
            eventArrayAdapter.addAll(events);
            eventArrayAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onEventSelected() {
        hideBottomNavigationBar(); // Implement this method
        showDetailsNavigationBar(); // Implement this method
    }

    private void hideBottomNavigationBar() {
        // Find the BottomNavigationView and set its visibility to GONE
        ((AttendeeActivity)getActivity()).hideBottomNavigationBar();
    }

    private void showDetailsNavigationBar() {
        // Find the BottomNavigationView and set its visibility to GONE
        ((AttendeeActivity)getActivity()).showDetailsNavigationBar();
    }

    public String getSelectedEventId() {
        return eventArrayAdapter.getSelectedEventId();
    }

    public void removeEventFromList(String eventId) {
        if (eventArrayAdapter != null) {
            for (int i = 0; i < eventArrayAdapter.getCount(); i++) {
                AdministratorEvent event = eventArrayAdapter.getItem(i);
                if (event != null && eventId.equals(event.getId())) {
                    eventArrayAdapter.remove(event);
                    eventArrayAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }
}