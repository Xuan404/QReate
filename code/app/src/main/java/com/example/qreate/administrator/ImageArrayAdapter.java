package com.example.qreate.administrator;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
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

import com.example.qreate.R;

import java.util.ArrayList;

/**
 * An ArrayAdapter subclass for displaying a list of {@link AdministratorImage} objects.
 * This adapter is designed for use within a ListView in the administrator dashboard, showing images
 * alongside their names and a radio button to select a specific image. Only one image
 * can be selected at a time.
 */
public class ImageArrayAdapter extends ArrayAdapter<AdministratorImage> {
    private int selectedPosition = -1; // Track the selected position
    private ImageArrayAdapter.OnImageSelectedListener mListener;

    /**
     * Constructs a new {@code ImageArrayAdapter}.
     * @param context The current context. Used to inflate the layout file.
     * @param images An ArrayList of {@link AdministratorImage} objects to display in the list.
     */
    public ImageArrayAdapter(Context context, ArrayList<AdministratorImage> images, ImageArrayAdapter.OnImageSelectedListener listener) {
        super(context, 0, images);
        mListener = listener;
    }

    /**
     * Provides a view (of images list) for the ListView
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return view
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

    public interface OnImageSelectedListener {
        void onImageSelected();
    }

    /*
    public void clearSelection() {
        selectedPosition = -1; // Reset the selected position
        notifyDataSetChanged(); // Notify the adapter to refresh the list view
    }
     */

    public String getSelectedImageId() {
        if (selectedPosition != -1) {
            AdministratorImage selectedImage = getItem(selectedPosition);
            return selectedImage.getId();
        }
        return null;
    }

    public String getSelectedImageType() {
        if (selectedPosition != -1) {
            AdministratorImage selectedImage = getItem(selectedPosition);
            if (selectedImage != null) {
                return selectedImage.getImageType();
            }
        }
        return null;
    }

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
}
