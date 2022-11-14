package com.apollo.pharmacy.ocr.activities.insertprescriptionnew;

import com.apollo.pharmacy.ocr.model.PlaceOrderResModel;
import com.apollo.pharmacy.ocr.model.RecallAddressResponse;

public interface InsertPrescriptionActivityNewListener {
    void onClickPrescription();

    void onCloseFullviewPrescription();

    void onClickZoomIn();

    void onClickZoomOut();

    void onClickScanAnotherPrescription();

    void onClickPrescription(String prescriptionPath);

    void onClickItemDelete(int position);

    void onClickBackPressed();

    void onClickContinue();

    void onSuccessPlaceOrder(PlaceOrderResModel model);

    void onFailureService(String message);

    void onSuccessRecallAddress(RecallAddressResponse body);

    void onFailureRecallAddress(RecallAddressResponse body);

    void onClickLastThreeAddresses(String s, String phoneNumber, String postalCode, String city, String state, String name, String address1, String address2, String onlyAddress);

}