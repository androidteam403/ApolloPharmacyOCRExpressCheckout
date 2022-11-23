package com.apollo.pharmacy.ocr.utility;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.StrictMode;

import com.apollo.pharmacy.ocr.receiver.ConnectivityReceiver;
import com.apollo.pharmacy.ocr.receiver.MedTrailConnectivityReceiver;
import com.apollo.pharmacy.ocr.zebrasdk.helper.Barcode;
import com.apollo.pharmacy.ocr.zebrasdk.helper.ScannerAppEngine;
import com.zebra.scannercontrol.DCSScannerInfo;
import com.zebra.scannercontrol.SDKHandler;

import java.util.ArrayList;

public class Constants extends Application {

    private static Context applicationContext;
    private static Constants mInstance;
    public static boolean IS_LOG_ENABLED = true;

    public static final String DATE_FORMAT_DD_MMM_YYYY = "dd-MMM-YYYY";

    public static String MainCategoryHealthWellness = "Health and Wellness";
    public static String Get_Redeption_url = "http://lms.apollopharmacy.org:8085/";
    public static String Pinelabs_URL = "https://www.plutuscloudserviceuat.in:8201/";

    public static String GetDeviceDetails = "http://lms.apollopharmacy.org:8033/MOBILEAPP/Apollo/Common/GetDeviceDetails";
    public static String CheckServiceAvailability = "http://lms.apollopharmacy.org:8033/MOBILEAPP/Apollo/Common/";
    public static String UPLOAD_IMAGE_URL = "http://pharmacydesk.apollopharmacy.org:83/Apollo/Uploadpresc";

    public static String GET_STORE_LIST = "http://lms.apollopharmacy.org:8033/APK/apollompos/Self/STORELIST";


    public static String Add_FCM_Token = "";
    public static String Send_Otp = "";
    public static String Get_Past_Prescription = "";
    public static String Delete_the_Prescription = "";
    public static String Get_Portfolio_of_the_User = "";
    public static String Get_Scanned_Prescription_Image = "";
    public static String Get_Prescription_Medicine_List = "";
    public static String Get_Special_Offers_Products = "";
    public static String Get_Trending_now_Products = "";
    public static String Get_The_price_for_Past_Prescription_Medicine_list = "";
    public static String Search_Suggestions = "";
    public static String Search_Product = "";
    public static String Get_Redeem_Points = "";
    public static String Redeem_points_Send_Otp = "";
    public static String Redeem_Points_Resend_Otp = "";
    public static String Redeem_Points_Validate_Otp = "";
    public static String Redeem_points_Retry_Validate_Otp = "";
    public static String Redeem_Points_Check_Voucher = "";
    public static String Redeem_Voucher = "";
    public static String Get_Product_List = "";
    public static String Get_Store_Locator = "";
    public static String Get_Bit_List_for_Jiyo = "";
    public static String Get_Display_Video_For_Jiyo = "";
    public static String Paytm_Payment_Transaction = "";
    public static String Pinelab_Upload_Transaction = "";
    public static String Pinelab_Get_Cloud_Bases_Transaction = "";
    public static String Pinelab_Cancel_Transaction = "";
    public static String Get_User_Delivery_Address_List = "";
    public static String Edit_User_Delivery_Address = "";
    public static String Add_User_Delivery_Address = "";
    public static String Delete_User_Delivery_address = "";
    public static String Get_State_city_List = "";
    public static String Get_Order_History_For_User = "";
    public static String Order_Pushing_Api = "";
    public static String Get_Image_link = "";
    public static String Categorylist_Url = "";
    public static String Get_Servicability_link = "https://uat.apollopharmacy.in/";
    public static String Send_Sms_Api = "https://online.apollopharmacy.org/";
    public static String Order_Place_With_Prescription_API = "https://online.apollopharmacy.org/TP/OrderPlace.svc/";        //"https://online.apollopharmacy.org/UAT/OrderPlace.svc/";
    public static String Order_Place_With_Prescription_Token = "9f15bdd0fcd5423190c2e877ba02bel";


