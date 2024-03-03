package com.example.qreate;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class QRMenuScreenActivity  extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_menu_screen);
        //BottomNavigationView bottomNavigationView = findViewById(R.id.bottomMenu);
        //bottomNavigationView.setSelectedItemId(R.id.defaultNavPlaceholder); // 'none' is a fake ID.
    }
}
