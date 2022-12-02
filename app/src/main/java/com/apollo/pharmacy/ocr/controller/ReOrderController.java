package com.apollo.pharmacy.ocr.controller;

import android.content.Context;

import androidx.annotation.NonNull;

import com.apollo.pharmacy.ocr.interfaces.ReOrderListener;
import com.apollo.pharmacy.ocr.model.BatchListRequest;
import com.apollo.pharmacy.ocr.model.BatchListResponse;
import com.apollo.pharmacy.ocr.model.ItemSearchRequest;
import com.apollo.pharmacy.ocr.model.ItemSearchResponse;
import com.apollo.pharmacy.ocr.network.ApiClient;
import com.apollo.pharmacy.ocr.network.ApiInterface;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReOrderController {

    ReOrderListener reOrderListener;
    Context activity;

    public ReOrderController(ReOrderListener reOrderListener, Context activity) {
        this.reOrderListener = reOrderListener;
        this.activity = activity;
    }

    public void searchItemProducts(String item, int position) {
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
                    reOrderListener.onSuccessSearchItemApi(itemSearchResponse, position);
                    if (itemSearchResponse != null && itemSearchResponse.getItemList().size() > 0) {
                        getBatchList(itemSearchResponse.getItemList().get(0).getArtCode(), position, itemSearchResponse.getItemList().get(0));
                    }
//                    else {
//                        getBatchList("", position, itemSearchResponse.getItemList().get(0));
//                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ItemSearchResponse> call, @NonNull Throwable t) {
                reOrderListener.onSearchFailure(t.getMessage());
            }
        });
    }

    public void getBatchList(String artcode, int position, ItemSearchResponse.Item itemSerachData) {
//        Utils.showDialog(activity, "Loading…");
        ApiInterface api = ApiClient.getApiServiceMposBaseUrl(SessionManager.INSTANCE.getEposUrl());
        BatchListRequest batchListRequest = new BatchListRequest();
        batchListRequest.setArticleCode(artcode);
        batchListRequest.setCustomerState("");
        batchListRequest.setDataAreaId(SessionManager.INSTANCE.getDataAreaId());
        batchListRequest.setSez(0);
        batchListRequest.setSearchType(1);
        batchListRequest.setStoreId(SessionManager.INSTANCE.getStoreId());
        batchListRequest.setStoreState("TS");
        batchListRequest.setExpiryDays("30");
        batchListRequest.setTerminalId(SessionManager.INSTANCE.getTerminalId());
        Call<BatchListResponse> call = api.GET_BATCH_LIST(batchListRequest);
        call.enqueue(new Callback<BatchListResponse>() {
            @Override
            public void onResponse(@NotNull Call<BatchListResponse> call, @NotNull Response<BatchListResponse> response) {
                if (response.body() != null) {
                    reOrderListener.setSuccessBatchList(response.body(), position, itemSerachData);
                }
            }

            @Override
            public void onFailure(@NotNull Call<BatchListResponse> call, @NotNull Throwable t) {
                Utils.dismissDialog();
            }
        });
    }
}
