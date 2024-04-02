package com.example.qreate.organizer.qrmenu;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.CompoundButtonCompat;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The following class is responsible for the qr generator screen and functionality
 *
 * @author Akib Zaman Choudhury
 */
public class OrganizerQRGeneratorActivity extends AppCompatActivity {

    String documentId ; // Dummy variable containing event doc id, should be the spinner value
    private FirebaseFirestore db;
    private OrganizerEvent selectedEvent;
    ArrayList<OrganizerEvent> events;
    private Button testButton;
    private int selectedId;
    String randomString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_generate_qr_code_screen);
        db = FirebaseFirestore.getInstance();
        changeRadioColor(); // Sets the radio color button to be orange when selected
        testButton = findViewById(R.id.generate_qr_code_spinner);

        ImageButton backButton = findViewById(R.id.generate_qr_code_screen_backbutton);
        Button confirmButton = findViewById(R.id.generate_qr_code_confirmbutton);
        RadioGroup radioGroup = findViewById(R.id.generate_qr_code_radio_group);
        ImageView qrCodeImageView = findViewById(R.id.generate_qr_code_qr_image);
        events = new ArrayList<OrganizerEvent>();
        addEventsInit();


        //DummyLoadUpData(); // Dummy Variable func call
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If statement to check if QRcode for that event already exists

                if (documentId == null) {
                    Toast.makeText(OrganizerQRGeneratorActivity.this, "Please select an event", Toast.LENGTH_SHORT).show();
                    return;
                }

                selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    // No radio item selected
                    Toast.makeText(OrganizerQRGeneratorActivity.this, "Error: Select QR type", Toast.LENGTH_SHORT).show();

                } else {

                    // Sets up field value
                    String field = null;
                    if (selectedId == R.id.generate_qr_code_radio_attendee) {
                        field = "attendee_qr_code";
                    } else if (selectedId == R.id.generate_qr_code_radio_promo) {
                        field = "promo_qr_code";

                    }

                    // Checks if field exists
                    checkIfQRCodePathExists(field, documentId, new QRCodeExistsCallback() {
                        @Override
                        public void onResult(boolean exists) {

                            if (!exists) {
                                // QR code path exists
                                try {
                                    randomString = UUID.randomUUID().toString();
                                    Bitmap bitmap = generateQRCode(randomString);
                                    qrCodeImageView.setImageBitmap(bitmap);
                                    uploadQRCode(bitmap, randomString);
                                } catch (WriterException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                // QR code path does not exist
                                Toast.makeText(OrganizerQRGeneratorActivity.this, "Error: QRcode already exists", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            // Handle error
                        }
                    });
                    //


                }

            }
        });


        backButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }));

    }


    interface QRCodeExistsCallback {
        void onResult(boolean exists);

        void onError(Exception e);
    }

    // Method to check if QR code path exists
    public void checkIfQRCodePathExists(String field, String documentId, QRCodeExistsCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events").document(documentId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists() && document.contains(field)) {
                                String qrCodePath = document.getString(field);
                                // Invoke callback based on whether the QR code path is not null or empty
                                callback.onResult(qrCodePath != null && !qrCodePath.isEmpty());
                            } else {
                                // Document does not exist or does not have the attendee_qr_code field
                                callback.onResult(false);
                            }
                        } else {
                            // Task failed with an exception
                            callback.onError(task.getException());
                        }
                    }
                });
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

    // Uploads the image to storage
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

                    if (selectedId == R.id.generate_qr_code_radio_attendee) {
                        saveImagePathToFirestore(storagePath, documentId, "attendee_qr_code");

                    } else if (selectedId == R.id.generate_qr_code_radio_promo) {
                        saveImagePathToFirestore(storagePath, documentId, "promo_qr_code");

                    }


                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                });
    }

    private void saveImagePathToFirestore(String storagePath, String documentId, String QRtype) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String stringQR = QRtype + "_string";
        Map<String, Object> data = new HashMap<>();
        data.put(QRtype, storagePath);
        data.put(stringQR, randomString);

        db.collection("Events").document(documentId)
                .update(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "QRcode was successfully generated", Toast.LENGTH_SHORT).show();
                    //finish();
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }


    // Change radio Group color
    private void changeRadioColor() {

        RadioGroup radioGroup = findViewById(R.id.generate_qr_code_radio_group);

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
                                                //String eventDetails = document.getString("description");
                                                //String eventDate = document.getString("date");
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
