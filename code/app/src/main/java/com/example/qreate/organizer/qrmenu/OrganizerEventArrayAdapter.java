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

import com.example.qreate.R;
import com.example.qreate.administrator.AdministratorEvent;
import com.example.qreate.administrator.EventArrayAdapter;

import java.util.ArrayList;

public class OrganizerEventArrayAdapter extends ArrayAdapter<AdministratorEvent> {
    private int selectedPosition = -1; // Track the selected position

    public OrganizerEventArrayAdapter(Context context, ArrayList<AdministratorEvent> events) {
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
            }
        });
        return view;
    }
}
