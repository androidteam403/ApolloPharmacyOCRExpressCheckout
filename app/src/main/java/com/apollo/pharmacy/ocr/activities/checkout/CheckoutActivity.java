package com.apollo.pharmacy.ocr.activities.checkout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.BaseActivity;
import com.apollo.pharmacy.ocr.activities.HomeActivity;
import com.apollo.pharmacy.ocr.activities.MapViewActivity;
import com.apollo.pharmacy.ocr.activities.paymentoptions.PaymentOptionsActivity;
import com.apollo.pharmacy.ocr.activities.paymentoptions.model.ExpressCheckoutTransactionApiRequest;
import com.apollo.pharmacy.ocr.activities.paymentoptions.model.ExpressCheckoutTransactionApiResponse;
import com.apollo.pharmacy.ocr.adapters.LastThreeAddressAdapter;
import com.apollo.pharmacy.ocr.databinding.ActivityCheckoutBinding;
import com.apollo.pharmacy.ocr.databinding.DialogForLast3addressBinding;
import com.apollo.pharmacy.ocr.databinding.DialogPharmaItemContainAlertBinding;
import com.apollo.pharmacy.ocr.databinding.DialogStockavailableAlertBinding;
import com.apollo.pharmacy.ocr.dialog.DeliveryAddressDialog;
import com.apollo.pharmacy.ocr.model.OCRToDigitalMedicineResponse;
import com.apollo.pharmacy.ocr.model.RecallAddressModelRequest;
import com.apollo.pharmacy.ocr.model.RecallAddressResponse;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends BaseActivity implements CheckoutListener {
    private ActivityCheckoutBinding activityCheckoutBinding;
    private List<OCRToDigitalMedicineResponse> dataList;
    private boolean isPharmaHomeDelivery = false;
    private boolean isFmcgHomeDelivery = false;
    private boolean isFmcgProductsThere = false;
    private boolean isPharmaProductsThere = false;
    private String fmcgOrderId;
    private boolean addressLatLng = false;
    //    private double grandTotalAmountFmcg = 0.0;
    private double fmcgToatalPass = 0.0;
    private String expressCheckoutTransactionId;
    DeliveryAddressDialog deliveryAddressDialog;
    private boolean nameEntered = false;
    Dialog dialogforAddress;
    private String mappingLat;
    private String mappingLong;
    private static final int MAP_VIEW_ACTIVITY = 314;
    private ArrayList<String> last3Address = new ArrayList<>();




//    public static Intent getStartIntent(Context context, List<OCRToDigitalMedicineResponse> dataList) {
//        Intent intent = new Intent(context, CheckoutActivity.class);
//        intent.putExtra("dataList", (Serializable) dataList);
//        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        return intent;
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCheckoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_checkout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        HomeActivity.isPaymentSelectionActivity = false;
        HomeActivity.isHomeActivity = false;
        activityCheckoutBinding.setCallback(this);
        setUp();

    }


    private void setUp() {
        activityCheckoutBinding.pharmaTotalInclOffer.setPaintFlags(activityCheckoutBinding.pharmaTotalInclOffer.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        activityCheckoutBinding.fmcgTotalInclOffer.setPaintFlags(activityCheckoutBinding.fmcgTotalInclOffer.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        dataList = SessionManager.INSTANCE.getDataList();
//        pharmaItemsContainsAlert();
        RecallAddressModelRequest recallAddressModelRequest = new RecallAddressModelRequest();
        recallAddressModelRequest.setMobileNo(SessionManager.INSTANCE.getMobilenumber());
        recallAddressModelRequest.setStoreId(SessionManager.INSTANCE.getStoreId());
        recallAddressModelRequest.setUrl("");
        recallAddressModelRequest.setDataAreaID(SessionManager.INSTANCE.getDataAreaId());

        new CheckoutActivityController(this, this).getOMSCallPunchingAddressList(recallAddressModelRequest);
        if (dataList != null && dataList.size() > 0) {
//            dataList = (List<OCRToDigitalMedicineResponse>) getIntent().getSerializableExtra("dataList");
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
                            isPharmaProductsThere = true;
                            if (data.getNetAmountInclTax() != null && !data.getNetAmountInclTax().isEmpty()) {
                                pharmaTotal = pharmaTotal + (Double.parseDouble(data.getNetAmountInclTax()));
                                pharmaTotalOffer = pharmaTotalOffer + (Double.parseDouble(data.getArtprice()) * data.getQty());
                            } else {
                                pharmaTotal = pharmaTotal + (Double.parseDouble(data.getArtprice()) * data.getQty());
                                pharmaTotalOffer = pharmaTotalOffer + (Double.parseDouble(data.getArtprice()) * data.getQty());
                            }
                        } else {
                            isFmcg = true;
                            isFmcgProductsThere = true;
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


                fmcgToatalPass = fmcgTotal;
                CheckoutuiModel checkoutuiModel = new CheckoutuiModel();

                checkoutuiModel.setPharmaCount(String.valueOf(pharmaMedicineCount));
                checkoutuiModel.setFmcgCount(String.valueOf(fmcgMedicineCount));
                DecimalFormat formatter = new DecimalFormat("#,###.00");
                String pharmaformatted = formatter.format(pharmaTotal);
                String fmcgFormatted = formatter.format(fmcgTotal);
                String pharmaOfferformatted = formatter.format(pharmaTotalOffer);
                String fmcgOfferFormatted = formatter.format(fmcgTotalOffer);
                checkoutuiModel.setPharmaTotalOffer(getResources().getString(R.string.rupee) + pharmaOfferformatted);
                checkoutuiModel.setFmcgTotalOffer(getResources().getString(R.string.rupee) + fmcgOfferFormatted);
                checkoutuiModel.setPharmaTotal(getResources().getString(R.string.rupee) + pharmaformatted);
                checkoutuiModel.setFmcgTotal(getResources().getString(R.string.rupee) + fmcgFormatted);
                checkoutuiModel.setTotalMedicineCount(String.valueOf(dataList.size()));
                String totalprodAmt = formatter.format(pharmaTotal + fmcgTotal);
                checkoutuiModel.setMedicineTotal(getResources().getString(R.string.rupee) + String.valueOf(totalprodAmt));
                checkoutuiModel.setFmcgPharma(isPharma && isFmcg);
                checkoutuiModel.setFmcg(isFmcg);
                checkoutuiModel.setPharma(isPharma);
                activityCheckoutBinding.setModel(checkoutuiModel);

            }
        }

        activityCheckoutBinding.reviewCartPharma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
            }
        });
        activityCheckoutBinding.reviewCartFmcg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
            }
        });
    }

    String address;
    String name, singleAdd, pincode, city, state, stateCode, mobileNumber;

    @Override
    public void onClickNeedHomeDelivery() {
//        deliveryModeHandle();
        isPharmaHomeDelivery = true;
        activityCheckoutBinding.payandCollectatCounter.setBackground(getResources().getDrawable(R.drawable.bg_lite_grey));
        activityCheckoutBinding.payandCollectatCounterText.setTextColor(getResources().getColor(R.color.grey_color));
        activityCheckoutBinding.payandCollectatCounterImg.setImageDrawable(getResources().getDrawable(R.drawable.tick_white));

        activityCheckoutBinding.needHomeDelivery.setBackground(getResources().getDrawable(R.drawable.blackstroke_yellowsolid));
        activityCheckoutBinding.needHomeDeliveryText.setTextColor(getResources().getColor(R.color.black));
        activityCheckoutBinding.needHomeDeliveryImg.setImageDrawable(getResources().getDrawable(R.drawable.tick_green));

//        if (address == null) {
//            DeliveryAddressDialog deliveryAddressDialog = new DeliveryAddressDialog(CheckoutActivity.this);
//            deliveryAddressDialog.setPositiveListener(view -> {
//                if (deliveryAddressDialog.validations()) {
//                    address = deliveryAddressDialog.getAddressData();
//                    name = deliveryAddressDialog.getName();
//                    singleAdd = deliveryAddressDialog.getAddress();
//                    pincode = deliveryAddressDialog.getPincode();
//                    city = deliveryAddressDialog.getCity();
//                    state = deliveryAddressDialog.getState();
//                    deliveryAddressDialog.dismiss();
//                }
//            });
//            deliveryAddressDialog.setNegativeListener(view -> {
//                deliveryAddressDialog.dismiss();
//            });
//            deliveryAddressDialog.show();
//        }
    }

    @Override
    public void onClickPayandCollectatCounter() {
//        deliveryModeHandle();
        isPharmaHomeDelivery = false;
        activityCheckoutBinding.needHomeDelivery.setBackground(getResources().getDrawable(R.drawable.bg_lite_grey));
        activityCheckoutBinding.needHomeDeliveryText.setTextColor(getResources().getColor(R.color.grey_color));
        activityCheckoutBinding.needHomeDeliveryImg.setImageDrawable(getResources().getDrawable(R.drawable.tick_white));

        activityCheckoutBinding.payandCollectatCounter.setBackground(getResources().getDrawable(R.drawable.blackstroke_yellowsolid));
        activityCheckoutBinding.payandCollectatCounterText.setTextColor(getResources().getColor(R.color.black));
        activityCheckoutBinding.payandCollectatCounterImg.setImageDrawable(getResources().getDrawable(R.drawable.tick_green));

    }

    @Override
    public void onClickNeedHomeDelivery1() {
//        deliveryModeHandle();
        isFmcgHomeDelivery = true;
        activityCheckoutBinding.payhereandCarry.setBackground(getResources().getDrawable(R.drawable.bg_lite_grey));
        activityCheckoutBinding.payHereAndcarryText.setTextColor(getResources().getColor(R.color.grey_color));
        activityCheckoutBinding.payHereAndcarryImg.setImageDrawable(getResources().getDrawable(R.drawable.tick_white));

        activityCheckoutBinding.needHomeDelivery1.setBackground(getResources().getDrawable(R.drawable.blackstroke_yellowsolid));
        activityCheckoutBinding.needHomeDelivery1Text.setTextColor(getResources().getColor(R.color.black));
        activityCheckoutBinding.needHomeDelivery1Img.setImageDrawable(getResources().getDrawable(R.drawable.tick_green));
//        if (address == null) {
//            DeliveryAddressDialog deliveryAddressDialog = new DeliveryAddressDialog(CheckoutActivity.this);
//            deliveryAddressDialog.setPositiveListener(view -> {
//                if (deliveryAddressDialog.validations()) {
//                    address = deliveryAddressDialog.getAddressData();
//                    name = deliveryAddressDialog.getName();
//                    singleAdd = deliveryAddressDialog.getAddress();
//                    pincode = deliveryAddressDialog.getPincode();
//                    city = deliveryAddressDialog.getCity();
//                    state = deliveryAddressDialog.getState();
//                    deliveryAddressDialog.dismiss();
//                }
//            });
//            deliveryAddressDialog.setNegativeListener(view -> {
//                deliveryAddressDialog.dismiss();
//            });
//            deliveryAddressDialog.show();
//        }
    }

    @Override
    public void onClickPayhereandCarry() {
//        deliveryModeHandle();
        isFmcgHomeDelivery = false;
        activityCheckoutBinding.needHomeDelivery1.setBackground(getResources().getDrawable(R.drawable.bg_lite_grey));
        activityCheckoutBinding.needHomeDelivery1Text.setTextColor(getResources().getColor(R.color.grey_color));
        activityCheckoutBinding.needHomeDelivery1Img.setImageDrawable(getResources().getDrawable(R.drawable.tick_white));

        activityCheckoutBinding.payhereandCarry.setBackground(getResources().getDrawable(R.drawable.blackstroke_yellowsolid));
        activityCheckoutBinding.payHereAndcarryText.setTextColor(getResources().getColor(R.color.black));
        activityCheckoutBinding.payHereAndcarryImg.setImageDrawable(getResources().getDrawable(R.drawable.tick_green));
    }

    @Override
    public void onClickBack() {
        onBackPressed();
        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       HomeActivity.isLoggedin=true;
        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
    }

    boolean isOverAllHomeDelivery = false;

    @Override
    public void onClickPaynow() {

        if (isFmcgHomeDelivery || isPharmaHomeDelivery) {
            isOverAllHomeDelivery = true;

        } else {
            isOverAllHomeDelivery = false;
        }


        if (recallAddressResponses.getCustomerDetails().size() > 0 && isOverAllHomeDelivery) {
//            deliveryAddressDialog.continueButtonGone();

            dialogforAddress = new Dialog(this);
            DialogForLast3addressBinding dialogForLast3addressBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_for_last3address, null, true);
            dialogforAddress.setContentView(dialogForLast3addressBinding.getRoot());
            if (dialogforAddress.getWindow() != null)
                dialogforAddress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogforAddress.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            dialogforAddress.setCancelable(false);

            dialogforAddress.show();
            
            dialogForLast3addressBinding.closeAddressDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogforAddress.dismiss();
                }
            });
