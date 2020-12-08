@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.flod.widget.gesture

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.gesture.Gesture
import android.gesture.GesturePoint
import android.gesture.GestureStroke
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import androidx.annotation.IntDef
import androidx.core.graphics.alpha
import com.flod.gesture.R
import com.flod.widget.gesture.GestureCatchView.PathKeepStyle.Companion.Duration
import com.flod.widget.gesture.GestureCatchView.PathKeepStyle.Companion.Keep
import com.flod.widget.gesture.GestureCatchView.PathKeepStyle.Companion.Next


/**
 * Create by Flood on 2020-12-08
 * Desc:
 * 1、点击点 √
 * 2、移动线  √
 * 3、笔画渐变 √
 * 4、资源释放 √
 * 5、多指处理
 * 6、载入手势或路径
 */
class GestureCatchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val DEBUG = true
        private const val TAG = "GestureCatchView"

    }


    enum class GestureType {
        Gesture, Tap, LongPress
    }

    //kotlin not working
    @IntDef(Duration, Next, Keep)
    @Retention(AnnotationRetention.SOURCE)
    annotation class PathKeepStyle {
        companion object {
            const val Duration = 0
            const val Next = 1
            const val Keep = 2
        }
    }

    //attrs
    var pointInScreen: Boolean       //true:以屏幕为坐标系   false:以View为坐标系
    var longPressDuration: Long      //长按触发时间
    var pathDrawLayer: Boolean

    var minPath: Int                //path小于这个长度为点击事件
    var pathWidth: Int
    var pathColor: Int


    @PathKeepStyle
    var pathKeepStyle: Int
    var pathDuration: Long

    var fadeEnabled: Boolean
    var fadeDuration: Long


    private var isRecoding = false
    private var startTimeTemp: Long = 0   //开始记录手势的时间点（用来得到开始时间到手指触摸的时间）

    private var curGestureItem: GestureItem? = null
    private val gestureItemList: ArrayList<GestureItem> = ArrayList()
    private val gestureInfoList: ArrayList<GestureInfo> = ArrayList()

    var onGestureListener: OnGestureListener? = null


    init {
        setWillNotDraw(false)

        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.GestureCatchView)
        pointInScreen = typeArray.getBoolean(R.styleable.GestureCatchView_globalPoint, true)
        longPressDuration = typeArray.getInteger(R.styleable.GestureCatchView_longPressDuration, 1500).toLong()
        minPath = typeArray.getDimensionPixelSize(R.styleable.GestureCatchView_minPath, 12)
        pathWidth = typeArray.getDimensionPixelSize(R.styleable.GestureCatchView_pathWidth, 15)
        pathColor = typeArray.getColor(R.styleable.GestureCatchView_pathColor, Color.BLACK)
        pathKeepStyle = typeArray.getInteger(R.styleable.GestureCatchView_pathKeepStyle, Duration)
        pathDuration = typeArray.getInteger(R.styleable.GestureCatchView_pathDuration, 0).toLong()
        pathDrawLayer = typeArray.getInteger(R.styleable.GestureCatchView_pathDrawLayer, 0) == 0

        fadeEnabled = typeArray.getBoolean(R.styleable.GestureCatchView_fadeEnabled, true)
        fadeDuration = typeArray.getInteger(R.styleable.GestureCatchView_fadeDuration, 1500).toLong()

        typeArray.recycle()
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (pathDrawLayer) {
            super.dispatchDraw(canvas)
            dispatchGestureItemDraw(canvas)
        } else {
            dispatchGestureItemDraw(canvas)
            super.dispatchDraw(canvas)
        }
    }


    private fun dispatchGestureItemDraw(canvas: Canvas) {
        for (item in gestureItemList) {
            item.draw(canvas)
        }
    }


    override fun onDetachedFromWindow() {
        releaseAll()
        super.onDetachedFromWindow()

    }

    private fun releaseAll() {
        for (item in gestureItemList) {
            item.release()
        }
        curGestureItem?.release()
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //super.onTouchEvent(event)
        processEvent(event)
        return true
    }


    private fun processEvent(event: MotionEvent) {
        val itemTemp = curGestureItem

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // actionDown(event)
                if (pathKeepStyle == Next) {
                    //其他的Path开始消失
                    for (item in gestureItemList) {
                        item.fadePath()
                    }
                }

                val gestureItem = GestureItem()
                gestureItem.startPath(event)
                curGestureItem = gestureItem
                gestureItemList.add(gestureItem)

            }

            MotionEvent.ACTION_MOVE -> {
                itemTemp?.addToPath(event)

            }

            MotionEvent.ACTION_UP -> {
                itemTemp?.endPath(event)
            }

        }

        invalidate()

    }


    fun startRecord() {
        // 清理旧的手势
        clear()

        isRecoding = true
        startTimeTemp = System.currentTimeMillis()
    }


    fun stopRecord(): ArrayList<GestureInfo> {
        isRecoding = false
        startTimeTemp = 0
        return getGestureInfo()
    }


    fun clear() {
        releaseAll()
        gestureItemList.clear()
        gestureInfoList.clear()
        invalidate()
    }

    fun getGestureInfo(): ArrayList<GestureInfo> {
        return ArrayList(gestureInfoList)
    }

    fun loadGestureInfo(list: ArrayList<GestureInfo>) {
        val location = IntArray(2)
        getLocationOnScreen(location)
        list.forEach {
            val path = it.gesture.toPath()
            if (pointInScreen) {
                path.offset(-location[0].toFloat(), -location[1].toFloat())
            }
            gestureItemList.add(GestureItem(path))
        }
        invalidate()
    }

    fun loadPath(path: ArrayList<Path>) {
        path.forEach {
            gestureItemList.add(GestureItem(it))
        }
        invalidate()
    }


    private val loadingMotionEvent = arrayListOf<MotionEvent>()
    private var loadingMotionEventIndex = -1
    private val loadingMotionEventRunnable = Runnable {
        if (loadingMotionEventIndex >= 0) {
            val event = loadingMotionEvent[loadingMotionEventIndex]
            processEvent(event)

            loadingMotionEventIndex++
            if (loadingMotionEventIndex == loadingMotionEvent.size) return@Runnable


            val nextEvent = loadingMotionEvent[loadingMotionEventIndex]


            val delayTime = nextEvent.eventTime - event.eventTime
            postDelayed(loadingMotionEventRunnable,delayTime)
        }

    }

    fun createFakeMotionEvent(list: ArrayList<GestureInfo>) {
        val uptime = SystemClock.uptimeMillis()

        list.forEach {
            loadingMotionEvent.addAll(it.points)
        }

        loadingMotionEventIndex = 0
        post(loadingMotionEventRunnable)

    }


    fun loadGestureInfoWithAnim(list: ArrayList<GestureInfo>) {
        val location = IntArray(2)
        getLocationOnScreen(location)
        list.forEach {
            val path = it.gesture.toPath()
            if (pointInScreen) {
                path.offset(-location[0].toFloat(), -location[1].toFloat())
            }
            gestureItemList.add(GestureItem(path))
        }
        invalidate()
    }


    open class OnGestureListener {
        open fun onGesturing(gesturePoint: GesturePoint) {

        }

        open fun onGestureFinish(gestureType: GestureType, gestureInfo: GestureInfo) {

        }

    }

    inner class GestureItem {
        private val animator = ValueAnimator.ofFloat(1f, 0f)
        private val path: Path = Path()
        private val paint = Paint()
        private var lastX: Float = 0f
        private var lastY: Float = 0f
        private val pointBuffer: ArrayList<GesturePoint> = ArrayList()
        private val motionEventBuffer: ArrayList<MotionEvent> = ArrayList()

        private val delayTime: Long =
            if (!isRecoding || startTimeTemp == 0L) 0 else System.currentTimeMillis() - startTimeTemp
        private var gesture: Gesture? = null
        private var isActionDown = false
        private var isLongPress = false
        private var drawRequest = true


        private val longPressRunnable: Runnable = Runnable {
            gesture?.run {
                addStroke(GestureStroke(pointBuffer))
                onGestureDone(GestureType.LongPress, this)
                isLongPress = true
            }
        }

        constructor()

        constructor(path: Path) {
            this.path.set(path)
        }

        init {
            //init
            paint.run {
                strokeWidth = pathWidth.toFloat()
                color = pathColor
                style = Paint.Style.STROKE
                strokeJoin = Paint.Join.ROUND  //拐角圆形
                strokeCap = Paint.Cap.ROUND    //开始和结尾加入圆形
                isAntiAlias = true
            }

            //initAnimator
            animator.apply {
                interpolator = AccelerateInterpolator()
                duration = fadeDuration
                addUpdateListener {
                    paint.alpha = (pathColor.alpha * it.animatedValue as Float).toInt()
                    paint.strokeWidth = pathWidth * it.animatedValue as Float
                    invalidate()
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        drawRequest = false
                    }
                })
            }
        }


        fun startPath(event: MotionEvent) {
            //如果当前不是在录制手势，那以此为开始手势时间点
            if (!isRecoding)
                startTimeTemp = System.currentTimeMillis()

            val x = event.x
            val y = event.y

            path.rewind()
            path.moveTo(x, y)
            path.lineTo(x, y)

            addToPointBuffer(event)

            lastX = x
            lastY = y

            if (gesture == null) {
                gesture = Gesture()
                //开始计时，如果按下后不动，一定时间后触发长按
                isActionDown = true
                postDelayed(longPressRunnable, longPressDuration)
            }
        }

        fun addToPath(event: MotionEvent) {
            removeLongPressCallback()

            val x = event.x
            val y = event.y

            //取上一个点为控制点
            val controlX = lastX
            val controlY = lastY
            //上一个点与当前点的中点为结束点
            val endX = (controlX + x) / 2
            val endY = (controlY + y) / 2
            //构成光滑曲线
            path.quadTo(lastX, lastY, endX, endY)

            addToPointBuffer(event)

            lastX = x
            lastY = y

            onGestureListener?.onGesturing(pointBuffer.last())
        }

        fun endPath(event: MotionEvent) {

            addToPointBuffer(event)
            removeLongPressCallback()

            //如果手指抬起时小于最小的手势范围认为是Tab
            val gesture = gesture
            if (gesture != null && !isLongPress) {
                gesture.addStroke(GestureStroke(pointBuffer))
                if (gesture.length < minPath) {
                    onGestureDone(GestureType.Tap, gesture)
                } else {
                    onGestureDone(GestureType.Gesture, gesture)
                }

            }

            isLongPress = false

        }


        private fun addToPointBuffer(event: MotionEvent) {

            val pointX = if (pointInScreen) event.rawX else event.x
            val pointY = if (pointInScreen) event.rawY else event.y

            val gesturePoint = GesturePoint(pointX, pointY, event.eventTime)
            pointBuffer.add(gesturePoint)
            motionEventBuffer.add(event)
        }

        private fun onGestureDone(gestureType: GestureType, gesture: Gesture) {
            //开始消失动画
            if (fadeEnabled && pathKeepStyle == Duration) {
                animator.startDelay = pathDuration
                animator.start()
            }


            val curTime = System.currentTimeMillis()

            val gestureInfo = GestureInfo(
                gestureType = gestureType,
                gesture = gesture,
                pathColor = pathColor,
                delayTime = delayTime,
                duration = curTime - startTimeTemp - delayTime,
            )

            if (isRecoding)
                gestureInfoList.add(gestureInfo)

            onGestureListener?.onGestureFinish(gestureType, gestureInfo)


            startTimeTemp = curTime
            pointBuffer.clear()
            this.gesture = null

        }


        fun fadePath() {
            if (fadeEnabled && !animator.isStarted && drawRequest) {
                animator.startDelay = pathDuration
                animator.start()
            }
        }

        private fun removeLongPressCallback() {
            if (isActionDown) {
                //移除长按触发
                isActionDown = false
                handler.removeCallbacks(longPressRunnable)
            }
        }


        fun draw(canvas: Canvas) {
            if (drawRequest)
                canvas.drawPath(path, paint)
        }

        fun release() {
            animator.cancel()
            animator.removeAllUpdateListeners()
            removeLongPressCallback()
        }


    }


}


