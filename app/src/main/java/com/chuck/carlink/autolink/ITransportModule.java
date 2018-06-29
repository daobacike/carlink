package com.chuck.carlink.autolink;

/**
 * Created by chuck.liuzhaopeng on 2018/6/28.
 */

public interface ITransportModule {
    public interface Listener {}

    String getVersion();

    int read(byte[]bArray, int i, int i2);
    int write(byte[]bArray, int i, int i2);
    void registerListener(Listener listener);
    void unregisterListener(Listener listener);

}
