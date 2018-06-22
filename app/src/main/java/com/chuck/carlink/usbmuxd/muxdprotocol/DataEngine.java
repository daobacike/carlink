package com.chuck.carlink.usbmuxd.muxdprotocol;

import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.util.Log;

import com.chuck.carlink.usbmuxd.Config;
import com.chuck.carlink.usbmuxd.usb.OnUSBConnStatusChanged;
import com.chuck.carlink.usbmuxd.usb.USBHelper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * 数据通信协议封装类，本例中只涉及简单usb层传输，并没有封装上层协议
 * Created by lidechen on 2/25/17.
 */

public class DataEngine implements ISimpleTcpManager {

    private static final String TAG = "DataEngine";

    private Context mContext;

    /**
     * 数据通信对象引用
     */
    private USBHelper mConmunicateHelper;

    private Thread mRecieveThread;
    private ReciveTask mReciveTask;

    /**
     * 强制重启usb标志
     */
    private boolean mForceOpenUSB = false;

    public DataEngine(Context context) {

        mContext = context;

        mConmunicateHelper = new USBHelper(mContext);
    }

    public void start(){

        //数据传输层准备就绪 可以开启数据收发任务
        mConmunicateHelper.setOnDataTranPrepared(new USBHelper.OnDataTranPrepared() {
            @Override
            public void onDataTranPrepared(FileInputStream inputStream, FileOutputStream outputStream) {

                if (Config.DEBUG) {
                    Log.i(TAG, "accessory opened: inputStream " + inputStream + " outputStream " + outputStream);
                }

                //建立从usb读取数据的任务
                mReciveTask = new ReciveTask(inputStream);
                mRecieveThread = new Thread(mReciveTask);
                mRecieveThread.start();
            }
        });
    }

    public void openUSBAsync(boolean reset) {

        if (mForceOpenUSB == false) {

            //状态为已连接且不是从后台进入 直接返回
            if (!reset) {
                return;
            }

            if(Config.DEBUG){
                Log.i(TAG, "openUSBAsync ...");
            }
        }

        mForceOpenUSB = false;


        mConmunicateHelper.openAsync(new OnUSBConnStatusChanged() {
            @Override
            public void onUSBConnect(UsbAccessory accessory) {

            }

            @Override
            public void onUSBConnectFailed(UsbAccessory accessory) {

                Log.i(TAG, "connect state ### onUSBConnectFailed ###");

                mConmunicateHelper.close();

                mForceOpenUSB = true;
            }

            @Override
            public void onUSBDisconnect(UsbAccessory accessory) {

                Log.i(TAG, "connect state ### onUSBDisconnect ###");

                mConmunicateHelper.close();

                mForceOpenUSB = true;
            }
        });
    }

    public void closeUSB() {

        if (mRecieveThread != null) {
            mReadTaskRun = false;
            Thread.State state = mRecieveThread.getState();
            if (state == Thread.State.BLOCKED || state == Thread.State.TIMED_WAITING || state == Thread.State.TIMED_WAITING) {
                mRecieveThread.interrupt();
            }
            mRecieveThread = null;
        }

        mConmunicateHelper.close();
    }

    @Override
    public int readTcpData(byte[] data, int realLen) {

        return 0;
    }

    @Override
    public void writeRawData(byte[] data, int realLen) {
    }

    private byte[] mReciveBuffer;
    private static final int RECIVE_BUF_SIZE = 1024*10;
    private boolean mReadTaskRun = false;

    public class ReciveTask implements Runnable {

        private FileInputStream mInputStream;

        public ReciveTask(FileInputStream inputStream) {

            mInputStream = inputStream;
        }

        @Override
        public void run() {

            mReciveBuffer = new byte[RECIVE_BUF_SIZE];

            mReadTaskRun = true;

            int off = 0;
            int len = 0;

            while (mReadTaskRun) {
                try {

                    if (Config.DEBUG) {
                        Log.i(TAG, "accessory opened start read");
                    }

                    //usb数据接收
                    int ret = mInputStream.read(mReciveBuffer, off, RECIVE_BUF_SIZE - off);

                    //将接收到的数据返回给usb
                    byte[] retBuf = new byte[ret];
                    System.arraycopy(mReciveBuffer, 0, retBuf, 0, ret);
                    mConmunicateHelper.writeSyncToUSB(retBuf);

                    parseRemoteDeviceData(retBuf);

                } catch (IOException e) {

                    if (Config.DEBUG) {
                        Log.i(TAG, "ReciveTask exception :\n" + e.toString());
                    }

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e1) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void parseRemoteDeviceData(byte[] data){

        Log.i(TAG, "feedback "+ Arrays.toString(data));

    }
}
