package com.apollo.pharmacy.ocr.activities.barcodegenerationforconnect;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.scanproduct.ScanProductActivity;
import com.apollo.pharmacy.ocr.databinding.ActivityBarcodeGenerationtoConnectBinding;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.zebrasdk.BaseActivity;
import com.apollo.pharmacy.ocr.zebrasdk.helper.Barcode;
import com.apollo.pharmacy.ocr.zebrasdk.helper.ScannerAppEngine;
import com.google.android.material.snackbar.Snackbar;
import com.zebra.scannercontrol.BarCodeView;
import com.zebra.scannercontrol.DCSSDKDefs;
import com.zebra.scannercontrol.DCSScannerInfo;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Objects;


import dmax.dialog.SpotsDialog;

import static com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.PREFS_NAME;
import static com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.PREF_DONT_SHOW_INSTRUCTIONS;

public class BarcodeGenerationtoConnectActivity extends BaseActivity implements ScannerAppEngine.IScannerAppEngineDevConnectionsDelegate {
    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 10;
    private FrameLayout llBarcode;
    static String btAddress;
    Dialog dialogBTAddress;
    ActivityBarcodeGenerationtoConnectBinding barcodeGenerationtoConnectBinding;
    private String scannerMode;
    public static final int BARCODE_GENERATIONTO_CONNECT_ACTIVITY = 100;
    static String userEnteredBluetoothAddress;
    private static final String DEFAULT_EMPTY_STRING = "";
    private static final String COLON_CHARACTER = ":";
    private static final int MAX_ALPHANUMERIC_CHARACTERS = 12;
    private static final int MAX_BLUETOOTH_ADDRESS_CHARACTERS = 17;
    public static final String BLUETOOTH_ADDRESS_VALIDATOR = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        barcodeGenerationtoConnectBinding = DataBindingUtil.setContentView(this, R.layout.activity_barcode_generationto_connect);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getIntent() != null) {
            scannerMode = getIntent().getStringExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.SCANNER_MODE);
        }

        //Disable bluetooth
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
//            mBluetoothAdapter.enable();
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_REQUEST_CODE);
        } else {
            initialize();
        }
        barcodeGenerationtoConnectBinding.backpressIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initialize() {
        initializeDcsSdk();
        llBarcode = (FrameLayout) findViewById(R.id.scan_to_connect_barcode);
//        addDevConnectionsDelegate(this);
        setTitle("Pair New Scanner");
        broadcastSCAisListening();
    }

    private void initializeDcsSdk() {
        /*Constants.sdkHandler.dcssdkEnableAvailableScannersDetection(true);
        Constants.sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_BT_NORMAL);
        Constants.sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_SNAPI);
        Constants.sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_BT_LE);
        Constants.sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_USB_CDC);*/
    }

    private void broadcastSCAisListening() {
        Intent intent = new Intent();
        intent.setAction("com.zebra.scannercontrol.LISTENING_STARTED");
        sendBroadcast(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    initialize();

                } else {
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    /**
     * Method to notify about the appearance of a scanner
     *
     * @param scannerID ID of the scanner which has appeared
     * @return -
     */
    @Override
    public boolean scannerHasAppeared(int scannerID) {
//        Utils.showDialog(this, "Connecting...");
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
//        Utils.showDialog(this,"Connecting to device...");
        Snackbar.make(findViewById(android.R.id.content), "Scanner connected", Snackbar.LENGTH_SHORT).show();

//        SsaSetSymbologyActivity.resetScanSpeedAnalyticSettings();

        ArrayList<DCSScannerInfo> activeScanners = new ArrayList<DCSScannerInfo>();
//        Constants.sdkHandler.dcssdkGetActiveScannersList(activeScanners);
        Intent intent = new Intent();
//        if (scannerMode.equals("BARCODE"))
//            intent = new Intent(BarcodeGenerationtoConnectActivity.this, ScanProductActivity.class);
//        else
//            intent = new Intent(BarcodeGenerationtoConnectActivity.this, ScanImageActivity.class);


//        pairNewScannerMenu.setTitle(R.string.menu_item_device_disconnect);

        for (DCSScannerInfo scannerInfo : Constants.mScannerInfoList) {
            if (scannerInfo.getScannerID() == scannerID) {
                intent.putExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.SCANNER_NAME, scannerInfo.getScannerName());
                intent.putExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.SCANNER_TYPE, scannerInfo.getConnectionType().value);
                intent.putExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.SCANNER_ADDRESS, scannerInfo.getScannerHWSerialNumber());
                intent.putExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.SCANNER_ID, scannerInfo.getScannerID());
                intent.putExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.AUTO_RECONNECTION, scannerInfo.isAutoCommunicationSessionReestablishment());
                intent.putExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.CONNECTED, true);
                intent.putExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.PICKLIST_MODE, getPickListMode(scannerID));

                if (scannerInfo.getScannerModel() != null && scannerInfo.getScannerModel().startsWith("PL3300")) { // remove this condition when CS4070 get the capability
                    intent.putExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.PAGER_MOTOR_STATUS, true);
                } else {
                    intent.putExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.PAGER_MOTOR_STATUS, isPagerMotorAvailable(scannerID));
                }

                intent.putExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.BEEPER_VOLUME, getBeeperVolume(scannerID));

                Constants.isAnyScannerConnected = true;
                Snackbar.make(findViewById(android.R.id.content), "Scanner connected", Snackbar.LENGTH_SHORT).show();
                Constants.currentConnectedScannerID = scannerID;
                Constants.currentConnectedScanner = scannerInfo;
                Constants.lastConnectedScanner = Constants.currentConnectedScanner;
                setResult(Activity.RESULT_OK, intent);
                finish();
