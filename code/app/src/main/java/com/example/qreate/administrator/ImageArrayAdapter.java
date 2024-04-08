package com.example.qreate.administrator;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.CompoundButtonCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.qreate.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Custom ArrayAdapter to display a list of {@link AdministratorImage} objects in a ListView within the administrator dashboard.
 * Features image display alongside names and a radio button for selecting a specific image. Only one image can be selected at any time.
 */
public class ImageArrayAdapter extends ArrayAdapter<AdministratorImage> {
    private int selectedPosition = -1; // Track the selected position
    private ImageArrayAdapter.OnImageSelectedListener mListener;

    /**
     * Constructs a new ImageArrayAdapter with the specified context, images list, and listener.
     *
     * @param context  The current context used to inflate layout files.
     * @param images   An ArrayList of {@link AdministratorImage} objects to be displayed.
     * @param listener A listener to handle image selection events.
     */
    public ImageArrayAdapter(Context context, ArrayList<AdministratorImage> images, ImageArrayAdapter.OnImageSelectedListener listener) {
        super(context, 0, images);
        mListener = listener;
    }

    /**
     * Provides a view for an AdapterView.
     *
     * @param position     Position of the item within the adapter's data set.
     * @param convertView  The old view to reuse, if possible.
     * @param parent       The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.administrator_dashboard_images_list, parent, false);
        } else {
            view = convertView;
        }

        AdministratorImage images = getItem(position);
        TextView image_name = view.findViewById(R.id.image_name_text);
        ImageView image = view.findViewById(R.id.image);
        RadioButton radioButton = view.findViewById(R.id.choose_image_radio_button);

        changeRadioColor(view);

        image_name.setText(images.getImageName());
        //image.setImageResource(R.drawable.profile);
        radioButton.setChecked(position == selectedPosition);


        if (images.getType() == AdministratorImage.TYPE_PROFILE) {
            if (images.getImageUrl() != null && !images.getImageUrl().isEmpty()) {
                Glide.with(getContext())
                        .load(images.getImageUrl())
                        .apply(new RequestOptions().circleCrop())
                        .into(image);
            } else if (images.getGeneratedImageUrl() != null && !images.getGeneratedImageUrl().isEmpty()) {
                // Decode Base64 to bitmap
                byte[] decodedString = Base64.decode(images.getGeneratedImageUrl(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                image.setImageBitmap(decodedByte);
            } else {
                // Fallback placeholder if both profile and generated pics are missing
                image.setImageResource(R.drawable.profile);
            }
        } else if (images.getType() == AdministratorImage.TYPE_EVENT) {
            loadEventImageFromFirebaseStorage(images.getImageUrl(), image);
        }


        // Handle radio button clicks
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position; // Update the selected position
                notifyDataSetChanged(); // Notify the adapter to update the radio buttons
                mListener.onImageSelected();
            }
        });
        return view;
    }

    /**
     * Interface for callbacks when an image is selected from the list.
     */
    public interface OnImageSelectedListener {
        void onImageSelected();
    }

    /**
     * Retrieves the ID of the selected image.
     *
     * @return The unique document ID of the selected image or null if no image is selected.
     */
    public String getSelectedImageId() {
        if (selectedPosition != -1) {
            AdministratorImage selectedImage = getItem(selectedPosition);
            return selectedImage.getId();
        }
        return null;
    }

    /**
     * Retrieves the type of the selected image.
     *
     * @return The type of the selected image or -1 if no image is selected.
     */
    public int getSelectedImageType() {
        if (selectedPosition != -1) {
            AdministratorImage selectedImage = getItem(selectedPosition);
            if (selectedImage != null) {
                return selectedImage.getType();
            }
        }
        return -1;
    }

    /**
     * Changes the color of the radio button depending on its checked state.
     * Utilizes a ColorStateList to define colors for checked and unchecked states.
     *
     * @param view The View containing the radio button whose color needs to be changed.
     */
    private void changeRadioColor(View view) {

        RadioButton radioButton = view.findViewById(R.id.choose_image_radio_button);

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


        // Apply the color state list to the RadioButton
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            radioButton.setButtonTintList(colorStateList);
        } else {
            CompoundButtonCompat.setButtonTintList(radioButton, colorStateList); // Support library for pre-Lollipop
        }

    }

    /**
     * Loads an event image from Firebase Storage and displays it in the provided ImageView.
     * Uses Glide to load the image efficiently.
     *
     * @param imagePath The path to the image in Firebase Storage.
     * @param imageView The ImageView where the image will be displayed.
     */
    private void loadEventImageFromFirebaseStorage(String imagePath, ImageView imageView) {
        if (imagePath != null && !imagePath.isEmpty()) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference imageRef = storage.getReference(imagePath);

            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(getContext())
                        .load(uri.toString())
                        .placeholder(R.drawable.profile)
                        .into(imageView);
            }).addOnFailureListener(e -> {
                Log.d("Firestore", "Error getting event image: ", e);
                // Handle the failure by setting a default image or leaving the placeholder
            });
        }
    }
}
