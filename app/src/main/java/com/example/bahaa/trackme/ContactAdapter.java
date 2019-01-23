package com.example.bahaa.trackme;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHelper> {

    List<ContactModel> contacts=new ArrayList<ContactModel>();
    public ArrayList<Bitmap> followings_photos=new ArrayList<>();

    Context mContext;
    public ContactAdapter(List<ContactModel> contactModelList,Context context){
        contacts=contactModelList;
        mContext=context;
    }

    @NonNull
    @Override
    public ContactHelper onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_contacts_rec_view,viewGroup,false);
        return new ContactHelper(itemView);
    }

    ContactModel contact;
    @Override
    public void onBindViewHolder(@NonNull final ContactHelper contactHelper, int position) {
        contact=contacts.get(position);

        Picasso.get().load(contact.getConImage()).resize(250,250).into(contactHelper.conImage);

        /*contactHelper.conImage.setDrawingCacheEnabled(true);
        Bitmap bitmap =contactHelper.conImage.getDrawingCache();
        followings_photos.add(bitmap);*/

        contactHelper.conName.setText(contact.getConName());
        contactHelper.conNum.setText(contact.getmConNumWork());

        if(contact.isSelected()){
            contactHelper.conCheckBox.setChecked(true);
        }
        else{
            contactHelper.conCheckBox.setChecked(false);
        }
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
            conCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        contact.setIsSelected(true);
                        Toast.makeText(ContactAdapter.this.mContext,contact.getConName(),Toast.LENGTH_SHORT).show();
                    }
                    else{
                        contact.setIsSelected(false);
                    }
                }
            });
        }
    }
}
