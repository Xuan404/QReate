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

/**
 * A Fragment that displays detailed information about an image, which can either be a profile picture or an event poster.
 * This includes displaying the image itself and its reference (such as the name of the user or the event).
 * The image and its details are retrieved from Firebase Firestore based on the passed image ID and type.
 */
public class AdministratorImageDetailsFragment extends Fragment {
    private ImageView image;
    private TextView imageRef;
    private FirebaseFirestore db;

    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view
     */
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

    /**
     * Loads and displays the profile image and name from Firebase Firestore given a profile ID.
     * Uses Glide to asynchronously load and display the profile picture.
     *
     * @param profileId The unique document identifier for the profile.
     */
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

    /**
     * Loads and displays the event image (poster) and name from Firebase Firestore given an event ID.
     * Retrieves the image from Firebase Storage and uses Glide to display it.
     *
     * @param eventId The unique document identifier for the event.
     */
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



    /**
     * Creates a new instance of AdministratorImageDetailsFragment with specified image ID and type.
     * This static method facilitates creating fragments with necessary data for fetching and displaying image details.
     *
     * @param imageId   The unique identifier of the image to be displayed.
     * @param imageType Indicates the type of the image, determining whether it's a profile image or an event poster.
     * @return A new instance of AdministratorImageDetailsFragment.
     */
    public static AdministratorImageDetailsFragment newInstance(String imageId, int imageType) {
        AdministratorImageDetailsFragment fragment = new AdministratorImageDetailsFragment();
        Bundle args = new Bundle();
        args.putString("imageId", imageId);
        args.putInt("imageType", imageType);
        fragment.setArguments(args);
        return fragment;
    }
}
