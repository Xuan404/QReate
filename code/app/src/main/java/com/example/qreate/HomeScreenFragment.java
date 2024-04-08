package com.example.qreate;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.qreate.attendee.GenerateProfilePic;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;

/**
 * This is the homescreen fragment that inflates after the User has updated his information
 * on the edit profile page.
 * If the User has previously logged in and used the app, then the homescreen will
 * automatically become the default landing page for the User
 *
 * @author Akib Zaman Choudhury
 */
public class HomeScreenFragment extends Fragment{
    private String selectedProfilePicUrl;

    /**
     * Creates the view and inflates the home_screen layout
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_screen, container, false);
        retrieveAndSetUserInfo(view);
        return view;
    }

    private void retrieveAndSetUserInfo(View view){

        String device_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ImageView profileGeneratedIV = view.findViewById(R.id.home_screen_empty_profile_pic);


        db.collection("Users")
                .whereEqualTo("device_id", device_id)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {

                                // Since the unique ID is unique, we only expect one result
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                                String name = document.getString("name");
                                String phoneNumber = document.getString("phone_number");
                                String email = document.getString("email");
                                String profileGenPicURL = document.getString("generated_pic");
                                String profilePicUrl = document.getString("profile_pic");

                                TextView nameText = view.findViewById(R.id.home_screen_welcome_user_text);
                                TextView phoneNumberText = view.findViewById(R.id.home_screen_phone_number_text);
                                TextView emailText = view.findViewById(R.id.home_screen_email_text);

                                nameText.setText(name);
                                phoneNumberText.setText(phoneNumber);
                                emailText.setText(email);


                                ImageView profileImageView = view.findViewById(R.id.home_screen_empty_profile_pic);
                                if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                                    // If the profile_pic URL exists, load it into the ImageView using Glide
                                    Glide.with(HomeScreenFragment.this)
                                            .load(profilePicUrl)
                                            .apply(new RequestOptions().circleCrop())
                                            .into(profileImageView);
                                } else {
                                    // If profile_pic URL doesn't exist, generate a profile picture based on initials
                                    assert name != null;
                                    String initials = getInitials(name);
                                    Bitmap generatedProfilePicBitmap = GenerateProfilePic.generateProfilePicture(initials);
                                    profileImageView.setImageBitmap(generatedProfilePicBitmap);
                                }

                            } else {
                                Log.d("Firestore", "No such document");
                            }
                        } else {
                            Log.d("Firestore", "get failed with ", task.getException());
                        }
                    }
                });
    }


    /**
     * Generate initials from user name from Firestore
     * @param name The user's name
     * @return initials of user in string format
     */

    private String getInitials(String name){
        String [] words = name.split("\\s+");
        StringBuilder initials = new StringBuilder();

        //ensure only first and/or last name is entered
        int nameCount = words.length;
        if(nameCount >= 1){
            String firstName = words[0];
            // if no last name dont add a "."
            String lastName = (nameCount > 1) ? words[nameCount -1 ]: "";

            //add first name only first letter
            if(!TextUtils.isEmpty(firstName)){
                for (char c : firstName.toCharArray()){
                    if(Character.isLetter(c)){
                        initials.append(Character.toUpperCase(c));
                        break;
                    }
                }
            }

            //add dot if there is a first and last name
            if(!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName) && isLetters(firstName) && isLetters(lastName)){
                initials.append(".");
            }

            //add last name only first letter
            if(!TextUtils.isEmpty(lastName)){
                for(char c : lastName.toCharArray()){
                    if(Character.isLetter(c)){
                        initials.append(Character.toUpperCase(c));
                        break;
                    }
                }
            }
        }
        return initials.toString();

    }



    /**
     * Checks if name entered by user is letters
     * @param name
     * @return true if name is appropriate, false if not
     */
    private boolean isLetters(String name){
        for(char c: name.toCharArray()){
            if(Character.isLetter(c)){
                return true;
            }
        }
        return false;
    }

    /**
     * Turns bitmap into Base64 in order to store it to Firestore
     * @param profilePictureBitmap a bitmap
     * @return Base64 of the bitmap
     */
    private String encodeBitmap(Bitmap profilePictureBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        profilePictureBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] byteArray = baos.toByteArray();
        String stringBase64 = android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP);
        return stringBase64;

    }



}