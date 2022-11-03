package com.apollo.pharmacy.ocr.interfaces;

import com.apollo.pharmacy.ocr.model.ItemSearchResponse;

public interface MySearchCallback {
    void onSuccessBarcodeItemApi(ItemSearchResponse itemSearchResponse);

    void onFailureBarcodeItemApi(String message);

    void onSendSmsSuccess();

    void onSendSmsFailure();
}
