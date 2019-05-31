package com.example.android.camera2basic;

import android.graphics.Bitmap;
import android.media.ImageReader;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import cc.rome753.yuvtools.YUVDetectView;
import cc.rome753.yuvtools.YUVTools;

public class Camera2Fragment extends Camera2BasicFragment {

    private YUVDetectView ydv;

    public static Camera2Fragment newInstance() {
        Bundle args = new Bundle();
        Camera2Fragment fragment = new Camera2Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ydv = view.findViewById(R.id.ydv);
    }

    @Override
    protected void handleImage(ImageReader reader) {
        ydv.input(reader);
    }


}
