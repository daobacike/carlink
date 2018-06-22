package com.chuck.carlink.usbmuxd.muxdprotocol.protocol;

    /*
     * TCP header.
     * Per RFC 793, September, 1981.
     */
//    struct tcphdr {
//        unsigned short	th_sport;	/* source port */
//        unsigned short	th_dport;	/* destination port */
//        tcp_seq	th_seq;			/* sequence number */
//        tcp_seq	th_ack;			/* acknowledgement number */
//        #if __DARWIN_BYTE_ORDER == __DARWIN_LITTLE_ENDIAN
//        unsigned int	th_x2:4,	/* (unused) */
//        th_off:4;	/* data offset */
//        #endif
//        #if __DARWIN_BYTE_ORDER == __DARWIN_BIG_ENDIAN
//        unsigned int	th_off:4,	/* data offset */
//        th_x2:4;	/* (unused) */
//        #endif
//        unsigned char	th_flags;
//        #define	TH_FIN	0x01
//        #define	TH_SYN	0x02
//        #define	TH_RST	0x04
//        #define	TH_PUSH	0x08
//        #define	TH_ACK	0x10
//        #define	TH_URG	0x20
//        #define	TH_ECE	0x40
//        #define	TH_CWR	0x80
//        #define	TH_FLAGS	(TH_FIN|TH_SYN|TH_RST|TH_ACK|TH_URG|TH_ECE|TH_CWR)
//
//        unsigned short	th_win;		/* window */
//        unsigned short	th_sum;		/* checksum */
//        unsigned short	th_urp;		/* urgent pointer */
//        };

/**
 *
 * Created by vonchenchen on 23/09/2017.
 */

public class TcpHeader extends BaseProtocol{

    public short th_sport;
    public short th_dport;

    public int th_seq;
    public int th_ack;

    public char th_x2off;  // tx_x:4   th_off:4
    public char th_flags;

    public short th_win;
    public short th_sum;
    public short th_urp;

    @Override
    public void build(byte[] data, int offset) {

        th_sport = u16BigEndian2Little(data, offset);
        th_dport = u16BigEndian2Little(data, offset + LENGTH_U16);

        th_seq = u32BigEndian2Little(data, offset + LENGTH_U16 * 2);
        th_ack = u32BigEndian2Little(data, offset + LENGTH_U16 * 2 + LENGTH_U32);

        th_x2off = u8BigEndian2Little(data, offset + LENGTH_U16 * 2 + LENGTH_U32 * 2);
        th_flags = u8BigEndian2Little(data, offset + LENGTH_U16 * 2 + LENGTH_U32 * 2 + LENGTH_U8);

        th_win = u16BigEndian2Little(data, offset + LENGTH_U16 * 2 + LENGTH_U32 * 2 + LENGTH_U8 * 2);
        th_sum = u16BigEndian2Little(data, offset + LENGTH_U16 * 2 + LENGTH_U32 * 2 + LENGTH_U8 * 2 + LENGTH_U16);
        th_urp = u16BigEndian2Little(data, offset + LENGTH_U16 * 2 + LENGTH_U32 * 2 + LENGTH_U8 * 2 + LENGTH_U16 * 2);
    }

    @Override
    public void extend(byte[] target, int offset) {

        u16LittleEndian2BigBuf(th_sport, target, offset);
        u16LittleEndian2BigBuf(th_dport, target, offset + LENGTH_U16);

        u32LittleEndian2BigBuf(th_seq, target, offset + LENGTH_U16 * 2);
        u32LittleEndian2BigBuf(th_ack, target, offset + LENGTH_U16 * 2 + LENGTH_U32);

        u8LittleEndian2BigBuf(th_x2off, target, offset + LENGTH_U16 * 2 + LENGTH_U32 * 2);
        u8LittleEndian2BigBuf(th_flags, target, offset + LENGTH_U16 * 2 + LENGTH_U32 * 2 + LENGTH_U8);

        u16LittleEndian2BigBuf(th_win, target, offset + LENGTH_U16 * 2 + LENGTH_U32 * 2 + LENGTH_U8 * 2);
        u16LittleEndian2BigBuf(th_sum, target, offset + LENGTH_U16 * 2 + LENGTH_U32 * 2 + LENGTH_U8 * 2 + LENGTH_U16);
        u16LittleEndian2BigBuf(th_urp, target, offset + LENGTH_U16 * 2 + LENGTH_U32 * 2 + LENGTH_U8 * 2 + LENGTH_U16 * 2);
    }

    @Override
    public int getHeaderLength() {
        return LENGTH_U16 * 2 + LENGTH_U32 * 2 + LENGTH_U8 *2 + LENGTH_U16 * 3;
    }
}