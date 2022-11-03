package com.apollo.pharmacy.ocr.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.adapters.PrescriptionListAdapter;
import com.apollo.pharmacy.ocr.controller.MyCartController;
import com.apollo.pharmacy.ocr.controller.UploadBgImageController;
import com.apollo.pharmacy.ocr.interfaces.MyCartListener;
import com.apollo.pharmacy.ocr.interfaces.PrescriptionPreviewListener;
import com.apollo.pharmacy.ocr.interfaces.UploadBgImageListener;
import com.apollo.pharmacy.ocr.model.BatchListResponse;
import com.apollo.pharmacy.ocr.model.CalculatePosTransactionResponse;
import com.apollo.pharmacy.ocr.model.GetImageRes;
import com.apollo.pharmacy.ocr.model.GetProductListResponse;
import com.apollo.pharmacy.ocr.model.ItemSearchResponse;
import com.apollo.pharmacy.ocr.model.OCRToDigitalMedicineResponse;
import com.apollo.pharmacy.ocr.model.PrescriptionItem;
import com.apollo.pharmacy.ocr.model.ScannedData;
import com.apollo.pharmacy.ocr.model.ScannedMedicine;
import com.apollo.pharmacy.ocr.model.UpCellCrossCellResponse;
import com.apollo.pharmacy.ocr.model.UserAddress;
import com.apollo.pharmacy.ocr.receiver.ConnectivityReceiver;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class PrescriptionPreviewActivity extends BaseActivity implements PrescriptionPreviewListener, MyCartListener,
        ConnectivityReceiver.ConnectivityReceiverListener, UploadBgImageListener {

    private ArrayList<PrescriptionItem> prescriptionList;
    private PrescriptionListAdapter prescriptionListAdapter;
    private String scannedPrescriptionImage = "";
    private CountDownTimer countDownTimer = null;
    private MyCartController myCartController;
    private UploadBgImageController uploadBgImageController;
    private List<OCRToDigitalMedicineResponse> dataList = new ArrayList<>();
    private static final String TAG = PrescriptionPreviewActivity.class.getSimpleName();
    private final int curationTimerVal = 60000;

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiverAddmore);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrescriptionPreviewActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        setContentView(R.layout.activity_prescription_preview);

        LinearLayout faqLayout = findViewById(R.id.help_layout);
        TextView helpText = findViewById(R.id.help_text);
        helpText.setText(getResources().getString(R.string.faq));
        ImageView faq = findViewById(R.id.faq);
        faq.setOnClickListener(view -> startActivity(new Intent(PrescriptionPreviewActivity.this, FAQActivity.class)));

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

        LinearLayout nextLayout = findViewById(R.id.next_btn_layout);
        nextLayout.setOnClickListener(v -> {
            SessionManager.INSTANCE.setOrderCompletionStatus(false);
            SessionManager.INSTANCE.setCurationStatus(true);
            SessionManager.INSTANCE.setScannedImagePath(scannedPrescriptionImage);
            SessionManager.INSTANCE.settimerlogin(true);

            Intent intent = new Intent(PrescriptionPreviewActivity.this, MyCartActivity.class);
            intent.putExtra("activityname", "ScannedImageActivity");
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });

        LinearLayout backLayout = findViewById(R.id.back_btn_layout);
        backLayout.setOnClickListener(v -> {
            onBackPressed();
        });

        RecyclerView prescriptionRecycleView = findViewById(R.id.prescription_recycler_view);
        myCartController = new MyCartController(this);
        uploadBgImageController = new UploadBgImageController(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(PrescriptionPreviewActivity.this,
                LinearLayoutManager.HORIZONTAL, false);
        prescriptionRecycleView.setLayoutManager(layoutManager);
        prescriptionList = new ArrayList<>();
        prescriptionListAdapter = new PrescriptionListAdapter(PrescriptionPreviewActivity.this, prescriptionList, PrescriptionPreviewActivity.this);
        prescriptionRecycleView.setAdapter(prescriptionListAdapter);

        Utils.showDialog(PrescriptionPreviewActivity.this, getApplicationContext().getResources().getString(R.string.label_curtion_progress_please_wait));

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("PrescriptionReceived"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverAddmore, new IntentFilter("MedicineReciver"));
        Constants.getInstance().setConnectivityListener(this);

        inItCountDownTimer();
    }

    private void inItCountDownTimer() {
        countDownTimer = new CountDownTimer(curationTimerVal, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (SessionManager.INSTANCE.getPrescriptionImageUrl().isEmpty()) {
                    handleTimeDelayDialog();
                }
            }
        }.start();
    }

    private void handleTimeDelayDialog() {
        Dialog mDialog = Utils.getDialog(PrescriptionPreviewActivity.this, R.layout.dialog_custom_alert);
        TextView dialogTitleText = mDialog.findViewById(R.id.dialog_info);
        Button okButton = mDialog.findViewById(R.id.dialog_ok);
        Button declineButton = mDialog.findViewById(R.id.dialog_cancel);
        dialogTitleText.setText(getApplicationContext().getResources().getString(R.string.label_curation_skip_alert));
        okButton.setText(getResources().getString(R.string.label_ok));
        declineButton.setText(getResources().getString(R.string.label_cancel_text));
        if (mDialog.getWindow() != null)
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mDialog.getWindow().setAttributes(params);
        mDialog.show();
        okButton.setOnClickListener(v -> {
            Utils.dismissDialog();
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            SessionManager.INSTANCE.setOrderCompletionStatus(false);
            SessionManager.INSTANCE.setCurationStatus(true);
            SessionManager.INSTANCE.setScannedImagePath("");
            SessionManager.INSTANCE.settimerlogin(true);

            Intent intent = new Intent(PrescriptionPreviewActivity.this, MyCartActivity.class);
            intent.putExtra("activityname", "ScannedImageActivity");
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });
        declineButton.setOnClickListener(v -> {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            inItCountDownTimer();
            mDialog.dismiss();
        });
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            findViewById(R.id.networkErrorLayout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.networkErrorLayout).setVisibility(View.VISIBLE);
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (message.equalsIgnoreCase("ImageName")) {
                // SessionManager.INSTANCE.setDynamicOrderId(intent.getStringExtra("imageId"));
                myCartController.handleImageService(intent.getStringExtra("data"));
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent1 = new Intent(PrescriptionPreviewActivity.this, InsertPrescriptionActivity.class);
        intent1.putExtra("activityname", "CapturedPrescriptionNew");
        startActivity(intent1);
        finish();
        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
    }

    @Override
    public void onViewClick(int position, String imagePath) {
        Utils.imageViewer(PrescriptionPreviewActivity.this, imagePath);
    }

    @Override
    public void onClosePrescriptionClick(int position) {
        PrescriptionItem item = prescriptionList.get(position);
        prescriptionList.remove(item);
        prescriptionListAdapter.notifyDataSetChanged();
    }

    private BroadcastReceiver mMessageReceiverAddmore = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (message.equals("Medicinedata")) {
                if (SessionManager.INSTANCE.getFcmMedicineReceived()) {
                    SessionManager.INSTANCE.setFcmMedicineReceived(false);
                    ScannedData fcmMedicine = new Gson().fromJson(intent.getStringExtra("MedininesNames"), ScannedData.class);
                    medConvertDialog(fcmMedicine);
                }
            }
        }
    };

    public void medConvertDialog(ScannedData fcmMedicine) {
        final Dialog dialog = new Dialog(PrescriptionPreviewActivity.this);
        dialog.setContentView(R.layout.dialog_custom_alert);
        dialog.setCancelable(false);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView dialogTitleText = dialog.findViewById(R.id.dialog_info);
        Button okButton = dialog.findViewById(R.id.dialog_ok);
        Button declineButton = dialog.findViewById(R.id.dialog_cancel);
        declineButton.setVisibility(View.GONE);
        dialogTitleText.setText(getResources().getString(R.string.label_curation_completed_alert));
        okButton.setText(getResources().getString(R.string.label_yes));
        declineButton.setText(getResources().getString(R.string.label_no));
        okButton.setOnClickListener(v -> {
            List<ScannedMedicine> scannedList = new ArrayList<>();
            for (ScannedMedicine m : fcmMedicine.getMedicineList()) {
                if (Double.parseDouble(Objects.requireNonNull(m.getArtprice())) > 0) {
                    OCRToDigitalMedicineResponse d = new OCRToDigitalMedicineResponse();
                    d.setArtName(m.getArtName());
                    d.setArtCode(m.getArtCode());
                    d.setQty(m.getQty());
                    d.setArtprice(m.getArtprice());
                    d.setContainer("");
                    dataList.add(d);
                    scannedList.add(m);
                }
            }
            dataList = removeDuplicate(dataList);
            SessionManager.INSTANCE.setScannedPrescriptionItems(scannedList);
            SessionManager.INSTANCE.setCurationStatus(false);
            if (null != SessionManager.INSTANCE.getDataList()) {
                if (SessionManager.INSTANCE.getDataList().size() > 0) {
                    List<OCRToDigitalMedicineResponse> tempCartItemList = new ArrayList<>();
                    tempCartItemList = SessionManager.INSTANCE.getDataList();
                    for (OCRToDigitalMedicineResponse item : tempCartItemList) {
                        dataList.add(item);
                    }
                }
            }
            SessionManager.INSTANCE.setDataList(dataList);
            SessionManager.INSTANCE.setScannedImagePath(scannedPrescriptionImage);
            dialog.dismiss();
            Intent intent = new Intent(PrescriptionPreviewActivity.this, MyCartActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });
        declineButton.setOnClickListener(v -> dialog.dismiss());
    }

    public List<OCRToDigitalMedicineResponse> removeDuplicate(List<OCRToDigitalMedicineResponse> dataList) {
        Set<OCRToDigitalMedicineResponse> s = new TreeSet<OCRToDigitalMedicineResponse>((o1, o2) -> {
            if (o1.getArtCode().equalsIgnoreCase(o2.getArtCode())) {
                return 0;
            }
            return 1;
        });
        s.addAll(dataList);
        return new ArrayList<>(s);
    }

    @Override
    public void onSuccessGetAddress(ArrayList<UserAddress> response) {

    }

    @Override
    public void onFailure(String message) {

    }

    @Override
    public void onSuccessProductList(HashMap<String, GetProductListResponse> productList) {

    }

    @Override
    public void onDeleteItemClicked(OCRToDigitalMedicineResponse ocrToDigitalMedicineResponse, int position) {

    }

    @Override
    public void onDeletedAddAllClicked() {

    }

    @Override
    public void onSuccessImageService(GetImageRes response) {
        Utils.dismissDialog();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        String imgUrl = response.getImageUrl();
        uploadBgImageController.handleUploadImageService(imgUrl);
        scannedPrescriptionImage = imgUrl;
        SessionManager.INSTANCE.setPrescriptionImageUrl(imgUrl);
        prescriptionList.clear();
        PrescriptionItem prescriptionItem = new PrescriptionItem(imgUrl);
        prescriptionList.add(prescriptionItem);
        prescriptionListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccessSearchItemApi(UpCellCrossCellResponse body) {

    }

    @Override
    public void onSearchFailure(String message) {

    }

    @Override
    public void onSuccessBarcodeItemApi(ItemSearchResponse itemSearchResponse, int serviceType, int qty, int position) {

    }


    @Override
    public void onFailureBarcodeItemApi(String message) {

    }

    @Override
    public void upSellCrosssellApiCall(List<UpCellCrossCellResponse.Crossselling> crossselling, List<UpCellCrossCellResponse.Upselling> upselling) {

    }

    @Override
    public void setSuccessBatchList(BatchListResponse body, int position, ItemSearchResponse.Item itemSearchData) {

    }

    @Override
    public void onSuccessCalculatePosTransactionApi(CalculatePosTransactionResponse calculatePosTransactionResponse) {

    }

    @Override
    public void onSuccessSearchItemApi(ItemSearchResponse itemSearchResponse, int position) {

    }

    @Override
    public void onSendSmsSuccess() {

    }

    @Override
    public void onSendSmsFailure() {

    }

    @Override
    public void onSuccessUploadBgImage() {
        Utils.printMessage(TAG, "Successfully Image Uploaded");
    }
}