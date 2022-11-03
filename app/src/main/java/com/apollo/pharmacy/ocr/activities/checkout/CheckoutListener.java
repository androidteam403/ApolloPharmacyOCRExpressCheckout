package com.apollo.pharmacy.ocr.activities.checkout;

import com.apollo.pharmacy.ocr.activities.paymentoptions.model.ExpressCheckoutTransactionApiResponse;
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

    void onClickLastThreeAddresses(String s, String phoneNumber, String postalCode, String city, String state, String name);

    void onSuccessRecallAddress(RecallAddressResponse body);

    void onFailureRecallAddress(RecallAddressResponse body);
}
