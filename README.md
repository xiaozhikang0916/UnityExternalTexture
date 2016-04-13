# UnityExternalTexture
Create and update texture in Android Java code, and show it in Unity3D scene.

[中文README](https://github.com/xiaozhikang0916/UnityExternalTexture/blob/master/README_zh.md)

##Environment
My building environment:

* Windows 10
* Android Studio 1.5 (Then update to 2.0)
* Unity3D 5.3.4f1 Pro
* Target phone: Samsung S6 and OnePlus in Android 6.0.1

##Build
Open the `AndroidProject` in Android Studio.

Make Project.

Open `\AndroidProject\app\build\outputs\aar\app-debug.aar` as zip file 
and delete `libs\classes.jar`

Copy(or replace) the above `app-debug.aar` file to `\UnityProject\Assets\Plugin\Android\bin`
(or anywhere you want but the `.aar` file can't be duplicated)

Open the `UnityProject` in Unity.

Build apk and run in your Android phone

*Notice* When installed, there will be 2 icons of this app, one of which leads to a wrong Activity. Make sure you start this app from the second icon. 

##How it works
Click the first button in the top-left corner, the plane in the buttom will show a picture.

Click the second button, the plane will show another picture.
###Create
When you click the first button, Unity will call a method defined in Android Java code `updateTexture`, in which method a new texture will be created and rendererd with a picture,
then returnd as a pointer.
```
//Unity code
int pointer = androidNativeCamActivity.Call<int>("updateTexture");
```
```
//Java code 
public int updateTexture(){
    nativeTexturePointer = createExternalTexture();
    ...
    bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.test, options);
    ...
    return nativeTexturePointer;
}
```
Then a texture will be created by the pointer in Unity and attach to the plane
```
//Unity code
Texture2D t = Texture2D.CreateExternalTexture(440, 220, TextureFormat.ARGB32, false, true, new IntPtr(pointer));
plane.GetComponent<Renderer>().material.mainTexture = t;
``` 
###Update
When the second button is clicked, the texture of the plane will be updated but not creating a new one.
```
//Unity code
androidNativeCamActivity.Call("updateTexture2");
```
```
//Java code
public void updateTexture2() {
    ...
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, nativeTexturePointer);
    ...
    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
    ...
}    
```
##Todo
###Duplicated Launcher Activities
Now it has 2 icons and launcher activities, one of which leads to a wrong scene and can't use the functions.

###Save texture as file in Unity
Try to save the external texture as file in Unity, but cannot read the data in it.
             
Tried: 
* GetRawTextureData() returns byte array length 0
* EncodeToJPG/PNG returns byte array length 0
* GetPixels32() returns Color array with all nodes valued (0, 0, 0, 0)