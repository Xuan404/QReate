package com.example.qreate.organizer.qrmenu;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qreate.R;
import com.example.qreate.organizer.qrmenu.OrganizerEvent;
import com.example.qreate.organizer.qrmenu.OrganizerEventSpinnerArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The following class is responsible for the reuse existing qr code screen
 *
 * Outstanding Issue: Event spinner is not pulling from firebase
 * @author Denis Soh
 */
public class OrganizerQRReuseExistingActivity extends AppCompatActivity {
    ArrayList<OrganizerEvent> events;
    private OrganizerEvent selectedEvent;
    //Spinner eventsSpinner;
    private Button testButton;
    private FirebaseFirestore db;
    OrganizerEventSpinnerArrayAdapter eventSpinnerArrayAdapter;
    Uri image;
    String documentId = "LrXKKSgx3TmrSWiWZnQc";
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_reuse_exisiting_qr_code_screen);
        db = FirebaseFirestore.getInstance();
        imagePickerLauncher = registerForActivityResult(

                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        image = data.getData();
                    }
                }
        );

        events = new ArrayList<OrganizerEvent>();

        addEventsInit();


        //eventSpinnerArrayAdapter = new OrganizerEventSpinnerArrayAdapter(this, events);

        testButton = findViewById(R.id.reuse_existing_qr_code_screen_spinner);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();
            }
        });

        //NEED TO GRAB THE ARRAY FROM FIREBASE THEN PARSE IT INTO THIS
        /*eventsSpinner = findViewById(R.id.reuse_existing_qr_code_screen_spinner);

        eventsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {*/
            /**
             * gets selected item string
             *
             * @param parent the adapter-view of the view
             * @param view current view
             * @param position current position in spinner
             * @param id current id
             *
             */
            /*@Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        eventSpinnerArrayAdapter.setDropDownViewResource(R.layout.organizer_event_list_recycler_row_layout);

        eventsSpinner.setAdapter(eventSpinnerArrayAdapter);*/
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        imagePickerLauncher.launch(photoPickerIntent);

        //Back Button
        ImageButton backButton = findViewById(R.id.reuse_existing_qr_code_screen_backbutton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //TODO
        //share Button
        Button shareButton = findViewById(R.id.reuse_existing_qr_code_screen_confirmbutton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                imagePickerLauncher.launch(photoPickerIntent);*/
                try {
                    String randomString = UUID.randomUUID().toString();
                    Bitmap bitmap = rebuildQR(decodeQRCode(image));
                    uploadQRCode(bitmap, randomString);
                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private String decodeQRCode(Uri uri){
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (bitmap == null) {
                //Log.e(TAG, "URI is not a bitmap: " + uri.toString());
                return null;
            }

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            bitmap.recycle();

            RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
            MultiFormatReader reader = new MultiFormatReader();

            try {
                Result result = reader.decode(bBitmap);
                return result.getText();
            } catch (NotFoundException e) {
                Log.e("QR", "QR code decoding exception", e);
                return null;
            }
        } catch (Exception e) {
            Log.e("QR", "Error reading QR code: " + e.getMessage());
            return null;
        }


    }

    private Bitmap rebuildQR(String text) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512);
        Bitmap bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565);
        for (int x = 0; x < 512; x++) {
            for (int y = 0; y < 512; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }
    private void uploadQRCode(Bitmap bitmap, final String imageName) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        // This is the storage path we're going to save in Firestore
        String storagePath = "qr_codes/" + imageName + ".png";
        StorageReference qrCodeRef = storageRef.child(storagePath);

        qrCodeRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {
                    saveImagePathToFirestore(storagePath, documentId, "attendee_qr_code");

                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    Toast.makeText(this, "somethings wrong qr upload", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveImagePathToFirestore(String storagePath, String documentId, String QRtype) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put(QRtype, storagePath);

        db.collection("Events").document(documentId)
                .update(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "QRcode was successfully generated", Toast.LENGTH_SHORT).show();
                    //finish();
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Toast.makeText(this, "somethings wrong", Toast.LENGTH_SHORT).show();
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

