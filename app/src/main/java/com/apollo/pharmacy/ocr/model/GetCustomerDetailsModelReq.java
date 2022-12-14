package com.apollo.pharmacy.ocr.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class GetCustomerDetailsModelReq implements Serializable {

    @SerializedName("SearchString")
    @Expose
    private String searchString;
    @SerializedName("SearchType")
    @Expose
    private Integer searchType;
    @SerializedName("ISAX")
    @Expose
    private Boolean isax;
    @SerializedName("ISOneApollo")
    @Expose
    private Boolean iSOneApollo;
    @SerializedName("Store")
    @Expose
    private String store;
    @SerializedName("Terminal")
    @Expose
    private Object terminal;
    @SerializedName("DataAreaID")
    @Expose
    private String dataAreaID;
    @SerializedName("ClusterCode")
    @Expose
    private String clusterCode;
    @SerializedName("OneApolloSearchUrl")
    @Expose
    private String oneApolloSearchUrl;
    @SerializedName("AXSearchUrl")
    @Expose
    private String aXSearchUrl;
    @SerializedName("CPEnquiry")
    @Expose
    private Boolean cPEnquiry;

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public Integer getSearchType() {
        return searchType;
    }

    public void setSearchType(Integer searchType) {
        this.searchType = searchType;
    }

    public Boolean getIsax() {
        return isax;
    }

    public void setIsax(Boolean isax) {
        this.isax = isax;
    }

    public Boolean getISOneApollo() {
        return iSOneApollo;
    }

    public void setISOneApollo(Boolean iSOneApollo) {
        this.iSOneApollo = iSOneApollo;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public Object getTerminal() {
        return terminal;
    }

    public void setTerminal(Object terminal) {
        this.terminal = terminal;
    }

    public String getDataAreaID() {
        return dataAreaID;
    }

    public void setDataAreaID(String dataAreaID) {
        this.dataAreaID = dataAreaID;
    }

    public String getClusterCode() {
        return clusterCode;
    }

    public void setClusterCode(String clusterCode) {
        this.clusterCode = clusterCode;
    }

    public String getOneApolloSearchUrl() {
        return oneApolloSearchUrl;
    }

    public void setOneApolloSearchUrl(String oneApolloSearchUrl) {
        this.oneApolloSearchUrl = oneApolloSearchUrl;
    }

    public String getAXSearchUrl() {
        return aXSearchUrl;
    }

    public void setAXSearchUrl(String aXSearchUrl) {
        this.aXSearchUrl = aXSearchUrl;
    }

    public Boolean getCPEnquiry() {
        return cPEnquiry;
    }

    public void setCPEnquiry(Boolean cPEnquiry) {
        this.cPEnquiry = cPEnquiry;
    }

}

