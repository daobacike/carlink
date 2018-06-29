package com.chuck.carlink.autolink;

/**
 * Created by chuck.liuzhaopeng on 2018/6/28.
 */

public class EncoderAdapter {
    private static IControllerModule mController;
    private static IEncoderModule mEncoder;
    private static EncoderAdapter mEncoderAdapter;

    private EncoderAdapter() {
    }

    public static EncoderAdapter getInstance() {
        if (mEncoderAdapter == null) {
            mEncoderAdapter = new EncoderAdapter();
        }
        return mEncoderAdapter;
    }

    public void buildModuleController(IControllerModule controller) {
        if (mController == null) {
            mController = controller;
        }
    }

    public void buildEncoderModule(IEncoderModule module) {
        if (mEncoder == null) {
            mEncoder = module;
        }
    }

    public IControllerModule getIModuleController() {
        return mController;
    }

    public IEncoderModule getIEncoderModule() {
        return mEncoder;
    }

}
