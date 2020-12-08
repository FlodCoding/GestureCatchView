package com.flod.widget.gesture

import android.gesture.Gesture
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.view.MotionEvent
import androidx.annotation.ColorInt


data class GestureInfo(
        val gestureType: GestureCatchView.GestureType,
        val gesture: Gesture,
        @ColorInt val pathColor: Int = Color.BLACK,
        val delayTime: Long = 0,
        val duration: Long,
        val isRawPoint: Boolean,
        val points: ArrayList<MotionEvent>

) : Parcelable {

    @Suppress("UNCHECKED_CAST")
    constructor(parcel: Parcel) : this(
            GestureCatchView.GestureType.valueOf(parcel.readString()!!),
            parcel.readParcelable(Gesture::class.java.classLoader)!!,
            parcel.readInt(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readInt() != 0,
            parcel.readArrayList(MotionEvent::class.java.classLoader) as ArrayList<MotionEvent>

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(gestureType.name)
        parcel.writeParcelable(gesture, flags)
        parcel.writeInt(pathColor)
        parcel.writeLong(delayTime)
        parcel.writeLong(duration)
        parcel.writeInt(if (isRawPoint) 1 else 0)
        parcel.writeTypedList(points)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "GestureInfo(gestureType=$gestureType, gesture=$gesture, delayTime=$delayTime, duration=$duration"
    }

    companion object CREATOR : Parcelable.Creator<GestureInfo> {
        override fun createFromParcel(parcel: Parcel): GestureInfo {
            return GestureInfo(parcel)
        }

        override fun newArray(size: Int): Array<GestureInfo?> {
            return arrayOfNulls(size)
        }
    }
}











