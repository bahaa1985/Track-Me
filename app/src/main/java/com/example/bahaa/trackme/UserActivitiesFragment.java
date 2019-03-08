package com.example.bahaa.trackme;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UserActivitiesFragment extends Fragment {
    TextView textView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_main_activities,container,false);
       textView=view.findViewById(R.id.section_label);
       textView.setText("جرجا - شيخ العرب");
       return view;
    }

    public static UserActivitiesFragment newInstance(){
        UserActivitiesFragment activities_fragment=new UserActivitiesFragment();
        Bundle args=new Bundle();
        args.putString("Address","جرجا - شيخ العرب");
        activities_fragment.setArguments(args);
        return activities_fragment;
    }
}
