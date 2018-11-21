package com.example.bahaa.trackme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import java.util.regex.Pattern;

public class RegActivity extends AppCompatActivity {


    private EditText mEditName,mEditPhone,mEditEmail,mEditPass,mEditConPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        mEditName=findViewById(R.id.editName);mEditPhone=findViewById(R.id.editPhone);
        mEditEmail=findViewById(R.id.editEmail);
        mEditPass=findViewById(R.id.editPass);mEditConPass=findViewById(R.id.editConPass);

    }
    private Boolean validateName(){
        boolean isValidated;
        if(mEditName.getText().length()<4 ||mEditName.getText().length() >25 || mEditName.getText()==null){
            mEditName.setError("الإسم يجب ألا يقل عن أربع أحرف و لا يزيد عن 25 حرف");
            isValidated=false;
        }
        else{
            isValidated=true;
            mEditName.setError(null);
        }
        return  isValidated;
    }

    private Boolean validatePhone(){
        boolean isValidated;
        if(mEditPhone.getText().toString().isEmpty()||!Patterns.PHONE.matcher(mEditPhone.getText()).matches()){
            mEditPhone.setError("تأكد من رقم الهاتف");
            isValidated=false;
        }
        else
        {
            mEditPhone.setError(null);
            isValidated=true;
        }
        return isValidated;
    }

    private Boolean validateEmail(){
        Boolean isValidate;
        if(!Patterns.EMAIL_ADDRESS.matcher(mEditEmail.getText()).matches()|| mEditEmail.getText().toString().isEmpty()){
            mEditEmail.setError("تأكد من الإيميل");
            isValidate=false;
        }
        else{
            mEditEmail.setError(null);
            isValidate=true;
        }
        return isValidate;
    }

    private static final Pattern passwordPattern=Pattern.compile("(?=.*[A-Za-z])(?=.*[0-9]).{4,}");
    private Boolean validatePassWord(){
        Boolean isValidate;
        if(!mEditPass.getText().toString().matches(passwordPattern.pattern())){
            isValidate=false;
            mEditPass.setError("كلمة المرور يجب أن تحتوي على حرف واحد و رقم واحد على اﻷقل و لا يقل طولها عن 6 خانات");
        }
        else {
            isValidate=true;
            mEditPass.setError(null);
        }
        return isValidate;
    }
    private Boolean validateConPassWord(){
        Boolean isValidate;
        String pass,conPass;
        pass=mEditConPass.getText().toString();
        conPass=mEditPass.getText().toString();
        if(!pass.equals(conPass)){
            isValidate=false;
            mEditConPass.setError("كلمة المرور غير مطابقة");
        }
        else {
            isValidate=true;
            mEditConPass.setError(null);
        }
        return isValidate;
    }

    public void buttonNext1_Click(View view) {
        if(validateName() && validateEmail() && validatePhone() && validatePassWord() && validateConPassWord()){
            Intent intent=new Intent(this,PhotoActivity.class);
            startActivity(intent);
        }
        else
        {

        }

    }
}
