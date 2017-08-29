package com.charles.audiodemo.video;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Charles.
 */

public class VideoEncoder {
    private MediaCodec mCodec;
    private Surface mSurface;
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

    private EncodeLister mLister;

    void prepare(int mWidth, int mHeight) {
        MediaFormat format = MediaFormat.createVideoFormat(Config.VIDEO_MIME, mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, Config.VIDEO_BITRATE);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, Config.FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, Config.VIDEO_I_FRAME_INTERVAL);

        try {
            mCodec = MediaCodec.createEncoderByType(Config.VIDEO_MIME);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mSurface = mCodec.createInputSurface();
        if (mLister != null) {
            mLister.onSurfaceCreated(mSurface);
        }
        mCodec.start();
    }

    void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                int status = mCodec.dequeueOutputBuffer(mBufferInfo, 10000);
                if (status >= 0) {
                    // encoded sample
                    ByteBuffer data;
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        // 从输入队列里去空闲buffer
                        data = mCodec.getInputBuffers()[status];
                    } else {
                        // SDK_INT > LOLLIPOP
                        data = mCodec.getOutputBuffer(status);
                    }
                    if (data != null) {
                        final int endOfStream = mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM;
                        // pass to whoever listens to
                        if (endOfStream == 0 && mLister != null) {
                            mLister.onSampleEncoded(mBufferInfo, data);
                        }
                        // releasing buffer is important
                        mCodec.releaseOutputBuffer(status, false);
                        if (endOfStream == MediaCodec.BUFFER_FLAG_END_OF_STREAM){
                            return;
                        }
                    }
                }

            }
        }).start();
    }

    void stop() {
        if (mCodec != null) {
            mCodec.stop();
            mCodec.release();
        }
        mSurface.release();
    }

    public interface EncodeLister {
        void onSurfaceCreated(Surface surface);
        void onSampleEncoded(MediaCodec.BufferInfo info, ByteBuffer data);
    }

    public void setLister(EncodeLister lister) {
        mLister = lister;
    }

}
