package com.flod.gesturecatchview

import android.gesture.GesturePoint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.flod.widget.gesture.GestureCatchView
import com.flod.widget.gesture.GestureInfo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var gestureInfoList = arrayListOf<GestureInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //startActivity(Intent(this,MainActivity2::class.java))


        gestureCatchView.onGestureListener =
            object : GestureCatchView.OnGestureListener() {
                override fun onGestureFinish(gestureType: GestureCatchView.GestureType, gestureInfo: GestureInfo) {
                    Log.d("MainActivity", "onGestureFinish")
                }


                override fun onGesturing(gesturePoint: GesturePoint) {

                }

            }

        btStart.setOnClickListener {
            gestureCatchView.startRecord()
        }

        btStop.setOnClickListener {
            gestureInfoList = gestureCatchView.stopRecord()
        }



        btClear.setOnClickListener {
            gestureCatchView.clear()
        }

        btLoad.setOnClickListener {
            gestureCatchView.loadGestureInfo(gestureInfoList)
        }
    }
}
