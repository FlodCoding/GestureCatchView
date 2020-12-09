package com.flod.gesturecatchview

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.flod.widget.gesture.GestureCatchView
import com.flod.widget.gesture.GestureInfo
import com.flod.widget.gesture.PathDrawLayer
import com.flod.widget.gesture.PathFadeStyle
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    private val randomColor:Int
    get() {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        rgPathFadeStyle.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbDelay -> gestureView.fadeStyle = PathFadeStyle.Delay
                R.id.rbNext -> gestureView.fadeStyle = PathFadeStyle.Next
                R.id.rbKeep -> gestureView.fadeStyle = PathFadeStyle.Keep
            }
        }

        rgPathDrawLayer.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbTop){
                gestureView.pathDrawLayer = PathDrawLayer.Top
                imBg.isVisible = false
            }else{
                gestureView.pathDrawLayer = PathDrawLayer.Bottom
                imBg.isVisible = true
            }
        }


        gestureView.pathColor = ContextCompat.getColor(this, android.R.color.tab_indicator_text)
        tbPathColor.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when {
                checkedId == R.id.bt1 && isChecked -> gestureView.pathColor = Color.RED

                checkedId == R.id.bt2 && isChecked -> gestureView.pathColor = Color.BLUE

                checkedId == R.id.bt3 && isChecked -> {

                    gestureView.pathColor = randomColor

                }
                else -> gestureView.pathColor = ContextCompat.getColor(this, android.R.color.tab_indicator_text)
            }
        }

        sbFadeDelay.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                gestureView.fadeDelay = progress.toLong()
            }
        })

        sbFadeDuration.progress = 1500
        sbFadeDuration.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                gestureView.fadeDuration = progress.toLong()
            }
        })

        sbPathWidth.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                gestureView.fadeDelay = progress.toLong()
            }
        })

        btCatchingGesture.setOnClickListener {

            if (!btCatchingGesture.isSelected) {
                btCatchingGesture.isEnabled = false
                gestureView.startRecord()
                countDownTimer.start()

            } else {
                gestureView.loadGestureInfoWithAnim(gestureInfoList)
                btCatchingGesture.text = "Catching Gesture"
            }
            btCatchingGesture.isSelected = !btCatchingGesture.isSelected
        }




        gestureView.onGestureListener =
                object : GestureCatchView.OnGestureListener() {
                    override fun onGesturing(motionEvent: MotionEvent) {

                    }

                    override fun onGestureFinish(gestureInfo: GestureInfo) {
                        if ((bt3 as MaterialButton).isChecked) gestureView.pathColor = randomColor

                        Log.d("MainActivity", "onGestureFinish: ${gestureInfo.gestureType}")
                    }

                    override fun onCollectionDone(list: ArrayList<GestureInfo>) {
                        gestureInfoList = list
                    }
                }


    }

    private var gestureInfoList = arrayListOf<GestureInfo>()
    private val countDownTimer = object : CountDownTimer(6000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            btCatchingGesture.text = "Collecting ${millisUntilFinished / 1000} s"
        }


        override fun onFinish() {
            gestureView.stopRecord()
            btCatchingGesture.text = "Load Gesture"
            btCatchingGesture.isEnabled = true
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
}


open class OnSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}