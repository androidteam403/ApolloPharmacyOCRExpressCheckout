package com.apollo.pharmacy.ocr.controller;

import android.content.Context;

import androidx.annotation.NonNull;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.userlogin.model.GetGlobalConfigurationResponse;
import com.apollo.pharmacy.ocr.interfaces.HomeListener;
import com.apollo.pharmacy.ocr.interfaces.UserLoginListener;
import com.apollo.pharmacy.ocr.model.AddFCMTokenRequest;
import com.apollo.pharmacy.ocr.model.Categorylist_Response;
import com.apollo.pharmacy.ocr.model.Global_api_request;
import com.apollo.pharmacy.ocr.model.Global_api_response;
import com.apollo.pharmacy.ocr.model.ItemSearchRequest;
import com.apollo.pharmacy.ocr.model.ItemSearchResponse;
import com.apollo.pharmacy.ocr.model.Meta;
import com.apollo.pharmacy.ocr.model.PortFolioModel;
import com.apollo.pharmacy.ocr.model.Send_Sms_Request;
import com.apollo.pharmacy.ocr.model.Send_Sms_Response;
import com.apollo.pharmacy.ocr.network.ApiClient;
import com.apollo.pharmacy.ocr.network.ApiInterface;
import com.apollo.pharmacy.ocr.network.CallbackWithRetry;
import com.apollo.pharmacy.ocr.utility.ApplicationConstant;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.utility.Session;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivityController {
    HomeListener homeListener;
    Context context;

    public HomeActivityController(HomeListener listInterface, Context context) {
        homeListener = listInterface;
        this.context=context;
    }

    public void handleSendSmsApi(Send_Sms_Request request) {
        ApiInterface api = ApiClient.getApiService(Constants.Send_Sms_Api);
        Call<Send_Sms_Response> call = api.send_sms_api(request);

        call.enqueue(new Callback<Send_Sms_Response>() {
            @Override
            public void onResponse(Call<Send_Sms_Response> call, Response<Send_Sms_Response> response) {
                if (response.isSuccessful()) {
                    homeListener.onSendSmsSuccess();
                } else {
                    homeListener.onSendSmsFailure();
                }
            }

            @Override
            public void onFailure(Call<Send_Sms_Response> call, Throwable t) {
                homeListener.onSendSmsFailure();
            }
        });
    }
    public void  getGlobalApiList() {
        Global_api_request request = new Global_api_request();
        request.setDEVICEID( Utils.getDeviceId(context));
        request.setKEY("2028");
        ApiInterface apiInterface = ApiClient.getApiService(ApplicationConstant.Global_api_url);
        Call<Global_api_response> call = apiInterface.get_global_apis(request);

        call.enqueue(new Callback<Global_api_response>() {
            @Override
            public void onResponse(Call<Global_api_response> call, Response<Global_api_response> response) {
                if (response.isSuccessful()) {
                    homeListener.onSuccessGlobalApiResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<Global_api_response> call, Throwable t) {
                homeListener.onFailure(t.getMessage());
            }
        });

    }

    public void handleFCMTokenRegistration(String token) {
        AddFCMTokenRequest addFCMTokenRequest = new AddFCMTokenRequest(token, SessionManager.INSTANCE.getKioskSetupResponse().getKIOSK_ID());
        ApiInterface apiInterface = ApiClient.getApiService(Constants.Add_FCM_Token);
        Call<Meta> call = apiInterface.addFcmToken(addFCMTokenRequest);

        call.enqueue(new Callback<Meta>() {
            @Override
            public void onResponse(Call<Meta> call, Response<Meta> response) {
                Meta m = response.body();
                //                assert m != null;
                if (m != null && m.getStatusCode() == 200) {
                    SessionManager.INSTANCE.addFcmLog();
                }
            }

            @Override
            public void onFailure(Call<Meta> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    public void  getGlobalConfigurationApiCall() {
        Utils.showDialog(context, "Loading…");

        ApiInterface apiInterface = ApiClient.getApiService(SessionManager.INSTANCE.getEposUrl());
        Call<GetGlobalConfigurationResponse> call = apiInterface.GET_GLOBAL_CONFING_API_CALL(SessionManager.INSTANCE.getStoreId(), SessionManager.INSTANCE.getTerminalId(), SessionManager.INSTANCE.getDataAreaId(), new Object());
//        val api = ApiClient.getApiService(SessionManager.getEposUrl());
//        val call = api.GET_GLOBAL_CONFING_API_CALL(getStoreId(), getTerminalId(), getDataAreaId(), Object())
        call.enqueue(new Callback<GetGlobalConfigurationResponse>() {
            @Override
            public void onResponse(Call<GetGlobalConfigurationResponse> call, @NotNull Response<GetGlobalConfigurationResponse> response) {
                Utils.dismissDialog();
                if (response.body()!= null && response.body().getRequestStatus() == 0) {
                    SessionManager.INSTANCE.setDataAreaId(response.body().getDataAreaID());
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());

                    SessionManager.INSTANCE.setGlobalConfigurationResponse(json);
                    homeListener.onSuccessGlobalConfigurationApiCall(response.body());
                }
                else if (response.body() != null) {
                    homeListener.onFailureConfigApi(response.body().getReturnMessage());
                }
            }

            @Override
            public void onFailure(Call<GetGlobalConfigurationResponse> call, Throwable t) {
                Utils.dismissDialog();
                homeListener.onFailureConfigApi(t.getMessage());
            }
        });

    }

    public void handleRedeemPoints(Context context) {
        ApiInterface apiInterface = ApiClient.getApiService(Constants.Get_Portfolio_of_the_User);
        Call<PortFolioModel> call = apiInterface.getPortFolio(SessionManager.INSTANCE.getMobilenumber(), "true", "Apollo pharmacy");
        call.enqueue(new CallbackWithRetry<PortFolioModel>(call) {
            @Override
            public void onResponse(@NotNull Call<PortFolioModel> call, @NotNull Response<PortFolioModel> response) {
                if (response.isSuccessful()) {
                    handleGetCategoryListService(context);
                    assert response.body() != null;
                    if (response.body().getCustomerData() != null && response.body().getCustomerData().getName() != null && !response.body().getCustomerData().getName().isEmpty())
                        SessionManager.INSTANCE.setCustName(response.body().getCustomerData().getName());
                    else
                        SessionManager.INSTANCE.setCustName("");
                    homeListener.onSuccessRedeemPoints(response.body());
                } else {
                    homeListener.onFailureService(context.getResources().getString(R.string.label_something_went_wrong));
                }
            }

            @Override
            public void onFailure(@NonNull Call<PortFolioModel> call, @NonNull Throwable t) {
                Utils.dismissDialog();
                homeListener.onFailureService(t.getMessage());
            }
        });
    }

    public void handleGetCategoryListService(Context context) {
        ApiInterface apiInterface = ApiClient.getApiService(Constants.Categorylist_Url + "/");
        Call<Categorylist_Response> call = apiInterface.getCategorylist(Constants.Categorylist_Url);
        call.enqueue(new Callback<Categorylist_Response>() {
            @Override
            public void onResponse(@NotNull Call<Categorylist_Response> call, @NotNull Response<Categorylist_Response> response) {
                Utils.dismissDialog();
                if (response.body() != null) {
                    homeListener.categoryListSuccessRes(response.body());
                } else {
                    homeListener.onFailureService(context.getResources().getString(R.string.label_something_went_wrong));
                }
            }

            @Override
            public void onFailure(@NotNull Call<Categorylist_Response> call, @NotNull Throwable t) {
                Utils.dismissDialog();
                homeListener.onFailureService(t.getMessage());
            }
        });
    }

    public void searchItemProducts(String item) {
        ApiInterface apiInterface = ApiClient.getApiServiceMposBaseUrl(SessionManager.INSTANCE.getEposUrl());
        ItemSearchRequest itemSearchRequest = new ItemSearchRequest();
        itemSearchRequest.setCorpCode("0");
        itemSearchRequest.setIsGeneric(false);
        itemSearchRequest.setIsInitial(true);
        itemSearchRequest.setIsStockCheck(SessionManager.INSTANCE.getGlobalConfigurationResponse().getDisplayStockItems());
        itemSearchRequest.setSearchString(item);
        itemSearchRequest.setStoreID(SessionManager.INSTANCE.getStoreId());
        Call<ItemSearchResponse> call = apiInterface.getSearchItemApiCall(itemSearchRequest);
        call.enqueue(new Callback<ItemSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<ItemSearchResponse> call, @NonNull Response<ItemSearchResponse> response) {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
                    System.out.println("void data" + json);
                    ItemSearchResponse itemSearchResponse = response.body();
                    assert itemSearchResponse != null;
                    homeListener.onSuccessSearchItemApi(itemSearchResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ItemSearchResponse> call, @NonNull Throwable t) {
                Utils.dismissDialog();
                homeListener.onSearchFailure(t.getMessage());
            }
        });
    }

}
