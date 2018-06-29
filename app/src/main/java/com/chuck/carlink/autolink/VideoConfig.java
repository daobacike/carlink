package com.chuck.carlink.autolink;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chuck.liuzhaopeng on 2018/6/28.
 */

public class VideoConfig implements Parcelable{

    public static final Creator<VideoConfig> CREATOR = new Creator<VideoConfig>() {
        @Override
        public VideoConfig createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public VideoConfig[] newArray(int size) {
            return new VideoConfig[0];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }


}
