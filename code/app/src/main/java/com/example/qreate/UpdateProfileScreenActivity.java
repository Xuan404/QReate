package com.example.qreate;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.qreate.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

/**
 * This class allows the user to change/update his profile picture or generate a default profile picture
 * for the user to use
 * @author Akib Zaman Choudhury
 */
public class UpdateProfileScreenActivity extends AppCompatActivity {

    private ActivityResultLauncher<String> mGetContent;
    private Uri selectedImageUri;

    /**
     * Creates and inflates the update_profile_pic layout
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile_pic);


        ImageButton backButton = findViewById(R.id.update_profile_screen_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        // Handle the returned Uri, for example, set it on an ImageView or save it
                        if (uri != null) {
                            selectedImageUri = uri;
                        }
                    }
                });

        Button chooseImageButton = findViewById(R.id.choose_image); // Replace with your button's ID
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // "image/*" indicates that we want to select any type of image
                mGetContent.launch("image/*");
            }
        });

        AppCompatButton saveChangesButton = findViewById(R.id.save_changes_button);
        saveChangesButton.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            returnIntent.setData(selectedImageUri);
            setResult(RESULT_OK, returnIntent);
            finish();
        });

    }


}
