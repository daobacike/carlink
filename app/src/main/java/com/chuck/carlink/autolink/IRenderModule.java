package com.chuck.carlink.autolink;

import android.content.Intent;

/**
 * Created by chuck.liuzhaopeng on 2018/6/28.
 */

public interface IRenderModule {
    public interface Listener {
        void onResetRender();
    }

    void registerListener(Listener listener);

    void setPermissionResult(int i, int i2, Intent intent);

    void unregisterListener(Listener listener);

}
