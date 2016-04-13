using UnityEngine;
using System.Collections;
using System.Runtime.InteropServices;
using System;
using UnityEngine.Assertions;
using System.IO;

public class PreviewTestNew : MonoBehaviour
{
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
        androidNativeCam = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        Debug.Log("AndroidJavaClass null " + (androidNativeCam == null).ToString());
        androidNativeCamActivity = androidNativeCam.GetStatic<AndroidJavaObject>("currentActivity");
        Debug.Log("Activity package " + androidNativeCamActivity.Call<string>("getLocalClassName"));
        androidNativeCam = new AndroidJavaClass("com.xiaozhikang.test.TestClass");
#endif
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
            int pointer = androidNativeCamActivity.Call<int>("updateTexture");
            //GL.InvalidateState();
            Texture2D t = Texture2D.CreateExternalTexture(440, 220, TextureFormat.ARGB32, false, true, new IntPtr(pointer));
            Debug.Log("Create texture null : " + (t == null).ToString());
            androidNativeCamActivity.Call("checkGlError", "UnityCreateTexture");
            //tex.Apply();
            plane.GetComponent<Renderer>().material.mainTexture = t;
            androidNativeCamActivity.Call("checkGlError", "UnityUpdateTexture");
            Byte[] bytes = t.GetRawTextureData();
            FileStream img = File.OpenWrite("/sdcard/testWrite.png");
            img.Write(bytes, 0, bytes.Length);
            Debug.Log("Write " + bytes.Length + " bytes");
            img.Flush();
            img.Close();
        }
        if (pic2)
        {
            pic2 = false;
            Debug.Log("pic 2 clicked");
            Debug.Log("Unity update texture");
            int pointer = androidNativeCamActivity.Call<int>("updateTexture2");
        }


    }

    public void setPic1()
    {
        pic1 = true;
    }

    public void setPic2()
    {
        pic2 = true;
    }

}