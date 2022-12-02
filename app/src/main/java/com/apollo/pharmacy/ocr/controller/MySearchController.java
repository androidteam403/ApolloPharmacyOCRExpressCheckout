package com.apollo.pharmacy.ocr.controller;

import androidx.annotation.NonNull;

import com.apollo.pharmacy.ocr.interfaces.MyOffersListener;
import com.apollo.pharmacy.ocr.interfaces.MySearchCallback;
import com.apollo.pharmacy.ocr.model.ItemSearchRequest;
import com.apollo.pharmacy.ocr.model.ItemSearchResponse;
import com.apollo.pharmacy.ocr.model.Send_Sms_Request;
import com.apollo.pharmacy.ocr.model.Send_Sms_Response;
import com.apollo.pharmacy.ocr.model.SubCategoryItemModel;
import com.apollo.pharmacy.ocr.network.ApiClient;
import com.apollo.pharmacy.ocr.network.ApiInterface;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MySearchController {
    MyOffersListener myOffersListener;
    MySearchCallback mySearchCallback;

    public MySearchController(MyOffersListener listInterface,MySearchCallback mySearchCallback) {
        myOffersListener = listInterface;
        this.mySearchCallback = mySearchCallback;
    }



    public void setFMCGListData(ArrayList<SubCategoryItemModel> subCategoryItemArr) {
        subCategoryItemArr.clear();
        SubCategoryItemModel subCategoryItemModel = new SubCategoryItemModel();
        subCategoryItemModel.setSubCategoryName("Baby Care");
        subCategoryItemModel.setSubCategoryId(Constants.Baby_Care);
        subCategoryItemModel.setSelected(true);
        subCategoryItemArr.add(subCategoryItemModel);

        subCategoryItemModel = new SubCategoryItemModel();
        subCategoryItemModel.setSubCategoryName("Health Monitoring Devices");
        subCategoryItemModel.setSubCategoryId(Constants.Health_Monitoring_Devices);
        subCategoryItemModel.setSelected(false);
        subCategoryItemArr.add(subCategoryItemModel);

        subCategoryItemModel = new SubCategoryItemModel();
        subCategoryItemModel.setSubCategoryName("First Aid");
        subCategoryItemModel.setSubCategoryId(Constants.First_Aid);
        subCategoryItemModel.setSelected(false);
        subCategoryItemArr.add(subCategoryItemModel);

        subCategoryItemModel = new SubCategoryItemModel();
        subCategoryItemModel.setSubCategoryName("Health Foods & Drinks");
        subCategoryItemModel.setSubCategoryId(Constants.Health_Foods_Drinks);
        subCategoryItemModel.setSelected(false);
        subCategoryItemArr.add(subCategoryItemModel);

        subCategoryItemModel = new SubCategoryItemModel();
        subCategoryItemModel.setSubCategoryName("Beauty & Hygiene");
        subCategoryItemModel.setSubCategoryId(Constants.Beauty_Hygiene);
        subCategoryItemModel.setSelected(false);
        subCategoryItemArr.add(subCategoryItemModel);

        subCategoryItemModel = new SubCategoryItemModel();
        subCategoryItemModel.setSubCategoryName("OTC");
        subCategoryItemModel.setSubCategoryId(Constants.OTC);
        subCategoryItemModel.setSelected(false);
        subCategoryItemArr.add(subCategoryItemModel);

        subCategoryItemModel = new SubCategoryItemModel();
        subCategoryItemModel.setSubCategoryName("General Wellnes");
        subCategoryItemModel.setSubCategoryId(Constants.General_Wellness);
        subCategoryItemModel.setSelected(false);
        subCategoryItemArr.add(subCategoryItemModel);

        subCategoryItemModel = new SubCategoryItemModel();
        subCategoryItemModel.setSubCategoryName("Mobility Aids & Rehabilitation");
        subCategoryItemModel.setSubCategoryId(Constants.Mobility_Aids_Rehabilitation);
        subCategoryItemModel.setSelected(false);
        subCategoryItemArr.add(subCategoryItemModel);

        subCategoryItemModel = new SubCategoryItemModel();
        subCategoryItemModel.setSubCategoryName("Nutrition Supplement");
        subCategoryItemModel.setSubCategoryId(Constants.Nutrition_Supplement);
        subCategoryItemModel.setSelected(false);
        subCategoryItemArr.add(subCategoryItemModel);
    }

    public void setPharmacyListData(ArrayList<SubCategoryItemModel> subCategoryItemArr) {
        subCategoryItemArr.clear();
        SubCategoryItemModel subCategoryItemModel = new SubCategoryItemModel();
        subCategoryItemModel.setSubCategoryName("Anti allergic drugs");
        subCategoryItemModel.setSubCategoryId(Constants.Anti_allergic_drugs);
        subCategoryItemModel.setSelected(true);
        subCategoryItemArr.add(subCategoryItemModel);

        subCategoryItemModel = new SubCategoryItemModel();
        subCategoryItemModel.setSubCategoryName("Infections & Infestation");
        subCategoryItemModel.setSubCategoryId(Constants.Infections_Infestation);
        subCategoryItemModel.setSelected(false);
        subCategoryItemArr.add(subCategoryItemModel);

        subCategoryItemModel = new SubCategoryItemModel();
        subCategoryItemModel.setSubCategoryName("C.N.S Drugs");
        subCategoryItemModel.setSubCategoryId(Constants.CNS_Drugs);
        subCategoryItemModel.setSelected(false);
        subCategoryItemArr.add(subCategoryItemModel);

        subCategoryItemModel = new SubCategoryItemModel();
        subCategoryItemModel.setSubCategoryName("Generic");
        subCategoryItemModel.setSubCategoryId(Constants.Generic);
        subCategoryItemModel.setSelected(false);
        subCategoryItemArr.add(subCategoryItemModel);

        subCategoryItemModel = new SubCategoryItemModel();
        subCategoryItemModel.setSubCategoryName("Diabetics");
        subCategoryItemModel.setSubCategoryId(Constants.Diabetics);
        subCategoryItemModel.setSelected(false);
        subCategoryItemArr.add(subCategoryItemModel);

        subCategoryItemModel = new SubCategoryItemModel();
        subCategoryItemModel.setSubCategoryName("Cardiology");
        subCategoryItemModel.setSubCategoryId(Constants.Cardiology);
        subCategoryItemModel.setSelected(false);
        subCategoryItemArr.add(subCategoryItemModel);

        subCategoryItemModel = new SubCategoryItemModel();
        subCategoryItemModel.setSubCategoryName("Skin disorders");
        subCategoryItemModel.setSubCategoryId(Constants.Skin_disorders);
        subCategoryItemModel.setSelected(false);
        subCategoryItemArr.add(subCategoryItemModel);

        subCategoryItemModel = new SubCategoryItemModel();
        subCategoryItemModel.setSubCategoryName("Gastro entrology");
        subCategoryItemModel.setSubCategoryId(Constants.Gastro_entrology);
        subCategoryItemModel.setSelected(false);
        subCategoryItemArr.add(subCategoryItemModel);

        subCategoryItemModel = new SubCategoryItemModel();
        subCategoryItemModel.setSubCategoryName("Endocrine disorders");
        subCategoryItemModel.setSubCategoryId(Constants.Endocrine_disorders);
        subCategoryItemModel.setSelected(false);
        subCategoryItemArr.add(subCategoryItemModel);
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
                    mySearchCallback.onSuccessBarcodeItemApi(itemSearchResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ItemSearchResponse> call, @NonNull Throwable t) {
                mySearchCallback.onFailureBarcodeItemApi(t.getMessage());
            }
        });
    }

    public void handleSendSmsApi(Send_Sms_Request request) {
        ApiInterface api = ApiClient.getApiService(Constants.Send_Sms_Api);
        Call<Send_Sms_Response> call = api.send_sms_api(request);

        call.enqueue(new Callback<Send_Sms_Response>() {
            @Override
            public void onResponse(Call<Send_Sms_Response> call, Response<Send_Sms_Response> response) {
                if (response.isSuccessful()) {
                    mySearchCallback.onSendSmsSuccess();
                } else {
                    mySearchCallback.onSendSmsFailure();
                }
            }

            @Override
            public void onFailure(Call<Send_Sms_Response> call, Throwable t) {
                myOffersListener.onSendSmsFailure();
            }
        });
    }

}
