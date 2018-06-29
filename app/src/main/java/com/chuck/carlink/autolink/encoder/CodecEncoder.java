package com.chuck.carlink.autolink.encoder;

import android.content.Intent;
import android.os.Bundle;

import com.chuck.carlink.autolink.IControllerModule;
import com.chuck.carlink.autolink.IEncoderModule;
import com.chuck.carlink.autolink.VideoPackage;
import com.chuck.carlink.screencapture.VideoEncoder;

/**
 * Created by chuck.liuzhaopeng on 2018/6/29.
 */

public class CodecEncoder implements IEncoderModule, IControllerModule{


    @Override
    public VideoEncoder checkVideoSupport() {
        return null;
    }

    @Override
    public Bundle getConfig() {
        return null;
    }

    @Override
    public int getFrameData(VideoPackage videoPackage, int i, int i2) {
        return 0;
    }

    @Override
    public void registerListener(IEncoderModule.Listener listener) {

    }

    @Override
    public int resetEncode() {
        return 0;
    }

    @Override
    public void unregisetListener(IEncoderModule.Listener listener) {

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
