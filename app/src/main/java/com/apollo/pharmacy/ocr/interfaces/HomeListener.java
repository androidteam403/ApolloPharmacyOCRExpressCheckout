package com.apollo.pharmacy.ocr.interfaces;

import com.apollo.pharmacy.ocr.activities.userlogin.model.GetGlobalConfigurationResponse;
import com.apollo.pharmacy.ocr.model.Categorylist_Response;
import com.apollo.pharmacy.ocr.model.Global_api_response;
import com.apollo.pharmacy.ocr.model.ItemSearchResponse;
import com.apollo.pharmacy.ocr.model.PortFolioModel;

public interface HomeListener {

    void onSuccessRedeemPoints(PortFolioModel model);

    void categoryListSuccessRes(Categorylist_Response response);

    void onFailureService(String message);

    void onSuccessSearchItemApi(ItemSearchResponse itemSearchResponse);

    void onSearchFailure(String message);

    void onSuccessGlobalApiResponse(Global_api_response body);

    void onFailure(String message);

    void onFailureConfigApi(String returnMessage);

    void onSuccessGlobalConfigurationApiCall(GetGlobalConfigurationResponse body);

    void onSendSmsSuccess();

    void onSendSmsFailure();
}
