package com.apollo.pharmacy.ocr.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.mposstoresetup.MposStoreSetupActivity;
import com.apollo.pharmacy.ocr.activities.userlogin.UserLoginActivity;
import com.apollo.pharmacy.ocr.adapters.CategoryGridItemAdapter;
import com.apollo.pharmacy.ocr.adapters.GroupOffersAdapters;
import com.apollo.pharmacy.ocr.adapters.MedicineSearchAdapter;
import com.apollo.pharmacy.ocr.adapters.MyOfersAdapterNew;
import com.apollo.pharmacy.ocr.adapters.MyOffersAdapter;
import com.apollo.pharmacy.ocr.adapters.MyoffersAdapterTrending;
import com.apollo.pharmacy.ocr.controller.MyOffersController;
import com.apollo.pharmacy.ocr.databinding.ActivityMyOffersBinding;
import com.apollo.pharmacy.ocr.databinding.DialogLoginPopupBinding;
import com.apollo.pharmacy.ocr.databinding.NewLoginScreenBinding;
import com.apollo.pharmacy.ocr.dialog.AccesskeyDialog;
import com.apollo.pharmacy.ocr.dialog.OfferSelectionDialog;
import com.apollo.pharmacy.ocr.interfaces.CartCountListener;
import com.apollo.pharmacy.ocr.interfaces.MyOffersListener;
import com.apollo.pharmacy.ocr.model.AllOffersResponse;
import com.apollo.pharmacy.ocr.model.BatchListResponse;
import com.apollo.pharmacy.ocr.model.CalculatePosTransactionRequest;
import com.apollo.pharmacy.ocr.model.CalculatePosTransactionResponse;
import com.apollo.pharmacy.ocr.model.GetProductListResponse;
import com.apollo.pharmacy.ocr.model.GroupOffersModelResponse;
import com.apollo.pharmacy.ocr.model.ItemSearchResponse;
import com.apollo.pharmacy.ocr.model.OCRToDigitalMedicineResponse;
import com.apollo.pharmacy.ocr.model.Product;
import com.apollo.pharmacy.ocr.model.ProductSearch;
import com.apollo.pharmacy.ocr.model.ScannedData;
import com.apollo.pharmacy.ocr.model.ScannedMedicine;
import com.apollo.pharmacy.ocr.model.Send_Sms_Request;
import com.apollo.pharmacy.ocr.model.Suggestion_Product;
import com.apollo.pharmacy.ocr.model.UpCellCrossCellResponse;
import com.apollo.pharmacy.ocr.receiver.ConnectivityReceiver;
import com.apollo.pharmacy.ocr.utility.ApplicationConstant;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.utility.NetworkUtils;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.net.ssl.HttpsURLConnection;

import static com.apollo.pharmacy.ocr.activities.HomeActivity.mobileNum;

public class MyOffersActivity extends BaseActivity implements MyOffersListener, CartCountListener,
        ConnectivityReceiver.ConnectivityReceiverListener, CategoryGridItemAdapter.CheckOutData {

    public MyOffersController myOffersController;
    public ArrayList<ProductSearch> searchResult1;
    public MedicineSearchAdapter myAdapter;
    private ArrayList<ProductSearch> item = new ArrayList<>();
    private RecyclerView promotionsRecyclerView, trendingNowRecyclerView;
    private MyOffersAdapter promotionList, trendingNowList;
    public TextView textView1;
    private ArrayList<Product> outofstock_array, stockin_array, outofstock_array1, stockin_array1;
    private ArrayList<Product> product_list_array, product_list_array1;
    private int totalPage = 1, currentPage = 1;
    private int totalPage1 = 1, currentPage1 = 1;
    private boolean isEmpty = false;
    private boolean isEmpty1 = false;
    public boolean isResend=false;
    private List<OCRToDigitalMedicineResponse> dataList = new ArrayList<>();
    private final List<OCRToDigitalMedicineResponse> dataListCount = new ArrayList<>();
    private TextView myCartCount;
    private TextView total_itemcount_textview;
    private String myOffersText = "SpecialOffers";
    private TextView plusIcon, itemsCount;
    private ImageView checkOutImage;
    private TextView fcItemCountTxt, pharmaItemCountTxt;
    private LinearLayout fcItemCountLayout, pharmaItemCountLayout;
    private ConstraintLayout constraintLayout;
    private LinearLayout offerLayout, trendingLayout;
    private TextView offerTxt, trendingTxt;
    private ActivityMyOffersBinding activityMyOffersBinding;
    private ImageView mPdfView;
    String googleDocs = "https://docs.google.com/viewer?url=";
    private String oldMobileNum = "";
    private int otp = 0;
//    private DialogLoginPopupBinding dialogLoginPopupBinding;
    Dialog dialog;
    Context context;
    CountDownTimer cTimer = null;
    NewLoginScreenBinding newLoginScreenBinding;
    private TextView configSroreText;

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
        MyOffersActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMyOffersBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_offers);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context=this;
        HomeActivity.isPaymentSelectionActivity=false;
        HomeActivity.isHomeActivity=false;

        new MyOffersController(this, this).getAllOffersApiCall();


//        mPdfView = (ImageView) findViewById(R.id.offers_pdf_image);
//        mPdfView = (ImageView) findViewById(R.id.offers_pdf_image);
//        WebSettings webSettings = mPdfView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setUseWideViewPort(true);
//        initWebView();
//        String pdf_url = "https://d1xsr68o6znzvt.cloudfront.net/Product_360/product_360/kisok_offers_1.pdf";
//        String pdf = "https://docs.google.com/viewer?url=https://d1xsr68o6znzvt.cloudfront.net/Product_360/product_360/kisok_offers_1.pdf";
//
//        mPdfView.loadUrl(pdf);


//        mPdfView.getSettings().setJavaScriptEnabled(true);
//        mPdfView.loadUrl("https://d1xsr68o6znzvt.cloudfront.net/Product_360/product_360/kisok_offers_1.pdf");
//        new RetrivePDFfromUrl().execute("https://d1xsr68o6znzvt.cloudfront.net/Product_360/product_360/kisok_offers_1.pdf");


        initLeftMenu();

        product_list_array = new ArrayList<Product>();
        product_list_array1 = new ArrayList<Product>();
        item = new ArrayList<>();
        searchResult1 = new ArrayList<>();
        outofstock_array = new ArrayList<Product>();
        stockin_array = new ArrayList<Product>();
        outofstock_array1 = new ArrayList<Product>();
        stockin_array1 = new ArrayList<Product>();

        total_itemcount_textview = findViewById(R.id.total_itemcount_textview);
        plusIcon = findViewById(R.id.plus_icon);
        itemsCount = findViewById(R.id.items_count);
        checkOutImage = findViewById(R.id.checkout_image);
        fcItemCountLayout = findViewById(R.id.fc_item_count_layout);
        fcItemCountTxt = findViewById(R.id.fc_item_count_txt);
        pharmaItemCountLayout = findViewById(R.id.pharma_item_count_layout);
        pharmaItemCountTxt = findViewById(R.id.pharma_item_count_txt);
        textView1 = (TextView) findViewById(R.id.textView1);
        myCartCount = findViewById(R.id.lblCartCnt);
        TextView checkOutText = findViewById(R.id.checkout_text);
        constraintLayout = findViewById(R.id.constraint_layout);

        configSroreText = (TextView) findViewById(R.id.config_store_text);
        configSroreText.setText(SessionManager.INSTANCE.getConfigStore() + " Offers");

        checkOutText.setOnClickListener(v -> {
            finish();
            Intent intent1 = new Intent(MyOffersActivity.this, MyCartActivity.class);
            startActivity(intent1);
            overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
        });

        if (null != SessionManager.INSTANCE.getDataList()) {
            if (SessionManager.INSTANCE.getDataList().size() > 0) {
                List<OCRToDigitalMedicineResponse> tempCartItemList = new ArrayList<>();
                tempCartItemList = SessionManager.INSTANCE.getDataList();
                for (OCRToDigitalMedicineResponse item : tempCartItemList) {
                    dataListCount.add(item);
                }
            }
        }
        SessionManager.INSTANCE.setDataList(dataListCount);
        cartCount(dataListCount.size());
        checkOutImage.setOnClickListener(v -> {
            finish();
            Intent intent1 = new Intent(MyOffersActivity.this, MyCartActivity.class);
            intent1.putExtra("activityname", "AddMoreActivity");
            startActivity(intent1);
            overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
        });

        myOffersController = new MyOffersController(this, this);
//        myOffersController.upcellCrosscellList("7353910637", MyOffersActivity.this);
        offerLayout = findViewById(R.id.offer_layout);
        trendingLayout = findViewById(R.id.trending_layout);
        offerTxt = findViewById(R.id.offer_name);
        trendingTxt = findViewById(R.id.trending_name);

