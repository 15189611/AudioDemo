package com.charles.audiodemo.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by Charles.
 * 利用AudioRecord采集wav　数据
 */

public class AudioCapturer {

    private static final String TAG = AudioCapturer.class.getName();

    private static final int DEFAULT_SOURCE = MediaRecorder.AudioSource.MIC;  //source
    private static final int DEFAULT_SAMPLE_RATE = 44100;                     //sampleRateInHz 采样率 Android一般是44100
    private static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;   //channel_config 通道数的配置CHANNEL_IN_MONO（单通道），CHANNEL_IN_STEREO（双通道）
    private static final int DEFAULT_DATA_FORMAT = AudioFormat.ENCODING_PCM_16BIT;   //数据位宽 常用的是 ENCODING_PCM_16BIT（16bit），ENCODING_PCM_8BIT（8bit），注意，前者是可以保证兼容所有Android手机的。

    private boolean mIsCaptureStarted = false;
    private AudioRecord mAudioRecord;
    private Thread mCaptureThread;
    private volatile boolean mIsLoopExit = false;

    public  boolean startCapture(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat ){
        if (mIsCaptureStarted) {
            Log.e(TAG, "Capture already started !");
            return false;
        }

        //1.获取AudioRecord内部音频缓冲区大小
        int minBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        if (minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(TAG, "Invalid parameter !");
            return false;
        }
        //2.创建AudioRecord对象
        mAudioRecord = new AudioRecord(audioSource,sampleRateInHz,channelConfig,audioFormat,minBufferSize);
        if (mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
            Log.e(TAG, "AudioRecord initialize fail !");
            return false;
        }

        //3.开始
        mAudioRecord.startRecording();
        //4.开启线程采集pcm音频数据
        mIsLoopExit = false;
        mCaptureThread = new Thread(new AudioCaptureRunnable());
        mCaptureThread.start();

        mIsCaptureStarted = true;
        Log.i(TAG, "Start audio capture success !");

        return true;
    }

    public void stopCapture(){
        if(!mIsCaptureStarted){
            return;
        }
        mIsLoopExit = true;
        //停止线程，让出执行权
        try {
            mCaptureThread.interrupt();
            mCaptureThread.join(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //还在录音状态，将停止
        if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            mAudioRecord.stop();
        }

        mAudioRecord.release();
        mIsCaptureStarted = false;
        mAudioFrameCapturedListener = null;
        Log.i(TAG, "Stop audio capture success !");
    }

    private class AudioCaptureRunnable implements Runnable {
        @Override
        public void run() {
            while (!mIsLoopExit) {
                byte[] buffer = new byte[1024 * 2];
                int ret = mAudioRecord.read(buffer, 0, buffer.length);
                if (ret == AudioRecord.ERROR_INVALID_OPERATION) {
                    Log.e(TAG, "Error ERROR_INVALID_OPERATION");
                } else if (ret == AudioRecord.ERROR_BAD_VALUE) {
                    Log.e(TAG, "Error ERROR_BAD_VALUE");
                } else {
                    Log.d("TAG", "Audio captured: " + buffer.length);
                    if (mAudioFrameCapturedListener != null) {
                        //将读取到的数据回调出去，并且以wav格式存储起来
                        mAudioFrameCapturedListener.onAudioFrameCaptured(buffer);
                    }
                }
            }
        }
    }

    private OnAudioFrameCapturedListener mAudioFrameCapturedListener;

    public interface OnAudioFrameCapturedListener {
        void onAudioFrameCaptured(byte[] audioData);
    }

    public void setOnAudioFrameCapturedListener(OnAudioFrameCapturedListener listener) {
        mAudioFrameCapturedListener = listener;
    }

}
