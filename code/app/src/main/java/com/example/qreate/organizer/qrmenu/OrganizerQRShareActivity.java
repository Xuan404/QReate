package com.example.qreate.organizer.qrmenu;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.example.qreate.R;
import com.example.qreate.organizer.OrganizerEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.widget.CompoundButtonCompat;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class OrganizerQRShareActivity extends AppCompatActivity {

    String documentId ;
    ArrayList<OrganizerEvent> events;
    private Button testButton;
    private OrganizerEvent selectedEvent;
    private FirebaseFirestore db;
    private Bitmap bitmapImage;
    private ImageView imageView;
    private Button buttonShare;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_share_qr_code_screen);

        db = FirebaseFirestore.getInstance();
        changeRadioColor();
        events = new ArrayList<OrganizerEvent>();
        addEventsInit();
        imageView = findViewById(R.id.share_qr_code_qr_image);


        testButton = findViewById(R.id.share_qr_code_spinner);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();
            }
        });


        buttonShare = findViewById(R.id.share_qr_code_sharebutton);
        //buttonShare.setEnabled(false);
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (documentId == null){
                    Toast.makeText(OrganizerQRShareActivity.this, "Error: Select an Event", Toast.LENGTH_SHORT).show();
                    return;
                }
                //buttonShare.setEnabled(false);
                shareImage(bitmapImage);
            }
        });


        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.share_qr_code_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                if (isChecked && documentId != null) {
                    // Sets up field value
                    if (checkedId == R.id.share_qr_code_radio_attendee) {
                        selectImage(documentId, "attendee_qr_code");
                    } else if (checkedId == R.id.share_qr_code_radio_promo) {
                        selectImage(documentId, "promo_qr_code");

                    }
                }
            }
        });

        //Back Button
        ImageButton backButton = findViewById(R.id.share_qr_code_screen_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }



    private void selectImage(String documentId, String field) {
        // Logic to retrieve and display image from Firebase Storage

        DocumentReference eventDocRef = db.collection("Events").document(documentId);
        eventDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Retrieve the image reference path
                String imagePath = documentSnapshot.getString(field);

                if (imagePath != null){
                    buttonShare.setEnabled(false);
                    downloadAndDisplayImage(imagePath);

                } else {
                    buttonShare.setEnabled(false);
                    imageView.setImageResource(R.drawable.qrimage);
                    Toast.makeText(OrganizerQRShareActivity.this, "QRcode image Does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(e -> {
            // Handle any errors in fetching the document
        });



    }


    private void downloadAndDisplayImage(String imagePath) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReference().child(imagePath);

        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            // Convert bytes data to a bitmap
            bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            // Display image in ImageView
            imageView.setImageBitmap(bitmapImage);
            buttonShare.setEnabled(true);


        }).addOnFailureListener(exception -> {
            // Handle any errors
        });
    }

    private void shareImage(Bitmap bitmapImage) {
        // Save the bitmap to cache directory
        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs(); // make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get URI from the saved bitmap
        File imagePath = new File(getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(this, "com.example.qreate.provider", newFile);

        if (contentUri != null) {
            // Create a sharing intent
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            // Temporarily grant permission to read the content URI
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            // Start the sharing intent chooser
            startActivity(Intent.createChooser(shareIntent, "Share image"));
        }
    }



    private void setupQRselect(){
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.share_qr_code_radio_group);
        // Pre-select the second radio button as an example
        RadioButton radioButtonAttendee = (RadioButton) findViewById(R.id.share_qr_code_radio_attendee);
        radioButtonAttendee.setChecked(true);
        selectImage(documentId, "attendee_qr_code");

    }

    private void changeRadioColor() {

        RadioGroup radioGroup = findViewById(R.id.share_qr_code_radio_group);

        // Define the color state list for checked and unchecked states
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked state
                        new int[]{android.R.attr.state_checked} // checked state
                },
                new int[]{
                        Color.parseColor("#CCCCCC"), // gray color for unchecked state in hex
                        Color.parseColor("#FCA311") // red color for checked state in hex
                }
        );

        // Iterate over all RadioButtons in the RadioGroup
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View child = radioGroup.getChildAt(i);

            // Check if the view is a RadioButton
            if (child instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) child;

                // Apply the color state list to the RadioButton
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    radioButton.setButtonTintList(colorStateList);
                } else {
                    CompoundButtonCompat.setButtonTintList(radioButton, colorStateList); // Support library for pre-Lollipop
                }
            }
        }

    }







    //Temporary to test swap this with the firebase data
    private void addEventsInit(){

        // TODO THIS CODE CRASHES IF THERES NO DETAIL OR DATE SO I COMMENTED IT OUT UNCOMMENT WHEN DATA IS FIXED
        String device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        db.collection("Organizers")
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

                                List<DocumentReference> referenceArray = (List<DocumentReference>) document.get("events_list");

                                //assert createdEvents != null;
                                for (DocumentReference reference : referenceArray) {
                                    reference.get().addOnCompleteListener(referencedTask -> {
                                        if (referencedTask.isSuccessful()) {
                                            DocumentSnapshot referencedDocument = referencedTask.getResult();
                                            if (referencedDocument.exists()) {
                                                //TODO description/dates are not set in most firebase stuff this will cause it to crash
                                                String eventName = referencedDocument.getString("name");
                                                String eventDetails = document.getString("description");
                                                String eventDate = document.getString("date");
                                                String eventOrganizer = referencedDocument.getString("organizer");
                                                String eventID = referencedDocument.getId();
                                                events.add(new OrganizerEvent(eventName, eventDetails, eventDate, eventOrganizer, eventID));
                                            } else {
                                                System.out.println("Referenced document does not exist");
                                            }
                                        } else {
                                            System.out.println("Error fetching referenced document: " + referencedTask.getException());
                                        }
                                    });
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

    private void showOptionsDialog() {
        final String[] items = new String[events.size()];
        for (int i = 0; i < events.size(); i++) {
            items[i] = events.get(i).getEvent();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Events");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                testButton.setText(items[which]);
                selectedEvent = events.get(which);
                documentId = selectedEvent.getDocumentID();

                setupQRselect();
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();

        // Make sure the dialog has a window
        if (dialog.getWindow() != null) {
            // Create a new GradientDrawable with rounded corners
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(50f); // Set the corner radius
            drawable.setColor(Color.WHITE); // Set the background color (change if needed)

            // Set the GradientDrawable as the background of the dialog's window
            dialog.getWindow().setBackgroundDrawable(drawable);
        }

        dialog.show();
    }



}


