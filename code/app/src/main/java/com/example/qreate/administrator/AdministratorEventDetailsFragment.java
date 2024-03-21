package com.example.qreate.administrator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qreate.R;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class AdministratorEventDetailsFragment extends Fragment {
    private TextView eventName;
    private TextView eventOrganizer;
    private TextView eventDescription;
    private TextView eventDate;
    private TextView eventTime;
    private TextView eventLocation;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.administrator_event_details_page, container, false);
        db = FirebaseFirestore.getInstance();
        eventName = view.findViewById(R.id.event_details_event_name);
        eventOrganizer = view.findViewById(R.id.event_details_event_organizer);
        eventDescription = view.findViewById(R.id.event_details_event_description);
        eventDate = view.findViewById(R.id.event_details_event_date);
        eventTime = view.findViewById(R.id.event_details_event_time);
        eventLocation = view.findViewById(R.id.event_details_event_location);

        return view;

    }
}
