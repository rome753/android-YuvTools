package cc.rome753.yuvtools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.android.camera2basic.R;

public class YUVDetectView extends FrameLayout {

    ImageView[] ivs;
    CheckBox cb;
    boolean isFlip = false;
    boolean isShowing = false;

    public YUVDetectView(@NonNull Context context) {
        this(context, null);
    }

    public YUVDetectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YUVDetectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_yuv_detect, this);

        ivs = new ImageView[]{
                findViewById(R.id.iv1), // I420
                findViewById(R.id.iv2), // YV12
                findViewById(R.id.iv3), // NV12
                findViewById(R.id.iv4), // NV21
        };
        cb = findViewById(R.id.cb);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isFlip = isChecked;
            }
        });
    }

    public void inputAsync(final byte[] data, int width, int height) {
        final int w = isFlip ? height : width;
        final int h = isFlip ? width : height;

        if(isShowing) return;
        isShowing = true;
        new Thread() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();

                byte[] b = new byte[data.length];
                YUVTools.i420ToNv21cpp(data, b, w, h);
                final Bitmap b0 = YUVTools.nv21ToBitmap(b, w, h);

                YUVTools.yv12ToNv21cpp(data, b, w, h);
                final Bitmap b1 = YUVTools.nv21ToBitmap(b, w, h);

                YUVTools.nv12ToNv21cpp(data, b, w, h);
                final Bitmap b2 = YUVTools.nv21ToBitmap(b, w, h);

                final Bitmap b3 = YUVTools.nv21ToBitmap(data, w, h);

                time = System.currentTimeMillis() - time;
                Log.d("YUVDetectView", "convert time: " + time);
                post(new Runnable() {
                    @Override
                    public void run() {
                        if(b0 != null) ivs[0].setImageBitmap(b0);
                        if(b1 != null) ivs[1].setImageBitmap(b1);
                        if(b2 != null) ivs[2].setImageBitmap(b2);
                        if(b3 != null) ivs[3].setImageBitmap(b3);
                        isShowing = false;
                    }
                });
            }
        }.start();
    }
}