//        myOffersController.groupOffersListeners(this);

        promotionsRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewOffers);
        promotionsRecyclerView.setLayoutManager(new GridLayoutManager(this, 7));
        trendingNowRecyclerView = findViewById(R.id.trendingnowproductrecycleerview);
        trendingNowRecyclerView.setLayoutManager(new GridLayoutManager(this, 7));

        if (null != getIntent().getExtras()) {
            String activityName = this.getIntent().getExtras().getString("categoryname");
            if (activityName.equalsIgnoreCase("Promotions")) {
                product_list_array.clear();
                trendingNowRecyclerView.setVisibility(View.GONE);
                promotionsRecyclerView.setVisibility(View.VISIBLE);
                myOffersText = "SpecialOffers";
                if (NetworkUtils.isNetworkConnected(MyOffersActivity.this)) {
//                    myOffersController.getProductList(myOffersText, this, this, Promotions);
                } else {
                    Utils.showSnackbar(MyOffersActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
                }
                offerLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                offerTxt.setTextColor(getResources().getColor(R.color.offers_list_bg));
                trendingLayout.setBackgroundColor(getResources().getColor(R.color.offers_list_bg));
                trendingTxt.setTextColor(getResources().getColor(R.color.colorWhite));
                fcItemCountLayout.setVisibility(View.VISIBLE);
                fcItemCountTxt.setText("0");
                pharmaItemCountLayout.setVisibility(View.GONE);
                pharmaItemCountTxt.setText("0");
            } else {
                product_list_array1.clear();
                promotionsRecyclerView.setVisibility(View.GONE);
                trendingNowRecyclerView.setVisibility(View.VISIBLE);
                myOffersText = "TrendingNow";
                if (NetworkUtils.isNetworkConnected(MyOffersActivity.this)) {
//                    myOffersController.getProductList("TrendingNow", this, this, TrendingNow);
                } else {
                    Utils.showSnackbar(MyOffersActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
                }
                offerLayout.setBackgroundColor(getResources().getColor(R.color.offers_list_bg));
                offerTxt.setTextColor(getResources().getColor(R.color.colorWhite));
                trendingLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                trendingTxt.setTextColor(getResources().getColor(R.color.offers_list_bg));
                fcItemCountLayout.setVisibility(View.GONE);
                fcItemCountTxt.setText("0");
                pharmaItemCountLayout.setVisibility(View.VISIBLE);
                pharmaItemCountTxt.setText("0");
            }
        }


        ImageView customerCareImg = findViewById(R.id.customer_care_icon);
        LinearLayout customerHelpLayout = findViewById(R.id.customer_help_layout);
        customerHelpLayout.setVisibility(View.GONE);
        customerCareImg.setOnClickListener(v -> {
            if (customerHelpLayout.getVisibility() == View.VISIBLE) {
                customerCareImg.setBackgroundResource(R.drawable.icon_help_circle);
                TranslateAnimation animate = new TranslateAnimation(0, customerHelpLayout.getWidth(), 0, 0);
                animate.setDuration(2000);
                animate.setFillAfter(true);
                customerHelpLayout.startAnimation(animate);
                customerHelpLayout.setVisibility(View.GONE);
            } else {
                customerCareImg.setBackgroundResource(R.drawable.icon_help_circle);
                TranslateAnimation animate = new TranslateAnimation(customerHelpLayout.getWidth(), 0, 0, 0);
                animate.setDuration(2000);
                animate.setFillAfter(true);
                customerHelpLayout.startAnimation(animate);
                customerHelpLayout.setVisibility(View.VISIBLE);
            }
        });

        SessionManager.INSTANCE.setCurrentPage(ApplicationConstant.ACTIVITY_ADDMORE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverAddmore, new IntentFilter("MedicineReciverAddMore"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("cardReceiver"));
        Constants.getInstance().setConnectivityListener(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverNew, new IntentFilter("OrderhistoryCardReciver"));
        Constants.getInstance().setConnectivityListener(this);

        activityMyOffersBinding.crossSellingOfferLay.setBackgroundColor(getResources().getColor(R.color.offers_list_bg));
        activityMyOffersBinding.crossSellingOfferName.setTextColor(getResources().getColor(R.color.white));
        activityMyOffersBinding.upSellingTrendingLayout.setBackgroundColor(getResources().getColor(R.color.white));
        activityMyOffersBinding.upSellingTrendingName.setTextColor(getResources().getColor(R.color.offers_list_bg));
        if (crosssellingList != null && crosssellingList.size() > 0) {
            activityMyOffersBinding.crossSellingCountLayout.setVisibility(View.VISIBLE);
            activityMyOffersBinding.crossSellingCountTxt.setText(String.valueOf(crosssellingList.size()));
        }
        if (upsellingList != null && upsellingList.size() > 0) {
            activityMyOffersBinding.upSellingItemCountLayout.setVisibility(View.VISIBLE);
            activityMyOffersBinding.upSellingItemCountTxt.setText(String.valueOf(upsellingList.size()));
        } else {
            activityMyOffersBinding.upSellingItemCountLayout.setVisibility(View.GONE);
            activityMyOffersBinding.upSellingItemCountTxt.setText("0");
        }
        activityMyOffersBinding.nodataFound.setVisibility(View.GONE);
        listeners();
    }

    @Override
    public void onSearchSuggestionList(List<Suggestion_Product> m) {
        item.clear();
        for (Suggestion_Product r : m) {
            ProductSearch product = new ProductSearch();
            product.setName(r.getName());
            product.setSku(r.getSku());
            product.setQty(1);
            product.setDescription(r.getDescription());
            product.setId(r.getId());
            product.setImage(r.getImage());
            product.setIsInStock(r.getIsInStock());
            product.setIsPrescriptionRequired(r.getIsPrescriptionRequired());
            product.setPrice(r.getPrice());
            product.setSmallImage(r.getSmallImage());
            product.setStatus(r.getStatus());
            product.setThumbnail(r.getThumbnail());
            product.setTypeId(r.getTypeId());
            item.add(product);
        }
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNewSearchResult(List<Suggestion_Product> m) {
        item.clear();
        for (Suggestion_Product r : m) {
            ProductSearch product = new ProductSearch();
            product.setName(r.getName());
            product.setSku(r.getSku());
            product.setQty(1);
            product.setDescription(r.getDescription());
            product.setId(r.getId());
            product.setImage(r.getImage());
            product.setIsInStock(r.getIsInStock());
            product.setIsPrescriptionRequired(r.getIsPrescriptionRequired());
            product.setPrice(r.getPrice());
            product.setSmallImage(r.getSmallImage());
            product.setStatus(r.getStatus());
            product.setThumbnail(r.getThumbnail());
            product.setTypeId(r.getTypeId());
            item.add(product);
        }
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccessLoadMoreTrendingNow(GetProductListResponse response) {
        if (product_list_array1.size() > 0) {
            product_list_array1.remove(product_list_array1.size() - 1);
            if (response.getProducts() != null && response.getProducts().size() > 0) {
                ArrayList<Product> temp = (ArrayList<Product>) response.getProducts();
                for (Product p : temp) {
                    if (p.getIsInStock() == 1) {
                        product_list_array1.add(p);
                    } else {
                        outofstock_array1.add(p);
                    }
                }
                if (totalPage1 == currentPage1) {
                    if (isEmpty1) {
                        product_list_array1.remove(0);
                        trendingNowList.notifyItemRemoved(0);
                        isEmpty1 = false;
                    }
                    product_list_array1.addAll(outofstock_array1);
                    trendingNowList.setMoreDataAvailable(false);
                }
            }
            if (null != product_list_array1 && product_list_array1.size() > 0) {
                total_itemcount_textview.setVisibility(View.GONE);
                total_itemcount_textview.setText(product_list_array1.size() + " " + getApplicationContext().getResources().getString(R.string.label_items));
                pharmaItemCountTxt.setText(String.valueOf(product_list_array1.size()));
            } else {
                total_itemcount_textview.setVisibility(View.GONE);
                total_itemcount_textview.setText("0" + " " + getApplicationContext().getResources().getString(R.string.label_items));
                pharmaItemCountTxt.setText("0");
            }
            trendingNowList.notifyDataSetChanged();
        }
    }

    @Override
    public void onFailureLoadMoreTrendingNow(String message) {
        Utils.showCustomAlertDialog(MyOffersActivity.this, message, false, getApplicationContext().getResources().getString(R.string.label_ok), "");
    }

    @Override
    public void onSuccessLoadMorePromotions(GetProductListResponse response) {
        if (product_list_array.size() > 0) {
            product_list_array.remove(product_list_array.size() - 1);
            if (null != response.getProducts() && response.getProducts().size() > 0) {
                ArrayList<Product> temp = (ArrayList<Product>) response.getProducts();
                for (Product p : temp) {
                    if (p.getIsInStock() == 1) {
                        product_list_array.add(p);
                    } else {
                        outofstock_array.add(p);
                    }
                }
                if (totalPage == currentPage) {
                    if (isEmpty) {
                        product_list_array.remove(0);
                        promotionList.notifyItemRemoved(0);
                        isEmpty = false;
                    }
                    product_list_array.addAll(outofstock_array);
                    promotionList.setMoreDataAvailable(false);
                }
            }
            if (null != product_list_array && product_list_array.size() > 0) {
                total_itemcount_textview.setVisibility(View.GONE);
                total_itemcount_textview.setText(product_list_array.size() + " " + getApplicationContext().getResources().getString(R.string.label_items));
                fcItemCountTxt.setText(String.valueOf(product_list_array.size()));
            } else {
                total_itemcount_textview.setVisibility(View.GONE);
                total_itemcount_textview.setText("0" + " " + getApplicationContext().getResources().getString(R.string.label_items));
                fcItemCountTxt.setText("0");
            }
            promotionList.notifyDataChanged();
        }
    }

    @Override
    public void onFailureLoadMorePromotions(String message) {
        Utils.showCustomAlertDialog(MyOffersActivity.this, message, false, getApplicationContext().getResources().getString(R.string.label_ok), "");
    }

    @Override
    public void onSuccessSearchItemApi(ItemSearchResponse itemSearchResponse) {

    }

    public void listeners() {
        activityMyOffersBinding.crossSellingOfferLay.setOnClickListener(v -> {
            product_list_array.clear();
            product_list_array1.clear();
            activityMyOffersBinding.upSellingTrendingrecycleerviewNew.setVisibility(View.GONE);
            activityMyOffersBinding.crossSellingRecycleNew.setVisibility(View.VISIBLE);
            myOffersText = "SpecialOffers";

            MyOfersAdapterNew crossCellAdapter = new MyOfersAdapterNew(MyOffersActivity.this, MyOffersActivity.this, crosssellingList, this);
            activityMyOffersBinding.crossSellingRecycleNew.setLayoutManager(new GridLayoutManager(this, 6));
            activityMyOffersBinding.crossSellingRecycleNew.setAdapter(crossCellAdapter);

//            if (NetworkUtils.isNetworkConnected(MyOffersActivity.this)) {
////                myOffersController.getProductList("SpecialOffers", this, this, Promotions);
//                myOffersController.upcellCrosscellList("7353910637", getApplicationContext());
//            } else {
//                Utils.showSnackbar(MyOffersActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
//            }
            activityMyOffersBinding.crossSellingOfferLay.setBackgroundColor(getResources().getColor(R.color.offers_list_bg));
            activityMyOffersBinding.crossSellingOfferName.setTextColor(getResources().getColor(R.color.colorWhite));
            activityMyOffersBinding.upSellingTrendingLayout.setBackgroundColor(getResources().getColor(R.color.white));
            activityMyOffersBinding.upSellingTrendingName.setTextColor(getResources().getColor(R.color.offers_list_bg));
            if (crosssellingList != null && crosssellingList.size() > 0) {

                activityMyOffersBinding.crossSellingCountLayout.setVisibility(View.VISIBLE);
                activityMyOffersBinding.crossSellingCountTxt.setText(String.valueOf(crosssellingList.size()));
            }
            if (upsellingList != null && upsellingList.size() > 0) {
                activityMyOffersBinding.upSellingItemCountLayout.setVisibility(View.VISIBLE);
                activityMyOffersBinding.upSellingItemCountTxt.setText(String.valueOf(upsellingList.size()));
            } else {
                activityMyOffersBinding.upSellingItemCountLayout.setVisibility(View.GONE);
                activityMyOffersBinding.upSellingItemCountTxt.setText("0");
            }
            activityMyOffersBinding.nodataFound.setVisibility(View.GONE);
            if (crosssellingList.size() < 1) {
                activityMyOffersBinding.nodataFound.setVisibility(View.VISIBLE);
            }
        });

        activityMyOffersBinding.upSellingTrendingLayout.setOnClickListener(v -> {
            product_list_array.clear();
            product_list_array1.clear();
            activityMyOffersBinding.crossSellingRecycleNew.setVisibility(View.GONE);
            activityMyOffersBinding.upSellingTrendingrecycleerviewNew.setVisibility(View.VISIBLE);
            myOffersText = "TrendingNow";

            MyoffersAdapterTrending trending = new MyoffersAdapterTrending(MyOffersActivity.this, this, upsellingList, this);
            activityMyOffersBinding.upSellingTrendingrecycleerviewNew.setLayoutManager(new GridLayoutManager(this, 6));
            activityMyOffersBinding.upSellingTrendingrecycleerviewNew.setAdapter(trending);

//            if (NetworkUtils.isNetworkConnected(MyOffersActivity.this)) {
////                myOffersController.getProductList("TrendingNow", this, this, TrendingNow);
//                myOffersController.upcellCrosscellList("7353910637", getApplicationContext());
//
//            } else {
//                Utils.showSnackbar(MyOffersActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
//            }
            activityMyOffersBinding.crossSellingOfferLay.setBackgroundColor(getResources().getColor(R.color.white));
            activityMyOffersBinding.crossSellingOfferName.setTextColor(getResources().getColor(R.color.offers_list_bg));
            activityMyOffersBinding.upSellingTrendingLayout.setBackgroundColor(getResources().getColor(R.color.offers_list_bg));
            activityMyOffersBinding.upSellingTrendingName.setTextColor(getResources().getColor(R.color.colorWhite));
            if (crosssellingList != null && crosssellingList.size() > 0) {
                activityMyOffersBinding.crossSellingCountLayout.setVisibility(View.VISIBLE);
                activityMyOffersBinding.crossSellingCountTxt.setText(String.valueOf(crosssellingList.size()));
            } else {
                activityMyOffersBinding.crossSellingCountLayout.setVisibility(View.GONE);
                activityMyOffersBinding.crossSellingCountTxt.setText("0");
            }
            if (upsellingList != null && upsellingList.size() > 0) {
                activityMyOffersBinding.upSellingItemCountLayout.setVisibility(View.VISIBLE);
                activityMyOffersBinding.upSellingItemCountTxt.setText(String.valueOf(upsellingList.size()));
            }
            activityMyOffersBinding.nodataFound.setVisibility(View.GONE);
            if (upsellingList.size() < 1) {
                activityMyOffersBinding.nodataFound.setVisibility(View.VISIBLE);
            }
        });
    }

    List<UpCellCrossCellResponse.Crossselling> crosssellingList = new ArrayList<>();

    List<UpCellCrossCellResponse.Upselling> upsellingList = new ArrayList<>();

    @Override
    public void onSuccessSearchUpcellCroscellApi(UpCellCrossCellResponse productList) {
        if (productList != null && productList.getCrossselling() != null && productList.getCrossselling().size() > 0) {
            crosssellingList = productList.getCrossselling();

            activityMyOffersBinding.upSellingTrendingrecycleerviewNew.setVisibility(View.GONE);
            activityMyOffersBinding.crossSellingRecycleNew.setVisibility(View.VISIBLE);
            myOffersText = "SpecialOffers";

            MyOfersAdapterNew crossCellAdapter = new MyOfersAdapterNew(MyOffersActivity.this, MyOffersActivity.this, crosssellingList, this);
            activityMyOffersBinding.crossSellingRecycleNew.setLayoutManager(new GridLayoutManager(this, 6));
            activityMyOffersBinding.crossSellingRecycleNew.setAdapter(crossCellAdapter);

            activityMyOffersBinding.crossSellingOfferLay.setBackgroundColor(getResources().getColor(R.color.offers_list_bg));
            activityMyOffersBinding.crossSellingOfferName.setTextColor(getResources().getColor(R.color.colorWhite));
            activityMyOffersBinding.upSellingTrendingLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            activityMyOffersBinding.upSellingTrendingName.setTextColor(getResources().getColor(R.color.offers_list_bg));
            activityMyOffersBinding.crossSellingCountLayout.setVisibility(View.VISIBLE);
            activityMyOffersBinding.crossSellingCountTxt.setText(String.valueOf(crosssellingList.size()));
            activityMyOffersBinding.nodataFound.setVisibility(View.GONE);
        } else {
            activityMyOffersBinding.crossSellingRecycleNew.setVisibility(View.GONE);
            activityMyOffersBinding.nodataFound.setVisibility(View.VISIBLE);
        }
        if (productList != null && productList.getUpselling() != null && productList.getUpselling().size() > 0) {
            upsellingList = productList.getUpselling();
            activityMyOffersBinding.nodataFound.setVisibility(View.GONE);
        } else {
            activityMyOffersBinding.crossSellingRecycleNew.setVisibility(View.GONE);
            activityMyOffersBinding.nodataFound.setVisibility(View.VISIBLE);
        }

        if (upsellingList != null && upsellingList.size() > 0) {
            activityMyOffersBinding.upSellingItemCountLayout.setVisibility(View.VISIBLE);
            activityMyOffersBinding.upSellingItemCountTxt.setText(String.valueOf(upsellingList.size()));
        } else {
            activityMyOffersBinding.upSellingItemCountLayout.setVisibility(View.GONE);
            activityMyOffersBinding.upSellingItemCountTxt.setText("0");
        }

        Utils.dismissDialog();
    }

    private int screeSize() {
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        String toastMsg;

        if (screenSize == 4) {
            return 4;
        } else if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            toastMsg = "Large screen";
            return 4;
        } else if (screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            toastMsg = "Normal screen";
            return 3;
        } else if (screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            toastMsg = "Small screen";
            return 3;
        } else {
            return 4;
        }
    }

    @Override
    public void onSearchFailureUpcellCroscell(String message) {

    }

    @Override
    public void onSuccessAllOffers(AllOffersResponse allOffersResponse) {
        GroupOffersAdapters adapter = new GroupOffersAdapters(this, allOffersResponse.getData(), this);
        activityMyOffersBinding.offersRecycle.setLayoutManager(new GridLayoutManager(this, 2));
//        activityMyOffersBinding.offersRecycle.setLayoutManager(new LinearLayoutManager(this));
        activityMyOffersBinding.offersRecycle.setAdapter(adapter);
    }

    @Override
    public void onFailureAllOffers(String message) {

    }

    @Override
    public void onSuccessGroupOffersApi(GroupOffersModelResponse groupOffersModelResponse) {
//        GroupOffersAdapters adapter = new GroupOffersAdapters(this, groupOffersModelResponse.getOffers(), this);
//        activityMyOffersBinding.offersRecycle.setLayoutManager(new LinearLayoutManager(this));
//        activityMyOffersBinding.offersRecycle.setAdapter(adapter);
    }

    @Override
    public void onFailureGroupOffers() {

    }


    @Override
    public void onfiftyPerOffOffer(AllOffersResponse.Datum offer, List<AllOffersResponse.PromoItem> image) {
        offerSelectionDialogCcall(offer, image);
        toasCount = 0;
    }

    @Override
    public void onBuyOneGetOneOffer(AllOffersResponse.Datum offer, List<AllOffersResponse.PromoItem> image) {
        offerSelectionDialogCcall(offer, image);
        toasCount = 0;

    }

    @Override
    public void onBuyMultipleOnGroupOfOffers(AllOffersResponse.Datum offer, List<AllOffersResponse.PromoItem> image) {
        offerSelectionDialogCcall(offer, image);
        toasCount = 0;
    }


    @Override
    public void onContinueOfSelectedOffers() {
        if (offerCount == offer.getPromoItemSelection()) {
//            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
            offerCount = 0;
            offerSelectionDialog.dismiss();

            for (int i = 0; i < imageList.size(); i++) {
                if (imageList.get(i).isSelected()) {
                    myOffersController.searchItemProducts(imageList.get(i).getArtCode().toString(), i);// ---CIP0081---SHE0003---THY0009----DER0056
                }
            }
//            offersAddToCartData();
        } else {
            Toast.makeText(this, "We can add maximum " + offer.getPromoItemSelection() + "products", Toast.LENGTH_SHORT).show();
        }
    }

    List<OCRToDigitalMedicineResponse> dummyDataList = new ArrayList<>();

    private void offersAddToCartData() {
        for (int i = 0; i < imageList.size(); i++) {
            if (imageList.get(i).isSelected()) {
                OCRToDigitalMedicineResponse data = new OCRToDigitalMedicineResponse();
                data.setArtName(imageList.get(i).getProductName());
                data.setArtCode(imageList.get(i).getArtCode());
                data.setBatchId("");
                data.setArtprice("0");
                data.setContainer("");
                data.setQty(0);

                data.setMedicineType("FMCG");
                dummyDataList.add(data);
            }
        }


        if (null != SessionManager.INSTANCE.getDataList()) {
            if (SessionManager.INSTANCE.getDataList().size() > 0) {
                List<OCRToDigitalMedicineResponse> tempCartItemList = new ArrayList<>();
                tempCartItemList = SessionManager.INSTANCE.getDataList();
                for (OCRToDigitalMedicineResponse listItem : tempCartItemList) {
                    boolean isItemEqual = false;
                    for (OCRToDigitalMedicineResponse duplecateItem : dummyDataList) {
                        if (duplecateItem.getArtCode().equals(listItem.getArtCode())) {
                            isItemEqual = true;
                        }
                    }
                    if (!isItemEqual)
                        dummyDataList.add(listItem);
                }
            }
        }
        SessionManager.INSTANCE.setDataList(dummyDataList);
        Intent intent = new Intent("OrderhistoryCardReciver");
        intent.putExtra("message", "OrderNow");
        intent.putExtra("MedininesNames", new Gson().toJson(dummyDataList));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }


    //    private GroupOffersModelResponse.Offer offer;
    private AllOffersResponse.Datum offer;
    private int offerCount;
    //    List<GroupOffersModelResponse.Offer.PromoItem> imageList;
    private List<AllOffersResponse.PromoItem> imageList;

    @Override
    public void onSelectedOffersList(AllOffersResponse.Datum offer, AllOffersResponse.PromoItem image, List<AllOffersResponse.PromoItem> imageList) {
        this.offerCount = image.getOfferCount();
    }

    //    private List<OCRToDigitalMedicineResponse> dummyDataList = new ArrayList<>();
    float overallBatchQuantity;

    int requiredBalanceQty;
    private int flag = 0;
    private int toasCount = 0;
    private int k = 0;

    @Override
    public void setSuccessBatchList(BatchListResponse batchListResponse, int position, ItemSearchResponse.Item iteSearchData) {

        int requiredQty = Integer.parseInt(imageList.get(position).getQty());
        for (int i = 0; i < batchListResponse.getBatchList().size(); i++) {
            if (Double.parseDouble(batchListResponse.getBatchList().get(i).getQOH()) >= requiredQty) {
                if (!batchListResponse.getBatchList().get(i).getNearByExpiry()) {
                    OCRToDigitalMedicineResponse data = new OCRToDigitalMedicineResponse();
                    data.setArtName(iteSearchData.getDescription());
                    data.setArtCode(iteSearchData.getArtCode() + "," + batchListResponse.getBatchList().get(i).getBatchNo());
                    data.setBatchId(batchListResponse.getBatchList().get(i).getBatchNo());
                    data.setArtprice(String.valueOf(batchListResponse.getBatchList().get(i).getMrp()));
                    data.setContainer("");
                    data.setQty(requiredQty);

                    //calculate pos transaction
                    CalculatePosTransactionRequest.SalesLine salesLine = new CalculatePosTransactionRequest.SalesLine();
                    salesLine.setBatchNo(batchListResponse.getBatchList().get(i).getBatchNo());
                    salesLine.setAdditionaltax(0.0);
                    salesLine.setApplyDiscount(false);
                    salesLine.setBarcode("");
                    salesLine.setBaseAmount(batchListResponse.getBatchList().get(i).getMrp());
                    salesLine.setCESSPerc(batchListResponse.getBatchList().get(i).getCESSPerc());
                    salesLine.setCESSTaxCode(batchListResponse.getBatchList().get(i).getCESSTaxCode());
                    salesLine.setCGSTPerc(batchListResponse.getBatchList().get(i).getCGSTPerc());
                    salesLine.setCGSTTaxCode(batchListResponse.getBatchList().get(i).getCGSTTaxCode());
                    salesLine.setCategory(iteSearchData.getCategory());
                    salesLine.setCategoryCode(iteSearchData.getCategoryCode());
                    salesLine.setCategoryReference("");
                    salesLine.setComment("");
                    salesLine.setDpco(iteSearchData.getDpco());
                    salesLine.setDiscAmount(0.0);
                    salesLine.setDiscId("");
                    salesLine.setDiscOfferId("");
                    salesLine.setDiscountStructureType(0.0);
                    salesLine.setDiscountType("");
                    salesLine.setDiseaseType(iteSearchData.getDiseaseType());
                    salesLine.setExpiry(batchListResponse.getBatchList().get(i).getExpDate());
                    salesLine.setHsncodeIn(iteSearchData.getHsncodeIn());
                    salesLine.setIGSTPerc(batchListResponse.getBatchList().get(i).getIGSTPerc());
                    salesLine.setIGSTTaxCode(batchListResponse.getBatchList().get(i).getIGSTTaxCode());
                    salesLine.setISPrescribed(0.0);
                    salesLine.setISReserved(false);
                    salesLine.setISStockAvailable(true);
                    salesLine.setInventBatchId("");//-----0B110081BQ
                    salesLine.setIsChecked(false);
                    salesLine.setIsGeneric(iteSearchData.getIsGeneric());
                    salesLine.setIsPriceOverride(false);
                    salesLine.setIsSubsitute(false);
                    salesLine.setIsVoid(false);
                    salesLine.setItemId(batchListResponse.getBatchList().get(i).getItemID());
                    salesLine.setItemName(iteSearchData.getDescription());
                    salesLine.setLineDiscPercentage(0.0);
                    salesLine.setLineManualDiscountAmount(0.0);
                    salesLine.setLineManualDiscountPercentage(0.0);
                    salesLine.setLineNo(1);
                    salesLine.setLinedscAmount(0.0);
                    salesLine.setMMGroupId("0");
                    salesLine.setMrp(batchListResponse.getBatchList().get(i).getMrp());
                    salesLine.setManufacturerCode(iteSearchData.getManufactureCode());
                    salesLine.setManufacturerName(iteSearchData.getManufacture());
                    salesLine.setMixMode(false);
                    salesLine.setModifyBatchId("");
                    salesLine.setNetAmount(batchListResponse.getBatchList().get(i).getMrp());
                    salesLine.setNetAmountInclTax(batchListResponse.getBatchList().get(i).getMrp());
                    salesLine.setOfferAmount(0.0);
                    salesLine.setOfferDiscountType(0.0);
                    salesLine.setOfferQty(0.0);
                    salesLine.setOfferType(0.0);
                    salesLine.setOmsLineID(0.0);
                    salesLine.setOmsLineRECID(0.0);
                    salesLine.setOrderStatus(0.0);
                    salesLine.setNetAmountInclTax(0.0);
                    salesLine.setOriginalPrice(batchListResponse.getBatchList().get(i).getMrp());
                    salesLine.setPeriodicDiscAmount(0.0);
                    salesLine.setPhysicalMRP(0.0);
                    salesLine.setPreviewText("");
                    salesLine.setPrice(batchListResponse.getBatchList().get(i).getMrp());
                    salesLine.setProductRecID(iteSearchData.getProductRecID());
                    salesLine.setQty(requiredQty);
                    salesLine.setRemainderDays(0.0);
                    salesLine.setRemainingQty(0.0);
                    salesLine.setResqtyflag(false);
                    salesLine.setRetailCategoryRecID(iteSearchData.getRetailCategoryRecID());
                    salesLine.setRetailMainCategoryRecID(iteSearchData.getRetailMainCategoryRecID());
                    salesLine.setRetailSubCategoryRecID(iteSearchData.getRetailSubCategoryRecID());
                    salesLine.setReturnQty(0.0);
                    salesLine.setSGSTPerc(batchListResponse.getBatchList().get(i).getSGSTPerc());
                    salesLine.setSGSTTaxCode(batchListResponse.getBatchList().get(i).getSGSTTaxCode());
                    salesLine.setScheduleCategory(iteSearchData.getSchCatg());
                    salesLine.setScheduleCategoryCode(iteSearchData.getSchCatgCode());
                    salesLine.setStockQty(Double.valueOf(iteSearchData.getStockqty()));
                    salesLine.setSubCategory(iteSearchData.getSubCategory());
                    salesLine.setSubCategoryCode(iteSearchData.getSubCategory());
                    salesLine.setSubClassification(iteSearchData.getSubClassification());
                    salesLine.setSubstitudeItemId("");
                    salesLine.setTax(batchListResponse.getBatchList().get(i).getTotalTax());
                    salesLine.setTaxAmount(batchListResponse.getBatchList().get(i).getTotalTax());
                    salesLine.setTotal(batchListResponse.getBatchList().get(i).getMrp());
                    salesLine.setTotalDiscAmount(0.0);
                    salesLine.setTotalDiscPct(0.0);
                    salesLine.setTotalRoundedAmount(0.0);
                    salesLine.setTotalTax(batchListResponse.getBatchList().get(i).getTotalTax());
                    salesLine.setUnit("");
                    salesLine.setUnitPrice(batchListResponse.getBatchList().get(i).getMrp());
                    salesLine.setUnitQty((double) requiredQty);
                    salesLine.setVariantId("");
                    salesLine.setIsReturnClick(false);
                    salesLine.setIsSelectedReturnItem(false);
                    data.setSalesLine(salesLine);

                    data.setMedicineType(iteSearchData.getCategory());
                    dummyDataList.add(data);
                    break;
                }
            } else if (Double.parseDouble(batchListResponse.getBatchList().get(i).getQOH()) < requiredQty) {
                if (!batchListResponse.getBatchList().get(i).getNearByExpiry()) {

                    OCRToDigitalMedicineResponse data = new OCRToDigitalMedicineResponse();
                    data.setArtName(iteSearchData.getDescription());
                    data.setArtCode(iteSearchData.getArtCode() + "," + batchListResponse.getBatchList().get(i).getBatchNo());
                    data.setBatchId(batchListResponse.getBatchList().get(i).getBatchNo());
                    data.setArtprice(String.valueOf(batchListResponse.getBatchList().get(i).getMrp()));
                    data.setContainer("");
                    data.setQty((int) Float.parseFloat(batchListResponse.getBatchList().get(i).getQOH()));


                    //calculate pos transaction
                    CalculatePosTransactionRequest.SalesLine salesLine = new CalculatePosTransactionRequest.SalesLine();
                    salesLine.setBatchNo(batchListResponse.getBatchList().get(i).getBatchNo());
                    salesLine.setAdditionaltax(0.0);
                    salesLine.setApplyDiscount(false);
                    salesLine.setBarcode("");
                    salesLine.setBaseAmount(batchListResponse.getBatchList().get(i).getMrp());
                    salesLine.setCESSPerc(batchListResponse.getBatchList().get(i).getCESSPerc());
                    salesLine.setCESSTaxCode(batchListResponse.getBatchList().get(i).getCESSTaxCode());
                    salesLine.setCGSTPerc(batchListResponse.getBatchList().get(i).getCGSTPerc());
                    salesLine.setCGSTTaxCode(batchListResponse.getBatchList().get(i).getCGSTTaxCode());
                    salesLine.setCategory(iteSearchData.getCategory());
                    salesLine.setCategoryCode(iteSearchData.getCategoryCode());
                    salesLine.setCategoryReference("");
                    salesLine.setComment("");
                    salesLine.setDpco(iteSearchData.getDpco());
                    salesLine.setDiscAmount(0.0);
                    salesLine.setDiscId("");
                    salesLine.setDiscOfferId("");
                    salesLine.setDiscountStructureType(0.0);
                    salesLine.setDiscountType("");
                    salesLine.setDiseaseType(iteSearchData.getDiseaseType());
                    salesLine.setExpiry(batchListResponse.getBatchList().get(i).getExpDate());
                    salesLine.setHsncodeIn(iteSearchData.getHsncodeIn());
                    salesLine.setIGSTPerc(batchListResponse.getBatchList().get(i).getIGSTPerc());
                    salesLine.setIGSTTaxCode(batchListResponse.getBatchList().get(i).getIGSTTaxCode());
                    salesLine.setISPrescribed(0.0);
                    salesLine.setISReserved(false);
                    salesLine.setISStockAvailable(true);
                    salesLine.setInventBatchId("");//-----0B110081BQ
                    salesLine.setIsChecked(false);
                    salesLine.setIsGeneric(iteSearchData.getIsGeneric());
                    salesLine.setIsPriceOverride(false);
                    salesLine.setIsSubsitute(false);
                    salesLine.setIsVoid(false);
                    salesLine.setItemId(batchListResponse.getBatchList().get(i).getItemID());
                    salesLine.setItemName(iteSearchData.getDescription());
                    salesLine.setLineDiscPercentage(0.0);
                    salesLine.setLineManualDiscountAmount(0.0);
                    salesLine.setLineManualDiscountPercentage(0.0);
                    salesLine.setLineNo(1);
                    salesLine.setLinedscAmount(0.0);
                    salesLine.setMMGroupId("0");
                    salesLine.setMrp(batchListResponse.getBatchList().get(i).getMrp());
                    salesLine.setManufacturerCode(iteSearchData.getManufactureCode());
                    salesLine.setManufacturerName(iteSearchData.getManufacture());
                    salesLine.setMixMode(false);
                    salesLine.setModifyBatchId("");
                    salesLine.setNetAmount(batchListResponse.getBatchList().get(i).getMrp());
                    salesLine.setNetAmountInclTax(batchListResponse.getBatchList().get(i).getMrp());
                    salesLine.setOfferAmount(0.0);
                    salesLine.setOfferDiscountType(0.0);
                    salesLine.setOfferQty(0.0);
                    salesLine.setOfferType(0.0);
                    salesLine.setOmsLineID(0.0);
                    salesLine.setOmsLineRECID(0.0);
                    salesLine.setOrderStatus(0.0);
                    salesLine.setNetAmountInclTax(0.0);
                    salesLine.setOriginalPrice(batchListResponse.getBatchList().get(i).getMrp());
                    salesLine.setPeriodicDiscAmount(0.0);
                    salesLine.setPhysicalMRP(0.0);
                    salesLine.setPreviewText("");
                    salesLine.setPrice(batchListResponse.getBatchList().get(i).getMrp());
                    salesLine.setProductRecID(iteSearchData.getProductRecID());
                    salesLine.setQty((int) Float.parseFloat(batchListResponse.getBatchList().get(i).getQOH()));
                    salesLine.setRemainderDays(0.0);
                    salesLine.setRemainingQty(0.0);
                    salesLine.setResqtyflag(false);
                    salesLine.setRetailCategoryRecID(iteSearchData.getRetailCategoryRecID());
                    salesLine.setRetailMainCategoryRecID(iteSearchData.getRetailMainCategoryRecID());
                    salesLine.setRetailSubCategoryRecID(iteSearchData.getRetailSubCategoryRecID());
                    salesLine.setReturnQty(0.0);
                    salesLine.setSGSTPerc(batchListResponse.getBatchList().get(i).getSGSTPerc());
                    salesLine.setSGSTTaxCode(batchListResponse.getBatchList().get(i).getSGSTTaxCode());
                    salesLine.setScheduleCategory(iteSearchData.getSchCatg());
                    salesLine.setScheduleCategoryCode(iteSearchData.getSchCatgCode());
                    salesLine.setStockQty(Double.valueOf(iteSearchData.getStockqty()));
                    salesLine.setSubCategory(iteSearchData.getSubCategory());
                    salesLine.setSubCategoryCode(iteSearchData.getSubCategory());
                    salesLine.setSubClassification(iteSearchData.getSubClassification());
                    salesLine.setSubstitudeItemId("");
                    salesLine.setTax(batchListResponse.getBatchList().get(i).getTotalTax());
                    salesLine.setTaxAmount(batchListResponse.getBatchList().get(i).getTotalTax());
                    salesLine.setTotal(batchListResponse.getBatchList().get(i).getMrp());
                    salesLine.setTotalDiscAmount(0.0);
                    salesLine.setTotalDiscPct(0.0);
                    salesLine.setTotalRoundedAmount(0.0);
                    salesLine.setTotalTax(batchListResponse.getBatchList().get(i).getTotalTax());
                    salesLine.setUnit("");
                    salesLine.setUnitPrice(batchListResponse.getBatchList().get(i).getMrp());
                    salesLine.setUnitQty((double) ((int) Float.parseFloat(batchListResponse.getBatchList().get(i).getQOH())));
                    salesLine.setVariantId("");
                    salesLine.setIsReturnClick(false);
                    salesLine.setIsSelectedReturnItem(false);
                    data.setSalesLine(salesLine);

                    data.setMedicineType(iteSearchData.getCategory());
                    dummyDataList.add(data);
                    requiredQty = (int) (requiredQty - Double.parseDouble(batchListResponse.getBatchList().get(i).getQOH()));
                }
            }
        }


//        if (batchListResponse != null && batchListResponse.getBatchList() != null && batchListResponse.getBatchList().size() > 0) {
//            toasCount = toasCount + 1;
//            for (int i = 0; i < batchListResponse.getBatchList().size(); i++) {
//                overallBatchQuantity += Float.parseFloat(batchListResponse.getBatchList().get(i).getQOH());
//            }
//            flag = 0;
//            for (int i = 0; i < batchListResponse.getBatchList().size(); i++) {
//                if (Float.parseFloat(batchListResponse.getBatchList().get(i).getQOH()) >= Float.parseFloat(String.valueOf(imageList.get(position).getQty()))) {
//                    flag = 1;
////                    dataList.get(position).setArtprice(String.valueOf(batchListResponse.getBatchList().get(i).getMrp()));
//
//                    OCRToDigitalMedicineResponse data = new OCRToDigitalMedicineResponse();
//                    data.setArtName(iteSearchData.getDescription());
//                    data.setArtCode(iteSearchData.getArtCode() + "," + batchListResponse.getBatchList().get(i).getBatchNo());
//                    data.setBatchId(batchListResponse.getBatchList().get(i).getBatchNo());
//                    data.setArtprice(String.valueOf(batchListResponse.getBatchList().get(i).getMrp()));
//                    data.setContainer("");
//                    data.setQty(Integer.parseInt(imageList.get(position).getQty()));
//
//                    //calculate pos transaction
//                    CalculatePosTransactionRequest.SalesLine salesLine = new CalculatePosTransactionRequest.SalesLine();
//                    salesLine.setBatchNo(batchListResponse.getBatchList().get(i).getBatchNo());
//                    salesLine.setAdditionaltax(0.0);
//                    salesLine.setApplyDiscount(false);
//                    salesLine.setBarcode("");
//                    salesLine.setBaseAmount(batchListResponse.getBatchList().get(i).getMrp());
//                    salesLine.setCESSPerc(batchListResponse.getBatchList().get(i).getCESSPerc());
//                    salesLine.setCESSTaxCode(batchListResponse.getBatchList().get(i).getCESSTaxCode());
//                    salesLine.setCGSTPerc(batchListResponse.getBatchList().get(i).getCGSTPerc());
//                    salesLine.setCGSTTaxCode(batchListResponse.getBatchList().get(i).getCGSTTaxCode());
//                    salesLine.setCategory(iteSearchData.getCategory());
//                    salesLine.setCategoryCode(iteSearchData.getCategoryCode());
//                    salesLine.setCategoryReference("");
//                    salesLine.setComment("");
//                    salesLine.setDpco(iteSearchData.getDpco());
//                    salesLine.setDiscAmount(0.0);
//                    salesLine.setDiscId("");
//                    salesLine.setDiscOfferId("");
//                    salesLine.setDiscountStructureType(0.0);
//                    salesLine.setDiscountType("");
//                    salesLine.setDiseaseType(iteSearchData.getDiseaseType());
//                    salesLine.setExpiry(batchListResponse.getBatchList().get(i).getExpDate());
//                    salesLine.setHsncodeIn(iteSearchData.getHsncodeIn());
//                    salesLine.setIGSTPerc(batchListResponse.getBatchList().get(i).getIGSTPerc());
//                    salesLine.setIGSTTaxCode(batchListResponse.getBatchList().get(i).getIGSTTaxCode());
//                    salesLine.setISPrescribed(0.0);
//                    salesLine.setISReserved(false);
//                    salesLine.setISStockAvailable(true);
//                    salesLine.setInventBatchId("");//-----0B110081BQ
//                    salesLine.setIsChecked(false);
//                    salesLine.setIsGeneric(iteSearchData.getIsGeneric());
//                    salesLine.setIsPriceOverride(false);
//                    salesLine.setIsSubsitute(false);
//                    salesLine.setIsVoid(false);
//                    salesLine.setItemId(batchListResponse.getBatchList().get(i).getItemID());
//                    salesLine.setItemName(iteSearchData.getDescription());
//                    salesLine.setLineDiscPercentage(0.0);
//                    salesLine.setLineManualDiscountAmount(0.0);
//                    salesLine.setLineManualDiscountPercentage(0.0);
//                    salesLine.setLineNo(1);
//                    salesLine.setLinedscAmount(0.0);
//                    salesLine.setMMGroupId("0");
//                    salesLine.setMrp(batchListResponse.getBatchList().get(i).getMrp());
//                    salesLine.setManufacturerCode(iteSearchData.getManufactureCode());
//                    salesLine.setManufacturerName(iteSearchData.getManufacture());
//                    salesLine.setMixMode(false);
//                    salesLine.setModifyBatchId("");
//                    salesLine.setNetAmount(batchListResponse.getBatchList().get(i).getMrp());
//                    salesLine.setNetAmountInclTax(batchListResponse.getBatchList().get(i).getMrp());
//                    salesLine.setOfferAmount(0.0);
//                    salesLine.setOfferDiscountType(0.0);
//                    salesLine.setOfferQty(0.0);
//                    salesLine.setOfferType(0.0);
//                    salesLine.setOmsLineID(0.0);
//                    salesLine.setOmsLineRECID(0.0);
//                    salesLine.setOrderStatus(0.0);
//                    salesLine.setNetAmountInclTax(0.0);
//                    salesLine.setOriginalPrice(batchListResponse.getBatchList().get(i).getMrp());
//                    salesLine.setPeriodicDiscAmount(0.0);
//                    salesLine.setPhysicalMRP(0.0);
//                    salesLine.setPreviewText("");
//                    salesLine.setPrice(batchListResponse.getBatchList().get(i).getMrp());
//                    salesLine.setProductRecID(iteSearchData.getProductRecID());
//                    salesLine.setQty(Integer.parseInt(imageList.get(position).getQty()));
//                    salesLine.setRemainderDays(0.0);
//                    salesLine.setRemainingQty(0.0);
//                    salesLine.setResqtyflag(false);
//                    salesLine.setRetailCategoryRecID(iteSearchData.getRetailCategoryRecID());
//                    salesLine.setRetailMainCategoryRecID(iteSearchData.getRetailMainCategoryRecID());
//                    salesLine.setRetailSubCategoryRecID(iteSearchData.getRetailSubCategoryRecID());
//                    salesLine.setReturnQty(0.0);
//                    salesLine.setSGSTPerc(batchListResponse.getBatchList().get(i).getSGSTPerc());
//                    salesLine.setSGSTTaxCode(batchListResponse.getBatchList().get(i).getSGSTTaxCode());
//                    salesLine.setScheduleCategory(iteSearchData.getSchCatg());
//                    salesLine.setScheduleCategoryCode(iteSearchData.getSchCatgCode());
//                    salesLine.setStockQty(Double.valueOf(iteSearchData.getStockqty()));
//                    salesLine.setSubCategory(iteSearchData.getSubCategory());
//                    salesLine.setSubCategoryCode(iteSearchData.getSubCategory());
//                    salesLine.setSubClassification(iteSearchData.getSubClassification());
//                    salesLine.setSubstitudeItemId("");
//                    salesLine.setTax(batchListResponse.getBatchList().get(i).getTotalTax());
//                    salesLine.setTaxAmount(batchListResponse.getBatchList().get(i).getTotalTax());
//                    salesLine.setTotal(batchListResponse.getBatchList().get(i).getMrp());
//                    salesLine.setTotalDiscAmount(0.0);
//                    salesLine.setTotalDiscPct(0.0);
//                    salesLine.setTotalRoundedAmount(0.0);
//                    salesLine.setTotalTax(batchListResponse.getBatchList().get(i).getTotalTax());
//                    salesLine.setUnit("");
//                    salesLine.setUnitPrice(batchListResponse.getBatchList().get(i).getMrp());
//                    salesLine.setUnitQty((double) Integer.parseInt(imageList.get(position).getQty()));
//                    salesLine.setVariantId("");
//                    salesLine.setIsReturnClick(false);
//                    salesLine.setIsSelectedReturnItem(false);
//                    data.setSalesLine(salesLine);
//
//
//                    for (OCRToDigitalMedicineResponse proQtyIncorg : SessionManager.INSTANCE.getDataList()) {
//                        if (proQtyIncorg.getArtCode().equals(data.getArtCode())) {
//                            data.setQty(proQtyIncorg.getQty() + Integer.parseInt(imageList.get(position).getQty().toString()));
//                        }
//                    }
//
//                    data.setMedicineType(iteSearchData.getCategory());
//                    dummyDataList.add(data);
//                    break;
//                } else if (overallBatchQuantity >= Integer.parseInt(imageList.get(position).getQty())) {
//                    if (Integer.parseInt(imageList.get(position).getQty()) > 0) {
//                        OCRToDigitalMedicineResponse data = new OCRToDigitalMedicineResponse();
//                        data.setArtName(iteSearchData.getDescription());
//                        data.setArtCode(iteSearchData.getArtCode() + "," + batchListResponse.getBatchList().get(i).getBatchNo());
//                        data.setBatchId(batchListResponse.getBatchList().get(i).getBatchNo());
//                        data.setArtprice(String.valueOf(batchListResponse.getBatchList().get(i).getMrp()));
//                        data.setContainer("");
//                        data.setQty((int) Float.parseFloat(batchListResponse.getBatchList().get(i).getQOH()));
//
//
//                        //calculate pos transaction
//                        CalculatePosTransactionRequest.SalesLine salesLine = new CalculatePosTransactionRequest.SalesLine();
//                        salesLine.setBatchNo(batchListResponse.getBatchList().get(i).getBatchNo());
//                        salesLine.setAdditionaltax(0.0);
//                        salesLine.setApplyDiscount(false);
//                        salesLine.setBarcode("");
//                        salesLine.setBaseAmount(batchListResponse.getBatchList().get(i).getMrp());
//                        salesLine.setCESSPerc(batchListResponse.getBatchList().get(i).getCESSPerc());
//                        salesLine.setCESSTaxCode(batchListResponse.getBatchList().get(i).getCESSTaxCode());
//                        salesLine.setCGSTPerc(batchListResponse.getBatchList().get(i).getCGSTPerc());
//                        salesLine.setCGSTTaxCode(batchListResponse.getBatchList().get(i).getCGSTTaxCode());
//                        salesLine.setCategory(iteSearchData.getCategory());
//                        salesLine.setCategoryCode(iteSearchData.getCategoryCode());
//                        salesLine.setCategoryReference("");
//                        salesLine.setComment("");
//                        salesLine.setDpco(iteSearchData.getDpco());
//                        salesLine.setDiscAmount(0.0);
//                        salesLine.setDiscId("");
//                        salesLine.setDiscOfferId("");
//                        salesLine.setDiscountStructureType(0.0);
//                        salesLine.setDiscountType("");
//                        salesLine.setDiseaseType(iteSearchData.getDiseaseType());
//                        salesLine.setExpiry(batchListResponse.getBatchList().get(i).getExpDate());
//                        salesLine.setHsncodeIn(iteSearchData.getHsncodeIn());
//                        salesLine.setIGSTPerc(batchListResponse.getBatchList().get(i).getIGSTPerc());
//                        salesLine.setIGSTTaxCode(batchListResponse.getBatchList().get(i).getIGSTTaxCode());
//                        salesLine.setISPrescribed(0.0);
//                        salesLine.setISReserved(false);
//                        salesLine.setISStockAvailable(true);
//                        salesLine.setInventBatchId("");//-----0B110081BQ
//                        salesLine.setIsChecked(false);
//                        salesLine.setIsGeneric(iteSearchData.getIsGeneric());
//                        salesLine.setIsPriceOverride(false);
//                        salesLine.setIsSubsitute(false);
//                        salesLine.setIsVoid(false);
//                        salesLine.setItemId(batchListResponse.getBatchList().get(i).getItemID());
//                        salesLine.setItemName(iteSearchData.getDescription());
//                        salesLine.setLineDiscPercentage(0.0);
//                        salesLine.setLineManualDiscountAmount(0.0);
//                        salesLine.setLineManualDiscountPercentage(0.0);
//                        salesLine.setLineNo(1);
//                        salesLine.setLinedscAmount(0.0);
//                        salesLine.setMMGroupId("0");
//                        salesLine.setMrp(batchListResponse.getBatchList().get(i).getMrp());
//                        salesLine.setManufacturerCode(iteSearchData.getManufactureCode());
//                        salesLine.setManufacturerName(iteSearchData.getManufacture());
//                        salesLine.setMixMode(false);
//                        salesLine.setModifyBatchId("");
//                        salesLine.setNetAmount(batchListResponse.getBatchList().get(i).getMrp());
//                        salesLine.setNetAmountInclTax(batchListResponse.getBatchList().get(i).getMrp());
//                        salesLine.setOfferAmount(0.0);
//                        salesLine.setOfferDiscountType(0.0);
//                        salesLine.setOfferQty(0.0);
//                        salesLine.setOfferType(0.0);
//                        salesLine.setOmsLineID(0.0);
//                        salesLine.setOmsLineRECID(0.0);
//                        salesLine.setOrderStatus(0.0);
//                        salesLine.setNetAmountInclTax(0.0);
//                        salesLine.setOriginalPrice(batchListResponse.getBatchList().get(i).getMrp());
//                        salesLine.setPeriodicDiscAmount(0.0);
//                        salesLine.setPhysicalMRP(0.0);
//                        salesLine.setPreviewText("");
//                        salesLine.setPrice(batchListResponse.getBatchList().get(i).getMrp());
//                        salesLine.setProductRecID(iteSearchData.getProductRecID());
//                        salesLine.setQty((int) Float.parseFloat(batchListResponse.getBatchList().get(i).getQOH()));
//                        salesLine.setRemainderDays(0.0);
//                        salesLine.setRemainingQty(0.0);
//                        salesLine.setResqtyflag(false);
//                        salesLine.setRetailCategoryRecID(iteSearchData.getRetailCategoryRecID());
//                        salesLine.setRetailMainCategoryRecID(iteSearchData.getRetailMainCategoryRecID());
//                        salesLine.setRetailSubCategoryRecID(iteSearchData.getRetailSubCategoryRecID());
//                        salesLine.setReturnQty(0.0);
//                        salesLine.setSGSTPerc(batchListResponse.getBatchList().get(i).getSGSTPerc());
//                        salesLine.setSGSTTaxCode(batchListResponse.getBatchList().get(i).getSGSTTaxCode());
//                        salesLine.setScheduleCategory(iteSearchData.getSchCatg());
//                        salesLine.setScheduleCategoryCode(iteSearchData.getSchCatgCode());
//                        salesLine.setStockQty(Double.valueOf(iteSearchData.getStockqty()));
//                        salesLine.setSubCategory(iteSearchData.getSubCategory());
//                        salesLine.setSubCategoryCode(iteSearchData.getSubCategory());
//                        salesLine.setSubClassification(iteSearchData.getSubClassification());
//                        salesLine.setSubstitudeItemId("");
//                        salesLine.setTax(batchListResponse.getBatchList().get(i).getTotalTax());
//                        salesLine.setTaxAmount(batchListResponse.getBatchList().get(i).getTotalTax());
//                        salesLine.setTotal(batchListResponse.getBatchList().get(i).getMrp());
//                        salesLine.setTotalDiscAmount(0.0);
//                        salesLine.setTotalDiscPct(0.0);
//                        salesLine.setTotalRoundedAmount(0.0);
//                        salesLine.setTotalTax(batchListResponse.getBatchList().get(i).getTotalTax());
//                        salesLine.setUnit("");
//                        salesLine.setUnitPrice(batchListResponse.getBatchList().get(i).getMrp());
//                        salesLine.setUnitQty((double) ((int) Float.parseFloat(batchListResponse.getBatchList().get(i).getQOH())));
//                        salesLine.setVariantId("");
//                        salesLine.setIsReturnClick(false);
//                        salesLine.setIsSelectedReturnItem(false);
//                        data.setSalesLine(salesLine);
//
//
//                        requiredBalanceQty = Integer.parseInt(imageList.get(position).getQty()) - 1;
//                        imageList.get(position).setQty(String.valueOf(requiredBalanceQty));
//                        for (OCRToDigitalMedicineResponse proQtyIncorg : SessionManager.INSTANCE.getDataList()) {
//                            if (proQtyIncorg.getArtCode().equals(data.getArtCode())) {
//                                data.setQty(proQtyIncorg.getQty() + Integer.parseInt(imageList.get(position).getQty().toString()));
//                            }
//                        }
//                        data.setMedicineType(iteSearchData.getCategory());
//                        dummyDataList.add(data);
//                    }
//                }
//            }
//
//            if (flag == 0) {
////                dataList.get(position).setOutOfStock(true);
//
//                OCRToDigitalMedicineResponse data = new OCRToDigitalMedicineResponse();
//                data.setArtName(iteSearchData.getDescription());
//                data.setArtCode(iteSearchData.getArtCode() + "," + batchListResponse.getBatchList().get(position).getBatchNo());
//                data.setBatchId(batchListResponse.getBatchList().get(position).getBatchNo());
//                data.setArtprice(String.valueOf(batchListResponse.getBatchList().get(position).getMrp()));
//                data.setContainer("");
//                data.setQty((int) Float.parseFloat(batchListResponse.getBatchList().get(position).getQOH()));
//                imageList.get(position).setOutOfStock(true);
//                data.setMedicineType(iteSearchData.getCategory());
//                requiredBalanceQty = Integer.parseInt(imageList.get(position).getQty()) - 1;
//                imageList.get(position).setQty(String.valueOf(requiredBalanceQty));
//
//                int i = position;
//
//                //calculate pos transaction
//                CalculatePosTransactionRequest.SalesLine salesLine = new CalculatePosTransactionRequest.SalesLine();
//                salesLine.setBatchNo(batchListResponse.getBatchList().get(i).getBatchNo());
//                salesLine.setAdditionaltax(0.0);
//                salesLine.setApplyDiscount(false);
//                salesLine.setBarcode("");
//                salesLine.setBaseAmount(batchListResponse.getBatchList().get(i).getMrp());
//                salesLine.setCESSPerc(batchListResponse.getBatchList().get(i).getCESSPerc());
//                salesLine.setCESSTaxCode(batchListResponse.getBatchList().get(i).getCESSTaxCode());
//                salesLine.setCGSTPerc(batchListResponse.getBatchList().get(i).getCGSTPerc());
//                salesLine.setCGSTTaxCode(batchListResponse.getBatchList().get(i).getCGSTTaxCode());
//                salesLine.setCategory(iteSearchData.getCategory());
//                salesLine.setCategoryCode(iteSearchData.getCategoryCode());
//                salesLine.setCategoryReference("");
//                salesLine.setComment("");
//                salesLine.setDpco(iteSearchData.getDpco());
//                salesLine.setDiscAmount(0.0);
//                salesLine.setDiscId("");
//                salesLine.setDiscOfferId("");
//                salesLine.setDiscountStructureType(0.0);
//                salesLine.setDiscountType("");
//                salesLine.setDiseaseType(iteSearchData.getDiseaseType());
//                salesLine.setExpiry(batchListResponse.getBatchList().get(i).getExpDate());
//                salesLine.setHsncodeIn(iteSearchData.getHsncodeIn());
//                salesLine.setIGSTPerc(batchListResponse.getBatchList().get(i).getIGSTPerc());
//                salesLine.setIGSTTaxCode(batchListResponse.getBatchList().get(i).getIGSTTaxCode());
//                salesLine.setISPrescribed(0.0);
//                salesLine.setISReserved(false);
//                salesLine.setISStockAvailable(true);
//                salesLine.setInventBatchId("");//-----0B110081BQ
//                salesLine.setIsChecked(false);
//                salesLine.setIsGeneric(iteSearchData.getIsGeneric());
//                salesLine.setIsPriceOverride(false);
//                salesLine.setIsSubsitute(false);
//                salesLine.setIsVoid(false);
//                salesLine.setItemId(batchListResponse.getBatchList().get(i).getItemID());
//                salesLine.setItemName(iteSearchData.getDescription());
//                salesLine.setLineDiscPercentage(0.0);
//                salesLine.setLineManualDiscountAmount(0.0);
//                salesLine.setLineManualDiscountPercentage(0.0);
//                salesLine.setLineNo(1);
//                salesLine.setLinedscAmount(0.0);
//                salesLine.setMMGroupId("0");
//                salesLine.setMrp(batchListResponse.getBatchList().get(i).getMrp());
//                salesLine.setManufacturerCode(iteSearchData.getManufactureCode());
//                salesLine.setManufacturerName(iteSearchData.getManufacture());
//                salesLine.setMixMode(false);
//                salesLine.setModifyBatchId("");
//                salesLine.setNetAmount(batchListResponse.getBatchList().get(i).getMrp());
//                salesLine.setNetAmountInclTax(batchListResponse.getBatchList().get(i).getMrp());
//                salesLine.setOfferAmount(0.0);
//                salesLine.setOfferDiscountType(0.0);
//                salesLine.setOfferQty(0.0);
//                salesLine.setOfferType(0.0);
//                salesLine.setOmsLineID(0.0);
//                salesLine.setOmsLineRECID(0.0);
//                salesLine.setOrderStatus(0.0);
//                salesLine.setNetAmountInclTax(0.0);
//                salesLine.setOriginalPrice(batchListResponse.getBatchList().get(i).getMrp());
//                salesLine.setPeriodicDiscAmount(0.0);
//                salesLine.setPhysicalMRP(0.0);
//                salesLine.setPreviewText("");
//                salesLine.setPrice(batchListResponse.getBatchList().get(i).getMrp());
//                salesLine.setProductRecID(iteSearchData.getProductRecID());
//                salesLine.setQty((int) Float.parseFloat(batchListResponse.getBatchList().get(i).getQOH()));
//                salesLine.setRemainderDays(0.0);
//                salesLine.setRemainingQty(0.0);
//                salesLine.setResqtyflag(false);
//                salesLine.setRetailCategoryRecID(iteSearchData.getRetailCategoryRecID());
//                salesLine.setRetailMainCategoryRecID(iteSearchData.getRetailMainCategoryRecID());
//                salesLine.setRetailSubCategoryRecID(iteSearchData.getRetailSubCategoryRecID());
//                salesLine.setReturnQty(0.0);
//                salesLine.setSGSTPerc(batchListResponse.getBatchList().get(i).getSGSTPerc());
//                salesLine.setSGSTTaxCode(batchListResponse.getBatchList().get(i).getSGSTTaxCode());
//                salesLine.setScheduleCategory(iteSearchData.getSchCatg());
//                salesLine.setScheduleCategoryCode(iteSearchData.getSchCatgCode());
//                salesLine.setStockQty(Double.valueOf(iteSearchData.getStockqty()));
//                salesLine.setSubCategory(iteSearchData.getSubCategory());
//                salesLine.setSubCategoryCode(iteSearchData.getSubCategory());
//                salesLine.setSubClassification(iteSearchData.getSubClassification());
//                salesLine.setSubstitudeItemId("");
//                salesLine.setTax(batchListResponse.getBatchList().get(i).getTotalTax());
//                salesLine.setTaxAmount(batchListResponse.getBatchList().get(i).getTotalTax());
//                salesLine.setTotal(batchListResponse.getBatchList().get(i).getMrp());
//                salesLine.setTotalDiscAmount(0.0);
//                salesLine.setTotalDiscPct(0.0);
//                salesLine.setTotalRoundedAmount(0.0);
//                salesLine.setTotalTax(batchListResponse.getBatchList().get(i).getTotalTax());
//                salesLine.setUnit("");
//                salesLine.setUnitPrice(batchListResponse.getBatchList().get(i).getMrp());
//                salesLine.setUnitQty((double) ((int) Float.parseFloat(batchListResponse.getBatchList().get(i).getQOH())));
//                salesLine.setVariantId("");
//                salesLine.setIsReturnClick(false);
//                salesLine.setIsSelectedReturnItem(false);
//                data.setSalesLine(salesLine);
//
//
//                for (OCRToDigitalMedicineResponse proQtyIncorg : SessionManager.INSTANCE.getDataList()) {
//                    if (proQtyIncorg.getArtCode().equals(data.getArtCode())) {
//                        data.setQty(proQtyIncorg.getQty() + Integer.parseInt(imageList.get(position).getQty().toString()));
//                    }
//                }
//                dummyDataList.add(data);
//
//
//            }
//        } else {
////            Toast.makeText(getContext(), "Product Out Of Stock", Toast.LENGTH_LONG).show();
////            dataList.get(position).setOutOfStock(true);
//
//            OCRToDigitalMedicineResponse data = new OCRToDigitalMedicineResponse();
//            data.setArtName(iteSearchData.getDescription());
//            data.setArtCode(iteSearchData.getArtCode() + "," + batchListResponse.getBatchList().get(position).getBatchNo());
//            data.setBatchId(batchListResponse.getBatchList().get(position).getBatchNo());
//            data.setArtprice(String.valueOf(batchListResponse.getBatchList().get(position).getMrp()));
//            data.setContainer("");
//            data.setQty((int) Float.parseFloat(batchListResponse.getBatchList().get(position).getQOH()));
//            imageList.get(position).setOutOfStock(true);
//            data.setMedicineType(iteSearchData.getCategory());
//            requiredBalanceQty = Integer.parseInt(imageList.get(position).getQty()) - 1;
//            imageList.get(position).setQty(String.valueOf(requiredBalanceQty));
//
//            int i = position;
//
//            //calculate pos transaction
//            CalculatePosTransactionRequest.SalesLine salesLine = new CalculatePosTransactionRequest.SalesLine();
//            salesLine.setBatchNo(batchListResponse.getBatchList().get(i).getBatchNo());
//            salesLine.setAdditionaltax(0.0);
//            salesLine.setApplyDiscount(false);
//            salesLine.setBarcode("");
//            salesLine.setBaseAmount(batchListResponse.getBatchList().get(i).getMrp());
//            salesLine.setCESSPerc(batchListResponse.getBatchList().get(i).getCESSPerc());
//            salesLine.setCESSTaxCode(batchListResponse.getBatchList().get(i).getCESSTaxCode());
//            salesLine.setCGSTPerc(batchListResponse.getBatchList().get(i).getCGSTPerc());
//            salesLine.setCGSTTaxCode(batchListResponse.getBatchList().get(i).getCGSTTaxCode());
//            salesLine.setCategory(iteSearchData.getCategory());
//            salesLine.setCategoryCode(iteSearchData.getCategoryCode());
//            salesLine.setCategoryReference("");
//            salesLine.setComment("");
//            salesLine.setDpco(iteSearchData.getDpco());
//            salesLine.setDiscAmount(0.0);
//            salesLine.setDiscId("");
//            salesLine.setDiscOfferId("");
//            salesLine.setDiscountStructureType(0.0);
//            salesLine.setDiscountType("");
//            salesLine.setDiseaseType(iteSearchData.getDiseaseType());
//            salesLine.setExpiry(batchListResponse.getBatchList().get(i).getExpDate());
//            salesLine.setHsncodeIn(iteSearchData.getHsncodeIn());
//            salesLine.setIGSTPerc(batchListResponse.getBatchList().get(i).getIGSTPerc());
//            salesLine.setIGSTTaxCode(batchListResponse.getBatchList().get(i).getIGSTTaxCode());
//            salesLine.setISPrescribed(0.0);
//            salesLine.setISReserved(false);
//            salesLine.setISStockAvailable(true);
//            salesLine.setInventBatchId("");//-----0B110081BQ
//            salesLine.setIsChecked(false);
//            salesLine.setIsGeneric(iteSearchData.getIsGeneric());
//            salesLine.setIsPriceOverride(false);
//            salesLine.setIsSubsitute(false);
//            salesLine.setIsVoid(false);
//            salesLine.setItemId(batchListResponse.getBatchList().get(i).getItemID());
//            salesLine.setItemName(iteSearchData.getDescription());
//            salesLine.setLineDiscPercentage(0.0);
//            salesLine.setLineManualDiscountAmount(0.0);
//            salesLine.setLineManualDiscountPercentage(0.0);
//            salesLine.setLineNo(1);
//            salesLine.setLinedscAmount(0.0);
//            salesLine.setMMGroupId("0");
//            salesLine.setMrp(batchListResponse.getBatchList().get(i).getMrp());
//            salesLine.setManufacturerCode(iteSearchData.getManufactureCode());
//            salesLine.setManufacturerName(iteSearchData.getManufacture());
//            salesLine.setMixMode(false);
//            salesLine.setModifyBatchId("");
//            salesLine.setNetAmount(batchListResponse.getBatchList().get(i).getMrp());
//            salesLine.setNetAmountInclTax(batchListResponse.getBatchList().get(i).getMrp());
//            salesLine.setOfferAmount(0.0);
//            salesLine.setOfferDiscountType(0.0);
//            salesLine.setOfferQty(0.0);
//            salesLine.setOfferType(0.0);
//            salesLine.setOmsLineID(0.0);
//            salesLine.setOmsLineRECID(0.0);
//            salesLine.setOrderStatus(0.0);
//            salesLine.setNetAmountInclTax(0.0);
//            salesLine.setOriginalPrice(batchListResponse.getBatchList().get(i).getMrp());
//            salesLine.setPeriodicDiscAmount(0.0);
//            salesLine.setPhysicalMRP(0.0);
//            salesLine.setPreviewText("");
//            salesLine.setPrice(batchListResponse.getBatchList().get(i).getMrp());
//            salesLine.setProductRecID(iteSearchData.getProductRecID());
//            salesLine.setQty((int) Float.parseFloat(batchListResponse.getBatchList().get(i).getQOH()));
//            salesLine.setRemainderDays(0.0);
//            salesLine.setRemainingQty(0.0);
//            salesLine.setResqtyflag(false);
//            salesLine.setRetailCategoryRecID(iteSearchData.getRetailCategoryRecID());
//            salesLine.setRetailMainCategoryRecID(iteSearchData.getRetailMainCategoryRecID());
//            salesLine.setRetailSubCategoryRecID(iteSearchData.getRetailSubCategoryRecID());
//            salesLine.setReturnQty(0.0);
//            salesLine.setSGSTPerc(batchListResponse.getBatchList().get(i).getSGSTPerc());
//            salesLine.setSGSTTaxCode(batchListResponse.getBatchList().get(i).getSGSTTaxCode());
//            salesLine.setScheduleCategory(iteSearchData.getSchCatg());
//            salesLine.setScheduleCategoryCode(iteSearchData.getSchCatgCode());
//            salesLine.setStockQty(Double.valueOf(iteSearchData.getStockqty()));
//            salesLine.setSubCategory(iteSearchData.getSubCategory());
//            salesLine.setSubCategoryCode(iteSearchData.getSubCategory());
//            salesLine.setSubClassification(iteSearchData.getSubClassification());
//            salesLine.setSubstitudeItemId("");
//            salesLine.setTax(batchListResponse.getBatchList().get(i).getTotalTax());
//            salesLine.setTaxAmount(batchListResponse.getBatchList().get(i).getTotalTax());
//            salesLine.setTotal(batchListResponse.getBatchList().get(i).getMrp());
//            salesLine.setTotalDiscAmount(0.0);
//            salesLine.setTotalDiscPct(0.0);
//            salesLine.setTotalRoundedAmount(0.0);
//            salesLine.setTotalTax(batchListResponse.getBatchList().get(i).getTotalTax());
//            salesLine.setUnit("");
//            salesLine.setUnitPrice(batchListResponse.getBatchList().get(i).getMrp());
//            salesLine.setUnitQty((double) ((int) Float.parseFloat(batchListResponse.getBatchList().get(i).getQOH())));
//            salesLine.setVariantId("");
//            salesLine.setIsReturnClick(false);
//            salesLine.setIsSelectedReturnItem(false);
//            data.setSalesLine(salesLine);
//
//
//            for (OCRToDigitalMedicineResponse proQtyIncorg : SessionManager.INSTANCE.getDataList()) {
//                if (proQtyIncorg.getArtCode().equals(data.getArtCode())) {
//                    data.setQty(proQtyIncorg.getQty() + Integer.parseInt(imageList.get(position).getQty().toString()));
//                }
//            }
//            dummyDataList.add(data);
//        }

        CalculatePosTransactionRequest calculatePosTransactionRequest = new CalculatePosTransactionRequest();
        calculatePosTransactionRequest.setAmounttoAccount(0);
        calculatePosTransactionRequest.setBatchTerminalid("");
        calculatePosTransactionRequest.setBillingCity("");
        calculatePosTransactionRequest.setBusinessDate(Utils.getCurrentDate(Constants.DATE_FORMAT_DD_MMM_YYYY));
        calculatePosTransactionRequest.setCancelReasonCode("");
        calculatePosTransactionRequest.setChannel("");
        calculatePosTransactionRequest.setComment("");
        calculatePosTransactionRequest.setCorpCode("0");
        calculatePosTransactionRequest.setCouponCode("");
        calculatePosTransactionRequest.setCreatedonPosTerminal(SessionManager.INSTANCE.getTerminalId());
        calculatePosTransactionRequest.setCurrency("INR");
        calculatePosTransactionRequest.setCurrentSalesLine(0);
        calculatePosTransactionRequest.setCustAccount("");
        calculatePosTransactionRequest.setCustAddress("");
        calculatePosTransactionRequest.setCustDiscamount(0);
        calculatePosTransactionRequest.setCustomerName(SessionManager.INSTANCE.getCustrName());
        calculatePosTransactionRequest.setCustomerState("");
        calculatePosTransactionRequest.setDob("");
        calculatePosTransactionRequest.setDataAreaId(SessionManager.INSTANCE.getDataAreaId());
        calculatePosTransactionRequest.setDeliveryDate("");
        calculatePosTransactionRequest.setDiscAmount(0);
        calculatePosTransactionRequest.setDoctorCode("");
        calculatePosTransactionRequest.setDoctorName("");
        calculatePosTransactionRequest.setEntryStatus(0);
        calculatePosTransactionRequest.setGender(0);
        calculatePosTransactionRequest.setGrossAmount(0.0);
        calculatePosTransactionRequest.setIpno("");
        calculatePosTransactionRequest.setIPSerialNO("");
        calculatePosTransactionRequest.setISAdvancePayment(true);
        calculatePosTransactionRequest.setISBatchModifiedAllowed(false);
        calculatePosTransactionRequest.setISOMSOrder(false);
        calculatePosTransactionRequest.setISPosted(false);
        calculatePosTransactionRequest.setISPrescibeDiscount(false);
        calculatePosTransactionRequest.setISReserved(false);
        calculatePosTransactionRequest.setISReturnAllowed(false);
        calculatePosTransactionRequest.setIsManualBill(false);
        calculatePosTransactionRequest.setIsReturn(false);
        calculatePosTransactionRequest.setIsStockCheck(true);
        calculatePosTransactionRequest.setIsVoid(false);
        calculatePosTransactionRequest.setMobileNO(SessionManager.INSTANCE.getMobilenumber());
        calculatePosTransactionRequest.setNetAmount(0.0);
        calculatePosTransactionRequest.setNetAmountInclTax(0.0);
        calculatePosTransactionRequest.setNumberofItemLines(1);
        calculatePosTransactionRequest.setNumberofItems(1);
        calculatePosTransactionRequest.setOrderSource("");
        calculatePosTransactionRequest.setOrderType("");
        calculatePosTransactionRequest.setPaymentSource("");
        calculatePosTransactionRequest.setPincode("");
        calculatePosTransactionRequest.setPosEvent(0);
        calculatePosTransactionRequest.setRefno("");
        calculatePosTransactionRequest.setReciptId("");
        calculatePosTransactionRequest.setRegionCode("");
        calculatePosTransactionRequest.setRemainingamount(0.0);
        calculatePosTransactionRequest.setReminderDays(0);
        calculatePosTransactionRequest.setRequestStatus(0);
        calculatePosTransactionRequest.setReturnMessage("");
        calculatePosTransactionRequest.setReturnReceiptId("");
        calculatePosTransactionRequest.setReturnStore("");
        calculatePosTransactionRequest.setReturnTerminal("");
        calculatePosTransactionRequest.setReturnTransactionId("");
        calculatePosTransactionRequest.setReturnType(0);
        calculatePosTransactionRequest.setRoundedAmount(0);
        calculatePosTransactionRequest.setSez(0);

        List<CalculatePosTransactionRequest.SalesLine> salesLineList = new ArrayList<>();
        if (dummyDataList != null && dummyDataList.size() > 0) {
            for (int i = 0; i < dummyDataList.size(); i++) {
                salesLineList.add(dummyDataList.get(i).getSalesLine());
            }
        }
        calculatePosTransactionRequest.setSalesLine(salesLineList);

        calculatePosTransactionRequest.setSalesOrigin("0");
        calculatePosTransactionRequest.setShippingMethod("");
        calculatePosTransactionRequest.setStaff("");
        calculatePosTransactionRequest.setState("AP");
        calculatePosTransactionRequest.setStore(SessionManager.INSTANCE.getStoreId());
        calculatePosTransactionRequest.setStoreName("POS TESTING");
        calculatePosTransactionRequest.setTenderLine(null);
        calculatePosTransactionRequest.setTerminal(SessionManager.INSTANCE.getTerminalId());
        calculatePosTransactionRequest.setTimewhenTransClosed(0);
        calculatePosTransactionRequest.setTotalDiscAmount(0);
        calculatePosTransactionRequest.setTotalMRP(0.0);
        calculatePosTransactionRequest.setTotalManualDiscountAmount(0);
        calculatePosTransactionRequest.setTotalManualDiscountPercentage(0);
        calculatePosTransactionRequest.setTotalTaxAmount(0.0);
        calculatePosTransactionRequest.setTrackingRef("");
        calculatePosTransactionRequest.setTransDate(Utils.getCurrentDate(Constants.DATE_FORMAT_DD_MMM_YYYY));
        calculatePosTransactionRequest.setTransType(0);
        calculatePosTransactionRequest.setTransactionId("");
        calculatePosTransactionRequest.setType(2);
        calculatePosTransactionRequest.setVendorId("");
        calculatePosTransactionRequest.setOMSCreditAmount(0.0);
        calculatePosTransactionRequest.setShippingCharges(0.0);
        k++;
        if (k == imageList.size()) {
            k = 0;
            dummyDataList.clear();
            if (calculatePosTransactionRequest.getSalesLine() != null && calculatePosTransactionRequest.getSalesLine().size() > 0) {
                new MyOffersController(this, this).calculatePosTransaction(calculatePosTransactionRequest);
            } else {
                Utils.showSnackbar(MyOffersActivity.this, constraintLayout, "Product Not Available.");
            }
        }

//        if (null != SessionManager.INSTANCE.getDataList()) {
//            if (SessionManager.INSTANCE.getDataList().size() > 0) {
//                List<OCRToDigitalMedicineResponse> tempCartItemList = new ArrayList<>();
//                tempCartItemList = SessionManager.INSTANCE.getDataList();
//                for (OCRToDigitalMedicineResponse listItem : tempCartItemList) {
//                    boolean isItemEqual = false;
//                    for (OCRToDigitalMedicineResponse duplecateItem : dummyDataList) {
//                        if (duplecateItem.getArtCode().equals(listItem.getArtCode())) {
//                            isItemEqual = true;
//                        }
//                    }
//                    if (!isItemEqual)
//                        dummyDataList.add(listItem);
//                }
//            }
//        }
//
//        for (int i = 0; i < dummyDataList.size(); i++) {
//            for (int j = 0; j < dummyDataList.size(); j++) {
//                if (i != j && dummyDataList.get(i).getArtCode().equals(dummyDataList.get(j).getArtCode())) {
////                    dummyDataList.get(j).setQty(dummyDataList.get(i).getQty()+dummyDataList.get(j).getQty());
//                    dummyDataList.set(i, dummyDataList.get(j));
//                    dummyDataList.remove(j);
//                    j--;
//                }
//            }
//        }
//        SessionManager.INSTANCE.setDataList(dummyDataList);
//        Intent intent = new Intent("OrderhistoryCardReciver");
//        intent.putExtra("message", "OrderNow");
//        intent.putExtra("MedininesNames", new Gson().toJson(dummyDataList));
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    public List<OCRToDigitalMedicineResponse> dataListLatest() {
        for (int i = 0; i < dummyDataList.size(); i++) {
            if (dummyDataList.get(i).isOutOfStock()) {
                dummyDataList.remove(dummyDataList.get(i));
                i--;
            }
        }
        return dummyDataList;
    }

    @Override
    public void onSuccessSearchItemApi(ItemSearchResponse itemSearchResponse, int position) {
        if (itemSearchResponse != null && itemSearchResponse.getItemList() != null && itemSearchResponse.getItemList().size() > 0) {
            for (int i = 0; i < itemSearchResponse.getItemList().size(); i++) {
                imageList.get(position).setMedicineType(String.valueOf(itemSearchResponse.getItemList().get(i).getCategory()));
                imageList.get(position).setArtName(String.valueOf(itemSearchResponse.getItemList().get(i).getDescription()));
            }
        } else {
            imageList.get(position).setOutOfStock(true);
        }
    }

    @Override
    public void onSuccessCalculatePosTransactionApi(CalculatePosTransactionResponse calculatePosTransactionResponse) {
        List<OCRToDigitalMedicineResponse> dummyDataListdummy = new ArrayList<>();
        if (calculatePosTransactionResponse != null && calculatePosTransactionResponse.getSalesLine() != null && calculatePosTransactionResponse.getSalesLine().size() > 0) {
            for (int i = 0; i < calculatePosTransactionResponse.getSalesLine().size(); i++) {
                OCRToDigitalMedicineResponse data = new OCRToDigitalMedicineResponse();
                data.setArtName(calculatePosTransactionResponse.getSalesLine().get(i).getItemName());
                data.setArtCode(calculatePosTransactionResponse.getSalesLine().get(i).getItemId() + "," + calculatePosTransactionResponse.getSalesLine().get(i).getBatchNo());
                data.setBatchId(calculatePosTransactionResponse.getSalesLine().get(i).getBatchNo());
                data.setArtprice(String.valueOf(calculatePosTransactionResponse.getSalesLine().get(i).getMrp()));
                data.setContainer("");
                data.setMedicineType(calculatePosTransactionResponse.getSalesLine().get(i).getCategory());
                data.setNetAmountInclTax((calculatePosTransactionResponse.getSalesLine().get(i).getBaseAmount().equals(calculatePosTransactionResponse.getSalesLine().get(i).getNetAmountInclTax())) ? null : String.valueOf(calculatePosTransactionResponse.getSalesLine().get(i).getNetAmountInclTax()));
                data.setQty(calculatePosTransactionResponse.getSalesLine().get(i).getQty());
                dummyDataListdummy.add(data);
            }
        }
        List<OCRToDigitalMedicineResponse> temporaryDataList = new ArrayList<>();
        if (SessionManager.INSTANCE.getDataList() != null && SessionManager.INSTANCE.getDataList().size() > 0) {
            temporaryDataList = SessionManager.INSTANCE.getDataList();
            for (int i = 0; i < temporaryDataList.size(); i++) {
                for (int j = 0; j < dummyDataListdummy.size(); j++) {
                    if (temporaryDataList.get(i).getArtCode().equals(dummyDataListdummy.get(j).getArtCode())) {
                        if (dummyDataListdummy.get(j).getNetAmountInclTax() != null)
                            temporaryDataList.get(i).setNetAmountInclTax(String.valueOf(Double.parseDouble(temporaryDataList.get(i).getNetAmountInclTax()) + Double.parseDouble(dummyDataListdummy.get(j).getNetAmountInclTax())));
                        temporaryDataList.get(i).setQty(temporaryDataList.get(i).getQty() + dummyDataListdummy.get(j).getQty());
                        dummyDataListdummy.remove(j);
                        j--;
                    }
                }
            }
            temporaryDataList.addAll(dummyDataListdummy);
            SessionManager.INSTANCE.setDataList(temporaryDataList);
            Intent intent = new Intent("OrderhistoryCardReciver");
            intent.putExtra("message", "OrderNow");
            intent.putExtra("MedininesNames", new Gson().toJson(temporaryDataList));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else {
            temporaryDataList.addAll(dummyDataListdummy);
            SessionManager.INSTANCE.setDataList(temporaryDataList);
            Intent intent = new Intent("OrderhistoryCardReciver");
            intent.putExtra("message", "OrderNow");
            intent.putExtra("MedininesNames", new Gson().toJson(temporaryDataList));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    @Override
    public void onSendSmsSuccess() {
        Utils.dismissDialog();
        if(!isResend){
            newLoginScreenBinding.mobileNumLoginPopup.setVisibility(View.GONE);
            newLoginScreenBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//        entered_mobile_number.setText(mobileNum)
            SessionManager.INSTANCE.setMobilenumber(mobileNum);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        else{
            newLoginScreenBinding.resendButtonNewLogin.setVisibility(View.GONE);
            newLoginScreenBinding.sendCodeinText.setVisibility(View.VISIBLE);
            newLoginScreenBinding.timerNewlogin.setVisibility(View.VISIBLE);
            cancelTimer();
            startTimer();
        }
    }

    @Override
    public void onSendSmsFailure() {
        Utils.dismissDialog();
        Utils.showCustomAlertDialog(this, "We are unable to process your request right now, Please try again later.", false, "OK", "");
    }


    OfferSelectionDialog offerSelectionDialog;

    private void offerSelectionDialogCcall(AllOffersResponse.Datum offer, List<AllOffersResponse.PromoItem> image) {
        this.offer = offer;
        this.imageList = image;
        offerSelectionDialog = new OfferSelectionDialog(this, offer, image, this, this);
        offerSelectionDialog.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onContinueOfSelectedOffers();
            }
        });
        offerSelectionDialog.setNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offerSelectionDialog.dismiss();
            }
        });
        offerSelectionDialog.show();
    }

    @Override
    public void onSearchFailure(String error) {
        Utils.showCustomAlertDialog(MyOffersActivity.this, error, false, getApplicationContext().getResources().getString(R.string.label_ok), "");
    }

    public void imageclicked_method(ProductSearch productsku) {

    }

    @Override
    public void onSuccessProductList(HashMap<String, GetProductListResponse> productList) {
//        product_list_array.clear();
//        product_list_array1.clear();
//        if (null != myOffersController.getSpecialOffersCategoryList() && myOffersController.getSpecialOffersCategoryList().size() > 0) {
//            for (String s : myOffersController.getSpecialOffersCategoryList()) {
//                if (myOffersText.equalsIgnoreCase("SpecialOffers")) {
//                    if (productList.get(s) != null) {
//                        totalPage = (productList.get(s).getProductCount() / 20);
//                        if (productList.get(s).getProductCount() % 3 != 0) {
//                            totalPage++;
//                        }
//                        ArrayList<Product> products = (ArrayList<Product>) productList.get(s).getProducts();
//                        for (Product d : products) {
//                            Product load_obj = new Product();
//                            load_obj.setName(d.getName());
//                            load_obj.setSku(d.getSku());
//                            load_obj.setPrice(d.getPrice());
//                            load_obj.setImage(d.getImage());
//                            load_obj.setSpecialPrice(d.getSpecialPrice());
//                            load_obj.setProduct_pricetype("offer");
//                            load_obj.setIsInStock(d.getIsInStock());
//                            load_obj.setMou(d.getMou());
//                            load_obj.setId(d.getId());
//                            if (d.getIsInStock() == 1) {
//                                stockin_array.add(load_obj);
//                            } else {
//                                outofstock_array.add(load_obj);
//                            }
//                        }
//                        product_list_array.addAll(stockin_array);
//                        if (null != product_list_array && product_list_array.size() > 0) {
//                            total_itemcount_textview.setVisibility(View.GONE);
//                            total_itemcount_textview.setText(product_list_array.size() + " " + getApplicationContext().getResources().getString(R.string.label_items));
//                            fcItemCountTxt.setText(String.valueOf(product_list_array.size()));
//                        } else {
//                            total_itemcount_textview.setVisibility(View.GONE);
//                            total_itemcount_textview.setText("0" + " " + getApplicationContext().getResources().getString(R.string.label_items));
//                            fcItemCountTxt.setText("0");
//                        }
//                        if (totalPage > currentPage) {
//                            product_list_array.remove(0);
//                        }
//                        promotionList = new MyOffersAdapter(this, product_list_array, product_list_array, this);
//                        promotionList.setViewMode(ViewMode.GRID);
//                        trendingNowRecyclerView.setVisibility(View.GONE);
////                        promotionsRecyclerView.setAdapter(promotionList);
//                        promotionsRecyclerView.setVisibility(View.VISIBLE);
//                        promotionList.notifyDataSetChanged();
//                        promotionList.setLoadMoreListener(() -> promotionsRecyclerView.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                currentPage++;
//                                if (totalPage >= currentPage) {
//                                    loadMoreOffers();
//                                }
//                            }
//                        }));
//                    }
//                } else {
//                    if (productList.get(s) != null) {
//                        if (productList.get(s).getProductCount() > 0) {
//                            totalPage1 = (productList.get(s).getProductCount() / 20);
//                            if (productList.get(s).getProductCount() % 3 != 0) {
//                                totalPage1++;
//                            }
//                            ArrayList<Product> products = (ArrayList<Product>) productList.get(s).getProducts();
//                            for (Product d : products) {
//                                Product load_obj = new Product();
//                                load_obj.setName(d.getName());
//                                load_obj.setSku(d.getSku());
//                                load_obj.setPrice(d.getPrice());
//                                load_obj.setImage(d.getImage());
//                                load_obj.setSpecialPrice(d.getSpecialPrice());
//                                load_obj.setProduct_pricetype("trendingnow");
//                                load_obj.setIsInStock(d.getIsInStock());
//                                load_obj.setMou(d.getMou());
//                                load_obj.setId(d.getId());
//                                if (d.getIsInStock() == 1) {
//                                    stockin_array1.add(load_obj);
//                                } else {
//                                    outofstock_array1.add(load_obj);
//                                }
//                            }
//                            product_list_array1.addAll(stockin_array1);
//                            if (null != product_list_array1 && product_list_array1.size() > 0) {
//                                total_itemcount_textview.setVisibility(View.GONE);
//                                total_itemcount_textview.setText(product_list_array1.size() + " " + getApplicationContext().getResources().getString(R.string.label_items));
//                                pharmaItemCountTxt.setText(String.valueOf(product_list_array1.size()));
//                            } else {
//                                total_itemcount_textview.setVisibility(View.GONE);
//                                total_itemcount_textview.setText("0" + " " + getApplicationContext().getResources().getString(R.string.label_items));
//                                pharmaItemCountTxt.setText("0");
//                            }
//                            if (totalPage1 > currentPage1) {
//                                product_list_array1.remove(0);
//                            }
//                            trendingNowList = new MyOffersAdapter(this, product_list_array1, product_list_array1, this);
//                            trendingNowList.setViewMode(ViewMode.GRID);
//                            promotionsRecyclerView.setVisibility(View.GONE);
////                            trendingNowRecyclerView.setAdapter(trendingNowList);
//                            trendingNowRecyclerView.setVisibility(View.VISIBLE);
//                            trendingNowList.notifyDataSetChanged();
//                            trendingNowList.setLoadMoreListener(() -> trendingNowRecyclerView.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    currentPage1++;
//                                    if (totalPage1 >= currentPage1) {
//                                        loadMore_tendingnow();
//                                    }
//                                }
//                            }));
//                        }
//                    }
//                }
//            }
//        }
    }

    @Override
    public void onSuccessProductListMainCategory
            (HashMap<String, GetProductListResponse> productList) {
    }

    @Override
    public void onFailure(String error) {
//        Utils.showCustomAlertDialog(MyOffersActivity.this, error, false, "OK", "");
    }

    private void loadMore_tendingnow() {
        product_list_array1.add(new Product("load"));
        trendingNowList.notifyItemInserted(product_list_array1.size() - 1);
        int cId = myOffersController.getHealthWellnessCategoryId("TrendingNow");
        myOffersController.handleLoadMoreTrendingNowService(cId, currentPage1);
    }

    private void loadMoreOffers() {
        product_list_array.add(new Product("load"));
        promotionList.notifyItemInserted(product_list_array.size() - 1);
        int cId = myOffersController.getHealthWellnessCategoryId("Promotions");
        myOffersController.handleLoadMorePromotionsService(cId, currentPage);
    }

    private void initLeftMenu() {
        RelativeLayout mySearchLayout = findViewById(R.id.mySearchLayout);
        RelativeLayout myCartLayout = findViewById(R.id.myCartLayout);
        RelativeLayout myOrdersLayout = findViewById(R.id.myOrdersLayout);
        RelativeLayout myOffersLayout = findViewById(R.id.myOffersLayout);
        RelativeLayout myProfileLayout = findViewById(R.id.myProfileLayout);

        ImageView dashboardSearchIcon = findViewById(R.id.dashboardSearchIcon);
        ImageView dashboardMyCartIcon = findViewById(R.id.dashboardMyCartIcon);
        ImageView dashboardMyOrdersIcon = findViewById(R.id.dashboardMyOrdersIcon);
        ImageView dashboardMyOffersIcon = findViewById(R.id.dashboardMyOffersIcon);
        ImageView dashboardMyProfileIcon = findViewById(R.id.dashboardMyProfileIcon);

        TextView dashboardMySearch = findViewById(R.id.dashboardMySearch);
        TextView dashboardMySearchText = findViewById(R.id.dashboardMySearchText);
        TextView dashboardMyCart = findViewById(R.id.dashboardMyCart);
        TextView dashboardMyCartText = findViewById(R.id.dashboardMyCartText);
        TextView dashboardMyOrders = findViewById(R.id.dashboardMyOrders);
        TextView dashboardMyOrdersText = findViewById(R.id.dashboardMyOrdersText);
        TextView dashboardMyOffers = findViewById(R.id.dashboardMyOffers);
        TextView dashboardMyOffersText = findViewById(R.id.dashboardMyOffersText);
        TextView dashboardMyProfile = findViewById(R.id.dashboardMyProfile);
        TextView dashboardMyProfileText = findViewById(R.id.dashboardMyProfileText);

        ImageView userLogout = findViewById(R.id.userLogout);
        if(!HomeActivity.isLoggedin){
            userLogout.setVisibility(View.GONE);
        }else{
            userLogout.setVisibility(View.VISIBLE);
        }
        userLogout.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(MyOffersActivity.this);
            dialog.setContentView(R.layout.dialog_custom_alert);
            if (dialog.getWindow() != null)
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            TextView dialogTitleText = dialog.findViewById(R.id.dialog_info);
            Button okButton = dialog.findViewById(R.id.dialog_ok);
            Button declineButton = dialog.findViewById(R.id.dialog_cancel);
            dialogTitleText.setText(getResources().getString(R.string.label_logout_alert));
            okButton.setText(getResources().getString(R.string.label_ok));
            declineButton.setText(getResources().getString(R.string.label_cancel_text));
            okButton.setOnClickListener(v1 -> {
                dialog.dismiss();
//                SessionManager.INSTANCE.logoutUser();

                HomeActivity.isLoggedin=false;


                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("userLoginActivity", "myOffersActivityLogout");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finishAffinity();


//                Intent intent = new Intent(MyOffersActivity.this, HomeActivity.class);
//                intent.putExtra("userLoginActivity", "myOffersActivityLogout");
//                startActivity(intent);
//                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                finishAffinity();

            });
            declineButton.setOnClickListener(v12 -> dialog.dismiss());
        });

        ImageView dashboardApolloIcon = findViewById(R.id.apollo_logo);
        dashboardApolloIcon.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            finishAffinity();
        });

        myOffersLayout.setBackgroundResource(R.color.selected_menu_color);
        dashboardMyOffersIcon.setImageResource(R.drawable.dashboard_offers_hover);
        dashboardMyOffers.setTextColor(getResources().getColor(R.color.selected_text_color));
        dashboardMyOffersText.setTextColor(getResources().getColor(R.color.selected_text_color));
        myOffersLayout.setClickable(false);

        mySearchLayout.setOnClickListener(v -> {
            mySearchLayout.setBackgroundResource(R.color.selected_menu_color);
            dashboardSearchIcon.setImageResource(R.drawable.dashboard_search_hover);
            dashboardMySearch.setTextColor(getResources().getColor(R.color.selected_text_color));
            dashboardMySearchText.setTextColor(getResources().getColor(R.color.selected_text_color));

            myCartLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyCartIcon.setImageResource(R.drawable.dashboard_cart);
            dashboardMyCart.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyCartText.setTextColor(getResources().getColor(R.color.colorWhite));

            myOrdersLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyOrdersIcon.setImageResource(R.drawable.dashboard_orders);
            dashboardMyOrders.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyOrdersText.setTextColor(getResources().getColor(R.color.colorWhite));

            myOffersLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyOffersIcon.setImageResource(R.drawable.dashboard_offers);
            dashboardMyOffers.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyOffersText.setTextColor(getResources().getColor(R.color.colorWhite));

            myProfileLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyProfileIcon.setImageResource(R.drawable.dashboard_profile);
            dashboardMyProfile.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyProfileText.setTextColor(getResources().getColor(R.color.colorWhite));

            Intent intent = new Intent(this, MySearchActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });

        myCartLayout.setOnClickListener(v -> {
            mySearchLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardSearchIcon.setImageResource(R.drawable.dashboard_search);
            dashboardMySearch.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMySearchText.setTextColor(getResources().getColor(R.color.colorWhite));

            myCartLayout.setBackgroundResource(R.color.selected_menu_color);
            dashboardMyCartIcon.setImageResource(R.drawable.dashboard_cart_hover);
            dashboardMyCart.setTextColor(getResources().getColor(R.color.selected_text_color));
            dashboardMyCartText.setTextColor(getResources().getColor(R.color.selected_text_color));

            myOrdersLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyOrdersIcon.setImageResource(R.drawable.dashboard_orders);
            dashboardMyOrders.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyOrdersText.setTextColor(getResources().getColor(R.color.colorWhite));

            myOffersLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyOffersIcon.setImageResource(R.drawable.dashboard_offers);
            dashboardMyOffers.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyOffersText.setTextColor(getResources().getColor(R.color.colorWhite));

            myProfileLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyProfileIcon.setImageResource(R.drawable.dashboard_profile);
            dashboardMyProfile.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyProfileText.setTextColor(getResources().getColor(R.color.colorWhite));

            Intent intent1 = new Intent(MyOffersActivity.this, MyCartActivity.class);
            intent1.putExtra("activityname", "AddMoreActivity");
            startActivity(intent1);
            finish();
            overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
        });

        myOrdersLayout.setOnClickListener(v -> {



            if (!HomeActivity.isLoggedin) {

                Intent intent1 = new Intent(MyOffersActivity.this, UserLoginActivity.class);
                intent1.putExtra("userLoginActivity", "myOffersActivityOrdersLogin");
                startActivity(intent1);
                finish();
                overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);

//                dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
//
//                newLoginScreenBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.new_login_screen, null, false);
//                dialog.setContentView(newLoginScreenBinding.getRoot());
//                if (dialog.getWindow() != null)
//                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
////                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
////                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
////                        WindowManager.LayoutParams.MATCH_PARENT);
//                dialog.setCancelable(true);
//                newLoginScreenBinding.mobileNumEditText.requestFocus();
//                newLoginScreenBinding.closeDialog.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                        onResumeAfterLogin();
//
////                        hideSoftKeyboard(newLoginScreenBinding.mobileNumEditText);
//                    }
//                });
//
//                newLoginScreenBinding.resendButtonNewLogin.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        isResend=true;
//                        Utils.showDialog(MyOffersActivity.this, "Sending OTP…");
//                        if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
//                            Send_Sms_Request sms_req = new Send_Sms_Request();
//                            sms_req.setMobileNo(mobileNum);
//                            sms_req.setMessage("Dear Apollo Customer, Your one time password is " + String.valueOf(otp) + " and is valid for 3mins.");
//                            sms_req.setIsOtp(true);
//                            sms_req.setOtp(String.valueOf(otp));
//                            sms_req.setApiType("KIOSk");
//                            myOffersController.handleSendSmsApi(sms_req);
//                        }
//                    }
//                });
//                newLoginScreenBinding.submit.setOnClickListener(new View.OnClickListener() {
//                    @SuppressLint("SetTextI18n")
//                    @Override
//                    public void onClick(View v) {
//                        if (newLoginScreenBinding.mobileNumEditText.getText().toString() != null && newLoginScreenBinding.mobileNumEditText.getText().toString() != "") {
//                            if (SessionManager.INSTANCE.getStoreId() != null && !SessionManager.INSTANCE.getStoreId().isEmpty()
//                                    && SessionManager.INSTANCE.getTerminalId() != null && !SessionManager.INSTANCE.getTerminalId().isEmpty() && SessionManager.INSTANCE.getEposUrl() != null && !SessionManager.INSTANCE.getEposUrl().isEmpty()) {
//                                String MobilePattern = "[0-9]{10}";
//                                mobileNum = newLoginScreenBinding.mobileNumEditText.getText().toString();
//                                if (mobileNum.length() < 10) {
//                                    Toast.makeText(getApplicationContext(), "Please enter 10 digit phone number", Toast.LENGTH_SHORT).show();
//                                } else {
////                                send_otp_image.setImageResource(R.drawable.right_selection_green)
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_country_code_bg)
////                                edittext_error_text.visibility = View.INVISIBLE
//                                    if (oldMobileNum.equals(newLoginScreenBinding.mobileNumEditText.getText().toString()) && newLoginScreenBinding.mobileNumEditText.getText().toString().length() > 0 && (mobileNum.matches(MobilePattern))) {
//                                        newLoginScreenBinding.mobileNumLoginPopup.setVisibility(View.GONE);
//                                        newLoginScreenBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//                                        String phoneNumber = newLoginScreenBinding.mobileNumEditText.getText().toString().trim();
//                                        int firstDigit = Integer.parseInt((phoneNumber).substring(0, 1));
//                                        String strTwoDigits = phoneNumber.length() >= 4 ? phoneNumber.substring(phoneNumber.length() - 2) : "";
//                                        newLoginScreenBinding.mobileNumStars.setText(firstDigit + "*******" + strTwoDigits);
//                                        newLoginScreenBinding.timerNewlogin.setText("");
//                                        cancelTimer();
//                                        startTimer();
//                                    } else {
//                                        newLoginScreenBinding.mobileNumLoginPopup.setVisibility(View.GONE);
//                                        newLoginScreenBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//                                        String phoneNumber = newLoginScreenBinding.mobileNumEditText.getText().toString().trim();
//                                        int firstDigit = Integer.parseInt((phoneNumber).substring(0, 1));
//                                        String strTwoDigits = phoneNumber.length() >= 4 ? phoneNumber.substring(phoneNumber.length() - 2) : "";
//                                        newLoginScreenBinding.mobileNumStars.setText(firstDigit + "*******" + strTwoDigits);
//                                        newLoginScreenBinding.timerNewlogin.setText("");
//                                        cancelTimer();
//                                        startTimer();
//
//
//                                        if (newLoginScreenBinding.mobileNumEditText.getText().toString().length() > 0) {
//                                            oldMobileNum = newLoginScreenBinding.mobileNumEditText.getText().toString();
//                                            if (mobileNum.matches(MobilePattern)) {
//                                                Utils.showDialog(MyOffersActivity.this, "Sending OTP…");
//                                                otp = (int) ((Math.random() * 9000) + 1000);
//                                                if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
//                                                    Send_Sms_Request sms_req = new Send_Sms_Request();
//                                                    sms_req.setMobileNo(mobileNum);
//                                                    sms_req.setMessage("Dear Apollo Customer, Your one time password is " + String.valueOf(otp) + " and is valid for 3mins.");
//                                                    sms_req.setIsOtp(true);
//                                                    sms_req.setOtp(String.valueOf(otp));
//                                                    sms_req.setApiType("KIOSk");
//                                                    myOffersController.handleSendSmsApi(sms_req);
//                                                } else {
//                                                    Utils.showSnackbar(getApplicationContext(), constraintLayout, "Internet Connection Not Available");
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            } else {
//                                AccesskeyDialog accesskeyDialog = new AccesskeyDialog(MyOffersActivity.this);
//                                accesskeyDialog.onClickSubmit(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        accesskeyDialog.listener();
//                                        if (accesskeyDialog.validate()) {
//                                            Intent intent = new Intent(MyOffersActivity.this, MposStoreSetupActivity.class);
//                                            startActivity(intent);
//                                            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                                            accesskeyDialog.dismiss();
//                                        }
//                                    }
//                                });
//
//
//                                accesskeyDialog.show();
////                Intent intent = new Intent(MainActivity.this, MposStoreSetupActivity.class);
////                startActivity(intent);
////                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                            }
//                        }
////                    dialog.dismiss();
//                    }
//                });
//
//                newLoginScreenBinding.otplayoutEditText1.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable editable) {
//                        if (editable.length() == 1) {
//                            newLoginScreenBinding.otplayoutEditText1.setBackgroundResource(R.drawable.backgroundforotpblack);
//                            newLoginScreenBinding.otplayoutEditText2.requestFocus();
//                        } else {
//                            newLoginScreenBinding.otplayoutEditText1.setBackgroundResource(R.drawable.backgroundforotp);
//                        }
//                    }
//                });
//                newLoginScreenBinding.otplayoutEditText2.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable editable) {
//                        if (editable.length() == 1) {
//                            newLoginScreenBinding.otplayoutEditText2.setBackgroundResource(R.drawable.backgroundforotpblack);
//                            newLoginScreenBinding.otplayoutEditText3.requestFocus();
//                        } else {
//                            newLoginScreenBinding.otplayoutEditText2.setBackgroundResource(R.drawable.backgroundforotp);
//                            newLoginScreenBinding.otplayoutEditText1.requestFocus();
//                        }
//                    }
//                });
//                newLoginScreenBinding.otplayoutEditText3.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable editable) {
//                        if (editable.length() == 1) {
//                            newLoginScreenBinding.otplayoutEditText3.setBackgroundResource(R.drawable.backgroundforotpblack);
//                            newLoginScreenBinding.otplayoutEditText4.requestFocus();
//                        } else {
//                            newLoginScreenBinding.otplayoutEditText3.setBackgroundResource(R.drawable.backgroundforotp);
//                            newLoginScreenBinding.otplayoutEditText2.requestFocus();
//                        }
//                    }
//                });
//                newLoginScreenBinding.otplayoutEditText4.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable editable) {
//                        if (editable.length() == 1) {
//                            newLoginScreenBinding.otplayoutEditText4.setBackgroundResource(R.drawable.backgroundforotpblack);
//                        } else {
//                            newLoginScreenBinding.otplayoutEditText4.setBackgroundResource(R.drawable.backgroundforotp);
//                            newLoginScreenBinding.otplayoutEditText3.requestFocus();
//                        }
//                    }
//                });
//
//                newLoginScreenBinding.verifyOtpLoginpopup.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (!TextUtils.isEmpty(newLoginScreenBinding.otplayoutEditText1.getText().toString()) && !TextUtils.isEmpty(newLoginScreenBinding.otplayoutEditText2.getText().toString())
//                                && !TextUtils.isEmpty(newLoginScreenBinding.otplayoutEditText3.getText().toString()) && !TextUtils.isEmpty(newLoginScreenBinding.otplayoutEditText4.getText().toString())) {
//                            if (String.valueOf(otp).equals(newLoginScreenBinding.otplayoutEditText1.getText().toString() + newLoginScreenBinding.otplayoutEditText2.getText().toString() + newLoginScreenBinding.otplayoutEditText3.getText().toString() + newLoginScreenBinding.otplayoutEditText4.getText().toString())) {
////                            UserLoginController().getGlobalConfigurationApiCall(this, this)
//                                dialog.dismiss();
//                                onResumeAfterLogin();
//                                HomeActivity.isLoggedin = true;
//                                mySearchLayout.setBackgroundResource(R.color.unselected_menu_color);
//                                dashboardSearchIcon.setImageResource(R.drawable.dashboard_search);
//                                dashboardMySearch.setTextColor(getResources().getColor(R.color.colorWhite));
//                                dashboardMySearchText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                                myCartLayout.setBackgroundResource(R.color.unselected_menu_color);
//                                dashboardMyCartIcon.setImageResource(R.drawable.dashboard_cart);
//                                dashboardMyCart.setTextColor(getResources().getColor(R.color.colorWhite));
//                                dashboardMyCartText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                                myOrdersLayout.setBackgroundResource(R.color.selected_menu_color);
//                                dashboardMyOrdersIcon.setImageResource(R.drawable.dashboard_orders_hover);
//                                dashboardMyOrders.setTextColor(getResources().getColor(R.color.selected_text_color));
//                                dashboardMyOrdersText.setTextColor(getResources().getColor(R.color.selected_text_color));
//
//                                myOffersLayout.setBackgroundResource(R.color.unselected_menu_color);
//                                dashboardMyOffersIcon.setImageResource(R.drawable.dashboard_offers);
//                                dashboardMyOffers.setTextColor(getResources().getColor(R.color.colorWhite));
//                                dashboardMyOffersText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                                myProfileLayout.setBackgroundResource(R.color.unselected_menu_color);
//                                dashboardMyProfileIcon.setImageResource(R.drawable.dashboard_profile);
//                                dashboardMyProfile.setTextColor(getResources().getColor(R.color.colorWhite));
//                                dashboardMyProfileText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                                Intent intent1 = new Intent(MyOffersActivity.this, MyOrdersActivity.class);
//                                startActivity(intent1);
//                                finish();
//                                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//
////                    verify_otp_image.setImageResource(R.drawable.right_selection_green)
////                    SessionManager.setMobilenumber(mobileNum)
////                    startActivity(Intent(applicationContext, HomeActivity::class.java))
////                    finishAffinity()
////                    this.overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
//                            } else {
//                                Toast.makeText(getApplicationContext(), "Please enter valid OTP.", Toast.LENGTH_SHORT).show();
//                                newLoginScreenBinding.otplayoutEditText1.setText("");
//                                newLoginScreenBinding.otplayoutEditText2.setText("");
//                                newLoginScreenBinding.otplayoutEditText3.setText("");
//                                newLoginScreenBinding.otplayoutEditText4.setText("");
////                                newLoginScreenBinding.otplayoutLoginpopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
//////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                                newLoginScreenBinding.accesskeyErrorTextOtp.setVisibility( View.VISIBLE);
////                            verify_otp_image.setImageResource(R.drawable.right_selection_green)
////                            Utils.showSnackbar(MyProfileActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_invalid_otp_try_again));
//                            }
//                        } else {
//                            Toast.makeText(getApplicationContext(), "Please enter valid OTP", Toast.LENGTH_SHORT).show();
//                            newLoginScreenBinding.otplayoutEditText1.setText("");
//                            newLoginScreenBinding.otplayoutEditText2.setText("");
//                            newLoginScreenBinding.otplayoutEditText3.setText("");
//                            newLoginScreenBinding.otplayoutEditText4.setText("");
////                            newLoginScreenBinding.otplayoutLoginpopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
//////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                            newLoginScreenBinding.accesskeyErrorTextOtp.setVisibility( View.VISIBLE);
////                        Utils.showSnackbar(MyProfileActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_invalid_otp_try_again));
//                        }
//                    }
//                });
//                removeAllExpiryCallbacks();
//                dialog.show();
            }
            else {
                mySearchLayout.setBackgroundResource(R.color.unselected_menu_color);
                dashboardSearchIcon.setImageResource(R.drawable.dashboard_search);
                dashboardMySearch.setTextColor(getResources().getColor(R.color.colorWhite));
                dashboardMySearchText.setTextColor(getResources().getColor(R.color.colorWhite));

                myCartLayout.setBackgroundResource(R.color.unselected_menu_color);
                dashboardMyCartIcon.setImageResource(R.drawable.dashboard_cart);
                dashboardMyCart.setTextColor(getResources().getColor(R.color.colorWhite));
                dashboardMyCartText.setTextColor(getResources().getColor(R.color.colorWhite));

                myOrdersLayout.setBackgroundResource(R.color.selected_menu_color);
                dashboardMyOrdersIcon.setImageResource(R.drawable.dashboard_orders_hover);
                dashboardMyOrders.setTextColor(getResources().getColor(R.color.selected_text_color));
                dashboardMyOrdersText.setTextColor(getResources().getColor(R.color.selected_text_color));

                myOffersLayout.setBackgroundResource(R.color.unselected_menu_color);
                dashboardMyOffersIcon.setImageResource(R.drawable.dashboard_offers);
                dashboardMyOffers.setTextColor(getResources().getColor(R.color.colorWhite));
                dashboardMyOffersText.setTextColor(getResources().getColor(R.color.colorWhite));

                myProfileLayout.setBackgroundResource(R.color.unselected_menu_color);
                dashboardMyProfileIcon.setImageResource(R.drawable.dashboard_profile);
                dashboardMyProfile.setTextColor(getResources().getColor(R.color.colorWhite));
                dashboardMyProfileText.setTextColor(getResources().getColor(R.color.colorWhite));

                Intent intent1 = new Intent(MyOffersActivity.this, MyOrdersActivity.class);
                startActivity(intent1);
                finish();
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            }

//            if(!HomeActivity.isLoggedin){
//
//                dialog = new Dialog(context);
//                dialogLoginPopupBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_login_popup, null, false);
//                dialog.setContentView(dialogLoginPopupBinding.getRoot());
//                if (dialog.getWindow() != null)
//                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.setCancelable(true);
////            final Dialog dialog = new Dialog(context);
//                // if button is clicked, close the custom dialog
//                dialogLoginPopupBinding.submit.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if(dialogLoginPopupBinding.mobileNumEditText.getText().toString()!=null && dialogLoginPopupBinding.mobileNumEditText.getText().toString()!=""){
//                            if (SessionManager.INSTANCE.getStoreId() != null && !SessionManager.INSTANCE.getStoreId().isEmpty()
//                                    && SessionManager.INSTANCE.getTerminalId() != null && !SessionManager.INSTANCE.getTerminalId().isEmpty() && SessionManager.INSTANCE.getEposUrl() != null && !SessionManager.INSTANCE.getEposUrl().isEmpty()) {
//                                String MobilePattern = "[0-9]{10}";
//                                mobileNum = dialogLoginPopupBinding.mobileNumEditText.getText().toString();
//                                if (mobileNum.length() < 10) {
////                                di.setImageResource(R.drawable.right_selection_green);
//                                    dialogLoginPopupBinding.mobileNumLoginPopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
//                                    dialogLoginPopupBinding.accesskeyErrorText.setVisibility( View.VISIBLE);
//                                }
//                                else {
//                                    dialogLoginPopupBinding.accesskeyErrorText.setVisibility( View.INVISIBLE);
////                                send_otp_image.setImageResource(R.drawable.right_selection_green)
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_country_code_bg)
////                                edittext_error_text.visibility = View.INVISIBLE
//                                    if (oldMobileNum.equals(dialogLoginPopupBinding.mobileNumEditText.getText().toString()) && dialogLoginPopupBinding.mobileNumEditText.getText().toString().length() > 0 && (mobileNum.matches(MobilePattern))) {
//                                        dialogLoginPopupBinding.mobileNumLoginPopup.setVisibility(View.GONE);
//                                        dialogLoginPopupBinding.submitLoginPopup.setVisibility(View.GONE);
//                                        dialogLoginPopupBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//                                        dialogLoginPopupBinding.verifyOtpLoginpopup.setVisibility(View.VISIBLE);
//                                    } else {
//                                        dialogLoginPopupBinding.mobileNumLoginPopup.setVisibility(View.GONE);
//                                        dialogLoginPopupBinding.submitLoginPopup.setVisibility(View.GONE);
//                                        dialogLoginPopupBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//                                        dialogLoginPopupBinding.verifyOtpLoginpopup.setVisibility(View.VISIBLE);
//                                        if (dialogLoginPopupBinding.mobileNumEditText.getText().toString().length() > 0) {
//                                            oldMobileNum = dialogLoginPopupBinding.mobileNumEditText.getText().toString();
//                                            if (mobileNum.matches(MobilePattern)) {
//                                                Utils.showDialog(MyOffersActivity.this, "Sending OTP…");
//                                                otp = (int) ((Math.random() * 9000) + 1000);
//                                                if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
//                                                    Send_Sms_Request sms_req = new Send_Sms_Request();
//                                                    sms_req.setMobileNo(mobileNum);
//                                                    sms_req.setMessage("Dear Apollo Customer, Your one time password is " + String.valueOf(otp) + " and is valid for 3mins.");
//                                                    sms_req.setIsOtp(true);
//                                                    sms_req.setOtp(String.valueOf(otp));
//                                                    sms_req.setApiType("KIOSk");
//                                                    myOffersController.handleSendSmsApi(sms_req);
//                                                } else {
//                                                    Utils.showSnackbar(getApplicationContext(), constraintLayout, "Internet Connection Not Available");
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                            else {
//                                AccesskeyDialog accesskeyDialog = new AccesskeyDialog(MyOffersActivity.this);
//                                accesskeyDialog.onClickSubmit(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        accesskeyDialog.listener();
//                                        if (accesskeyDialog.validate()) {
//                                            Intent intent = new Intent(MyOffersActivity.this, MposStoreSetupActivity.class);
//                                            startActivity(intent);
//                                            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                                            accesskeyDialog.dismiss();
//                                        }
//                                    }
//                                });
//
//
//
//                                accesskeyDialog.show();
////                Intent intent = new Intent(MainActivity.this, MposStoreSetupActivity.class);
////                startActivity(intent);
////                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                            }
//                        }
////                    dialog.dismiss();
//                    }
//                });
//
//                dialogLoginPopupBinding.verifyOtpLoginpopup.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (!TextUtils.isEmpty(dialogLoginPopupBinding.otplayoutEditText.getText().toString()) && dialogLoginPopupBinding.otplayoutEditText.getText().toString().length() > 0) {
//                            if (String.valueOf(otp).equals(dialogLoginPopupBinding.otplayoutEditText.getText().toString()) ) {
////                            UserLoginController().getGlobalConfigurationApiCall(this, this)
//                                dialog.dismiss();
//                                HomeActivity.isLoggedin=true;
//                                mySearchLayout.setBackgroundResource(R.color.unselected_menu_color);
//                                dashboardSearchIcon.setImageResource(R.drawable.dashboard_search);
//                                dashboardMySearch.setTextColor(getResources().getColor(R.color.colorWhite));
//                                dashboardMySearchText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                                myCartLayout.setBackgroundResource(R.color.unselected_menu_color);
//                                dashboardMyCartIcon.setImageResource(R.drawable.dashboard_cart);
//                                dashboardMyCart.setTextColor(getResources().getColor(R.color.colorWhite));
//                                dashboardMyCartText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                                myOrdersLayout.setBackgroundResource(R.color.selected_menu_color);
//                                dashboardMyOrdersIcon.setImageResource(R.drawable.dashboard_orders_hover);
//                                dashboardMyOrders.setTextColor(getResources().getColor(R.color.selected_text_color));
//                                dashboardMyOrdersText.setTextColor(getResources().getColor(R.color.selected_text_color));
//
//                                myOffersLayout.setBackgroundResource(R.color.unselected_menu_color);
//                                dashboardMyOffersIcon.setImageResource(R.drawable.dashboard_offers);
//                                dashboardMyOffers.setTextColor(getResources().getColor(R.color.colorWhite));
//                                dashboardMyOffersText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                                myProfileLayout.setBackgroundResource(R.color.unselected_menu_color);
//                                dashboardMyProfileIcon.setImageResource(R.drawable.dashboard_profile);
//                                dashboardMyProfile.setTextColor(getResources().getColor(R.color.colorWhite));
//                                dashboardMyProfileText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                                Intent intent1 = new Intent(MyOffersActivity.this, MyOrdersActivity.class);
//                                startActivity(intent1);
//                                finish();
//                                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//
////                    verify_otp_image.setImageResource(R.drawable.right_selection_green)
////                    SessionManager.setMobilenumber(mobileNum)
////                    startActivity(Intent(applicationContext, HomeActivity::class.java))
////                    finishAffinity()
////                    this.overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
//                            } else {
//                                dialogLoginPopupBinding.otplayoutLoginpopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
//                                dialogLoginPopupBinding.accesskeyErrorTextOtp.setVisibility( View.VISIBLE);
////                            verify_otp_image.setImageResource(R.drawable.right_selection_green)
////                            Utils.showSnackbar(MyProfileActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_invalid_otp_try_again));
//                            }
//                        } else {
//                            dialogLoginPopupBinding.otplayoutLoginpopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
//                            dialogLoginPopupBinding.accesskeyErrorTextOtp.setVisibility( View.VISIBLE);
////                        Utils.showSnackbar(MyProfileActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_invalid_otp_try_again));
//                        }
//                    }
//                });
//                dialog.show();
//            }
//            else{
//                mySearchLayout.setBackgroundResource(R.color.unselected_menu_color);
//                dashboardSearchIcon.setImageResource(R.drawable.dashboard_search);
//                dashboardMySearch.setTextColor(getResources().getColor(R.color.colorWhite));
//                dashboardMySearchText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                myCartLayout.setBackgroundResource(R.color.unselected_menu_color);
//                dashboardMyCartIcon.setImageResource(R.drawable.dashboard_cart);
//                dashboardMyCart.setTextColor(getResources().getColor(R.color.colorWhite));
//                dashboardMyCartText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                myOrdersLayout.setBackgroundResource(R.color.selected_menu_color);
//                dashboardMyOrdersIcon.setImageResource(R.drawable.dashboard_orders_hover);
//                dashboardMyOrders.setTextColor(getResources().getColor(R.color.selected_text_color));
//                dashboardMyOrdersText.setTextColor(getResources().getColor(R.color.selected_text_color));
//
//                myOffersLayout.setBackgroundResource(R.color.unselected_menu_color);
//                dashboardMyOffersIcon.setImageResource(R.drawable.dashboard_offers);
//                dashboardMyOffers.setTextColor(getResources().getColor(R.color.colorWhite));
//                dashboardMyOffersText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                myProfileLayout.setBackgroundResource(R.color.unselected_menu_color);
//                dashboardMyProfileIcon.setImageResource(R.drawable.dashboard_profile);
//                dashboardMyProfile.setTextColor(getResources().getColor(R.color.colorWhite));
//                dashboardMyProfileText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                Intent intent1 = new Intent(MyOffersActivity.this, MyOrdersActivity.class);
//                startActivity(intent1);
//                finish();
//                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//            }

        });


        myProfileLayout.setOnClickListener(v -> {

            if (!HomeActivity.isLoggedin) {

                Intent intent1 = new Intent(MyOffersActivity.this, UserLoginActivity.class);
                intent1.putExtra("userLoginActivity", "myOffersActivityProfileLogin");
                startActivity(intent1);
                finish();
                overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);


//                dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
//
//                newLoginScreenBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.new_login_screen, null, false);
//                dialog.setContentView(newLoginScreenBinding.getRoot());
//                if (dialog.getWindow() != null)
//                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
////                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
////                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
////                        WindowManager.LayoutParams.MATCH_PARENT);
//                dialog.setCancelable(true);
//                newLoginScreenBinding.mobileNumEditText.requestFocus();
//                newLoginScreenBinding.closeDialog.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                        onResumeAfterLogin();
//                    }
//                });
//
//                newLoginScreenBinding.resendButtonNewLogin.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        isResend=true;
//                        Utils.showDialog(MyOffersActivity.this, "Sending OTP…");
//                        if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
//                            Send_Sms_Request sms_req = new Send_Sms_Request();
//                            sms_req.setMobileNo(mobileNum);
//                            sms_req.setMessage("Dear Apollo Customer, Your one time password is " + String.valueOf(otp) + " and is valid for 3mins.");
//                            sms_req.setIsOtp(true);
//                            sms_req.setOtp(String.valueOf(otp));
//                            sms_req.setApiType("KIOSk");
//                            myOffersController.handleSendSmsApi(sms_req);
//                        }
//                    }
//                });
//                newLoginScreenBinding.submit.setOnClickListener(new View.OnClickListener() {
//                    @SuppressLint("SetTextI18n")
//                    @Override
//                    public void onClick(View v) {
//                        if (newLoginScreenBinding.mobileNumEditText.getText().toString() != null && newLoginScreenBinding.mobileNumEditText.getText().toString() != "") {
//                            if (SessionManager.INSTANCE.getStoreId() != null && !SessionManager.INSTANCE.getStoreId().isEmpty()
//                                    && SessionManager.INSTANCE.getTerminalId() != null && !SessionManager.INSTANCE.getTerminalId().isEmpty() && SessionManager.INSTANCE.getEposUrl() != null && !SessionManager.INSTANCE.getEposUrl().isEmpty()) {
//                                String MobilePattern = "[0-9]{10}";
//                                mobileNum = newLoginScreenBinding.mobileNumEditText.getText().toString();
//                                if (mobileNum.length() < 10) {
//                                    Toast.makeText(getApplicationContext(), "Please enter 10 digit phone number", Toast.LENGTH_SHORT).show();
//                                } else {
////                                send_otp_image.setImageResource(R.drawable.right_selection_green)
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_country_code_bg)
////                                edittext_error_text.visibility = View.INVISIBLE
//                                    if (oldMobileNum.equals(newLoginScreenBinding.mobileNumEditText.getText().toString()) && newLoginScreenBinding.mobileNumEditText.getText().toString().length() > 0 && (mobileNum.matches(MobilePattern))) {
//                                        newLoginScreenBinding.mobileNumLoginPopup.setVisibility(View.GONE);
//                                        newLoginScreenBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//                                        String phoneNumber = newLoginScreenBinding.mobileNumEditText.getText().toString().trim();
//                                        int firstDigit = Integer.parseInt((phoneNumber).substring(0, 1));
//                                        String strTwoDigits = phoneNumber.length() >= 4 ? phoneNumber.substring(phoneNumber.length() - 2) : "";
//                                        newLoginScreenBinding.mobileNumStars.setText(firstDigit + "*******" + strTwoDigits);
//                                        newLoginScreenBinding.timerNewlogin.setText("");
//                                        cancelTimer();
//                                        startTimer();
//                                    } else {
//                                        newLoginScreenBinding.mobileNumLoginPopup.setVisibility(View.GONE);
//                                        newLoginScreenBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//                                        String phoneNumber = newLoginScreenBinding.mobileNumEditText.getText().toString().trim();
//                                        int firstDigit = Integer.parseInt((phoneNumber).substring(0, 1));
//                                        String strTwoDigits = phoneNumber.length() >= 4 ? phoneNumber.substring(phoneNumber.length() - 2) : "";
//                                        newLoginScreenBinding.mobileNumStars.setText(firstDigit + "*******" + strTwoDigits);
//                                        newLoginScreenBinding.timerNewlogin.setText("");
//                                        cancelTimer();
//                                        startTimer();
//
//
//                                        if (newLoginScreenBinding.mobileNumEditText.getText().toString().length() > 0) {
//                                            oldMobileNum = newLoginScreenBinding.mobileNumEditText.getText().toString();
//                                            if (mobileNum.matches(MobilePattern)) {
//                                                Utils.showDialog(MyOffersActivity.this, "Sending OTP…");
//                                                otp = (int) ((Math.random() * 9000) + 1000);
//                                                if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
//                                                    Send_Sms_Request sms_req = new Send_Sms_Request();
//                                                    sms_req.setMobileNo(mobileNum);
//                                                    sms_req.setMessage("Dear Apollo Customer, Your one time password is " + String.valueOf(otp) + " and is valid for 3mins.");
//                                                    sms_req.setIsOtp(true);
//                                                    sms_req.setOtp(String.valueOf(otp));
//                                                    sms_req.setApiType("KIOSk");
//                                                    myOffersController.handleSendSmsApi(sms_req);
//                                                } else {
//                                                    Utils.showSnackbar(getApplicationContext(), constraintLayout, "Internet Connection Not Available");
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            } else {
//                                AccesskeyDialog accesskeyDialog = new AccesskeyDialog(MyOffersActivity.this);
//                                accesskeyDialog.onClickSubmit(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        accesskeyDialog.listener();
//                                        if (accesskeyDialog.validate()) {
//                                            Intent intent = new Intent(MyOffersActivity.this, MposStoreSetupActivity.class);
//                                            startActivity(intent);
//                                            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                                            accesskeyDialog.dismiss();
//                                        }
//                                    }
//                                });
//
//
//                                accesskeyDialog.show();
////                Intent intent = new Intent(MainActivity.this, MposStoreSetupActivity.class);
////                startActivity(intent);
////                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                            }
//                        }
////                    dialog.dismiss();
//                    }
//                });
//
//                newLoginScreenBinding.otplayoutEditText1.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable editable) {
//                        if (editable.length() == 1) {
//                            newLoginScreenBinding.otplayoutEditText1.setBackgroundResource(R.drawable.backgroundforotpblack);
//                            newLoginScreenBinding.otplayoutEditText2.requestFocus();
//                        } else {
//                            newLoginScreenBinding.otplayoutEditText1.setBackgroundResource(R.drawable.backgroundforotp);
//                        }
//                    }
//                });
//                newLoginScreenBinding.otplayoutEditText2.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable editable) {
//                        if (editable.length() == 1) {
//                            newLoginScreenBinding.otplayoutEditText2.setBackgroundResource(R.drawable.backgroundforotpblack);
//                            newLoginScreenBinding.otplayoutEditText3.requestFocus();
//                        } else {
//                            newLoginScreenBinding.otplayoutEditText2.setBackgroundResource(R.drawable.backgroundforotp);
//                            newLoginScreenBinding.otplayoutEditText1.requestFocus();
//                        }
//                    }
//                });
//                newLoginScreenBinding.otplayoutEditText3.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable editable) {
//                        if (editable.length() == 1) {
//                            newLoginScreenBinding.otplayoutEditText3.setBackgroundResource(R.drawable.backgroundforotpblack);
//                            newLoginScreenBinding.otplayoutEditText4.requestFocus();
//                        } else {
//                            newLoginScreenBinding.otplayoutEditText3.setBackgroundResource(R.drawable.backgroundforotp);
//                            newLoginScreenBinding.otplayoutEditText2.requestFocus();
//                        }
//                    }
//                });
//                newLoginScreenBinding.otplayoutEditText4.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable editable) {
//                        if (editable.length() == 1) {
//                            newLoginScreenBinding.otplayoutEditText4.setBackgroundResource(R.drawable.backgroundforotpblack);
//                        } else {
//                            newLoginScreenBinding.otplayoutEditText4.setBackgroundResource(R.drawable.backgroundforotp);
//                            newLoginScreenBinding.otplayoutEditText3.requestFocus();
//                        }
//                    }
//                });
//
//                newLoginScreenBinding.verifyOtpLoginpopup.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (!TextUtils.isEmpty(newLoginScreenBinding.otplayoutEditText1.getText().toString()) && !TextUtils.isEmpty(newLoginScreenBinding.otplayoutEditText2.getText().toString())
//                                && !TextUtils.isEmpty(newLoginScreenBinding.otplayoutEditText3.getText().toString()) && !TextUtils.isEmpty(newLoginScreenBinding.otplayoutEditText4.getText().toString())) {
//                            if (String.valueOf(otp).equals(newLoginScreenBinding.otplayoutEditText1.getText().toString() + newLoginScreenBinding.otplayoutEditText2.getText().toString() + newLoginScreenBinding.otplayoutEditText3.getText().toString() + newLoginScreenBinding.otplayoutEditText4.getText().toString())) {
////                            UserLoginController().getGlobalConfigurationApiCall(this, this)
//                                dialog.dismiss();
//                                onResumeAfterLogin();
//                                HomeActivity.isLoggedin = true;
//                                mySearchLayout.setBackgroundResource(R.color.unselected_menu_color);
//                                dashboardSearchIcon.setImageResource(R.drawable.dashboard_search);
//                                dashboardMySearch.setTextColor(getResources().getColor(R.color.colorWhite));
//                                dashboardMySearchText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                                myCartLayout.setBackgroundResource(R.color.unselected_menu_color);
//                                dashboardMyCartIcon.setImageResource(R.drawable.dashboard_cart);
//                                dashboardMyCart.setTextColor(getResources().getColor(R.color.colorWhite));
//                                dashboardMyCartText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                                myOrdersLayout.setBackgroundResource(R.color.unselected_menu_color);
//                                dashboardMyOrdersIcon.setImageResource(R.drawable.dashboard_orders);
//                                dashboardMyOrders.setTextColor(getResources().getColor(R.color.colorWhite));
//                                dashboardMyOrdersText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                                myOffersLayout.setBackgroundResource(R.color.unselected_menu_color);
//                                dashboardMyOffersIcon.setImageResource(R.drawable.dashboard_offers);
//                                dashboardMyOffers.setTextColor(getResources().getColor(R.color.colorWhite));
//                                dashboardMyOffersText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                                myProfileLayout.setBackgroundResource(R.color.selected_menu_color);
//                                dashboardMyProfileIcon.setImageResource(R.drawable.dashboard_profile_hover);
//                                dashboardMyProfile.setTextColor(getResources().getColor(R.color.selected_text_color));
//                                dashboardMyProfileText.setTextColor(getResources().getColor(R.color.selected_text_color));
//
//                                Intent intent1 = new Intent(MyOffersActivity.this, MyProfileActivity.class);
//                                startActivity(intent1);
//                                finish();
//                                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//
////                    verify_otp_image.setImageResource(R.drawable.right_selection_green)
////                    SessionManager.setMobilenumber(mobileNum)
////                    startActivity(Intent(applicationContext, HomeActivity::class.java))
////                    finishAffinity()
////                    this.overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
//                            } else {
//                                Toast.makeText(getApplicationContext(), "Please enter valid OTP.", Toast.LENGTH_SHORT).show();
//                                newLoginScreenBinding.otplayoutEditText1.setText("");
//                                newLoginScreenBinding.otplayoutEditText2.setText("");
//                                newLoginScreenBinding.otplayoutEditText3.setText("");
//                                newLoginScreenBinding.otplayoutEditText4.setText("");
////                                newLoginScreenBinding.otplayoutLoginpopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
//////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                                newLoginScreenBinding.accesskeyErrorTextOtp.setVisibility( View.VISIBLE);
////                            verify_otp_image.setImageResource(R.drawable.right_selection_green)
////                            Utils.showSnackbar(MyProfileActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_invalid_otp_try_again));
//                            }
//                        } else {
//                            Toast.makeText(getApplicationContext(), "Please enter valid OTP", Toast.LENGTH_SHORT).show();
//                            newLoginScreenBinding.otplayoutEditText1.setText("");
//                            newLoginScreenBinding.otplayoutEditText2.setText("");
//                            newLoginScreenBinding.otplayoutEditText3.setText("");
//                            newLoginScreenBinding.otplayoutEditText4.setText("");
////                            newLoginScreenBinding.otplayoutLoginpopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
//////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                            newLoginScreenBinding.accesskeyErrorTextOtp.setVisibility( View.VISIBLE);
////                        Utils.showSnackbar(MyProfileActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_invalid_otp_try_again));
//                        }
//                    }
//                });
//                removeAllExpiryCallbacks();
//                dialog.show();
            }
            else {
                mySearchLayout.setBackgroundResource(R.color.unselected_menu_color);
                dashboardSearchIcon.setImageResource(R.drawable.dashboard_search);
                dashboardMySearch.setTextColor(getResources().getColor(R.color.colorWhite));
                dashboardMySearchText.setTextColor(getResources().getColor(R.color.colorWhite));

                myCartLayout.setBackgroundResource(R.color.unselected_menu_color);
                dashboardMyCartIcon.setImageResource(R.drawable.dashboard_cart);
                dashboardMyCart.setTextColor(getResources().getColor(R.color.colorWhite));
                dashboardMyCartText.setTextColor(getResources().getColor(R.color.colorWhite));

                myOrdersLayout.setBackgroundResource(R.color.unselected_menu_color);
                dashboardMyOrdersIcon.setImageResource(R.drawable.dashboard_orders);
                dashboardMyOrders.setTextColor(getResources().getColor(R.color.colorWhite));
                dashboardMyOrdersText.setTextColor(getResources().getColor(R.color.colorWhite));

                myOffersLayout.setBackgroundResource(R.color.unselected_menu_color);
                dashboardMyOffersIcon.setImageResource(R.drawable.dashboard_offers);
                dashboardMyOffers.setTextColor(getResources().getColor(R.color.colorWhite));
                dashboardMyOffersText.setTextColor(getResources().getColor(R.color.colorWhite));

                myProfileLayout.setBackgroundResource(R.color.selected_menu_color);
                dashboardMyProfileIcon.setImageResource(R.drawable.dashboard_profile_hover);
                dashboardMyProfile.setTextColor(getResources().getColor(R.color.selected_text_color));
                dashboardMyProfileText.setTextColor(getResources().getColor(R.color.selected_text_color));

                Intent intent1 = new Intent(MyOffersActivity.this, MyProfileActivity.class);
                startActivity(intent1);
                finish();
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            }


