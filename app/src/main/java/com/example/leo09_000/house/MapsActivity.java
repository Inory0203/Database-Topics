package com.example.leo09_000.house;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import android.support.annotation.NonNull;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, OnMyLocationButtonClickListener, OnMyLocationClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private GoogleMap map;
    ArrayList<String> addresslist = new ArrayList<String>();
    String marker_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Intent intent=getIntent();
        addresslist=intent.getStringArrayListExtra("address_list");
        map = googleMap;
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        enableMyLocation();
        map.getUiSettings().setZoomControlsEnabled(true);  // 右下角的放大縮小功能
        map.getUiSettings().setCompassEnabled(true);       // 左上角的指南針，要兩指旋轉才會出現
        map.getUiSettings().setMapToolbarEnabled(true);    // 右下角的導覽及開啟 Google Map功能

        for(int i=0;i<addresslist.size();i++) {
            String placeName = addresslist.get(i).trim();
            if (placeName.length() > 0) {
                Geocoder gc = new Geocoder(MapsActivity.this);
                List<Address> addressList = null;
                try {
                    addressList = gc.getFromLocationName(placeName, 1);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (addressList == null || addressList.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "找不到該位置", Toast.LENGTH_SHORT).show();
                } else {
                    Address address = addressList.get(0);
                    LatLng position = new LatLng(address.getLatitude(), address.getLongitude());
                    String snippet = address.getAddressLine(0);
                    map.addMarker(new MarkerOptions().position(position).snippet(snippet).title(placeName)).showInfoWindow();
                    map.moveCamera(CameraUpdateFactory.newLatLng(position));
                    map.animateCamera(CameraUpdateFactory.zoomTo(14));     // 放大地圖到 14 倍大

                }
            }
        }
        map.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        map.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        marker_address = marker.getTitle().toString();
        Intent intent=new Intent();
        intent.putExtra("marker_address",marker_address);
        intent.setClass(MapsActivity.this,Main2Activity.class);
        startActivity(intent);
        return false;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        if (map == null) {
            ((SupportMapFragment)
                    getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            //NORMAL-街景圖 HYBRID-衛星地圖 SATELLITE-衛星照片 TERRAIN-地形圖

        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (map != null) {
            // Access to the location has been granted to the app.
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

}