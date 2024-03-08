package com.example.qreate.attendee;

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

public class NotifArrayAdapter extends ArrayAdapter<Notif> {
    private ArrayList<Notif> notifs;
    private Context context;

    public NotifArrayAdapter(Context context, ArrayList<Notif> notifs){
        super(context,0,notifs);
        this.notifs = notifs;
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.attendee_notifications_list_rows, parent,false);
        }

        Notif notif = notifs.get(position);

        TextView notifName = view.findViewById(R.id.notif_description_text);
        TextView organizerName = view.findViewById(R.id.notif_organizer_text);

        notifName.setText(notif.getNotificationDescription());
        organizerName.setText(notif.getOrganizerName());

        return view;
    }

}
