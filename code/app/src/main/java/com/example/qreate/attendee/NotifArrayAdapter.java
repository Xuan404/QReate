package com.example.qreate.attendee;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.CompoundButtonCompat;

import com.example.qreate.R;
import com.example.qreate.administrator.AdministratorEvent;
import com.example.qreate.administrator.EventArrayAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
/**
 * NotifArrayAdapter is a custom ArrayAdapter designed to display notification objects (Notif) within a ListView.
 * This adapter is for displaying each notification's details, such as its description
 * and the organizer's name, in a structured format as defined in the attendee_notifications_list_rows layout.
 *
 * It extends ArrayAdapter to leverage built-in Android functionality while providing a custom
 * view for each item in the list.
 *
 * @author Shraddha Mehta
 */

public class NotifArrayAdapter extends ArrayAdapter<Notif> {
    private ArrayList<Notif> notifs;
    private Context mContext;
    private int selectedPosition = -1;
    private OnNotifSelectedListener mListener;

    /**
     * Constructs a new NotifArrayAdapter.
     *
     * @param context The current context. Used to inflate the layout file.
     * @param notifs An ArrayList of Notif objects to display in a list.
     */
    public NotifArrayAdapter(Context context, ArrayList<Notif> notifs, NotifArrayAdapter.OnNotifSelectedListener listener) {
        super(context, 0, notifs);
        mListener = listener;
        this.notifs = notifs;
        this.mContext=context;
    }
    /**
     * Provides a view for an AdapterView
     * Checks if an existing view is being reused, otherwise inflates the view.
     *
     *
     * @param position The position in the list of data that should be displayed in the list item view.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View viewItem = convertView;

        if(viewItem == null){
            viewItem = LayoutInflater.from(mContext).inflate(R.layout.attendee_notifications_list_rows, parent,false);
        }

        Notif currentNotif = notifs.get(position);

        TextView notifDescriptionText = viewItem.findViewById(R.id.notif_description_text);
        TextView notifTitleText = viewItem.findViewById(R.id.notif_title_text);
        RadioButton radioButton = viewItem.findViewById(R.id.notif_radio_button);

        //set the text
        notifDescriptionText.setText(currentNotif.getNotificationDescription());
        notifTitleText.setText(currentNotif.getTitle());

        radioButton.setChecked(position == selectedPosition);
        changeRadioColor(viewItem);

        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position; // Update the selected position
                notifyDataSetChanged(); // Notify the adapter to update the radio buttons
                mListener.onNotifSelected();
            }
        });

        return viewItem;
    }

    /**
     * Changes the color of the radio button to indicate selection status.
     * This method customizes the appearance of the radio button based on its checked state.
     *
     * @param view The View containing the radio button whose color needs to be changed.
     */
    private void changeRadioColor(View view) {

        RadioButton radioButton = view.findViewById(R.id.notif_radio_button);

        // Define the color state list for checked and unchecked states
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked state
                        new int[]{android.R.attr.state_checked} // checked state
                },
                new int[]{
                        Color.parseColor("#CCCCCC"), // gray color for unchecked state in hex
                        Color.parseColor("#FCA311") // red color for checked state in hex
                }
        );


        // Apply the color state list to the RadioButton
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            radioButton.setButtonTintList(colorStateList);
        } else {
            CompoundButtonCompat.setButtonTintList(radioButton, colorStateList); // Support library for pre-Lollipop
        }


    }

    /**
     * Returns the ID of the selected notification.
     *
     * @return The ID of the selected notification, or null if no notification is selected.
     */
    public String getSelectedNotifId() {
        if (selectedPosition != -1) {
            Notif selectedNotif = getItem(selectedPosition);
            return selectedNotif.getId();
        }
        return null;
    }

    /**
     * Interface definition for a callback to be invoked when a notification is selected.
     */
    public interface OnNotifSelectedListener {
        void onNotifSelected();
    }

}
