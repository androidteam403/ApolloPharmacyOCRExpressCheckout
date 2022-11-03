package com.apollo.pharmacy.ocr.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.mposstoresetup.MposStoreSetupActivity;
import com.apollo.pharmacy.ocr.adapters.MyOrdersAdapter;
import com.apollo.pharmacy.ocr.controller.MyOrdersController;
import com.apollo.pharmacy.ocr.databinding.DialogLoginPopupBinding;
import com.apollo.pharmacy.ocr.dialog.AccesskeyDialog;
import com.apollo.pharmacy.ocr.dialog.ReOrderDilaog;
import com.apollo.pharmacy.ocr.interfaces.MyOrdersListener;
import com.apollo.pharmacy.ocr.model.Meta;
import com.apollo.pharmacy.ocr.model.OCRToDigitalMedicineResponse;
import com.apollo.pharmacy.ocr.model.OrderHistoryResponse;
import com.apollo.pharmacy.ocr.model.PricePrescriptionResponse;
import com.apollo.pharmacy.ocr.model.ScannedData;
import com.apollo.pharmacy.ocr.model.ScannedMedicine;
import com.apollo.pharmacy.ocr.model.SelfOrderHistoryResponse;
import com.apollo.pharmacy.ocr.model.Send_Sms_Request;
import com.apollo.pharmacy.ocr.receiver.ConnectivityReceiver;
import com.apollo.pharmacy.ocr.utility.ApplicationConstant;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.utility.NetworkUtils;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import static com.apollo.pharmacy.ocr.activities.HomeActivity.mobileNum;
import static com.apollo.pharmacy.ocr.utility.Constants.getContext;

