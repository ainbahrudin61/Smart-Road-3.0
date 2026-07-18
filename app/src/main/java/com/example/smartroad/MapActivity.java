package com.example.smartroad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    private GoogleMap mMap;
    private BottomNavigationView bottomNavigationView;
    private DatabaseReference mDatabase;
    private Map<String, Report> reportMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        NavigationUtils.setupBottomNavigation(this, bottomNavigationView, R.id.nav_map);


        SupportMapFragment mapFragment =
                (SupportMapFragment)
                        getSupportFragmentManager()
                                .findFragmentById(R.id.map);


        if (mapFragment != null) {

            mapFragment.getMapAsync(this);

        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        loadHazards();

        // Default location (Johor Bahru)
        LatLng johorBahru = new LatLng(1.492659, 103.741359);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(johorBahru, 12));
    }

    private void loadHazards() {
        mDatabase.child("all_reports").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mMap == null) return;
                mMap.clear();
                reportMap.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Report report = data.getValue(Report.class);
                    if (report != null) {
                        LatLng pos = new LatLng(report.latitude, report.longitude);
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(pos)
                                .title(report.hazardType)
                                .snippet(report.description)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        
                        if (marker != null) {
                            reportMap.put(marker.getId(), report);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MapActivity.this, "Failed to load reports.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Report report = reportMap.get(marker.getId());
        if (report != null) {
            showReportDetails(report);
        }
        return false;
    }

    private void showReportDetails(Report report) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(report.hazardType);
        
        StringBuilder details = new StringBuilder();
        details.append("Status: ").append(report.status).append("\n");
        details.append("Description: ").append(report.description).append("\n");
        details.append("Address: ").append(report.address).append("\n");
        details.append("Date: ").append(report.date).append(" ").append(report.time).append("\n");
        
        builder.setMessage(details.toString());
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


}