package com.chuck.carlink.screencapture;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.Surface;

import com.chuck.carlink.core.Packager;
import com.chuck.carlink.tools.LogTools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by chuck.liuzhaopeng on 2018/6/1.
 */

public class ScreenRecorder extends  Thread {
    private static final String TAG = "ScreenRecorder";
    //private Context mContext;
    private int mWidth;
    private int mHeight;
    private int mBitRate;
    private int mDpi;
    private MediaProjection mMediaProjection;
    // parameters for the encoder
    private static final String MIME_TYPE = "video/avc"; // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 30; // 30 fps
    private static final int IFRAME_INTERVAL = 2; // 2 seconds between I-frames
    private static final int TIMEOUT_US = 10000;
    private final String mDstPath;

    private MediaCodec mEncoder;
    private Surface mSurface;
    private long startTime = 0;
    private AtomicBoolean mQuit = new AtomicBoolean(false);
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    private VirtualDisplay mVirtualDisplay;
    //private RESFlvDataCollecter mDataCollecter;

    private ImageReader mImageReader ;
    private int mNumber =  0;
    private HandlerThread mHandlerThread;
    private Handler mThreadHandler;

    public ScreenRecorder(int width, int height, int dpi,
                          MediaProjection mp, String dstPath){
        mWidth = width;
        mHeight = height;
        mDpi = dpi;
        mMediaProjection = mp;
        startTime = 0;
        //mVideoEncoder = new VideoEncoder(video);
        mDstPath = dstPath;
    }

    /**
     * stop task
     */
    public final void quit() {
        mQuit.set(true);
    }

