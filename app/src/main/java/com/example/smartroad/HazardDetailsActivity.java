package com.example.smartroad;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HazardDetailsActivity extends AppCompatActivity {

    private TextView txtType, txtReportedBy, txtDescription, txtStatus, txtLocation;
    private ImageView imgPhoto;
    private Button btnBack;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hazard_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        txtType = findViewById(R.id.txtDetailType);
        txtReportedBy = findViewById(R.id.txtDetailReportedBy);
        txtDescription = findViewById(R.id.txtDetailDescription);
        txtStatus = findViewById(R.id.txtDetailStatus);
        txtLocation = findViewById(R.id.txtDetailLocation);
        imgPhoto = findViewById(R.id.imgDetailPhoto);
        btnBack = findViewById(R.id.btnBack);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Hazards");

        String hazardId = getIntent().getStringExtra("HAZARD_ID");
        if (hazardId != null) {
            loadHazardDetails(hazardId);
        } else {
            Toast.makeText(this, "No Hazard ID found", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadHazardDetails(String hazardId) {
        mDatabase.child(hazardId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Report report = snapshot.getValue(Report.class);
                if (report != null) {
                    txtType.setText(report.hazardType);
                    txtReportedBy.setText(report.username);
                    txtDescription.setText(report.description);
                    txtStatus.setText(report.status);
                    
                    String loc = report.latitude + ", " + report.longitude;
                    txtLocation.setText(loc);

                    if (report.image != null && !report.image.isEmpty()) {
                        try {
                            byte[] decodedString = Base64.decode(report.image, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imgPhoto.setImageBitmap(decodedByte);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    // Set status color
                    if (report.status != null) {
                        if (report.status.equalsIgnoreCase("Resolved")) {
                            txtStatus.setTextColor(getResources().getColor(R.color.success));
                        } else if (report.status.equalsIgnoreCase("New") || report.status.equalsIgnoreCase("Under Investigation")) {
                            txtStatus.setTextColor(getResources().getColor(R.color.warning));
                        }
                    }
                } else {
                    Toast.makeText(HazardDetailsActivity.this, "Hazard data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HazardDetailsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
