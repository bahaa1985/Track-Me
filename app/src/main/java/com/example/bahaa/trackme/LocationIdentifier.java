package com.example.bahaa.trackme;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.webkit.WebSettings;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationIdentifier extends FragmentActivity {

    private Context mContext;
    GoogleMap mMap;
    Double longitude, latitude;
    private FusedLocationProviderClient mFusedLocationClient;
    boolean mLocationPermissionGranted;
    final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public LocationIdentifier(Context context){
        mContext=context;
    }

    public boolean getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext.getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            } else {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
        return mLocationPermissionGranted;
    }

    public void addMarkerinDeviceLocation(LatLng home) {
        // Add a marker in Sydney and move the camera
        Geocoder geocoder = new Geocoder(mContext);
        try {
            List<Address> addressList =new ArrayList<>();
            addressList=geocoder.getFromLocation(home.latitude, home.longitude, 1);
            Address address = addressList.get(0);
//                            textViewAddress=findViewById(R.id.text_view_home);
//                            textViewAddress.setText(textViewAddress.getText().toString()+" "+address.getAddressLine(0));
            mMap.addMarker(new MarkerOptions().position(home).title(address.getAddressLine(0)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 10));
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getDeviceLocation(GoogleMap map) {
        if (mLocationPermissionGranted) {
            //get the the current location:
            mMap=map;
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            final Task locationResult = mFusedLocationClient.getLastLocation();
            locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.getResult() != null && task.isSuccessful()) {
                        Location currentLocation=(Location) task.getResult();
                        longitude = currentLocation.getLongitude();
                        latitude = currentLocation.getLatitude();//
                        // Add a marker in location and move the camera
                        LatLng home = new LatLng(latitude,longitude);
                        addMarkerinDeviceLocation(home);
                    }
                }
            });
        }
        else {

        }
    }
}
