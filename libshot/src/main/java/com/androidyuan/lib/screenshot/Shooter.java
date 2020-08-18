package com.androidyuan.lib.screenshot;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;

/**
 * Created by wei on 16-12-1.
 * <p>
 * Remind:
 * Run this class after you got record permission.
 */
public class Shooter {

    public static boolean hasPermission;

    private final SoftReference<Context> mRefContext;
    private ImageReader mImageReader;

    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;

    private String mLocalUrl = "";

    private OnShotListener mOnShotListener;
    private int mHeight;
    private int mWidth;

    //using a default path.
    private String getSavedPath() {
        if (TextUtils.isEmpty(mLocalUrl)) {
            mLocalUrl = getContext().getExternalFilesDir("screenshot").getAbsoluteFile() + "/"
                    + SystemClock.currentThreadTimeMillis() + ".png";
        }
        return mLocalUrl;
    }


    public Shooter(Context context, int reqCode, Intent data) {
        this.mRefContext = new SoftReference<>(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            mMediaProjection = getMediaProjectionManager().getMediaProjection(reqCode, data);

            WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display mDisplay = window.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            mDisplay.getRealMetrics(metrics);
            mWidth = metrics.widthPixels;//size.x;
            mHeight = metrics.heightPixels;//size.y;

            mImageReader = ImageReader.newInstance(
                    mWidth,
                    mHeight,
                    PixelFormat.RGBA_8888,//this is necessary to equal buffer format in #copyPixelsFromBuffer.
                    1);
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void virtualDisplay() {

        mVirtualDisplay = mMediaProjection.createVirtualDisplay(
                "screen-mirror",
                mWidth,
                mHeight,
                Resources.getSystem().getDisplayMetrics().densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null
        );

    }

    /**
     * @param onShotListener
     * @param savedPath
     */
    public void startScreenShot(String savedPath, OnShotListener onShotListener) {
        mLocalUrl = savedPath;
        startScreenShot(onShotListener);
    }


    /**
     * This method will using {@link #getSavedPath} to save.
     * @param onShotListener
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void startScreenShot(OnShotListener onShotListener) {
        hasPermission = true;
        mOnShotListener = onShotListener;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            virtualDisplay();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Image image = mImageReader.acquireLatestImage();
                                        new SaveTask().doInBackground(image);
                                    }
                                },
                    800);
            //this is a delay due to that record screen permission dialog has not dismissed on some devices cause take dialog graphic in screenshot
            //.@see<a href="https://github.com/weizongwei5/AndroidScreenShot_SysApi/issues/4">issues</a>
        }

    }


    public class SaveTask extends AsyncTask<Image, Void, Bitmap> {

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Bitmap doInBackground(Image... params) {
            if (params == null || params.length < 1 || params[0] == null) {
                return null;
            }

            Image image = params[0];

            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            //每个像素的间距
            int pixelStride = planes[0].getPixelStride();
            //总的间距
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height,
                    Bitmap.Config.ARGB_8888);//even though ARGB8888 will consume more memory,it has better compatibility on device.
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            image.close();
            File fileImage = null;
            if (bitmap != null) {
                try {
                    fileImage = new File(getSavedPath());

                    if (!fileImage.exists()) {
                        fileImage.createNewFile();
                    }
                    FileOutputStream out = new FileOutputStream(fileImage);
                    if (out != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                        out.flush();
                        out.close();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    if (mOnShotListener != null) mOnShotListener.onError();
                    release();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    if (mOnShotListener != null) mOnShotListener.onError();

                    release();
                    return null;
                }
            }

            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }

            if (mVirtualDisplay != null) {
                mVirtualDisplay.release();
            }
            if (mMediaProjection != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mMediaProjection.stop();
                }
            }

            if (mOnShotListener != null) {
                mOnShotListener.onFinish(getSavedPath());
            }

            return null;
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }
    }

    public void release(){
        if (mVirtualDisplay != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mVirtualDisplay.release();
            }
        }
        if (mMediaProjection != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mMediaProjection.stop();
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private MediaProjectionManager getMediaProjectionManager() {

        return (MediaProjectionManager) getContext().getSystemService(
                Context.MEDIA_PROJECTION_SERVICE);
    }

    private Context getContext() {
        return mRefContext.get();
    }

    public interface OnShotListener {
        void onFinish(String path);
        void onError();
    }
}
