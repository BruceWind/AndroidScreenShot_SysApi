package com.androidyuan.androidscreenshot_sysapi;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;


/**
 * Created by wei on 18-4-24.
 */

public class ScreenShotService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent("androidyuan.shotter");
                // 这个不是必需的
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        }, 3200);//这里留足够的时间切换到别的app
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
