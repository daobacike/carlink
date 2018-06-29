package com.chuck.carlink.autolink.encoder.eglutils;

/**
 * Created by chuck.liuzhaopeng on 2018/6/29.
 */

public class OffScreenSurface extends EglSurfaceBase{
    public OffScreenSurface(EglCore eglBase, int width, int height) {
        super(eglBase);
        createOffscreenSurface(width, height);
        makeCurrent();
    }

    public void release() {
        releaseEglSurface();
    }

}
