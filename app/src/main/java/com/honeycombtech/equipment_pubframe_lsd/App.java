package com.honeycombtech.equipment_pubframe_lsd;

import android.app.Application;
import android.content.Context;


/**
 * Created by zhoujr on 20-4-5.
 */

public class App extends Application {

    private static Context sInstance;

    private static String token = "";

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        // TODO:暂时没空适配高版本
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//        }

        // 初始化内存分析工具
//        if (!LeakCanary.isInAnalyzerProcess(this)) {
//            LeakCanary.install(this);
//        }

    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        App.token = token;
    }

    public static Context getContext() {
        return sInstance;
    }
}