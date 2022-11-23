package com.apollo.pharmacy.ocr.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.adapters.RecyclerViewAdapter;
import com.apollo.pharmacy.ocr.model.Image;
import com.apollo.pharmacy.ocr.model.UserAddress;
import com.apollo.pharmacy.ocr.network.ApiClient;
import com.apollo.pharmacy.ocr.network.ApiInterface;
import com.apollo.pharmacy.ocr.receiver.ConnectivityReceiver;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.utility.NetworkUtils;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Singleton;
import com.apollo.pharmacy.ocr.utility.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.apollo.pharmacy.ocr.utility.Utils.getNameFromFilePath;

public class UploadPrescriptionActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener,
        RecyclerViewAdapter.ItemListener {

    private LinearLayout continueLayout;
    private int selectedDocCnt = 0;
    private RecyclerViewAdapter prescriptionAdapter;
    private final ArrayList<Image> images = new ArrayList<>();
    private final int uploadDocumentCnt = 2;
    private final int USB_CAMERA_REQUEST_CODE = 111;
    private ConstraintLayout constraint_layout;

    public UploadPrescriptionActivity() {
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
        UploadPrescriptionActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        setContentView(R.layout.activity_upload_prescription);

        LinearLayout faqLayout = findViewById(R.id.help_layout);
        TextView helpText = findViewById(R.id.help_text);
        helpText.setText(getResources().getString(R.string.faq));
        faqLayout.setOnClickListener(view -> startActivity(new Intent(UploadPrescriptionActivity.this, FAQActivity.class)));

        ImageView customerCareImg = findViewById(R.id.customer_care_icon);
        LinearLayout customerHelpLayout = findViewById(R.id.customer_help_layout);
        customerHelpLayout.setVisibility(View.VISIBLE);
        customerCareImg.setOnClickListener(v -> {
            if (customerHelpLayout.getVisibility() == View.VISIBLE) {
                customerCareImg.setBackgroundResource(R.drawable.icon_help_circle);
                TranslateAnimation animate = new TranslateAnimation(0, customerHelpLayout.getWidth(), 0, 0);
                animate.setDuration(2000);
                animate.setFillAfter(true);
                customerHelpLayout.startAnimation(animate);
                customerHelpLayout.setVisibility(View.GONE);
            } else {
                customerCareImg.setBackgroundResource(R.drawable.icon_help_circle);
                TranslateAnimation animate = new TranslateAnimation(customerHelpLayout.getWidth(), 0, 0, 0);
                animate.setDuration(2000);
                animate.setFillAfter(true);
                customerHelpLayout.startAnimation(animate);
                customerHelpLayout.setVisibility(View.VISIBLE);
            }
        });

        constraint_layout = findViewById(R.id.constraint_layout);
        continueLayout = findViewById(R.id.continue_layout);
        RecyclerView prescriptionsRecyclerView = findViewById(R.id.prescriptions_RecyclerView);
        LinearLayout cameraLayout = findViewById(R.id.camera_layout);

        cameraLayout.setOnClickListener(v -> {
            if (images.size() < uploadDocumentCnt) {
                getCameraImagePicker();
            } else {
                Toast.makeText(this, "You can't upload more than 2 images", Toast.LENGTH_SHORT).show();
            }
        });
        if (images.size() == 0) {
            getCameraImagePicker();
        }
        findViewById(R.id.back_icon).setOnClickListener(v -> {
            finish();
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });
        prescriptionAdapter = new RecyclerViewAdapter(this, images, this);
        prescriptionsRecyclerView.setAdapter(prescriptionAdapter);
        GridLayoutManager manager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        prescriptionsRecyclerView.setLayoutManager(manager);
        prescriptionAdapter.notifyDataSetChanged();

        continueLayout.setOnClickListener(arg0 -> {
            if (images.size() > 0) {
                Singleton.getInstance().imageArrayList.addAll(images);
                SessionManager.INSTANCE.setUploadPrescriptionDeliveryMode(true);
                handelFetchAddressService();
            } else {
                Toast.makeText(this, getApplicationContext().getResources().getString(R.string.label_upload_image_alert), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent1 = new Intent(UploadPrescriptionActivity.this, HomeActivity.class);
        intent1.putExtra("activityname", "InsertPrescriptionActivity");
        startActivity(intent1);
        finish();
        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
    }

    @Override
    public void onItemClick(Image item) {
        showDeletePrescriptionDialog(item);
    }

    private void showDeletePrescriptionDialog(Image item) {
        final Dialog dialog = new Dialog(UploadPrescriptionActivity.this);
        dialog.setContentView(R.layout.dialog_custom_alert);
        dialog.setCancelable(false);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView dialogTitleText = dialog.findViewById(R.id.dialog_info);
        Button okButton = dialog.findViewById(R.id.dialog_ok);
        Button declineButton = dialog.findViewById(R.id.dialog_cancel);
        dialogTitleText.setText(getResources().getString(R.string.label_delete_prescription));
        okButton.setText(getResources().getString(R.string.label_ok));
        declineButton.setText(getResources().getString(R.string.label_cancel_text));
        okButton.setOnClickListener(v -> {
            selectedDocCnt = selectedDocCnt - 1;
            dialog.dismiss();
            images.remove(item);
            prescriptionAdapter.notifyDataSetChanged();
            checkImageSize();
        });
        declineButton.setOnClickListener(v -> dialog.dismiss());
    }

    private void getCameraImagePicker() {
        Intent intent = new Intent(this, USBCameraActivity.class);
        startActivityForResult(intent, USB_CAMERA_REQUEST_CODE);
        overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                if (requestCode == USB_CAMERA_REQUEST_CODE) {
                    String result = data.getStringExtra("result");
                    images.add(new Image(selectedDocCnt, getNameFromFilePath(result), result));
                    prescriptionAdapter.notifyDataSetChanged();
                    selectedDocCnt = selectedDocCnt + images.size();
                    checkImageSize();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkImageSize() {
        if (images.size() > 0) {
            continueLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.lang_selection_btn_bg));
        } else {
            continueLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.lang_selection_btn_bg_gray));
        }
    }

    private void handelFetchAddressService() {
        SessionManager.INSTANCE.setDeliverytype("DeliveryAtHome");
        if (NetworkUtils.isNetworkConnected(this)) {
            Utils.showDialog(this, getApplicationContext().getResources().getString(R.string.label_please_wait));
            ApiInterface apiInterface = ApiClient.getApiService(Constants.Get_User_Delivery_Address_List);
            Call<ArrayList<UserAddress>> call = apiInterface.getUserAddressList(SessionManager.INSTANCE.getMobilenumber());
            call.enqueue(new Callback<ArrayList<UserAddress>>() {
                @Override
                public void onResponse(@NotNull Call<ArrayList<UserAddress>> call, @NotNull Response<ArrayList<UserAddress>> response) {
                    if (response.body() != null) {
                        if (response.isSuccessful()) {
                            onSuccessGetAddress(response.body());
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ArrayList<UserAddress>> call, @NotNull Throwable t) {
                    t.printStackTrace();
                    onErrorMessage(t.getMessage());
                }
            });
        } else {
            Utils.showSnackbar(this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
        }
    }

    public void onSuccessGetAddress(ArrayList<UserAddress> response) {
        Utils.dismissDialog();
        if (response != null && response.size() > 0) {
            ArrayList<UserAddress> addressList = new ArrayList<>(response);
            if (addressList.size() == 0 || addressList == null) {
                Intent intent = new Intent(this, AddressAddActivity.class);
                intent.putExtra("addressList", addressList.size());
                startActivity(intent);
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            } else {
                Intent intent = new Intent(this, AddressListActivity.class);
                intent.putExtra("addressList", addressList);
                startActivity(intent);
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            }
        } else {
            Intent intent = new Intent(this, AddressAddActivity.class);
            intent.putExtra("addressList", 0);
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        }
    }

    private void onErrorMessage(String error) {
        Utils.showCustomAlertDialog(UploadPrescriptionActivity.this, getResources().getString(R.string.label_server_err_message), false, "OK", "");
    }
}
