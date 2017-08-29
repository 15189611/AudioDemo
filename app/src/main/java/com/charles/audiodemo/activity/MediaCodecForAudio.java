package com.charles.audiodemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.charles.audiodemo.R;
import com.charles.audiodemo.audio.AudioCapturer;
import com.charles.audiodemo.audio.AudioDecoder;
import com.charles.audiodemo.audio.AudioEncoder;
import com.charles.audiodemo.audio.AudioPlayer;

public class MediaCodecForAudio extends AppCompatActivity implements AudioCapturer.OnAudioFrameCapturedListener, AudioDecoder.OnAudioDecodedListener, AudioEncoder.OnAudioEncodedListener {

    private AudioCapturer audioCapturer;
    private AudioPlayer audioPlayer;
    private AudioEncoder mAudioEncoder;   //编码
    private AudioDecoder mAudioDecoder;  //解码

    private volatile boolean mIsTestingExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_codec_for_audio);
    }

    public void start(View view) {
        startAudioDecoder();
    }

    public void stop(View view) {
        mIsTestingExit = true;
        audioCapturer.stopCapture();
    }

    private boolean startAudioDecoder() {
        //1.初始化AudioRecord AudioTrack
        audioCapturer = new AudioCapturer();
        audioPlayer = new AudioPlayer();
        audioCapturer.setOnAudioFrameCapturedListener(this); //设置录音回调的pcm数据

        //2.初始化编码解码对象
        mAudioEncoder = new AudioEncoder();
        mAudioDecoder = new AudioDecoder();
        if (!mAudioEncoder.open() || !mAudioDecoder.open()) {  //初始化一些 编码 解码的配置
            return false;
        }

        //3.设置监听
        mAudioEncoder.setAudioEncodedListener(this);
        mAudioDecoder.setAudioDecodedListener(this);

        //4.开启线程读取 编码解码 好的数据
        new Thread(mEncodeRenderRunnable).start();
        new Thread(mDecodeRenderRunnable).start();

        //5.开始录制  和 AudioTrack的初始化
        if (!audioCapturer.startCapture()) {
            return false;
        }
        audioPlayer.startPlayer();

        return true;
    }

    private Runnable mEncodeRenderRunnable = new Runnable() {
        @Override
        public void run() {
            while (!mIsTestingExit) {
                mAudioEncoder.retrieve();
            }
            mAudioEncoder.close();
        }
    };

    private Runnable mDecodeRenderRunnable = new Runnable() {
        @Override
        public void run() {
            while (!mIsTestingExit) {
                mAudioDecoder.retrieve();
            }
            mAudioDecoder.close();
        }
    };

    @Override
    public void onAudioFrameCaptured(byte[] audioData) {  //录音的回调数据
        //1.拿到数据是pcm的，将它编码成acc
        long presentationTimeUs = (System.nanoTime()) / 1000L;
        mAudioEncoder.encode(audioData, presentationTimeUs);
    }

    @Override
    public void onFrameEncoded(byte[] encoded, long presentationTimeUs) { //编码后的回调
        //2.编码后的data 需要解码 才能播放
        mAudioDecoder.decode(encoded, presentationTimeUs);
    }

    @Override
    public void onFrameDecoded(byte[] decoded, long presentationTimeUs) {  //解码后回调
        //3.解码成功后，使用AudioTrack 播放
        audioPlayer.play(decoded, 0, decoded.length);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsTestingExit = true;
        if(audioCapturer != null){
            audioCapturer.stopCapture();
        }
    }
}
