package com.chuck.carlink;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Half;
import android.util.Log;
import android.view.WindowManager;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    CarLinkService.CarLinkBinder  mCarLinkBinder;
    MediaProjectionManager mMediaProjectionManager;
    MediaProjection       mMediaProjection;
    private static final int REQUEST_MEDIA_PROJECTION = 1;
    private Handler mBackGroundHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent  intent = new Intent();
        intent.setClass(this, CarLinkService.class);
        startService(intent);
        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);

        mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        Log.d(TAG, "end of create");
    }


    private final ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCarLinkBinder = ((CarLinkService.CarLinkBinder)service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void createVirtualDisplay() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int widthPixels = dm.widthPixels;
        int heightPixels = dm.heightPixels;
        int dpi = dm.densityDpi;

        Log.d(TAG, " w:" + widthPixels + " h " + heightPixels);
        //mMediaProjection.createVirtualDisplay("CarLink", widthPixels, heightPixels, dpi,
         //       DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, imageReader.getSurface(), null, null);

        Log.d(TAG, "end of createVirtualDisplay");
    }

    private Handler getBackGroundHandler() {
        if (mBackGroundHandler == null) {
            HandlerThread backThread = new HandlerThread("background image thread",
                    Process.THREAD_PRIORITY_BACKGROUND);
            backThread.start();
            mBackGroundHandler = new Handler(backThread.getLooper());
        }
        return mBackGroundHandler;
    }

    private final ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            int numb = reader.getMaxImages();
            Log.d(TAG, " numb :"  +  numb);
            Image image = reader.acquireLatestImage();
            //image.getPlanes();
            Log.d(TAG, "Image available");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Log.d(TAG, "start of onActivityResult  requestCode: " + requestCode + "  " + data  + "  " + resultCode);
       if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (data != null) {
                mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
                if (mMediaProjection == null) {
                    Log.e(TAG, "mMediaProjection is null!!!");
                    return;
                }
                mMediaProjection.registerCallback(new MediaProjection.Callback() {
                    @Override
                    public void onStop() {
                        super.onStop();
                        Log.d(TAG, "stopping");
                    }
                }, null);
                createVirtualDisplay();
            }

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
