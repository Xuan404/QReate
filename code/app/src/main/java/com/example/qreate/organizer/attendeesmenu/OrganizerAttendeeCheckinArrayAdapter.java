package com.example.qreate.organizer.attendeesmenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qreate.R;


import java.util.ArrayList;

public class OrganizerAttendeeCheckinArrayAdapter extends ArrayAdapter<OrganizerAttendeeCheckin> {

    public OrganizerAttendeeCheckinArrayAdapter(Context context, ArrayList<OrganizerAttendeeCheckin> attendees) {
        super(context, 0, attendees);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.organizer_attendee_checkin_list, parent, false);
        } else {
            view = convertView;
        }

        OrganizerAttendeeCheckin attendee = getItem(position);
        TextView attendeeName = view.findViewById(R.id.attendee_name_text);
        TextView attendeeCount = view.findViewById(R.id.attendee_count_text);


        attendeeName.setText(attendee.getAttendeeName());
        attendeeCount.setText(attendee.getCheckinCount());


        return view;
    }

}
