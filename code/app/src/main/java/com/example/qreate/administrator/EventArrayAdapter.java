package com.example.qreate.administrator;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.CompoundButtonCompat;

import com.example.qreate.R;

import java.util.ArrayList;

/**
 * Custom ArrayAdapter to display a list of {@link AdministratorEvent} objects within a ListView.
 * It features event names and organizers, along with a radio button for selecting a specific event.
 * The class ensures that only one event can be selected at any given time and provides functionality
 * to reflect these selections visually in the ListView.
 */
public class EventArrayAdapter extends ArrayAdapter<AdministratorEvent> {
    private int selectedPosition = -1; // Track the selected position
    private OnEventSelectedListener mListener;

    /**
     * Constructs a new EventArrayAdapter.
     *
     * @param context  The current context. Used to inflate the layout file.
     * @param events   An ArrayList of {@link AdministratorEvent} objects to display in the list.
     * @param listener Listener to handle events when a specific event is selected from the list.
     */
    public EventArrayAdapter(Context context, ArrayList<AdministratorEvent> events, OnEventSelectedListener listener) {
        super(context, 0, events);
        mListener = listener;
    }

    /**
     * Provides a view for each event in the ListView.
     *
     * @param position     The position of the item within the adapter's data set of the item whose view we want.
     * @param convertView  The old view to reuse, if possible.
     * @param parent       The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.administrator_dashboard_event_list, parent, false);
        } else {
            view = convertView;
        }

        AdministratorEvent event = getItem(position);
        TextView event_name = view.findViewById(R.id.event_name_text);
        RadioButton radioButton = view.findViewById(R.id.choose_event_radio_button);

        event_name.setText(event.getEventName());
        radioButton.setChecked(position == selectedPosition);

        changeRadioColor(view);

        // Handle radio button clicks
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position; // Update the selected position
                notifyDataSetChanged(); // Notify the adapter to update the radio buttons
                mListener.onEventSelected();
            }
        });
        return view;
    }

    /**
     * Interface definition for a callback to be invoked when an event is selected.
     */
    public interface OnEventSelectedListener {
        void onEventSelected();
    }

    /**
     * Returns the ID of the currently selected event.
     *
     * @return The unique document ID of the selected event or {@code null} if no event is selected.
     */
    public String getSelectedEventId() {
        if (selectedPosition != -1) {
            AdministratorEvent selectedEvent = getItem(selectedPosition);
            return selectedEvent.getId();
        }
        return null;
    }

    /**
     * Changes the color of the radio button depending on its checked state.
     * Applies a color state list with defined colors for checked and unchecked states.
     *
     * @param view The current view containing the radio button.
     */
    private void changeRadioColor(View view) {

        RadioButton radioButton = view.findViewById(R.id.choose_event_radio_button);

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
}
