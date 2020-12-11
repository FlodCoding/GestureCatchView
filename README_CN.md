# GestureCatchView [![GestureCatchView](https://jitpack.io/v/FlodCoding/GestureCatchView.svg)](https://jitpack.io/#FlodCoding/GestureCatchView)

可用来捕获手势，模拟手势输入。 类似于[GestureOverlayView](https://developer.android.com/reference/android/gesture/GestureOverlayView)

## 特性
   * 可收集多个手势
   * 模拟手势输入
   * 笔画渐变消失
   * 自定义笔画颜色、粗细、消失时间
   
   
## 安装

root directory build.gradle
```
	allprojects {
	
		  repositories {
		  	...
		  	maven { url 'https://jitpack.io' }
		  	 
		  }
	}
```

App module build.gradle 

``` 
 	dependencies {
		//need Androidx and kotlin
 		implementation 'com.github.FlodCoding:GestureCatchView:1.0.0-alpha01'
		
	}
```


  
 
## Demo [download demo](https://github.com/FlodCoding/GestureCatchView/releases/download/1.0.0-alpha01/app-debug.apk)

![image.png](https://upload-images.jianshu.io/upload_images/7565394-46cbc2d2026a29dd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/300)

### Demo screenshot

![1.gif](https://upload-images.jianshu.io/upload_images/7565394-5beb2b690a419445.gif?imageMogr2/auto-orient/strip)


![2.gif](https://upload-images.jianshu.io/upload_images/7565394-3f0f4669d5279a07.gif?imageMogr2/auto-orient/strip)


## 基本用法

### XML
```
<com.flod.widget.gesture.GestureCatchView
        android:id="@+id/gestureView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:fadeStyle="Delay"
        app:pathColor="@android:color/tab_indicator_text"
        app:pathDrawLayer="TOP"
        app:pathWidth="10dp">
        
</com.flod.widget.gesture.GestureCatchView>
```
### Code


```
// Start collect
gestureView.startCollect()
// Stop collect and get getsture list
val gestureInfoList = gestureView.stopCollect()

// Clear all path
gestureView.clear() 
```



### 回调

```
gestureView.onGestureListener = object : GestureCatchView.OnGestureListener() {
    override fun onGesturing(motionEvent: MotionEvent) {

    }

    override fun onGestureFinish(gestureInfo: GestureInfo) {
       
    }

    override fun onCollectionDone(list: ArrayList<GestureInfo>) {
        
    }
}


```

## Attribute
### XML
Attribute name|type|Default value|Description
---|:--:|:---:|---:
enabled         |boolean                |true |关闭手势的捕获
globalPoint     |boolean                |false  | 每个输入点的坐标依据 true:以屏幕为坐标 false:以view为原点坐标 
pathWidth       |dimension              |15px| 笔画路径的粗细
pathColor       |color                  |black|笔画路径的颜色
pathDrawLayer   |enum<br>(Top,Bottom)   |Top| 绘制的图层，绘制于子视图的顶部或者底部
fadeEnabled     |boolean                |true  | 是否开启渐变效果
fadeStyle       |enum<br>(Delay,Next,Keep)|Delay| Delay:延迟一会后开始渐变消失<br>Next:下一个手势开始渐变消失<br>Keep:保留所有的
fadeDelay       |integer                |0|当fadeStyle=Delay 时，设置延时的时间
fadeDuration    |integer                |1500|渐变消失的动画时间
tapMaxLimit     |dimension              |15px|低于该值被视为点击或者长按<br>(GestureCatchView.Type:Gesture, Tap, LongPress)
longPressDuration|integer               |1500|小于tapMaxLimit的手势，并且高于该值被视为长按
### Public Func
Method name|Parameter|Description
---|:--:|---:
startCollect |- |开始收集手势
stopCollect |- |停止收集手势，并且得到一个list
clear |-  |清除view上的笔画路径
loadGestureInfoWithAnim|ArrayList<GestureInfo> |加载一个收集的手势列表到view中，并且模拟手势的输入
loadGestureInfo|ArrayList<GestureInfo>|加载一个手势集
loadPath|ArrayList<Path>|load path|加载一个Path集