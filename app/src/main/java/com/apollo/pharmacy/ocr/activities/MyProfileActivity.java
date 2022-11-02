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
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.mposstoresetup.MposStoreSetupActivity;
import com.apollo.pharmacy.ocr.adapters.MyOfersAdapterNew;
import com.apollo.pharmacy.ocr.adapters.PromotionsAdapter;
import com.apollo.pharmacy.ocr.controller.MyCartController;
import com.apollo.pharmacy.ocr.databinding.DialogLoginPopupBinding;
import com.apollo.pharmacy.ocr.dialog.AccesskeyDialog;
import com.apollo.pharmacy.ocr.enums.ViewMode;
import com.apollo.pharmacy.ocr.interfaces.CartCountListener;
import com.apollo.pharmacy.ocr.interfaces.MyCartListener;
import com.apollo.pharmacy.ocr.model.BatchListResponse;
import com.apollo.pharmacy.ocr.model.CalculatePosTransactionResponse;
import com.apollo.pharmacy.ocr.model.CustomerData;
import com.apollo.pharmacy.ocr.model.GetImageRes;
import com.apollo.pharmacy.ocr.model.GetProductListResponse;
import com.apollo.pharmacy.ocr.model.ItemSearchResponse;
import com.apollo.pharmacy.ocr.model.OCRToDigitalMedicineResponse;
import com.apollo.pharmacy.ocr.model.PortFolioModel;
import com.apollo.pharmacy.ocr.model.Product;
import com.apollo.pharmacy.ocr.model.ScannedData;
import com.apollo.pharmacy.ocr.model.ScannedMedicine;
import com.apollo.pharmacy.ocr.model.Send_Sms_Request;
import com.apollo.pharmacy.ocr.model.UpCellCrossCellResponse;
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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Response;

import static com.apollo.pharmacy.ocr.activities.HomeActivity.mobileNum;

