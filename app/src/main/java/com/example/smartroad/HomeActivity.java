package com.example.smartroad;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    Button btnGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnGPS = findViewById(R.id.btnGPS);

        btnGPS.setOnClickListener(v -> {

            Toast.makeText(this,
                    "Getting GPS...",
                    Toast.LENGTH_SHORT).show();

        });

    }
}