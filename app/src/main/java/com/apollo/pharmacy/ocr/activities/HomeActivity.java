package com.apollo.pharmacy.ocr.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.barcodegenerationforconnect.BarcodeGenerationtoConnectActivity;
import com.apollo.pharmacy.ocr.activities.epsonscan.EpsonScanActivity;
import com.apollo.pharmacy.ocr.activities.mposstoresetup.MposStoreSetupActivity;
import com.apollo.pharmacy.ocr.activities.userlogin.UserLoginActivity;
import com.apollo.pharmacy.ocr.activities.userlogin.model.GetGlobalConfigurationResponse;
import com.apollo.pharmacy.ocr.controller.HomeActivityController;
import com.apollo.pharmacy.ocr.databinding.ActivityHomeBinding;
import com.apollo.pharmacy.ocr.databinding.NewLoginScreenBinding;
import com.apollo.pharmacy.ocr.dialog.AccesskeyDialog;
import com.apollo.pharmacy.ocr.dialog.ItemBatchSelectionDilaog;
import com.apollo.pharmacy.ocr.interfaces.HomeListener;
import com.apollo.pharmacy.ocr.model.API;
import com.apollo.pharmacy.ocr.model.CategoryList;
import com.apollo.pharmacy.ocr.model.Categorylist_Response;
import com.apollo.pharmacy.ocr.model.Global_api_response;
import com.apollo.pharmacy.ocr.model.ItemSearchResponse;
import com.apollo.pharmacy.ocr.model.OCRToDigitalMedicineResponse;
import com.apollo.pharmacy.ocr.model.PortFolioModel;
import com.apollo.pharmacy.ocr.model.ProductSearch;
import com.apollo.pharmacy.ocr.receiver.ConnectivityReceiver;
import com.apollo.pharmacy.ocr.utility.ApplicationConstant;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.utility.NetworkUtils;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;
import com.apollo.pharmacy.ocr.zebrasdk.BaseActivity;
import com.apollo.pharmacy.ocr.zebrasdk.helper.ScannerAppEngine;
import com.zebra.scannercontrol.FirmwareUpdateEvent;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener, HomeListener, ScannerAppEngine.IScannerAppEngineDevEventsDelegate, ItemBatchSelectionDilaog.ItemBatchListDialogListener {

    private TextView myCartCount, welcomeTxt;
    private boolean isUploadPrescription = false;
    private final int MY_READ_PERMISSION_REQUEST_CODE = 103;
    private HomeActivityController homeActivityController;
    private ConstraintLayout constraintLayout;
    private ActivityHomeBinding activityHomeBinding;
    List<OCRToDigitalMedicineResponse> dataList = new ArrayList<>();
    private ImageView scannerStatus;
    private boolean isDialogShow = false;
    private EditText usbScanEdit;
    public static String mobileNum = "";
    public static boolean isLoggedin;
    public static boolean isPaymentSelectionActivity = false;
    public static boolean isHomeActivity = true;
    Context context;
    public boolean isResend = false;
    CountDownTimer cTimer = null;
    NewLoginScreenBinding newLoginScreenBinding;
    private String oldMobileNum = "";
    private String loginActivityName="";
    private int otp = 0;
    //    private DialogLoginPopupBinding dialogLoginPopupBinding;
    Dialog dialog;


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            findViewById(R.id.networkErrorLayout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.networkErrorLayout).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        addDevEventsDelegate(this);
        scannerConnectedorNot();
        HomeActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        Constants.getInstance().setConnectivityListener(this);
        if (!ConnectivityReceiver.isConnected()) {
            findViewById(R.id.networkErrorLayout).setVisibility(View.VISIBLE);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("cardReceiver"));

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverNew, new IntentFilter("OrderhistoryCardReciver"));
        Constants.getInstance().setConnectivityListener(this);
    }

    //TextView crash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        isHomeActivity = true;
//
        if(!HomeActivity.isLoggedin){
            List<OCRToDigitalMedicineResponse> dataList = new ArrayList<>();
            SessionManager.INSTANCE.setDataList(dataList);
            SessionManager.INSTANCE.setDeletedDataList(dataList);
            SessionManager.INSTANCE.setMobilenumber("");
            SessionManager.INSTANCE.setCustName("");
        }else{

        }

        if (SessionManager.INSTANCE.getStoreId() != null && !SessionManager.INSTANCE.getStoreId().isEmpty()
                && SessionManager.INSTANCE.getTerminalId() != null && !SessionManager.INSTANCE.getTerminalId().isEmpty() && SessionManager.INSTANCE.getEposUrl() != null && !SessionManager.INSTANCE.getEposUrl().isEmpty()) {

        } else {
            AccesskeyDialog accesskeyDialog = new AccesskeyDialog(this);
            accesskeyDialog.onClickSubmit(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accesskeyDialog.listener();
                    if (accesskeyDialog.validate()) {
                        Intent intent = new Intent(HomeActivity.this, MposStoreSetupActivity.class);
                        intent.putExtra("homeActivity", "homeActivity");
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
                        accesskeyDialog.dismiss();
                    }
                }
            });


            accesskeyDialog.show();
//                Intent intent = new Intent(MainActivity.this, MposStoreSetupActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        }

        if (getIntent() != null) {
            loginActivityName = getIntent().getStringExtra("userLoginActivity");
        }
        if (loginActivityName != null) {
            if (loginActivityName.equals("mySearchActivityLogout")) {
                SessionManager.INSTANCE.setMobilenumber("");
                SessionManager.INSTANCE.setCustName("");
            } else if (loginActivityName.equals("myCartActivityLogout")) {
                SessionManager.INSTANCE.setMobilenumber("");
                SessionManager.INSTANCE.setCustName("");
            } else if (loginActivityName.equals("myOrdersActivityLogout")) {
                SessionManager.INSTANCE.setMobilenumber("");
                SessionManager.INSTANCE.setCustName("");
            } else if (loginActivityName.equals("myOffersActivityLogout")) {
                SessionManager.INSTANCE.setMobilenumber("");
                SessionManager.INSTANCE.setCustName("");
            } else if (loginActivityName.equals("myProfileActivityLogout")) {
                SessionManager.INSTANCE.setMobilenumber("");
                SessionManager.INSTANCE.setCustName("");
            } else if (loginActivityName.equals("")) {
                SessionManager.INSTANCE.setMobilenumber("");
                SessionManager.INSTANCE.setCustName("");
            }else if(loginActivityName.equals("INSERT_PRESCRIPTION_ACTIVITY_NEW")){
                SessionManager.INSTANCE.setMobilenumber("");
                SessionManager.INSTANCE.setCustName("");
            }
        }
//
        if (SessionManager.INSTANCE.getMobilenumber().isEmpty()) {
            List<OCRToDigitalMedicineResponse> dataLists = new ArrayList<>();
            SessionManager.INSTANCE.setDataList(dataLists);
            SessionManager.INSTANCE.setDeletedDataList(dataLists);
        }


        if (null != SessionManager.INSTANCE.getDataList() && SessionManager.INSTANCE.getDataList().size() > 0)
            activityHomeBinding.checkoutImage.setVisibility(View.VISIBLE);
        else
            activityHomeBinding.checkoutImage.setVisibility(View.GONE);

//        activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.updated_homescreen_layout);

//        addDevConnectionsDelegate(this);
        addDevEventsDelegate(this);
        scannerStatus = (ImageView) findViewById(R.id.scanner_check);
        scannerConnectedorNot();
        ImageView customerCareImg = findViewById(R.id.customer_care_icon);
        LinearLayout customerHelpLayout = findViewById(R.id.customer_help_layout);
        customerHelpLayout.setVisibility(View.VISIBLE);
        usbScanEdit = (EditText) findViewById(R.id.usb);
        usbScanEdit.setShowSoftInputOnFocus(false);
        usbScanEdit.requestFocus();
        homeActivityController = new HomeActivityController(this, this);
        homeActivityController.getGlobalApiList();

        if (SessionManager.INSTANCE.getEposUrl() != null && !SessionManager.INSTANCE.getEposUrl().isEmpty() && !SessionManager.INSTANCE.getEposUrl().equals("")) {

            homeActivityController.getGlobalConfigurationApiCall();

        }


        usbScanEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString() != null && !s.toString().isEmpty()) {
                    usbScanHandler.removeCallbacks(usbScanRunnable);
                    usbScanHandler.postDelayed(usbScanRunnable, 250);
                }
            }
        });
