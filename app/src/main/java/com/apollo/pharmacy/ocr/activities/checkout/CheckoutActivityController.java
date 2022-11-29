package com.apollo.pharmacy.ocr.activities.checkout;

import android.content.Context;
import android.widget.Toast;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.paymentoptions.model.ExpressCheckoutTransactionApiRequest;
import com.apollo.pharmacy.ocr.activities.paymentoptions.model.ExpressCheckoutTransactionApiResponse;
import com.apollo.pharmacy.ocr.model.GetPointDetailRequest;
import com.apollo.pharmacy.ocr.model.GetPointDetailResponse;
import com.apollo.pharmacy.ocr.model.RecallAddressModelRequest;
import com.apollo.pharmacy.ocr.model.RecallAddressResponse;
import com.apollo.pharmacy.ocr.network.ApiClient;
import com.apollo.pharmacy.ocr.network.ApiInterface;
import com.apollo.pharmacy.ocr.utility.Session;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivityController {
    private Context mContext;
    private CheckoutListener mListener;

    public CheckoutActivityController(Context mContext, CheckoutListener mListener) {
        this.mContext = mContext;
        this.mListener = mListener;
    }

    public void expressCheckoutTransactionApiCall(ExpressCheckoutTransactionApiRequest expressCheckoutTransactionApiRequest) {
        String baseUrl = "";
        if (SessionManager.INSTANCE.getEposUrl().contains("EPOS")) {
            baseUrl = SessionManager.INSTANCE.getEposUrl().replace("EPOS", "ExpressDelivery");
        } else {
            baseUrl = SessionManager.INSTANCE.getEposUrl();
        }

        ApiInterface apiInterface = ApiClient.getApiService(baseUrl);
        Gson gson = new Gson();
        String json = gson.toJson(expressCheckoutTransactionApiRequest);
        Utils.showDialog(mContext, "Please wait...");
        System.out.println("EXPRESS_CHECKOUT_TRANSACTION_API_CALL_RQUEST===================" + json);
        Call<ExpressCheckoutTransactionApiResponse> call = apiInterface.EXPRESS_CHECKOUT_TRANSACTION_API_CALL(expressCheckoutTransactionApiRequest);
        call.enqueue(new Callback<ExpressCheckoutTransactionApiResponse>() {
            @Override
            public void onResponse(@NotNull Call<ExpressCheckoutTransactionApiResponse> call, @NotNull Response<ExpressCheckoutTransactionApiResponse> response) {
                Utils.dismissDialog();
                if (response.body() != null) {
                    String jsonResponse = gson.toJson(response.body());
                    System.out.println("EXPRESS_CHECKOUT_TRANSACTION_API_CALL_RESPONSE===================" + jsonResponse);
                    if (response.body().getRequestStatus() == 0 || response.body().getRequestStatus() == 2) {
                        mListener.onSuccessexpressCheckoutTransactionApiCall(response.body());
                    } else {
                        mListener.onFailuremessage(response.body().getReturnMessage());
                    }
                } else {
                    mListener.onFailuremessage(mContext.getResources().getString(R.string.label_something_went_wrong));
                    Utils.dismissDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ExpressCheckoutTransactionApiResponse> call, @NotNull Throwable t) {
                mListener.onFailuremessage(mContext.getResources().getString(R.string.label_something_went_wrong));
                Utils.dismissDialog();

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
        Utils.showDialog(mContext, "Please wait...");
        System.out.println("EXPRESS_CHECKOUT_TRANSACTION_API_CALL_RQUEST===================" + json);
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
                mListener.onFailuremessage(mContext.getResources().getString(R.string.label_something_went_wrong));
                Utils.dismissDialog();
            }
        });
    }

    public void getPointDetail(String action, String redeem_points, String RRno, String enteredOtp) {
//        Utils.showDialog(activity, "Loading…");

        ApiInterface api = ApiClient.getApiServiceMposBaseUrl(SessionManager.INSTANCE.getEposUrl());
        GetPointDetailRequest getPointDetailRequest = new GetPointDetailRequest();
        GetPointDetailRequest.RequestData requestData= new GetPointDetailRequest.RequestData();
        requestData.setStoreId(SessionManager.INSTANCE.getStoreId());
        requestData.setDocNum("200007854");
        requestData.setMobileNum(SessionManager.INSTANCE.getMobilenumber());
        requestData.setReqBy("M");
        requestData.setPoints(redeem_points);
        requestData.setRrno(RRno);
        requestData.setOtp(enteredOtp);
        requestData.setAction(action);
        requestData.setCoupon("");
        requestData.setType("");
        requestData.setCustomerID("");
        requestData.setUrl("http://10.4.14.4:8015/AXPOS/OneApolloService.svc/ONEAPOLLOTRANS");
        getPointDetailRequest.setRequestData(requestData);


        //http://172.16.2.251:8033/PHONEPEUAT/APOLLO/PhonePe
        //http://10.4.14.7:8041/APOLLO/PhonePe
        Gson gson = new Gson();
        String json = gson.toJson(getPointDetailRequest);

        Call<GetPointDetailResponse> call = api.GET_POINT_DETAIL(getPointDetailRequest);
        call.enqueue(new Callback<GetPointDetailResponse>() {
            @Override
            public void onResponse(@NotNull Call<GetPointDetailResponse> call, @NotNull Response<GetPointDetailResponse> response) {
                if (response.body() != null && response.body().getRequestStatus()!= null && response.body().getRequestStatus()==0) {
                    mListener.onSuccessGetPointDetailResponse(response.body());
//                    Utils.dismissDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<GetPointDetailResponse> call, @NotNull Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
//                Utils.dismissDialog();
            }
        });
    }
}
