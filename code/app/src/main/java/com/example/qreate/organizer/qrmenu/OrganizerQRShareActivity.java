package com.example.qreate.organizer.qrmenu;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.qreate.R;
import com.example.qreate.organizer.qrmenu.OrganizerEvent;
import com.example.qreate.organizer.qrmenu.OrganizerEventSpinnerArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class OrganizerQRShareActivity extends AppCompatActivity {
    ArrayList<OrganizerEvent> events;
    private Button testButton;
    private OrganizerEvent selectedEvent;
    //File cacheDir = getCacheDir();
    private FirebaseFirestore db;
    ImageView qrImage;
    Uri firebaseUri;
    String downloadUrl;
    StorageReference promoRef;
    FirebaseStorage storage;
    StorageReference storageRef;
    //Spinner eventsSpinner;
    //OrganizerEventSpinnerArrayAdapter eventSpinnerArrayAdapter;
    //temporary fake id
    String documentId = "3Z0RAltfeXSvMg3zO7Kw";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_share_qr_code_screen);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        //FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();


        events = new ArrayList<OrganizerEvent>();


        addEventsInit();


        //eventSpinnerArrayAdapter = new OrganizerEventSpinnerArrayAdapter(this, events);
        qrImage = findViewById(R.id.share_qr_code_qr_image);

        //NEED TO GRAB THE ARRAY FROM FIREBASE THEN PARSE IT INTO THIS
        //eventsSpinner = findViewById(R.id.share_qr_code_spinner);


        /*eventsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                //getPromoQR();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });*/
        //TODO get promo qr grabs the qr for the selected event so call it everytime a new event is selected
         //FIX DIDNT WORK im on like 10 attempts now i cant even find anything else online
        getPromoQR();
        //grabbed the access token just to make sure the image could even be loaded
        Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/qreate-bb8b8.appspot.com/o/qr_codes%2F537b55b1-7ed9-4202-9a82-815cca1715a5.png?alt=media&token=77d6e246-c9de-47ad-8a90-838889935feb").into(qrImage);
        //Glide.with(this).load(firebaseUri).into(qrImage);
        //checks if url can parse into bitmap the exception didnt pop up so im even more lost now
        /*Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(firebaseUri.toString()).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.d("Error", e.getStackTrace().toString());

        }*/

        //eventSpinnerArrayAdapter.setDropDownViewResource(R.layout.organizer_event_list_recycler_row_layout);
        testButton = findViewById(R.id.share_qr_code_spinner);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();
            }
        });


        //eventsSpinner.setAdapter(eventSpinnerArrayAdapter);


        Button shareButton = findViewById(R.id.share_qr_code_sharebutton);
        //it cant be set as text but it pulls up the correct file idk whats wrong
        /*try{
            shareButton.setText(firebaseUri.toString()); //uh somethings wrong with the uri i think
        }catch (NullPointerException e) {
            // Handle the null Uri case
            e.printStackTrace();
        }*/


        shareButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/*");
                //GOTTA PUT THE IMAGE LOCATION HERE even this basic one doesnt work btw :(
                //Uri imageUri = Uri.parse("android.resource://" + getPackageName() + "/drawable/qricon.png");

                sharingIntent.putExtra(Intent.EXTRA_STREAM, firebaseUri);
                startActivity(Intent.createChooser(sharingIntent, "Share Image"));*/
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/png"); // Set the appropriate image type
                //shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(downloadUrl));
                shareIntent.putExtra(Intent.EXTRA_STREAM, firebaseUri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share Image"));
            }
        }));


        //Back Button
        ImageButton backButton = findViewById(R.id.share_qr_code_screen_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getPromoQR(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events").document(documentId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            promoRef = storage.getInstance().getReference(document.getString("promo_qr_code"));
                            storageRef.child(document.getString("promo_qr_code")).getDownloadUrl().addOnSuccessListener(uri -> {
                            /*promoRef.child("537b55b1-7ed9-4202-9a82-815cca1715a5.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri uri) {
                                    firebaseUri = uri;
                                }*/
                                //Content uri code this fix didn't work
                                //firebaseUri = FileProvider.getUriForFile(context, "com.example.qreate.organizer.qrmenu", new File(uri.getPath()));
                                //String imageUrl = String.valueOf(uri);

                                firebaseUri = uri;
                                //downloadUrl = uri.toString();
                            }).addOnFailureListener(exception -> {
                                // Handle any errors (e.g., image not found, network issues)
                                Log.e("ImageError", "Error downloading image: " + exception.getMessage());
                            });
                            //different content uri fix this didn't work either
                            /*StorageReference imageRef = storageRef.child(document.getString("promo_qr_code"));
                            imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    // Convert byte array to Bitmap
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
                                    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Promo", null);
                                    firebaseUri = Uri.parse(path);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });*/
                        } else {
                            // Task failed with an exception
                            Log.d("Firestore", "get failed with ", task.getException());
                        }

                    }
                });
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
// fix attempt 5 or 6 https://youtu.be/_3JFZqfYLNU?si=NaCYlFKy99ELE0tZ
    /*private void shareImage(){
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Uri uri = getImageToShare(bitmap);
    }
    private Uri getImageToShare(Bitmap bitmap) {
        File imagefolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imagefolder.mkdirs();
            File file = new File(imagefolder, "shared_image.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(this, "com.anni.shareimage.fileprovider", file);
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return uri;
    }*/

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


