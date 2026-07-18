package com.example.smartroad;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private BottomNavigationView bottomNavigationView;
    private DatabaseReference mDatabase;
    private Map<String, Report> reportMap = new HashMap<>();
    private RecyclerView rvNearbyHazards;
    private NearbyHazardAdapter adapter;
    private ArrayList<NearbyHazard> nearbyHazards;
    private FusedLocationProviderClient fusedLocationClient;

    private double userLat;
    private double userLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationUtils.setupBottomNavigation(this, bottomNavigationView, R.id.nav_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        rvNearbyHazards = findViewById(R.id.rvNearbyHazards);
        nearbyHazards = new ArrayList<>();
        adapter = new NearbyHazardAdapter(nearbyHazards);
        rvNearbyHazards.setLayoutManager(new LinearLayoutManager(this));
        rvNearbyHazards.setAdapter(adapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                userLat = location.getLatitude();
                userLng = location.getLongitude();
                loadHazards();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        mMap.setOnMarkerClickListener(this);
        getCurrentLocation();

        // Default location (Johor Bahru)
        LatLng johorBahru = new LatLng(1.492659, 103.741359);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(johorBahru, 12));
    }

    private void loadHazards() {
        mDatabase.child("Hazards").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mMap == null) return;
                mMap.clear();
                reportMap.clear();
                nearbyHazards.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Report report = data.getValue(Report.class);
                    if (report == null) continue;

                    float[] result = new float[1];
                    double lat = Double.parseDouble(report.latitude);
                    double lng = Double.parseDouble(report.longitude);
                    
                    Location.distanceBetween(
                            userLat,
                            userLng,
                            lat,
                            lng,
                            result
                    );
                    float distanceMeter = result[0];

                    if (distanceMeter <= 5000) {
                        nearbyHazards.add(new NearbyHazard(
                                report.hazardId,
                                report.hazardType,
                                String.format(getString(R.string.map_distance_format), distanceMeter)
                        ));
                    }

                    LatLng pos = new LatLng(lat, lng);
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(pos)
                            .title(report.hazardType)
                            .snippet(report.description)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    if (marker != null) {
                        reportMap.put(marker.getId(), report);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MapActivity.this, getString(R.string.map_load_failed), Toast.LENGTH_SHORT).show();
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
        details.append(getString(R.string.map_report_status, report.status)).append("\n");
        details.append(getString(R.string.map_report_description, report.description)).append("\n");
        details.append(getString(R.string.map_report_address, report.location)).append("\n");
        details.append(getString(R.string.map_report_date, report.date, report.time)).append("\n");

        builder.setMessage(details.toString());
        builder.setPositiveButton(getString(R.string.map_close), (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
