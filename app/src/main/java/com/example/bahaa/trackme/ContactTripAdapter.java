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
    public ContactTripAdapter(Context context,List<ContactModel> contacts){
        this.context=context;
        this.contacts=contacts;
    }
    @NonNull
    @Override
    public ContactTripAdapter.ContactHelper onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view_holder=LayoutInflater.from(context).inflate(R.layout.activity_contacts_rec_view,viewGroup,false);
        return new ContactHelper(view_holder);
    }


    public void enableOrDisableCheckboxs(boolean status){
        if(status==true){
            for(int i=0;i<checkBoxList.size();i++){
                checkBoxList.get(i).setChecked(true);
            }
        }
        else{
            for(int i=0;i<checkBoxList.size();i++){
                checkBoxList.get(i).setChecked(false);
            }
        }
    }

    ContactModel contact;
    List<CheckBox> checkBoxList=new ArrayList<>();
    @Override
    public void onBindViewHolder(@NonNull final ContactTripAdapter.ContactHelper contactHelper, final int position) {
        contact=contacts.get(position);

        contactHelper.conImage.setImageURI(contact.getConImage());

        contactHelper.conName.setText(contact.getConName());

        contactHelper.conCheckBox.setTag(position);

        contactHelper.conCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int pos=(int)contactHelper.conCheckBox.getTag();
                if(isChecked){
                    contacts.get(pos).setIsSelected(true);

                }
                else{
                    contacts.get(pos).setIsSelected(false);

                }
            }
        });
        checkBoxList.add(contactHelper.conCheckBox);
    }


    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class ContactHelper extends RecyclerView.ViewHolder{
        ImageView conImage;
        TextView conName;
        public CheckBox conCheckBox;
        public ContactHelper(@NonNull View itemView) {
            super(itemView);
            conImage=itemView.findViewById(R.id.contactImage);
            conName=itemView.findViewById(R.id.contactName);
            conCheckBox=itemView.findViewById(R.id.contactCheckBox);

        }

    }
}
