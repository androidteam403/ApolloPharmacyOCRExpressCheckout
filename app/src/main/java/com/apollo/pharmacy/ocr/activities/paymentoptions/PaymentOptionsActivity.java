package com.apollo.pharmacy.ocr.activities.paymentoptions;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.BaseActivity;
import com.apollo.pharmacy.ocr.activities.HomeActivity;
import com.apollo.pharmacy.ocr.activities.MySearchActivity;
import com.apollo.pharmacy.ocr.activities.OrderinProgressActivity;
import com.apollo.pharmacy.ocr.activities.checkout.CheckoutActivity;
import com.apollo.pharmacy.ocr.activities.paymentoptions.model.ExpressCheckoutTransactionApiRequest;
import com.apollo.pharmacy.ocr.activities.paymentoptions.model.ExpressCheckoutTransactionApiResponse;
import com.apollo.pharmacy.ocr.adapters.LastThreeAddressAdapter;
import com.apollo.pharmacy.ocr.controller.PhonePayQrCodeController;
import com.apollo.pharmacy.ocr.databinding.ActivityPaymentOptionsBinding;
import com.apollo.pharmacy.ocr.databinding.DialogForLast3addressBinding;
import com.apollo.pharmacy.ocr.dialog.DeliveryAddressDialog;
import com.apollo.pharmacy.ocr.interfaces.PhonePayQrCodeListener;
import com.apollo.pharmacy.ocr.model.GetPackSizeResponse;
import com.apollo.pharmacy.ocr.model.OCRToDigitalMedicineResponse;
import com.apollo.pharmacy.ocr.model.PhonePayQrCodeResponse;
import com.apollo.pharmacy.ocr.model.PlaceOrderReqModel;
import com.apollo.pharmacy.ocr.model.PlaceOrderResModel;
import com.apollo.pharmacy.ocr.model.RecallAddressResponse;
import com.apollo.pharmacy.ocr.model.StateCodes;
import com.apollo.pharmacy.ocr.model.UserAddress;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;
import com.google.zxing.WriterException;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class PaymentOptionsActivity extends BaseActivity implements PhonePayQrCodeListener {
    private ActivityPaymentOptionsBinding activityPaymentOptionsBinding;
    private double pharmaTotalData = 0.0;
    private List<OCRToDigitalMedicineResponse> dataList;
    private String customerDeliveryAddress, name, singleAdd, pincode, city, state, stateCode, mobileNumber;
    private double grandTotalAmountFmcg = 0.0;
    private double grandTotalAmountPharma = 0.0;
    private boolean isPharmaOrder;
    private boolean isFmcgOrder;
    private boolean isPharmadeliveryType, isFmcgDeliveryType;
    Dialog dialogforAddress;
    private List<RecallAddressResponse.CustomerDetail> recallAddressResponse;
    DeliveryAddressDialog deliveryAddressDialog;
    public static boolean isTimerfor20mins = true;
    private String fmcgOrderId = "";
    private boolean isFmcgQrCodePayment = false;
    public static String isPaymentActivityForTimer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPaymentOptionsBinding = DataBindingUtil.setContentView(this, R.layout.activity_payment_options);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        isPaymentActivityForTimer = "isPaymentActivity";
        HomeActivity.isPaymentSelectionActivity = true;
        HomeActivity.isHomeActivity = false;

        activityPaymentOptionsBinding.pharmaTotalInclOffer.setPaintFlags(activityPaymentOptionsBinding.pharmaTotalInclOffer.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        activityPaymentOptionsBinding.fmcgTotalInclOffer.setPaintFlags(activityPaymentOptionsBinding.fmcgTotalInclOffer.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        if (SessionManager.INSTANCE.getMobilenumber() != null) {
            activityPaymentOptionsBinding.userNum.setText(SessionManager.INSTANCE.getMobilenumber());
        }

        OrderDetailsuiModel orderDetailsuiModel = new OrderDetailsuiModel();
        if (getIntent() != null) {
            pharmaTotalData = (double) getIntent().getDoubleExtra("fmcgTotal", 0.0);
            isFmcgDeliveryType = getIntent().getBooleanExtra("isFmcgHomeDelivery", false);
            isPharmadeliveryType = getIntent().getBooleanExtra("isPharmaHomeDelivery", false);
            orderDetailsuiModel.setPharmaHomeDelivery(getIntent().getBooleanExtra("isPharmaHomeDelivery", false));
            orderDetailsuiModel.setFmcgHomeDelivery(getIntent().getBooleanExtra("isFmcgHomeDelivery", false));
            customerDeliveryAddress = (String) getIntent().getStringExtra("customerDeliveryAddress");
            name = (String) getIntent().getStringExtra("name");
            singleAdd = (String) getIntent().getStringExtra("singleAdd");
            pincode = (String) getIntent().getStringExtra("pincode");
            city = (String) getIntent().getStringExtra("city");
            state = (String) getIntent().getStringExtra("state");
            stateCode = (String) getIntent().getStringExtra("STATE_CODE");
            mobileNumber = (String) getIntent().getStringExtra("MOBILE_NUMBER");
            fmcgOrderId = (String) getIntent().getStringExtra("FMCG_TRANSACTON_ID");
            expressCheckoutTransactionId = (String) getIntent().getStringExtra("EXPRESS_CHECKOUT_TRANSACTION_ID");
            recallAddressResponse = (List<RecallAddressResponse.CustomerDetail>) getIntent().getSerializableExtra("recallAddressResponses");
        }


        if (null != SessionManager.INSTANCE.getDataList())
            this.dataList = SessionManager.INSTANCE.getDataList();
        if (dataList != null && dataList.size() > 0) {

            List<OCRToDigitalMedicineResponse> countUniques = new ArrayList<>();
            countUniques.addAll(dataList);

            for (int i = 0; i < countUniques.size(); i++) {
                for (int j = 0; j < countUniques.size(); j++) {
                    if (i != j && countUniques.get(i).getArtName().equals(countUniques.get(j).getArtName())) {
                        countUniques.remove(j);
                        j--;
                    }
                }
            }


            int pharmaMedicineCount = 0;
            int fmcgMedicineCount = 0;
            double pharmaTotal = 0.0;
            double fmcgTotal = 0.0;
            boolean isFmcg = false;
            boolean isPharma = false;
            double pharmaTotalOffer = 0.0;
            double fmcgTotalOffer = 0.0;
            for (OCRToDigitalMedicineResponse data : dataList) {
                if (data.getMedicineType() != null) {
                    if (data.getMedicineType().equals("PHARMA")) {
                        isPharma = true;
                        isPharmaOrder = true;
                        if (data.getNetAmountInclTax() != null && !data.getNetAmountInclTax().isEmpty()) {
                            pharmaTotal = pharmaTotal + (Double.parseDouble(data.getNetAmountInclTax()));
                            pharmaTotalOffer = pharmaTotalOffer + (Double.parseDouble(data.getArtprice()) * data.getQty());
                        } else {
                            pharmaTotal = pharmaTotal + (Double.parseDouble(data.getArtprice()) * data.getQty());
                            pharmaTotalOffer = pharmaTotalOffer + (Double.parseDouble(data.getArtprice()) * data.getQty());
                        }

                    } else {
                        isFmcg = true;
                        isFmcgOrder = true;
                        if (data.getNetAmountInclTax() != null && !data.getNetAmountInclTax().isEmpty()) {
                            fmcgTotal = fmcgTotal + (Double.parseDouble(data.getNetAmountInclTax()));
                            fmcgTotalOffer = fmcgTotalOffer + (Double.parseDouble(data.getArtprice()) * data.getQty());
                        } else {
                            fmcgTotal = fmcgTotal + (Double.parseDouble(data.getArtprice()) * data.getQty());
                            fmcgTotalOffer = fmcgTotalOffer + (Double.parseDouble(data.getArtprice()) * data.getQty());
                        }

                    }
                }
            }

            for (int i = 0; i < dataList.size(); i++) {
                for (int j = 0; j < countUniques.size(); j++) {
                    if (dataList.get(i).getArtName().equalsIgnoreCase(countUniques.get(j).getArtName())) {
                        if (countUniques.get(j).getMedicineType().equals("FMCG")) {
                            fmcgMedicineCount++;
                            countUniques.remove(j);
                            j--;
                        } else if (countUniques.get(j).getMedicineType().equals("PHARMA")) {
                            pharmaMedicineCount++;
                            countUniques.remove(j);
                            j--;
                        } else {
                            fmcgMedicineCount++;
                            countUniques.remove(j);
                            j--;
                        }
                    }
                }
            }
            if (isFmcgDeliveryType || isFmcgOrder) {
                activityPaymentOptionsBinding.cashOnDelivery.setVisibility(View.VISIBLE);
            } else {
                activityPaymentOptionsBinding.cashOnDelivery.setVisibility(View.GONE);
            }

//            fmcgToatalPass = fmcgTotal;
            orderDetailsuiModel.setPharmaCount(String.valueOf(pharmaMedicineCount));
            orderDetailsuiModel.setFmcgCount(String.valueOf(fmcgMedicineCount));

            DecimalFormat formatter = new DecimalFormat("#,###.00");
            String pharmaformatted = formatter.format(pharmaTotal);
            String fmcgFormatted = formatter.format(fmcgTotal);
            String pharmaOfferformatted = formatter.format(pharmaTotalOffer);
            String fmcgOfferFormatted = formatter.format(fmcgTotalOffer);
            orderDetailsuiModel.setPharmaTotalOffer(getResources().getString(R.string.rupee) + pharmaOfferformatted);
            orderDetailsuiModel.setFmcgTotalOffer(getResources().getString(R.string.rupee) + fmcgOfferFormatted);

            orderDetailsuiModel.setPharmaTotal(getResources().getString(R.string.rupee) + String.valueOf(pharmaformatted));
            orderDetailsuiModel.setFmcgTotal(getResources().getString(R.string.rupee) + String.valueOf(fmcgFormatted));
            orderDetailsuiModel.setTotalMedicineCount(String.valueOf(dataList.size()));
            String totalprodAmt = formatter.format(pharmaTotal + fmcgTotal);
            orderDetailsuiModel.setMedicineTotal(getResources().getString(R.string.rupee) + String.valueOf(totalprodAmt));

            orderDetailsuiModel.setFmcgPharma(isPharma && isFmcg);
            orderDetailsuiModel.setFmcg(isFmcg);
            orderDetailsuiModel.setPharma(isPharma);
            grandTotalAmountFmcg = fmcgTotal;
//            DecimalFormat formatter1 = new DecimalFormat("#,###.00");
//            String formatted = formatter1.format(pharmaTotal);
//            grandTotalAmountPharma = Double.parseDouble(formatted);
            grandTotalAmountPharma = pharmaTotal;
            activityPaymentOptionsBinding.setModel(orderDetailsuiModel);
        }
        if (orderDetailsuiModel.isPharma && !orderDetailsuiModel.isFmcg) {
            activityPaymentOptionsBinding.paymentHeaderParent.setVisibility(View.GONE);
            activityPaymentOptionsBinding.confirmOnlyPharmaOrder.setVisibility(View.VISIBLE);
            activityPaymentOptionsBinding.confirmOnlyPharmaOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (activityPaymentOptionsBinding.deliveryAddress.getText().toString() != null &&
//                            !activityPaymentOptionsBinding.deliveryAddress.getText().toString().isEmpty()) {
//                    placeOrder();

                    if (isFmcgOrder) {
                        isFmcgOrder = false;
//                        new PhonePayQrCodeController(PaymentOptionsActivity.this, PaymentOptionsActivity.this).expressCheckoutTransactionApiCall(getExpressCheckoutTransactionApiRequest());
                        placeOrderFmcg();
                    } else {
                        isPharmaOrder = false;
                        placeOrderPharma();
                    }

//                    } else {
//                        DeliveryAddressDialog deliveryAddressDialog = new DeliveryAddressDialog(PaymentOptionsActivity.this);
//                        deliveryAddressDialog.setPositiveListener(view1 -> {
//                            if (deliveryAddressDialog.validations()) {
//                                customerDeliveryAddress = deliveryAddressDialog.getAddressData();
//                                if (customerDeliveryAddress != null) {
//                                    activityPaymentOptionsBinding.deliveryAddress.setText(customerDeliveryAddress);
//
//                                    name = deliveryAddressDialog.getName();
//                                    singleAdd = deliveryAddressDialog.getAddress();
//                                    pincode = deliveryAddressDialog.getPincode();
//                                    city = deliveryAddressDialog.getCity();
//                                    state = deliveryAddressDialog.getState();
//
//                                }
//                                deliveryAddressDialog.dismiss();
//                            }
//                        });
//                        deliveryAddressDialog.setNegativeListener(view2 -> {
//                            deliveryAddressDialog.dismiss();
//                        });
//                        deliveryAddressDialog.show();
//                    }
                }
            });
        }

        activityPaymentOptionsBinding.pharmaTotal.setText(getResources().getString(R.string.rupee) + String.valueOf(pharmaTotalData));

        unselectedBgClors();
        activityPaymentOptionsBinding.scanToPay.setBackgroundResource(R.drawable.ic_payment_methods_selectebg);
        PaymentInfoLayoutsHandlings();
        activityPaymentOptionsBinding.scanToPayInfoLay.setVisibility(View.VISIBLE);
        if (grandTotalAmountFmcg > 0) {
            Utils.showDialog(PaymentOptionsActivity.this, "Loading…");
            PhonePayQrCodeController phonePayQrCodeController = new PhonePayQrCodeController(getApplicationContext(), PaymentOptionsActivity.this);
            phonePayQrCodeController.getPhonePayQrCodeGeneration(scanPay, grandTotalAmountFmcg);
        }
        activityPaymentOptionsBinding.changeDeliveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deliveryAddressDialog = new DeliveryAddressDialog(PaymentOptionsActivity.this);
                if (name != null && singleAdd != null && pincode != null && city != null && state != null) {
                    deliveryAddressDialog.setDeliveryAddress(name, singleAdd, pincode, city, state);
                }
                if (recallAddressResponse.size() > 0) {
                    deliveryAddressDialog.reCallAddressButtonVisible();
                } else {
                    deliveryAddressDialog.reCallAddressButtonGone();
                }

                deliveryAddressDialog.setPositiveListener(view1 -> {
                    if (deliveryAddressDialog.validations()) {
                        customerDeliveryAddress = deliveryAddressDialog.getAddressData();
                        if (customerDeliveryAddress != null) {
                            activityPaymentOptionsBinding.deliveryAddress.setText(customerDeliveryAddress);

                            name = deliveryAddressDialog.getName();
                            singleAdd = deliveryAddressDialog.getAddress();
                            pincode = deliveryAddressDialog.getPincode();
                            city = deliveryAddressDialog.getCity();
                            state = deliveryAddressDialog.getState();

                        }
                        deliveryAddressDialog.dismiss();
                    }
                });
                deliveryAddressDialog.setNegativeListener(view1 -> {
//            deliveryAddressDialog.continueButtonGone();

                    dialogforAddress = new Dialog(PaymentOptionsActivity.this);
                    DialogForLast3addressBinding dialogForLast3addressBinding = DataBindingUtil.inflate(LayoutInflater.from(PaymentOptionsActivity.this), R.layout.dialog_for_last3address, null, true);
                    dialogforAddress.setContentView(dialogForLast3addressBinding.getRoot());
                    if (dialogforAddress.getWindow() != null)
                        dialogforAddress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogforAddress.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.WRAP_CONTENT);
                    dialogforAddress.setCancelable(false);

                    if (recallAddressResponse.size() > 0) {
                        dialogForLast3addressBinding.nolistfound.setVisibility(View.GONE);
                        dialogForLast3addressBinding.last3addressRecyclerView.setVisibility(View.VISIBLE);
                        RecyclerView rvTest = (RecyclerView) dialogforAddress.findViewById(R.id.last_3addressRecyclerView);
                        rvTest.setHasFixedSize(true);
                        rvTest.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                    rvTest.addItemDecoration(new SimpleDividerItemDecoration(context, R.drawable.divider));

                        LastThreeAddressAdapter lastThreeAddressAdapter = new LastThreeAddressAdapter(getApplicationContext(), recallAddressResponse, null, PaymentOptionsActivity.this);
                        rvTest.setAdapter(lastThreeAddressAdapter);
                    } else {
                        dialogForLast3addressBinding.nolistfound.setVisibility(View.VISIBLE);
                        dialogForLast3addressBinding.last3addressRecyclerView.setVisibility(View.GONE);
                    }


                    dialogForLast3addressBinding.dialogButtonAddAddress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogforAddress.dismiss();
//                            deliveryAddressDialog = new DeliveryAddressDialog(PaymentOptionsActivity.this);
                            deliveryAddressDialog.reCallAddressButtonVisible();
                            deliveryAddressDialog.setNegativeListener(view -> {
                                dialogforAddress.show();
                            });

//                9958704005




//                deliveryAddressDialog.continueButtonVisible();
                        }
                    });


                    dialogforAddress.show();
