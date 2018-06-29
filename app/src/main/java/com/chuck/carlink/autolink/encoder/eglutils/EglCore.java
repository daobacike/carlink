package com.chuck.carlink.autolink.encoder.eglutils;


import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.util.Log;
import android.view.Surface;


/**
 * Created by chuck.liuzhaopeng on 2018/6/29.
 */

public final class EglCore {
    private static final int EGL_RECORDABLE_ANDROID = 0x3142;
    public static final int FLAG_RECORDABLE = 0x1;
    public static final int FLAG_TRY_GLES3 = 0x2;
    private static final String TAG = "EglCore";
    private EGLConfig mEGLConfig;
    private EGLContext mEGLContext;
    private EGLDisplay  mEGLDisplay;
    private int mEGLVerison;


    public EglCore() {

    }

    public EglCore(EGLContext  sharedContext,  int flags){
        mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        mEGLContext = EGL14.EGL_NO_CONTEXT;
        mEGLVerison = -1;
        mEGLConfig = null;

        if (sharedContext == null) {
            sharedContext = EGL14.EGL_NO_CONTEXT;
        }

        mEGLDisplay = EGL14.eglGetDisplay(0);
        if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("unable to get EGL14 display");
        }

        int[] version = new int[2];
        if (EGL14.eglInitialize(mEGLDisplay, version,0,version, 1)) {
            EGLConfig config;
            EGLContext context;
            if ((flags & 2) != 0) {
                config = getConfig(flags, 3);
                if (config != null) {
                    context = EGL14.eglCreateContext(mEGLDisplay,config,sharedContext,new int[]{12440, 3, 12344}, 0);

                    mEGLConfig = config;
                    mEGLContext = context;
                    mEGLVerison = 3;
                }
            }
            if (mEGLContext == EGL14.EGL_NO_CONTEXT) {
                config = getConfig(flags, 2);
                if (config == null) {
                    throw new RuntimeException("Unable to find a suitable EGLConfig");
                }
                context = EGL14.eglCreateContext(mEGLDisplay, config, sharedContext, new int[]{12440, 2, 12344}, 0);
                checkEGLError("eglCreateContext");
                mEGLConfig = config;
                mEGLContext = context;
                mEGLVerison = 2;
            }
            int[] values = new int[1];
            EGL14.eglQueryContext(this.mEGLDisplay, this.mEGLContext, 12440, values, 0);
            Log.d(TAG, "EGLContext created, client version " + values[0]);
            return;
        }
        this.mEGLDisplay = null;
        throw new RuntimeException("unable to initialize EGL14");
    }

    private EGLConfig getConfig(int flags, int version) {
        int renderableType = 4;
        if (version >= 3) {
            renderableType = 4 | 64;
        }
        int[] attribList = new int[13];
        attribList[0] = 12324;
        attribList[1] = 8;
        attribList[2] = 12323;
        attribList[3] = 8;
        attribList[4] = 12322;
        attribList[5] = 8;
        attribList[6] = 12321;
        attribList[7] = 8;
        attribList[8] = 12352;
        attribList[9] = renderableType;
        attribList[10] = 12344;
        attribList[12] = 12344;
        if ((flags & 1) != 0) {
            attribList[attribList.length - 3] = EGL_RECORDABLE_ANDROID;
            attribList[attribList.length - 2] = 1;
        }

        EGLConfig[] configs = new EGLConfig[1];
        if (EGL14.eglChooseConfig(mEGLDisplay, attribList, 0, configs, 0, configs.length, new int[1], 0)) {
            return configs[0];
        }
        Log.w(TAG, "unable to find RGB8888 / " + version + " EGLConfig");
        return null;
    }

    private void checkEGLError(String msg) {
        int error =EGL14.eglGetError();
        if (error != EGL14.EGL_SUCCESS) {
            throw new RuntimeException(new StringBuilder(String.valueOf(msg)).append(": EGL error: 0x").append(Integer.toHexString(error)).toString());
        }
    }

    public void release() {
        if (this.mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglMakeCurrent(this.mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroyContext(this.mEGLDisplay, this.mEGLContext);
            EGL14.eglReleaseThread();
            EGL14.eglTerminate(this.mEGLDisplay);
        }
        this.mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        this.mEGLContext = EGL14.EGL_NO_CONTEXT;
        this.mEGLConfig = null;
    }

    protected void finalize () throws Throwable {
        try {
            if (this.mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
                Log.w(TAG, "WARNING: EglCore was not explicitly released -- state may be leaked");
                release();
            }
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
        }

    }

    public void releaseSurface(EGLSurface eglSurface) {
        EGL14.eglDestroySurface(this.mEGLDisplay, eglSurface);
    }

    public EGLSurface createWindowSurface(Object surface) {
        if ((surface instanceof Surface) || (surface instanceof SurfaceTexture)) {
            EGLSurface eglSurface = EGL14.eglCreateWindowSurface(this.mEGLDisplay, this.mEGLConfig, surface, new int[]{12344}, 0);
            checkEGLError("eglCreateWindowSurface");
            if (eglSurface != null) {
                return eglSurface;
            }
            throw new RuntimeException("surface was null");
        }
        throw new RuntimeException("invalid surface: " + surface);
    }

    public EGLSurface createOffscreenSurface(int width, int height) {
        EGLSurface eglSurface = EGL14.eglCreatePbufferSurface(this.mEGLDisplay, this.mEGLConfig, new int[]{12375, width, 12374, height, 12344}, 0);
        checkEGLError("eglCreatePbufferSurface");
        if (eglSurface != null) {
            return eglSurface;
        }
        throw new RuntimeException("surface was null");
    }


    public void makeCurrent(EGLSurface eglSurface) {
        if (this.mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            Log.d(TAG, "NOTE: makeCurrent w/o display");
        }
        if (!EGL14.eglMakeCurrent(this.mEGLDisplay, eglSurface, eglSurface, this.mEGLContext)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
    }
    public void makeCurrent(EGLSurface drawSurface, EGLSurface readSurface) {
        if (this.mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            Log.d(TAG, "NOTE: makeCurrent w/o display");
        }
        if (!EGL14.eglMakeCurrent(this.mEGLDisplay, drawSurface, readSurface, this.mEGLContext)) {
            throw new RuntimeException("eglMakeCurrent(draw,read) failed");
        }
    }

    public void makeNothingCurrent() {
        if (!EGL14.eglMakeCurrent(this.mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
    }

    public boolean swapBuffers(EGLSurface eglSurface) {
        return EGL14.eglSwapBuffers(this.mEGLDisplay, eglSurface);
    }

    public void setPresentationTime(EGLSurface eglSurface, long nsecs) {
        EGLExt.eglPresentationTimeANDROID(this.mEGLDisplay, eglSurface, nsecs);
    }

    public boolean isCurrent(EGLSurface eglSurface) {
        return this.mEGLContext.equals(EGL14.eglGetCurrentContext()) && eglSurface.equals(EGL14.eglGetCurrentSurface(12377));
    }

    public int querySurface(EGLSurface eglSurface, int what) {
        int[] value = new int[1];
        EGL14.eglQuerySurface(this.mEGLDisplay, eglSurface, what, value, 0);
        return value[0];
    }

    public int getGlVersion() {
        return this.mEGLVerison;
    }

    public static void logCurrent(String msg) {
        EGLDisplay display = EGL14.eglGetCurrentDisplay();
        EGLContext context = EGL14.eglGetCurrentContext();
        Log.i(TAG, "Current EGL (" + msg + "): display=" + display + ", context=" + context + ", surface=" + EGL14.eglGetCurrentSurface(12377));
    }

    public EGLContext getContext() {
        return this.mEGLContext;
    }



}
