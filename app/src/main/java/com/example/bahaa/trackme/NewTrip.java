package com.example.bahaa.trackme;

import android.Manifest;
import android.app.admin.DeviceAdminInfo;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class NewTrip extends AppCompatActivity implements LocationListener {
    LocationIdentifier locationIdentifier;
    boolean location_permission_granted;
    Double latitude, longitude;
    TextView text_view_from_location, text_view_destination;
    Spinner spinner_location;
    Switch switch_undetermined,switch_notify_all;
    String location_address, destination;
    UserDestinationAdapter user_dest_adapter;
    double[] userLatLng = new double[2];
    final int PERMISSIONS_REQUEST = 1;
    FusedLocationProviderClient mFusedLocationClient;
    LocationManager locationManager;
    String location_provider="network";
    RecyclerView rec_view_trip_contacts;
    CheckBox contact_checkBox;
    ArrayList<ContactModel> contacts_arr=new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);
        text_view_from_location = findViewById(R.id.text_view_from_location);
        text_view_destination = findViewById(R.id.text_view_destination);
        spinner_location = findViewById(R.id.spinner_location);
        switch_undetermined=findViewById(R.id.switch_undetermined);
        rec_view_trip_contacts=findViewById(R.id.rec_view_trip_contacts);
        contact_checkBox=findViewById(R.id.contactCheckBox);
        switch_notify_all=findViewById(R.id.switch_notify_all);
        final RecyclerView contacts_recycler;
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

        if (getIntent() != null) {
            destination = getIntent().getStringExtra("destination");
            text_view_destination.setText(destination);
            text_view_destination.setVisibility(View.VISIBLE);
        }
        try {
            //fill spinner:
            getDestinationsList();
            //
            if(user_dest_arr.size()>0) {
                spinner_location = findViewById(R.id.spinner_location);
                user_dest_adapter = new UserDestinationAdapter(this, user_dest_arr);
                spinner_location.setAdapter(user_dest_adapter);
                int initialSelection=spinner_location.getSelectedItemPosition();
                spinner_location.setSelection(initialSelection,false );
                spinner_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        UserDestination userDestination = (UserDestination) parent.getSelectedItem();
                        title = userDestination.getTitle();
                        text_view_destination.setText(title);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
            //contacts recycler view:
            StringBuilder builder1=new StringBuilder();
            FileInputStream fis=new FileInputStream(getFilesDir().getPath()+"/followings.json");
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(fis));
            ArrayList<String> ids=new ArrayList<>();
            while(bufferedReader.read()!=-1){
                builder1.append(bufferedReader.readLine());
            }
            bufferedReader.close();
            JSONObject jsonObject=new JSONObject('{'+builder1.toString()+'}');
            Iterator<String> itr_string=jsonObject.keys();
            while(itr_string.hasNext()) {
                String ss=itr_string.next();
                ids.add(ss);
            };

            for(int i=0;i<ids.size();i++)
            {
                //JsonObject contact_json=new JsonObject();
                ContactModel contact=new ContactModel();
                contact.setmConId(ids.get(i));
                contact.setConName(jsonObject.getJSONObject(ids.get(i)).getString("following_name"));
                contact.setConImage(Uri.parse(jsonObject.getJSONObject(ids.get(i)).getString("following_Photo")));
                contacts_arr.add(contact);
            }

            contacts_recycler=findViewById(R.id.rec_view_trip_contacts);
            contacts_recycler.setLayoutManager(new LinearLayoutManager(this));
            contacts_recycler.setHasFixedSize(true);
            final ContactTripAdapter contacts_adapter=new ContactTripAdapter(this,contacts_arr);
            contacts_recycler.setAdapter(contacts_adapter);

            //switchs:
            switch_notify_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        contacts_adapter.enableOrDisableCheckboxs(true);
                    }
                    else{
                        contacts_adapter.enableOrDisableCheckboxs(false);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //NotificationService notificationService=new NotificationService();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            text_view_from_location.setBackgroundColor(getColor(R.color.colorAccent));
            Toast.makeText(this,"is stopped",Toast.LENGTH_SHORT).show();
        }
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

    InputStream is;
    StringBuilder builder=new StringBuilder();
    Gson gson=new Gson();
    ArrayList<UserDestination> user_dest_arr=new ArrayList<>();
    ArrayList<JSONArray> json_arr=new ArrayList<>();
    String address,title;
    public void getDestinationsList() throws IOException, JSONException {

        is=getResources().openRawResource(R.raw.destinations_list);
        BufferedReader reader=new BufferedReader(new InputStreamReader(is));
        while(reader.read()!=-1){
            builder.append(reader.readLine());
       }

        int dest_count=0;
        JSONObject jsonObject=new JSONObject('{'+builder.toString()+'}');
        for(int i=jsonObject.length();i>0;i--) {
            JSONArray json_arr_dest=new JSONArray();
            json_arr_dest=jsonObject.getJSONArray(String.valueOf(i));
            json_arr.add(json_arr_dest);
            dest_count++;
            if(dest_count==5){
                break;
            }
        }
        ///
        for(int i=0;i<json_arr.size();i++){
            latitude=json_arr.get(i).getDouble(0);
            longitude=json_arr.get(i).getDouble(1);
            address=json_arr.get(i).getString(2);
            title=json_arr.get(i).getString(3);
            UserDestination user_dest=new UserDestination(latitude,longitude,address,title);
            user_dest_arr.add(user_dest);
        }
       //Toast.makeText(this,title,Toast.LENGTH_LONG).show();
    }

    public void on_button_start_trip_click(View view) {
        Intent locationServiceIntent=new Intent(this,LocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this,locationServiceIntent);
            Toast.makeText(this,"service is started",Toast.LENGTH_SHORT).show();
        }
        else {
            startService(locationServiceIntent);
            Toast.makeText(this,"service is started",Toast.LENGTH_SHORT).show();
        }
    }

}
