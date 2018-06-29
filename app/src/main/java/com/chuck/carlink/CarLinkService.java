package com.chuck.carlink;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.chuck.carlink.autolink.AdapterIntegration;
import com.chuck.carlink.autolink.RenderAdapter;
import com.chuck.carlink.autolink.TransportAdapter;
import com.chuck.carlink.autolink.transport.UsbTransport;

import java.lang.ref.WeakReference;

public class CarLinkService extends Service {

    private CarLinkBinder mBinder;
    public static final String ACTION_PLAYPAUSE = "com.chuck.carlink.playpause";
    public static final String ACTION_STARTSERVICE = "com.chuck.carlink.startservice";
    public static final String ACTION_STOPSERVICE = "com.chuck.carlink.stopservice";
    private static final int MSG_FORCE_LANDSCAPE = 1;
    private static final int MSG_OPENBT_FAIL = 5;
    private static final int MSG_PROJECT_PAUSE = 3;
    private static final int MSG_PROJECT_START = 2;
    private static final int MSG_PROJECT_STOP = 4;
    private static final int NOTIFICATION_ID = 412;
    private static final String TAG = "CarLinkService";
    private Listener mListener;
    private ServiceHandler mHandler;
    private AdapterIntegration  mAdapterInte;
    private TransportAdapter mTransportAdapter;

    public CarLinkService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new CarLinkBinder();
        mHandler = new ServiceHandler(this);
        mAdapterInte = new AdapterIntegration();

        mTransportAdapter = TransportAdapter.getInstance();
        mAdapterInte.mAccessoryAdatper = mTransportAdapter;
        UsbTransport usbTransport = new UsbTransport(getApplicationContext());
        mTransportAdapter.buildTransportModule(usbTransport);
        mTransportAdapter.buildControlModule(usbTransport);

        RenderAdapter renderAdapter = RenderAdapter.getInstance();
        mAdapterInte.mRenderAdapter = renderAdapter;
        

    }

    public class CarLinkBinder extends Binder {
        public  CarLinkBinder(){}

        public int ForceLandscape(boolean paramBoolean) {
            return  CarLinkService.this.ForceLandscape(paramBoolean);
        }

        public void checkAccessoryConfigue() {

        }

        public int getStatus() {
            return 0;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {

        }

        public int start() {
            return 0;
        }

        public int stop() {
            return 0;
        }

        public void registListener(Listener  listener) {
            mListener = listener;
        }

        public void unregistListener(Listener  listener) {
            if (mListener == listener) {
                mListener = null;
            }
        }
    }




    public interface Listener{
        void  onEncodeErr();
        void onFrameOut(int i,int i2);
        void onLoseAccessory();
        void onStatusChanged(int i);
        void requestPermission(Intent intent, int i);
        void requestUpdate(Bundle bundle);
    }

    private int ForceLandscape(Boolean paramBoolean) {
        Object localObject;
        //if ((Build.VERSION.SDK_INT > 23) && (Build.BRAND.equals("Xiaomi")) && )

        return 0;
    }


    static class ServiceHandler extends Handler {
        WeakReference<CarLinkService>  mWeakReference;

        public ServiceHandler(CarLinkService  service) {
            mWeakReference = new WeakReference<CarLinkService>(service);
        }


        @Override
        public void handleMessage(Message msg) {
            CarLinkService mCarlinkService =  mWeakReference.get();
            if (mCarlinkService != null) {
                Bundle bundle = msg.getData();
                switch (msg.what){
                    case MSG_FORCE_LANDSCAPE:
                        if (bundle.getByte("ForceLandscape") == (byte)1) {
                            mCarlinkService.ForceLandscape(true);
                        } else {
                            mCarlinkService.ForceLandscape(false);
                        }
                        break;
                    case MSG_PROJECT_START:
                        //mCarlinkService.updateNotification();
                        break;
                    case MSG_PROJECT_PAUSE:
                        //mCarlinkService.updateNotification();
                        break;
                    case MSG_PROJECT_STOP:
                        //mCarlinkService.showErrorDialog((String) msg.obj);
                        break;
                    case MSG_OPENBT_FAIL:
                        //mAutolinkService.showErrorToast((String) msg.obj);
                        //mAutolinkService.mListener.requestPermission(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), Defines.REQUEST_CODE_BT);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
