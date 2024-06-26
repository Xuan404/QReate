package com.example.qreate.attendee;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.example.qreate.R;
import com.example.qreate.organizer.qrmenu.OrganizerQRGeneratorActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    private String device_id;
    private String stringQR;
    private int count;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String> locationPermissionRequest;
    private SharedPreferences sharedPreferences;
    private Double latitude;
    private Double longitude;

    /**
     * Initializes components and registers activity result launchers for QR code scanning and location permission requests.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the ActivityResultLauncher here
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        locationPermissionRequest = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission has been granted, you can retrieve the location
                        retrieveLocation();
                    } else {
                        // Permission was denied, handle the situation
                    }
                }
        );
    }

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
                if (view.getId() == R.id.tap_to_scan_qr_button) {
                    startQRScan();
                }
            }
        });

        //initialize the scan launcher
        scanLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                Intent data = result.getData();
                IntentResult intentResult = IntentIntegrator.parseActivityResult(result.getResultCode(), data);

                stringQR = intentResult.getContents();
                Log.w("Scannerr", stringQR);
                if (stringQR == null) {
                    popUpAlert("Scan Aborted");

                } else {
                    // ALL CHECKING AND INSERTION GOES HERE

                    // First seaches promo then checkin
                    count = 0; // To check if neither promo not checkin
                    findDocumentByFieldValueCheckin("promo_qr_code_string", stringQR);
                    findDocumentByFieldValueCheckin("attendee_qr_code_string", stringQR); // First seaches dor

                    //popUpResultDialog(intentResult.getContents());
                }
            } else {
                popUpAlert("Scan Aborted");
            }
        });

        return view;
    }


    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * Searches for documents within the "Events" collection in Firestore that match a given field value.
     * This method is used to validate QR codes for event check-ins or promotional offers.
     * @param fieldName The name of the field to search by.
     * @param fieldValue The value of the field to match against.
     */
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


                            if (fieldName.equals("promo_qr_code_string")) {
                                // If Promo
                                navigateToEventDetailsFragment(documentId);
                                //Log.d("PromoTest", "Yayy ");

                            } else if (fieldName.equals("attendee_qr_code_string")) {
                                // If Attendee
                                checkCurrentlyCheckedIn(device_id, documentId);
                            }

                            //Toast.makeText(getContext(), "Document ID: " + documentId, Toast.LENGTH_SHORT).show();
                        } else {
                            // Document not found, show a Toast message
                            count += 1;
                            if (count == 2) {
                                // both test failed
                                Toast.makeText(getContext(), "Could not identify QRcode", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }


                });
    }

    /**
     * Navigates to an event details fragment, displaying information about the event associated with the scanned QR code.
     * @param eventId The ID of the event to display details for.
     */
    private void navigateToEventDetailsFragment(String eventId){
        AttendeeEventViewDetailsFragment detailsFragment = AttendeeEventViewDetailsFragment.newInstance(eventId);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.attendee_handler_frame, detailsFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Verifies if the attendee is currently checked into the event associated with the scanned QR code.
     * @param deviceId The device ID of the attendee.
     * @param documentId The document ID of the event to check against.
     */
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

                            // if already checked in then remove currently checkin from event list
                            if (currentlyCheckedIn != "") {

                                removeEventCheckin(currentlyCheckedIn);
                                //Toast.makeText(getContext(), "(TEMP) REMOVING LIST REFERENCE", Toast.LENGTH_SHORT).show();

                            }

                            //Signup validation
                            db.collection("Attendees").whereEqualTo("device_id", device_id).get()
                                    .addOnCompleteListener(attendeeTask -> {
                                        // Assuming device_id is unique, we expect only one matching document
                                        DocumentSnapshot attendeeDocument = attendeeTask.getResult().getDocuments().get(0);
                                        DocumentReference attendeeRef = attendeeDocument.getReference();

                                        // Step 2: Retrieve the event document using the provided event document id
                                        DocumentReference eventDocRef = db.collection("Events").document(documentId);

                                        eventDocRef.get().addOnCompleteListener(eventTask -> {
                                            if (eventTask.isSuccessful() && eventTask.getResult().exists()) {
                                                DocumentSnapshot eventDocument = eventTask.getResult();
                                                List<Map<String, Object>> signedUpAttendees = (List<Map<String, Object>>) eventDocument.get("signedup_attendees");

                                                // Step 3 & 4: Identify the matching attendee reference
                                                if (signedUpAttendees != null) {

                                                    boolean signupExist = false;

                                                    for (int i = 0; i < signedUpAttendees.size(); i++) {
                                                        Map<String, Object> attendeeInfo = signedUpAttendees.get(i);
                                                        DocumentReference signedUpAttendeeRef = (DocumentReference) attendeeInfo.get("attendeeRef");

                                                        if (signedUpAttendeeRef.getId().equals(attendeeRef.getId())) {

                                                            signupExist = true;

                                                            // Date validation
                                                            db.collection("Events").document(documentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    DocumentSnapshot document = task.getResult();
                                                                    Timestamp timestamp = document.getTimestamp("date");
                                                                    Date date = timestamp.toDate();
                                                                    String time = document.getString("timeOfEvent");

                                                                    // Firestore Inserts and signup/date validation before checkin
                                                                    if (validCheckIn(date, time)) {

                                                                        setEventCheckin(documentId);
                                                                        setCheckinAttendee(documentId);
                                                                        updateCheckinCount(documentId);
                                                                        setAttendeeGeolocation();

                                                                        Toast.makeText(getContext(), "You have successfully checkedin", Toast.LENGTH_SHORT).show();

                                                                    } else {
                                                                        Toast.makeText(getContext(), "Cannot Checkin before due date/time", Toast.LENGTH_SHORT).show();
                                                                    }


                                                                }
                                                            });

                                                        }
                                                    }

                                                    if (!signupExist){
                                                        Toast.makeText(getContext(), "You have not signed up for this event", Toast.LENGTH_SHORT).show();
                                                    }

                                                } else {
                                                    Toast.makeText(getContext(), "You have not signed up for this event", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    });

                            //Toast.makeText(getContext(), "Mismatch: The document ID does not match the currently checked-in status.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //Toast.makeText(getContext(), "No attendee found with the given device ID.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Validates if the current time and date allow for checking into the event.
     * @param date The date of the event.
     * @param time The time the event starts.
     * @return True if the current time and date are valid for check-in, otherwise false.
     */
    private boolean validCheckIn(Date date, String time) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("TimeDate", "Yayy");
            // Time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);
            LocalTime timeToCompare = LocalTime.parse(time, formatter);
            LocalTime currentTime = LocalTime.now();

            //Date
            LocalDate timestampDate = date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            LocalDate today = LocalDate.now();

            Log.d("TimeDate", "Yayyy");

            Log.d("TimeDate", String.valueOf(today.equals(timestampDate)));

            return currentTime.isAfter(timeToCompare) && today.equals(timestampDate);
        }

        return false;

    }

    /**
     * Removes an attendee's check-in status from an event.
     * @param eventId The ID of the event to remove the check-in status from.
     */
    private void removeEventCheckin(String eventId) {



        db.collection("Attendees").whereEqualTo("device_id", device_id).get()
                .addOnCompleteListener(attendeeTask -> {
                    if (attendeeTask.isSuccessful() && !attendeeTask.getResult().isEmpty()) {

                        DocumentSnapshot attendeeDocument = attendeeTask.getResult().getDocuments().get(0);
                        DocumentReference attendeeRef = attendeeDocument.getReference();

                        // Retrieve the event document using the provided event document id
                        DocumentReference eventDocRef = db.collection("Events").document(eventId);

                        db.runTransaction(transaction -> {
                            DocumentSnapshot eventSnapshot = transaction.get(eventDocRef);
                            // Decrement count
                            Long currentCount = eventSnapshot.getLong("checkin_count");
                            if (currentCount != null && currentCount > 0) {
                                Long newCount = currentCount - 1;
                                transaction.update(eventDocRef, "checkin_count", newCount);
                            } else {
                                // Handle the case where count is already 0 or not set (should not decrement below 0)
                            }

                            // Remove the attendee reference
                            transaction.update(eventDocRef, "checkedin_attendees", FieldValue.arrayRemove(attendeeRef));
                            return null; // Transaction success
                        }).addOnSuccessListener(aVoid -> {
                            // Handle transaction success
                        }).addOnFailureListener(e -> {
                            // Handle transaction failure
                        });

//                        // Remove the attendee reference from the checkedin_attendees field of the event document
//                        eventDocRef.update("checkedin_attendees", FieldValue.arrayRemove(attendeeRef))
//                                .addOnSuccessListener(aVoid -> {
//                                    // Successfully removed the attendee reference from the event's checkedin_attendees list
//                                })
//                                .addOnFailureListener(e -> {
//                                    // Handle the error
//                                });

                    } else {
                        // Handle the case where no attendee matches the device_id or the query failed
                    }
                });

    }


    /**
     * Marks an attendee as checked in to an event.
     * @param eventId The ID of the event to check the attendee into.
     */
    private void setEventCheckin(String eventId) {
        // inserts the attendee ref into the checked in list of event doc
        db.collection("Attendees").whereEqualTo("device_id", device_id)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Assuming device_id is unique, get the first document found
                        DocumentReference attendeeRef = queryDocumentSnapshots.getDocuments().get(0).getReference();

                        // Step 2 & 3: Add the attendee's document reference to the checkedin_attendees field of an event
                        DocumentReference eventRef = db.collection("Events").document(eventId);
                        db.runTransaction(transaction -> {
                            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
                            // Fetch the count as a long and cast it to an int for further operations
                            Long currentCountLong = eventSnapshot.getLong("checkin_count");
                            int currentCount = (currentCountLong != null) ? currentCountLong.intValue() : 0;

                            // Increment count and convert back to Long for Firestore
                            transaction.update(eventRef, "checkedin_attendees", FieldValue.arrayUnion(attendeeRef));
                            transaction.update(eventRef, "checkin_count", (long) (currentCount + 1));
                            // Transaction success
                            return null;
                        });


                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error in querying the Attendees collection
                });

    }

    /**
     * Updates the attendee's document to indicate they are currently checked in to an event.
     * @param eventDocId The ID of the event document.
     */
    private void setCheckinAttendee(String eventDocId) {

        // Get the event document reference from the "Events" collection
        //DocumentReference eventDocRef = db.collection("Events").document(eventDocId);

        // Query for the attendee with the matching deviceId
        db.collection("Attendees")
                .whereEqualTo("device_id", device_id)
                .get()
                .addOnCompleteListener(task -> {

                    DocumentReference attendeeDocRef = task.getResult().getDocuments().get(0).getReference();
                    // Update the currently_checkedin field with the event document reference
                    attendeeDocRef.update("currently_checkedin", eventDocId);

                });


    }

    /**
     * Updates the check-in count for an event based on attendee check-ins.
     * @param eventId The ID of the event to update the check-in count for.
     */
    private void updateCheckinCount(String eventId){

        // Step 1: Find the attendee document with the provided device_id
        db.collection("Attendees").whereEqualTo("device_id", device_id).get()
                .addOnCompleteListener(attendeeTask -> {
                    if (attendeeTask.isSuccessful() && !attendeeTask.getResult().isEmpty()) {
                        // Assuming device_id is unique, we expect only one matching document
                        DocumentSnapshot attendeeDocument = attendeeTask.getResult().getDocuments().get(0);
                        DocumentReference attendeeRef = attendeeDocument.getReference();

                        // Step 2: Retrieve the event document using the provided event document id
                        DocumentReference eventDocRef = db.collection("Events").document(eventId);

                        eventDocRef.get().addOnCompleteListener(eventTask -> {
                            if (eventTask.isSuccessful() && eventTask.getResult().exists()) {
                                DocumentSnapshot eventDocument = eventTask.getResult();
                                List<Map<String, Object>> signedUpAttendees = (List<Map<String, Object>>) eventDocument.get("signedup_attendees");

                                // Step 3 & 4: Identify the matching attendee reference and increment the count
                                if (signedUpAttendees != null) {

                                    WriteBatch batch = db.batch();

                                    for (int i = 0; i < signedUpAttendees.size(); i++) {
                                        Map<String, Object> attendeeInfo = signedUpAttendees.get(i);
                                        DocumentReference signedUpAttendeeRef = (DocumentReference) attendeeInfo.get("attendeeRef");

                                        if (signedUpAttendeeRef.getId().equals(attendeeRef.getId())) {
                                            // Step 5: Increment the count for the matching item
                                            int currentCount = ((Number) attendeeInfo.get("checkInCount")).intValue();
                                            signedUpAttendees.get(i).put("checkInCount", currentCount + 1);

                                            // Update the event document with the modified list
                                            batch.update(eventDocRef, "signedup_attendees", signedUpAttendees);
                                            break; // Assuming each attendee can only appear once in the list
                                        }
                                    }

                                    // Commit the batch operation
                                    batch.commit().addOnCompleteListener(commitTask -> {
                                        if (commitTask.isSuccessful()) {
                                            // The count has been successfully incremented
                                        } else {
                                            // Handle the error in committing the batch
                                        }
                                    });
                                }
                            } else {
                                // Handle the case where the event document does not exist or the retrieval failed
                            }
                        });
                    } else {
                        // Handle the case where no attendee matches the device_id or the query failed
                    }
                });



    }

    /**
     * Sets the geolocation for an attendee based on their current location.
     */
    private void setAttendeeGeolocation() {
        // sets the current checked in event to event to the event scanned

        // Query for the user with the matching deviceId
        db.collection("Users")
                .whereEqualTo("device_id", device_id)
                .get()
                .addOnCompleteListener(task -> {

                    // Assuming device_id is unique, there should only be one matching document
                    DocumentSnapshot userDocument = task.getResult().getDocuments().get(0);

                    // Check if the allow_coordinates field is true
                    if (userDocument.getBoolean("allow_coordinates") != null && userDocument.getBoolean("allow_coordinates")) {

                        // Initialize the launcher with a callback to handle the permission result
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            // You already have permission, you can retrieve the location
                            retrieveLocation();
                        } else {
                            // You don't have permission, so you request it
                            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                        // Request location permission
                        requestLocationPermission();


                    }


                });

    }

    /**
     * Requests the location permission from the user.
     */
    private void requestLocationPermission() {
        // Check if location permission is not already granted
        if (ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            // Permission already granted, proceed with location retrieval
            retrieveLocation();
        }
    }

    /**
     * Retrieves the current location of the device and updates the user's geolocation in Firestore.
     */
    private void retrieveLocation() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // You have the permission, get the location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener((Activity) getContext(), location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Assign the location to the global variables
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            db.collection("Users")
                                    .whereEqualTo("device_id", device_id)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        // Assuming device_id is unique, there should only be one matching document
                                        DocumentSnapshot userDocument = task.getResult().getDocuments().get(0);
                                        // Update the coordinates field with the new latitude and longitude
                                        userDocument.getReference().update("coordinates", new GeoPoint(latitude, longitude));
                                    });


                        }
                    });


        }


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
