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
        //Assert.IsNotNull(androidNativeCam);
        Debug.Log("AndroidJavaClass null " + (androidNativeCam == null).ToString());
        androidNativeCamActivity = androidNativeCam.GetStatic<AndroidJavaObject>("currentActivity");
        Debug.Log("Activity package " + androidNativeCamActivity.Call<string>("getLocalClassName"));

        //Assert.IsNotNull(androidNativeCamActivity);

        //Debug.Log("AndroidJavaObj null " + (androidNativeCamActivity == null).ToString());
        androidNativeCam = new AndroidJavaClass("com.xiaozhikang.test.TestClass");
        //androidNativeCamActivity.Call("setmContext", androidNativeCamActivity);

        ////start cam (this will generate a texture on the java side)
        //int nativeTextureID = androidNativeCamActivity.Call<int>("startCamera");
        //texWidth = androidNativeCamActivity.Call<int>("getPreviewSizeWidth");
        //texHeight = androidNativeCamActivity.Call<int>("getPreviewSizeHeight");

        //Assert.IsTrue(nativeTextureID > 0, "nativeTextureID=" + nativeTextureID);
        //Assert.IsTrue(nativeTextureID > 0, "width=" + texWidth);
        //Assert.IsTrue(nativeTextureID > 0, "height=" + texHeight);

        //camTexture = Texture2D.CreateExternalTexture(texWidth, texHeight, TextureFormat.ARGB32, false, true, new IntPtr(nativeTextureID));
        //plane.GetComponent<Renderer>().material.mainTexture = camTexture; // TODO this line causes the error
#endif
    }

    // Update is called once per frame
    void Update()
    {
        //Debug.Log("Unity update texture");
        //if (texWidth != camTexture.width || texHeight != camTexture.height)
        //{
        //    Debug.LogWarning("texWidth != camTexture.width || texHeight != camTexture.height");
        //    camTexture.Resize(texWidth, texHeight, TextureFormat.ARGB32, false);
        //    camTexture.Apply();
        //}
        //androidNativeCamActivity.Call("updateTexture");

        //plane.GetComponent<Renderer>().material.mainTexture = camTexture;
        //transform.Rotate(Time.deltaTime * 10, Time.deltaTime * 30, 0);

        //if (Input.GetKey(KeyCode.Escape))
        //{
        //    Debug.Log("Back clicked");
        //    Texture2D tex = new Texture2D(440, 220, TextureFormat.ARGB32, false);
        //    Debug.Log("Unity update texture");
        //    byte[] bytes = androidNativeCam.CallStatic<byte[]>("updateTexture", tex.GetRawTextureData().Length);
        //    Debug.Log("Returned bytes length : " + bytes.Length);
        //    Debug.Log("Bytes length needed : " + tex.GetRawTextureData().Length);
        //    GL.InvalidateState();
        //    Debug.Log(tex.format.ToString());
        //    tex.LoadRawTextureData(bytes);
        //    tex.Apply();
        //    plane.GetComponent<Renderer>().material.mainTexture = tex;
        //    FileStream img = File.OpenWrite("/sdcard/testWrite.bmp");
        //    img.Write(bytes, 0, bytes.Length);
        //    img.Flush();
        //    img.Close();
        //}

        //if (Input.GetKey(KeyCode.Escape))
        //{
        //    Debug.Log("Back clicked");
        //    Debug.Log("Unity update texture");
        //    int pointer = androidNativeCam.CallStatic<int>("updateTexture");
        //    //GL.InvalidateState();
        //    Texture2D tex = Texture2D.CreateExternalTexture(440, 220, TextureFormat.ARGB32, false, true, new IntPtr(pointer));

        //    androidNativeCam.CallStatic("checkGlError", "UnityCreateTexture");
        //    //tex.Apply();
        //    plane.GetComponent<Renderer>().material.mainTexture = tex;
        //    androidNativeCam.CallStatic("checkGlError", "UnityUpdateTexture");
        //}

        if (Input.GetKey(KeyCode.Escape))
        {
            //Debug.Log("Back clicked");
            //Debug.Log("Unity update texture");
            //androidNativeCamActivity.Call("checkGlError", "UnityBeforeUpdate");
            //Texture2D t = new Texture2D(440, 220, TextureFormat.ARGB32, false, true);
            //androidNativeCamActivity.Call("checkGlError", "UnityNewTexture");
            //int pointer = androidNativeCamActivity.Call<int>("updateTexture");
            ////GL.InvalidateState();
            //t = Texture2D.CreateExternalTexture(440, 220, TextureFormat.ARGB32, false, true, new IntPtr(pointer));
            //androidNativeCamActivity.Call("checkGlError", "UnityCreateTexture");
            ////tex.Apply();
            //plane.GetComponent<Renderer>().material.mainTexture = t;
            //androidNativeCamActivity.Call("checkGlError", "UnityUpdateTexture");
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
        //Debug.Log("pic 1 clicked");
        //Debug.Log("Unity update texture");
        //androidNativeCamActivity.Call("checkGlError", "UnityBeforeUpdate");
        //Texture2D t;
        //androidNativeCamActivity.Call("checkGlError", "UnityNewTexture");
        //int pointer = androidNativeCamActivity.Call<int>("updateTexture");
        ////GL.InvalidateState();
        //t = Texture2D.CreateExternalTexture(440, 220, TextureFormat.ARGB32, false, true, new IntPtr(pointer));
        //androidNativeCamActivity.Call("checkGlError", "UnityCreateTexture");
        ////tex.Apply();
        ////plane.GetComponent<Renderer>().material.mainTexture = t;
        //androidNativeCamActivity.Call("checkGlError", "UnityUpdateTexture");
        pic1 = true;
    }

    public void setPic2()
    {
        //Debug.Log("pic 2 clicked");
        //Debug.Log("Unity update texture");
        //int pointer = androidNativeCamActivity.Call<int>("updateTexture2");
        pic2 = true;
    }

    //void OnGUI()
    //{
    //    GUI.Label(new Rect(10, 10, Screen.width - 20, Screen.height - 20), msg);
    //}
}