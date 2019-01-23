package com.example.bahaa.trackme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ImageWriter;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

public class HomeMap extends FragmentActivity implements OnMapReadyCallback ,GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    private LocationIdentifier locationIdentifier;
    boolean mLocationPermissionGranted;
    GoogleApiClient mGoogleApiClient;
    PlaceAutocompleteAdapter mPlacesAdapter;
    AutoCompleteTextView mAutocompletePlace;
    String search_string;
    ImageView mImageLocation;
    LatLng latLng;
    LatLngBounds mLatLngBounds;

    @SuppressLint("MissingPermission")
    @Override
    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_map);
        setTitle("حدد موقع منزلك");
        mAutocompletePlace = findViewById(R.id.autocomplete_text_places);

        /*get user's current location*/
        locationIdentifier = new LocationIdentifier(this);
        mLocationPermissionGranted = locationIdentifier.getLocationPermission();
        if (mLocationPermissionGranted) {
            //Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        /*set current location icon event*/
        mImageLocation=findViewById(R.id.image_View_gps);
        mImageLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationIdentifier.getDeviceLocation(mMap);
            }
        });
    }

    private  void searchForPlace(){
        /*search for location*/
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mLatLngBounds=new LatLngBounds(new LatLng(0,-180), new LatLng(360,180));
        mAutocompletePlace.setOnItemClickListener(mAutocompleteItemListener);
        /*set adapter*/
        mPlacesAdapter=new PlaceAutocompleteAdapter(this,mGoogleApiClient,mLatLngBounds,null);
        mAutocompletePlace.setAdapter(mPlacesAdapter);

        mAutocompletePlace.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    //execute method for searching:
                    search_string = mAutocompletePlace.getText().toString();
                    locationIdentifier.geoLocationAddress(search_string);
                    //latLng=new LatLng(locationIdentifier.getLatitude(), locationIdentifier.getLongitude());
                    hideSoftKeyBoard();
                }
                return false;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (mLocationPermissionGranted) {
            locationIdentifier.getDeviceLocation(mMap);
//           ///*******************
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
            mMap.setMyLocationEnabled(false);
            //
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    mMap.clear();
                    // Add a marker in location and move the camera
                    locationIdentifier.addMarkerinDeviceLocation(latLng);
                }
            });
            /*auto complete place search:*/
            searchForPlace();
        }
    }

    private void hideSoftKeyBoard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /*Google places Api Autocomplete*/
    private AdapterView.OnItemClickListener mAutocompleteItemListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item=mPlacesAdapter.getItem(position);
            final String placeId=item.getPlaceId();
            PendingResult<PlaceBuffer> placeResults=Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient,placeId);
            placeResults.setResultCallback(mUpdatePlaceBuffer);
            hideSoftKeyBoard();
            mAutocompletePlace.setText("");
        }
    };

    Double placeLat,placeLong;LatLng placeLatLng;
    private ResultCallback<PlaceBuffer> mUpdatePlaceBuffer=new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(places.getStatus().isSuccess()){
                placeLatLng= places.get(0).getLatLng();
                placeLat=placeLatLng.latitude;placeLong=placeLatLng.longitude;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng,15));
                locationIdentifier.addMarkerinDeviceLocation(placeLatLng);
                places.release();
            }
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

   double[] userLatLng=new double[2];
    public void buttonNext3_Click(View view) {
        try{
            userLatLng[0]=locationIdentifier.getLatitude();
            userLatLng[1]=locationIdentifier.getLongitude();

            Intent intent=new Intent(this,FirebaseJsonActivity.class);
            intent.putExtras(getIntent());
            intent.putExtra("userLatLng",userLatLng);
            //start the main activity:
            startActivity(intent);
        }
        catch (Exception ex){
            Toast.makeText(this,ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}

