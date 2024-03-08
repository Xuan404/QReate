package com.example.qreate.administrator;

import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdministratorDashboardFragment extends Fragment {
    private Button imagesButton;
    private Button profilesButton;
    private Button eventsButton;
    private ListView list;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.administrator_dashboard, container, false);

        eventsButton = view.findViewById(R.id.events);
        profilesButton = view.findViewById(R.id.profiles);
        imagesButton = view.findViewById(R.id.images);

        list = view.findViewById(R.id.list);
        db = FirebaseFirestore.getInstance();

        // Set click listeners for each button
        eventsButton.setOnClickListener(new View.OnClickListener() {
            private CollectionReference eventsRef;
            @Override
            public void onClick(View v) {
                eventsButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_button));
                profilesButton.setBackgroundColor(ContextCompat.getColor(getContext(), com.google.zxing.client.android.R.color.zxing_transparent));
                imagesButton.setBackgroundColor(ContextCompat.getColor(getContext(), com.google.zxing.client.android.R.color.zxing_transparent));

                eventsRef = db.collection("Events");

                ArrayList<AdministratorEvent> events = new ArrayList<>();
                EventArrayAdapter arrayAdapter = new EventArrayAdapter(getContext(), events);
                list.setAdapter(arrayAdapter);

                eventsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<AdministratorEvent> eventsList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String eventName = document.getString("name");
                                String eventOrganizer = document.getString("organizer");
                                eventsList.add(new AdministratorEvent(eventName, eventOrganizer));
                            }
                            // Update the adapter with the new list
                            arrayAdapter.clear();
                            arrayAdapter.addAll(eventsList);
                            arrayAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        });

        profilesButton.setOnClickListener(new View.OnClickListener() {
            private CollectionReference profilesRef;
            @Override
            public void onClick(View v) {
                profilesButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_button));
                imagesButton.setBackgroundColor(ContextCompat.getColor(getContext(), com.google.zxing.client.android.R.color.zxing_transparent));
                eventsButton.setBackgroundColor(ContextCompat.getColor(getContext(), com.google.zxing.client.android.R.color.zxing_transparent));

                profilesRef = db.collection("Users");

                ArrayList<AdministratorProfile> profiles = new ArrayList<>();
                ProfileArrayAdapter arrayAdapter = new ProfileArrayAdapter(getContext(), profiles);
                list.setAdapter(arrayAdapter);

                profilesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<AdministratorProfile> profilesList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String profileName = document.getString("name");
                                String profileImage = document.getString("profile_picture");
                                profilesList.add(new AdministratorProfile(profileName, profileImage));
                            }
                            // Update the adapter with the new list
                            arrayAdapter.clear();
                            arrayAdapter.addAll(profilesList);
                            arrayAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        });

        imagesButton.setOnClickListener(new View.OnClickListener() {
            private CollectionReference profilesImagesRef;
            private CollectionReference postersImagesRef;
            @Override
            public void onClick(View v) {
                imagesButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_button));
                eventsButton.setBackgroundColor(ContextCompat.getColor(getContext(), com.google.zxing.client.android.R.color.zxing_transparent));
                profilesButton.setBackgroundColor(ContextCompat.getColor(getContext(), com.google.zxing.client.android.R.color.zxing_transparent));

                profilesImagesRef = db.collection("Users");
                postersImagesRef = db.collection("Events");

                ArrayList<AdministratorImage> images = new ArrayList<>();
                ImageArrayAdapter arrayAdapter = new ImageArrayAdapter(getContext(), images);
                list.setAdapter(arrayAdapter);

                profilesImagesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<AdministratorImage> imagesList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String imageName = document.getString("name");
                                String image = document.getString("profile_picture");
                                imagesList.add(new AdministratorImage(imageName, image));
                            }
                            // Update the adapter with the new list
                            arrayAdapter.addAll(imagesList);
                            arrayAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });

                postersImagesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<AdministratorImage> imagesList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String imageName = document.getString("name");
                                String image = document.getString("profile_picture");
                                imagesList.add(new AdministratorImage(imageName, image));
                            }
                            // Update the adapter with the new list
                            arrayAdapter.addAll(imagesList);
                            arrayAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });
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