package com.example.qreate.attendee;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

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

    private FirebaseFirestore db = FirebaseFirestore.getInstance();;

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

                    // First seaches attendee qr document then promo qr
                    findDocumentByFieldValue("attendee_qr_code_string",stringQR); // First seaches dor

                    popUpResultDialog(intentResult.getContents());
                }
            } else{
                popUpAlert("Scan Aborted");
            }
        });

        return view;
    }


    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------



    public void findDocumentByFieldValue(String fieldName, String fieldValue) {
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
                            // For example, store it in a class variable or use directly
                            Toast.makeText(getContext(), "Document ID: " + documentId, Toast.LENGTH_SHORT).show();
                        } else {
                            // Document not found, show a Toast message
                            Toast.makeText(getContext(), "Could not identify QRcode", Toast.LENGTH_SHORT).show();
                        }

                    }


                });
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
