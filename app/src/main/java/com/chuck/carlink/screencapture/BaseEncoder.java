package com.chuck.carlink.screencapture;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Created by chuck.liuzhaopeng on 2018/6/12.
 */

public abstract class BaseEncoder implements Encoder{

    static  abstract class Callback implements Encoder.Callback {
        void onInputBufferAvailable(BaseEncoder encoder, int index) {
        }

        void onOutputFormatChanged(BaseEncoder encoder, MediaFormat format) {
        }

        void onOutputBufferAvailable(BaseEncoder encoder, int index, MediaCodec.BufferInfo info) {
        }
    }



    private String mCodecName;
    private MediaCodec mMediaCodec;
    private Callback mCallBack;
    private static final String TAG = "BaseEncoder" ;

    BaseEncoder(){}
    BaseEncoder(String name){
        mCodecName = name;
    }

    @Override
    public void setCallback(Encoder.Callback callback) {
        if (! (callback instanceof Callback)) {
            throw new IllegalArgumentException();
        }
        this.setCallBack((Callback) callback);
    }

    void setCallBack(Callback callBack) {
        if (this.mMediaCodec != null) {
            throw new IllegalMonitorStateException("mMediaCodec is not  null");
        }
        this.mCallBack = callBack;
    }

    @Override
    public void prepare() throws IOException {
        if (Looper.myLooper() == null
                || Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException("should run in a HandlerThread");
        }

        if (mMediaCodec != null) {
            throw new IllegalStateException("prepared!!");
        }

        MediaFormat format = createMeidaFormat();
        Log.d(TAG, "create media format: " +  format);

        String  mimeType = format.getString(MediaFormat.KEY_MIME);
        final MediaCodec encodec =createEncoder(mimeType);
        try {
            if (this.mCallBack != null) {
                encodec.setCallback(mCodecCallback);
            }
            encodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            encodec.start();
        } catch (MediaCodec.CodecException e) {
            Log.e(TAG, "Configure  codec failure with format " + format, e);
            throw e;
        }
        mMediaCodec = encodec;
    }

    protected abstract MediaFormat createMeidaFormat();

    private MediaCodec createEncoder(String type) throws IOException {
        try {
            if (this.mCodecName != null) {
                return MediaCodec.createByCodecName(mCodecName);
            }
        } catch (IOException e) {
            Log.d(TAG, "Create MediaCodec by name " +  mCodecName + "  failure!");
        }
        return MediaCodec.createEncoderByType(type);
    }


    private MediaCodec.Callback mCodecCallback = new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
            mCallBack.onInputBufferAvailable(BaseEncoder.this, index);
        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
            mCallBack.onOutputBufferAvailable(BaseEncoder.this, index, info);
        }

        @Override
        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
            mCallBack.onError(BaseEncoder.this, e);
        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
            mCallBack.onOutputFormatChanged(BaseEncoder.this, format);
        }
    };

    @Override
    public void stop() {
        if (mMediaCodec != null)
            mMediaCodec.stop();
    }

    @Override
    public void release() {
        if (mMediaCodec != null) {
            mMediaCodec.release();
            mMediaCodec = null;
        }
    }


    protected final MediaCodec getEncoder() {
        return Objects.requireNonNull(mMediaCodec, "doesn't prepare" );
    }

    /**
     * @throws NullPointerException if prepare() not call
     * @param index
     * @return
     */
    public final ByteBuffer getOutputBuffer(int  index) {
        return getEncoder().getOutputBuffer(index);
    }

    public final ByteBuffer getInputBuffer(int index) {
        return getEncoder().getInputBuffer(index);
    }

    public final void queueInputBuffer(int index, int offset , int size, long psTs, int flags) {
        getEncoder().queueInputBuffer(index, offset, size, psTs, flags);
    }

    public final void releaseOutputBuffer(int index) {
        getEncoder().releaseOutputBuffer(index, false);
    }
}
