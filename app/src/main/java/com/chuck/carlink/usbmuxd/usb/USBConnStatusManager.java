package com.chuck.carlink.usbmuxd.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.chuck.carlink.application.MyApplication;

/**
 * 单例全局USB连接管理器
 * Created by lidechen on 2/25/17.
 */

public class USBConnStatusManager{

    private static final String TAG = "USBConnStatusManager";
    private static final String ACTION_USB_PERMISSION = "org.ammlab.android.app.helloadk.action.USB_PERMISSION";

    /** 设备已连接 且数据通道可用 */
    public static final int STATUS_CONN_OK = 0;
    /** 设备已连接 数据通道错误 */
    public static final int STATUS_CONN_ERR = 1;
    /** 设备未连接 */
    public static final int STATUS_DISCONN = 2;

    private static int sCurStatus = STATUS_DISCONN;

    private BroadcastReceiver mUsbReceiver;

    private PendingIntent mPermissionIntent;
    private boolean mPermissionRequestPending = false;

    private OnUSBConnStatusChanged mOnUSBConnStatusChanged = null;

    //系统UsbManager
    private UsbManager mUsbManager;
    //当前实例管理的accessory
    private UsbAccessory mUsbAccessory;

    private static USBConnStatusManager sInstance = new USBConnStatusManager();
    private USBConnStatusManager(){

        mUsbReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                Log.i(TAG, "receive usb connect broadcast:" + action);

                if (ACTION_USB_PERMISSION.equals(action)) {
                    synchronized (this) {
                        //UsbAccessory accessory = UsbManager.getAccessory(intent);
                        UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);

                        //获取accessory句柄成功
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            Log.d(TAG, "prepare to open usb stream");

                            sCurStatus = STATUS_CONN_OK;
                            mUsbAccessory = accessory;

                            //synchronized (USBConnStatusManager.class) {
                                if (mOnUSBConnStatusChanged != null) {
                                    mOnUSBConnStatusChanged.onUSBConnect(accessory);
                                }
                            //}
                            //openAccessory(accessory);
                        } else {
                            Log.d(TAG, "permission denied for accessory " + accessory);

                            sCurStatus = STATUS_CONN_ERR;
                            mUsbAccessory = null;

                            //synchronized (USBConnStatusManager.class) {
                                if (mOnUSBConnStatusChanged != null) {
                                    mOnUSBConnStatusChanged.onUSBConnectFailed(accessory);
                                }
                            //}
                        }
                    }
                } else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
                    UsbAccessory accessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                    //if (accessory != null && accessory.equals(mAccessory)) {

                       //检测到usb断开
                       Log.d(TAG, "USB_ACCESSORY_DETACHED " + accessory);

                        sCurStatus = STATUS_DISCONN;
                        mUsbAccessory = null;
                        //closeAccessory();
                        //synchronized (USBConnStatusManager.class) {
                            if (mOnUSBConnStatusChanged != null) {
                                mOnUSBConnStatusChanged.onUSBDisconnect(accessory);
                            }
                        //}
                    //}
                }
            }
        };

        //注册使用usb设备的权限
        // Broadcast Intent for myPermission
        mPermissionIntent = PendingIntent.getBroadcast(getContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);

        //注册usb设备插拔消息广播
        // Register Intent myPermission and remove accessory
        IntentFilter filter = new IntentFilter();
        //接收权限信息
        filter.addAction(ACTION_USB_PERMISSION);
        //接收accessory连接事件
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        //接收accessory断开事件
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        getContext().registerReceiver(mUsbReceiver, filter);

        mUsbManager = (UsbManager) getContext().getSystemService(getContext().USB_SERVICE);
    }

    public static USBConnStatusManager getInstance(){
        return sInstance;
    }

    public void registOnUSBConnStatusChangedListener(OnUSBConnStatusChanged onUSBConnStatusChanged){

        synchronized (USBConnStatusManager.class) {
            mOnUSBConnStatusChanged = onUSBConnStatusChanged;
        }
    }

    public void unRegistOnUSBConnStatusChangedListener(){

        //synchronized (USBConnStatusManager.class) {
            mOnUSBConnStatusChanged = null;
        //}
    }

    public int getCurStatus(){
        return sCurStatus;
    }

    public UsbAccessory getUsbAccessory(){
        return mUsbAccessory;
    }

    /**
     * 直接检查usb设备是否连接
     * @return
     */
    public void checkUSBDevice() {

        UsbAccessory[] accessories = mUsbManager.getAccessoryList();

        if(accessories == null){
            Log.i(TAG, "accessories list is null");
            return;
        }

        Log.i(TAG, "accessories length "+accessories.length);

        UsbAccessory accessory = (accessories == null ? null : accessories[0]);
        if (accessory != null) {
            if (mUsbManager.hasPermission(accessory)) {

                sCurStatus = STATUS_CONN_OK;
                mUsbAccessory = accessory;

                //synchronized (USBConnStatusManager.class) {
                    if (mOnUSBConnStatusChanged != null) {
                        mOnUSBConnStatusChanged.onUSBConnect(accessory);
                    }
                //}
            } else {
                //synchronized (mUsbReceiver) {
                    if (!mPermissionRequestPending) {
                        mUsbManager.requestPermission(accessory, mPermissionIntent);
                        mPermissionRequestPending = true;
                    }
                //}

            }
        }
    }

    private Context getContext(){
        //Context context = null;
        //return context;
        return MyApplication.getApplication();
    }
}
