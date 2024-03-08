package com.example.qreate.organizer;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qreate.R;

/**
 * The following class allows the user to see the map and check attendee checkin locations
 *
 * @author Akib Zaman Choudhurhy
 */
public class GeolocationMenuFragment extends Fragment {

    /**
     * Creates the view and inflates the organizer_geolocation_menu_screen layout
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

        registerForContextMenu(profileButton); //floating profile menu

        return view;
    }

    /**
     * Implements the profile drop down menu
     *
     * @param menu The context menu that is being built
     * @param v The view for which the context menu is being built
     * @param menuInfo Extra information about the item for which the
     *            context menu should be shown. This information will vary
     *            depending on the class of v.
     */
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.profile_menu, menu);

        //Colors the text color white
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            SpannableString s = new SpannableString(menuItem.getTitle());
            s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
            menuItem.setTitle(s);
        }
    }
}
