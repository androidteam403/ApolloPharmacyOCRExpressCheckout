package com.apollo.pharmacy.ocr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.checkout.CheckoutListener;
import com.apollo.pharmacy.ocr.interfaces.PhonePayQrCodeListener;
import com.apollo.pharmacy.ocr.model.RecallAddressResponse;

import java.util.List;

public class LastThreeAddressAdapter extends RecyclerView.Adapter<LastThreeAddressAdapter.MyViewHolder> {
    private Context context;
    private List<RecallAddressResponse.CustomerDetail> last3AddressList;
    private CheckoutListener checkoutListenerl;
    private PhonePayQrCodeListener phonePayQrCodeListenerl;

    public LastThreeAddressAdapter(Context applicationContext, List<RecallAddressResponse.CustomerDetail> last3Address, CheckoutListener checkoutListener, PhonePayQrCodeListener phonePayQrCodeListener) {
        context = applicationContext;
        last3AddressList = last3Address;
        checkoutListenerl = checkoutListener;
        phonePayQrCodeListenerl= phonePayQrCodeListener;
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
        String onlyAddress;
        onlyAddress=last3AddressList.get(position).getAddress1() + " " + last3AddressList.get(position).getAddress2() + " " + last3AddressList.get(position).getLandMark();
        if (last3AddressList.get(position).getAddress1().contains(",")) {
            address = last3AddressList.get(position).getAddress1() + " " + last3AddressList.get(position).getAddress2() + " " + last3AddressList.get(position).getCity() + " " + last3AddressList.get(position).getLandMark() + " " + last3AddressList.get(position).getPostalCode() + " " + last3AddressList.get(position).getState() + "-" + last3AddressList.get(position).getPhoneNumber();

//             address = last3AddressList.get(position).getAddress1() + last3AddressList.get(position).getAddress2() + last3AddressList.get(position).getCity() + last3AddressList.get(position).getLandMark() + last3AddressList.get(position).getPostalCode() + last3AddressList.get(position).getState();
        } else {
            address = last3AddressList.get(position).getAddress1() + "," + last3AddressList.get(position).getAddress2() + "," + last3AddressList.get(position).getCity() + "," + last3AddressList.get(position).getLandMark() + "," + last3AddressList.get(position).getPostalCode() + "," + last3AddressList.get(position).getState() + "-" + last3AddressList.get(position).getPhoneNumber();
        }
        holder.addressLastThree.setText(address);

//        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                                                       @Override
//                                                       public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                                                           if (isChecked) {
//                                                               checkoutListenerl.onClickLastThreeAddresses(address, last3Address.getPhoneNumber(), last3Address.getPostalCode(), last3Address.getCity(), last3Address.getState(), last3Address.getName());
//                                                           }
//                                                       }
//                                                   }

//        );

        holder.selectAndContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkoutListenerl!=null){
                    checkoutListenerl.onClickLastThreeAddresses(address, last3Address.getPhoneNumber(), last3Address.getPostalCode(), last3Address.getCity(), last3Address.getState(), last3Address.getName(), last3Address.getAddress1(), last3Address.getAddress2(),onlyAddress);
                }else if(phonePayQrCodeListenerl!=null){
                    phonePayQrCodeListenerl.onClickLastThreeAddresses(address, last3Address.getPhoneNumber(), last3Address.getPostalCode(), last3Address.getCity(), last3Address.getState(), last3Address.getName(), last3Address.getAddress1(), last3Address.getAddress2(),onlyAddress);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return last3AddressList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView addressLastThree;
        private LinearLayout selectAndContinueButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            addressLastThree = itemView.findViewById(R.id.addressLastThree);
            selectAndContinueButton = itemView.findViewById(R.id.select_and_continue_button);
        }
    }
}
