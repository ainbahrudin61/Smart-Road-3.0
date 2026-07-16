package com.example.smartroad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class AboutActivity extends AppCompatActivity {


    Button btnContact;
    BottomNavigationView bottomNavigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        NavigationUtils.setupBottomNavigation(this, bottomNavigationView, R.id.nav_about);



        btnContact=findViewById(R.id.btnContact);



        btnContact.setOnClickListener(v -> {

            Toast.makeText(

                    this,

                    "Contact SmartRoad Team",

                    Toast.LENGTH_SHORT).show();


        });



    }


}