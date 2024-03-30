package com.example.qreate.attendee;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.qreate.R;
import com.example.qreate.organizer.qrmenu.OrganizerQRGeneratorActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

/**
 * AttendeeScanFragment provides a user interface for attendees to scan QR codes within the event app.
 *
 * It features a simple layout with a scan button that, when clicked,
 * initiates the QR code scanning process.
 * The fragment is designed to be embedded within the attendee section of the application.
 *
 * References: - ankur035, GeeksforGeeks, How to Read QR Code using Zxing Library in Android?,
 *                  Last Updated: 15 Jan,2021, https://www.geeksforgeeks.org/how-to-read-qr-code-using-zxing-library-in-android/
 *
 *             - Cambo Tutorial, YouTube, Uploaded Mar 18, 2022,
 *                  Implement Barcode QR Scanner in Android Studio Barcode Reader | Cambo Tutorial,
 *                  https://www.youtube.com/watch?v=jtT60yFPelI
 *
 * @author Shraddha Mehta
 */

public class AttendeeScanFragment extends Fragment {

    ImageButton scanButton;
    TextView textContent;
    private ActivityResultLauncher<Intent> scanLauncher;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String device_id;

    String stringQR;

    /**
     * This method inflates the layout for the attendee QR code scanning page, initializes UI components,
     * and sets up a click listener for the scan button.
     *
     * @param inflater LayoutInflater: The LayoutInflater object that can be used to inflate
     *                 any views in the fragment.
     * @param container ViewGroup: If non-null, this is the parent view that the fragment's
     *                 UI should be attached to.
     * @param savedInstanceState Bundle: If non-null, this fragment is being re-constructed
     *                 from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        device_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        View view = inflater.inflate(R.layout.attendee_tap_to_scan_page, container, false);

        scanButton = view.findViewById(R.id.tap_to_scan_qr_button);
        textContent = view.findViewById(R.id.text_content);
        //scanButton.setOnClickListener(this);

        // Clicking scan image pops out scan camera
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.tap_to_scan_qr_button){
                    startQRScan();
                }
            }
        });

        //initialize the scan launcher
        scanLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == AppCompatActivity.RESULT_OK){
                Intent data = result.getData();
                IntentResult intentResult = IntentIntegrator.parseActivityResult(result.getResultCode(), data);

                stringQR = intentResult.getContents();
                Log.w("Scannerr", stringQR);
                if (stringQR == null) {
                    popUpAlert("Scan Aborted");

                } else {
                    // ALL CHECKING AND INSERTION GOES HERE

                    // First seaches checkin qr document then promo qr
                    findDocumentByFieldValueCheckin("attendee_qr_code_string",stringQR); // First seaches dor
                    //TODO findDocumentByFieldValuePromo
                    popUpResultDialog(intentResult.getContents());
                }
            } else{
                popUpAlert("Scan Aborted");
            }
        });

        return view;
    }


    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------



    private void findDocumentByFieldValueCheckin(String fieldName, String fieldValue) {
        db.collection("Events")
                .whereEqualTo(fieldName, fieldValue)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // Assuming you are looking for the first document that matches the criteria
                            QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                            String documentId = document.getId(); // This is your document ID
                            // You can now use this documentId as needed
                            checkCurrentlyCheckedIn(device_id, documentId);
                            //Toast.makeText(getContext(), "Document ID: " + documentId, Toast.LENGTH_SHORT).show();
                        } else {
                            // Document not found, show a Toast message
                            Toast.makeText(getContext(), "Could not identify QRcode", Toast.LENGTH_SHORT).show();
                        }

                    }


                });
    }

    private void checkCurrentlyCheckedIn(String deviceId, String documentId) {
        db.collection("Attendees")
                .whereEqualTo("device_id", deviceId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Assuming device_id is unique, get the first document.
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        String currentlyCheckedIn = document.getString("currently_checkedin");


                        // Compare the currently_checkedin field with the provided documentId
                        if (documentId.equals(currentlyCheckedIn)) {
                            Toast.makeText(getContext(), "You are already checked into this event", Toast.LENGTH_SHORT).show();

                        } else {

                            // Run Test on document
                            db.collection("Events").document(documentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot document = task.getResult();
                                    Timestamp timestamp = document.getTimestamp("date");
                                    Date date = timestamp.toDate();

                                    // Firestore Inserts
                                    if (validCheckIn(date)) {
                                        setEventCheckin(documentId);
                                        setCheckinAttendee(documentId);


                                    } else{
                                        Toast.makeText(getContext(), "Unable to checkin", Toast.LENGTH_SHORT).show();
                                    }



                                }
                            });

                            //Toast.makeText(getContext(), "Mismatch: The document ID does not match the currently checked-in status.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //Toast.makeText(getContext(), "No attendee found with the given device ID.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validCheckIn(Date date){
        // TODO add the signup validation after harshita and gitanjali finish their side of things

        // Convert the timestamp to a LocalDate
        LocalDate timestampDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            timestampDate = date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        // Get today's date
        LocalDate today = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            today = LocalDate.now();
        }

        // Compare the two dates
        return today.equals(timestampDate);

    }


    private void setEventCheckin(String eventId){
        // inserts the attendee ref into the checked in list of event doc
        db.collection("Attendees").whereEqualTo("device_id", device_id)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Assuming device_id is unique, get the first document found
                        DocumentReference attendeeRef = queryDocumentSnapshots.getDocuments().get(0).getReference();

                        // Step 2 & 3: Add the attendee's document reference to the checkedin_attendees field of an event
                        DocumentReference eventRef = db.collection("Events").document(eventId);
                        eventRef.update("checkedin_attendees", FieldValue.arrayUnion(attendeeRef));

                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error in querying the Attendees collection
                });

    }
    private void setCheckinAttendee(String eventDocId){

        // Get the event document reference from the "Events" collection
        //DocumentReference eventDocRef = db.collection("Events").document(eventDocId);

        // Query for the attendee with the matching deviceId
        db.collection("Attendees")
                .whereEqualTo("device_id", device_id)
                .get()
                .addOnCompleteListener(task -> {

                    DocumentReference attendeeDocRef = task.getResult().getDocuments().get(0).getReference();
                    // Update the currently_checkedin field with the event document reference
                    attendeeDocRef.update("currently_checkedin",eventDocId);

                });


    }

    private void setAttendeeGeolocation(){
        // sets the current checked in event to event to the event scanned

    }





    //-------------------------------------------------------------------------------END-------------------------------------------------------------------------------------


    /**
     * For displaying pop-up dialog after scanning of qr code
     * for the user to know the outcome
     * @param resultMessage from scan
     */
    private void popUpResultDialog(String resultMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Result from Scan");
        builder.setMessage(resultMessage);
        builder.setPositiveButton("OK", (dialog, which) -> {
            //can remove or change later depending on implementation
            //textContent.setText(result);
            dialog.dismiss();
        });

        builder.show();
    }

    /**
     * For scans that are not need warnings
     * @param scanAborted message
     */
    private void popUpAlert(String scanAborted) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage(scanAborted);

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }



    /**
     * For starting scanner using IntentIntegrator library
     */
    private void startQRScan(){
        IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(this);
        intentIntegrator.setPrompt("SCAN A QR CODE");
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setCaptureActivity(CapActivityForAttendeeQRScannerPage.class);

        Intent scanIntent = intentIntegrator.createScanIntent();
        scanLauncher.launch(scanIntent);
    }



}
