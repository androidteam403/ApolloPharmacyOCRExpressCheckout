package com.apollo.pharmacy.ocr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.databinding.ActivityOrderinProgressBinding;
import com.apollo.pharmacy.ocr.interfaces.OrderinProgressListener;
import com.apollo.pharmacy.ocr.model.OCRToDigitalMedicineResponse;
import com.apollo.pharmacy.ocr.utility.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class OrderinProgressActivity extends AppCompatActivity implements OrderinProgressListener {

    private ActivityOrderinProgressBinding orderinProgressBinding;
    private List<OCRToDigitalMedicineResponse> dataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderinProgressBinding = DataBindingUtil.setContentView(this, R.layout.activity_orderin_progress);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        orderinProgressBinding.setCallback(this);
        setUp();
    }

    private void setUp() {
        OrderinProgresssuiModel orderinProgresssuiModel = new OrderinProgresssuiModel();
        if (null != SessionManager.INSTANCE.getDataList())
            this.dataList = SessionManager.INSTANCE.getDataList();
        if (dataList != null && dataList.size() > 0) {
            int pharmaMedicineCount = 0;
            int fmcgMedicineCount = 0;
            double pharmaTotal = 0.0;
            double fmcgTotal = 0.0;
            boolean isFmcg = false;
            boolean isPharma = false;
            for (OCRToDigitalMedicineResponse data : dataList) {
                if (data.getMedicineType() != null) {
                    if (data.getMedicineType().equals("PHARMA")) {
                        isPharma = true;
                        pharmaMedicineCount++;
                        pharmaTotal = pharmaTotal + (Double.parseDouble(data.getArtprice()) * data.getQty());
                    } else {
                        isFmcg = true;
                        fmcgMedicineCount++;
                        fmcgTotal = fmcgTotal + (Double.parseDouble(data.getArtprice()) * data.getQty());
                    }
                }
            }
//            fmcgToatalPass = fmcgTotal;
            orderinProgresssuiModel.setPharmaCount(String.valueOf(pharmaMedicineCount));
            orderinProgresssuiModel.setFmcgCount(String.valueOf(fmcgMedicineCount));
            orderinProgresssuiModel.setPharmaTotal(getResources().getString(R.string.rupee) + String.valueOf(pharmaTotal));
            orderinProgresssuiModel.setFmcgTotal(getResources().getString(R.string.rupee) + String.valueOf(fmcgTotal));
            orderinProgresssuiModel.setTotalMedicineCount(String.valueOf(dataList.size()));
            orderinProgresssuiModel.setMedicineTotal(getResources().getString(R.string.rupee) + String.valueOf(pharmaTotal + fmcgTotal));
            orderinProgresssuiModel.setFmcgPharma(isPharma && isFmcg);
            orderinProgresssuiModel.setFmcg(isFmcg);
            orderinProgresssuiModel.setPharma(isPharma);
            orderinProgressBinding.setModel(orderinProgresssuiModel);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onClickContinueShopping();
    }

    @Override
    public void onClickContinueShopping() {
        Toast.makeText(this, "You clicked", Toast.LENGTH_SHORT).show();
        List<OCRToDigitalMedicineResponse> dataList = new ArrayList<>();
        SessionManager.INSTANCE.setDataList(dataList);
        Intent intent = new Intent(OrderinProgressActivity.this, MySearchActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
    }

    public class OrderinProgresssuiModel {
        private String pharmaCount;
        private String fmcgCount;
        private String fmcgTotal;
        private String pharmaTotal;
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
}
