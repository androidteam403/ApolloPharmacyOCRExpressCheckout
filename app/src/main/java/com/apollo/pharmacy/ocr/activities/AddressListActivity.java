package com.apollo.pharmacy.ocr.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.adapters.AddressListAdapter;
import com.apollo.pharmacy.ocr.controller.AddressListController;
import com.apollo.pharmacy.ocr.interfaces.AddressListListener;
import com.apollo.pharmacy.ocr.interfaces.OnItemClickListener;
import com.apollo.pharmacy.ocr.model.Meta;
import com.apollo.pharmacy.ocr.model.ModelMobileNumVerify;
import com.apollo.pharmacy.ocr.model.OCRToDigitalMedicineResponse;
import com.apollo.pharmacy.ocr.model.PlaceOrderReqModel;
import com.apollo.pharmacy.ocr.model.PlaceOrderResModel;
import com.apollo.pharmacy.ocr.model.StateCodes;
import com.apollo.pharmacy.ocr.model.UserAddress;
import com.apollo.pharmacy.ocr.network.ApiClient;
import com.apollo.pharmacy.ocr.network.ApiInterface;
import com.apollo.pharmacy.ocr.network.CallbackWithRetry;
import com.apollo.pharmacy.ocr.receiver.ConnectivityReceiver;
import com.apollo.pharmacy.ocr.utility.ApplicationConstant;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.utility.ImageManager;
import com.apollo.pharmacy.ocr.utility.NetworkUtils;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Singleton;
import com.apollo.pharmacy.ocr.utility.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import kotlin.jvm.internal.Intrinsics;
import retrofit2.Call;
import retrofit2.Response;

import static com.apollo.pharmacy.ocr.utility.Utils.getTransactionGeneratedId;

