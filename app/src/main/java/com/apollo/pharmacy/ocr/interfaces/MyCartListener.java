package com.apollo.pharmacy.ocr.interfaces;

import com.apollo.pharmacy.ocr.model.BatchListResponse;
import com.apollo.pharmacy.ocr.model.CalculatePosTransactionResponse;
import com.apollo.pharmacy.ocr.model.GetImageRes;
import com.apollo.pharmacy.ocr.model.GetProductListResponse;
import com.apollo.pharmacy.ocr.model.ItemSearchResponse;
import com.apollo.pharmacy.ocr.model.OCRToDigitalMedicineResponse;
import com.apollo.pharmacy.ocr.model.UpCellCrossCellResponse;
import com.apollo.pharmacy.ocr.model.UserAddress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface MyCartListener {

    void onSuccessGetAddress(ArrayList<UserAddress> response);

    void onFailure(String message);

    void onSuccessProductList(HashMap<String, GetProductListResponse> productList);

    void onDeleteItemClicked(OCRToDigitalMedicineResponse ocrToDigitalMedicineResponse, int position);

    void onDeletedAddAllClicked();

    void onSuccessImageService(GetImageRes response);

    void onSuccessSearchItemApi(UpCellCrossCellResponse body);

    void onSearchFailure(String message);

    void onSuccessBarcodeItemApi(ItemSearchResponse itemSearchResponse, int serviceType, int qty, int position);

    void onFailureBarcodeItemApi(String message);

    void upSellCrosssellApiCall(List<UpCellCrossCellResponse.Crossselling> crossselling,
                                        List<UpCellCrossCellResponse.Upselling> upselling);

    void setSuccessBatchList(BatchListResponse body, int position, ItemSearchResponse.Item itemSearchData);

    void onSuccessCalculatePosTransactionApi(CalculatePosTransactionResponse calculatePosTransactionResponse);

    void onSuccessSearchItemApi(ItemSearchResponse itemSearchResponse, int position);

    void onSendSmsSuccess();

    void onSendSmsFailure();
}
