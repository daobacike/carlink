package com.chuck.carlink.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.chuck.carlink.usbmuxd.ConnectorService;
import com.chuck.carlink.usbmuxd.UsbmuxdManager;

/**
 * Created by chuck.liuzhaopeng on 2018/6/14.
 */

public class MyApplication extends Application {

    private static Context sContext;

    private static MyApplication sAppApplication;

    private UsbmuxdManager mUsbmuxdManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppApplication = this;

        Log.i("AppApplication", "create ... ");

        //1.获取一个UsbmuxdManager实例
        mUsbmuxdManager = UsbmuxdManager.getInstance();
        //2.注册生命周期方法
        mUsbmuxdManager.setOnRemoteServiceConnectedListener(new UsbmuxdManager.OnRemoteServiceConnectedListener() {
            @Override
            public void onConnected() {
                registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityCreated(Activity activity, Bundle bundle) {

                    }

                    @Override
                    public void onActivityStarted(Activity activity) {
                        mUsbmuxdManager.openUsbOnActivityStart();
                    }

                    @Override
                    public void onActivityResumed(Activity activity) {
                    }

                    @Override
                    public void onActivityPaused(Activity activity) {
                    }

                    @Override
                    public void onActivityStopped(Activity activity) {
                        mUsbmuxdManager.closeUsbOnActivityStop();
                    }

                    @Override
                    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

                    }

                    @Override
                    public void onActivityDestroyed(Activity activity) {

                    }
                });

            }

            @Override
            public void onDisconnected() {
                //unregisterActivityLifecycleCallbacks();
            }
        });

        //开启连接服务
        mUsbmuxdManager.startUsbmuxdService(MyApplication.this, ConnectorService.class);
    }

    public static MyApplication getApplication(){
        return sAppApplication;
    }


}
