package com.apollo.pharmacy.ocr.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AddFCMTokenRequest(@SerializedName("fcmKey")
                              @Expose
                              var fcmKey: String,
                              @SerializedName("kioskId")
                              @Expose
                              var kioskId: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fcmKey)
        parcel.writeString(kioskId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddFCMTokenRequest> {
        override fun createFromParcel(parcel: Parcel): AddFCMTokenRequest {
            return AddFCMTokenRequest(parcel)
        }

        override fun newArray(size: Int): Array<AddFCMTokenRequest?> {
            return arrayOfNulls(size)
        }
    }
}