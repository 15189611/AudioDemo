package com.charles.audiodemo;

import android.hardware.Camera;

/**
 * Created by Charles.
 */

public interface IPreviewCallBack {
    void preViewCall (byte[] data, Camera camera);
}
