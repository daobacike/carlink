package com.chuck.carlink.usbmuxd.muxdprotocol.protocol;

/**
 * Created by vonchenchen on 23/09/2017.
 */

public class VersionHeader extends  BaseProtocol{

    public int major;
    public int minor;
    public int padding;

    @Override
    public void build(byte[] data, int offset) {

        major = u32BigEndian2Little(data, offset);
        minor = u32BigEndian2Little(data, offset + LENGTH_U32);
        padding = u32BigEndian2Little(data, offset + LENGTH_U32 * 2);
    }

    @Override
    public void extend(byte[] target, int offset) {

        u32LittleEndian2BigBuf(major, target, offset);
        u32LittleEndian2BigBuf(minor, target, offset + LENGTH_U32);
        u32LittleEndian2BigBuf(padding, target, offset + LENGTH_U32 * 2);
    }

    @Override
    public int getHeaderLength() {
        return LENGTH_U32 * 3;
    }
}
