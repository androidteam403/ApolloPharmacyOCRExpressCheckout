package com.apollo.pharmacy.ocr.epsonsdk;

/**
 * Copyright (c) 2015 Seiko Epson Corporation. All rights reserved.
 * Created by Takehiko YOSHIDA on 2015/08/31.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.epson.epsonscansdk.usb.UsbFinder;

import java.util.List;


public class FindUsbScannerTask extends AsyncTask<Void, String, List> {

    private Context context;
    private FindScannerCallback callback;
    private ProgressDialog dialog;


    public FindUsbScannerTask(Context context, FindScannerCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        this.dialog = new ProgressDialog(this.context);
        this.dialog.setTitle("Please wait");
        this.dialog.setMessage("Finding USB scanners...");
        this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    @Override
    protected List doInBackground(Void... params) {
        return UsbFinder.getDeviceProfileList(this.context);
    }

    @Override
    protected void onPostExecute(List devices) {
        this.dialog.dismiss();
        if (devices.size() > 0) {
            this.callback.onFindUsbDevices(devices);
        } else {
            this.callback.onNoUsbDevicesFound();
        }
    }
}
