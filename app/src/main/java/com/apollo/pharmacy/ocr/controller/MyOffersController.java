package com.apollo.pharmacy.ocr.controller;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.interfaces.MyOffersListener;
import com.apollo.pharmacy.ocr.model.AllOffersResponse;
import com.apollo.pharmacy.ocr.model.BatchListRequest;
import com.apollo.pharmacy.ocr.model.BatchListResponse;
import com.apollo.pharmacy.ocr.model.CalculatePosTransactionRequest;
import com.apollo.pharmacy.ocr.model.CalculatePosTransactionResponse;
import com.apollo.pharmacy.ocr.model.Category_request;
import com.apollo.pharmacy.ocr.model.GetProductListResponse;
import com.apollo.pharmacy.ocr.model.GroupOffersModelResponse;
import com.apollo.pharmacy.ocr.model.ItemSearchRequest;
import com.apollo.pharmacy.ocr.model.ItemSearchResponse;
import com.apollo.pharmacy.ocr.model.NewSearchapirequest;
import com.apollo.pharmacy.ocr.model.NewSearchapiresponse;
import com.apollo.pharmacy.ocr.model.Searchsuggestionrequest;
import com.apollo.pharmacy.ocr.model.Searchsuggestionresponse;
import com.apollo.pharmacy.ocr.model.Send_Sms_Request;
import com.apollo.pharmacy.ocr.model.Send_Sms_Response;
import com.apollo.pharmacy.ocr.model.UpCellCrossCellRequest;
import com.apollo.pharmacy.ocr.model.UpCellCrossCellResponse;
import com.apollo.pharmacy.ocr.network.ApiClient;
import com.apollo.pharmacy.ocr.network.ApiInterface;
import com.apollo.pharmacy.ocr.network.CallbackWithRetry;
import com.apollo.pharmacy.ocr.utility.ApplicationConstant;
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

import static com.apollo.pharmacy.ocr.utility.Constants.Promotions;
import static com.apollo.pharmacy.ocr.utility.Utils.dismissDialog;
import static com.apollo.pharmacy.ocr.utility.Utils.showDialog;

public class MyOffersController {
    MyOffersListener myOffersListener;
    private Context mContext;

    public MyOffersController(MyOffersListener listInterface, Context mContext) {
        myOffersListener = listInterface;
        this.mContext = mContext;
    }

