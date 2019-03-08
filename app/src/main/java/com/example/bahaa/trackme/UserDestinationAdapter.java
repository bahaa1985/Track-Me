package com.example.bahaa.trackme;


import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class UserDestinationAdapter extends ArrayAdapter<UserDestination> {

    public UserDestinationAdapter(Context context, ArrayList<UserDestination> dest_List){
        super(context,0,dest_List);

    }

    @Nullable
    @Override
    public UserDestination getItem(int position) {
        return super.getItem(position);
    }

    @Nullable
    @Override
    public View getView(int position, @Nullable View convertView,   @Nullable ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    @Override
    public View getDropDownView(int position,  @Nullable View convertView,   @Nullable ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    private View initView(int position,View convert_view,ViewGroup parent){
        if(convert_view==null){
            convert_view=LayoutInflater.from(getContext()).inflate(R.layout.activity_user_dest_spinner_row,parent,false);
        }
        TextView textView_dest=convert_view.findViewById(R.id.text_view_destination);
        UserDestination user_dest=getItem(position);
        if(user_dest!=null){
            textView_dest.setText(user_dest.getTitle());
        }
        return  convert_view;
    }
}
