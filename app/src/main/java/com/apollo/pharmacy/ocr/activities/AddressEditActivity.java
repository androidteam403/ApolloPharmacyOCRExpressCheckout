package com.apollo.pharmacy.ocr.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.controller.AddressEditController;
import com.apollo.pharmacy.ocr.interfaces.AddressEditListener;
import com.apollo.pharmacy.ocr.model.Meta;
import com.apollo.pharmacy.ocr.model.ServicabilityResponse;
import com.apollo.pharmacy.ocr.model.UserAddress;
import com.apollo.pharmacy.ocr.receiver.ConnectivityReceiver;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.utility.NetworkUtils;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;

public class AddressEditActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener, AddressEditListener {

    private EditText nameEditText, addressEditText, cityEditText, pinCodeEditText, stateEditText;
    private ConstraintLayout constraintLayout;
    private UserAddress userAddress = null;
    private boolean isServiceAvailable = false;
    private int addressId = 0;
    private AddressEditController addressEditController;
    LinearLayout updateLayout;

    public AddressEditActivity() {
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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        nameEditText = findViewById(R.id.name_editText);
        addressEditText = findViewById(R.id.address_editText);
        pinCodeEditText = findViewById(R.id.pinCode_editText);
        cityEditText = findViewById(R.id.city_editText);
        stateEditText = findViewById(R.id.state_editText);
         updateLayout = findViewById(R.id.update_layout);
        LinearLayout cancelLayout = findViewById(R.id.cancel_layout);
        constraintLayout = findViewById(R.id.constraint_layout);
        addressEditController = new AddressEditController(this);
        FrameLayout parentView = findViewById(R.id.parent_view);

        userAddress = new UserAddress();
        userAddress = SessionManager.INSTANCE.getUseraddress();

        addressId = userAddress.getId();
        nameEditText.setText(userAddress.getName());
        nameEditText.setSelection(nameEditText.length());
        addressEditText.setText(userAddress.getAddress1());
        pinCodeEditText.setText(userAddress.getPincode());
        cityEditText.setText(userAddress.getCity());
        stateEditText.setText(userAddress.getState());

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

        updateLayout.setOnClickListener(view -> {
            Log.d("Update Action-->",String.valueOf(validate()));
            if (validate()) {
                userAddress = new UserAddress();
                userAddress.setId(addressId);
                userAddress.setName(nameEditText.getText().toString());
                userAddress.setAddress1(addressEditText.getText().toString());
                userAddress.setAddress2("");
                userAddress.setAddress3("");
                userAddress.setPincode(pinCodeEditText.getText().toString());
                userAddress.setCity(cityEditText.getText().toString());
                userAddress.setState(stateEditText.getText().toString());
                userAddress.setMobile(SessionManager.INSTANCE.getMobilenumber());
                userAddress.setAddressType("");
                userAddress.setMappingMobile(SessionManager.INSTANCE.getMobilenumber());
                if (NetworkUtils.isNetworkConnected(AddressEditActivity.this)) {
                    Utils.showDialog(AddressEditActivity.this, getApplicationContext().getResources().getString(R.string.label_please_wait));
                    addressEditController.handleEditAddress(userAddress);
                } else {
                    Utils.showSnackbar(AddressEditActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
                }
            }
        });

        cancelLayout.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });

        parentView.setOnClickListener(v -> {
            Utils.hideKeyboard(AddressEditActivity.this);
        });
    }

    private boolean validate() {
        String name = nameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String zipCode = pinCodeEditText.getText().toString().trim();
        if (name.isEmpty()) {
            nameEditText.setError("Name should not be empty");
            nameEditText.requestFocus();
            return false;
        }
        else if(!name.isEmpty())
        {
            return true;
        }
        else if (address.isEmpty()) {
            addressEditText.setError("Address should not be empty");
            addressEditText.requestFocus();
            return false;
        }
        else if(!address.isEmpty())
        {
            return true;
        }

        else if (zipCode.isEmpty()) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
    }

    @Override
    public void onSuccessEditAddress(Meta response) {
        Utils.dismissDialog();
        if (response.getStatusCode() == 200) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", "addressUpdated");
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        } else {
            Toast.makeText(AddressEditActivity.this, response.getStatusMsg(), Toast.LENGTH_LONG).show();
        }
    }

    private void handleServiceAvailability() {
        if (NetworkUtils.isNetworkConnected(AddressEditActivity.this)) {
            Utils.showDialog(AddressEditActivity.this, getApplicationContext().getResources().getString(R.string.label_please_wait));
            addressEditController.checkServiceAvailability(this, pinCodeEditText.getText().toString());
        } else {
            Utils.showSnackbar(AddressEditActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
        }
    }

    @Override
    public void onSuccessServiceability(ServicabilityResponse response) {
        Utils.dismissDialog();
        hideInputKeyboard();
        cityEditText.setText(response.getDeviceDetails().get(0).getCITY());
        stateEditText.setText(response.getDeviceDetails().get(0).getSTATE());
        isServiceAvailable = true;
        updateLayout.setEnabled(true);
        updateLayout.setAlpha(0.5f);

        Utils.showCustomSnackbar(constraintLayout, getResources().getString(R.string.label_service_available), getApplicationContext().getResources().getColor(R.color.service_found));
    }

    @Override
    public void onFailureServiceability(String message) {
        Utils.dismissDialog();
        hideInputKeyboard();
        cityEditText.setText("");
        stateEditText.setText("");
        isServiceAvailable = false;
        updateLayout.setEnabled(false);
        updateLayout.setAlpha(1.0f);
        Utils.showCustomSnackbar(constraintLayout, getResources().getString(R.string.label_service_not_available), getApplicationContext().getResources().getColor(R.color.no_service_found));
    }

    @Override
    public void onFailureService(String message) {
        Utils.dismissDialog();
        Utils.showSnackbar(AddressEditActivity.this, constraintLayout, message);
    }

    private void hideInputKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}