public class MyOrdersActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener,
        MyOrdersListener {

    private RecyclerView orderListRecyclerView;
    private MyOrdersAdapter orderdetails_adaptor;
    private List<OrderHistoryResponse> orderdetials_list = new ArrayList<>();

    private List<SelfOrderHistoryResponse.Order> orderList = new ArrayList<>();
    List<OrderHistoryResponse> dateList = new ArrayList<>();
    private MyOrdersController myOrdersController;
    private TextView myCartCount;
    private List<OCRToDigitalMedicineResponse> dataList = new ArrayList<>();
    private ConstraintLayout constraintLayout;
    private Button refresh_button;
    private Context context;
    public int i, j;
    private String oldMobileNum= "";
    private int otp= 0;
//    private DialogLoginPopupBinding dialogLoginPopupBinding;

    @Override
    protected void onResume() {
        super.onResume();
        MyOrdersActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

    private void initLeftMenu() {
        ImageView faqLayout = findViewById(R.id.faq);
        TextView helpText = findViewById(R.id.help_text);
        helpText.setText(getResources().getString(R.string.faq));
        faqLayout.setOnClickListener(view -> startActivity(new Intent(MyOrdersActivity.this, FAQActivity.class)));

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
        refresh_button = findViewById(R.id.refresh_button);

        myCartCount = findViewById(R.id.lblCartCnt);

        if (null != SessionManager.INSTANCE.getDataList()) {
            if (SessionManager.INSTANCE.getDataList().size() > 0) {
                List<OCRToDigitalMedicineResponse> tempCartItemList = new ArrayList<>();
                tempCartItemList = SessionManager.INSTANCE.getDataList();
                for (OCRToDigitalMedicineResponse item : tempCartItemList) {
                    dataList.add(item);
                }
                SessionManager.INSTANCE.setDataList(dataList);
                cartCount(dataList.size());
            }
        }
        ImageView userLogout = findViewById(R.id.userLogout);
        userLogout.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(MyOrdersActivity.this);
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
//                SessionManager.INSTANCE.logoutUser();
                HomeActivity.isLoggedin=false;
                Intent intent = new Intent(MyOrdersActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
                finishAffinity();


            });
            declineButton.setOnClickListener(v12 -> dialog.dismiss());
        });

        ImageView dashboardApolloIcon = findViewById(R.id.apollo_logo);
        dashboardApolloIcon.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            finishAffinity();
        });


        refresh_button.setOnClickListener(v -> {
            finish();
            startActivity(getIntent());
        });

        myOrdersLayout.setBackgroundResource(R.color.selected_menu_color);
        dashboardMyOrdersIcon.setImageResource(R.drawable.dashboard_orders_hover);
        dashboardMyOrders.setTextColor(getResources().getColor(R.color.selected_text_color));
        dashboardMyOrdersText.setTextColor(getResources().getColor(R.color.selected_text_color));
        myOrdersLayout.setClickable(false);

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

            Intent intent = new Intent(this, MySearchActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
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

            Intent intent1 = new Intent(MyOrdersActivity.this, MyCartActivity.class);
            intent1.putExtra("activityname", "AddMoreActivity");
            startActivity(intent1);
            finish();
            overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
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

            Intent intent = new Intent(this, MyOffersActivity.class);
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
            finish();
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        myOrdersController = new MyOrdersController(this);
        context = this;
        HomeActivity.isPaymentSelectionActivity=false;
        HomeActivity.isHomeActivity=false;

//        if(!HomeActivity.isLoggedin){
//            Dialog dialog = new Dialog(context);
//            dialogLoginPopupBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_login_popup, null, false);
//            dialog.setContentView(dialogLoginPopupBinding.getRoot());
//            if (dialog.getWindow() != null)
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            dialog.setCancelable(true);
////            final Dialog dialog = new Dialog(context);
//            // if button is clicked, close the custom dialog
//            dialogLoginPopupBinding.submit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(dialogLoginPopupBinding.mobileNumEditText.getText().toString()!=null && dialogLoginPopupBinding.mobileNumEditText.getText().toString()!=""){
//                        if (SessionManager.INSTANCE.getStoreId() != null && !SessionManager.INSTANCE.getStoreId().isEmpty()
//                                && SessionManager.INSTANCE.getTerminalId() != null && !SessionManager.INSTANCE.getTerminalId().isEmpty() && SessionManager.INSTANCE.getEposUrl() != null && !SessionManager.INSTANCE.getEposUrl().isEmpty()) {
//                            String MobilePattern = "[0-9]{10}";
//                            mobileNum = dialogLoginPopupBinding.mobileNumEditText.getText().toString();
//                            if (mobileNum.length() < 10) {
////                                di.setImageResource(R.drawable.right_selection_green);
//                                dialogLoginPopupBinding.mobileNumLoginPopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
//                                dialogLoginPopupBinding.accesskeyErrorText.setVisibility( View.VISIBLE);
//                            }
//                            else {
//                                dialogLoginPopupBinding.accesskeyErrorText.setVisibility( View.INVISIBLE);
////                                send_otp_image.setImageResource(R.drawable.right_selection_green)
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_country_code_bg)
////                                edittext_error_text.visibility = View.INVISIBLE
//                                if (oldMobileNum.equals(dialogLoginPopupBinding.mobileNumEditText.getText().toString()) && dialogLoginPopupBinding.mobileNumEditText.getText().toString().length() > 0 && (mobileNum.matches(MobilePattern))) {
//                                    dialogLoginPopupBinding.mobileNumLoginPopup.setVisibility(View.GONE);
//                                    dialogLoginPopupBinding.submitLoginPopup.setVisibility(View.GONE);
//                                    dialogLoginPopupBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//                                    dialogLoginPopupBinding.verifyOtpLoginpopup.setVisibility(View.VISIBLE);
//                                } else {
//                                    dialogLoginPopupBinding.mobileNumLoginPopup.setVisibility(View.GONE);
//                                    dialogLoginPopupBinding.submitLoginPopup.setVisibility(View.GONE);
//                                    dialogLoginPopupBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//                                    dialogLoginPopupBinding.verifyOtpLoginpopup.setVisibility(View.VISIBLE);
//                                    if (dialogLoginPopupBinding.mobileNumEditText.getText().toString().length() > 0) {
//                                        oldMobileNum = dialogLoginPopupBinding.mobileNumEditText.getText().toString();
//                                        if (mobileNum.matches(MobilePattern)) {
//                                            Utils.showDialog(MyOrdersActivity.this, "Sending OTP…");
//                                            otp = (int) ((Math.random() * 9000) + 1000);
//                                            if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
//                                                Send_Sms_Request sms_req = new Send_Sms_Request();
//                                                sms_req.setMobileNo(mobileNum);
//                                                sms_req.setMessage("Dear Apollo Customer, Your one time password is " + String.valueOf(otp) + " and is valid for 3mins.");
//                                                sms_req.setIsOtp(true);
//                                                sms_req.setOtp(String.valueOf(otp));
//                                                sms_req.setApiType("KIOSk");
//                                                myOrdersController.handleSendSmsApi(sms_req);
//                                            } else {
//                                                Utils.showSnackbar(getApplicationContext(), constraintLayout, "Internet Connection Not Available");
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        else {
//                            AccesskeyDialog accesskeyDialog = new AccesskeyDialog(MyOrdersActivity.this);
//                            accesskeyDialog.onClickSubmit(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    accesskeyDialog.listener();
//                                    if (accesskeyDialog.validate()) {
//                                        Intent intent = new Intent(MyOrdersActivity.this, MposStoreSetupActivity.class);
//                                        startActivity(intent);
//                                        overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                                        accesskeyDialog.dismiss();
//                                    }
//                                }
//                            });
//
//
//
//                            accesskeyDialog.show();
////                Intent intent = new Intent(MainActivity.this, MposStoreSetupActivity.class);
////                startActivity(intent);
////                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                        }
//                    }
////                    dialog.dismiss();
//                }
//            });
//
//            dialogLoginPopupBinding.verifyOtpLoginpopup.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (!TextUtils.isEmpty(dialogLoginPopupBinding.otplayoutEditText.getText().toString()) && dialogLoginPopupBinding.otplayoutEditText.getText().toString().length() > 0) {
//                        if (String.valueOf(otp).equals(dialogLoginPopupBinding.otplayoutEditText.getText().toString()) ) {
////                            UserLoginController().getGlobalConfigurationApiCall(this, this)
//                            dialog.dismiss();
//                            HomeActivity.isLoggedin=true;
////                    verify_otp_image.setImageResource(R.drawable.right_selection_green)
////                    SessionManager.setMobilenumber(mobileNum)
////                    startActivity(Intent(applicationContext, HomeActivity::class.java))
////                    finishAffinity()
////                    this.overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
//                        } else {
//                            dialogLoginPopupBinding.otplayoutLoginpopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
//                            dialogLoginPopupBinding.accesskeyErrorTextOtp.setVisibility( View.VISIBLE);
////                            verify_otp_image.setImageResource(R.drawable.right_selection_green)
////                            Utils.showSnackbar(MyProfileActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_invalid_otp_try_again));
//                        }
//                    } else {
//                        dialogLoginPopupBinding.otplayoutLoginpopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
//                        dialogLoginPopupBinding.accesskeyErrorTextOtp.setVisibility( View.VISIBLE);
////                        Utils.showSnackbar(MyProfileActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_invalid_otp_try_again));
//                    }
//                }
//            });
//            dialog.show();
//        }

        initLeftMenu();
        orderListRecyclerView = findViewById(R.id.myOrdersRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        orderListRecyclerView.setLayoutManager(mLayoutManager);
        orderListRecyclerView.setHasFixedSize(true);
        orderListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        constraintLayout = findViewById(R.id.constraint_layout);

        ImageView imageView = findViewById(R.id.customer_care_icon);
        LinearLayout linearText = findViewById(R.id.customer_help_layout);
        imageView.setOnClickListener(v -> {
            if (linearText.getVisibility() == View.VISIBLE) {
                imageView.setBackgroundResource(R.drawable.icon_help_circle);
                linearText.setVisibility(View.GONE);
            } else {
                imageView.setBackgroundResource(R.drawable.icon_customer_care);
                linearText.setVisibility(View.VISIBLE);
            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("OrderhistoryCardReciver"));
        Constants.getInstance().setConnectivityListener(this);

        if (NetworkUtils.isNetworkConnected(MyOrdersActivity.this)) {
//            myOrdersController.getOrderHistory(this);
            myOrdersController.getSelfOrderHistoryApiCall(this);
        } else {
            Utils.showSnackbar(MyOrdersActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
        }
        SessionManager.INSTANCE.setCurrentPage(ApplicationConstant.ACTIVITY_ADDMORE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverAddmore, new IntentFilter("MedicineReciverAddMore"));
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            findViewById(R.id.networkErrorLayout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.networkErrorLayout).setVisibility(View.VISIBLE);
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (message.equalsIgnoreCase("OrderNow")) {
                Intent intent1 = new Intent(context, MyCartActivity.class);
                intent.putExtra("activityname", "MyOrdersActivity");
                startActivity(intent1);
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
    }

    @Override
    public void onPricePrescription(PricePrescriptionResponse response) {

    }

    @Override
    public void onOrderHistorySuccess(List<OrderHistoryResponse> response) {
        orderdetials_list = new ArrayList<OrderHistoryResponse>();
        orderdetials_list = response;


        if (response.size() > 0) {
//            for (int i = 0; i < response.size(); i++) {
//                orderdetials_list.add(response.get(i));
//                if (i == 4)
//                    break;
//                ;
//          }
//            orderdetials_list.addAll(response);
//            Collections.reverse(orderdetials_list);
//for(int i=0; i<orderdetials_list.size(); i++){
//    if (orderdetials_list.get(i).getStatusHistory().get(i).getStatus().equalsIgnoreCase(""))
//}
//


//            for( i=0;i<=orderdetials_list.size()-2;i++){
//                for ( j=+1;j<=orderdetials_list.size()-1;j++){
//                    Collections.sort(orderdetials_list, new Comparator<OrderHistoryResponse>() {
//                        public int compare(OrderHistoryResponse o1, OrderHistoryResponse o2) {
//
//                            return o1.getStatusHistory().get(i).getDateTime().compareTo(o2.getStatusHistory().get(j).getDateTime());
//                        }
//                    });
//                }
//            }

            Collections.sort(orderdetials_list, new Comparator<OrderHistoryResponse>() {
                public int compare(OrderHistoryResponse o1, OrderHistoryResponse o2) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date date1 = null;
                    Date date2 = null;
                    try {
                        date1 = dateFormat.parse(o1.getStatusHistory().get(0).getDateTime());
                        date2 = dateFormat.parse(o2.getStatusHistory().get(0).getDateTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    return date1.compareTo(date2);
                }
            });
            Collections.reverse(orderdetials_list);
//          Collections.sort(orderdetials_list, new Comparator<OrderHistoryResponse>() {
//                public int compare(OrderHistoryResponse o1, OrderHistoryResponse o2) {
//                  return o1.getStatusHistory().get(0).getDateTime().compareTo(o2.getStatusHistory().get(0).getDateTime());
//     }
//            });
//           SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
//            for (int i=0;i<=orderdetials_list.size()-2;i++){
//                for (int j=+1;j<=orderdetials_list.size()-1;j++){
//                    if (simpleDateFormat.format(orderdetials_list.get(i).getStatusHistory().get(i).getDateTime()).equals(simpleDateFormat.format(orderdetials_list.get(j).getStatusHistory().get(j).getDateTime()) )){
//                        dateList.add((OrderHistoryResponse) orderdetials_list);
//                    }
//                }
//
//            }


            orderdetails_adaptor = new MyOrdersAdapter(this, this, orderdetials_list);
//            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MyOrdersActivity.this);
//            orderListRecyclerView.setLayoutManager(mLayoutManager);
            orderListRecyclerView.setAdapter(orderdetails_adaptor);
            orderdetails_adaptor.notifyDataSetChanged();
        } else {
            Utils.showCustomAlertDialog(MyOrdersActivity.this, getApplicationContext().getResources().getString(R.string.try_again_later), false, getResources().getString(R.string.label_ok), "");
        }
    }

    @Override
    public void onOrderHistoryFailure(String error) {
//        Utils.showCustomAlertDialog(MyOrdersActivity.this, getResources().getString(R.string.label_server_err_message), false, getResources().getString(R.string.label_ok), "");
    }

    @Override
    public void onSelfOrderHistorySuccess(SelfOrderHistoryResponse selfOrderHistoryResponse) {
        if (selfOrderHistoryResponse.getOrders() != null && selfOrderHistoryResponse.getOrders().size() > 0) {
            this.orderList = selfOrderHistoryResponse.getOrders();

//            Collections.sort(orderList, new Comparator<SelfOrderHistoryResponse.Order>() {
//                public int compare(SelfOrderHistoryResponse.Order o1, SelfOrderHistoryResponse.Order o2) {
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                    Date date1 = null;
//                    Date date2 = null;
//                    try {
//                        date1 = dateFormat.parse(o1.getOrderJourney().get(0).getDate());
//                        date2 = dateFormat.parse(o2.getOrderJourney().get(0).getDate());
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    if (date1 != null && date2 != null)
//                        return date1.compareTo(date2);
//                    else
//                        return 0;
//                }
//            });
//            Collections.reverse(orderList);


            orderdetails_adaptor = new MyOrdersAdapter(this, orderList, this);
//            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MyOrdersActivity.this);
//            orderListRecyclerView.setLayoutManager(mLayoutManager);
            orderListRecyclerView.setAdapter(orderdetails_adaptor);
            orderdetails_adaptor.notifyDataSetChanged();

        } else {
            Utils.showCustomAlertDialog(MyOrdersActivity.this, getApplicationContext().getResources().getString(R.string.try_again_later), false, getResources().getString(R.string.label_ok), "");

        }

    }

    @Override
    public void onSelfOrderHistoryFailure(String error) {

    }

    @Override
    public void onDeletePrescriptionSuccess(Meta m) {

    }

    @Override
    public void onReorderClick(List<OCRToDigitalMedicineResponse> dataList) {
        ReOrderDilaog reOrderDilaog = new ReOrderDilaog(MyOrdersActivity.this, dataList);
        reOrderDilaog.setPositiveListener(view -> {
            reOrderDilaog.dataListLatest();

            if (null != SessionManager.INSTANCE.getDataList()) {
                if (SessionManager.INSTANCE.getDataList().size() > 0) {
                    List<OCRToDigitalMedicineResponse> tempCartItemList = new ArrayList<>();
                    tempCartItemList = SessionManager.INSTANCE.getDataList();
                    for (OCRToDigitalMedicineResponse listItem : tempCartItemList) {
                        boolean isItemEqual = false;
                        for (OCRToDigitalMedicineResponse duplecateItem : reOrderDilaog.dataListLatest()) {
                            if (duplecateItem.getArtCode().equals(listItem.getArtCode())) {
                                isItemEqual = true;
                            }
                        }
                        if (!isItemEqual)
                            reOrderDilaog.dataListLatest().add(listItem);
                    }
                }
            }
            SessionManager.INSTANCE.setDataList(reOrderDilaog.dataListLatest());
            Intent intent = new Intent("OrderhistoryCardReciver");
            intent.putExtra("message", "OrderNow");
            intent.putExtra("MedininesNames", new Gson().toJson(reOrderDilaog.dataListLatest()));
            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        });
        reOrderDilaog.setNegativeListener(view -> {
            reOrderDilaog.dismiss();
        });
        reOrderDilaog.show();
    }

    @Override
    public void onSendSmsSuccess() {
        Utils.dismissDialog();
//        dialogLoginPopupBinding.mobileNumLoginPopup.setVisibility(View.GONE);
//        dialogLoginPopupBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//        entered_mobile_number.setText(mobileNum)
        SessionManager.INSTANCE.setMobilenumber(mobileNum);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onSendSmsFailure() {
        Utils.dismissDialog();
        Utils.showCustomAlertDialog(this, "We are unable to process your request right now, Please try again later.", false, "OK", "");
    }

    public void cartCount(int count) {
        if (count != 0) {

            List<OCRToDigitalMedicineResponse> countUniques = new ArrayList<>();
            countUniques.addAll(SessionManager.INSTANCE.getDataList());

            for (int i = 0; i < countUniques.size(); i++) {
                for (int j = 0; j < countUniques.size(); j++) {
                    if (i != j && countUniques.get(i).getArtName().equals(countUniques.get(j).getArtName())) {
                        countUniques.remove(j);
                        j--;
                    }
                }
            }

            myCartCount.setVisibility(View.VISIBLE);
//            myCartCount.setText(String.valueOf(count));
            myCartCount.setText(String.valueOf(countUniques.size()));
        } else {
            myCartCount.setVisibility(View.GONE);
            myCartCount.setText("0");
        }
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiverAddmore);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
        Constants.getInstance().setConnectivityListener(null);
    }

    private BroadcastReceiver mMessageReceiverAddmore = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (message.equals("MedicinedataAddMore")) {
                ScannedData fcmMedicine = new Gson().fromJson(intent.getStringExtra("MedininesNames"), ScannedData.class);
                medConvertDialog(fcmMedicine);
            }
        }
    };

    public void medConvertDialog(ScannedData fcmMedicine) {
        final Dialog dialog = new Dialog(MyOrdersActivity.this);
        dialog.setContentView(R.layout.dialog_custom_alert);
        dialog.setCancelable(false);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView dialogTitleText = dialog.findViewById(R.id.dialog_info);
        Button okButton = dialog.findViewById(R.id.dialog_ok);
        Button declineButton = dialog.findViewById(R.id.dialog_cancel);
        declineButton.setVisibility(View.GONE);
        dialogTitleText.setText(getResources().getString(R.string.label_curation_completed_alert));
        okButton.setText(getResources().getString(R.string.label_yes));
        declineButton.setText(getResources().getString(R.string.label_no));
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ScannedMedicine> scannedList = new ArrayList<>();
                for (ScannedMedicine m : fcmMedicine.getMedicineList()) {
                    if (Double.parseDouble(Objects.requireNonNull(m.getArtprice())) > 0) {
                        OCRToDigitalMedicineResponse d = new OCRToDigitalMedicineResponse();
                        d.setArtName(m.getArtName());
                        d.setArtCode(m.getArtCode());
                        d.setQty(m.getQty());
                        d.setArtprice(m.getArtprice());
                        d.setContainer("");
                        d.setMou(m.getMou());
                        dataList.add(d);
                        scannedList.add(m);
                    }
                }
                dataList = removeDuplicate(dataList);
                SessionManager.INSTANCE.setScannedPrescriptionItems(scannedList);
                SessionManager.INSTANCE.setCurationStatus(false);
                if (null != SessionManager.INSTANCE.getDataList()) {
                    if (SessionManager.INSTANCE.getDataList().size() > 0) {
                        List<OCRToDigitalMedicineResponse> tempCartItemList = new ArrayList<>();
                        tempCartItemList = SessionManager.INSTANCE.getDataList();
                        for (OCRToDigitalMedicineResponse item : tempCartItemList) {
                            dataList.add(item);
                        }
                        SessionManager.INSTANCE.setDataList(dataList);
                        cartCount(SessionManager.INSTANCE.getDataList().size());
                    }
                }
                Utils.showSnackbar(MyOrdersActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_medicine_added_success));
                dialog.dismiss();
            }
        });
        declineButton.setOnClickListener(v -> dialog.dismiss());
    }

    public List<OCRToDigitalMedicineResponse> removeDuplicate(List<OCRToDigitalMedicineResponse> dataList) {
        Set<OCRToDigitalMedicineResponse> s = new TreeSet<OCRToDigitalMedicineResponse>(new Comparator<OCRToDigitalMedicineResponse>() {

            @Override
            public int compare(OCRToDigitalMedicineResponse o1, OCRToDigitalMedicineResponse o2) {
                if (o1.getArtCode().equalsIgnoreCase(o2.getArtCode())) {
                    return 0;
                }
                return 1;
            }
        });
        s.addAll(dataList);
        return new ArrayList<>(s);
    }
}