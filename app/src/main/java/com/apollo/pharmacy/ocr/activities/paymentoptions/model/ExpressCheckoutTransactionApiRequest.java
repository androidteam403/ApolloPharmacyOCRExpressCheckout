package com.apollo.pharmacy.ocr.activities.paymentoptions.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExpressCheckoutTransactionApiRequest {

    @SerializedName("Remainingamount")
    @Expose
    private Integer remainingamount;
    @SerializedName("AvailablePoint")
    @Expose
    private Integer availablePoint;
    @SerializedName("IsPickPackOrder")
    @Expose
    private Boolean isPickPackOrder;
    @SerializedName("IsHDOrder")
    @Expose
    private Boolean isHDOrder;
    @SerializedName("TimeSlot")
    @Expose
    private String timeSlot;
    @SerializedName("AmazonPrintStatus")
    @Expose
    private Object amazonPrintStatus;
    @SerializedName("IsTPASeller")
    @Expose
    private Boolean isTPASeller;
    @SerializedName("DonationAmount")
    @Expose
    private Integer donationAmount;
    @SerializedName("IsBulkDiscount")
    @Expose
    private Boolean isBulkDiscount;
    @SerializedName("ReturnRequestId")
    @Expose
    private String returnRequestId;
    @SerializedName("IsOMSJurnalsScreen")
    @Expose
    private Boolean isOMSJurnalsScreen;
    @SerializedName("ISOMSReturn")
    @Expose
    private Boolean iSOMSReturn;
    @SerializedName("RiderMobile")
    @Expose
    private String riderMobile;
    @SerializedName("RiderName")
    @Expose
    private String riderName;
    @SerializedName("RiderCode")
    @Expose
    private String riderCode;
    @SerializedName("DspName")
    @Expose
    private String dspName;
    @SerializedName("RevReturnOtp")
    @Expose
    private String revReturnOtp;
    @SerializedName("PickupOtp")
    @Expose
    private String pickupOtp;
    @SerializedName("FwdReturnOtp")
    @Expose
    private String fwdReturnOtp;
    @SerializedName("RTOStatus")
    @Expose
    private Boolean rTOStatus;
    @SerializedName("PickupStatus")
    @Expose
    private Boolean pickupStatus;
    @SerializedName("Tier")
    @Expose
    private String tier;
    @SerializedName("CustomerType")
    @Expose
    private String customerType;
    @SerializedName("StockStatus")
    @Expose
    private String stockStatus;
    @SerializedName("IsUHIDBilling")
    @Expose
    private Boolean isUHIDBilling;
    @SerializedName("HCOfferCode")
    @Expose
    private String hCOfferCode;
    @SerializedName("DiscountStatus")
    @Expose
    private Integer discountStatus;
    @SerializedName("DiscountReferenceID")
    @Expose
    private String discountReferenceID;
    @SerializedName("ISOnlineOrder")
    @Expose
    private Boolean iSOnlineOrder;
    @SerializedName("ISCancelled")
    @Expose
    private Boolean iSCancelled;
    @SerializedName("VendorCode")
    @Expose
    private String vendorCode;
    @SerializedName("ISReserved")
    @Expose
    private Boolean iSReserved;
    @SerializedName("ISBulkBilling")
    @Expose
    private Boolean iSBulkBilling;
    @SerializedName("DeliveryDate")
    @Expose
    private String deliveryDate;
    @SerializedName("OrderType")
    @Expose
    private String orderType;
    @SerializedName("OrderSource")
    @Expose
    private String orderSource;
    @SerializedName("ShippingMethod")
    @Expose
    private String shippingMethod;
    @SerializedName("ShippingMethodDesc")
    @Expose
    private String shippingMethodDesc;
    @SerializedName("BillingCity")
    @Expose
    private String billingCity;
    @SerializedName("VendorId")
    @Expose
    private String vendorId;
    @SerializedName("PaymentSource")
    @Expose
    private String paymentSource;
    @SerializedName("ISPrescibeDiscount")
    @Expose
    private Boolean iSPrescibeDiscount;
    @SerializedName("CancelReasonCode")
    @Expose
    private String cancelReasonCode;
    @SerializedName("StoreName")
    @Expose
    private String storeName;
    @SerializedName("RegionCode")
    @Expose
    private String regionCode;
    @SerializedName("CustomerID")
    @Expose
    private String customerID;
    @SerializedName("CorpCode")
    @Expose
    private String corpCode;
    @SerializedName("MobileNO")
    @Expose
    private String mobileNO;
    @SerializedName("DOB")
    @Expose
    private String dob;
    @SerializedName("CustomerName")
    @Expose
    private String customerName;
    @SerializedName("CustAddress")
    @Expose
    private String custAddress;
    @SerializedName("CustomerState")
    @Expose
    private String customerState;
    @SerializedName("Gender")
    @Expose
    private Integer gender;
    @SerializedName("Pincode")
    @Expose
    private String pincode;
    @SerializedName("DoctorName")
    @Expose
    private String doctorName;
    @SerializedName("DoctorCode")
    @Expose
    private String doctorCode;
    @SerializedName("SalesOrigin")
    @Expose
    private String salesOrigin;
    @SerializedName("TrackingRef")
    @Expose
    private String trackingRef;
    @SerializedName("REFNO")
    @Expose
    private String refno;
    @SerializedName("IPNO")
    @Expose
    private String ipno;
    @SerializedName("IPSerialNO")
    @Expose
    private String iPSerialNO;
    @SerializedName("ReciptId")
    @Expose
    private String reciptId;
    @SerializedName("BatchTerminalid")
    @Expose
    private String batchTerminalid;
    @SerializedName("BusinessDate")
    @Expose
    private String businessDate;
    @SerializedName("Channel")
    @Expose
    private String channel;
    @SerializedName("Comment")
    @Expose
    private String comment;
    @SerializedName("CreatedonPosTerminal")
    @Expose
    private String createdonPosTerminal;
    @SerializedName("Currency")
    @Expose
    private String currency;
    @SerializedName("CustAccount")
    @Expose
    private String custAccount;
    @SerializedName("CustDiscamount")
    @Expose
    private Integer custDiscamount;
    @SerializedName("DiscAmount")
    @Expose
    private Integer discAmount;
    @SerializedName("EntryStatus")
    @Expose
    private Integer entryStatus;
    @SerializedName("GrossAmount")
    @Expose
    private Integer grossAmount;
    @SerializedName("NetAmount")
    @Expose
    private Integer netAmount;
    @SerializedName("NetAmountInclTax")
    @Expose
    private Integer netAmountInclTax;
    @SerializedName("NumberofItemLines")
    @Expose
    private Integer numberofItemLines;
    @SerializedName("NumberofItems")
    @Expose
    private Integer numberofItems;
    @SerializedName("RoundedAmount")
    @Expose
    private Integer roundedAmount;
    @SerializedName("Staff")
    @Expose
    private String staff;
    @SerializedName("Store")
    @Expose
    private String store;
    @SerializedName("State")
    @Expose
    private String state;
    @SerializedName("Terminal")
    @Expose
    private String terminal;
    @SerializedName("ReturnStore")
    @Expose
    private String returnStore;
    @SerializedName("ReturnTerminal")
    @Expose
    private String returnTerminal;
    @SerializedName("ReturnTransactionId")
    @Expose
    private String returnTransactionId;
    @SerializedName("ReturnReceiptId")
    @Expose
    private String returnReceiptId;
    @SerializedName("TimewhenTransClosed")
    @Expose
    private Integer timewhenTransClosed;
    @SerializedName("TotalDiscAmount")
    @Expose
    private Integer totalDiscAmount;
    @SerializedName("TotalManualDiscountAmount")
    @Expose
    private Integer totalManualDiscountAmount;
    @SerializedName("TotalManualDiscountPercentage")
    @Expose
    private Integer totalManualDiscountPercentage;
    @SerializedName("TotalMRP")
    @Expose
    private Integer totalMRP;
    @SerializedName("TotalTaxAmount")
    @Expose
    private Integer totalTaxAmount;
    @SerializedName("TransactionId")
    @Expose
    private String transactionId;
    @SerializedName("TransDate")
    @Expose
    private String transDate;
    @SerializedName("Type")
    @Expose
    private Integer type;
    @SerializedName("DataAreaId")
    @Expose
    private String dataAreaId;
    @SerializedName("IsVoid")
    @Expose
    private Boolean isVoid;
    @SerializedName("IsReturn")
    @Expose
    private Boolean isReturn;
    @SerializedName("ISBatchModifiedAllowed")
    @Expose
    private Boolean iSBatchModifiedAllowed;
    @SerializedName("ISReturnAllowed")
    @Expose
    private Boolean iSReturnAllowed;
    @SerializedName("IsManualBill")
    @Expose
    private Boolean isManualBill;
    @SerializedName("ReturnType")
    @Expose
    private Integer returnType;
    @SerializedName("CurrentSalesLine")
    @Expose
    private Integer currentSalesLine;
    @SerializedName("RequestStatus")
    @Expose
    private Integer requestStatus;
    @SerializedName("ReturnMessage")
    @Expose
    private String returnMessage;
    @SerializedName("PosEvent")
    @Expose
    private Integer posEvent;
    @SerializedName("TransType")
    @Expose
    private Integer transType;
    @SerializedName("IsStockCheck")
    @Expose
    private Boolean isStockCheck;
    @SerializedName("ISPosted")
    @Expose
    private Boolean iSPosted;
    @SerializedName("SEZ")
    @Expose
    private Integer sez;
    @SerializedName("CouponCode")
    @Expose
    private String couponCode;
    @SerializedName("ISAdvancePayment")
    @Expose
    private Boolean iSAdvancePayment;
    @SerializedName("AmounttoAccount")
    @Expose
    private Integer amounttoAccount;
    @SerializedName("ReminderDays")
    @Expose
    private Integer reminderDays;
    @SerializedName("ISOMSOrder")
    @Expose
    private Boolean iSOMSOrder;
    @SerializedName("ISHBPStore")
    @Expose
    private Boolean iSHBPStore;
    @SerializedName("PatientID")
    @Expose
    private String patientID;
    @SerializedName("ApprovedID")
    @Expose
    private String approvedID;
    @SerializedName("DiscountRef")
    @Expose
    private String discountRef;
    @SerializedName("AWBNo")
    @Expose
    private String aWBNo;
    @SerializedName("DSPCode")
    @Expose
    private String dSPCode;
    @SerializedName("ISHyperLocalDelivery")
    @Expose
    private Boolean iSHyperLocalDelivery;
    @SerializedName("ISHyperDelivered")
    @Expose
    private Boolean iSHyperDelivered;
    @SerializedName("CreatedDateTime")
    @Expose
    private String createdDateTime;
    @SerializedName("OMSCreditAmount")
    @Expose
    private Integer oMSCreditAmount;
    @SerializedName("ISOMSValidate")
    @Expose
    private Boolean iSOMSValidate;
    @SerializedName("AllowedTenderType")
    @Expose
    private String allowedTenderType;
    @SerializedName("ShippingCharges")
    @Expose
    private Integer shippingCharges;
    @SerializedName("AgeGroup")
    @Expose
    private String ageGroup;
    @SerializedName("ExpiryDays")
    @Expose
    private Integer expiryDays;
    @SerializedName("SalesLine")
    @Expose
    private List<SalesLine> salesLine = null;
    @SerializedName("TenderLine")
    @Expose
    private List<TenderLine> tenderLine = null;
    @SerializedName("OrderPrescriptionURL")
    @Expose
    private Object orderPrescriptionURL;

    public Integer getRemainingamount() {
        return remainingamount;
    }

    public void setRemainingamount(Integer remainingamount) {
        this.remainingamount = remainingamount;
    }

    public Integer getAvailablePoint() {
        return availablePoint;
    }

    public void setAvailablePoint(Integer availablePoint) {
        this.availablePoint = availablePoint;
    }

    public Boolean getIsPickPackOrder() {
        return isPickPackOrder;
    }

    public void setIsPickPackOrder(Boolean isPickPackOrder) {
        this.isPickPackOrder = isPickPackOrder;
    }

    public Boolean getIsHDOrder() {
        return isHDOrder;
    }

    public void setIsHDOrder(Boolean isHDOrder) {
        this.isHDOrder = isHDOrder;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Object getAmazonPrintStatus() {
        return amazonPrintStatus;
    }

    public void setAmazonPrintStatus(Object amazonPrintStatus) {
        this.amazonPrintStatus = amazonPrintStatus;
    }

    public Boolean getIsTPASeller() {
        return isTPASeller;
    }

    public void setIsTPASeller(Boolean isTPASeller) {
        this.isTPASeller = isTPASeller;
    }

    public Integer getDonationAmount() {
        return donationAmount;
    }

    public void setDonationAmount(Integer donationAmount) {
        this.donationAmount = donationAmount;
    }

    public Boolean getIsBulkDiscount() {
        return isBulkDiscount;
    }

    public void setIsBulkDiscount(Boolean isBulkDiscount) {
        this.isBulkDiscount = isBulkDiscount;
    }

    public String getReturnRequestId() {
        return returnRequestId;
    }

    public void setReturnRequestId(String returnRequestId) {
        this.returnRequestId = returnRequestId;
    }

    public Boolean getIsOMSJurnalsScreen() {
        return isOMSJurnalsScreen;
    }

    public void setIsOMSJurnalsScreen(Boolean isOMSJurnalsScreen) {
        this.isOMSJurnalsScreen = isOMSJurnalsScreen;
    }

    public Boolean getISOMSReturn() {
        return iSOMSReturn;
    }

    public void setISOMSReturn(Boolean iSOMSReturn) {
        this.iSOMSReturn = iSOMSReturn;
    }

    public String getRiderMobile() {
        return riderMobile;
    }

    public void setRiderMobile(String riderMobile) {
        this.riderMobile = riderMobile;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public String getRiderCode() {
        return riderCode;
    }

    public void setRiderCode(String riderCode) {
        this.riderCode = riderCode;
    }

    public String getDspName() {
        return dspName;
    }

    public void setDspName(String dspName) {
        this.dspName = dspName;
    }

    public String getRevReturnOtp() {
        return revReturnOtp;
    }

    public void setRevReturnOtp(String revReturnOtp) {
        this.revReturnOtp = revReturnOtp;
    }

    public String getPickupOtp() {
        return pickupOtp;
    }

    public void setPickupOtp(String pickupOtp) {
        this.pickupOtp = pickupOtp;
    }

    public String getFwdReturnOtp() {
        return fwdReturnOtp;
    }

    public void setFwdReturnOtp(String fwdReturnOtp) {
        this.fwdReturnOtp = fwdReturnOtp;
    }

    public Boolean getRTOStatus() {
        return rTOStatus;
    }

    public void setRTOStatus(Boolean rTOStatus) {
        this.rTOStatus = rTOStatus;
    }

    public Boolean getPickupStatus() {
        return pickupStatus;
    }

    public void setPickupStatus(Boolean pickupStatus) {
        this.pickupStatus = pickupStatus;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }

    public Boolean getIsUHIDBilling() {
        return isUHIDBilling;
    }

    public void setIsUHIDBilling(Boolean isUHIDBilling) {
        this.isUHIDBilling = isUHIDBilling;
    }

    public String getHCOfferCode() {
        return hCOfferCode;
    }

    public void setHCOfferCode(String hCOfferCode) {
        this.hCOfferCode = hCOfferCode;
    }

    public Integer getDiscountStatus() {
        return discountStatus;
    }

    public void setDiscountStatus(Integer discountStatus) {
        this.discountStatus = discountStatus;
    }

    public String getDiscountReferenceID() {
        return discountReferenceID;
    }

    public void setDiscountReferenceID(String discountReferenceID) {
        this.discountReferenceID = discountReferenceID;
    }

    public Boolean getISOnlineOrder() {
        return iSOnlineOrder;
    }

    public void setISOnlineOrder(Boolean iSOnlineOrder) {
        this.iSOnlineOrder = iSOnlineOrder;
    }

    public Boolean getISCancelled() {
        return iSCancelled;
    }

    public void setISCancelled(Boolean iSCancelled) {
        this.iSCancelled = iSCancelled;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public Boolean getISReserved() {
        return iSReserved;
    }

    public void setISReserved(Boolean iSReserved) {
        this.iSReserved = iSReserved;
    }

    public Boolean getISBulkBilling() {
        return iSBulkBilling;
    }

    public void setISBulkBilling(Boolean iSBulkBilling) {
        this.iSBulkBilling = iSBulkBilling;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderSource() {
        return orderSource;
    }

    public void setOrderSource(String orderSource) {
        this.orderSource = orderSource;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public String getShippingMethodDesc() {
        return shippingMethodDesc;
    }

    public void setShippingMethodDesc(String shippingMethodDesc) {
        this.shippingMethodDesc = shippingMethodDesc;
    }

    public String getBillingCity() {
        return billingCity;
    }

    public void setBillingCity(String billingCity) {
        this.billingCity = billingCity;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getPaymentSource() {
        return paymentSource;
    }

    public void setPaymentSource(String paymentSource) {
        this.paymentSource = paymentSource;
    }

    public Boolean getISPrescibeDiscount() {
        return iSPrescibeDiscount;
    }

    public void setISPrescibeDiscount(Boolean iSPrescibeDiscount) {
        this.iSPrescibeDiscount = iSPrescibeDiscount;
    }

    public String getCancelReasonCode() {
        return cancelReasonCode;
    }

    public void setCancelReasonCode(String cancelReasonCode) {
        this.cancelReasonCode = cancelReasonCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCorpCode() {
        return corpCode;
    }

    public void setCorpCode(String corpCode) {
        this.corpCode = corpCode;
    }

    public String getMobileNO() {
        return mobileNO;
    }

    public void setMobileNO(String mobileNO) {
        this.mobileNO = mobileNO;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustAddress() {
        return custAddress;
    }

    public void setCustAddress(String custAddress) {
        this.custAddress = custAddress;
    }

    public String getCustomerState() {
        return customerState;
    }

    public void setCustomerState(String customerState) {
        this.customerState = customerState;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorCode() {
        return doctorCode;
    }

    public void setDoctorCode(String doctorCode) {
        this.doctorCode = doctorCode;
    }

    public String getSalesOrigin() {
        return salesOrigin;
    }

    public void setSalesOrigin(String salesOrigin) {
        this.salesOrigin = salesOrigin;
    }

    public String getTrackingRef() {
        return trackingRef;
    }

    public void setTrackingRef(String trackingRef) {
        this.trackingRef = trackingRef;
    }

    public String getRefno() {
        return refno;
    }

    public void setRefno(String refno) {
        this.refno = refno;
    }

    public String getIpno() {
        return ipno;
    }

    public void setIpno(String ipno) {
        this.ipno = ipno;
    }

    public String getIPSerialNO() {
        return iPSerialNO;
    }

    public void setIPSerialNO(String iPSerialNO) {
        this.iPSerialNO = iPSerialNO;
    }

    public String getReciptId() {
        return reciptId;
    }

    public void setReciptId(String reciptId) {
        this.reciptId = reciptId;
    }

    public String getBatchTerminalid() {
        return batchTerminalid;
    }

    public void setBatchTerminalid(String batchTerminalid) {
        this.batchTerminalid = batchTerminalid;
    }

    public String getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedonPosTerminal() {
        return createdonPosTerminal;
    }

    public void setCreatedonPosTerminal(String createdonPosTerminal) {
        this.createdonPosTerminal = createdonPosTerminal;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCustAccount() {
        return custAccount;
    }

    public void setCustAccount(String custAccount) {
        this.custAccount = custAccount;
    }

    public Integer getCustDiscamount() {
        return custDiscamount;
    }

    public void setCustDiscamount(Integer custDiscamount) {
        this.custDiscamount = custDiscamount;
    }

    public Integer getDiscAmount() {
        return discAmount;
    }

    public void setDiscAmount(Integer discAmount) {
        this.discAmount = discAmount;
    }

    public Integer getEntryStatus() {
        return entryStatus;
    }

    public void setEntryStatus(Integer entryStatus) {
        this.entryStatus = entryStatus;
    }

    public Integer getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(Integer grossAmount) {
        this.grossAmount = grossAmount;
    }

    public Integer getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(Integer netAmount) {
        this.netAmount = netAmount;
    }

    public Integer getNetAmountInclTax() {
        return netAmountInclTax;
    }

    public void setNetAmountInclTax(Integer netAmountInclTax) {
        this.netAmountInclTax = netAmountInclTax;
    }

    public Integer getNumberofItemLines() {
        return numberofItemLines;
    }

    public void setNumberofItemLines(Integer numberofItemLines) {
        this.numberofItemLines = numberofItemLines;
    }

    public Integer getNumberofItems() {
        return numberofItems;
    }

    public void setNumberofItems(Integer numberofItems) {
        this.numberofItems = numberofItems;
    }

    public Integer getRoundedAmount() {
        return roundedAmount;
    }

    public void setRoundedAmount(Integer roundedAmount) {
        this.roundedAmount = roundedAmount;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getReturnStore() {
        return returnStore;
    }

    public void setReturnStore(String returnStore) {
        this.returnStore = returnStore;
    }

    public String getReturnTerminal() {
        return returnTerminal;
    }

    public void setReturnTerminal(String returnTerminal) {
        this.returnTerminal = returnTerminal;
    }

    public String getReturnTransactionId() {
        return returnTransactionId;
    }

    public void setReturnTransactionId(String returnTransactionId) {
        this.returnTransactionId = returnTransactionId;
    }

    public String getReturnReceiptId() {
        return returnReceiptId;
    }

    public void setReturnReceiptId(String returnReceiptId) {
        this.returnReceiptId = returnReceiptId;
    }

    public Integer getTimewhenTransClosed() {
        return timewhenTransClosed;
    }

    public void setTimewhenTransClosed(Integer timewhenTransClosed) {
        this.timewhenTransClosed = timewhenTransClosed;
    }

    public Integer getTotalDiscAmount() {
        return totalDiscAmount;
    }

    public void setTotalDiscAmount(Integer totalDiscAmount) {
        this.totalDiscAmount = totalDiscAmount;
    }

    public Integer getTotalManualDiscountAmount() {
        return totalManualDiscountAmount;
    }

    public void setTotalManualDiscountAmount(Integer totalManualDiscountAmount) {
        this.totalManualDiscountAmount = totalManualDiscountAmount;
    }

    public Integer getTotalManualDiscountPercentage() {
        return totalManualDiscountPercentage;
    }

    public void setTotalManualDiscountPercentage(Integer totalManualDiscountPercentage) {
        this.totalManualDiscountPercentage = totalManualDiscountPercentage;
    }

    public Integer getTotalMRP() {
        return totalMRP;
    }

    public void setTotalMRP(Integer totalMRP) {
        this.totalMRP = totalMRP;
    }

    public Integer getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public void setTotalTaxAmount(Integer totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDataAreaId() {
        return dataAreaId;
    }

    public void setDataAreaId(String dataAreaId) {
        this.dataAreaId = dataAreaId;
    }

    public Boolean getIsVoid() {
        return isVoid;
    }

    public void setIsVoid(Boolean isVoid) {
        this.isVoid = isVoid;
    }

    public Boolean getIsReturn() {
        return isReturn;
    }

    public void setIsReturn(Boolean isReturn) {
        this.isReturn = isReturn;
    }

    public Boolean getISBatchModifiedAllowed() {
        return iSBatchModifiedAllowed;
    }

    public void setISBatchModifiedAllowed(Boolean iSBatchModifiedAllowed) {
        this.iSBatchModifiedAllowed = iSBatchModifiedAllowed;
    }

    public Boolean getISReturnAllowed() {
        return iSReturnAllowed;
    }

    public void setISReturnAllowed(Boolean iSReturnAllowed) {
        this.iSReturnAllowed = iSReturnAllowed;
    }

    public Boolean getIsManualBill() {
        return isManualBill;
    }

    public void setIsManualBill(Boolean isManualBill) {
        this.isManualBill = isManualBill;
    }

    public Integer getReturnType() {
        return returnType;
    }

    public void setReturnType(Integer returnType) {
        this.returnType = returnType;
    }

    public Integer getCurrentSalesLine() {
        return currentSalesLine;
    }

    public void setCurrentSalesLine(Integer currentSalesLine) {
        this.currentSalesLine = currentSalesLine;
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

    public Integer getPosEvent() {
        return posEvent;
    }

    public void setPosEvent(Integer posEvent) {
        this.posEvent = posEvent;
    }

    public Integer getTransType() {
        return transType;
    }

    public void setTransType(Integer transType) {
        this.transType = transType;
    }

    public Boolean getIsStockCheck() {
        return isStockCheck;
    }

    public void setIsStockCheck(Boolean isStockCheck) {
        this.isStockCheck = isStockCheck;
    }

    public Boolean getISPosted() {
        return iSPosted;
    }

    public void setISPosted(Boolean iSPosted) {
        this.iSPosted = iSPosted;
    }

    public Integer getSez() {
        return sez;
    }

    public void setSez(Integer sez) {
        this.sez = sez;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public Boolean getISAdvancePayment() {
        return iSAdvancePayment;
    }

    public void setISAdvancePayment(Boolean iSAdvancePayment) {
        this.iSAdvancePayment = iSAdvancePayment;
    }

    public Integer getAmounttoAccount() {
        return amounttoAccount;
    }

    public void setAmounttoAccount(Integer amounttoAccount) {
        this.amounttoAccount = amounttoAccount;
    }

    public Integer getReminderDays() {
        return reminderDays;
    }

    public void setReminderDays(Integer reminderDays) {
        this.reminderDays = reminderDays;
    }

    public Boolean getISOMSOrder() {
        return iSOMSOrder;
    }

    public void setISOMSOrder(Boolean iSOMSOrder) {
        this.iSOMSOrder = iSOMSOrder;
    }

    public Boolean getISHBPStore() {
        return iSHBPStore;
    }

    public void setISHBPStore(Boolean iSHBPStore) {
        this.iSHBPStore = iSHBPStore;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getApprovedID() {
        return approvedID;
    }

    public void setApprovedID(String approvedID) {
        this.approvedID = approvedID;
    }

    public String getDiscountRef() {
        return discountRef;
    }

    public void setDiscountRef(String discountRef) {
        this.discountRef = discountRef;
    }

    public String getAWBNo() {
        return aWBNo;
    }

    public void setAWBNo(String aWBNo) {
        this.aWBNo = aWBNo;
    }

    public String getDSPCode() {
        return dSPCode;
    }

    public void setDSPCode(String dSPCode) {
        this.dSPCode = dSPCode;
    }

    public Boolean getISHyperLocalDelivery() {
        return iSHyperLocalDelivery;
    }

    public void setISHyperLocalDelivery(Boolean iSHyperLocalDelivery) {
        this.iSHyperLocalDelivery = iSHyperLocalDelivery;
    }

    public Boolean getISHyperDelivered() {
        return iSHyperDelivered;
    }

    public void setISHyperDelivered(Boolean iSHyperDelivered) {
        this.iSHyperDelivered = iSHyperDelivered;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Integer getOMSCreditAmount() {
        return oMSCreditAmount;
    }

    public void setOMSCreditAmount(Integer oMSCreditAmount) {
        this.oMSCreditAmount = oMSCreditAmount;
    }

    public Boolean getISOMSValidate() {
        return iSOMSValidate;
    }

    public void setISOMSValidate(Boolean iSOMSValidate) {
        this.iSOMSValidate = iSOMSValidate;
    }

    public String getAllowedTenderType() {
        return allowedTenderType;
    }

    public void setAllowedTenderType(String allowedTenderType) {
        this.allowedTenderType = allowedTenderType;
    }

    public Integer getShippingCharges() {
        return shippingCharges;
    }

    public void setShippingCharges(Integer shippingCharges) {
        this.shippingCharges = shippingCharges;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public Integer getExpiryDays() {
        return expiryDays;
    }

    public void setExpiryDays(Integer expiryDays) {
        this.expiryDays = expiryDays;
    }

    public List<SalesLine> getSalesLine() {
        return salesLine;
    }

    public void setSalesLine(List<SalesLine> salesLine) {
        this.salesLine = salesLine;
    }

    public List<TenderLine> getTenderLine() {
        return tenderLine;
    }

    public void setTenderLine(List<TenderLine> tenderLine) {
        this.tenderLine = tenderLine;
    }

    public Object getOrderPrescriptionURL() {
        return orderPrescriptionURL;
    }

    public void setOrderPrescriptionURL(Object orderPrescriptionURL) {
        this.orderPrescriptionURL = orderPrescriptionURL;
    }

    public static class SalesLine {

        @SerializedName("ReasonCode")
        @Expose
        private String reasonCode;
        @SerializedName("PriceVariation")
        @Expose
        private Boolean priceVariation;
        @SerializedName("QCPass")
        @Expose
        private Boolean qCPass;
        @SerializedName("QCFail")
        @Expose
        private Boolean qCFail;
        @SerializedName("QCStatus")
        @Expose
        private Integer qCStatus;
        @SerializedName("QCDate")
        @Expose
        private String qCDate;
        @SerializedName("QCRemarks")
        @Expose
        private String qCRemarks;
        @SerializedName("AlternetItemID")
        @Expose
        private String alternetItemID;
        @SerializedName("LineNo")
        @Expose
        private Integer lineNo;
        @SerializedName("ItemId")
        @Expose
        private String itemId;
        @SerializedName("ItemName")
        @Expose
        private String itemName;
        @SerializedName("Category")
        @Expose
        private String category;
        @SerializedName("CategoryCode")
        @Expose
        private String categoryCode;
        @SerializedName("SubCategory")
        @Expose
        private String subCategory;
        @SerializedName("SubCategoryCode")
        @Expose
        private String subCategoryCode;
        @SerializedName("ScheduleCategory")
        @Expose
        private String scheduleCategory;
        @SerializedName("ScheduleCategoryCode")
        @Expose
        private String scheduleCategoryCode;
        @SerializedName("ManufacturerCode")
        @Expose
        private String manufacturerCode;
        @SerializedName("ManufacturerName")
        @Expose
        private String manufacturerName;
        @SerializedName("Expiry")
        @Expose
        private String expiry;
        @SerializedName("Qty")
        @Expose
        private Integer qty;
        @SerializedName("StockQty")
        @Expose
        private Integer stockQty;
        @SerializedName("ReturnQty")
        @Expose
        private Integer returnQty;
        @SerializedName("RemainingQty")
        @Expose
        private Integer remainingQty;
        @SerializedName("MRP")
        @Expose
        private Double mrp;
        @SerializedName("Tax")
        @Expose
        private Integer tax;
        @SerializedName("Additionaltax")
        @Expose
        private Integer additionaltax;
        @SerializedName("Barcode")
        @Expose
        private String barcode;
        @SerializedName("Comment")
        @Expose
        private String comment;
        @SerializedName("DiscAmount")
        @Expose
        private Double discAmount;
        @SerializedName("DiscOfferId")
        @Expose
        private String discOfferId;
        @SerializedName("Hsncode_In")
        @Expose
        private String hsncodeIn;
        @SerializedName("InventBatchId")
        @Expose
        private String inventBatchId;
        @SerializedName("PreviewText")
        @Expose
        private String previewText;
        @SerializedName("LinedscAmount")
        @Expose
        private Integer linedscAmount;
        @SerializedName("LineManualDiscountAmount")
        @Expose
        private Integer lineManualDiscountAmount;
        @SerializedName("LineManualDiscountPercentage")
        @Expose
        private Integer lineManualDiscountPercentage;
        @SerializedName("NetAmount")
        @Expose
        private Integer netAmount;
        @SerializedName("NetAmountInclTax")
        @Expose
        private Integer netAmountInclTax;
        @SerializedName("OriginalPrice")
        @Expose
        private Integer originalPrice;
        @SerializedName("PeriodicDiscAmount")
        @Expose
        private Integer periodicDiscAmount;
        @SerializedName("Price")
        @Expose
        private Integer price;
        @SerializedName("TaxAmount")
        @Expose
        private Integer taxAmount;
        @SerializedName("BaseAmount")
        @Expose
        private Integer baseAmount;
        @SerializedName("TotalDiscAmount")
        @Expose
        private Double totalDiscAmount;
        @SerializedName("TotalDiscPct")
        @Expose
        private Integer totalDiscPct;
        @SerializedName("TotalRoundedAmount")
        @Expose
        private Integer totalRoundedAmount;
        @SerializedName("Unit")
        @Expose
        private String unit;
        @SerializedName("UnitPrice")
        @Expose
        private Double unitPrice;
        @SerializedName("UnitQty")
        @Expose
        private Integer unitQty;
        @SerializedName("VariantId")
        @Expose
        private String variantId;
        @SerializedName("Total")
        @Expose
        private Integer total;
        @SerializedName("ISPrescribed")
        @Expose
        private Integer iSPrescribed;
        @SerializedName("RemainderDays")
        @Expose
        private Integer remainderDays;
        @SerializedName("IsVoid")
        @Expose
        private Boolean isVoid;
        @SerializedName("IsPriceOverride")
        @Expose
        private Boolean isPriceOverride;
        @SerializedName("IsChecked")
        @Expose
        private Boolean isChecked;
        @SerializedName("RetailCategoryRecID")
        @Expose
        private String retailCategoryRecID;
        @SerializedName("RetailSubCategoryRecID")
        @Expose
        private String retailSubCategoryRecID;
        @SerializedName("RetailMainCategoryRecID")
        @Expose
        private String retailMainCategoryRecID;
        @SerializedName("DPCO")
        @Expose
        private Boolean dpco;
        @SerializedName("ProductRecID")
        @Expose
        private String productRecID;
        @SerializedName("ModifyBatchId")
        @Expose
        private String modifyBatchId;
        @SerializedName("DiseaseType")
        @Expose
        private String diseaseType;
        @SerializedName("Classification")
        @Expose
        private String classification;
        @SerializedName("SubClassification")
        @Expose
        private String subClassification;
        @SerializedName("OfferQty")
        @Expose
        private Integer offerQty;
        @SerializedName("OfferAmount")
        @Expose
        private Integer offerAmount;
        @SerializedName("OfferDiscountType")
        @Expose
        private Integer offerDiscountType;
        @SerializedName("OfferDiscountValue")
        @Expose
        private Integer offerDiscountValue;
        @SerializedName("DiscountType")
        @Expose
        private String discountType;
        @SerializedName("MixMode")
        @Expose
        private Boolean mixMode;
        @SerializedName("MMGroupId")
        @Expose
        private String mMGroupId;
        @SerializedName("DiscId")
        @Expose
        private String discId;
        @SerializedName("OfferType")
        @Expose
        private Integer offerType;
        @SerializedName("LineDiscPercentage")
        @Expose
        private Integer lineDiscPercentage;
        @SerializedName("ApplyDiscount")
        @Expose
        private Boolean applyDiscount;
        @SerializedName("IGSTPerc")
        @Expose
        private Integer iGSTPerc;
        @SerializedName("CESSPerc")
        @Expose
        private Integer cESSPerc;
        @SerializedName("CGSTPerc")
        @Expose
        private Integer cGSTPerc;
        @SerializedName("SGSTPerc")
        @Expose
        private Integer sGSTPerc;
        @SerializedName("TotalTax")
        @Expose
        private Integer totalTax;
        @SerializedName("IGSTTaxCode")
        @Expose
        private Object iGSTTaxCode;
        @SerializedName("CESSTaxCode")
        @Expose
        private Object cESSTaxCode;
        @SerializedName("CGSTTaxCode")
        @Expose
        private Object cGSTTaxCode;
        @SerializedName("SGSTTaxCode")
        @Expose
        private Object sGSTTaxCode;
        @SerializedName("DiscountStructureType")
        @Expose
        private Integer discountStructureType;
        @SerializedName("SubstitudeItemId")
        @Expose
        private String substitudeItemId;
        @SerializedName("CategoryReference")
        @Expose
        private String categoryReference;
        @SerializedName("OrderStatus")
        @Expose
        private Integer orderStatus;
        @SerializedName("OmsLineID")
        @Expose
        private Integer omsLineID;
        @SerializedName("IsSubsitute")
        @Expose
        private Boolean isSubsitute;
        @SerializedName("IsGeneric")
        @Expose
        private Boolean isGeneric;
        @SerializedName("OmsLineRECID")
        @Expose
        private Integer omsLineRECID;
        @SerializedName("ISReserved")
        @Expose
        private Boolean iSReserved;
        @SerializedName("ISStockAvailable")
        @Expose
        private Boolean iSStockAvailable;
        @SerializedName("ISRestricted")
        @Expose
        private Boolean iSRestricted;
        @SerializedName("PhysicalBatchID")
        @Expose
        private Object physicalBatchID;
        @SerializedName("PhysicalMRP")
        @Expose
        private Integer physicalMRP;
        @SerializedName("PhysicalExpiry")
        @Expose
        private Object physicalExpiry;

        public String getReasonCode() {
            return reasonCode;
        }

        public void setReasonCode(String reasonCode) {
            this.reasonCode = reasonCode;
        }

        public Boolean getPriceVariation() {
            return priceVariation;
        }

        public void setPriceVariation(Boolean priceVariation) {
            this.priceVariation = priceVariation;
        }

        public Boolean getQCPass() {
            return qCPass;
        }

        public void setQCPass(Boolean qCPass) {
            this.qCPass = qCPass;
        }

        public Boolean getQCFail() {
            return qCFail;
        }

        public void setQCFail(Boolean qCFail) {
            this.qCFail = qCFail;
        }

        public Integer getQCStatus() {
            return qCStatus;
        }

        public void setQCStatus(Integer qCStatus) {
            this.qCStatus = qCStatus;
        }

        public String getQCDate() {
            return qCDate;
        }

        public void setQCDate(String qCDate) {
            this.qCDate = qCDate;
        }

        public String getQCRemarks() {
            return qCRemarks;
        }

        public void setQCRemarks(String qCRemarks) {
            this.qCRemarks = qCRemarks;
        }

        public String getAlternetItemID() {
            return alternetItemID;
        }

        public void setAlternetItemID(String alternetItemID) {
            this.alternetItemID = alternetItemID;
        }

        public Integer getLineNo() {
            return lineNo;
        }

        public void setLineNo(Integer lineNo) {
            this.lineNo = lineNo;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getCategoryCode() {
            return categoryCode;
        }

        public void setCategoryCode(String categoryCode) {
            this.categoryCode = categoryCode;
        }

        public String getSubCategory() {
            return subCategory;
        }

        public void setSubCategory(String subCategory) {
            this.subCategory = subCategory;
        }

        public String getSubCategoryCode() {
            return subCategoryCode;
        }

        public void setSubCategoryCode(String subCategoryCode) {
            this.subCategoryCode = subCategoryCode;
        }

        public String getScheduleCategory() {
            return scheduleCategory;
        }

        public void setScheduleCategory(String scheduleCategory) {
            this.scheduleCategory = scheduleCategory;
        }

        public String getScheduleCategoryCode() {
            return scheduleCategoryCode;
        }

        public void setScheduleCategoryCode(String scheduleCategoryCode) {
            this.scheduleCategoryCode = scheduleCategoryCode;
        }

        public String getManufacturerCode() {
            return manufacturerCode;
        }

        public void setManufacturerCode(String manufacturerCode) {
            this.manufacturerCode = manufacturerCode;
        }

        public String getManufacturerName() {
            return manufacturerName;
        }

        public void setManufacturerName(String manufacturerName) {
            this.manufacturerName = manufacturerName;
        }

        public String getExpiry() {
            return expiry;
        }

        public void setExpiry(String expiry) {
            this.expiry = expiry;
        }

        public Integer getQty() {
            return qty;
        }

        public void setQty(Integer qty) {
            this.qty = qty;
        }

        public Integer getStockQty() {
            return stockQty;
        }

        public void setStockQty(Integer stockQty) {
            this.stockQty = stockQty;
        }

        public Integer getReturnQty() {
            return returnQty;
        }

        public void setReturnQty(Integer returnQty) {
            this.returnQty = returnQty;
        }

        public Integer getRemainingQty() {
            return remainingQty;
        }

        public void setRemainingQty(Integer remainingQty) {
            this.remainingQty = remainingQty;
        }

        public Double getMrp() {
            return mrp;
        }

        public void setMrp(Double mrp) {
            this.mrp = mrp;
        }

        public Integer getTax() {
            return tax;
        }

        public void setTax(Integer tax) {
            this.tax = tax;
        }

        public Integer getAdditionaltax() {
            return additionaltax;
        }

        public void setAdditionaltax(Integer additionaltax) {
            this.additionaltax = additionaltax;
        }

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public Double getDiscAmount() {
            return discAmount;
        }

        public void setDiscAmount(Double discAmount) {
            this.discAmount = discAmount;
        }

        public String getDiscOfferId() {
            return discOfferId;
        }

        public void setDiscOfferId(String discOfferId) {
            this.discOfferId = discOfferId;
        }

        public String getHsncodeIn() {
            return hsncodeIn;
        }

        public void setHsncodeIn(String hsncodeIn) {
            this.hsncodeIn = hsncodeIn;
        }

        public String getInventBatchId() {
            return inventBatchId;
        }

        public void setInventBatchId(String inventBatchId) {
            this.inventBatchId = inventBatchId;
        }

        public String getPreviewText() {
            return previewText;
        }

        public void setPreviewText(String previewText) {
            this.previewText = previewText;
        }

        public Integer getLinedscAmount() {
            return linedscAmount;
        }

        public void setLinedscAmount(Integer linedscAmount) {
            this.linedscAmount = linedscAmount;
        }

        public Integer getLineManualDiscountAmount() {
            return lineManualDiscountAmount;
        }

        public void setLineManualDiscountAmount(Integer lineManualDiscountAmount) {
            this.lineManualDiscountAmount = lineManualDiscountAmount;
        }

        public Integer getLineManualDiscountPercentage() {
            return lineManualDiscountPercentage;
        }

        public void setLineManualDiscountPercentage(Integer lineManualDiscountPercentage) {
            this.lineManualDiscountPercentage = lineManualDiscountPercentage;
        }

        public Integer getNetAmount() {
            return netAmount;
        }

        public void setNetAmount(Integer netAmount) {
            this.netAmount = netAmount;
        }

        public Integer getNetAmountInclTax() {
            return netAmountInclTax;
        }

        public void setNetAmountInclTax(Integer netAmountInclTax) {
            this.netAmountInclTax = netAmountInclTax;
        }

        public Integer getOriginalPrice() {
            return originalPrice;
        }

        public void setOriginalPrice(Integer originalPrice) {
            this.originalPrice = originalPrice;
        }

        public Integer getPeriodicDiscAmount() {
            return periodicDiscAmount;
        }

        public void setPeriodicDiscAmount(Integer periodicDiscAmount) {
            this.periodicDiscAmount = periodicDiscAmount;
        }

        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }

        public Integer getTaxAmount() {
            return taxAmount;
        }

        public void setTaxAmount(Integer taxAmount) {
            this.taxAmount = taxAmount;
        }

        public Integer getBaseAmount() {
            return baseAmount;
        }

        public void setBaseAmount(Integer baseAmount) {
            this.baseAmount = baseAmount;
        }

        public Double getTotalDiscAmount() {
            return totalDiscAmount;
        }

        public void setTotalDiscAmount(Double totalDiscAmount) {
            this.totalDiscAmount = totalDiscAmount;
        }

        public Integer getTotalDiscPct() {
            return totalDiscPct;
        }

        public void setTotalDiscPct(Integer totalDiscPct) {
            this.totalDiscPct = totalDiscPct;
        }

        public Integer getTotalRoundedAmount() {
            return totalRoundedAmount;
        }

        public void setTotalRoundedAmount(Integer totalRoundedAmount) {
            this.totalRoundedAmount = totalRoundedAmount;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public Double getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(Double unitPrice) {
            this.unitPrice = unitPrice;
        }

        public Integer getUnitQty() {
            return unitQty;
        }

        public void setUnitQty(Integer unitQty) {
            this.unitQty = unitQty;
        }

        public String getVariantId() {
            return variantId;
        }

        public void setVariantId(String variantId) {
            this.variantId = variantId;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getISPrescribed() {
            return iSPrescribed;
        }

        public void setISPrescribed(Integer iSPrescribed) {
            this.iSPrescribed = iSPrescribed;
        }

        public Integer getRemainderDays() {
            return remainderDays;
        }

        public void setRemainderDays(Integer remainderDays) {
            this.remainderDays = remainderDays;
        }

        public Boolean getIsVoid() {
            return isVoid;
        }

        public void setIsVoid(Boolean isVoid) {
            this.isVoid = isVoid;
        }

        public Boolean getIsPriceOverride() {
            return isPriceOverride;
        }

        public void setIsPriceOverride(Boolean isPriceOverride) {
            this.isPriceOverride = isPriceOverride;
        }

        public Boolean getIsChecked() {
            return isChecked;
        }

        public void setIsChecked(Boolean isChecked) {
            this.isChecked = isChecked;
        }

        public String getRetailCategoryRecID() {
            return retailCategoryRecID;
        }

        public void setRetailCategoryRecID(String retailCategoryRecID) {
            this.retailCategoryRecID = retailCategoryRecID;
        }

        public String getRetailSubCategoryRecID() {
            return retailSubCategoryRecID;
        }

        public void setRetailSubCategoryRecID(String retailSubCategoryRecID) {
            this.retailSubCategoryRecID = retailSubCategoryRecID;
        }

        public String getRetailMainCategoryRecID() {
            return retailMainCategoryRecID;
        }

        public void setRetailMainCategoryRecID(String retailMainCategoryRecID) {
            this.retailMainCategoryRecID = retailMainCategoryRecID;
        }

        public Boolean getDpco() {
            return dpco;
        }

        public void setDpco(Boolean dpco) {
            this.dpco = dpco;
        }

        public String getProductRecID() {
            return productRecID;
        }

        public void setProductRecID(String productRecID) {
            this.productRecID = productRecID;
        }

        public String getModifyBatchId() {
            return modifyBatchId;
        }

        public void setModifyBatchId(String modifyBatchId) {
            this.modifyBatchId = modifyBatchId;
        }

        public String getDiseaseType() {
            return diseaseType;
        }

        public void setDiseaseType(String diseaseType) {
            this.diseaseType = diseaseType;
        }

        public String getClassification() {
            return classification;
        }

        public void setClassification(String classification) {
            this.classification = classification;
        }

        public String getSubClassification() {
            return subClassification;
        }

        public void setSubClassification(String subClassification) {
            this.subClassification = subClassification;
        }

        public Integer getOfferQty() {
            return offerQty;
        }

        public void setOfferQty(Integer offerQty) {
            this.offerQty = offerQty;
        }

        public Integer getOfferAmount() {
            return offerAmount;
        }

        public void setOfferAmount(Integer offerAmount) {
            this.offerAmount = offerAmount;
        }

        public Integer getOfferDiscountType() {
            return offerDiscountType;
        }

        public void setOfferDiscountType(Integer offerDiscountType) {
            this.offerDiscountType = offerDiscountType;
        }

        public Integer getOfferDiscountValue() {
            return offerDiscountValue;
        }

        public void setOfferDiscountValue(Integer offerDiscountValue) {
            this.offerDiscountValue = offerDiscountValue;
        }

        public String getDiscountType() {
            return discountType;
        }

        public void setDiscountType(String discountType) {
            this.discountType = discountType;
        }

        public Boolean getMixMode() {
            return mixMode;
        }

        public void setMixMode(Boolean mixMode) {
            this.mixMode = mixMode;
        }

        public String getMMGroupId() {
            return mMGroupId;
        }

        public void setMMGroupId(String mMGroupId) {
            this.mMGroupId = mMGroupId;
        }

        public String getDiscId() {
            return discId;
        }

        public void setDiscId(String discId) {
            this.discId = discId;
        }

        public Integer getOfferType() {
            return offerType;
        }

        public void setOfferType(Integer offerType) {
            this.offerType = offerType;
        }

        public Integer getLineDiscPercentage() {
            return lineDiscPercentage;
        }

        public void setLineDiscPercentage(Integer lineDiscPercentage) {
            this.lineDiscPercentage = lineDiscPercentage;
        }

        public Boolean getApplyDiscount() {
            return applyDiscount;
        }

        public void setApplyDiscount(Boolean applyDiscount) {
            this.applyDiscount = applyDiscount;
        }

        public Integer getIGSTPerc() {
            return iGSTPerc;
        }

        public void setIGSTPerc(Integer iGSTPerc) {
            this.iGSTPerc = iGSTPerc;
        }

        public Integer getCESSPerc() {
            return cESSPerc;
        }

        public void setCESSPerc(Integer cESSPerc) {
            this.cESSPerc = cESSPerc;
        }

        public Integer getCGSTPerc() {
            return cGSTPerc;
        }

        public void setCGSTPerc(Integer cGSTPerc) {
            this.cGSTPerc = cGSTPerc;
        }

        public Integer getSGSTPerc() {
            return sGSTPerc;
        }

        public void setSGSTPerc(Integer sGSTPerc) {
            this.sGSTPerc = sGSTPerc;
        }

        public Integer getTotalTax() {
            return totalTax;
        }

        public void setTotalTax(Integer totalTax) {
            this.totalTax = totalTax;
        }

        public Object getIGSTTaxCode() {
            return iGSTTaxCode;
        }

        public void setIGSTTaxCode(Object iGSTTaxCode) {
            this.iGSTTaxCode = iGSTTaxCode;
        }

        public Object getCESSTaxCode() {
            return cESSTaxCode;
        }

        public void setCESSTaxCode(Object cESSTaxCode) {
            this.cESSTaxCode = cESSTaxCode;
        }

        public Object getCGSTTaxCode() {
            return cGSTTaxCode;
        }

        public void setCGSTTaxCode(Object cGSTTaxCode) {
            this.cGSTTaxCode = cGSTTaxCode;
        }

        public Object getSGSTTaxCode() {
            return sGSTTaxCode;
        }

        public void setSGSTTaxCode(Object sGSTTaxCode) {
            this.sGSTTaxCode = sGSTTaxCode;
        }

        public Integer getDiscountStructureType() {
            return discountStructureType;
        }

        public void setDiscountStructureType(Integer discountStructureType) {
            this.discountStructureType = discountStructureType;
        }

        public String getSubstitudeItemId() {
            return substitudeItemId;
        }

        public void setSubstitudeItemId(String substitudeItemId) {
            this.substitudeItemId = substitudeItemId;
        }

        public String getCategoryReference() {
            return categoryReference;
        }

        public void setCategoryReference(String categoryReference) {
            this.categoryReference = categoryReference;
        }

        public Integer getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(Integer orderStatus) {
            this.orderStatus = orderStatus;
        }

        public Integer getOmsLineID() {
            return omsLineID;
        }

        public void setOmsLineID(Integer omsLineID) {
            this.omsLineID = omsLineID;
        }

        public Boolean getIsSubsitute() {
            return isSubsitute;
        }

        public void setIsSubsitute(Boolean isSubsitute) {
            this.isSubsitute = isSubsitute;
        }

        public Boolean getIsGeneric() {
            return isGeneric;
        }

        public void setIsGeneric(Boolean isGeneric) {
            this.isGeneric = isGeneric;
        }

        public Integer getOmsLineRECID() {
            return omsLineRECID;
        }

        public void setOmsLineRECID(Integer omsLineRECID) {
            this.omsLineRECID = omsLineRECID;
        }

        public Boolean getISReserved() {
            return iSReserved;
        }

        public void setISReserved(Boolean iSReserved) {
            this.iSReserved = iSReserved;
        }

        public Boolean getISStockAvailable() {
            return iSStockAvailable;
        }

        public void setISStockAvailable(Boolean iSStockAvailable) {
            this.iSStockAvailable = iSStockAvailable;
        }

        public Boolean getISRestricted() {
            return iSRestricted;
        }

        public void setISRestricted(Boolean iSRestricted) {
            this.iSRestricted = iSRestricted;
        }

        public Object getPhysicalBatchID() {
            return physicalBatchID;
        }

        public void setPhysicalBatchID(Object physicalBatchID) {
            this.physicalBatchID = physicalBatchID;
        }

        public Integer getPhysicalMRP() {
            return physicalMRP;
        }

        public void setPhysicalMRP(Integer physicalMRP) {
            this.physicalMRP = physicalMRP;
        }

        public Object getPhysicalExpiry() {
            return physicalExpiry;
        }

        public void setPhysicalExpiry(Object physicalExpiry) {
            this.physicalExpiry = physicalExpiry;
        }

    }

    public static class TenderLine {

        @SerializedName("AuthentitcationCode")
        @Expose
        private String authentitcationCode;
        @SerializedName("LineNo")
        @Expose
        private Integer lineNo;
        @SerializedName("TenderId")
        @Expose
        private Integer tenderId;
        @SerializedName("TenderType")
        @Expose
        private Integer tenderType;
        @SerializedName("TenderName")
        @Expose
        private String tenderName;
        @SerializedName("ExchRate")
        @Expose
        private Integer exchRate;
        @SerializedName("ExchRateMst")
        @Expose
        private Integer exchRateMst;
        @SerializedName("AmountTendered")
        @Expose
        private Double amountTendered;
        @SerializedName("AmountCur")
        @Expose
        private Double amountCur;

        public String getAuthentitcationCode() {
            return authentitcationCode;
        }

        public void setAuthentitcationCode(String authentitcationCode) {
            this.authentitcationCode = authentitcationCode;
        }

        @SerializedName("AmountMst")
        @Expose
        private Double amountMst;
        @SerializedName("MobileNo")
        @Expose
        private String mobileNo;
        @SerializedName("WalletType")
        @Expose
        private Integer walletType;
        @SerializedName("WalletOrderId")
        @Expose
        private String walletOrderId;
        @SerializedName("WalletTransactionID")
        @Expose
        private String walletTransactionID;
        @SerializedName("RewardsPoint")
        @Expose
        private Integer rewardsPoint;
        @SerializedName("PreviewText")
        @Expose
        private String previewText;
        @SerializedName("IsVoid")
        @Expose
        private Boolean isVoid;
        @SerializedName("BarCode")
        @Expose
        private String barCode;

        public Integer getLineNo() {
            return lineNo;
        }

        public void setLineNo(Integer lineNo) {
            this.lineNo = lineNo;
        }

        public Integer getTenderId() {
            return tenderId;
        }

        public void setTenderId(Integer tenderId) {
            this.tenderId = tenderId;
        }

        public Integer getTenderType() {
            return tenderType;
        }

        public void setTenderType(Integer tenderType) {
            this.tenderType = tenderType;
        }

        public String getTenderName() {
            return tenderName;
        }

        public void setTenderName(String tenderName) {
            this.tenderName = tenderName;
        }

        public Integer getExchRate() {
            return exchRate;
        }

        public void setExchRate(Integer exchRate) {
            this.exchRate = exchRate;
        }

        public Integer getExchRateMst() {
            return exchRateMst;
        }

        public void setExchRateMst(Integer exchRateMst) {
            this.exchRateMst = exchRateMst;
        }

        public Double getAmountTendered() {
            return amountTendered;
        }

        public void setAmountTendered(Double amountTendered) {
            this.amountTendered = amountTendered;
        }

        public Double getAmountCur() {
            return amountCur;
        }

        public void setAmountCur(Double amountCur) {
            this.amountCur = amountCur;
        }

        public Double getAmountMst() {
            return amountMst;
        }

        public void setAmountMst(Double amountMst) {
            this.amountMst = amountMst;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public Integer getWalletType() {
            return walletType;
        }

        public void setWalletType(Integer walletType) {
            this.walletType = walletType;
        }

        public String getWalletOrderId() {
            return walletOrderId;
        }

        public void setWalletOrderId(String walletOrderId) {
            this.walletOrderId = walletOrderId;
        }

        public String getWalletTransactionID() {
            return walletTransactionID;
        }

        public void setWalletTransactionID(String walletTransactionID) {
            this.walletTransactionID = walletTransactionID;
        }

        public Integer getRewardsPoint() {
            return rewardsPoint;
        }

        public void setRewardsPoint(Integer rewardsPoint) {
            this.rewardsPoint = rewardsPoint;
        }

        public String getPreviewText() {
            return previewText;
        }

        public void setPreviewText(String previewText) {
            this.previewText = previewText;
        }

        public Boolean getIsVoid() {
            return isVoid;
        }

        public void setIsVoid(Boolean isVoid) {
            this.isVoid = isVoid;
        }

        public String getBarCode() {
            return barCode;
        }

        public void setBarCode(String barCode) {
            this.barCode = barCode;
        }

    }
}