    public void searchSuggestion(Searchsuggestionrequest q, MyOffersListener myOffersListener) {
        ApiInterface apiInterface = ApiClient.getApiService(Constants.Get_Special_Offers_Products);
        Call<Searchsuggestionresponse> call = apiInterface.searchsuggestion(q);
        call.enqueue(new CallbackWithRetry<Searchsuggestionresponse>(call) {
            @Override
            public void onResponse(@NonNull Call<Searchsuggestionresponse> call, @NonNull Response<Searchsuggestionresponse> response) {
                if (response.isSuccessful()) {
                    Searchsuggestionresponse productSearchResponse = response.body();
                    assert productSearchResponse != null;
                    myOffersListener.onSearchSuggestionList(productSearchResponse.getProducts());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Searchsuggestionresponse> call, @NonNull Throwable t) {
                myOffersListener.onSearchFailure(t.getMessage());
            }
        });
    }

    public void newSearchProduct(NewSearchapirequest q, MyOffersListener myOffersListener) {
        ApiInterface apiInterface = ApiClient.getApiService(Constants.Get_Special_Offers_Products);
        Call<NewSearchapiresponse> call = apiInterface.newsearchapi(q);
        call.enqueue(new CallbackWithRetry<NewSearchapiresponse>(call) {
            @Override
            public void onResponse(@NonNull Call<NewSearchapiresponse> call, @NonNull Response<NewSearchapiresponse> response) {
                if (response.isSuccessful()) {
                    NewSearchapiresponse productSearchResponse = response.body();
                    assert productSearchResponse != null;
                    myOffersListener.onNewSearchResult(productSearchResponse.getProducts());
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewSearchapiresponse> call, @NonNull Throwable t) {
                myOffersListener.onSearchFailure(t.getMessage());
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
            categoryList.put("Promotions", Promotions);
            id = categoryList.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public void getProductList(String mainCategory, Context context, MyOffersListener myOffersListener, int categoryId) {
        if (null != mainCategory && mainCategory.length() > 0) {
            List<Observable<GetProductListResponse>> observableList;
            showDialog(context, context.getResources().getString(R.string.label_please_wait));
            ApiInterface restConnection = ApiClient.createService(ApiInterface.class, Constants.Get_Special_Offers_Products);
            observableList = setupProductNetwork(restConnection, mainCategory, context, categoryId);
            Observable<HashMap<String, GetProductListResponse>> combined = Observable.zip(observableList, new FuncN<HashMap<String, GetProductListResponse>>() {
                @Override
                public HashMap<String, GetProductListResponse> call(Object... args) {
                    HashMap<String, GetProductListResponse> data = new HashMap<>();
                    List<String> categList = getSpecialOffersCategoryList();
                    data = prepareToListView(categList, args);
                    return data;
                }
            });

            combined.subscribe(new Subscriber<HashMap<String, GetProductListResponse>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
//                    dismissDialog();
                    myOffersListener.onFailure(e.getMessage());
                    e.printStackTrace();
                }

                @Override
                public void onNext(HashMap<String, GetProductListResponse> o) {
//                    dismissDialog();
                    myOffersListener.onSuccessProductList(o);
                }
            });
        }
    }

    public List<Observable<GetProductListResponse>> setupProductNetwork(ApiInterface apiInterface, String mainCategory, Context context, int categoryId) {
        List<Observable<GetProductListResponse>> list = new ArrayList<>();

        Category_request request = new Category_request();
        request.setCategoryId(categoryId);
        request.setPageId(1);
        list.add(apiInterface.getProductListByCategId(request)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()));
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

    public void handleLoadMoreTrendingNowService(int cId, int currentPage) {
        ApiInterface apiInterface = ApiClient.getApiService(ApplicationConstant.Testmonials_Url);
        Call<GetProductListResponse> call = apiInterface.getProductListByCategIdLoadMore(cId, String.valueOf(currentPage), "category");
        call.enqueue(new CallbackWithRetry<GetProductListResponse>(call) {
            @Override
            public void onResponse(@NonNull Call<GetProductListResponse> call, @NonNull Response<GetProductListResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        myOffersListener.onSuccessLoadMoreTrendingNow(response.body());
                    }
                } else {
                    myOffersListener.onFailureLoadMoreTrendingNow(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetProductListResponse> call, @NonNull Throwable t) {
                myOffersListener.onFailureLoadMoreTrendingNow(t.getMessage());
            }
        });
    }

    public void handleLoadMorePromotionsService(int cId, int currentPage) {
        ApiInterface apiInterface = ApiClient.getApiService(ApplicationConstant.Testmonials_Url);
        Call<GetProductListResponse> call = apiInterface.getProductListByCategIdLoadMore(cId, String.valueOf(currentPage), "category");
        call.enqueue(new CallbackWithRetry<GetProductListResponse>(call) {
            @Override
            public void onResponse(@NonNull Call<GetProductListResponse> call, @NonNull Response<GetProductListResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        myOffersListener.onSuccessLoadMorePromotions(response.body());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetProductListResponse> call, @NonNull Throwable t) {
                myOffersListener.onFailureLoadMorePromotions(t.getMessage());
            }
        });
    }

    public void searchItemProducts(String item) {
        ApiInterface apiInterface = ApiClient.getApiServiceMposBaseUrl(SessionManager.INSTANCE.getEposUrl());
        ItemSearchRequest itemSearchRequest = new ItemSearchRequest();
        itemSearchRequest.setCorpCode("0");
        itemSearchRequest.setIsGeneric(false);
        itemSearchRequest.setIsInitial(true);
        itemSearchRequest.setIsStockCheck(true);
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
                    myOffersListener.onSuccessSearchItemApi(itemSearchResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ItemSearchResponse> call, @NonNull Throwable t) {
                myOffersListener.onSearchFailure(t.getMessage());
            }
        });
    }

    public void upcellCrosscellList(String mobileNo, Context context) {
        ApiInterface apiInterface = ApiClient.getApiService();
        showDialog(context, context.getResources().getString(R.string.label_please_wait));
        UpCellCrossCellRequest upCellCrossCellRequest = new UpCellCrossCellRequest();
        upCellCrossCellRequest.setMobileno("7353910637");
        Call<UpCellCrossCellResponse> call = apiInterface.GET_UPCELL_CROSSCELL_OFEERS(upCellCrossCellRequest);
        call.enqueue(new Callback<UpCellCrossCellResponse>() {
            @Override
            public void onResponse(@NonNull Call<UpCellCrossCellResponse> call, @NonNull Response<UpCellCrossCellResponse> response) {
                if (response.isSuccessful()) {
//                    dismissDialog();
                    myOffersListener.onSuccessSearchUpcellCroscellApi(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UpCellCrossCellResponse> call, @NonNull Throwable t) {
                dismissDialog();
                myOffersListener.onSearchFailureUpcellCroscell(t.getMessage());
            }
        });
    }

    public void getAllOffersApiCall() {
        ApiInterface apiInterface = ApiClient.getApiService();
        showDialog(mContext, mContext.getResources().getString(R.string.label_please_wait));
        Call<AllOffersResponse> call = apiInterface.GET_ALL_OFFERS_API_CALL("Bearer SHJSKSTBBSYYDYYYSMSUUYEJKSAY");
        call.enqueue(new Callback<AllOffersResponse>() {
            @Override
            public void onResponse(@NonNull Call<AllOffersResponse> call, @NonNull Response<AllOffersResponse> response) {
                if (response.isSuccessful()) {
                    dismissDialog();
                    if (response.body().getSuccess() && response.body().getData() != null && response.body().getData().size() > 0) {
                        myOffersListener.onSuccessAllOffers(response.body());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AllOffersResponse> call, @NonNull Throwable t) {
                dismissDialog();
                myOffersListener.onFailure(t.getMessage());
            }
        });
    }


    public void groupOffersListeners(Context context) {
        ApiInterface apiInterface = ApiClient.getApiService();
        showDialog(context, context.getResources().getString(R.string.label_please_wait));

        Call<GroupOffersModelResponse> call = apiInterface.GET_OFFERS_LIST();
        call.enqueue(new Callback<GroupOffersModelResponse>() {
            @Override
            public void onResponse(@NonNull Call<GroupOffersModelResponse> call, @NonNull Response<GroupOffersModelResponse> response) {
                if (response.isSuccessful()) {
                    dismissDialog();
                    myOffersListener.onSuccessGroupOffersApi(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<GroupOffersModelResponse> call, @NonNull Throwable t) {
                dismissDialog();
                myOffersListener.onFailureGroupOffers();
            }
        });
    }


    public void searchItemProducts(String item, int position) {
        ApiInterface apiInterface = ApiClient.getApiServiceMposBaseUrl(SessionManager.INSTANCE.getEposUrl());
        ItemSearchRequest itemSearchRequest = new ItemSearchRequest();
        itemSearchRequest.setCorpCode("0");
        itemSearchRequest.setIsGeneric(false);
        itemSearchRequest.setIsInitial(true);
        itemSearchRequest.setIsStockCheck(true);
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
                    myOffersListener.onSuccessSearchItemApi(itemSearchResponse, position);
                    if (itemSearchResponse != null && itemSearchResponse.getItemList().size() > 0) {
                        getBatchList(itemSearchResponse.getItemList().get(0).getArtCode(), position, itemSearchResponse.getItemList().get(0));
                    }
                } else if (response.isSuccessful() && response.body().getItemList().size() == 0) {
                    Toast.makeText(mContext, "No Items Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ItemSearchResponse> call, @NonNull Throwable t) {
                myOffersListener.onSearchFailure(t.getMessage());
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
                    myOffersListener.setSuccessBatchList(response.body(), position, itemSerachData);
                }
            }

            @Override
            public void onFailure(@NotNull Call<BatchListResponse> call, @NotNull Throwable t) {
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
                    myOffersListener.onSendSmsSuccess();
                } else {
                    myOffersListener.onSendSmsFailure();
                }
            }

            @Override
            public void onFailure(Call<Send_Sms_Response> call, Throwable t) {
                myOffersListener.onSendSmsFailure();
            }
        });
    }

    public void calculatePosTransaction(CalculatePosTransactionRequest calculatePosTransactionRequest) {
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
                    myOffersListener.onSuccessCalculatePosTransactionApi(response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<CalculatePosTransactionResponse> call, @NotNull Throwable t) {
                Utils.dismissDialog();
            }
        });
    }
}
