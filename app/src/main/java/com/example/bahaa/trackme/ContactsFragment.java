package com.example.bahaa.trackme;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.protobuf.DescriptorProtos;

public class ContactsFragment extends Fragment {
    TextView textView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_main_contacts,container,false);
       textView=view.findViewById(R.id.section_label);
       textView.setText("محمد صلاح الدين");
       return view;
    }

    public static ContactsFragment newInstance(){
        ContactsFragment contacts_fragment=new ContactsFragment();
        Bundle args=new Bundle();
        args.putString("Contact","محمد صلاح الدين");
        contacts_fragment.setArguments(args);
        return contacts_fragment;
    }
}
