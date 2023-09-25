package com.apollo.pharmacy.ocr.activities.mposstoresetup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.HomeActivity;
import com.apollo.pharmacy.ocr.activities.mposstoresetup.dialog.MposGetStoresDialog;
import com.apollo.pharmacy.ocr.activities.mposstoresetup.model.StoreListResponseModel;
import com.apollo.pharmacy.ocr.activities.mposstoresetup.model.StoreSetupModel;
import com.apollo.pharmacy.ocr.databinding.MposStoreSetupActivityBinding;
import com.apollo.pharmacy.ocr.model.DeviceRegistrationResponse;
import com.apollo.pharmacy.ocr.model.UserAddress;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MposStoreSetupActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener, StoreSetupMvpView {

    MposStoreSetupActivityBinding mposStoreSetupActivityBinding;
    private String deviceId;
    private String registerDate = "";
    private String deviceType = "";
    double latitude = 0.0;
    double longitude = 0.0;
    private GoogleApiClient googleApiClient;
    private Location mylocation;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private StoreListResponseModel storeListObj = null;
    private StoreListResponseModel.StoreListObj selectedStoreId = null;
    private StoreListResponseModel.StoreListObj selectedStoreContactNum = null;
    private String loginActivityName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mposStoreSetupActivityBinding = DataBindingUtil.setContentView(this, R.layout.mpos_store_setup_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mposStoreSetupActivityBinding.deletePar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                SessionManager.INSTANCE.setAccessDialogHandler("Dismiss");
            }
        });
        mposStoreSetupActivityBinding.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = MposStoreSetupActivity.this.getCurrentFocus();
                if (view1 != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
//        mposStoreSetupActivityBinding.baseUrl.setText("http://online.apollopharmacy.org:51/EPOS/");
        SessionManager.INSTANCE.setEposUrl("http://online.apollopharmacy.org:51/MPOS/");
        setUp();
    }

    private MposStoreSetupController storeSetupController;

    private void setUp() {
        storeSetupController = new MposStoreSetupController(this, this);
        storeSetupController.getStoreList();

        mposStoreSetupActivityBinding.setCallback(this);

        deviceId = Utils.getDeviceId(this);
        if (getIntent() != null) {
            loginActivityName = getIntent().getStringExtra("homeActivity");
        }

        if (getIntent() != null) {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            registerDate = currentDate.format(c);
            deviceType = android.os.Build.MODEL;
            StoreSetupModel storeSetupModel = new StoreSetupModel();
            storeSetupModel.setMacId(deviceId);
            storeSetupModel.setDeviceName(deviceType);
            storeSetupModel.setDeviceType(Build.DEVICE);
            storeSetupModel.setStoreDate(registerDate);
            storeSetupModel.setStoreLatitude(latitude);
            storeSetupModel.setStoreLongitude(longitude);
            mposStoreSetupActivityBinding.macid.setText(storeSetupModel.getMacId());
            mposStoreSetupActivityBinding.deviceType.setText(storeSetupModel.getDeviceType());
            mposStoreSetupActivityBinding.deviceName.setText(storeSetupModel.getDeviceName());
            mposStoreSetupActivityBinding.storeDate.setText(storeSetupModel.getStoreDate());
            mposStoreSetupActivityBinding.storeLattitude.setText(String.valueOf((int) storeSetupModel.getStoreLatitude()));
            mposStoreSetupActivityBinding.storeLongitude.setText(String.valueOf((int) storeSetupModel.getStoreLongitude()));

        }
        if (SessionManager.INSTANCE.getTerminalId() != null && SessionManager.INSTANCE.getEposUrl() != null) {
            mposStoreSetupActivityBinding.terminalIdText.setText(SessionManager.INSTANCE.getTerminalId());
            mposStoreSetupActivityBinding.baseUrl.setText(SessionManager.INSTANCE.getEposUrl());
        }

        if (SessionManager.INSTANCE.getStoreId() != null && !SessionManager.INSTANCE.getStoreId().isEmpty()) {
            StoreListResponseModel.StoreListObj item = new StoreListResponseModel.StoreListObj();
            item.setStoreId(SessionManager.INSTANCE.getStoreId());
            item.setStoreName(SessionManager.INSTANCE.getUseraddress().getName());
            item.setAddress(SessionManager.INSTANCE.getUseraddress().getAddress1());
            mposStoreSetupActivityBinding.setStoreinfo(item);
            selectedStoreId = item;
        }

        setUpGClient();
    }

    private synchronized void setUpGClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, 0, this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //TODO for website validation
    private boolean isValidate() {
        String url = mposStoreSetupActivityBinding.baseUrl.getText().toString().trim();
        String terminalId = mposStoreSetupActivityBinding.terminalIdText.getText().toString().trim();
        String sessionTime = mposStoreSetupActivityBinding.sessionTime.getText().toString().trim();
        String configStore = mposStoreSetupActivityBinding.configStore.getText().toString().trim();
//        String companyName = mposStoreSetupActivityBinding.companyName.getText().toString().trim();
        if (url.isEmpty()) {
            mposStoreSetupActivityBinding.baseUrl.setError("Please Enter Epos Url");
            mposStoreSetupActivityBinding.baseUrl.requestFocus();
            return false;
        } else if (terminalId.isEmpty()) {
            mposStoreSetupActivityBinding.terminalIdText.setError("Please Enter Terminal Id");
            mposStoreSetupActivityBinding.terminalIdText.requestFocus();
            return false;
        } else if (sessionTime.isEmpty()) {
            mposStoreSetupActivityBinding.sessionTime.setError("Please Enter session time");
            mposStoreSetupActivityBinding.sessionTime.requestFocus();
            return false;

        } else if (configStore.isEmpty()) {
            mposStoreSetupActivityBinding.configStore.setError("Store should not be empty");
            mposStoreSetupActivityBinding.configStore.requestFocus();
            return false;
        }
//        else if (companyName.isEmpty()) {
//            mposStoreSetupActivityBinding.companyName.setError("Company name should not be empty");
//            mposStoreSetupActivityBinding.companyName.requestFocus();
//            return false;
//        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (googleApiClient != null) {
            googleApiClient.stopAutoManage(MposStoreSetupActivity.this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (mylocation != null) {
            latitude = mylocation.getLatitude();
            longitude = mylocation.getLongitude();
//            mposStoreSetupActivityBinding.getStoremodel().setStoreLatitude(mylocation.getLatitude());
//            mposStoreSetupActivityBinding.getStoremodel().setStoreLongitude(mylocation.getLongitude());

            mposStoreSetupActivityBinding.storeLattitude.setText(String.valueOf(mylocation.getLatitude()));
            mposStoreSetupActivityBinding.storeLongitude.setText(String.valueOf(mylocation.getLatitude()));

        } else {
            getMyLocation();
        }
    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(MposStoreSetupActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(MposStoreSetupActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        } else {
        }
    }

    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(MposStoreSetupActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    @SuppressLint("RestrictedApi") LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (LocationListener) this);
                    PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                        @Override
                        public void onResult(@NonNull LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    int permissionLocation = ContextCompat.checkSelfPermission(MposStoreSetupActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        status.startResolutionForResult(MposStoreSetupActivity.this, REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    break;
                            }
                        }
                    });
                }
            } else {
                setUpGClient();
            }
        }
    }

    private boolean isShowDialog = false;

    @Override
    public void onSelectStoreSearch() {
        if (storeListObj != null) {
            if (storeListObj.getStoreListArr().size() > 0) {
                MposGetStoresDialog dialog = new MposGetStoresDialog(this);
                dialog.setStoreDetailsMvpView(this);
                dialog.setStoreListArray(storeListObj.getStoreListArr());
                if (!isShowDialog) {
                    isShowDialog = true;
                    dialog.show();
                }
            }
        } else {
            Toast.makeText(this, "No store list found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancelBtnClick() {
        final Dialog dialog = new Dialog(MposStoreSetupActivity.this);
        dialog.setContentView(R.layout.dialog_custom_alert);
        dialog.setCancelable(false);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView dialogTitleText = dialog.findViewById(R.id.dialog_info);
        Button okButton = dialog.findViewById(R.id.dialog_ok);
        Button declineButton = dialog.findViewById(R.id.dialog_cancel);
        dialogTitleText.setText("Do you want to remove site id?");
        okButton.setText(getResources().getString(R.string.label_yes));
        declineButton.setText(getResources().getString(R.string.label_cancel_text));
        okButton.setOnClickListener(v -> {
            selectedStoreId = null;
            mposStoreSetupActivityBinding.setStoreinfo(selectedStoreId);
            dialog.dismiss();

        });
        declineButton.setOnClickListener(v -> dialog.dismiss());


    }

    @Override
    public void handleStoreSetupService() {

    }

    @Override
    public void getStoreList() {

    }

//    @Override
//    public void insertAdminLoginDetails() {
//
//    }

    @Override
    public void checkConfingApi() {

    }

    String firebaseToken;

    @Override
    public void onVerifyClick() {
        if (isValidate()) {
//            mPresenter.checkConfingApi();
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
                firebaseToken = instanceIdResult.getToken();
                Log.e("newToken", firebaseToken);
                this.getPreferences(Context.MODE_PRIVATE).edit().putString("fb", firebaseToken).apply();
                storeSetupController.getDeviceRegistrationDetails(mposStoreSetupActivityBinding.storeDate.getText().toString(), mposStoreSetupActivityBinding.deviceType.getText().toString(), firebaseToken, mposStoreSetupActivityBinding.storeLattitude.getText().toString(), mposStoreSetupActivityBinding.storeLongitude.getText().toString(), mposStoreSetupActivityBinding.macid.getText().toString(), mposStoreSetupActivityBinding.storeId.getText().toString(), mposStoreSetupActivityBinding.terminalIdText.getText().toString(), "admin");
            });
            if (storeIdNumber != null) SessionManager.INSTANCE.setStoreId(storeIdNumber);
            else
                SessionManager.INSTANCE.setStoreId(mposStoreSetupActivityBinding.storeId.getText().toString());
            SessionManager.INSTANCE.setSessionTime(Integer.parseInt(mposStoreSetupActivityBinding.sessionTime.getText().toString().trim()));
            SessionManager.INSTANCE.setConfigStore(mposStoreSetupActivityBinding.configStore.getText().toString().trim());
//            SessionManager.INSTANCE.setDataAreaId(mposStoreSetupActivityBinding.companyName.getText().toString().trim());
            SessionManager.INSTANCE.setDataAreaId(selectedStoreId.getDataAreaId());
            SessionManager.INSTANCE.setTerminalId(mposStoreSetupActivityBinding.terminalIdText.getText().toString());
            SessionManager.INSTANCE.setEposUrl(mposStoreSetupActivityBinding.baseUrl.getText().toString());
            SessionManager.INSTANCE.setAccessDialogHandler("Dismiss");
        }
    }

    @Override
    public void getDeviceRegistrationDetails(DeviceRegistrationResponse deviceRegistrationResponse) {
        Toast.makeText(this, "" + deviceRegistrationResponse.getMessage(), Toast.LENGTH_SHORT).show();
        if (loginActivityName != null && loginActivityName != "" && loginActivityName.equals("homeActivity")) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();

        } else {
            finish();
        }

    }

    @Override
    public void dialogCloseListiner() {
        isShowDialog = false;
    }

    String storeIdNumber;

    @Override
    public void onSelectStore(StoreListResponseModel.StoreListObj item) {
        mposStoreSetupActivityBinding.setStoreinfo(item);
        storeIdNumber = item.getStoreId();
        selectedStoreId = item;
        selectedStoreContactNum = item;
        UserAddress userAddress = new UserAddress();
        userAddress.setId(Integer.parseInt(item.getStoreId()));
        userAddress.setCity(item.getCity());
        userAddress.setAddress1(item.getAddress());
        userAddress.setName(item.getStoreName());
        userAddress.setMobile(SessionManager.INSTANCE.getMobilenumber());
        userAddress.setState(item.getCity());
        userAddress.setPincode("000000");
        SessionManager.INSTANCE.setUseraddress(userAddress);
    }


    @Override
    public void setStoresList(StoreListResponseModel storesList) {
        storeListObj = storesList;
    }
}
