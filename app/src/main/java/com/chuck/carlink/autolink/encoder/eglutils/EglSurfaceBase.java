package com.chuck.carlink.autolink.encoder.eglutils;



import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.opengl.EGL14;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.util.Log;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by chuck.liuzhaopeng on 2018/6/29.
 */

public class EglSurfaceBase {
    private static final boolean DEBUG = false;
    protected static final String TAG = "EglSurfaceBase";
    private EGLSurface mEGLSurface = EGL14.EGL_NO_SURFACE;
    protected EglCore mEglCore;
    private int mHeight = -1;
    private int mWidth = -1;

    protected EglSurfaceBase(EglCore eglBase) {
        this.mEglCore = eglBase;
    }

    public void createWindowSurface(Object surface) {
        if (this.mEGLSurface != EGL14.EGL_NO_SURFACE) {
            throw new IllegalStateException("surface already created");
        }
        this.mEGLSurface = this.mEglCore.createWindowSurface(surface);
        this.mWidth = this.mEglCore.querySurface(this.mEGLSurface, 12375);
        this.mHeight = this.mEglCore.querySurface(this.mEGLSurface, 12374);
    }

    public void createOffscreenSurface(int width, int height) {
        if (this.mEGLSurface != EGL14.EGL_NO_SURFACE) {
            throw new IllegalStateException("surface already created");
        }
        this.mEGLSurface = this.mEglCore.createOffscreenSurface(width, height);
        this.mWidth = width;
        this.mHeight = height;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public void releaseEglSurface() {
        this.mEglCore.releaseSurface(this.mEGLSurface);
        this.mEGLSurface = EGL14.EGL_NO_SURFACE;
        this.mHeight = -1;
        this.mWidth = -1;
    }

    public void makeCurrent() {
        this.mEglCore.makeCurrent(this.mEGLSurface);
        GLES20.glViewport(0, 0, this.mWidth, this.mHeight);
    }

    public void makeCurrentReadFrom(EglSurfaceBase readSurface) {
        this.mEglCore.makeCurrent(this.mEGLSurface, readSurface.mEGLSurface);
    }

    public boolean swapBuffers() {
        boolean result = this.mEglCore.swapBuffers(this.mEGLSurface);
        if (!result) {
            Log.d(TAG, "WARNING: swapBuffers() failed");
        }
        return result;
    }

    public void setPresentationTime(long nsecs) {
        this.mEglCore.setPresentationTime(this.mEGLSurface, nsecs);
    }

    public void saveFrame(File file, int scaleFactor)  {
        if (this.mEglCore.isCurrent(this.mEGLSurface)) {
            final long startTime = System.currentTimeMillis();
            String filename = file.toString();
            final ByteBuffer buf = ByteBuffer.allocateDirect((this.mWidth * this.mHeight) * 4);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            GLES20.glReadPixels(0, 0, this.mWidth, this.mHeight, 6408, 5121, buf);
            buf.rewind();
            final String str = filename;
            final int i = scaleFactor;
            new Thread(new Runnable() {
                public void run() {
                    OutputStream outputStream;
                    FileNotFoundException e;
                    Throwable th;
                    BufferedOutputStream bos = null;
                    try {
                        OutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(str));
                        {
                            Bitmap fullBitmap = Bitmap.createBitmap(EglSurfaceBase.this.mWidth, EglSurfaceBase.this.mHeight, Config.ARGB_8888);
                            fullBitmap.copyPixelsFromBuffer(buf);
                            Matrix m = new Matrix();
                            m.preScale(1.0f, -1.0f);
                            if (i != 1) {
                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(fullBitmap, EglSurfaceBase.this.mWidth / i, EglSurfaceBase.this.mHeight / i, true);
                                Bitmap flippedScaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), m, true);
                                flippedScaledBitmap.compress(CompressFormat.JPEG, 90, bufferedOutputStream);
                                scaledBitmap.recycle();
                                flippedScaledBitmap.recycle();
                            } else {
                                Bitmap.createBitmap(fullBitmap, 0, 0, EglSurfaceBase.this.mWidth, EglSurfaceBase.this.mHeight, m, true).compress(CompressFormat.JPEG, 90, bufferedOutputStream);
                            }
                            fullBitmap.recycle();
                            Log.d(EglSurfaceBase.TAG, "Saved " + (EglSurfaceBase.this.mWidth / i) + "x" + (EglSurfaceBase.this.mHeight / i) + " frame as '" + str + "' in " + (System.currentTimeMillis() - startTime) + " ms");
                            if (bufferedOutputStream != null) {
                                try {
                                    bufferedOutputStream.close();
                                    outputStream = bufferedOutputStream;
                                    return;
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                }
                            }
                            outputStream = bufferedOutputStream;
                        }

                    } catch (FileNotFoundException fnfe) {
                        fnfe.printStackTrace();
                    }
                }
            }).start();
            return;
        }
        throw new RuntimeException("Expected EGL context/surface is not current");
    }

    public void updateSize() {
        this.mWidth = this.mEglCore.querySurface(this.mEGLSurface, 12375);
        this.mHeight = this.mEglCore.querySurface(this.mEGLSurface, 12374);
    }

}
