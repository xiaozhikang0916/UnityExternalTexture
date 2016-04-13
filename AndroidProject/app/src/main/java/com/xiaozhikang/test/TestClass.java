package com.xiaozhikang.test;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.util.Log;

import com.unity3d.player.UnityPlayerActivity;

/**
 * Created by xiaozhikang on 2016/4/8.
 */
public class TestClass extends UnityPlayerActivity
{
    private static final String LOG_TAG = TestClass.class
            .getSimpleName();

    public static Context mContext;


    // unity texture
    private static int nativeTexturePointer = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    public void testMethod(String name){
        Log.i("Android", name);
    }


    public int updateTexture() {

        checkGlError("begin_updateTexture()");
        //create new texture
        nativeTexturePointer = createExternalTexture();
        Log.d(LOG_TAG, "Loading image");

        final Bitmap bitmap;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   // No pre-scaling
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; //Unity will create texture in this format


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        checkGlError("activeTexture");
        // Read in the resource
        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.test, options);
        checkGlError("bindTexture");
        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        // Load the bitmap into the bound texture.
        checkGlError("beforeTexImage");
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        checkGlError("texImage");
        bitmap.recycle();
        return nativeTexturePointer;


    }
    public void updateTexture2() {
        checkGlError("begin_updateTexture()");

        Log.d(LOG_TAG, "Loading image");
        //Update the previous texture so need not to create

        final Bitmap bitmap;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   // No pre-scaling
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // Read in the resource
        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.test2, options);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        checkGlError("activeTexture");
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
        bitmap.recycle();
    }


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
