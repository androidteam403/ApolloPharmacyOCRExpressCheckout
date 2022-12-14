package com.apollo.pharmacy.ocr.activities.paymentoptions.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExpressCheckoutTransactionApiResponse {

    @SerializedName("AWBNo")
    @Expose
    private String aWBNo;
    @SerializedName("AgeGroup")
    @Expose
    private String ageGroup;
    @SerializedName("AllowedTenderType")
    @Expose
    private String allowedTenderType;
    @SerializedName("AmazonPrintStatus")
    @Expose
    private Object amazonPrintStatus;
    @SerializedName("AmounttoAccount")
    @Expose
    private Integer amounttoAccount;
    @SerializedName("ApprovedID")
    @Expose
    private String approvedID;
    @SerializedName("AvailablePoint")
    @Expose
    private Integer availablePoint;
    @SerializedName("BatchTerminalid")
    @Expose
    private String batchTerminalid;
    @SerializedName("BillingCity")
    @Expose
    private String billingCity;
    @SerializedName("BusinessDate")
    @Expose
    private String businessDate;
    @SerializedName("CancelReasonCode")
    @Expose
    private String cancelReasonCode;
    @SerializedName("Channel")
    @Expose
    private String channel;
    @SerializedName("Comment")
    @Expose
    private String comment;
    @SerializedName("CorpCode")
    @Expose
    private String corpCode;
    @SerializedName("CouponCode")
    @Expose
    private String couponCode;
    @SerializedName("CreatedDateTime")
    @Expose
    private String createdDateTime;
    @SerializedName("CreatedonPosTerminal")
    @Expose
    private String createdonPosTerminal;
    @SerializedName("Currency")
    @Expose
    private String currency;
    @SerializedName("CustAccount")
    @Expose
    private String custAccount;
    @SerializedName("CustAddress")
    @Expose
    private String custAddress;
    @SerializedName("CustDiscamount")
    @Expose
    private Integer custDiscamount;
    @SerializedName("CustomerID")
    @Expose
    private String customerID;
    @SerializedName("CustomerName")
    @Expose
    private String customerName;
    @SerializedName("CustomerState")
    @Expose
    private String customerState;
    @SerializedName("CustomerType")
    @Expose
    private String customerType;
    @SerializedName("DOB")
    @Expose
    private String dob;
    @SerializedName("DSPCode")
    @Expose
    private String dSPCode;
    @SerializedName("DataAreaId")
    @Expose
    private String dataAreaId;
    @SerializedName("DeliveryDate")
    @Expose
    private String deliveryDate;
    @SerializedName("DiscAmount")
    @Expose
    private Integer discAmount;
    @SerializedName("DiscountRef")
    @Expose
    private String discountRef;
    @SerializedName("DiscountReferenceID")
    @Expose
    private String discountReferenceID;
    @SerializedName("DiscountStatus")
    @Expose
    private Integer discountStatus;
    @SerializedName("DoctorCode")
    @Expose
    private String doctorCode;
    @SerializedName("DoctorName")
    @Expose
    private String doctorName;
    @SerializedName("DonationAmount")
    @Expose
    private Integer donationAmount;
    @SerializedName("DspName")
    @Expose
    private String dspName;
    @SerializedName("EntryStatus")
    @Expose
    private Integer entryStatus;
    @SerializedName("ExpiryDays")
    @Expose
    private Integer expiryDays;
    @SerializedName("FwdReturnOtp")
    @Expose
    private String fwdReturnOtp;
    @SerializedName("Gender")
    @Expose
    private Integer gender;
    @SerializedName("GrossAmount")
    @Expose
    private Double grossAmount;
    @SerializedName("HCOfferCode")
    @Expose
    private String hCOfferCode;
    @SerializedName("IPNO")
    @Expose
    private String ipno;
    @SerializedName("IPSerialNO")
    @Expose
    private String iPSerialNO;
    @SerializedName("ISAdvancePayment")
    @Expose
    private Boolean iSAdvancePayment;
    @SerializedName("ISBatchModifiedAllowed")
    @Expose
    private Boolean iSBatchModifiedAllowed;
    @SerializedName("ISBulkBilling")
    @Expose
    private Boolean iSBulkBilling;
    @SerializedName("ISCancelled")
    @Expose
    private Boolean iSCancelled;
    @SerializedName("ISHBPStore")
    @Expose
    private Boolean iSHBPStore;
    @SerializedName("ISHyperDelivered")
    @Expose
    private Boolean iSHyperDelivered;
    @SerializedName("ISHyperLocalDelivery")
    @Expose
    private Boolean iSHyperLocalDelivery;
    @SerializedName("ISOMSOrder")
    @Expose
    private Boolean iSOMSOrder;
    @SerializedName("ISOMSReturn")
    @Expose
    private Boolean iSOMSReturn;
    @SerializedName("ISOMSValidate")
    @Expose
    private Boolean iSOMSValidate;
    @SerializedName("ISOnlineOrder")
    @Expose
    private Boolean iSOnlineOrder;
    @SerializedName("ISPosted")
    @Expose
    private Boolean iSPosted;
    @SerializedName("ISPrescibeDiscount")
    @Expose
    private Boolean iSPrescibeDiscount;
    @SerializedName("ISReserved")
    @Expose
    private Boolean iSReserved;
    @SerializedName("ISReturnAllowed")
    @Expose
    private Boolean iSReturnAllowed;
    @SerializedName("IsBulkDiscount")
    @Expose
    private Boolean isBulkDiscount;
    @SerializedName("IsHDOrder")
    @Expose
    private Boolean isHDOrder;
    @SerializedName("IsManualBill")
    @Expose
    private Boolean isManualBill;
    @SerializedName("IsOMSJurnalsScreen")
    @Expose
    private Boolean isOMSJurnalsScreen;
    @SerializedName("IsPickPackOrder")
    @Expose
    private Boolean isPickPackOrder;
    @SerializedName("IsRepeatBill")
    @Expose
    private Boolean isRepeatBill;
    @SerializedName("IsReturn")
    @Expose
    private Boolean isReturn;
    @SerializedName("IsStockCheck")
    @Expose
    private Boolean isStockCheck;
    @SerializedName("IsTPASeller")
    @Expose
    private Boolean isTPASeller;
    @SerializedName("IsUHIDBilling")
    @Expose
    private Boolean isUHIDBilling;
    @SerializedName("IsVoid")
    @Expose
    private Boolean isVoid;
    @SerializedName("MobileNO")
    @Expose
    private String mobileNO;
    @SerializedName("NetAmount")
    @Expose
    private Double netAmount;
    @SerializedName("NetAmountInclTax")
    @Expose
    private Double netAmountInclTax;
    @SerializedName("NumberofItemLines")
    @Expose
    private Integer numberofItemLines;
    @SerializedName("NumberofItems")
    @Expose
    private Integer numberofItems;
    @SerializedName("OMSCreditAmount")
    @Expose
    private Integer oMSCreditAmount;
    @SerializedName("OrderPrescriptionURL")
    @Expose
    private Object orderPrescriptionURL;
    @SerializedName("OrderSource")
    @Expose
    private String orderSource;
    @SerializedName("OrderType")
    @Expose
    private String orderType;
    @SerializedName("PatientID")
    @Expose
    private String patientID;
    @SerializedName("PaymentSource")
    @Expose
    private String paymentSource;
    @SerializedName("PickPackReservation")
    @Expose
    private Object pickPackReservation;
    @SerializedName("PickupOtp")
    @Expose
    private String pickupOtp;
    @SerializedName("PickupStatus")
    @Expose
    private Boolean pickupStatus;
    @SerializedName("Pincode")
    @Expose
    private String pincode;
    @SerializedName("PosEvent")
    @Expose
    private Integer posEvent;
    @SerializedName("REFNO")
    @Expose
    private String refno;
    @SerializedName("RTOStatus")
    @Expose
    private Boolean rTOStatus;
    @SerializedName("ReciptId")
    @Expose
    private String reciptId;
    @SerializedName("RegionCode")
    @Expose
    private String regionCode;
    @SerializedName("Remainingamount")
    @Expose
    private Double remainingamount;
    @SerializedName("ReminderDays")
    @Expose
    private Integer reminderDays;
    @SerializedName("RequestStatus")
    @Expose
    private Integer requestStatus;
    @SerializedName("ReturnMessage")
    @Expose
    private String returnMessage;
    @SerializedName("ReturnReceiptId")
    @Expose
    private String returnReceiptId;
    @SerializedName("ReturnRequestId")
    @Expose
    private String returnRequestId;
    @SerializedName("ReturnStore")
    @Expose
    private String returnStore;
    @SerializedName("ReturnTerminal")
    @Expose
    private String returnTerminal;
    @SerializedName("ReturnTransactionId")
    @Expose
    private String returnTransactionId;
    @SerializedName("ReturnType")
    @Expose
    private Integer returnType;
    @SerializedName("RevReturnOtp")
    @Expose
    private String revReturnOtp;
    @SerializedName("RiderCode")
    @Expose
    private String riderCode;
    @SerializedName("RiderMobile")
    @Expose
    private String riderMobile;
    @SerializedName("RiderName")
    @Expose
    private String riderName;
    @SerializedName("RoundedAmount")
    @Expose
    private Integer roundedAmount;
    @SerializedName("SEZ")
    @Expose
    private Integer sez;
    @SerializedName("SalesLine")
    @Expose
    private List<SalesLine> salesLine = null;
    @SerializedName("SalesOrigin")
    @Expose
    private String salesOrigin;
    @SerializedName("ShippingCharges")
    @Expose
    private Integer shippingCharges;
    @SerializedName("ShippingMethod")
    @Expose
    private String shippingMethod;
    @SerializedName("Staff")
    @Expose
    private String staff;
    @SerializedName("State")
    @Expose
    private String state;
    @SerializedName("StockStatus")
    @Expose
    private String stockStatus;
    @SerializedName("Store")
    @Expose
    private String store;
    @SerializedName("StoreName")
    @Expose
    private String storeName;
    @SerializedName("TenderLine")
    @Expose
    private List<TenderLine> tenderLine = null;
    @SerializedName("Terminal")
    @Expose
    private String terminal;
    @SerializedName("Tier")
    @Expose
    private String tier;
    @SerializedName("TimeSlot")
    @Expose
    private String timeSlot;
    @SerializedName("TimewhenTransClosed")
    @Expose
    private Integer timewhenTransClosed;
    @SerializedName("TotalDiscAmount")
    @Expose
    private Integer totalDiscAmount;
    @SerializedName("TotalMRP")
    @Expose
    private Double totalMRP;
    @SerializedName("TotalManualDiscountAmount")
    @Expose
    private Integer totalManualDiscountAmount;
    @SerializedName("TotalManualDiscountPercentage")
    @Expose
    private Integer totalManualDiscountPercentage;
    @SerializedName("TotalTaxAmount")
    @Expose
    private Double totalTaxAmount;
    @SerializedName("TrackingRef")
    @Expose
    private String trackingRef;
    @SerializedName("TransDate")
    @Expose
    private String transDate;
    @SerializedName("TransType")
    @Expose
    private Integer transType;
    @SerializedName("TransactionId")
    @Expose
    private String transactionId;
    @SerializedName("Type")
    @Expose
    private Integer type;
    @SerializedName("VendorCode")
    @Expose
    private String vendorCode;
    @SerializedName("VendorId")
    @Expose
    private String vendorId;

    public String getAWBNo() {
        return aWBNo;
    }

    public void setAWBNo(String aWBNo) {
        this.aWBNo = aWBNo;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getAllowedTenderType() {
        return allowedTenderType;
    }

    public void setAllowedTenderType(String allowedTenderType) {
        this.allowedTenderType = allowedTenderType;
    }

    public Object getAmazonPrintStatus() {
        return amazonPrintStatus;
    }

    public void setAmazonPrintStatus(Object amazonPrintStatus) {
        this.amazonPrintStatus = amazonPrintStatus;
    }

    public Integer getAmounttoAccount() {
        return amounttoAccount;
    }

    public void setAmounttoAccount(Integer amounttoAccount) {
        this.amounttoAccount = amounttoAccount;
    }

    public String getApprovedID() {
        return approvedID;
    }

    public void setApprovedID(String approvedID) {
        this.approvedID = approvedID;
    }

    public Integer getAvailablePoint() {
        return availablePoint;
    }

    public void setAvailablePoint(Integer availablePoint) {
        this.availablePoint = availablePoint;
    }

    public String getBatchTerminalid() {
        return batchTerminalid;
    }

    public void setBatchTerminalid(String batchTerminalid) {
        this.batchTerminalid = batchTerminalid;
    }

    public String getBillingCity() {
        return billingCity;
    }

    public void setBillingCity(String billingCity) {
        this.billingCity = billingCity;
    }

    public String getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    public String getCancelReasonCode() {
        return cancelReasonCode;
    }

    public void setCancelReasonCode(String cancelReasonCode) {
        this.cancelReasonCode = cancelReasonCode;
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

    public String getCorpCode() {
        return corpCode;
    }

    public void setCorpCode(String corpCode) {
        this.corpCode = corpCode;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
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

    public String getCustAddress() {
        return custAddress;
    }

    public void setCustAddress(String custAddress) {
        this.custAddress = custAddress;
    }

    public Integer getCustDiscamount() {
        return custDiscamount;
    }

    public void setCustDiscamount(Integer custDiscamount) {
        this.custDiscamount = custDiscamount;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerState() {
        return customerState;
    }

    public void setCustomerState(String customerState) {
        this.customerState = customerState;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDSPCode() {
        return dSPCode;
    }

    public void setDSPCode(String dSPCode) {
        this.dSPCode = dSPCode;
    }

    public String getDataAreaId() {
        return dataAreaId;
    }

    public void setDataAreaId(String dataAreaId) {
        this.dataAreaId = dataAreaId;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Integer getDiscAmount() {
        return discAmount;
    }

    public void setDiscAmount(Integer discAmount) {
        this.discAmount = discAmount;
    }

    public String getDiscountRef() {
        return discountRef;
    }

    public void setDiscountRef(String discountRef) {
        this.discountRef = discountRef;
    }

    public String getDiscountReferenceID() {
        return discountReferenceID;
    }

    public void setDiscountReferenceID(String discountReferenceID) {
        this.discountReferenceID = discountReferenceID;
    }

    public Integer getDiscountStatus() {
        return discountStatus;
    }

    public void setDiscountStatus(Integer discountStatus) {
        this.discountStatus = discountStatus;
    }

    public String getDoctorCode() {
        return doctorCode;
    }

    public void setDoctorCode(String doctorCode) {
        this.doctorCode = doctorCode;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public Integer getDonationAmount() {
        return donationAmount;
    }

    public void setDonationAmount(Integer donationAmount) {
        this.donationAmount = donationAmount;
    }

    public String getDspName() {
        return dspName;
    }

    public void setDspName(String dspName) {
        this.dspName = dspName;
    }

    public Integer getEntryStatus() {
        return entryStatus;
    }

    public void setEntryStatus(Integer entryStatus) {
        this.entryStatus = entryStatus;
    }

    public Integer getExpiryDays() {
        return expiryDays;
    }

    public void setExpiryDays(Integer expiryDays) {
        this.expiryDays = expiryDays;
    }

    public String getFwdReturnOtp() {
        return fwdReturnOtp;
    }

    public void setFwdReturnOtp(String fwdReturnOtp) {
        this.fwdReturnOtp = fwdReturnOtp;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Double getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(Double grossAmount) {
        this.grossAmount = grossAmount;
    }

    public String getHCOfferCode() {
        return hCOfferCode;
    }

    public void setHCOfferCode(String hCOfferCode) {
        this.hCOfferCode = hCOfferCode;
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

    public Boolean getISAdvancePayment() {
        return iSAdvancePayment;
    }

    public void setISAdvancePayment(Boolean iSAdvancePayment) {
        this.iSAdvancePayment = iSAdvancePayment;
    }

    public Boolean getISBatchModifiedAllowed() {
        return iSBatchModifiedAllowed;
    }

    public void setISBatchModifiedAllowed(Boolean iSBatchModifiedAllowed) {
        this.iSBatchModifiedAllowed = iSBatchModifiedAllowed;
    }

    public Boolean getISBulkBilling() {
        return iSBulkBilling;
    }

    public void setISBulkBilling(Boolean iSBulkBilling) {
        this.iSBulkBilling = iSBulkBilling;
    }

    public Boolean getISCancelled() {
        return iSCancelled;
    }

    public void setISCancelled(Boolean iSCancelled) {
        this.iSCancelled = iSCancelled;
    }

    public Boolean getISHBPStore() {
        return iSHBPStore;
    }

    public void setISHBPStore(Boolean iSHBPStore) {
        this.iSHBPStore = iSHBPStore;
    }

    public Boolean getISHyperDelivered() {
        return iSHyperDelivered;
    }

    public void setISHyperDelivered(Boolean iSHyperDelivered) {
        this.iSHyperDelivered = iSHyperDelivered;
    }

    public Boolean getISHyperLocalDelivery() {
        return iSHyperLocalDelivery;
    }

    public void setISHyperLocalDelivery(Boolean iSHyperLocalDelivery) {
        this.iSHyperLocalDelivery = iSHyperLocalDelivery;
    }

    public Boolean getISOMSOrder() {
        return iSOMSOrder;
    }

    public void setISOMSOrder(Boolean iSOMSOrder) {
        this.iSOMSOrder = iSOMSOrder;
    }

    public Boolean getISOMSReturn() {
        return iSOMSReturn;
    }

    public void setISOMSReturn(Boolean iSOMSReturn) {
        this.iSOMSReturn = iSOMSReturn;
    }

    public Boolean getISOMSValidate() {
        return iSOMSValidate;
    }

    public void setISOMSValidate(Boolean iSOMSValidate) {
        this.iSOMSValidate = iSOMSValidate;
    }

    public Boolean getISOnlineOrder() {
        return iSOnlineOrder;
    }

    public void setISOnlineOrder(Boolean iSOnlineOrder) {
        this.iSOnlineOrder = iSOnlineOrder;
    }

    public Boolean getISPosted() {
        return iSPosted;
    }

    public void setISPosted(Boolean iSPosted) {
        this.iSPosted = iSPosted;
    }

    public Boolean getISPrescibeDiscount() {
        return iSPrescibeDiscount;
    }

    public void setISPrescibeDiscount(Boolean iSPrescibeDiscount) {
        this.iSPrescibeDiscount = iSPrescibeDiscount;
    }

    public Boolean getISReserved() {
        return iSReserved;
    }

    public void setISReserved(Boolean iSReserved) {
        this.iSReserved = iSReserved;
    }

    public Boolean getISReturnAllowed() {
        return iSReturnAllowed;
    }

    public void setISReturnAllowed(Boolean iSReturnAllowed) {
        this.iSReturnAllowed = iSReturnAllowed;
    }

    public Boolean getIsBulkDiscount() {
        return isBulkDiscount;
    }

    public void setIsBulkDiscount(Boolean isBulkDiscount) {
        this.isBulkDiscount = isBulkDiscount;
    }

    public Boolean getIsHDOrder() {
        return isHDOrder;
    }

    public void setIsHDOrder(Boolean isHDOrder) {
        this.isHDOrder = isHDOrder;
    }

    public Boolean getIsManualBill() {
        return isManualBill;
    }

    public void setIsManualBill(Boolean isManualBill) {
        this.isManualBill = isManualBill;
    }

    public Boolean getIsOMSJurnalsScreen() {
        return isOMSJurnalsScreen;
    }

    public void setIsOMSJurnalsScreen(Boolean isOMSJurnalsScreen) {
        this.isOMSJurnalsScreen = isOMSJurnalsScreen;
    }

    public Boolean getIsPickPackOrder() {
        return isPickPackOrder;
    }

    public void setIsPickPackOrder(Boolean isPickPackOrder) {
        this.isPickPackOrder = isPickPackOrder;
    }

    public Boolean getIsRepeatBill() {
        return isRepeatBill;
    }

    public void setIsRepeatBill(Boolean isRepeatBill) {
        this.isRepeatBill = isRepeatBill;
    }

    public Boolean getIsReturn() {
        return isReturn;
    }

    public void setIsReturn(Boolean isReturn) {
        this.isReturn = isReturn;
    }

    public Boolean getIsStockCheck() {
        return isStockCheck;
    }

    public void setIsStockCheck(Boolean isStockCheck) {
        this.isStockCheck = isStockCheck;
    }

    public Boolean getIsTPASeller() {
        return isTPASeller;
    }

    public void setIsTPASeller(Boolean isTPASeller) {
        this.isTPASeller = isTPASeller;
    }

    public Boolean getIsUHIDBilling() {
        return isUHIDBilling;
    }

    public void setIsUHIDBilling(Boolean isUHIDBilling) {
        this.isUHIDBilling = isUHIDBilling;
    }

    public Boolean getIsVoid() {
        return isVoid;
    }

    public void setIsVoid(Boolean isVoid) {
        this.isVoid = isVoid;
    }

    public String getMobileNO() {
        return mobileNO;
    }

    public void setMobileNO(String mobileNO) {
        this.mobileNO = mobileNO;
    }

    public Double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(Double netAmount) {
        this.netAmount = netAmount;
    }

    public Double getNetAmountInclTax() {
        return netAmountInclTax;
    }

    public void setNetAmountInclTax(Double netAmountInclTax) {
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

    public Integer getOMSCreditAmount() {
        return oMSCreditAmount;
    }

    public void setOMSCreditAmount(Integer oMSCreditAmount) {
        this.oMSCreditAmount = oMSCreditAmount;
    }

    public Object getOrderPrescriptionURL() {
        return orderPrescriptionURL;
    }

    public void setOrderPrescriptionURL(Object orderPrescriptionURL) {
        this.orderPrescriptionURL = orderPrescriptionURL;
    }

    public String getOrderSource() {
        return orderSource;
    }

    public void setOrderSource(String orderSource) {
        this.orderSource = orderSource;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getPaymentSource() {
        return paymentSource;
    }

    public void setPaymentSource(String paymentSource) {
        this.paymentSource = paymentSource;
    }

    public Object getPickPackReservation() {
        return pickPackReservation;
    }

    public void setPickPackReservation(Object pickPackReservation) {
        this.pickPackReservation = pickPackReservation;
    }

    public String getPickupOtp() {
        return pickupOtp;
    }

    public void setPickupOtp(String pickupOtp) {
        this.pickupOtp = pickupOtp;
    }

    public Boolean getPickupStatus() {
        return pickupStatus;
    }

    public void setPickupStatus(Boolean pickupStatus) {
        this.pickupStatus = pickupStatus;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public Integer getPosEvent() {
        return posEvent;
    }

    public void setPosEvent(Integer posEvent) {
        this.posEvent = posEvent;
    }

    public String getRefno() {
        return refno;
    }

    public void setRefno(String refno) {
        this.refno = refno;
    }

    public Boolean getRTOStatus() {
        return rTOStatus;
    }

    public void setRTOStatus(Boolean rTOStatus) {
        this.rTOStatus = rTOStatus;
    }

    public String getReciptId() {
        return reciptId;
    }

    public void setReciptId(String reciptId) {
        this.reciptId = reciptId;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public Double getRemainingamount() {
        return remainingamount;
    }

    public void setRemainingamount(Double remainingamount) {
        this.remainingamount = remainingamount;
    }

    public Integer getReminderDays() {
        return reminderDays;
    }

    public void setReminderDays(Integer reminderDays) {
        this.reminderDays = reminderDays;
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

    public String getReturnReceiptId() {
        return returnReceiptId;
    }

    public void setReturnReceiptId(String returnReceiptId) {
        this.returnReceiptId = returnReceiptId;
    }

    public String getReturnRequestId() {
        return returnRequestId;
    }

    public void setReturnRequestId(String returnRequestId) {
        this.returnRequestId = returnRequestId;
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

    public Integer getReturnType() {
        return returnType;
    }

    public void setReturnType(Integer returnType) {
        this.returnType = returnType;
    }

    public String getRevReturnOtp() {
        return revReturnOtp;
    }

    public void setRevReturnOtp(String revReturnOtp) {
        this.revReturnOtp = revReturnOtp;
    }

    public String getRiderCode() {
        return riderCode;
    }

    public void setRiderCode(String riderCode) {
        this.riderCode = riderCode;
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

    public Integer getRoundedAmount() {
        return roundedAmount;
    }

    public void setRoundedAmount(Integer roundedAmount) {
        this.roundedAmount = roundedAmount;
    }

    public Integer getSez() {
        return sez;
    }

    public void setSez(Integer sez) {
        this.sez = sez;
    }

    public List<SalesLine> getSalesLine() {
        return salesLine;
    }

    public void setSalesLine(List<SalesLine> salesLine) {
        this.salesLine = salesLine;
    }

    public String getSalesOrigin() {
        return salesOrigin;
    }

    public void setSalesOrigin(String salesOrigin) {
        this.salesOrigin = salesOrigin;
    }

    public Integer getShippingCharges() {
        return shippingCharges;
    }

    public void setShippingCharges(Integer shippingCharges) {
        this.shippingCharges = shippingCharges;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public List<TenderLine> getTenderLine() {
        return tenderLine;
    }

    public void setTenderLine(List<TenderLine> tenderLine) {
        this.tenderLine = tenderLine;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
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

    public Double getTotalMRP() {
        return totalMRP;
    }

    public void setTotalMRP(Double totalMRP) {
        this.totalMRP = totalMRP;
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

    public Double getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public void setTotalTaxAmount(Double totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }

    public String getTrackingRef() {
        return trackingRef;
    }

    public void setTrackingRef(String trackingRef) {
        this.trackingRef = trackingRef;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public Integer getTransType() {
        return transType;
    }

    public void setTransType(Integer transType) {
        this.transType = transType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getVendorCode() {
        return vendorCode;
    }

    public void setVendorCode(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }
    public class SalesLine {

        @SerializedName("Additionaltax")
        @Expose
        private Integer additionaltax;
        @SerializedName("ApplyDiscount")
        @Expose
        private Boolean applyDiscount;
        @SerializedName("Barcode")
        @Expose
        private String barcode;
        @SerializedName("BaseAmount")
        @Expose
        private Double baseAmount;
        @SerializedName("CESSPerc")
        @Expose
        private Integer cESSPerc;
        @SerializedName("CESSTaxCode")
        @Expose
        private String cESSTaxCode;
        @SerializedName("CGSTPerc")
        @Expose
        private Integer cGSTPerc;
        @SerializedName("CGSTTaxCode")
        @Expose
        private String cGSTTaxCode;
        @SerializedName("Category")
        @Expose
        private String category;
        @SerializedName("CategoryCode")
        @Expose
        private String categoryCode;
        @SerializedName("CategoryReference")
        @Expose
        private String categoryReference;
        @SerializedName("Classification")
        @Expose
        private String classification;
        @SerializedName("Comment")
        @Expose
        private String comment;
        @SerializedName("DPCO")
        @Expose
        private Boolean dpco;
        @SerializedName("DiscAmount")
        @Expose
        private Integer discAmount;
        @SerializedName("DiscId")
        @Expose
        private String discId;
        @SerializedName("DiscOfferId")
        @Expose
        private String discOfferId;
        @SerializedName("DiscountStructureType")
        @Expose
        private Integer discountStructureType;
        @SerializedName("DiscountType")
        @Expose
        private String discountType;
        @SerializedName("DiseaseType")
        @Expose
        private String diseaseType;
        @SerializedName("Expiry")
        @Expose
        private String expiry;
        @SerializedName("Hsncode_In")
        @Expose
        private String hsncodeIn;
        @SerializedName("IGSTPerc")
        @Expose
        private Integer iGSTPerc;
        @SerializedName("IGSTTaxCode")
        @Expose
        private String iGSTTaxCode;
        @SerializedName("ISPrescribed")
        @Expose
        private Integer iSPrescribed;
        @SerializedName("ISReserved")
        @Expose
        private Boolean iSReserved;
        @SerializedName("ISRestricted")
        @Expose
        private Boolean iSRestricted;
        @SerializedName("ISStockAvailable")
        @Expose
        private Boolean iSStockAvailable;
        @SerializedName("InventBatchId")
        @Expose
        private String inventBatchId;
        @SerializedName("IsChecked")
        @Expose
        private Boolean isChecked;
        @SerializedName("IsGeneric")
        @Expose
        private Boolean isGeneric;
        @SerializedName("IsPriceOverride")
        @Expose
        private Boolean isPriceOverride;
        @SerializedName("IsSubsitute")
        @Expose
        private Boolean isSubsitute;
        @SerializedName("IsVoid")
        @Expose
        private Boolean isVoid;
        @SerializedName("ItemId")
        @Expose
        private String itemId;
        @SerializedName("ItemName")
        @Expose
        private String itemName;
        @SerializedName("LineDiscPercentage")
        @Expose
        private Integer lineDiscPercentage;
        @SerializedName("LineManualDiscountAmount")
        @Expose
        private Integer lineManualDiscountAmount;
        @SerializedName("LineManualDiscountPercentage")
        @Expose
        private Integer lineManualDiscountPercentage;
        @SerializedName("LineNo")
        @Expose
        private Integer lineNo;
        @SerializedName("LinedscAmount")
        @Expose
        private Integer linedscAmount;
        @SerializedName("MMGroupId")
        @Expose
        private String mMGroupId;
        @SerializedName("MRP")
        @Expose
        private Double mrp;
        @SerializedName("ManufacturerCode")
        @Expose
        private String manufacturerCode;
        @SerializedName("ManufacturerName")
        @Expose
        private String manufacturerName;
        @SerializedName("MixMode")
        @Expose
        private Boolean mixMode;
        @SerializedName("ModifyBatchId")
        @Expose
        private String modifyBatchId;
        @SerializedName("NetAmount")
        @Expose
        private Double netAmount;
        @SerializedName("NetAmountInclTax")
        @Expose
        private Double netAmountInclTax;
        @SerializedName("OfferAmount")
        @Expose
        private Integer offerAmount;
        @SerializedName("OfferDiscountType")
        @Expose
        private Integer offerDiscountType;
        @SerializedName("OfferDiscountValue")
        @Expose
        private Integer offerDiscountValue;
        @SerializedName("OfferQty")
        @Expose
        private Integer offerQty;
        @SerializedName("OfferType")
        @Expose
        private Integer offerType;
        @SerializedName("OmsLineID")
        @Expose
        private Integer omsLineID;
        @SerializedName("OmsLineRECID")
        @Expose
        private Integer omsLineRECID;
        @SerializedName("OrderStatus")
        @Expose
        private Integer orderStatus;
        @SerializedName("OriginalPrice")
        @Expose
        private Double originalPrice;
        @SerializedName("PeriodicDiscAmount")
        @Expose
        private Integer periodicDiscAmount;
        @SerializedName("PhysicalBatchID")
        @Expose
        private Object physicalBatchID;
        @SerializedName("PhysicalExpiry")
        @Expose
        private Object physicalExpiry;
        @SerializedName("PhysicalMRP")
        @Expose
        private Integer physicalMRP;
        @SerializedName("PreviewText")
        @Expose
        private String previewText;
        @SerializedName("Price")
        @Expose
        private Double price;
        @SerializedName("PriceVariation")
        @Expose
        private Boolean priceVariation;
        @SerializedName("ProductRecID")
        @Expose
        private String productRecID;
        @SerializedName("QCDate")
        @Expose
        private String qCDate;
        @SerializedName("QCRemarks")
        @Expose
        private String qCRemarks;
        @SerializedName("QCStatus")
        @Expose
        private Integer qCStatus;
        @SerializedName("Qty")
        @Expose
        private Integer qty;
        @SerializedName("RackId")
        @Expose
        private String rackId;
        @SerializedName("ReasonCode")
        @Expose
        private String reasonCode;
        @SerializedName("RemainderDays")
        @Expose
        private Integer remainderDays;
        @SerializedName("RemainingQty")
        @Expose
        private Integer remainingQty;
        @SerializedName("RetailCategoryRecID")
        @Expose
        private String retailCategoryRecID;
        @SerializedName("RetailMainCategoryRecID")
        @Expose
        private String retailMainCategoryRecID;
        @SerializedName("RetailSubCategoryRecID")
        @Expose
        private String retailSubCategoryRecID;
        @SerializedName("ReturnQty")
        @Expose
        private Integer returnQty;
        @SerializedName("SGSTPerc")
        @Expose
        private Integer sGSTPerc;
        @SerializedName("SGSTTaxCode")
        @Expose
        private String sGSTTaxCode;
        @SerializedName("ScheduleCategory")
        @Expose
        private String scheduleCategory;
        @SerializedName("ScheduleCategoryCode")
        @Expose
        private String scheduleCategoryCode;
        @SerializedName("StockQty")
        @Expose
        private Integer stockQty;
        @SerializedName("SubCategory")
        @Expose
        private String subCategory;
        @SerializedName("SubCategoryCode")
        @Expose
        private String subCategoryCode;
        @SerializedName("SubClassification")
        @Expose
        private String subClassification;
        @SerializedName("SubstitudeItemId")
        @Expose
        private String substitudeItemId;
        @SerializedName("Tax")
        @Expose
        private Integer tax;
        @SerializedName("TaxAmount")
        @Expose
        private Double taxAmount;
        @SerializedName("Total")
        @Expose
        private Double total;
        @SerializedName("TotalDiscAmount")
        @Expose
        private Integer totalDiscAmount;
        @SerializedName("TotalDiscPct")
        @Expose
        private Integer totalDiscPct;
        @SerializedName("TotalRoundedAmount")
        @Expose
        private Integer totalRoundedAmount;
        @SerializedName("TotalTax")
        @Expose
        private Integer totalTax;
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

        public Integer getAdditionaltax() {
            return additionaltax;
        }

        public void setAdditionaltax(Integer additionaltax) {
            this.additionaltax = additionaltax;
        }

        public Boolean getApplyDiscount() {
            return applyDiscount;
        }

        public void setApplyDiscount(Boolean applyDiscount) {
            this.applyDiscount = applyDiscount;
        }

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        public Double getBaseAmount() {
            return baseAmount;
        }

        public void setBaseAmount(Double baseAmount) {
            this.baseAmount = baseAmount;
        }

        public Integer getCESSPerc() {
            return cESSPerc;
        }

        public void setCESSPerc(Integer cESSPerc) {
            this.cESSPerc = cESSPerc;
        }

        public String getCESSTaxCode() {
            return cESSTaxCode;
        }

        public void setCESSTaxCode(String cESSTaxCode) {
            this.cESSTaxCode = cESSTaxCode;
        }

        public Integer getCGSTPerc() {
            return cGSTPerc;
        }

        public void setCGSTPerc(Integer cGSTPerc) {
            this.cGSTPerc = cGSTPerc;
        }

        public String getCGSTTaxCode() {
            return cGSTTaxCode;
        }

        public void setCGSTTaxCode(String cGSTTaxCode) {
            this.cGSTTaxCode = cGSTTaxCode;
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

        public String getCategoryReference() {
            return categoryReference;
        }

        public void setCategoryReference(String categoryReference) {
            this.categoryReference = categoryReference;
        }

        public String getClassification() {
            return classification;
        }

        public void setClassification(String classification) {
            this.classification = classification;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public Boolean getDpco() {
            return dpco;
        }

        public void setDpco(Boolean dpco) {
            this.dpco = dpco;
        }

        public Integer getDiscAmount() {
            return discAmount;
        }

        public void setDiscAmount(Integer discAmount) {
            this.discAmount = discAmount;
        }

        public String getDiscId() {
            return discId;
        }

        public void setDiscId(String discId) {
            this.discId = discId;
        }

        public String getDiscOfferId() {
            return discOfferId;
        }

        public void setDiscOfferId(String discOfferId) {
            this.discOfferId = discOfferId;
        }

        public Integer getDiscountStructureType() {
            return discountStructureType;
        }

        public void setDiscountStructureType(Integer discountStructureType) {
            this.discountStructureType = discountStructureType;
        }

        public String getDiscountType() {
            return discountType;
        }

        public void setDiscountType(String discountType) {
            this.discountType = discountType;
        }

        public String getDiseaseType() {
            return diseaseType;
        }

        public void setDiseaseType(String diseaseType) {
            this.diseaseType = diseaseType;
        }

        public String getExpiry() {
            return expiry;
        }

        public void setExpiry(String expiry) {
            this.expiry = expiry;
        }

        public String getHsncodeIn() {
            return hsncodeIn;
        }

        public void setHsncodeIn(String hsncodeIn) {
            this.hsncodeIn = hsncodeIn;
        }

        public Integer getIGSTPerc() {
            return iGSTPerc;
        }

        public void setIGSTPerc(Integer iGSTPerc) {
            this.iGSTPerc = iGSTPerc;
        }

        public String getIGSTTaxCode() {
            return iGSTTaxCode;
        }

        public void setIGSTTaxCode(String iGSTTaxCode) {
            this.iGSTTaxCode = iGSTTaxCode;
        }

        public Integer getISPrescribed() {
            return iSPrescribed;
        }

        public void setISPrescribed(Integer iSPrescribed) {
            this.iSPrescribed = iSPrescribed;
        }

        public Boolean getISReserved() {
            return iSReserved;
        }

        public void setISReserved(Boolean iSReserved) {
            this.iSReserved = iSReserved;
        }

        public Boolean getISRestricted() {
            return iSRestricted;
        }

        public void setISRestricted(Boolean iSRestricted) {
            this.iSRestricted = iSRestricted;
        }

        public Boolean getISStockAvailable() {
            return iSStockAvailable;
        }

        public void setISStockAvailable(Boolean iSStockAvailable) {
            this.iSStockAvailable = iSStockAvailable;
        }

        public String getInventBatchId() {
            return inventBatchId;
        }

        public void setInventBatchId(String inventBatchId) {
            this.inventBatchId = inventBatchId;
        }

        public Boolean getIsChecked() {
            return isChecked;
        }

        public void setIsChecked(Boolean isChecked) {
            this.isChecked = isChecked;
        }

        public Boolean getIsGeneric() {
            return isGeneric;
        }

        public void setIsGeneric(Boolean isGeneric) {
            this.isGeneric = isGeneric;
        }

        public Boolean getIsPriceOverride() {
            return isPriceOverride;
        }

        public void setIsPriceOverride(Boolean isPriceOverride) {
            this.isPriceOverride = isPriceOverride;
        }

        public Boolean getIsSubsitute() {
            return isSubsitute;
        }

        public void setIsSubsitute(Boolean isSubsitute) {
            this.isSubsitute = isSubsitute;
        }

        public Boolean getIsVoid() {
            return isVoid;
        }

        public void setIsVoid(Boolean isVoid) {
            this.isVoid = isVoid;
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

        public Integer getLineDiscPercentage() {
            return lineDiscPercentage;
        }

        public void setLineDiscPercentage(Integer lineDiscPercentage) {
            this.lineDiscPercentage = lineDiscPercentage;
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

        public Integer getLineNo() {
            return lineNo;
        }

        public void setLineNo(Integer lineNo) {
            this.lineNo = lineNo;
        }

        public Integer getLinedscAmount() {
            return linedscAmount;
        }

        public void setLinedscAmount(Integer linedscAmount) {
            this.linedscAmount = linedscAmount;
        }

        public String getMMGroupId() {
            return mMGroupId;
        }

        public void setMMGroupId(String mMGroupId) {
            this.mMGroupId = mMGroupId;
        }

        public Double getMrp() {
            return mrp;
        }

        public void setMrp(Double mrp) {
            this.mrp = mrp;
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

        public Boolean getMixMode() {
            return mixMode;
        }

        public void setMixMode(Boolean mixMode) {
            this.mixMode = mixMode;
        }

        public String getModifyBatchId() {
            return modifyBatchId;
        }

        public void setModifyBatchId(String modifyBatchId) {
            this.modifyBatchId = modifyBatchId;
        }

        public Double getNetAmount() {
            return netAmount;
        }

        public void setNetAmount(Double netAmount) {
            this.netAmount = netAmount;
        }

        public Double getNetAmountInclTax() {
            return netAmountInclTax;
        }

        public void setNetAmountInclTax(Double netAmountInclTax) {
            this.netAmountInclTax = netAmountInclTax;
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

        public Integer getOfferQty() {
            return offerQty;
        }

        public void setOfferQty(Integer offerQty) {
            this.offerQty = offerQty;
        }

        public Integer getOfferType() {
            return offerType;
        }

        public void setOfferType(Integer offerType) {
            this.offerType = offerType;
        }

        public Integer getOmsLineID() {
            return omsLineID;
        }

        public void setOmsLineID(Integer omsLineID) {
            this.omsLineID = omsLineID;
        }

        public Integer getOmsLineRECID() {
            return omsLineRECID;
        }

        public void setOmsLineRECID(Integer omsLineRECID) {
            this.omsLineRECID = omsLineRECID;
        }

        public Integer getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(Integer orderStatus) {
            this.orderStatus = orderStatus;
        }

        public Double getOriginalPrice() {
            return originalPrice;
        }

        public void setOriginalPrice(Double originalPrice) {
            this.originalPrice = originalPrice;
        }

        public Integer getPeriodicDiscAmount() {
            return periodicDiscAmount;
        }

        public void setPeriodicDiscAmount(Integer periodicDiscAmount) {
            this.periodicDiscAmount = periodicDiscAmount;
        }

        public Object getPhysicalBatchID() {
            return physicalBatchID;
        }

        public void setPhysicalBatchID(Object physicalBatchID) {
            this.physicalBatchID = physicalBatchID;
        }

        public Object getPhysicalExpiry() {
            return physicalExpiry;
        }

        public void setPhysicalExpiry(Object physicalExpiry) {
            this.physicalExpiry = physicalExpiry;
        }

        public Integer getPhysicalMRP() {
            return physicalMRP;
        }

        public void setPhysicalMRP(Integer physicalMRP) {
            this.physicalMRP = physicalMRP;
        }

        public String getPreviewText() {
            return previewText;
        }

        public void setPreviewText(String previewText) {
            this.previewText = previewText;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public Boolean getPriceVariation() {
            return priceVariation;
        }

        public void setPriceVariation(Boolean priceVariation) {
            this.priceVariation = priceVariation;
        }

        public String getProductRecID() {
            return productRecID;
        }

        public void setProductRecID(String productRecID) {
            this.productRecID = productRecID;
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

        public Integer getQCStatus() {
            return qCStatus;
        }

        public void setQCStatus(Integer qCStatus) {
            this.qCStatus = qCStatus;
        }

        public Integer getQty() {
            return qty;
        }

        public void setQty(Integer qty) {
            this.qty = qty;
        }

        public String getRackId() {
            return rackId;
        }

        public void setRackId(String rackId) {
            this.rackId = rackId;
        }

        public String getReasonCode() {
            return reasonCode;
        }

        public void setReasonCode(String reasonCode) {
            this.reasonCode = reasonCode;
        }

        public Integer getRemainderDays() {
            return remainderDays;
        }

        public void setRemainderDays(Integer remainderDays) {
            this.remainderDays = remainderDays;
        }

        public Integer getRemainingQty() {
            return remainingQty;
        }

        public void setRemainingQty(Integer remainingQty) {
            this.remainingQty = remainingQty;
        }

        public String getRetailCategoryRecID() {
            return retailCategoryRecID;
        }

        public void setRetailCategoryRecID(String retailCategoryRecID) {
            this.retailCategoryRecID = retailCategoryRecID;
        }

        public String getRetailMainCategoryRecID() {
            return retailMainCategoryRecID;
        }

        public void setRetailMainCategoryRecID(String retailMainCategoryRecID) {
            this.retailMainCategoryRecID = retailMainCategoryRecID;
        }

        public String getRetailSubCategoryRecID() {
            return retailSubCategoryRecID;
        }

        public void setRetailSubCategoryRecID(String retailSubCategoryRecID) {
            this.retailSubCategoryRecID = retailSubCategoryRecID;
        }

        public Integer getReturnQty() {
            return returnQty;
        }

        public void setReturnQty(Integer returnQty) {
            this.returnQty = returnQty;
        }

        public Integer getSGSTPerc() {
            return sGSTPerc;
        }

        public void setSGSTPerc(Integer sGSTPerc) {
            this.sGSTPerc = sGSTPerc;
        }

        public String getSGSTTaxCode() {
            return sGSTTaxCode;
        }

        public void setSGSTTaxCode(String sGSTTaxCode) {
            this.sGSTTaxCode = sGSTTaxCode;
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

        public Integer getStockQty() {
            return stockQty;
        }

        public void setStockQty(Integer stockQty) {
            this.stockQty = stockQty;
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

        public String getSubClassification() {
            return subClassification;
        }

        public void setSubClassification(String subClassification) {
            this.subClassification = subClassification;
        }

        public String getSubstitudeItemId() {
            return substitudeItemId;
        }

        public void setSubstitudeItemId(String substitudeItemId) {
            this.substitudeItemId = substitudeItemId;
        }

        public Integer getTax() {
            return tax;
        }

        public void setTax(Integer tax) {
            this.tax = tax;
        }

        public Double getTaxAmount() {
            return taxAmount;
        }

        public void setTaxAmount(Double taxAmount) {
            this.taxAmount = taxAmount;
        }

        public Double getTotal() {
            return total;
        }

        public void setTotal(Double total) {
            this.total = total;
        }

        public Integer getTotalDiscAmount() {
            return totalDiscAmount;
        }

        public void setTotalDiscAmount(Integer totalDiscAmount) {
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

        public Integer getTotalTax() {
            return totalTax;
        }

        public void setTotalTax(Integer totalTax) {
            this.totalTax = totalTax;
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

    }
    public class TenderLine {

        @SerializedName("AmountCur")
        @Expose
        private Double amountCur;
        @SerializedName("AmountMst")
        @Expose
        private Double amountMst;
        @SerializedName("AmountTendered")
        @Expose
        private Double amountTendered;
        @SerializedName("BarCode")
        @Expose
        private String barCode;
        @SerializedName("ExchRate")
        @Expose
        private Integer exchRate;
        @SerializedName("ExchRateMst")
        @Expose
        private Integer exchRateMst;
        @SerializedName("IsVoid")
        @Expose
        private Boolean isVoid;
        @SerializedName("LineNo")
        @Expose
        private Integer lineNo;
        @SerializedName("MobileNo")
        @Expose
        private String mobileNo;
        @SerializedName("PreviewText")
        @Expose
        private String previewText;
        @SerializedName("RewardsPoint")
        @Expose
        private Integer rewardsPoint;
        @SerializedName("TenderId")
        @Expose
        private String tenderId;
        @SerializedName("TenderName")
        @Expose
        private String tenderName;
        @SerializedName("TenderType")
        @Expose
        private Integer tenderType;
        @SerializedName("WalletOrderId")
        @Expose
        private String walletOrderId;
        @SerializedName("WalletTransactionID")
        @Expose
        private String walletTransactionID;
        @SerializedName("WalletType")
        @Expose
        private Integer walletType;

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

        public Double getAmountTendered() {
            return amountTendered;
        }

        public void setAmountTendered(Double amountTendered) {
            this.amountTendered = amountTendered;
        }

        public String getBarCode() {
            return barCode;
        }

        public void setBarCode(String barCode) {
            this.barCode = barCode;
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

        public Boolean getIsVoid() {
            return isVoid;
        }

        public void setIsVoid(Boolean isVoid) {
            this.isVoid = isVoid;
        }

        public Integer getLineNo() {
            return lineNo;
        }

        public void setLineNo(Integer lineNo) {
            this.lineNo = lineNo;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public String getPreviewText() {
            return previewText;
        }

        public void setPreviewText(String previewText) {
            this.previewText = previewText;
        }

        public Integer getRewardsPoint() {
            return rewardsPoint;
        }

        public void setRewardsPoint(Integer rewardsPoint) {
            this.rewardsPoint = rewardsPoint;
        }

        public String getTenderId() {
            return tenderId;
        }

        public void setTenderId(String tenderId) {
            this.tenderId = tenderId;
        }

        public String getTenderName() {
            return tenderName;
        }

        public void setTenderName(String tenderName) {
            this.tenderName = tenderName;
        }

        public Integer getTenderType() {
            return tenderType;
        }

        public void setTenderType(Integer tenderType) {
            this.tenderType = tenderType;
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

        public Integer getWalletType() {
            return walletType;
        }

        public void setWalletType(Integer walletType) {
            this.walletType = walletType;
        }

    }
}