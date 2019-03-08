package com.example.bahaa.trackme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class NewTrip extends AppCompatActivity implements LocationListener {
    LocationIdentifier locationIdentifier;
    boolean location_permission_granted;
    Double latitude, longitude;
    TextView text_view_from_location, text_view_destination;
    Spinner spinner_location;
    Switch switch_undetermined;
    String location_address, destination;
    double[] userLatLng = new double[2];
    final int PERMISSIONS_REQUEST = 1;
    FusedLocationProviderClient mFusedLocationClient;
    LocationManager locationManager;
    String location_provider="network";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);
        text_view_from_location = findViewById(R.id.text_view_from_location);
        text_view_destination = findViewById(R.id.text_view_destination);
        spinner_location = findViewById(R.id.spinner_location);
        switch_undetermined=findViewById(R.id.switch_undetermined);
        //
        locationIdentifier=new LocationIdentifier(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
//               public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET}, PERMISSIONS_REQUEST);
            }
            return;
        }
        locationManager.requestLocationUpdates(location_provider, 1000, 1, this);
//        else {
//            if(location_permission_granted){
//
//            }
//        }
        //
        if (getIntent() != null) {
            destination = getIntent().getStringExtra("destination");
            text_view_destination.setText(destination);
        }
        try {
            getDestinationsList();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //switchs:

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1:
                Toast.makeText(this, "permissions are granted", Toast.LENGTH_SHORT).show();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(location_provider, 1000, 1, this);
       }
    }

    double[] lat_long=new double[2];
    public void on_text_view_google_click(View view){
        if(latitude !=null || longitude!=null) {
            Intent intent = new Intent(this, DestinationMap.class);
            lat_long[0]=latitude;lat_long[1]=longitude;
            intent.putExtra("lat_long",lat_long);
            startActivity(intent);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        longitude=location.getLongitude();
        latitude=location.getLatitude();
        location_address= locationIdentifier.geoLocationAddress(latitude,longitude);
        text_view_from_location.setText(location_address);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            location_provider="network";
        }
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            location_provider="gps";
        }
        Toast.makeText(this,location_provider+" is enabled",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            location_provider="gps";
        }
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            location_provider="network";
        }
        Toast.makeText(this,location_provider+" is disabled",Toast.LENGTH_SHORT).show();
    }



    public void on_button_start_trip_click(View view) {

    }

    InputStream is;
    StringBuilder builder=new StringBuilder();
    Gson gson=new Gson();
    ArrayList<UserDestination> user_dest_arr=new ArrayList<>();
    ArrayList<JSONArray> json_arr=new ArrayList<>();
    UserDestinationAdapter user_dest_adapter;
    String address,title;
    public void getDestinationsList() throws IOException, JSONException {

        is=getResources().openRawResource(R.raw.destinations_list);
        BufferedReader reader=new BufferedReader(new InputStreamReader(is));
        while(reader.read()!=-1){
            builder.append(reader.readLine());
       }
       //JsonReader json_reader=new JsonReader(reader);
//        JSONArray jsonArray=new JSONArray('{'+builder.toString()+'}');
//        for (int i=0;i<jsonArray.length();i++){
//            JSONObject json_dest=new JSONObject();
//            json_dest=(JSONObject)jsonArray.get(i);
//            json_oject_arr.add(json_dest);
//        }
        JSONObject jsonObject=new JSONObject('{'+builder.toString()+'}');
        JSONArray json_arr_dest_1=new JSONArray();
        json_arr_dest_1=jsonObject.getJSONArray("dest_1");
        JSONArray json_arr_dest_2=new JSONArray();
        json_arr_dest_2=jsonObject.getJSONArray("dest_2");
        JSONArray json_arr_dest_3=new JSONArray();
        json_arr_dest_3=jsonObject.getJSONArray("dest_3");
        json_arr.add(json_arr_dest_1);
        json_arr.add(json_arr_dest_2);
        json_arr.add(json_arr_dest_3);
//        JSONObject json_dest2=new JSONObject();
//        json_dest2=jsonObject.getJSONObject("dest2");
//        json_oject_arr.add(json_dest2);
//        JSONObject json_dest3=new JSONObject();
//        json_dest3=jsonObject.getJSONObject("dest3");
//        json_oject_arr.add(json_dest3);
        ///
        for(int i=0;i<json_arr.size();i++){
            latitude=json_arr.get(i).getDouble(0);
            longitude=json_arr.get(i).getDouble(1);
            address=json_arr.get(i).getString(2);
            title=json_arr.get(i).getString(3);
            UserDestination user_dest=new UserDestination(latitude,longitude,address,title);
            user_dest_arr.add(user_dest);
        }
//
       spinner_location=findViewById(R.id.spinner_location);
        user_dest_adapter=new UserDestinationAdapter(this,user_dest_arr);
        spinner_location.setAdapter(user_dest_adapter);
        spinner_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                UserDestination userDestination=(UserDestination)parent.getSelectedItem();
                title=userDestination.getTitle();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Toast.makeText(this,title,Toast.LENGTH_LONG).show();
    }

//        try {
//            FirebaseMessaging.getInstance().subscribeToTopic("address")
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.i("message","message is sent!");
//
//                        }
//                    })
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//
//                        }
//                    });
//            FirebaseInstanceId.getInstance().getInstanceId()
//                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                            if (!task.isSuccessful()) {
//                                Log.w("instance id", "getInstanceId failed", task.getException());
//                                return;
//                            }
//
//                            // Get new Instance ID token
//                            String token = task.getResult().getToken();
//                            Log.w("instance id", token);
//
//                            // Log and toast
////                            String msg = getString(R.string.msg_token_fmt, token);
////                            Log.d(TAG, msg);
////                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
}
