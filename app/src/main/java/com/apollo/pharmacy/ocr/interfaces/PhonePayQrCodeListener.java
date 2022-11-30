package com.apollo.pharmacy.ocr.interfaces;

import com.apollo.pharmacy.ocr.activities.paymentoptions.model.ExpressCheckoutTransactionApiResponse;
import com.apollo.pharmacy.ocr.model.GetCustomerDetailsModelRes;
import com.apollo.pharmacy.ocr.model.GetPackSizeResponse;
import com.apollo.pharmacy.ocr.model.GetPointDetailResponse;
import com.apollo.pharmacy.ocr.model.PhonePayQrCodeResponse;
import com.apollo.pharmacy.ocr.model.PlaceOrderResModel;

public interface PhonePayQrCodeListener {
    void onSuccessGetPhonePayQrCodeUpi(PhonePayQrCodeResponse phonePayQrCodeResponse, boolean scanpay);

    void onSuccessPlaceOrderFMCG(PlaceOrderResModel body);

    void onFailureService(String string);

    void onSuccessPhonepePaymentDetails(PhonePayQrCodeResponse phonePayQrCodeResponse, String transactionId);

    void onSuccessGetPackSizeApi(GetPackSizeResponse getPackSizeResponse);

    void onFailureGetPackSizeApi(String message);

    void onSuccessexpressCheckoutTransactionApiCall(ExpressCheckoutTransactionApiResponse expressCheckoutTransactionApiResponse);

    void onClickLastThreeAddresses(String s, String phoneNumber, String postalCode, String city, String state, String name, String address1, String address2, String onlyAddress, boolean last3AddressSelected);

    void toCallTimerInDialog();

    void onLastDigitPinCode();

    void onSuccessCustomerDetailsResponse(GetCustomerDetailsModelRes body);

    void onSuccessGetPointDetailResponse(GetPointDetailResponse body);
}
