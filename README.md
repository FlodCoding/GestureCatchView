# GestureCatchView [![GestureCatchView](https://jitpack.io/v/FlodCoding/GestureCatchView.svg)](https://jitpack.io/#FlodCoding/GestureCatchView)

Catching gesture and load gesture with animation, similar to [GestureOverlayView](https://developer.android.com/reference/android/gesture/GestureOverlayView)

## Feature
   * Collecting mutiple gesture
   * Load gesture list or gesture path 
   * fade path anim
   * Custom path color or width
   
   
## How to install [中文说明]()

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


  
 
## Demo [download demo]()



### Demo screenshot

![1.gif](https://upload-images.jianshu.io/upload_images/7565394-e3b0ccd9ff0c8497.gif?imageMogr2/auto-orient/strip)


![2.gif](https://upload-images.jianshu.io/upload_images/7565394-f50e9bab17cd6b02.gif?imageMogr2/auto-orient/strip)


## Basic usage

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



### Callback

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
enabled            |boolean    |true |disable catching any gesture
globalPoint   |boolean    |false  | according to the screen as coordinates，or view
pathWidth |dimension|15px|path stroke width
pathColor |color| black|path color
pathDrawLayer|enum<br>(Top,Bottom)|Top| draw layer,Top or bottom
fadeEnabled|boolean    |true  | enable fade path
fadeStyle|enum<br>(Delay,Next,Keep)|Delay| Delay:start fade after delay<br>Next:start fade after next gesture<br>Keep:keep all
fadeDelay|integer|0|set delay time when fadeStyle is Delay 
fadeDuration|integer|1500|set fade animation time
tapMaxLimit|dimension|15px|lower than the set value is Tap or Press<br>(GestureCatchView.Type:Gesture, Tap, LongPress)
longPressDuration|integer|1500|minimum time to trigger longPress when gesture type is LongPress
### Public Func
Method name|Parameter|Description
---|:--:|---:
startCollect |- |start collect gesture list
stopCollect |- |stop collect gesture and get list
clear |-  |clear all gesture path on view
loadGestureInfoWithAnim|ArrayList<GestureInfo> |load GestureInfo list, simulate input gestures
loadGestureInfo|ArrayList<GestureInfo>|load GestureInfo list without anim
loadPath|ArrayList<Path>|load path