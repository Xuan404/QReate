package com.example.qreate.administrator;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.qreate.AccountProfileScreenFragment;
import com.example.qreate.R;
import com.example.qreate.attendee.profilePicViewModel;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A Fragment representing the dashboard view for administrators. It allows the administrator to view and interact
 * with lists of events, profiles, and images. This fragment provides functionalities such as loading these lists
 * from Firestore, displaying them using custom ArrayAdapters, and navigating to detailed views.
 */
public class AdministratorDashboardFragment extends Fragment implements EventArrayAdapter.OnEventSelectedListener,ProfileArrayAdapter.OnProfileSelectedListener,ImageArrayAdapter.OnImageSelectedListener{
    private ListView list;
    private FirebaseFirestore db;
    private EventArrayAdapter eventArrayAdapter;
    private ProfileArrayAdapter profileArrayAdapter;
    private ImageArrayAdapter imageArrayAdapter;
    private profilePicViewModel profilePicViewModel;

    /**
     * Creates the view and inflates the administrator_dashboard layout, changing the custom ArrayAdapter ({@link EventArrayAdapter}, {@link ProfileArrayAdapter}, {@link ImageArrayAdapter}) for the ListView according to what the user chooses.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.administrator_dashboard, container, false);

        list = view.findViewById(R.id.list);
        db = FirebaseFirestore.getInstance();
        initializePicViewModel(view);

        ImageButton profileButton = view.findViewById(R.id.admin_dashboard_profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });



        return view;
    }

    /**
     * Initializes the ViewModel for managing the profile picture. Sets an observer on the profilePicUrl LiveData.
     * Depending on the URL being present or not, it either fetches the profile picture from the URL or generates
     * a new one.
     *
     * @param view The current view instance of the fragment
     */
    private void initializePicViewModel(View view){
        profilePicViewModel = new ViewModelProvider(requireActivity()).get(com.example.qreate.attendee.profilePicViewModel.class);

        ImageButton profileButton = view.findViewById(R.id.admin_dashboard_profile_button);
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
     * Loads events from the Firestore database and updates the ListView with the fetched data.
     * Uses the EventArrayAdapter for populating the ListView.
     */
    public void loadEvents() {
        CollectionReference eventsRef = db.collection("Events");
        ArrayList<AdministratorEvent> events = new ArrayList<>();
        eventArrayAdapter = new EventArrayAdapter(getContext(), events, this); // Use class field here
        list.setAdapter(eventArrayAdapter);

        eventsRef.get().addOnCompleteListener(task -> {
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

    /**
     * Loads profiles from the Firestore database and updates the ListView with the fetched data.
     * Uses the ProfileArrayAdapter for populating the ListView.
     */
    public void loadProfiles() {
        CollectionReference profilesRef = db.collection("Users");
        ArrayList<AdministratorProfile> profiles = new ArrayList<>();
        profileArrayAdapter = new ProfileArrayAdapter(getContext(), profiles,this);
        list.setAdapter(profileArrayAdapter);

        profilesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<AdministratorProfile> profilesList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String profileName = document.getString("name");
                    String profileImage = document.getString("profile_pic");
                    String generatedPic = document.getString("generated_pic");
                    String profileId = document.getId();
                    profilesList.add(new AdministratorProfile(profileName, profileImage, generatedPic, profileId));
                }
                // Update the adapter with the new list
                profileArrayAdapter.clear();
                profileArrayAdapter.addAll(profilesList);
                profileArrayAdapter.notifyDataSetChanged();


            } else {
                Log.d("Firestore", "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Loads profiles from the Firestore database and updates the ListView with the fetched data.
     * Uses the ProfileArrayAdapter for populating the ListView.
     */
    public void loadImages() {
        CollectionReference profilesImagesRef = db.collection("Users");
        CollectionReference postersImagesRef = db.collection("Events");
        ArrayList<AdministratorImage> images = new ArrayList<>();
        imageArrayAdapter = new ImageArrayAdapter(getContext(), images,this);
        list.setAdapter(imageArrayAdapter);

        profilesImagesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String profileName = document.getString("name");
                    String profile_image = document.getString("profile_pic");
                    String generated_image = document.getString("generated_pic");
                    String profile_id = document.getId();
                    if (profile_image != null && !profile_image.isEmpty()) {
                        images.add(new AdministratorImage(profileName, profile_image, generated_image, profile_id, AdministratorImage.TYPE_PROFILE));
                    }
                }
                imageArrayAdapter.notifyDataSetChanged();
            } else {
                Log.d("Firestore", "Error getting documents: ", task.getException());
            }
        });

        postersImagesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String eventName = document.getString("name");
                    String poster_image = document.getString("poster");
                    String eventId = document.getId();
                    if (poster_image != null && !poster_image.isEmpty()) {
                        images.add(new AdministratorImage(eventName, poster_image, null, eventId, AdministratorImage.TYPE_EVENT));
                    }
                }
                // Update the adapter with the new list
                imageArrayAdapter.notifyDataSetChanged();
            } else {
                Log.d("Firestore", "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Handles navigation to the account profile fragment, hiding the main bottom navigation bar and
     * replacing the current view with the {@link AccountProfileScreenFragment}.
     */
    private void accountProfile() {
        //Handles fragment transaction related to the account profile

        ((AdministratorActivity)getActivity()).hideMainBottomNavigationBar();

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.administrator_handler_frame, new AccountProfileScreenFragment("administrator"));
        transaction.addToBackStack(null); // Add this transaction to the back stack
        transaction.commit();
    }

    /**
     * Displays a popup menu when the profile button is clicked. This menu allows the administrator to navigate
     * to their account profile or log out.
     *
     * @param view The view from which the popup menu is anchored
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
     * Handles the event selection action within the dashboard. This method is triggered when an event is selected
     * from the list of events displayed to the administrator. It performs actions to modify the UI in response to
     * the selection, specifically hiding the main bottom navigation bar and showing the details navigation bar to
     * facilitate further interactions with the selected event.
     */
    @Override
    public void onEventSelected() {
        hideBottomNavigationBar(); // Implement this method
        showDetailsNavigationBar(); // Implement this method
    }


    /**
     * Handles the profile selection action within the dashboard. This method is called when a profile is selected
     * from the list of user profiles shown to the administrator. It updates the UI to reflect this selection by
     * hiding the main bottom navigation bar and presenting the details navigation bar, allowing the administrator
     * to perform actions related to the selected profile.
     */
    @Override
    public void onProfileSelected() {
        hideBottomNavigationBar(); // Implement this method
        showDetailsNavigationBar(); // Implement this method
    }

    /**
     * Manages the image selection action within the dashboard. When an image is selected from the list, this method
     * is invoked to adapt the UI accordingly. It hides the main bottom navigation bar and shows the details
     * navigation bar, providing access to functionality pertinent to the selected image, such as viewing detailed
     * information or initiating image-related operations.
     */
    @Override
    public void onImageSelected() {
        hideBottomNavigationBar(); // Implement this method
        showDetailsNavigationBar(); // Implement this method
    }


    /**
     * Hides the main bottom navigation bar by calling the corresponding method in the {@link AdministratorActivity}.
     */
    private void hideBottomNavigationBar() {
        // Find the BottomNavigationView and set its visibility to GONE
        ((AdministratorActivity)getActivity()).hideMainBottomNavigationBar();
    }

    /**
     * Shows the details navigation bar by calling the corresponding method in the {@link AdministratorActivity}.
     */
    private void showDetailsNavigationBar() {
        // Show the administrator_view_details_handler Bottom Navigation Bar
        // This assumes you have a method in AdministratorActivity to show this specific bar
        ((AdministratorActivity)getActivity()).showDetailsNavigationBar();
    }

    /**
     * Retrieves the document ID of the currently selected event. This method is intended to be used in situations where
     * an event's detailed information needs to be displayed or manipulated.
     *
     * @return The unique document ID of the selected event as a {@link String}. Returns {@code null} if no event is selected.
     */
    public String getSelectedEventId() {
        return eventArrayAdapter.getSelectedEventId();
    }

    /**
     * Retrieves the document ID of the currently selected image. This method facilitates access to specific images,
     * enabling operations such as viewing or deleting the image.
     *
     * @return The unique document ID of the selected image as a {@link String}. Returns {@code null} if no image is selected.
     */
    public String getSelectedImageId() {
        return imageArrayAdapter.getSelectedImageId();
    }


    /**
     * Retrieves the type of the currently selected image. The image type helps in identifying the context or
     * category of the image, such as whether it is a profile picture or an event poster.
     *
     * @return The type of the selected image as an {@code int}. Standard types are defined in the {@link AdministratorImage}
     * class. Returns a default value or {@code -1} if no image is selected or if the type is unspecified.
     */
    public int getSelectedImageType() {
        return imageArrayAdapter.getSelectedImageType();
    }

    /**
     * Retrieves the document ID of the currently selected profile. This method is useful for operations that require
     * reference to a specific user profile, such as editing profile details or viewing profile-related information.
     *
     * @return The unique document ID of the selected profile as a {@link String}. Returns {@code null} if no profile is selected.
     */
    public String getSelectedProfileId() {
        return profileArrayAdapter.getSelectedProfileId();
    }



}