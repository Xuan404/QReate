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

public class AdministratorProfileDetailsFragment extends Fragment {
    private ImageView profileImage;
    private TextView profileName;
    private TextView profileMobileNum;
    private TextView profileEmail;
    private TextView profileHomepage;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.administrator_profile_details_page, container, false);
        db = FirebaseFirestore.getInstance();
        profileImage = view.findViewById(R.id.profile_details_pfp);
        profileName = view.findViewById(R.id.profile_details_name);
        profileMobileNum = view.findViewById(R.id.profile_details_phone_num);
        profileEmail = view.findViewById(R.id.profile_details_email);
        profileHomepage = view.findViewById(R.id.profile_details_homepage);

        return view;

    }
}
