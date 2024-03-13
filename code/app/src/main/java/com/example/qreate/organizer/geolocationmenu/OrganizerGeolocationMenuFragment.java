package com.example.qreate.organizer.geolocationmenu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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

import com.example.qreate.R;
import com.example.qreate.organizer.qrmenu.OrganizerQRGeneratorActivity;

/**
 * The following class allows the user to see the map and check attendee checkin locations
 *
 * @author Akib Zaman Choudhurhy
 */
public class OrganizerGeolocationMenuFragment extends Fragment {

    /**
     * Creates the view and inflates the organizer_geolocation_menu_screen layout
     * (MIGHT NEED TO IMPLEMENT A SPINNER TO SELECT WHICH EVENT TO SHOW)
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

        View view = inflater.inflate(R.layout.organizer_geolocation_menu_screen, container, false);

        ImageButton profileButton = view.findViewById(R.id.geolocation_menu_screen_profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

        Button seeMap = view.findViewById(R.id.geolocation_menu_screen_see_attendee_checkins);
        seeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrganizerGeolocationMap.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void showPopupMenu(View view) {
        // Initialize the PopupMenu
        PopupMenu popupMenu = new PopupMenu(getActivity(), view); // For Fragment, use getActivity() instead of this
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());

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
                    // Handle Profile Action
                    return true;
                } else if (id == R.id.profile_settings) {
                    // Handle Settings Action
                    return true;
                } else {
                    return false;
                }
            }
        });


        popupMenu.show();
    }

}
