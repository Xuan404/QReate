package com.example.qreate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ShareQRScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_qr_screen);
        Button shareButton = findViewById(R.id.buttonConfirm);

        shareButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/jpeg");
                //GOTTA PUT THE IMAGE LOCATION HERE
                Uri imageUri = Uri.parse("android.resource://" + getPackageName() + "/drawable/qricon.png");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                startActivity(Intent.createChooser(sharingIntent, "Share Image"));
            }
        }));
    }
}
