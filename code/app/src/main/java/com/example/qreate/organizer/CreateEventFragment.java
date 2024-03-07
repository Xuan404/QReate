package com.example.qreate.organizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.qreate.Event;
import com.example.qreate.R;

public class CreateEventFragment extends DialogFragment {
    interface AddEventDialogListener {
        void addEvent(Event event);
    }
    private AddEventDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddEventDialogListener) {
            listener = (AddEventDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement AddCityDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_create_event, null);
        EditText addName = view.findViewById(R.id.editTextEventName);
        EditText addDescription = view.findViewById(R.id.editTextEventDescription);
        Button createButton = view.findViewById(R.id.buttonCreateEvent);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(view)
                .setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        createButton.setOnClickListener(v -> {
            String eventName = addName.getText().toString();
            String eventDescription = addDescription.getText().toString();
            listener.addEvent(new Event(eventName, eventDescription));
            dialog.dismiss();
        });
        return dialog;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);

        EditText addName = view.findViewById(R.id.editTextEventName);
        EditText addDescription = view.findViewById(R.id.editTextEventDescription);
        ImageButton addPosterButton = view.findViewById(R.id.buttonUploadPoster);
        Button createButton = view.findViewById(R.id.buttonCreateEvent);


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_event, container, false);

    }
}