package cc.rome753.yuvtools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.media.ImageReader;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class YUVTools {
    
    public static void rotateP(byte[] data, byte[] rotated, int w, int h, int rotation) {
        switch (rotation) {
            case 0:
                break;
            case 90:
                rotateP90(data, rotated, w, h);
                break;
            case 180:
                rotateP180(data, rotated, w, h);
                break;
            case 270:
                rotateP270(data, rotated, w, h);
                break;
        }
    }

    public static void rotateSP(byte[] data, byte[] rotated, int w, int h, int rotation) {
        switch (rotation) {
            case 0:
                break;
            case 90:
                rotateSP90(data, rotated, w, h);
                break;
            case 180:
                rotateSP180(data, rotated, w, h);
                break;
            case 270:
                rotateSP270(data, rotated, w, h);
                break;
        }
    }
    
    // NV21或NV12顺时针旋转90度
    public static void rotateSP90(byte[] src, byte[] dest, int w, int h) {
        int pos = 0;
        int k = 0;
        for (int i = 0; i <= w - 1; i++) {
            for (int j = h - 1; j >= 0; j--) {
                dest[k++] = src[j * w + i];
            }
        }

        pos = w * h;
        for (int i = 0; i <= w - 2; i += 2) {
            for (int j = h / 2 - 1; j >= 0; j--) {
                dest[k++] = src[pos + j * w + i];
                dest[k++] = src[pos + j * w + i + 1];
            }
        }
    }

    // NV21或NV12顺时针旋转270度
    public static void rotateSP270(byte[] src, byte[] dest, int w, int h) {
        int pos = 0;
        int k = 0;
        for (int i = w - 1; i >= 0; i--) {
            for (int j = 0; j <= h - 1; j++) {
                dest[k++] = src[j * w + i];
            }
        }

        pos = w * h;
        for (int i = w - 2; i >= 0; i -= 2) {
            for (int j = 0; j <= h / 2 - 1; j++) {
                dest[k++] = src[pos + j * w + i];
                dest[k++] = src[pos + j * w + i + 1];
            }
        }
    }

    // NV21或NV12顺时针旋转180度
    public static void rotateSP180(byte[] src, byte[] dest, int w, int h) {
        int pos = 0;
        int k = w * h - 1;
        while (k >= 0) {
            dest[pos++] = src[k--];
        }

        k = src.length - 2;
        while (pos < dest.length) {
            dest[pos++] = src[k];
            dest[pos++] = src[k + 1];
            k -= 2;
        }
    }

    // I420或YV12顺时针旋转90度
    public static void rotateP90(byte[] src, byte[] dest, int w, int h) {
        int pos = 0;
        //旋转Y
        int k = 0;
        for (int i = 0; i < w; i++) {
            for (int j = h - 1; j >= 0; j--) {
                dest[k++] = src[j * w + i];
            }
        }
        //旋转U
        pos = w * h;
        for (int i = 0; i < w / 2; i++) {
            for (int j = h / 2 - 1; j >= 0; j--) {
                dest[k++] = src[pos + j * w / 2 + i];
            }
        }

        //旋转V
        pos = w * h * 5 / 4;
        for (int i = 0; i < w / 2; i++) {
            for (int j = h / 2 - 1; j >= 0; j--) {
                dest[k++] = src[pos + j * w / 2 + i];
            }
        }
    }

    // I420或YV12顺时针旋转270度
    public static void rotateP270(byte[] src, byte[] dest, int w, int h) {
        int pos = 0;
        //旋转Y
        int k = 0;
        for (int i = w - 1; i >= 0; i--) {
            for (int j = 0; j < h; j++) {
                dest[k++] = src[j * w + i];
            }
        }
        //旋转U
        pos = w * h;
        for (int i = w / 2 - 1; i >= 0; i--) {
            for (int j = 0; j < h / 2; j++) {
                dest[k++] = src[pos + j * w / 2 + i];
            }
        }

        //旋转V
        pos = w * h * 5 / 4;
        for (int i = w / 2 - 1; i >= 0; i--) {
            for (int j = 0; j < h / 2; j++) {
                dest[k++] = src[pos + j * w / 2 + i];
            }
        }
    }

    // I420或YV12顺时针旋转180度
    public static void rotateP180(byte[] src, byte[] dest, int w, int h) {
        int pos = 0;
        int k = w * h - 1;
        while (k >= 0) {
            dest[pos++] = src[k--];
        }

        k = w * h * 5 / 4;
        while (k >= w * h) {
            dest[pos++] = src[k--];
        }

        k = src.length - 1;
        while (pos < dest.length) {
            dest[pos++] = src[k--];
        }
    }

    public static void i420ToNv21(byte[] src, byte[] dest, int w, int h) {
        int pos = w * h;
        int u = pos;
        int v = pos + (pos >> 2);
        System.arraycopy(src, 0, dest, 0, pos);
        while (pos < src.length) {
            dest[pos++] = src[v++];
            dest[pos++] = src[u++];
        }
    }

    public static void yv12ToNv21(byte[] src, byte[] dest, int w, int h) {
        int pos = w * h;
        int v = pos;
        int u = pos + (pos >> 2);
        System.arraycopy(src, 0, dest, 0, pos);
        while (pos < src.length) {
            dest[pos++] = src[v++];
            dest[pos++] = src[u++];
        }
    }

    public static void nv21ToNv12(byte[] src, byte[] dest, int w, int h) {
        nv12ToNv21(src, dest, w, h);
    }

    public static void nv12ToNv21(byte[] src, byte[] dest, int w, int h) {
        int pos = w * h;
        System.arraycopy(src, 0, dest, 0, pos);
        for (; pos < src.length; pos += 2) {
            dest[pos] = src[pos + 1];
            dest[pos + 1] = src[pos];
        }
    }

    public static Bitmap nv21ToBitmap(byte[] data, int w, int h) {
        final YuvImage image = new YuvImage(data, ImageFormat.NV21, w, h, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
        if (image.compressToJpeg(new Rect(0, 0, w, h), 100, os)) {
            byte[] tmp = os.toByteArray();
            return BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
        }
        return null;
    }


    public static int[] planesToColors(Image.Plane[] planes, int height) {
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
                if (uvPos >= uvCapacity - 1)
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

    public static byte[] getImageBytes(Image.Plane[] planes) {
        int len = 0;
        for (Image.Plane plane : planes) {
            len += plane.getBuffer().remaining();
        }
        byte[] bytes = new byte[len];
        int off = 0;
        for (Image.Plane plane : planes) {
            ByteBuffer buffer = plane.getBuffer();
            int remain = buffer.remaining();
            buffer.get(bytes, off, remain);
            off += remain;
        }
        return bytes;
    }

    static {
        System.loadLibrary("yuv420");
    }

    public static native void i420ToNv21cpp(byte[] src, byte[] dest, int width, int height);

    public static native void yv12ToNv21cpp(byte[] src, byte[] dest, int width, int height);

    public static native void nv12ToNv21cpp(byte[] src, byte[] dest, int width, int height);
}
