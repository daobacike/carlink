package com.chuck.carlink.screencapture;

import java.io.IOException;

/**
 * Created by chuck.liuzhaopeng on 2018/6/12.
 */

interface Encoder {
    void prepare() throws IOException;
    void stop();
    void release();
    void setCallback(Callback callback);

    interface Callback {
        void onError(Encoder encoder, Exception e);
    }
}