public class MyProfileActivity extends BaseActivity implements MyCartListener, CartCountListener,
        ConnectivityReceiver.ConnectivityReceiverListener {

    private TextView name_txt, phone_txt, emial_address_txt, location_txt, address_txt, customer_id_txt;
    private PromotionsAdapter promotionAdaptor;
    private Context context;
    Dialog dialog;
    private ArrayList<Product> product_list_array;
    private int totalPage = 1, currentPage = 1;
    private HashMap<String, GetProductListResponse> productListResponseHashMap;
    private List<String> specialOfferList;
    private DialogLoginPopupBinding dialogLoginPopupBinding;
    private ArrayList<Product> outofstock_array, stockin_array;
    private TextView myCartCount, promotionViewAll, nodataFound;
    private List<OCRToDigitalMedicineResponse> dataList = new ArrayList<>();
    private ConstraintLayout constraintLayout;
    private MyCartController myCartController;
    private RecyclerView crossCellDataRecycle;
    private String oldMobileNum= "";
    private int otp= 0;
//    private ActivityMyProfileBinding activityMyProfileBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        context = this;
        HomeActivity.isPaymentSelectionActivity=false;

//        if(!HomeActivity.isLoggedin){
//
//            dialog = new Dialog(context);
//             dialogLoginPopupBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_login_popup, null, false);
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
//                                            Utils.showDialog(MyProfileActivity.this, "Sending OTP…");
//                                            otp = (int) ((Math.random() * 9000) + 1000);
//                                            if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
//                                                Send_Sms_Request sms_req = new Send_Sms_Request();
//                                                sms_req.setMobileNo(mobileNum);
//                                                sms_req.setMessage("Dear Apollo Customer, Your one time password is " + String.valueOf(otp) + " and is valid for 3mins.");
//                                                sms_req.setIsOtp(true);
//                                                sms_req.setOtp(String.valueOf(otp));
//                                                sms_req.setApiType("KIOSk");
//                                                myCartController.handleSendSmsApi(sms_req);
//                                            } else {
//                                                Utils.showSnackbar(getApplicationContext(), constraintLayout, "Internet Connection Not Available");
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        else {
//                            AccesskeyDialog accesskeyDialog = new AccesskeyDialog(MyProfileActivity.this);
//                            accesskeyDialog.onClickSubmit(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    accesskeyDialog.listener();
//                                    if (accesskeyDialog.validate()) {
//                                        Intent intent = new Intent(MyProfileActivity.this, MposStoreSetupActivity.class);
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initViewProfile();
        initLeftMenu();

        RecyclerView promotionsRecyclerView = findViewById(R.id.promotionsRecyclerView);
        promotionsRecyclerView.setLayoutManager(new GridLayoutManager(MyProfileActivity.this, 6));
        crossCellDataRecycle = findViewById(R.id.cross_cell_data_recycle);
        promotionViewAll = findViewById(R.id.promotion_viewAll);
        nodataFound = findViewById(R.id.nodata_found);
        product_list_array = new ArrayList<Product>();
        outofstock_array = new ArrayList<Product>();
        stockin_array = new ArrayList<Product>();
        myCartController = new MyCartController(this);

        myCartController.upcellCrosscellList("7353910637", MyProfileActivity.this);


        myCartCount = findViewById(R.id.lblCartCnt);
        TextView promotionViewAll = findViewById(R.id.promotion_viewAll);
        constraintLayout = findViewById(R.id.constraint_layout);

        if (null != SessionManager.INSTANCE.getDataList()) {
            if (SessionManager.INSTANCE.getDataList().size() > 0) {
                cartCount(SessionManager.INSTANCE.getDataList().size());
            }
        }

        ImageView faqLayout = findViewById(R.id.faq);
        TextView helpText = findViewById(R.id.help_text);
        helpText.setText(getResources().getString(R.string.faq));
        faqLayout.setOnClickListener(view -> startActivity(new Intent(MyProfileActivity.this, FAQActivity.class)));

        ImageView customerCareImg = findViewById(R.id.customer_care_icon);
        LinearLayout customerHelpLayout = findViewById(R.id.customer_help_layout);
        customerHelpLayout.setVisibility(View.GONE);
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

        ArrayList<Product> offerList = SessionManager.INSTANCE.getOfferList();
        if (offerList == null) {
            if (NetworkUtils.isNetworkConnected(context)) {
                myCartController.getProductList("SpecialOffers", this);
            } else {
                Utils.showSnackbar(context, constraintLayout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));

            }
        }
        if (offerList != null && offerList.size() > 0) {
            promotionAdaptor = new PromotionsAdapter(MyProfileActivity.this, offerList);
        } else {
            promotionAdaptor = new PromotionsAdapter(MyProfileActivity.this, product_list_array);
        }

        promotionAdaptor.setViewMode(ViewMode.GRID);
        promotionsRecyclerView.setAdapter(promotionAdaptor);
        if (product_list_array.size() == 0) {
            product_list_array.add(new Product("load"));
        }
        promotionAdaptor.notifyDataChanged();

        promotionViewAll.setOnClickListener(arg0 -> {
            Intent intent = new Intent(context, MyOffersActivity.class);
            intent.putExtra("categoryname", "Promotions");
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });

        SessionManager.INSTANCE.setCurrentPage(ApplicationConstant.ACTIVITY_ADDMORE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverAddmore, new IntentFilter("MedicineReciverAddMore"));

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("cardReceiver"));

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverNew, new IntentFilter("OrderhistoryCardReciver"));
        Constants.getInstance().setConnectivityListener(this);
    }

    /* view components*/
    private void initViewProfile() {
        name_txt = findViewById(R.id.name_txt);
        phone_txt = findViewById(R.id.phone_txt);
        emial_address_txt = findViewById(R.id.emial_address_txt);
        location_txt = findViewById(R.id.location_txt);
        address_txt = findViewById(R.id.address_txt);
        customer_id_txt = findViewById(R.id.customer_id_txt);
        handleCategoryListService();
    }

    private void handleCategoryListService() {
        Utils.showDialog(MyProfileActivity.this, getApplicationContext().getResources().getString(R.string.label_please_wait));
        ApiInterface apiInterface = ApiClient.getApiService(Constants.Get_Portfolio_of_the_User);
        Call<PortFolioModel> call = apiInterface.getPortFolio(SessionManager.INSTANCE.getMobilenumber(), "true", "Apollo pharmacy");
        call.enqueue(new CallbackWithRetry<PortFolioModel>(call) {
            @Override
            public void onResponse(@NonNull Call<PortFolioModel> call, @NonNull Response<PortFolioModel> response) {
                if (response.isSuccessful()) {
                    Utils.dismissDialog();
                    assert response.body() != null;
                    updateUI(response.body());
                } else {
                    Utils.dismissDialog();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PortFolioModel> call, @NonNull Throwable t) {
                Utils.dismissDialog();
                Utils.showCustomAlertDialog(MyProfileActivity.this, t.getMessage(), false, getApplicationContext().getResources().getString(R.string.label_ok), "");
            }
        });
    }


    private void updateUI(PortFolioModel portFolioModel) {
        CustomerData customerData = portFolioModel.getCustomerData();
        if (customerData != null) {
            customer_id_txt.setText(customerData.getLCIN());
            if (null != customerData.getName() && !customerData.getName().isEmpty()) {
                name_txt.setText(customerData.getName());
            } else {
                name_txt.setText("One Apollo User");
            }
            if (null != customerData.getMobileNumber() && !customerData.getMobileNumber().isEmpty()) {
                phone_txt.setText(customerData.getMobileNumber().trim());
            } else {
                phone_txt.setText("-");
            }
            emial_address_txt.setText(String.valueOf(customerData.getEarnedCredits()));
            location_txt.setText(String.valueOf(customerData.getAvailableCredits()));
            address_txt.setText(String.valueOf(customerData.getBurnedCredits()));
        }else{
            name_txt.setText("One Apollo User");
        }
    }

    private void initLeftMenu() {
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

        ImageView userLogout = findViewById(R.id.userLogout);
        userLogout.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(MyProfileActivity.this);
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
                Intent intent = new Intent(MyProfileActivity.this, HomeActivity.class);
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

        myProfileLayout.setBackgroundResource(R.color.selected_menu_color);
        dashboardMyProfileIcon.setImageResource(R.drawable.dashboard_profile_hover);
        dashboardMyProfile.setTextColor(getResources().getColor(R.color.selected_text_color));
        dashboardMyProfileText.setTextColor(getResources().getColor(R.color.selected_text_color));
        myProfileLayout.setClickable(false);

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

            Intent intent1 = new Intent(MyProfileActivity.this, MyCartActivity.class);
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
            finish();
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

            Intent intent = new Intent(this, MyOffersActivity.class);
            intent.putExtra("categoryname", "Promotions");
            startActivity(intent);
            finish();
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });
    }

    @Override
    public void onSuccessGetAddress(ArrayList<UserAddress> response) {

    }

    @Override
    public void onFailure(String error) {
        Utils.showCustomAlertDialog(this, getResources().getString(R.string.label_server_err_message), false, "OK", "");
    }

    @Override
    public void onSuccessProductList(HashMap<String, GetProductListResponse> productList) {
        if (productList != null && productList.size() > 0) {
            productListResponseHashMap = productList;
            specialOfferList = myCartController.getSpecialOffersCategoryList();
            if (null != specialOfferList && specialOfferList.size() > 0) {
                for (String s : specialOfferList) {
                    if (s.equalsIgnoreCase("Promotions")) {
                        totalPage = (productListResponseHashMap.get(s).getProductCount() / 20);

                        if (productListResponseHashMap.get(s).getProductCount() % 3 != 0) {
                            totalPage++;
                        }

                        ArrayList<Product> products = (ArrayList<Product>) productListResponseHashMap.get(s).getProducts();
                        for (Product d : products) {
                            Product load_obj = new Product();
                            load_obj.setName(d.getName());
                            load_obj.setSku(d.getSku());
                            load_obj.setPrice(d.getPrice());
                            load_obj.setImage(d.getImage());
                            load_obj.setSpecialPrice(d.getSpecialPrice());
                            load_obj.setProduct_pricetype("offer");
                            load_obj.setIsInStock(d.getIsInStock());
                            load_obj.setMou(d.getMou());
                            load_obj.setId(d.getId());

                            if (d.getIsInStock() == 1) {
                                stockin_array.add(load_obj);
                            } else {
                                outofstock_array.add(load_obj);
                            }
                        }

                        ArrayList<Product> sessionOfferList = new ArrayList<>();
                        if (stockin_array.size() >= 6) {
                            product_list_array.add(stockin_array.get(0));
                            product_list_array.add(stockin_array.get(1));
                            product_list_array.add(stockin_array.get(2));
                            product_list_array.add(stockin_array.get(3));
                            product_list_array.add(stockin_array.get(4));
                            product_list_array.add(stockin_array.get(5));

                            sessionOfferList.clear();
                            sessionOfferList.add(stockin_array.get(0));
                            sessionOfferList.add(stockin_array.get(1));
                            sessionOfferList.add(stockin_array.get(2));
                            sessionOfferList.add(stockin_array.get(3));
                            sessionOfferList.add(stockin_array.get(4));
                            sessionOfferList.add(stockin_array.get(5));
                            SessionManager.INSTANCE.setOfferList(sessionOfferList);
                        } else {
                            product_list_array.addAll(stockin_array);
                        }
                        if (totalPage > currentPage) {
                            product_list_array.remove(0);
                        }
                        promotionAdaptor.notifyDataSetChanged();
                    }
                }
            }
        } else {
            Utils.showCustomAlertDialog(this, getApplicationContext().getResources().getString(R.string.try_again_later), false, getApplicationContext().getResources().getString(R.string.label_ok), "");
        }
    }

    @Override
    public void onDeleteItemClicked(OCRToDigitalMedicineResponse ocrToDigitalMedicineResponse, int position) {

    }

    @Override
    public void onDeletedAddAllClicked() {

    }

    @Override
    public void onSuccessImageService(GetImageRes response) {

    }

    @Override
    public void onSuccessSearchItemApi(UpCellCrossCellResponse body) {
        if (body != null && body.getCrossselling() != null && body.getCrossselling().size() > 0) {
            MyOfersAdapterNew crossCellAdapter = new MyOfersAdapterNew(MyProfileActivity.this, MyProfileActivity.this, body.getCrossselling(), this);
            RecyclerView.LayoutManager mLayoutManager4 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            crossCellDataRecycle.setLayoutManager(mLayoutManager4);
            crossCellDataRecycle.setItemAnimator(new DefaultItemAnimator());
            crossCellDataRecycle.setAdapter(crossCellAdapter);
            nodataFound.setVisibility(View.GONE);
            promotionViewAll.setVisibility(View.VISIBLE);
        } else {
            nodataFound.setVisibility(View.VISIBLE);
            promotionViewAll.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSearchFailure(String message) {

    }

    @Override
    public void onSuccessBarcodeItemApi(ItemSearchResponse itemSearchResponse, int serviceType, int qty, int position) {

    }

    @Override
    public void onFailureBarcodeItemApi(String message) {

    }

    @Override
    public void upSellCrosssellApiCall(List<UpCellCrossCellResponse.Crossselling> crossselling, List<UpCellCrossCellResponse.Upselling> upselling) {

    }

    @Override
    public void setSuccessBatchList(BatchListResponse body, int position, ItemSearchResponse.Item itemSearchData) {

    }

    @Override
    public void onSuccessCalculatePosTransactionApi(CalculatePosTransactionResponse calculatePosTransactionResponse) {

    }

    @Override
    public void onSuccessSearchItemApi(ItemSearchResponse itemSearchResponse, int position) {

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
    public void onResume() {
        super.onResume();
        MyProfileActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
    public void onBackPressed() {
        dialog.dismiss();
        super.onBackPressed();
        finish();

    }

    @Override
    public void showSnackBAr() {

    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiverAddmore);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiverNew);
        super.onPause();
        Constants.getInstance().setConnectivityListener(null);
    }

    private BroadcastReceiver mMessageReceiverNew = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (message.equalsIgnoreCase("OrderNow")) {
                if (null != SessionManager.INSTANCE.getDataList()) {
                    if (SessionManager.INSTANCE.getDataList().size() > 0) {
                        cartCount(SessionManager.INSTANCE.getDataList().size());
                        dataList = SessionManager.INSTANCE.getDataList();
                    }
                }
                Utils.showSnackbar(MyProfileActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_item_added_cart));
                cartCount(dataList.size());
            }
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (message.equalsIgnoreCase("Addtocart")) {
                if (null != SessionManager.INSTANCE.getDataList()) {
                    if (SessionManager.INSTANCE.getDataList().size() > 0) {
                        cartCount(SessionManager.INSTANCE.getDataList().size());
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
                Utils.showSnackbar(MyProfileActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_item_added_cart));
                cartCount(dataList.size());
            }
//            if (null != SessionManager.INSTANCE.getDataList())
//                checkOutNewBtn.setVisibility(View.VISIBLE);
//            else
//                checkOutNewBtn.setVisibility(View.GONE);
        }
    };

    private final BroadcastReceiver mMessageReceiverAddmore = new BroadcastReceiver() {
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
        final Dialog dialog = new Dialog(MyProfileActivity.this);
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
        okButton.setOnClickListener(v -> {
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
                }
            }
            SessionManager.INSTANCE.setDataList(dataList);
            cartCount(SessionManager.INSTANCE.getDataList().size());
            Utils.showSnackbar(MyProfileActivity.this, constraintLayout, getResources().getString(R.string.label_medicine_added_success));
            dialog.dismiss();
        });
        declineButton.setOnClickListener(v -> dialog.dismiss());
    }

    public List<OCRToDigitalMedicineResponse> removeDuplicate(List<OCRToDigitalMedicineResponse> dataList) {
        Set<OCRToDigitalMedicineResponse> s = new TreeSet<OCRToDigitalMedicineResponse>((o1, o2) -> {
            if (o1.getArtCode().equalsIgnoreCase(o2.getArtCode())) {
                return 0;
            }
            return 1;
        });
        s.addAll(dataList);
        return new ArrayList<>(s);
    }

    @Override
    public void onSendSmsSuccess() {
        Utils.dismissDialog();
        dialogLoginPopupBinding.mobileNumLoginPopup.setVisibility(View.GONE);
        dialogLoginPopupBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//        entered_mobile_number.setText(mobileNum)
        SessionManager.INSTANCE.setMobilenumber(mobileNum);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }

    @Override
    public void onSendSmsFailure() {
        Utils.dismissDialog();
        Utils.showCustomAlertDialog(this, "We are unable to process your request right now, Please try again later.", false, "OK", "");
    }

}
