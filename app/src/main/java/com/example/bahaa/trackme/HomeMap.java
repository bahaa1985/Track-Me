package com.example.bahaa.trackme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class HomeMap extends FragmentActivity implements OnMapReadyCallback {

    Double longitude, latitude;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private GoogleMap mMap;
    private  LocationIdentifier locationIdentifier;
    boolean mLocationPermissionGranted;
    @SuppressLint("MissingPermission")
    @Override
    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_map);
        setTitle("حدد موقع منزلك");
//        getLocationPermission();
        locationIdentifier=new LocationIdentifier(this);
        mLocationPermissionGranted=locationIdentifier.getLocationPermission();
        if(mLocationPermissionGranted){
            //Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }


    final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

//    private void getLocationPermission() {
//        /*
//         * Request location permission, so that we can get the location of the
//         * device. The result of the permission request is handled by a callback,
//         * onRequestPermissionsResult.
//         */
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                mLocationPermissionGranted = true;
//
//            } else {
//                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//            }
//        }
//    }
//    private void addMarkerinDeviceLocation(LatLng home){
//    // Add a marker in Sydney and move the camera
//    Geocoder geocoder=new Geocoder(HomeMap.this);
//    try {
//        List<Address> addressList= geocoder.getFromLocation(home.latitude,home.longitude,1);
//        Address address=addressList.get(0);
////                            textViewAddress=findViewById(R.id.text_view_home);
////                            textViewAddress.setText(textViewAddress.getText().toString()+" "+address.getAddressLine(0));
//        mMap.addMarker(new MarkerOptions().position(home).title(address.getAddressLine(0)));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 30));
//        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
//}
//    private void getDeviceLocation() {
//        if (mLocationPermissionGranted) {
//            //get the the current location:
//            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            final Task locationResult = mFusedLocationClient.getLastLocation();
//            locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
//                @Override
//                public void onComplete(@NonNull Task task) {
//                    if (task.getResult() != null && task.isSuccessful()) {
//                        Location currentLocation=(Location) task.getResult();
//                        longitude = currentLocation.getLongitude();
//                        latitude = currentLocation.getLatitude();
////                        String strLocation=longitude.toString().concat(latitude.toString());
////                        Toast.makeText(HomeMap.this,strLocation,Toast.LENGTH_LONG).show();
//                        // Add a marker in location and move the camera
//                        LatLng home = new LatLng(latitude,longitude);
//                        addMarkerinDeviceLocation(home);
//                    }
//                }
//            });
//        }
//        else {
//
//        }
//    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (mLocationPermissionGranted) {
            locationIdentifier.getDeviceLocation(mMap);///*******************
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            //
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    mMap.clear();
                    // Add a marker in location and move the camera
                   locationIdentifier.addMarkerinDeviceLocation(latLng);
                }
            });
        }
    }
}

