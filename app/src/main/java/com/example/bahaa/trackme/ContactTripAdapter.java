package com.example.bahaa.trackme;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ContactTripAdapter extends RecyclerView.Adapter<ContactTripAdapter.ContactHelper> {

    Context context;
    List<ContactModel> contacts=new ArrayList<ContactModel>();
    @NonNull
    @Override
    public ContactTripAdapter.ContactHelper onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view_holder=LayoutInflater.from(context).inflate(R.layout.activity_contacts_rec_view,viewGroup,false);
        return new ContactHelper(view_holder);
    }

    ContactModel contact;
    @Override
    public void onBindViewHolder(@NonNull final ContactTripAdapter.ContactHelper contactHelper, int position) {
        contact=contacts.get(position);

        contactHelper.conImage.setImageURI(contact.getConImage());

        contactHelper.conName.setText(contact.getConName());

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
        return 0;
    }

    class ContactHelper extends RecyclerView.ViewHolder{
        ImageView conImage;
        TextView conName;
        CheckBox conCheckBox;
        public ContactHelper(@NonNull View itemView) {
            super(itemView);
            conImage=itemView.findViewById(R.id.contactImage);
            conName=itemView.findViewById(R.id.contactName);
            conCheckBox=itemView.findViewById(R.id.contactCheckBox);

        }
    }
}
