package com.chuck.carlink.autolink;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by chuck.liuzhaopeng on 2018/6/28.
 */

public interface IControllerModule {
    public static final int MODULE_STATUS_CONFIGURED = 2;
    public static final int MODULE_STATUS_CONNECTED = 1;
    public static final int MODULE_STATUS_NONE = 0;
    public static final int MODULE_STATUS_WORKING = 3;

    public interface Listener {
        void onConfigureResult(int i, Bundle bundle);

        void onConnectResult(int i, Bundle bundle);

        void onDisconnect();

        void onStartResult(int i);

        void onStopResult(int i);
    }

    void disconnect();

    int getStatus();

    void receiveControlIntent(int i, int i2, Intent intent);

    void registerListener(Listener listener);

    void requestConnect();

    void setConfig(Bundle bundle);

    void start();

    void stop();

    void unregisterListener(Listener listener);


}
