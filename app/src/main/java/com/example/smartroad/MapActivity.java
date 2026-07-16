package com.example.smartroad;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback {


    GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);


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


        //JOHOR BAHRU LOCATION

        LatLng johorBahru = new LatLng(
                1.492659,
                103.741359);


        //MARKER

        mMap.addMarker(

                new MarkerOptions()

                        .position(johorBahru)

                        .title("Pothole")

                        .snippet("Verified Hazard Report")

                        .icon(BitmapDescriptorFactory
                                .defaultMarker(
                                        BitmapDescriptorFactory.HUE_ORANGE))

        );


        //ZOOM MAP

        mMap.moveCamera(

                CameraUpdateFactory
                        .newLatLngZoom(
                                johorBahru,
                                15)

        );


    }


}