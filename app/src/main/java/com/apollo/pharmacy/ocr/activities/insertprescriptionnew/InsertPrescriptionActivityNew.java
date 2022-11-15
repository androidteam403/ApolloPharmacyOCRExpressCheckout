package com.apollo.pharmacy.ocr.activities.insertprescriptionnew;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.BaseActivity;
import com.apollo.pharmacy.ocr.activities.MapViewActivity;
import com.apollo.pharmacy.ocr.activities.epsonscan.EpsonScanActivity;
import com.apollo.pharmacy.ocr.activities.insertprescriptionnew.adapter.PrescriptionListAdapter;
import com.apollo.pharmacy.ocr.activities.insertprescriptionnew.adapter.PrescriptionViewPagerAdapter;
import com.apollo.pharmacy.ocr.activities.userlogin.UserLoginActivity;
import com.apollo.pharmacy.ocr.activities.yourorderstatus.YourOrderStatusActivity;
import com.apollo.pharmacy.ocr.adapters.LastThreeAddressAdapter;
import com.apollo.pharmacy.ocr.databinding.ActivityNewInsertPrescriptionBinding;
import com.apollo.pharmacy.ocr.databinding.DialogForLast3addressBinding;
import com.apollo.pharmacy.ocr.databinding.DialogPrescriptionFullviewBinding;
import com.apollo.pharmacy.ocr.dialog.ChooseDeliveryType;
import com.apollo.pharmacy.ocr.dialog.DeliveryAddressDialog;
import com.apollo.pharmacy.ocr.model.PlaceOrderReqModel;
import com.apollo.pharmacy.ocr.model.PlaceOrderResModel;
import com.apollo.pharmacy.ocr.model.RecallAddressModelRequest;
import com.apollo.pharmacy.ocr.model.RecallAddressResponse;
import com.apollo.pharmacy.ocr.model.StateCodes;
import com.apollo.pharmacy.ocr.model.UserAddress;
import com.apollo.pharmacy.ocr.utility.ImageManager;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InsertPrescriptionActivityNew extends BaseActivity implements InsertPrescriptionActivityNewListener, ChooseDeliveryType.ChooseDeliveryTypeListener {
    private ActivityNewInsertPrescriptionBinding activityNewInsertPrescriptionBinding;
    //    private ScaleGestureDetector mScaleGestureDetector;
//    private float mScaleFactor = 1.0f;
    private PrescriptionViewPagerAdapter prescriptionViewPagerAdapter;
    private PrescriptionListAdapter prescriptionListAdapter;
    private List<String> imagePathList;
    private String deliveryTypeName = null;

    //made changes by naveen
    private Dialog dialogforAddress;
    private DeliveryAddressDialog deliveryAddressDialog;
    private RecallAddressResponse recallAddressResponses;
    public static boolean addressLatLng = false;
    private String mappingLat;
    private String mappingLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityNewInsertPrescriptionBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_insert_prescription);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activityNewInsertPrescriptionBinding.setCallback(this);
//        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());


        String filePath = null;
        if (getIntent() != null) {
            filePath = (String) getIntent().getStringExtra("filePath");
        }
        if (SessionManager.INSTANCE.getImagePathList() != null && SessionManager.INSTANCE.getImagePathList().size() > 0) {
            this.imagePathList = SessionManager.INSTANCE.getImagePathList();
            imagePathList.add(filePath);
            SessionManager.INSTANCE.setImagePath(imagePathList);
        } else {
            imagePathList = new ArrayList<>();
            imagePathList.add(filePath);
            SessionManager.INSTANCE.setImagePath(imagePathList);
        }
        prescriptionListAdapter = new PrescriptionListAdapter(this, imagePathList, this);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        activityNewInsertPrescriptionBinding.prescriptionListRecyclerview.setLayoutManager(mLayoutManager2);
        activityNewInsertPrescriptionBinding.prescriptionListRecyclerview.setAdapter(prescriptionListAdapter);
