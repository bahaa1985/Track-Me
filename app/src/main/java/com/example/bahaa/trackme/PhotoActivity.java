package com.example.bahaa.trackme;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PhotoActivity extends AppCompatActivity {

    private ImageView mImageUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_photo);

        mImageUser=findViewById(R.id.imageUser);
    }


    private static final int PICK_IMAGE_REQUEST=1;
    Uri mImageUri;
    public void buttonChooser_Click(View view) {
        openChooser();
    }

    private void openChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK
                && data!=null && data.getData()!=null){
            mImageUri=data.getData();
            Picasso.get().load(mImageUri).resize(250,250).into(mImageUser);
        }
    }

    public void buttonNext2_Click(View view) {
        try{
            Bitmap bitmap = ((BitmapDrawable)mImageUser.getDrawable()).getBitmap();
            Intent intent=new Intent(this,ContactsActivity.class);
            intent.putExtras(getIntent().getExtras());
            intent.putExtra("photoUri",mImageUri);
            intent.putExtra("photo", bitmap);
            startActivity(intent);
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}