//            if (!HomeActivity.isLoggedin) {
//
//                dialog = new Dialog(context);
//                dialogLoginPopupBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_login_popup, null, false);
//                dialog.setContentView(dialogLoginPopupBinding.getRoot());
//                if (dialog.getWindow() != null)
//                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.setCancelable(true);
////            final Dialog dialog = new Dialog(context);
//                // if button is clicked, close the custom dialog
//                dialogLoginPopupBinding.submit.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (dialogLoginPopupBinding.mobileNumEditText.getText().toString() != null && dialogLoginPopupBinding.mobileNumEditText.getText().toString() != "") {
//                            if (SessionManager.INSTANCE.getStoreId() != null && !SessionManager.INSTANCE.getStoreId().isEmpty()
//                                    && SessionManager.INSTANCE.getTerminalId() != null && !SessionManager.INSTANCE.getTerminalId().isEmpty() && SessionManager.INSTANCE.getEposUrl() != null && !SessionManager.INSTANCE.getEposUrl().isEmpty()) {
//                                String MobilePattern = "[0-9]{10}";
//                                mobileNum = dialogLoginPopupBinding.mobileNumEditText.getText().toString();
//                                if (mobileNum.length() < 10) {
////                                di.setImageResource(R.drawable.right_selection_green);
//                                    dialogLoginPopupBinding.mobileNumLoginPopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
//                                    dialogLoginPopupBinding.accesskeyErrorText.setVisibility(View.VISIBLE);
//                                } else {
//                                    dialogLoginPopupBinding.accesskeyErrorText.setVisibility(View.INVISIBLE);
////                                send_otp_image.setImageResource(R.drawable.right_selection_green)
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_country_code_bg)
////                                edittext_error_text.visibility = View.INVISIBLE
//                                    if (oldMobileNum.equals(dialogLoginPopupBinding.mobileNumEditText.getText().toString()) && dialogLoginPopupBinding.mobileNumEditText.getText().toString().length() > 0 && (mobileNum.matches(MobilePattern))) {
//                                        dialogLoginPopupBinding.mobileNumLoginPopup.setVisibility(View.GONE);
//                                        dialogLoginPopupBinding.submitLoginPopup.setVisibility(View.GONE);
//                                        dialogLoginPopupBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//                                        dialogLoginPopupBinding.verifyOtpLoginpopup.setVisibility(View.VISIBLE);
//                                    } else {
//                                        dialogLoginPopupBinding.mobileNumLoginPopup.setVisibility(View.GONE);
//                                        dialogLoginPopupBinding.submitLoginPopup.setVisibility(View.GONE);
//                                        dialogLoginPopupBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//                                        dialogLoginPopupBinding.verifyOtpLoginpopup.setVisibility(View.VISIBLE);
//                                        if (dialogLoginPopupBinding.mobileNumEditText.getText().toString().length() > 0) {
//                                            oldMobileNum = dialogLoginPopupBinding.mobileNumEditText.getText().toString();
//                                            if (mobileNum.matches(MobilePattern)) {
//                                                Utils.showDialog(MyOffersActivity.this, "Sending OTP…");
//                                                otp = (int) ((Math.random() * 9000) + 1000);
//                                                if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
//                                                    Send_Sms_Request sms_req = new Send_Sms_Request();
//                                                    sms_req.setMobileNo(mobileNum);
//                                                    sms_req.setMessage("Dear Apollo Customer, Your one time password is " + String.valueOf(otp) + " and is valid for 3mins.");
//                                                    sms_req.setIsOtp(true);
//                                                    sms_req.setOtp(String.valueOf(otp));
//                                                    sms_req.setApiType("KIOSk");
//                                                    myOffersController.handleSendSmsApi(sms_req);
//                                                } else {
//                                                    Utils.showSnackbar(getApplicationContext(), constraintLayout, "Internet Connection Not Available");
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            } else {
//                                AccesskeyDialog accesskeyDialog = new AccesskeyDialog(MyOffersActivity.this);
//                                accesskeyDialog.onClickSubmit(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        accesskeyDialog.listener();
//                                        if (accesskeyDialog.validate()) {
//                                            Intent intent = new Intent(MyOffersActivity.this, MposStoreSetupActivity.class);
//                                            startActivity(intent);
//                                            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                                            accesskeyDialog.dismiss();
//                                        }
//                                    }
//                                });
//
//
//                                accesskeyDialog.show();
////                Intent intent = new Intent(MainActivity.this, MposStoreSetupActivity.class);
////                startActivity(intent);
////                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                            }
//                        }
////                    dialog.dismiss();
//                    }
//                });
//
//                dialogLoginPopupBinding.verifyOtpLoginpopup.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (!TextUtils.isEmpty(dialogLoginPopupBinding.otplayoutEditText.getText().toString()) && dialogLoginPopupBinding.otplayoutEditText.getText().toString().length() > 0) {
//                            if (String.valueOf(otp).equals(dialogLoginPopupBinding.otplayoutEditText.getText().toString())) {
////                            UserLoginController().getGlobalConfigurationApiCall(this, this)
//                                dialog.dismiss();
//                                HomeActivity.isLoggedin = true;
//                                mySearchLayout.setBackgroundResource(R.color.unselected_menu_color);
//                                dashboardSearchIcon.setImageResource(R.drawable.dashboard_search);
//                                dashboardMySearch.setTextColor(getResources().getColor(R.color.colorWhite));
//                                dashboardMySearchText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                                myCartLayout.setBackgroundResource(R.color.unselected_menu_color);
//                                dashboardMyCartIcon.setImageResource(R.drawable.dashboard_cart);
//                                dashboardMyCart.setTextColor(getResources().getColor(R.color.colorWhite));
//                                dashboardMyCartText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                                myOrdersLayout.setBackgroundResource(R.color.unselected_menu_color);
//                                dashboardMyOrdersIcon.setImageResource(R.drawable.dashboard_orders);
//                                dashboardMyOrders.setTextColor(getResources().getColor(R.color.colorWhite));
//                                dashboardMyOrdersText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                                myOffersLayout.setBackgroundResource(R.color.unselected_menu_color);
//                                dashboardMyOffersIcon.setImageResource(R.drawable.dashboard_offers);
//                                dashboardMyOffers.setTextColor(getResources().getColor(R.color.colorWhite));
//                                dashboardMyOffersText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                                myProfileLayout.setBackgroundResource(R.color.selected_menu_color);
//                                dashboardMyProfileIcon.setImageResource(R.drawable.dashboard_profile_hover);
//                                dashboardMyProfile.setTextColor(getResources().getColor(R.color.selected_text_color));
//                                dashboardMyProfileText.setTextColor(getResources().getColor(R.color.selected_text_color));
//
//                                Intent intent1 = new Intent(MyOffersActivity.this, MyProfileActivity.class);
//                                startActivity(intent1);
//                                finish();
//                                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//
////                    verify_otp_image.setImageResource(R.drawable.right_selection_green)
////                    SessionManager.setMobilenumber(mobileNum)
////                    startActivity(Intent(applicationContext, HomeActivity::class.java))
////                    finishAffinity()
////                    this.overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
//                            } else {
//                                dialogLoginPopupBinding.otplayoutLoginpopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
//                                dialogLoginPopupBinding.accesskeyErrorTextOtp.setVisibility(View.VISIBLE);
////                            verify_otp_image.setImageResource(R.drawable.right_selection_green)
////                            Utils.showSnackbar(MyProfileActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_invalid_otp_try_again));
//                            }
//                        } else {
//                            dialogLoginPopupBinding.otplayoutLoginpopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
//                            dialogLoginPopupBinding.accesskeyErrorTextOtp.setVisibility(View.VISIBLE);
////                        Utils.showSnackbar(MyProfileActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_invalid_otp_try_again));
//                        }
//                    }
//                });
//                dialog.show();
//            }
//            else{
//                mySearchLayout.setBackgroundResource(R.color.unselected_menu_color);
//                dashboardSearchIcon.setImageResource(R.drawable.dashboard_search);
//                dashboardMySearch.setTextColor(getResources().getColor(R.color.colorWhite));
//                dashboardMySearchText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                myCartLayout.setBackgroundResource(R.color.unselected_menu_color);
//                dashboardMyCartIcon.setImageResource(R.drawable.dashboard_cart);
//                dashboardMyCart.setTextColor(getResources().getColor(R.color.colorWhite));
//                dashboardMyCartText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                myOrdersLayout.setBackgroundResource(R.color.unselected_menu_color);
//                dashboardMyOrdersIcon.setImageResource(R.drawable.dashboard_orders);
//                dashboardMyOrders.setTextColor(getResources().getColor(R.color.colorWhite));
//                dashboardMyOrdersText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                myOffersLayout.setBackgroundResource(R.color.unselected_menu_color);
//                dashboardMyOffersIcon.setImageResource(R.drawable.dashboard_offers);
//                dashboardMyOffers.setTextColor(getResources().getColor(R.color.colorWhite));
//                dashboardMyOffersText.setTextColor(getResources().getColor(R.color.colorWhite));
//
//                myProfileLayout.setBackgroundResource(R.color.selected_menu_color);
//                dashboardMyProfileIcon.setImageResource(R.drawable.dashboard_profile_hover);
//                dashboardMyProfile.setTextColor(getResources().getColor(R.color.selected_text_color));
//                dashboardMyProfileText.setTextColor(getResources().getColor(R.color.selected_text_color));
//
//                Intent intent1 = new Intent(MyOffersActivity.this, MyProfileActivity.class);
//                startActivity(intent1);
//                finish();
//                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//            }

        });
    }

    public void showSoftKeyboard(View view){
        if(view.requestFocus()){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
        }
    }
    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    @Override
    public void cartCount(int count) {
        if (count != 0) {

            List<OCRToDigitalMedicineResponse> countUniques = new ArrayList<>();
            countUniques.addAll(SessionManager.INSTANCE.getDataList());

            for (int i = 0; i < countUniques.size(); i++) {
                for (int j = 0; j < countUniques.size(); j++) {
                    if (i != j && countUniques.get(i).getArtName().equals(countUniques.get(j).getArtName())) {
                        countUniques.remove(j);
                        j--;
                    }
                }
            }

            myCartCount.setVisibility(View.VISIBLE);
//            myCartCount.setText(String.valueOf(count));
            myCartCount.setText(String.valueOf(countUniques.size()));
//            itemsCount.setVisibility(View.VISIBLE);
//            plusIcon.setVisibility(View.VISIBLE);
//            checkOutImage.setVisibility(View.VISIBLE);
            checkOutImage.setImageResource(R.drawable.checkout_cart);
            itemsCount.setText(count + " " + getApplicationContext().getResources().getString(R.string.label_items) + " " + getApplicationContext().getResources().getString(R.string.label_added));
        } else {
            plusIcon.setVisibility(View.GONE);
            itemsCount.setVisibility(View.GONE);
            checkOutImage.setVisibility(View.GONE);
            myCartCount.setVisibility(View.GONE);
            myCartCount.setText("");
            itemsCount.setText("");
        }
    }

    @Override
    public void showSnackBAr() {

    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiverAddmore);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiverNew);

        super.onPause();
        Constants.getInstance().setConnectivityListener(null);
    }

    private BroadcastReceiver mMessageReceiverAddmore = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (message.equals("MedicinedataAddMore")) {
                ScannedData fcmMedicine = new Gson().fromJson(intent.getStringExtra("MedininesNames"), ScannedData.class);
                medConvertDialog(fcmMedicine);
            }
        }
    };


    private BroadcastReceiver mMessageReceiverNew = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (message.equalsIgnoreCase("OrderNow")) {
                if (null != SessionManager.INSTANCE.getDataList()) {
                    if (SessionManager.INSTANCE.getDataList().size() > 0) {
                        cartCount(SessionManager.INSTANCE.getDataList().size());
                        dataList = SessionManager.INSTANCE.getDataList();
                    }
                }
                if (toasCount == 1) {
                    Utils.showSnackbar(MyOffersActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_item_added_cart));
                }
                cartCount(dataList.size());
            }
        }
    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (message.equalsIgnoreCase("Addtocart")) {
                if (null != SessionManager.INSTANCE.getDataList()) {
                    if (SessionManager.INSTANCE.getDataList().size() > 0) {
                        cartCount(SessionManager.INSTANCE.getDataList().size());
                        dataList = SessionManager.INSTANCE.getDataList();
                    }
                }
                boolean product_avilable = false;
                if (null != dataList) {
                    int count = 0;
                    for (OCRToDigitalMedicineResponse data : dataList) {
                        if (data.getArtCode() != null) {
                            if (data.getArtCode().equalsIgnoreCase(intent.getStringExtra("product_sku"))) {
                                product_avilable = true;
                                int qty = data.getQty();
                                qty = qty + 1;
                                dataList.remove(count);
                                OCRToDigitalMedicineResponse object1 = new OCRToDigitalMedicineResponse();
                                object1.setArtName(intent.getStringExtra("product_name"));
                                object1.setArtCode(intent.getStringExtra("product_sku"));
                                object1.setMedicineType(intent.getStringExtra("medicineType"));
                                object1.setQty(Integer.parseInt(intent.getStringExtra("product_quantyty")));
                                if (null != intent.getStringExtra("product_price")) {
                                    object1.setArtprice(intent.getStringExtra("product_price"));
                                } else {
                                    object1.setArtprice(String.valueOf(intent.getStringExtra("product_price")));
                                }
                                object1.setMou(String.valueOf(intent.getStringExtra("product_mou")));
//                                object1.setQty(qty);
                                object1.setContainer("Strip");
                                dataList.add(object1);
                                SessionManager.INSTANCE.setDataList(dataList);
                                break;
                            } else {
                                product_avilable = false;
                            }
                        }
                        count = count + 1;
                    }
                    if (!product_avilable) {
                        OCRToDigitalMedicineResponse object1 = new OCRToDigitalMedicineResponse();
                        object1.setArtName(intent.getStringExtra("product_name"));
                        object1.setArtCode(intent.getStringExtra("product_sku"));
                        object1.setMedicineType(intent.getStringExtra("medicineType"));
                        object1.setQty(Integer.parseInt(intent.getStringExtra("product_quantyty")));
                        if (null != intent.getStringExtra("product_price")) {
                            object1.setArtprice(intent.getStringExtra("product_price"));
                        } else {
                            object1.setArtprice(String.valueOf(intent.getStringExtra("product_price")));
                        }
                        object1.setMou(String.valueOf(intent.getStringExtra("product_mou")));
//                        object1.setQty(1);
                        object1.setContainer("Strip");
                        dataList.add(object1);
                        SessionManager.INSTANCE.setDataList(dataList);
                    }
                }
                Utils.showSnackbar(MyOffersActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_item_added_cart));
                cartCount(dataList.size());
            }
