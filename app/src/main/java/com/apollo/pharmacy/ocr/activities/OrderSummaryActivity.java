package com.apollo.pharmacy.ocr.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.adapters.OrderSummaryAdapter;
import com.apollo.pharmacy.ocr.controller.OrderSummaryController;
import com.apollo.pharmacy.ocr.controller.PaymentOptionActivityController;
import com.apollo.pharmacy.ocr.interfaces.OrderSummaryListener;
import com.apollo.pharmacy.ocr.interfaces.PaymentSelectionListener;
import com.apollo.pharmacy.ocr.model.Customerdetails;
import com.apollo.pharmacy.ocr.model.Customerdetails_new;
import com.apollo.pharmacy.ocr.model.Itemdetail;
import com.apollo.pharmacy.ocr.model.Itemdetail_new;
import com.apollo.pharmacy.ocr.model.ModelMobileNumVerify;
import com.apollo.pharmacy.ocr.model.OCRToDigitalMedicineResponse;
import com.apollo.pharmacy.ocr.model.OrderPushingRequest;
import com.apollo.pharmacy.ocr.model.OrderPushingRequest_new;
import com.apollo.pharmacy.ocr.model.OrderPushingResponse;
import com.apollo.pharmacy.ocr.model.OrderPushingResponse_new;
import com.apollo.pharmacy.ocr.model.OrdersResult;
import com.apollo.pharmacy.ocr.model.OrdersResult_new;
import com.apollo.pharmacy.ocr.model.Paymentdetails;
import com.apollo.pharmacy.ocr.model.Paymentdetails_new;
import com.apollo.pharmacy.ocr.model.PinelabTransaction_cancelresponse;
import com.apollo.pharmacy.ocr.model.Pinelabs_paymenttransaction_response;
import com.apollo.pharmacy.ocr.model.Pinelabs_transaction_payment_request;
import com.apollo.pharmacy.ocr.model.Pinelabsrequest;
import com.apollo.pharmacy.ocr.model.Pinelabtransaction_cancelrequest;
import com.apollo.pharmacy.ocr.model.PinepayrequestResult;
import com.apollo.pharmacy.ocr.model.PrescUrl;
import com.apollo.pharmacy.ocr.model.Reddemponits_getpoints_response;
import com.apollo.pharmacy.ocr.model.Reddemponits_sendotp_response;
import com.apollo.pharmacy.ocr.model.Redeempoints_checkvoucher_request;
import com.apollo.pharmacy.ocr.model.Redeempoints_checkvoucher_response;
import com.apollo.pharmacy.ocr.model.Redeempoints_redeemvoucher_request;
import com.apollo.pharmacy.ocr.model.Redeempoints_redeemvoucher_response;
import com.apollo.pharmacy.ocr.model.Redeempoints_resendotp_response;
import com.apollo.pharmacy.ocr.model.Redeempoints_validateotp_response;
import com.apollo.pharmacy.ocr.model.Redeempoints_validateotp_retry_response;
import com.apollo.pharmacy.ocr.model.RequestData;
import com.apollo.pharmacy.ocr.model.StateCodes;
import com.apollo.pharmacy.ocr.model.Tpdetails;
import com.apollo.pharmacy.ocr.model.Tpdetails_new;
import com.apollo.pharmacy.ocr.model.UserAddress;
import com.apollo.pharmacy.ocr.network.ApiClient;
import com.apollo.pharmacy.ocr.network.ApiInterface;
import com.apollo.pharmacy.ocr.network.CallbackWithRetry;
import com.apollo.pharmacy.ocr.receiver.ConnectivityReceiver;
import com.apollo.pharmacy.ocr.utility.ApplicationConstant;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.utility.NetworkUtils;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;
import com.apollo.pharmacy.ocr.widget.CustomOTPKeyboard;
import com.apollo.pharmacy.ocr.widget.OtpView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Pattern;

import kotlin.jvm.internal.Intrinsics;
import retrofit2.Call;
import retrofit2.Response;

