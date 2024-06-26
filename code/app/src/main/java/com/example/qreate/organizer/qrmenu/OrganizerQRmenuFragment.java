package com.example.qreate.organizer.qrmenu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.example.qreate.organizer.OrganizerActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * Creates and handles all activities within the QR menu screen
 * @author Akib Zaman Choudhury
 */
public class OrganizerQRmenuFragment extends Fragment {
    private com.example.qreate.attendee.profilePicViewModel profilePicViewModel;

    /**
     * Creates the view and inflates the organizer_qr_menu_screen layout
     * This method also handles all button interactions of the QR menu
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.organizer_qr_menu_screen, container, false);

        ImageButton profileButton = view.findViewById(R.id.qr_menu_screen_profile_button);
        initializePicViewModel(view);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

        Button generateButton = view.findViewById(R.id.qr_menu_screen_button_generate_qr_code);
        Button reuseExistingQRButton = view.findViewById(R.id.qr_menu_screen_button_reuse_qr_code);
        Button eventListButton = view.findViewById(R.id.qr_menu_screen_button_event_list);
        Button shareQRButton = view.findViewById(R.id.qr_menu_screen_button_share_qr_code);


        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrganizerQRGeneratorActivity.class);
                startActivity(intent);
            }
        });
        reuseExistingQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrganizerQRReuseExistingActivity.class);
                startActivity(intent);
            }
        });
        eventListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrganizerQREventListActivity.class);
                startActivity(intent);
            }
        });
        shareQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrganizerQRShareActivity.class);
                startActivity(intent);
            }
        });



        return view;
    }

    /**
     * Shows the name injtials for the profile icon
     * @param view
     */
    private void initializePicViewModel(View view){
        profilePicViewModel = new ViewModelProvider(requireActivity()).get(com.example.qreate.attendee.profilePicViewModel.class);

        ImageButton profileButton = view.findViewById(R.id.qr_menu_screen_profile_button);
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
     * Drop down profile menu
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
     *  Handles swiching from current fragment to Account details fragment
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
