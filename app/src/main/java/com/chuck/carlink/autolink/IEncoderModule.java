package com.chuck.carlink.autolink;

import android.os.Bundle;

import com.chuck.carlink.screencapture.VideoEncoder;

/**
 * Created by chuck.liuzhaopeng on 2018/6/28.
 */

public interface IEncoderModule {
    public interface  Listener {
        void onError();
        void onResetEncode(int n);
    }

    VideoEncoder checkVideoSupport();
    Bundle getConfig();

    int getFrameData(VideoPackage  videoPackage, int i, int i2);

    void registerListener(Listener  listener);

    int resetEncode();

    void unregisetListener(Listener  listener);
}