//        activityNewInsertPrescriptionBinding.prescriptionFullviewImg.setMaxZoom(4f);
        listeners();
    }

    private void listeners() {
        activityNewInsertPrescriptionBinding.scanStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backCountTimer();
            }
        });
        activityNewInsertPrescriptionBinding.plusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (yourCountDownTimer != null) {
                    yourCountDownTimer.cancel();
                }
                activityNewInsertPrescriptionBinding.scanStart.setVisibility(View.VISIBLE);
                activityNewInsertPrescriptionBinding.timeStart.setVisibility(View.GONE);
                activityNewInsertPrescriptionBinding.startPrescriptionLay.setVisibility(View.VISIBLE);
                activityNewInsertPrescriptionBinding.scannedLay.setVisibility(View.GONE);
            }
        });
        activityNewInsertPrescriptionBinding.gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ImagePicker.Builder(InsertPrescriptionActivityNew.this).mode(ImagePicker.Mode.CAMERA_AND_GALLERY).compressLevel(ImagePicker.ComperesLevel.MEDIUM).directory(ImagePicker.Directory.DEFAULT).extension(ImagePicker.Extension.PNG).scale(600, 600).allowMultipleImages(true).enableDebuggingMode(true).build();
            }
        });

        activityNewInsertPrescriptionBinding.uploadPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPaths != null && mPaths.size() > 0) {
                    handleUploadImageService();
                } else {
                    Toast.makeText(InsertPrescriptionActivityNew.this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    CountDownTimer yourCountDownTimer;

    public void backCountTimer() {
        yourCountDownTimer = new CountDownTimer(30000, 1000) {

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onTick(long millisUntilFinished) {

                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                activityNewInsertPrescriptionBinding.scanStart.setVisibility(View.GONE);
                activityNewInsertPrescriptionBinding.timeStart.setVisibility(View.VISIBLE);
                activityNewInsertPrescriptionBinding.timeStart.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                activityNewInsertPrescriptionBinding.startPrescriptionLay.setVisibility(View.GONE);
                activityNewInsertPrescriptionBinding.scannedLay.setVisibility(View.VISIBLE);
            }
        }.start();

    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return mScaleGestureDetector.onTouchEvent(event);
//    }
//
//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//
//        // when a scale gesture is detected, use it to resize the image
//        @Override
//        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
//            mScaleFactor *= scaleGestureDetector.getScaleFactor();
//            activityNewInsertPrescriptionBinding.prescriptionFullviewImg.setScaleX(mScaleFactor);
//            activityNewInsertPrescriptionBinding.prescriptionFullviewImg.setScaleY(mScaleFactor);
//            return true;
//        }
//    }

    @Override
    public void onClickPrescription() {
        activityNewInsertPrescriptionBinding.closeFullviewPrescription.setVisibility(View.VISIBLE);
        activityNewInsertPrescriptionBinding.scannedLay.setVisibility(View.GONE);
        activityNewInsertPrescriptionBinding.gallery.setVisibility(View.GONE);
        activityNewInsertPrescriptionBinding.uploadPrescription.setVisibility(View.GONE);
//        activityNewInsertPrescriptionBinding.prescriptionFullviewLayout.setVisibility(View.VISIBLE);
        activityNewInsertPrescriptionBinding.prescriptionFullviewImg.setVisibility(View.VISIBLE);
        activityNewInsertPrescriptionBinding.zoominZoomoutLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCloseFullviewPrescription() {
        activityNewInsertPrescriptionBinding.scannedLay.setVisibility(View.VISIBLE);
        activityNewInsertPrescriptionBinding.closeFullviewPrescription.setVisibility(View.GONE);
        activityNewInsertPrescriptionBinding.gallery.setVisibility(View.VISIBLE);
        activityNewInsertPrescriptionBinding.uploadPrescription.setVisibility(View.VISIBLE);
//        activityNewInsertPrescriptionBinding.prescriptionFullviewLayout.setVisibility(View.GONE);
        activityNewInsertPrescriptionBinding.prescriptionFullviewImg.setVisibility(View.GONE);
        activityNewInsertPrescriptionBinding.zoominZoomoutLayout.setVisibility(View.GONE);

    }

    @Override
    public void onClickZoomIn() {
//        activityNewInsertPrescriptionBinding.prescriptionFullviewImg.zoomIn();
    }

    @Override
    public void onClickZoomOut() {
//        activityNewInsertPrescriptionBinding.prescriptionFullviewImg.zoomOut();
    }

    @Override
    public void onClickScanAnotherPrescription() {
        Intent intent = new Intent(this, EpsonScanActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        finish();
    }

    @Override
    public void onClickPrescription(String prescriptionPath) {
        Dialog prescriptionZoomDialog = new Dialog(this, R.style.fadeinandoutcustomDialog);
        DialogPrescriptionFullviewBinding prescriptionFullviewBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_prescription__fullview, null, false);
        prescriptionZoomDialog.setContentView(prescriptionFullviewBinding.getRoot());
        File imgFile = new File(prescriptionPath + "/1.jpg");
        if (imgFile.exists()) {
            Uri uri = Uri.fromFile(imgFile);
            prescriptionFullviewBinding.prescriptionFullviewImg.setImageURI(uri);
        }
        prescriptionFullviewBinding.dismissPrescriptionFullview.setOnClickListener(v -> prescriptionZoomDialog.dismiss());
        prescriptionZoomDialog.show();
    }

    @Override
    public void onClickItemDelete(int position) {
        Dialog dialog = new Dialog(InsertPrescriptionActivityNew.this);
        dialog.setContentView(R.layout.dialog_custom_alert);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView dialogTitleText = dialog.findViewById(R.id.dialog_info);
        Button okButton = dialog.findViewById(R.id.dialog_ok);
        Button declineButton = dialog.findViewById(R.id.dialog_cancel);
        dialogTitleText.setText("Are you sure want to delete?");
        okButton.setText(getResources().getString(R.string.label_ok));
        declineButton.setText(getResources().getString(R.string.label_cancel_text));
        okButton.setOnClickListener(v1 -> {
            dialog.dismiss();
            if (SessionManager.INSTANCE.getImagePathList() != null && SessionManager.INSTANCE.getImagePathList().size() > 0) {
                this.imagePathList = SessionManager.INSTANCE.getImagePathList();
                imagePathList.remove(position);
                SessionManager.INSTANCE.setImagePath(imagePathList);
                if (imagePathList.size() > 0) {
                    prescriptionListAdapter = new PrescriptionListAdapter(this, imagePathList, this);
                    RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                    activityNewInsertPrescriptionBinding.prescriptionListRecyclerview.setLayoutManager(mLayoutManager2);
                    activityNewInsertPrescriptionBinding.prescriptionListRecyclerview.setAdapter(prescriptionListAdapter);
                } else {
                    finish();
                }
            }
        });
        declineButton.setOnClickListener(v12 -> dialog.dismiss());
    }

    @Override
    public void onClickBackPressed() {
        onBackPressed();
    }

    ChooseDeliveryType chooseDeliveryType;
    public static final int INSERT_PRESCRIPTION_ACTIVITY_NEW = 161;

    @Override
    public void onClickContinue() {
        if (SessionManager.INSTANCE.getImagePathList() != null && SessionManager.INSTANCE.getImagePathList().size() > 0) {
            showChooseDelieryDialog();
        } else {
            Utils.showSnackbarDialog(this, findViewById(android.R.id.content), "No Prescriction");
        }
    }

    private void showChooseDelieryDialog() {
        chooseDeliveryType = new ChooseDeliveryType(InsertPrescriptionActivityNew.this);
        chooseDeliveryType.setmListener(this);
//            chooseDeliveryType.setPositiveListener(view1 -> {
//
////            Intent intent = new Intent(InsertPrescriptionActivityNew.this, YourOrderStatusActivity.class);
////            startActivity(intent);
//            });
        chooseDeliveryType.setNegativeListener(v -> {
            activityNewInsertPrescriptionBinding.transColorId.setVisibility(View.GONE);
            chooseDeliveryType.dismiss();
        });
        chooseDeliveryType.show();
    }

    @Override
    public void onSuccessPlaceOrder(PlaceOrderResModel model) {
        if (model != null) {
            String orderNo = model.getOrdersResult().getOrderNo();
            Utils.showSnackbarDialog(this, findViewById(android.R.id.content), "Placed order");
            Intent intent = new Intent(InsertPrescriptionActivityNew.this, YourOrderStatusActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("orderNo", orderNo);
            intent.putExtra("deliveryTypeName", deliveryTypeName);
            startActivity(intent);
            List<String> imagePathList = new ArrayList<>();
            SessionManager.INSTANCE.setImagePath(imagePathList);
        }
    }

    @Override
    public void onFailureService(String message) {
        Utils.showSnackbarDialog(this, findViewById(android.R.id.content), message);
    }

    @Override
    public void onSuccessRecallAddress(RecallAddressResponse body) {
        this.recallAddressResponses = body;
        if (chooseDeliveryType != null && chooseDeliveryType.isShowing()) {
            chooseDeliveryType.dismiss();
        }
        showAddressListDialog();
    }

    String address;
    String name, singleAdd, pincode, city, state, stateCode, mobileNumber;
    ;

    private void showAddressListDialog() {
        if (recallAddressResponses.getCustomerDetails().size() > 0) {
            dialogforAddress = new Dialog(this);
            DialogForLast3addressBinding dialogForLast3addressBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_for_last3address, null, true);
            dialogforAddress.setContentView(dialogForLast3addressBinding.getRoot());
            if (dialogforAddress.getWindow() != null)
                dialogforAddress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogforAddress.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialogforAddress.setCancelable(false);
            dialogforAddress.show();
            dialogForLast3addressBinding.dialogButtonAddAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogforAddress.dismiss();

                    deliveryAddressDialog = new DeliveryAddressDialog(InsertPrescriptionActivityNew.this, null, null);
                    deliveryAddressDialog.reCallAddressButtonVisible();
                    deliveryAddressDialog.setNegativeListener(view -> {
                        deliveryAddressDialog.dismiss();
                        dialogforAddress.show();
                    });

                    deliveryAddressDialog.setPositiveListener(view -> {
                        if (deliveryAddressDialog.validations()) {
                            address = deliveryAddressDialog.getAddressData();
                            name = deliveryAddressDialog.getName();
                            singleAdd = deliveryAddressDialog.getAddress();
                            pincode = deliveryAddressDialog.getPincode();
                            city = deliveryAddressDialog.getCity();
                            state = deliveryAddressDialog.getState();
                            UserAddress userAddress = new UserAddress();
                            userAddress.setAddress1(address);
                            userAddress.setCity(city);
                            userAddress.setPincode(pincode);
                            userAddress.setState(state);
                            userAddress.setName(name);
                            SessionManager.INSTANCE.setUseraddress(userAddress);
                            deliveryAddressDialog.dismiss();


//                            this.deliveryTypeName = deliveryTypeName;
                            activityNewInsertPrescriptionBinding.transColorId.setVisibility(View.GONE);
                            chooseDeliveryType.dismiss();
                            handleUploadImageService();
//                            navigateToPaymentOptionsActivity();

                        }
                    });

                    deliveryAddressDialog.setCloseIconListener(view -> {
                        deliveryAddressDialog.dismiss();
                        deliveryAddressDialog.onClickCrossIcon();
                        address = null;
                        name = null;
                    });

                    deliveryAddressDialog.onClickLocateAddressOnMap(view -> {
                        if (deliveryAddressDialog.validationsForMap()) {
                            address = deliveryAddressDialog.getAddressData();
                            name = deliveryAddressDialog.getName();
                            singleAdd = deliveryAddressDialog.getAddress();
                            pincode = deliveryAddressDialog.getPincode();
                            city = deliveryAddressDialog.getCity();
                            state = deliveryAddressDialog.getState();
                            stateCode = deliveryAddressDialog.getStateCode();
                            mobileNumber = deliveryAddressDialog.getMobileNumber();
                            if (!addressLatLng) {
                                Intent intent = new Intent(getApplicationContext(), MapViewActivity.class);
                                intent.putExtra("locatedPlace", singleAdd);
                                intent.putExtra("testinglatlng", addressLatLng);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivityForResult(intent, 799);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), MapViewActivity.class);
                                intent.putExtra("locatedPlace", singleAdd);
                                intent.putExtra("testinglatlng", addressLatLng);
                                intent.putExtra("mapLats", mappingLat);
                                intent.putExtra("mapLangs", mappingLong);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivityForResult(intent, 799);
                            }

                        }

                    });
                    deliveryAddressDialog.show();
                }
            });


            if (recallAddressResponses.getCustomerDetails().size() > 0) {
                dialogForLast3addressBinding.nolistfound.setVisibility(View.GONE);
                dialogForLast3addressBinding.last3addressRecyclerView.setVisibility(View.VISIBLE);
                RecyclerView rvTest = (RecyclerView) dialogforAddress.findViewById(R.id.last_3addressRecyclerView);
                rvTest.setHasFixedSize(true);
                rvTest.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                    rvTest.addItemDecoration(new SimpleDividerItemDecoration(context, R.drawable.divider));

                LastThreeAddressAdapter lastThreeAddressAdapter = new LastThreeAddressAdapter(getApplicationContext(), recallAddressResponses.getCustomerDetails(), null, null);
                lastThreeAddressAdapter.setInsertPrescriptionActivityNewListener(this);
                rvTest.setAdapter(lastThreeAddressAdapter);
            } else {
                dialogForLast3addressBinding.nolistfound.setVisibility(View.VISIBLE);
                dialogForLast3addressBinding.last3addressRecyclerView.setVisibility(View.GONE);
            }

        } else if (recallAddressResponses.getCustomerDetails().size() == 0) {
            if (address == null) {
                deliveryAddressDialog = new DeliveryAddressDialog(InsertPrescriptionActivityNew.this, null, null);
                deliveryAddressDialog.reCallAddressButtonGone();
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

//                        this.deliveryTypeName = deliveryTypeName;
                        activityNewInsertPrescriptionBinding.transColorId.setVisibility(View.GONE);
                        chooseDeliveryType.dismiss();
                        handleUploadImageService();
                    }
                });
                deliveryAddressDialog.setCloseIconListener(view -> {
                    deliveryAddressDialog.dismiss();
                    deliveryAddressDialog.onClickCrossIcon();
                    address = null;
                    name = null;
                });
                deliveryAddressDialog.onClickLocateAddressOnMap(view -> {
                    if (deliveryAddressDialog.validationsForMap()) {
                        address = deliveryAddressDialog.getAddressData();
                        name = deliveryAddressDialog.getName();
                        singleAdd = deliveryAddressDialog.getAddress();
                        pincode = deliveryAddressDialog.getPincode();
                        city = deliveryAddressDialog.getCity();
                        state = deliveryAddressDialog.getState();
                        stateCode = deliveryAddressDialog.getStateCode();
                        mobileNumber = deliveryAddressDialog.getMobileNumber();
                        if (!addressLatLng) {
                            Intent intent = new Intent(getApplicationContext(), MapViewActivity.class);
                            intent.putExtra("locatedPlace", singleAdd);
                            intent.putExtra("testinglatlng", addressLatLng);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivityForResult(intent, 799);
                        } else {
                            Intent intent = new Intent(getApplicationContext(), MapViewActivity.class);
                            intent.putExtra("locatedPlace", singleAdd);
                            intent.putExtra("testinglatlng", addressLatLng);
                            intent.putExtra("mapLats", mappingLat);
                            intent.putExtra("mapLangs", mappingLong);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivityForResult(intent, 799);
                        }

                    }

                });
                deliveryAddressDialog.show();

            }
        }
    }

    @Override
    public void onFailureRecallAddress(RecallAddressResponse body) {

    }

    @Override
    public void onClickLastThreeAddresses(String selectedAdress, String phoneNumber, String postalCode, String cityLastThreeAddress, String stateLastThreeAddress, String nameLastThreeAddress, String address1, String address2, String onlyAddress) {
        if (dialogforAddress != null && dialogforAddress.isShowing()) {
            dialogforAddress.dismiss();
        }

        DeliveryAddressDialog deliveryAddressDialog = new DeliveryAddressDialog(InsertPrescriptionActivityNew.this, null, null);
//        SessionManager.INSTANCE.setLast3Address(selectedAdress);
        if (deliveryAddressDialog != null) {
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
//                if (isPharmaProductsThere) {
//                    pharmaItemsContainsAlert();
//                } else {
////                if (isFmcgProductsThere) {
////                    new CheckoutActivityController(this, this).expressCheckoutTransactionApiCall(getExpressCheckoutTransactionApiRequest());
////                } else {
//                    navigateToPaymentOptionsActivity();
////                }
//                }
                activityNewInsertPrescriptionBinding.transColorId.setVisibility(View.GONE);
                chooseDeliveryType.dismiss();
                handleUploadImageService();
            }
        }
    }

    private List<String> mPaths = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INSERT_PRESCRIPTION_ACTIVITY_NEW && resultCode == RESULT_OK) {

            if (data != null) {
                boolean isOtpVerified = data.getBooleanExtra("IS_OTP_VERIFIED", false);
                if (isOtpVerified) {
                    RecallAddressModelRequest recallAddressModelRequest = new RecallAddressModelRequest();
                    recallAddressModelRequest.setMobileNo(SessionManager.INSTANCE.getMobilenumber());////"9958704005"
                    recallAddressModelRequest.setStoreId(SessionManager.INSTANCE.getStoreId());
                    recallAddressModelRequest.setUrl("");
                    recallAddressModelRequest.setDataAreaID(SessionManager.INSTANCE.getDataAreaId());
                    getController().getOMSCallPunchingAddressList(recallAddressModelRequest);

                }
            }
        } else if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> mPathsDummy = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
            if (mPaths != null) {
                if (mPathsDummy != null && mPathsDummy.size() > 0) {
                    for (String path : mPathsDummy) {
                        mPaths.add(path);
                    }
                }
            } else {
                mPaths = new ArrayList<>();
                if (mPathsDummy != null && mPathsDummy.size() > 0) {
                    for (String path : mPathsDummy) {
                        mPaths.add(path);
                    }
                }
            }
        } else if (resultCode == RESULT_OK) {
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

    ArrayList<PlaceOrderReqModel.PrescUrlEntity> prescEntityArray = new ArrayList<>();

    private void compressFileSize() {
        List<String> mPathsTemp = SessionManager.INSTANCE.getImagePathList();
        for (String pho : mPathsTemp) {
            Bitmap photo = decodeSampledBitmapFromFile(pho, 960, 960);

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(new File(pho + "/1.jpg"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
        }


    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private void handleUploadImageService() {
        Utils.showDialog(this, "Please wait");
        this.mPaths = SessionManager.INSTANCE.getImagePathList();
        try {
            for (int i = 0; i < mPaths.size(); i++) {
                final InputStream imageStream = InsertPrescriptionActivityNew.this.getContentResolver().openInputStream(Uri.fromFile(new File(mPaths.get(i) + "/1.jpg")));
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
                            if (mPaths.size() == prescEntityArray.size()) {
                                Utils.dismissDialog();
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

    @Override
    public void onBackPressed() {
        Dialog dialog = new Dialog(InsertPrescriptionActivityNew.this);
        dialog.setContentView(R.layout.dialog_custom_alert);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView dialogTitleText = dialog.findViewById(R.id.dialog_info);
        Button okButton = dialog.findViewById(R.id.dialog_ok);
        Button declineButton = dialog.findViewById(R.id.dialog_cancel);
        dialogTitleText.setText("Are you sure want to leave current page?");
        okButton.setText(getResources().getString(R.string.label_ok));
        declineButton.setText(getResources().getString(R.string.label_cancel_text));
        okButton.setOnClickListener(v1 -> {
            dialog.dismiss();
            List<String> imagePathList = new ArrayList<>();
            SessionManager.INSTANCE.setImagePath(imagePathList);
            super.onBackPressed();
        });
        declineButton.setOnClickListener(v12 -> dialog.dismiss());
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
        for (StateCodes codes : Utils.getStoreListFromAssets(InsertPrescriptionActivityNew.this)) {
            String codestate_name = codes.getName().trim();
            if (codestate_name.equalsIgnoreCase(stateName)) {
                selectedStateCode = codes.getCode();
            }
        }

        PlaceOrderReqModel placeOrderReqModel = new PlaceOrderReqModel();
        PlaceOrderReqModel.TpdetailsEntity tpDetailsEntity = new PlaceOrderReqModel.TpdetailsEntity();
        tpDetailsEntity.setOrderId(Utils.getTransactionGeneratedId());
        tpDetailsEntity.setShopId(SessionManager.INSTANCE.getStoreId());
        tpDetailsEntity.setShippingMethod(deliveryTypeName);
        tpDetailsEntity.setPaymentMethod("COD");
        tpDetailsEntity.setVendorName(SessionManager.INSTANCE.getKioskSetupResponse().getKIOSK_ID());
        PlaceOrderReqModel.CustomerDetailsEntity customerDetailsEntity = new PlaceOrderReqModel.CustomerDetailsEntity();
        customerDetailsEntity.setMobileNo(mobileNumber);
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
        PlaceOrderReqModel.ItemDetailsEntity itemDetailsEntity = new PlaceOrderReqModel.ItemDetailsEntity();
        itemDetailsEntity.setItemID("APC0011");
        itemDetailsEntity.setItemName("AP Cough Dropns 300N - Rs.1");
        itemDetailsEntity.setPrice(1);
        itemDetailsEntity.setPack("1");
        itemDetailsEntity.setMOU(1);
        itemDetailsEntity.setQty(1);
        itemDetailsArr.add(itemDetailsEntity);

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
//        tpDetailsEntity.setOrderDate(Utils.getOrderedID());
        tpDetailsEntity.setRequestType("CART");//NONCART
        tpDetailsEntity.setPrescUrl(prescEntityArray);
        placeOrderReqModel.setTpdetails(tpDetailsEntity);
        new InsertPrescriptionActivityNewController(this, this).handleOrderPlaceService(this, placeOrderReqModel);
    }

    @Override
    public void onClickOkPositive(String deliveryTypeName, String address) {
        this.deliveryTypeName = deliveryTypeName;
        if (deliveryTypeName.equalsIgnoreCase("HOME DELIVERY")) {
            Intent intent = new Intent(InsertPrescriptionActivityNew.this, UserLoginActivity.class);
            intent.putExtra("userLoginActivity", "INSERT_PRESCRIPTION_ACTIVITY_NEW");
            intent.putExtra("INSERT_PRESCRIPTION_ACTIVITY_NEW", "INSERT_PRESCRIPTION_ACTIVITY_NEW");
            startActivityForResult(intent, INSERT_PRESCRIPTION_ACTIVITY_NEW);
            overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
        } else {
            deliveryAddressDialog = new DeliveryAddressDialog(InsertPrescriptionActivityNew.this, null, null);
            deliveryAddressDialog.reCallAddressButtonGone();
            deliveryAddressDialog.setParentListener(view -> {
                delayedIdle(SessionManager.INSTANCE.getSessionTime());
            });
            deliveryAddressDialog.isNotHomeDeliveryPrescription();

            deliveryAddressDialog.setPositiveListener(view -> {

                if (deliveryAddressDialog.notHomeDeliveryValidationsPrescription()) {
//                        address = deliveryAddressDialog.getAddressData();
                    name = deliveryAddressDialog.getName();
//                        singleAdd = deliveryAddressDialog.getAddress();
//                        pincode = deliveryAddressDialog.getPincode();
//                        city = deliveryAddressDialog.getCity();
//                        state = deliveryAddressDialog.getState();
//                        stateCode = deliveryAddressDialog.getStateCode();
                    mobileNumber = deliveryAddressDialog.getMobileNumber();
                    deliveryAddressDialog.dismiss();
                    //
                    activityNewInsertPrescriptionBinding.transColorId.setVisibility(View.GONE);
                    chooseDeliveryType.dismiss();
                    handleUploadImageService();
                }


            });
            deliveryAddressDialog.setCloseIconListener(view -> {
                deliveryAddressDialog.dismiss();
            });
            deliveryAddressDialog.setNegativeListener(view -> {
                deliveryAddressDialog.dismiss();
            });
            deliveryAddressDialog.show();


        }
    }

    private InsertPrescriptionActivityNewController getController() {
        return new InsertPrescriptionActivityNewController(this, this);
    }
}
