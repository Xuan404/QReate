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

/**
 * Array adapter class for signup list
 * @author Akib Zamn Choudhury
 */
public class OrganizerAttendeeSignupArrayAdapter extends ArrayAdapter<OrganizerAttendeeSignup> {

    public OrganizerAttendeeSignupArrayAdapter(Context context, ArrayList<OrganizerAttendeeSignup> attendees) {
        super(context, 0, attendees);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.organizer_attendee_signup_list, parent, false);
        } else {
            view = convertView;
        }

        OrganizerAttendeeSignup attendee = getItem(position);
        TextView attendeeName = view.findViewById(R.id.attendee_name_text);

        attendeeName.setText(attendee.getAttendeeName());


        return view;
    }
}