//        Toast.makeText(getApplicationContext(), ""+recallAddressResponse.getCustomerDetails().size(), Toast.LENGTH_SHORT).show();

            dialogForLast3addressBinding.parentLayoutForTimer.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                   delayedIdle(SessionManager.INSTANCE.getSessionTime());
                    return false;
                }
            });

            dialogForLast3addressBinding.last3addressRecyclerView.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    delayedIdle(SessionManager.INSTANCE.getSessionTime());
                    return false;
                }
            });
            dialogForLast3addressBinding.dialogButtonAddAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogforAddress.dismiss();

                    deliveryAddressDialog = new DeliveryAddressDialog(CheckoutActivity.this,CheckoutActivity.this, null);
                    deliveryAddressDialog.reCallAddressButtonVisible();
                    deliveryAddressDialog.setParentListener(view->{
                        delayedIdle(SessionManager.INSTANCE.getSessionTime());
                    });
                    deliveryAddressDialog.setCloseIconListener(view ->{
                        deliveryAddressDialog.dismiss();
                    });
                    deliveryAddressDialog.setNegativeListener(view ->{
                        deliveryAddressDialog.dismiss();
                        dialogforAddress.show();
                    });
                    deliveryAddressDialog.onClickLocateAddressOnMap(view->{
                        if (deliveryAddressDialog.validations()) {
                            address = deliveryAddressDialog.getAddressData();
                            name = deliveryAddressDialog.getName();
                            singleAdd = deliveryAddressDialog.getAddress();
                            pincode = deliveryAddressDialog.getPincode();
                            city = deliveryAddressDialog.getCity();
                            state = deliveryAddressDialog.getState();
                            stateCode = deliveryAddressDialog.getStateCode();
                            mobileNumber = deliveryAddressDialog.getMobileNumber();
                            if(!addressLatLng){
                                Intent intent = new Intent(getApplicationContext(), MapViewActivity.class);
                                intent.putExtra("locatedPlace", singleAdd);
                                intent.putExtra("testinglatlng", addressLatLng);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivityForResult(intent,799);
                            }else{
                                Intent intent = new Intent(getApplicationContext(), MapViewActivity.class);
                                intent.putExtra("locatedPlace", singleAdd);
                                intent.putExtra("testinglatlng", addressLatLng);
                                intent.putExtra("mapLats", mappingLat);
                                intent.putExtra("mapLangs", mappingLong);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivityForResult(intent,799);
                            }

                        }











                    });
                    deliveryAddressDialog.setPositiveListener(view -> {
                        if (deliveryAddressDialog.validations()) {
                            address = deliveryAddressDialog.getAddressData();
                            name = deliveryAddressDialog.getName();
                            singleAdd = deliveryAddressDialog.getAddress();
                            pincode = deliveryAddressDialog.getPincode();
                            city = deliveryAddressDialog.getCity();
                            state = deliveryAddressDialog.getState();
                            stateCode = deliveryAddressDialog.getStateCode();
                            mobileNumber = deliveryAddressDialog.getMobileNumber();
                            deliveryAddressDialog.dismiss();
                            if (isPharmaProductsThere) {
                                pharmaItemsContainsAlert();
                            } else {
//                if (isFmcgProductsThere) {
//                    new CheckoutActivityController(this, this).expressCheckoutTransactionApiCall(getExpressCheckoutTransactionApiRequest());
//                } else {
                                navigateToPaymentOptionsActivity();
//                }
                            }
                        }
                    });
