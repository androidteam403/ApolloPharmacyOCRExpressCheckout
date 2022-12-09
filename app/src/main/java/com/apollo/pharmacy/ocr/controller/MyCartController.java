package com.apollo.pharmacy.ocr.controller;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.interfaces.MyCartListener;
import com.apollo.pharmacy.ocr.model.BatchListRequest;
import com.apollo.pharmacy.ocr.model.BatchListResponse;
import com.apollo.pharmacy.ocr.model.CalculatePosTransactionRequest;
import com.apollo.pharmacy.ocr.model.CalculatePosTransactionResponse;
import com.apollo.pharmacy.ocr.model.Category_request;
import com.apollo.pharmacy.ocr.model.GetImageRes;
import com.apollo.pharmacy.ocr.model.GetProductListResponse;
import com.apollo.pharmacy.ocr.model.ItemSearchRequest;
import com.apollo.pharmacy.ocr.model.ItemSearchResponse;
import com.apollo.pharmacy.ocr.model.Send_Sms_Request;
import com.apollo.pharmacy.ocr.model.Send_Sms_Response;
import com.apollo.pharmacy.ocr.model.UpCellCrossCellRequest;
import com.apollo.pharmacy.ocr.model.UpCellCrossCellResponse;
import com.apollo.pharmacy.ocr.model.UserAddress;
import com.apollo.pharmacy.ocr.network.ApiClient;
import com.apollo.pharmacy.ocr.network.ApiInterface;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;

import static com.apollo.pharmacy.ocr.utility.Utils.dismissDialog;
import static com.apollo.pharmacy.ocr.utility.Utils.showDialog;

public class MyCartController {
    MyCartListener myCartListener;

    public MyCartController(MyCartListener listInterface) {
        myCartListener = listInterface;
    }

    public void handleImageService(String imageName) {
        ApiInterface apiInterface = ApiClient.getApiService(Constants.Get_Scanned_Prescription_Image);
        Call<GetImageRes> call = apiInterface.getScannedPrescription(Constants.Get_Scanned_Prescription_Image + imageName);
        call.enqueue(new Callback<GetImageRes>() {
            @Override
            public void onResponse(@NotNull Call<GetImageRes> call, @NotNull Response<GetImageRes> response) {
                Utils.dismissDialog();
                if (response.body() != null) {
                    myCartListener.onSuccessImageService(response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<GetImageRes> call, @NotNull Throwable t) {
                Utils.dismissDialog();
            }
        });
    }

    public void handelFetchAddressService() {
        ApiInterface apiInterface = ApiClient.getApiService(Constants.Get_User_Delivery_Address_List);
        Call<ArrayList<UserAddress>> call = apiInterface.getUserAddressList(SessionManager.INSTANCE.getMobilenumber());
        call.enqueue(new Callback<ArrayList<UserAddress>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<UserAddress>> call, @NotNull Response<ArrayList<UserAddress>> response) {
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        myCartListener.onSuccessGetAddress(response.body());
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ArrayList<UserAddress>> call, @NotNull Throwable t) {
                myCartListener.onFailure(t.getMessage());
            }
        });
    }

    public List<String> getSpecialOffersCategoryList() {
        List<String> data = new ArrayList<>();
        data.add("Promotions");
        data.add("TrendingNow");
        return data;
    }

    public Integer getHealthWellnessCategoryId(String key) {
        int id = 24;
        try {
            HashMap<String, Integer> categoryList = new HashMap<>();
            categoryList.put("Promotions", 234);
            categoryList.put("TrendingNow", 97);
            id = categoryList.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public void getProductList(String mainCategory, Context context) {
        if (null != mainCategory && mainCategory.length() > 0) {
            showDialog(context, context.getResources().getString(R.string.label_please_wait));
            ApiInterface restConnection = ApiClient.createService(ApiInterface.class, Constants.Get_Special_Offers_Products);
            List<Observable<GetProductListResponse>> observableList = setupProductNetwork(restConnection, mainCategory);
            Observable<HashMap<String, GetProductListResponse>> combined = Observable.zip(observableList, new FuncN<HashMap<String, GetProductListResponse>>() {
                @Override
                public HashMap<String, GetProductListResponse> call(Object... args) {
                    HashMap<String, GetProductListResponse> data = new HashMap<>();
                    if (mainCategory.equalsIgnoreCase("SpecialOffers")) {
                        List<String> categList = getSpecialOffersCategoryList();
                        data = prepareToListView(categList, args);
                    }
                    return data;
                }
            });

            combined.subscribe(new Subscriber<HashMap<String, GetProductListResponse>>() {

                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    dismissDialog();
                    myCartListener.onFailure(e.getMessage());
                    e.printStackTrace();
                }

                @Override
                public void onNext(HashMap<String, GetProductListResponse> o) {
                    dismissDialog();
                    myCartListener.onSuccessProductList(o);
                }
            });
        }
    }

    public List<Observable<GetProductListResponse>> setupProductNetwork(ApiInterface apiInterface, String mainCategory) {
        List<Observable<GetProductListResponse>> list = new ArrayList<>();
        if (mainCategory.equalsIgnoreCase("SpecialOffers")) {
            List<String> categList = getSpecialOffersCategoryList();
            for (String s : categList) {


                Category_request request = new Category_request();
                request.setCategoryId(getHealthWellnessCategoryId(s));
                request.setPageId(1);
                list.add(apiInterface.getProductListByCategId(request)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()));
            }
        }
        return list;
    }

