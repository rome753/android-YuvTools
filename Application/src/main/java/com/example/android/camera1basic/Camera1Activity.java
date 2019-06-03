/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.camera1basic;

import android.app.AlertDialog;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.android.camera2basic.R;

import java.util.List;

import cc.rome753.yuvtools.YUVDetectView;
import cc.rome753.yuvtools.YUVTools;

import static cc.rome753.yuvtools.YUVTools.yuv420map;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.

// ----------------------------------------------------------------------

public class Camera1Activity extends AppCompatActivity implements Camera.PreviewCallback {
    private Camera1Preview mPreview;
    private YUVDetectView ydv;
    private RadioGroup rg;
    Camera mCamera;
    int numberOfCameras;
    int cameraCurrentlyLocked;

    // The first rear facing camera
    int defaultCameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera1);
//
//        // Hide the window title.
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Create a RelativeLayout container that will hold a SurfaceView,
        // and set it as the content of our activity.
        mPreview = findViewById(R.id.surface);
        ydv = findViewById(R.id.ydv);
        rg = findViewById(R.id.rg);

        // Find the total number of cameras available
        numberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the default camera
        CameraInfo cameraInfo = new CameraInfo();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                    defaultCameraId = i;
                }
            }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Open the default i.e. the first rear facing camera.
        mCamera = Camera.open();
        mCamera.setPreviewCallback(this);
        mCamera.setDisplayOrientation(90);
        initFormats();
        cameraCurrentlyLocked = defaultCameraId;
        mPreview.setCamera(mCamera);
    }

    private void initFormats() {
        List<Integer> supportedPreviewFormats = mCamera.getParameters().getSupportedPreviewFormats();
        if(rg.getChildCount() > 0) return;
        rg.removeAllViews();
        int id = 0;
        for(int i : supportedPreviewFormats) {
            if(yuv420map.containsKey(i)) {
                RadioButton rb = new RadioButton(this);
                rb.setText(yuv420map.get(i));
                rb.setTag(i);
                rb.setId(id++);
                rg.addView(rb);
            }
        }
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.getChildAt(checkedId);
                Log.d("chao", rb == null ? "null" : rb.getText().toString());
                int imageFormat = (int) rb.getTag();
                mPreview.setImageFormat(imageFormat);
            }
        });
    }

    public void checkFirst() {
        if(rg.getChildCount() > 0) ((RadioButton)rg.getChildAt(0)).setChecked(true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            mPreview.setCamera(null);
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private void switchCam() {
        if (numberOfCameras == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Device has only one camera!")
                   .setNeutralButton("Close", null);
            AlertDialog alert = builder.create();
            alert.show();
            return;
        }

        // OK, we have multiple cameras.
        // Release this camera -> cameraCurrentlyLocked
        if (mCamera != null) {
            mCamera.stopPreview();
            mPreview.setCamera(null);
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }

        // Acquire the next camera and request Preview to reconfigure
        // parameters.
        mCamera = Camera
                .open((cameraCurrentlyLocked + 1) % numberOfCameras);
        cameraCurrentlyLocked = (cameraCurrentlyLocked + 1)
                % numberOfCameras;
        mPreview.switchCamera(mCamera);

        // Start the preview
        mCamera.startPreview();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if(camera == null) return;
        Camera.Size size = camera.getParameters().getPreviewSize(); //获取预览大小
        final int w = size.width;
        final int h = size.height;
        ydv.inputAsync(data, w, h);
    }

}

// ----------------------------------------------------------------------
