package com.example.qreate.administrator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.qreate.R;

import java.util.ArrayList;
import java.util.List;

public class AdministratorDashboardFragment extends Fragment {
    private Button imagesButton;
    private Button profilesButton;
    private Button eventsButton;
    private ListView list;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.administrator_dashboard, container, false);

        eventsButton = view.findViewById(R.id.events);
        profilesButton = view.findViewById(R.id.profiles);
        imagesButton = view.findViewById(R.id.images);

        list = view.findViewById(R.id.list);

        // Set click listeners for each button
        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventsButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_button));
                profilesButton.setBackgroundColor(ContextCompat.getColor(getContext(), com.google.zxing.client.android.R.color.zxing_transparent));
                imagesButton.setBackgroundColor(ContextCompat.getColor(getContext(), com.google.zxing.client.android.R.color.zxing_transparent));

                ArrayList<Event> events = new ArrayList<>();
                events.add(new Event("Event A", "Organizer A"));
                events.add(new Event("Event B", "Organizer B"));
                events.add(new Event("Event C", "Organizer C"));
                events.add(new Event("Event D", "Organizer D"));
                events.add(new Event("Event E", "Organizer E"));

                EventArrayAdapter arrayAdapter = new EventArrayAdapter(getContext(), events);
                list.setAdapter(arrayAdapter);
                list.setVisibility(View.VISIBLE);
            }
        });

        profilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilesButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_button));
                imagesButton.setBackgroundColor(ContextCompat.getColor(getContext(), com.google.zxing.client.android.R.color.zxing_transparent));
                eventsButton.setBackgroundColor(ContextCompat.getColor(getContext(), com.google.zxing.client.android.R.color.zxing_transparent));

                ArrayList<Profile> profiles = new ArrayList<>();
                profiles.add(new Profile("Profile 1 Name", R.drawable.profile));
                profiles.add(new Profile("Profile 2 Name",R.drawable.profile));
                profiles.add(new Profile("Profile 3 Name", R.drawable.profile));
                profiles.add(new Profile("Profile 4 Name", R.drawable.profile));
                profiles.add(new Profile("Profile 5 Name", R.drawable.profile));

                ProfileArrayAdapter arrayAdapter = new ProfileArrayAdapter(getContext(), profiles);
                list.setAdapter(arrayAdapter);
                list.setVisibility(View.VISIBLE);
            }
        });

        imagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagesButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_button));
                eventsButton.setBackgroundColor(ContextCompat.getColor(getContext(), com.google.zxing.client.android.R.color.zxing_transparent));
                profilesButton.setBackgroundColor(ContextCompat.getColor(getContext(), com.google.zxing.client.android.R.color.zxing_transparent));

                ArrayList<Image> images = new ArrayList<>();
                images.add(new Image("Profile 1 Image", R.drawable.profile));
                images.add(new Image("Profile 2 Image",R.drawable.profile));
                images.add(new Image("Profile 3 Image", R.drawable.profile));
                images.add(new Image("Poster 1 Image", R.drawable.poster));
                images.add(new Image("Poster 2 Image", R.drawable.poster));

                ImageArrayAdapter arrayAdapter = new ImageArrayAdapter(getContext(), images);
                list.setAdapter(arrayAdapter);
                list.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventsButton = view.findViewById(R.id.events);

        // Programmatically clicking the "Events" button
        eventsButton.performClick();
    }

}