//                9958704005
//                deliveryAddressDialog.setNegativeListener(view -> {
//                    RecallAddressModelRequest recallAddressModelRequest = new RecallAddressModelRequest();
//                    recallAddressModelRequest.setMobileNo("9958704005");
//                    recallAddressModelRequest.setStoreId(SessionManager.INSTANCE.getStoreId());
//                    recallAddressModelRequest.setUrl("");
//                    recallAddressModelRequest.setDataAreaID("AHEL");
//
//                    new CheckoutActivityController(this, this).getOMSCallPunchingAddressList(recallAddressModelRequest);
////                    last3Address.clear();
////                    last3Address.add("100-1, LB Nagar, Hyderabad");
////                    last3Address.add("13-15-1, Gachibowli, Hyderabad");
////                    last3Address.add("17-10-16,Hitech City, Hyderabad");
//
//                });
                    deliveryAddressDialog.show();


//                deliveryAddressDialog.continueButtonVisible();
                }
            });


            if (recallAddressResponses.getCustomerDetails().size() > 0) {
                dialogForLast3addressBinding.nolistfound.setVisibility(View.GONE);
                dialogForLast3addressBinding.last3addressRecyclerView.setVisibility(View.VISIBLE);
                RecyclerView rvTest = (RecyclerView) dialogforAddress.findViewById(R.id.last_3addressRecyclerView);
                rvTest.setHasFixedSize(true);
                rvTest.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                    rvTest.addItemDecoration(new SimpleDividerItemDecoration(context, R.drawable.divider));

                LastThreeAddressAdapter lastThreeAddressAdapter = new LastThreeAddressAdapter(getApplicationContext(), recallAddressResponses.getCustomerDetails(), this, null);
                rvTest.setAdapter(lastThreeAddressAdapter);
            } else {
                dialogForLast3addressBinding.nolistfound.setVisibility(View.VISIBLE);
                dialogForLast3addressBinding.last3addressRecyclerView.setVisibility(View.GONE);
            }

        }
        else if (recallAddressResponses.getCustomerDetails().size() == 0 && isOverAllHomeDelivery) {
            if (address == null) {
                deliveryAddressDialog = new DeliveryAddressDialog(CheckoutActivity.this, this,null);
                deliveryAddressDialog.reCallAddressButtonGone();
                deliveryAddressDialog.setParentListener(view->{
                    delayedIdle(SessionManager.INSTANCE.getSessionTime());
                });
                deliveryAddressDialog.setCloseIconListener(view ->{
                    deliveryAddressDialog.dismiss();
                });
                deliveryAddressDialog.onClickLocateAddressOnMap(view->{
                    if (deliveryAddressDialog.validations()) {
                        address = deliveryAddressDialog.getAddressData();
                        name = deliveryAddressDialog.getName();
                        singleAdd = deliveryAddressDialog.getAddress();
                        pincode = deliveryAddressDialog.getPincode();
                        city = deliveryAddressDialog.getCity();
                        state = deliveryAddressDialog.getState();
                        stateCode = deliveryAddressDialog.getStateCode();
                        mobileNumber = deliveryAddressDialog.getMobileNumber();
                        if(!addressLatLng){
                            Intent intent = new Intent(getApplicationContext(), MapViewActivity.class);
                            intent.putExtra("locatedPlace", singleAdd);
                            intent.putExtra("testinglatlng", addressLatLng);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivityForResult(intent,799);
                        }else{
                            Intent intent = new Intent(getApplicationContext(), MapViewActivity.class);
                            intent.putExtra("locatedPlace", singleAdd);
                            intent.putExtra("testinglatlng", addressLatLng);
                            intent.putExtra("mapLats", mappingLat);
                            intent.putExtra("mapLangs", mappingLong);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivityForResult(intent,799);
                        }

                    }











                });
                deliveryAddressDialog.setPositiveListener(view -> {
                    if (deliveryAddressDialog.validations()) {
                        address = deliveryAddressDialog.getAddressData();
                        name = deliveryAddressDialog.getName();
                        singleAdd = deliveryAddressDialog.getAddress();
                        pincode = deliveryAddressDialog.getPincode();
                        city = deliveryAddressDialog.getCity();
                        state = deliveryAddressDialog.getState();
                        stateCode = deliveryAddressDialog.getStateCode();
                        mobileNumber = deliveryAddressDialog.getMobileNumber();
                        deliveryAddressDialog.dismiss();
                        if (isPharmaProductsThere) {
                            pharmaItemsContainsAlert();
                        } else {
//                if (isFmcgProductsThere) {
//                    new CheckoutActivityController(this, this).expressCheckoutTransactionApiCall(getExpressCheckoutTransactionApiRequest());
//                } else {
                            navigateToPaymentOptionsActivity();
//                }
                        }
                    }
                });
//                9958704005
//                deliveryAddressDialog.setNegativeListener(view -> {
//                    RecallAddressModelRequest recallAddressModelRequest = new RecallAddressModelRequest();
//                    recallAddressModelRequest.setMobileNo("9958704005");
//                    recallAddressModelRequest.setStoreId(SessionManager.INSTANCE.getStoreId());
//                    recallAddressModelRequest.setUrl("");
//                    recallAddressModelRequest.setDataAreaID("AHEL");
//
//                    new CheckoutActivityController(this, this).getOMSCallPunchingAddressList(recallAddressModelRequest);
////                    last3Address.clear();
////                    last3Address.add("100-1, LB Nagar, Hyderabad");
////                    last3Address.add("13-15-1, Gachibowli, Hyderabad");
////                    last3Address.add("17-10-16,Hitech City, Hyderabad");
//
//                });
                deliveryAddressDialog.show();

            } else {
                if (isPharmaProductsThere) {
                    pharmaItemsContainsAlert();
                } else {
//                if (isFmcgProductsThere) {
//                    new CheckoutActivityController(this, this).expressCheckoutTransactionApiCall(getExpressCheckoutTransactionApiRequest());
//                } else {
                    navigateToPaymentOptionsActivity();
//                }
                }
            }
        }
        else {
            if (name == null) {
                deliveryAddressDialog = new DeliveryAddressDialog(CheckoutActivity.this, this,null);
                deliveryAddressDialog.reCallAddressButtonGone();
                deliveryAddressDialog.setParentListener(view->{
                    delayedIdle(SessionManager.INSTANCE.getSessionTime());
                });
                if (activityCheckoutBinding.getModel().isPharma && !isPharmaHomeDelivery) {
                    deliveryAddressDialog.isNotHomeDelivery();
                } else if (activityCheckoutBinding.getModel().isFmcg && !isFmcgHomeDelivery) {
                    deliveryAddressDialog.isNotHomeDelivery();
                }
                deliveryAddressDialog.setCloseIconListener(view ->{
                    deliveryAddressDialog.dismiss();
                });
                deliveryAddressDialog.setPositiveListener(view -> {

                    if (deliveryAddressDialog.notHomeDeliveryValidations()) {
//                        address = deliveryAddressDialog.getAddressData();
                        name = deliveryAddressDialog.getName();
//                        singleAdd = deliveryAddressDialog.getAddress();
//                        pincode = deliveryAddressDialog.getPincode();
//                        city = deliveryAddressDialog.getCity();
//                        state = deliveryAddressDialog.getState();
//                        stateCode = deliveryAddressDialog.getStateCode();
                        mobileNumber = deliveryAddressDialog.getMobileNumber();
                        deliveryAddressDialog.dismiss();
                        if (isPharmaProductsThere) {
                            pharmaItemsContainsAlert();
                        } else {
//                if (isFmcgProductsThere) {
//                    new CheckoutActivityController(this, this).expressCheckoutTransactionApiCall(getExpressCheckoutTransactionApiRequest());
//                } else {
                            navigateToPaymentOptionsActivity();
//                }
                        }
                    }


                });
                deliveryAddressDialog.setNegativeListener(view -> {
                    deliveryAddressDialog.dismiss();
                });
                deliveryAddressDialog.show();
            } else {
                if (isPharmaProductsThere) {
                    pharmaItemsContainsAlert();
                } else {
//                if (isFmcgProductsThere) {
//                    new CheckoutActivityController(this, this).expressCheckoutTransactionApiCall(getExpressCheckoutTransactionApiRequest());
//                } else {
                    navigateToPaymentOptionsActivity();
//                }
                }
            }
        }
    }


