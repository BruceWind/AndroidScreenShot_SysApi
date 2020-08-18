package com.androidyuan.lib.screenshot;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by wei on 16-9-18.
 * <p>
 * There is totally transparent activity,only has a record permission dialog.
 * If you want to screenshot on other applications,might you need to use this activity to take screenshot.
 */
public class ScreenShotActivity extends Activity {

    public static final String KEY_PATH = "path";
    public static final String KEY_DELAY = "delay_time";

    public static final int REQUEST_MEDIA_PROJECTION = 0x2304;
    public static final String ACTION_SHOTER = "androidyuan.shooter";

    private String savedPath;
    private long delay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        setTheme(android.R.style.Theme_Dialog);//this line cause a problem, activity background wasn't transparent but black.
        super.onCreate(savedInstanceState);

        //here is a transparent activity,and previous activity will not be called Activity#onPause().
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setDimAmount(0f);
        savedPath = getIntent().getStringExtra(KEY_PATH);
        delay = getIntent().getLongExtra(KEY_DELAY,0);
        requestScreenShotPermission();
    }

    public static Intent createIntent(Context context, String path,long delay) {
        Intent intent = new Intent(context, ScreenShotActivity.class);
        intent.putExtra(KEY_PATH, path);
        intent.putExtra(KEY_DELAY, delay);
        return intent;
    }


    public void requestScreenShotPermission() {
        if (Build.VERSION.SDK_INT >= 21) {
            startActivityForResult(createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        }
    }

    private Intent createScreenCaptureIntent() {
        //here used media_projection instead of Context.MEDIA_PROJECTION_SERVICE to  make it successfully build on low api.
        return ((MediaProjectionManager) getSystemService("media_projection")).createScreenCaptureIntent();
    }


    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION: {
                if (resultCode == RESULT_OK && data != null) {

                    getWindow().getDecorView().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Shooter shooter = new Shooter(ScreenShotActivity.this, resultCode, data);
                            shooter.startScreenShot(savedPath, new Shooter.OnShotListener() {
                                @Override
                                public void onFinish(String path) {
                                    Intent intent = new Intent();
                                    intent.setData(Uri.parse(path));
                                    setResult(RESULT_OK, intent);
                                    finish(); // don't forget finish activity
                                }

                                @Override
                                public void onError() {
                                    setResult(RESULT_CANCELED);
                                    finish();
                                }
                            });
                        }
                    },delay);
                } else if (resultCode == RESULT_CANCELED) {
                    setResult(RESULT_CANCELED);
                    finish();
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        }
    }


}