package com.example.qreate.organizer.qrmenu;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;



import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.example.qreate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// https://www.youtube.com/watch?v=qCoidM98zNk

public class OrganizerQREventListPopupWindow {

    private Context context;
    private PopupWindow popupWindow;
    private View popupView;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private Button uploadPosterButton;

    private Date selectedDate;

    public OrganizerQREventListPopupWindow(Context context) {

        this.context = context;

        // Inflate the custom layout/view
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.organizer_fragment_create_event, null);

        initializePopupWindow(); // Creates the pop up menu

        // Confirm button actions
        Button confirmDataButton = popupView.findViewById(R.id.buttonCreateEvent);
        confirmDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createEvent(popupView);
                popupWindow.dismiss();

            }
        });

    }
















    private void createEvent(View view){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.w("EventFirestore", "Firestore has been initialized.");

        // Database insertion goes here
        EditText editTextName = view.findViewById(R.id.editTextEventName);
        EditText editTextDescription = view.findViewById(R.id.editTextEventDescription);
        EditText editTextLimitSignup = view.findViewById(R.id.signupNumber);
        Log.w("EventFirestore", "works1");

        String name = editTextName.getText().toString();
        String description = editTextDescription.getText().toString();
        Log.w("EventFirestore", "works2");
        String signupLimit = editTextLimitSignup.getText().toString();


        Map<String, Object> device = new HashMap<>();
        //device.put("organizer", name);
        device.put("name", name);
        device.put("description", description);
        device.put("date", selectedDate);
        //device.put("poster", homepage);
        device.put("signup_limit", signupLimit);


        // Creates a new Events document in Firestore
        db.collection("Events").add(device)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.w("EventFirestore", "Yayy");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("EventFirestore", "Nayy");

                    }
                });

    }

























    private void initializePopupWindow() {

        // Create PopupWindow
        popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

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

        // Dismiss the popup window when touched outside
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

    }

    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }



    private void initDatePicker() {

        Calendar cal = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month = month + 1;
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

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void showPopupWindow() {
        // Show the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }


}
