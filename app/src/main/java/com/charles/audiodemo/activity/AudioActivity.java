package com.charles.audiodemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.charles.audiodemo.R;
import com.charles.audiodemo.dialog.RecordAudioDialog;

public class AudioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
    }

    public void audioClick(View view){
        RecordAudioDialog recordAudioDialog = RecordAudioDialog.newInstance();
        recordAudioDialog.show(getSupportFragmentManager());
    }
}