public class AddressListActivity extends BaseActivity implements AddressListListener, OnItemClickListener,
        ConnectivityReceiver.ConnectivityReceiverListener {

    private AddressListController addressListController;
    private RecyclerView addressListRecyclerView;
    private AddressListAdapter addressListAdapter;
    private ArrayList<UserAddress> addressList;
    private ConstraintLayout constraint_layout;
    private final int EDIT_USER_ADDRESS = 111;
    private final ArrayList<PlaceOrderReqModel.PrescUrlEntity> prescEntityArray = new ArrayList<>();

    public AddressListActivity() {
        super();
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
        AddressListActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        addressListController = new AddressListController(this);
        Constants.getInstance().setConnectivityListener(this);

        LinearLayout newAddressLayout = findViewById(R.id.new_address_layout);
        newAddressLayout.setOnClickListener(view -> {
            Intent intent = new Intent(AddressListActivity.this, AddressAddActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });

        findViewById(R.id.back_icon).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });

        TextView expectedDateTime = findViewById(R.id.expected_date_time);
        constraint_layout = findViewById(R.id.constraint_layout);
        LinearLayout checkoutImage = findViewById(R.id.checkout_image);

        checkoutImage.setOnClickListener(v -> {
            if (SessionManager.INSTANCE.getUploadPrescriptionDeliveryMode()) {
                if (NetworkUtils.isNetworkConnected(this)) {
                    Utils.showDialog(this, getApplicationContext().getResources().getString(R.string.label_please_wait));
                    handleUploadImageService();
                } else {
                    Utils.showSnackbar(this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
                }
            } else {
                Intent intent2 = new Intent(this, OrderSummaryActivity.class);
                intent2.putExtra("activityname", "AddressActivity");
                startActivity(intent2);
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            }
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
                        expectedDateTime.setText(SessionManager.INSTANCE.getKioskSetupResponse().getAT_HOME_DELIVERABLE_TIME());
                        SessionManager.INSTANCE.setDeliverydate(SessionManager.INSTANCE.getKioskSetupResponse().getAT_HOME_DELIVERABLE_TIME());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        ImageView customerCareImg = findViewById(R.id.customer_care_icon);
        LinearLayout customerHelpLayout = findViewById(R.id.customer_help_layout);
        customerHelpLayout.setVisibility(View.VISIBLE);
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

        newAddressLayout.setOnClickListener(arg0 -> {
            Intent intent = new Intent(AddressListActivity.this, AddressAddActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });
        addressList = new ArrayList<>();
        if (getIntent().getExtras() != null) {
            addressList = (ArrayList<UserAddress>) getIntent().getExtras().getSerializable("addressList");
        } else {
            if (NetworkUtils.isNetworkConnected(this)) {
                Utils.showDialog(this, getApplicationContext().getResources().getString(R.string.label_please_wait));
                addressListController.handleGetAddressListService(AddressListActivity.this);
            } else {
                Utils.showSnackbar(this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
            }
        }
        addressListRecyclerView = findViewById(R.id.addressDetails_recyclerview);
        addressListRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        if (addressList.size() > 0) {
            addressList.get(0).setItemSelected(true);
        }
        addressListAdapter = new AddressListAdapter(AddressListActivity.this, addressList, this);
        addressListRecyclerView.setAdapter(addressListAdapter);
        addressListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddressListActivity.this, DeliverySelectionActivity.class));
        finish();
        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
    }

    @Override
    public void onFailure(String error) {
        Utils.dismissDialog();
        Utils.showCustomAlertDialog(AddressListActivity.this, error, false, "OK", "");
    }

    @Override
    public void onSuccessGetAddressList(ArrayList<UserAddress> response) {
        Utils.dismissDialog();
        addressList.clear();
        addressList = response;
        if (addressList.size() == 0 || addressList == null) {
            Intent intent = new Intent(this, AddressAddActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        } else {
            if (addressList.size() > 0) {
                addressList.get(0).setItemSelected(true);
            }
            addressListAdapter = new AddressListAdapter(AddressListActivity.this, addressList, this);
            addressListRecyclerView.setAdapter(addressListAdapter);
            addressListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSuccessDeleteAddress(Meta response) {
        Utils.dismissDialog();
        if (response.getStatusCode() == 200) {
            Utils.showDialog(AddressListActivity.this, getApplicationContext().getResources().getString(R.string.label_please_wait));
            addressListController.handleGetAddressListService(AddressListActivity.this);
            Utils.showSnackbar(AddressListActivity.this, constraint_layout, response.getStatusMsg());
        } else {
            Utils.showSnackbar(AddressListActivity.this, constraint_layout, response.getStatusMsg());
        }
    }

    private void handleUploadImageService() {
        try {
            for (int i = 0; i < Singleton.getInstance().imageArrayList.size(); i++) {
                final InputStream imageStream = AddressListActivity.this.getContentResolver().openInputStream(Uri.fromFile(new File(Singleton.getInstance().imageArrayList.get(i).getPath())));
                final int imageLength = imageStream.available();
                final Handler handler = new Handler();
                int finalI = i;
                Thread th = new Thread(() -> {
                    try {
                        final String imageName = ImageManager.UploadImage(imageStream, imageLength, getTransactionGeneratedId() + "_" + finalI, "", "");
                        handler.post(() -> {
                            PlaceOrderReqModel.PrescUrlEntity prescEntity = new PlaceOrderReqModel.PrescUrlEntity();
                            prescEntity.setUrl(imageName);
                            prescEntityArray.add(prescEntity);
                            if (Singleton.getInstance().imageArrayList.size() == prescEntityArray.size()) {
                                doPlaceOrder();
                            }
                        });
                    } catch (Exception ex) {
                        final String exceptionMessage = ex.getMessage();
                        handler.post(() -> showMessage(exceptionMessage));
                    }
                });
                th.start();
            }
        } catch (Exception ex) {
            showMessage(ex.getMessage());
        }
    }

    private void showMessage(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void doPlaceOrder() {
        String userSelectedAdd = "";
        String selectedStateCode = "";
        UserAddress userAddress = SessionManager.INSTANCE.getUseraddress();
        if (null != userAddress.getAddress1() && userAddress.getAddress1().length() > 0) {
            String address = userAddress.getAddress1().toUpperCase() + ", " + userAddress.getCity().toUpperCase() + ", " + userAddress.getState().toUpperCase();
            address = address.replace("null", "");
            userSelectedAdd = address;
        } else {
            if (null != userAddress.getCity() && null != userAddress.getState()) {
                String address = userAddress.getCity().toUpperCase() + ", " + userAddress.getState().toUpperCase();
                address = address.replace("null", "");
                userSelectedAdd = address;
            }
        }

        String statename = userAddress.getState().trim();
        String statename1 = Utils.removeAllDigit(statename);
        for (StateCodes codes : Utils.getStoreListFromAssets(AddressListActivity.this)) {
            String codestate_name = codes.getName().trim();
            if (codestate_name.equalsIgnoreCase(statename1)) {
                selectedStateCode = codes.getCode();
            }
        }

        PlaceOrderReqModel placeOrderReqModel = new PlaceOrderReqModel();
        PlaceOrderReqModel.TpdetailsEntity tpDetailsEntity = new PlaceOrderReqModel.TpdetailsEntity();
        tpDetailsEntity.setOrderId(getTransactionGeneratedId());
        tpDetailsEntity.setShopId(SessionManager.INSTANCE.getStoreId());
        tpDetailsEntity.setShippingMethod("HOME DELIVERY");
        tpDetailsEntity.setPaymentMethod("COD");
        tpDetailsEntity.setVendorName(SessionManager.INSTANCE.getKioskSetupResponse().getKIOSK_ID());
        PlaceOrderReqModel.CustomerDetailsEntity customerDetailsEntity = new PlaceOrderReqModel.CustomerDetailsEntity();
        customerDetailsEntity.setMobileNo(SessionManager.INSTANCE.getMobilenumber());
        customerDetailsEntity.setComm_addr(userSelectedAdd);
        customerDetailsEntity.setDel_addr(userSelectedAdd);
        customerDetailsEntity.setFirstName(userAddress.getName());
        customerDetailsEntity.setLastName(userAddress.getName());
        customerDetailsEntity.setUHID("");
        customerDetailsEntity.setCity(statename1);
        customerDetailsEntity.setPostCode(userAddress.getPincode());
        customerDetailsEntity.setMailId("");
        customerDetailsEntity.setAge(25);
        customerDetailsEntity.setCardNo("");
        customerDetailsEntity.setPatientName(userAddress.getName());
        tpDetailsEntity.setCustomerDetails(customerDetailsEntity);

        PlaceOrderReqModel.PaymentDetailsEntity paymentDetailsEntity = new PlaceOrderReqModel.PaymentDetailsEntity();
        paymentDetailsEntity.setTotalAmount("0");
        paymentDetailsEntity.setPaymentSource("COD");
        paymentDetailsEntity.setPaymentStatus("");
        paymentDetailsEntity.setPaymentOrderId("");
        tpDetailsEntity.setPaymentDetails(paymentDetailsEntity);

        ArrayList<PlaceOrderReqModel.ItemDetailsEntity> itemDetailsArr = new ArrayList<>();
        tpDetailsEntity.setItemDetails(itemDetailsArr);
        tpDetailsEntity.setDotorName("Apollo");
        tpDetailsEntity.setStateCode(selectedStateCode);
        tpDetailsEntity.setTAT("");
        tpDetailsEntity.setUserId(SessionManager.INSTANCE.getKioskSetupResponse().getKIOSK_ID());
        tpDetailsEntity.setOrderType("Pharma");
        tpDetailsEntity.setCouponCode("MED10");
        tpDetailsEntity.setOrderDate(Utils.getOrderedID());
        tpDetailsEntity.setRequestType("NONCART");
        tpDetailsEntity.setPrescUrl(prescEntityArray);
        placeOrderReqModel.setTpdetails(tpDetailsEntity);

        addressListController.handlePlaceOrderService(this, placeOrderReqModel);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void onSuccessPlaceOrder(PlaceOrderResModel resModel) {
        Dialog mDialog = Utils.getOrderSuccessDialog(AddressListActivity.this, R.layout.dialog_payment_success);
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

        deliveryModeTypeImage.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.payment_cash_on_delivery));
        deliveryModeTypeText.setText(getApplicationContext().getResources().getString(R.string.COD));
        orderIdText.setText(resModel.getOrdersResult().getOrderNo());
        itemsCountText.setVisibility(View.GONE);

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

        if (SessionManager.INSTANCE.getDeliverydate() != null) {
            estimatedDateText.setText(SessionManager.INSTANCE.getDeliverydate());
        }

        if (SessionManager.INSTANCE.getUseraddress() != null) {
            UserAddress useraddress = SessionManager.INSTANCE.getUseraddress();
            String address_str = useraddress.getAddress1() + ", " + useraddress.getCity() + ", " + useraddress.getState();
            deliveryAddressText.setText(address_str);
        }

        downloadAskApolloLayout.setOnClickListener(v -> sendSMS());
        downloadAskApolloImage.setOnClickListener((View v) -> sendSMS());

        trackOrderLayout.setOnClickListener(v -> {
            SessionManager.INSTANCE.clearMedicineData();
            SessionManager.INSTANCE.clearDeliveryType();
            SessionManager.INSTANCE.setOrderCompletionStatus(true);
            SessionManager.INSTANCE.setCurrentPage(ApplicationConstant.ORDER_SUCCESS);
            SessionManager.INSTANCE.setScannedImagePath("");
            SessionManager.INSTANCE.setUploadPrescriptionDeliveryMode(false);
            SessionManager.INSTANCE.setDynamicOrderId("");

            Intent intent = new Intent(AddressListActivity.this, MyOrdersActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            finishAffinity();
        });
        mDialog.show();
    }

    public void sendSMS() {
        String askApolloTinyURL = "Please Download the Ask Apollo from here https://tinyurl.com/y9p3f49q";
        ApiInterface apiInterface = ApiClient.getApiService(Constants.Send_Otp);
        Call<ModelMobileNumVerify> call = apiInterface.verifyMobileNumber(ApplicationConstant.SMS_METHOD, ApplicationConstant.SMS_API, SessionManager.INSTANCE.getMobilenumber(), "APOLLO", askApolloTinyURL);
        call.enqueue((new CallbackWithRetry<ModelMobileNumVerify>(call) {
            public void onFailure(@NotNull Call callx, @NotNull Throwable t) {
                Intrinsics.checkParameterIsNotNull(callx, "call");
                Intrinsics.checkParameterIsNotNull(t, "t");
                Utils.showCustomAlertDialog(AddressListActivity.this, t.getMessage(), false, getResources().getString(R.string.label_ok), "");
            }

            public void onResponse(@NotNull Call callx, @NotNull Response response) {
                Intrinsics.checkParameterIsNotNull(callx, "call");
                Intrinsics.checkParameterIsNotNull(response, "response");
            }
        }));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                if (requestCode == EDIT_USER_ADDRESS) {
                    String result = data.getStringExtra("result");
                    if (result.equalsIgnoreCase("addressUpdated")) {
                        if (NetworkUtils.isNetworkConnected(this)) {
                            Utils.showDialog(AddressListActivity.this, getApplicationContext().getResources().getString(R.string.label_please_wait));
                            addressListController.handleGetAddressListService(AddressListActivity.this);
                        } else {
                            Utils.showSnackbar(this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClickIncrement(int position) {

    }

    @Override
    public void onClickDecrement(int position) {

    }

    @Override
    public void onClickDelete(int position, OCRToDigitalMedicineResponse item) {
        final Dialog dialog = new Dialog(AddressListActivity.this);
        dialog.setContentView(R.layout.dialog_custom_alert);
        dialog.setCancelable(false);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView dialogTitleText = dialog.findViewById(R.id.dialog_info);
        Button okButton = dialog.findViewById(R.id.dialog_ok);
        Button declineButton = dialog.findViewById(R.id.dialog_cancel);
        dialogTitleText.setText(getApplicationContext().getResources().getString(R.string.label_delete_address_alert));
        okButton.setText(getApplicationContext().getResources().getString(R.string.label_yes));
        declineButton.setText(getApplicationContext().getResources().getString(R.string.label_no));
        okButton.setOnClickListener(v -> {
            UserAddress address = SessionManager.INSTANCE.getUseraddress();
            int id = address.getId();
            dialog.dismiss();
            if (NetworkUtils.isNetworkConnected(this)) {
                Utils.showDialog(AddressListActivity.this, getApplicationContext().getResources().getString(R.string.label_please_wait));
                addressListController.handleDeleteAddressService(id, AddressListActivity.this);
            } else {
                Utils.showSnackbar(this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
            }
        });
        declineButton.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public void onClickEdit(int position) {
        SessionManager.INSTANCE.setUseraddress(addressList.get(position));
        Intent intent = new Intent(AddressListActivity.this, AddressEditActivity.class);
        startActivityForResult(intent, EDIT_USER_ADDRESS);
        overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
    }

    @Override
    public void onItemClick(int position) {
        for (UserAddress address : addressList) {
            address.setItemSelected(false);
        }
        addressList.get(position).setItemSelected(true);
        SessionManager.INSTANCE.setUseraddress(addressList.get(position));
        addressListAdapter.notifyDataSetChanged();
    }
}
