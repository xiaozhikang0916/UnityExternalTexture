package com.xiaozhikang.test;


import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

import com.unity3d.player.UnityPlayerActivity;

import android.graphics.BitmapFactory;
import android.hardware.camera2.*;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by xiaozhikang on 2016/4/8.
 */
public class TestClass extends UnityPlayerActivity
//        implements SurfaceTexture.OnFrameAvailableListener
{
    private static final String LOG_TAG = TestClass.class
            .getSimpleName();

    public static Context mContext;

    private static Camera mCamera;
    private static SurfaceTexture texture;

    // unity texture
    private static int nativeTexturePointer = -1;

    private static int prevHeight;
    private static int prevWidth;

    private static ByteBuffer mPixelBuf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    public void setmContext(Object c){
        if(c instanceof Activity) {
            mContext = ((Activity) c).getApplicationContext();
        } else {
            Log.e(LOG_TAG, "Object error");
        }
        Log.i(LOG_TAG, "Set context");
    }
    public void testMethod(String name){
        Log.i("Android", name);
    }

    public int startCamera() {
        // create the texture

        nativeTexturePointer = createExternalTexture();
        texture = new SurfaceTexture(nativeTexturePointer);
        if(mContext == null){
            Log.e(LOG_TAG, "Context null!");
        }
        texture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {

                Log.d(LOG_TAG, "onFrameAvailable");
            }
        });

        // open the camera
        mCamera = Camera.open();
        setupCamera();

        Log.d(LOG_TAG, "camera opened: " + (mCamera != null));

        try {
            mCamera.setPreviewTexture(texture);
            mCamera.startPreview();

        } catch (IOException ioe) {
            Log.w("MainActivity", "CAM LAUNCH FAILED");
        }

        Log.d(LOG_TAG, "nativeTexturePointer="+nativeTexturePointer);
        return nativeTexturePointer;
    }

    @SuppressLint("NewApi")
    private void setupCamera() {
        Camera.Parameters parms = mCamera.getParameters();

        // Give the camera a hint that we're recording video. This can have a
        // big impact on frame rate.
        parms.setRecordingHint(true);
        parms.setPreviewFormat(20);

        // leave the frame rate set to default
        mCamera.setParameters(parms);

        Camera.Size mCameraPreviewSize = parms.getPreviewSize();
        prevWidth = parms.getPreviewSize().width;
        prevHeight = parms.getPreviewSize().height;

//		mPixelBuf = ByteBuffer.allocateDirect(prevWidth * prevHeight * 4);
//		mPixelBuf.order(ByteOrder.LITTLE_ENDIAN);

        // only for debugging output
        int[] fpsRange = new int[2];
        parms.getPreviewFpsRange(fpsRange);
        String previewFacts = mCameraPreviewSize.width + "x"
                + mCameraPreviewSize.height;
        if (fpsRange[0] == fpsRange[1]) {
            previewFacts += " @" + (fpsRange[0] / 1000.0) + "fps";
        } else {
            previewFacts += " @[" + (fpsRange[0] / 1000.0) + " - "
                    + (fpsRange[1] / 1000.0) + "] fps";
        }

//		previewFacts += ", supported Preview Formats: ";
//		List<Integer> formats = parms.getSupportedPreviewFormats();
//		for (int i = 0; i < formats.size(); i++) {
//			previewFacts += formats.get(i).toString() + " ";
//		}
//		Integer format = parms.getPreviewFormat();
//		previewFacts += ", Preview Format: ";
//		previewFacts += format.toString();

        Log.i(LOG_TAG, "previewFacts=" + previewFacts);

        checkGlError("endSetupCamera");
    }

    public int updateTexture() {
        // check for errors at the beginning
//        checkGlError("begin_updateTexture()");
//
//        Log.d(LOG_TAG, "GLES20.glActiveTexture..");
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//        checkGlError("glActiveTexture");
//        Log.d(LOG_TAG, "GLES20.glBindTexture..");
//        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
//                nativeTexturePointer);
//        checkGlError("glBindTexture");
//
//        Log.d(LOG_TAG,"ThreadID="+Thread.currentThread().getId());
//        Log.d(LOG_TAG, "texture.updateTexImage..");
//
//        texture.updateTexImage();
//        checkGlError("updateTexImage");
//        mPixelBuf = ByteBuffer.allocateDirect(length);
//
//        mPixelBuf.rewind();
//        GLES20.glReadPixels(0, 0, prevWidth, prevHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
//                mPixelBuf);
//        return mPixelBuf.array();
        checkGlError("begin_updateTexture()");
        nativeTexturePointer = createExternalTexture();
        if(GLES20.glIsTexture(nativeTexturePointer)) {
            texture = new SurfaceTexture(nativeTexturePointer);
        }
        else {
            Log.e(LOG_TAG, nativeTexturePointer + " is not texture");
        }
        Log.d(LOG_TAG, "Loading image");

        final Bitmap bitmap;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   // No pre-scaling
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        checkGlError("activeTexture");
        // Read in the resource
        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.test, options);
        // Bind to the texture in OpenGL
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, nativeTexturePointer);
        checkGlError("bindTexture");
        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        // Load the bitmap into the bound texture.

        checkGlError("beforeTexImage");
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
//        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 1024, 1024, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mPixelBuf);

        checkGlError("texImage");