    public static String New_Order_Place_With_Prescription_Token = "9f15bdd0fcd5423190c2e877ba022ExPc"; //"9f15bdd0fcd5423190c2e877ba0228APM";

    public static final String DIRECTORY_NAME = "USBCamera";

    public static int Baby_Care = 1154;
    public static int Health_Monitoring_Devices = 78;
    public static int First_Aid = 2047;
    public static int Health_Foods_Drinks = 132;
    public static int Beauty_Hygiene = 3;
    public static int OTC = 685;
    public static int General_Wellness = 132;
    public static int Mobility_Aids_Rehabilitation = 83;
    public static int Nutrition_Supplement = 7;

    public static int Anti_allergic_drugs = 831;
    public static int Infections_Infestation = 837;
    public static int CNS_Drugs = 839;
    public static int Generic = 841;
    public static int Diabetics = 846;
    public static int Cardiology = 848;
    public static int Skin_disorders = 850;
    public static int Gastro_entrology = 859;
    public static int Endocrine_disorders = 897;

    public static int Promotions = 234;
    public static int TrendingNow = 97;

    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String SHARED_PREF = "ah_firebase";

//    public static String Add_FCM_Token = "http://10.4.35.5:8085/ApolloPharmacyOCR/rest/";
//    public static String Send_Otp = "http://api-alerts.solutionsinfini.com/";
//    public static String Get_Past_Prescription = "http://10.4.35.5:8085/ApolloPharmacyOCR/rest/";
//    public static String Delete_the_Prescription = "http://10.4.35.5:8085/ApolloPharmacyOCR/rest/";
//    public static String Get_Portfolio_of_the_User = "https://lmsapi.oneapollo.com/api/";
//    public static String Get_Scanned_Prescription_Image = "http://10.4.35.5:8085/ApolloPharmacyOCR/rest/getImage/get/";
//    public static String Get_Prescription_Medicine_List = "http://10.4.35.5:8085/WSS/mapp/items/is/";
//    public static String Get_Special_Offers_Products = "https://www.apollopharmacy.in/";
//    public static String Get_Trending_now_Products = "https://www.apollopharmacy.in/";
//    public static String Get_The_price_for_Past_Prescription_Medicine_list = "https://www.apollopharmacy.in/";
//    public static String Search_Suggestions = "https://www.apollopharmacy.in/";
//    public static String Search_Product = "https://www.apollopharmacy.in/";
//    public static String Get_Redeem_Points = "https://lms.apollopharmacy.org/OA/";
//    public static String Redeem_points_Send_Otp = "https://lms.apollopharmacy.org/OA/";
//    public static String Redeem_Points_Resend_Otp = "https://lms.apollopharmacy.org/OA/";
//    public static String Redeem_Points_Validate_Otp = "https://lms.apollopharmacy.org/OA/";
//    public static String Redeem_points_Retry_Validate_Otp = "https://lms.apollopharmacy.org/OA/";
//    public static String Redeem_Points_Check_Voucher = "https://lms.apollopharmacy.org/OA/";
//    public static String Redeem_Voucher = "https://lms.apollopharmacy.org/OA/";
//    public static String Get_Product_List = "https://www.apollopharmacy.in/";
//    public static String Get_Store_Locator = "http://api.apollopharmacy.in/";
//    public static String Get_Bit_List_for_Jiyo = "https://api.jiyo.com/v3/public/";
//    public static String Get_Display_Video_For_Jiyo = "https://api.jiyo.com/v3/public/";
//    public static String Paytm_Payment_Transaction = "http://172.16.2.251:91/";
//    public static String Pinelab_Upload_Transaction = "https://www.plutuscloudserviceuat.in:8201/";
//    public static String Pinelab_Get_Cloud_Bases_Transaction = "https://www.plutuscloudserviceuat.in:8201/";
//    public static String Pinelab_Cancel_Transaction = "https://www.plutuscloudserviceuat.in:8201/";
//    public static String Get_User_Delivery_Address_List = "http://10.4.35.5:8085/ApolloPharmacyOCR/rest/";
//    public static String Edit_User_Delivery_Address = "http://10.4.35.5:8085/ApolloPharmacyOCR/rest/";
//    public static String Add_User_Delivery_Address = "http://10.4.35.5:8085/ApolloPharmacyOCR/rest/";
//    public static String Delete_User_Delivery_address = "http://10.4.35.5:8085/ApolloPharmacyOCR/rest/";
//    public static String Get_State_city_List = "http://10.4.35.5:8085/ApolloPharmacyOCR/rest/";
//    public static String Get_Order_History_For_User = "http://172.16.2.251:8085/AppIntegrator/";
//    public static String Order_Pushing_Api = "https://online.apollopharmacy.org/UAT/";
//    public static String Get_Image_link = "https://d27zlipt1pllog.cloudfront.net/pub/media";
//    public static String Categorylist_Url = "http://lms.apollopharmacy.org:8033/RPA/Apollo/CATEGORYLIST";


