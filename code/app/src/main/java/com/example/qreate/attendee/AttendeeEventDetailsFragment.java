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
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.qreate.AccountProfileScreenFragment;
import com.example.qreate.R;
import com.example.qreate.administrator.AdministratorActivity;
import com.example.qreate.administrator.AdministratorDashboardFragment;
import com.example.qreate.administrator.EventArrayAdapter;
import com.example.qreate.organizer.OrganizerActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A Fragment representing the event details page for attendees.
 * This fragment is used to display the details of an event to the attendee, including
 * information such as event title, description, date, time, and location.
 * Attendees can view  details about the event they are interested in or planning to attend.
 * This fragment is accessed from the attendee's main navigation, typically through selecting an event
 * in a list.
 *
 * @author Akib Zaman Choudhury
 */

public class AttendeeEventDetailsFragment extends Fragment {

    private profilePicViewModel profilePicViewModel;
    /**
     * This method inflates the layout for the events page.
     * It also initiates switching to different fragments from this main event details fragment page.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return attendee event menu screen view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.attendee_event_menu_screen, container, false);

        ImageButton profileButton = view.findViewById(R.id.profile);

        initializePicViewModel(view);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });


        AppCompatButton currentEventsButton = view.findViewById(R.id.button_current_events);
        currentEventsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openCurrentEventsLayout();
            }
        });

        AppCompatButton upcomingEventsButton = view.findViewById(R.id.button_upcoming_events);
        upcomingEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpcomingEventsLayout();
            }

        });

        AppCompatButton otherEventsButton = view.findViewById(R.id.button_other_events);
        otherEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOtherEventsLayout();
            }

        });

        return view;
    }

    /**
     * Initializes the profile picture ViewModel and sets up the profile picture in the UI.
     * This method fetches the profile picture URL or a generated profile picture based on the device ID,
     * then displays it using Glide or directly sets the Bitmap if the picture is generated. Observers
     * are used to react to data changes asynchronously.
     *
     * @param view The View in which the profile picture ImageView exists.
     */
    private void initializePicViewModel(View view){
        profilePicViewModel = new ViewModelProvider(requireActivity()).get(com.example.qreate.attendee.profilePicViewModel.class);

        ImageButton profileButton = view.findViewById(R.id.profile);
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
     * Opens to other events layout by replacing current fragment
     */
    private void openCurrentEventsLayout(){
        //replace fragment with current_events
        Fragment currentEventsFragment = new CurrentEventsFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.attendee_handler_frame, currentEventsFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    /**
     * Opens to upcoming events layout by replacing current fragment
     */
    private void openUpcomingEventsLayout(){
        //replace fragment with current_events
        Fragment upcomingEventsFragment = new UpcomingEventsFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.attendee_handler_frame, upcomingEventsFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    /**
     * Opens to other events layout by replacing current fragment
     */
    private void openOtherEventsLayout(){
        //replace fragment with current_events
        OtherEventsFragment otherEventsFragment = new OtherEventsFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.attendee_handler_frame, otherEventsFragment);
        transaction.addToBackStack(null);
        transaction.commit();

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