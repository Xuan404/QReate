package com.example.qreate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.qreate.R;
import com.example.qreate.attendee.GenerateProfilePic;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the Edit profile screen fragment that allows the user to update his User info.
 * After the use clicks the confirm button
 * the Class validates the inserted User info and updates the database.
 * Furthermore it also removes itself from the backstack of the fragment stack
 *
 * @author Akib Zaman Choudhury
 */
public class EditProfileScreenFragment extends Fragment {

    private String selectedProfilePicUrl;

    private ImageView emptyProfilePic;
    private ActivityResultLauncher<Intent> updateProfileLauncher;

    // Regex pattern for validating an email address
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    // Compile the regex into a Pattern object
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Interface for implementing onFragmentDestroyed().
     * which is methods for handling the UI after the EditProfileScreenFragment is removed from
     * the back stack
     */
    public interface OnFragmentInteractionListener {
        void onFragmentDestroyed();
    }

    /**
     * Interface for the fragment listener
     */
    private OnFragmentInteractionListener mListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Creates view and inflates the edit_profile_info layout.
     * The method also validates the inserted User info and updates the database.
     * Furthermore it removes itself from the backstack of the fragment stack after the
     * user clicks the confirm button
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.edit_profile_info, container, false);
        //This one goes to the next screen, wherease the class AccountProfileScreenFragment destroys itself and returns to previous fragment
        // so two classes, same fragment layout but different behaviour
        // on pressing confirm, validates user details and returns

        emptyProfilePic = view.findViewById(R.id.empty_profile_pic);

        Button confirmDataButton = view.findViewById(R.id.edit_profile_confirm_button);

        confirmDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateUserInfo(view);
            }
        });

        ImageButton plusButton = view.findViewById(R.id.add_photo_button); // Assume your plus button ID is plus_button
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent creation and starting the activity should be inside the onClick method
                Intent intent = new Intent(getActivity(), UpdateProfileScreenActivity.class);
                updateProfileLauncher.launch(intent);
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the launcher
        updateProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            uploadImageToFirebaseStorage(imageUri);
                        }
                    }
                });
    }

    private void uploadImageToFirebaseStorage(Uri fileUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("profile_pics/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        selectedProfilePicUrl = downloadUri.toString();
                        Glide.with(this)
                                .load(downloadUri)
                                .apply(new RequestOptions().circleCrop())
                                .into(emptyProfilePic);
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void authenticateUserInfo(View view) {

        boolean nonEmptyInput = true;
        boolean validName = true;
        boolean validEmail = true;

        EditText editTextName = view.findViewById(R.id.edit_name);
        EditText editTextPhone = view.findViewById(R.id.edit_number);
        EditText editTextEmail = view.findViewById(R.id.edit_email);
        EditText editTextHomepage = view.findViewById(R.id.edit_homepage_website);
        SwitchCompat switchButton = view.findViewById(R.id.edit_profile_switchcompat);

        // Retrieve user input from EditTexts
        String name = editTextName.getText().toString();
        String phone = editTextPhone.getText().toString();
        String email = editTextEmail.getText().toString();
        String homepage = editTextHomepage.getText().toString();


        String initials = getInitials(name);
        Bitmap generatedPicBitmap = GenerateProfilePic.generateProfilePicture(initials);
        String encodedBitmap = encodeBitmap(generatedPicBitmap);

        // Name condition check.
        if (TextUtils.isEmpty(name)) {
            nonEmptyInput = false;
        }

        // Phone condition check
        if (TextUtils.isEmpty(phone)) {
            nonEmptyInput = false;
        }

        // Email condition check.
        if (TextUtils.isEmpty(email)) {
            nonEmptyInput = false;
        }
        if (!isValidEmail(email)) {
            validEmail = false;
        }

        // Homepage condition check
        if (TextUtils.isEmpty(homepage)) {
            nonEmptyInput = false;
        }


        // Button status
        boolean status = switchButton.isChecked();


        if (!nonEmptyInput) {
            Toast.makeText(getActivity(), "Please Enter Your Details", Toast.LENGTH_SHORT).show();

        } else if (!validEmail) {
            Toast.makeText(getActivity(), "Invalid email address", Toast.LENGTH_SHORT).show();

        } else {

            sendUserInfoToFirestore(name, phone, email, homepage, status, encodedBitmap, selectedProfilePicUrl);
            removeFragment(); //removes the fragment
        }

    }



    /**
     * The following method is used to update the database
     *
     * @param name
     * @param phone
     * @param email
     * @param homepage
     * @param status
     * @param generatedEncodedPic
     */
    private void sendUserInfoToFirestore(String name, String phone, String email, String homepage, boolean status, String generatedEncodedPic, String profilePicUrl) {

        // Get a Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("FirestoreConnection", "Firestore has been initialized.");
        // Get the unique Android ID
        String device_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        // Prepare the data to send
        Map<String, Object> device = new HashMap<>();
        device.put("device_id", device_id);
        device.put("name", name);
        device.put("phone_number", phone);
        device.put("email", email);
        device.put("homepage", homepage);
        device.put("allow_coordinates", status);
        device.put("generated_pic", generatedEncodedPic);
        device.put("profile_pic", profilePicUrl);

        // Send the unique ID to Firestore
        db.collection("Users").add(device);

    }

    //bitmap to Base64
    private String encodeBitmap(Bitmap profilePictureBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        profilePictureBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] byteArray = baos.toByteArray();
        String stringBase64 = android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP);
        return stringBase64;

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
     * Validates if the given string is a valid email address.
     *
     * @param email the string to be validated
     * @return true if the string is a valid email address; false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }


    public void removeFragment() {
        //removes fragment from the back stack, in this case its the current fragment
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this); // 'this' refers to the current fragment
        fragmentTransaction.commit();
        fragmentManager.popBackStack(); // This line ensures the fragment is removed from the back stack
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mListener != null) {
            mListener.onFragmentDestroyed();
        }
    }

}
