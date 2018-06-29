package com.chuck.carlink.autolink.render;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Surface;

import com.chuck.carlink.autolink.IControllerModule;
import com.chuck.carlink.autolink.IRenderModule;

/**
 * Created by chuck.liuzhaopeng on 2018/6/29.
 */

public class VisualRender implements IRenderModule, IControllerModule{
    private final static String TAG = "VisualRender";
    private Thread mCaptureThread = null;
    private Context mContext;
    private IControllerModule.Listener mControlListener;
    private Handler mHandler;
    private Surface mInputSurface;


    private final class DrawTask  extends EglTask {

    }


    @Override
    public void registerListener(IRenderModule.Listener listener) {

    }

    @Override
    public void setPermissionResult(int i, int i2, Intent intent) {

    }

    @Override
    public void unregisterListener(IRenderModule.Listener listener) {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public void receiveControlIntent(int i, int i2, Intent intent) {

    }

    @Override
    public void registerListener(IControllerModule.Listener listener) {

    }

    @Override
    public void requestConnect() {

    }

    @Override
    public void setConfig(Bundle bundle) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void unregisterListener(IControllerModule.Listener listener) {

    }
}