//        crash.setText("crash");

        if (SessionManager.INSTANCE.getDataList() != null && SessionManager.INSTANCE.getDataList().size() > 0) {
            activityHomeBinding.checkoutImage.setImageResource(R.drawable.checkout_cart);
        } else {
            activityHomeBinding.checkoutImage.setImageResource(R.drawable.checkout_cart_unselect);
        }

        activityHomeBinding.checkoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SessionManager.INSTANCE.getDataList() != null && SessionManager.INSTANCE.getDataList().size() > 0) {
//                    finish();
//                    Intent intent1 = new Intent(HomeActivity.this, MyCartActivity.class);
//                    startActivity(intent1);
//                    overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);


//                    if (!HomeActivity.isLoggedin) {
//
//                        Intent intent1 = new Intent(HomeActivity.this, UserLoginActivity.class);
//                        intent1.putExtra("userLoginActivity", "homeActivityCheckoutLogin");
//                        startActivity(intent1);
//                        finish();
//                        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);


                    // No need to
//                        dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
//
//                        newLoginScreenBinding = DataBindingUtil.inflate(LayoutInflater.from(HomeActivity.this), R.layout.new_login_screen, null, false);
//                        dialog.setContentView(newLoginScreenBinding.getRoot());
//                        if (dialog.getWindow() != null)
//                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
////                        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
////                        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
////                                WindowManager.LayoutParams.MATCH_PARENT);
//                        dialog.setCancelable(true);
//                       newLoginScreenBinding.mobileNumEditText.requestFocus();
//                        newLoginScreenBinding.closeDialog.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                            }
//                        });
//
//                        newLoginScreenBinding.resendButtonNewLogin.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                isResend=true;
//                                Utils.showDialog(HomeActivity.this, "Sending OTP…");
//                                if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
//                                    Send_Sms_Request sms_req = new Send_Sms_Request();
//                                    sms_req.setMobileNo(mobileNum);
//                                    sms_req.setMessage("Dear Apollo Customer, Your one time password is " + String.valueOf(otp) + " and is valid for 3mins.");
//                                    sms_req.setIsOtp(true);
//                                    sms_req.setOtp(String.valueOf(otp));
//                                    sms_req.setApiType("KIOSk");
//                                    homeActivityController.handleSendSmsApi(sms_req);
//                                }
//                            }
//                        });
//                        newLoginScreenBinding.submit.setOnClickListener(new View.OnClickListener() {
//                            @SuppressLint("SetTextI18n")
//                            @Override
//                            public void onClick(View v) {
//                                if (newLoginScreenBinding.mobileNumEditText.getText().toString() != null && newLoginScreenBinding.mobileNumEditText.getText().toString() != "") {
//                                    if (SessionManager.INSTANCE.getStoreId() != null && !SessionManager.INSTANCE.getStoreId().isEmpty()
//                                            && SessionManager.INSTANCE.getTerminalId() != null && !SessionManager.INSTANCE.getTerminalId().isEmpty() && SessionManager.INSTANCE.getEposUrl() != null && !SessionManager.INSTANCE.getEposUrl().isEmpty()) {
//                                        String MobilePattern = "[0-9]{10}";
//                                        mobileNum = newLoginScreenBinding.mobileNumEditText.getText().toString();
//                                        if (mobileNum.length() < 10) {
//                                            Toast.makeText(getApplicationContext(), "Please enter 10 digit phone number", Toast.LENGTH_SHORT).show();
//                                        } else {
////                                send_otp_image.setImageResource(R.drawable.right_selection_green)
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_country_code_bg)
////                                edittext_error_text.visibility = View.INVISIBLE
//                                            if (oldMobileNum.equals(newLoginScreenBinding.mobileNumEditText.getText().toString()) && newLoginScreenBinding.mobileNumEditText.getText().toString().length() > 0 && (mobileNum.matches(MobilePattern))) {
//                                                newLoginScreenBinding.mobileNumLoginPopup.setVisibility(View.GONE);
//                                                newLoginScreenBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//                                                String phoneNumber = newLoginScreenBinding.mobileNumEditText.getText().toString().trim();
//                                                int firstDigit = Integer.parseInt((phoneNumber).substring(0, 1));
//                                                String strTwoDigits = phoneNumber.length() >= 4 ? phoneNumber.substring(phoneNumber.length() - 2) : "";
//                                                newLoginScreenBinding.mobileNumStars.setText(firstDigit + "*******" + strTwoDigits);
//                                                newLoginScreenBinding.timerNewlogin.setText("");
//                                                cancelTimer();
//                                                startTimer();
//                                            } else {
//                                                newLoginScreenBinding.mobileNumLoginPopup.setVisibility(View.GONE);
//                                                newLoginScreenBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//                                                String phoneNumber = newLoginScreenBinding.mobileNumEditText.getText().toString().trim();
//                                                int firstDigit = Integer.parseInt((phoneNumber).substring(0, 1));
//                                                String strTwoDigits = phoneNumber.length() >= 4 ? phoneNumber.substring(phoneNumber.length() - 2) : "";
//                                                newLoginScreenBinding.mobileNumStars.setText(firstDigit + "*******" + strTwoDigits);
//                                                newLoginScreenBinding.timerNewlogin.setText("");
//                                                cancelTimer();
//                                                startTimer();
//
//
//                                                if (newLoginScreenBinding.mobileNumEditText.getText().toString().length() > 0) {
//                                                    oldMobileNum = newLoginScreenBinding.mobileNumEditText.getText().toString();
//                                                    if (mobileNum.matches(MobilePattern)) {
//                                                        Utils.showDialog(HomeActivity.this, "Sending OTP…");
//                                                        otp = (int) ((Math.random() * 9000) + 1000);
//                                                        if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
//                                                            Send_Sms_Request sms_req = new Send_Sms_Request();
//                                                            sms_req.setMobileNo(mobileNum);
//                                                            sms_req.setMessage("Dear Apollo Customer, Your one time password is " + String.valueOf(otp) + " and is valid for 3mins.");
//                                                            sms_req.setIsOtp(true);
//                                                            sms_req.setOtp(String.valueOf(otp));
//                                                            sms_req.setApiType("KIOSk");
//                                                            homeActivityController.handleSendSmsApi(sms_req);
//                                                        } else {
//                                                            Utils.showSnackbar(getApplicationContext(), constraintLayout, "Internet Connection Not Available");
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    } else {
//                                        AccesskeyDialog accesskeyDialog = new AccesskeyDialog(HomeActivity.this);
//                                        accesskeyDialog.onClickSubmit(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                accesskeyDialog.listener();
//                                                if (accesskeyDialog.validate()) {
//                                                    Intent intent = new Intent(HomeActivity.this, MposStoreSetupActivity.class);
//                                                    startActivity(intent);
//                                                    overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                                                    accesskeyDialog.dismiss();
//                                                }
//                                            }
//                                        });
//
//
//                                        accesskeyDialog.show();
////                Intent intent = new Intent(MainActivity.this, MposStoreSetupActivity.class);
////                startActivity(intent);
////                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                                    }
//                                }
////                    dialog.dismiss();
//                            }
//                        });
//
//                        newLoginScreenBinding.otplayoutEditText1.addTextChangedListener(new TextWatcher() {
//                            @Override
//                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                            }
//
//                            @Override
//                            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                            }
//
//                            @Override
//                            public void afterTextChanged(Editable editable) {
//                                if (editable.length() == 1) {
//                                    newLoginScreenBinding.otplayoutEditText1.setBackgroundResource(R.drawable.backgroundforotpblack);
//                                    newLoginScreenBinding.otplayoutEditText2.requestFocus();
//                                    newLoginScreenBinding.otplayoutEditText3.clearFocus();
//                                    newLoginScreenBinding.otplayoutEditText4.clearFocus();
//                                    newLoginScreenBinding.otplayoutEditText1.clearFocus();
//                                } else {
//                                    newLoginScreenBinding.otplayoutEditText1.setBackgroundResource(R.drawable.backgroundforotp);
//                                    newLoginScreenBinding.otplayoutEditText2.clearFocus();
//                                    newLoginScreenBinding.otplayoutEditText3.clearFocus();
//                                    newLoginScreenBinding.otplayoutEditText4.clearFocus();
//
//                                }
//                            }
//                        });
//                        newLoginScreenBinding.otplayoutEditText2.addTextChangedListener(new TextWatcher() {
//                            @Override
//                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                            }
//
//                            @Override
//                            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                            }
//
//                            @Override
//                            public void afterTextChanged(Editable editable) {
//                                if (editable.length() == 1) {
//                                    newLoginScreenBinding.otplayoutEditText2.setBackgroundResource(R.drawable.backgroundforotpblack);
//                                    newLoginScreenBinding.otplayoutEditText3.requestFocus();
//                                    newLoginScreenBinding.otplayoutEditText1.clearFocus();
//                                    newLoginScreenBinding.otplayoutEditText4.clearFocus();
//                                    newLoginScreenBinding.otplayoutEditText2.clearFocus();
//                                } else {
//                                    newLoginScreenBinding.otplayoutEditText2.setBackgroundResource(R.drawable.backgroundforotp);
//                                    newLoginScreenBinding.otplayoutEditText1.requestFocus();
//                                    newLoginScreenBinding.otplayoutEditText3.clearFocus();
//                                    newLoginScreenBinding.otplayoutEditText4.clearFocus();
//                                    newLoginScreenBinding.otplayoutEditText2.clearFocus();
//                                }
//                            }
//                        });
//                        newLoginScreenBinding.otplayoutEditText3.addTextChangedListener(new TextWatcher() {
//                            @Override
//                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                            }
//
//                            @Override
//                            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                            }
//
//                            @Override
//                            public void afterTextChanged(Editable editable) {
//                                if (editable.length() == 1) {
//                                    newLoginScreenBinding.otplayoutEditText3.setBackgroundResource(R.drawable.backgroundforotpblack);
//                                    newLoginScreenBinding.otplayoutEditText4.requestFocus();
//                                    newLoginScreenBinding.otplayoutEditText3.clearFocus();
//                                    newLoginScreenBinding.otplayoutEditText2.clearFocus();
//                                    newLoginScreenBinding.otplayoutEditText1.clearFocus();
//                                } else {
//                                    newLoginScreenBinding.otplayoutEditText3.setBackgroundResource(R.drawable.backgroundforotp);
//                                    newLoginScreenBinding.otplayoutEditText2.requestFocus();
//                                    newLoginScreenBinding.otplayoutEditText3.clearFocus();
//                                    newLoginScreenBinding.otplayoutEditText4.clearFocus();
//                                    newLoginScreenBinding.otplayoutEditText1.clearFocus();
//                                }
//                            }
//                        });
//                        newLoginScreenBinding.otplayoutEditText4.addTextChangedListener(new TextWatcher() {
//                            @Override
//                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                            }
//
//                            @Override
//                            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                            }
//
//                            @Override
//                            public void afterTextChanged(Editable editable) {
//                                if (editable.length() == 1) {
//                                    newLoginScreenBinding.otplayoutEditText4.setBackgroundResource(R.drawable.backgroundforotpblack);
//                                } else {
//                                    newLoginScreenBinding.otplayoutEditText4.setBackgroundResource(R.drawable.backgroundforotp);
//                                    newLoginScreenBinding.otplayoutEditText3.requestFocus();
//                                    newLoginScreenBinding.otplayoutEditText1.clearFocus();
//                                    newLoginScreenBinding.otplayoutEditText2.clearFocus();
//                                    newLoginScreenBinding.otplayoutEditText4.clearFocus();
//                                }
//                            }
//                        });
//
//                        newLoginScreenBinding.verifyOtpLoginpopup.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (!TextUtils.isEmpty(newLoginScreenBinding.otplayoutEditText1.getText().toString()) && !TextUtils.isEmpty(newLoginScreenBinding.otplayoutEditText2.getText().toString())
//                                        && !TextUtils.isEmpty(newLoginScreenBinding.otplayoutEditText3.getText().toString()) && !TextUtils.isEmpty(newLoginScreenBinding.otplayoutEditText4.getText().toString())) {
//                                    if (String.valueOf(otp).equals(newLoginScreenBinding.otplayoutEditText1.getText().toString() + newLoginScreenBinding.otplayoutEditText2.getText().toString() + newLoginScreenBinding.otplayoutEditText3.getText().toString() + newLoginScreenBinding.otplayoutEditText4.getText().toString())) {
////                            UserLoginController().getGlobalConfigurationApiCall(this, this)
//                                        dialog.dismiss();
//                                        HomeActivity.isLoggedin = true;
//                                        finish();
//                                        Intent intent1 = new Intent(HomeActivity.this, MyCartActivity.class);
//                                        startActivity(intent1);
//                                        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
//
////                    verify_otp_image.setImageResource(R.drawable.right_selection_green)
////                    SessionManager.setMobilenumber(mobileNum)
////                    startActivity(Intent(applicationContext, HomeActivity::class.java))
////                    finishAffinity()
////                    this.overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "Please enter valid OTP.", Toast.LENGTH_SHORT).show();
//                                        newLoginScreenBinding.otplayoutEditText1.setText("");
//                                        newLoginScreenBinding.otplayoutEditText2.setText("");
//                                        newLoginScreenBinding.otplayoutEditText3.setText("");
//                                        newLoginScreenBinding.otplayoutEditText4.setText("");
////                                newLoginScreenBinding.otplayoutLoginpopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
//////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                                newLoginScreenBinding.accesskeyErrorTextOtp.setVisibility( View.VISIBLE);
////                            verify_otp_image.setImageResource(R.drawable.right_selection_green)
////                            Utils.showSnackbar(MyProfileActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_invalid_otp_try_again));
//                                    }
//                                } else {
//                                    Toast.makeText(getApplicationContext(), "Please enter valid OTP", Toast.LENGTH_SHORT).show();
//                                    newLoginScreenBinding.otplayoutEditText1.setText("");
//                                    newLoginScreenBinding.otplayoutEditText2.setText("");
//                                    newLoginScreenBinding.otplayoutEditText3.setText("");
//                                    newLoginScreenBinding.otplayoutEditText4.setText("");
////                            newLoginScreenBinding.otplayoutLoginpopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
//////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                            newLoginScreenBinding.accesskeyErrorTextOtp.setVisibility( View.VISIBLE);
////                        Utils.showSnackbar(MyProfileActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_invalid_otp_try_again));
//                                }
//                            }
//                        });
//                        removeAllExpiryCallbacks();
//                        dialog.show();
// No need
//                    } else {
                        finish();
                        Intent intent1 = new Intent(HomeActivity.this, MyCartActivity.class);
                        startActivity(intent1);
                        finish();
                        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
//                    }
                }
            }
        });
        customerCareImg.setOnClickListener(v -> {
            if (customerHelpLayout.getVisibility() == View.VISIBLE) {
                customerCareImg.setBackgroundResource(R.drawable.icon_help_circle);
                TranslateAnimation animate = new TranslateAnimation(0, customerHelpLayout.getWidth(), 0, 0);
                animate.setDuration(2000);
                animate.setFillAfter(true);
                customerHelpLayout.startAnimation(animate);
                customerHelpLayout.setVisibility(View.GONE);
            } else {
                customerCareImg.setBackgroundResource(R.drawable.icon_help_circle);
                TranslateAnimation animate = new TranslateAnimation(customerHelpLayout.getWidth(), 0, 0, 0);
                animate.setDuration(2000);
                animate.setFillAfter(true);
                customerHelpLayout.startAnimation(animate);
                customerHelpLayout.setVisibility(View.VISIBLE);
            }
        });

        ImageView faqLayout = findViewById(R.id.faq);
        TextView helpText = findViewById(R.id.help_text);
        helpText.setText(getResources().getString(R.string.faq));
        faqLayout.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, FAQActivity.class)));

        RelativeLayout mySearchLayout = findViewById(R.id.mySearchLayout);
        RelativeLayout myCartLayout = findViewById(R.id.myCartLayout);
        RelativeLayout myOrdersLayout = findViewById(R.id.myOrdersLayout);
        RelativeLayout myOffersLayout = findViewById(R.id.myOffersLayout);
        RelativeLayout myProfileLayout = findViewById(R.id.myProfileLayout);

        ImageView dashboardSearchIcon = findViewById(R.id.dashboardSearchIcon);
        ImageView dashboardMyCartIcon = findViewById(R.id.dashboardMyCartIcon);
        ImageView dashboardMyOrdersIcon = findViewById(R.id.dashboardMyOrdersIcon);
        ImageView dashboardMyOffersIcon = findViewById(R.id.dashboardMyOffersIcon);
        ImageView dashboardMyProfileIcon = findViewById(R.id.dashboardMyProfileIcon);

        TextView dashboardMySearch = findViewById(R.id.dashboardMySearch);
        TextView dashboardMySearchText = findViewById(R.id.dashboardMySearchText);
        TextView dashboardMyCart = findViewById(R.id.dashboardMyCart);
        TextView dashboardMyCartText = findViewById(R.id.dashboardMyCartText);
        TextView dashboardMyOrders = findViewById(R.id.dashboardMyOrders);
        TextView dashboardMyOrdersText = findViewById(R.id.dashboardMyOrdersText);
        TextView dashboardMyOffers = findViewById(R.id.dashboardMyOffers);
        TextView dashboardMyOffersText = findViewById(R.id.dashboardMyOffersText);
        TextView dashboardMyProfile = findViewById(R.id.dashboardMyProfile);
        TextView dashboardMyProfileText = findViewById(R.id.dashboardMyProfileText);
        welcomeTxt = findViewById(R.id.welcome_txt);
        ImageView userLogout = findViewById(R.id.userLogout);
        ImageView dashboardApolloIcon = findViewById(R.id.apollo_logo);
        myCartCount = findViewById(R.id.lblCartCnt);
        LinearLayout uploadPrescriptionBtn = findViewById(R.id.upload_prescription);
        constraintLayout = findViewById(R.id.constraint_layout);


        userLogout.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(HomeActivity.this);
            dialog.setContentView(R.layout.dialog_custom_alert);
            if (dialog.getWindow() != null)
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            TextView dialogTitleText = dialog.findViewById(R.id.dialog_info);
            Button okButton = dialog.findViewById(R.id.dialog_ok);
            Button declineButton = dialog.findViewById(R.id.dialog_cancel);
            dialogTitleText.setText(getResources().getString(R.string.label_logout_alert));
            okButton.setText(getResources().getString(R.string.label_ok));
            declineButton.setText(getResources().getString(R.string.label_cancel_text));
            okButton.setOnClickListener(v1 -> {
                dialog.dismiss();
                SessionManager.INSTANCE.logoutUser();

                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
                finishAffinity();
            });
            declineButton.setOnClickListener(v12 -> dialog.dismiss());
        });

        dashboardApolloIcon.setClickable(false);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("cardReceiver"));

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverNew, new IntentFilter("OrderhistoryCardReciver"));
        Constants.getInstance().setConnectivityListener(this);

        mySearchLayout.setOnClickListener(v -> {
            mySearchLayout.setBackgroundResource(R.color.selected_menu_color);
            dashboardSearchIcon.setImageResource(R.drawable.dashboard_search_hover);
            dashboardMySearch.setTextColor(getResources().getColor(R.color.selected_text_color));
            dashboardMySearchText.setTextColor(getResources().getColor(R.color.selected_text_color));

            myCartLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyCartIcon.setImageResource(R.drawable.dashboard_cart);
            dashboardMyCart.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyCartText.setTextColor(getResources().getColor(R.color.colorWhite));

            myOrdersLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyOrdersIcon.setImageResource(R.drawable.dashboard_orders);
            dashboardMyOrders.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyOrdersText.setTextColor(getResources().getColor(R.color.colorWhite));

            myOffersLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyOffersIcon.setImageResource(R.drawable.dashboard_offers);
            dashboardMyOffers.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyOffersText.setTextColor(getResources().getColor(R.color.colorWhite));

            myProfileLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyProfileIcon.setImageResource(R.drawable.dashboard_profile);
            dashboardMyProfile.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyProfileText.setTextColor(getResources().getColor(R.color.colorWhite));

            Intent intent = new Intent(HomeActivity.this, MySearchActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });

        activityHomeBinding.shopProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MySearchActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            }
        });

        myCartLayout.setOnClickListener(v -> {
            mySearchLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardSearchIcon.setImageResource(R.drawable.dashboard_search);
            dashboardMySearch.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMySearchText.setTextColor(getResources().getColor(R.color.colorWhite));

            myCartLayout.setBackgroundResource(R.color.selected_menu_color);
            dashboardMyCartIcon.setImageResource(R.drawable.dashboard_cart_hover);
            dashboardMyCart.setTextColor(getResources().getColor(R.color.selected_text_color));
            dashboardMyCartText.setTextColor(getResources().getColor(R.color.selected_text_color));

            myOrdersLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyOrdersIcon.setImageResource(R.drawable.dashboard_orders);
            dashboardMyOrders.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyOrdersText.setTextColor(getResources().getColor(R.color.colorWhite));

            myOffersLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyOffersIcon.setImageResource(R.drawable.dashboard_offers);
            dashboardMyOffers.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyOffersText.setTextColor(getResources().getColor(R.color.colorWhite));

            myProfileLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyProfileIcon.setImageResource(R.drawable.dashboard_profile);
            dashboardMyProfile.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyProfileText.setTextColor(getResources().getColor(R.color.colorWhite));

            SessionManager.INSTANCE.setCurrentPage(ApplicationConstant.ORDER_SUCCESS);
            Intent intent1 = new Intent(HomeActivity.this, MyCartActivity.class);
            intent1.putExtra("activityname", "AddMoreActivity");
            startActivity(intent1);
            finish();
            overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
        });

        myOrdersLayout.setOnClickListener(v -> {
            mySearchLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardSearchIcon.setImageResource(R.drawable.dashboard_search);
            dashboardMySearch.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMySearchText.setTextColor(getResources().getColor(R.color.colorWhite));

            myCartLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyCartIcon.setImageResource(R.drawable.dashboard_cart);
            dashboardMyCart.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyCartText.setTextColor(getResources().getColor(R.color.colorWhite));

            myOrdersLayout.setBackgroundResource(R.color.selected_menu_color);
            dashboardMyOrdersIcon.setImageResource(R.drawable.dashboard_orders_hover);
            dashboardMyOrders.setTextColor(getResources().getColor(R.color.selected_text_color));
            dashboardMyOrdersText.setTextColor(getResources().getColor(R.color.selected_text_color));

            myOffersLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyOffersIcon.setImageResource(R.drawable.dashboard_offers);
            dashboardMyOffers.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyOffersText.setTextColor(getResources().getColor(R.color.colorWhite));

            myProfileLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyProfileIcon.setImageResource(R.drawable.dashboard_profile);
            dashboardMyProfile.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyProfileText.setTextColor(getResources().getColor(R.color.colorWhite));

            Intent intent1 = new Intent(this, MyOrdersActivity.class);
            startActivity(intent1);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });

        myOffersLayout.setOnClickListener(v -> {
            mySearchLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardSearchIcon.setImageResource(R.drawable.dashboard_search);
            dashboardMySearch.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMySearchText.setTextColor(getResources().getColor(R.color.colorWhite));

            myCartLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyCartIcon.setImageResource(R.drawable.dashboard_cart);
            dashboardMyCart.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyCartText.setTextColor(getResources().getColor(R.color.colorWhite));

            myOrdersLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyOrdersIcon.setImageResource(R.drawable.dashboard_orders);
            dashboardMyOrders.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyOrdersText.setTextColor(getResources().getColor(R.color.colorWhite));

            myOffersLayout.setBackgroundResource(R.color.selected_menu_color);
            dashboardMyOffersIcon.setImageResource(R.drawable.dashboard_offers_hover);
            dashboardMyOffers.setTextColor(getResources().getColor(R.color.selected_text_color));
            dashboardMyOffersText.setTextColor(getResources().getColor(R.color.selected_text_color));

            myProfileLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyProfileIcon.setImageResource(R.drawable.dashboard_profile);
            dashboardMyProfile.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyProfileText.setTextColor(getResources().getColor(R.color.colorWhite));

            Intent intent = new Intent(HomeActivity.this, MyOffersActivity.class);
            intent.putExtra("categoryname", "Promotions");
            startActivity(intent);
            finish();
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });

        myProfileLayout.setOnClickListener(v -> {
            mySearchLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardSearchIcon.setImageResource(R.drawable.dashboard_search);
            dashboardMySearch.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMySearchText.setTextColor(getResources().getColor(R.color.colorWhite));

            myCartLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyCartIcon.setImageResource(R.drawable.dashboard_cart);
            dashboardMyCart.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyCartText.setTextColor(getResources().getColor(R.color.colorWhite));

            myOrdersLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyOrdersIcon.setImageResource(R.drawable.dashboard_orders);
            dashboardMyOrders.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyOrdersText.setTextColor(getResources().getColor(R.color.colorWhite));

            myOffersLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyOffersIcon.setImageResource(R.drawable.dashboard_offers);
            dashboardMyOffers.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyOffersText.setTextColor(getResources().getColor(R.color.colorWhite));

            myProfileLayout.setBackgroundResource(R.color.selected_menu_color);
            dashboardMyProfileIcon.setImageResource(R.drawable.dashboard_profile_hover);
            dashboardMyProfile.setTextColor(getResources().getColor(R.color.selected_text_color));
            dashboardMyProfileText.setTextColor(getResources().getColor(R.color.selected_text_color));

            Intent intent1 = new Intent(this, MyProfileActivity.class);
            startActivity(intent1);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });
        if (null != SessionManager.INSTANCE.getDataList()) {
            if (SessionManager.INSTANCE.getDataList().size() > 0) {
                cartCountData(SessionManager.INSTANCE.getDataList().size());
            }
        }
        activityHomeBinding.scanPrescription.setOnClickListener(arg0 -> {
            List<String> imagePathList = new ArrayList<>();
            SessionManager.INSTANCE.setImagePath(imagePathList);
            //Orginal Code

//            Utils.dismissDialog();
//            finish();
//            Intent intent = new Intent(this, InsertPrescriptionActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);

            //new code
            Utils.dismissDialog();
            Intent intent = new Intent(this, EpsonScanActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);

//            Intent intent = new Intent(HomeActivity.this, BarcodeGenerationtoConnectActivity.class);
//            intent.putExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.SCANNER_MODE, "Image");
//            startActivity(intent);
        });

        uploadPrescriptionBtn.setOnClickListener(v -> {
            isUploadPrescription = true;
            checkGalleryPermission();
        });


        activityHomeBinding.scanProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
