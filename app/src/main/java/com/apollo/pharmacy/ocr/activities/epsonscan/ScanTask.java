package com.apollo.pharmacy.ocr.activities.epsonscan;

/**
 * Copyright (c) 2015 Seiko Epson Corporation. All rights reserved.
 * Created by Takehiko YOSHIDA on 2015/08/12.
 */

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.databinding.DialogScanStatusBinding;
import com.epson.epsonscansdk.EpsonScanner;
import com.epson.epsonscansdk.EpsonScannerEventListner;
import com.epson.epsonscansdk.ErrorCode;

import java.io.File;
import java.util.ArrayList;

interface OnFinishedListener {
    void onFinished(ArrayList<String> arrayFileNames);

    void onImageStored(String filePath);

    void onScannerNotAvailable();
}

public class ScanTask extends AsyncTask<Void, Void, ErrorCode> {
    private Context context;
    private EpsonScanner scanner;
    private ProgressDialog dialog;
    private ArrayList<String> arrayFileNames = new ArrayList<String>();
    private String folder;
    private OnFinishedListener finishedListener;
    private String targetFolder;

    public ScanTask(Context context, EpsonScanner scanner, String targetFolder) {
        this.context = context;
        this.scanner = scanner;
        this.targetFolder = targetFolder;
    }

    public void SetOnFinishedListener(OnFinishedListener finishedListener) {
        this.finishedListener = finishedListener;
    }

    @Override
    protected void onPreExecute() {

        this.dialog = new ProgressDialog(this.context);
        this.dialog.setTitle("Please wait");
        this.dialog.setMessage("Scanning images...");
        this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    @Override
    protected ErrorCode doInBackground(Void... params) {
        final File filePath = new File(targetFolder);

        // delete all contents
        if (filePath.exists()) {
            if (filePath.isDirectory()) {
                String[] children = filePath.list();
                for (int i = 0; i < children.length; i++) {
                    new File(filePath, children[i]).delete();
                }
            }
        }

        // if folder not exsits create
        if (filePath.exists() == false) {
            if (filePath.mkdirs() == false) {
                Log.d("EpsonScan2SDKSample", "fails to create dir");
            }
        }

        scanner.registerEventListner(new EpsonScannerEventListner() {
            @Override
            public void onSaveToPath(String path, boolean isBackSide, int pageNumber) {

                Log.d("EpsonScan2SDKSample", "images saved to " + path);
                arrayFileNames.add(path);
            }

            @Override
            public void onPressScannerButton() {
                Log.d("EpsonScan2SDKSample", "on press scanner button");

            }

            @Override
            public void onCancelScanning() {
                Log.d("EpsonScan2SDKSample", "onCancelScanning");

            }
        });

        folder = filePath.getAbsolutePath();
        return scanner.scan(filePath.getAbsolutePath());
    }

    @Override
    protected void onPostExecute(ErrorCode result) {
        this.dialog.dismiss();
        Log.d("EpsonScan2SDKSample", "scan finish");


        //AlertDialog.Builder
        Dialog dlg = new Dialog(context);
        DialogScanStatusBinding scanStatusBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_scan_status, null, false);
        dlg.setContentView(scanStatusBinding.getRoot());
        dlg.setCancelable(false);
        if (result != ErrorCode.kEPSErrorNoError) {
            scanStatusBinding.tittle.setText("ERROR");
            scanStatusBinding.OK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlg.dismiss();
                    // OK button pressed
                }
            });
            if (result == ErrorCode.kEPSErrorPaperEmpty) {
                scanStatusBinding.message.setText("Paper empty");
            } else if (result == ErrorCode.kEPSErrorPaperJam) {
                scanStatusBinding.message.setText("Paper Jam");
            } else if (result == ErrorCode.kEPSErrorPaperDoubleFeed) {
                scanStatusBinding.message.setText("Paper feed");
            } else if (result.getCode() == 200) {
                scanStatusBinding.tittle.setText("Scanner Not Available!!!");
                scanStatusBinding.message.setText("Please Contact Store Executive");
                scanStatusBinding.OK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dlg.dismiss();
                        if (finishedListener != null) {
                            finishedListener.onScannerNotAvailable();
                        }
                    }
                });
            } else {
                scanStatusBinding.message.setText("error occurs code = " + result.getCode());
            }
        } else {
            scanStatusBinding.tittle.setText("Success");
            scanStatusBinding.OK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishedListener.onImageStored(targetFolder);
                }
            });
            scanStatusBinding.message.setText("Scanning finished " + arrayFileNames.size() + " images stored");
        }

        dlg.show();

        if (finishedListener != null) {
            finishedListener.onFinished(arrayFileNames);
        }
    }
}
