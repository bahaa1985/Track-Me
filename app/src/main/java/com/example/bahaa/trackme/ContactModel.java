package com.example.bahaa.trackme;

import android.net.Uri;

public class ContactModel {

    private String mConId;
    private String mConName;
    private String mConNum;
    private String mConNumMobile;
    private String mConNumWork;
    private Uri mConImage;

    public String getConName() {
        return mConName;
    }

    public void setConName(String mConName) {
        this.mConName = mConName;
    }

    public String getConNumHome() {
        return mConNum;
    }

    public void setConNumHome(String mConNumHome) {
        this.mConNum = mConNumHome;
    }

    public Uri getConImage() {
        return mConImage;
    }

    public void setConImage(Uri mConImage) {
        this.mConImage = mConImage;
    }

    public String getmConId() {
        return mConId;
    }

    public void setmConId(String mConId) {
        this.mConId = mConId;
    }

    public String getmConNumMobile() {
        return mConNumMobile;
    }

    public void setmConNumMobile(String mConNuMobile) {
        this.mConNumMobile = mConNuMobile;
    }

    public String getmConNumWork() {
        return mConNumWork;
    }

    public void setmConNumWork(String mConNumWork) {
        this.mConNumWork = mConNumWork;
    }
}
