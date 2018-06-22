package com.chuck.carlink.screencapture;

import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.provider.MediaStore;

/**
 * Created by chuck.liuzhaopeng on 2018/6/13.
 */

public class VideoEncodeConfig {
    final int width;
    final int height;
    final int bitrate;
    final int framerate;
    final int iframeInterval;
    String codeName;
    final String mimeType;
    final MediaCodecInfo.CodecProfileLevel codecProfileLevel;


    public VideoEncodeConfig(int width, int height, int bitrate, int framerate,
                             int iframeInterval, String codeName, String mimeType,
                            MediaCodecInfo.CodecProfileLevel codecProfileLevel) {
        this.width = width;
        this.height = height;
        this.bitrate = bitrate;
        this.framerate = framerate;
        this.iframeInterval = iframeInterval;
        this.codeName = codeName;
        this.mimeType = mimeType;
        this.codecProfileLevel = codecProfileLevel;
    }

    MediaFormat toFormat() {
        MediaFormat format = MediaFormat.createVideoFormat(mimeType, width, height);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, framerate);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, iframeInterval);

        if (codecProfileLevel != null && codecProfileLevel.profile !=0 && codecProfileLevel.level != 0) {
            format.setInteger(MediaFormat.KEY_PROFILE, codecProfileLevel.profile);
            format.setInteger(MediaFormat.KEY_LEVEL, codecProfileLevel.level);
        }

        return  format;
    }

}
