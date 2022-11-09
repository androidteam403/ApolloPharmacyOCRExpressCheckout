package com.apollo.pharmacy.ocr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.checkout.CheckoutActivity;
import com.apollo.pharmacy.ocr.databinding.DialogChooseDeliveryTypeBinding;
import com.apollo.pharmacy.ocr.model.UserAddress;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;

public class ChooseDeliveryType {
    private Dialog dialog;
    private DialogChooseDeliveryTypeBinding dialogChooseDeliveryTypeBinding;
    private boolean chooseDeliveryType;
    private String deliveryTypeName = null;
    private boolean isFmcgHomeDelivery;
    private boolean isPharmaHomeDelivery;
    String address;
    String name, singleAdd, pincode, city, state;

    public ChooseDeliveryType(Context context) {
        dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialogChooseDeliveryTypeBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_choose_delivery_type, null, false);
        dialog.setContentView(dialogChooseDeliveryTypeBinding.getRoot());
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(R.color.transparent_color);
        dialog.setCancelable(false);

        dialogChooseDeliveryTypeBinding.homeDeliveryLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (address == null) {
                    DeliveryAddressDialog deliveryAddressDialog = new DeliveryAddressDialog(context, null, null);
                    deliveryAddressDialog.setPositiveListener(view1 -> {
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
                        }
                    });
                    deliveryAddressDialog.setNegativeListener(view2 -> {
                        deliveryAddressDialog.dismiss();
                    });
                    deliveryAddressDialog.show();
                }

                chooseDeliveryType = true;
                deliveryTypeName = "HOME DELIVERY";
                dialogChooseDeliveryTypeBinding.homeDeliveryLay.setBackgroundResource(R.drawable.choose_deliverytype_selectedbg);
                dialogChooseDeliveryTypeBinding.payPickLay.setBackgroundResource(R.drawable.choose_deliverytype_unselectedbg);
                dialogChooseDeliveryTypeBinding.continueButon.setAlpha((float) 0.9);
            }
        });
        dialogChooseDeliveryTypeBinding.payPickLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDeliveryType = true;
                deliveryTypeName = "Pay and collect at counter";
                dialogChooseDeliveryTypeBinding.payPickLay.setBackgroundResource(R.drawable.choose_deliverytype_selectedbg);
                dialogChooseDeliveryTypeBinding.homeDeliveryLay.setBackgroundResource(R.drawable.choose_deliverytype_unselectedbg);
                dialogChooseDeliveryTypeBinding.continueButon.setAlpha((float) 0.9);
            }
        });
        dialogChooseDeliveryTypeBinding.continueButon.setOnClickListener(view -> {
            if (chooseDeliveryType) {
                if (deliveryTypeName.equalsIgnoreCase("HOME DELIVERY")) {
                    if (address != null) {
                        if (mListener != null) {
                            mListener.onClickOkPositive(deliveryTypeName, address);
                        }
                    } else {
                        View views = dialog.getWindow().getDecorView();
                        Utils.showSnackbarDialog(context, views, "Add delivery address");
                    }
                } else if (deliveryTypeName.equalsIgnoreCase("Pay and collect at counter")) {
                    if (mListener != null) {
                        mListener.onClickOkPositive(deliveryTypeName, null);
                    }
                }

            }
        });
//        dialogChooseDeliveryTypeBinding.dialogButtonOK.setOnClickListener(v -> {
//            if (chooseDeliveryType) {
//                if (deliveryTypeName.equalsIgnoreCase("HOME DELIVERY")) {
//                    if (address != null) {
//                        if (mListener != null) {
//                            mListener.onClickOkPositive(deliveryTypeName, address);
//                        }
//                    } else {
//                        View views = dialog.getWindow().getDecorView();
//                        Utils.showSnackbarDialog(context, views, "Add delivery address");
//                    }
//                } else if (deliveryTypeName.equalsIgnoreCase("Pay and collect at counter")) {
//                    if (mListener != null) {
//                        mListener.onClickOkPositive(deliveryTypeName, null);
//                    }
//                }
//            }
//        });
    }

//    public void setPositiveListener(View.OnClickListener okListener) {
//        dialogChooseDeliveryTypeBinding.dialogButtonOK.setOnClickListener(okListener);
//    }


    public void setNegativeListener(View.OnClickListener okListener) {
        dialogChooseDeliveryTypeBinding.dialogButtonNO.setOnClickListener(okListener);
    }

    public void show() {

        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public void setTitle(String title) {
        dialogChooseDeliveryTypeBinding.title.setText(title);
    }

    public ChooseDeliveryTypeListener mListener;

    public void setmListener(ChooseDeliveryTypeListener mListener) {
        this.mListener = mListener;
    }

    public interface ChooseDeliveryTypeListener {
        void onClickOkPositive(String deliveryTypeName, String address);
    }

//    public void setPositiveLabel(String positive) {
//        alertDialogBoxBinding.btnYes.setText(positive);
//    }
//
//    public void setNegativeLabel(String negative) {
//        negativeExist = true;
//        alertDialogBoxBinding.btnNo.setText(negative);
//    }

}
