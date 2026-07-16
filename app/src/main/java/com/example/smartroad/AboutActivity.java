package com.example.smartroad;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class AboutActivity extends AppCompatActivity {


    Button btnContact;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);



        btnContact=findViewById(R.id.btnContact);



        btnContact.setOnClickListener(v -> {

            Toast.makeText(

                    this,

                    "Contact SmartRoad Team",

                    Toast.LENGTH_SHORT).show();


        });



    }


}