package com.example.qreate.organizer.qrmenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qreate.R;
import com.example.qreate.organizer.qrmenu.OrganizerEvent;

import java.util.ArrayList;
import java.util.Objects;
/**
 * The following class is responsible for adapting events into spinners
 *
 * @author Denis Soh
 */
public class OrganizerEventSpinnerArrayAdapter extends ArrayAdapter<OrganizerEvent> {
    private ArrayList<OrganizerEvent> events;
    private Context context;

    /**
     * constructor for the adapter
     *
     * @param context the current context
     * @param events the events to be parsed
     *
     */

    public OrganizerEventSpinnerArrayAdapter(Context context, ArrayList<OrganizerEvent> events){
        super(context,0, events);
        this.events = events;
        this.context = context;
    }


    /**
     * generates view
     *
     * @param parent the viewgroup of the view
     * @param convertView the current view
     * @param position the current position in spinner
     *
     * @return View
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.organizer_spinner_items, parent,false);
        }

        OrganizerEvent event = events.get(position);

        TextView eventName = view.findViewById(R.id.event_name);

        eventName.setText(event.getEvent());

        return view;
    }

    /**
     * generates dropdown view
     *
     * @param parent the viewgroup of the view
     * @param convertView the current view
     * @param position the current position in spinner
     *
     * @return View
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View customDropdownView = LayoutInflater.from(getContext())
                .inflate(R.layout.organizer_spinner_items, parent, false);

        TextView listItem = customDropdownView.findViewById(R.id.event_name);
        listItem.setText(Objects.requireNonNull(getItem(position)).getEvent());

        return customDropdownView;
    }
}
