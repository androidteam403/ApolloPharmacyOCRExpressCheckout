package com.apollo.pharmacy.ocr.activities.epsonscan;

import android.content.Context;

import com.epson.epsonscansdk.usb.UsbProfile;

import java.util.List;

public class UsbFinder {
    public UsbFinder() {
    }

    public static List<UsbProfile> getDeviceProfileList(Context context) {
        UsbDriver driver = UsbDriver.getInstance(context);
        return driver.getDeviceProfileList();
    }
}