//                if (Constants.isAnyScannerConnected) {
//                    Utils.showSnackbar(HomeActivity.this, constraintLayout, "Scanner connected already");
//
////                    Toast.makeText(HomeActivity.this, "Scanner connected already", Toast.LENGTH_SHORT).show();
//                } else {
//                    Intent intent = new Intent(HomeActivity.this, BarcodeGenerationtoConnectActivity.class);
//                    intent.putExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.SCANNER_MODE, "BARCODE");
//                    startActivityForResult(intent, BarcodeGenerationtoConnectActivity.BARCODE_GENERATIONTO_CONNECT_ACTIVITY);
//                }


//                activityHomeBinding.transColorId.setVisibility(View.VISIBLE);
//                ProductScanDialog productScanDialog = new ProductScanDialog(HomeActivity.this);
//
//                productScanDialog.setPositiveListener(view -> {
//                    productScanDialog.dismiss();
//                    new IntentIntegrator(HomeActivity.this).setCaptureActivity(BarcodeScannerActivity.class).initiateScan();
//
//
////                    ItemBatchSelectionDilaog itemBatchSelectionDilaog = new ItemBatchSelectionDilaog(HomeActivity.this,null);
////                    ProductSearch medicine = new ProductSearch();
////                    medicine.setSku("APC0005");
////                    medicine.setQty(1);
////                    medicine.setName("Dolo");
////                    medicine.setMedicineType("PHAMRA");
////                    medicine.setPrice("6");
////                    medicine.setMou("");
////
////                    itemBatchSelectionDilaog.setUnitIncreaseListener(view3 -> {
////                        medicine.setQty(medicine.getQty() + 1);
////                        itemBatchSelectionDilaog.setQtyCount("" + medicine.getQty());
////                    });
////                    itemBatchSelectionDilaog.setUnitDecreaseListener(view4 -> {
////                        if (medicine.getQty() > 1) {
////                            medicine.setQty(medicine.getQty() - 1);
////                            itemBatchSelectionDilaog.setQtyCount("" + medicine.getQty());
////                        }
////                    });
////                    itemBatchSelectionDilaog.setPositiveListener(view2 -> {
////                        activityHomeBinding.transColorId.setVisibility(View.GONE);
////                        Intent intent = new Intent("cardReceiver");
////                        intent.putExtra("message", "Addtocart");
////                        intent.putExtra("product_sku", medicine.getSku());
////                        intent.putExtra("medicineType", medicine.getMedicineType());
////                        intent.putExtra("product_name", medicine.getName());
////                        intent.putExtra("product_quantyty", itemBatchSelectionDilaog.getQtyCount().toString());
////                        intent.putExtra("product_price", String.valueOf(medicine.getPrice()));
////                        // intent.putExtra("product_container", product_container);
////                        intent.putExtra("product_mou", String.valueOf(medicine.getMou()));
////                        intent.putExtra("product_position", String.valueOf(0));
////                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
////                        itemBatchSelectionDilaog.dismiss();
////                        Intent intent1 = new Intent(HomeActivity.this, MyCartActivity.class);
////                        intent.putExtra("activityname", "AddMoreActivity");
////                        startActivity(intent1);
////                        finish();
////                        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
////                    });
////                    itemBatchSelectionDilaog.setNegativeListener(v -> {
////                        activityHomeBinding.transColorId.setVisibility(View.GONE);
////                        itemBatchSelectionDilaog.dismiss();
////                    });
////                    itemBatchSelectionDilaog.show();
//
//                });
//                productScanDialog.setNegativeListener(v -> {
//                    activityHomeBinding.transColorId.setVisibility(View.GONE);
//                    productScanDialog.dismiss();
//                });
//                productScanDialog.show();
            }
        });
    }

    Handler usbScanHandler = new Handler();
    Runnable usbScanRunnable = new Runnable() {
        @Override
        public void run() {
            Utils.showDialog(HomeActivity.this, "Plaese wait...");
            new HomeActivityController(HomeActivity.this, HomeActivity.this).searchItemProducts(usbScanEdit.getText().toString());
            usbScanEdit.setText("");
        }
    };

    private void checkGalleryPermission() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            //Permission Granted
            if (isUploadPrescription) {
                handleNextActivity();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_READ_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == MY_READ_PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Permission Granted
            if (isUploadPrescription) {
                handleNextActivity();
            }
        }
    }

    private void handleNextActivity() {
        isUploadPrescription = false;
        Utils.dismissDialog();
        finish();
        Intent intent = new Intent(this, UploadPrescriptionActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
    }

    @Override
    public void onBackPressed() {
        HomeActivity.isHomeActivity = false;
//        super.onBackPressed();
//        startActivity(new Intent(HomeActivity.this, UserLoginActivity.class));
//        finish();
//        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
    }

    public void cartCountData(int count) {
        if (count != 0) {
            myCartCount.setVisibility(View.VISIBLE);
            myCartCount.setText(String.valueOf(count));
        } else {
            myCartCount.setVisibility(View.GONE);
            myCartCount.setText("0");
        }
    }

    private void handleRedeemPointsService() {
        if (!Constants.Get_Portfolio_of_the_User.isEmpty()) {
            if (NetworkUtils.isNetworkConnected(HomeActivity.this)) {
                Utils.showDialog(HomeActivity.this, getApplicationContext().getResources().getString(R.string.label_please_wait));
                homeActivityController.handleRedeemPoints(this);
            } else {
                Utils.showSnackbar(HomeActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
            }
        } else {
            Utils.showSnackbar(HomeActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_something_went_wrong));
        }
    }

    @Override
    public void categoryListSuccessRes(Categorylist_Response response) {
        if (response != null) {
            if (response.getStatus()) {
                List<CategoryList> list = response.getCategoryList();
                for (CategoryList list1 : list) {
                    if (list1.getKEY().equalsIgnoreCase("Baby_Care")) {
                        Constants.Baby_Care = list1.getVALUE();
                    } else if (list1.getKEY().equalsIgnoreCase("Health_Monitoring_Devices")) {
                        Constants.Health_Monitoring_Devices = list1.getVALUE();
                    } else if (list1.getKEY().equalsIgnoreCase("First_Aid")) {
                        Constants.First_Aid = list1.getVALUE();
                    } else if (list1.getKEY().equalsIgnoreCase("Health_Foods_Drinks")) {
                        Constants.Health_Foods_Drinks = list1.getVALUE();
                    } else if (list1.getKEY().equalsIgnoreCase("Beauty_Hygiene")) {
                        Constants.Beauty_Hygiene = list1.getVALUE();
                    } else if (list1.getKEY().equalsIgnoreCase("OTC")) {
                        Constants.OTC = list1.getVALUE();
                    } else if (list1.getKEY().equalsIgnoreCase("General_Wellness")) {
                        Constants.General_Wellness = list1.getVALUE();
                    } else if (list1.getKEY().equalsIgnoreCase("Mobility_Aids_Rehabilitation")) {
                        Constants.Mobility_Aids_Rehabilitation = list1.getVALUE();
                    } else if (list1.getKEY().equalsIgnoreCase("Nutrition_Supplement")) {
                        Constants.Nutrition_Supplement = list1.getVALUE();
                    } else if (list1.getKEY().equalsIgnoreCase("Anti_allergic_drugs")) {
                        Constants.Anti_allergic_drugs = list1.getVALUE();
                    } else if (list1.getKEY().equalsIgnoreCase("Infections_Infestation")) {
                        Constants.Infections_Infestation = list1.getVALUE();
                    } else if (list1.getKEY().equalsIgnoreCase("Diabetics")) {
                        Constants.Diabetics = list1.getVALUE();
                    } else if (list1.getKEY().equalsIgnoreCase("Cardiology")) {
                        Constants.Cardiology = list1.getVALUE();
                    } else if (list1.getKEY().equalsIgnoreCase("Skin_disorders")) {
                        Constants.Skin_disorders = list1.getVALUE();
                    } else if (list1.getKEY().equalsIgnoreCase("Gastro_entrology")) {
                        Constants.Gastro_entrology = list1.getVALUE();
                    } else if (list1.getKEY().equalsIgnoreCase("Endocrine_disorders")) {
                        Constants.Endocrine_disorders = list1.getVALUE();
                    } else if (list1.getKEY().equalsIgnoreCase("Promotions")) {
                        Constants.Promotions = list1.getVALUE();
                    } else if (list1.getKEY().equalsIgnoreCase("TrendingNow")) {
                        Constants.TrendingNow = list1.getVALUE();
                    } else if (list1.getKEY().equalsIgnoreCase("CNS_Drugs")) {
                        Constants.CNS_Drugs = list1.getVALUE();
                    } else if (list1.getKEY().equalsIgnoreCase("Generic")) {
                        Constants.Generic = list1.getVALUE();
                    }
                }
            }
        }
    }

    @Override
    public void onFailureService(String message) {
        Utils.dismissDialog();
        Utils.showSnackbar(HomeActivity.this, constraintLayout, message);
    }

    List<OCRToDigitalMedicineResponse> dummyDataList = new ArrayList<>();
    private float balanceQty;

    @Override
    public void onSuccessSearchItemApi(ItemSearchResponse itemSearchResponse) {
        if (itemSearchResponse.getItemList() != null && itemSearchResponse.getItemList().size() > 0) {
            ItemBatchSelectionDilaog itemBatchSelectionDilaog = new ItemBatchSelectionDilaog(HomeActivity.this, itemSearchResponse.getItemList().get(0).getArtCode());
            itemBatchSelectionDilaog.setItemBatchListDialogListener(this);
            ProductSearch medicine = new ProductSearch();
            medicine.setName(itemSearchResponse.getItemList().get(0).getGenericName());
            itemBatchSelectionDilaog.setTitle(itemSearchResponse.getItemList().get(0).getDescription());
            medicine.setSku(itemSearchResponse.getItemList().get(0).getArtCode());
            medicine.setQty(1);
            medicine.setDescription(itemSearchResponse.getItemList().get(0).getDescription());
            medicine.setCategory(itemSearchResponse.getItemList().get(0).getCategory());
            medicine.setMedicineType(itemSearchResponse.getItemList().get(0).getCategory());
            medicine.setIsInStock(itemSearchResponse.getItemList().get(0).getStockqty() != 0 ? 1 : 0);
            medicine.setIsPrescriptionRequired(0);
            medicine.setPrice(itemSearchResponse.getItemList().get(0).getMrp());


            itemBatchSelectionDilaog.setUnitIncreaseListener(view3 -> {
                if (itemBatchSelectionDilaog.getQtyCount() != null && !itemBatchSelectionDilaog.getQtyCount().isEmpty()) {
                    if (itemBatchSelectionDilaog.getQtyCount() != null && !itemBatchSelectionDilaog.getQtyCount().isEmpty()) {
                        medicine.setQty(Integer.parseInt(itemBatchSelectionDilaog.getQtyCount()) + 1);
                    } else {
                        medicine.setQty(medicine.getQty() + 1);
                    }
                    itemBatchSelectionDilaog.setQtyCount("" + medicine.getQty());
                } else {
                    Toast.makeText(HomeActivity.this, "Please enter product quantity", Toast.LENGTH_SHORT).show();
                }
            });
            itemBatchSelectionDilaog.setUnitDecreaseListener(view4 -> {
                if (itemBatchSelectionDilaog.getQtyCount() != null && !itemBatchSelectionDilaog.getQtyCount().isEmpty()) {
                    if (itemBatchSelectionDilaog.getQtyCount() != null && !itemBatchSelectionDilaog.getQtyCount().isEmpty()) {
                        medicine.setQty(Integer.parseInt(itemBatchSelectionDilaog.getQtyCount()));
                    }
                    if (medicine.getQty() > 1) {
                        medicine.setQty(medicine.getQty() - 1);
                        itemBatchSelectionDilaog.setQtyCount("" + medicine.getQty());
                    }
                }
            });
            itemBatchSelectionDilaog.setPositiveListener(view2 -> {
                activityHomeBinding.transColorId.setVisibility(View.GONE);

                itemBatchSelectionDilaog.globalBatchListHandlings(medicine.getDescription(), medicine.getSku(),
                        balanceQty, dummyDataList, HomeActivity.this, medicine.getMedicineType());

//                if (itemBatchSelectionDilaog.getQtyCount() != null && !itemBatchSelectionDilaog.getQtyCount().isEmpty()) {
//                    if (itemBatchSelectionDilaog.getItemBatchSelectionDataQty() != null && Float.parseFloat(itemBatchSelectionDilaog.getItemBatchSelectionDataQty().getQOH()) >= Float.parseFloat(itemBatchSelectionDilaog.getQtyCount())) {
//                        Intent intent = new Intent("cardReceiver");
//                        intent.putExtra("message", "Addtocart");
//                        intent.putExtra("product_sku", medicine.getSku());
//                        intent.putExtra("product_name", medicine.getDescription());
//                        intent.putExtra("product_quantyty", itemBatchSelectionDilaog.getQtyCount().toString());
//                        intent.putExtra("product_price", String.valueOf(itemBatchSelectionDilaog.getItemProice()));//String.valueOf(medicine.getPrice())
//                        // intent.putExtra("product_container", product_container);
//                        intent.putExtra("medicineType", medicine.getMedicineType());
//                        intent.putExtra("product_mou", String.valueOf(medicine.getMou()));
////                intent.putExtra("product_position", String.valueOf(0));
//                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//                        isDialogShow = false;
//                        itemBatchSelectionDilaog.dismiss();
//                    } else {
//                        Toast.makeText(HomeActivity.this, "Selected quantity is not available in batch", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(HomeActivity.this, "Please enter product quantity", Toast.LENGTH_SHORT).show();
//                }
            });
            itemBatchSelectionDilaog.setNegativeListener(v -> {
                activityHomeBinding.transColorId.setVisibility(View.GONE);
                isDialogShow = false;
                itemBatchSelectionDilaog.dismiss();
            });
            itemBatchSelectionDilaog.show();
            isDialogShow = true;
        } else {
            Utils.showSnackbar(HomeActivity.this, constraintLayout, "No Item found");
        }
        Utils.dismissDialog();

    }

    @Override
    public void onSearchFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccessRedeemPoints(PortFolioModel model) {
        if (model.getCustomerData() != null) {
            if (model.getCustomerData().getName() != null && !model.getCustomerData().getName().isEmpty()) {
                SessionManager.INSTANCE.setLoggedUserName(model.getCustomerData().getName());
                welcomeTxt.setText(getApplicationContext().getResources().getString(R.string.label_welcome) + " " + SessionManager.INSTANCE.getLoggedUserName());
            } else {
                welcomeTxt.setText(getApplicationContext().getResources().getString(R.string.label_welcome) + " " + getApplicationContext().getResources().getString(R.string.label_guest));
            }
        } else {
            welcomeTxt.setText(getApplicationContext().getResources().getString(R.string.label_welcome) + " " + getApplicationContext().getResources().getString(R.string.label_guest));
        }
    }

    private BroadcastReceiver mMessageReceiverNew = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (message.equalsIgnoreCase("OrderNow")) {
                if (null != SessionManager.INSTANCE.getDataList()) {
                    if (SessionManager.INSTANCE.getDataList().size() > 0) {
//                        cartCount(SessionManager.INSTANCE.getDataList().size());
                        dataList = SessionManager.INSTANCE.getDataList();
                    }
                }
                if (SessionManager.INSTANCE.getDataList() != null && SessionManager.INSTANCE.getDataList().size() > 0) {
                    activityHomeBinding.checkoutImage.setImageResource(R.drawable.checkout_cart);
                } else {
                    activityHomeBinding.checkoutImage.setImageResource(R.drawable.checkout_cart_unselect);
                }
                Utils.showSnackbar(HomeActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_item_added_cart));
//                cartCount(dataList.size());
            }

            if (null != SessionManager.INSTANCE.getDataList() && SessionManager.INSTANCE.getDataList().size() > 0)
                activityHomeBinding.checkoutImage.setVisibility(View.VISIBLE);
            else
                activityHomeBinding.checkoutImage.setVisibility(View.GONE);
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (message.equalsIgnoreCase("Addtocart")) {
                if (null != SessionManager.INSTANCE.getDataList()) {
                    if (SessionManager.INSTANCE.getDataList().size() > 0) {
//                        cartCount(SessionManager.INSTANCE.getDataList().size());
                        dataList = SessionManager.INSTANCE.getDataList();
                    }
                }
                boolean product_avilable = false;
                if (null != dataList) {
                    int count = 0;
                    for (OCRToDigitalMedicineResponse data : dataList) {
                        if (data.getArtCode() != null) {
                            if (data.getArtCode().equalsIgnoreCase(intent.getStringExtra("product_sku"))) {
                                product_avilable = true;
                                int qty = data.getQty();
                                qty = qty + 1;
                                dataList.remove(count);
                                OCRToDigitalMedicineResponse object1 = new OCRToDigitalMedicineResponse();
                                object1.setArtName(intent.getStringExtra("product_name"));
                                object1.setArtCode(intent.getStringExtra("product_sku"));
                                object1.setMedicineType(intent.getStringExtra("medicineType"));
                                object1.setQty(Integer.parseInt(intent.getStringExtra("product_quantyty")));
                                if (null != intent.getStringExtra("product_price")) {
                                    object1.setArtprice(intent.getStringExtra("product_price"));
                                } else {
                                    object1.setArtprice(String.valueOf(intent.getStringExtra("product_price")));
                                }
                                object1.setMou(String.valueOf(intent.getStringExtra("product_mou")));
//                                object1.setQty(qty);
                                object1.setContainer("Strip");
                                dataList.add(object1);
                                SessionManager.INSTANCE.setDataList(dataList);
                                break;
                            } else {
                                product_avilable = false;
                            }
                        }
                        count = count + 1;
                    }
                    if (!product_avilable) {
                        OCRToDigitalMedicineResponse object1 = new OCRToDigitalMedicineResponse();
                        object1.setArtName(intent.getStringExtra("product_name"));
                        object1.setArtCode(intent.getStringExtra("product_sku"));
                        object1.setMedicineType(intent.getStringExtra("medicineType"));
                        object1.setQty(Integer.parseInt(intent.getStringExtra("product_quantyty")));
                        if (null != intent.getStringExtra("product_price")) {
                            object1.setArtprice(intent.getStringExtra("product_price"));
                        } else {
                            object1.setArtprice(String.valueOf(intent.getStringExtra("product_price")));
                        }
                        object1.setMou(String.valueOf(intent.getStringExtra("product_mou")));
//                        object1.setQty(1);
                        object1.setContainer("Strip");
                        dataList.add(object1);
                        SessionManager.INSTANCE.setDataList(dataList);
                    }
                }
                if (SessionManager.INSTANCE.getDataList() != null && SessionManager.INSTANCE.getDataList().size() > 0) {
                    activityHomeBinding.checkoutImage.setImageResource(R.drawable.checkout_cart);
                } else {
                    activityHomeBinding.checkoutImage.setImageResource(R.drawable.checkout_cart_unselect);
                }
                Utils.showSnackbar(HomeActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_item_added_cart));
