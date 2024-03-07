package com.example.qreate.administrator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qreate.R;

import java.util.ArrayList;

public class ProfileArrayAdapter extends ArrayAdapter<Profile> {
    private int selectedPosition = -1; // Track the selected position
    public ProfileArrayAdapter(Context context, ArrayList<Profile> profiles) {
        super(context, 0, profiles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.administrator_dashboard_profiles_list, parent, false);
        } else {
            view = convertView;
        }

        Profile profile = getItem(position);
        TextView profile_name = view.findViewById(R.id.profile_name_text);
        ImageView profile_image = view.findViewById(R.id.profile_image);
        RadioButton radioButton = view.findViewById(R.id.choose_profile_radio_button);

        profile_name.setText(profile.getProfileName());
        profile_image.setImageResource(R.drawable.profile);
        radioButton.setChecked(position == selectedPosition);

        // Handle radio button clicks
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position; // Update the selected position
                notifyDataSetChanged(); // Notify the adapter to update the radio buttons
            }
        });
        return view;
    }
}