//        Toast.makeText(getApplicationContext(), ""+recallAddressResponse.getCustomerDetails().size(), Toast.LENGTH_SHORT).show();
                });
//
//                deliveryAddressDialog.setPositiveListener(view1 -> {
//                    if (deliveryAddressDialog.validations()) {
//                        customerDeliveryAddress = deliveryAddressDialog.getAddressData();
//                        if (customerDeliveryAddress != null) {
//                            activityPaymentOptionsBinding.deliveryAddress.setText(customerDeliveryAddress);
//
//                            name = deliveryAddressDialog.getName();
//                            singleAdd = deliveryAddressDialog.getAddress();
//                            pincode = deliveryAddressDialog.getPincode();
//                            city = deliveryAddressDialog.getCity();
//                            state = deliveryAddressDialog.getState();
//
//                        }
//                        deliveryAddressDialog.dismiss();
//                    }
//                });
//                deliveryAddressDialog.setNegativeListener(view1 ->{
////            deliveryAddressDialog.continueButtonGone();
//
//                    dialogforAddress = new Dialog(PaymentOptionsActivity.this);
//                    DialogForLast3addressBinding dialogForLast3addressBinding = DataBindingUtil.inflate(LayoutInflater.from(PaymentOptionsActivity.this), R.layout.dialog_for_last3address, null, true);
//                    dialogforAddress.setContentView(dialogForLast3addressBinding.getRoot());
//                    if (dialogforAddress.getWindow() != null)
//                        dialogforAddress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                    dialogforAddress.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
//                            WindowManager.LayoutParams.WRAP_CONTENT);
//                    dialogforAddress.setCancelable(false);
//
//                    if (recallAddressResponse.size() > 0) {
//                        dialogForLast3addressBinding.nolistfound.setVisibility(View.GONE);
//                        dialogForLast3addressBinding.last3addressRecyclerView.setVisibility(View.VISIBLE);
//                        RecyclerView rvTest = (RecyclerView) dialogforAddress.findViewById(R.id.last_3addressRecyclerView);
//                        rvTest.setHasFixedSize(true);
//                        rvTest.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
////                    rvTest.addItemDecoration(new SimpleDividerItemDecoration(context, R.drawable.divider));
//
//                        LastThreeAddressAdapter lastThreeAddressAdapter = new LastThreeAddressAdapter(getApplicationContext(), recallAddressResponse, null, PaymentOptionsActivity.this);
//                        rvTest.setAdapter(lastThreeAddressAdapter);
//                    } else {
//                        dialogForLast3addressBinding.nolistfound.setVisibility(View.VISIBLE);
//                        dialogForLast3addressBinding.last3addressRecyclerView.setVisibility(View.GONE);
//                    }
//
//
//                    dialogForLast3addressBinding.dialogButtonAddAddress.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialogforAddress.dismiss();
//
//                            deliveryAddressDialog = new DeliveryAddressDialog(PaymentOptionsActivity.this);
//                            deliveryAddressDialog.reCallAddressButtonVisible();
//                            deliveryAddressDialog.setNegativeListener(view ->{
//                                deliveryAddressDialog.dismiss();
//                                dialogforAddress.show();
//                            });
//
////                9958704005
//
//                            deliveryAddressDialog.show();
//
//
////                deliveryAddressDialog.continueButtonVisible();
//                        }
//                    });
//
//
//                    dialogforAddress.show();
////        Toast.makeText(getApplicationContext(), ""+recallAddressResponse.getCustomerDetails().size(), Toast.LENGTH_SHORT).show();
//                });
                deliveryAddressDialog.show();
            }
        });

        listeners();
        if (dataList != null && dataList.size() > 0) {
            new PhonePayQrCodeController(this, this).getPackSizeApiCall(dataList);

        }
    }

    boolean scanPay = true;


    private void listeners() {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(PaymentOptionsActivity.this, R.anim.sample_fade_in);
        activityPaymentOptionsBinding.scanToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unselectedBgClors();
                scanPay = true;
                activityPaymentOptionsBinding.scanToPay.setBackgroundResource(R.drawable.ic_payment_methods_selectebg);
                PaymentInfoLayoutsHandlings();
                activityPaymentOptionsBinding.scanToPayInfoLay.setVisibility(View.VISIBLE);
                activityPaymentOptionsBinding.scanToPayInfoLay.startAnimation(fadeInAnimation);
                Utils.showDialog(PaymentOptionsActivity.this, "Loading…");
                if (!qrCodeFirstTimeHandel) {
                    PhonePayQrCodeController phonePayQrCodeController = new PhonePayQrCodeController(getApplicationContext(), PaymentOptionsActivity.this);
                    phonePayQrCodeController.getPhonePayQrCodeGeneration(scanPay, grandTotalAmountFmcg);
                } else {
                    qrCodeData(qrCode, scanPay);
                }
            }
        });
        activityPaymentOptionsBinding.recievePaymentSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unselectedBgClors();
                activityPaymentOptionsBinding.recievePaymentSms.setBackgroundResource(R.drawable.ic_payment_methods_selectebg);
                PaymentInfoLayoutsHandlings();
                activityPaymentOptionsBinding.receivePaymentSmsInfoLay.setVisibility(View.VISIBLE);
                activityPaymentOptionsBinding.receivePaymentSmsInfoLay.startAnimation(fadeInAnimation);
            }
        });
        activityPaymentOptionsBinding.upi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanPay = false;
                unselectedBgClors();
                activityPaymentOptionsBinding.upi.setBackgroundResource(R.drawable.ic_payment_methods_selectebg);
                PaymentInfoLayoutsHandlings();
                activityPaymentOptionsBinding.upiInfoLay.setVisibility(View.VISIBLE);
                activityPaymentOptionsBinding.upiInfoLay.startAnimation(fadeInAnimation);

                activityPaymentOptionsBinding.tickPhonePay.setImageResource(0);
                activityPaymentOptionsBinding.tickPhonePayLay.setBackgroundResource(R.drawable.upi_payment_unselected_bg);
                activityPaymentOptionsBinding.tickPhonePay.setBackgroundResource(R.drawable.round_untick_bg);
                Utils.showDialog(PaymentOptionsActivity.this, "Loading…");
                if (!qrCodeFirstTimeHandel) {
                    PhonePayQrCodeController phonePayQrCodeController = new PhonePayQrCodeController(getApplicationContext(), PaymentOptionsActivity.this);
                    phonePayQrCodeController.getPhonePayQrCodeGeneration(scanPay, grandTotalAmountFmcg);
                } else {
                    qrCodeData(qrCode, scanPay);
                }
            }
        });
        activityPaymentOptionsBinding.cashOnDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unselectedBgClors();
                activityPaymentOptionsBinding.cashOnDelivery.setBackgroundResource(R.drawable.ic_payment_methods_selectebg);
                PaymentInfoLayoutsHandlings();
                activityPaymentOptionsBinding.cashOnDeliveryInfoLay.setVisibility(View.VISIBLE);
                activityPaymentOptionsBinding.cashOnDeliveryInfoLay.startAnimation(fadeInAnimation);
                if (customerDeliveryAddress != null) {
                    activityPaymentOptionsBinding.deliveryAddress.setText(customerDeliveryAddress);
                }
            }
        });
        activityPaymentOptionsBinding.backClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        activityPaymentOptionsBinding.placeAnOrderScanpayPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PaymentOptionsActivity.this, "Payment is not done", Toast.LENGTH_SHORT).show();
            }
        });
        activityPaymentOptionsBinding.placeAnOrderPhonePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PaymentOptionsActivity.this, "Payment is not done", Toast.LENGTH_SHORT).show();
            }
        });
        activityPaymentOptionsBinding.scanToPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(PaymentOptionsActivity.this, OrderinProgressActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            }
        });
        activityPaymentOptionsBinding.placeAnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityPaymentOptionsBinding.deliveryAddress.getText().toString() != null &&
                        !activityPaymentOptionsBinding.deliveryAddress.getText().toString().isEmpty()) {
//                    placeOrder();

                    if (isFmcgOrder) {
                        isFmcgOrder = false;
//                        new PhonePayQrCodeController(PaymentOptionsActivity.this, PaymentOptionsActivity.this).expressCheckoutTransactionApiCall(getExpressCheckoutTransactionApiRequest());
                        placeOrderFmcg();
                    } else {
                        isPharmaOrder = false;
                        placeOrderPharma();
                    }

                } else {
                    DeliveryAddressDialog deliveryAddressDialog = new DeliveryAddressDialog(PaymentOptionsActivity.this);
                    deliveryAddressDialog.setPositiveListener(view1 -> {
                        if (deliveryAddressDialog.validations()) {
                            customerDeliveryAddress = deliveryAddressDialog.getAddressData();
                            if (customerDeliveryAddress != null) {
                                activityPaymentOptionsBinding.deliveryAddress.setText(customerDeliveryAddress);

                                name = deliveryAddressDialog.getName();
                                singleAdd = deliveryAddressDialog.getAddress();
                                pincode = deliveryAddressDialog.getPincode();
                                city = deliveryAddressDialog.getCity();
                                state = deliveryAddressDialog.getState();

                            }
                            deliveryAddressDialog.dismiss();
                        }
                    });
                    deliveryAddressDialog.setNegativeListener(view2 -> {
                        deliveryAddressDialog.dismiss();
                    });
                    deliveryAddressDialog.show();
                }
            }
        });
        activityPaymentOptionsBinding.parentInner.setOnClickListener(v -> startActivity(new Intent(PaymentOptionsActivity.this, MySearchActivity.class)));

        paymentTicksUnticksHandlings();
    }

    private void unselectedBgClors() {
        activityPaymentOptionsBinding.scanToPay.setBackgroundResource(R.drawable.ic_payment_methods_unselectebg);
        activityPaymentOptionsBinding.recievePaymentSms.setBackgroundResource(R.drawable.ic_payment_methods_unselectebg);
        activityPaymentOptionsBinding.upi.setBackgroundResource(R.drawable.ic_payment_methods_unselectebg);
        activityPaymentOptionsBinding.cashOnDelivery.setBackgroundResource(R.drawable.ic_payment_methods_unselectebg);
    }

    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    private void PaymentInfoLayoutsHandlings() {
        activityPaymentOptionsBinding.scanToPayInfoLay.setVisibility(View.GONE);
        activityPaymentOptionsBinding.receivePaymentSmsInfoLay.setVisibility(View.GONE);
        activityPaymentOptionsBinding.upiInfoLay.setVisibility(View.GONE);
        activityPaymentOptionsBinding.cashOnDeliveryInfoLay.setVisibility(View.GONE);
    }


    private void paymentTicksUnticksHandlings() {
        activityPaymentOptionsBinding.tickBhimLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityPaymentOptionsBinding.tickBhim.setImageResource(R.drawable.tick_mark);
                activityPaymentOptionsBinding.tickBhim.setBackgroundResource(R.drawable.round_tick_bg);
                activityPaymentOptionsBinding.tickBhimLay.setBackgroundResource(R.drawable.upi_payment_selected_bg);

                activityPaymentOptionsBinding.tickUpi.setImageResource(0);
                activityPaymentOptionsBinding.tickPhonePay.setImageResource(0);


                activityPaymentOptionsBinding.tickUpiLay.setBackgroundResource(R.drawable.upi_payment_unselected_bg);
                activityPaymentOptionsBinding.tickPhonePayLay.setBackgroundResource(R.drawable.upi_payment_unselected_bg);

                activityPaymentOptionsBinding.tickUpi.setBackgroundResource(R.drawable.round_untick_bg);
                activityPaymentOptionsBinding.tickPhonePay.setBackgroundResource(R.drawable.round_untick_bg);

            }
        });
        activityPaymentOptionsBinding.tickUpiLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityPaymentOptionsBinding.tickUpi.setImageResource(R.drawable.tick_mark);
                activityPaymentOptionsBinding.tickUpi.setBackgroundResource(R.drawable.round_tick_bg);
                activityPaymentOptionsBinding.tickUpiLay.setBackgroundResource(R.drawable.upi_payment_selected_bg);

                activityPaymentOptionsBinding.tickPhonePay.setImageResource(0);
                activityPaymentOptionsBinding.tickBhim.setImageResource(0);


                activityPaymentOptionsBinding.tickBhimLay.setBackgroundResource(R.drawable.upi_payment_unselected_bg);
                activityPaymentOptionsBinding.tickPhonePayLay.setBackgroundResource(R.drawable.upi_payment_unselected_bg);

                activityPaymentOptionsBinding.tickBhim.setBackgroundResource(R.drawable.round_untick_bg);
                activityPaymentOptionsBinding.tickPhonePay.setBackgroundResource(R.drawable.round_untick_bg);
            }
        });

        activityPaymentOptionsBinding.tickPhonePayLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                scanPay = false;