//                cartCount(dataList.size());
            }
        }
    };

    @Override
    protected void onPause() {
        usbScanHandler.removeCallbacks(usbScanRunnable);
        usbScanEdit.setText("");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiverNew);

        super.onPause();
    }

    private int scannerID;
    private int scannerType;
    static int picklistMode;
    static boolean pagerMotorAvailable;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == BarcodeGenerationtoConnectActivity.BARCODE_GENERATIONTO_CONNECT_ACTIVITY) {
                if (data != null) {

                    scannerID = (int) data.getSerializableExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.SCANNER_ID);
                    BaseActivity.lastConnectedScannerID = scannerID;
                    String scannerName = (String) data.getSerializableExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.SCANNER_NAME);
                    String address = (String) data.getSerializableExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.SCANNER_ADDRESS);
                    scannerType = (int) data.getSerializableExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.SCANNER_TYPE);

                    picklistMode = (int) data.getSerializableExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.PICKLIST_MODE);

                    pagerMotorAvailable = (Boolean) data.getSerializableExtra(com.apollo.pharmacy.ocr.zebrasdk.helper.Constants.PAGER_MOTOR_STATUS);

                    Constants.currentScannerId = scannerID;
                    Constants.currentScannerName = scannerName;
                    Constants.currentScannerAddress = address;

                    scannerConnectedorNot();

