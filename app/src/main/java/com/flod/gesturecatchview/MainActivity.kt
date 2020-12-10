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

    private val randomColor: Int
        get() {
            val rnd = Random()
            return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        tbPathFadeStyle.addOnButtonCheckedListener { _, checkedId, _ ->
            when (checkedId) {
                R.id.btDelay -> gestureView.fadeStyle = PathFadeStyle.Delay
                R.id.btNext -> gestureView.fadeStyle = PathFadeStyle.Next
                R.id.btKeep -> gestureView.fadeStyle = PathFadeStyle.Keep
            }
        }

        tbPathDrawLayer.addOnButtonCheckedListener { _, checkedId, _ ->
            if (checkedId == R.id.btTop) {
                gestureView.pathDrawLayer = PathDrawLayer.Top
                imBg.isVisible = false
            } else {
                gestureView.pathDrawLayer = PathDrawLayer.Bottom
                imBg.isVisible = true
            }
        }


        val defaultColor = ContextCompat.getColor(this, android.R.color.tab_indicator_text)
        gestureView.pathColor = defaultColor
        tbPathColor.addOnButtonCheckedListener { _, checkedId, _ ->
            when (checkedId) {
                R.id.btDefault -> gestureView.pathColor = defaultColor

                R.id.btBlue -> gestureView.pathColor = Color.BLUE

                R.id.btRandom -> gestureView.pathColor = randomColor
            }
        }

        sbFadeDelay.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                gestureView.fadeDelay = progress.toLong()
            }
        })


        sbFadeDuration.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                gestureView.fadeDuration = progress.toLong()
            }
        })


        sbPathWidth.setOnSeekBarChangeListener(object : OnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                gestureView.pathWidth = progress + 20
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

        btReset.setOnClickListener {
            gestureView.clear()
            (btDefault as MaterialButton).isChecked = true
            (btTop as MaterialButton).isChecked = true
            (btDelay as MaterialButton).isChecked = true
            sbPathWidth.progress = 0
            sbFadeDelay.progress = 0
            sbFadeDuration.progress = 1500
            gestureInfoList.clear()

        }




        gestureView.onGestureListener =
            object : GestureCatchView.OnGestureListener() {
                override fun onGesturing(motionEvent: MotionEvent) {

                }

                override fun onGestureFinish(gestureInfo: GestureInfo) {
                    if ((btRandom as MaterialButton).isChecked) gestureView.pathColor = randomColor

                    Log.d("MainActivity", "onGestureFinish: ${gestureInfo.gestureType}")
                }

                override fun onCollectionDone(list: ArrayList<GestureInfo>) {
                    gestureInfoList = list
                }
            }


    }

    private var gestureInfoList = arrayListOf<GestureInfo>()
    private val countDownTimer = object : CountDownTimer(8000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            btCatchingGesture.text = "Collecting... ${millisUntilFinished / 1000} s"
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