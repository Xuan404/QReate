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

public class ImageArrayAdapter extends ArrayAdapter<Image> {
    private int selectedPosition = -1; // Track the selected position
    public ImageArrayAdapter(Context context, ArrayList<Image> images) {
        super(context, 0, images);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.administrator_dashboard_images_list, parent, false);
        } else {
            view = convertView;
        }

        Image images = getItem(position);
        TextView image_name = view.findViewById(R.id.image_name_text);
        ImageView image = view.findViewById(R.id.image);
        RadioButton radioButton = view.findViewById(R.id.choose_image_radio_button);

        image_name.setText(images.getImageName());
        //image.setImageResource(R.drawable.profile);
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
