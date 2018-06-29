package com.chuck.carlink.autolink.transport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;

import com.chuck.carlink.autolink.Defines;
import com.chuck.carlink.autolink.IControllerModule;
import com.chuck.carlink.autolink.ITransportModule;

/**
 * Created by chuck.liuzhaopeng on 2018/6/28.
 */

public class UsbTransport implements IControllerModule, ITransportModule {
    private static final String ACTION_USB_PERMISSION = "com.chuck.carlink.action.USB_PERMISSION";
    private static final String TAG = "CarlinkAccessory";
    private UsbAccessory mAccessory = null;
    private Context mContext;
    private IControllerModule.Listener  mControlListener;
    private ParcelFileDescriptor mFileDescriptor;
    private UsbManager mUsbManager;
    private int mStatus;
    private AccessoryReciver mAccessoryReciver;

    public UsbTransport(Context context) {
        mContext = context;
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        mStatus = 0;
        IntentFilter filter = new IntentFilter("android.hardware.usb.action.USB_ACCESSORY_DETACHED");
        filter.addAction(ACTION_USB_PERMISSION);
        mAccessoryReciver = new AccessoryReciver();
        mControlListener.onConnectResult(Defines.AUTOLINK_ERR_UNSUPPORT, null);

    }

    private final class AccessoryReciver extends BroadcastReceiver {
        AccessoryReciver(){}

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            UsbAccessory accessory;
            if (action.equals("android.hardware.usb.action.USB_ACCESSORY_DETACHED")) {
                accessory = (UsbAccessory) intent.getParcelableExtra("accessory");
                if (accessory != null && accessory.equals(mAccessory)) {

                }
            }
        }
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public int read(byte[] bArray, int i, int i2) {
        return 0;
    }

    @Override
    public int write(byte[] bArray, int i, int i2) {
        return 0;
    }

    @Override
    public void registerListener(ITransportModule.Listener listener) {

    }

    @Override
    public void unregisterListener(ITransportModule.Listener listener) {

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


    private int openTransport() {
        if (mAccessory == null) {
            return -1;
        }

        return 0;
    }
}
