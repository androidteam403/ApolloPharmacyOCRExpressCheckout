package com.apollo.pharmacy.ocr.Utils.fileupload;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface FileUploadCallback {

    void onFailureUpload(String message);

    void allFilesDownloaded(List<FileUploadModel> fileUploadModelList) throws GeneralSecurityException, IOException;

    void allFilesUploaded(List<FileUploadModel> fileUploadModelList);
}
