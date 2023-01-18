package com.apollo.pharmacy.ocr.activities.insertprescriptionnew.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.insertprescriptionnew.InsertPrescriptionActivityNewListener;
import com.apollo.pharmacy.ocr.databinding.AdapterPrescriptionViewPagerBinding;

import java.io.File;
import java.util.List;

public class PrescriptionListAdapter extends RecyclerView.Adapter<PrescriptionListAdapter.ViewHolder> {
    private Context context;
    private List<String> prescriptionPathList;
    private InsertPrescriptionActivityNewListener mListener;

    public PrescriptionListAdapter(Context context, List<String> prescriptionPathList, InsertPrescriptionActivityNewListener mListener) {
        this.context = context;
        this.prescriptionPathList = prescriptionPathList;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterPrescriptionViewPagerBinding prescriptionViewPagerBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.adapter_prescription_view_pager, parent, false);
        return new PrescriptionListAdapter.ViewHolder(prescriptionViewPagerBinding);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String prescriptionPath = prescriptionPathList.get(position);
        File imgFile = new File(prescriptionPath);
        if (imgFile.exists()) {
            Uri uri = Uri.fromFile(imgFile);
            holder.prescriptionViewPagerBinding.prescriptionViewPager.setImageURI(uri);
        }
        holder.prescriptionViewPagerBinding.prescriptionViewPager.setOnClickListener(v -> {
            if (mListener != null)
                mListener.onClickPrescription(prescriptionPath);
        });
        holder.prescriptionViewPagerBinding.itemCount.setText(String.valueOf(position + 1));
        holder.prescriptionViewPagerBinding.itemDelete.setOnClickListener(v -> {
            if (mListener != null)
                mListener.onClickItemDelete(position);
        });
    }

    @Override
    public int getItemCount() {
        return prescriptionPathList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AdapterPrescriptionViewPagerBinding prescriptionViewPagerBinding;

        public ViewHolder(@NonNull AdapterPrescriptionViewPagerBinding prescriptionViewPagerBinding) {
            super(prescriptionViewPagerBinding.getRoot());
            this.prescriptionViewPagerBinding = prescriptionViewPagerBinding;
        }
    }
}