//                Utils.dismissDialog();
                break;
            }
        }
        return true;
    }

    /**
     * Method to notify about that connection has been terminated with a scanner
     *
     * @param scannerID ID of the connected scanner which was disconnected
     * @return -
     */
    @Override
    public boolean scannerHasDisconnected(int scannerID) {
        Snackbar.make(findViewById(android.R.id.content), "Scanner Disconnected", Snackbar.LENGTH_SHORT).show();
//        pairNewScannerMenu.setTitle(R.string.menu_item_device_pair);
        Constants.isAnyScannerConnected = false;
        Constants.currentConnectedScannerID = -1;
        Constants.lastConnectedScanner = Constants.currentConnectedScanner;
        Constants.currentConnectedScanner = null;
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        addDevConnectionsDelegate(this);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String sourceString = "";
//        txtBarcodeType.setText(Html.fromHtml(sourceString));
//        txtScannerConfiguration.setText("");
        boolean dntShowMessage = settings.getBoolean(PREF_DONT_SHOW_INSTRUCTIONS, false);
        int barcode = settings.getInt(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.PREF_PAIRING_BARCODE_TYPE, 0);
        boolean setDefaults = settings.getBoolean(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.PREF_PAIRING_BARCODE_CONFIG, true);
        int protocolInt = settings.getInt(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.PREF_COMMUNICATION_PROTOCOL_TYPE, 0);
        String strProtocol = "SSI over Bluetooth LE";
        llBarcode = (FrameLayout) findViewById(R.id.scan_to_connect_barcode);
        DCSSDKDefs.DCSSDK_BT_PROTOCOL protocol = DCSSDKDefs.DCSSDK_BT_PROTOCOL.LEGACY_B;
        DCSSDKDefs.DCSSDK_BT_SCANNER_CONFIG config = DCSSDKDefs.DCSSDK_BT_SCANNER_CONFIG.KEEP_CURRENT;
        if (barcode == 0) {
//            txtBarcodeType.setText("");
//            txtScannerConfiguration.setText("");
            sourceString = "STC Barcode ";
//            txtBarcodeType.setText(Html.fromHtml(sourceString));
//            switch (protocolInt) {
//                case 0:
//                    protocol = DCSSDKDefs.DCSSDK_BT_PROTOCOL.SSI_BT_LE;//SSI over Bluetooth LE
//                    strProtocol = "Bluetooth LE";
//                    break;
//                case 1:
            protocol = DCSSDKDefs.DCSSDK_BT_PROTOCOL.SSI_BT_CRADLE_HOST;//SSI over Classic Bluetooth
//                    strProtocol = "SSI over Classic Bluetooth";
//                    break;
//                default:
//                    protocol = DCSSDKDefs.DCSSDK_BT_PROTOCOL.SSI_BT_LE;//SSI over Bluetooth LE
//                    break;
//            }
            if (setDefaults) {
                config = DCSSDKDefs.DCSSDK_BT_SCANNER_CONFIG.SET_FACTORY_DEFAULTS;
//                txtScannerConfiguration.setText(Html.fromHtml("<i> Set Factory Defaults, Com Protocol = "+strProtocol+"</i>"));
            } else {
//                txtScannerConfiguration.setText(Html.fromHtml("<i> Keep Current Settings, Com Protocol = "+strProtocol+"</i>"));
            }
        } else {
            sourceString = "Legacy Pairing ";
//            txtBarcodeType.setText(Html.fromHtml(sourceString));
//            txtScannerConfiguration.setText("");
        }
        selectedProtocol = protocol;
        selectedConfig = config;
        generatePairingBarcode();


        if ((barcode == 0)
                && (setDefaults == true)
                && (protocolInt == 0)) {
//            txtScannerConfiguration.setText(Html.fromHtml("<i> Factory Defaults Are Set, Com Protocol = "+strProtocol+"</i>"));
        } else {
            if (barcode == 0) {
//                txtBarcodeType.setText(Html.fromHtml(sourceString));
            } else {
//                txtBarcodeType.setText(Html.fromHtml(sourceString));
            }

        }

    }

    private void generatePairingBarcode() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
