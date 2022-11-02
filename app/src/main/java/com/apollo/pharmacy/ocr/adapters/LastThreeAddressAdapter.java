package com.apollo.pharmacy.ocr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.checkout.CheckoutActivity;
import com.apollo.pharmacy.ocr.activities.checkout.CheckoutListener;
import com.apollo.pharmacy.ocr.model.RecallAddressResponse;

import java.util.ArrayList;
import java.util.List;

public class LastThreeAddressAdapter extends RecyclerView.Adapter<LastThreeAddressAdapter.MyViewHolder> {
    private Context context;
    private List<RecallAddressResponse.CustomerDetail> last3AddressList;
    private CheckoutListener checkoutListenerl;

    public LastThreeAddressAdapter(Context applicationContext, List<RecallAddressResponse.CustomerDetail> last3Address, CheckoutListener checkoutListener) {
        context = applicationContext;
        last3AddressList = last3Address;
        checkoutListenerl = checkoutListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_last3address, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        RecallAddressResponse.CustomerDetail last3Address = last3AddressList.get(position);
        String address;
        if (last3AddressList.get(position).getAddress1().contains(",")) {
            address = last3AddressList.get(position).getAddress1() + " " + last3AddressList.get(position).getAddress2() + " " + last3AddressList.get(position).getCity() + " " + last3AddressList.get(position).getLandMark() + " " + last3AddressList.get(position).getPostalCode() + " " + last3AddressList.get(position).getState();

//             address = last3AddressList.get(position).getAddress1() + last3AddressList.get(position).getAddress2() + last3AddressList.get(position).getCity() + last3AddressList.get(position).getLandMark() + last3AddressList.get(position).getPostalCode() + last3AddressList.get(position).getState();
        } else {
            address = last3AddressList.get(position).getAddress1() + "," + last3AddressList.get(position).getAddress2() + "," + last3AddressList.get(position).getCity() + "," + last3AddressList.get(position).getLandMark() + "," + last3AddressList.get(position).getPostalCode() + "," + last3AddressList.get(position).getState();
        }
        holder.checkBox.setText(address);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                       @Override
                                                       public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                           if (isChecked) {
                                                               checkoutListenerl.onClickLastThreeAddresses(address, last3Address.getPhoneNumber(), last3Address.getPostalCode(), last3Address.getCity(), last3Address.getState(), last3Address.getName());
                                                           }
                                                       }
                                                   }

        );


    }

    @Override
    public int getItemCount() {
        return last3AddressList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.addressText);
        }
    }
}
