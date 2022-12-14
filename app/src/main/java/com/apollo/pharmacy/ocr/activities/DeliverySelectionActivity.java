package com.apollo.pharmacy.ocr.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.controller.DeliverySelectionController;
import com.apollo.pharmacy.ocr.interfaces.DeliverySelectionListener;
import com.apollo.pharmacy.ocr.model.Inventory;
import com.apollo.pharmacy.ocr.model.InventoryBasedonSiteid;
import com.apollo.pharmacy.ocr.model.PharmacyLocatorCustomList;
import com.apollo.pharmacy.ocr.model.StoreLocatorResponse;
import com.apollo.pharmacy.ocr.model.UserAddress;
import com.apollo.pharmacy.ocr.model.pharmacylocatorFilterlist;
import com.apollo.pharmacy.ocr.receiver.ConnectivityReceiver;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.utility.NetworkUtils;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DeliverySelectionActivity extends BaseActivity implements DeliverySelectionListener,
        ConnectivityReceiver.ConnectivityReceiverListener {

    private DeliverySelectionController controller;
    private String stringLatitude, stringLongitude;
    private ArrayList<PharmacyLocatorCustomList> contacts;
    private ArrayList<Integer> quantityArrayList;
    private ArrayList<String> availabilityArrayList;
    private boolean isHomeDeliveryChecked = false;
    private boolean isNearestApolloChecked = false;
    private boolean isAtKioskChecked = false;
    private ArrayList<UserAddress> addressList;
    private ConstraintLayout constraint_layout;
    private LinearLayout firstHomeLayout, thirdHomeLayout, firstApolloLayout, thirdApolloLayout, firstKioskLayout, thirdKioskLayout, proceedLayout;
    private TextView homeButton, apolloButton, kioskButton, homeTxt, estimatedHomeTxt, apolloTxt, estimatedApolloTxt, kioskTxt, estimatedKioskTxt;

    public DeliverySelectionActivity() {
        super();
    }

    public void onFailure(String error) {
        Utils.showCustomAlertDialog(DeliverySelectionActivity.this, getResources().getString(R.string.label_server_err_message), false, "OK", "");
    }

    public void onSuccessGetAddressDetails(ArrayList<UserAddress> response) {
        Utils.dismissDialog();
        if (response != null && response.size() > 0) {
            addressList.clear();
            addressList = response;
            if (addressList.size() == 0 || addressList == null) {
                Intent intent = new Intent(DeliverySelectionActivity.this, AddressAddActivity.class);
                intent.putExtra("addressList", addressList.size());
                startActivity(intent);
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            } else {
                Intent intent = new Intent(DeliverySelectionActivity.this, AddressListActivity.class);
                intent.putExtra("addressList", addressList);
                startActivity(intent);
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            }
        } else {
            Intent intent = new Intent(DeliverySelectionActivity.this, AddressAddActivity.class);
            intent.putExtra("addressList", 0);
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        }
    }

    @Override
    public void onSuccessGetStoreLocators(List<StoreLocatorResponse> response) {
        if (response != null && response.size() > 0) {
            contacts = new ArrayList<>();
            ArrayList<pharmacylocatorFilterlist> near_locations_array = new ArrayList<>();
            ArrayList<Double> distance_array1 = new ArrayList<>();
            ArrayList<Double> temp_distance_array1 = new ArrayList<>();
            for (StoreLocatorResponse data : response) {
                pharmacylocatorFilterlist load_filterist = new pharmacylocatorFilterlist();
                String cityName = "HYDERABAD";
                String compare_str = data.getPcity();
                if (compare_str.equals(cityName)) {
                    load_filterist.setStoreid(data.getStoreid());
                    load_filterist.setStorename(data.getStorename());
                    load_filterist.setStoretype(data.getBranchtype());
                    load_filterist.setStoreaddress(data.getBaddress());
                    load_filterist.setStorenumber(data.getPhno());
                    load_filterist.setStorelatitude(data.getPlat());
                    load_filterist.setStorelongtude(data.getPlon());
                    load_filterist.setStorecity(data.getPcity());
                    load_filterist.setStorestate(data.getStaten());
                    load_filterist.setStorepincode(data.getPincode());
                    String storelatitude2 = data.getPlat();
                    String storelongtude2 = data.getPlon();
                    if (storelatitude2.length() == 0) {
                        storelatitude2 = "0.0";
                    }
                    if (storelongtude2.length() == 0) {
                        storelongtude2 = "0.0";
                    }
                    storelatitude2 = storelatitude2.replaceAll("\\s+", "");
                    storelongtude2 = storelongtude2.replaceAll("\\s+", "");
                    double theta = Double.parseDouble(stringLongitude) - Double.parseDouble(storelongtude2);
                    double dist = Math.sin(deg2rad(Double.parseDouble(stringLatitude)))
                            * Math.sin(deg2rad(Double.parseDouble(storelatitude2)))
                            + Math.cos(deg2rad(Double.parseDouble(stringLatitude)))
                            * Math.cos(deg2rad(Double.parseDouble(storelatitude2)))
                            * Math.cos(deg2rad(theta));
                    dist = Math.acos(dist);
                    dist = rad2deg(dist);
                    dist = dist * 60 * 1.1515;
                    load_filterist.setStoredistance(String.valueOf(dist));
                    near_locations_array.add(load_filterist);
                    distance_array1.add(dist);
                    temp_distance_array1.add(dist);
                }
            }
            Collections.sort(distance_array1);
            for (int i = 0; i < distance_array1.size(); i++) {
                PharmacyLocatorCustomList load_obj = new PharmacyLocatorCustomList();
                pharmacylocatorFilterlist load_filterist;
                Double fetch_value = distance_array1.get(i);
                int index = temp_distance_array1.indexOf(fetch_value);
                load_filterist = near_locations_array.get(index);
                load_obj.setStoreid(load_filterist.getStoreid());
                load_obj.setStorename(load_filterist.getStorename());
                load_obj.setStoretype(load_filterist.getStoretype());
                load_obj.setStoreaddress(load_filterist.getStoreaddress());
                load_obj.setStorenumber(load_filterist.getStorenumber());
                load_obj.setStorelatitude(load_filterist.getStorelatitude());
                load_obj.setStorelongtude(load_filterist.getStorelongtude());
                load_obj.setStorecity(load_filterist.getStorecity());
                load_obj.setStorestate(load_filterist.getStorestate());
                load_obj.setStorepincode(load_filterist.getStorepincode());
                String storedistance2 = load_filterist.getStoredistance();
                storedistance2 = String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(storedistance2));
                storedistance2 = storedistance2 + " km";
                load_obj.setStoredistance(storedistance2);
                contacts.add(load_obj);
            }
        }
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            findViewById(R.id.networkErrorLayout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.networkErrorLayout).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DeliverySelectionActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        checkSelectedStatus();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        Constants.getInstance().setConnectivityListener(this);
        if (!ConnectivityReceiver.isConnected()) {
            findViewById(R.id.networkErrorLayout).setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_selection);

        addressList = new ArrayList<>();
        String itemCount = "";
        String grandTotalPrice = "";
        Intent intent = getIntent();
        if (intent != null) {
            itemCount = intent.getStringExtra("items_count");
            grandTotalPrice = intent.getStringExtra("grand_total_price");
        }
        ImageView imageView = findViewById(R.id.customer_care_icon);
        LinearLayout linearText = findViewById(R.id.customer_help_layout);
        constraint_layout = findViewById(R.id.constraint_layout);
        imageView.setOnClickListener(v -> {
            if (linearText.getVisibility() == View.VISIBLE) {
                imageView.setBackgroundResource(R.drawable.icon_help_circle);
                linearText.setVisibility(View.GONE);
            } else {
                imageView.setBackgroundResource(R.drawable.icon_customer_care);
                linearText.setVisibility(View.VISIBLE);
            }
        });
        LinearLayout faqLayout = findViewById(R.id.help_layout);
        TextView helpText = findViewById(R.id.help_text);
        helpText.setText(getResources().getString(R.string.faq));
        faqLayout.setOnClickListener(view -> startActivity(new Intent(DeliverySelectionActivity.this, FAQActivity.class)));
        TextView cartCount = findViewById(R.id.cart_count_txt);
        TextView cartAmout = findViewById(R.id.amount_txt);
        cartCount.setText(itemCount + " " + getApplicationContext().getResources().getString(R.string.label_items_in_cart));
        cartAmout.setText(grandTotalPrice);
        FrameLayout yourHomeLayout = findViewById(R.id.your_home_layout);
        FrameLayout nearApolloLayout = findViewById(R.id.near_apollo_layout);
        FrameLayout atKioskLayout = findViewById(R.id.at_kiosk_layout);

        firstHomeLayout = findViewById(R.id.first_home_layout);
        thirdHomeLayout = findViewById(R.id.third_home_layout);
        homeButton = findViewById(R.id.home_button);
        firstApolloLayout = findViewById(R.id.first_apollo_layout);
        thirdApolloLayout = findViewById(R.id.third_apollo_layout);
        apolloButton = findViewById(R.id.apollo_store_button);
        firstKioskLayout = findViewById(R.id.first_kiosk_layout);
        thirdKioskLayout = findViewById(R.id.third_kiosk_layout);
        kioskButton = findViewById(R.id.kiosk_button);

        homeTxt = findViewById(R.id.home_txt);
        estimatedHomeTxt = findViewById(R.id.estimated_home_txt);
        apolloTxt = findViewById(R.id.apollo_txt);
        estimatedApolloTxt = findViewById(R.id.estimate_apollo_txt);
        kioskTxt = findViewById(R.id.kiosk_txt);
        estimatedKioskTxt = findViewById(R.id.estimated_kiosk_txt);
        proceedLayout = findViewById(R.id.proceed_layout);

        findViewById(R.id.back_icon).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });

        homeButton.setText(SessionManager.INSTANCE.getKioskSetupResponse().getAT_HOME_DELIVERABLE_TIME());
        kioskButton.setText(SessionManager.INSTANCE.getKioskSetupResponse().getAT_KiOSK_DELIVERABLE_TIME());

        stringLongitude = Double.toString(78.409420);
        stringLatitude = Double.toString(17.416500);
        contacts = new ArrayList<>();
        availabilityArrayList = new ArrayList<>();
        quantityArrayList = new ArrayList<>();
        controller = new DeliverySelectionController(this);

        if (SessionManager.INSTANCE.getUploadPrescriptionDeliveryMode()) {
            SessionManager.INSTANCE.setDeliverytype("DeliveryAtHome");
            if (NetworkUtils.isNetworkConnected(DeliverySelectionActivity.this)) {
                Utils.showDialog(this, getApplicationContext().getResources().getString(R.string.label_please_wait));
                controller.getAddressList(this, DeliverySelectionActivity.this);
            } else {
                Utils.showSnackbar(DeliverySelectionActivity.this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
            }
        } else {
            if (SessionManager.INSTANCE.getKioskSetupResponse().getDeliveryMode().getHomeDelivery()) {
                yourHomeLayout.setVisibility(View.VISIBLE);
            }
            if (SessionManager.INSTANCE.getKioskSetupResponse().getDeliveryMode().getStorePickup()) {
                controller.getStoreLocators(DeliverySelectionActivity.this, DeliverySelectionActivity.this);
                nearApolloLayout.setVisibility(View.VISIBLE);
            }
            if (SessionManager.INSTANCE.getKioskSetupResponse().getDeliveryMode().getAtKiosk()) {
                atKioskLayout.setVisibility(View.VISIBLE);
            }
        }
        yourHomeLayout.setOnClickListener(v -> {
            isHomeDeliveryChecked = true;
            isNearestApolloChecked = false;
            isAtKioskChecked = false;

            firstHomeLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.apollo_store_btn_bg));
            thirdHomeLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.near_apollo_red_btn_bg));
            proceedLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.lang_selection_btn_bg));
            homeTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.near_apollo_store_black_color));
            estimatedHomeTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.near_apollo_store_black_color));
            homeButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
            firstApolloLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_address_button_bg));
            thirdApolloLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_btn_bg));
            apolloTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
            estimatedApolloTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.color_delivery));
            apolloButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorBlack));
            firstKioskLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_address_button_bg));
            thirdKioskLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_btn_bg));
            kioskTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
            estimatedKioskTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.color_delivery));
            kioskButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorBlack));
        });

        nearApolloLayout.setOnClickListener(v -> {
            isHomeDeliveryChecked = false;
            isNearestApolloChecked = true;
            isAtKioskChecked = false;

            firstHomeLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_address_button_bg));
            thirdHomeLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_btn_bg));
            proceedLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.lang_selection_btn_bg));
            homeTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
            estimatedHomeTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.color_delivery));
            homeButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorBlack));
            firstApolloLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.apollo_store_btn_bg));
            thirdApolloLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.near_apollo_red_btn_bg));
            apolloTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.near_apollo_store_black_color));
            estimatedApolloTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.near_apollo_store_black_color));
            apolloButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
            firstKioskLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_address_button_bg));
            thirdKioskLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_btn_bg));
            kioskTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
            estimatedKioskTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.color_delivery));
            kioskButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorBlack));
        });

        atKioskLayout.setOnClickListener(v -> {
            isHomeDeliveryChecked = false;
            isNearestApolloChecked = false;
            isAtKioskChecked = true;

            firstHomeLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_address_button_bg));
            thirdHomeLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_btn_bg));
            proceedLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.lang_selection_btn_bg));
            homeTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
            estimatedHomeTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.color_delivery));
            homeButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorBlack));
            firstApolloLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_address_button_bg));
            thirdApolloLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_btn_bg));
            apolloTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
            estimatedApolloTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.color_delivery));
            apolloButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorBlack));
            firstKioskLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.apollo_store_btn_bg));
            thirdKioskLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.near_apollo_red_btn_bg));
            kioskTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.near_apollo_store_black_color));
            estimatedKioskTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.near_apollo_store_black_color));
            kioskButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
        });

        proceedLayout.setOnClickListener(v -> {
            if (!isHomeDeliveryChecked && !isNearestApolloChecked && !isAtKioskChecked) {
                Utils.showSnackbar(DeliverySelectionActivity.this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_select_delivery_option_alert));
            } else {
                if (isHomeDeliveryChecked) {
                    SessionManager.INSTANCE.setDeliverytype("DeliveryAtHome");
                    if (NetworkUtils.isNetworkConnected(DeliverySelectionActivity.this)) {
                        Utils.showDialog(this, getApplicationContext().getResources().getString(R.string.label_please_wait));
                        controller.getAddressList(this, DeliverySelectionActivity.this);
                    } else {
                        Utils.showSnackbar(DeliverySelectionActivity.this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
                    }
                } else if (isNearestApolloChecked) {
                    SessionManager.INSTANCE.setDeliverytype("PickfromStore");
                    Intent intent1 = new Intent(DeliverySelectionActivity.this, StorePickupActivity.class);
                    startActivity(intent1);
                    overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
                } else if (isAtKioskChecked) {
                    SessionManager.INSTANCE.setDeliverytype("AtKiosk");
                    UserAddress userAddress = new UserAddress();
                    userAddress.setName(SessionManager.INSTANCE.getKioskSetupResponse().getKIOSK_ID());
                    userAddress.setAddress1(SessionManager.INSTANCE.getKioskSetupResponse().getADDRESS());
                    userAddress.setCity(SessionManager.INSTANCE.getKioskSetupResponse().getCITY());
                    userAddress.setState(SessionManager.INSTANCE.getKioskSetupResponse().getSTATE());
                    userAddress.setPincode(SessionManager.INSTANCE.getKioskSetupResponse().getPINCODE());
                    SessionManager.INSTANCE.setUseraddress(userAddress);

                    Intent intent2 = new Intent(DeliverySelectionActivity.this, OrderSummaryActivity.class);
                    intent2.putExtra("activityname", "DeliverySelectionActivity");
                    startActivity(intent2);
                    overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
                }
            }
        });
    }

    public void onSuccessGetProductcountFromLocation(List<List<Inventory>> response) {
        if (response != null && response.size() > 0) {
            availabilityArrayList.clear();
            quantityArrayList.clear();
            if (response.size() > 0) {
                for (int n = 0; n < response.size(); n++) {
                    int count = 0;
                    List<Inventory> MainData = response.get(n);
                    if (MainData.size() > 0) {
                        for (Inventory d : MainData) {
                            count = count + d.getQty();
                        }
                        quantityArrayList.add(count);
                    } else {
                        quantityArrayList.add(count);
                    }
                }
            }
        } else {
            Utils.showCustomAlertDialog(DeliverySelectionActivity.this, getApplicationContext().getResources().getString(R.string.try_again_later), false, "OK", "");
        }
    }

    public void onSuccessGetProductcountBasedonsiteid(List<InventoryBasedonSiteid> response) {
        if (response != null && response.size() > 0) {
            availabilityArrayList.clear();
            quantityArrayList.clear();
            if (response.size() > 0) {
                for (InventoryBasedonSiteid d : response) {
                    availabilityArrayList.add(d.getArtcode());
                    quantityArrayList.add(d.getQty());
                }
            }
        } else {
            Utils.showCustomAlertDialog(DeliverySelectionActivity.this, getApplicationContext().getResources().getString(R.string.try_again_later), false, "OK", "");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void checkSelectedStatus() {
        String deliveryType = SessionManager.INSTANCE.getDeliverytype();
        if (deliveryType.equalsIgnoreCase("DeliveryAtHome")) {
            isHomeDeliveryChecked = true;
            isNearestApolloChecked = false;
            isAtKioskChecked = false;

            firstHomeLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.apollo_store_btn_bg));
            thirdHomeLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.near_apollo_red_btn_bg));
            proceedLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.lang_selection_btn_bg));
            homeTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.near_apollo_store_black_color));
            estimatedHomeTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.near_apollo_store_black_color));
            homeButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
            firstApolloLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_address_button_bg));
            thirdApolloLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_btn_bg));
            apolloTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
            estimatedApolloTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.color_delivery));
            apolloButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorBlack));
            firstKioskLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_address_button_bg));
            thirdKioskLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_btn_bg));
            kioskTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
            estimatedKioskTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.color_delivery));
            kioskButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorBlack));
        } else if (deliveryType.equalsIgnoreCase("PickfromStore")) {
            isHomeDeliveryChecked = false;
            isNearestApolloChecked = true;
            isAtKioskChecked = false;

            firstHomeLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_address_button_bg));
            thirdHomeLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_btn_bg));
            proceedLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.lang_selection_btn_bg));
            homeTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
            estimatedHomeTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.color_delivery));
            homeButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorBlack));
            firstApolloLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.apollo_store_btn_bg));
            thirdApolloLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.near_apollo_red_btn_bg));
            apolloTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.near_apollo_store_black_color));
            estimatedApolloTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.near_apollo_store_black_color));
            apolloButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
            firstKioskLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_address_button_bg));
            thirdKioskLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_btn_bg));
            kioskTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
            estimatedKioskTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.color_delivery));
            kioskButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorBlack));
        } else if (deliveryType.equalsIgnoreCase("AtKiosk")) {
            isHomeDeliveryChecked = false;
            isNearestApolloChecked = false;
            isAtKioskChecked = true;

            firstHomeLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_address_button_bg));
            thirdHomeLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_btn_bg));
            homeTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
            estimatedHomeTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.color_delivery));
            homeButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorBlack));
            firstApolloLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_address_button_bg));
            thirdApolloLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.delivery_btn_bg));
            proceedLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.lang_selection_btn_bg));
            apolloTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
            estimatedApolloTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.color_delivery));
            apolloButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorBlack));
            firstKioskLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.apollo_store_btn_bg));
            thirdKioskLayout.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.near_apollo_red_btn_bg));
            kioskTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.near_apollo_store_black_color));
            estimatedKioskTxt.setTextColor(getApplicationContext().getResources().getColor(R.color.near_apollo_store_black_color));
            kioskButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorWhite));
        }
    }
}