//        BarCodeView barCodeView = Constants.sdkHandler.dcssdkGetPairingBarcode(selectedProtocol, selectedConfig);
       /* if (barCodeView != null) {
            updateBarcodeView(layoutParams, barCodeView);
        } else {
            // SDK was not able to determine Bluetooth MAC. So call the dcssdkGetPairingBarcode with BT Address.

            btAddress = getDeviceBTAddress(settings);
            if (btAddress.equals("")) {
                llBarcode.removeAllViews();
            } else {
                Constants.sdkHandler.dcssdkSetBTAddress(btAddress);
                barCodeView = Constants.sdkHandler.dcssdkGetPairingBarcode(selectedProtocol, selectedConfig, btAddress);
                if (barCodeView != null) {
                    updateBarcodeView(layoutParams, barCodeView);
                }
            }
        }*/
    }

    private void updateBarcodeView(LinearLayout.LayoutParams layoutParams, BarCodeView barCodeView) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        int orientation = this.getResources().getConfiguration().orientation;
        int x = width * 9 / 10;
        int y = x / 3;
        if (getDeviceScreenSize() > 6) { // TODO: Check 6 is ok or not
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                x = width / 2;
                y = x / 3;
                x = 600;
                y = 200;
                layoutParams = new LinearLayout.LayoutParams(600, 200);
            } else {
                x = width * 2 / 3;
                y = x / 3;
            }
        }
        barCodeView.setSize(x, y);
        barCodeView.setBackground(getResources().getDrawable(R.drawable.activity_theme_bg));
        llBarcode.addView(barCodeView, layoutParams);
    }

    private double getDeviceScreenSize() {
        double screenInches = 0;
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();

        int mWidthPixels;
        int mHeightPixels;

        try {
            Point realSize = new Point();
            Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
            mWidthPixels = realSize.x;
            mHeightPixels = realSize.y;
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            double x = Math.pow(mWidthPixels / dm.xdpi, 2);
            double y = Math.pow(mHeightPixels / dm.ydpi, 2);
            screenInches = Math.sqrt(x + y);
        } catch (Exception ignored) {
        }
        return screenInches;
    }

    private String getDeviceBTAddress(SharedPreferences settings) {
        String bluetoothMAC = settings.getString(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.PREF_BT_ADDRESS, "");
        if (bluetoothMAC.equals("")) {
            if (dialogBTAddress == null) {
                dialogBTAddress = new Dialog(BarcodeGenerationtoConnectActivity.this);
                dialogBTAddress.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogBTAddress.setContentView(R.layout.dialog_get_bt_address);

                final TextView cancelContinueButton = (TextView) dialogBTAddress.findViewById(R.id.cancel_continue);
                final TextView abtPhoneButton = (TextView) dialogBTAddress.findViewById(R.id.abt_phone);
                final TextView skipButton = dialogBTAddress.findViewById(R.id.skip);
                abtPhoneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent statusSettings = new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS);
                        startActivity(statusSettings);
                    }

                });
                cancelContinueButton.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("ApplySharedPref")
                    @Override
                    public void onClick(View view) {
                        if (cancelContinueButton.getText().equals(getResources().getString(R.string.cancel))) {
                            if (dialogBTAddress != null) {
                                dialogBTAddress.dismiss();
                                finish();
                            }

                        } else {
//                            Constants.sdkHandler.dcssdkSetSTCEnabledState(true);
                            SharedPreferences.Editor settingsEditor = getSharedPreferences(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.PREFS_NAME, 0).edit();
                            settingsEditor.putString(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.PREF_BT_ADDRESS, userEnteredBluetoothAddress).commit();// Commit is required here. So suppressing warning.
                            if (dialogBTAddress != null) {
                                dialogBTAddress.dismiss();
                                dialogBTAddress = null;
                            }
                            onResume();
                        }
                    }
                });

                final EditText editTextBluetoothAddress = (EditText) dialogBTAddress.findViewById(R.id.text_bt_address);
                editTextBluetoothAddress.addTextChangedListener(new TextWatcher() {
                    String previousMac = null;

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String enteredMacAddress = editTextBluetoothAddress.getText().toString().toUpperCase();
                        String cleanMacAddress = clearNonMacCharacters(enteredMacAddress);
                        String formattedMacAddress = formatMacAddress(cleanMacAddress);

                        int selectionStart = editTextBluetoothAddress.getSelectionStart();
                        formattedMacAddress = handleColonDeletion(enteredMacAddress, formattedMacAddress, selectionStart);
                        int lengthDiff = formattedMacAddress.length() - enteredMacAddress.length();

                        setMacEdit(cleanMacAddress, formattedMacAddress, selectionStart, lengthDiff);

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        userEnteredBluetoothAddress = s.toString();
                        if (userEnteredBluetoothAddress.length() > MAX_BLUETOOTH_ADDRESS_CHARACTERS)
                            return;

                        if (isValidBTAddress(userEnteredBluetoothAddress)) {

                            Drawable dr = getResources().getDrawable(R.drawable.tick);
                            dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
                            editTextBluetoothAddress.setCompoundDrawables(null, null, dr, null);
                            cancelContinueButton.setText(getResources().getString(R.string.continue_txt));

                        } else {
                            editTextBluetoothAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            cancelContinueButton.setText(getResources().getString(R.string.cancel));

                        }
                    }

                    /**
                     * Strips all characters from a string except A-F and 0-9
                     * (Keep Bluetooth address allowed characters only).
                     *
                     * @param inputMacString User input string.
                     * @return String containing bluetooth MAC-allowed characters.
                     */
                    private String clearNonMacCharacters(String inputMacString) {
                        return inputMacString.toString().replaceAll("[^A-Fa-f0-9]", DEFAULT_EMPTY_STRING);
                    }

                    /**
                     * Adds a colon character to an unformatted bluetooth MAC address after
                     * every second character (strips full MAC trailing colon)
                     *
                     * @param cleanMacAddress Unformatted MAC address.
                     * @return Properly formatted MAC address.
                     */
                    private String formatMacAddress(String cleanMacAddress) {
                        int groupedCharacters = 0;
                        String formattedMacAddress = DEFAULT_EMPTY_STRING;

                        for (int i = 0; i < cleanMacAddress.length(); ++i) {
                            formattedMacAddress += cleanMacAddress.charAt(i);
                            ++groupedCharacters;

                            if (groupedCharacters == 2) {
                                formattedMacAddress += COLON_CHARACTER;
                                groupedCharacters = 0;
                            }
                        }

                        // Removes trailing colon for complete MAC address
                        if (cleanMacAddress.length() == MAX_ALPHANUMERIC_CHARACTERS)
                            formattedMacAddress = formattedMacAddress.substring(0, formattedMacAddress.length() - 1);

                        return formattedMacAddress;
                    }

                    /**
                     * Upon users colon deletion, deletes bluetooth MAC character preceding deleted colon as well.
                     *
                     * @param enteredMacAddress     User input MAC.
                     * @param formattedMacAddress   Formatted MAC address.
                     * @param selectionStartPosition MAC EditText field cursor position.
                     * @return Formatted MAC address.
                     */
                    private String handleColonDeletion(String enteredMacAddress, String formattedMacAddress, int selectionStartPosition) {
                        if (previousMac != null && previousMac.length() > 1) {
                            int previousColonCount = colonCount(previousMac);
                            int currentColonCount = colonCount(enteredMacAddress);

                            if (currentColonCount < previousColonCount) {
                                try {
                                    formattedMacAddress = formattedMacAddress.substring(0, selectionStartPosition - 1) + formattedMacAddress.substring(selectionStartPosition);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                String cleanMacAddress = clearNonMacCharacters(formattedMacAddress);
                                formattedMacAddress = formatMacAddress(cleanMacAddress);
                            }
                        }
                        return formattedMacAddress;
                    }

                    /**
                     * Gets bluetooth MAC address current colon count.
                     *
                     * @param formattedMacAddress Formatted MAC address.
                     * @return Current number of colons in MAC address.
                     */
                    private int colonCount(String formattedMacAddress) {
                        return formattedMacAddress.replaceAll("[^:]", DEFAULT_EMPTY_STRING).length();
                    }

                    /**
                     * Removes TextChange listener, sets MAC EditText field value,
                     * sets new cursor position and re-initiates the listener.
                     *
                     * @param cleanMacAddress       Clean MAC address.
                     * @param formattedMacAddress   Formatted MAC address.
                     * @param selectionStartPosition MAC EditText field cursor position.
                     * @param characterDifferenceLength     Formatted/Entered MAC number of characters difference.
                     */
                    private void setMacEdit(String cleanMacAddress, String formattedMacAddress, int selectionStartPosition, int characterDifferenceLength) {
                        editTextBluetoothAddress.removeTextChangedListener(this);
                        if (cleanMacAddress.length() <= MAX_ALPHANUMERIC_CHARACTERS) {
                            editTextBluetoothAddress.setText(formattedMacAddress);

                            editTextBluetoothAddress.setSelection(selectionStartPosition + characterDifferenceLength);
                            previousMac = formattedMacAddress;
                        } else {
                            editTextBluetoothAddress.setText(previousMac);
                            editTextBluetoothAddress.setSelection(previousMac.length());
                        }
                        editTextBluetoothAddress.addTextChangedListener(this);
                    }

                });

                dialogBTAddress.setCancelable(false);
                dialogBTAddress.setCanceledOnTouchOutside(false);
                dialogBTAddress.show();
                Window window = dialogBTAddress.getWindow();
                if (window != null)
                    window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                bluetoothMAC = settings.getString(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.PREF_BT_ADDRESS, "");
            } else {
                dialogBTAddress.show();
            }
        }
        return bluetoothMAC;
    }

    public boolean isValidBTAddress(String text) {
        return text != null && text.length() > 0 && text.matches(BLUETOOTH_ADDRESS_VALIDATOR);
    }

    private int getPickListMode(int scannerID) {
        int attrVal = 0;
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID><cmdArgs><arg-xml><attrib_list>402</attrib_list></arg-xml></cmdArgs></inArgs>";
        StringBuilder outXML = new StringBuilder();
//        executeCommand(DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_RSM_ATTR_GET, in_xml, outXML, scannerID);

        try {
            XmlPullParser parser = Xml.newPullParser();

            parser.setInput(new StringReader(outXML.toString()));
            int event = parser.getEventType();
            String text = null;
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (name.equals("value")) {
                            attrVal = Integer.parseInt(text != null ? text.trim() : null);
                        }
                        break;
                }
                event = parser.next();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return attrVal;
    }

    private boolean isPagerMotorAvailable(int scannerID) {
        boolean isFound = false;
        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID><cmdArgs><arg-xml><attrib_list>613</attrib_list></arg-xml></cmdArgs></inArgs>";
        StringBuilder outXML = new StringBuilder();
//        executeCommand(DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_RSM_ATTR_GET, in_xml, outXML, scannerID);
        if (outXML.toString().contains("<id>613</id>")) {
            isFound = true;
        }
        return isFound;
    }

    private int getBeeperVolume(int scannerID) {
        int beeperVolume = 0;

        String in_xml = "<inArgs><scannerID>" + scannerID + "</scannerID><cmdArgs><arg-xml><attrib_list>140</attrib_list></arg-xml></cmdArgs></inArgs>";
        StringBuilder outXML = new StringBuilder();
//        executeCommand(DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_RSM_ATTR_GET, in_xml, outXML, scannerID);

        try {
            XmlPullParser parser = Xml.newPullParser();

            parser.setInput(new StringReader(outXML.toString()));
            int event = parser.getEventType();
            String text = null;
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (name.equals("value")) {
                            beeperVolume = Integer.parseInt(text != null ? text.trim() : null);
                        }
                        break;
                }
                event = parser.next();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (beeperVolume == 0) {
            return 100;
        } else if (beeperVolume == 1) {
            return 50;
        } else {
            return 0;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ScanProductActivity.SCAN_PRODUCT_ACTIVITY) {
                if (data != null) {
                    String barcode_code = (String) data.getSerializableExtra("barcode_code");

                    Barcode barcode = (Barcode) data.getSerializableExtra("barcode");
                    Intent intent = new Intent();
                    intent.putExtra("barcode", barcode);
                    intent.putExtra("barcode_code", barcode_code);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}
