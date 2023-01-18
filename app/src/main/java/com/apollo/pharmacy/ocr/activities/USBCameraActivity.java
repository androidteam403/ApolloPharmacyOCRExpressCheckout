package com.apollo.pharmacy.ocr.activities;

import android.app.Activity;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.utility.Utils;
import com.jiangdg.usbcamera.UVCCameraHelper;
import com.jiangdg.usbcamera.utils.FileUtils;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;
import com.serenegiant.usb.widget.CameraViewInterface;

import static com.apollo.pharmacy.ocr.utility.Constants.DIRECTORY_NAME;

public class USBCameraActivity extends BaseActivity implements CameraDialog.CameraDialogParent, CameraViewInterface.Callback {

    private static final String TAG = "USBCameraActivity";
    private UVCCameraHelper mCameraHelper;
    private CameraViewInterface mUVCCameraView;
    private LinearLayout captureImgLayout;
    private boolean isRequest;
    private boolean isPreview;

    private UVCCameraHelper.OnMyDevConnectListener listener = new UVCCameraHelper.OnMyDevConnectListener() {
        @Override
        public void onAttachDev(UsbDevice device) {
            // request open permission
            if (!isRequest) {
                isRequest = true;
                if (mCameraHelper != null) {
                    mCameraHelper.requestPermission(0);
                }
            }
        }

        @Override
        public void onDettachDev(UsbDevice device) {
            // close camera
            if (isRequest) {
                isRequest = false;
                mCameraHelper.closeCamera();
                showShortMsg(device.getDeviceName() + " is out");
            }
        }

        @Override
        public void onConnectDev(UsbDevice device, boolean isConnected) {
            if (!isConnected) {
                showShortMsg("fail to connect,please check resolution params");
                isPreview = false;
            } else {
                isPreview = true;
                //showShortMsg("Connecting...");
                // initialize seekbar
                // need to wait UVCCamera initialize over
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Looper.prepare();
                        Looper.loop();
                    }
                }).start();
            }
        }

        @Override
        public void onDisConnectDev(UsbDevice device) {
            //showShortMsg("disconnecting");
        }
    };

    public USBCameraActivity() {
        super();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usbcamera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View mTextureView = findViewById(R.id.camera_view);
        captureImgLayout = findViewById(R.id.capture_image_layout);
        ImageView backImage = findViewById(R.id.back_img);

        // step.1 initialize UVCCameraHelper
        mUVCCameraView = (CameraViewInterface) mTextureView;
        mUVCCameraView.setCallback(this);
        mCameraHelper = UVCCameraHelper.getInstance();
        if (mCameraHelper != null) {
            mCameraHelper.setDefaultFrameFormat(1);
            mCameraHelper.initUSBMonitor(this, mUVCCameraView, listener);
        }

        mCameraHelper.setOnPreviewFrameListener(new AbstractUVCCameraHandler.OnPreViewResultListener() {
            @Override
            public void onPreviewResult(byte[] nv21Yuv) {
                Utils.printMessage(TAG, "onPreviewResult: " + nv21Yuv.length);
            }
        });

        backImage.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
        });

        captureImgLayout.setOnClickListener(v -> {
            if (mCameraHelper == null || !mCameraHelper.isCameraOpened()) {
                showShortMsg("sorry,camera open failed");
            }
            String picPath = UVCCameraHelper.ROOT_PATH + DIRECTORY_NAME + "/images/"
                    + System.currentTimeMillis() + UVCCameraHelper.SUFFIX_JPEG;

            mCameraHelper.capturePicture(picPath, new AbstractUVCCameraHandler.OnCaptureListener() {
                @Override
                public void onCaptureResult(String path) {
                    if (TextUtils.isEmpty(path)) {
                        return;
                    }
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (isRequest) {
                                isRequest = false;
                                mCameraHelper.closeCamera();
                            }
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result", path);
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }
                    });
                }
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // step.2 register USB event broadcast
        if (mCameraHelper != null) {
            mCameraHelper.registerUSB();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // step.3 unregister USB event broadcast
        if (mCameraHelper != null) {
            mCameraHelper.unregisterUSB();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mCameraHelper != null) {
//            mCameraHelper.unregisterUSB();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtils.releaseFile();
        // step.4 release uvc camera resources
        if (mCameraHelper != null) {
            mCameraHelper.release();
        }
    }

    private void showShortMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public USBMonitor getUSBMonitor() {
        return mCameraHelper.getUSBMonitor();
    }

    @Override
    public void onDialogResult(boolean canceled) {
        if (canceled) {
            showShortMsg("Cancelled");
        }
    }

    public boolean isCameraOpened() {
        return mCameraHelper.isCameraOpened();
    }

    @Override
    public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
        if (!isPreview && mCameraHelper.isCameraOpened()) {
            mCameraHelper.startPreview(mUVCCameraView);
            isPreview = true;
        }
    }

    @Override
    public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {

    }

    @Override
    public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
        if (isPreview && mCameraHelper.isCameraOpened()) {
            mCameraHelper.stopPreview();
            isPreview = false;
        }
    }
}
