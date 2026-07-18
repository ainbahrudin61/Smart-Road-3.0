package com.example.smartroad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {


    TextView txtName,txtEmail;
    TextView txtInvestigation,txtResolved;

    Button btnEdit;
    Button btnPassword;
    Button btnLogout;

    RecyclerView rvMyReports;
    MyReportAdapter adapter;
    List<Report> myReports;

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

        //RECYCLER VIEW
        rvMyReports = findViewById(R.id.rvMyReports);
        myReports = new ArrayList<>();
        adapter = new MyReportAdapter(myReports);
        rvMyReports.setLayoutManager(new LinearLayoutManager(this));
        rvMyReports.setAdapter(adapter);



        //DISPLAY DATA

        // Fetch User Info from Firebase Realtime Database
        fetchUserInfo();

        // Fetch My Reports stats from Firebase
        fetchReportStats();

        //EDIT PROFILE

        btnEdit.setOnClickListener(v -> {
            showEditProfileDialog();
        });



        //CHANGE PASSWORD

        btnPassword.setOnClickListener(v -> {
            String email = mAuth.getCurrentUser().getEmail();
            if (email != null) {
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Password reset email sent to " + email, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });



        //LOGOUT

        btnLogout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("SMARTROAD");
            builder.setMessage("Are you sure you want to logout ?");

            //YES BUTTON
            builder.setPositiveButton("YES", (dialog, which) -> {
                mAuth.signOut();
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

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Profile");

        final EditText input = new EditText(this);
        input.setText(txtName.getText().toString());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                updateProfile(newName);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateProfile(String newName) {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();

        mDatabase.child("users").child(userId).child("name").setValue(newName)
                .addOnSuccessListener(aVoid -> {
                    txtName.setText(newName);
                    Toast.makeText(ProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchUserInfo() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();

        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    txtName.setText(user.name);
                    txtEmail.setText(user.email);

                    // Update local SharedPreferences as backup
                    SharedPreferences sharedPreferences = getSharedPreferences("SmartRoadPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name", user.name);
                    editor.putString("email", user.email);
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Fallback to SharedPreferences if database fails
                SharedPreferences sharedPreferences = getSharedPreferences("SmartRoadPrefs", MODE_PRIVATE);
                String name = sharedPreferences.getString("name", "SmartRoad User");
                String email = sharedPreferences.getString("email", "smartroad@gmail.com");
                txtName.setText(name);
                txtEmail.setText(email);
            }
        });
    }

    private void fetchReportStats() {
        if (mAuth.getCurrentUser() == null) return;
        
        String userId = mAuth.getCurrentUser().getUid();
        
        // Mengambil data dari nod "Hazards" dan menapis mengikut userId
        mDatabase.child("Hazards").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int investigationCount = 0;
                int resolvedCount = 0;
                myReports.clear();
                
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Report report = postSnapshot.getValue(Report.class);
                    if (report != null && userId.equals(report.userId)) {
                        myReports.add(report);
                        String status = report.status;
                        if (status != null) {
                            // Mengira status "New" sebagai sebahagian daripada "Under Investigation" atau mengikut status tepat
                            if (status.equalsIgnoreCase("Under Investigation") || status.equalsIgnoreCase("New")) {
                                investigationCount++;
                            } else if (status.equalsIgnoreCase("Resolved")) {
                                resolvedCount++;
                            }
                        }
                    }
                }
                
                txtInvestigation.setText(String.valueOf(investigationCount));
                txtResolved.setText(String.valueOf(resolvedCount));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load stats: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}