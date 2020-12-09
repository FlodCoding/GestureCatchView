package com.flod.widget.gesture

import android.gesture.Gesture
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.ColorInt


data class GestureInfo(
    val gestureType: GestureCatchView.Type,
    val gesture: Gesture,
    @ColorInt val pathColor: Int = Color.BLACK,
    val delayTime: Long,
    val duration: Long,
    val points: ArrayList<EventPoint>

) : Parcelable {

    @Suppress("UNCHECKED_CAST")
    constructor(parcel: Parcel) : this(
            GestureCatchView.Type.valueOf(parcel.readString()!!),
            parcel.readParcelable(Gesture::class.java.classLoader)!!,
            parcel.readInt(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readArrayList(EventPoint::class.java.classLoader) as ArrayList<EventPoint>

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(gestureType.name)
        parcel.writeParcelable(gesture, flags)
        parcel.writeInt(pathColor)
        parcel.writeLong(delayTime)
        parcel.writeLong(duration)
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











