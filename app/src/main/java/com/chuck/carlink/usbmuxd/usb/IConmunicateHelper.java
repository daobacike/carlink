package com.chuck.carlink.usbmuxd.usb;

/**
 * Created by lidechen on 2/25/17.
 */

public interface IConmunicateHelper {
    void openAsync(OnUSBConnStatusChanged onUSBConnStatusChanged);
    void close();

    /** 同步读取usb数据 */
    byte[] readSyncFromUSB();
    /** 同步写入usb数据 */
    void writeSyncToUSB(byte[] data);
    /** 同步写入usb数据 */
    void writeSyncToUSB(byte[] data, int off, int len);

    void release();
}
