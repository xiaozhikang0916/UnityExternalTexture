using UnityEngine;
using System.Collections;
using System.Runtime.InteropServices;
using System;
using UnityEngine.Assertions;
using System.IO;

public class PreviewTestNew : MonoBehaviour
{
    //Texture to be renderered on
    public GameObject plane;
    AndroidJavaClass androidNativeCam;
    AndroidJavaObject androidNativeCamActivity;

    Texture2D camTexture;

    private int texWidth;
    private int texHeight;

    private bool pic1 = false;
    private bool pic2 = false;


    // Use this for initialization
    void Start()
    {
#if UNITY_ANDROID
        //find the plugin
        //AndroidJNI.AttachCurrentThread();

        //Get current unityplayer
        androidNativeCam = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        Debug.Log("AndroidJavaClass null " + (androidNativeCam == null).ToString());

        //Get current activity, which has my customized methods
        androidNativeCamActivity = androidNativeCam.GetStatic<AndroidJavaObject>("currentActivity");
        Debug.Log("Activity package " + androidNativeCamActivity.Call<string>("getLocalClassName"));
#endif
    }
    //check which button was clicked
    //external texture should be created in Update method, or it will cause glError 0x502
    public void setPic1()
    {
        pic1 = true;
    }

    public void setPic2()
    {
        pic2 = true;
    }
    // Update is called once per frame
    void Update()
    {

        if (Input.GetKey(KeyCode.Escape))
        {
            Application.Quit();
        }

        if (pic1)
        {
            pic1 = false;
            Debug.Log("pic 1 clicked");
            Debug.Log("Unity update texture");
            androidNativeCamActivity.Call("checkGlError", "UnityBeforeUpdate");
            //Get the pointer to the external texture created in Android plugin
            //See TestClass#updateTexture in the Android project
            int pointer = androidNativeCamActivity.Call<int>("updateTexture");
            //GL.InvalidateState();

            //Create texture with the pointer
            Texture2D t = Texture2D.CreateExternalTexture(440, 220, TextureFormat.YUY2, false, true, new IntPtr(pointer));
            Debug.Log("Create texture null : " + (t == null).ToString());
            androidNativeCamActivity.Call("checkGlError", "UnityCreateTexture");
            //tex.Apply();

            //Apply the texture to the plane
            plane.GetComponent<Renderer>().material.mainTexture = t;
            androidNativeCamActivity.Call("checkGlError", "UnityUpdateTexture");
            
            /*TODO: wants to save the texture as an image file,
             *but seems unable to read data in the texture
             *Tried: GetRawTextureData() returns byte array length 0
             *       EncodeToJPG/PNG returns byte array length 0
             *       GetPixels32() returns Color array with all nodes valued (0, 0, 0, 0)
             */
            //Byte[] bytes = t.GetRawTextureData();
            //FileStream img = File.OpenWrite("/sdcard/testWrite.png");
            //img.Write(bytes, 0, bytes.Length);
            //Debug.Log("Write " + bytes.Length + " bytes");
            //img.Flush();
            //img.Close();
            Debug.Log("Create finish");
        }
        if (pic2)
        {
            pic2 = false;
            Debug.Log("pic 2 clicked");
            Debug.Log("Unity update texture");
            //Render a new image to the previous texture pointer
            //Need not to create a new texture
            androidNativeCamActivity.Call("updateTexture2");
            Debug.Log("Update finish");
        }


    }



}