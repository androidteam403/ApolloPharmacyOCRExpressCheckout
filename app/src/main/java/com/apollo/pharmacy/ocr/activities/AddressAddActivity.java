package com.apollo.pharmacy.ocr.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.controller.AddressAddController;
import com.apollo.pharmacy.ocr.interfaces.AddressAddListener;
import com.apollo.pharmacy.ocr.model.ModelMobileNumVerify;
import com.apollo.pharmacy.ocr.model.PlaceOrderReqModel;
import com.apollo.pharmacy.ocr.model.PlaceOrderResModel;
import com.apollo.pharmacy.ocr.model.ServicabilityResponse;
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

public class AddressAddActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener,
        AddressAddListener {

    private EditText nameEditText, addressEditText, cityEditText, pinCodeEditText, stateEditText;
    private boolean isServiceAvailable = false;
    private ConstraintLayout constraintLayout;
    private UserAddress userAddress = null;
    private int addressCount = 0;
    private AddressAddController addressAddController;
    private LinearLayout okLayout;

    public AddressAddActivity() {
        super();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        setContentView(R.layout.activity_address_add);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (getIntent().getExtras() != null) {
            addressCount = getIntent().getExtras().getInt("addressList");
        }

        nameEditText = findViewById(R.id.name_editText);
        addressEditText = findViewById(R.id.address_editText);
        pinCodeEditText = findViewById(R.id.pinCode_editText);
        cityEditText = findViewById(R.id.city_editText);
        stateEditText = findViewById(R.id.state_editText);
        okLayout = findViewById(R.id.ok_layout);
        LinearLayout cancelLayout = findViewById(R.id.cancel_layout);
        constraintLayout = findViewById(R.id.constraint_layout);
        addressAddController = new AddressAddController(this);
        FrameLayout parentView = findViewById(R.id.parent_view);

        addressEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        addressEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        pinCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 6) {
                    handleServiceAvailability();
                }
            }
        });

        okLayout.setOnClickListener(v -> {
            if (validate()) {
                userAddress = new UserAddress();
                userAddress.setMappingMobile(SessionManager.INSTANCE.getMobilenumber());
                userAddress.setName(nameEditText.getText().toString());
                userAddress.setMobile(SessionManager.INSTANCE.getMobilenumber());
                userAddress.setAddress1(addressEditText.getText().toString());
                userAddress.setAddress2("");
                userAddress.setAddress3("");
                userAddress.setCity(cityEditText.getText().toString());
                userAddress.setState(stateEditText.getText().toString());
                userAddress.setIsDefault(1);
                userAddress.setAddressType("");
                userAddress.setIsDeleted(0);
                userAddress.setPincode(pinCodeEditText.getText().toString());
                userAddress.setItemSelected(true);
                if (NetworkUtils.isNetworkConnected(AddressAddActivity.this)) {
                    Utils.showDialog(AddressAddActivity.this, getApplicationContext().getResources().getString(R.string.label_please_wait));
                    addressAddController.handleSaveDeliveryAddress(userAddress);
                } else {
                    Utils.showSnackbar(AddressAddActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
                }
            }
        });

        cancelLayout.setOnClickListener(v -> {
            finish();
        });

        parentView.setOnClickListener(v -> {
            Utils.hideKeyboard(AddressAddActivity.this);
        });
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
    }

    private boolean validate() {
        String name = nameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String zipCode = pinCodeEditText.getText().toString().trim();
        if (name.isEmpty()) {
            nameEditText.setError("Name should not be empty");
            nameEditText.requestFocus();
            return false;
        } else if (address.isEmpty()) {
            addressEditText.setError("Address should not be empty");
            addressEditText.requestFocus();
            return false;
        } else if (zipCode.isEmpty()) {
            pinCodeEditText.setError("Pincode should not be empty");
            pinCodeEditText.requestFocus();
            return false;
        } else if (zipCode.length() < 6) {
            pinCodeEditText.setError("Please enter valid pincode");
            pinCodeEditText.requestFocus();
            return false;
        } else if (zipCode.length() == 6) {
            if (!isServiceAvailable)
                return false;
        }
        return true;
    }

    private void handleServiceAvailability() {
        if (NetworkUtils.isNetworkConnected(AddressAddActivity.this)) {
            Utils.showDialog(AddressAddActivity.this, getApplicationContext().getResources().getString(R.string.label_please_wait));
            addressAddController.checkServiceAvailability(this, pinCodeEditText.getText().toString());
        } else {
            Utils.showSnackbar(AddressAddActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
        }
    }

    ArrayList<PlaceOrderReqModel.PrescUrlEntity> prescEntityArray = new ArrayList<>();

    private void handleUploadImageService() {
        try {
            for (int i = 0; i < Singleton.getInstance().imageArrayList.size(); i++) {
                final InputStream imageStream = AddressAddActivity.this.getContentResolver().openInputStream(Uri.fromFile(new File(Singleton.getInstance().imageArrayList.get(i).getPath())));
                final int imageLength = imageStream.available();
                final Handler handler = new Handler();
                int finalI = i;
                Thread th = new Thread(() -> {
                    try {
                        final String imageName = ImageManager.UploadImage(imageStream, imageLength, Utils.getTransactionGeneratedId() + "_" + finalI, "", "");
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
            Toast.makeText(this, getApplicationContext().getResources().getString(R.string.label_something_went_wrong), Toast.LENGTH_SHORT).show();
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

        String stateName = Utils.removeAllDigit(userAddress.getState().trim());
        for (StateCodes codes : Utils.getStoreListFromAssets(AddressAddActivity.this)) {
            String codestate_name = codes.getName().trim();
            if (codestate_name.equalsIgnoreCase(stateName)) {
                selectedStateCode = codes.getCode();
            }
        }

        PlaceOrderReqModel placeOrderReqModel = new PlaceOrderReqModel();
        PlaceOrderReqModel.TpdetailsEntity tpDetailsEntity = new PlaceOrderReqModel.TpdetailsEntity();
        tpDetailsEntity.setOrderId(Utils.getTransactionGeneratedId());
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
        customerDetailsEntity.setCity(stateName);
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

        addressAddController.handleOrderPlaceService(AddressAddActivity.this, placeOrderReqModel);
    }

    @Override
    public void onSuccessPlaceOrder(PlaceOrderResModel resModel) {
        Dialog mDialog = Utils.getOrderSuccessDialog(AddressAddActivity.this, R.layout.dialog_payment_success);
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

            Intent intent = new Intent(AddressAddActivity.this, MyOrdersActivity.class);
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
                Utils.showCustomAlertDialog(AddressAddActivity.this, t.getMessage(), false, getResources().getString(R.string.label_ok), "");
            }

            public void onResponse(@NotNull Call callx, @NotNull Response response) {
                Intrinsics.checkParameterIsNotNull(callx, "call");
                Intrinsics.checkParameterIsNotNull(response, "response");
            }
        }));
    }

    @Override
    public void onSuccessSaveDeliveryAddress() {
        Utils.dismissDialog();
        if (addressCount == 0) {
            SessionManager.INSTANCE.setUseraddress(userAddress);
            if (SessionManager.INSTANCE.getUploadPrescriptionDeliveryMode()) {
                Utils.showDialog(AddressAddActivity.this, getApplicationContext().getResources().getString(R.string.label_please_wait));
                handleUploadImageService();
            } else {
                Intent intent = new Intent(AddressAddActivity.this, OrderSummaryActivity.class);
                intent.putExtra("activityname", "OrderSummaryActivity");
                startActivity(intent);
                finish();
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            }
        } else {
            Intent intent = new Intent(AddressAddActivity.this, AddressListActivity.class);
            intent.putExtra("activityname", "OrderSummaryActivity");
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        }
    }

    @Override
    public void onSuccessServiceability(ServicabilityResponse response) {
        Utils.dismissDialog();
        hideInputKeyboard();
        isServiceAvailable = true;
        cityEditText.setText(response.getDeviceDetails().get(0).getCITY());
        stateEditText.setText(response.getDeviceDetails().get(0).getSTATE());
        okLayout.setEnabled(true);
        okLayout.setAlpha(1.0f);
        Utils.showCustomSnackbar(constraintLayout, getResources().getString(R.string.label_service_available), getApplicationContext().getResources().getColor(R.color.service_found));
    }

    @Override
    public void onFailureServiceability(String message) {
        Utils.dismissDialog();
        hideInputKeyboard();
        isServiceAvailable = false;
        cityEditText.setText("");
        stateEditText.setText("");
        okLayout.setEnabled(false);
        okLayout.setAlpha(0.5f);
        Utils.showCustomSnackbar(constraintLayout, getResources().getString(R.string.label_service_not_available), getApplicationContext().getResources().getColor(R.color.no_service_found));
    }

    @Override
    public void onFailureService(String message) {
        Utils.dismissDialog();
        Utils.showSnackbar(AddressAddActivity.this, constraintLayout, message);
    }

    private void hideInputKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
