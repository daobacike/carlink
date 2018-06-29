package com.chuck.carlink.autolink.encoder.eglutils;

import android.opengl.EGLContext;
import android.util.Log;

import java.lang.ref.ReferenceQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by chuck.liuzhaopeng on 2018/6/29.
 */

public abstract class EglTask implements Runnable{
    private static final int REQUEST_EGL_TASK_NON = 0;
    private static final int REQUEST_EGL_TASK_QUIT = -9;
    private static final int REQUEST_EGL_TASK_RUN = -1;
    private static final int REQUEST_EGL_TASK_START = -8;
    private static final String TAG = "EglTask";

    private EglCore mEglCore = null;
    private OffScreenSurface mEglHolder;
    private boolean mIsRunning = true;
    private final LinkedBlockingQueue<Request> mRequestPool = new LinkedBlockingQueue();
    private final LinkedBlockingDeque<Request> mRequestQueue = new LinkedBlockingDeque();
    private final Object mSync = new Object();

    protected static final class Request {
        int arg1;
        Object arg2;
        int request;

        public Request(int _request, int _arg1, Object _arg2) {
            this.request = _request;
            this.arg1 = _arg1;
            this.arg2 = _arg2;
        }

        public boolean equals(Object o) {
            if (o instanceof Request) {
                return this.request == ((Request) o).request && this.arg1 == ((Request) o).arg1 && this.arg2 == ((Request) o).arg2;
            } else {
                return super.equals(o);
            }
        }
    }

    protected abstract boolean onError(Exception exception);

    protected abstract void onStart();

    protected abstract void onStop();

    protected abstract boolean processRequest(int i, int i2, Object obj);

    public EglTask(EGLContext shared_context, int flags) {
        Log.i(TAG, "shared_context=" + shared_context);
        offer(-8, flags, shared_context);
    }

    @Override
    public void run() {
        Request localRequest = null;
        try {

            localRequest =  mRequestQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (mSync) {
            if ((localRequest.arg2 == null)  || (localRequest.arg2 ))
        }

    }

    private boolean callOnError(Exception e) {
        try {
            return onError(e);
        } catch (Exception e2) {
            Log.e(TAG, "exception occurred in callOnError", e);
            return true;
        }
    }

    protected Request obtain(int request, int arg1, Object arg2) {
        Request req = (Request) this.mRequestPool.poll();
        if (req == null) {
            return new Request(request, arg1, arg2);
        }
        req.request = request;
        req.arg1 = arg1;
        req.arg2 = arg2;
        return req;
    }

    public void offer(int request, int arg1, Object arg2) {
        this.mRequestQueue.offer(obtain(request, arg1, arg2));
    }

    public void offerFirst(int request, int arg1, Object arg2) {
        this.mRequestQueue.offerFirst(obtain(request, arg1, arg2));
    }

    public void queueEvent(Runnable task) {
        if (task != null) {
            this.mRequestQueue.offer(obtain(-1, 0, task));
        }
    }

    public void removeRequest(Request request) {
        do {
        } while (this.mRequestQueue.remove(request));
    }

    public EglCore getEglCore() {
        return this.mEglCore;
    }

    protected void makeCurrent() {
        this.mEglHolder.makeCurrent();
    }

    public void release() {
        this.mRequestQueue.clear();
        synchronized (this.mSync) {
            if (this.mIsRunning) {
                offerFirst(-9, 0, null);
                this.mIsRunning = false;
                try {
                    this.mSync.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public void releaseSelf() {
        this.mRequestQueue.clear();
        synchronized (this.mSync) {
            if (this.mIsRunning) {
                offerFirst(-9, 0, null);
                this.mIsRunning = false;
            }
        }
    }


}
