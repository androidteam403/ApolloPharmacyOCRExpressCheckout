package com.apollo.pharmacy.ocr.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.mposstoresetup.MposStoreSetupActivity;
import com.apollo.pharmacy.ocr.controller.StoreSetupController;
import com.apollo.pharmacy.ocr.dialog.AccesskeyDialog;
import com.apollo.pharmacy.ocr.dialog.GetStoresDialog;
import com.apollo.pharmacy.ocr.interfaces.StoreSetupListener;
import com.apollo.pharmacy.ocr.model.GetStoreInfoResponse;
import com.apollo.pharmacy.ocr.receiver.ConnectivityReceiver;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.utility.NetworkUtils;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;

import java.util.ArrayList;

public class StoreSetupActivity extends AppCompatActivity implements StoreSetupListener,
        ConnectivityReceiver.ConnectivityReceiverListener {

    private TextView storeIdText, storeNameText, storeAddressText;
    private ConstraintLayout constraintLayout;
    private RelativeLayout storeSelectionLayout;
    private LinearLayout storeInformationLayout;
    private GetStoresDialog getStoresDialog = null;
    private final ArrayList<GetStoreInfoResponse.DeviceDetailsEntity> storesArrList = new ArrayList<>();
    private boolean isShowDialog = false;
    private String selectedKioskId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_setup);
        if (SessionManager.INSTANCE.getIsDeviceSetup()) {
            Intent intent = new Intent(StoreSetupActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        } else {
            setUp();
        }
    }

    private void setUp() {
        storeSelectionLayout = findViewById(R.id.store_selection_layout);
        storeInformationLayout = findViewById(R.id.store_information_layout);
        storeIdText = findViewById(R.id.store_id_text);
        storeNameText = findViewById(R.id.store_name_text);
        storeAddressText = findViewById(R.id.store_address_text);
        TextView cancelBtn = findViewById(R.id.cancel_button);
        TextView proceedBtn = findViewById(R.id.proceed_button);
        constraintLayout = findViewById(R.id.constraint_layout);
        StoreSetupController storeSetupController = new StoreSetupController(this);

        if (NetworkUtils.isNetworkConnected(this)) {
            Utils.showDialog(this, getApplicationContext().getResources().getString(R.string.label_please_wait));
            storeSetupController.getStoreListDetails(selectedKioskId, false);
        }

        storeSelectionLayout.setOnClickListener(v -> {
            isShowDialog = false;
            showStoreListDialog();
        });
        cancelBtn.setOnClickListener(v -> {
            storeSelectionLayout.setVisibility(View.VISIBLE);
            storeInformationLayout.setVisibility(View.GONE);
        });
        proceedBtn.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkConnected(this)) {
                Utils.showDialog(this, getApplicationContext().getResources().getString(R.string.label_please_wait));
                storeSetupController.getStoreListDetails(selectedKioskId, true); // make true for permanent disable
            } else {
                Utils.showSnackbar(this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        StoreSetupActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            findViewById(R.id.networkErrorLayout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.networkErrorLayout).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSuccessGetStoreList(GetStoreInfoResponse storeInfoResponse) {
        if (storeInfoResponse.getDeviceDetails().size() > 0) {
            storesArrList.clear();
            storesArrList.addAll(storeInfoResponse.getDeviceDetails());
            showStoreListDialog();
        }
    }

    @Override
    public void onFailure(String error) {
//        Utils.showSnackbar(StoreSetupActivity.this, constraintLayout, error);
        Utils.showSnackbarMessage(StoreSetupActivity.this, findViewById(android.R.id.content), error);
    }

    @Override
    public void onSuccessDeviceRegister(GetStoreInfoResponse response) {
        SessionManager.INSTANCE.setIsDeviceSetup(true);
        Intent intent = new Intent(StoreSetupActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClickListener(GetStoreInfoResponse.DeviceDetailsEntity item) {
        if (getStoresDialog != null)
            getStoresDialog.dismiss();
        storeSelectionLayout.setVisibility(View.GONE);
        storeInformationLayout.setVisibility(View.VISIBLE);
        SessionManager.INSTANCE.setKioskSetupResponse(item);
        selectedKioskId = item.getKIOSK_ID();
        storeIdText.setText(item.getSTORE());
        storeNameText.setText(item.getKIOSK_NAME());
        storeAddressText.setText(item.getADDRESS() + ", " + item.getSTATE() + ", " + item.getCITY() + ", " + item.getPINCODE());
    }

    private void showStoreListDialog() {
        getStoresDialog = new GetStoresDialog(this, storesArrList, this);
        if (!isShowDialog) {
            isShowDialog = true;
            getStoresDialog.show();
        }
    }
}
