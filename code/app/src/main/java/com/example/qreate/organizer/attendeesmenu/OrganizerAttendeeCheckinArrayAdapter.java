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
 *  Adapter class for attendees
 * @author Akib Zaman Choudhury
 */
public class OrganizerAttendeeCheckinArrayAdapter extends ArrayAdapter<OrganizerAttendeeCheckin> {

    public OrganizerAttendeeCheckinArrayAdapter(Context context, ArrayList<OrganizerAttendeeCheckin> attendees) {
        super(context, 0, attendees);
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
