package com.apollo.pharmacy.ocr.Utils.fileupload;

import android.content.Context;
import android.widget.Toast;

import com.apollo.pharmacy.ocr.network.ApiClient;
import com.apollo.pharmacy.ocr.network.ApiInterface;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.utility.NetworkUtils;
import com.apollo.pharmacy.ocr.utility.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileUpload {
    private Context context;
    private FileUploadCallback fileUploadCallback;
    private List<FileUploadModel> fileUploadModelList;

    public void uploadFiles(Context context, FileUploadCallback fileUploadCallback, List<FileUploadModel> fileUploadModelList) {
        this.context = context;
        this.fileUploadCallback = fileUploadCallback;
        this.fileUploadModelList = fileUploadModelList;

        if (NetworkUtils.isNetworkConnected(context)) {
//            showLoading(context !!)
            uploadFile(fileUploadModelList.get(0));
        } else {
            fileUploadCallback.onFailureUpload("Something went wrong.");
        }
    }

    public void uploadFile(FileUploadModel fileUploadModel) {
        Utils.showDialog(context, "Please wait");

        FileUploadRequest fileUploadRequest = new FileUploadRequest();
        fileUploadRequest.setFilename(fileUploadModel.getFile());

        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), fileUploadModel.getFile());
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", fileUploadModel.getFile().getName(), requestBody);

        ApiInterface apiInterface = ApiClient.getApiService("https://online.apollopharmacy.org/UAT/OrderPlace.svc/");
        Call<FileUploadResponse> call = apiInterface.FILE_UPLOAD_API_CALL(Constants.FILE_UPLOAD_URL_UAT, "multipart/form-data", Constants.FILE_UPLOAD_TOKEN_UAT, fileToUpload);

        call.enqueue(new Callback<FileUploadResponse>() {
            @Override
            public void onResponse(@NotNull Call<FileUploadResponse> call, @NotNull Response<FileUploadResponse> response) {
                Utils.dismissDialog();
                if (response.body() != null && response.body().isStatus()) {
                    onSuccessFileUpload(fileUploadModel, response.body());
                } else {
                    onFailureFileUpload(fileUploadModel, response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<FileUploadResponse> call, @NotNull Throwable t) {
                Utils.dismissDialog();
                fileUploadCallback.onFailureUpload(t.getMessage());
            }
        });
    }

    public void onSuccessFileUpload(FileUploadModel fileUploadModel, FileUploadResponse fileUploadResponse) {
        fileUploadModel.setFileUploaded(true);
        fileUploadModel.setFileUploadResponse(fileUploadResponse);
        FileUploadModel fileUploadModelTemp = null;
        for (FileUploadModel fum : fileUploadModelList) {
            if (!fum.isFileUploaded()) {
                fileUploadModelTemp = fum;
                break;
            }
        }
        if (fileUploadModelTemp != null) {
            uploadFile(fileUploadModelTemp);
        } else {
            Utils.dismissDialog();
//            fileUploadCallback.allFilesUploaded(fileUploadModelList);
            downloadFiles(context, fileUploadCallback, fileUploadModelList);
        }
    }

    public void onFailureFileUpload(FileUploadModel fileUploadModel, FileUploadResponse fileUploadResponse) {
        Utils.dismissDialog();
        Toast.makeText(context, "File upload failed", Toast.LENGTH_SHORT).show();
    }


    public void downloadFiles(Context context, FileUploadCallback fileUploadCallback, List<FileUploadModel> fileUploadModelList) {
        this.context = context;
        this.fileUploadCallback = fileUploadCallback;
        this.fileUploadModelList = fileUploadModelList;

        if (NetworkUtils.isNetworkConnected(context)) {
            Utils.showDialog(context, "Please wait");
            downloadFile(fileUploadModelList.get(0));
        } else {
            fileUploadCallback.onFailureUpload("Something went wrong.");
        }
    }


    public void downloadFile(FileUploadModel fileUploadModel) {
        Utils.showDialog(context, "Please wait");

        FileDownloadRequest fileDownloadRequest = new FileDownloadRequest();
        fileDownloadRequest.setRefURL(fileUploadModel.getFileUploadResponse().getReferenceurl());

        ApiInterface apiInterface = ApiClient.getApiService("https://online.apollopharmacy.org/UAT/OrderPlace.svc/");
        Call<FileDownloadResponse> call = apiInterface.FILE_DOWNLOAD_API_CALL(Constants.FILE_DOWNLOAD_URL_UAT, Constants.FILE_DOWNLOAD_TOEKN_UAT, fileDownloadRequest);

        call.enqueue(new Callback<FileDownloadResponse>() {
            @Override
            public void onResponse(@NotNull Call<FileDownloadResponse> call, @NotNull Response<FileDownloadResponse> response) {
                Utils.dismissDialog();
                if (response.body() != null && response.body().isStatus()) {
                    onSuccessFileDownload(fileUploadModel, response.body());
                } else {
                    onFailureFileDownload(fileUploadModel, response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<FileDownloadResponse> call, @NotNull Throwable t) {
                Utils.dismissDialog();
                fileUploadCallback.onFailureUpload(t.getMessage());
            }
        });
    }


    public void onSuccessFileDownload(FileUploadModel fileUploadModel, FileDownloadResponse fileDownloadResponse) {
        fileUploadModel.setFileDownloaded(true);
        fileUploadModel.setFileDownloadResponse(fileDownloadResponse);
        FileUploadModel fileUploadModelTemp = null;
        for (FileUploadModel fum : fileUploadModelList) {
            if (!fum.isFileDownloaded()) {
                fileUploadModelTemp = fum;
                break;
            }
        }
        if (fileUploadModelTemp != null) {
            downloadFile(fileUploadModelTemp);
        } else {
            Utils.dismissDialog();
            try {
                fileUploadCallback.allFilesDownloaded(fileUploadModelList);
            } catch (Exception e) {
                System.out.println("onSuccessFileDownload ::::::: FileUpload");
            }

        }
    }

    public void onFailureFileDownload(FileUploadModel fileUploadModel, FileDownloadResponse fileDownloadResponse) {
        Utils.dismissDialog();
        Toast.makeText(context, "File download failed", Toast.LENGTH_SHORT).show();
    }



  /*  public void handleOrderPlaceService(Context context, PlaceOrderReqModel placeOrderReqModel) {
        Utils.showDialog(context, "Please wait");
//        ApiInterface apiInterface = ApiClient.getApiService(Constants.Order_Place_With_Prescription_API);
//        Call<PlaceOrderResModel> call = apiInterface.PLACE_ORDER_SERVICE_CALL(Constants.New_Order_Place_With_Prescription_Token, placeOrderReqModel);
        ApiInterface apiInterface = ApiClient.getApiService("https://online.apollopharmacy.org/UAT/OrderPlace.svc/");
        Call<PlaceOrderResModel> call = apiInterface.PLACE_ORDER_SERVICE_CALL("9f15bdd0fcd5423190c2e877ba0228APM", placeOrderReqModel);

        call.enqueue(new Callback<PlaceOrderResModel>() {
            @Override
            public void onResponse(@NotNull Call<PlaceOrderResModel> call, @NotNull Response<PlaceOrderResModel> response) {
                Utils.dismissDialog();

                if (response.body() != null && response.body().getOrdersResult().getStatus()) {
                    mListener.onSuccessPlaceOrder(response.body());
                } else if (response.body() != null && response.body().getOrdersResult().getMessage() != null && !response.body().getOrdersResult().getMessage().isEmpty()) {
                    mListener.onFailureService(response.body().getOrdersResult().getMessage());
                } else {
                    mListener.onFailureService(context.getResources().getString(R.string.label_something_went_wrong));
                }
            }

            @Override
            public void onFailure(@NotNull Call<PlaceOrderResModel> call, @NotNull Throwable t) {
                Utils.dismissDialog();
                mListener.onFailureService(t.getMessage());//context.getResources().getString(R.string.label_something_went_wrong)
            }
        });
    }*/
}
