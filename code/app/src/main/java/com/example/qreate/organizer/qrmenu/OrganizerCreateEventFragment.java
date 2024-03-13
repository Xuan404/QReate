package com.example.qreate.organizer.qrmenu;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import com.example.qreate.R;

/**
 * The following class is responsible for the create event popup
 *
 * Outstanding Issue: Created event isn't stored in firebase yet
 * @author Denis Soh
 */
public class OrganizerCreateEventFragment extends DialogFragment {
    private static final int REQUEST_IMAGE_PICKER = 1;
    private ImageView imageView;
    interface AddEventDialogListener {
        void addEvent(OrganizerEvent event);
    }
    private AddEventDialogListener listener;
    /**
     * attaches fragment and adds listener
     *
     * @param context current context
     *
     */

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddEventDialogListener) {
            listener = (AddEventDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement AddCityDialogListener");
        }
    }

    /**
     * creates fragment
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_create_event, null);
        EditText addName = view.findViewById(R.id.editTextEventName);
        EditText addDescription = view.findViewById(R.id.editTextEventDescription);
        Button createButton = view.findViewById(R.id.buttonCreateEvent);
        ImageButton addPoster = view.findViewById(R.id.buttonUploadPoster);
        TextView posterName = view.findViewById(R.id.imageName);
        addPoster.setOnClickListener(v -> {
            openImagePicker();
            posterName.setVisibility(View.VISIBLE);
            addPoster.setVisibility(View.INVISIBLE);
        });
        //TODO don't need this cancel button we should change it such that the fragment closes once you click off
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(view)
                .setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        createButton.setOnClickListener(v -> {
            String eventName = addName.getText().toString();
            String eventDescription = addDescription.getText().toString();
            listener.addEvent(new OrganizerEvent(eventName, eventDescription));
            dialog.dismiss();
        });
        return dialog;
    }
    /**
     * Creates the view and inflates the fragment_create_event layout
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     */
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
    /**
     * opens image picker
     *
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICKER);
    }
    /**
     * gets image picker result might need to be updated as there are depreciated components
     *
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == RESULT_OK) {
            // Handle the selected image here
            Uri selectedImageUri = data.getData();
            // Load the image into your ImageView or process it further
            imageView.setImageURI(selectedImageUri);
        }
    }
}