package com.example.bahaa.trackme;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHelper> {

    List<ContactModel> contacts=new ArrayList<ContactModel>();

    public ContactAdapter(List<ContactModel> contactModelList){
        contacts=contactModelList;
    }

    @NonNull
    @Override
    public ContactHelper onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_contacts_rec_view,viewGroup,false);
        return new ContactHelper(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHelper contactHelper, int position) {
        ContactModel contact=contacts.get(position);
        Picasso.get().load(contact.getConImage()).into(contactHelper.conImage);
        //contactHelper.conImage.setImageURI(contact.getConImage());
        contactHelper.conName.setText(contact.getConName());
        contactHelper.conNum.setText(contact.getmConNumWork());
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }



    class ContactHelper extends RecyclerView.ViewHolder{

        ImageView conImage;TextView conName, conNum;
        //CheckBox conCheckBox;
        public ContactHelper(@NonNull View itemView) {
            super(itemView);
            conImage=itemView.findViewById(R.id.contactImage);
            conName=itemView.findViewById(R.id.contactName);
            conNum=itemView.findViewById(R.id.contactNum);
            //conCheckBox=itemView.findViewById(R.id.contactCheckBox);
        }
    }
}
