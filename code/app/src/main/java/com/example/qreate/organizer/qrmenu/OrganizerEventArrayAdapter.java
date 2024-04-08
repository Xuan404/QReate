package com.example.qreate.organizer.qrmenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.example.qreate.R;
import com.example.qreate.administrator.AdministratorEvent;
import com.example.qreate.administrator.EventArrayAdapter;
import com.example.qreate.attendee.AttendeeSignedUpEventsDetailsFragment;

import java.util.ArrayList;

/**
 * Adapter class for Events
 *
 */
public class OrganizerEventArrayAdapter extends ArrayAdapter<AdministratorEvent> {
    private int selectedPosition = -1; // Track the selected position
    private EventSelectionListener listener;

    public OrganizerEventArrayAdapter(Context context, ArrayList<AdministratorEvent> events) {
        super(context, 0, events);
    }

    /**
     *
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return
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

        // Handle radio button clicks
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedEventId = getItem(position).getId();
                notifyDataSetChanged(); // Notify the adapter to update the radio buttons
                if (listener != null) {
                    listener.onEventSelected(selectedEventId);
                }
            }
        });
        return view;
    }

    /**
     * the function that implements the inferface EventSelectionListener
     * @param listener
     */
    public void setEventSelectionListener(EventSelectionListener listener) {
        this.listener = listener;
    }

    /**
     * interface to keep track of selected event
     */
    public interface EventSelectionListener {
        void onEventSelected(String eventId);
    }



}