//                    String barcode_code = (String) data.getSerializableExtra("barcode_code");
//                    if (barcode_code != null) {
//                        Utils.showDialog(this,"Plaese wait...");
//                        homeActivityController.searchItemProducts(barcode_code);
//                    } else {
//                        Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
//                    }
                }
            }
        }


//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
////check for null
//        if (result != null) {
//            if (result.getContents() == null) {
//                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
//                homeActivityController.searchItemProducts(result.getContents());
//            }
//        } else {
//// This is important, otherwise the result will not be passed to the fragment
//        }

    }

    private int scannerEvent = 0;

    @Override
    public void scannerBarcodeEvent(byte[] barcodeData, int barcodeType, int scannerID) {
        if (!isDialogShow) {
            if (scannerEvent == 0) {
                scannerEvent = 1;
                barcodeEventHandle();
                String barcode_code = new String(barcodeData);
                if (barcode_code != null) {
//            Toast.makeText(this, barcode_code, Toast.LENGTH_LONG).show();
                    Utils.showDialog(this, "Plaese wait...");
                    homeActivityController.searchItemProducts(barcode_code);
                } else {
                    Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Utils.showSnackbar(HomeActivity.this, constraintLayout, "Please complete present action first");
        }
    }

    private void barcodeEventHandle() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scannerEvent = 0;
            }
        }, 5000);
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

    private void scannerConnectedorNot() {
        if (Constants.isAnyScannerConnected) {
            scannerStatus.setImageDrawable(getResources().getDrawable(R.drawable.tick_green));
        } else {
            scannerStatus.setImageDrawable(getResources().getDrawable(R.drawable.cross_icon));
        }
    }


    @Override
    public void onDismissDialog() {
        isDialogShow = false;
    }


    @Override
    public void onSuccessGlobalApiResponse(Global_api_response list) {

        List<API> apiList = list.getAPIS();
        for (API list1 : apiList) {
            String comparestring = list1.getNAME();
            String url = list1.getURL();
            Utils.printMessage("USERLOGINACTIVITY", "Api list--> " + url + list1.getNAME());
            if (comparestring.equals("Add_FCM_Token")) {
                Constants.Add_FCM_Token = list1.getURL();
            } else if (comparestring.equals("Send_Otp")) {
                Constants.Send_Otp = list1.getURL();
            } else if (comparestring.equals("Get_Past_Prescription")) {
                Constants.Get_Past_Prescription = list1.getURL();
            } else if (comparestring.equals("Delete_the_Prescription")) {
                Constants.Delete_the_Prescription = list1.getURL();
            } else if (comparestring.equals("Get_Portfolio_of_the_User")) {
                Constants.Get_Portfolio_of_the_User = list1.getURL();
            } else if (comparestring.equals("Get_Scanned_Prescription_Image")) {
                Constants.Get_Scanned_Prescription_Image = list1.getURL();
            } else if (comparestring.equals("Get_Prescription_Medicine_List")) {
                Constants.Get_Prescription_Medicine_List = list1.getURL();
            } else if (comparestring.equals("Get_Special_Offers_Products")) {
                Constants.Get_Special_Offers_Products = list1.getURL();
                Constants.Get_Product_List = list1.getURL();
            } else if (comparestring.equals("Get_Trending_now_Products")) {
                Constants.Get_Trending_now_Products = list1.getURL();
            } else if (comparestring.equals("Get_The_price_for_Past_Prescription_Medicine_list")) {
                Constants.Get_The_price_for_Past_Prescription_Medicine_list = list1.getURL();
            } else if (comparestring.equals("Search_Suggestions")) {
                Constants.Search_Suggestions = list1.getURL();
            } else if (comparestring.equals("Search_Product")) {
                Constants.Search_Product = list1.getURL();
            } else if (comparestring.equals("Get_Redeem_Points")) {
                Constants.Get_Redeem_Points = list1.getURL();
            } else if (comparestring.equals("Redeem_points_Send_Otp")) {
                Constants.Redeem_points_Send_Otp = list1.getURL();
            } else if (comparestring.equals("Redeem_Points_Resend_Otp")) {
                Constants.Redeem_Points_Resend_Otp = list1.getURL();
            } else if (comparestring.equals("Redeem_Points_Validate_Otp")) {
                Constants.Redeem_Points_Validate_Otp = list1.getURL();
            } else if (comparestring.equals("Redeem_points_Retry_Validate_Otp")) {
                Constants.Redeem_points_Retry_Validate_Otp = list1.getURL();
            } else if (comparestring.equals("Redeem_Points_Check_Voucher")) {
                Constants.Redeem_Points_Check_Voucher = list1.getURL();
            } else if (comparestring.equals("Redeem_Voucher")) {
                Constants.Redeem_Voucher = list1.getURL();
            } else if (comparestring.equals("Get_Store_Locator")) {
                Constants.Get_Store_Locator = list1.getURL();
            } else if (comparestring.equals("Get_Bit_List_for_Jiyo")) {
                Constants.Get_Bit_List_for_Jiyo = list1.getURL();
            } else if (comparestring.equals("Get_Display_Video_For_Jiyo")) {
                Constants.Get_Display_Video_For_Jiyo = list1.getURL();
            } else if (comparestring.equals("Paytm_Payment_Transaction")) {
                Constants.Paytm_Payment_Transaction = list1.getURL();
            } else if (comparestring.equals("Pinelab_Upload_Transaction")) {
                Constants.Pinelab_Upload_Transaction = list1.getURL();
            } else if (comparestring.equals("Pinelab_Get_Cloud_Bases_Transaction")) {
                Constants.Pinelab_Get_Cloud_Bases_Transaction = list1.getURL();
            } else if (comparestring.equals("Pinelab_Cancel_Transaction")) {
                Constants.Pinelab_Cancel_Transaction = list1.getURL();
            } else if (comparestring.equals("Get_User_Delivery_Address_List")) {
                Constants.Get_User_Delivery_Address_List = list1.getURL();
            } else if (comparestring.equals("Edit_User_Delivery_Address")) {
                Constants.Edit_User_Delivery_Address = list1.getURL();
            } else if (comparestring.equals("Add_User_Delivery_Address")) {
                Constants.Add_User_Delivery_Address = list1.getURL();
            } else if (comparestring.equals("Delete_User_Delivery_address")) {
                Constants.Delete_User_Delivery_address = list1.getURL();
            } else if (comparestring.equals("Get_State_city_List")) {
                Constants.Get_State_city_List = list1.getURL();
            } else if (comparestring.equals("Get_Order_History_For_User")) {
                Constants.Get_Order_History_For_User = list1.getURL();
            } else if (comparestring.equals("Order_Pushing_Api")) {
                Constants.Order_Pushing_Api = list1.getURL();
            } else if (comparestring.equals("Get_Image_link")) {
                Constants.Get_Image_link = list1.getURL();
            } else if (comparestring.equals("GET_CATEGORY_LIST")) {
                Constants.Categorylist_Url = list1.getURL();
            }
        }
//       handleFcmTokenFunctionality();
        if (SessionManager.INSTANCE.getLoggedUserName().isEmpty()) {
            handleRedeemPointsService(); //For Showing User Name on Dashboard
        } else {
            welcomeTxt.setText(getApplicationContext().getResources().getString(R.string.label_welcome) + " " + SessionManager.INSTANCE.getLoggedUserName());
        }
    }

