package com.example.qreate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class UpdateProfilePicFragment extends Fragment {
    private ActivityResultLauncher<String> mGetContent;
    private OnImageSelectedListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.update_profile_pic, container, false);

        ImageButton backButton = view.findViewById(R.id.update_profile_screen_backbutton);
        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        LinearLayout chooseImageButton = view.findViewById(R.id.choose_image);
        chooseImageButton.setOnClickListener(v -> mGetContent.launch("image/*"));

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if (uri != null && listener != null) {
                    // upload the image to Firebase or another server
                    // TODO: uploadImageToFirebaseStorage(uri);
                    getParentFragmentManager().popBackStack();
                    listener.onImageSelected(uri);
                }
            }
        });
    }

    public interface OnImageSelectedListener {
        void onImageSelected(Uri imageUri);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnImageSelectedListener) {
            listener = (OnImageSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnImageSelectedListener");
        }
    }

}
