package com.chuck.carlink.autolink;

/**
 * Created by chuck.liuzhaopeng on 2018/6/28.
 */

public class RenderAdapter {
    private static IControllerModule mController;
    private static IRenderModule mRender;
    private static RenderAdapter mRenderAdpter;

    private RenderAdapter() {
    }

    public static RenderAdapter getInstance() {
        if (mRenderAdpter == null) {
            mRenderAdpter = new RenderAdapter();
        }
        return mRenderAdpter;
    }

    public void buildModuleController(IControllerModule controller) {
        if (mController == null) {
            mController = controller;
        }
    }

    public void buildRenderModule(IRenderModule module) {
        if (mRender == null) {
            mRender = module;
        }
    }

    public IControllerModule getIModuleController() {
        return mController;
    }

    public IRenderModule getIRenderModule() {
        return mRender;
    }

}
