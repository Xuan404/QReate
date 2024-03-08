package com.example.qreate.attendee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qreate.R;
import com.google.zxing.integration.android.IntentIntegrator;

public class AttendeeScanFragment extends Fragment implements View.OnClickListener{

    //variables
    ImageButton scanButton;
    TextView textContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.attendee_tap_to_scan_page, container, false);

        //initializing
        scanButton= view.findViewById(R.id.tap_to_scan_qr_button);
        textContent = view.findViewById(R.id.text_content);
        //button listener
        scanButton.setOnClickListener(this);

        return view;
    }
    @Override
    public void onClick(View v){
        //make object
        IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
        intentIntegrator.setPrompt("Scan a QR code");
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.initiateScan();
    }

}