//        texture.updateTexImage();
        bitmap.recycle();
        return nativeTexturePointer;


//		mPixelBuf.rewind();
//		Log.d(LOG_TAG, "GLES20.glReadPixels..");
//		GLES20.glReadPixels(0, 0, prevWidth, prevHeight, GLES20.GL_RGBA,
//				GLES20.GL_UNSIGNED_SHORT_4_4_4_4, mPixelBuf);
//		checkGlError("glReadPixels");

//		Log.d(LOG_TAG, "mPixelBuf.get(0)=" + mPixelBuf.get(0));
    }
    public int updateTexture2() {
        // check for errors at the beginning
//        checkGlError("begin_updateTexture()");
//
//        Log.d(LOG_TAG, "GLES20.glActiveTexture..");
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//        checkGlError("glActiveTexture");
//        Log.d(LOG_TAG, "GLES20.glBindTexture..");
//        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
//                nativeTexturePointer);
//        checkGlError("glBindTexture");
//
//        Log.d(LOG_TAG,"ThreadID="+Thread.currentThread().getId());
//        Log.d(LOG_TAG, "texture.updateTexImage..");
//
//        texture.updateTexImage();
//        checkGlError("updateTexImage");
//        mPixelBuf = ByteBuffer.allocateDirect(length);
//
//        mPixelBuf.rewind();
//        GLES20.glReadPixels(0, 0, prevWidth, prevHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
//                mPixelBuf);
//        return mPixelBuf.array();
        checkGlError("begin_updateTexture()");

        Log.d(LOG_TAG, "Loading image");

        final Bitmap bitmap;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   // No pre-scaling
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        checkGlError("activeTexture");
        // Read in the resource
        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.test2, options);
        // Bind to the texture in OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, nativeTexturePointer);
        checkGlError("bindTexture");
        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        // Load the bitmap into the bound texture.

        checkGlError("beforeTexImage");
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        checkGlError("texImage");
//        texture.updateTexImage();
        bitmap.recycle();
        return nativeTexturePointer;


//		mPixelBuf.rewind();
//		Log.d(LOG_TAG, "GLES20.glReadPixels..");
//		GLES20.glReadPixels(0, 0, prevWidth, prevHeight, GLES20.GL_RGBA,
//				GLES20.GL_UNSIGNED_SHORT_4_4_4_4, mPixelBuf);
//		checkGlError("glReadPixels");

//		Log.d(LOG_TAG, "mPixelBuf.get(0)=" + mPixelBuf.get(0));
    }
    public int getPreviewSizeWidth() {
        return prevWidth;
    }

    public int getPreviewSizeHeight() {

        return prevHeight;
    }

//    @Override
//    public  void onFrameAvailable(SurfaceTexture arg0) {
//
//        Log.d(LOG_TAG, "onFrameAvailable");
//    }

    // create texture here instead by Unity
    private int createExternalTexture() {
        int[] textureIdContainer = new int[1];
        GLES20.glGenTextures(1, textureIdContainer, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                textureIdContainer[0]);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        return textureIdContainer[0];
    }

    // check for OpenGL errors
    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(LOG_TAG, op + ": glError 0x" + Integer.toHexString(error));
        }
    }




}
