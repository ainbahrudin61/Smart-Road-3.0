package com.example.smartroad;

import static androidx.core.location.LocationManagerCompat.getCurrentLocation;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.location.Location;

import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.location.Address;

import android.location.Geocoder;

import java.io.IOException;

import java.util.List;


public class ReportActivity extends AppCompatActivity {

    Button btnImage, btnLocation, btnSubmit;

    EditText etDescription;

    ImageView imgReport;

    RadioGroup radioHazard;

    TextView txtAddress, txtLatitude, txtLongitude, txtDate, txtTime;

    BottomNavigationView bottomNavigationView;

    boolean imageUploaded=false;

    boolean gpsTaken=false;

    FusedLocationProviderClient fusedLocation;

    private ActivityResultLauncher<String> mGetContent;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize ActivityResultLauncher
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imgReport.setImageURI(uri);
                        imageUploaded = true;
                        Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show();
                    }
                });

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        NavigationUtils.setupBottomNavigation(this, bottomNavigationView, R.id.nav_report);


        //CONNECT XML

        btnImage = findViewById(R.id.btnImage);
        btnLocation = findViewById(R.id.btnLocation);
        btnSubmit = findViewById(R.id.btnSubmit);

        etDescription = findViewById(R.id.etDescription);

        imgReport = findViewById(R.id.imgReport);

        radioHazard = findViewById(R.id.radioHazard);

        txtLatitude = findViewById(R.id.txtLatitude);
        txtLongitude = findViewById(R.id.txtLongitude);
        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txtTime);
        txtAddress= findViewById(R.id.txtAddress);
        fusedLocation=

                LocationServices
                        .getFusedLocationProviderClient(this);


        //CURRENT DATE

        String currentDate = new SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault())
                .format(new Date());


        //CURRENT TIME

        String currentTime = new SimpleDateFormat(
                "hh:mm a",
                Locale.getDefault())
                .format(new Date());


        txtDate.setText("Date : " + currentDate);
        txtTime.setText("Time : " + currentTime);


        //IMAGE BUTTON

        btnImage.setOnClickListener(v -> {
            mGetContent.launch("image/*");
        });


        //LOCATION BUTTON

        btnLocation.setOnClickListener(v -> {

            getCurrentLocation();

        });


        //SUBMIT BUTTON

        btnSubmit.setOnClickListener(v -> {


            String description =
                    etDescription.getText()
                            .toString()
                            .trim();


            //DESCRIPTION

            if (description.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please enter description",
                        Toast.LENGTH_SHORT).show();

                return;
            }


            //HAZARD

            if (radioHazard.getCheckedRadioButtonId() == -1) {

                Toast.makeText(
                        this,
                        "Please select hazard type",
                        Toast.LENGTH_SHORT).show();

                return;
            }


            //IMAGE

            if (!imageUploaded) {

                Toast.makeText(
                        this,
                        "Please upload image",
                        Toast.LENGTH_SHORT).show();

                return;
            }


            //GPS

            if (!gpsTaken) {

                Toast.makeText(
                        this,
                        "Please get current location",
                        Toast.LENGTH_SHORT).show();

                return;
            }


            //SUCCESS

            saveReportToFirebase(description);


        });


    }

    private void getCurrentLocation(){


        if(ActivityCompat.checkSelfPermission(

                this,

                Manifest.permission
                        .ACCESS_FINE_LOCATION)

                != PackageManager.PERMISSION_GRANTED){



            ActivityCompat.requestPermissions(

                    this,

                    new String[]{

                            Manifest.permission
                                    .ACCESS_FINE_LOCATION

                    },

                    100

            );


            return;


        }



        fusedLocation.getLastLocation()

                .addOnSuccessListener(location->{


                    if(location!=null){



                        double latitude=
                                location.getLatitude();


                        double longitude=
                                location.getLongitude();

                        Geocoder geocoder=

                                new Geocoder(
                                        this,
                                        Locale.getDefault());


                        try{


                            List<Address> addresses=

                                    geocoder.getFromLocation(

                                            latitude,
                                            longitude,
                                            1);


                            if(addresses!=null && !addresses.isEmpty()){


                                Address address=
                                        addresses.get(0);


                                String fullAddress=


                                        address.getAddressLine(0);


                                txtAddress.setText(

                                        "Current Location : \n\n"

                                                +fullAddress);



                            }


                        }catch (IOException e){

                            e.printStackTrace();

                        }



                        txtLatitude.setText(

                                "Latitude : "+latitude);


                        txtLongitude.setText(

                                "Longitude : "+longitude);



                        gpsTaken=true;



                        Toast.makeText(

                                this,

                                "GPS Retrieved",

                                Toast.LENGTH_SHORT).show();



                    } else {
                        Toast.makeText(this, "Unable to get GPS. Please ensure GPS is on.", Toast.LENGTH_SHORT).show();
                    }


                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Location Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });


    }


    private void saveReportToFirebase(String description) {
        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();
        String reportId = mDatabase.child("reports").push().getKey();

        int selectedId = radioHazard.getCheckedRadioButtonId();
        android.widget.RadioButton radioButton = findViewById(selectedId);
        String hazardType = radioButton.getText().toString();

        double latitude = Double.parseDouble(txtLatitude.getText().toString().replace("Latitude : ", ""));
        double longitude = Double.parseDouble(txtLongitude.getText().toString().replace("Longitude : ", ""));
        String address = txtAddress.getText().toString().replace("Current Location : \n\n", "");
        String date = txtDate.getText().toString().replace("Date : ", "");
        String time = txtTime.getText().toString().replace("Time : ", "");

        Report report = new Report(reportId, userId, description, hazardType, 
                                   latitude, longitude, address, date, time, "Pending Verification");

        if (reportId != null) {
            // Save under global reports for home map and under user's reports for profile
            mDatabase.child("all_reports").child(reportId).setValue(report);
            mDatabase.child("user_reports").child(userId).child(reportId).setValue(report)
                .addOnSuccessListener(aVoid -> {
                    showSuccessDialog();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to submit: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        }
    }


    //SUCCESS POPUP

    private void showSuccessDialog() {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);


        builder.setTitle("SMARTROAD");


        builder.setMessage(

                "Report Submitted Successfully.\n\n" +

                        "Status : Pending Verification\n\n" +

                        "Your report will be verified by the administrator before being displayed on the map."

        );


        builder.setPositiveButton(

                "OK",

                (dialog, which) -> dialog.dismiss()

        );


        builder.show();

    }


}