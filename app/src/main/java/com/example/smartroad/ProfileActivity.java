package com.example.smartroad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

public class ProfileActivity extends AppCompatActivity {


    TextView txtName,txtEmail;
    TextView txtInvestigation,txtResolved;

    Button btnEdit;
    Button btnPassword;
    Button btnLogout;

    BottomNavigationView bottomNavigationView;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        NavigationUtils.setupBottomNavigation(this, bottomNavigationView, R.id.nav_profile);


        //USER INFORMATION

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);


        //REPORT STATUS

        txtInvestigation = findViewById(R.id.txtInvestigation);
        txtResolved = findViewById(R.id.txtResolved);


        //BUTTON

        btnEdit = findViewById(R.id.btnEdit);
        btnPassword = findViewById(R.id.btnPassword);
        btnLogout = findViewById(R.id.btnLogout);



        //DISPLAY DATA

        SharedPreferences sharedPreferences = getSharedPreferences("SmartRoadPrefs", MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "SmartRoad User");
        String email = sharedPreferences.getString("email", "smartroad@gmail.com");

        txtName.setText(name);
        txtEmail.setText(email);

        // Fetch My Reports stats from Firebase
        fetchReportStats();

        //EDIT PROFILE

        btnEdit.setOnClickListener(v -> {
            Toast.makeText(this, "Edit Profile", Toast.LENGTH_SHORT).show();
        });



        //CHANGE PASSWORD

        btnPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Change Password", Toast.LENGTH_SHORT).show();
        });



        //LOGOUT

        btnLogout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("SMARTROAD");
            builder.setMessage("Are you sure you want to logout ?");

            //YES BUTTON
            builder.setPositiveButton("YES", (dialog, which) -> {
                Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });

            //NO BUTTON
            builder.setNegativeButton("NO", (dialog, which) -> {
                dialog.dismiss();
            });

            builder.show();
        });
    }

    private void fetchReportStats() {
        if (mAuth.getCurrentUser() == null) return;
        
        String userId = mAuth.getCurrentUser().getUid();
        
        // Struktur data: reports/userId/
        mDatabase.child("reports").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int investigationCount = 0;
                int resolvedCount = 0;
                
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String status = postSnapshot.child("status").getValue(String.class);
                    if (status != null) {
                        if (status.equalsIgnoreCase("Under Investigation")) {
                            investigationCount++;
                        } else if (status.equalsIgnoreCase("Resolved")) {
                            resolvedCount++;
                        }
                    }
                }
                
                txtInvestigation.setText(String.valueOf(investigationCount));
                txtResolved.setText(String.valueOf(resolvedCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load stats: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}