package com.example.smartroad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    Button btnGPS;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnGPS = findViewById(R.id.btnGPS);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        NavigationUtils.setupBottomNavigation(this, bottomNavigationView, R.id.nav_home);

        btnGPS.setOnClickListener(v -> {

            Toast.makeText(this,
                    "Getting GPS...",
                    Toast.LENGTH_SHORT).show();

        });

    }
}