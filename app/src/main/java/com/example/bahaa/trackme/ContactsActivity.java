package com.example.bahaa.trackme;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    List<ContactModel> deviceContacts=new ArrayList<>();
    FirebaseFirestore db;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        progressBar=findViewById(R.id.progressContacts);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        contactsFromFirebase();
    }

    String conID,conName,conHome,conMobile,conWork;
    boolean isExisted;int phoneType;
    void showContacts(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            }
            else
             {
                ContentResolver resolver=getContentResolver();
                Cursor contactCursor=resolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
                while(contactCursor.moveToNext()){
                    conID=contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));
                    conName=contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    Cursor phoneCursor=resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "+conID,null, null);
                    int ii=phoneCursor.getCount();
                    while (phoneCursor.moveToNext()){
                        conHome=phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    //                    phoneType = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
    //                    conHome="";conMobile="";conWork="";
    //                    switch (phoneType){
    //                        case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
    //                            conHome=phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    //                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
    //                            conMobile=phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    //                        case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
    //                            conWork=phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    //                    }
                    }
                    phoneCursor.close();
                    ContactModel contact=new ContactModel();
                    contact.setmConId(conID); contact.setConName(conName);
                    contact.setConNumHome(conHome);
    //                contact.setmConNumMobile(conMobile);contact.setmConNumWork(conWork);
                    deviceContacts.add(contact);
                    Log.i("contact",contact.getConName()+" "+contact.getConNumHome());
                }
               contactCursor.close();
            }
        } catch (Exception e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }

    List<ContactModel> appContacts=new ArrayList<>();
    List<QueryDocumentSnapshot> fireUsers;
    String conNum,homeId,mobileId,workId,fireId,fireNum;Uri imageUrl;
    ArrayList<Bitmap> followings_Photos=new ArrayList<>();
    private List<QueryDocumentSnapshot> contactsFromFirebase(){
        try {
            fireUsers = new ArrayList<>();
            db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        fireUsers.add(document);
                    }
                    if(fireUsers.size()>0){
                        showContacts();//get phone's contacts
                        //compare device's contacts and firestore users:
                        for(int i=0;i<fireUsers.size();i++){
                            fireId=fireUsers.get(i).getId();
                            fireNum=fireUsers.get(i).getData().get("phoneNum").toString();
                            imageUrl=Uri.parse(fireUsers.get(i).getData().get("imageUrl").toString());
                            Log.i("fire", fireUsers.get(i).getData().get("name").toString());
                            Log.i("fireNum", fireUsers.get(i).getData().get("phoneNum").toString());
                            for(int n=0;n<deviceContacts.size();n++){
                                conNum=deviceContacts.get(n).getConNumHome();
//                                mobileId=deviceContacts.get(n).getmConNumMobile();
//                                workId=deviceContacts.get(n).getmConNumWork();
                                if(conNum.equals(fireNum)){
                                    //add user's Id and photo url to the list which will be displayed:
                                    deviceContacts.get(n).setmConId(fireId);
                                    deviceContacts.get(n).setConImage(imageUrl);
                                    appContacts.add(deviceContacts.get(n));

                                    break;
                                }
                            }
                        }
                        //display the contacts whose using the app:
                        RecyclerView recyclerView=findViewById(R.id.contacts_recycler);
                        recyclerView.setLayoutManager(new LinearLayoutManager(ContactsActivity.this));
                        recyclerView.setHasFixedSize(true);
                        ContactAdapter conAdapter=new ContactAdapter(appContacts,getApplicationContext());
                        recyclerView.setAdapter(conAdapter);
                        followings_Photos=conAdapter.followings_photos;
                        recyclerView.addItemDecoration(new DividerItemDecoration(ContactsActivity.this,1));

                        progressBar.setVisibility(View.INVISIBLE);
                    }

                }
            });
        } catch (Exception e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        finally {

        }
        return fireUsers;
    }
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }
    ArrayList<String> following_Ids=new ArrayList<>();
    ArrayList<String> following_names=new ArrayList<>();
    ArrayList<String> following_phones=new ArrayList<>();
    ArrayList<String> following_photos=new ArrayList<>();
    public void buttonNext2_Click(View view) {
        try {
            Intent intent = new Intent(this,HomeMap.class);
            intent.putExtras(getIntent());
            for (int i=0;i<appContacts.size();i++) {
                following_Ids.add(appContacts.get(i).getmConId());
                following_names.add(appContacts.get(i).getConName());
                following_phones.add(appContacts.get(i).getConNumHome());
                following_photos.add(appContacts.get(i).getConImage().toString());
            }

            intent.putStringArrayListExtra("following_Ids",following_Ids);
            intent.putStringArrayListExtra("following_names",following_names);
            intent.putStringArrayListExtra("following_phones",following_phones);
            intent.putExtra("followings_Photos",followings_Photos);

            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

}
