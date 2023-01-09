package com.apollo.pharmacy.ocr.activities.epsonscan;
/**
 * Created by naveen.m on Nov 10, 2021.
 */

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.insertprescriptionnew.InsertPrescriptionActivityNew;
import com.apollo.pharmacy.ocr.databinding.ActivityEpsonScanBinding;
import com.apollo.pharmacy.ocr.databinding.DialogScanStatusBinding;
import com.apollo.pharmacy.ocr.epsonsdk.FindScannerCallback;
import com.apollo.pharmacy.ocr.epsonsdk.FindUsbScannerTask;
import com.apollo.pharmacy.ocr.epsonsdk.FolderUtility;
import com.apollo.pharmacy.ocr.zebrasdk.BaseActivity;
import com.epson.epsonscansdk.EpsonPDFCreator;
import com.epson.epsonscansdk.EpsonScanner;
import com.epson.epsonscansdk.ErrorCode;
import com.epson.epsonscansdk.usb.UsbProfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class EpsonScanActivity extends BaseActivity implements FindScannerCallback {
    private ActivityEpsonScanBinding epsonScanBinding;
    private final int REQUEST_CODE = 1000;
    private List<UsbProfile> usbDevices;
    EpsonScanner scanner;

    FolderUtility folderUtility = new FolderUtility(this);
    EpsonPDFCreator pdfCreator = new EpsonPDFCreator();
    private String devicePath = null;
    private boolean isCameFromInsertPrescriptionActivityNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        epsonScanBinding = DataBindingUtil.setContentView(this, R.layout.activity_epson_scan);
        setUp();
    }

    private void setUp() {
        if (getIntent() != null) {
            isCameFromInsertPrescriptionActivityNew = (Boolean) getIntent().getBooleanExtra("IS_CAME_FROM_INSERT_PRESCRIPTION_ACTIVITY_NEW", false);
        }
        {
            // Android 6, API 23以上でパーミッションの確認
            if (Build.VERSION.SDK_INT >= 23) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,};
                checkPermission(permissions, REQUEST_CODE);
            }
        }
        FindUsbScannerTask task = new FindUsbScannerTask(EpsonScanActivity.this, EpsonScanActivity.this);
        task.execute();
        onScanClick();
    }

    private void scanDialog(String devicePath) {
        if (pdfCreator.initFilePath(folderUtility.getPDFFileName()) == false) {
            new AlertDialog.Builder(this).setCancelable(false).setTitle("Alert").setMessage("pdfCreator init fails").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
            return;
        }

        if (scanner.init(true, this) == false) {
            new AlertDialog.Builder(this).setCancelable(false).setTitle("Alert").setMessage("epson scan library init fails").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
            return;
        }

        ErrorCode err = scanner.open();
        if (err != ErrorCode.kEPSErrorNoError) {
            new AlertDialog.Builder(this).setCancelable(false).setTitle("Alert").setMessage("fails to open scanner code : " + err.getCode()).setPositiveButton("OK", (dialog, which) -> finish()).show();
            return;
        }
    }

    public void checkPermission(final String[] permissions, final int request_code) {
        ActivityCompat.requestPermissions(this, permissions, request_code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        if (devicePath != null && !devicePath.isEmpty()) scanDialog(devicePath);
//                        Toast toast = Toast.makeText(this,
//                                "Added Permission: " + permissions[i], Toast.LENGTH_SHORT);
//                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(this, "Rejected Permission: " + permissions[i], Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                break;
            default:
                break;
        }
    }

    String devicePathh = null;

    @Override
    public void onFindUsbDevices(List<UsbProfile> devices) {
        for (int idx = 0; idx < devices.size(); idx++) {
            UsbProfile device = (UsbProfile) devices.get(idx);
            String productName = device.getProductName();
            https:
//d1xsr68o6znzvt.cloudfront.net/videos/FOO0093.mp4
            if (productName.equalsIgnoreCase("ES-60W")) {
                devicePath = device.getDevicePath();
                devicePathh = devicePath;
                scanner = new EpsonScanner();
                scanner.setDevicePath(devicePath);
//                scanDialog(devicePath);
                break;
            }
        }
    }

    @Override
    public void onNoUsbDevicesFound() {
        Dialog dialog = new Dialog(this);
        DialogScanStatusBinding scanStatusBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_scan_status, null, false);
        dialog.setContentView(scanStatusBinding.getRoot());
        dialog.setCancelable(false);
        scanStatusBinding.tittle.setText("Scanner Not Available!!! ");
        scanStatusBinding.message.setText("Please contact Store Executive");
        scanStatusBinding.OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCameFromInsertPrescriptionActivityNew) {
                    Intent intent = new Intent(EpsonScanActivity.this, InsertPrescriptionActivityNew.class);
                    startActivity(intent);
                    overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
                    finish();
                } else {
                    finish();
                }
            }
        });
        dialog.show();
    }

    private void onScanClick() {
        epsonScanBinding.scan.setOnClickListener(v -> {
            // do Scan
            ScanTask scanTask = new ScanTask(EpsonScanActivity.this, scanner, folderUtility.getTempImageStoreDir());
            scanTask.execute();
            scanTask.SetOnFinishedListener(new OnFinishedListener() {
                @Override
                public void onFinished(ArrayList<String> arrayFileNames) {
                    for (String fileName : arrayFileNames) {
                        if (fileName.endsWith(".jpg")) {
                            if (pdfCreator.addJpegFile(fileName, 200, 200) == false) {
                                new AlertDialog.Builder(EpsonScanActivity.this).setCancelable(false).setTitle("Alert").setMessage("pdfCreator add fails").setPositiveButton("OK", (dialog, which) -> finish()).show();
                                return;
                            }
                        } else {
                            if (pdfCreator.addPNMFile(fileName, 200, 200) == false) {
                                new AlertDialog.Builder(EpsonScanActivity.this).setCancelable(false).setTitle("Alert").setMessage("pdfCreator add fails").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                                return;
                            }
                        }
                    }
                }

                @Override
                public void onImageStored(String filePath) {
                    if (scanner != null) {
                        scanner.close();
                        scanner.destory();
                    }
                    if (pdfCreator != null) pdfCreator.destory();
                    Intent intent = new Intent(EpsonScanActivity.this, InsertPrescriptionActivityNew.class);
                    intent.putExtra("filePath", saveBitmapToFile(filePath).getAbsolutePath());
                    startActivity(intent);
                    overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
                    finish();
                }

                @Override
                public void onScannerNotAvailable() {
                    if (scanner != null) {
                        scanner.close();
                        scanner.destory();
                    }
                    if (pdfCreator != null) pdfCreator.destory();
                    finish();
                }
            });
        });
    }

    @Override
    protected void onPause() {
        if (scanner != null) {
            scanner.close();
            scanner.destory();
        }
        if (pdfCreator != null) pdfCreator.destory();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (scanner != null) {
            scanner.close();
            scanner.destory();
        }
        if (pdfCreator != null) pdfCreator.destory();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scanner != null) {
            scanner.close();
            scanner.destory();
        }
        if (pdfCreator != null) pdfCreator.destory();
    }

    public File saveBitmapToFile(String imgfile) {
        try {
            File file = new File(imgfile + "/1.jpg");
            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }
}
