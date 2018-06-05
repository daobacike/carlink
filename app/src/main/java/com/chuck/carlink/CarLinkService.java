package com.chuck.carlink;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class CarLinkService extends Service {

    private CarLinkBinder mBinder;

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

    }

    public class CarLinkBinder extends Binder {
        public  CarLinkBinder(){}




    }


}
