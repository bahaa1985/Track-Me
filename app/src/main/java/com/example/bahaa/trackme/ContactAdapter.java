package com.example.bahaa.trackme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHelper>{

    List<ContactModel> contacts=new ArrayList<ContactModel>();
    public ArrayList<byte[]> photos_bytes=new ArrayList<>();
    Context context;
    ProgressBar progressBar;
    StorageReference storageRef;
    public ContactAdapter(List<ContactModel> contacts,ProgressBar progressBar){
        this.contacts=contacts;
//        this.context=context;
        storageRef=FirebaseStorage.getInstance().getReference("userImage");
        this.progressBar=progressBar;
    }

    @NonNull
    @Override
    public ContactHelper onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_contacts_rec_view,viewGroup,false);
        return new ContactHelper(itemView);
    }

    ContactModel contact;

    @Override
    public void onBindViewHolder(@NonNull final ContactHelper contactHelper, final int position) {

        contact=contacts.get(position);

//        Picasso.get().load(contact.getConImage()).into(contactHelper.conImage);

        final long ONE_MEGABYTE = (1024 * 1024)*3;
        StorageReference photo_ref=storageRef.child(contact.getConImageName());
        photo_ref.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        progressBar.setVisibility(View.VISIBLE);
                        Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        contactHelper.conImage.setImageBitmap(bitmap);

                    }
                })
                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        if(task.isComplete()) {
                            photos_bytes.add(task.getResult());
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });


        contactHelper.conName.setText(contact.getConName());
        contactHelper.conNum.setText(contact.getmConNumWork());

        contactHelper.conCheckBox.setTag(position);
        contactHelper.conCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int pos=(int)contactHelper.conCheckBox.getTag();
//                Toast.makeText(ContactAdapter.class.get,contacts.get(pos).getConName(),Toast.LENGTH_SHORT).show();
                if(isChecked){
                    contacts.get(pos).setIsSelected(true);
                    contactHelper.conName.setTextColor(Color.GREEN);
                }
                else{
                    contacts.get(pos).setIsSelected(false);
                    contactHelper.conName.setTextColor(Color.BLACK);
                }
            }
        });


    }



    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class ContactHelper extends RecyclerView.ViewHolder{

        ImageView conImage;TextView conName, conNum;
        CheckBox conCheckBox;
        public ContactHelper(@NonNull View itemView) {
            super(itemView);
            conImage=itemView.findViewById(R.id.contactImage);
            conName=itemView.findViewById(R.id.contactName);
            conNum=itemView.findViewById(R.id.contactNum);
            conCheckBox=itemView.findViewById(R.id.contactCheckBox);
//            conCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if(isChecked){
//                        contact.setIsSelected(true);
//                        Toast.makeText(ContactAdapter.this.context,contact.getConName(),Toast.LENGTH_SHORT).show();
//                    }
//                    else{
//                        contact.setIsSelected(false);
//                    }
//                }
//            });
        }
    }


}
