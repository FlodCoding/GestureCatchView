package com.flod.widget.gesture

import android.gesture.Gesture
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.ColorInt

data class GestureInfo(
    val gestureType: GestureCatchView.GestureType,
    val gesture: Gesture,
    @ColorInt val pathColor: Int = Color.BLACK,
    val delayTime: Long = 0,
    val duration: Long,
    val isRawPoint: Boolean,
    val points: ArrayList<Point>

) : Parcelable {
    constructor(parcel: Parcel) : this(
        GestureCatchView.GestureType.valueOf(parcel.readString()!!),
        parcel.readParcelable(Gesture::class.java.classLoader)!!,
        parcel.readInt(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readBoolean(),
        parcel.readParcelableList(Point::class.java.classLoader)

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(gestureType.name)
        parcel.writeParcelable(gesture, flags)
        parcel.writeInt(pathColor)
        parcel.writeLong(delayTime)
        parcel.writeLong(duration)
        parcel.writeBoolean(isRawPoint)
        parcel.writeParcelable(Point::class.java.classLoader)!!
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


    data class Point(
        val x: Float,
        val y: Float,
        val timestamp: Long
    ):Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readFloat(),
            parcel.readFloat(),
            parcel.readLong()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeFloat(x)
            parcel.writeFloat(y)
            parcel.writeLong(timestamp)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Point> {
            override fun createFromParcel(parcel: Parcel): Point {
                return Point(parcel)
            }

            override fun newArray(size: Int): Array<Point?> {
                return arrayOfNulls(size)
            }
        }
    }
}











