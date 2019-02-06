package com.example.bahaa.trackme;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseJsonActivity extends AppCompatActivity {

    FirebaseFirestore mFireStore;
    private StorageReference mStorageRef;
    ProgressBar progressBar;Button button;TextView text_view;
    boolean isCreated;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_json);

        progressBar=findViewById(R.id.progressBar);
        progressBar.setMinimumWidth(findViewById(R.id.firebase_container).getWidth()/5);
        progressBar.setMinimumHeight(findViewById(R.id.firebase_container).getHeight()/5);
        button=findViewById(R.id.button_next4);
        text_view=findViewById(R.id.textView);

        mStorageRef = FirebaseStorage.getInstance().getReference("userImage");
        mFireStore = FirebaseFirestore.getInstance();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        if(isCreated==false) {
            try {
                saveUserFirePhoto();
                //
                isCreated = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    String name,date,time,photoUrl,photo_name,photo_path; Uri mImageUri;
    boolean photo_isSaved;

    private void saveUserFirePhoto(){
        mImageUri=(Uri)getIntent().getExtras().get("photoUri");
        name = getIntent().getStringExtra("name");

        //upload user photo to firebase and retrieve the url:
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
        date = sdf.format(new Date());
        SimpleDateFormat sdf_time = new SimpleDateFormat("HH_MM");
        time=sdf_time.format(new Date().getTime());
        photo_name= new StringBuilder().append(name).append(date+"_"+time).append(".png").toString();

        StorageReference photoRef=mStorageRef.child(photo_name);
        photoRef.putFile(mImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FirebaseJsonActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Toast.makeText(FirebaseJsonActivity.this,"User's photo is saved",Toast.LENGTH_SHORT).show();
                        //
                        photoUrl=task.getResult().getStorage().getDownloadUrl().getResult().toString();
//                              Toast.makeText(FirebaseJsonActivity.this,photoUrl,Toast.LENGTH_SHORT).show();
                        photo_isSaved=true;
                        //save user's data to firestore:
                        saveUserFireStore();
                    }
                });

    }

    String[] following;String Id,email,phone,pass,reg_date;
    ArrayList<String> users_Ids=new ArrayList<>();
    double[] userLatLng=new double[2];
    ArrayList<Double> userLatLngFire=new ArrayList<>();
    boolean user_isSaved;
    DocumentReference documentReference;
    private void saveUserFireStore(){
        if(photo_isSaved) {
            name=getIntent().getStringExtra("name");
            email = getIntent().getStringExtra("email");
            pass = getIntent().getStringExtra("pass");
            phone = getIntent().getStringExtra("phone");
            users_Ids = getIntent().getStringArrayListExtra("following_Ids");
            reg_date = new Date().toString();
//        userLatLng=getIntent().getDoubleArrayExtra("userLatLng");
//        userLatLngFire.add(userLatLng[0]);userLatLngFire.add(userLatLng[1]);
            //save user to firestore database:
            //if(!saveUserFirePhoto().isEmpty()) {
            Map<String, Object> user = new HashMap<>();
            user.put("name", name);
            user.put("email", email);
            user.put("pass", pass);
            user.put("phoneNum", phone);
            user.put("imageName", photo_name);
            user.put("imageUrl", photoUrl);
            user.put("following", users_Ids);
//        user.put("homeCoor", userLatLngFire);
            user.put("regDate", reg_date);
            // Add a new document with a generated ID:
            mFireStore.collection("users")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Error adding document", e.getMessage(), e);
                            button.setEnabled(false);
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            task.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    Id = task.getResult().getId();
                                    Toast.makeText(FirebaseJsonActivity.this, "User's data is saved", Toast.LENGTH_SHORT).show();
                                    ////
                                    user_isSaved = true;
                                    ////
                                    saveJsonUserData();
                                    /////
                                    saveJsonFollowingData();
                                }
                            });

                        }
                    });
        }
        else
        {
            Toast.makeText(this, "photo is not saved correctly! back to regerstration screen", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveJsonUserData() {

        //create user json file:
        if(photo_isSaved && user_isSaved) {
            JsonObject jsonUser = new JsonObject();
            jsonUser.addProperty("Id", Id);
            jsonUser.addProperty("name", name);
            jsonUser.addProperty("pass", pass);
            jsonUser.addProperty("phone", phone);
            jsonUser.addProperty("email", email);

            //create user's photo in app directory and get its path:
            InputStream in = null;
            try {
                in = getContentResolver().openInputStream(mImageUri);
                OutputStream out = null;
                out = new FileOutputStream(new File(getFilesDir().getPath()+"/"+ photo_name));
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
                photo_path = getFilesDir().getPath() + "/" + photo_name;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            jsonUser.addProperty("photo_path", photo_path);
            jsonUser.addProperty("regDate", reg_date);

//            JsonArray jsonCoorArr = new JsonArray();
//            for (int i = 0; i < userLatLng.length; i++) {
//                jsonCoorArr.add(userLatLng[i]);
//            }
//            jsonUser.add("homeCoor", jsonCoorArr);

            JsonArray jsonFollowing = new JsonArray();
            for (int i = 0; i < users_Ids.size(); i++) {
                jsonFollowing.add(users_Ids.get(i));
            }
            jsonUser.add("following", jsonFollowing);

            //save json file to internal storage:
            create_json_file("user.json",jsonUser,"Saved to user.json");
        }
    }

    String following_Id,following_Name,followingEmail,following_Phone,following_PhotoUrl,get_following_PhotoPath;
    String following_photo;
    ArrayList<String> following_Ids_arr=new ArrayList<>();
    ArrayList<String> following_names_arr=new ArrayList<>();
    ArrayList<String> following_phones_arr=new ArrayList<>();
    ArrayList<String> following_photos_arr=new ArrayList<>();
    AsyncTask.Status status;
    private void saveJsonFollowingData(){
        try{
            //get the following data from the previous data before assigning JSON object to it:
            following_Ids_arr=getIntent().getStringArrayListExtra("following_Ids");
            following_names_arr=getIntent().getStringArrayListExtra("following_names");
            following_phones_arr=getIntent().getStringArrayListExtra("following_phones");
            following_photos_arr=getIntent().getStringArrayListExtra("following_photos");

            //assigning the JsonObject to then followings' data:
            JsonObject jsonFollowings=new JsonObject();

            for(int i=0;i<following_Ids_arr.size();i++){
                following_Id=following_Ids_arr.get(i);
                following_Name=following_names_arr.get(i);
                following_Phone=following_phones_arr.get(i);
                following_photo=following_photos_arr.get(i);
//                following_PhotoUrl=following_photos_arr.get(i);

                JsonObject jsonFollowing=new JsonObject();
                //Add following name to "followings.json":
                jsonFollowing.addProperty("following_name",following_Name);
                //Add following phone to followings.json":
                jsonFollowing.addProperty("following_phone",following_Phone);
                //Add following photo to "followings.json":
                download_following_photo(following_photo);
                get_following_PhotoPath=getFilesDir().getPath()+"/"+following_photo;
                jsonFollowing.addProperty("following_Photo", get_following_PhotoPath);
//                status =new SaveFollowingPhoto(this,following_photo,following_Name).execute().getStatus();
//                if(status== AsyncTask.Status.FINISHED) {
//                    jsonFollowing.addProperty("following_Photo", get_Following_PhotoPath);
//                    jsonFollowings.add(following_Id, jsonFollowing);
//                }
                jsonFollowings.add(following_Id, jsonFollowing);
            }

            //save json file to internal storage:
            create_json_file("followings.json",jsonFollowings,"Saved to followings.json");

        }
        catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void create_json_file(String jsonName, JsonObject jsonObject,String message){
        //save json file to internal storage:
        FileOutputStream outFileStream;
        OutputStreamWriter outStream;
        BufferedWriter writer = null;
        try {
            File file = new File(getFilesDir(), jsonName);
            if (!file.exists()) {
                file.createNewFile();
            }
            outFileStream = new FileOutputStream(file);
            outStream = new OutputStreamWriter(outFileStream);
            writer = new BufferedWriter(outStream);
            String jsonString = jsonObject.toString();//jsonUser is json object which has some data
            writer.write(jsonString);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                    if(jsonName=="followings.json"){
                        progressBar.setVisibility(View.INVISIBLE);
                        button.setVisibility(View.VISIBLE);
                        text_view.setVisibility(View.INVISIBLE);
                    }
                } catch (IOException e) {

                }
            }
        }
    }

    StorageReference storage_ref;
    static  final long ONE_MEGA_BYTES=1024*1024;
    private void download_following_photo(final String photo_name){
        storage_ref=FirebaseStorage.getInstance().getReference("userImage");
        StorageReference photo_ref=storage_ref.child(photo_name);
        photo_ref.getBytes(ONE_MEGA_BYTES)
            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    FileOutputStream foStream;
                    try {
                        foStream=openFileOutput(photo_name,MODE_PRIVATE);
                        foStream.write(bytes);
                        Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        bitmap.compress(Bitmap.CompressFormat.PNG,80,foStream);
                        foStream.flush();
                        foStream.close();
                        button.setVisibility(View.INVISIBLE);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            })
            .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    button.setVisibility(View.VISIBLE);
                }
            });
    }

    public void button_next4_click(View view) {
        try{


        }
        catch(Exception ex){
                Log.e("firebase error",ex.getMessage());
        }

    }
}
