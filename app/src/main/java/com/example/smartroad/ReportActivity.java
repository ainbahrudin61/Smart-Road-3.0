package com.example.smartroad;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    private Button btnImage, btnLocation, btnSubmit;
    private EditText etDescription;
    private ImageView imgReport;
    private RadioGroup radioHazard;
    private TextView txtAddress, txtLatitude, txtLongitude, txtDate, txtTime;
    private BottomNavigationView bottomNavigationView;

    private boolean imageUploaded = false;
    private boolean gpsTaken = false;
    private Uri selectedImageUri;
    private String currentUsername = "Unknown User";

    private FusedLocationProviderClient fusedLocation;
    private ActivityResultLauncher<String> mGetContent;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    // Variables to store location data
    private double currentLat, currentLng;
    private String currentLocationStr = "";
    private String currentDateStr, currentTimeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        fusedLocation = LocationServices.getFusedLocationProviderClient(this);

        initUI();
        setupDateTime();
        fetchUsername();

        // Initialize ActivityResultLauncher
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        imgReport.setImageURI(uri);
                        imageUploaded = true;
                        Toast.makeText(this, getString(R.string.report_image_selected), Toast.LENGTH_SHORT).show();
                    }
                });

        NavigationUtils.setupBottomNavigation(this, bottomNavigationView, R.id.nav_report);

        btnImage.setOnClickListener(v -> mGetContent.launch("image/*"));
        btnLocation.setOnClickListener(v -> getCurrentLocation());
        btnSubmit.setOnClickListener(v -> validateAndSubmit());
    }

    private void initUI() {
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
        txtAddress = findViewById(R.id.txtAddress);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupDateTime() {
        currentDateStr = "-";
        currentTimeStr = "-";

        txtDate.setText(getString(R.string.report_date_label, currentDateStr));
        txtTime.setText(getString(R.string.report_time_label, currentTimeStr));
    }

    private void fetchUsername() {
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null && user.name != null) {
                            currentUsername = user.name;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocation.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        currentLat = location.getLatitude();
                        currentLng = location.getLongitude();

                        currentDateStr = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        currentTimeStr = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(new Date()).toLowerCase();
                        txtDate.setText(getString(R.string.report_date_label, currentDateStr));
                        txtTime.setText(getString(R.string.report_time_label, currentTimeStr));

                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(currentLat, currentLng, 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                currentLocationStr = addresses.get(0).getAddressLine(0);
                                txtAddress.setText(getString(R.string.report_address_display, currentLocationStr));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        txtLatitude.setText(getString(R.string.report_lat_label, String.valueOf(currentLat)));
                        txtLongitude.setText(getString(R.string.report_long_label, String.valueOf(currentLng)));
                        gpsTaken = true;
                        Toast.makeText(this, getString(R.string.report_gps_retrieved), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getString(R.string.report_gps_error), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
            Toast.makeText(this, getString(R.string.report_location_error, e.getMessage()), Toast.LENGTH_SHORT).show();
        });
    }

    private void validateAndSubmit() {
        String description = etDescription.getText().toString().trim();

        if (description.isEmpty()) {
            Toast.makeText(this, getString(R.string.report_enter_description), Toast.LENGTH_SHORT).show();
            return;
        }

        if (radioHazard.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, getString(R.string.report_select_hazard), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!imageUploaded) {
            Toast.makeText(this, getString(R.string.report_upload_image), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!gpsTaken) {
            Toast.makeText(this, getString(R.string.report_get_location), Toast.LENGTH_SHORT).show();
            return;
        }

        saveReportToFirebase(description);
    }

    private void saveReportToFirebase(String description) {
        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();
        String hazardId = mDatabase.child("Hazards").push().getKey();

        int selectedId = radioHazard.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        String hazardType = radioButton.getText().toString();

        String base64Image = encodeImageToBase64(selectedImageUri);
        String userAgent = android.os.Build.MODEL; // Mendapatkan model peranti (cth: Pixel 4, SM-G973F)

        // Updated constructor: hazardId, userId, username, description, hazardType, lat, lng, location, date, time, status, image, userAgent
        Report report = new Report(hazardId, userId, currentUsername, description, hazardType,
                String.valueOf(currentLat), String.valueOf(currentLng), currentLocationStr, currentDateStr, currentTimeStr, "New", base64Image, userAgent);

        if (hazardId != null) {
            mDatabase.child("Hazards").child(hazardId).setValue(report)
                    .addOnSuccessListener(unused -> showSuccessDialog())
                    .addOnFailureListener(e -> Toast.makeText(ReportActivity.this,
                            "Failed to save report: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show());
        }
    }

    private String encodeImageToBase64(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            
            // Kecilkan saiz gambar (Resize) untuk mengelakkan string terlalu panjang
            int maxSize = 480; // Maksimum lebar atau tinggi
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float bitmapRatio = (float) width / (float) height;
            if (bitmapRatio > 1) {
                width = maxSize;
                height = (int) (width / bitmapRatio);
            } else {
                height = maxSize;
                width = (int) (height * bitmapRatio);
            }
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // Gunakan mampatan 70% untuk kualiti yang seimbang
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.report_success_title))
                .setMessage(getString(R.string.report_success_message))
                .setPositiveButton(getString(R.string.report_ok), (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }
}