    @Override
    public void run() {
        setupImage();
        try {
            while(true) {
                sleep(5000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void initThread()
    {
        mHandlerThread = new HandlerThread("check-message-coming");
        mHandlerThread.start();

        mThreadHandler = new Handler(mHandlerThread.getLooper());

    }

    private void setupImage(){
        initThread();
        mImageReader = ImageReader.newInstance(mWidth,mHeight, ImageFormat.JPEG, 2);
        mImageReader.setOnImageAvailableListener(onImageAvailableListener, mThreadHandler);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(TAG + "-display",
                mWidth, mHeight, mDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                mImageReader.getSurface(), null, null);
        Log.d(TAG, "created virtual display: " + mVirtualDisplay);
    }


    private final ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            int numb = reader.getMaxImages();
            Log.d(TAG, " numb :"  +  numb);
            Image image = null;
            try {
                image = mImageReader.acquireLatestImage();
                if (image != null) {
                    final Image.Plane[] planes = image.getPlanes();
                    if (planes.length > 0) {
                        final ByteBuffer buffer = planes[0].getBuffer();
                        //int pixelStride = planes[0].getPixelStride();
                        //int rowStride = planes[0].getRowStride();
                        //int rowPadding = rowStride - pixelStride * mWidth;


                        // create bitmap
                        Bitmap bmp = Bitmap.createBitmap(mWidth,
                                mHeight, Bitmap.Config.ARGB_8888);
                        bmp.copyPixelsFromBuffer(buffer);

                        Bitmap croppedBitmap = Bitmap.createBitmap(bmp, 0, 0, mWidth, mHeight);

                        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),"ScreenCaptures");
                        Log.d(TAG, " dir "  +  dir);
                        File newFile = new File(dir, (mNumber++) +"_screen.jpg");
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newFile));
                        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        bos.flush();
                        bos.close();

                        if (croppedBitmap != null) {
                            croppedBitmap.recycle();
                        }
                        if (bmp != null) {
                            bmp.recycle();
                        }
                    }
                }




            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                image.close();
                //byteBuffer.clear();
            }
            Log.d(TAG, " close "  );
            image.close();

        }
    };


    private void prepareEncoder() throws IOException {
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        Log.d(TAG, "created video format: " + format);
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mSurface = mEncoder.createInputSurface();
        Log.d(TAG, "created input surface: " + mSurface);
        mEncoder.start();
    }

    private void recordVirtualDisplay() {
        while (!mQuit.get()) {
            int eobIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_US);
            switch (eobIndex) {
                case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                    LogTools.d("VideoSenderThread,MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED");
                    break;
                case MediaCodec.INFO_TRY_AGAIN_LATER:
//                    LogTools.d("VideoSenderThread,MediaCodec.INFO_TRY_AGAIN_LATER");
                    break;
                case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                    LogTools.d("VideoSenderThread,MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:" +
                            mEncoder.getOutputFormat().toString());
                    sendAVCDecoderConfigurationRecord(0, mEncoder.getOutputFormat());
                    break;
                default:
                    LogTools.d("VideoSenderThread,MediaCode,eobIndex=" + eobIndex);
                    if (startTime == 0) {
                        startTime = mBufferInfo.presentationTimeUs / 1000;
                    }
                    /**
                     * we send sps pps already in INFO_OUTPUT_FORMAT_CHANGED
                     * so we ignore MediaCodec.BUFFER_FLAG_CODEC_CONFIG
                     */
                    if (mBufferInfo.flags != MediaCodec.BUFFER_FLAG_CODEC_CONFIG && mBufferInfo.size != 0) {
                        ByteBuffer realData = mEncoder.getOutputBuffers()[eobIndex];
                        realData.position(mBufferInfo.offset + 4);
                        realData.limit(mBufferInfo.offset + mBufferInfo.size);
                        sendRealData((mBufferInfo.presentationTimeUs / 1000) - startTime, realData);
                    }
                    mEncoder.releaseOutputBuffer(eobIndex, false);
                    break;
            }
        }
    }


    private void release() {
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
        }
    }


    public final boolean getStatus() {
        return !mQuit.get();
    }


    private void sendAVCDecoderConfigurationRecord(long tms, MediaFormat format) {
        byte[] AVCDecoderConfigurationRecord = Packager.H264Packager.generateAVCDecoderConfigurationRecord(format);
        int packetLen = Packager.FLVPackager.FLV_VIDEO_TAG_LENGTH +
                AVCDecoderConfigurationRecord.length;
        byte[] finalBuff = new byte[packetLen];
        Packager.FLVPackager.fillFlvVideoTag(finalBuff,
                0,
                true,
                true,
                AVCDecoderConfigurationRecord.length);
        System.arraycopy(AVCDecoderConfigurationRecord, 0,
                finalBuff, Packager.FLVPackager.FLV_VIDEO_TAG_LENGTH, AVCDecoderConfigurationRecord.length);
//        RESFlvData resFlvData = new RESFlvData();
//        resFlvData.droppable = false;
//        resFlvData.byteBuffer = finalBuff;
//        resFlvData.size = finalBuff.length;
//        resFlvData.dts = (int) tms;
//        resFlvData.flvTagType = FLV_RTMP_PACKET_TYPE_VIDEO;
//        resFlvData.videoFrameType = RESFlvData.NALU_TYPE_IDR;
//        mDataCollecter.collect(resFlvData, FLV_RTMP_PACKET_TYPE_VIDEO);
    }

    private void sendRealData(long tms, ByteBuffer realData) {
        int realDataLength = realData.remaining();
        int packetLen = Packager.FLVPackager.FLV_VIDEO_TAG_LENGTH +
                Packager.FLVPackager.NALU_HEADER_LENGTH +
                realDataLength;
        byte[] finalBuff = new byte[packetLen];
        realData.get(finalBuff, Packager.FLVPackager.FLV_VIDEO_TAG_LENGTH +
                        Packager.FLVPackager.NALU_HEADER_LENGTH,
                realDataLength);
        int frameType = finalBuff[Packager.FLVPackager.FLV_VIDEO_TAG_LENGTH +
                Packager.FLVPackager.NALU_HEADER_LENGTH] & 0x1F;
        Packager.FLVPackager.fillFlvVideoTag(finalBuff,
                0,
                false,
                frameType == 5,
                realDataLength);
//        RESFlvData resFlvData = new RESFlvData();
//        resFlvData.droppable = true;
//        resFlvData.byteBuffer = finalBuff;
//        resFlvData.size = finalBuff.length;
//        resFlvData.dts = (int) tms;
//        resFlvData.flvTagType = FLV_RTMP_PACKET_TYPE_VIDEO;
//        resFlvData.videoFrameType = frameType;
//        mDataCollecter.collect(resFlvData, FLV_RTMP_PACKET_TYPE_VIDEO);
    }

}
