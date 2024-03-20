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

/**
 * The following class allows the administrator to see the Dashboard and view all {@link AdministratorEvent}, {@link AdministratorProfile} and {@link AdministratorImage}. (Deletion functionality will be implemented for Part 4)
 */
public class AdministratorDashboardFragment extends Fragment {
    private ListView list;
    private FirebaseFirestore db;

    /**
     * Creates the view and inflates the administrator_dashboard layout, changing the custom ArrayAdapter ({@link EventArrayAdapter}, {@link ProfileArrayAdapter}, {@link ImageArrayAdapter}) for the ListView according to what the user chooses.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.administrator_dashboard, container, false);

        list = view.findViewById(R.id.list);
        db = FirebaseFirestore.getInstance();
        return view;
    }
    
    public void loadEvents() {
        CollectionReference eventsRef = db.collection("Events");
        ArrayList<AdministratorEvent> events = new ArrayList<>();
        EventArrayAdapter arrayAdapter = new EventArrayAdapter(getContext(), events);
        list.setAdapter(arrayAdapter);

        eventsRef.get().addOnCompleteListener(task -> {
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
        });
    }

    public void loadProfiles() {
        CollectionReference profilesRef = db.collection("Users");
        ArrayList<AdministratorProfile> profiles = new ArrayList<>();
        ProfileArrayAdapter arrayAdapter = new ProfileArrayAdapter(getContext(), profiles);
        list.setAdapter(arrayAdapter);

        profilesRef.get().addOnCompleteListener(task -> {
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
        });
    }

    public void loadImages() {
        CollectionReference profilesImagesRef = db.collection("Users");
        CollectionReference postersImagesRef = db.collection("Events");
        ArrayList<AdministratorImage> images = new ArrayList<>();
        ImageArrayAdapter arrayAdapter = new ImageArrayAdapter(getContext(), images);
        list.setAdapter(arrayAdapter);

        profilesImagesRef.get().addOnCompleteListener(task -> {
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
        });

        postersImagesRef.get().addOnCompleteListener(task -> {
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
        });
    }
}
