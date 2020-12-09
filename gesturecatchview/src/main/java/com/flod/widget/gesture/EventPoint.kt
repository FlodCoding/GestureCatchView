package com.flod.widget.gesture

import android.gesture.GesturePoint
import android.os.Parcel
import android.os.Parcelable
import android.view.MotionEvent

/**
 * Create by Flood on 2020-12-09
 * Desc:
 */
data class EventPoint(
    val x: Float,
    val y: Float,
    val rawX: Float,
    val rawY: Float,
    val action: Int,
    val timestamp: Long
) : Parcelable {


    constructor(motionEvent: MotionEvent) : this(
        motionEvent.x,
        motionEvent.y,
        motionEvent.rawX,
        motionEvent.rawY,
        motionEvent.action,
        motionEvent.eventTime
    )


    constructor(parcel: Parcel) : this(
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readLong()
    )

    fun obtain(): MotionEvent {
        return MotionEvent.obtain(timestamp, timestamp, action, x, y, 0)
    }



    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(x)
        parcel.writeFloat(y)
        parcel.writeFloat(rawX)
        parcel.writeFloat(rawY)
        parcel.writeInt(action)
        parcel.writeLong(timestamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EventPoint> {
        override fun createFromParcel(parcel: Parcel): EventPoint {
            return EventPoint(parcel)
        }

        override fun newArray(size: Int): Array<EventPoint?> {
            return arrayOfNulls(size)
        }
    }
}