//                activityPaymentOptionsBinding.tickPhonePay.setImageResource(R.drawable.tick_mark);
//                activityPaymentOptionsBinding.tickPhonePay.setBackgroundResource(R.drawable.round_tick_bg);
//                activityPaymentOptionsBinding.tickPhonePayLay.setBackgroundResource(R.drawable.upi_payment_selected_bg);
//
//                activityPaymentOptionsBinding.tickUpi.setImageResource(0);
//                activityPaymentOptionsBinding.tickBhim.setImageResource(0);
//
//                activityPaymentOptionsBinding.tickUpiLay.setBackgroundResource(R.drawable.upi_payment_unselected_bg);
//                activityPaymentOptionsBinding.tickBhimLay.setBackgroundResource(R.drawable.upi_payment_unselected_bg);
//
//                activityPaymentOptionsBinding.tickUpi.setBackgroundResource(R.drawable.round_untick_bg);
//                activityPaymentOptionsBinding.tickBhim.setBackgroundResource(R.drawable.round_untick_bg);
//
//                Utils.showDialog(PaymentOptionsActivity.this, "Loading…");
//                if (!firstQrCodePhonePay) {
//                    PhonePayQrCodeController phonePayQrCodeController = new PhonePayQrCodeController(getApplicationContext(), PaymentOptionsActivity.this);
//                    phonePayQrCodeController.getPhonePayQrCodeGeneration(scanPay, grandTotalAmount);
//                    firstQrCodePhonePay = true;
//                }
            }
        });
    }


    String qrCode;
    boolean qrCodeFirstTimeHandel;

    @Override
    public void onSuccessGetPhonePayQrCodeUpi(PhonePayQrCodeResponse phonePayQrCodeResponse, boolean scanpay) {
        qrCodeFirstTimeHandel = true;
        qrCode = (String) phonePayQrCodeResponse.getQrCode();
        qrCodeData((String) phonePayQrCodeResponse.getQrCode(), scanpay);
    }

    private void qrCodeData(String qrcode, boolean scanpay) {
        if (!scanpay) {
            activityPaymentOptionsBinding.idIVQrcode.setVisibility(View.VISIBLE);
            activityPaymentOptionsBinding.qrCodeDisplay.setVisibility(View.GONE);
        } else {
            activityPaymentOptionsBinding.qrCodeDisplay.setVisibility(View.VISIBLE);
            activityPaymentOptionsBinding.idIVQrcode.setVisibility(View.GONE);
        }
// below line is for getting
        // the windowmanager service.
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();

        // creating a variable for point which
        // is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);

        // getting width and
        // height of a point
        int width = point.x;
        int height = point.y;

        // generating dimension from width and height.
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        qrgEncoder = new QRGEncoder((String) qrcode, null, QRGContents.Type.TEXT, dimen);
        try {
            // getting our qrcode in the form of bitmap.
            bitmap = qrgEncoder.encodeAsBitmap();
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
            if (!scanpay) {
                activityPaymentOptionsBinding.idIVQrcode.setImageBitmap(bitmap);
                activityPaymentOptionsBinding.qrLogoPhonepay.setVisibility(View.VISIBLE);
                activityPaymentOptionsBinding.qrLogo.setVisibility(View.GONE);
                Utils.dismissDialog();
            } else {
                activityPaymentOptionsBinding.qrCodeDisplay.setImageBitmap(bitmap);
                activityPaymentOptionsBinding.qrLogoPhonepay.setVisibility(View.GONE);
                activityPaymentOptionsBinding.qrLogo.setVisibility(View.VISIBLE);
                Utils.dismissDialog();
            }

        } catch (WriterException e) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString());
        }
    }

    //    String fmcgOrderId;
    String pharmaOrderId;
    boolean onlineAmountPaid;

    @Override
    public void onSuccessPlaceOrderFMCG(PlaceOrderResModel body) {
        if (body.getOrdersResult().getStatus()) {
            if (!isFmcgOrder && !isPharmaOrder) {
                if (pharmaOrderId == null) {
                    pharmaOrderId = body.getOrdersResult().getOrderNo().toString();
                    paymentSuccess = false;
                    Utils.dismissDialog();
                }
                if (fmcgOrderId == null) {
                    fmcgOrderId = body.getOrdersResult().getOrderNo().toString();
                    paymentSuccess = false;
                    Utils.dismissDialog();
                }
            }
            if (isFmcgOrder) {
                pharmaOrderId = body.getOrdersResult().getOrderNo().toString();
                placeOrderFmcg();
//                new PhonePayQrCodeController(PaymentOptionsActivity.this, PaymentOptionsActivity.this).expressCheckoutTransactionApiCall(getExpressCheckoutTransactionApiRequest());
                isFmcgOrder = false;
            } else if (isPharmaOrder) {
                fmcgOrderId = body.getOrdersResult().getOrderNo().toString();
                placeOrderPharma();
                isPharmaOrder = false;
            }
            if (!isFmcgOrder && !isPharmaOrder) {
                if (pharmaOrderId != null && fmcgOrderId != null) {
                    Intent intent = new Intent(PaymentOptionsActivity.this, OrderinProgressActivity.class);
                    intent.putExtra("PharmaOrderPlacedData", pharmaOrderId);
                    intent.putExtra("FmcgOrderPlacedData", fmcgOrderId);
                    intent.putExtra("OnlineAmountPaid", onlineAmountPaid);
                    intent.putExtra("pharma_delivery_type", isPharmadeliveryType);
                    intent.putExtra("fmcg_delivery_type", isFmcgDeliveryType);
                    intent.putExtra("IS_FMCG_QR_CODE_PAYMENT", isFmcgQrCodePayment);
                    intent.putExtra("EXPRESS_CHECKOUT_TRANSACTION_ID", expressCheckoutTransactionId);
                    startActivity(intent);
                    overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
                }
            }
        } else {
            Toast.makeText(this, "Order is not Placed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailureService(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onSuccessPhonepePaymentDetails(PhonePayQrCodeResponse phonePayQrCodeResponse, String transactionId) {

        if (phonePayQrCodeResponse.getStatus()) {
            Toast.makeText(this, "Payment is successfully done", Toast.LENGTH_SHORT).show();
            onlineAmountPaid = true;
            if (isFmcgOrder) {
//                isFmcgOrder = false;
//                placeOrderFmcg();
                isFmcgQrCodePayment = true;
                new PhonePayQrCodeController(PaymentOptionsActivity.this, PaymentOptionsActivity.this).expressCheckoutTransactionApiCall(getExpressCheckoutTransactionApiRequest(phonePayQrCodeResponse, transactionId));
            } else {
                isPharmaOrder = false;
                placeOrderPharma();
            }
        } else {
            if (paymentSuccess) {
                PhonePayQrCodeController phonePayQrCodeController = new PhonePayQrCodeController(getApplicationContext(), PaymentOptionsActivity.this);
                phonePayQrCodeController.getPhonePayPaymentSuccess(transactionId, grandTotalAmountFmcg);
            }
        }
    }

    @Override
    public void onSuccessGetPackSizeApi(GetPackSizeResponse getPackSizeResponse) {
        if (getPackSizeResponse.getItemsdetails() != null && getPackSizeResponse.getItemsdetails().size() > 0) {
            for (GetPackSizeResponse.Itemsdetail itemsdetail : getPackSizeResponse.getItemsdetails()) {
                if (dataList != null && dataList.size() > 0) {
                    for (int i = 0; i < dataList.size(); i++) {
                        if (itemsdetail.getItemid().equalsIgnoreCase(dataList.get(i).getArtCode().contains(",") ? dataList.get(i).getArtCode().substring(0, dataList.get(i).getArtCode().indexOf(",")) : dataList.get(i).getArtCode())) {
                            dataList.get(i).setPack(String.valueOf(itemsdetail.getPacksize()));
                        }
                    }
                }
            }
            assert dataList != null;
            SessionManager.INSTANCE.setDataList(dataList);
        }
    }

    @Override
    public void onFailureGetPackSizeApi(String message) {
        Utils.showSnackbarDialog(this, findViewById(android.R.id.content), message);
    }

    private String expressCheckoutTransactionId = "";

    @Override
    public void onSuccessexpressCheckoutTransactionApiCall(ExpressCheckoutTransactionApiResponse expressCheckoutTransactionApiResponse) {
        if (expressCheckoutTransactionApiResponse.getRequestStatus() != null && expressCheckoutTransactionApiResponse.getRequestStatus() == 0) {
            isFmcgOrder = false;
            this.expressCheckoutTransactionId = expressCheckoutTransactionApiResponse.getTransactionId();

            if (!isFmcgOrder && !isPharmaOrder) {
                if (fmcgOrderId == null) {
                    fmcgOrderId = expressCheckoutTransactionId;
                    paymentSuccess = false;
                    Utils.dismissDialog();
                }
            }
            if (isPharmaOrder) {
//                fmcgOrderId = expressCheckoutTransactionId;
                placeOrderPharma();
                isPharmaOrder = false;
            }
            if (!isFmcgOrder && !isPharmaOrder) {
//                if (pharmaOrderId != null && fmcgOrderId != null) {
                Intent intent = new Intent(PaymentOptionsActivity.this, OrderinProgressActivity.class);
                intent.putExtra("PharmaOrderPlacedData", pharmaOrderId);
                intent.putExtra("FmcgOrderPlacedData", fmcgOrderId);
                intent.putExtra("OnlineAmountPaid", onlineAmountPaid);
                intent.putExtra("pharma_delivery_type", isPharmadeliveryType);
                intent.putExtra("fmcg_delivery_type", isFmcgDeliveryType);
                intent.putExtra("IS_FMCG_QR_CODE_PAYMENT", isFmcgQrCodePayment);
                intent.putExtra("EXPRESS_CHECKOUT_TRANSACTION_ID", expressCheckoutTransactionId);
                startActivity(intent);
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                }
            }


//            placeOrderFmcg();


        }
    }

    @Override
    public void onClickLastThreeAddresses(String selectedAdress, String phoneNumber, String postalCode, String cityLastThreeAddress, String stateLastThreeAddress, String nameLastThreeAddress, String address1, String address2, String onlyAddress) {
        dialogforAddress.dismiss();
//        DeliveryAddressDialog deliveryAddressDialog = new DeliveryAddressDialog(PaymentOptionsActivity.this);
        if (deliveryAddressDialog != null) {
            deliveryAddressDialog.setAddressforLast3Address(selectedAdress, phoneNumber, postalCode, cityLastThreeAddress, stateLastThreeAddress, nameLastThreeAddress, address1, address2, onlyAddress);
        }
    }
//        SessionManager.INSTANCE.setLast3Address(selectedAdress);
//        if (deliveryAddressDialog != null) {
//            deliveryAddressDialog.setAddressforLast3Address(selectedAdress, phoneNumber, postalCode, cityLastThreeAddress, stateLastThreeAddress, nameLastThreeAddress, address1, address2, onlyAddress);
//            if (deliveryAddressDialog.validations()) {
//                name = deliveryAddressDialog.getName();
//                singleAdd = deliveryAddressDialog.getAddress();
//                pincode = deliveryAddressDialog.getPincode();
//                city = deliveryAddressDialog.getCity();
//                state = deliveryAddressDialog.getState();
//                stateCode = deliveryAddressDialog.getStateCode();
//                mobileNumber = deliveryAddressDialog.getMobileNumber();
//                deliveryAddressDialog.dismiss();
//
//            }
//        }
//    }

    boolean paymentSuccess = true;

    @Override
    protected void onResume() {
        isPaymentActivityForTimer = "isPaymentActivity";
        super.onResume();
    }

    @Override
    protected void onPause() {
        isPaymentActivityForTimer = "";
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
        finish();
        paymentSuccess = false;
        HomeActivity.isPaymentSelectionActivity = false;
        HomeActivity.isHomeActivity = false;
        isPaymentActivityForTimer = "";
    }

    private boolean loader;

    private void placeOrderFmcg() {
        String userSelectedAdd = "";
        String selectedStateCode = "";
        UserAddress userAddress = new UserAddress();
        if (isFmcgDeliveryType) {
            userAddress.setMappingMobile(SessionManager.INSTANCE.getMobilenumber());
            userAddress.setName(name);
            userAddress.setMobile(SessionManager.INSTANCE.getMobilenumber());
            userAddress.setAddress1(singleAdd);
            userAddress.setAddress2("");
            userAddress.setAddress3("");
            userAddress.setCity(city);
            userAddress.setState(state);
            userAddress.setIsDefault(1);
            userAddress.setAddressType("");
            userAddress.setIsDeleted(0);
            userAddress.setPincode(pincode);
            userAddress.setItemSelected(true);
        } else {
            userAddress.setMappingMobile(SessionManager.INSTANCE.getMobilenumber());
            userAddress.setName(SessionManager.INSTANCE.getUseraddress().getName());
            userAddress.setMobile(SessionManager.INSTANCE.getMobilenumber());
            userAddress.setAddress1(SessionManager.INSTANCE.getUseraddress().getAddress1());
            userAddress.setAddress2("");
            userAddress.setAddress3("");
            userAddress.setCity(SessionManager.INSTANCE.getUseraddress().getCity());
            userAddress.setState("Telangana");
            userAddress.setIsDefault(1);
            userAddress.setAddressType("");
            userAddress.setIsDeleted(0);
            userAddress.setPincode(SessionManager.INSTANCE.getUseraddress().getPincode());
            userAddress.setItemSelected(true);
        }
//        UserAddress userAddress = SessionManager.INSTANCE.getUseraddress();
        if (null != userAddress.getAddress1() && userAddress.getAddress1().length() > 0) {
            String address = userAddress.getAddress1().toUpperCase() + ", " + userAddress.getCity().toUpperCase() + ", " + userAddress.getState().toUpperCase() + userAddress.getPincode();
            address = address.replace("null", "");
            userSelectedAdd = address;
        } else {
            if (null != userAddress.getCity() && null != userAddress.getState()) {
                String address = userAddress.getCity().toUpperCase() + ", " + userAddress.getState().toUpperCase() + userAddress.getPincode();
                address = address.replace("null", "");
                userSelectedAdd = address;
            }
        }


        String stateName = Utils.removeAllDigit(userAddress.getState().trim());
        for (StateCodes codes : Utils.getStoreListFromAssets(PaymentOptionsActivity.this)) {
            String codestate_name = codes.getName().trim();
            if (codestate_name.equalsIgnoreCase(stateName)) {
                selectedStateCode = codes.getCode();
            }
        }

        PlaceOrderReqModel placeOrderReqModel = new PlaceOrderReqModel();
        PlaceOrderReqModel.TpdetailsEntity tpDetailsEntity = new PlaceOrderReqModel.TpdetailsEntity();
        if (this.fmcgOrderId == null || (this.fmcgOrderId != null && this.fmcgOrderId.isEmpty())) {
            this.fmcgOrderId = Utils.getTransactionGeneratedId();
            tpDetailsEntity.setOrderId(this.fmcgOrderId);

        } else {
            tpDetailsEntity.setOrderId(this.fmcgOrderId);//Utils.getTransactionGeneratedId()
        }
        tpDetailsEntity.setShopId(SessionManager.INSTANCE.getStoreId());
        if (isFmcgDeliveryType) {
            tpDetailsEntity.setShippingMethod("HOME DELIVERY");
        } else {
            tpDetailsEntity.setShippingMethod("Pay here and Carry");
        }
        if (onlineAmountPaid) {
            tpDetailsEntity.setPaymentMethod("Online Payment");
        } else {
            tpDetailsEntity.setPaymentMethod("COD");
        }
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

        paymentDetailsEntity.setTotalAmount(String.format(Locale.US, "%.2f", grandTotalAmountFmcg));//String.valueOf(grandTotalAmountFmcg)
        if (onlineAmountPaid) {
            paymentDetailsEntity.setPaymentSource("Online Payment");
        } else {
            paymentDetailsEntity.setPaymentSource("COD");
        }
        paymentDetailsEntity.setPaymentStatus("");
        paymentDetailsEntity.setPaymentOrderId("");
        tpDetailsEntity.setPaymentDetails(paymentDetailsEntity);

        ArrayList<PlaceOrderReqModel.ItemDetailsEntity> itemDetailsArr = new ArrayList<>();
        for (int i = 0; i < SessionManager.INSTANCE.getDataList().size(); i++) {
            PlaceOrderReqModel.ItemDetailsEntity itemDetailsEntity = new PlaceOrderReqModel.ItemDetailsEntity();
            if (SessionManager.INSTANCE.getDataList().get(i).getMedicineType().equals("FMCG") || SessionManager.INSTANCE.getDataList().get(i).getMedicineType().equals("PRIVATE LABEL")) {
                itemDetailsEntity.setItemID(SessionManager.INSTANCE.getDataList().get(i).getArtCode().contains(",") ? SessionManager.INSTANCE.getDataList().get(i).getArtCode().substring(0, SessionManager.INSTANCE.getDataList().get(i).getArtCode().indexOf(",")) : SessionManager.INSTANCE.getDataList().get(i).getArtCode());
                itemDetailsEntity.setItemName(SessionManager.INSTANCE.getDataList().get(i).getArtName());
                itemDetailsEntity.setMOU(SessionManager.INSTANCE.getDataList().get(i).getQty() * Integer.parseInt(SessionManager.INSTANCE.getDataList().get(i).getPack()));
                itemDetailsEntity.setPack(String.valueOf(SessionManager.INSTANCE.getDataList().get(i).getPack()));
                itemDetailsEntity.setQty(SessionManager.INSTANCE.getDataList().get(i).getQty());
                itemDetailsEntity.setPrice(Double.parseDouble(SessionManager.INSTANCE.getDataList().get(i).getArtprice()));
                itemDetailsEntity.setStatus(true);
                itemDetailsArr.add(itemDetailsEntity);
            }
        }
        tpDetailsEntity.setItemDetails(itemDetailsArr);
        tpDetailsEntity.setDotorName("Apollo");
        tpDetailsEntity.setStateCode(selectedStateCode);
        tpDetailsEntity.setTAT("");
        tpDetailsEntity.setUserId(SessionManager.INSTANCE.getKioskSetupResponse().getKIOSK_ID());
        tpDetailsEntity.setOrderType("fmcg");
        tpDetailsEntity.setCouponCode("MED10");
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-dd");
        String strDate = formatter.format(date);
        tpDetailsEntity.setOrderDate(strDate);
        tpDetailsEntity.setRequestType("CART");
        ArrayList<PlaceOrderReqModel.PrescUrlEntity> prescEntityArray = new ArrayList<>();
        PlaceOrderReqModel.PrescUrlEntity pue = new PlaceOrderReqModel.PrescUrlEntity();
//        pue.setUrl("http://172.16.2.251:8033/ApolloKMD/apolloMedBuddy/Medibuddy/WalletCheck");
        prescEntityArray.add(pue);
        tpDetailsEntity.setPrescUrl(prescEntityArray);
        placeOrderReqModel.setTpdetails(tpDetailsEntity);
        PhonePayQrCodeController phonePayQrCodeController = new PhonePayQrCodeController(getApplicationContext(), PaymentOptionsActivity.this);
        if (!loader) {
            loader = true;
            Utils.showDialog(PaymentOptionsActivity.this, "Loading…");
        }
        phonePayQrCodeController.handleOrderPlaceService(PaymentOptionsActivity.this, placeOrderReqModel);
    }

    private void placeOrderPharma() {
        String userSelectedAdd = "";
        String selectedStateCode = "";
        UserAddress userAddress = new UserAddress();
        if (isPharmadeliveryType) {
            userAddress.setMappingMobile(SessionManager.INSTANCE.getMobilenumber());
            userAddress.setName(name);
            userAddress.setMobile(SessionManager.INSTANCE.getMobilenumber());
            userAddress.setAddress1(singleAdd);
            userAddress.setAddress2("");
            userAddress.setAddress3("");
            userAddress.setCity(city);
            userAddress.setState(state);
            userAddress.setIsDefault(1);
            userAddress.setAddressType("");
            userAddress.setIsDeleted(0);
            userAddress.setPincode(pincode);
            userAddress.setItemSelected(true);
        } else {
            userAddress.setMappingMobile(SessionManager.INSTANCE.getMobilenumber());
            userAddress.setName(SessionManager.INSTANCE.getUseraddress().getName());
            userAddress.setMobile(SessionManager.INSTANCE.getMobilenumber());
            userAddress.setAddress1(SessionManager.INSTANCE.getUseraddress().getAddress1());
            userAddress.setAddress2("");
            userAddress.setAddress3("");
            userAddress.setCity(SessionManager.INSTANCE.getUseraddress().getCity());
            userAddress.setState("Telangana");
            userAddress.setIsDefault(1);
            userAddress.setAddressType("");
            userAddress.setIsDeleted(0);
            userAddress.setPincode(SessionManager.INSTANCE.getUseraddress().getPincode());
            userAddress.setItemSelected(true);
        }
//        UserAddress userAddress = SessionManager.INSTANCE.getUseraddress();
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
        for (StateCodes codes : Utils.getStoreListFromAssets(PaymentOptionsActivity.this)) {
            String codestate_name = codes.getName().trim();
            if (codestate_name.equalsIgnoreCase(stateName)) {
                selectedStateCode = codes.getCode();
            }
        }

        PlaceOrderReqModel placeOrderReqModel = new PlaceOrderReqModel();
        PlaceOrderReqModel.TpdetailsEntity tpDetailsEntity = new PlaceOrderReqModel.TpdetailsEntity();
        tpDetailsEntity.setOrderId(Utils.getTransactionGeneratedId());
        tpDetailsEntity.setShopId(SessionManager.INSTANCE.getStoreId());
        if (isPharmadeliveryType) {
            tpDetailsEntity.setShippingMethod("HOME DELIVERY");
        } else {
            tpDetailsEntity.setShippingMethod("Pay and collect at counter");
        }
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
        paymentDetailsEntity.setTotalAmount(String.format(Locale.US, "%.2f", grandTotalAmountPharma));
        paymentDetailsEntity.setPaymentSource("COD");
        paymentDetailsEntity.setPaymentStatus("");
        paymentDetailsEntity.setPaymentOrderId("");
        tpDetailsEntity.setPaymentDetails(paymentDetailsEntity);

        ArrayList<PlaceOrderReqModel.ItemDetailsEntity> itemDetailsArr = new ArrayList<>();
        for (int i = 0; i < SessionManager.INSTANCE.getDataList().size(); i++) {
            PlaceOrderReqModel.ItemDetailsEntity itemDetailsEntity = new PlaceOrderReqModel.ItemDetailsEntity();
            if (SessionManager.INSTANCE.getDataList().get(i).getMedicineType().equals("PHARMA")) {
                itemDetailsEntity.setItemID(SessionManager.INSTANCE.getDataList().get(i).getArtCode().contains(",") ? SessionManager.INSTANCE.getDataList().get(i).getArtCode().substring(0, SessionManager.INSTANCE.getDataList().get(i).getArtCode().indexOf(",")) : SessionManager.INSTANCE.getDataList().get(i).getArtCode());
                itemDetailsEntity.setItemName(SessionManager.INSTANCE.getDataList().get(i).getArtName());
                itemDetailsEntity.setMOU(SessionManager.INSTANCE.getDataList().get(i).getQty() * Integer.parseInt(SessionManager.INSTANCE.getDataList().get(i).getPack()));
                itemDetailsEntity.setPack(String.valueOf(SessionManager.INSTANCE.getDataList().get(i).getPack()));
                itemDetailsEntity.setQty(SessionManager.INSTANCE.getDataList().get(i).getQty());
                itemDetailsEntity.setPrice(Double.parseDouble(SessionManager.INSTANCE.getDataList().get(i).getArtprice()));
                itemDetailsEntity.setStatus(true);
                itemDetailsArr.add(itemDetailsEntity);
            }
        }
        tpDetailsEntity.setItemDetails(itemDetailsArr);
        tpDetailsEntity.setDotorName("Apollo");
        tpDetailsEntity.setStateCode(selectedStateCode);
        tpDetailsEntity.setTAT("");
        tpDetailsEntity.setUserId(SessionManager.INSTANCE.getKioskSetupResponse().getKIOSK_ID());
        tpDetailsEntity.setOrderType("Pharma");
        tpDetailsEntity.setCouponCode("MED10");
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-dd");
        String strDate = formatter.format(date);
        tpDetailsEntity.setOrderDate(strDate);
        tpDetailsEntity.setRequestType("CART");
        ArrayList<PlaceOrderReqModel.PrescUrlEntity> prescEntityArray = new ArrayList<>();
        PlaceOrderReqModel.PrescUrlEntity pue = new PlaceOrderReqModel.PrescUrlEntity();
        prescEntityArray.add(pue);
        tpDetailsEntity.setPrescUrl(prescEntityArray);
        placeOrderReqModel.setTpdetails(tpDetailsEntity);
        PhonePayQrCodeController phonePayQrCodeController = new PhonePayQrCodeController(getApplicationContext(), PaymentOptionsActivity.this);
        if (!loader) {
            loader = true;
            Utils.showDialog(PaymentOptionsActivity.this, "Loading…");
        }
        phonePayQrCodeController.handleOrderPlaceService(PaymentOptionsActivity.this, placeOrderReqModel);
    }

    public class OrderDetailsuiModel {
        private String pharmaCount;
        private String fmcgCount;
        private String fmcgTotal;
        private String pharmaTotal;
        private String pharmaTotalOffer;
        private String fmcgTotalOffer;
        private String totalMedicineCount;
        private String medicineTotal;
        private boolean isFmcgPharma;
        private boolean isFmcg;
        private boolean isPharma;
        private boolean isPharmaHomeDelivery;
        private boolean isFmcgHomeDelivery;

        public String getPharmaCount() {
            return pharmaCount;
        }

        public void setPharmaCount(String pharmaCount) {
            this.pharmaCount = pharmaCount;
        }

        public String getFmcgCount() {
            return fmcgCount;
        }

        public void setFmcgCount(String fmcgCount) {
            this.fmcgCount = fmcgCount;
        }

        public String getFmcgTotal() {
            return fmcgTotal;
        }

        public void setFmcgTotal(String fmcgTotal) {
            this.fmcgTotal = fmcgTotal;
        }

        public String getPharmaTotal() {
            return pharmaTotal;
        }

        public void setPharmaTotal(String pharmaTotal) {
            this.pharmaTotal = pharmaTotal;
        }

        public String getPharmaTotalOffer() {
            return pharmaTotalOffer;
        }

        public void setPharmaTotalOffer(String pharmaTotalOffer) {
            this.pharmaTotalOffer = pharmaTotalOffer;
        }

        public String getFmcgTotalOffer() {
            return fmcgTotalOffer;
        }

        public void setFmcgTotalOffer(String fmcgTotalOffer) {
            this.fmcgTotalOffer = fmcgTotalOffer;
        }

        public String getTotalMedicineCount() {
            return totalMedicineCount;
        }

        public void setTotalMedicineCount(String totalMedicineCount) {
            this.totalMedicineCount = totalMedicineCount;
        }

        public String getMedicineTotal() {
            return medicineTotal;
        }

        public void setMedicineTotal(String medicineTotal) {
            this.medicineTotal = medicineTotal;
        }

        public boolean isFmcgPharma() {
            return isFmcgPharma;
        }

        public void setFmcgPharma(boolean fmcgPharma) {
            isFmcgPharma = fmcgPharma;
        }

        public boolean isFmcg() {
            return isFmcg;
        }

        public void setFmcg(boolean fmcg) {
            isFmcg = fmcg;
        }

        public boolean isPharma() {
            return isPharma;
        }

        public void setPharma(boolean pharma) {
            isPharma = pharma;
        }

        public boolean isPharmaHomeDelivery() {
            return isPharmaHomeDelivery;
        }

        public void setPharmaHomeDelivery(boolean pharmaHomeDelivery) {
            isPharmaHomeDelivery = pharmaHomeDelivery;
        }

        public boolean isFmcgHomeDelivery() {
            return isFmcgHomeDelivery;
        }

        public void setFmcgHomeDelivery(boolean fmcgHomeDelivery) {
            isFmcgHomeDelivery = fmcgHomeDelivery;
        }
    }


    public ExpressCheckoutTransactionApiRequest getExpressCheckoutTransactionApiRequest(PhonePayQrCodeResponse phonePayQrCodeResponse, String transactionId) {
        ExpressCheckoutTransactionApiRequest expressCheckoutTransactionApiRequest = new ExpressCheckoutTransactionApiRequest();
        expressCheckoutTransactionApiRequest.setRemainingamount(0);
        expressCheckoutTransactionApiRequest.setAvailablePoint(0);
        expressCheckoutTransactionApiRequest.setIsPickPackOrder(false);
        expressCheckoutTransactionApiRequest.setIsHDOrder(false);
        expressCheckoutTransactionApiRequest.setTimeSlot("1900-01-01");
        expressCheckoutTransactionApiRequest.setAmazonPrintStatus(null);
        expressCheckoutTransactionApiRequest.setIsTPASeller(false);
        expressCheckoutTransactionApiRequest.setDonationAmount(0);
        expressCheckoutTransactionApiRequest.setIsBulkDiscount(false);
        expressCheckoutTransactionApiRequest.setReturnRequestId("");
        expressCheckoutTransactionApiRequest.setIsOMSJurnalsScreen(false);
        expressCheckoutTransactionApiRequest.setISOMSReturn(false);
        expressCheckoutTransactionApiRequest.setRiderMobile("");
        expressCheckoutTransactionApiRequest.setRiderName("");
        expressCheckoutTransactionApiRequest.setRiderCode("");
        expressCheckoutTransactionApiRequest.setDspName("");
        expressCheckoutTransactionApiRequest.setRevReturnOtp("");
        expressCheckoutTransactionApiRequest.setPickupOtp("");
        expressCheckoutTransactionApiRequest.setFwdReturnOtp("");
        expressCheckoutTransactionApiRequest.setRTOStatus(false);
        expressCheckoutTransactionApiRequest.setPickupStatus(false);
        expressCheckoutTransactionApiRequest.setTier("");
        expressCheckoutTransactionApiRequest.setCustomerType("");
        expressCheckoutTransactionApiRequest.setStockStatus("");
        expressCheckoutTransactionApiRequest.setIsUHIDBilling(false);
        expressCheckoutTransactionApiRequest.setHCOfferCode("");
        expressCheckoutTransactionApiRequest.setDiscountStatus(0);
        expressCheckoutTransactionApiRequest.setDiscountReferenceID("");
        expressCheckoutTransactionApiRequest.setISOnlineOrder(false);
        expressCheckoutTransactionApiRequest.setISCancelled(false);
        expressCheckoutTransactionApiRequest.setVendorCode("");
        expressCheckoutTransactionApiRequest.setISReserved(false);
        expressCheckoutTransactionApiRequest.setISBulkBilling(false);
        expressCheckoutTransactionApiRequest.setDeliveryDate("1900-01-01");
        expressCheckoutTransactionApiRequest.setOrderType("");
        expressCheckoutTransactionApiRequest.setOrderSource("");
        expressCheckoutTransactionApiRequest.setShippingMethod("");
        expressCheckoutTransactionApiRequest.setShippingMethodDesc("");
        expressCheckoutTransactionApiRequest.setBillingCity("");
        expressCheckoutTransactionApiRequest.setVendorId("");
        expressCheckoutTransactionApiRequest.setPaymentSource("");
        expressCheckoutTransactionApiRequest.setISPrescibeDiscount(false);
        expressCheckoutTransactionApiRequest.setCancelReasonCode("");
        expressCheckoutTransactionApiRequest.setStoreName("");
        expressCheckoutTransactionApiRequest.setRegionCode("");
        expressCheckoutTransactionApiRequest.setCustomerID("");
        expressCheckoutTransactionApiRequest.setDob("");
        expressCheckoutTransactionApiRequest.setCustAddress("");
        expressCheckoutTransactionApiRequest.setCustomerState("");
        expressCheckoutTransactionApiRequest.setGender(0);
        expressCheckoutTransactionApiRequest.setSalesOrigin("");
        expressCheckoutTransactionApiRequest.setRefno("");
        expressCheckoutTransactionApiRequest.setIpno("");
        expressCheckoutTransactionApiRequest.setIPSerialNO("");
        expressCheckoutTransactionApiRequest.setReciptId("");
        expressCheckoutTransactionApiRequest.setBatchTerminalid("");
        expressCheckoutTransactionApiRequest.setBusinessDate("");
        expressCheckoutTransactionApiRequest.setChannel("");
        expressCheckoutTransactionApiRequest.setComment("");
        expressCheckoutTransactionApiRequest.setCreatedonPosTerminal("");
        expressCheckoutTransactionApiRequest.setCurrency("");
        expressCheckoutTransactionApiRequest.setCustAccount("");
        expressCheckoutTransactionApiRequest.setCustDiscamount(0);
        expressCheckoutTransactionApiRequest.setDiscAmount(0);
        expressCheckoutTransactionApiRequest.setEntryStatus(0);
        expressCheckoutTransactionApiRequest.setGrossAmount(0);
        expressCheckoutTransactionApiRequest.setNetAmount(0);
        expressCheckoutTransactionApiRequest.setNetAmountInclTax(0);
        expressCheckoutTransactionApiRequest.setNumberofItemLines(0);
        expressCheckoutTransactionApiRequest.setNumberofItems(0);
        expressCheckoutTransactionApiRequest.setRoundedAmount(0);
        expressCheckoutTransactionApiRequest.setReturnStore("");
        expressCheckoutTransactionApiRequest.setReturnTerminal("");
        expressCheckoutTransactionApiRequest.setReturnTransactionId("");
        expressCheckoutTransactionApiRequest.setReturnReceiptId("");
        expressCheckoutTransactionApiRequest.setTimewhenTransClosed(0);
        expressCheckoutTransactionApiRequest.setTotalDiscAmount(0);
        expressCheckoutTransactionApiRequest.setTotalManualDiscountAmount(0);
        expressCheckoutTransactionApiRequest.setTotalManualDiscountPercentage(0);
        expressCheckoutTransactionApiRequest.setTotalMRP(0);
        expressCheckoutTransactionApiRequest.setTotalTaxAmount(0);
        expressCheckoutTransactionApiRequest.setTransactionId("");
        expressCheckoutTransactionApiRequest.setTransDate("");
        expressCheckoutTransactionApiRequest.setType(0);
        expressCheckoutTransactionApiRequest.setIsVoid(false);
        expressCheckoutTransactionApiRequest.setIsReturn(false);
        expressCheckoutTransactionApiRequest.setISBatchModifiedAllowed(false);
        expressCheckoutTransactionApiRequest.setISReturnAllowed(false);
        expressCheckoutTransactionApiRequest.setIsManualBill(false);
        expressCheckoutTransactionApiRequest.setReturnType(0);
        expressCheckoutTransactionApiRequest.setCurrentSalesLine(0);
        expressCheckoutTransactionApiRequest.setRequestStatus(0);
        expressCheckoutTransactionApiRequest.setReturnMessage("");
        expressCheckoutTransactionApiRequest.setPosEvent(0);
        expressCheckoutTransactionApiRequest.setTransType(0);
        expressCheckoutTransactionApiRequest.setISPosted(false);
        expressCheckoutTransactionApiRequest.setSez(0);
        expressCheckoutTransactionApiRequest.setCouponCode("");
        expressCheckoutTransactionApiRequest.setISAdvancePayment(false);
        expressCheckoutTransactionApiRequest.setAmounttoAccount(0);
        expressCheckoutTransactionApiRequest.setReminderDays(0);
        expressCheckoutTransactionApiRequest.setISOMSOrder(false);
        expressCheckoutTransactionApiRequest.setISHBPStore(false);
        expressCheckoutTransactionApiRequest.setPatientID("");
        expressCheckoutTransactionApiRequest.setApprovedID("");
        expressCheckoutTransactionApiRequest.setDiscountRef("");
        expressCheckoutTransactionApiRequest.setAWBNo("");
        expressCheckoutTransactionApiRequest.setDSPCode("");
        expressCheckoutTransactionApiRequest.setISHyperLocalDelivery(false);
        expressCheckoutTransactionApiRequest.setISHyperDelivered(false);
        expressCheckoutTransactionApiRequest.setCreatedDateTime("1900-01-01");
        expressCheckoutTransactionApiRequest.setOMSCreditAmount(0);
        expressCheckoutTransactionApiRequest.setISOMSValidate(false);
        expressCheckoutTransactionApiRequest.setAllowedTenderType("");
        expressCheckoutTransactionApiRequest.setShippingCharges(0);
        expressCheckoutTransactionApiRequest.setAgeGroup("");


        expressCheckoutTransactionApiRequest.setCorpCode("8860");
        expressCheckoutTransactionApiRequest.setMobileNO(mobileNumber);
        expressCheckoutTransactionApiRequest.setCustomerName(name);
        expressCheckoutTransactionApiRequest.setDoctorName("APOLLO");
        expressCheckoutTransactionApiRequest.setDoctorCode("0");
        this.fmcgOrderId = Utils.getTransactionGeneratedId();
        expressCheckoutTransactionApiRequest.setTrackingRef(this.fmcgOrderId);
        expressCheckoutTransactionApiRequest.setStaff("System");
        expressCheckoutTransactionApiRequest.setStore(SessionManager.INSTANCE.getStoreId());
        expressCheckoutTransactionApiRequest.setState(stateCode);
        expressCheckoutTransactionApiRequest.setTerminal(SessionManager.INSTANCE.getTerminalId());
        expressCheckoutTransactionApiRequest.setDataAreaId(SessionManager.INSTANCE.getDataAreaId());
        expressCheckoutTransactionApiRequest.setIsStockCheck(true);
        expressCheckoutTransactionApiRequest.setExpiryDays(30);

        List<ExpressCheckoutTransactionApiRequest.SalesLine> salesLineList = new ArrayList<>();
        for (int i = 0; i < SessionManager.INSTANCE.getDataList().size(); i++) {
            ExpressCheckoutTransactionApiRequest.SalesLine salesLine = new ExpressCheckoutTransactionApiRequest.SalesLine();
            if (SessionManager.INSTANCE.getDataList().get(i).getMedicineType().equals("FMCG") || SessionManager.INSTANCE.getDataList().get(i).getMedicineType().equals("PRIVATE LABEL")) {
                salesLine.setReasonCode("");
                salesLine.setPriceVariation(false);
                salesLine.setQCPass(false);
                salesLine.setQCFail(false);
                salesLine.setQCStatus(0);
                salesLine.setQCDate("");
                salesLine.setQCRemarks("");
                salesLine.setAlternetItemID("");
                salesLine.setItemName("");
                salesLine.setCategory("");
                salesLine.setCategoryCode("");
                salesLine.setSubCategory("");
                salesLine.setSubCategoryCode("");
                salesLine.setScheduleCategory("");
                salesLine.setScheduleCategoryCode("");
                salesLine.setManufacturerCode("");
                salesLine.setManufacturerName("");
                salesLine.setExpiry("01-Jan-1900");
                salesLine.setStockQty(0);
                salesLine.setReturnQty(0);
                salesLine.setRemainingQty(0);
                salesLine.setTax(0);
                salesLine.setAdditionaltax(0);
                salesLine.setBarcode("");
                salesLine.setComment("");
                salesLine.setDiscOfferId("");
                salesLine.setHsncodeIn("");
                salesLine.setInventBatchId("");
                salesLine.setPreviewText("");
                salesLine.setLinedscAmount(0);
                salesLine.setLineManualDiscountAmount(0);
                salesLine.setLineManualDiscountPercentage(0);
                salesLine.setNetAmount(0);
                salesLine.setNetAmountInclTax(0);
                salesLine.setOriginalPrice(0);
                salesLine.setPeriodicDiscAmount(0);
                salesLine.setPrice(0);
                salesLine.setTaxAmount(0);
                salesLine.setBaseAmount(0);
                salesLine.setTotalRoundedAmount(0);
                salesLine.setUnit("");
                salesLine.setVariantId("");
                salesLine.setTotal(0);
                salesLine.setISPrescribed(0);
                salesLine.setRemainderDays(0);
                salesLine.setIsVoid(false);
                salesLine.setIsPriceOverride(false);
                salesLine.setIsChecked(false);
                salesLine.setRetailCategoryRecID("");
                salesLine.setRetailSubCategoryRecID("");
                salesLine.setRetailMainCategoryRecID("");
                salesLine.setDpco(false);
                salesLine.setProductRecID("");
                salesLine.setModifyBatchId("");
                salesLine.setDiseaseType("");
                salesLine.setClassification("");
                salesLine.setSubClassification("");
                salesLine.setOfferQty(0);
                salesLine.setOfferAmount(0);
                salesLine.setOfferDiscountType(0);
                salesLine.setOfferDiscountValue(0);
                salesLine.setDiscountType("");
                salesLine.setMixMode(false);
                salesLine.setMMGroupId("");
                salesLine.setOfferType(0);
                salesLine.setApplyDiscount(false);
                salesLine.setIGSTPerc(0);
                salesLine.setCESSPerc(0);
                salesLine.setCGSTPerc(0);
                salesLine.setSGSTPerc(0);
                salesLine.setTotalTax(0);
                salesLine.setIGSTTaxCode(null);
                salesLine.setCESSTaxCode(null);
                salesLine.setCGSTTaxCode(null);
                salesLine.setSGSTTaxCode(null);
                salesLine.setDiscountStructureType(0);
                salesLine.setSubstitudeItemId("");
                salesLine.setCategoryReference("");
                salesLine.setOrderStatus(0);
                salesLine.setOmsLineID(0);
                salesLine.setIsSubsitute(false);
                salesLine.setIsGeneric(false);
                salesLine.setOmsLineRECID(0);
                salesLine.setISReserved(false);
                salesLine.setISStockAvailable(false);
                salesLine.setISRestricted(false);
                salesLine.setPhysicalBatchID(null);
                salesLine.setPhysicalMRP(0);
                salesLine.setPhysicalExpiry(null);


                salesLine.setLineNo(i + 1);
                salesLine.setItemId(SessionManager.INSTANCE.getDataList().get(i).getArtCode().contains(",") ? SessionManager.INSTANCE.getDataList().get(i).getArtCode().substring(0, SessionManager.INSTANCE.getDataList().get(i).getArtCode().indexOf(",")) : SessionManager.INSTANCE.getDataList().get(i).getArtCode());
                salesLine.setItemName(SessionManager.INSTANCE.getDataList().get(i).getArtName());
                salesLine.setQty(SessionManager.INSTANCE.getDataList().get(i).getQty());
                salesLine.setMrp(Double.parseDouble(SessionManager.INSTANCE.getDataList().get(i).getArtprice()));
                salesLine.setDiscAmount(0.0);
                salesLine.setTotalDiscAmount(0.0);
                salesLine.setTotalDiscPct(0);
                salesLine.setInventBatchId(SessionManager.INSTANCE.getDataList().get(i).getArtCode().contains(",") ? SessionManager.INSTANCE.getDataList().get(i).getArtCode().substring(SessionManager.INSTANCE.getDataList().get(i).getArtCode().indexOf(",") + 1) : "");
                salesLine.setUnitPrice(Double.parseDouble(SessionManager.INSTANCE.getDataList().get(i).getArtprice()));
                salesLine.setUnitQty(SessionManager.INSTANCE.getDataList().get(i).getQty());
                salesLine.setDiscId("");//EXPRESSCHECKOUT
                salesLine.setLineDiscPercentage(0);


                salesLineList.add(salesLine);
            }
        }

        expressCheckoutTransactionApiRequest.setSalesLine(salesLineList);

        List<ExpressCheckoutTransactionApiRequest.TenderLine> tenderLineList = new ArrayList<>();
        ExpressCheckoutTransactionApiRequest.TenderLine tenderLine = new ExpressCheckoutTransactionApiRequest.TenderLine();
        tenderLine.setLineNo(1);
        tenderLine.setTenderId(32);
        tenderLine.setTenderType(5);
        tenderLine.setTenderName("QR CODE");
        tenderLine.setExchRate(0);
        tenderLine.setExchRateMst(0);
        tenderLine.setMobileNo("");
        tenderLine.setWalletType(0);
        tenderLine.setWalletOrderId(phonePayQrCodeResponse.getProviderReferenceId());
        tenderLine.setWalletTransactionID(transactionId);
        tenderLine.setRewardsPoint(0);
        tenderLine.setPreviewText("");
        tenderLine.setIsVoid(false);
        tenderLine.setBarCode("");


        tenderLine.setAmountTendered(grandTotalAmountFmcg);
        tenderLine.setAmountCur(grandTotalAmountFmcg);
        tenderLine.setAmountMst(grandTotalAmountFmcg);
        tenderLineList.add(tenderLine);

        expressCheckoutTransactionApiRequest.setTenderLine(tenderLineList);

        return expressCheckoutTransactionApiRequest;
    }
}