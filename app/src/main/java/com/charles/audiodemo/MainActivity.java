package com.charles.audiodemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.charles.audiodemo.activity.AudioActivity;
import com.charles.audiodemo.activity.DrawingActivity;
import com.charles.audiodemo.activity.VideoActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btn1(View view){
        Intent intent = new Intent(this,DrawingActivity.class);
        startActivity(intent);
    }

    public void btn2(View view){
        Intent intent = new Intent(this,AudioActivity.class);
        startActivity(intent);
    }

    public void btn3(View view){
        Intent intent = new Intent(this,VideoActivity.class);
        startActivity(intent);
    }

    public void btn4(View view){

    }

}
