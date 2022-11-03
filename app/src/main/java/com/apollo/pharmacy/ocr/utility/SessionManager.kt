package com.apollo.pharmacy.ocr.utility

import android.content.Context
import android.content.SharedPreferences
import com.apollo.pharmacy.ocr.activities.userlogin.model.GetGlobalConfigurationResponse
import com.apollo.pharmacy.ocr.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SessionManager {
    internal lateinit var pref: SharedPreferences
    internal lateinit var prefDefault: SharedPreferences

    internal lateinit var editor: SharedPreferences.Editor
    internal lateinit var editorDefault: SharedPreferences.Editor

    internal lateinit var _context: Context
    internal var PRIVATE_MODE = 0

    fun initSharedPref(context: Context) {
        _context = context
        pref = _context.getSharedPreferences(ApplicationConstant.PREF_NAME_KIOSK, PRIVATE_MODE)
        editor = pref.edit()

        prefDefault = _context.getSharedPreferences(ApplicationConstant.PREF_NAME, PRIVATE_MODE)
        editorDefault = prefDefault.edit()
    }

    fun setIsDeviceSetup(status: Boolean) {
        editorDefault.putBoolean(ApplicationConstant.IS_SETUP_DEVICE, status)
        editorDefault.apply()
    }

    fun getIsDeviceSetup(): Boolean {
        return prefDefault.getBoolean(ApplicationConstant.IS_SETUP_DEVICE, false)
    }

    fun addFcmLog() {
        editorDefault.putBoolean(ApplicationConstant.ISFCM, true)
        editorDefault.apply()
    }

    fun isFcmAdded(): Boolean {
        return prefDefault.getBoolean(ApplicationConstant.ISFCM, false)
    }

    fun setKioskSetupResponse(setupResponse: GetStoreInfoResponse.DeviceDetailsEntity) {
        editorDefault.putString(ApplicationConstant.KIOSK_SETUP_RESPONSE, Gson().toJson(setupResponse))
        editorDefault.apply()
    }

    fun getKioskSetupResponse(): GetStoreInfoResponse.DeviceDetailsEntity {
        val json = prefDefault.getString(ApplicationConstant.KIOSK_SETUP_RESPONSE, null)
        val type = object : TypeToken<GetStoreInfoResponse.DeviceDetailsEntity>() {
        }.type
        if (null != json) {
            return Gson().fromJson<GetStoreInfoResponse.DeviceDetailsEntity>(json, type)
        } else {
            return GetStoreInfoResponse.DeviceDetailsEntity();
        }
    }

    fun settimerlogin(status: Boolean) {
        editor.putBoolean(ApplicationConstant.TIMER_ID, status)
        editor.apply()
    }

    fun gettimerlogin(): Boolean {
        return pref.getBoolean(ApplicationConstant.TIMER_ID, false)
    }

    fun settimerstart_status(status: Boolean) {
        editor.putBoolean(ApplicationConstant.TIMER_STATUS, status)
        editor.apply()
    }

    fun gettimerstart_status(): Boolean {
        return pref.getBoolean(ApplicationConstant.TIMER_STATUS, false)
    }

    fun setSiteid(id: String) {
        editor.putString(ApplicationConstant.SITE_ID, id)
        editor.apply()
    }

    fun getSiteId(): String {
        return pref.getString(ApplicationConstant.SITE_ID, String())
    }

    fun setMobilenumber(number: String) {
        editor.putString(ApplicationConstant.MOBILE_NUMBER, number)
        editor.apply();
    }

    fun setLast3Address(address: String) {
        editor.putString(ApplicationConstant.LAST3ADDRESS, address)
        editor.apply();
    }
    fun getLast3Address(): String {
        return pref.getString(ApplicationConstant.LAST3ADDRESS, String());
    }


    fun getMobilenumber(): String {
        return pref.getString(ApplicationConstant.MOBILE_NUMBER, String());
    }

    fun setStoreId(number: String) {
        editor.putString(ApplicationConstant.STORE_ID, number)
        editor.apply();
    }

    fun getStoreId(): String {
        return pref.getString(ApplicationConstant.STORE_ID, String());
    }

    fun setEposUrl(number: String) {
        editor.putString(ApplicationConstant.EPOS_URL, number)
        editor.apply();
    }

    fun getEposUrl(): String {
        return pref.getString(ApplicationConstant.EPOS_URL, String());
    }


    fun setTerminalId(number: String) {
        editor.putString(ApplicationConstant.TERMINAL_ID, number)
        editor.apply();
    }

    fun getTerminalId(): String {
        return pref.getString(ApplicationConstant.TERMINAL_ID, String());
    }

    fun setCartItems(list: List<OCRToDigitalMedicineResponse>) {
        editor.putString(ApplicationConstant.CartItems, Gson().toJson(list))
        editor.apply()
    }

    fun getCartItems(): List<OCRToDigitalMedicineResponse>? {
        val json = pref.getString(ApplicationConstant.CartItems, null)
        val type = object : TypeToken<List<OCRToDigitalMedicineResponse>>() {
        }.type
        return Gson().fromJson<List<OCRToDigitalMedicineResponse>>(json, type)
    }

    fun clearUserAddressData() {
        editor.remove(ApplicationConstant.USERDATA).apply()
    }

    fun clearMedicineData() {
        editor.remove(ApplicationConstant.DATALIST).apply()
    }

    fun setDataList(list: List<OCRToDigitalMedicineResponse>) {
        editor.putString(ApplicationConstant.DATALIST, Gson().toJson(list))
        editor.apply()
    }

    fun getDataList(): List<OCRToDigitalMedicineResponse>? {
        val json = pref.getString(ApplicationConstant.DATALIST, null)
        val type = object : TypeToken<List<OCRToDigitalMedicineResponse>>() {
        }.type
        return Gson().fromJson<List<OCRToDigitalMedicineResponse>>(json, type)
    }

    fun setDeletedDataList(list: List<OCRToDigitalMedicineResponse>) {
        editor.putString(ApplicationConstant.DELETEDDATALIST, Gson().toJson(list))
        editor.apply()
    }

    fun getDeletedDataList(): List<OCRToDigitalMedicineResponse>? {
        val json = pref.getString(ApplicationConstant.DELETEDDATALIST, null)
        val type = object : TypeToken<List<OCRToDigitalMedicineResponse>>() {

        }.type
        return Gson().fromJson<List<OCRToDigitalMedicineResponse>>(json, type)
    }

    fun clearDeletedMedicineData() {
        editor.remove(ApplicationConstant.DELETEDDATALIST).apply()
    }

    fun setOfferList(list: ArrayList<Product>) {
        editor.putString(ApplicationConstant.OFFERLIST, Gson().toJson(list))
        editor.apply()
    }

    fun getOfferList(): ArrayList<Product>? {
        val json = pref.getString(ApplicationConstant.OFFERLIST, null)
        val type = object : TypeToken<ArrayList<Product>>() {
        }.type
        return Gson().fromJson<ArrayList<Product>>(json, type)
    }

    fun setTrendingList(list: ArrayList<Product>) {
        editor.putString(ApplicationConstant.TRENDINGLIST, Gson().toJson(list))
        editor.apply()
    }

    fun getTrendingList(): ArrayList<Product>? {
        val json = pref.getString(ApplicationConstant.TRENDINGLIST, null)
        val type = object : TypeToken<ArrayList<Product>>() {
        }.type
        return Gson().fromJson<ArrayList<Product>>(json, type)
    }

    fun setUseraddress(address: UserAddress) {
        editor.putString(ApplicationConstant.USERDATA, Gson().toJson(address))
        editor.apply()
    }

    fun getUseraddress(): UserAddress {
        val json = pref.getString(ApplicationConstant.USERDATA, null)
        val type = object : TypeToken<UserAddress>() {
        }.type
        if (null != json) {
            return Gson().fromJson<UserAddress>(json, type)
        } else {
            return UserAddress();
        }
    }

    fun setDeliverytype(deliverytype: String) {
        editor.putString(ApplicationConstant.DELIVERYTYPE, deliverytype)
        editor.apply()
    }

    fun getDeliverytype(): String {
        return pref.getString(ApplicationConstant.DELIVERYTYPE, "");
    }

    fun clearDeliveryType() {
        editor.remove(ApplicationConstant.DELIVERYTYPE).apply()
    }

    fun setUserName(name: String) {
        editor.putString(ApplicationConstant.USERNAME, name)
        editor.apply()
    }

    fun getUserName(): String {
        return pref.getString(ApplicationConstant.USERNAME, "");
    }

    fun setDeliverydate(date: String) {
        editor.putString(ApplicationConstant.DELIVERYDATE, date)
        editor.apply()
    }

    fun getDeliverydate(): String {
        return pref.getString(ApplicationConstant.DELIVERYDATE, "");
    }

    fun setPinelabtransaction_status() {
        editor.putBoolean(ApplicationConstant.pinelab_transaction_status, true)
        editor.apply()
    }

    fun getPinelabtransaction_status(): Boolean {
        return pref.getBoolean(ApplicationConstant.pinelab_transaction_status, false)
    }

    fun getTransaction_referenceid(): Int {
        return pref.getInt(ApplicationConstant.TRANSACTION_REFERENCEID, 0)
    }

    fun setTransaction_referenceid(id: Int) {
        editorDefault.putInt(ApplicationConstant.TRANSACTION_REFERENCEID, id)
        editorDefault.apply()
    }

    fun setProductListSaved(key: String) {
        editor.putBoolean(key + 1, true)
        editor.apply()
    }

    fun getProductListSaved(key: String): Boolean? {
        return pref.getBoolean(key + 1, false)
    }

    fun setCategoryProductList(list: List<Product>, key: String) {
        editor.putString(key, Gson().toJson(list))
        editor.apply()
    }

    fun logoutUser() {
        editor.clear()
        editor.apply()
    }

    fun setImageUploadId(id: String) {
        editor.putString(ApplicationConstant.IMAGE_UPLOAD_ID, id)
        editor.apply()
    }

    fun getImageUploadId(): String {
        return pref.getString(ApplicationConstant.IMAGE_UPLOAD_ID, "")
    }

    fun setCurrentPage(page: String) {
        editor.putString(ApplicationConstant.CURRENT_PAGE, page)
        editor.apply()
    }

    fun getCurrentPage(): String {
        return pref.getString(ApplicationConstant.CURRENT_PAGE, "")
    }

    fun setOrderCompletionStatus(status: Boolean) {
        editor.putBoolean(ApplicationConstant.ORDER_STATUS, status)
        editor.apply()
    }

    fun getOrderCompletionStatus(): Boolean {
        return pref.getBoolean(ApplicationConstant.ORDER_STATUS, false)
    }

    fun setCurationStatus(status: Boolean) {
        editor.putBoolean(ApplicationConstant.CURATION_STATUS, status)
        editor.apply()
    }

    fun getCurationStatus(): Boolean {
        return pref.getBoolean(ApplicationConstant.CURATION_STATUS, false)
    }

    fun setScannedImagePath(image: String) {
        editor.putString(ApplicationConstant.SCANNED_IMAGE_PATH, image)
        editor.apply()
    }

    fun getScannedImagePath(): String {
        return pref.getString(ApplicationConstant.SCANNED_IMAGE_PATH, "")
    }

    fun setPrescriptionImageUrl(image: String) {
        editor.putString(ApplicationConstant.PRESCRIPTION_IMAGE_PATH, image)
        editor.apply()
    }

    fun getPrescriptionImageUrl(): String {
        return pref.getString(ApplicationConstant.PRESCRIPTION_IMAGE_PATH, "")
    }

    fun setScannedPrescriptionItems(list: List<ScannedMedicine>) {
        editor.putString(ApplicationConstant.SCANNEDITEMS, Gson().toJson(list))
        editor.apply()
    }

    fun getScannedPrescriptionItems(): List<ScannedMedicine>? {
        val json = pref.getString(ApplicationConstant.SCANNEDITEMS, null)
        val type = object : TypeToken<List<ScannedMedicine>>() {

        }.type
        return Gson().fromJson<List<ScannedMedicine>>(json, type)
    }

    fun setUploadPrescriptionDeliveryMode(status: Boolean) {
        editor.putBoolean(ApplicationConstant.DELIVERY_MODE_UPLOAD_PRESCRIPTION, status)
        editor.apply()
    }

    fun getUploadPrescriptionDeliveryMode(): Boolean {
        return pref.getBoolean(ApplicationConstant.DELIVERY_MODE_UPLOAD_PRESCRIPTION, false)
    }

    fun setLoggedUserName(name: String) {
        editor.putString(ApplicationConstant.USER_NAME, name)
        editor.apply()
    }

    fun getLoggedUserName(): String {
        return pref.getString(ApplicationConstant.USER_NAME, "")
    }

    fun setFcmMedicineReceived(status: Boolean) {
        editor.putBoolean(ApplicationConstant.FCM_MEDICINE_RECEIVED, status)
        editor.apply()
    }

    fun getFcmMedicineReceived(): Boolean {
        return pref.getBoolean(ApplicationConstant.FCM_MEDICINE_RECEIVED, false)
    }

    fun setDynamicOrderId(orderId: String) {
        editor.putString(ApplicationConstant.DYNAMIC_ORDER_ID, orderId)
        editor.apply()
    }

    fun getDynamicOrderId(): String {
        return pref.getString(ApplicationConstant.DYNAMIC_ORDER_ID, "")
    }

    fun setAccessKey(accessKey: String) {
        editor.putString(ApplicationConstant.ACCESS_KEY, accessKey)
        editor.apply()
    }

    fun getAccessKey(): String {
        return pref.getString(ApplicationConstant.ACCESS_KEY, "")
    }

    fun setAccessDialogHandler(accessKey: String) {
        editor.putString(ApplicationConstant.ACESS_DIALOG_HANDEL, accessKey)
        editor.apply()
    }

    fun getAccessDialogHandler(): String {
        return pref.getString(ApplicationConstant.ACESS_DIALOG_HANDEL, "")
    }

    fun setImagePath(imagePathList: List<String>) {
        editor.putString(ApplicationConstant.IMAGEPATHLIST, Gson().toJson(imagePathList))
        editor.apply()
    }

    var lists: List<String> = emptyList()
    fun getImagePathList(): List<String> {
        val json = pref.getString(ApplicationConstant.IMAGEPATHLIST, null)
        val type = object : TypeToken<List<String>>() {

        }.type
        if (Gson().fromJson<List<String>>(json, type) != null)
            return Gson().fromJson<List<String>>(json, type)
        else
            return lists
    }

    fun setBatchId(batchId: String) {
        editor.putString(ApplicationConstant.BATCH_ID, batchId)
        editor.apply();
    }

    fun getBatchId(): String {
        return pref.getString(ApplicationConstant.BATCH_ID, String());
    }

    fun setCustName(name: String) {
        editor.putString(ApplicationConstant.CUST_NAME, name)
        editor.apply()
    }

    fun getCustrName(): String {
        return pref.getString(ApplicationConstant.CUST_NAME, "");
    }

    fun setSessionTime(sessionTime: Int) {
        editor.putInt(ApplicationConstant.SESSION_TIME, sessionTime)
        editor.apply()
    }

    fun getSessionTime(): Int {
        return pref.getInt(ApplicationConstant.SESSION_TIME, 5);
    }

    fun setConfigStore(configStore: String) {
        editor.putString(ApplicationConstant.CONFIG_STORE, configStore)
        editor.apply()
    }

    fun getConfigStore(): String {
        return pref.getString(ApplicationConstant.CONFIG_STORE, "")
    }

    fun setDataAreaId(companyName: String) {
        editor.putString(ApplicationConstant.COMPANY_NAME, companyName)
        editor.apply()
    }

    fun getDataAreaId(): String {
        return pref.getString(ApplicationConstant.COMPANY_NAME, "")
    }

    fun setGlobalConfigurationResponse(getGlobalConfigurationResponse: String) {
        editor.putString(ApplicationConstant.GET_GLOBAL_CONFIGURATION_RESPONSE, getGlobalConfigurationResponse)
        editor.apply()
    }

    fun getGlobalConfigurationResponse(): GetGlobalConfigurationResponse {
        val gson = Gson()
        val json: String = pref.getString(ApplicationConstant.GET_GLOBAL_CONFIGURATION_RESPONSE, "")
        return gson.fromJson(json, GetGlobalConfigurationResponse::class.java)
    }

}