package com.apollo.pharmacy.ocr.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RecallAddressResponse implements Serializable
    {

        @SerializedName("CustomerDetails")
        @Expose
        private List<CustomerDetail> customerDetails = null;
        @SerializedName("Message")
        @Expose
        private Object message;
        @SerializedName("ReqStatus")
        @Expose
        private Integer reqStatus;
        @SerializedName("RequestStatus")
        @Expose
        private Boolean requestStatus;
        @SerializedName("ReturnMessage")
        @Expose
        private String returnMessage;

        public class CustomerDetail implements Serializable
        {

            @SerializedName("Address1")
            @Expose
            private String address1;
            @SerializedName("Address2")
            @Expose
            private String address2;
            @SerializedName("City")
            @Expose
            private String city;
            @SerializedName("LandMark")
            @Expose
            private String landMark;
            @SerializedName("Name")
            @Expose
            private String name;
            @SerializedName("PhoneNumber")
            @Expose
            private String phoneNumber;
            @SerializedName("PostalCode")
            @Expose
            private String postalCode;
            @SerializedName("State")
            @Expose
            private String state;
            private final static long serialVersionUID = -2179733594334570937L;

            public String getAddress1() {
                return address1;
            }

            public void setAddress1(String address1) {
                this.address1 = address1;
            }

            public String getAddress2() {
                return address2;
            }

            public void setAddress2(String address2) {
                this.address2 = address2;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getLandMark() {
                return landMark;
            }

            public void setLandMark(String landMark) {
                this.landMark = landMark;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPhoneNumber() {
                return phoneNumber;
            }

            public void setPhoneNumber(String phoneNumber) {
                this.phoneNumber = phoneNumber;
            }

            public String getPostalCode() {
                return postalCode;
            }

            public void setPostalCode(String postalCode) {
                this.postalCode = postalCode;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

        }

        public List<CustomerDetail> getCustomerDetails() {
            return customerDetails;
        }

        public void setCustomerDetails(List<CustomerDetail> customerDetails) {
            this.customerDetails = customerDetails;
        }

        public Object getMessage() {
            return message;
        }

        public void setMessage(Object message) {
            this.message = message;
        }

        public Integer getReqStatus() {
            return reqStatus;
        }

        public void setReqStatus(Integer reqStatus) {
            this.reqStatus = reqStatus;
        }

        public Boolean getRequestStatus() {
            return requestStatus;
        }

        public void setRequestStatus(Boolean requestStatus) {
            this.requestStatus = requestStatus;
        }

        public String getReturnMessage() {
            return returnMessage;
        }

        public void setReturnMessage(String returnMessage) {
            this.returnMessage = returnMessage;
        }

    }






