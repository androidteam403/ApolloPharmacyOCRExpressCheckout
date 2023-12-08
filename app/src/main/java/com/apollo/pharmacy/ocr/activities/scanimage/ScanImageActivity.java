package com.apollo.pharmacy.ocr.activities.scanimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.databinding.ActivityScanImageBinding;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.zebrasdk.BaseActivity;
import com.apollo.pharmacy.ocr.zebrasdk.helper.ScannerAppEngine;
import com.google.android.material.snackbar.Snackbar;
import com.zebra.scannercontrol.FirmwareUpdateEvent;

import static com.zebra.scannercontrol.DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_IMAGE_MODE;

public class ScanImageActivity extends BaseActivity implements ScanImageListener, ScannerAppEngine.IScannerAppEngineDevConnectionsDelegate, ScannerAppEngine.IScannerAppEngineDevEventsDelegate {
    static int picklistMode;
    static boolean pagerMotorAvailable;

    private int scannerID;
    private int scannerType;
    private ListView barcodesList;
    private Button imageMode;
    private ImageView imageView;
    public static Bitmap retrievedDecodeImage = null;
    private ActivityScanImageBinding scanImageBinding;
    private String scannerMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scanImageBinding = DataBindingUtil.setContentView(this, R.layout.activity_scan_image);
        scanImageBinding.setCallback(this);
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
    }

    @Override
    public void scannerFirmwareUpdateEvent(FirmwareUpdateEvent firmwareUpdateEvent) {

    }

    @Override
    public void scannerImageEvent(byte[] imageData) {
        Snackbar.make(findViewById(android.R.id.content), "Image scanned", Snackbar.LENGTH_SHORT).show();
        scanImageBinding.image.setImageDrawable(null);
        final byte[] tempImgData = imageData;

        scanImageBinding.image.setPadding(0, 0, 0, 0);
        retrievedDecodeImage = BitmapFactory.decodeByteArray(tempImgData, 0, tempImgData.length);
        scanImageBinding.image.setImageBitmap(retrievedDecodeImage);
    }

    @Override
    public void scannerVideoEvent(byte[] videoData) {

    }

    @Override
    protected void onResume() {
        super.onResume();
//        addDevEventsDelegate(this);
//        addDevConnectionsDelegate(this);
    }

    @Override
    public void onClickScanImage() {
        Snackbar.make(findViewById(android.R.id.content), "Now scan image.", Snackbar.LENGTH_SHORT).show();
        String inXML = "<inArgs><scannerID>" + getIntent().getIntExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.SCANNER_ID, 0) + "</scannerID></inArgs>";
        StringBuilder sb = new StringBuilder();
//        Constants.sdkHandler.dcssdkExecuteCommandOpCodeInXMLForScanner(DCSSDK_DEVICE_IMAGE_MODE, inXML, sb, scannerID);
    }
}