//    private void handleFcmTokenFunctionality() {
//        if (NetworkUtils.isNetworkConnected(this)) {
//            FirebaseInstanceId.getInstance().getInstanceId()
//                    .addOnCompleteListener { task: Task<InstanceIdResult> ->
//                if (task.isSuccessful) {
//                    if (!isFcmAdded()) {
//                        userLoginController.handleFCMTokenRegistration(task.result.token, this)
//                    }
//                }
//            }
//        } else {
//            Utils.showSnackbar(this, constraint_layout, applicationContext.resources.getString(R.string.label_internet_error_text));
//        }
//    }

    @Override
    public void onFailure(String message) {

    }

    @Override
    public void onFailureConfigApi(String returnMessage) {

    }

    @Override
    public void onSuccessGlobalConfigurationApiCall(GetGlobalConfigurationResponse body) {

//        SessionManager.INSTANCE.setMobilenumber(mobileNum);
//        startActivity(Intent(getApplicationContext(), HomeActivity::class.java))
//        finishAffinity();
//        this.overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
    }

    @Override
    public void onSendSmsSuccess() {
        Utils.dismissDialog();
        if (!isResend) {
            newLoginScreenBinding.mobileNumLoginPopup.setVisibility(View.GONE);
            newLoginScreenBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//        entered_mobile_number.setText(mobileNum)
//            SessionManager.INSTANCE.setMobilenumber(mobileNum);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            newLoginScreenBinding.resendButtonNewLogin.setVisibility(View.GONE);
            newLoginScreenBinding.sendCodeinText.setVisibility(View.VISIBLE);
            newLoginScreenBinding.timerNewlogin.setVisibility(View.VISIBLE);
            cancelTimer();
            startTimer();
        }
    }

    @Override
    public void onSendSmsFailure() {
        Utils.dismissDialog();
        Utils.showCustomAlertDialog(this, "We are unable to process your request right now, Please try again later.", false, "OK", "");

    }


    void startTimer() {

        cTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished < 10) {
                    millisUntilFinished = Long.parseLong("0" + (millisUntilFinished / 1000));
                    newLoginScreenBinding.timerNewlogin.setText("" + (millisUntilFinished));
                } else {
                    newLoginScreenBinding.timerNewlogin.setText("00:" + (millisUntilFinished / 1000));
                }


            }

            public void onFinish() {
                newLoginScreenBinding.sendCodeinText.setVisibility(View.GONE);
                newLoginScreenBinding.timerNewlogin.setVisibility(View.GONE);
                newLoginScreenBinding.resendButtonNewLogin.setVisibility(View.VISIBLE);
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }
}