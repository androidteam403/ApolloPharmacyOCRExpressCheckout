package com.apollo.pharmacy.ocr.activities.paymentoptions;

public interface PaymentOptionsListener {

    void onClickSendOtp();

    void onValidateOtp();

    void onClickSendOtpUpi();

    void onValidateOtpUpi();

    void withRedeem();

    void withoutRedeem();
}
