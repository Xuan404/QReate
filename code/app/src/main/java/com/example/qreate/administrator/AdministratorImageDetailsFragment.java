package com.example.qreate.administrator;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.qreate.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AdministratorImageDetailsFragment extends Fragment {
    private ImageView image;
    private TextView imageRef;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.administrator_image_details_page, container, false);
        db = FirebaseFirestore.getInstance();
        image = view.findViewById(R.id.image_details_picture);
        imageRef = view.findViewById(R.id.image_details_reference);

        // Retrieve the event ID passed from the previous fragment
        Bundle args = getArguments();
        if (args != null) {
            String imageId = args.getString("imageId");
            int imageType = args.getInt("imageType", -1); // Default to -1 if not found
            if (imageType == AdministratorImage.TYPE_PROFILE) {
                loadProfile(imageId);
            } else if (imageType == AdministratorImage.TYPE_EVENT) {
                loadEvent(imageId);
            }
        }
        return view;
    }

    private void loadProfile(String profileId) {
        if (profileId != null) {
            db.collection("Users").document(profileId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            imageRef.setText(documentSnapshot.getString("name"));
                            String imageUrl = documentSnapshot.getString("profile_pic");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(this).load(imageUrl).into(image);
                            }
                        }
                    }).addOnFailureListener(e -> Log.e("Firestore", "Error loading profile", e));
        }
    }

    private void loadEvent(String eventId) {
        if (eventId != null) {
            db.collection("Events").document(eventId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            imageRef.setText(documentSnapshot.getString("name"));
                            String imagePath = documentSnapshot.getString(("poster"));
                            if (imagePath != null && !imagePath.isEmpty()) {
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference imageRef = storage.getReference(imagePath);

                                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    Glide.with(this).load(uri.toString()).into(image);
                                }).addOnFailureListener(e -> Log.e("Firestore", "Error getting event image", e));
                            }
                        }
                    }).addOnFailureListener(e -> Log.e("Firestore", "Error loading event", e));
        }
    }



    public static AdministratorImageDetailsFragment newInstance(String imageId, int imageType) {
        AdministratorImageDetailsFragment fragment = new AdministratorImageDetailsFragment();
        Bundle args = new Bundle();
        args.putString("imageId", imageId);
        args.putInt("imageType", imageType);
        fragment.setArguments(args);
        return fragment;
    }
}
