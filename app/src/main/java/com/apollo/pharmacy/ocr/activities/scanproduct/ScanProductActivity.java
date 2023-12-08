package com.apollo.pharmacy.ocr.activities.scanproduct;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.databinding.ActivityScanProductBinding;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.zebrasdk.BaseActivity;
import com.apollo.pharmacy.ocr.zebrasdk.helper.Barcode;
import com.apollo.pharmacy.ocr.zebrasdk.helper.ScannerAppEngine;
import com.google.android.material.snackbar.Snackbar;
import com.zebra.scannercontrol.FirmwareUpdateEvent;

public class ScanProductActivity extends BaseActivity implements ScannerAppEngine.IScannerAppEngineDevConnectionsDelegate, ScannerAppEngine.IScannerAppEngineDevEventsDelegate {
    static int picklistMode;
    static boolean pagerMotorAvailable;
    private int scannerID;
    private int scannerType;
    private ListView barcodesList;
    private Button imageMode;
    private ImageView imageView;
    public static Bitmap retrievedDecodeImage = null;
    private ActivityScanProductBinding scanProductBinding;
    private String scannerMode;
    public static final int SCAN_PRODUCT_ACTIVITY = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scanProductBinding = DataBindingUtil.setContentView(this, R.layout.activity_scan_product);
//        addDevConnectionsDelegate(this);
        getScannerInfoFromIntent();

    }

    /**
     * This method is to get and validate scanner information in intent
     */
    public void getScannerInfoFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.SCANNER_ID)) {


            scannerID = getIntent().getIntExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.SCANNER_ID, -1);
            BaseActivity.lastConnectedScannerID = scannerID;
            String scannerName = getIntent().getStringExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.SCANNER_NAME);
            String address = getIntent().getStringExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.SCANNER_ADDRESS);
            scannerType = getIntent().getIntExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.SCANNER_TYPE, -1);

            picklistMode = getIntent().getIntExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.PICKLIST_MODE, 0);

            pagerMotorAvailable = getIntent().getBooleanExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.PAGER_MOTOR_STATUS, false);

            Constants.currentScannerId = scannerID;
            Constants.currentScannerName = scannerName;
            Constants.currentScannerAddress = address;
        }
    }

    public int getScannerID() {
        return scannerID;
    }


    /**
     * Method to notify about the appearance of a scanner
     *
     * @param scannerID ID of the scanner which has appeared
     * @return -
     */
    @Override
    public boolean scannerHasAppeared(int scannerID) {
        return false;
    }

    /**
     * Method to notify about the disappearance of a scanner
     *
     * @param scannerID ID of the scanner which has disappeared
     * @return -
     */
    @Override
    public boolean scannerHasDisappeared(int scannerID) {
        return false;
    }

    /**
     * Method to notify about that connection has been established with a scanner
     *
     * @param scannerID ID of the scanner with which a connection was established
     * @return -
     */
    @Override
    public boolean scannerHasConnected(int scannerID) {
        return false;
    }

    /**
     * Method to notify about that connection has been terminated with a scanner
     *
     * @param scannerID ID of the connected scanner which was disconnected
     * @return -
     */
    @Override
    public boolean scannerHasDisconnected(int scannerID) {
        return false;
    }

    @Override
    public void scannerBarcodeEvent(byte[] barcodeData, int barcodeType, int scannerID) {
        Snackbar.make(findViewById(android.R.id.content), "Item barcode scanned", Snackbar.LENGTH_SHORT).show();
        Intent intent = new Intent();
        Barcode barcode = new Barcode(barcodeData, barcodeType, scannerID);
        intent.putExtra("barcode", barcode);
        String barcodes = new String(barcodeData);
        intent.putExtra("barcode_code", barcodes);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void scannerFirmwareUpdateEvent(FirmwareUpdateEvent firmwareUpdateEvent) {

    }

    @Override
    public void scannerImageEvent(byte[] imageData) {

    }

    @Override
    public void scannerVideoEvent(byte[] videoData) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        /*addDevEventsDelegate(this);
        addDevConnectionsDelegate(this);*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}