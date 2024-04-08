package com.example.qreate.administrator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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
import com.bumptech.glide.request.RequestOptions;
import com.example.qreate.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A Fragment that displays detailed information about a user profile, including the profile's image, name,
 * contact information, and homepage URL. The details are retrieved from Firebase Firestore based on the provided
 * profile ID.
 */
public class AdministratorProfileDetailsFragment extends Fragment {
    private ImageView profileImage;
    private TextView profileName;
    private TextView profileMobileNum;
    private TextView profileEmail;
    private TextView profileHomepage;
    private FirebaseFirestore db;
    private String profileId;

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
        View view = inflater.inflate(R.layout.administrator_profile_details_page, container, false);
        db = FirebaseFirestore.getInstance();
        profileImage = view.findViewById(R.id.profile_details_pfp);
        profileName = view.findViewById(R.id.profile_details_name);
        profileMobileNum = view.findViewById(R.id.profile_details_phone_num);
        profileEmail = view.findViewById(R.id.profile_details_email);
        profileHomepage = view.findViewById(R.id.profile_details_homepage);

        Bundle arguments = getArguments();
        if (arguments != null) {
            profileId = arguments.getString("profileId");
        }

        if (profileId != null) {
            db.collection("Users").document(profileId)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Extract event details from the document and update the UI
                                profileName.setText(document.getString("name"));
                                profileMobileNum.setText(document.getString("phone_number"));
                                profileEmail.setText(document.getString("email"));
                                profileHomepage.setText(document.getString("homepage"));
                                // query image once they have figured it out
                                String profilePicUrl = document.getString("profile_pic");
                                String generatedPicBase64 = document.getString("generated_pic");

                                if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                                    // If there is a profile picture URL, use Glide to load it
                                    Glide.with(this)
                                            .load(profilePicUrl)
                                            .apply(new RequestOptions().circleCrop())
                                            .into(profileImage);
                                } else if (generatedPicBase64 != null && !generatedPicBase64.isEmpty()) {
                                    // If profile_pic is not available, decode generated_pic from Base64
                                    byte[] decodedString = Base64.decode(generatedPicBase64, Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    profileImage.setImageBitmap(decodedByte);
                                } else {
                                    // Fallback placeholder if no image is available
                                    profileImage.setImageResource(R.drawable.profile);
                                }
                            } else {
                                Log.d("Firestore", "Error getting documents: ", task.getException());
                            }
                        } else {
                            Log.d("Firestore", "Task Failure: ", task.getException());

                        }
                    });
        }
        return view;

    }

    /**
     * Creates a new instance of AdministratorProfileDetailsFragment with the specified profile ID.
     * This method allows for creating fragment instances pre-populated with necessary data.
     *
     * @param profileId The unique ID of the profile whose details are to be displayed.
     * @return A new instance of AdministratorProfileDetailsFragment.
     */
    public static AdministratorProfileDetailsFragment newInstance(String profileId) {
        AdministratorProfileDetailsFragment fragment = new AdministratorProfileDetailsFragment();
        Bundle args = new Bundle();
        args.putString("profileId", profileId);
        fragment.setArguments(args);
        return fragment;
    }
}
