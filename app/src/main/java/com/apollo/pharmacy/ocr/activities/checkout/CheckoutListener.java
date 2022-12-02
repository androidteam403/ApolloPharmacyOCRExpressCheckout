package com.apollo.pharmacy.ocr.activities.checkout;

import com.apollo.pharmacy.ocr.activities.paymentoptions.model.ExpressCheckoutTransactionApiResponse;
import com.apollo.pharmacy.ocr.model.GetPointDetailResponse;
import com.apollo.pharmacy.ocr.model.PhonePayQrCodeResponse;
import com.apollo.pharmacy.ocr.model.RecallAddressResponse;

public interface CheckoutListener {
    void onClickNeedHomeDelivery();

    void onClickPayandCollectatCounter();

    void onClickNeedHomeDelivery1();

    void onClickPayhereandCarry();

    void onClickBack();

    void onClickPaynow();

    void onSuccessexpressCheckoutTransactionApiCall(ExpressCheckoutTransactionApiResponse expressCheckoutTransactionApiResponse);

    void onFailuremessage(String message);

    void onClickLastThreeAddresses(String s, String phoneNumber, String postalCode, String city, String state, String name, String address1, String address2, String onlyAddress, boolean last3AddressSelected);

    void onSuccessRecallAddress(RecallAddressResponse body);

    void onFailureRecallAddress(RecallAddressResponse body);

    void toCallTimerInDialog();

    void onLastDigitPinCode();

//    void onSuccessGetPointDetailResponse(GetPointDetailResponse body);

//    void onClickSendOtp();
//
//    void onValidateOtp();
}
