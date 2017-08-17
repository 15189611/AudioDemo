package com.charles.audiodemo.video;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.Toast;

import com.charles.audiodemo.R;
import com.charles.audiodemo.video.camera.MyCameraManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TextureViewActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    @Bind(R.id.textureView)
    TextureView textureView;
    @Bind(R.id.surfaceImage)
    ImageView surfaceImage;

    private MyCameraManager myCameraManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texture_view);
        ButterKnife.bind(this);
        initCameraManager();
        initView();
    }

    private void initCameraManager() {
        MyCameraManager.init(this);
        myCameraManager = MyCameraManager.get();
    }

    private void initView() {
        textureView.setSurfaceTextureListener(this);
    }

    private Handler cameraHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case R.id.auto_focus:
                    boolean success = (boolean) msg.obj;
                    if (success) {
                        Toast.makeText(TextureViewActivity.this, "自动对焦成功", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.request_preview:
                    Bundle bundle = msg.getData();
                    byte[] data = bundle.getByteArray("data");
                    Camera.Parameters parameters = (Camera.Parameters) msg.obj;
                    Camera.Size size = parameters.getPreviewSize();
                    try {
                        YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
                        Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                        //将采集的数据图片 旋转
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
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (myCameraManager != null) {
            try {
                myCameraManager.openDriver(surface);
                myCameraManager.startPreview();
                myCameraManager.requestPreviewFrame(cameraHandler, R.id.request_preview);
                myCameraManager.requestAutoFocus(cameraHandler, R.id.auto_focus);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (myCameraManager != null) {
            cameraHandler.removeMessages(R.id.request_preview);
            myCameraManager.stopPreview();
            myCameraManager.closeDriver();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();

    }
}
