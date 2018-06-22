package com.chuck.carlink.usbmuxd.muxdprotocol.protocol;

/**
 * Created by vonchenchen on 23/09/2017.
 */

public abstract class BaseProtocol {

    public static final int LENGTH_U32 = 4;
    public static final int LENGTH_U16 = 2;
    public static final int LENGTH_U8 = 1;

    /** 将buffer数据解析到协议对象中 */
    public abstract void build(byte[] data, int offset);
    /** 将协议对象展开到buffer中 */
    public abstract void extend(byte[] target, int offset);

    public abstract int getHeaderLength();

    /**
     * 将数组中的大端数据转换为小端int
     * @param buf
     * @param offset
     * @return
     */
    protected int u32BigEndian2Little(byte[] buf, int offset){
        int ret = 0;
        for(int i=0; i<4; i++) {
            ret |= (buf[offset+i]| 0x00000000)<< ((3-i)*8);
        }
        return ret;
    }

    /**
     * 将小端int数据还原到数组中
     * @param data
     * @param buf
     * @param offset
     */
    protected void u32LittleEndian2BigBuf(int data, byte[] buf, int offset){

        buf[offset + 0] = (byte) ((0xff000000 & data) >> 8*3);
        buf[offset + 1] = (byte) ((0x00ff0000 & data) >> 8*2);
        buf[offset + 2] = (byte) ((0x0000ff00 & data) >> 8*1);
        buf[offset + 3] = (byte) ((0x000000ff & data) >> 8*0);
    }

    /**
     * 将数组中的大端数据转换为小端u16
     * @param buf
     * @param offset
     * @return
     */
    protected short u16BigEndian2Little(byte[] buf, int offset){
        short ret = 0;
        for(int i=0; i<2; i++) {
            ret |= (buf[offset+i]| 0x0000)<< ((1-i)*8);
        }
        return ret;
    }

    /**
     * 将小端u16数据还原到数组中
     * @param data
     * @param buf
     * @param offset
     */
    protected void u16LittleEndian2BigBuf(short data, byte[] buf, int offset){

        buf[offset + 0] = (byte) ((0xff00 & data) >> 8*1);
        buf[offset + 1] = (byte) ((0x00ff & data) >> 8*0);
    }

    protected char u8BigEndian2Little(byte[] buf, int offset){
        return (char) buf[offset];
    }

    protected void u8LittleEndian2BigBuf(char data, byte[] buf, int offset){
        buf[offset] = (byte) data;
    }
}
