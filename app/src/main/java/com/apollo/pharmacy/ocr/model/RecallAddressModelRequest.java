package com.apollo.pharmacy.ocr.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RecallAddressModelRequest implements Serializable
    {

        @SerializedName("MobileNo")
        @Expose
        private String mobileNo;
        @SerializedName("StoreId")
        @Expose
        private String storeId;
        @SerializedName("Url")
        @Expose
        private Object url;
        @SerializedName("DataAreaID")
        @Expose
        private String dataAreaID;

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public String getStoreId() {
            return storeId;
        }

        public void setStoreId(String storeId) {
            this.storeId = storeId;
        }

        public Object getUrl() {
            return url;
        }

        public void setUrl(Object url) {
            this.url = url;
        }

        public String getDataAreaID() {
            return dataAreaID;
        }

        public void setDataAreaID(String dataAreaID) {
            this.dataAreaID = dataAreaID;
        }

    }

