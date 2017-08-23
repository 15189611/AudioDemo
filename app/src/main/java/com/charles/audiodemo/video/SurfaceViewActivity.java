package com.charles.audiodemo.video;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.Toast;

import com.charles.audiodemo.R;
import com.charles.audiodemo.video.camera.MyCameraManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SurfaceViewActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    @Bind(R.id.surfaceView)
    SurfaceView surfaceView;
    @Bind(R.id.surfaceImage)
    ImageView surfaceImage;

    private SurfaceHolder surfaceHolder;
    private MyCameraManager myCameraManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view);
        ButterKnife.bind(this);
        initCameraManager();
        initView();
    }

    private void initCameraManager() {
        MyCameraManager.init(this);
        myCameraManager = MyCameraManager.get();
    }

    private void initView() {
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }

    private Handler cameraHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case R.id.auto_focus:
                    boolean success = (boolean) msg.obj;
                    if (success) {
                        Toast.makeText(SurfaceViewActivity.this, "自动对焦成功", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.request_preview:
                    Bundle bundle = msg.getData();
                    byte[] data = bundle.getByteArray("data");
                    Camera.Parameters parameters = (Camera.Parameters) msg.obj;
                    Camera.Size size = parameters.getPreviewSize();
                    try {
                        //byte[] bytes = rotateYUV420Degree90(data, size.width, size.height);  //方法2 直接转原始数据
                        YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
                        Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());

                        //将采集的数据图片 旋转  (方法1)
                       Matrix matrix = new Matrix();
                        matrix.preRotate(90);
                        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp
                                .getHeight(), matrix, true);

                        if (bmp != null) {
                            surfaceImage.setImageBitmap(bmp);
                        }
                        stream.close();
                    } catch (Exception ex) {
                        Log.e("Sys", "Error:" + ex.getMessage());
                    }
                    break;
            }
        }
    };

    //直接转换原始数据
    private byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

    private static byte[] rotateYUV420Degree180(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int i = 0;
        int count = 0;
        for (i = imageWidth * imageHeight - 1; i >= 0; i--) {
            yuv[count] = data[i];
            count++;
        }
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (i = imageWidth * imageHeight * 3 / 2 - 1; i >= imageWidth
                * imageHeight; i -= 2) {
            yuv[count++] = data[i - 1];
            yuv[count++] = data[i];
        }
        return yuv;
    }

    public static byte[] rotateYUV420Degree270(byte[] data, int imageWidth,
                                               int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int nWidth = 0, nHeight = 0;
        int wh = 0;
        int uvHeight = 0;
        if (imageWidth != nWidth || imageHeight != nHeight) {
            nWidth = imageWidth;
            nHeight = imageHeight;
            wh = imageWidth * imageHeight;
            uvHeight = imageHeight >> 1;// uvHeight = height / 2
        }
        // ??Y
        int k = 0;
        for (int i = 0; i < imageWidth; i++) {
            int nPos = 0;
            for (int j = 0; j < imageHeight; j++) {
                yuv[k] = data[nPos + i];
                k++;
                nPos += imageWidth;
            }
        }
        for (int i = 0; i < imageWidth; i += 2) {
            int nPos = wh;
            for (int j = 0; j < uvHeight; j++) {
                yuv[k] = data[nPos + i];
                yuv[k + 1] = data[nPos + i + 1];
                k += 2;
                nPos += imageWidth;
            }
        }
        return rotateYUV420Degree180(yuv, imageWidth, imageHeight);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (myCameraManager != null) {
            try {
                myCameraManager.openDriver(surfaceHolder);
                myCameraManager.startPreview();
                myCameraManager.requestPreviewFrame(cameraHandler, R.id.request_preview);
                myCameraManager.requestAutoFocus(cameraHandler, R.id.auto_focus);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (myCameraManager != null) {
            cameraHandler.removeMessages(R.id.request_preview);
            myCameraManager.stopPreview();
            myCameraManager.closeDriver();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (myCameraManager != null) {
            cameraHandler.removeMessages(R.id.request_preview);
            myCameraManager.stopPreview();
            myCameraManager.closeDriver();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
