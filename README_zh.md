# UnityExternalTexture
使用安卓原生代码生成、更新texture，并将其显示在Unity场景中。

##使用环境
我使用的环境：

* Windows 10
* Android Studio 1.5 (后升级到2.0)
* Unity3D 5.3.4f1 Pro
* 运行手机: Samsung S6 and OnePlus in Android 6.0.1

##编译
使用Android Studio打开`AndroidProject`

Make Project.

将`\AndroidProject\app\build\outputs\aar\app-debug.aar`作为压缩包打开 
删除`libs\classes.jar`

将上述`app-debug.aar`文件复制（替换）到`\UnityProject\Assets\Plugin\Android\bin`中
(或者其它目录，但记得删除上述目录下的aar文件)。

使用Unity打开`UnityProject`

Build and Run

*Notice* 安装后会出现两个应用图标，其中一个会进入一个错误的场景，此场景中功能不能正常演示。确认通过第二个图标进入应用

##运行说明
点击左上角第一个按钮，下方面板会显示一张图片；

点击第二个按钮，面板上的图片会被替换。
###创建Texture
点击第一个按钮后，Unity会调用安卓插件中的`updateTexture`方法，此方法会生成一个texture、将图片渲染上去并返回此texture的指针。
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
Unity将根据这个指针生成一个Texture2D对象，绑定到plane对象上。
```
//Unity code
Texture2D t = Texture2D.CreateExternalTexture(440, 220, TextureFormat.ARGB32, false, true, new IntPtr(pointer));
plane.GetComponent<Renderer>().material.mainTexture = t;
``` 
###更新Texture
按下第二个按钮后，plane上的texture会被更新而不是重新创建一个。
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
###多个应用入口
安装apk之后会出现两个应用图标，而其中一个不能正常工作。

###将Texture保存成文件
尝试在Unity中将外部Texture保存成图片文件，但目前不能读取其中的数据。
             
尝试过的方法：
* GetRawTextureData() 返回的byte数组长度是0
* EncodeToJPG/PNG 同上
* GetPixels32() 返回了合适长度的Color32数组，但里面元素的值全是(0, 0, 0, 0)