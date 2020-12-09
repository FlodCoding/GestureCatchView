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
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import androidx.core.graphics.alpha
import com.flod.gesture.R


/**
 * Create by Flood on 2020-12-08
 * Desc:
 *
 * 1、点击点 √
 * 2、移动线  √
 * 3、笔画渐变 √
 * 4、资源释放 √
 * 5、载入手势或路径 √
 * 6、多指处理
 * 7、优化绘制放方式移除GestureItem，利用addPath
 * 8、优化下Fade的动画，能否先从尾再到头fade
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

    enum class Type {
        Gesture, Tap, LongPress
    }

    //attrs
    var globalPoint: Boolean            //true:以屏幕为坐标系   false:以View为坐标系

    var tapMaxLimit: Int                //视为点击事件的最大滑动长度
    var longPressDuration: Long         //长按触发时间

    var pathWidth: Int
    var pathColor: Int
    var pathDrawLayer: PathDrawLayer    //路径绘制的层级，Top实在容器的顶层，Bottom为底层


    var fadeStyle: PathFadeStyle
    var fadeDelay: Long
    var fadeEnabled: Boolean
    var fadeDuration: Long


    private var collecting = false
    private var timestemp: Long = 0   //开始记录手势的时间点（用来得到开始时间到手指触摸的时间）

    private var curGestureItem: GestureItem? = null
    private val gestureItemList: ArrayList<GestureItem> = ArrayList()
    private val gestureInfoList: ArrayList<GestureInfo> = ArrayList()


    private val fakeMotionEvent = arrayListOf<EventPoint>()
    private var fakeMotionEventIndex = -1
    private val fakeMotionEventRunnable = object : Runnable {
        override fun run() {
            if (fakeMotionEventIndex >= 0) {
                val event = fakeMotionEvent[fakeMotionEventIndex].obtain()
                processEvent(event)

                fakeMotionEventIndex++
                if (fakeMotionEventIndex == fakeMotionEvent.size) {
                    fakeMotionEventIndex = -1
                    return
                }


                val nextEvent = fakeMotionEvent[fakeMotionEventIndex].obtain()


                val delayTime = nextEvent.eventTime - event.eventTime

                postDelayed(this, delayTime)
            }
        }

    }


    var onGestureListener: OnGestureListener? = null

    init {
        setWillNotDraw(false)

        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.GestureCatchView)
        isEnabled =  typeArray.getBoolean(R.styleable.GestureCatchView_android_enabled, true)
        globalPoint = typeArray.getBoolean(R.styleable.GestureCatchView_globalPoint, true)
        longPressDuration = typeArray.getInteger(R.styleable.GestureCatchView_longPressDuration, 1500).toLong()
        tapMaxLimit = typeArray.getDimensionPixelSize(R.styleable.GestureCatchView_tapMaxLimit, 12)
        pathWidth = typeArray.getDimensionPixelSize(R.styleable.GestureCatchView_pathWidth, 15)
        pathColor = typeArray.getColor(R.styleable.GestureCatchView_pathColor, Color.BLACK)
        pathDrawLayer = PathDrawLayer.values()[typeArray.getInteger(R.styleable.GestureCatchView_pathDrawLayer, PathDrawLayer.Top.ordinal)]

        fadeEnabled = typeArray.getBoolean(R.styleable.GestureCatchView_fadeEnabled, true)
        fadeStyle = PathFadeStyle.values()[typeArray.getInteger(R.styleable.GestureCatchView_fadeStyle, PathFadeStyle.Delay.ordinal)]
        fadeDelay = typeArray.getInteger(R.styleable.GestureCatchView_fadeDelay, 0).toLong()
        fadeDuration = typeArray.getInteger(R.styleable.GestureCatchView_fadeDuration, 1500).toLong()

        typeArray.recycle()
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (pathDrawLayer == PathDrawLayer.Top) {
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
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (isEnabled) {
            super.onTouchEvent(event)
            processEvent(event)
            true
        } else super.onTouchEvent(event)

    }

    private fun newGesture(): GestureItem {
        val gestureItem = GestureItem()
        curGestureItem = gestureItem
        gestureItemList.add(gestureItem)
        return gestureItem
    }


    private fun processEvent(event: MotionEvent) {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (fadeStyle == PathFadeStyle.Next) {
                    //其他的Path开始消失
                    for (item in gestureItemList) {
                        item.fadePath()
                    }
                }

                newGesture().startPath(event)
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                if (curGestureItem == null) {
                    newGesture()
                }
                val item = curGestureItem
                item?.run {
                    addToPath(event)
                    invalidate()
                }


            }

            MotionEvent.ACTION_UP -> {
                curGestureItem?.run {
                    endPath(event)
                    invalidate()
                }

            }

        }

    }


    fun startRecord() {
        // 清理旧的手势
        clear()

        collecting = true
        timestemp = System.currentTimeMillis()
    }


    fun stopRecord(): ArrayList<GestureInfo> {
        collecting = false
        timestemp = 0
        onGestureListener?.onCollectionDone(gestureInfoList)
        return ArrayList(gestureInfoList)
    }


    fun clear() {
        releaseAll()
        gestureItemList.clear()
        gestureInfoList.clear()
        invalidate()
    }

    fun loadGestureInfo(list: ArrayList<GestureInfo>) {
        val location = IntArray(2)
        getLocationOnScreen(location)
        list.forEach {
            val path = it.gesture.toPath()
            if (globalPoint) {
                path.offset(-location[0].toFloat(), -location[1].toFloat())
            }
            gestureItemList.add(GestureItem(path))
        }
        invalidate()
    }

    fun loadGestureInfoWithAnim(list: ArrayList<GestureInfo>) {
        if (list.isEmpty()) return

        handler.removeCallbacks(fakeMotionEventRunnable)
        fakeMotionEvent.clear()

        list.forEach {
            fakeMotionEvent.addAll(it.points)
        }

        fakeMotionEventIndex = 0
        post(fakeMotionEventRunnable)
    }

    fun loadPath(path: ArrayList<Path>) {
        path.forEach {
            gestureItemList.add(GestureItem(it))
        }
        invalidate()
    }


    inner class GestureItem {
        private val animator = ValueAnimator.ofFloat(1f, 0f)
        private val path = Path()
        private val paint = Paint()
        private var lastX = 0f
        private var lastY = 0f
        private val motionEventBuffer = arrayListOf<EventPoint>()
        private val createAt = System.currentTimeMillis()

        private val delayTime: Long =
            if (!collecting || timestemp == 0L) 0 else System.currentTimeMillis() - timestemp

        private var drawRequest = true


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
            if (!collecting)
                timestemp = System.currentTimeMillis()

            val x = event.x
            val y = event.y

            path.rewind()
            path.moveTo(x, y)
            path.lineTo(x, y)

            lastX = x
            lastY = y

            motionEventBuffer.add(EventPoint(event))
        }

        fun addToPath(event: MotionEvent) {

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

            lastX = x
            lastY = y

            motionEventBuffer.add(EventPoint(event))

            onGestureListener?.onGesturing(event)
        }

        fun endPath(event: MotionEvent) {

            motionEventBuffer.add(EventPoint(event))

            //如果手指抬起时小于最小的手势范围认为是Tab
            val pathLen = PathMeasure(path, false).length

            if (pathLen < tapMaxLimit) {
                if (System.currentTimeMillis() - createAt < longPressDuration) {
                    onGestureDone(Type.Tap)
                } else {
                    onGestureDone(Type.LongPress)
                }
            } else {
                onGestureDone(Type.Gesture)
            }


        }


        private fun onGestureDone(gestureType: Type) {
            //开始消失动画
            if (fadeEnabled && fadeStyle == PathFadeStyle.Delay) {
                animator.startDelay = fadeDelay
                animator.start()
            }

            val curTime = System.currentTimeMillis()

            val gesture = Gesture().apply {
                val gesturePoints = motionEventBuffer.map {
                    if (globalPoint) {
                        GesturePoint(it.rawX, it.rawY, it.timestamp)
                    } else {
                        GesturePoint(it.x, it.y, it.timestamp)
                    }
                }
                addStroke(GestureStroke(ArrayList(gesturePoints)))
            }

            val gestureInfo = GestureInfo(
                gestureType = gestureType,
                gesture = gesture,
                pathColor = pathColor,
                delayTime = delayTime,
                duration = curTime - timestemp - delayTime,
                points = ArrayList(motionEventBuffer)
            )

            if (collecting)
                gestureInfoList.add(gestureInfo)

            onGestureListener?.onGestureFinish(gestureInfo)

            timestemp = curTime
            motionEventBuffer.clear()
        }


        fun fadePath() {
            if (fadeEnabled && !animator.isStarted && drawRequest) {
                animator.startDelay = fadeDelay
                animator.start()
            }
        }


        fun draw(canvas: Canvas) {
            if (drawRequest)
                canvas.drawPath(path, paint)
        }

        fun release() {
            animator.cancel()
            animator.removeAllUpdateListeners()
        }


    }


    open class OnGestureListener {
        open fun onGesturing(motionEvent: MotionEvent) {

        }

        open fun onGestureFinish(gestureInfo: GestureInfo) {

        }

        open fun onCollectionDone(list: ArrayList<GestureInfo>){

        }

    }
}


