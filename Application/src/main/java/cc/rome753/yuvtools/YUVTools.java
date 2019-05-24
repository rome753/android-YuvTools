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

public class YUVTools {

    public static void i420ToNv21(byte[] src, byte[] dest, int w, int h) {
        int pos = w * h;
        int u = pos;
        int v = pos + (pos >> 2);
        System.arraycopy(src, 0, dest, 0, pos);
        while(pos < src.length) {
            dest[pos++] = src[v++];
            dest[pos++] = src[u++];
        }
    }

    public static void yv12ToNv21(byte[] src, byte[] dest, int w, int h) {
        int pos = w * h;
        int v = pos;
        int u = pos + (pos >> 2);
        System.arraycopy(src, 0, dest, 0, pos);
        while(pos < src.length) {
            dest[pos++] = src[v++];
            dest[pos++] = src[u++];
        }
    }

    public static void nv12ToNv21(byte[] src, byte[] dest, int w, int h) {
        int pos = w * h;
        System.arraycopy(src, 0, dest, 0, pos);
        for(; pos < src.length; pos += 2) {
            dest[pos] = src[pos+1];
            dest[pos+1] = src[pos];
        }
    }

    public static Bitmap nv21ToBitmap(byte[] data, int w, int h) {
        final YuvImage image = new YuvImage(data, ImageFormat.NV21, w, h, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
        if(!image.compressToJpeg(new Rect(0, 0, w, h), 100, os)){
            return null;
        }
        byte[] tmp = os.toByteArray();
        return BitmapFactory.decodeByteArray(tmp, 0,tmp.length);
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


//    public static Bitmap imageReaderToBitmap(ImageReader reader, int w, int h) {
//        try (Image image = reader.acquireNextImage()) {
//            Image.Plane[] planes = image.getPlanes();
//            int[] rgb = planesToColors(planes, h);
//            return Bitmap.createBitmap(rgb, w, h, Bitmap.Config.RGB_565);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static Bitmap imageReaderToBitmap(ImageReader reader, int w, int h) {
        try (Image image = reader.acquireNextImage()) {
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer yPlane = planes[0].getBuffer();
            ByteBuffer uPlane = planes[1].getBuffer();
            ByteBuffer vPlane = planes[2].getBuffer();
            int y = yPlane.remaining();
            int u = uPlane.remaining();
            int v = vPlane.remaining();
            byte[] bytes = new byte[y + u + v];
            yPlane.get(bytes, 0, y);
            vPlane.get(bytes, y, v);
            uPlane.get(bytes, y + v, u);
//            bytes = yv12ToNv21(bytes, w, h);
            return nv21ToBitmap(bytes, w, h);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
