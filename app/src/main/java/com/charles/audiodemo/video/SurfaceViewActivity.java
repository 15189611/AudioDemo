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
import com.charles.audiodemo.utils.Util;
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

                    byte[] dst;
                    if (Util.getDgree(SurfaceViewActivity.this) == 0) {
                      //  dst = Util.rotateYUV420Degree90(data, size.width, size.height);  //方法1 直接转原始数据
                        dst = data;
                    } else {
                        dst = data;
                    }

                    try {
                        YuvImage image = new YuvImage(dst, ImageFormat.NV21, size.width, size.height, null);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
                        Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());

                        //将采集的数据图片 旋转  (方法2)
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
