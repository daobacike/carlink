package com.chuck.carlink;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;

import  com.chuck.carlink.autolink.Defines;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    CarLinkService.CarLinkBinder  mCarLinkBinder;
    MediaProjectionManager mMediaProjectionManager;
    MediaProjection       mMediaProjection;
    private static final int MSG_COUNT_DOWN = 5;
    private static final int MSG_ENCODE_ERR = 4;
    private static final int MSG_NEW_FRAME = 1;
    private static final int MSG_REQUEST_PERMISSION = 3;
    private static final int MSG_STATUS_CHANGE = 2;

    private int mCountDown = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent  intent = new Intent();
        intent.setAction(CarLinkService.ACTION_STARTSERVICE);
        intent.setClass(this, CarLinkService.class);
        startService(intent);
        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);

        //mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        //startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        Log.d(TAG, "end of create");
    }


    private final ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCarLinkBinder = ((CarLinkService.CarLinkBinder)service);
            mCarLinkBinder.registListener(mServiceListener);
            mCarLinkBinder.checkAccessoryConfigue();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            finish();
        }
    };

    public Handler mUIHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_COUNT_DOWN:
                    mCountDown = mCountDown - 1;
                    if (mCountDown > 0) {
                        mUIHandle.sendMessageDelayed(mUIHandle.obtainMessage(MSG_COUNT_DOWN), 1000);
                        return;
                    }
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    private final CarLinkService.Listener  mServiceListener = new CarLinkService.Listener() {
        @Override
        public void onEncodeErr() {

        }

        @Override
        public void onFrameOut(int frameIn, int frameOut) {
            Message msg = mUIHandle.obtainMessage(MSG_NEW_FRAME);
            Bundle bundle = new Bundle();
            bundle.putInt("FRAME_IN", frameIn);
            bundle.putInt("FRAME_OUT", frameOut);
            msg.setData(bundle);
            mUIHandle.sendMessage(msg);
        }

        @Override
        public void onLoseAccessory() {
            mCarLinkBinder.unregistListener(mServiceListener);
            unbindService(mServiceConn);
            finish();
        }

        @Override
        public void onStatusChanged(int status) {
            Message  msg =  mUIHandle.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("STATUS", status);
            msg.setData(bundle);
            mUIHandle.sendMessage(msg);
        }

        @Override
        public void requestPermission(Intent intent, int id) {
            switch (id) {
                case Defines.REQUEST_CODE_CAPTURE:
                    startActivityForResult(intent, id);
                    break;
                case Defines.REQUEST_CODE_BT:
                    startActivity(intent);
                    break;
                case Defines.REQUEST_CODE_ROTATE:
                    startActivity(intent);
                    break;
                default:
                    moveTaskToBack(true);
            }
        }

        @Override
        public void requestUpdate(Bundle bundle) {

        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (mCarLinkBinder != null) {
           mCarLinkBinder.onActivityResult(int requestCode, int resultCode, Intent data);
       }
       moveTaskToBack(true);
    }





    @Override
    protected void onRestart() {
        super.onRestart();
        moveTaskToBack(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //moveTaskToBack(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConn);
        super.onDestroy();
    }
}
