package com.example.smartroad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class AboutActivity extends AppCompatActivity {


    Button btnGithub;
    BottomNavigationView bottomNavigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        NavigationUtils.setupBottomNavigation(this, bottomNavigationView, R.id.nav_about);



        btnGithub = findViewById(R.id.btnGithub);



        btnGithub.setOnClickListener(v -> {
            // Replace with your actual GitHub URL
            String githubUrl = "https://github.com/ainbahrudin61/Smart-Road-V4.git";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl));
            startActivity(intent);
        });



    }


}