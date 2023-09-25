package com.apollo.pharmacy.ocr.Utils.fileupload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FileDownloadRequest {
    @SerializedName("RefURL")
    @Expose
    private String refURL;

    public String getRefURL() {
        return refURL;
    }

    public void setRefURL(String refURL) {
        this.refURL = refURL;
    }
}
