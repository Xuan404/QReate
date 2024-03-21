package com.example.qreate.administrator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qreate.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdministratorImageDetailsFragment extends Fragment {
    private ImageView image;
    private TextView imageRef;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.administrator_image_details_page, container, false);
        db = FirebaseFirestore.getInstance();
        image = view.findViewById(R.id.image_details_picture);
        imageRef = view.findViewById(R.id.image_details_reference);

        return view;
    }
}