//        else if(!isPharmaHomeDelivery || !isFmcgHomeDelivery) {
//            nameEntered=true;
//            deliveryAddressDialog = new DeliveryAddressDialog(CheckoutActivity.this);
//
//            if (activityCheckoutBinding.getModel().isPharma && !isPharmaHomeDelivery) {
//                deliveryAddressDialog.isNotHomeDelivery();
//            } else if (activityCheckoutBinding.getModel().isFmcg && !isFmcgHomeDelivery) {
//                deliveryAddressDialog.isNotHomeDelivery();
//            }
//
//            deliveryAddressDialog.setPositiveListener(view -> {
//                if (activityCheckoutBinding.getModel().isPharma && !isPharmaHomeDelivery) {
//                    if (deliveryAddressDialog.notHomeDeliveryValidations()) {
////                        address = deliveryAddressDialog.getAddressData();
//                        name = deliveryAddressDialog.getName();
////                        singleAdd = deliveryAddressDialog.getAddress();
////                        pincode = deliveryAddressDialog.getPincode();
////                        city = deliveryAddressDialog.getCity();
////                        state = deliveryAddressDialog.getState();
////                        stateCode = deliveryAddressDialog.getStateCode();
//                        mobileNumber = deliveryAddressDialog.getMobileNumber();
//                        deliveryAddressDialog.dismiss();
//                    }
//                } else if (activityCheckoutBinding.getModel().isFmcg && !isFmcgHomeDelivery) {
//                    if (deliveryAddressDialog.notHomeDeliveryValidations()) {
//                        name = deliveryAddressDialog.getName();
////                        singleAdd = deliveryAddressDialog.getAddress();
////                        pincode = deliveryAddressDialog.getPincode();
////                        city = deliveryAddressDialog.getCity();
////                        state = deliveryAddressDialog.getState();
////                        stateCode = deliveryAddressDialog.getStateCode();
//                        mobileNumber = deliveryAddressDialog.getMobileNumber();
//                        deliveryAddressDialog.dismiss();
//                    }
//
//                }
//            });
//            deliveryAddressDialog.setNegativeListener(view -> {
//                deliveryAddressDialog.dismiss();
//            });
//            deliveryAddressDialog.show();
//        }


