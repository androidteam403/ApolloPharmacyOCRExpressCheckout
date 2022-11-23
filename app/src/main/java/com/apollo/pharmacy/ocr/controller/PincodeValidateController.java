package com.apollo.pharmacy.ocr.controller;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.interfaces.PincodeValidateListener;
import com.apollo.pharmacy.ocr.model.PincodeValidateResponse;
import com.apollo.pharmacy.ocr.model.ServicabilityResponse;
import com.apollo.pharmacy.ocr.model.ServiceAvailabilityRequest;
import com.apollo.pharmacy.ocr.network.ApiClient;
import com.apollo.pharmacy.ocr.network.ApiInterface;
import com.apollo.pharmacy.ocr.network.CallbackWithRetry;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.utility.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PincodeValidateController {
    PincodeValidateListener pincodeValidateListener;
    Context context;

    public PincodeValidateController(Context context, PincodeValidateListener pincodeValidateListener) {
        this.context = context;
        this.pincodeValidateListener = pincodeValidateListener;
    }

    public void onPincodeValidateApi(String pincode) {
        Utils.showDialog(context, "Loading…");
        ApiInterface api = ApiClient.getApiService();
        Map<String, Object> obj = new HashMap<>();
        obj.put("pincode", pincode);
        Call<List<PincodeValidateResponse>> call = api.doPincodeValidateApi("Bearer p8g0s5tcwz1qnqibpszco93rp36ec7mk", obj);
        call.enqueue(new Callback<List<PincodeValidateResponse>>() {
            @Override
            public void onResponse(Call<List<PincodeValidateResponse>> call, Response<List<PincodeValidateResponse>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        pincodeValidateListener.onSuccessPincodeValidate(response.body());
                        Utils.dismissDialog();
                    }
                } else {
                    pincodeValidateListener.onFailurePincode(response.body());
                    Utils.dismissDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<PincodeValidateResponse>> call, @NotNull Throwable t) {
                pincodeValidateListener.onFailurePincode(null);
                Utils.dismissDialog();
            }
        });
    }

    public void checkServiceAvailability(Context context, String inputPincode) {
        ApiInterface apiInterface = ApiClient.getApiService(Constants.CheckServiceAvailability);
     Utils.showDialog(context, "Loading…");
        Utils.hideKeyboard((Activity) context);
        ServiceAvailabilityRequest serviceAvailabilityRequest = new ServiceAvailabilityRequest();
        serviceAvailabilityRequest.setVendorName("KIOSK");
        serviceAvailabilityRequest.setPincode(inputPincode);

        Call<ServicabilityResponse> call = apiInterface.get_servicability_api(serviceAvailabilityRequest);
        call.enqueue(new CallbackWithRetry<ServicabilityResponse>(call) {
            @Override
            public void onResponse(@NonNull Call<ServicabilityResponse> call, @NonNull Response<ServicabilityResponse> response) {
              if (response.code() == 200) {
                  if (response.body() != null) {
                      if (response.body().getStatus()) {
                          pincodeValidateListener.onSuccessServiceability(response.body());
                      } else {
                          Utils.dismissDialog();
                          pincodeValidateListener.onFailureServiceability(response.body().getMessage());
                      }
                  }
              }else if(response.code()==500){
                  Utils.dismissDialog();
                   Toast.makeText(context, "Internal Server Error", Toast.LENGTH_SHORT).show();
               }

            }

            @Override
            public void onFailure(@NonNull Call<ServicabilityResponse> call, @NonNull Throwable t) {
                pincodeValidateListener.onFailureServiceability(context.getApplicationContext().getResources().getString(R.string.try_again_later));
            }
        });
    }

}