//            if (null != SessionManager.INSTANCE.getDataList())
//                checkOutNewBtn.setVisibility(View.VISIBLE);
//            else
//                checkOutNewBtn.setVisibility(View.GONE);
        }
    };

    public void medConvertDialog(ScannedData fcmMedicine) {
        final Dialog dialog = new Dialog(MyOffersActivity.this);
        dialog.setContentView(R.layout.dialog_custom_alert);
        dialog.setCancelable(false);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView dialogTitleText = dialog.findViewById(R.id.dialog_info);
        Button okButton = dialog.findViewById(R.id.dialog_ok);
        Button declineButton = dialog.findViewById(R.id.dialog_cancel);
        declineButton.setVisibility(View.GONE);
        dialogTitleText.setText(getResources().getString(R.string.label_curation_completed_alert));
        okButton.setText(getResources().getString(R.string.label_yes));
        declineButton.setText(getResources().getString(R.string.label_no));
        okButton.setOnClickListener(v -> {
            List<ScannedMedicine> scannedList = new ArrayList<>();
            for (ScannedMedicine m : fcmMedicine.getMedicineList()) {
                if (Double.parseDouble(Objects.requireNonNull(m.getArtprice())) > 0) {
                    OCRToDigitalMedicineResponse d = new OCRToDigitalMedicineResponse();
                    d.setArtName(m.getArtName());
                    d.setArtCode(m.getArtCode());
                    d.setQty(m.getQty());
                    d.setArtprice(m.getArtprice());
                    d.setContainer("");
                    d.setMou(m.getMou());
                    dataList.add(d);
                    scannedList.add(m);
                }
            }
            dataList = removeDuplicate(dataList);
            SessionManager.INSTANCE.setScannedPrescriptionItems(scannedList);
            SessionManager.INSTANCE.setCurationStatus(false);
            if (null != SessionManager.INSTANCE.getDataList()) {
                if (SessionManager.INSTANCE.getDataList().size() > 0) {
                    List<OCRToDigitalMedicineResponse> tempCartItemList = new ArrayList<>();
                    tempCartItemList = SessionManager.INSTANCE.getDataList();
                    for (OCRToDigitalMedicineResponse item : tempCartItemList) {
                        dataList.add(item);
                    }
                }
            }
            SessionManager.INSTANCE.setDataList(dataList);
            cartCount(SessionManager.INSTANCE.getDataList().size());
            Utils.showSnackbar(MyOffersActivity.this, constraintLayout, getResources().getString(R.string.label_medicine_added_success));
            dialog.dismiss();
        });
        declineButton.setOnClickListener(v -> dialog.dismiss());
    }

    public List<OCRToDigitalMedicineResponse> removeDuplicate
            (List<OCRToDigitalMedicineResponse> dataList) {
        Set<OCRToDigitalMedicineResponse> s = new TreeSet<OCRToDigitalMedicineResponse>(new Comparator<OCRToDigitalMedicineResponse>() {
            @Override
            public int compare(OCRToDigitalMedicineResponse o1, OCRToDigitalMedicineResponse o2) {
                if (o1.getArtCode().equalsIgnoreCase(o2.getArtCode())) {
                    return 0;
                }
                return 1;
            }
        });
        s.addAll(dataList);
        return new ArrayList<>(s);
    }

    @Override
    public void checkoutData() {
        finish();
        Intent intent1 = new Intent(MyOffersActivity.this, MyCartActivity.class);
        intent1.putExtra("activityname", "AddMoreActivity");
        startActivity(intent1);
        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
    }

    class RetrivePDFfromUrl extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            // we are using inputstream
            // for getting out PDF.
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                // below is the step where we are
                // creating our connection.
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    // response is success.
                    // we are getting input stream from url
                    // and storing it in our variable.
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }

            } catch (IOException e) {
                // this is the method
                // to handle errors.
                e.printStackTrace();
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            // after the execution of our async
            // task we are loading our pdf in our pdf view.
//            mPdfView.fromStream(inputStream).load();
        }

    }

