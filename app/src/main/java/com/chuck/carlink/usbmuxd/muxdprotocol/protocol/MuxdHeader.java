package com.chuck.carlink.usbmuxd.muxdprotocol.protocol;

/**
 * Created by vonchenchen on 23/09/2017.
 */

public class MuxdHeader extends BaseProtocol{

    public int protocol;
    public int length;

    @Override
    public void build(byte[] data, int offset) {

        protocol = u32BigEndian2Little(data, offset);
        length = u32BigEndian2Little(data, offset + LENGTH_U32);
    }

    @Override
    public void extend(byte[] target, int offset) {

        u32LittleEndian2BigBuf(protocol, target, offset);
        u32LittleEndian2BigBuf(length, target, offset + LENGTH_U32);
    }

    @Override
    public int getHeaderLength() {
        return LENGTH_U32 * 2;
    }
}
