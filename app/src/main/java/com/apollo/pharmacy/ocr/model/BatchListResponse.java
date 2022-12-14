package com.apollo.pharmacy.ocr.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BatchListResponse implements Serializable {

    @SerializedName("BatchList")
    @Expose
    private List<Batch> batchList = null;
    @SerializedName("DataAreaID")
    @Expose
    private Object dataAreaID;
    @SerializedName("RequestStatus")
    @Expose
    private Integer requestStatus;
    @SerializedName("ReturnMessage")
    @Expose
    private String returnMessage;
    @SerializedName("Store")
    @Expose
    private Object store;
    @SerializedName("TerminalID")
    @Expose
    private Object terminalID;
    @SerializedName("UserID")
    @Expose
    private Object userID;
    private final static long serialVersionUID = -1310781435737574267L;

    public List<Batch> getBatchList() {
        return batchList;
    }

    public void setBatchList(List<Batch> batchList) {
        this.batchList = batchList;
    }

    public Object getDataAreaID() {
        return dataAreaID;
    }

    public void setDataAreaID(Object dataAreaID) {
        this.dataAreaID = dataAreaID;
    }

    public Integer getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(Integer requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public Object getStore() {
        return store;
    }

    public void setStore(Object store) {
        this.store = store;
    }

    public Object getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(Object terminalID) {
        this.terminalID = terminalID;
    }

    public Object getUserID() {
        return userID;
    }

    public void setUserID(Object userID) {
        this.userID = userID;
    }

    public class Batch implements Serializable {

        @SerializedName("BatchNo")
        @Expose
        private String batchNo;
        @SerializedName("CESSPerc")
        @Expose
        private Double cESSPerc;
        @SerializedName("CESSTaxCode")
        @Expose
        private String cESSTaxCode;
        @SerializedName("CGSTPerc")
        @Expose
        private Double cGSTPerc;
        @SerializedName("CGSTTaxCode")
        @Expose
        private String cGSTTaxCode;
        @SerializedName("ExpDate")
        @Expose
        private String expDate;
        @SerializedName("IGSTPerc")
        @Expose
        private Double iGSTPerc;
        @SerializedName("IGSTTaxCode")
        @Expose
        private String iGSTTaxCode;
        @SerializedName("ISMRPChange")
        @Expose
        private Boolean iSMRPChange;
        @SerializedName("ItemID")
        @Expose
        private String itemID;
        @SerializedName("MRP")
        @Expose
        private Double mrp;
        @SerializedName("NearByExpiry")
        @Expose
        private Boolean nearByExpiry;
        @SerializedName("Price")
        @Expose
        private Double price;
        @SerializedName("Q_O_H")
        @Expose
        private String qOH;
        @SerializedName("REQQTY")
        @Expose
        private Integer reqqty;
        @SerializedName("SGSTPerc")
        @Expose
        private Double sGSTPerc;
        @SerializedName("SGSTTaxCode")
        @Expose
        private String sGSTTaxCode;
        @SerializedName("SNO")
        @Expose
        private String sno;
        @SerializedName("TotalTax")
        @Expose
        private Double totalTax;
        private final static long serialVersionUID = -2078008818526940221L;

        private boolean isBatchQtySelected;

        public boolean isBatchQtySelected() {
            return isBatchQtySelected;
        }

        public void setBatchQtySelected(boolean batchQtySelected) {
            isBatchQtySelected = batchQtySelected;
        }

        public String getBatchNo() {
            return batchNo;
        }

        public void setBatchNo(String batchNo) {
            this.batchNo = batchNo;
        }

        public Double getCESSPerc() {
            return cESSPerc;
        }

        public void setCESSPerc(Double cESSPerc) {
            this.cESSPerc = cESSPerc;
        }

        public String getCESSTaxCode() {
            return cESSTaxCode;
        }

        public void setCESSTaxCode(String cESSTaxCode) {
            this.cESSTaxCode = cESSTaxCode;
        }

        public Double getCGSTPerc() {
            return cGSTPerc;
        }

        public void setCGSTPerc(Double cGSTPerc) {
            this.cGSTPerc = cGSTPerc;
        }

        public String getCGSTTaxCode() {
            return cGSTTaxCode;
        }

        public void setCGSTTaxCode(String cGSTTaxCode) {
            this.cGSTTaxCode = cGSTTaxCode;
        }

        public String getExpDate() {
            return expDate;
        }

        public void setExpDate(String expDate) {
            this.expDate = expDate;
        }

        public Double getIGSTPerc() {
            return iGSTPerc;
        }

        public void setIGSTPerc(Double iGSTPerc) {
            this.iGSTPerc = iGSTPerc;
        }

        public String getIGSTTaxCode() {
            return iGSTTaxCode;
        }

        public void setIGSTTaxCode(String iGSTTaxCode) {
            this.iGSTTaxCode = iGSTTaxCode;
        }

        public Boolean getISMRPChange() {
            return iSMRPChange;
        }

        public void setISMRPChange(Boolean iSMRPChange) {
            this.iSMRPChange = iSMRPChange;
        }

        public String getItemID() {
            return itemID;
        }

        public void setItemID(String itemID) {
            this.itemID = itemID;
        }

        public Double getMrp() {
            return mrp;
        }

        public void setMrp(Double mrp) {
            this.mrp = mrp;
        }

        public Boolean getNearByExpiry() {
            return nearByExpiry;
        }

        public void setNearByExpiry(Boolean nearByExpiry) {
            this.nearByExpiry = nearByExpiry;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public String getQOH() {
            return qOH;
        }

        public void setQOH(String qOH) {
            this.qOH = qOH;
        }

        public Integer getReqqty() {
            return reqqty;
        }

        public void setReqqty(Integer reqqty) {
            this.reqqty = reqqty;
        }

        public Double getSGSTPerc() {
            return sGSTPerc;
        }

        public void setSGSTPerc(Double sGSTPerc) {
            this.sGSTPerc = sGSTPerc;
        }

        public String getSGSTTaxCode() {
            return sGSTTaxCode;
        }

        public void setSGSTTaxCode(String sGSTTaxCode) {
            this.sGSTTaxCode = sGSTTaxCode;
        }

        public String getSno() {
            return sno;
        }

        public void setSno(String sno) {
            this.sno = sno;
        }

        public Double getTotalTax() {
            return totalTax;
        }

        public void setTotalTax(Double totalTax) {
            this.totalTax = totalTax;
        }

    }
}

