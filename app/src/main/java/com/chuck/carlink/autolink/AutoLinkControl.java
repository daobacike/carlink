package com.chuck.carlink.autolink;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by chuck.liuzhaopeng on 2018/6/28.
 */

public class AutoLinkControl {
    private static final String TAG = "AutolinkControl";

    private StateAdapter mStateAdapter;


    public interface Listener {
        void onEncodeErr();

        void onLoseConnect(int i, int i2);

        void onStatusChanged(int i);

        void requestBtPermission();

        void requestLandscape(Byte b);

        void requestPermission(Intent intent, int i);

        void requestUpdate(Bundle bundle);
    }

    private abstract class StateAdapter {
        public static final int TYPE_AUDIO = 24;
        public static final int TYPE_ENCODER = 22;
        public static final int TYPE_PROTOCAL = 23;
        public static final int TYPE_RENDER = 21;
        public static final int TYPE_TRANSPORT = 20;
        public static final int TYPE_USERINPUT = 25;

        public abstract void onConfigureResult(int i, int i2, Bundle bundle);

        public abstract void onConnectResult(int i, int i2, Intent intent, int i3);

        public abstract void onDisconnect(int i);

        public abstract void onInit();

        private StateAdapter() {
        }

        void transState(StateAdapter adapter) {
            mStateAdapter = adapter;
            mStateAdapter.onInit();
        }
    }







}
