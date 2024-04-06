package com.example.qreate.organizer.qrmenu;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;

import android.os.Bundle;
import android.Manifest;

import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.example.qreate.R;

import com.example.qreate.organizer.OrganizerEvent;
import com.example.qreate.organizer.OrganizerEventSpinnerArrayAdapter;
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
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The following class is responsible for the reuse existing qr code screen
 *
 * Outstanding Issue: Event spinner is not pulling from firebase
 * @author Denis Soh
 */
public class OrganizerQRReuseExistingActivity extends AppCompatActivity {

    private String documentId;
    private Bitmap qrSelectBitmap;
    private String qrImageString;
    ArrayList<OrganizerEvent> events;
    private OrganizerEvent selectedEvent;
    private Button testButton;
    private ImageView qrImageView;
    private FirebaseFirestore db;
    OrganizerEventSpinnerArrayAdapter eventSpinnerArrayAdapter;
    Uri imageUri;


    private static final int REQUEST_GALLERY = 100;
    private static final int PERMISSION_REQUEST_READ_STORAGE = 101;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_reuse_exisiting_qr_code_screen);
        db = FirebaseFirestore.getInstance();
        events = new ArrayList<OrganizerEvent>();
        addEventsInit();



        // Event dialoq menu button
        testButton = findViewById(R.id.reuse_existing_qr_code_screen_spinner);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();
            }
        });

        // Back Button
        ImageButton backButton = findViewById(R.id.reuse_existing_qr_code_screen_backbutton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // QRimage button
        qrImageView = findViewById(R.id.reuse_existing_qr_code_screen_qr_image);
        qrImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();

            }
        });


        // share Button
        Button shareButton = findViewById(R.id.reuse_existing_qr_code_screen_confirmbutton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (qrImageString == null || documentId == null) {
                    Toast.makeText(OrganizerQRReuseExistingActivity.this, "Error: Select an Event and an Image", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    qrSelectBitmap = generateQRCode(qrImageString);
                    uploadQRCode(qrSelectBitmap, qrImageString);
                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }

            }
        });

    }











    private void openGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY);
            startGalleryIntent();
        } else {
            startGalleryIntent();
        }
    }

    private void startGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_GALLERY) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startGalleryIntent();
//            } else {
//                // Permission denied.
//                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                qrSelectBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                decodeQRCode(qrSelectBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private void decodeQRCode(Bitmap bitmap) {
//        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
//        //copy pixel data from the Bitmap into the 'intArray' array
//        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
//
//        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
//        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

        MultiFormatReader reader = new MultiFormatReader();
        // Use a HashMap to set the TRY_HARDER hint
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.of(BarcodeFormat.QR_CODE));
        reader.setHints(hints);

        try {
            Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap);
            qrImageString = qrCodeResult.getText();
            qrImageView.setImageBitmap(bitmap);
        } catch (NotFoundException e) {
            Toast.makeText(this, "Not a QRcode", Toast.LENGTH_SHORT).show();
        }
    }
















    // Generates the qr code
    private Bitmap generateQRCode(String text) throws WriterException {
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

    // Sets everything up for upload
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
                    saveImagePathToFirestore(storagePath);

                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    //Toast.makeText(this, "somethings wrong qr upload", Toast.LENGTH_SHORT).show();
                });
    }

    // Saves image onto firebase storage
    private void saveImagePathToFirestore(String storagePath) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String stringQR = "attendee_qr_code_string";
        Map<String, Object> data = new HashMap<>();
        data.put("attendee_qr_code", storagePath);
        data.put(stringQR, qrImageString);

        db.collection("Events").document(documentId)
                .update(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Attendee QRcode successfully assigned", Toast.LENGTH_SHORT).show();
                    //finish();
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
                                                String eventOrganizer = referencedDocument.getString("organizer");
                                                String eventID = referencedDocument.getId();
                                                events.add(new OrganizerEvent(eventName, "details", "date", eventOrganizer, eventID));
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
}

