package com.apollo.pharmacy.ocr.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Meta(@SerializedName("statusCode")
                @Expose
                var statusCode: Int,
                @SerializedName("statusMsg")
                @Expose
                var statusMsg: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(statusCode)
        parcel.writeString(statusMsg)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Meta> {
        override fun createFromParcel(parcel: Parcel): Meta {
            return Meta(parcel)
        }

        override fun newArray(size: Int): Array<Meta?> {
            return arrayOfNulls(size)
        }
    }
}