package com.charles.audiodemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.charles.audiodemo.R;
import com.charles.audiodemo.video.SurfaceViewActivity;
import com.charles.audiodemo.video.TextureViewActivity;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
    }


    public void openCamera1(View view){
        Intent intent = new Intent(this,SurfaceViewActivity.class);
        startActivity(intent);
    }

    public void openCamera2(View view){
        Intent intent = new Intent(this,TextureViewActivity.class);
        startActivity(intent);
    }
}
