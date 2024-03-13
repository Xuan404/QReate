package com.example.qreate.organizer.notificationsmenu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.qreate.R;
import com.example.qreate.organizer.notificationsmenu.OrganizerNotificationsSendActivity;
import com.example.qreate.organizer.qrmenu.OrganizerQRmenuFragment;


/**
 * The following class allows the User(Organizer) to send notifications to all Attendees
 * @author Akib Zaman Choudhurhy
 */
public class OrganizerNotificationsMenuFragment extends Fragment {

    /**
     * Creates the view and inflates the organizer_notifications_menu_screen layout
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

        View view = inflater.inflate(R.layout.organizer_notifications_menu_screen, container, false);

        ImageButton profileButton = view.findViewById(R.id.notifications_menu_screen_profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });



        Button button = view.findViewById(R.id.notifications_menu_screen_send_notifications);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrganizerNotificationsSendActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }


//    @Override
//    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        getActivity().getMenuInflater().inflate(R.menu.profile_menu, menu);
//
//        //Colors the text color white
//        for (int i = 0; i < menu.size(); i++) {
//            MenuItem menuItem = menu.getItem(i);
//            SpannableString s = new SpannableString(menuItem.getTitle());
//            s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
//            menuItem.setTitle(s);
//        }
//    }





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
