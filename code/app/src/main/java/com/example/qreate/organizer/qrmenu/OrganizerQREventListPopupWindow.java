package com.example.qreate.organizer.qrmenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;



import android.view.ViewGroup.LayoutParams;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.example.qreate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


/**
 * A Pup up window that allows the user to create an aevent
 * Reference https://www.youtube.com/watch?v=qCoidM98zNk
 * @author Akib Zaman Choudhury
 */
public class OrganizerQREventListPopupWindow {

    private Context context;
    private PopupWindow popupWindow;
    private View popupView;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private Button timeButton;
    private Button uploadPosterButton;
    private EventCreationListener eventCreationListener;

    private Date selectedDate;
    private Uri selectedImageUri;
    private String imagePath;
    private String imageName;
    private String name;
    private String description;
    private String signupLimit;
    private String selectedTime;
    private static final int PICK_IMAGE_REQUEST = 1;
    public static final int IMAGE_PICK_CODE = 1000;

    /**
     * Initializer class
     * @param context
     */
    public OrganizerQREventListPopupWindow(Context context) {

        this.context = context;

        // Inflate the custom layout/view
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.organizer_fragment_create_event, null);

        initializePopupWindow(); // Creates the pop up menu

        // Sets up the date picker button
        dateButton = popupView.findViewById(R.id.dateselector);
        dateButton.setText(getTodaysDate());
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showDatePickerDialog();
                initDatePicker();
            }
        });

        timeButton = popupView.findViewById(R.id.timeselector);
        selectedTime = CurrentTime();
        timeButton.setText(selectedTime);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTimePicker();
            }
        });

        uploadPosterButton = popupView.findViewById(R.id.uploadButton);
        uploadPosterButton.setOnClickListener(view -> {
            openImageSelector();

        });

        // Confirm button actions
        Button confirmDataButton = popupView.findViewById(R.id.buttonCreateEvent);
        confirmDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setInfoUp(popupView);
                //Log.w("nameCheck", String.valueOf(name));

                if (imageName != null && !Objects.equals(name, "") && !Objects.equals(description, "")) {
                    // Uploads the image and then creates the event automatically


                    uploadImageToFirebaseStorage(selectedImageUri);
                    //createEvent(popupView); // had to insert this in uploadImageToFirebaseStorage as its an asynchronous event
                    popupWindow.dismiss();
                } else {
                    // imageName is null, show a Toast message
                    Toast.makeText(context, "Fill in All fields", Toast.LENGTH_SHORT).show();
                }




            }
        });

    }

    /**
     * Listener inerface for event creation
     */
    public interface EventCreationListener {
        void onEventCreated();
    }

    /**
     * Function that implements the EventCreationListener inerface
     * @param listener
     */
    public void setEventCreationListener(EventCreationListener listener) {
        this.eventCreationListener = listener;
    }


    /**
     * Opens up gallery for image selection
     */
    private void openImageSelector() {
        Activity activity = (Activity) context;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    /**
     * Sets the image Uri
     * @param imageUri
     */
    public void setImageUri(Uri imageUri) {
        this.selectedImageUri = imageUri;
        Log.w("UriImage", String.valueOf(selectedImageUri));
    }

    /**
     * Sets image name
     * @param imageName
     */
    public void setImageName(String imageName) {
        this.imageName = imageName;
        uploadPosterButton.setText(imageName);
        Log.w("UriImage", String.valueOf(imageName));
    }

    /**
     * Retrieves the file name from the Uri
     * @param context
     * @param uri
     * @return
     */
    public String getFileNameByUri(Context context, Uri uri) {
        String fileName = "unknown"; // Default to "unknown" if the file name can't be found
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            // If the device version is Android Q or above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Try to get the file name from the column DISPLAY_NAME
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst();
                fileName = cursor.getString(nameIndex);
            } else {
                // For devices below Android Q, you may need to manually parse the last segment
                // of the URI path as the file name
                List<String> pathSegments = uri.getPathSegments();
                if (pathSegments != null && pathSegments.size() > 0) {
                    fileName = pathSegments.get(pathSegments.size() - 1);
                }
            }
            cursor.close();
        }
        return fileName;
    }

    /**
     * Uploads the image selected to firebase
     * @param fileUri
     */
    private void uploadImageToFirebaseStorage(Uri fileUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("posters/" + UUID.randomUUID().toString());
        storageRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Saves the image path linking to the firebase storage
                imagePath = storageRef.getPath();
                // Creates the event after getting the storage path
                createEvent(popupView);
            }
        });
    }

    /**
     * Sets up rest of the document field
     * @param view
     */
    private void setInfoUp(View view) {

        EditText editTextName = view.findViewById(R.id.editTextEventName);
        EditText editTextDescription = view.findViewById(R.id.editTextEventDescription);
        EditText editTextLimitSignup = view.findViewById(R.id.signupNumber);


        name = editTextName.getText().toString();
        description = editTextDescription.getText().toString();
        signupLimit = editTextLimitSignup.getText().toString();




    }


    /**
     * Creates the document field for event
     * @param view
     */
    private void createEvent(View view){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        //setInfoUp(view);

        // Database insertion goes here
        Map<String, Object> device = new HashMap<>();
        device.put("org_device_id", device_id);
        device.put("name", name);
        device.put("description", description);
        device.put("date", selectedDate);
        device.put("timeOfEvent", selectedTime);
        device.put("poster", imagePath);
        device.put("signup_limit", signupLimit);
        device.put("signup_count", 0);
        device.put("checkin_count", 0);


        // Creates a new Events document in Firestore
        db.collection("Events").add(device)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        addEventToOrganizer(documentReference);
                        Log.w("EventFirestore", "Yayy");
                        if (eventCreationListener != null) {
                            eventCreationListener.onEventCreated();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("EventFirestore", "Nayy");

                    }
                });

    }


    /**
     * Adds the Event doc reference to the Organizer document
     * @param eventRef
     */
    private void addEventToOrganizer(DocumentReference eventRef){


        String device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Retrieves User document and updates it
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
                                String documentId = document.getId();
                                Log.d("Firestore", documentId);

                                // Update the document directly
                                DocumentReference docRef = db.collection("Organizers").document(documentId);
                                docRef.update("events_list", FieldValue.arrayUnion(eventRef));



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
     * Initializes the pop up menu
     */
    private void initializePopupWindow() {

        // Create PopupWindow
        popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

        // Dismiss the popup window when touched outside
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

    }

    /**
     * Retrieves Today's date
     * @return
     */
    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        selectedDate = cal.getTime(); // Store the selected date fir firestore use

        return makeDateString(day, month, year);
    }

    /**
     * Retrieves current time
     * @return
     */
    private String CurrentTime(){
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        String timePeriod = (hour < 12) ? "AM":"PM";
        if( hour > 12){
            hour -= 12;
        } else if (hour == 0){
            hour = 12;
        }


        timePeriod= String.format("%02d:%02d %s", hour, minute, timePeriod);
        return timePeriod;

    }


    /**
     * Method for time picker, selecting time of event
     */
    private void initTimePicker(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                updateTime(hour,minute);
            }
        };

        //show dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, timeSetListener, hour, min, false);
        timePickerDialog.show();

    }

    /**
     * Sets time format
     * @param hour
     * @param minute
     */
    private void updateTime(int hour, int minute){
        String timePeriod = (hour >= 12) ? "PM": "AM";
        if(hour > 12){
            hour -= 12;
        } else if(hour ==0){
            hour = 12;
        }

        selectedTime = String.format("%02d:%02d %s", hour, minute, timePeriod);
        timeButton.setText(selectedTime);
    }

    /**
     * Function used to set Event date
     */
    private void initDatePicker() {

        Calendar cal = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                String date = makeDateString(day, month, year);
                dateButton.setText(date);

                // sets the selected date for firebase use
                cal.set(year, month, day);
                selectedDate = cal.getTime(); // Store the selected date
            }
        };


        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(context, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());

        datePickerDialog.show();

    }

    /**
     * Function used for outputting the selected date to the screen
     * @param day
     * @param month
     * @param year
     * @return
     */
    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    /**
     * Function used to format the selected date
     * @param month
     * @return
     */
    private String getMonthFormat(int month)
    {
        if(month == 0)
            return "JAN";
        if(month == 1)
            return "FEB";
        if(month == 2)
            return "MAR";
        if(month == 3)
            return "APR";
        if(month == 4)
            return "MAY";
        if(month == 5)
            return "JUN";
        if(month == 6)
            return "JUL";
        if(month == 7)
            return "AUG";
        if(month == 8)
            return "SEP";
        if(month == 9)
            return "OCT";
        if(month == 10)
            return "NOV";
        if(month == 11)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    /**
     * Shows the pop up menu in the middle of the screen
     */
    public void showPopupWindow() {
        // Show the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }


}
