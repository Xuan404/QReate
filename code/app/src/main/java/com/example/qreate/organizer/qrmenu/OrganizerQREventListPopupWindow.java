package com.example.qreate.organizer.qrmenu;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;



import android.view.ViewGroup.LayoutParams;


import androidx.fragment.app.FragmentManager;

import com.example.qreate.R;

public class OrganizerQREventListPopupWindow {

    private PopupWindow popupWindow;
    private View popupView;

    public OrganizerQREventListPopupWindow(Context context) {

        // Inflate the custom layout/view
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.organizer_fragment_create_event, null);

        initializePopupWindow(); // Creates the pop up menu

        // Confirm button actions
        Button confirmDataButton = popupView.findViewById(R.id.buttonCreateEvent);
        confirmDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createEvent(view);
                popupWindow.dismiss();

            }
        });

    }




    private void createEvent(View view){

        EditText editTextName = view.findViewById(R.id.editTextEventName);
        EditText editTextDescription = view.findViewById(R.id.editTextEventDescription);


    }








    private void initializePopupWindow() {

        // Create PopupWindow
        popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        // Dismiss the popup window when touched outside
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

    }

    public void showPopupWindow() {
        // Show the popup window
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

}