//    private void initWebView() {
//        mPdfView.setWebChromeClient(new MyWebChromeClient(getBaseContext()));
//        mPdfView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                Utils.showDialog(MyOffersActivity.this, "Please Wait");
//            }
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
////                if (url.endsWith(".pdf")) {
////                    String pdfUrl = googleDocs + url;
////                    mPdfView.loadUrl(pdfUrl);
////                } else {
////                mPdfView.loadUrl(url);
////                }
////                return true;
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                Utils.dismissDialog();
//            }
//
//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                super.onReceivedError(view, request, error);
//                Utils.dismissDialog();
//            }
//        });
////        mPdfView.clearCache(true);
////        mPdfView.clearHistory();
////        mPdfView.getSettings().setJavaScriptEnabled(true);
////        mPdfView.getSettings().setLoadWithOverviewMode(true);
////        mPdfView.getSettings().setUseWideViewPort(true);
//
//    }

//    private class MyWebChromeClient extends WebChromeClient {
//        Context context;
//
//        public MyWebChromeClient(Context context) {
//            super();
//            this.context = context;
//        }
//    }



    void startTimer() {

        cTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished < 10) {
                    millisUntilFinished = Long.parseLong("0" + (millisUntilFinished / 1000));
                    newLoginScreenBinding.timerNewlogin.setText("" + (millisUntilFinished));
                } else {
                    newLoginScreenBinding.timerNewlogin.setText("00:" + (millisUntilFinished / 1000));
                }


            }

            public void onFinish() {
                newLoginScreenBinding.sendCodeinText.setVisibility(View.GONE);
                newLoginScreenBinding.timerNewlogin.setVisibility(View.GONE);
                newLoginScreenBinding.resendButtonNewLogin.setVisibility(View.VISIBLE);
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }
}