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
 * Custom ArrayAdapter for displaying a list of signed-up Attendees.
 * This ArrayAdapter is specifically designed for OrganizerAttendeeSignup objects.
 *
 */
public class OrganizerAttendeeSignupArrayAdapter extends ArrayAdapter<OrganizerAttendeeSignup> {

    /**
     * Constructs a new OrganizerAttendeeSignupArrayAdapter with the given context and list of attendees.
     *
     * @param context   The current context.
     * @param attendees The list of signed-up Attendees to be displayed.
     */
    public OrganizerAttendeeSignupArrayAdapter(Context context, ArrayList<OrganizerAttendeeSignup> attendees) {
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
     * @return view
     */
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
