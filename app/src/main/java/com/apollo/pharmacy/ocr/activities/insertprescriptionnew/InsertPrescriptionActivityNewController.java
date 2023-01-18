package com.apollo.pharmacy.ocr.activities.insertprescriptionnew;

import android.content.Context;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.model.PlaceOrderReqModel;
import com.apollo.pharmacy.ocr.model.PlaceOrderResModel;
import com.apollo.pharmacy.ocr.model.RecallAddressModelRequest;
import com.apollo.pharmacy.ocr.model.RecallAddressResponse;
import com.apollo.pharmacy.ocr.network.ApiClient;
import com.apollo.pharmacy.ocr.network.ApiInterface;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsertPrescriptionActivityNewController {
    private Context context;
    private InsertPrescriptionActivityNewListener mListener;

    public InsertPrescriptionActivityNewController(Context context, InsertPrescriptionActivityNewListener mListener) {
        this.context = context;
        this.mListener = mListener;
    }

    public void handleOrderPlaceService(Context context, PlaceOrderReqModel placeOrderReqModel) {
        Utils.showDialog(context, "Please wait");
        ApiInterface apiInterface = ApiClient.getApiService(Constants.Order_Place_With_Prescription_API);
        Call<PlaceOrderResModel> call = apiInterface.PLACE_ORDER_SERVICE_CALL(Constants.New_Order_Place_With_Prescription_Token, placeOrderReqModel);
//        ApiInterface apiInterface = ApiClient.getApiService("https://online.apollopharmacy.org/UAT/OrderPlace.svc/");
//        Call<PlaceOrderResModel> call = apiInterface.PLACE_ORDER_SERVICE_CALL("9f15bdd0fcd5423190c2e877ba0228APM", placeOrderReqModel);

        call.enqueue(new Callback<PlaceOrderResModel>() {
            @Override
            public void onResponse(@NotNull Call<PlaceOrderResModel> call, @NotNull Response<PlaceOrderResModel> response) {
                Utils.dismissDialog();

                if (response.body() != null && response.body().getOrdersResult().getStatus()) {
                    mListener.onSuccessPlaceOrder(response.body());
                } else if (response.body() != null && response.body().getOrdersResult().getMessage() != null && !response.body().getOrdersResult().getMessage().isEmpty()) {
                    mListener.onFailureService(response.body().getOrdersResult().getMessage());
                } else {
                    mListener.onFailureService(context.getResources().getString(R.string.label_something_went_wrong));
                }
            }

            @Override
            public void onFailure(@NotNull Call<PlaceOrderResModel> call, @NotNull Throwable t) {
                Utils.dismissDialog();
                mListener.onFailureService(t.getMessage());//context.getResources().getString(R.string.label_something_went_wrong)
            }
        });
    }

    public void getOMSCallPunchingAddressList(RecallAddressModelRequest recallAddressModelRequest) {
        String baseUrl = "";
        if (SessionManager.INSTANCE.getEposUrl().contains("EPOS")) {
            baseUrl = SessionManager.INSTANCE.getEposUrl().replace("EPOS", "ExpressDelivery");
        } else {
            baseUrl = SessionManager.INSTANCE.getEposUrl();
        }

        ApiInterface apiInterface = ApiClient.getApiService(baseUrl);
        Gson gson = new Gson();
        String json = gson.toJson(recallAddressModelRequest);
        Utils.showDialog(context, "Please wait...");
        System.out.println("RECALL_LAST_3ADDRESS===================" + json);
        Call<RecallAddressResponse> call = apiInterface.RECALL_LAST_3ADDRESS(recallAddressModelRequest);
        call.enqueue(new Callback<RecallAddressResponse>() {
            @Override
            public void onResponse(Call<RecallAddressResponse> call, Response<RecallAddressResponse> response) {
                Utils.dismissDialog();
                if (response.isSuccessful()) {
                    mListener.onSuccessRecallAddress(response.body());
                } else {
                    mListener.onFailureRecallAddress(response.body());
                }
            }

            @Override
            public void onFailure(Call<RecallAddressResponse> call, Throwable t) {
                mListener.onFailureService(context.getResources().getString(R.string.label_something_went_wrong));
                Utils.dismissDialog();
            }
        });
    }

}
