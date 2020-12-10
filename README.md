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


  
 
## Demo [Click me to download the apk]()

![]()

### Demo screenshot

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
globalPoint   |boolean    |false  | According to the screen as coordinates，or view


### Public Func
Method name|Parameter description|default value|Description
---|:--:|:---:|---:
start()                             |-                  |-      |Start 