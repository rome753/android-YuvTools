package cc.rome753.yuvtools;

import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.android.camera2basic.Camera2BasicFragment;
import com.example.android.camera2basic.R;

import java.nio.ByteBuffer;

public class Camera2Fragment extends Camera2BasicFragment {

    private ImageView iv;

    public static Camera2Fragment newInstance() {
        Bundle args = new Bundle();
        Camera2Fragment fragment = new Camera2Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iv = view.findViewById(R.id.iv);
    }

    @Override
    protected void handleImage(ImageReader reader) {
        final Bitmap bitmap = yuv2bitmap(reader);
        iv.post(new Runnable() {
            @Override
            public void run() {
                iv.setImageBitmap(bitmap);
            }
        });
    }

    private static int[] getRGBIntFromPlanes(Image.Plane[] planes, int height) {
        ByteBuffer yPlane = planes[0].getBuffer();
        ByteBuffer uPlane = planes[1].getBuffer();
        ByteBuffer vPlane = planes[2].getBuffer();

        int bufferIndex = 0;
        final int total = yPlane.capacity();
        final int uvCapacity = uPlane.capacity();
        final int width = planes[0].getRowStride();

        int[] rgbBuffer = new int[width * height];

        int yPos = 0;
        for (int i = 0; i < height; i++) {
            int uvPos = (i >> 1) * width;

            for (int j = 0; j < width; j++) {
                if (uvPos >= uvCapacity-1)
                    break;
                if (yPos >= total)
                    break;

                final int y1 = yPlane.get(yPos++) & 0xff;

            /*
              The ordering of the u (Cb) and v (Cr) bytes inside the planes is a
              bit strange. The _first_ byte of the u-plane and the _second_ byte
              of the v-plane build the u/v pair and belong to the first two pixels
              (y-bytes), thus usual YUV 420 behavior. What the Android devs did
              here (IMHO): just copy the interleaved NV21 U/V data to two planes
              but keep the offset of the interleaving.
             */
                final int u = (uPlane.get(uvPos) & 0xff) - 128;
                final int v = (vPlane.get(uvPos) & 0xff) - 128;
                if ((j & 1) == 1) {
                    uvPos += 2;
                }

                // This is the integer variant to convert YCbCr to RGB, NTSC values.
                // formulae found at
                // https://software.intel.com/en-us/android/articles/trusted-tools-in-the-new-android-world-optimization-techniques-from-intel-sse-intrinsics-to
                // and on StackOverflow etc.
                final int y1192 = 1192 * y1;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                r = (r < 0) ? 0 : ((r > 262143) ? 262143 : r);
                g = (g < 0) ? 0 : ((g > 262143) ? 262143 : g);
                b = (b < 0) ? 0 : ((b > 262143) ? 262143 : b);

                rgbBuffer[bufferIndex++] = ((r << 6) & 0xff0000) |
                        ((g >> 2) & 0xff00) |
                        ((b >> 10) & 0xff);
            }
        }
        return rgbBuffer;
    }


    private static Bitmap yuv2bitmap(ImageReader reader) {
        try (Image image = reader.acquireNextImage()) {
            Image.Plane[] planes = image.getPlanes();
            int[] rgb = getRGBIntFromPlanes(planes, 480);
            return Bitmap.createBitmap(rgb, 640, 480, Bitmap.Config.RGB_565);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
