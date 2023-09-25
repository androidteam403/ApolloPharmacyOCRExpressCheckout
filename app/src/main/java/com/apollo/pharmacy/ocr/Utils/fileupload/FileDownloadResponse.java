package com.apollo.pharmacy.ocr.Utils.fileupload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FileDownloadResponse {

    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("referenceurl")
    @Expose
    private String referenceurl;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReferenceurl() {
        return referenceurl;
    }

    public void setReferenceurl(String referenceurl) {
        this.referenceurl = referenceurl;
    }
}
