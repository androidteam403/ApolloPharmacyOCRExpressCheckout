package com.apollo.pharmacy.ocr.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PdfModelResponse implements Serializable {

    @SerializedName("BillingType")
    @Expose
    private Integer billingType;
    @SerializedName("Bold")
    @Expose
    private Object bold;
    @SerializedName("Condense")
    @Expose
    private Object condense;
    @SerializedName("DataAreaID")
    @Expose
    private String dataAreaID;
    @SerializedName("DigitalReceiptRequired")
    @Expose
    private Boolean digitalReceiptRequired;
    @SerializedName("FeedLine")
    @Expose
    private Object feedLine;
    @SerializedName("INIFilePath")
    @Expose
    private Object iNIFilePath;
    @SerializedName("Initialize")
    @Expose
    private Object initialize;
    @SerializedName("PageBk")
    @Expose
    private Object pageBk;
    @SerializedName("PrintPort")
    @Expose
    private Object printPort;
    @SerializedName("PrintType")
    @Expose
    private Object printType;
    @SerializedName("PrinterCode")
    @Expose
    private Object printerCode;
    @SerializedName("PrinterName")
    @Expose
    private Object printerName;
    @SerializedName("PrintingBackupFilePath")
    @Expose
    private Object printingBackupFilePath;
    @SerializedName("PrintingFilePath")
    @Expose
    private Object printingFilePath;
    @SerializedName("RequestStatus")
    @Expose
    private Integer requestStatus;
    @SerializedName("ReturnMessage")
    @Expose
    private String returnMessage;
    @SerializedName("Reverseline")
    @Expose
    private Object reverseline;
    @SerializedName("SalesHeader")
    @Expose
    private List<SalesHeader> salesHeader = null;
    @SerializedName("SalesLine")
    @Expose
    private List<SalesLine> salesLine = null;
    @SerializedName("StoreCode")
    @Expose
    private String storeCode;
    @SerializedName("TemplateName")
    @Expose
    private Object templateName;
    @SerializedName("TerminalID")
    @Expose
    private String terminalID;
    @SerializedName("TotalFormat")
    @Expose
    private Object totalFormat;
    @SerializedName("TransactionId")
    @Expose
    private String transactionId;
    @SerializedName("UnBold")
    @Expose
    private Object unBold;
    private final static long serialVersionUID = 5725932227578548270L;

    public Integer getBillingType() {
        return billingType;
    }

    public void setBillingType(Integer billingType) {
        this.billingType = billingType;
    }

    public Object getBold() {
        return bold;
    }

    public void setBold(Object bold) {
        this.bold = bold;
    }

    public Object getCondense() {
        return condense;
    }

    public void setCondense(Object condense) {
        this.condense = condense;
    }

    public String getDataAreaID() {
        return dataAreaID;
    }

    public void setDataAreaID(String dataAreaID) {
        this.dataAreaID = dataAreaID;
    }

    public Boolean getDigitalReceiptRequired() {
        return digitalReceiptRequired;
    }

    public void setDigitalReceiptRequired(Boolean digitalReceiptRequired) {
        this.digitalReceiptRequired = digitalReceiptRequired;
    }

    public Object getFeedLine() {
        return feedLine;
    }

    public void setFeedLine(Object feedLine) {
        this.feedLine = feedLine;
    }

    public Object getINIFilePath() {
        return iNIFilePath;
    }

    public void setINIFilePath(Object iNIFilePath) {
        this.iNIFilePath = iNIFilePath;
    }

    public Object getInitialize() {
        return initialize;
    }

    public void setInitialize(Object initialize) {
        this.initialize = initialize;
    }

    public Object getPageBk() {
        return pageBk;
    }

    public void setPageBk(Object pageBk) {
        this.pageBk = pageBk;
    }

    public Object getPrintPort() {
        return printPort;
    }

    public void setPrintPort(Object printPort) {
        this.printPort = printPort;
    }

    public Object getPrintType() {
        return printType;
    }

    public void setPrintType(Object printType) {
        this.printType = printType;
    }

    public Object getPrinterCode() {
        return printerCode;
    }

    public void setPrinterCode(Object printerCode) {
        this.printerCode = printerCode;
    }

    public Object getPrinterName() {
        return printerName;
    }

    public void setPrinterName(Object printerName) {
        this.printerName = printerName;
    }

    public Object getPrintingBackupFilePath() {
        return printingBackupFilePath;
    }

    public void setPrintingBackupFilePath(Object printingBackupFilePath) {
        this.printingBackupFilePath = printingBackupFilePath;
    }

    public Object getPrintingFilePath() {
        return printingFilePath;
    }

    public void setPrintingFilePath(Object printingFilePath) {
        this.printingFilePath = printingFilePath;
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

    public Object getReverseline() {
        return reverseline;
    }

    public void setReverseline(Object reverseline) {
        this.reverseline = reverseline;
    }

    public List<SalesHeader> getSalesHeader() {
        return salesHeader;
    }

    public void setSalesHeader(List<SalesHeader> salesHeader) {
        this.salesHeader = salesHeader;
    }

    public class SalesHeader implements Serializable {

        @SerializedName("Address")
        @Expose
        private String address;
        @SerializedName("Address1")
        @Expose
        private String addressOne;
        @SerializedName("address2")
        @Expose
        private String addressTwo;
        @SerializedName("AmountDescription")
        @Expose
        private String amountDescription;
        @SerializedName("BankAccountNo")
        @Expose
        private String bankAccountNo;
        @SerializedName("Branch")
        @Expose
        private String branch;
        @SerializedName("CGSTIN")
        @Expose
        private String cgstin;
        @SerializedName("CancelRemark")
        @Expose
        private String cancelRemark;
        @SerializedName("Corporate")
        @Expose
        private String corporate;
        @SerializedName("CustMobile")
        @Expose
        private String custMobile;
        @SerializedName("CustName")
        @Expose
        private String custName;
        @SerializedName("DLNO")
        @Expose
        private String dlno;
        @SerializedName("Discount")
        @Expose
        private String discount;
        @SerializedName("DoctorName")
        @Expose
        private String doctorName;
        @SerializedName("DonationAmount")
        @Expose
        private String donationAmount;
        @SerializedName("FSSAINO")
        @Expose
        private String fssaino;
        @SerializedName("GSTDescription")
        @Expose
        private String gSTDescription;
        @SerializedName("GSTIN")
        @Expose
        private String gstin;
        @SerializedName("IFSCCode")
        @Expose
        private String iFSCCode;
        @SerializedName("InvoiceCancelled")
        @Expose
        private String invoiceCancelled;
        @SerializedName("NetTotal")
        @Expose
        private String netTotal;
        @SerializedName("ReceiptId")
        @Expose
        private String receiptId;
        @SerializedName("RefNo")
        @Expose
        private String refNo;
        @SerializedName("Store")
        @Expose
        private String store;
        @SerializedName("TelNo")
        @Expose
        private String telNo;
        @SerializedName("TerminalId")
        @Expose
        private String terminalId;
        @SerializedName("TokenNumber")
        @Expose
        private String tokenNumber;
        @SerializedName("Total")
        @Expose
        private String total;
        @SerializedName("TransDate")
        @Expose
        private String transDate;
        @SerializedName("TransactionId")
        @Expose
        private String transactionId;
        @SerializedName("UserID")
        @Expose
        private String userID;
        @SerializedName("WalletTrxID")
        @Expose
        private String walletTrxID;
        private final static long serialVersionUID = 4956378778518762950L;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAddressOne() {
            return addressOne;
        }

        public void setAddressOne(String addressOne) {
            this.addressOne = addressOne;
        }

        public String getAddressTwo() {
            return addressTwo;
        }

        public void setAddressTwo(String addressTwo) {
            this.addressTwo = addressTwo;
        }

        public String getAmountDescription() {
            return amountDescription;
        }

        public void setAmountDescription(String amountDescription) {
            this.amountDescription = amountDescription;
        }

        public String getBankAccountNo() {
            return bankAccountNo;
        }

        public void setBankAccountNo(String bankAccountNo) {
            this.bankAccountNo = bankAccountNo;
        }

        public String getBranch() {
            return branch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }

        public String getCgstin() {
            return cgstin;
        }

        public void setCgstin(String cgstin) {
            this.cgstin = cgstin;
        }

        public String getCancelRemark() {
            return cancelRemark;
        }

        public void setCancelRemark(String cancelRemark) {
            this.cancelRemark = cancelRemark;
        }

        public String getCorporate() {
            return corporate;
        }

        public void setCorporate(String corporate) {
            this.corporate = corporate;
        }

        public String getCustMobile() {
            return custMobile;
        }

        public void setCustMobile(String custMobile) {
            this.custMobile = custMobile;
        }

        public String getCustName() {
            return custName;
        }

        public void setCustName(String custName) {
            this.custName = custName;
        }

        public String getDlno() {
            return dlno;
        }

        public void setDlno(String dlno) {
            this.dlno = dlno;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public String getDoctorName() {
            return doctorName;
        }

        public void setDoctorName(String doctorName) {
            this.doctorName = doctorName;
        }

        public String getDonationAmount() {
            return donationAmount;
        }

        public void setDonationAmount(String donationAmount) {
            this.donationAmount = donationAmount;
        }

        public String getFssaino() {
            return fssaino;
        }

        public void setFssaino(String fssaino) {
            this.fssaino = fssaino;
        }

        public String getGSTDescription() {
            return gSTDescription;
        }

        public void setGSTDescription(String gSTDescription) {
            this.gSTDescription = gSTDescription;
        }

        public String getGstin() {
            return gstin;
        }

        public void setGstin(String gstin) {
            this.gstin = gstin;
        }

        public String getIFSCCode() {
            return iFSCCode;
        }

        public void setIFSCCode(String iFSCCode) {
            this.iFSCCode = iFSCCode;
        }

        public String getInvoiceCancelled() {
            return invoiceCancelled;
        }

        public void setInvoiceCancelled(String invoiceCancelled) {
            this.invoiceCancelled = invoiceCancelled;
        }

        public String getNetTotal() {
            return netTotal;
        }

        public void setNetTotal(String netTotal) {
            this.netTotal = netTotal;
        }

        public String getReceiptId() {
            return receiptId;
        }

        public void setReceiptId(String receiptId) {
            this.receiptId = receiptId;
        }

        public String getRefNo() {
            return refNo;
        }

        public void setRefNo(String refNo) {
            this.refNo = refNo;
        }

        public String getStore() {
            return store;
        }

        public void setStore(String store) {
            this.store = store;
        }

        public String getTelNo() {
            return telNo;
        }

        public void setTelNo(String telNo) {
            this.telNo = telNo;
        }

        public String getTerminalId() {
            return terminalId;
        }

        public void setTerminalId(String terminalId) {
            this.terminalId = terminalId;
        }

        public String getTokenNumber() {
            return tokenNumber;
        }

        public void setTokenNumber(String tokenNumber) {
            this.tokenNumber = tokenNumber;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getTransDate() {
            return transDate;
        }

        public void setTransDate(String transDate) {
            this.transDate = transDate;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }

        public String getWalletTrxID() {
            return walletTrxID;
        }

        public void setWalletTrxID(String walletTrxID) {
            this.walletTrxID = walletTrxID;
        }

    }

    public List<SalesLine> getSalesLine() {
        return salesLine;
    }

    public void setSalesLine(List<SalesLine> salesLine) {
        this.salesLine = salesLine;
    }

    public class SalesLine implements Serializable {

        @SerializedName("BatchNo")
        @Expose
        private String batchNo;
        @SerializedName("CGSTPer")
        @Expose
        private String cGSTPer;
        @SerializedName("ExpDate")
        @Expose
        private String expDate;
        @SerializedName("HSNCode")
        @Expose
        private String hSNCode;
        @SerializedName("IsCircleCharge")
        @Expose
        private Integer isCircleCharge;
        @SerializedName("IsShippingCharge")
        @Expose
        private Integer isShippingCharge;
        @SerializedName("ItemName")
        @Expose
        private String itemName;
        @SerializedName("LineTotAmount")
        @Expose
        private String lineTotAmount;
        @SerializedName("MRP")
        @Expose
        private String mrp;
        @SerializedName("Manufacturer")
        @Expose
        private String manufacturer;
        @SerializedName("QTY")
        @Expose
        private String qty;
        @SerializedName("RackId")
        @Expose
        private String rackId;
        @SerializedName("ReceiptId")
        @Expose
        private String receiptId;
        @SerializedName("SCH")
        @Expose
        private String sch;
        @SerializedName("SGSTPer")
        @Expose
        private String sGSTPer;
        @SerializedName("Taxable")
        @Expose
        private String taxable;
        private final static long serialVersionUID = -221916317312271771L;

        public String getBatchNo() {
            return batchNo;
        }

        public void setBatchNo(String batchNo) {
            this.batchNo = batchNo;
        }

        public String getCGSTPer() {
            return cGSTPer;
        }

        public void setCGSTPer(String cGSTPer) {
            this.cGSTPer = cGSTPer;
        }

        public String getExpDate() {
            return expDate;
        }

        public void setExpDate(String expDate) {
            this.expDate = expDate;
        }

        public String getHSNCode() {
            return hSNCode;
        }

        public void setHSNCode(String hSNCode) {
            this.hSNCode = hSNCode;
        }

        public Integer getIsCircleCharge() {
            return isCircleCharge;
        }

        public void setIsCircleCharge(Integer isCircleCharge) {
            this.isCircleCharge = isCircleCharge;
        }

        public Integer getIsShippingCharge() {
            return isShippingCharge;
        }

        public void setIsShippingCharge(Integer isShippingCharge) {
            this.isShippingCharge = isShippingCharge;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getLineTotAmount() {
            return lineTotAmount;
        }

        public void setLineTotAmount(String lineTotAmount) {
            this.lineTotAmount = lineTotAmount;
        }

        public String getMrp() {
            return mrp;
        }

        public void setMrp(String mrp) {
            this.mrp = mrp;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }

        public String getRackId() {
            return rackId;
        }

        public void setRackId(String rackId) {
            this.rackId = rackId;
        }

        public String getReceiptId() {
            return receiptId;
        }

        public void setReceiptId(String receiptId) {
            this.receiptId = receiptId;
        }

        public String getSch() {
            return sch;
        }

        public void setSch(String sch) {
            this.sch = sch;
        }

        public String getSGSTPer() {
            return sGSTPer;
        }

        public void setSGSTPer(String sGSTPer) {
            this.sGSTPer = sGSTPer;
        }

        public String getTaxable() {
            return taxable;
        }

        public void setTaxable(String taxable) {
            this.taxable = taxable;
        }

    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public Object getTemplateName() {
        return templateName;
    }

    public void setTemplateName(Object templateName) {
        this.templateName = templateName;
    }

    public String getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }

    public Object getTotalFormat() {
        return totalFormat;
    }

    public void setTotalFormat(Object totalFormat) {
        this.totalFormat = totalFormat;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Object getUnBold() {
        return unBold;
    }

    public void setUnBold(Object unBold) {
        this.unBold = unBold;
    }

}


