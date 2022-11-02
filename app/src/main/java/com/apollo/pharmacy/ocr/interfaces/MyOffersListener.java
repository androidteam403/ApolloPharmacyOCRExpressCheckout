package com.apollo.pharmacy.ocr.interfaces;

import com.apollo.pharmacy.ocr.model.AllOffersResponse;
import com.apollo.pharmacy.ocr.model.BatchListResponse;
import com.apollo.pharmacy.ocr.model.CalculatePosTransactionResponse;
import com.apollo.pharmacy.ocr.model.GetProductListResponse;
import com.apollo.pharmacy.ocr.model.GroupOffersModelResponse;
import com.apollo.pharmacy.ocr.model.ItemSearchResponse;
import com.apollo.pharmacy.ocr.model.Suggestion_Product;
import com.apollo.pharmacy.ocr.model.UpCellCrossCellResponse;

import java.util.HashMap;
import java.util.List;

public interface MyOffersListener {

    void onSearchFailure(String error);

    void onSuccessProductList(HashMap<String, GetProductListResponse> productList);

    void onSuccessProductListMainCategory(HashMap<String, GetProductListResponse> productList);

    void onFailure(String error);

    void onSearchSuggestionList(List<Suggestion_Product> m);

    void onNewSearchResult(List<Suggestion_Product> m);

    void onSuccessLoadMoreTrendingNow(GetProductListResponse body);

    void onFailureLoadMoreTrendingNow(String message);

    void onSuccessLoadMorePromotions(GetProductListResponse body);

    void onFailureLoadMorePromotions(String message);

    void onSuccessSearchItemApi(ItemSearchResponse itemSearchResponse);

    void onSuccessSearchUpcellCroscellApi(UpCellCrossCellResponse body);

    void onSearchFailureUpcellCroscell(String message);

    void onSuccessAllOffers(AllOffersResponse allOffersResponse);

    void onFailureAllOffers(String message);

    void onSuccessGroupOffersApi(GroupOffersModelResponse groupOffersModelResponse);

    void onFailureGroupOffers();

    void onfiftyPerOffOffer(AllOffersResponse.Datum offer, List<AllOffersResponse.PromoItem> image);

    void onBuyOneGetOneOffer(AllOffersResponse.Datum offer, List<AllOffersResponse.PromoItem> image);

    void onBuyMultipleOnGroupOfOffers(AllOffersResponse.Datum offer, List<AllOffersResponse.PromoItem> image);

    void onContinueOfSelectedOffers();

    void onSelectedOffersList(AllOffersResponse.Datum offer, AllOffersResponse.PromoItem image, List<AllOffersResponse.PromoItem> imageList);

    void setSuccessBatchList(BatchListResponse body, int position, ItemSearchResponse.Item itemSearchData);

    void onSuccessSearchItemApi(ItemSearchResponse itemSearchResponse, int position);

    void onSuccessCalculatePosTransactionApi(CalculatePosTransactionResponse calculatePosTransactionResponse);

    void onSendSmsSuccess();

    void onSendSmsFailure();
}
