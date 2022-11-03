package com.apollo.pharmacy.ocr.activities.yourorderstatus;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.BaseActivity;
import com.apollo.pharmacy.ocr.activities.HomeActivity;
import com.apollo.pharmacy.ocr.databinding.ActivityYourorderStatusBinding;
import com.apollo.pharmacy.ocr.model.OCRToDigitalMedicineResponse;
import com.apollo.pharmacy.ocr.utility.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class YourOrderStatusActivity extends BaseActivity implements YourOrderStatusActivityListener {

    private ActivityYourorderStatusBinding activityYourorderStatusBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityYourorderStatusBinding = DataBindingUtil.setContentView(this, R.layout.activity_yourorder_status);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activityYourorderStatusBinding.setCallback(this);
        String orderNo = null;
        String deliveryTypeName = null;
        if (getIntent() != null) {
            orderNo = (String) getIntent().getStringExtra("orderNo");
            deliveryTypeName = (String) getIntent().getStringExtra("deliveryTypeName");
            if (deliveryTypeName.equalsIgnoreCase("HOME DELIVERY")) {
                activityYourorderStatusBinding.pleaseproceedtoBillingText.setText("Thank you! for placing your order with Us.");
            }
        }
        activityYourorderStatusBinding.orderNo.setText(orderNo);
        activityYourorderStatusBinding.userNamePhoneNumber.setText(SessionManager.INSTANCE.getMobilenumber() != null ? SessionManager.INSTANCE.getMobilenumber() : "--");
        activityYourorderStatusBinding.deliveryTypeName.setText(deliveryTypeName);
    }

    @Override
    public void onClickContinueShopping() {
        Intent intent = new Intent(YourOrderStatusActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        finish();
    }

    @Override
    public void onBackPressed() {
    }


    Handler continueShopAlertHandler = new Handler();
    Runnable continuShopAlertRunnable = () -> continueShoppingAlert();


    private void continueShoppingAlert() {
        Dialog continueShopAlertDialog = new Dialog(this);
        continueShopAlertDialog.setContentView(R.layout.dialog_alert_for_idle);
        if (continueShopAlertDialog.getWindow() != null)
            continueShopAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        continueShopAlertDialog.setCancelable(false);
        TextView dialogTitleText = continueShopAlertDialog.findViewById(R.id.dialog_info);
        Button okButton = continueShopAlertDialog.findViewById(R.id.dialog_ok);
        Button declineButton = continueShopAlertDialog.findViewById(R.id.dialog_cancel);
        TextView alertTittle = continueShopAlertDialog.findViewById(R.id.session_time_expiry_countdown);

        SpannableStringBuilder alertSpannnable = new SpannableStringBuilder("Alert!");
        alertSpannnable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, alertSpannnable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        alertTittle.setText(alertSpannnable);


        dialogTitleText.setText("Do you want to continue shopping?");
        okButton.setText("Yes, Continue");
        declineButton.setText("Logout");
        okButton.setOnClickListener(v -> {
            if (continueShopAlertDialog != null && continueShopAlertDialog.isShowing()) {
                continueShopAlertDialog.dismiss();
            }
            List<OCRToDigitalMedicineResponse> dataList = new ArrayList<>();
            SessionManager.INSTANCE.setDataList(dataList);
            Intent intent = new Intent(YourOrderStatusActivity.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            finish();

        });
        declineButton.setOnClickListener(v -> {
            continueShopAlertDialog.dismiss();
//            SessionManager.INSTANCE.logoutUser();
            HomeActivity.isLoggedin=false;
            Intent intent = new Intent(YourOrderStatusActivity.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            finishAffinity();

        });
        continueShopAlertDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        continueShopAlertHandler.removeCallbacks(continuShopAlertRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        continueShopAlertHandler.removeCallbacks(continuShopAlertRunnable);
        continueShopAlertHandler.postDelayed(continuShopAlertRunnable, 5000);
    }

}
