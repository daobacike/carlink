package com.chuck.carlink.usbmuxd.usb;

import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lidechen on 2/25/17.
 */

public class USBHelper implements IConmunicateHelper {

    private static final String TAG = "USBHelper";

    private static final int RECIVE_BUF_SIZE = 1024 * 10;
    private static final int SEND_BUF_SIZE = 1024 * 10;

    private Context mContext;

    private ParcelFileDescriptor mFileDescriptor;

    private FileInputStream mInputStream;
    private FileOutputStream mOutputStream;

    private UsbManager mUsbManager;
    private UsbAccessory mAccessory;
    private byte[] mReciveBuffer;

    //自定义usb状态管理器
    private USBConnStatusManager mUSBConnStatusManager;

    public USBHelper(Context context) {
        mContext = context;
        mUSBConnStatusManager = USBConnStatusManager.getInstance();
        mUsbManager = (UsbManager) mContext.getSystemService(mContext.USB_SERVICE);
    }

    /**
     * accessory模式打开android的 usb设备
     * 如果当前列表有处于accessory模式的句柄则直接打开
     * 如果当前没有则回监听usb插拔，监听到对应事件后检查系统列表
     * @param onUSBConnStatusChanged
     */
    @Override
    public void openAsync(final OnUSBConnStatusChanged onUSBConnStatusChanged) {

        mReciveBuffer = new byte[RECIVE_BUF_SIZE];

        //注册USB连接状态监听
        mUSBConnStatusManager.registOnUSBConnStatusChangedListener(new OnUSBConnStatusChanged() {
            @Override
            public void onUSBConnect(UsbAccessory accessory) {

                openAccessory(accessory);
                if (onUSBConnStatusChanged != null) {
                    onUSBConnStatusChanged.onUSBConnect(accessory);
                }
            }

            @Override
            public void onUSBConnectFailed(UsbAccessory accessory) {

                closeAccessory();
                if (onUSBConnStatusChanged != null) {
                    onUSBConnStatusChanged.onUSBConnectFailed(accessory);
                }
            }

            @Override
            public void onUSBDisconnect(UsbAccessory accessory) {

                closeAccessory();
                if (onUSBConnStatusChanged != null) {
                    onUSBConnStatusChanged.onUSBDisconnect(accessory);
                }
            }
        });

        //检查usb列表 查看是否已经连接accessory设备
        mUSBConnStatusManager.checkUSBDevice();
    }

    /**
     * 通过accessory句柄拿到usb设备的输入输出流
     * @param accessory
     */
    private void openAccessory(UsbAccessory accessory) {

        mFileDescriptor = mUsbManager.openAccessory(accessory);

        if (mFileDescriptor != null) {
            mAccessory = accessory;
            FileDescriptor fd = mFileDescriptor.getFileDescriptor();

            mInputStream = new FileInputStream(fd);
            mOutputStream = new FileOutputStream(fd);

            if (mOnDataTranPrepared != null) {
                Log.d(TAG, "accessory opened DataTranPrepared");
                mOnDataTranPrepared.onDataTranPrepared(mInputStream, mOutputStream);
            }

            Log.d(TAG, "accessory opened");
        } else {
            Log.d(TAG, "accessory open fail");
        }
    }

    private void closeAccessory() {

        //停止监听usb连接状态变化
        mUSBConnStatusManager.unRegistOnUSBConnStatusChangedListener();

        try {
            if (mFileDescriptor != null) {
                synchronized (mFileDescriptor) {
                    mFileDescriptor.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mFileDescriptor = null;
            mAccessory = null;
        }

        if (mInputStream != null) {
            try {
                synchronized (mInputStream) {
                    mInputStream.close();
                    mInputStream = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mOutputStream != null) {
            try {
                synchronized (mOutputStream) {
                    mOutputStream.close();
                    mOutputStream = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "accessory closed ...");
    }

    @Override
    public void close() {

        closeAccessory();
    }

    /**
     * 阻塞读取usb数据
     * @return
     */
    @Override
    public byte[] readSyncFromUSB() {

        if (mInputStream != null) {
            try {
                Log.i(TAG, "read pending...... ");
                int ret = mInputStream.read(mReciveBuffer);
                Log.i(TAG + "usbread", "" + ret);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mReciveBuffer;
    }

    /**
     * 阻塞写入usb数据
     * @param data
     */
    @Override
    public void writeSyncToUSB(byte[] data) {

        if (mOutputStream != null) {
            try {
                //Log.i(TAG, "write start...... " + Arrays.toString(data));
                mOutputStream.write(data);
                mOutputStream.flush();
                //Log.i(TAG, "write over......");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void writeSyncToUSB(byte[] data, int off, int len) {
        if (mOutputStream != null) {
            try {
                Log.i(TAG, "write start...... ");
                mOutputStream.write(data, off, len);
                Log.i(TAG, "write over......");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void release() {

    }

    private OnDataTranPrepared mOnDataTranPrepared = null;

    public void setOnDataTranPrepared(OnDataTranPrepared onDataTranPrepared) {
        mOnDataTranPrepared = onDataTranPrepared;
    }

    /**
     * 用于监听是否可以开启数据收发线程(如果拿到usb读写流则回调此接口)
     */
    public interface OnDataTranPrepared {
        void onDataTranPrepared(FileInputStream inputStream, FileOutputStream outputStream);
    }
}
