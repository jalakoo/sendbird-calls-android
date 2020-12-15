package com.sendbird.calls.demo;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import androidx.multidex.MultiDexApplication;
import com.sendbird.calls.demo.utils.PrefUtils;

public class BaseApplication extends MultiDexApplication { // multidex

    public static final String TAG = "SendBirdCalls";
    public static final String APP_ID = "";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(BaseApplication.TAG, "[BaseApplication] onCreate()");

        initSendBirdCall(PrefUtils.getAppId(getApplicationContext()));
    }

    public boolean initSendBirdCall(String appId) {
        Log.i(BaseApplication.TAG, "[BaseApplication] initSendBirdCall(appId: " + appId + ")");
        Context context = getApplicationContext();

        if (TextUtils.isEmpty(appId)) {
            appId = APP_ID;
        }

        return SendbirdHelper.init(context, appId);
    }
}
