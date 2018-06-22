package com.chuck.carlink.usbmuxd.usb;

import android.hardware.usb.UsbAccessory;

/**
 * Created by lidechen on 2/25/17.
 */

public interface OnUSBConnStatusChanged {
    /** USB连接上 并且连接成功 */
    void onUSBConnect(UsbAccessory accessory);
    /** USB连接上 但是连接失败*/
    void onUSBConnectFailed(UsbAccessory accessory);
    /** USB拔出 */
    void onUSBDisconnect(UsbAccessory accessory);
}
