package jp.co.rjc.myindicater_android

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

/**
 * Created by h_miyamoto on 2018/06/08.
 */

/**
 * 場所情報保持クラス.
 */
class Place() : Parcelable {

    var name: String? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var id: String? = null
    var place_id: String? = null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        latitude = parcel.readDouble()
        longitude = parcel.readDouble()
        id = parcel.readString()
        place_id = parcel.readString()
    }


    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("name").append(":").append(name).append("/")
                .append("latitude").append(":").append(latitude).append("/")
                .append("longitude").append(":").append(longitude).append("/")
                .append("id").append(":").append(id).append("/")
                .append("place_id").append(":").append(place_id).append("/")

        return sb.toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(id)
        parcel.writeString(place_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Place> {
        override fun createFromParcel(parcel: Parcel): Place {
            return Place(parcel)
        }

        override fun newArray(size: Int): Array<Place?> {
            return arrayOfNulls(size)
        }
    }

}