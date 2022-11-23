package com.apollo.pharmacy.ocr.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.InsertPrescriptionActivity;
import com.apollo.pharmacy.ocr.activities.checkout.CheckoutActivity;
import com.apollo.pharmacy.ocr.activities.checkout.CheckoutListener;
import com.apollo.pharmacy.ocr.activities.insertprescriptionnew.InsertPrescriptionActivityNew;
import com.apollo.pharmacy.ocr.activities.insertprescriptionnew.InsertPrescriptionActivityNewListener;
import com.apollo.pharmacy.ocr.activities.paymentoptions.PaymentOptionsActivity;
import com.apollo.pharmacy.ocr.controller.PincodeValidateController;
import com.apollo.pharmacy.ocr.databinding.DialogDeliveryAddressBinding;
import com.apollo.pharmacy.ocr.interfaces.PhonePayQrCodeListener;
import com.apollo.pharmacy.ocr.interfaces.PincodeValidateListener;
import com.apollo.pharmacy.ocr.model.PincodeValidateResponse;
import com.apollo.pharmacy.ocr.model.ServicabilityResponse;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Objects;

public class DeliveryAddressDialog implements PincodeValidateListener {

    private Dialog dialog;
    private DialogDeliveryAddressBinding deliveryAddressDialog;

    private boolean negativeExist = false;
    Context context;
    private ConstraintLayout constraintLayout;
    private String stateCode;
    private CheckoutListener checkoutListeners;
    private PhonePayQrCodeListener phonePayQrCodeListeners;
    private InsertPrescriptionActivityNewListener insertPrescriptionActivityNewListener;
    double lating;
    double langing;
    boolean whilePinCodeEntered=false;
    GoogleMap googleMap;
    boolean setDetailsAfterMapping = false;


    public DeliveryAddressDialog(Context context, CheckoutListener checkoutListener, PhonePayQrCodeListener phonePayQrCodeListener,InsertPrescriptionActivityNewListener insertPrescriptionActivityNewListener ) {
        this.checkoutListeners = checkoutListener;
        this.phonePayQrCodeListeners = phonePayQrCodeListener;
        this.insertPrescriptionActivityNewListener=insertPrescriptionActivityNewListener;

        this.context = context;
        dialog = new Dialog(context);
        dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        deliveryAddressDialog = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_delivery_address, null, false);
        dialog.setContentView(deliveryAddressDialog.getRoot());

        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        if (SessionManager.INSTANCE.getMobilenumber() != null && !SessionManager.INSTANCE.getMobilenumber().isEmpty()) {
            deliveryAddressDialog.number.setText(SessionManager.INSTANCE.getMobilenumber());
        }
        if (SessionManager.INSTANCE.getCustrName() != null && !SessionManager.INSTANCE.getCustrName().isEmpty()) {
            deliveryAddressDialog.name.setText(SessionManager.INSTANCE.getCustrName());
        }

        deliveryAddressDialog.zipCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (checkoutListeners != null) {
                    checkoutListeners.toCallTimerInDialog();
                } else if (phonePayQrCodeListeners != null) {
                    phonePayQrCodeListeners.toCallTimerInDialog();
                }

                if (s.length() == 6) {
                    PincodeValidateController pincodeValidateController = new PincodeValidateController(context, DeliveryAddressDialog.this);
//                    pincodeValidateController.onPincodeValidateApi(s.toString());
                    View view1 = dialog.getCurrentFocus();
                    if (view1 != null) {
                        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(dialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }


                    pincodeValidateController.checkServiceAvailability(context, s.toString());
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });


