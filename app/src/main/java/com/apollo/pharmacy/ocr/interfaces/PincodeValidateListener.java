package com.apollo.pharmacy.ocr.interfaces;

import com.apollo.pharmacy.ocr.model.PincodeValidateResponse;
import com.apollo.pharmacy.ocr.model.ServicabilityResponse;

import java.util.List;

public interface PincodeValidateListener {
    void onSuccessPincodeValidate(List<PincodeValidateResponse> body);

    void onFailurePincode(List<PincodeValidateResponse> body);

    void onSuccessServiceability(ServicabilityResponse response);

    void onFailureServiceability(String message);

}
