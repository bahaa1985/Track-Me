package com.example.bahaa.trackme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseJsonActivity extends AppCompatActivity {

    FirebaseFirestore mFireStore;
    private StorageReference mStorageRef;
    ProgressBar progressBar;Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_json);

        progressBar=findViewById(R.id.progressBar);
        button=findViewById(R.id.button_next4);

        mStorageRef = FirebaseStorage.getInstance().getReference("userImage");
        mFireStore = FirebaseFirestore.getInstance();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });


        try {
            saveUserFirePhoto();
            //
//            saveJsonUserData();
//            //
//            saveFollowingData();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //saveFollowingData();

        //saveJsonUserActivities();

        //saveJsonFollowingActivities();
    }

    String name,photoUrl,photo_name; Uri mImageUri;
    boolean photo_isSaved;
    private void saveUserFirePhoto(){

        name=getIntent().getStringExtra("email");
        mImageUri=(Uri)getIntent().getExtras().get("photoUri");

//      upload user photo to firebase and retrieve the url:
        photo_name=name+new Date().toString()+".jpg";
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
                        task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                photoUrl=task.getResult().toString();
                                Toast.makeText(FirebaseJsonActivity.this,photoUrl,Toast.LENGTH_SHORT).show();
                                photo_isSaved=true;
                                //save user's data to firestore:
                                saveUserFireStore();
                            }
                        });

                    }
                });

    }

    String[] following;String Id,email,phone,pass;
    ArrayList<String> users_Ids=new ArrayList<>();
    double[] userLatLng=new double[2];
    ArrayList<Double> userLatLngFire=new ArrayList<>();
    boolean user_isSaved;
    DocumentReference documentReference;
    private void saveUserFireStore(){
        email=getIntent().getStringExtra("email");
        pass=getIntent().getStringExtra("pass");
        phone=getIntent().getStringExtra("phone");
        users_Ids=getIntent().getStringArrayListExtra("followingIds");
        userLatLng=getIntent().getDoubleArrayExtra("userLatLng");
        userLatLngFire.add(userLatLng[0]);userLatLngFire.add(userLatLng[1]);

        //save user to firestore database:
        //if(!saveUserFirePhoto().isEmpty()) {
        Map<String,Object> user=new HashMap<>();
        user.put("name",name);
        user.put("email",email);
        user.put("pass",pass);
        user.put("phoneNum",phone);
        user.put("imageUrl", photoUrl);
        user.put("following", users_Ids);
        user.put("homeCoor", userLatLngFire);
        user.put("regDate", new Date().toString());
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
                        Log.e("user error", "Error adding document", e);
                        button.setEnabled(false);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                        task.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                Id=task.getResult().getId();
                                Toast.makeText(FirebaseJsonActivity.this,"User's data is saved",Toast.LENGTH_SHORT).show();
                                user_isSaved=true;
                                saveJsonUserData();
                                saveJsonFollowingData();
//                                progressBar.setVisibility(View.INVISIBLE);
                                button.setEnabled(true);



                            }
                        });

                    }
                });
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
            jsonUser.addProperty("photo", mImageUri.getPath());
            jsonUser.addProperty("regDate", new Date().toString());

            JsonArray jsonCoorArr = new JsonArray();
            for (int i = 0; i < userLatLng.length; i++) {
                jsonCoorArr.add(userLatLng[i]);
            }
            jsonUser.add("homeCoor", jsonCoorArr);

            JsonArray jsonFollowing = new JsonArray();
            for (int i = 0; i < users_Ids.size(); i++) {
                jsonFollowing.add(users_Ids.get(i));
            }
            jsonUser.add("following", jsonFollowing);

            //save json file to internal storage:
            FileOutputStream outFileStream;
            OutputStreamWriter outStream;
            BufferedWriter writer = null;
            try {
                File file = new File(getFilesDir(), "user.json");
                if (!file.exists()) {
                    file.createNewFile();
                }
                outFileStream = new FileOutputStream(file);
                outStream = new OutputStreamWriter(outFileStream);
                writer = new BufferedWriter(outStream);
                String jsonString = jsonUser.toString();//jsonUser is json object which has some data
                writer.write(jsonString);
                Toast.makeText(this, "Saved to user.json", Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {

                    }
                }
            }
        }
    }

    String followingId,followingName,followingEmail,followingPhone,followingPhotoUrl,getFollowingPhotoPath;
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
                followingId=following_Ids_arr.get(i);
                followingName=following_names_arr.get(i);
                followingPhone=following_phones_arr.get(i);
                followingPhotoUrl=following_photos_arr.get(i);

                JsonObject jsonFollowing=new JsonObject();

                jsonFollowing.addProperty("following_name",followingName);
                jsonFollowing.addProperty("following_phone",followingPhone);


                status =new DownloadImage().execute(followingPhotoUrl).getStatus();
                if(status== AsyncTask.Status.FINISHED) {
                    getFollowingPhotoPath = getFilesDir().getAbsolutePath() + "/" + followingName + new Date().toString() + ".jpg";
                    jsonFollowing.addProperty("following_Photo", getFollowingPhotoPath);

                    jsonFollowings.add(followingId, jsonFollowing);
                }
            }
            //save json file to internal storage:
            FileOutputStream outFileStream;
            OutputStreamWriter outStream;
            BufferedWriter writer=null;
            try {
                File file=new File(getFilesDir(),"followings.json");
                if(!file.exists()){
                    file.createNewFile();
                }
                outFileStream=new FileOutputStream(file);
                outStream=new OutputStreamWriter(outFileStream);
                writer=new BufferedWriter(outStream);
                String jsonString=jsonFollowings.toString();//jsonFollowings is json object which has some data
                writer.write(jsonString);
                Toast.makeText(this, "Saved to followings.json", Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(),Toast.LENGTH_LONG).show();
            }
            finally {
                if(writer!=null){
                    try{
                        writer.close();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    catch(Exception e){
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

        }
        catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void saveImage(Context context, Bitmap b, String imageName) {
        FileOutputStream foStream;
        try {
            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, foStream);
            foStream.close();
        } catch (Exception e) {
            Log.d("saveImage", "Exception 2, Something went wrong!");
            e.printStackTrace();
        }
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        private String TAG = "DownloadImage";
        private Bitmap downloadImageBitmap(String sUrl) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(sUrl).openStream();   // Download Image from URL
                bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
                inputStream.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception 1, Something went wrong!");
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImageBitmap(params[0]);
        }

        protected void onPostExecute(Bitmap result) {
            saveImage(getApplicationContext(), result,
                    FirebaseJsonActivity.this.followingName+new Date().toString()+".png");
        }
    }

    public void button_next4_click(View view) {
        try{


        }
        catch(Exception ex){
                Log.e("firebase error",ex.getMessage());
        }

    }
}