//        if (isPharmaHomeDelivery || isFmcgHomeDelivery) {
//            if (address != null) {
//                Intent intent = new Intent(CheckoutActivity.this, PaymentOptionsActivity.class);
//                intent.putExtra("fmcgTotal", fmcgToatalPass);
//                intent.putExtra("isPharmaHomeDelivery", isPharmaHomeDelivery);
//                intent.putExtra("isFmcgHomeDelivery", isFmcgHomeDelivery);
//                intent.putExtra("customerDeliveryAddress", address);
//                intent.putExtra("name", name);
//                intent.putExtra("singleAdd", singleAdd);
//                intent.putExtra("pincode", pincode);
//                intent.putExtra("city", city);
//                intent.putExtra("state", state);
//                startActivity(intent);
//                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//            } else {
////                Toast.makeText(this, "Please Fill Address Form", Toast.LENGTH_SHORT).show();
//
//                if (address == null) {
//                    DeliveryAddressDialog deliveryAddressDialog = new DeliveryAddressDialog(CheckoutActivity.this);
//                    deliveryAddressDialog.setPositiveListener(view -> {
//                        if (deliveryAddressDialog.validations()) {
//                            address = deliveryAddressDialog.getAddressData();
//                            name = deliveryAddressDialog.getName();
//                            singleAdd = deliveryAddressDialog.getAddress();
//                            pincode = deliveryAddressDialog.getPincode();
//                            city = deliveryAddressDialog.getCity();
//                            state = deliveryAddressDialog.getState();
//                            deliveryAddressDialog.dismiss();
//                        }
//                    });
//                    deliveryAddressDialog.setNegativeListener(view -> {
//                        deliveryAddressDialog.dismiss();
//                    });
//                    deliveryAddressDialog.show();
//                }
//
//            }
//        } else {
//            Intent intent = new Intent(CheckoutActivity.this, PaymentOptionsActivity.class);
//            intent.putExtra("fmcgTotal", fmcgToatalPass);
//            intent.putExtra("isPharmaHomeDelivery", isPharmaHomeDelivery);
//            intent.putExtra("isFmcgHomeDelivery", isFmcgHomeDelivery);
//            intent.putExtra("customerDeliveryAddress", address);
//            intent.putExtra("name", name);
//            intent.putExtra("singleAdd", singleAdd);
//            intent.putExtra("pincode", pincode);
//            intent.putExtra("city", city);
//            intent.putExtra("state", state);
//            startActivity(intent);
//            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//        }


    private void pharmaItemsContainsAlert() {
        Dialog pharmaItemsContainsAlertDialog = new Dialog(this);
        DialogPharmaItemContainAlertBinding dialogPharmaItemContainAlertBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_pharma_item_contain_alert, null, true);
        pharmaItemsContainsAlertDialog.setContentView(dialogPharmaItemContainAlertBinding.getRoot());
        if (pharmaItemsContainsAlertDialog.getWindow() != null)
            pharmaItemsContainsAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pharmaItemsContainsAlertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialogPharmaItemContainAlertBinding.dialogCancel.setOnClickListener(v -> {
            if (pharmaItemsContainsAlertDialog != null) {
                pharmaItemsContainsAlertDialog.dismiss();
            }
            finish();
        });
        dialogPharmaItemContainAlertBinding.dialogOk.setOnClickListener(v -> {
            pharmaItemsContainsAlertDialog.dismiss();
//            if (isFmcgProductsThere) {
//                new CheckoutActivityController(this, this).expressCheckoutTransactionApiCall(getExpressCheckoutTransactionApiRequest());
//            } else {
            navigateToPaymentOptionsActivity();
//            }
        });
        pharmaItemsContainsAlertDialog.show();

    }

    @Override
    public void onSuccessexpressCheckoutTransactionApiCall(ExpressCheckoutTransactionApiResponse expressCheckoutTransactionApiResponse) {
        if (expressCheckoutTransactionApiResponse != null) {
            if (expressCheckoutTransactionApiResponse.getRequestStatus() == 0) {
                this.expressCheckoutTransactionId = expressCheckoutTransactionApiResponse.getTransactionId();
                navigateToPaymentOptionsActivity();
            } else if (expressCheckoutTransactionApiResponse.getRequestStatus() == 2) {
                Dialog stockAvailableDialog = new Dialog(this);
                DialogStockavailableAlertBinding dialogStockavailableAlertBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_stockavailable_alert, null, false);
                stockAvailableDialog.setContentView(dialogStockavailableAlertBinding.getRoot());
                if (stockAvailableDialog.getWindow() != null)
                    stockAvailableDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                stockAvailableDialog.setCancelable(false);
                dialogStockavailableAlertBinding.dialogInfo.setText("Stock available for " + expressCheckoutTransactionApiResponse.getReturnMessage() + "\nDo you want to continue?");
                dialogStockavailableAlertBinding.dialogOk.setText("Yes");
                dialogStockavailableAlertBinding.dialogCancel.setText("Cancel");
                dialogStockavailableAlertBinding.dialogOk.setOnClickListener(v -> {
                    this.expressCheckoutTransactionId = expressCheckoutTransactionApiResponse.getTransactionId();
                    List<OCRToDigitalMedicineResponse> ocrToDigitalMedicineResponseList = SessionManager.INSTANCE.getDataList();
                    if (ocrToDigitalMedicineResponseList != null && ocrToDigitalMedicineResponseList.size() > 0) {
                        for (int i = 0; i < ocrToDigitalMedicineResponseList.size(); i++) {
                            if (ocrToDigitalMedicineResponseList.get(i).getMedicineType().equalsIgnoreCase("FMCG") || ocrToDigitalMedicineResponseList.get(i).getMedicineType().equalsIgnoreCase("PRIVATE LABEL")) {
                                if (expressCheckoutTransactionApiResponse.getReturnMessage().contains(",")) {
                                    String artcodeList[] = expressCheckoutTransactionApiResponse.getReturnMessage().split(",");
                                    for (int j = 0; j < artcodeList.length; j++) {
                                        if (artcodeList[j].equalsIgnoreCase(ocrToDigitalMedicineResponseList.get(i).getArtCode().substring(0, ocrToDigitalMedicineResponseList.get(i).getArtCode().indexOf(",")))) {
                                            ocrToDigitalMedicineResponseList.remove(i);
                                            i--;
                                        }
                                    }
                                } else {
                                    if (expressCheckoutTransactionApiResponse.getReturnMessage().equalsIgnoreCase(ocrToDigitalMedicineResponseList.get(i).getArtCode().substring(0, ocrToDigitalMedicineResponseList.get(i).getArtCode().indexOf(",")))) {
                                        ocrToDigitalMedicineResponseList.remove(i);
                                        i--;
                                    }
                                }

                            }
                        }
                    }
                    SessionManager.INSTANCE.setDataList(ocrToDigitalMedicineResponseList);
                    stockAvailableDialog.dismiss();
                    if (SessionManager.INSTANCE.getDataList() != null && SessionManager.INSTANCE.getDataList().size() > 0) {
                        navigateToPaymentOptionsActivity();
                    } else {
                        Toast.makeText(this, "Stock not Available for this order", Toast.LENGTH_SHORT).show();
                    }
                });
                dialogStockavailableAlertBinding.dialogCancel.setOnClickListener(v -> stockAvailableDialog.dismiss());

            }
        }
    }

    private void navigateToPaymentOptionsActivity() {
        Intent intent = new Intent(CheckoutActivity.this, PaymentOptionsActivity.class);
        intent.putExtra("fmcgTotal", fmcgToatalPass);
        intent.putExtra("isPharmaHomeDelivery", isPharmaHomeDelivery);
        intent.putExtra("isFmcgHomeDelivery", isFmcgHomeDelivery);
        intent.putExtra("customerDeliveryAddress", address);
        intent.putExtra("name", name);
        intent.putExtra("singleAdd", singleAdd);
        intent.putExtra("pincode", pincode);
        intent.putExtra("city", city);
        intent.putExtra("state", state);
        intent.putExtra("FMCG_TRANSACTON_ID", fmcgOrderId);
//        intent.putExtra("EXPRESS_CHECKOUT_TRANSACTION_ID", expressCheckoutTransactionId);
        intent.putExtra("STATE_CODE", stateCode);
        intent.putExtra("MOBILE_NUMBER", mobileNumber);
        intent.putExtra("recallAddressResponses", (Serializable) recallAddressResponses.getCustomerDetails());
        PaymentOptionsActivity.isPaymentActivityForTimer="isPaymentActivity";
        startActivity(intent);
        overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
    }

    @Override
    public void onFailuremessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickLastThreeAddresses(String selectedAdress, String phoneNumber, String postalCode, String cityLastThreeAddress, String stateLastThreeAddress, String nameLastThreeAddress, String address1, String address2, String onlyAddress) {
        dialogforAddress.dismiss();
        DeliveryAddressDialog deliveryAddressDialog = new DeliveryAddressDialog(CheckoutActivity.this, this,null);
//        SessionManager.INSTANCE.setLast3Address(selectedAdress);
        if (deliveryAddressDialog != null) {
            deliveryAddressDialog.setParentListener(view->{
                delayedIdle(SessionManager.INSTANCE.getSessionTime());
            });
            deliveryAddressDialog.setAddressforLast3Address(selectedAdress, phoneNumber, postalCode, cityLastThreeAddress, stateLastThreeAddress, nameLastThreeAddress, address1, address2, onlyAddress);
            if (deliveryAddressDialog.validations()) {
                name = deliveryAddressDialog.getName();
                singleAdd = deliveryAddressDialog.getAddress();
                pincode = deliveryAddressDialog.getPincode();
                city = deliveryAddressDialog.getCity();
                state = deliveryAddressDialog.getState();
                stateCode = deliveryAddressDialog.getStateCode();
                mobileNumber = deliveryAddressDialog.getMobileNumber();
                address = deliveryAddressDialog.getAddressData();

                mobileNumber = phoneNumber;
                deliveryAddressDialog.dismiss();
                if (isPharmaProductsThere) {
                    pharmaItemsContainsAlert();
                } else {
//                if (isFmcgProductsThere) {
//                    new CheckoutActivityController(this, this).expressCheckoutTransactionApiCall(getExpressCheckoutTransactionApiRequest());
//                } else {
                    navigateToPaymentOptionsActivity();
//                }
                }
            }
        }
    }

    RecallAddressResponse recallAddressResponses;

    @Override
    public void onSuccessRecallAddress(RecallAddressResponse recallAddressResponse) {
        this.recallAddressResponses = recallAddressResponse;


    }

    @Override
    public void onFailureRecallAddress(RecallAddressResponse body) {
        Toast.makeText(getApplicationContext(), body.getReturnMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toCallTimerInDialog() {
        delayedIdle(SessionManager.INSTANCE.getSessionTime());
    }

    private void deliveryModeHandle() {
        activityCheckoutBinding.needHomeDelivery.setBackground(getResources().getDrawable(R.drawable.bg_lite_grey));
//        activityCheckoutBinding.payandCollectatCounter.setBackground(getResources().getDrawable(R.drawable.bg_lite_grey));
        activityCheckoutBinding.needHomeDelivery1.setBackground(getResources().getDrawable(R.drawable.bg_lite_grey));
        activityCheckoutBinding.payhereandCarry.setBackground(getResources().getDrawable(R.drawable.bg_lite_grey));

        activityCheckoutBinding.needHomeDeliveryText.setTextColor(getResources().getColor(R.color.grey_color));
//        activityCheckoutBinding.payandCollectatCounterText.setTextColor(getResources().getColor(R.color.grey_color));
        activityCheckoutBinding.needHomeDelivery1Text.setTextColor(getResources().getColor(R.color.grey_color));
        activityCheckoutBinding.payHereAndcarryText.setTextColor(getResources().getColor(R.color.grey_color));

        activityCheckoutBinding.needHomeDeliveryImg.setImageDrawable(getResources().getDrawable(R.drawable.tick_white));
//        activityCheckoutBinding.payandCollectatCounterImg.setImageDrawable(getResources().getDrawable(R.drawable.tick_white));
        activityCheckoutBinding.needHomeDelivery1Img.setImageDrawable(getResources().getDrawable(R.drawable.tick_white));
        activityCheckoutBinding.payHereAndcarryImg.setImageDrawable(getResources().getDrawable(R.drawable.tick_white));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            {
                if (data != null) {
                        String mapAddress = (String) data.getStringExtra("mapnewaddress");
                        String mapCity = (String) data.getStringExtra("mapnewcity");
                        String mapPostalCode = (String) data.getStringExtra("mapnewzipcode");
                        deliveryAddressDialog.setAddressFromMap(mapAddress, mapCity, mapPostalCode);
                        boolean latLngLoc = (boolean) data.getBooleanExtra("getlatlnglocations", false);
                        String mapLattitudes = (String) data.getStringExtra("latitudes");
                        String mapLongitudes = (String) data.getStringExtra("longitudes");

                        addressLatLng = latLngLoc;
                        mappingLat = mapLattitudes;
                        mappingLong = mapLongitudes;
                    }


            }
        }

    }

    public ExpressCheckoutTransactionApiRequest getExpressCheckoutTransactionApiRequest() {
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
        tenderLine.setWalletOrderId("");
        tenderLine.setWalletTransactionID("");
        tenderLine.setRewardsPoint(0);
        tenderLine.setPreviewText("");
        tenderLine.setIsVoid(false);
        tenderLine.setBarCode("");


        tenderLine.setAmountTendered(fmcgToatalPass);
        tenderLine.setAmountCur(fmcgToatalPass);
        tenderLine.setAmountMst(fmcgToatalPass);
        tenderLineList.add(tenderLine);

        expressCheckoutTransactionApiRequest.setTenderLine(tenderLineList);

        return expressCheckoutTransactionApiRequest;
    }


    public class CheckoutuiModel {
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
    }
}
/*  double amount = Double.parseDouble(String.valueOf(getProducts.getOfferprice()));
 *DecimalFormat formatter = new DecimalFormat("#,###.00");
 *String formatted = formatter.format(amount);*/