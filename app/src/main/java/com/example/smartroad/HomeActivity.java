package com.example.smartroad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import java.util.Locale;
import android.location.Location;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    Button btnGPS;
    TextView txtGreeting, txtLat, txtLong;
    BottomNavigationView bottomNavigationView;
    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnGPS = findViewById(R.id.btnGPS);
        txtGreeting = findViewById(R.id.txtGreeting);
        txtLat = findViewById(R.id.txtLat);
        txtLong = findViewById(R.id.txtLong);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        NavigationUtils.setupBottomNavigation(this, bottomNavigationView, R.id.nav_home);

        // Fetch user name for greeting
        fetchUserName();

        // Initialize Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.home_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnGPS.setOnClickListener(v -> {
            getCurrentLocation();
        });

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        Toast.makeText(this, "Updating Location...", Toast.LENGTH_SHORT).show();
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                txtLat.setText("Latitude : " + String.format(Locale.US, "%.5f", lat));
                txtLong.setText("Longitude : " + String.format(Locale.US, "%.5f", lon));

                if (mMap != null) {
                    LatLng currentLatLng = new LatLng(lat, lon);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                }
            } else {
                Toast.makeText(this, "Unable to get location. Please enable GPS.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserName() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
                if (name != null) {
                    txtGreeting.setText("Hello, " + name + "!");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        loadHazards();
        
        // Default location (Johor Bahru)
        LatLng defaultLoc = new LatLng(1.492659, 103.741359);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 12));
    }

    private void loadHazards() {
        mDatabase.child("Hazards").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mMap == null) return;
                mMap.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Report report = data.getValue(Report.class);
                    if (report != null && report.latitude != null && report.longitude != null) {
                        try {
                            double lat = Double.parseDouble(report.latitude);
                            double lng = Double.parseDouble(report.longitude);
                            LatLng pos = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .position(pos)
                                    .title(report.hazardType)
                                    .snippet(report.description)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