public class OrderSummaryActivity extends BaseActivity implements OrderSummaryListener, PaymentSelectionListener,
        ConnectivityReceiver.ConnectivityReceiverListener {

    public String LastActivity;
    private LinearLayout pay_button_layout, otp_keyboard_wrapper_layout;
    private String otp;
    private OrderSummaryController orderSummaryController;
    private String PaymentMethod = "";
    private int button_tag = 0;
    private int pluts_referenceid = 0;
    private CountDownTimer countDownTimer;
    private String Random_Order_id;
    private String getotp;
    public String response = null;
    public HttpURLConnection connection;
    private int responseCode;
    private ConstraintLayout constraint_layout;
    private TextView availableHealthPoints;
    private float redeption_points = 0;
    private String rupeesymbol, subtotal_string, request_number;
    private TextView subtotal_textview, totalpayable_textview, redeem_points_value;

    public OrderSummaryActivity() {
        super();
    }

    @Override
    public void onFailure(String error) {
        Utils.showCustomAlertDialog(OrderSummaryActivity.this, error, false, getResources().getString(R.string.label_ok), "");
    }

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
        OrderSummaryActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
    }

    //new code developed for new redeem points Api on 18-11-2019
    @Override
    public void onSuccessGetRedeemPoints(Reddemponits_getpoints_response response) {
        if (response != null) {
            if (response.getOneApolloProcessResult().getStatus().equalsIgnoreCase("True")) {
                String text = "\u20B9";
                byte[] utf8 = null;
                try {
                    utf8 = text.getBytes("UTF-8");
                    text = new String(utf8, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                availableHealthPoints.setText(response.getOneApolloProcessResult().getAvailablePoints());
            }
        }
    }

    @Override
    public void onSuccessRedeemPointsSendOtp(Reddemponits_sendotp_response response) {

    }

    @Override
    public void onSuccessRedeemPointsResendOtp(Redeempoints_resendotp_response response) {

    }

    @Override
    public void onSuccessRedeemPointsValidateOtp(Redeempoints_validateotp_response response) {
        if (response != null) {
            if (response.getOneApolloProcessResult().getStatus().equalsIgnoreCase("True")) {
                Redeempoints_checkvoucher_request main_request = new Redeempoints_checkvoucher_request();
                RequestData req_data = new RequestData();
                req_data.setStoreId("");
                req_data.setDocNum("");
                req_data.setMobileNum("");
                req_data.setReqBy("");
                req_data.setPoints("");
                req_data.setRRNO("");
                req_data.setOTP("");
                req_data.setAction("");
                req_data.setCoupon("");
                req_data.setType("");
                req_data.setCustomerID("");
                main_request.setRequestData(req_data);
                if (NetworkUtils.isNetworkConnected(OrderSummaryActivity.this)) {
                    orderSummaryController.redeempoints_checkvoucher(main_request);
                } else {
                    Utils.showSnackbar(OrderSummaryActivity.this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
                }
            }
        }
    }

    @Override
    public void onSuccessRedeemPointsValidateOtpRetry(Redeempoints_validateotp_retry_response response) {

    }

    @Override
    public void onSuccessRedeemPointsCheckVoucher(Redeempoints_checkvoucher_response response) {
        if (response != null) {
            if (response.getOneApolloProcessResult().getStatus().equalsIgnoreCase("True")) {
                Redeempoints_redeemvoucher_request main_request = new Redeempoints_redeemvoucher_request();
                RequestData req_data = new RequestData();
                req_data.setStoreId("");
                req_data.setDocNum("");
                req_data.setMobileNum("");
                req_data.setReqBy("");
                req_data.setPoints("");
                req_data.setRRNO("");
                req_data.setOTP("");
                req_data.setAction("");
                req_data.setCoupon("");
                req_data.setType("");
                req_data.setCustomerID("");
                main_request.setRequestData(req_data);
                if (NetworkUtils.isNetworkConnected(OrderSummaryActivity.this)) {
                    orderSummaryController.redeempoints_redeemvoucher(main_request);
                } else {
                    Utils.showSnackbar(OrderSummaryActivity.this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
                }
            }
        }
    }

    @Override
    public void onSuccessRedeemPointsRedeemVoucher(Redeempoints_redeemvoucher_response response) {
        if (response != null) {
            if (response.getOneApolloProcessResult().getStatus().equalsIgnoreCase("True")) {
                String text = "\u20B9";
                byte[] utf8 = null;
                try {
                    utf8 = text.getBytes("UTF-8");
                    text = new String(utf8, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                redeem_points_value.setText(text + " " + Float.parseFloat(availableHealthPoints.getText().toString()));
                String value = totalpayable_textview.getText().toString().trim().replace(rupeesymbol, "");
                totalpayable_textview.setText(rupeesymbol + String.format("%.2f", Float.parseFloat(value) - Float.parseFloat(availableHealthPoints.getText().toString())));
                availableHealthPoints.setText("00");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        otp_keyboard_wrapper_layout = (LinearLayout) findViewById(R.id.keyboard_wrapper_layout);
        constraint_layout = findViewById(R.id.constraint_layout);

        findViewById(R.id.back_icon).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });

        if (null != SessionManager.INSTANCE.getDeliverytype()) {
            if (SessionManager.INSTANCE.getDeliverytype().equalsIgnoreCase("DeliveryAtHome")) {
                Calendar c = Calendar.getInstance();
                c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                Date date = c.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                try {
                    Date parsedDate = sdf.parse(String.valueOf(date));
                    SimpleDateFormat print = new SimpleDateFormat("yyyy-MMM-dd", Locale.ENGLISH);
                    String current_date = print.format(parsedDate);
                    c.setTime(print.parse(current_date));
                    c.add(Calendar.DATE, 1);
                    current_date = print.format(c.getTime());

                    String[] date_array = current_date.split("-");
                    if (date_array.length > 0) {
                        String date_str = date_array[2];
                        String month_str = date_array[1];
                        String year_str = date_array[0];
                        String delivery_time = date_str + " " + month_str + ", " + year_str;
                        SessionManager.INSTANCE.setDeliverydate(SessionManager.INSTANCE.getKioskSetupResponse().getAT_HOME_DELIVERABLE_TIME());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (SessionManager.INSTANCE.getDeliverytype().equalsIgnoreCase("PickfromStore")) {
                Calendar c = Calendar.getInstance();
                c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                Date date = c.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                try {
                    Date parsedDate = sdf.parse(String.valueOf(date));
                    SimpleDateFormat print = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss a", Locale.ENGLISH);
                    String current_date = print.format(parsedDate);
                    c.setTime(print.parse(current_date));
                    c.add(Calendar.MINUTE, 30);
                    current_date = print.format(c.getTime());
                    String time_str = "00";
                    String period = "AM";

                    String[] date_array = current_date.split("-");
                    if (date_array.length > 0) {
                        String date_str = date_array[2];
                        String[] time_array = date_str.split(" ");
                        if (time_array.length > 0) {
                            date_str = time_array[0];
                            time_str = time_array[1];
                            period = time_array[2];
                        }
                        String month_str = date_array[1];
                        String year_str = date_array[0];
                        String delivery_time = time_str + " " + period + ", " + date_str + " " + month_str + ", " + year_str;
                        SessionManager.INSTANCE.setDeliverydate(SessionManager.INSTANCE.getKioskSetupResponse().getAT_HOME_DELIVERABLE_TIME());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (SessionManager.INSTANCE.getDeliverytype().equalsIgnoreCase("AtKiosk")) {
                Calendar c = Calendar.getInstance();
                c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                Date date = c.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                try {
                    Date parsedDate = sdf.parse(String.valueOf(date));
                    SimpleDateFormat print = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss a", Locale.ENGLISH);
                    String current_date = print.format(parsedDate);
                    c.setTime(print.parse(current_date));
                    c.add(Calendar.MINUTE, 30);
                    current_date = print.format(c.getTime());
                    String time_str = "00";
                    String period = "AM";

                    String[] date_array = current_date.split("-");
                    if (date_array.length > 0) {
                        String date_str = date_array[2];
                        String[] time_array = date_str.split(" ");
                        if (time_array.length > 0) {
                            date_str = time_array[0];
                            time_str = time_array[1];
                            period = time_array[2];
                        }
                        String month_str = date_array[1];
                        String year_str = date_array[0];
                        String delivery_time = time_str + " " + period + ", " + date_str + " " + month_str + ", " + year_str;
                        SessionManager.INSTANCE.setDeliverydate(SessionManager.INSTANCE.getKioskSetupResponse().getAT_KiOSK_DELIVERABLE_TIME());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        subtotal_textview = (TextView) findViewById(R.id.subtotal_textview);
        totalpayable_textview = (TextView) findViewById(R.id.totalpayable_textview);
        redeem_points_value = (TextView) findViewById(R.id.redeem_points_value);

        rupeesymbol = "\u20B9";
        byte[] utf8 = null;
        try {
            utf8 = rupeesymbol.getBytes("UTF-8");
            rupeesymbol = new String(utf8, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        orderSummaryController = new OrderSummaryController(this);

        TextView typeTxt = findViewById(R.id.type_txt);
        TextView nameTxt = findViewById(R.id.name_txt);
        TextView mobileTxt = findViewById(R.id.mobile_txt);
        TextView pincodeTxt = findViewById(R.id.pincode_txt);
        TextView addressTxt = findViewById(R.id.address_txt);

        if (null != SessionManager.INSTANCE.getDeliverytype()) {
            typeTxt.setText(SessionManager.INSTANCE.getDeliverytype());
        }

        ImageView customerCareImg = findViewById(R.id.customer_care_icon);
        LinearLayout customerHelpLayout = findViewById(R.id.customer_help_layout);
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

        if (null != SessionManager.INSTANCE.getUseraddress()) {
            UserAddress userAddress = SessionManager.INSTANCE.getUseraddress();
            if (null != userAddress.getAddress1() && userAddress.getAddress1().length() > 0) {
                String address = userAddress.getAddress1().toUpperCase() + ", " + userAddress.getCity().toUpperCase() + ", " + userAddress.getState().toString().toUpperCase();
                address = address.replace("null", "");
                addressTxt.setText(address);
            } else {
                if (null != addressTxt && null != userAddress.getCity() && null != userAddress.getState()) {
                    String address = userAddress.getCity().toUpperCase() + ", " + userAddress.getState().toUpperCase();
                    address = address.replace("null", "");
                    addressTxt.setText(address);
                }
            }
            nameTxt.setText(userAddress.getName());
            mobileTxt.setText(userAddress.getMobile());
            pincodeTxt.setText(userAddress.getPincode());
        }

        List<OCRToDigitalMedicineResponse> dataList = new ArrayList<OCRToDigitalMedicineResponse>();
        if (null != SessionManager.INSTANCE.getDataList()) {
            dataList = SessionManager.INSTANCE.getDataList();
            float total_price = 0;
            for (OCRToDigitalMedicineResponse response : dataList) {
                if (null != response.getArtprice()) {
                    Float price = Float.parseFloat(response.getArtprice()) * response.getQty();
                    total_price = total_price + price;
                    subtotal_string = String.valueOf(total_price);
                    subtotal_textview.setText(rupeesymbol + String.format("%.2f", total_price));
                    totalpayable_textview.setText(rupeesymbol + String.format("%.2f", total_price));
                } else {
                    String price = "200";
                    total_price = total_price + Float.parseFloat(price);
                    subtotal_string = String.valueOf(total_price);
                    subtotal_textview.setText(rupeesymbol + String.format("%.2f", total_price));
                    totalpayable_textview.setText(rupeesymbol + String.format("%.2f", total_price));
                }
            }
        }
        RecyclerView productCartRecyclerView = findViewById(R.id.product_cart_recyclerView);
        productCartRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        OrderSummaryAdapter booksAdapter1 = new OrderSummaryAdapter(this, dataList);
        productCartRecyclerView.setAdapter(booksAdapter1);
        booksAdapter1.notifyDataSetChanged();
        pay_button_layout = (LinearLayout) findViewById(R.id.pay_button_layout);

        if (null != this.getIntent().getExtras() && null != this.getIntent().getExtras().getString("activityname")) {
            LastActivity = this.getIntent().getExtras().getString("activityname");
        }

        pay_button_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                checkPaymentOption();
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
    }

    private void checkPaymentOption() {
        boolean cardPaymentMode = SessionManager.INSTANCE.getKioskSetupResponse().getPaymentMode().getCard();
        boolean codPaymentMode = SessionManager.INSTANCE.getKioskSetupResponse().getPaymentMode().getCOD();
        boolean paytmPaymentMode = SessionManager.INSTANCE.getKioskSetupResponse().getPaymentMode().getPaytm();
        int paymentMode = 0;
        if (cardPaymentMode) {
            paymentMode++;
        }
        if (codPaymentMode) {
            paymentMode++;
        }
        if (paytmPaymentMode) {
            paymentMode++;
        }
        if (paymentMode >= 2) {
            handleNextActivity();
        } else {
            if (cardPaymentMode) {
                button_tag = 1;
                PaymentMethod = "PREPAID";
                handleCardSwipePayment();
            } else if (codPaymentMode) {
                button_tag = 2;
                PaymentMethod = "COD";
                Orderpushrequest();
            } else if (paytmPaymentMode) {
                button_tag = 3;
                PaymentMethod = "PREPAID";
                Dialog mDialog = Utils.getDialog(OrderSummaryActivity.this, R.layout.dialog_paytm_payment);
                mDialog.setCancelable(false);
                mDialog.setCanceledOnTouchOutside(false);
                OtpView otp_view = mDialog.findViewById(R.id.otp_view);
                otp_keyboard_wrapper_layout = mDialog.findViewById(R.id.otp_keyboard_wrapper_layout);
                mDialog.findViewById(R.id.verify_otp).setOnClickListener(view -> {
                    getotp = otp_view.getText().toString();
                    if (!getotp.isEmpty() && getotp.length() == 6) {
                        mDialog.dismiss();
                        new paytmtransaction().execute();
                    } else {
                        Utils.showSnackbar(OrderSummaryActivity.this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_enter_otp_alert));
                    }
                });
                mDialog.findViewById(R.id.close_image).setOnClickListener(view -> {
                    mDialog.dismiss();
                });
                mDialog.show();
                CustomOTPKeyboard keyboard = new CustomOTPKeyboard(OrderSummaryActivity.this);
                if (null != keyboard) {
                    InputConnection inputConnection = otp_view.onCreateInputConnection(new EditorInfo());
                    keyboard.setInputConnection(inputConnection);
                    otp_keyboard_wrapper_layout.removeAllViews();
                    otp_keyboard_wrapper_layout.addView(keyboard);
                }
            } else {
                Utils.showCustomAlertDialog(OrderSummaryActivity.this, getResources().getString(R.string.label_server_err_message), false, getApplicationContext().getResources().getString(R.string.label_ok), "");
            }
        }
    }

    private void handleNextActivity() {
        Intent intent = new Intent(OrderSummaryActivity.this, PaymentSelectionActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
    }

    private void handleCardSwipePayment() {
        Pinelabsrequest request = new Pinelabsrequest();
        request.setTransactionNumber("AK" + String.valueOf(System.currentTimeMillis()));
        request.setSequenceNumber(1);
        request.setAllowedPaymentMode("1");
        request.setMerchantStorePosCode("04190826");
        request.setAmount("1");
        request.setUserID("Apollo");
        request.setMerchantID(2300);
        request.setSecurityToken("661c9a5f-5d7a-4d76-a950-68beb8225cfd");
        request.setIMEI("APtest01");
        if (NetworkUtils.isNetworkConnected(OrderSummaryActivity.this)) {
            PaymentOptionActivityController.cardswipefunctionality(OrderSummaryActivity.this, OrderSummaryActivity.this, request);
        } else {
            Utils.showSnackbar(OrderSummaryActivity.this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
        }
    }

    @Override
    public void onSuccessOrderPush(OrderPushingResponse response) {
        if (response != null) {
            OrdersResult responsedata = response.getOrdersResult();
            String OrderStatus = responsedata.getStatus();
            String order_number = responsedata.getOrderno();
            if (OrderStatus.equalsIgnoreCase("success")) {
                Dialog mDialog = Utils.getOrderSuccessDialog(OrderSummaryActivity.this, R.layout.dialog_payment_success);
                mDialog.setCancelable(false);
                mDialog.setCanceledOnTouchOutside(false);
                ImageView deliveryModeTypeImage = mDialog.findViewById(R.id.delivery_mode_type_image);
                TextView deliveryModeTypeText = mDialog.findViewById(R.id.delivery_mode_type_text);
                TextView orderIdText = mDialog.findViewById(R.id.order_id_text);
                TextView itemsCountText = mDialog.findViewById(R.id.items_count_text);
                TextView estimatedDateText = mDialog.findViewById(R.id.estimated_date_text);
                TextView deliveryAddressText = mDialog.findViewById(R.id.delivery_address_text);
                ImageView downloadAskApolloImage = mDialog.findViewById(R.id.download_ask_apollo_image);
                LinearLayout downloadAskApolloLayout = mDialog.findViewById(R.id.download_ask_apollo_layout);
                LinearLayout trackOrderLayout = mDialog.findViewById(R.id.track_order_layout);

                if (button_tag == 1) {
                    deliveryModeTypeImage.setImageDrawable(getResources().getDrawable(R.drawable.payment_card_swipe));
                    deliveryModeTypeText.setText(getResources().getString(R.string.Credit_Card));
                } else if (button_tag == 2) {
                    deliveryModeTypeImage.setImageDrawable(getResources().getDrawable(R.drawable.payment_cash_on_delivery));
                    deliveryModeTypeText.setText(getResources().getString(R.string.COD));
                } else if (button_tag == 3) {
                    deliveryModeTypeImage.setImageDrawable(getResources().getDrawable(R.drawable.payment_paytm));
                    deliveryModeTypeText.setText(getResources().getString(R.string.label_paytm_cash_pay));
                }

                orderIdText.setText(order_number);
                itemsCountText.setText(SessionManager.INSTANCE.getDataList().size() + " " + getResources().getString(R.string.label_item));

                if (SessionManager.INSTANCE.getDeliverydate() != null) {
                    estimatedDateText.setText(SessionManager.INSTANCE.getDeliverydate());
                }

                if (SessionManager.INSTANCE.getUseraddress() != null) {
                    UserAddress useraddress = new UserAddress();
                    useraddress = SessionManager.INSTANCE.getUseraddress();
                    String address_str = "";
                    if (useraddress.getAddress1() != null) {
                        address_str = useraddress.getAddress1();
                    }
                    if (useraddress.getCity() != null) {
                        address_str = address_str + "," + useraddress.getCity();
                    }
                    if (useraddress.getState() != null) {
                        address_str = address_str + "," + useraddress.getState();
                    }
                    deliveryAddressText.setText(address_str);
                }

                downloadAskApolloLayout.setOnClickListener(v -> {
                    sendSMS();
                });
                downloadAskApolloImage.setOnClickListener(v -> {
                    sendSMS();
                });

                trackOrderLayout.setOnClickListener(v -> {
                    SessionManager.INSTANCE.clearMedicineData();
                    SessionManager.INSTANCE.clearDeliveryType();
                    SessionManager.INSTANCE.setOrderCompletionStatus(true);
                    SessionManager.INSTANCE.setCurrentPage(ApplicationConstant.ORDER_SUCCESS);
                    SessionManager.INSTANCE.clearUserAddressData();
                    SessionManager.INSTANCE.setUploadPrescriptionDeliveryMode(false);
                    SessionManager.INSTANCE.setScannedImagePath("");
                    SessionManager.INSTANCE.setDynamicOrderId("");

                    Intent intent = new Intent(OrderSummaryActivity.this, MyOrdersActivity.class);
                    startActivity(intent);
                    finishAffinity();
                });
                mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                mDialog.show();
                mDialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            } else {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(OrderSummaryActivity.this);
                builder1.setCancelable(false);
                builder1.setMessage(responsedata.getMessage() + "\n" + "with Order No :" + responsedata.getOrderno());
                builder1.setPositiveButton(
                        getApplicationContext().getResources().getString(R.string.label_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Utils.dismissDialog();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        } else {
            Utils.showCustomAlertDialog(OrderSummaryActivity.this, getApplicationContext().getResources().getString(R.string.try_again_later), false, getApplicationContext().getResources().getString(R.string.label_ok), "");
        }
    }

    @Override
    public void onSuccessOrderPushNew(OrderPushingResponse_new response) {
        if (response != null) {
            OrdersResult_new responsedata = response.getOrdersResult();
            Boolean OrderStatus = responsedata.getStatus();
            String order_number = responsedata.getOrderNo();
            if (OrderStatus) {
                Dialog mDialog = Utils.getOrderSuccessDialog(OrderSummaryActivity.this, R.layout.dialog_payment_success);
                mDialog.setCancelable(false);
                mDialog.setCanceledOnTouchOutside(false);
                ImageView deliveryModeTypeImage = mDialog.findViewById(R.id.delivery_mode_type_image);
                TextView deliveryModeTypeText = mDialog.findViewById(R.id.delivery_mode_type_text);
                TextView orderIdText = mDialog.findViewById(R.id.order_id_text);
                TextView itemsCountText = mDialog.findViewById(R.id.items_count_text);
                TextView estimatedDateText = mDialog.findViewById(R.id.estimated_date_text);
                TextView deliveryAddressText = mDialog.findViewById(R.id.delivery_address_text);
                ImageView downloadAskApolloImage = mDialog.findViewById(R.id.download_ask_apollo_image);
                LinearLayout downloadAskApolloLayout = mDialog.findViewById(R.id.download_ask_apollo_layout);
                LinearLayout trackOrderLayout = mDialog.findViewById(R.id.track_order_layout);

                if (button_tag == 1) {
                    deliveryModeTypeImage.setImageDrawable(getResources().getDrawable(R.drawable.payment_card_swipe));
                    deliveryModeTypeText.setText(getResources().getString(R.string.Credit_Card));
                } else if (button_tag == 2) {
                    deliveryModeTypeImage.setImageDrawable(getResources().getDrawable(R.drawable.payment_cash_on_delivery));
                    deliveryModeTypeText.setText(getResources().getString(R.string.COD));
                } else if (button_tag == 3) {
                    deliveryModeTypeImage.setImageDrawable(getResources().getDrawable(R.drawable.payment_paytm));
                    deliveryModeTypeText.setText(getResources().getString(R.string.label_paytm_cash_pay));
                }
                orderIdText.setText(order_number);
                itemsCountText.setText(SessionManager.INSTANCE.getDataList().size() + " " + getResources().getString(R.string.label_item));

                if (SessionManager.INSTANCE.getDeliverydate() != null) {
                    estimatedDateText.setText(SessionManager.INSTANCE.getDeliverydate());
                }

                if (SessionManager.INSTANCE.getUseraddress() != null) {
                    UserAddress useraddress = SessionManager.INSTANCE.getUseraddress();
                    String address_str = useraddress.getAddress1() + ", " + useraddress.getCity() + ", " + useraddress.getState();
                    deliveryAddressText.setText(address_str);
                }

                downloadAskApolloLayout.setOnClickListener(v -> {
                    sendSMS();
                });
                downloadAskApolloImage.setOnClickListener(v -> {
                    sendSMS();
                });

                trackOrderLayout.setOnClickListener(v -> {
                    SessionManager.INSTANCE.clearMedicineData();
                    SessionManager.INSTANCE.clearDeliveryType();
                    SessionManager.INSTANCE.setOrderCompletionStatus(true);
                    SessionManager.INSTANCE.setCurrentPage(ApplicationConstant.ORDER_SUCCESS);
                    SessionManager.INSTANCE.setScannedImagePath("");
                    SessionManager.INSTANCE.setUploadPrescriptionDeliveryMode(false);
                    SessionManager.INSTANCE.setDynamicOrderId("");
                    Intent intent = new Intent(OrderSummaryActivity.this, MyOrdersActivity.class);
                    startActivity(intent);
                    finishAffinity();
                });
                Objects.requireNonNull(mDialog.getWindow()).setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                mDialog.show();
                mDialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            } else {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(OrderSummaryActivity.this);
                builder1.setCancelable(false);
                builder1.setMessage(responsedata.getMessage());
                builder1.setPositiveButton(getApplicationContext().getResources().getString(R.string.label_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Utils.dismissDialog();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        } else {
            Utils.showCustomAlertDialog(OrderSummaryActivity.this, getApplicationContext().getResources().getString(R.string.try_again_later), false, getResources().getString(R.string.label_ok), "");
        }
    }

    @Override
    public void onSuccessPaytmTransaction(Object response) {
        String Reposnse = (String) response;
    }

    @Override
    public void onFailurePaytmTransaction(String error) {
        Utils.showCustomAlertDialog(OrderSummaryActivity.this, getResources().getString(R.string.label_server_err_message), false, "OK", "");
    }

    @Override
    public void onSuccessPinelabsTransaction(PinepayrequestResult response) {
        if (response != null) {
            PinepayrequestResult result = new PinepayrequestResult();
            result = response;
            pluts_referenceid = result.getPlutusTransactionReferenceID();
            if (result.getResponseMessage().equalsIgnoreCase("APPROVED")) {
                SessionManager.INSTANCE.setTransaction_referenceid(result.getPlutusTransactionReferenceID());
                SessionManager.INSTANCE.setPinelabtransaction_status();

                Utils.showDialog(OrderSummaryActivity.this, "Progressing");
                Pinelabs_transaction_payment_request requst = new Pinelabs_transaction_payment_request();
                requst.setIMEI("APtest01");
                requst.setMerchantID(2300);
                requst.setMerchantStorePosCode("04190826");
                requst.setPlutusTransactionReferenceID(pluts_referenceid);
                requst.setSecurityToken("661c9a5f-5d7a-4d76-a950-68beb8225cfd");
                if (NetworkUtils.isNetworkConnected(OrderSummaryActivity.this)) {
                    PaymentOptionActivityController.paymentstatusrequest(OrderSummaryActivity.this, OrderSummaryActivity.this, requst);
                } else {
                    Utils.showSnackbar(OrderSummaryActivity.this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
                }
                countDownTimer = new CountDownTimer(200000, 1000) {
                    int count = 0;

                    public void onTick(long millisUntilFinished) {
                        count++;
                        if (count == 10) {
                            count = 0;
                            Pinelabs_transaction_payment_request requst = new Pinelabs_transaction_payment_request();
                            requst.setIMEI("APtest01");
                            requst.setMerchantID(2300);
                            requst.setMerchantStorePosCode("04190826");
                            requst.setPlutusTransactionReferenceID(pluts_referenceid);
                            requst.setSecurityToken("661c9a5f-5d7a-4d76-a950-68beb8225cfd");
                            if (NetworkUtils.isNetworkConnected(OrderSummaryActivity.this)) {
                                PaymentOptionActivityController.paymentstatusrequest(OrderSummaryActivity.this, OrderSummaryActivity.this, requst);
                            } else {
                                Utils.showSnackbar(OrderSummaryActivity.this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
                            }
                        }
                    }

                    public void onFinish() {
                        Utils.dismissDialog();
                        Pinelabtransaction_cancelrequest request = new Pinelabtransaction_cancelrequest();
                        request.setIMEI("APtest01");
                        request.setAmount(1);
                        request.setMerchantID(2300);
                        request.setMerchantStorePosCode("04190826");
                        request.setPlutusTransactionReferenceID(SessionManager.INSTANCE.getTransaction_referenceid());
                        request.setSecurityToken("661c9a5f-5d7a-4d76-a950-68beb8225cfd");
                        if (NetworkUtils.isNetworkConnected(OrderSummaryActivity.this)) {
                            PaymentOptionActivityController.pinelabcanceltransaction(OrderSummaryActivity.this, OrderSummaryActivity.this, request);
                        } else {
                            Utils.showSnackbar(OrderSummaryActivity.this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
                        }
                        Utils.showSnackbar(OrderSummaryActivity.this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_transaction_fail_try_again_alert));
                    }
                }.start();
            } else {
                Pinelabtransaction_cancelrequest request = new Pinelabtransaction_cancelrequest();
                request.setIMEI("APtest01");
                request.setAmount(1);
                request.setMerchantID(2300);
                request.setMerchantStorePosCode("04190826");
                request.setPlutusTransactionReferenceID(SessionManager.INSTANCE.getTransaction_referenceid());
                request.setSecurityToken("661c9a5f-5d7a-4d76-a950-68beb8225cfd");
                if (NetworkUtils.isNetworkConnected(OrderSummaryActivity.this)) {
                    PaymentOptionActivityController.pinelabcanceltransaction(OrderSummaryActivity.this, OrderSummaryActivity.this, request);
                } else {
                    Utils.showSnackbar(OrderSummaryActivity.this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
                }
                Utils.showSnackbar(OrderSummaryActivity.this, constraint_layout, result.getResponseMessage());
            }
        } else {
            Utils.showCustomAlertDialog(OrderSummaryActivity.this, getApplicationContext().getResources().getString(R.string.try_again_later), false, getResources().getString(R.string.label_ok), "");
        }
    }

    @Override
    public void OnonSuccessPinelabsCancelTransaction(PinelabTransaction_cancelresponse response) {
        if (null != response && response.getResponseCode() == 0) {
            if (response.getResponseMessage().equalsIgnoreCase("APPROVED")) {
                Pinelabsrequest request = new Pinelabsrequest();
                request.setTransactionNumber("AK" + String.valueOf(System.currentTimeMillis()));
                request.setSequenceNumber(1);
                request.setAllowedPaymentMode("1");
                request.setMerchantStorePosCode("04190826");
                request.setAmount("1");
                request.setUserID("Apollo");
                request.setMerchantID(2300);
                request.setSecurityToken("661c9a5f-5d7a-4d76-a950-68beb8225cfd");
                request.setIMEI("APtest01");
                if (NetworkUtils.isNetworkConnected(OrderSummaryActivity.this)) {
                    PaymentOptionActivityController.cardswipefunctionality(OrderSummaryActivity.this, OrderSummaryActivity.this, request);
                } else {
                    Utils.showSnackbar(OrderSummaryActivity.this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
                }
            }
        } else {
            Utils.showCustomAlertDialog(this, response.getResponseMessage(), false, getResources().getString(R.string.label_ok), "");
        }
    }

    @Override
    public void onSuccessPinelabsPaymentTransaction(Pinelabs_paymenttransaction_response response) {
        Pinelabs_paymenttransaction_response response1 = new Pinelabs_paymenttransaction_response();
        response1 = response;
        if (response != null) {
            if (response1.getResponseMessage().equalsIgnoreCase("TXN APPROVED")) {
                Utils.dismissDialog();
                countDownTimer.cancel();
                Orderpushrequest();
            }
        } else {
            Utils.showCustomAlertDialog(OrderSummaryActivity.this, getApplicationContext().getResources().getString(R.string.try_again_later), false, getResources().getString(R.string.label_ok), "");
        }
    }

    public void sendSMS() {
        String askApolloTinyURL = "Please Download the Ask Apollo from here https://tinyurl.com/y9p3f49q";
        ApiInterface apiInterface = ApiClient.getApiService(Constants.Send_Otp);
        Call<ModelMobileNumVerify> call = apiInterface.verifyMobileNumber(ApplicationConstant.SMS_METHOD, ApplicationConstant.SMS_API, SessionManager.INSTANCE.getMobilenumber(), "APOLLO", askApolloTinyURL);
        call.enqueue(new CallbackWithRetry<ModelMobileNumVerify>(call) {
            public void onFailure(@NotNull Call callx, @NotNull Throwable t) {
                Intrinsics.checkParameterIsNotNull(callx, "call");
                Intrinsics.checkParameterIsNotNull(t, "t");
                Utils.showCustomAlertDialog(OrderSummaryActivity.this, t.getMessage(), false, getResources().getString(R.string.label_ok), "");
            }

            public void onResponse(@NotNull Call callx, @NotNull Response response) {
                Intrinsics.checkParameterIsNotNull(callx, "call");
                Intrinsics.checkParameterIsNotNull(response, "response");
            }
        });
    }

    public void Orderpushrequest() {
        Customerdetails cust_obj = new Customerdetails();
        cust_obj.setMobileno(SessionManager.INSTANCE.getMobilenumber());

        cust_obj.setMobileno(SessionManager.INSTANCE.getMobilenumber());
        UserAddress userAddress = SessionManager.INSTANCE.getUseraddress();
        if (null != userAddress.getAddress1() && userAddress.getAddress1().length() > 0) {
            cust_obj.setCommAddr(userAddress.getAddress1() + ", " + userAddress.getCity() + ", " + userAddress.getState() + ", " + userAddress.getPincode());
            cust_obj.setDelAddr(userAddress.getAddress1() + ", " + userAddress.getCity() + ", " + userAddress.getState() + ", " + userAddress.getPincode());
        } else {
            cust_obj.setCommAddr(userAddress.getCity() + ", " + userAddress.getState());
            cust_obj.setDelAddr(userAddress.getCity() + ", " + userAddress.getState());
        }
        cust_obj.setCity(userAddress.getCity());
        cust_obj.setPostcode(userAddress.getPincode());
        if (null != SessionManager.INSTANCE.getUserName() && SessionManager.INSTANCE.getUserName().length() > 0) {
            cust_obj.setFirstName(SessionManager.INSTANCE.getUserName());
        } else {
            cust_obj.setFirstName("Guest");
        }
        cust_obj.setFirstName(userAddress.getName());
        cust_obj.setLastName("");
        cust_obj.setMailid("");
        cust_obj.setAge(0);
        cust_obj.setCardno("00");
        cust_obj.setPatientName("");

        Paymentdetails payment_obj = new Paymentdetails();
        payment_obj.setTotalamount((float) 203.75);
        payment_obj.setPaymentsource("ccavenue");
        payment_obj.setPaymentstatus("approved");
        payment_obj.setPaymentorderid("107458598328");
        List<Itemdetail> itemdetail_obj = new ArrayList<>();

        if (null != SessionManager.INSTANCE.getCartItems() && SessionManager.INSTANCE.getCartItems().size() > 0) {
            for (int i = 0; i < SessionManager.INSTANCE.getCartItems().size(); i++) {
                Itemdetail new_obg = new Itemdetail();
                new_obg.setItemID(SessionManager.INSTANCE.getCartItems().get(i).getArtCode());
                new_obg.setItemName(SessionManager.INSTANCE.getCartItems().get(i).getArtName());
                new_obg.setQty(String.valueOf(SessionManager.INSTANCE.getCartItems().get(i).getQty()));
                new_obg.setOrderdate(Utils.getOrderedID());
                if (null != SessionManager.INSTANCE.getCartItems().get(i).getArtprice()) {
                    new_obg.setPricemrp(Float.parseFloat(SessionManager.INSTANCE.getCartItems().get(i).getArtprice()));
                }
                new_obg.setMou(SessionManager.INSTANCE.getCartItems().get(i).getMou());
                itemdetail_obj.add(new_obg);
            }
        }

        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        Random_Order_id = String.valueOf(n);
        Tpdetails main_obj = new Tpdetails();
        main_obj.setOrderid(Random_Order_id);
        main_obj.setShopid(SessionManager.INSTANCE.getStoreId());
        main_obj.setShippingMethod("Home Delivery - Fixed");
        main_obj.setPaymentMethod("ccavenue");
        main_obj.setVendorname("MAPP");
        main_obj.setDotorName("Apollo");
        main_obj.setUrl("");
        main_obj.setOrdertype("Fmcg");
        main_obj.setCustomerdetails(cust_obj);
        main_obj.setPaymentdetails(payment_obj);
        main_obj.setItemdetails(itemdetail_obj);
        main_obj.setOrderdate(Utils.getOrderedID());
        OrderPushingRequest request = new OrderPushingRequest();
        request.setTpdetails(main_obj);

        Customerdetails_new cust_obj_new = new Customerdetails_new();
        cust_obj_new.setMobileNo(SessionManager.INSTANCE.getMobilenumber());
        UserAddress userAddress_new = SessionManager.INSTANCE.getUseraddress();
        if (null != userAddress_new.getAddress1() && userAddress_new.getAddress1().length() > 0) {
            cust_obj_new.setCommAddr(userAddress_new.getAddress1() + ", " + userAddress_new.getCity() + ", " + userAddress_new.getState() + ", " + userAddress_new.getPincode());
            cust_obj_new.setDelAddr(userAddress_new.getAddress1() + ", " + userAddress_new.getCity() + ", " + userAddress_new.getState() + ", " + userAddress_new.getPincode());
        } else {
            cust_obj_new.setCommAddr(userAddress_new.getCity() + ", " + userAddress_new.getState());
            cust_obj_new.setDelAddr(userAddress_new.getCity() + ", " + userAddress_new.getState());
        }
        cust_obj_new.setCity(userAddress_new.getCity());
        cust_obj_new.setPostCode(userAddress_new.getPincode());
        if (null != SessionManager.INSTANCE.getUserName() && SessionManager.INSTANCE.getUserName().length() > 0) {
            cust_obj_new.setFirstName(SessionManager.INSTANCE.getUserName());
        } else {
            cust_obj_new.setFirstName("Guest");
        }
        cust_obj_new.setFirstName(userAddress_new.getName());
        cust_obj_new.setLastName("");
        cust_obj_new.setMailId("");
        cust_obj_new.setAge(0);
        cust_obj_new.setCardNo("1234567890");
        cust_obj_new.setPatientName(userAddress_new.getName());
        cust_obj_new.setUHID("");
        cust_obj_new.setLatitude(0);
        cust_obj_new.setLatitude(0);

        Paymentdetails_new payment_obj_new = new Paymentdetails_new();
        payment_obj_new.setPaymentSource("");
        if (PaymentMethod.equalsIgnoreCase("PREPAID")) {
            payment_obj_new.setPaymentStatus("true");
            payment_obj_new.setTotalAmount(String.valueOf((float) 1.00));
        } else if (PaymentMethod.equalsIgnoreCase("COD")) {
            payment_obj_new.setPaymentStatus("false");
            payment_obj_new.setTotalAmount(String.valueOf((float) 0.00));
        }
        payment_obj_new.setPaymentOrderId("");

        SimpleDateFormat sdf_new = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String currentDateandTime_new = sdf_new.format(new Date());
        String orderdate_new = currentDateandTime_new;
        List<Itemdetail_new> itemdetail_obj_new = new ArrayList<>();
        if (null != SessionManager.INSTANCE.getCartItems() && SessionManager.INSTANCE.getCartItems().size() > 0) {
            for (int i = 0; i < SessionManager.INSTANCE.getCartItems().size(); i++) {
                Itemdetail_new new_obg = new Itemdetail_new();
                new_obg.setItemID(SessionManager.INSTANCE.getCartItems().get(i).getArtCode());
                new_obg.setItemName(SessionManager.INSTANCE.getCartItems().get(i).getArtName());
                if (null != SessionManager.INSTANCE.getCartItems().get(i).getArtprice()) {
                    new_obg.setPrice(Double.valueOf(SessionManager.INSTANCE.getCartItems().get(i).getArtprice()));
                }
                if (SessionManager.INSTANCE.getCartItems().get(i).getMou() != null && !SessionManager.INSTANCE.getCartItems().get(i).getMou().isEmpty()) {
                    new_obg.setMOU(Integer.valueOf(SessionManager.INSTANCE.getCartItems().get(i).getMou()));
                    new_obg.setQty(SessionManager.INSTANCE.getCartItems().get(i).getQty() * Integer.parseInt(SessionManager.INSTANCE.getCartItems().get(i).getMou()));
                } else {
                    new_obg.setQty(SessionManager.INSTANCE.getCartItems().get(i).getQty());
                }
                itemdetail_obj_new.add(new_obg);
            }
        }

        List<PrescUrl> prescriptionurllist = new ArrayList<>();
        PrescUrl prescriptionurl = new PrescUrl();
        prescriptionurl.setUrl("");
        prescriptionurllist.add(prescriptionurl);

        int n_new = 10000;
        n_new = generator.nextInt(n);
        Random_Order_id = String.valueOf(n_new);

        Tpdetails_new main_obj_new = new Tpdetails_new();
        main_obj_new.setOrderId(Utils.getOrderedID());

        if (SessionManager.INSTANCE.getDeliverytype().equalsIgnoreCase("PickfromStore") || SessionManager.INSTANCE.getDeliverytype().equalsIgnoreCase("AtKiosk")) {
            if (SessionManager.INSTANCE.getDeliverytype().equalsIgnoreCase("PickfromStore")) {
                main_obj_new.setShopId(SessionManager.INSTANCE.getSiteId());
                main_obj_new.setShippingMethod("Strote Pickup");
                main_obj_new.setRemarks("");
            } else {
                main_obj_new.setShopId(SessionManager.INSTANCE.getKioskSetupResponse().getSTORE());
                main_obj_new.setShippingMethod("Strote Pickup");
                main_obj_new.setRemarks("Pick From Kiosk");
            }
        } else {
            main_obj_new.setShopId(SessionManager.INSTANCE.getStoreId());
            main_obj_new.setShippingMethod("Home Delivery");
            main_obj_new.setRemarks("");
        }

        // main_obj_new.setPaymentMethod("PREPAID");
        main_obj_new.setPaymentMethod(PaymentMethod);
        main_obj_new.setVendorName("MAPP");
        main_obj_new.setDotorName("Apollo");
        main_obj_new.setRequestType("CART");

        ArrayList<StateCodes> statecodelist = new ArrayList<>();
        try {
            String rawData = Utils.readJSONFromAsset(OrderSummaryActivity.this, "State_codes.json");
            statecodelist = new Gson().fromJson(rawData, new TypeToken<List<StateCodes>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String statename = userAddress.getState().trim();
        String statename1 = Utils.removeAllDigit(statename);
        for (StateCodes codes : statecodelist) {
            String codestate_name = codes.getName().trim();
            if (codestate_name.equalsIgnoreCase(statename1)) {
                main_obj_new.setStateCode(codes.getCode());
            }
        }
        main_obj_new.setTAT("");
        main_obj_new.setOrderType("Pharma");
        main_obj_new.setCouponCode("");
        main_obj_new.setOrderDate(orderdate_new);

        String device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        main_obj_new.setUserId(device_id);
        main_obj_new.setCustomerdetails_new(cust_obj_new);
        main_obj_new.setPaymentdetails_new(payment_obj_new);
        main_obj_new.setItemdetails_new(itemdetail_obj_new);
        main_obj.setOrderdate(Utils.getOrderedID());
        main_obj_new.setPrescUrl(prescriptionurllist);

        OrderPushingRequest_new request_new = new OrderPushingRequest_new();
        request_new.setTpdetails_new(main_obj_new);
        if (NetworkUtils.isNetworkConnected(OrderSummaryActivity.this)) {
            PaymentOptionActivityController.OrderPushingmethod_new(OrderSummaryActivity.this, request_new, OrderSummaryActivity.this);
        } else {
            Utils.showSnackbar(OrderSummaryActivity.this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
        }
    }

    private class paytmtransaction extends AsyncTask<Void, Void, String> {
        final ProgressDialog mProgress = new ProgressDialog(OrderSummaryActivity.this);

        @Override
        protected void onPreExecute() {
            Utils.showDialog(OrderSummaryActivity.this, getResources().getString(R.string.label_payment_process));
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String request = "16001&tid=001&Mnum=" + SessionManager.INSTANCE.getMobilenumber() + "&amt=1&bcv=" + getotp + "&Action=WITHDRAW&DC=TS&docnum=0002";
            String api_name = "https://online.apollopharmacy.org/PAYTM/" + "Default.aspx?siteid=" + request;
            return makeServiceCall_for_getorderhistory(api_name);
        }

        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                String[] Split_response = response.split(Pattern.quote("||"));
                if (Split_response.length > 0) {
                    String spilt_response1 = Split_response[0];
                    String[] split_response2 = spilt_response1.split(Pattern.quote("|"));
                    if (split_response2.length > 0) {
                        if (split_response2[0].equalsIgnoreCase("SUCCESS")) {
                            Utils.dismissDialog();
                            String error_message = split_response2[2];
                            Utils.showSnackbar(OrderSummaryActivity.this, constraint_layout, error_message);
                            Orderpushrequest();
                        } else {
                            Utils.dismissDialog();
                            String error_message = split_response2[2];
                            Utils.showSnackbar(OrderSummaryActivity.this, constraint_layout, error_message);
                        }
                    }
                }
            } else {
                Utils.dismissDialog();
                Utils.showCustomAlertDialog(OrderSummaryActivity.this, getApplicationContext().getResources().getString(R.string.try_again_later), false, getResources().getString(R.string.label_ok), "");
            }
        }
    }

    public String makeServiceCall_for_getorderhistory(String reqUrl) {
        try {
            URL gcmAPI = new URL(reqUrl);
            connection = (HttpURLConnection) gcmAPI.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.connect();

            InputStream in = new BufferedInputStream(connection.getInputStream());
            response = getStringFromInputStream(in);
            responseCode = connection.getResponseCode();
        } catch (MalformedURLException e) {
        } catch (ProtocolException e) {
        } catch (IOException e) {
        } catch (Exception e) {
        }
        return response;
    }

    private String getStringFromInputStream(InputStream is) throws XmlPullParserException, IOException {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
