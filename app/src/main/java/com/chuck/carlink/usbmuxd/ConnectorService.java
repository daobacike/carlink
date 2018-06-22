package com.chuck.carlink.usbmuxd;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.chuck.carlink.IRemoteConnector;
import com.chuck.carlink.usbmuxd.muxdprotocol.DataEngine;

/**
 * 连接服务
 * 在独立线程中使用
 * Created by lidechen on 3/24/17.
 */

public class ConnectorService extends Service {

    private static final String TAG = "ConnectorService";

    public ConnectorService(){
        super();
    }

    IRemoteConnector.Stub mRemoteStud = new IRemoteConnector.Stub(){

        @Override
        public int openUSBAsync(int param) throws RemoteException {

            if(mSimpleTcpWrapper != null){
                boolean reset;
                if(param == 0){
                    reset = false;
                }else {
                    reset = true;
                }
                mSimpleTcpWrapper.openUSBAsync(reset);

                return 0;
            }else {
                return -1;
            }
        }

        @Override
        public int closeUSBAsync(int param) throws RemoteException {

            if(mSimpleTcpWrapper != null) {
                mSimpleTcpWrapper.closeUSB();
                return 0;
            }else{
                return -1;
            }
        }

        @Override
        public ParcelFileDescriptor getFileDescriptor(int port) throws RemoteException {
            return null;
        }
    };

    protected DataEngine mSimpleTcpWrapper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mRemoteStud;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSimpleTcpWrapper = new DataEngine(ConnectorService.this);
        setupLocalSocketClients();
        mSimpleTcpWrapper.start();
    }

    protected void setupLocalSocketClients(){

        if(Config.DEBUG){
            Log.i(TAG, "setupLocalSocketClients");
        }
    }
}
