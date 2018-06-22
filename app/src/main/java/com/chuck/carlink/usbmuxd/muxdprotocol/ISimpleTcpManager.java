package com.chuck.carlink.usbmuxd.muxdprotocol;

/**
 * Created by lidechen on 2/25/17.
 */

public interface ISimpleTcpManager {

    /** 读入tcp数据 */
    int readTcpData(byte[] data, int realLen);

    /** 写入未封装tcp的数据 */
    void writeRawData(byte[] data, int realLen);
}
