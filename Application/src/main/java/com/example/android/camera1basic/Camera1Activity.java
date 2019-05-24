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
import android.content.res.Resources;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import cc.rome753.yuvtools.YUVDetectView;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.

// ----------------------------------------------------------------------

public class Camera1Activity extends AppCompatActivity {
    private Camera1Preview mPreview;
    Camera mCamera;
    int numberOfCameras;
    int cameraCurrentlyLocked;

    // The first rear facing camera
    int defaultCameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Create a RelativeLayout container that will hold a SurfaceView,
        // and set it as the content of our activity.
        mPreview = new Camera1Preview(this);
        setContentView(mPreview);

        mPreview.setImage(addImage());

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
        cameraCurrentlyLocked = defaultCameraId;
        mPreview.setCamera(mCamera);
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

    private YUVDetectView addImage() {
        float density = Resources.getSystem().getDisplayMetrics().density;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (int)(100 * density));
        YUVDetectView image = new YUVDetectView(this);
        image.setLayoutParams(params);
        ((ViewGroup)(getWindow().findViewById(android.R.id.content))).addView(image);
        return image;
    }
}

// ----------------------------------------------------------------------
