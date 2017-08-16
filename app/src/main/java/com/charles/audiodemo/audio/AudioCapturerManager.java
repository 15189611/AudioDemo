package com.charles.audiodemo.audio;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Environment;

import com.charles.audiodemo.audio.wav.WavFileReader;
import com.charles.audiodemo.audio.wav.WavFileWriter;

import java.io.IOException;

/**
 * Created by Charles.
 */

public class AudioCapturerManager implements AudioCapturer.OnAudioFrameCapturedListener {

    private static final String DEFAULT_TEST_FILE =  Environment.getExternalStorageDirectory() + "/charles.wav";

    private AudioCapturer mAudioCapturer;  //录音AudioRecord
    private WavFileWriter mWavFileWriter;

    private AudioPlayer mAudioPlayer;     //播放AudioTrack
    private WavFileReader mWavFileReader;
    private volatile boolean mIsTestingExit = false;

    /**
     * 录音
     */
    public boolean startAudioRecord() {
        mWavFileWriter = new WavFileWriter();
        mAudioCapturer = new AudioCapturer();
        try {
            mWavFileWriter.openFile(DEFAULT_TEST_FILE, 44100, 1, 16);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        mAudioCapturer.setOnAudioFrameCapturedListener(this);
        return mAudioCapturer.startCapture(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
    }

    /**
     * 停止录音
     */
    public boolean stopAudioRecord() {
        mAudioCapturer.stopCapture();
        try {
            mWavFileWriter.closeFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void onAudioFrameCaptured(byte[] audioData) {
        //将读取的音频pcm数据 存起来
        mWavFileWriter.writeData(audioData, 0, audioData.length);
    }

    /**
     * 播放录音
     */
    public boolean startAudioPlay(){
        //1.创建读取wav的类
        mWavFileReader = new WavFileReader();
        //2.创建AudioTrack
        mAudioPlayer = new AudioPlayer();
        try {
            mWavFileReader.openFile(DEFAULT_TEST_FILE);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        //3.初始化AudioTrack
        mAudioPlayer.startPlayer();
        //4.开启线程读取wav文件并且播放
        new Thread(AudioPlayRunnable).start();

        return true;
    }

    /**
     * 停止播放
     */
    public boolean stopAudioPlay() {
        mIsTestingExit = true;
        return true;
    }

    private Runnable AudioPlayRunnable = new Runnable() {
        @Override
        public void run() {
            byte[] buffer = new byte[1024 * 2];
            while (!mIsTestingExit && mWavFileReader.readData(buffer, 0, buffer.length) > 0) {
                //将读取到的数据 来播放
                mAudioPlayer.play(buffer, 0, buffer.length);
            }
            //数据读完了 就停止播放
            mAudioPlayer.stopPlayer();
            try {
                mWavFileReader.closeFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public static AudioCapturerManager getInstance() {
        return AudioCapturerManager.InstanceHolder.instance;
    }

    private static class InstanceHolder {
        final static AudioCapturerManager instance = new AudioCapturerManager();
    }

}
