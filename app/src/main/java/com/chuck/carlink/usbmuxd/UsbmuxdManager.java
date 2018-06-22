package com.chuck.carlink.usbmuxd;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;

import com.chuck.carlink.IRemoteConnector;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * 上层封装类，用于本应用中usb相关
 * Created by lidechen on 3/24/17.
 */

public class UsbmuxdManager {

    private static final String TAG = "UsbmuxdManager";

    private Context mContext;

    private ServiceConnection mRemoteConn;
    private IRemoteConnector mRemoteConnector;

    private static UsbmuxdManager sUsbmuxdManager = new UsbmuxdManager();
    private UsbmuxdManager(){}

    public static UsbmuxdManager getInstance(){
        return sUsbmuxdManager;
    }

    /**
     * 开启usbmuxd服务
     */
    public void startUsbmuxdService(Context context, Class<? extends ConnectorService> clazz){

        if(Config.DEBUG){
            Log.i(TAG, "startUsbmuxdService");
        }

        mContext = context;

        Intent intent = new Intent(mContext, clazz);
        //Intent intent = new Intent(mContext, TestConnectorService.class);
        //Intent intent = new Intent(mContext, ConnectorService.class);
        mContext.startService(intent);

        mRemoteConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

                mRemoteConnector = IRemoteConnector.Stub.asInterface(iBinder);

                if(Config.DEBUG){
                    Log.i(TAG, "mRemoteConnector bind success");
                }

                if(mOnRemoteServiceConnectedListener != null){
                    mOnRemoteServiceConnectedListener.onConnected();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

                if(Config.DEBUG){
                    Log.i(TAG, "mRemoteConnector bind failed");
                }

                if(mOnRemoteServiceConnectedListener != null){
                    mOnRemoteServiceConnectedListener.onDisconnected();
                }
            }
        };

        Intent intentConnectorService = new Intent(mContext, clazz);
        //Intent intentConnectorService = new Intent(mContext, TestConnectorService.class);
        //Intent intentConnectorService = new Intent(mContext, ConnectorService.class);
        mContext.bindService(intentConnectorService, mRemoteConn, BIND_AUTO_CREATE);
    }

    private OnRemoteServiceConnectedListener mOnRemoteServiceConnectedListener = null;

    public interface OnRemoteServiceConnectedListener{
        void onConnected();
        void onDisconnected();
    }

    public void setOnRemoteServiceConnectedListener(OnRemoteServiceConnectedListener onRemoteServiceConnectedListener){
        mOnRemoteServiceConnectedListener = onRemoteServiceConnectedListener;
    }

    public ParcelFileDescriptor getRemoteFd(int port){
        if(mRemoteConnector != null){
            try {
                return mRemoteConnector.getFileDescriptor(port);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void openUsb(int reset){

        if(Config.DEBUG){
            Log.i(TAG, "openUsb mRemoteConnector "+mRemoteConnector);
        }

        try {
            if(mRemoteConnector != null) {
                mRemoteConnector.openUSBAsync(reset);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void closeUsb(){

        try {
            if(mRemoteConnector != null) {
                mRemoteConnector.closeUSBAsync(1);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //***********************************************************************
    /**
     * 在activity的生命周期方法中调用
     */
    private boolean mIsStoped = true;
    private int mActivityCnt = 1;

    public void openUsbOnActivityStart(){

        if(mIsStoped) {
            mIsStoped = false;
            openUsb(1);
        }
        mActivityCnt++;
    }

    public void closeUsbOnActivityStop(){
        mActivityCnt--;
        if(mActivityCnt == 0) {
            closeUsb();
            mIsStoped = true;
        }
    }
}










