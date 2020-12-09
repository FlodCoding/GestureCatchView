package com.flod.gesturecatchview

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.flod.widget.gesture.GestureCatchView
import com.flod.widget.gesture.GestureInfo
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var gestureInfoList = arrayListOf<GestureInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gestureCatchView.onGestureListener =
            object : GestureCatchView.OnGestureListener() {
                override fun onGestureFinish(gestureInfo: GestureInfo) {
                    Log.d("MainActivity", "onGestureFinish: ${gestureInfo.gestureType}")
                    val rnd = Random()
                    gestureCatchView.pathColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                }


                override fun onGesturing(motionEvent: MotionEvent) {

                }

            }

    }
}
