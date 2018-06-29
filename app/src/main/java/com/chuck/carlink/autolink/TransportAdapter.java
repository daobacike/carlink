package com.chuck.carlink.autolink;

/**
 * Created by chuck.liuzhaopeng on 2018/6/28.
 */

public class TransportAdapter {
    private static IControllerModule mControl;
    private static ITransportModule mTransport;
    private static TransportAdapter mTransportAdapter;

    public static TransportAdapter getInstance() {
        if (mTransportAdapter == null) {
            mTransportAdapter = new TransportAdapter();
        }
        return mTransportAdapter;
    }

    private TransportAdapter(){}


    public void buildTransportModule(ITransportModule module) {
        if (mTransport == null) {
            mTransport = module;
        }
    }

    public void buildControlModule(IControllerModule module) {
        if (mControl == null) {
            mControl = module;
        }
    }

    public IControllerModule getIControllerModule() {
        return mControl;
    }

    public ITransportModule getITransportModule() {
        return mTransport;
    }
}
