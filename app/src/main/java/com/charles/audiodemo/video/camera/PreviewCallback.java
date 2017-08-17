package com.charles.audiodemo.video.camera;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

final class PreviewCallback implements Camera.PreviewCallback {

    private static final String TAG = PreviewCallback.class.getSimpleName();

    private final boolean useOneShotPreviewCallback;
    private Handler previewHandler;
    private int previewMessage;

    PreviewCallback(boolean useOneShotPreviewCallback) {
        this.useOneShotPreviewCallback = useOneShotPreviewCallback;
    }

    void setHandler(Handler previewHandler, int previewMessage) {
        this.previewHandler = previewHandler;
        this.previewMessage = previewMessage;
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        if (!useOneShotPreviewCallback) {
            camera.setPreviewCallback(null);
        }
        if (previewHandler != null) {

            Message message = previewHandler.obtainMessage();
            Camera.Parameters parameters = camera.getParameters();
            message.what = previewMessage;
            Bundle bundle = new Bundle();
            bundle.putByteArray("data",data);
            message.setData(bundle);
            message.obj = parameters;
            message.sendToTarget();
        } else {
            Log.d(TAG, "Got preview callback, but no handler for it");
        }
    }

}
