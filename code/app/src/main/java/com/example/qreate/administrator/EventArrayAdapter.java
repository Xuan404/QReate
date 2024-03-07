package com.example.qreate.administrator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qreate.R;

import java.util.ArrayList;

public class EventArrayAdapter extends ArrayAdapter<Event> {
    private int selectedPosition = -1; // Track the selected position
    public EventArrayAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.administrator_dashboard_event_list, parent, false);
        } else {
            view = convertView;
        }

        Event event = getItem(position);
        TextView event_name = view.findViewById(R.id.event_name_text);
        TextView event_organizer = view.findViewById(R.id.event_organizer_text);
        RadioButton radioButton = view.findViewById(R.id.choose_event_radio_button);

        event_name.setText(event.getEventName());
        event_organizer.setText(event.getEventOrganizer());
        radioButton.setChecked(position == selectedPosition);

        // Handle radio button clicks
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position; // Update the selected position
                notifyDataSetChanged(); // Notify the adapter to update the radio buttons
            }
        });
        return view;
    }
}
