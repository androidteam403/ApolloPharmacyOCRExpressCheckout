package com.apollo.pharmacy.ocr.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetCustomerDetailsModelRes implements Serializable {

    @SerializedName("RequestStatus")
    @Expose
    private Integer requestStatus;
    @SerializedName("ReturnMessage")
    @Expose
    private Object returnMessage;
    @SerializedName("_Customer")
    @Expose
    private List<Customer> customer = null;

    public Integer getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(Integer requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Object getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(Object returnMessage) {
        this.returnMessage = returnMessage;
    }

    public List<Customer> getCustomer() {
        return customer;
    }

    public void setCustomer(List<Customer> customer) {
        this.customer = customer;
    }

    public class Customer implements Serializable {

        @SerializedName("AvailablePoints")
        @Expose
        private String availablePoints;
        @SerializedName("CPEnquiry")
        @Expose
        private Boolean cPEnquiry;
        @SerializedName("CardName")
        @Expose
        private String cardName;
        @SerializedName("CardNo")
        @Expose
        private Object cardNo;
        @SerializedName("CorpId")
        @Expose
        private Object corpId;
        @SerializedName("CustActiveStatus")
        @Expose
        private Object custActiveStatus;
        @SerializedName("CustId")
        @Expose
        private Object custId;
        @SerializedName("CustomerName")
        @Expose
        private String customerName;
        @SerializedName("EmpNo")
        @Expose
        private Object empNo;
        @SerializedName("MobileNo")
        @Expose
        private String mobileNo;
        @SerializedName("StoreId")
        @Expose
        private Object storeId;
        @SerializedName("TelephoneNo")
        @Expose
        private Object telephoneNo;
        @SerializedName("Tier")
        @Expose
        private String tier;


        public String getAvailablePoints() {
            return availablePoints;
        }

        public void setAvailablePoints(String availablePoints) {
            this.availablePoints = availablePoints;
        }

        public Boolean getCPEnquiry() {
            return cPEnquiry;
        }

        public void setCPEnquiry(Boolean cPEnquiry) {
            this.cPEnquiry = cPEnquiry;
        }

        public String getCardName() {
            return cardName;
        }

        public void setCardName(String cardName) {
            this.cardName = cardName;
        }

        public Object getCardNo() {
            return cardNo;
        }

        public void setCardNo(Object cardNo) {
            this.cardNo = cardNo;
        }

        public Object getCorpId() {
            return corpId;
        }

        public void setCorpId(Object corpId) {
            this.corpId = corpId;
        }

        public Object getCustActiveStatus() {
            return custActiveStatus;
        }

        public void setCustActiveStatus(Object custActiveStatus) {
            this.custActiveStatus = custActiveStatus;
        }

        public Object getCustId() {
            return custId;
        }

        public void setCustId(Object custId) {
            this.custId = custId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public Object getEmpNo() {
            return empNo;
        }

        public void setEmpNo(Object empNo) {
            this.empNo = empNo;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public Object getStoreId() {
            return storeId;
        }

        public void setStoreId(Object storeId) {
            this.storeId = storeId;
        }

        public Object getTelephoneNo() {
            return telephoneNo;
        }

        public void setTelephoneNo(Object telephoneNo) {
            this.telephoneNo = telephoneNo;
        }

        public String getTier() {
            return tier;
        }

        public void setTier(String tier) {
            this.tier = tier;
        }

    }

}




