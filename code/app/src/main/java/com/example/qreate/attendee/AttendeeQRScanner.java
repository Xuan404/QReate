package com.example.qreate.attendee;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qreate.R;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import javax.annotation.Nullable;

public class AttendeeQRScanner extends AppCompatActivity implements View.OnClickListener{

    //variables
    ImageButton scanButton;
    TextView textContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_tap_to_scan_page);

        //initializing
        scanButton = findViewById(R.id.tap_to_scan_qr_button);
        textContent = findViewById(R.id.text_content);

        //button listener
        scanButton.setOnClickListener(this);

    }
    @Override
    public void onClick(View v){
        //make object
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan a QR code");
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if(intentResult != null){
            if(intentResult.getContents()==null){
                Toast.makeText(getBaseContext(), "Aborted",Toast.LENGTH_SHORT).show();
            } else{
                textContent.setText(intentResult.getContents());
            }
        } else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

}