        deliveryAddressDialog.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (checkoutListeners != null) {
                    checkoutListeners.toCallTimerInDialog();
                } else if (phonePayQrCodeListeners != null) {
                    phonePayQrCodeListeners.toCallTimerInDialog();
                }
            }
        });

        deliveryAddressDialog.address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                CheckoutActivity.addressLatLng = false;
                PaymentOptionsActivity.addressLatLng = false;
                InsertPrescriptionActivityNew.addressLatLng = false;
                if (checkoutListeners != null) {
                    checkoutListeners.toCallTimerInDialog();
                } else if (phonePayQrCodeListeners != null) {
                    phonePayQrCodeListeners.toCallTimerInDialog();
                }
            }
        });

        deliveryAddressDialog.number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (checkoutListeners != null) {
                    checkoutListeners.toCallTimerInDialog();
                } else if (phonePayQrCodeListeners != null) {
                    phonePayQrCodeListeners.toCallTimerInDialog();
                }
            }
        });

        deliveryAddressDialog.city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (checkoutListeners != null) {
                    checkoutListeners.toCallTimerInDialog();
                } else if (phonePayQrCodeListeners != null) {
                    phonePayQrCodeListeners.toCallTimerInDialog();
                }
            }
        });

        deliveryAddressDialog.city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (checkoutListeners != null) {
                    checkoutListeners.toCallTimerInDialog();
                } else if (phonePayQrCodeListeners != null) {
                    phonePayQrCodeListeners.toCallTimerInDialog();
                }
            }
        });

        deliveryAddressDialog.state.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (checkoutListeners != null) {
                    checkoutListeners.toCallTimerInDialog();
                } else if (phonePayQrCodeListeners != null) {
                    phonePayQrCodeListeners.toCallTimerInDialog();
                }
            }
        });

        deliveryAddressDialog.constraintLayout.setOnTouchListener((view, motionEvent) -> {
            View view1 = dialog.getCurrentFocus();
            if (view1 != null) {
                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(dialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            return false;
        });

        deliveryAddressDialog.dialogLayout.setOnTouchListener((view, motionEvent) -> {
            View view1 = dialog.getCurrentFocus();
            if (view1 != null) {
                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(dialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            return false;
        });
    }

    public void setPositiveListener(View.OnClickListener okListener) {
        deliveryAddressDialog.dialogButtonOK.setOnClickListener(okListener);
    }

    public void setNegativeListener(View.OnClickListener okListener) {
        deliveryAddressDialog.dialogButtonRecallAddress.setOnClickListener(okListener);
    }

    public void setParentListener(View.OnClickListener okListener) {
        deliveryAddressDialog.parentView.setOnClickListener(okListener);
    }

    public void setCloseIconListener(View.OnClickListener okListener) {
        deliveryAddressDialog.closeAddressDialog.setOnClickListener(okListener);
    }
    public Dialog getDialog(){
        return dialog;
    }
    public void onClickLocateAddressOnMap(View.OnClickListener okListener) {
//        deliveryAddressDialog.addressOnMap.setOnClickListener(okListener);
    }

        public void resetLocationOnMap(View.OnClickListener okListener){
        deliveryAddressDialog.cancel.setOnClickListener(okListener);
    }
    public void selectAndContinue(View.OnClickListener okListener){
        deliveryAddressDialog.save.setOnClickListener(okListener);
    }
    public void show() {

        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void setTitle(String title) {
//        deliveryAddressDialog.title.setText(title);
    }

    String userAddress;

    public String getAddressData() {
        userAddress = deliveryAddressDialog.address.getText().toString() + "," + deliveryAddressDialog.city.getText().toString() + "," + deliveryAddressDialog.state.getText().toString() + "," + deliveryAddressDialog.zipCode.getText().toString();
        return userAddress;
    }

    public String getName() {
        String name = deliveryAddressDialog.name.getText().toString();
        return name;
    }

    public String getAddress() {
        String address = deliveryAddressDialog.address.getText().toString();
        return address;
    }

    public void setAddressforLast3Address(String address, String phoneNumber, String postalCode, String city, String state, String name, String address1, String address2, String onlyAddress) {
//        if(SessionManager.INSTANCE.getLast3Address() !=null && !SessionManager.INSTANCE.getLast3Address().equals("")){
        deliveryAddressDialog.address.setText(onlyAddress);
        deliveryAddressDialog.number.setText(phoneNumber);
        deliveryAddressDialog.zipCode.setText(postalCode);
        deliveryAddressDialog.city.setText(city);
        deliveryAddressDialog.state.setText(state);
        deliveryAddressDialog.name.setText(name);
//        }

    }

    public void setDetailsAfterMapping(String address, String cityForMap, String stateForMap, String postalCodForMap) {
        CheckoutActivity.whilePinCodeEnteredAddressDialog=true;
        InsertPrescriptionActivityNew.whilePinCodeEnteredAddressDialog=true;
        setDetailsAfterMapping=true;
//        if(SessionManager.INSTANCE.getLast3Address() !=null && !SessionManager.INSTANCE.getLast3Address().equals("")){
        deliveryAddressDialog.address.setText(address);
        deliveryAddressDialog.zipCode.setText(postalCodForMap);
        deliveryAddressDialog.city.setText(cityForMap);
        deliveryAddressDialog.state.setText(stateForMap);
        if(PaymentOptionsActivity.isResetClicked){
            PaymentOptionsActivity.isResetClicked=false;
        }
        if(PaymentOptionsActivity.isResetClicked){
            PaymentOptionsActivity.isResetClicked=false;
        }
//        }

    }

    public void selectandContinueFromMap() {
        if(!deliveryAddressDialog.lattitude.getText().toString().equals("Lattitude:0.00")){
            lating = Double.parseDouble(deliveryAddressDialog.lattitude.getText().toString());
        }else{

        }

        if(!deliveryAddressDialog.longitude.getText().toString().equals("Longitude:0.00")){
            langing = Double.parseDouble(deliveryAddressDialog.longitude.getText().toString());
        }else{

        }



    }
//
    public void setTextForLatLong(String mapUserLats, String mapUserLangs) {
        deliveryAddressDialog.lattitude.setText(mapUserLats);
        deliveryAddressDialog.longitude.setText(mapUserLangs);
    }

    public void setTextForLongLangDouble(double latitude, double longitude){
        deliveryAddressDialog.lattitude.setText("" + latitude);
        deliveryAddressDialog.longitude.setText("" + longitude);

    }


    public double getlating() {
        return lating;
    }

    public double getlanging() {
        return langing;
    }


    public void setAddressFromMap(String mapAddress, String mapCity, String mapPostalCode) {

//        if(SessionManager.INSTANCE.getLast3Address() !=null && !SessionManager.INSTANCE.getLast3Address().equals("")){
        deliveryAddressDialog.address.setText(mapAddress);
        deliveryAddressDialog.zipCode.setText(mapPostalCode);
        deliveryAddressDialog.city.setText(mapCity);
//        }

    }

    public String getPincode() {
        String pincode = deliveryAddressDialog.zipCode.getText().toString();
        return pincode;
    }

    public String getCity() {
        String city = deliveryAddressDialog.city.getText().toString();
        return city;
    }

    public String getState() {
        String state = deliveryAddressDialog.state.getText().toString();
        return state;
    }

    public String getStateCode() {
        return stateCode;
    }

    public String getMobileNumber() {
        return deliveryAddressDialog.number.getText().toString();
    }

    public void isNotHomeDelivery() {
        deliveryAddressDialog.addressLayout.setVisibility(View.GONE);
        deliveryAddressDialog.pinCodeLayout.setVisibility(View.GONE);
        deliveryAddressDialog.cityLayout.setVisibility(View.GONE);
        deliveryAddressDialog.stateLayout.setVisibility(View.GONE);
        deliveryAddressDialog.resetButtonLayout.setVisibility(View.GONE);
    }


    public void isNotHomeDeliveryPrescription() {
        deliveryAddressDialog.addressLayout.setVisibility(View.GONE);
        deliveryAddressDialog.pinCodeLayout.setVisibility(View.GONE);
        deliveryAddressDialog.cityLayout.setVisibility(View.GONE);
        deliveryAddressDialog.stateLayout.setVisibility(View.GONE);
//        deliveryAddressDialog.addressOnMap.setVisibility(View.GONE);
        deliveryAddressDialog.tittle.setText("Enter your personal details");
        deliveryAddressDialog.number.setText("");
        deliveryAddressDialog.number.setEnabled(true);
    }

    public void continueButtonVisible() {
        deliveryAddressDialog.dialogButtonOK.setVisibility(View.VISIBLE);
    }

    public void continueButtonGone() {
        deliveryAddressDialog.dialogButtonOK.setVisibility(View.GONE);
    }

    public void reCallAddressButtonVisible() {
        deliveryAddressDialog.dialogButtonRecallAddress.setVisibility(View.VISIBLE);
    }

    public void reCallAddressButtonGone() {
        deliveryAddressDialog.dialogButtonRecallAddress.setVisibility(View.GONE);
    }

    public void locateAddressOnMapVisible() {
//        deliveryAddressDialog.addressOnMap.setVisibility(View.VISIBLE);
    }

    public void locateAddressOnMapGone() {
//        deliveryAddressDialog.addressOnMap.setVisibility(View.GONE);
    }

    public void layoutForMapVisible() {
        deliveryAddressDialog.layoutForMap.setVisibility(View.VISIBLE);
    }

    public void resetButtonVisible() {
        deliveryAddressDialog.resetButtonLayout.setVisibility(View.VISIBLE);
    }


    public void resetButtonGone() {
        deliveryAddressDialog.resetButtonLayout.setVisibility(View.GONE);
    }


    public void layoutForMapGone() {
        deliveryAddressDialog.layoutForMap.setVisibility(View.GONE);
    }

    public void setlayoutWithoutMap(){
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        );
        deliveryAddressDialog.addressFields.setLayoutParams(param);
        LinearLayout.LayoutParams paramsForName = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                0.5f

        );
        deliveryAddressDialog.nameLayout.setLayoutParams(paramsForName);

        LinearLayout.LayoutParams paramsForMobile = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                0.5f
        );
        deliveryAddressDialog.mobileNumberlayout.setLayoutParams(paramsForMobile);
        deliveryAddressDialog.tittle.setText("Enter your Name and Mobile Number");
    }


    public void onbackPressed() {
        dialog.onBackPressed();
    }

    public void setDeliveryAddress(String name, String userAddress, String pincode, String city, String state) {
        deliveryAddressDialog.name.setText(name);
        deliveryAddressDialog.address.setText(userAddress);
        deliveryAddressDialog.number.setText(SessionManager.INSTANCE.getMobilenumber());
        deliveryAddressDialog.zipCode.setText(pincode);
        PincodeValidateController pincodeValidateController = new PincodeValidateController(context, DeliveryAddressDialog.this);
//        pincodeValidateController.onPincodeValidateApi(pincode.toString());
        View view1 = dialog.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(dialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
//        pincodeValidateController.checkServiceAvailability(context, pincode.toString());
    }

    public boolean notHomeDeliveryValidationsPrescription() {
        String name = deliveryAddressDialog.name.getText().toString();
        String number = Objects.requireNonNull(deliveryAddressDialog.number.getText()).toString();
//        String emailAddress = Objects.requireNonNull(deliveryAddressDialog.email.getText()).toString();
        String address = deliveryAddressDialog.address.getText().toString().trim();
        String zipCode = deliveryAddressDialog.zipCode.getText().toString().trim();
//        String city = deliveryAddressDialog.city.getText().toString().trim();
//        String state = deliveryAddressDialog.state.getText().toString().trim();

        if (name.isEmpty()) {
            deliveryAddressDialog.name.setError("Name should not empty");
            deliveryAddressDialog.name.requestFocus();
            return false;
        } else if (number.isEmpty()) {
            deliveryAddressDialog.number.setError("Phone Number should not empty");
            deliveryAddressDialog.number.requestFocus();
            return false;
        } else if (deliveryAddressDialog.number.getText().length() < 10 || deliveryAddressDialog.number.getText().length() > 10) {
            deliveryAddressDialog.number.setError("phone number must be 10 digits");
            deliveryAddressDialog.number.requestFocus();
            return false;
        }
//        else if (emailAddress.isEmpty()) {
//            deliveryAddressDialog.email.setError("Enter Valid Email");
//            deliveryAddressDialog.email.requestFocus();
//            return false;
//        } else if (!Utils.isValidEmail(emailAddress)) {
//            deliveryAddressDialog.email.setError("Enter Valid Email");
//            deliveryAddressDialog.email.requestFocus();
//            return false;
//    }
        else if (!name.matches("^[A-Za-z ]+$")) {
            deliveryAddressDialog.name.setError("Enter valid name");
            deliveryAddressDialog.name.requestFocus();
            return false;
        }
//        else if (address.isEmpty()) {
//            deliveryAddressDialog.address.setError("Address should not be empty");
//            deliveryAddressDialog.address.requestFocus();
//            return false;
//        }
//        else if (zipCode.isEmpty()) {
//            deliveryAddressDialog.zipCode.setError("Pin Code should not be empty");
//            deliveryAddressDialog.zipCode.requestFocus();
//            return false;
//        } else if (city.isEmpty()) {
//            deliveryAddressDialog.city.setError("City should not empty");
//            deliveryAddressDialog.city.requestFocus();
//            return false;
//        } else if (state.isEmpty()) {
//            deliveryAddressDialog.state.setError("State should not empty");
//            deliveryAddressDialog.state.requestFocus();
//            return false;
//        }
//        else if (deliveryAddressDialog.zipCode.getText().toString().length() < 6) {
//            deliveryAddressDialog.zipCode.setError("Enter valid pincode");
//            deliveryAddressDialog.zipCode.requestFocus();
//            return false;
//        }

        return true;

    }

    public boolean notHomeDeliveryValidations() {
        String name = deliveryAddressDialog.name.getText().toString();
//        String number = Objects.requireNonNull(deliveryAddressDialog.number.getText()).toString();
//        String emailAddress = Objects.requireNonNull(deliveryAddressDialog.email.getText()).toString();
        String address = deliveryAddressDialog.address.getText().toString().trim();
        String zipCode = deliveryAddressDialog.zipCode.getText().toString().trim();
//        String city = deliveryAddressDialog.city.getText().toString().trim();
//        String state = deliveryAddressDialog.state.getText().toString().trim();

        if (name.isEmpty()) {
            deliveryAddressDialog.name.setError("Name should not empty");
            deliveryAddressDialog.name.requestFocus();
            return false;
//        } else if (number.isEmpty()) {
//            deliveryAddressDialog.number.setError("Phone Number should not empty");
//            deliveryAddressDialog.number.requestFocus();
//            return false;
//        } else if (deliveryAddressDialog.number.getText().length() < 10 || deliveryAddressDialog.number.getText().length() > 10) {
//            deliveryAddressDialog.number.setError("phone number must be 10 digits");
//            deliveryAddressDialog.number.requestFocus();
//            return false;
//        } else if (emailAddress.isEmpty()) {
//            deliveryAddressDialog.email.setError("Enter Valid Email");
//            deliveryAddressDialog.email.requestFocus();
//            return false;
//        } else if (!Utils.isValidEmail(emailAddress)) {
//            deliveryAddressDialog.email.setError("Enter Valid Email");
//            deliveryAddressDialog.email.requestFocus();
//            return false;
        } else if (!name.matches("^[A-Za-z ]+$")) {
            deliveryAddressDialog.name.setError("Enter valid name");
            deliveryAddressDialog.name.requestFocus();
            return false;
        }
//        else if (address.isEmpty()) {
//            deliveryAddressDialog.address.setError("Address should not be empty");
//            deliveryAddressDialog.address.requestFocus();
//            return false;
//        }
//        else if (zipCode.isEmpty()) {
//            deliveryAddressDialog.zipCode.setError("Pin Code should not be empty");
//            deliveryAddressDialog.zipCode.requestFocus();
//            return false;
//        } else if (city.isEmpty()) {
//            deliveryAddressDialog.city.setError("City should not empty");
//            deliveryAddressDialog.city.requestFocus();
//            return false;
//        } else if (state.isEmpty()) {
//            deliveryAddressDialog.state.setError("State should not empty");
//            deliveryAddressDialog.state.requestFocus();
//            return false;
//        }
//        else if (deliveryAddressDialog.zipCode.getText().toString().length() < 6) {
//            deliveryAddressDialog.zipCode.setError("Enter valid pincode");
//            deliveryAddressDialog.zipCode.requestFocus();
//            return false;
//        }
        return true;
    }

    public boolean validations() {
        String name = deliveryAddressDialog.name.getText().toString();
        String number = Objects.requireNonNull(deliveryAddressDialog.number.getText()).toString();
//        String emailAddress = Objects.requireNonNull(deliveryAddressDialog.email.getText()).toString();
        String address = deliveryAddressDialog.address.getText().toString().trim();
        String zipCode = deliveryAddressDialog.zipCode.getText().toString().trim();
        String phoneNumber= deliveryAddressDialog.number.getText().toString().trim();
        if (name.isEmpty()) {
            deliveryAddressDialog.name.setError("Name should not empty");
            deliveryAddressDialog.name.requestFocus();
            return false;
        } else if (!name.matches("^[A-Za-z ]+$")) {
            deliveryAddressDialog.name.setError("Enter valid name");
            deliveryAddressDialog.name.requestFocus();
            return false;
        } else if (address.isEmpty()) {
            deliveryAddressDialog.address.setError("Address should not be empty");
            deliveryAddressDialog.address.requestFocus();
            return false;
        } else if (zipCode.isEmpty()) {
            deliveryAddressDialog.zipCode.setError("Pin Code should not be empty");
            deliveryAddressDialog.zipCode.requestFocus();
            return false;
        } else if (deliveryAddressDialog.zipCode.getText().toString().length() < 6) {
            deliveryAddressDialog.zipCode.setError("Enter valid pincode");
            deliveryAddressDialog.zipCode.requestFocus();
            return false;
        }

        return true;
    }

    public boolean validationsForMap() {
        String address = deliveryAddressDialog.address.getText().toString().trim();
        String zipCode = deliveryAddressDialog.zipCode.getText().toString().trim();
        if (zipCode.isEmpty()) {
            deliveryAddressDialog.zipCode.setError("Pin Code should not be empty");
            deliveryAddressDialog.zipCode.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onSuccessPincodeValidate(List<PincodeValidateResponse> body) {
        if (body != null && body.size() > 0 && body.get(0) != null && body.get(0).getCity() != null && body.get(0).getState() != null) {
            deliveryAddressDialog.city.setText(body.get(0).getCity().toString());
            deliveryAddressDialog.state.setText(body.get(0).getState().toString());
//            stateCode = body.get(0).get().toString()
            View view1 = dialog.getCurrentFocus();
            if (view1 != null) {
                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(dialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }


        } else if (body != null && body.size() > 0 && body.get(0).getStatus() != null && body.get(0).getStatus().equals("False")) {
            deliveryAddressDialog.city.setText(null);
            deliveryAddressDialog.state.setText(null);
            Toast.makeText(context, body.get(0).getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            deliveryAddressDialog.city.setText(null);
            deliveryAddressDialog.state.setText(null);
        }
    }

    public void onClickCrossIcon() {
        deliveryAddressDialog.name.setText("");
        deliveryAddressDialog.address.setText("");
        deliveryAddressDialog.zipCode.setText("");
        deliveryAddressDialog.city.setText("");
        deliveryAddressDialog.state.setText("");
    }

    @Override
    public void onFailurePincode(List<PincodeValidateResponse> body) {
        deliveryAddressDialog.zipCode.setText("");
        deliveryAddressDialog.city.setText("");
        deliveryAddressDialog.state.setText("");

        Toast.makeText(context, "Please enter a valid pincode", Toast.LENGTH_SHORT).show();

        View view1 = dialog.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(dialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onSuccessServiceability(ServicabilityResponse response) {
        Utils.dismissDialog();
        deliveryAddressDialog.city.setText(response.getDeviceDetails().get(0).getCITY());
        deliveryAddressDialog.state.setText(response.getDeviceDetails().get(0).getSTATE());
        stateCode = response.getDeviceDetails().get(0).getSTATE_CODE();
        View view1 = dialog.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(dialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        if(!CheckoutActivity.whilePinCodeEnteredAddressDialog && checkoutListeners!=null){
            checkoutListeners.onLastDigitPinCode();
        }else{
            CheckoutActivity.whilePinCodeEnteredAddressDialog=false;
        }

        if(!PaymentOptionsActivity.isFirstTimeLoading && !setDetailsAfterMapping){
            if(phonePayQrCodeListeners!=null){
                phonePayQrCodeListeners.onLastDigitPinCode();
            }else {

            }
        }else{
            PaymentOptionsActivity.isFirstTimeLoading=false;
            setDetailsAfterMapping=false;
        }


        if(!setDetailsAfterMapping && !InsertPrescriptionActivityNew.isFirstTimeLoading && !InsertPrescriptionActivityNew.whilePinCodeEnteredAddressDialog ){
            if(insertPrescriptionActivityNewListener!=null){
                insertPrescriptionActivityNewListener.onLastDigitPinCode();
            }else{

            }

        }else{
            InsertPrescriptionActivityNew.whilePinCodeEnteredAddressDialog=false;
            InsertPrescriptionActivityNew.isFirstTimeLoading=false;
            setDetailsAfterMapping=false;
        }

        deliveryAddressDialog.snackText.setText(context.getResources().getString(R.string.label_service_available));
        deliveryAddressDialog.snackText.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            deliveryAddressDialog.snackText.setVisibility(View.GONE);
        }, 2000);
//        Utils.showSnackbarDialog(context, dialog.getWindow().getDecorView(), context.getResources().getString(R.string.label_service_available));
    }


    @Override
    public void onFailureServiceability(String message) {
        Utils.dismissDialog();
        deliveryAddressDialog.city.setText(null);
        deliveryAddressDialog.state.setText(null);
        View view1 = dialog.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(dialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        deliveryAddressDialog.snackText.setText(message);
        deliveryAddressDialog.snackText.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            deliveryAddressDialog.snackText.setVisibility(View.GONE);
        }, 2000);
//        Utils.showSnackbarDialog(context, dialog.getWindow().getDecorView(), message);
    }

//
//    @Override
//    public void onMarkerDragStart(Marker marker) {
//
//    }
//
//    @Override
//    public void onMarkerDrag(Marker marker) {
//
//    }
//
//    @Override
//    public void onMarkerDragEnd(Marker marker) {
//        LatLng position = marker.getPosition();
//
//        deliveryAddressDialog.lattitude.setText("" + position.latitude);
//        deliveryAddressDialog.longitude.setText("" + position.longitude);
//    }



    public void onMarkerssragEnd(LatLng marker){
        deliveryAddressDialog.lattitude.setText("" + marker.latitude);
        deliveryAddressDialog.longitude.setText("" + marker.longitude);
    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        map = googleMap;
//
//        map.setOnMarkerDragListener(this);
//
//        if (!testingmapViewLats) {
//            mapRepresentData();
//        } else {
//            mapHandling = true;
//            getLocationDetails(Double.parseDouble(mapUserLats), Double.parseDouble(mapUserLangs));
//        }
//    }
//    @SuppressLint("SetTextI18n")
//    public void getLocationDetails(double lating, double langing) {
//        List<Address> addresses;
//        geocoder = new Geocoder(context, Locale.getDefault());
//
//        try {
//            addresses = geocoder.getFromLocation(lating, langing, 1);
//            address = addresses.get(0).getAddressLine(0);
//            city = addresses.get(0).getLocality();
//            state = addresses.get(0).getAdminArea();
//            country = addresses.get(0).getCountryName();
//            postalCode = addresses.get(0).getPostalCode();
//            knonName = addresses.get(0).getFeatureName();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        LatLng latLng = new LatLng(lating, langing);
//        map.addMarker(new MarkerOptions().position(latLng).draggable(true).title("Marker in : " + address));
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
//
//        if (mapHandling) {
//            textViewlat.setText(mapUserLats);
//            textViewLang.setText(mapUserLangs);
//            mapHandling = false;
//        }
//
//    }

//    public void mapRepresentData() {
//        if (locations!= null) {
//
//            try {
////                locations = getIntent().getStringExtra("locatedPlace");
//                List<Address> addressList = null;
//                if (locations != null || !locations.equals("")) {
//                    geocoder = new Geocoder(context);
//                    try {
//                        addressList = geocoder.getFromLocationName(locations, 1);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Address address = addressList.get(0);
//                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//                    map.clear();
//                    map.addMarker(new MarkerOptions().
//                            position(latLng).
//                            title(locations).draggable(true)
//                    );
//                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//                    textViewlat.setText("" + address.getLatitude());
//                    textViewLang.setText("" + address.getLongitude());
//
//                } else {
//                    Toast.makeText(context, "Please Enter Valid Address", Toast.LENGTH_SHORT).show();
//
////                    Toast toast = Toast.makeText(MapViewActvity.this, "Please Enter Valid Address", Toast.LENGTH_SHORT);
////                    toast.getView().setBackground(getResources().getDrawable(R.drawable.toast_bg));
////                    TextView text = (TextView) toast.getView().findViewById(android.R.id.message);
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                        Typeface typeface = Typeface.createFromAsset(this.getAssets(),"font/montserrat_bold.ttf");
////                        text.setTypeface(typeface);
////                        text.setTextColor(Color.WHITE);
////                        text.setTextSize(14);
////                    }
////                    toast.show();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                Toast.makeText(context, "Please Enter Valid Address", Toast.LENGTH_SHORT).show();
//
////                Toast toast = Toast.makeText(MapViewActvity.this, "Please Enter Valid Address", Toast.LENGTH_SHORT);
////                toast.getView().setBackground(getResources().getDrawable(R.drawable.toast_bg));
////                TextView text = (TextView) toast.getView().findViewById(android.R.id.message);
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                    Typeface typeface = Typeface.createFromAsset(this.getAssets(),"font/montserrat_bold.ttf");
////                    text.setTypeface(typeface);
////                    text.setTextColor(Color.WHITE);
////                    text.setTextSize(14);
////                }
////                toast.show();
//            }
//
//        }
//    }


//    public void setPositiveLabel(String positive) {
//        alertDialogBoxBinding.btnYes.setText(positive);
//    }
//
//    public void setNegativeLabel(String negative) {
//        negativeExist = true;
//        alertDialogBoxBinding.btnNo.setText(negative);
//    }



}
