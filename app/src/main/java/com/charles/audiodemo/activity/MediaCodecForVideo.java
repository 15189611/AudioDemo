package com.charles.audiodemo.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.charles.audiodemo.R;
import com.charles.audiodemo.video.VideoDecoder;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MediaCodecForVideo extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String TAG = MediaCodecForVideo.class.getSimpleName();
    @Bind(R.id.surfaceView)
    SurfaceView surfaceView;

    private static final String FilePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "input.mp4";
    private SurfaceHolder holder;
    VideoDecoder videoDecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_codec_for_video);
        ButterKnife.bind(this);
        holder = surfaceView.getHolder();
        holder.addCallback(this);
        videoDecoder = new VideoDecoder();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(videoDecoder == null){
            return;
        }

        if(videoDecoder.init(holder.getSurface(),FilePath)){
            videoDecoder.start();
        }else {
            Toast.makeText(MediaCodecForVideo.this,"文件没找到",Toast.LENGTH_SHORT).show();
            Log.d(TAG,"文件没有找到---------");
            onBackPressed();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (videoDecoder != null) {
            videoDecoder.close();
        }
    }

}
