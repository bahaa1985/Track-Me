package com.example.bahaa.trackme;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ContactModel{

    private String mConName;
    private String mConNum;
    private Uri mConImage;

    public String getConName() {
        return mConName;
    }

    public void setConName(String mConName) {
        this.mConName = mConName;
    }

    public String getConNum() {
        return mConNum;
    }

    public void setConNum(String mConNum) {
        this.mConNum = mConNum;
    }

    public Uri getConImage() {
        return mConImage;
    }

    public void setConImage(Uri mConImage) {
        this.mConImage = mConImage;
    }
}