    public HashMap<String, GetProductListResponse> prepareToListView(List<String> categList, Object[] args) {
        HashMap<String, GetProductListResponse> data = new HashMap<>();
        if ((null != args && args.length > 0) && (null != categList && categList.size() > 0))
            for (int i = 0; i < args.length; i++) {
                data.put(categList.get(i), (GetProductListResponse) args[i]);
            }
        return data;
    }

    public void upcellCrosscellList(String mobileNo, Context context) {
        showDialog(context, context.getResources().getString(R.string.label_please_wait));
        ApiInterface apiInterface = ApiClient.getApiService();
        UpCellCrossCellRequest upCellCrossCellRequest = new UpCellCrossCellRequest();
        upCellCrossCellRequest.setMobileno("7353910637");
        Call<UpCellCrossCellResponse> call = apiInterface.GET_UPCELL_CROSSCELL_OFEERS(upCellCrossCellRequest);
        call.enqueue(new Callback<UpCellCrossCellResponse>() {
            @Override
            public void onResponse(@NonNull Call<UpCellCrossCellResponse> call, @NonNull Response<UpCellCrossCellResponse> response) {
                if (response.isSuccessful()) {
                    myCartListener.onSuccessSearchItemApi(response.body());
                    Utils.dismissDialog();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UpCellCrossCellResponse> call, @NonNull Throwable t) {
                myCartListener.onSearchFailure(t.getMessage());
                Utils.dismissDialog();
            }
        });
    }

    public void searchItemProducts(String item, int serviceType, int qty, int position) {
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
                    if (itemSearchResponse.getItemList() != null && itemSearchResponse.getItemList().size() > 0)
                        itemSearchResponse.getItemList().get(0).setMedicineType(itemSearchResponse.getItemList().get(0).getCategory());
                    myCartListener.onSuccessBarcodeItemApi(itemSearchResponse, serviceType, qty, position);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ItemSearchResponse> call, @NonNull Throwable t) {
                myCartListener.onFailureBarcodeItemApi(t.getMessage());
            }
        });
    }

    public void searchItemProducts(String item, int position, Context mContext) {
        Utils.showDialog(mContext, "Please Wait...");
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
                if (response.isSuccessful() && response.body().getItemList().size() > 0) {
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body());
                    System.out.println("void data" + json);
                    ItemSearchResponse itemSearchResponse = response.body();
                    assert itemSearchResponse != null;
                    myCartListener.onSuccessSearchItemApi(itemSearchResponse, position);
                    if (itemSearchResponse != null && itemSearchResponse.getItemList().size() > 0) {
                        getBatchList(itemSearchResponse.getItemList().get(0).getArtCode(), position, itemSearchResponse.getItemList().get(0), mContext);
                    }
                } else if (response.isSuccessful() && response.body().getItemList().size() == 0) {
                    Utils.dismissDialog();
                    Toast.makeText(mContext, "No Items Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ItemSearchResponse> call, @NonNull Throwable t) {
                myCartListener.onSearchFailure(t.getMessage());
                Utils.dismissDialog();
            }
        });
    }

    public void getBatchList(String artcode, int position, ItemSearchResponse.Item itemSerachData, Context mContext) {
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
                    myCartListener.setSuccessBatchList(response.body(), position, itemSerachData);
                }
            }

            @Override
            public void onFailure(@NotNull Call<BatchListResponse> call, @NotNull Throwable t) {
                Utils.dismissDialog();
            }
        });
    }

    public void calculatePosTransaction(CalculatePosTransactionRequest calculatePosTransactionRequest, Context mContext) {
        Utils.showDialog(mContext, "Please Wait...");
        ApiInterface api = ApiClient.getApiServiceMposBaseUrl(SessionManager.INSTANCE.getEposUrl());
        Call<CalculatePosTransactionResponse> call = api.CALCULATE_POS_TRANSACTION_API_CALL(calculatePosTransactionRequest);
        call.enqueue(new Callback<CalculatePosTransactionResponse>() {
            @Override
            public void onResponse(@NotNull Call<CalculatePosTransactionResponse> call, @NotNull Response<CalculatePosTransactionResponse> response) {
                if (response.body() != null) {
                    Utils.dismissDialog();
                    for (int i = 0; i < calculatePosTransactionRequest.getSalesLine().size(); i++) {
                        response.body().getSalesLine().get(i).setBatchNo(calculatePosTransactionRequest.getSalesLine().get(i).getBatchNo());
                    }
                    myCartListener.onSuccessCalculatePosTransactionApi(response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<CalculatePosTransactionResponse> call, @NotNull Throwable t) {
                Utils.dismissDialog();
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
                    myCartListener.onSendSmsSuccess();
                } else {
                    myCartListener.onSendSmsFailure();
                }
            }

            @Override
            public void onFailure(Call<Send_Sms_Response> call, Throwable t) {
                myCartListener.onSendSmsFailure();
            }
        });
    }
}
