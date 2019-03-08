package com.example.bahaa.trackme;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocationIdentifier extends FragmentActivity {

    private Context mContext;
    GoogleMap mMap;
    private Double longitude;
    private Double latitude;
    private FusedLocationProviderClient mFusedLocationClient;
    boolean mLocationPermissionGranted;
    final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    public LocationIdentifier(Context context){
        mContext=context;
    }

    public boolean getLocationPermission() {
        try {
            /*
             * Request location permission, so that we can get the location of the
             * device. The result of the permission request is handled by a callback,
             * onRequestPermissionsResult.
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                } else {
                    this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }
            }

        } catch (Exception e) {
            Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return mLocationPermissionGranted;
    }



    public void getDeviceLocation(GoogleMap map) {
        try {
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
                        task.addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                Location currentLocation=(Location) task.getResult();
                                setLongitude(currentLocation.getLongitude());
                                setLatitude(currentLocation.getLatitude());//
                                // Add a marker in location and move the camera
                                LatLng home = new LatLng(getLatitude(), getLongitude());
                                //addMarkerinDeviceLocation(home);
                            }
                        });//
                    }
                });
            }
            else {

            }
        } catch (Exception e) {
             Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }

    public void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                //get the the current location:
               // mMap=map;
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
//                        if (task.getResult() != null && task.isSuccessful()) {
                            Location currentLocation=(Location) task.getResult();
                            setLongitude(currentLocation.getLongitude());
                            setLatitude(currentLocation.getLatitude());//
                            // Add a marker in location and move the camera
                            //LatLng home = new LatLng(getLatitude(), getLongitude());
                            //addMarkerinDeviceLocation(home);
//                        }
                    }
                });
            }
            else {

            }
        } catch (Exception e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }
    HashMap<String,MarkerOptions> hash_marker=new HashMap<>();
    public void addMarkerinDeviceLocation(LatLng home,GoogleMap mMap) {
        // Add a marker in Sydney and move the camera
        Geocoder geocoder = new Geocoder(mContext);
        try {
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 15));
            List<Address> addressList =new ArrayList<>();
            addressList=geocoder.getFromLocation(home.latitude, home.longitude, 1);
            String address_result="";
            if(addressList.size()>0){
                Address address = addressList.get(0);
                address_result=address.getAddressLine(0);
               // Log.i("tag",address_result);
            }
            if(hash_marker.size()>0) {
                hash_marker.clear();
            }
            hash_marker.put("new marker",new MarkerOptions().position(home).title(address_result));
            mMap.addMarker(hash_marker.get("new marker"));
            setLongitude(home.longitude);setLatitude(home.latitude);
        } catch (IOException e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void moveCamera(String search_string){
        Geocoder geocoder=new Geocoder(mContext);
        try {
            List<Address> addressList =new ArrayList<>();
            addressList=geocoder.getFromLocationName(search_string,1);
            if(addressList.size()>0){
                Address address = addressList.get(0);
                LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());
                setLatitude(latLng.latitude);setLongitude(latLng.longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String geoLocationAddress(double latitude,double longitude){
        Geocoder geocoder=new Geocoder(mContext);
        String address_str=null;
        try {
            List<Address> addressList =new ArrayList<>();
            addressList=geocoder.getFromLocation(latitude,longitude,1);
            if(addressList.size()>0){
                Address address = addressList.get(0);
                LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());
                setLatitude(latLng.latitude);setLongitude(latLng.longitude);
                address_str=address.getAddressLine(0);
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address_str;
    }

    public String geoLocationAddress(String search_text){
        Geocoder geocoder=new Geocoder(mContext);
        String address_str=null;
        try {
            List<Address> addressList =new ArrayList<>();
            addressList=geocoder.getFromLocationName(search_text,1);
            if(addressList.size()>0){
                Address address = addressList.get(0);
                LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());
                setLatitude(latLng.latitude);setLongitude(latLng.longitude);
                address_str=address.getAddressLine(0);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address_str;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
