package com.example.qreate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.generate_qr_code_screen);
        //Intent intent = new Intent(MainActivity.this, QRMenuScreenActivity.class);
        //startActivity(intent);

    }
}