    // Zebra sdk
    //Instance of SDK Handler
    public static SDKHandler sdkHandler;

    //Handler to handle bluetooth events
    public static Handler globalMsgHandler;

    //Var to access scanner app engine
    public static ScannerAppEngine scannerAppEngine;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    //Settings for notifications
    public static int MOT_SETTING_OPMODE;
    public static boolean MOT_SETTING_SCANNER_DETECTION;
    public static boolean MOT_SETTING_EVENT_ACTIVE;
    public static boolean MOT_SETTING_EVENT_AVAILABLE;
    public static boolean MOT_SETTING_EVENT_BARCODE;
    public static boolean MOT_SETTING_EVENT_IMAGE;
    public static boolean MOT_SETTING_EVENT_VIDEO;
    public static boolean MOT_SETTING_EVENT_BINARY_DATA;

    public static boolean MOT_SETTING_NOTIFICATION_ACTIVE;
    public static boolean MOT_SETTING_NOTIFICATION_AVAILABLE;
    public static boolean MOT_SETTING_NOTIFICATION_BARCODE;
    public static boolean MOT_SETTING_NOTIFICATION_IMAGE;
    public static boolean MOT_SETTING_NOTIFICATION_VIDEO;
    public static boolean MOT_SETTING_NOTIFICATION_BINARY_DATA;

    public static volatile int INTENT_ID = 0xFFFF;

    public static int SCANNER_ID_NONE = -1;
    public static String currentScannerName = "";
    public static String currentScannerAddress = "";
    public static int currentScannerId = SCANNER_ID_NONE;
    public static boolean currentAutoReconnectionState = true;
    public static boolean isAnyScannerConnected = false; //True, if currently connected to any scanner
    public static int currentConnectedScannerID = -1; //Track scannerId of currently connected Scanner
    public static boolean isFirmwareUpdateInProgress = false;
    public static boolean intentionallyDisconnected = false;
    public static boolean virtualTetherHostActivated = false;
    public static boolean virtualTetherEventOccurred = false;
    //Scanners (both available and active)
    public static ArrayList<DCSScannerInfo> mScannerInfoList = new ArrayList<DCSScannerInfo>();
    public static ArrayList<ScannerAppEngine.IScannerAppEngineDevListDelegate> mDevListDelegates = new ArrayList<ScannerAppEngine.IScannerAppEngineDevListDelegate>();
    //Barcode data
    public static ArrayList<Barcode> barcodeData = new ArrayList<Barcode>();
    public static DCSScannerInfo currentConnectedScanner;
    public static DCSScannerInfo lastConnectedScanner;


    public static int minScreenWidth = 360;

    public Constants() {

    }


    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        mInstance = this;
        sdkHandler = new SDKHandler(this);
        SessionManager.INSTANCE.initSharedPref(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    public static Context getContext() {
        return applicationContext;
    }

    public static synchronized Constants getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    public void setConnectivityListener1(MedTrailConnectivityReceiver.MedTrailConnectivityListener listener) {
        MedTrailConnectivityReceiver.medtrailconnectivityListener = listener;
    }
}
