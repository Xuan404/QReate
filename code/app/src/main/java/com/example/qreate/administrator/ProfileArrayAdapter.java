package com.example.qreate.administrator;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Base64;
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

import java.util.ArrayList;

/**
 * A custom ArrayAdapter designed to handle lists of {@link AdministratorProfile} objects. This adapter is tailored
 * for displaying administrator profiles within a ListView in the administrator dashboard. Each list item features a
 * profile name, an optional profile image, and a radio button to select a specific profile, allowing for single-choice
 * selection behavior within the list.
 */
public class ProfileArrayAdapter extends ArrayAdapter<AdministratorProfile> {
    private int selectedPosition = -1; // Track the selected position
    private ProfileArrayAdapter.OnProfileSelectedListener mListener;

    /**
     * Constructs a new ProfileArrayAdapter.
     *
     * @param context  The current context. Used to inflate the layout file.
     * @param profiles An ArrayList of {@link AdministratorProfile} objects to display in the list.
     * @param listener An instance of OnProfileSelectedListener to handle profile selection events.
     */
    public ProfileArrayAdapter(Context context, ArrayList<AdministratorProfile> profiles, ProfileArrayAdapter.OnProfileSelectedListener listener) {
        super(context, 0, profiles);
        mListener = listener;
    }

    /**
     * Provides a view for an AdapterView (ListView).
     *
     * @param position     The position of the item within the adapter's data set.
     * @param convertView  The old view to reuse, if possible.
     * @param parent       The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.administrator_dashboard_profiles_list, parent, false);
        } else {
            view = convertView;
        }

        AdministratorProfile profile = getItem(position);
        TextView profile_name = view.findViewById(R.id.profile_name_text);
        ImageView profile_image = view.findViewById(R.id.profile_image);
        RadioButton radioButton = view.findViewById(R.id.choose_profile_radio_button);

        changeRadioColor(view);

        profile_name.setText(profile.getProfileName());
        //profile_image.setImageResource(R.drawable.profile);
        radioButton.setChecked(position == selectedPosition);

        // Handle radio button clicks
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position; // Update the selected position
                notifyDataSetChanged(); // Notify the adapter to update the radio buttons
                mListener.onProfileSelected();
            }
        });

        AdministratorProfile profile_id = getItem(position);

        // Check if profileImage (URL) is not null and not empty
        if (profile.getProfileImage() != null && !profile.getProfileImage().isEmpty()) {
            Glide.with(getContext())
                    .load(profile.getProfileImage())
                    .apply(new RequestOptions().circleCrop())
                    .into(profile_image);
        } else if (profile.getGeneratedPic() != null && !profile.getGeneratedPic().isEmpty()) {
            // Decode Base64 to bitmap
            byte[] decodedString = Base64.decode(profile.getGeneratedPic(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            profile_image.setImageBitmap(decodedByte);
        } else {
            // Fallback placeholder if both profile and generated pics are missing
            profile_image.setImageResource(R.drawable.profile);
        }
        return view;
    }


    /**
     * Interface for receiving notification when a profile is selected.
     */
    public interface OnProfileSelectedListener {
        void onProfileSelected();
    }

    /**
     * Retrieves the ID of the currently selected profile.
     *
     * @return The unique document ID of the selected profile, or null if no profile is selected.
     */
    public String getSelectedProfileId() {
        if (selectedPosition != -1) {
            AdministratorProfile selectedProfile = getItem(selectedPosition);
            return selectedProfile.getId();
        }
        return null;
    }

    /**
     * Changes the color of the radio button based on its checked state. Uses a ColorStateList to define
     * different colors for checked and unchecked states.
     *
     * @param view The View that contains the radio button.
     */
    private void changeRadioColor(View view) {

        RadioButton radioButton = view.findViewById(R.id.choose_profile_radio_button);

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
