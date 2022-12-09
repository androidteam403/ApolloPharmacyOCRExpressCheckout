package com.apollo.pharmacy.ocr.controller;

import android.content.Context;
import android.widget.Toast;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.paymentoptions.model.ExpressCheckoutTransactionApiRequest;
import com.apollo.pharmacy.ocr.activities.paymentoptions.model.ExpressCheckoutTransactionApiResponse;
import com.apollo.pharmacy.ocr.interfaces.PhonePayQrCodeListener;
import com.apollo.pharmacy.ocr.model.GetCustomerDetailsModelReq;
import com.apollo.pharmacy.ocr.model.GetCustomerDetailsModelRes;
import com.apollo.pharmacy.ocr.model.GetPackSizeRequest;
import com.apollo.pharmacy.ocr.model.GetPackSizeResponse;
import com.apollo.pharmacy.ocr.model.GetPointDetailRequest;
import com.apollo.pharmacy.ocr.model.GetPointDetailResponse;
import com.apollo.pharmacy.ocr.model.OCRToDigitalMedicineResponse;
import com.apollo.pharmacy.ocr.model.PhonePayQrCodeRequest;
import com.apollo.pharmacy.ocr.model.PhonePayQrCodeResponse;
import com.apollo.pharmacy.ocr.model.PlaceOrderReqModel;
import com.apollo.pharmacy.ocr.model.PlaceOrderResModel;
import com.apollo.pharmacy.ocr.network.ApiClient;
import com.apollo.pharmacy.ocr.network.ApiInterface;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.utility.Session;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhonePayQrCodeController {

    PhonePayQrCodeListener phonePayQrCodeListener;
    Context activity;

    public PhonePayQrCodeController(Context activity, PhonePayQrCodeListener phonePayQrCodeListener) {
        this.activity = activity;
        this.phonePayQrCodeListener = phonePayQrCodeListener;
    }

    public void getPhonePayQrCodeGeneration(boolean scanpay, double grandTotal) {
//        Utils.showDialog(activity, "Loading…");

        ApiInterface api = ApiClient.getApiServiceMposBaseUrl(SessionManager.INSTANCE.getEposUrl());
        PhonePayQrCodeRequest phonePayQrCodeRequest = new PhonePayQrCodeRequest();
        phonePayQrCodeRequest.setAmount(grandTotal);
        phonePayQrCodeRequest.setExpiresIn(2000);
        phonePayQrCodeRequest.setMessage("");
        phonePayQrCodeRequest.setOriginalTransactionId("");
        phonePayQrCodeRequest.setProviderReferenceId("");
        phonePayQrCodeRequest.setReqType("GENERATEQRCODE");
        phonePayQrCodeRequest.setStoreId(SessionManager.INSTANCE.getStoreId());
        phonePayQrCodeRequest.setTransactionId(Utils.getOrderedID());
        phonePayQrCodeRequest.setUrl("http://172.16.2.251:8033/PHONEPEUAT/APOLLO/PhonePe");
        //http://172.16.2.251:8033/PHONEPEUAT/APOLLO/PhonePe
        //http://10.4.14.7:8041/APOLLO/PhonePe
        Gson gson = new Gson();
        String json = gson.toJson(phonePayQrCodeRequest);

        Call<PhonePayQrCodeResponse> call = api.GET_PhonePay_Qr_Code(phonePayQrCodeRequest);
        call.enqueue(new Callback<PhonePayQrCodeResponse>() {
            @Override
            public void onResponse(@NotNull Call<PhonePayQrCodeResponse> call, @NotNull Response<PhonePayQrCodeResponse> response) {
                if (response.body() != null && response.body().getQrCode() != null) {
                    phonePayQrCodeListener.onSuccessGetPhonePayQrCodeUpi(response.body(), scanpay);
//                    Utils.dismissDialog();
                    getPhonePayPaymentSuccess(phonePayQrCodeRequest.getTransactionId(), grandTotal);
                }
            }

            @Override
            public void onFailure(@NotNull Call<PhonePayQrCodeResponse> call, @NotNull Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
//                Utils.dismissDialog();
            }
        });
    }

    public void handleOrderPlaceService(Context context, PlaceOrderReqModel placeOrderReqModel) {
//        ApiInterface apiInterface = ApiClient.getApiService(Constants.Order_Place_With_Prescription_API);
        Gson gson = new Gson();
        String json = gson.toJson(placeOrderReqModel);
//        Call<PlaceOrderResModel> call = apiInterface.PLACE_ORDER_SERVICE_CALL(Constants.New_Order_Place_With_Prescription_Token, placeOrderReqModel);

        ApiInterface apiInterface = ApiClient.getApiService("https://online.apollopharmacy.org/UAT/OrderPlace.svc/");
        Call<PlaceOrderResModel> call = apiInterface.PLACE_ORDER_SERVICE_CALL("9f15bdd0fcd5423190c2e877ba0228APM", placeOrderReqModel);


        call.enqueue(new Callback<PlaceOrderResModel>() {
            @Override
            public void onResponse(@NotNull Call<PlaceOrderResModel> call, @NotNull Response<PlaceOrderResModel> response) {
                assert response.body() != null;
                if (response.body().getOrdersResult().getStatus()) {
                    phonePayQrCodeListener.onSuccessPlaceOrderFMCG(response.body());
                } else {
                    if (response.body() != null && response.body().getOrdersResult() != null && response.body().getOrdersResult().getMessage() != null) {
                        phonePayQrCodeListener.onFailureService(response.body().getOrdersResult().getMessage());
                    } else if (response.body() != null && response.body().getOrdersResult().getMessage() != null && !response.body().getOrdersResult().getMessage().isEmpty()) {
                        phonePayQrCodeListener.onFailureService(response.body().getOrdersResult().getMessage());
                    } else {
                        phonePayQrCodeListener.onFailureService(context.getResources().getString(R.string.label_something_went_wrong));
                    }
                    Utils.dismissDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<PlaceOrderResModel> call, @NotNull Throwable t) {
                phonePayQrCodeListener.onFailureService(t.getMessage());//context.getResources().getString(R.string.label_something_went_wrong)
                Utils.dismissDialog();

            }
        });
    }

    public void getPhonePayPaymentSuccess(String tranId, double amt) {
//        Utils.showDialog(activity, "Loading…");
        ApiInterface api = ApiClient.getApiServiceMposBaseUrl(SessionManager.INSTANCE.getEposUrl());
        PhonePayQrCodeRequest phonePayQrCodeRequest = new PhonePayQrCodeRequest();
        phonePayQrCodeRequest.setAmount(amt);
        phonePayQrCodeRequest.setExpiresIn(2000);
        phonePayQrCodeRequest.setMessage("");
        phonePayQrCodeRequest.setOriginalTransactionId("");
        phonePayQrCodeRequest.setProviderReferenceId("");
        phonePayQrCodeRequest.setReqType("CHECKPAYMENTSTATUS");
        phonePayQrCodeRequest.setStoreId(SessionManager.INSTANCE.getStoreId());
        phonePayQrCodeRequest.setTransactionId(tranId);
        phonePayQrCodeRequest.setUrl("http://172.16.2.251:8033/PHONEPEUAT/APOLLO/PhonePe");
        //http://172.16.2.251:8033/PHONEPEUAT/APOLLO/PhonePe
       // http://10.4.14.7:8041/APOLLO/PhonePe

        Call<PhonePayQrCodeResponse> call = api.GET_PhonePay_Qr_payment_Success(phonePayQrCodeRequest);
        call.enqueue(new Callback<PhonePayQrCodeResponse>() {
            @Override
            public void onResponse(@NotNull Call<PhonePayQrCodeResponse> call, @NotNull Response<PhonePayQrCodeResponse> response) {
                if (response.body() != null) {
//                    Utils.dismissDialog();
                    phonePayQrCodeListener.onSuccessPhonepePaymentDetails(response.body(), tranId);
                }
            }

            @Override
            public void onFailure(@NotNull Call<PhonePayQrCodeResponse> call, @NotNull Throwable t) {
//                Utils.dismissDialog();
            }
        });
    }

    public void getPackSizeApiCall(List<OCRToDigitalMedicineResponse> dataList) {
        Utils.showDialog(activity, "Loading…");
        ApiInterface api = ApiClient.getApiServiceMposBaseUrl(SessionManager.INSTANCE.getEposUrl());
        GetPackSizeRequest getPackSizeRequest = new GetPackSizeRequest();
        List<GetPackSizeRequest.Itemsdetail> itemsdetails = new ArrayList<>();
        for (OCRToDigitalMedicineResponse ocrToDigitalMedicineResponse : dataList) {
            GetPackSizeRequest.Itemsdetail itemDetail = new GetPackSizeRequest.Itemsdetail();
            if (ocrToDigitalMedicineResponse != null) {
                itemDetail.setItemid(ocrToDigitalMedicineResponse.getArtCode().contains(",") ? ocrToDigitalMedicineResponse.getArtCode().substring(0, ocrToDigitalMedicineResponse.getArtCode().indexOf(",")) : ocrToDigitalMedicineResponse.getArtCode());
                itemDetail.setPacksze(0);
                itemsdetails.add(itemDetail);
            }
        }
        getPackSizeRequest.setItemsdetails(itemsdetails);

        Call<GetPackSizeResponse> call = api.GET_PACK_SIZE_API_CALL("YXV0aF91c2VyOnN1cGVyc2VjcmV0X3Rhd", getPackSizeRequest);
        call.enqueue(new Callback<GetPackSizeResponse>() {
            @Override
            public void onResponse(@NotNull Call<GetPackSizeResponse> call, @NotNull Response<GetPackSizeResponse> response) {
                Utils.dismissDialog();
                if (response.body() != null && response.body().getStatus()) {
                    phonePayQrCodeListener.onSuccessGetPackSizeApi(response.body());
                } else {
                    if (response.body() != null)
                        phonePayQrCodeListener.onFailureGetPackSizeApi(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(@NotNull Call<GetPackSizeResponse> call, @NotNull Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                Utils.dismissDialog();
            }
        });
    }

    public void expressCheckoutTransactionApiCall(ExpressCheckoutTransactionApiRequest expressCheckoutTransactionApiRequest) {
        String baseUrl = "";
        if (SessionManager.INSTANCE.getEposUrl().contains("EPOS")) {
            baseUrl = SessionManager.INSTANCE.getEposUrl().replace("EPOS", "ExpressDelivery");
        } else {
            baseUrl = SessionManager.INSTANCE.getEposUrl();
        }
        ApiInterface apiInterface = ApiClient.getApiService(baseUrl);
        Gson gson = new Gson();
        String json = gson.toJson(expressCheckoutTransactionApiRequest);
        Utils.showDialog(activity, "Please wait...");
        System.out.println("EXPRESS_CHECKOUT_TRANSACTION_API_CALL_RQUEST===================" + json);
        Call<ExpressCheckoutTransactionApiResponse> call = apiInterface.EXPRESS_CHECKOUT_TRANSACTION_API_CALL(expressCheckoutTransactionApiRequest);
        call.enqueue(new Callback<ExpressCheckoutTransactionApiResponse>() {
            @Override
            public void onResponse(@NotNull Call<ExpressCheckoutTransactionApiResponse> call, @NotNull Response<ExpressCheckoutTransactionApiResponse> response) {
                Utils.dismissDialog();
                if (response.body() != null) {
                    String jsonResponse = gson.toJson(response.body());
                    System.out.println("EXPRESS_CHECKOUT_TRANSACTION_API_CALL_RESPONSE===================" + jsonResponse);
                    if (response.body().getRequestStatus() == 0) {
                        phonePayQrCodeListener.onSuccessexpressCheckoutTransactionApiCall(response.body());
                    } else {
                        phonePayQrCodeListener.onFailureService(response.body().getReturnMessage());
                    }
                } else {
                    phonePayQrCodeListener.onFailureService(activity.getResources().getString(R.string.label_something_went_wrong));
                    Utils.dismissDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ExpressCheckoutTransactionApiResponse> call, @NotNull Throwable t) {
                phonePayQrCodeListener.onFailureService(activity.getResources().getString(R.string.label_something_went_wrong));
                Utils.dismissDialog();

            }
        });
    }

    public void getCustomerDetailsRedeem() {

        Utils.showDialog(activity, activity.getResources().getString(R.string.label_please_wait));
        ApiInterface api = ApiClient.getApiServiceMposBaseUrl(SessionManager.INSTANCE.getEposUrl());
        GetCustomerDetailsModelReq getCustomerDetailsModelReq = new GetCustomerDetailsModelReq();
        getCustomerDetailsModelReq.setSearchString(SessionManager.INSTANCE.getMobilenumber());
        getCustomerDetailsModelReq.setSearchType(0);
        getCustomerDetailsModelReq.setIsax(true);
        getCustomerDetailsModelReq.setISOneApollo(true);
        getCustomerDetailsModelReq.setStore(SessionManager.INSTANCE.getStoreId());
        getCustomerDetailsModelReq.setTerminal(SessionManager.INSTANCE.getTerminalId());
        getCustomerDetailsModelReq.setDataAreaID(SessionManager.INSTANCE.getDataAreaId());
        getCustomerDetailsModelReq.setClusterCode(SessionManager.INSTANCE.getClusterCode());
        getCustomerDetailsModelReq.setOneApolloSearchUrl(SessionManager.INSTANCE.getOneApolloSearchUrl());
        getCustomerDetailsModelReq.setAXSearchUrl(SessionManager.INSTANCE.getAxSearchUrl());
        getCustomerDetailsModelReq.setCPEnquiry(true);



        //http://172.16.2.251:8033/PHONEPEUAT/APOLLO/PhonePe
        //http://10.4.14.7:8041/APOLLO/PhonePe
//        Gson gson = new Gson();
//        String json = gson.toJson(getCustomerDetailsModelReq);

        Call<GetCustomerDetailsModelRes> call = api.GET_CUST_DETAILS_QR(getCustomerDetailsModelReq);
        call.enqueue(new Callback<GetCustomerDetailsModelRes>() {
            @Override
            public void onResponse(@NotNull Call<GetCustomerDetailsModelRes> call, @NotNull Response<GetCustomerDetailsModelRes> response) {
             if (response.body() != null) {
                    phonePayQrCodeListener.onSuccessCustomerDetailsResponse(response.body());

                }
            }

            @Override
            public void onFailure(@NotNull Call<GetCustomerDetailsModelRes> call, @NotNull Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                Utils.dismissDialog();
            }
        });
    }

    public void getPointDetail(String action, String redeem_points, String RRno, String enteredOtp) {
       Utils.showDialog(activity, "Loading…");

        ApiInterface api = ApiClient.getApiServiceMposBaseUrl(SessionManager.INSTANCE.getEposUrl());
        GetPointDetailRequest getPointDetailRequest = new GetPointDetailRequest();
        GetPointDetailRequest.RequestData requestData= new GetPointDetailRequest.RequestData();
        requestData.setStoreId(SessionManager.INSTANCE.getStoreId());
        requestData.setDocNum("123");
        requestData.setMobileNum(SessionManager.INSTANCE.getMobilenumber());
        requestData.setReqBy("M");
        requestData.setPoints(redeem_points);
        requestData.setRrno(RRno);
        requestData.setOtp(enteredOtp);
        requestData.setAction(action);
        requestData.setCoupon("");
        requestData.setType("");
        requestData.setCustomerID("");
        requestData.setUrl(SessionManager.INSTANCE.getOneApolloUrl());
        getPointDetailRequest.setRequestData(requestData);


        //http://172.16.2.251:8033/PHONEPEUAT/APOLLO/PhonePe
        //http://10.4.14.7:8041/APOLLO/PhonePe
        Gson gson = new Gson();
        String json = gson.toJson(getPointDetailRequest);

        Call<GetPointDetailResponse> call = api.GET_POINT_DETAIL(getPointDetailRequest);
        call.enqueue(new Callback<GetPointDetailResponse>() {
            @Override
            public void onResponse(@NotNull Call<GetPointDetailResponse> call, @NotNull Response<GetPointDetailResponse> response) {
                if (response.body() != null && response.body().getRequestStatus()!= null && response.body().getRequestStatus()==0) {
                    phonePayQrCodeListener.onSuccessGetPointDetailResponse(response.body());
//                    Utils.dismissDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<GetPointDetailResponse> call, @NotNull Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                Utils.dismissDialog();
            }
        });
    }
}
