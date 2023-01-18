package com.apollo.pharmacy.ocr.activities.mposstoresetup.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class StoreSetupModel extends BaseObservable {

    private String macId;
    private String deviceType;
    private String deviceName;
    private String storeDate;
    private double storeLatitude;
    private double storeLongitude;

    public String getMacId() {
        return macId;
    }

    public void setMacId(String macId) {
        this.macId = macId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String imeiNo) {
        this.deviceType = imeiNo;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getStoreDate() {
        return storeDate;
    }

    public void setStoreDate(String storeDate) {
        this.storeDate = storeDate;
    }

    @Bindable
    public double getStoreLatitude() {
        return storeLatitude;
    }

    public void setStoreLatitude(double storeLatitude) {
        this.storeLatitude = storeLatitude;
//        notifyPropertyChanged(BR.storeLatitude);
    }

    @Bindable
    public double getStoreLongitude() {
        return storeLongitude;
    }

    public void setStoreLongitude(double storeLongitude) {
        this.storeLongitude = storeLongitude;
//        notifyPropertyChanged(BR.storeLongitude);
    }
}
