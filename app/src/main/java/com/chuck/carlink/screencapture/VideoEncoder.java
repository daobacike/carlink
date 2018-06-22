package com.chuck.carlink.screencapture;

import android.media.MediaFormat;
import android.view.Surface;

import java.util.Objects;

/**
 * Created by chuck.liuzhaopeng on 2018/6/13.
 */

public class VideoEncoder extends BaseEncoder {
    private VideoEncodeConfig mConfig;
    private Surface mSurface;

    VideoEncoder(VideoEncodeConfig config){
        super(config.codeName);
        mConfig = config;
    }
    @Override
    protected MediaFormat createMeidaFormat() {
        if (mConfig != null)
            return  mConfig.toFormat();
        return null;
    }


    Surface getInputSurface() {
        return Objects.requireNonNull(mSurface, "don't prepared");
    }


    @Override
    public void release() {
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
        super.release();
    }
}

