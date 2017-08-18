package com.charles.audiodemo.activity;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.charles.audiodemo.R;

import java.io.IOException;
import java.nio.ByteBuffer;

import static android.R.attr.format;

/**
 * MediaMuxer类主要用于将音频和视频数据进行混合生成多媒体文件（如：mp4文件），而MediaExtractor则刚好相反，主要用于多媒体文件的音视频数据的分离。
 */
public class MediaExtractorActivity extends AppCompatActivity {

    private static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath();
    private static final String INPUT_FILEPATH = SDCARD_PATH + "/input.mp4";
    private static final String OUTPUT_FILEPATH = SDCARD_PATH + "/output.mp4";

    private TextView mLogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_extractor);
        mLogView = (TextView) findViewById(R.id.LogView);
    }

    /**
     * 生成mp4文件
     */
    public void onClick1(View view) {

    }

    /**
     * 解析mp4文件,去掉音频或者视频
     */
    public void onClick2(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    transcode(INPUT_FILEPATH, OUTPUT_FILEPATH);
                } catch (IOException e) {
                    e.printStackTrace();
                    logout(e.getMessage());
                }
            }
        }).start();
    }

    protected boolean transcode(String input, String output) throws IOException {
        logout("start processing...");
        MediaMuxer muxer = null;
        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(input);  //1.首先通过setDataSource()设置数据源，数据源可以是本地文件地址，也可以是网络地址：
        logout("start demuxer: " + input);

        int mVideoTrackIndex = -1;
        for (int i = 0; i < extractor.getTrackCount(); i++) {
            //2.然后可以通过getTrackFormat(int index)来获取各个track的MediaFormat，通过MediaFormat来获取track的详细信息，如：MimeType、分辨率、采样频率、帧率等等：
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (!mime.startsWith("video")) {  //如果不是video 就过滤掉，其实就是将音频pcm 分离出来,相反改成audio就只会有音频没有视频
                logout("mime not video, continue search");
                continue;
            }

            //3.获取到track的详细信息后，通过selectTrack(int index)选择指定的通道：
            extractor.selectTrack(i);

            muxer = new MediaMuxer(output, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            mVideoTrackIndex = muxer.addTrack(format);
            muxer.start();           // 添加完所有轨道后start
            logout("start muxer: " + output);

        }

        if (muxer == null) {
            logout("no video found !");
            return false;
        }

        //下面开始封装视频track
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        info.presentationTimeUs = 0;
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024 * 2);
        while (true) {
            //4.指定通道之后就可以从MediaExtractor中读取数据了：
            int sampleSize = extractor.readSampleData(buffer, 0);
            if (sampleSize < 0) {
                logout("read sample data failed , break !");
                break;
            }
            info.offset = 0;
            info.size = sampleSize;
            info.flags = extractor.getSampleFlags();  //MediaCodec.BUFFER_FLAG_SYNC_FRAME;
            info.presentationTimeUs = extractor.getSampleTime();

            boolean keyframe = (info.flags & MediaCodec.BUFFER_FLAG_SYNC_FRAME) > 0;
            logout("write sample " + keyframe + ", " + sampleSize + ", " + info.presentationTimeUs);

            //5.最后将读取到的音频或者视频文件 写到out.mp4文件中
            muxer.writeSampleData(mVideoTrackIndex, buffer, info);
            extractor.advance(); //前进到下一帧。
        }

        extractor.release();

        muxer.stop();
        muxer.release();

        logout("process success !");

        return true;
    }

    private void logout(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("MediaDemo", content);
                mLogView.setText(mLogView.getText() + "\n" + content);
            }
        });
    }

}
