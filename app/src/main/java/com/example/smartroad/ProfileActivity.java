package com.example.smartroad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {


    TextView txtName,txtEmail,txtPhone;
    TextView txtPending,txtVerified,txtResolved;

    Button btnEdit;
    Button btnPassword;
    Button btnLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        //USER INFORMATION

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);


        //REPORT STATUS

        txtPending = findViewById(R.id.txtPending);
        txtVerified = findViewById(R.id.txtVerified);
        txtResolved = findViewById(R.id.txtResolved);


        //BUTTON

        btnEdit = findViewById(R.id.btnEdit);
        btnPassword = findViewById(R.id.btnPassword);
        btnLogout = findViewById(R.id.btnLogout);



        //DISPLAY DATA

        txtName.setText("Aqilah");
        txtEmail.setText("aqilah@gmail.com");
        txtPhone.setText("0123456789");

        txtPending.setText("2");
        txtVerified.setText("4");
        txtResolved.setText("5");



        //EDIT PROFILE

        btnEdit.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Edit Profile",
                    Toast.LENGTH_SHORT).show();

        });



        //CHANGE PASSWORD

        btnPassword.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Change Password",
                    Toast.LENGTH_SHORT).show();

        });



        //LOGOUT

        btnLogout.setOnClickListener(v -> {


            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this);


            builder.setTitle("SMARTROAD");


            builder.setMessage(
                    "Are you sure you want to logout ?");


            //YES BUTTON

            builder.setPositiveButton("YES",
                    (dialog, which) -> {


                        Toast.makeText(
                                this,
                                "Logout Successful",
                                Toast.LENGTH_SHORT).show();



                        Intent intent =
                                new Intent(
                                        ProfileActivity.this,
                                        LoginActivity.class);


                        intent.addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                        Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK);



                        startActivity(intent);


                        finish();


                    });



            //NO BUTTON

            builder.setNegativeButton("NO",
                    (dialog, which) -> {


                        dialog.dismiss();


                    });



            builder.show();


        });



    }


}