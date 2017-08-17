package com.charles.audiodemo.video.camera;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.charles.audiodemo.IPreviewCallBack;

import java.io.IOException;

/**
 * Created by Charles.
 */

public class MyCameraManager {

    private static final String TAG = MyCameraManager.class.getSimpleName();

    static {
        int sdkInt;
        try {
            sdkInt = Integer.parseInt(Build.VERSION.SDK);
        } catch (NumberFormatException nfe) {
            sdkInt = 10000;
        }
        SDK_INT = sdkInt;
    }

    private final Context context;
    private Camera camera;
    private boolean initialized;
    private boolean previewing;
    private final boolean useOneShotPreviewCallback;
    private final PreviewCallback previewCallback;
    private final AutoFocusCallback autoFocusCallback;
    private static MyCameraManager cameraManager;
    private final CameraConfigurationManager configManager;
    static final int SDK_INT; // Later we can use Build.VERSION.SDK_INT

    public static void init(Context context) {
        if (cameraManager == null) {
            cameraManager = new MyCameraManager(context);
        }
    }

    public static MyCameraManager get() {
        return cameraManager;
    }

    private MyCameraManager(Context context) {
        this.context = context;
        useOneShotPreviewCallback = SDK_INT > 3; // 3 = Cupcake
        this.configManager = new CameraConfigurationManager(context);
        previewCallback = new PreviewCallback(useOneShotPreviewCallback);
        autoFocusCallback = new AutoFocusCallback();
    }

    public void openDriver(SurfaceHolder holder) throws IOException {
        if (camera == null) {
            camera = Camera.open();
            if (camera == null) {
                throw new IOException();
            }
            camera.setPreviewDisplay(holder);
            if (!initialized) {
                initialized = true;
                configManager.initFromCameraParameters(camera);
            }

            configManager.setDesiredCameraParameters(camera);

            FlashlightManager.enableFlashlight();
        }
    }

    /**
     * Closes the camera driver if still in use.
     */
    public void closeDriver() {
        if (camera != null) {
            FlashlightManager.disableFlashlight();
            camera.release();
            camera = null;
        }
    }

    /**
     * Asks the camera hardware to begin drawing preview frames to the screen.
     */
    public void startPreview() {
        if (camera != null && !previewing) {
            camera.startPreview();
            previewing = true;
        }
    }

    /**
     * Tells the camera to stop drawing preview frames.
     */
    public void stopPreview() {
        if (camera != null && previewing) {
            if (!useOneShotPreviewCallback) {
                camera.setPreviewCallback(null);
            }else {
                camera.setPreviewCallback(null);
            }
            camera.stopPreview();
            previewCallback.setHandler(null, 0);
            autoFocusCallback.setHandler(null, 0);
            previewing = false;
        }
    }

    public void requestPreviewFrame(Handler handler, int message) {
        if (camera != null && previewing) {
            previewCallback.setHandler(handler, message);
            Log.d(TAG, "Requesting requestPreviewFrame");
            if (useOneShotPreviewCallback) {
                camera.setPreviewCallback(previewCallback);
                //camera.setOneShotPreviewCallback(previewCallback);
            } else {
                camera.setPreviewCallback(previewCallback);
            }
        }
    }

    public void requestPreviewFrame(final IPreviewCallBack callBack) {
        if (camera != null) {
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    if(callBack != null){
                        callBack.preViewCall(data,camera);
                    }
                }
            });
        }
    }


    public void requestAutoFocus(Handler handler, int message) {
        if (camera != null && previewing) {
            autoFocusCallback.setHandler(handler, message);
            Log.d(TAG, "Requesting auto-focus callback");
            camera.autoFocus(autoFocusCallback);
        }
    }

    public Context getContext() {
        return context;
    }

}
