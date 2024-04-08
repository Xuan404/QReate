package com.example.qreate;

import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.qreate.administrator.AdministratorActivity;
import com.example.qreate.attendee.AttendeeActivity;
import com.example.qreate.attendee.GenerateProfilePic;
import com.example.qreate.organizer.OrganizerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AccountProfileScreenFragment allows users to view and edit their profile information.
 * This fragment is responsible for displaying user details like name, phone number, email, and homepage,
 * as well as allowing the user to update their profile picture and privacy settings regarding their coordinates.
 * Users can also navigate back to their respective activity views after updating their profile.
 */

public class AccountProfileScreenFragment extends Fragment {
    private String selectedProfilePicUrl;

    private ImageView emptyProfilePic;
    private ActivityResultLauncher<Intent> updateProfileLauncher;

    String current_activity;
    // Regex pattern for validating an email address
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    // Compile the regex into a Pattern object
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Constructor for AccountProfileScreenFragment.
     * @param activity The name of the current activity to adjust navigation behavior.
     */
    public AccountProfileScreenFragment(String activity) {
        current_activity = activity;
    }

    /**
     * Inflates the fragment's layout and sets up UI components and event handlers.
     * @param inflater The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return Returns the View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //This one goes to the next screen, wherease the class AccountProfileScreenFragment destroys itself and returns to previous fragment
        // so two classes, same fragment layout but different behaviour
        // on pressing confirm, validates user details and returns

        View view = inflater.inflate(R.layout.edit_profile_info, container, false);
        retrieveAndSetUserInfo(view);

        Button confirmDataButton = view.findViewById(R.id.edit_profile_confirm_button);
        emptyProfilePic = view.findViewById(R.id.empty_profile_pic);

        confirmDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //authenticates and then destroys itself
                authenticateUserInfo(view);
                FragmentManager fragmentManager = getParentFragmentManager(); // Use getSupportFragmentManager() in an Activity
                fragmentManager.popBackStack();
            }
        });

        ImageButton plusButton = view.findViewById(R.id.add_photo_button); // Assume your plus button ID is plus_button
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateProfileScreenActivity.class);
                intent.putExtra("sourceFragment", "AccountProfileScreenFragment");
                updateProfileLauncher.launch(intent);
            }
        });


        return view;
    }

    /**
     * Initializes the updateProfileLauncher used for starting the UpdateProfileScreenActivity
     * to select and upload a new profile picture.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
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

    /**
     * Uploads the selected image file to Firebase Storage and updates the profile picture ImageView.
     * @param fileUri The URI of the selected image file.
     */
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


    /**
     * Turns bitmap into Base64 in order to store it to Firestore
     * @param profilePictureBitmap a bitmap
     * @return Base64 of the bitmap
     */
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
     * Authenticates updated user info
     * @param view
     */
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

        //generate a profile pic if none stored by user
        Bitmap generatedProfilePic = GenerateProfilePic.generateProfilePicture(getInitials(name));

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
            updateUserInfoToFirestore(name, phone, email, homepage, status, generatedProfilePic, selectedProfilePicUrl);
        }

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


    // Sends updated user info to firebase
    /**
     * Updates the current user's information in Firestore with the provided details.
     * This method retrieves the user document based on the device ID, then updates the document
     * with the new user information, including name, phone number, email, homepage URL,
     * status for allowing coordinates, the encoded bitmap for the generated profile picture, and
     * the URL for the profile picture if one has been selected or uploaded.
     *
     * @param name The updated name of the user.
     * @param phone The updated phone number of the user.
     * @param email The updated email address of the user.
     * @param homepage The updated homepage or website URL of the user.
     * @param status The updated status indicating whether the user allows sharing their coordinates.
     * @param generatedProfilePicBitmap The Bitmap of the generated profile picture based on the user's initials.
     * @param profilePicUrl The URL of the selected or uploaded profile picture; null if no picture was selected.
     */
    private void updateUserInfoToFirestore(String name, String phone, String email, String homepage, boolean status, Bitmap generatedProfilePicBitmap, String profilePicUrl) {

        String device_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //encode the profile pic and send to firebase
        String encodedProfilePic = encodeBitmap(generatedProfilePicBitmap);

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
                                String documentId = document.getId();
                                Log.d("Firestore", documentId);

                                // Update the document directly
                                DocumentReference docRef = db.collection("Users").document(documentId);
                                docRef.update("name", name);
                                docRef.update("phone_number", phone);
                                docRef.update("email", email);
                                docRef.update("homepage", homepage);
                                docRef.update("allow_coordinates", status);
                                docRef.update("generated_pic", encodedProfilePic);
                                docRef.update("profile_pic", selectedProfilePicUrl);

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
     * Retrieves the user's information from Firestore and sets it to the UI components.
     * @param view The view containing the UI components.
     */
    private void retrieveAndSetUserInfo(View view){

        String device_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                                String homepage = document.getString("homepage");
                                Boolean status = document.getBoolean("allow_coordinates");
                                String profilePicUrl = document.getString("profile_pic");




                                EditText editTextName = view.findViewById(R.id.edit_name);
                                EditText editTextPhone = view.findViewById(R.id.edit_number);
                                EditText editTextEmail = view.findViewById(R.id.edit_email);
                                EditText editTextHomepage = view.findViewById(R.id.edit_homepage_website);
                                SwitchCompat switchButton = view.findViewById(R.id.edit_profile_switchcompat);

                                editTextName.setText(name);
                                editTextPhone.setText(phoneNumber);
                                editTextEmail.setText(email);
                                editTextHomepage.setText(homepage);
                                switchButton.setChecked(status);

                                ImageView profileImageView = view.findViewById(R.id.empty_profile_pic);
                                if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                                    // If the profile_pic URL exists, load it into the ImageView using Glide
                                    Glide.with(AccountProfileScreenFragment.this)
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
     * Refreshes the user's information when the fragment resumes.
     */
    @Override
    public void onResume() {
        super.onResume();
        // Refresh user information when fragment resumes
        retrieveAndSetUserInfo(getView());
    }

    /**
     * Performs cleanup and navigation adjustments when the view is destroyed.
     */
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {

            if (Objects.equals(current_activity, "organizer")){
                ((OrganizerActivity)getActivity()).showBottomNavigationBar();
            } else if (Objects.equals(current_activity, "attendee")) {
                ((AttendeeActivity)getActivity()).showBottomNavigationBar();
            }else if (Objects.equals(current_activity, "administrator")) {
                ((AdministratorActivity)getActivity()).showMainBottomNavigationBar();
            }
        }
    }
}
