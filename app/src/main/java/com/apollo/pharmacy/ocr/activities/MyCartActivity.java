package com.apollo.pharmacy.ocr.activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.checkout.CheckoutActivity;
import com.apollo.pharmacy.ocr.activities.userlogin.UserLoginActivity;
import com.apollo.pharmacy.ocr.adapters.CrossCellAdapter;
import com.apollo.pharmacy.ocr.adapters.MyCartListAdapter;
import com.apollo.pharmacy.ocr.adapters.PromotionsAdapter;
import com.apollo.pharmacy.ocr.adapters.TrendingNowAdapter;
import com.apollo.pharmacy.ocr.adapters.UpCellAdapter;
import com.apollo.pharmacy.ocr.controller.MyCartController;
import com.apollo.pharmacy.ocr.controller.UploadBgImageController;
import com.apollo.pharmacy.ocr.databinding.NewLoginScreenBinding;
import com.apollo.pharmacy.ocr.dialog.CartDeletedItemsDialog;
import com.apollo.pharmacy.ocr.dialog.ItemBatchSelectionDilaog;
import com.apollo.pharmacy.ocr.dialog.ScannedPrescriptionViewDialog;
import com.apollo.pharmacy.ocr.enums.ViewMode;
import com.apollo.pharmacy.ocr.interfaces.CartCountListener;
import com.apollo.pharmacy.ocr.interfaces.CheckPrescriptionListener;
import com.apollo.pharmacy.ocr.interfaces.MyCartListener;
import com.apollo.pharmacy.ocr.interfaces.OnItemClickListener;
import com.apollo.pharmacy.ocr.interfaces.UploadBgImageListener;
import com.apollo.pharmacy.ocr.model.BatchListResponse;
import com.apollo.pharmacy.ocr.model.CalculatePosTransactionRequest;
import com.apollo.pharmacy.ocr.model.CalculatePosTransactionResponse;
import com.apollo.pharmacy.ocr.model.GetImageRes;
import com.apollo.pharmacy.ocr.model.GetProductListResponse;
import com.apollo.pharmacy.ocr.model.ItemSearchResponse;
import com.apollo.pharmacy.ocr.model.OCRToDigitalMedicineResponse;
import com.apollo.pharmacy.ocr.model.Product;
import com.apollo.pharmacy.ocr.model.ProductSearch;
import com.apollo.pharmacy.ocr.model.ScannedData;
import com.apollo.pharmacy.ocr.model.ScannedMedicine;
import com.apollo.pharmacy.ocr.model.UpCellCrossCellResponse;
import com.apollo.pharmacy.ocr.model.UserAddress;
import com.apollo.pharmacy.ocr.receiver.ConnectivityReceiver;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.utility.NetworkUtils;
import com.apollo.pharmacy.ocr.utility.Session;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;
import com.apollo.pharmacy.ocr.zebrasdk.BaseActivity;
import com.apollo.pharmacy.ocr.zebrasdk.helper.ScannerAppEngine;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.zebra.scannercontrol.FirmwareUpdateEvent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import static com.apollo.pharmacy.ocr.activities.HomeActivity.mobileNum;

public class MyCartActivity extends BaseActivity implements OnItemClickListener, CartCountListener, MyCartListener, ItemBatchSelectionDilaog.ItemBatchListDialogListener,
        ConnectivityReceiver.ConnectivityReceiverListener, CheckPrescriptionListener, UploadBgImageListener, ScannerAppEngine.IScannerAppEngineDevEventsDelegate {

    private ImageView checkOutImage;
    private LinearLayout continueShoppingLayout;
    private PromotionsAdapter promotionadaptor;
    private TrendingNowAdapter trendingNowAdapter;
    private ArrayList<Product> outOfStockArrayList, inStockArrayList, outOfStockArrayList1, inStockArrayList1;
    private ArrayList<Product> product_list_array, product_list_array1;
    private List<OCRToDigitalMedicineResponse> dataList;
    Context context;
    CountDownTimer cTimer = null;
    public boolean isResend = false;
    NewLoginScreenBinding newLoginScreenBinding;
    private List<OCRToDigitalMedicineResponse> dataComparingList = new ArrayList<>();
    private List<OCRToDigitalMedicineResponse> expandholdedDataList = new ArrayList<>();
    private List<OCRToDigitalMedicineResponse> labelDataList = new ArrayList<>();

    private List<OCRToDigitalMedicineResponse> deletedataList;
    private Boolean existelemnt;
    private List<String> SpecialOfferList;
    private int totalPage = 1, currentPage = 1;
    private int totalPage1 = 1, currentPage1 = 1;
    boolean isEmpty = false;
    boolean isEmpty1 = false;
    private TextView total_itemcount_textview;
    private LinearLayout cartItemCountLayout;
    private RelativeLayout addAgainLayout;
    private ConstraintLayout constraint_Layout;
    private CountDownTimer countdown_timer = null;
    public String message_string;
    public String delete_item_sku;
    private TextView removedItemsCount;
    private TextView grandTotalPrice;
    private String grandTotalPriceValue = "";
    private TextView myCartCount;
    private LinearLayout curationViewLayout;
    private LinearLayout curationProcessLayout;
    private final List<ScannedMedicine> dialogMedicineList = new ArrayList<>();
    private RelativeLayout bottomSheetLayout;
    private BottomSheetBehavior sheetBehaviour;
    private final ScannedData presSelectedItem = null;
    private CartDeletedItemsDialog cartDeleteItemDialog;
    private boolean curationFlag = false, curationDoneFlag = false;
    private long fixedtime_period_millisec = 300000;
    private long fixedScanTime = 300000;
    private long timeperiod_1_millisce;
    private long timeperiod_2_millisec;
    private Session session;
    private MyCartListAdapter cartListAdapter;
    private RecyclerView cartItemRecyclerView;
    private TextView scannedImageHeader;
    private FrameLayout scannedImageLayout;
    private ImageView scannedImage;
    private MyCartController myCartController;
    private UploadBgImageController uploadBgImageController;
    private static final String TAG = MyCartActivity.class.getSimpleName();
    private TextView timerHeaderText, timerChildText;
    private long minVal = 0, secVal = 0;
    RecyclerView crossCell_recycle, upcell_recycle;
    TextView noDataFound, noDataFoundCrosssel, noDataFoundUpsel;
    private boolean isDialogShow = false;
    private List<OCRToDigitalMedicineResponse> dataListSphare;
    private EditText usbScanEdit;
    private String oldMobileNum = "";
    private int otp = 0;
    //    private DialogLoginPopupBinding dialogLoginPopupBinding;
    Dialog dialog;


    @Override
    public void onSuccessProductList(HashMap<String, GetProductListResponse> productList) {
        if (productList != null && productList.size() > 0) {
            if (NetworkUtils.isNetworkConnected(MyCartActivity.this)) {
                SpecialOfferList = myCartController.getSpecialOffersCategoryList();
            } else {
                Utils.showSnackbar(MyCartActivity.this, constraint_Layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
            }
            if (null != SpecialOfferList && SpecialOfferList.size() > 0) {
                for (String s : SpecialOfferList) {
                    if (s.equalsIgnoreCase("Promotions")) {
                        totalPage = (productList.get(s).getProductCount() / 20);
                        if (productList.get(s).getProductCount() % 3 != 0) {
                            totalPage++;
                        }
                        ArrayList<Product> products = (ArrayList<Product>) productList.get(s).getProducts();
                        for (Product d : products) {
                            Product load_obj = new Product();
                            load_obj.setName(d.getName());
                            load_obj.setSku(d.getSku());
                            load_obj.setPrice(d.getPrice());
                            load_obj.setImage(d.getImage());
                            load_obj.setSpecialPrice(d.getSpecialPrice());
                            load_obj.setProduct_pricetype("offer");
                            load_obj.setIsInStock(d.getIsInStock());
                            load_obj.setMou(d.getMou());
                            load_obj.setId(d.getId());
                            if (d.getIsInStock() == 1) {
                                inStockArrayList.add(load_obj);
                            } else {
                                outOfStockArrayList.add(load_obj);
                            }
                        }
                        ArrayList<Product> sessionOfferList = new ArrayList<>();
                        if (inStockArrayList.size() >= 3) {
                            product_list_array.add(inStockArrayList.get(0));
                            product_list_array.add(inStockArrayList.get(1));
                            product_list_array.add(inStockArrayList.get(2));

                            sessionOfferList.clear();
                            sessionOfferList.add(inStockArrayList.get(0));
                            sessionOfferList.add(inStockArrayList.get(1));
                            sessionOfferList.add(inStockArrayList.get(2));
                            sessionOfferList.add(inStockArrayList.get(3));
                            sessionOfferList.add(inStockArrayList.get(4));
                            sessionOfferList.add(inStockArrayList.get(5));
                            SessionManager.INSTANCE.setOfferList(sessionOfferList);
                        } else {
                            product_list_array.addAll(inStockArrayList);
                        }
                        if (totalPage > currentPage) {
                            product_list_array.remove(0);
                        }
                        promotionadaptor.notifyDataSetChanged();
                    } else {
                        totalPage1 = (productList.get(s).getProductCount() / 20);
                        if (productList.get(s).getProductCount() % 3 != 0) {
                            totalPage1++;
                        }
                        ArrayList<Product> products = (ArrayList<Product>) productList.get(s).getProducts();
                        for (Product d : products) {
                            Product load_obj = new Product();
                            load_obj.setName(d.getName());
                            load_obj.setSku(d.getSku());
                            load_obj.setPrice(d.getPrice());
                            load_obj.setImage(d.getImage());
                            load_obj.setSpecialPrice(d.getSpecialPrice());
                            load_obj.setProduct_pricetype("trendingnow");
                            load_obj.setIsInStock(d.getIsInStock());
                            load_obj.setMou(d.getMou());
                            load_obj.setId(d.getId());
                            if (d.getIsInStock() == 1) {
                                inStockArrayList1.add(load_obj);
                            } else {
                                outOfStockArrayList1.add(load_obj);
                            }
                        }
                        ArrayList<Product> sessionTrendingList = new ArrayList<>();
                        if (inStockArrayList1.size() >= 3) {
                            product_list_array1.add(inStockArrayList1.get(0));
                            product_list_array1.add(inStockArrayList1.get(1));
                            product_list_array1.add(inStockArrayList1.get(2));
                            sessionTrendingList.clear();
                            sessionTrendingList.add(inStockArrayList1.get(0));
                            sessionTrendingList.add(inStockArrayList1.get(1));
                            sessionTrendingList.add(inStockArrayList1.get(2));
                            SessionManager.INSTANCE.setTrendingList(sessionTrendingList);
                        } else {
                            product_list_array1.addAll(inStockArrayList1);
                        }
                        if (totalPage1 > currentPage1) {
                            product_list_array1.remove(0);
                        }
                        trendingNowAdapter.notifyDataSetChanged();
                    }
                }
            }
        } else {
            Utils.showCustomAlertDialog(this, getApplicationContext().getResources().getString(R.string.try_again_later), false, getApplicationContext().getResources().getString(R.string.label_ok), "");
        }
    }

    private boolean onItemAddAgainClick;

    @Override
    public void onDeleteItemClicked(OCRToDigitalMedicineResponse ocrToDigitalMedicineResponse, int position) {
        cartDeleteItemDialog.dismiss();
//        deletedataList.remove(position);
        String s1 = ocrToDigitalMedicineResponse.getArtCode();
        String[] words = s1.split(",");
        String itemId = words[0];
        String batchItemId = words[1];
        SessionManager.INSTANCE.setBatchId(batchItemId);
        SessionManager.INSTANCE.getBatchId();
//        SessionManager.INSTANCE.setDeletedDataList(deletedataList);
//        cartUniqueCount();
//        total_itemcount_textview.setText(String.valueOf(countUniques.size()));
//        cartCount(dataList.size());
        onItemAddAgainClick = true;
        myCartController.searchItemProducts(itemId, 0, ocrToDigitalMedicineResponse.getQty(), position);

//        if (dataList.size() > 0) {
//            float grandTotalVal = 0;
//            for (int i = 0; i < dataList.size(); i++) {
//                if (!TextUtils.isEmpty(dataList.get(i).getArtprice())) {
//                    float totalPrice = Float.parseFloat(dataList.get(i).getArtprice()) * dataList.get(i).getQty();
//                    grandTotalVal = grandTotalVal + totalPrice;
//                }
//            }
//            String rupeeSymbol = "\u20B9";
//            grandTotalPrice.setText(rupeeSymbol + "" + String.format("%.2f", grandTotalVal));
//            grandTotalPriceValue = grandTotalPrice.getText().toString();
//            cartListAdapter.notifyDataSetChanged();
//            checkOutImage.setImageResource(R.drawable.checkout_cart);
//        }
//        if (SessionManager.INSTANCE.getDeletedDataList().size() > 0) {
//            addAgainLayout.setVisibility(View.VISIBLE);
//            removedItemsCount.setText(String.valueOf(SessionManager.INSTANCE.getDeletedDataList().size()));
//        } else {
//            addAgainLayout.setVisibility(View.GONE);
//        }
//        if (SessionManager.INSTANCE.getCurationStatus()) {
//            curationProcessLayout.setVisibility(View.VISIBLE);
//        } else {
//            curationProcessLayout.setVisibility(View.GONE);
//        }
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
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceivers);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiverNew);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiverCheckOut);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPrescriptionMessageReceiver);
        super.onPause();
        Constants.getInstance().setConnectivityListener(null);
    }

    private final BroadcastReceiver mMessageReceiverCheckOut = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (message.equals("Medicinedata")) {
                if (SessionManager.INSTANCE.getFcmMedicineReceived()) {
                    SessionManager.INSTANCE.setFcmMedicineReceived(false);
                    final Dialog dialog = new Dialog(MyCartActivity.this);
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
                    okButton.setText(getResources().getString(R.string.label_ok));
                    declineButton.setText(getResources().getString(R.string.label_no));
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            SessionManager.INSTANCE.setCurationStatus(false);
                            curationDoneFlag = true;
                            curationViewLayout.setVisibility(View.GONE);
                            curationProcessLayout.setVisibility(View.GONE);
                            if (countdown_timer != null) {
                                countdown_timer.cancel();
                            }
                            ScannedData fcmMedicine = new Gson().fromJson(intent.getStringExtra("MedininesNames"), ScannedData.class);
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
                            if (null != dataList && dataList.size() > 0) {
                                cartItemCountLayout.setVisibility(View.VISIBLE);
                                cartUniqueCount();
                                total_itemcount_textview.setText(String.valueOf(countUniques.size()));
                                checkOutImage.setImageResource(R.drawable.checkout_cart);
                            } else {
                                cartItemCountLayout.setVisibility(View.GONE);
                                checkOutImage.setImageResource(R.drawable.checkout_cart_unselect);
                            }
                            if (dataList.size() > 0) {
                                float grandTotalVal = 0;
                                for (int i = 0; i < dataList.size(); i++) {
                                    if (!TextUtils.isEmpty(dataList.get(i).getArtprice())) {
                                        if (dataList.get(i).getNetAmountInclTax() != null && !dataList.get(i).getNetAmountInclTax().isEmpty()) {
                                            grandTotalVal = grandTotalVal + Float.parseFloat(dataList.get(i).getNetAmountInclTax());
                                        } else {
                                            float totalPrice = Float.parseFloat(dataList.get(i).getArtprice()) * dataList.get(i).getQty();
                                            grandTotalVal = grandTotalVal + totalPrice;
                                        }
                                    }
                                }
                                String rupeeSymbol = "\u20B9";

                                DecimalFormat formatter = new DecimalFormat("#,###.00");
                                String pharmaformatted = formatter.format(grandTotalVal);

                                grandTotalPrice.setText(rupeeSymbol + "" + pharmaformatted);
                                grandTotalPriceValue = grandTotalPrice.getText().toString();
                            }
                            SessionManager.INSTANCE.setDataList(dataList);
                            cartCount(dataList.size());

                            groupingDuplicates();

                            cartListAdapter.notifyDataSetChanged();
                            curationViewLayout.setVisibility(View.GONE);
                            if (countdown_timer != null) {
                                countdown_timer.cancel();
                            }
                        }
                    });

                    declineButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        }
    };

    private final BroadcastReceiver mPrescriptionMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (message.equalsIgnoreCase("ImageName")) {
                //SessionManager.INSTANCE.setDynamicOrderId(intent.getStringExtra("imageId"));
                myCartController.handleImageService(intent.getStringExtra("data"));
            }
        }
    };

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            if (message.equalsIgnoreCase("itemAdded")) {
                String product_sku = intent.getStringExtra("product_sku");
                if (dataList != null && dataList.size() > 0) {
                    for (int n = 0; n < dataList.size(); n++) {
                        OCRToDigitalMedicineResponse object = dataList.get(n);
                        if (object.getArtCode().equalsIgnoreCase(product_sku)) {
                            existelemnt = true;
                        }
                    }
                    if (existelemnt == true) {
                        existelemnt = false;
                        Utils.showSnackbar(MyCartActivity.this, constraint_Layout, getApplicationContext().getResources().getString(R.string.label_item_already_added));
                    } else {
                        String product_name = intent.getStringExtra("product_name");
                        String product_quantyty = intent.getStringExtra("product_quantyty");
                        String product_container = intent.getStringExtra("product_container");
                        OCRToDigitalMedicineResponse loadobject = new OCRToDigitalMedicineResponse();
                        loadobject.setArtCode(product_sku);
                        loadobject.setArtName(product_name);
                        loadobject.setQty(Integer.parseInt(product_quantyty));
                        loadobject.setContainer(product_container);
                        dataList.add(loadobject);
                        existelemnt = false;
                        SessionManager.INSTANCE.setDataList(dataList);
                        if (dataList.size() > 0) {
                            cartItemCountLayout.setVisibility(View.VISIBLE);
                            cartUniqueCount();
                            total_itemcount_textview.setText(String.valueOf(countUniques.size()));
                            checkOutImage.setImageResource(R.drawable.checkout_cart);
                            float grandTotalVal = 0;
                            for (int i = 0; i < dataList.size(); i++) {
                                if (!TextUtils.isEmpty(dataList.get(i).getArtprice())) {
                                    if (dataList.get(i).getNetAmountInclTax() != null && !dataList.get(i).getNetAmountInclTax().isEmpty()) {
                                        grandTotalVal = grandTotalVal + Float.parseFloat(dataList.get(i).getNetAmountInclTax());
                                    } else {
                                        float totalPrice = Float.parseFloat(dataList.get(i).getArtprice()) * dataList.get(i).getQty();
                                        grandTotalVal = grandTotalVal + totalPrice;
                                    }
                                }
                            }
                            String rupeeSymbol = "\u20B9";

                            DecimalFormat formatter = new DecimalFormat("#,###.00");
                            String pharmaformatted = formatter.format(grandTotalVal);

                            grandTotalPrice.setText(rupeeSymbol + "" + pharmaformatted);
                            grandTotalPriceValue = grandTotalPrice.getText().toString();
                        } else {
                            cartItemCountLayout.setVisibility(View.GONE);
                            checkOutImage.setImageResource(R.drawable.checkout_cart_unselect);
                        }

                        groupingDuplicates();

                        cartListAdapter.notifyDataSetChanged();
                    }
                    curationViewLayout.setVisibility(View.GONE);
                    if (countdown_timer != null) {
                        countdown_timer.cancel();
                    }
                } else {
                    String product_name = intent.getStringExtra("product_name");
                    String product_quantyty = intent.getStringExtra("product_quantyty");
                    String product_container = intent.getStringExtra("product_container");
                    OCRToDigitalMedicineResponse loadobject = new OCRToDigitalMedicineResponse();
                    loadobject.setArtCode(product_sku);
                    loadobject.setArtName(product_name);
                    loadobject.setQty(Integer.parseInt(product_quantyty));
                    loadobject.setContainer(product_container);
                    dataList.add(loadobject);
                    SessionManager.INSTANCE.setDataList(dataList);
                    if (null != dataList && dataList.size() > 0) {
                        cartItemCountLayout.setVisibility(View.VISIBLE);
                        cartUniqueCount();
                        total_itemcount_textview.setText(String.valueOf(countUniques.size()));
                        checkOutImage.setImageResource(R.drawable.checkout_cart);
                        float grandTotalVal = 0;
                        for (int i = 0; i < dataList.size(); i++) {
                            if (!TextUtils.isEmpty(dataList.get(i).getArtprice())) {
                                if (dataList.get(i).getNetAmountInclTax() != null && !dataList.get(i).getNetAmountInclTax().isEmpty()) {
                                    grandTotalVal = grandTotalVal + Float.parseFloat(dataList.get(i).getNetAmountInclTax());
                                } else {
                                    float totalPrice = Float.parseFloat(dataList.get(i).getArtprice()) * dataList.get(i).getQty();
                                    grandTotalVal = grandTotalVal + totalPrice;
                                }
                            }
                        }
                        String rupeeSymbol = "\u20B9";
                        DecimalFormat formatter = new DecimalFormat("#,###.00");
                        String pharmaformatted = formatter.format(grandTotalVal);

                        grandTotalPrice.setText(rupeeSymbol + "" + pharmaformatted);
                        grandTotalPriceValue = grandTotalPrice.getText().toString();
                    } else {
                        cartItemCountLayout.setVisibility(View.GONE);
                        checkOutImage.setImageResource(R.drawable.checkout_cart_unselect);
                    }

                    groupingDuplicates();

                    cartListAdapter.notifyDataSetChanged();
                    curationViewLayout.setVisibility(View.GONE);
                }
            } else if (message.equalsIgnoreCase("addagainitem")) {
                cartDeleteItemDialog.dismiss();
                String product_position = intent.getStringExtra("product_position");
                int int_position = Integer.parseInt(product_position);
                deletedataList.remove(int_position);
                String product_sku = intent.getStringExtra("product_sku");
                String product_name = intent.getStringExtra("product_name");
                String product_quantyty = intent.getStringExtra("product_quantyty");
                String product_container = intent.getStringExtra("product_container");
                String product_price = intent.getStringExtra("product_price");

                OCRToDigitalMedicineResponse loadobject = new OCRToDigitalMedicineResponse();
                loadobject.setArtCode(product_sku);
                loadobject.setArtName(product_name);
                loadobject.setQty(Integer.parseInt(product_quantyty));
                loadobject.setContainer(product_container);
                loadobject.setArtprice(product_price);
                dataList.add(loadobject);
                SessionManager.INSTANCE.setDataList(dataList);
                SessionManager.INSTANCE.setDeletedDataList(deletedataList);
                if (dataList.size() > 0) {
                    float grandTotalVal = 0;
                    for (int i = 0; i < dataList.size(); i++) {
                        if (!TextUtils.isEmpty(dataList.get(i).getArtprice())) {
                            if (dataList.get(i).getNetAmountInclTax() != null && !dataList.get(i).getNetAmountInclTax().isEmpty()) {
                                grandTotalVal = grandTotalVal + Float.parseFloat(dataList.get(i).getNetAmountInclTax());
                            } else {
                                float totalPrice = Float.parseFloat(dataList.get(i).getArtprice()) * dataList.get(i).getQty();
                                grandTotalVal = grandTotalVal + totalPrice;
                            }
                        }
                    }
                    String rupeeSymbol = "\u20B9";

                    DecimalFormat formatter = new DecimalFormat("#,###.00");
                    String pharmaformatted = formatter.format(grandTotalVal);

                    grandTotalPrice.setText(rupeeSymbol + "" + pharmaformatted);
                    grandTotalPriceValue = grandTotalPrice.getText().toString();

                    groupingDuplicates();

                    cartListAdapter.notifyDataSetChanged();
                }
                if (SessionManager.INSTANCE.getDeletedDataList().size() > 0) {
                    addAgainLayout.setVisibility(View.VISIBLE);
                    removedItemsCount.setText(String.valueOf(SessionManager.INSTANCE.getDeletedDataList().size()));
                } else {
                    addAgainLayout.setVisibility(View.GONE);
                }
                if (SessionManager.INSTANCE.getCurationStatus()) {
                    curationProcessLayout.setVisibility(View.VISIBLE);
                } else {
                    curationProcessLayout.setVisibility(View.GONE);
                }
            } else if (message.equalsIgnoreCase("OrderNow")) {
                Intent intent1 = new Intent(context, MyCartActivity.class);
                intent.putExtra("activityname", "MyOrdersActivity");
                startActivity(intent1);
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        ImageView userLogout = findViewById(R.id.userLogout);
        if(!HomeActivity.isLoggedin){
            userLogout.setVisibility(View.GONE);
        }else{
            userLogout.setVisibility(View.VISIBLE);
        }
        addDevEventsDelegate(this);
        MyCartActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        existelemnt = false;
        Constants.getInstance().setConnectivityListener(this);
        if (!ConnectivityReceiver.isConnected()) {
            findViewById(R.id.networkErrorLayout).setVisibility(View.VISIBLE);
        }
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
        if (!HomeActivity.isLoggedin) {
            userLogout.setVisibility(View.GONE);
        } else {
            userLogout.setVisibility(View.VISIBLE);
        }
        userLogout.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(MyCartActivity.this);
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

                HomeActivity.isLoggedin = false;
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("userLoginActivity", "myCartActivityLogout");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finishAffinity();
//                Intent intent = new Intent(MyCartActivity.this, HomeActivity.class);
//                intent.putExtra("userLoginActivity", "myCartActivityLogout");
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

        myCartLayout.setBackgroundResource(R.color.selected_menu_color);
        dashboardMyCartIcon.setImageResource(R.drawable.dashboard_cart_hover);
        dashboardMyCart.setTextColor(getResources().getColor(R.color.selected_text_color));
        dashboardMyCartText.setTextColor(getResources().getColor(R.color.selected_text_color));
        myCartLayout.setClickable(false);

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

            if (session != null) {
                session.setfixedtimeperiod(fixedtime_period_millisec);
                session.settimeperiod1(System.currentTimeMillis());
            }

            Intent intent = new Intent(MyCartActivity.this, MySearchActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });

        myOrdersLayout.setOnClickListener(v -> {

            if (!HomeActivity.isLoggedin) {

                Intent intent1 = new Intent(MyCartActivity.this, UserLoginActivity.class);
                intent1.putExtra("userLoginActivity", "myCartActivityOrdersLogin");
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
//                        EditText editText = findViewById(R.id.usb_scan);
//                        editText.setEnabled(false);
//                        hideKeyboard();
//                        editText.setEnabled(true);
//                        onResumeAfterLogin();
//                    }
//                });
//
//                newLoginScreenBinding.resendButtonNewLogin.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        isResend = true;
//                        Utils.showDialog(MyCartActivity.this, "Sending OTP…");
//                        if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
//                            Send_Sms_Request sms_req = new Send_Sms_Request();
//                            sms_req.setMobileNo(mobileNum);
//                            sms_req.setMessage("Dear Apollo Customer, Your one time password is " + String.valueOf(otp) + " and is valid for 3mins.");
//                            sms_req.setIsOtp(true);
//                            sms_req.setOtp(String.valueOf(otp));
//                            sms_req.setApiType("KIOSk");
//                            myCartController.handleSendSmsApi(sms_req);
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
//                                                Utils.showDialog(MyCartActivity.this, "Sending OTP…");
//                                                otp = (int) ((Math.random() * 9000) + 1000);
//                                                if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
//                                                    Send_Sms_Request sms_req = new Send_Sms_Request();
//                                                    sms_req.setMobileNo(mobileNum);
//                                                    sms_req.setMessage("Dear Apollo Customer, Your one time password is " + String.valueOf(otp) + " and is valid for 3mins.");
//                                                    sms_req.setIsOtp(true);
//                                                    sms_req.setOtp(String.valueOf(otp));
//                                                    sms_req.setApiType("KIOSk");
//                                                    myCartController.handleSendSmsApi(sms_req);
//                                                } else {
//                                                    Utils.showSnackbar(getApplicationContext(), constraint_Layout, "Internet Connection Not Available");
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            } else {
//                                AccesskeyDialog accesskeyDialog = new AccesskeyDialog(MyCartActivity.this);
//                                accesskeyDialog.onClickSubmit(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        accesskeyDialog.listener();
//                                        if (accesskeyDialog.validate()) {
//                                            Intent intent = new Intent(MyCartActivity.this, MposStoreSetupActivity.class);
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
//                                session.setfixedtimeperiod(fixedtime_period_millisec);
//                                session.settimeperiod1(System.currentTimeMillis());
//                                Intent intent1 = new Intent(MyCartActivity.this, MyOrdersActivity.class);
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
//                dialog.show();
//                removeAllExpiryCallbacks();
            } else {
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
                if (session != null) {
                    session.setfixedtimeperiod(fixedtime_period_millisec);
                    session.settimeperiod1(System.currentTimeMillis());
                }
                Intent intent1 = new Intent(MyCartActivity.this, MyOrdersActivity.class);
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
////                                    dialogLoginPopupBinding.mobileNumLoginPopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                                    dialogLoginPopupBinding.accesskeyErrorText.setVisibility(View.VISIBLE);
//                                } else {
////                                    dialogLoginPopupBinding.accesskeyErrorText.setVisibility(View.INVISIBLE);
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
//                                                Utils.showDialog(MyCartActivity.this, "Sending OTP…");
//                                                otp = (int) ((Math.random() * 9000) + 1000);
//                                                if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
//                                                    Send_Sms_Request sms_req = new Send_Sms_Request();
//                                                    sms_req.setMobileNo(mobileNum);
//                                                    sms_req.setMessage("Dear Apollo Customer, Your one time password is " + String.valueOf(otp) + " and is valid for 3mins.");
//                                                    sms_req.setIsOtp(true);
//                                                    sms_req.setOtp(String.valueOf(otp));
//                                                    sms_req.setApiType("KIOSk");
//                                                    myCartController.handleSendSmsApi(sms_req);
//                                                } else {
//                                                    Utils.showSnackbar(getApplicationContext(), constraint_Layout, "Internet Connection Not Available");
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            } else {
//                                AccesskeyDialog accesskeyDialog = new AccesskeyDialog(MyCartActivity.this);
//                                accesskeyDialog.onClickSubmit(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        accesskeyDialog.listener();
//                                        if (accesskeyDialog.validate()) {
//                                            Intent intent = new Intent(MyCartActivity.this, MposStoreSetupActivity.class);
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
//                                session.setfixedtimeperiod(fixedtime_period_millisec);
//                                session.settimeperiod1(System.currentTimeMillis());
//                                Intent intent1 = new Intent(MyCartActivity.this, MyOrdersActivity.class);
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
//                session.setfixedtimeperiod(fixedtime_period_millisec);
//                session.settimeperiod1(System.currentTimeMillis());
//                Intent intent1 = new Intent(MyCartActivity.this, MyOrdersActivity.class);
//                startActivity(intent1);
//                finish();
//                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//            }

        });

        myOffersLayout.setOnClickListener(v -> {
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

            myOffersLayout.setBackgroundResource(R.color.selected_menu_color);
            dashboardMyOffersIcon.setImageResource(R.drawable.dashboard_offers_hover);
            dashboardMyOffers.setTextColor(getResources().getColor(R.color.selected_text_color));
            dashboardMyOffersText.setTextColor(getResources().getColor(R.color.selected_text_color));

            myProfileLayout.setBackgroundResource(R.color.unselected_menu_color);
            dashboardMyProfileIcon.setImageResource(R.drawable.dashboard_profile);
            dashboardMyProfile.setTextColor(getResources().getColor(R.color.colorWhite));
            dashboardMyProfileText.setTextColor(getResources().getColor(R.color.colorWhite));

            if (session != null) {
                session.setfixedtimeperiod(fixedtime_period_millisec);
                session.settimeperiod1(System.currentTimeMillis());
            }
            Intent intent = new Intent(MyCartActivity.this, MyOffersActivity.class);
            intent.putExtra("categoryname", "Promotions");
            startActivity(intent);
            finish();
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });

        myProfileLayout.setOnClickListener(v -> {


            if (!HomeActivity.isLoggedin) {

                Intent intent1 = new Intent(MyCartActivity.this, UserLoginActivity.class);
                intent1.putExtra("userLoginActivity", "myCartActivityProfileLogin");
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
//                        isResend = true;
//                        Utils.showDialog(MyCartActivity.this, "Sending OTP…");
//                        if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
//                            Send_Sms_Request sms_req = new Send_Sms_Request();
//                            sms_req.setMobileNo(mobileNum);
//                            sms_req.setMessage("Dear Apollo Customer, Your one time password is " + String.valueOf(otp) + " and is valid for 3mins.");
//                            sms_req.setIsOtp(true);
//                            sms_req.setOtp(String.valueOf(otp));
//                            sms_req.setApiType("KIOSk");
//                            myCartController.handleSendSmsApi(sms_req);
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
//                                                Utils.showDialog(MyCartActivity.this, "Sending OTP…");
//                                                otp = (int) ((Math.random() * 9000) + 1000);
//                                                if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
//                                                    Send_Sms_Request sms_req = new Send_Sms_Request();
//                                                    sms_req.setMobileNo(mobileNum);
//                                                    sms_req.setMessage("Dear Apollo Customer, Your one time password is " + String.valueOf(otp) + " and is valid for 3mins.");
//                                                    sms_req.setIsOtp(true);
//                                                    sms_req.setOtp(String.valueOf(otp));
//                                                    sms_req.setApiType("KIOSk");
//                                                    myCartController.handleSendSmsApi(sms_req);
//                                                } else {
//                                                    Utils.showSnackbar(getApplicationContext(), constraint_Layout, "Internet Connection Not Available");
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            } else {
//                                AccesskeyDialog accesskeyDialog = new AccesskeyDialog(MyCartActivity.this);
//                                accesskeyDialog.onClickSubmit(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        accesskeyDialog.listener();
//                                        if (accesskeyDialog.validate()) {
//                                            Intent intent = new Intent(MyCartActivity.this, MposStoreSetupActivity.class);
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
//                                session.setfixedtimeperiod(fixedtime_period_millisec);
//                                session.settimeperiod1(System.currentTimeMillis());
//                                Intent intent1 = new Intent(MyCartActivity.this, MyProfileActivity.class);
//                                startActivity(intent1);
//                                finish();
//                                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//
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
            } else {
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
                if (session != null) {
                    session.setfixedtimeperiod(fixedtime_period_millisec);
                    session.settimeperiod1(System.currentTimeMillis());
                }
                Intent intent1 = new Intent(MyCartActivity.this, MyProfileActivity.class);
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
//                                                Utils.showDialog(MyCartActivity.this, "Sending OTP…");
//                                                otp = (int) ((Math.random() * 9000) + 1000);
//                                                if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
//                                                    Send_Sms_Request sms_req = new Send_Sms_Request();
//                                                    sms_req.setMobileNo(mobileNum);
//                                                    sms_req.setMessage("Dear Apollo Customer, Your one time password is " + String.valueOf(otp) + " and is valid for 3mins.");
//                                                    sms_req.setIsOtp(true);
//                                                    sms_req.setOtp(String.valueOf(otp));
//                                                    sms_req.setApiType("KIOSk");
//                                                    myCartController.handleSendSmsApi(sms_req);
//                                                } else {
//                                                    Utils.showSnackbar(getApplicationContext(), constraint_Layout, "Internet Connection Not Available");
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            } else {
//                                AccesskeyDialog accesskeyDialog = new AccesskeyDialog(MyCartActivity.this);
//                                accesskeyDialog.onClickSubmit(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        accesskeyDialog.listener();
//                                        if (accesskeyDialog.validate()) {
//                                            Intent intent = new Intent(MyCartActivity.this, MposStoreSetupActivity.class);
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
//                                session.setfixedtimeperiod(fixedtime_period_millisec);
//                                session.settimeperiod1(System.currentTimeMillis());
//                                Intent intent1 = new Intent(MyCartActivity.this, MyProfileActivity.class);
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
//                session.setfixedtimeperiod(fixedtime_period_millisec);
//                session.settimeperiod1(System.currentTimeMillis());
//                Intent intent1 = new Intent(MyCartActivity.this, MyProfileActivity.class);
//                startActivity(intent1);
//                finish();
//                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//            }

        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);
        addDevEventsDelegate(this);
        HomeActivity.isPaymentSelectionActivity = false;
        HomeActivity.isHomeActivity = false;
        context = this;
        ImageView userLogout = findViewById(R.id.userLogout);
        if(!HomeActivity.isLoggedin){
            userLogout.setVisibility(View.GONE);
        }else{
            userLogout.setVisibility(View.VISIBLE);
        }
        dataList = new ArrayList<>();
        deletedataList = new ArrayList<>();
        initLeftMenu();
        if (SessionManager.INSTANCE.getDataList() != null && SessionManager.INSTANCE.getDataList().size() > 0) {
            this.dataListSphare = SessionManager.INSTANCE.getDataList();
            for (int i = 0; i < dataListSphare.size(); i++) {
                if (dataListSphare.get(i).getArtCode().contains(",")) {
                    dataListSphare.get(i).setArtCode(dataListSphare.get(i).getArtCode().substring(0, dataListSphare.get(i).getArtCode().indexOf(",")));
                }
            }

            for (int i = 0; i < dataListSphare.size(); i++) {
                for (int j = 0; j < dataListSphare.size(); j++) {
                    if (dataListSphare.get(i).getArtCode().equals(dataListSphare.get(j).getArtCode()) && i != j) {
                        dataListSphare.get(i).setQty(dataListSphare.get(i).getQty() + dataListSphare.get(j).getQty());
                        dataListSphare.remove(j);
                        j--;
                    }
                }
            }
            SessionManager.INSTANCE.setDataList(dataListSphare);
            for (int i = 0; i < dataListSphare.size(); i++) {
                new MyCartController(this).searchItemProducts(dataListSphare.get(i).getArtCode(), i, this);
            }
        } else {
            setUp();
        }

        usbScanEdit = (EditText) findViewById(R.id.usb_scan);
        usbScanEdit.setShowSoftInputOnFocus(false);
        usbScanEdit.requestFocus();

        usbScanEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString() != null && !s.toString().isEmpty()) {
                    usbScanHandler.removeCallbacks(usbScanRunnable);
                    usbScanHandler.postDelayed(usbScanRunnable, 250);
                }
            }
        });
    }

    Handler usbScanHandler = new Handler();
    Runnable usbScanRunnable = new Runnable() {
        @Override
        public void run() {
            Utils.showDialog(MyCartActivity.this, "Plaese wait...");
            new MyCartController(MyCartActivity.this).searchItemProducts(usbScanEdit.getText().toString(), 0, 0, 0);
            usbScanEdit.setText("");
        }
    };

    private void setUp() {
        grandTotalPrice = findViewById(R.id.grand_total_price);
        myCartCount = findViewById(R.id.lblCartCnt);
        constraint_Layout = findViewById(R.id.constraint_layout);
        continueShoppingLayout = findViewById(R.id.continue_shopping);
        ImageView faqLayout = findViewById(R.id.faq);
        TextView helpText = findViewById(R.id.help_text);
        helpText.setText(getResources().getString(R.string.faq));
        faqLayout.setOnClickListener(view -> startActivity(new Intent(MyCartActivity.this, FAQActivity.class)));
        continueShoppingLayout.setOnClickListener(view -> startActivity(new Intent(MyCartActivity.this, MySearchActivity.class)));


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

        RecyclerView recycleView = findViewById(R.id.recycle_view_prescription);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MyCartActivity.this, LinearLayoutManager.VERTICAL, false);
        recycleView.setLayoutManager(layoutManager);
        product_list_array = new ArrayList<Product>();
        product_list_array1 = new ArrayList<Product>();
        total_itemcount_textview = findViewById(R.id.total_itemcount_textview);
        cartItemCountLayout = findViewById(R.id.cart_item_count_layout);

        checkOutImage = findViewById(R.id.checkout_image);
        outOfStockArrayList = new ArrayList<Product>();
        inStockArrayList = new ArrayList<Product>();
        outOfStockArrayList1 = new ArrayList<Product>();
        inStockArrayList1 = new ArrayList<Product>();
        TextView promotionViewAll = findViewById(R.id.promotion_viewAll);
        TextView trendingNowViewAll = findViewById(R.id.trending_now_viewAll);
        curationViewLayout = findViewById(R.id.curation_view_layout);
        TextView curationProcessText = findViewById(R.id.curation_process_text);
        curationProcessLayout = findViewById(R.id.curation_process_layout);
        TextView curationText = findViewById(R.id.curation_text);
        addAgainLayout = findViewById(R.id.add_again_layout);
        removedItemsCount = findViewById(R.id.removed_items_count);

        scannedImageHeader = findViewById(R.id.scanned_image_header);
        scannedImageLayout = findViewById(R.id.scanned_image_layout);
        scannedImage = findViewById(R.id.scanned_prescription_image);
        myCartController = new MyCartController(this);
        uploadBgImageController = new UploadBgImageController(this);
        timerHeaderText = findViewById(R.id.timer_header_text);
        timerChildText = findViewById(R.id.timer_child_text);

        handleScannedImageView();

        ImageView offersCloseImage = findViewById(R.id.offers_close_image);
        ImageView tendingCloseImage = findViewById(R.id.tending_close_image);
        ImageView cartBottomImage = findViewById(R.id.cart_bottom_image);
        bottomSheetLayout = findViewById(R.id.bottom_sheet);
        sheetBehaviour = BottomSheetBehavior.from(bottomSheetLayout);
        sheetBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        cartBottomImage.setRotation(0);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        cartBottomImage.setRotation(180);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        bottomSheetLayout.setOnClickListener(v -> {
            if (sheetBehaviour.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                cartBottomImage.setRotation(0);
                sheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                cartBottomImage.setRotation(180);
                sheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        offersCloseImage.setOnClickListener(v -> {
            bottomSheetLayout.setVisibility(View.GONE);
        });
        tendingCloseImage.setOnClickListener(v -> {
            bottomSheetLayout.setVisibility(View.GONE);
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        String rupeeSymbol = "\u20B9";

        DecimalFormat formatter = new DecimalFormat("#,###.00");
        String pharmaformatted = formatter.format(00.00);

        grandTotalPrice.setText(rupeeSymbol + pharmaformatted);

        if (ConnectivityReceiver.isConnected()) {
            findViewById(R.id.networkErrorLayout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.networkErrorLayout).setVisibility(View.VISIBLE);
        }

        myCartController = new MyCartController(this);
        myCartController.upcellCrosscellList("7353910637", MyCartActivity.this);
        upcell_recycle = findViewById(R.id.up_cell_data_recycle);
        crossCell_recycle = findViewById(R.id.cross_cell_data_recycle);
        noDataFound = findViewById(R.id.no_data);
        noDataFoundCrosssel = findViewById(R.id.no_data_found_crosssell);
        noDataFoundUpsel = findViewById(R.id.no_data_found_upsell);
        RecyclerView promotionproductrecycleerview = findViewById(R.id.promotionsRecyclerView);
        promotionproductrecycleerview.setLayoutManager(new GridLayoutManager(MyCartActivity.this, 3));
        RecyclerView trendingnowproductrecycleerview = findViewById(R.id.trendingnowproductrecycleerview);
        trendingnowproductrecycleerview.setLayoutManager(new GridLayoutManager(MyCartActivity.this, 3));
        if (SessionManager.INSTANCE.getOrderCompletionStatus()) {
            curationViewLayout.setVisibility(View.GONE);
            curationProcessLayout.setVisibility(View.GONE);
        }
        if (SessionManager.INSTANCE.getCurationStatus()) {
            curationFlag = true;
            curationViewLayout.setVisibility(View.VISIBLE);
            curationProcessLayout.setVisibility(View.GONE);
        } else {
            curationFlag = false;
            curationViewLayout.setVisibility(View.GONE);
            curationProcessLayout.setVisibility(View.GONE);
        }

        if (null != getIntent().getExtras()) {
            String activityname = this.getIntent().getExtras().getString("activityname");
            if (activityname.equalsIgnoreCase("DeliverySelectionActivity")) {
                if (SessionManager.INSTANCE.getDataList() != null) {
                    dataList = SessionManager.INSTANCE.getDataList();
                }
                if (null != dataList && dataList.size() > 0) {
                    cartItemCountLayout.setVisibility(View.VISIBLE);
                    cartUniqueCount();
                    total_itemcount_textview.setText(String.valueOf(countUniques.size()));
                    checkOutImage.setImageResource(R.drawable.checkout_cart);
                    curationViewLayout.setVisibility(View.GONE);
                    curationProcessText.setVisibility(View.GONE);
                    if (dataList.size() > 0) {
                        float grandTotalVal = 0;
                        for (int i = 0; i < dataList.size(); i++) {
                            if (!TextUtils.isEmpty(dataList.get(i).getArtprice())) {
                                if (dataList.get(i).getNetAmountInclTax() != null && !dataList.get(i).getNetAmountInclTax().isEmpty()) {
                                    grandTotalVal = grandTotalVal + Float.parseFloat(dataList.get(i).getNetAmountInclTax());
                                } else {
                                    float totalPrice = Float.parseFloat(dataList.get(i).getArtprice()) * dataList.get(i).getQty();
                                    grandTotalVal = grandTotalVal + totalPrice;
                                }
                            }
                        }
                        DecimalFormat formatter1 = new DecimalFormat("#,###.00");
                        String pharmaformatted1 = formatter1.format(grandTotalVal);

                        grandTotalPrice.setText(rupeeSymbol + "" + pharmaformatted1);
                        grandTotalPriceValue = grandTotalPrice.getText().toString();
                    }
                } else {
                    cartItemCountLayout.setVisibility(View.GONE);
                    checkOutImage.setImageResource(R.drawable.checkout_cart_unselect);
                }
            } else if (activityname.equalsIgnoreCase("AddMoreActivity")) {
                dataList = SessionManager.INSTANCE.getDataList();
                if (null != dataList && dataList.size() > 0) {
                    cartItemCountLayout.setVisibility(View.VISIBLE);
                    cartUniqueCount();
                    total_itemcount_textview.setText(String.valueOf(countUniques.size()));
                    checkOutImage.setImageResource(R.drawable.checkout_cart);
                    curationViewLayout.setVisibility(View.GONE);
                    if (SessionManager.INSTANCE.getCurationStatus()) {
                        curationProcessLayout.setVisibility(View.VISIBLE);
                    } else {
                        curationProcessLayout.setVisibility(View.VISIBLE);
                    }
                    if (dataList.size() > 0) {
                        float grandTotalVal = 0;
                        for (int i = 0; i < dataList.size(); i++) {
                            if (dataList.get(i).getArtprice() != null) {
                                if (!TextUtils.isEmpty(dataList.get(i).getArtprice())) {
                                    if (dataList.get(i).getNetAmountInclTax() != null && !dataList.get(i).getNetAmountInclTax().isEmpty()) {
                                        grandTotalVal = grandTotalVal + Float.parseFloat(dataList.get(i).getNetAmountInclTax());
                                    } else {
                                        float totalPrice = Float.parseFloat(dataList.get(i).getArtprice()) * dataList.get(i).getQty();
                                        grandTotalVal = grandTotalVal + totalPrice;
                                    }
                                }
                            }
                        }

                        DecimalFormat formatter1 = new DecimalFormat("#,###.00");
                        String pharmaformatted1 = formatter1.format(grandTotalVal);

                        grandTotalPrice.setText(rupeeSymbol + "" + pharmaformatted1);
                        grandTotalPriceValue = grandTotalPrice.getText().toString();
                    }
                    if (countdown_timer != null) {
                        countdown_timer.cancel();
                    }
                } else {
                    cartItemCountLayout.setVisibility(View.GONE);
                    checkOutImage.setImageResource(R.drawable.checkout_cart_unselect);
                }
            } else if (activityname.equalsIgnoreCase("MyOrdersActivity")) {
                dataList = SessionManager.INSTANCE.getDataList();
                if (null != dataList && dataList.size() > 0) {
                    cartItemCountLayout.setVisibility(View.VISIBLE);
                    cartUniqueCount();
                    total_itemcount_textview.setText(String.valueOf(countUniques.size()));
                    checkOutImage.setImageResource(R.drawable.checkout_cart);
                    curationViewLayout.setVisibility(View.GONE);
                    if (SessionManager.INSTANCE.getCurationStatus()) {
                        curationProcessLayout.setVisibility(View.VISIBLE);
                    } else {
                        curationProcessLayout.setVisibility(View.GONE);
                    }
                    if (countdown_timer != null) {
                        countdown_timer.cancel();
                    }
                } else {
                    cartItemCountLayout.setVisibility(View.GONE);
                    checkOutImage.setImageResource(R.drawable.checkout_cart_unselect);
                }
            } else {
                if (null != dataList && dataList.size() > 0) {
                    curationViewLayout.setVisibility(View.GONE);
                    if (SessionManager.INSTANCE.getCurationStatus()) {
                        curationProcessLayout.setVisibility(View.VISIBLE);
                    } else {
                        curationProcessLayout.setVisibility(View.GONE);
                    }
                    if (countdown_timer != null) {
                        countdown_timer.cancel();
                    }
                }
            }
        }
        boolean curationstatus = SessionManager.INSTANCE.getCurationStatus();
        if (!curationstatus) {
            curationProcessLayout.setVisibility(View.GONE);
        }

        checkOutImage.setOnClickListener(arg0 -> {
            if (dataList != null && dataList.size() > 0) {


                if (!HomeActivity.isLoggedin) {
//                    HomeActivity.isLoggedin = true;
//                   SessionManager.INSTANCE.setMobilenumber("7673930018");
                    Intent intent1 = new Intent(MyCartActivity.this, UserLoginActivity.class);
                    intent1.putExtra("userLoginActivity", "myCartActivityCheckoutLogin");
                    startActivity(intent1);
                    overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);


//                    dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
//
//                    newLoginScreenBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.new_login_screen, null, false);
//                    dialog.setContentView(newLoginScreenBinding.getRoot());
//                    if (dialog.getWindow() != null)
//                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
////                    dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
////                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
////                            WindowManager.LayoutParams.MATCH_PARENT);
//                    dialog.setCancelable(true);
//                    newLoginScreenBinding.mobileNumEditText.requestFocus();
//                    newLoginScreenBinding.closeDialog.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                            onResumeAfterLogin();
//                        }
//                    });
//
//                    newLoginScreenBinding.resendButtonNewLogin.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            isResend = true;
//                            Utils.showDialog(MyCartActivity.this, "Sending OTP…");
//                            if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
//                                Send_Sms_Request sms_req = new Send_Sms_Request();
//                                sms_req.setMobileNo(mobileNum);
//                                sms_req.setMessage("Dear Apollo Customer, Your one time password is " + String.valueOf(otp) + " and is valid for 3mins.");
//                                sms_req.setIsOtp(true);
//                                sms_req.setOtp(String.valueOf(otp));
//                                sms_req.setApiType("KIOSk");
//                                myCartController.handleSendSmsApi(sms_req);
//                            }
//                        }
//                    });
//                    newLoginScreenBinding.submit.setOnClickListener(new View.OnClickListener() {
//                        @SuppressLint("SetTextI18n")
//                        @Override
//                        public void onClick(View v) {
//                            if (newLoginScreenBinding.mobileNumEditText.getText().toString() != null && newLoginScreenBinding.mobileNumEditText.getText().toString() != "") {
//                                if (SessionManager.INSTANCE.getStoreId() != null && !SessionManager.INSTANCE.getStoreId().isEmpty()
//                                        && SessionManager.INSTANCE.getTerminalId() != null && !SessionManager.INSTANCE.getTerminalId().isEmpty() && SessionManager.INSTANCE.getEposUrl() != null && !SessionManager.INSTANCE.getEposUrl().isEmpty()) {
//                                    String MobilePattern = "[0-9]{10}";
//                                    mobileNum = newLoginScreenBinding.mobileNumEditText.getText().toString();
//                                    if (mobileNum.length() < 10) {
//                                        Toast.makeText(getApplicationContext(), "Please enter 10 digit phone number", Toast.LENGTH_SHORT).show();
//                                    } else {
////                                send_otp_image.setImageResource(R.drawable.right_selection_green)
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_country_code_bg)
////                                edittext_error_text.visibility = View.INVISIBLE
//                                        if (oldMobileNum.equals(newLoginScreenBinding.mobileNumEditText.getText().toString()) && newLoginScreenBinding.mobileNumEditText.getText().toString().length() > 0 && (mobileNum.matches(MobilePattern))) {
//                                            newLoginScreenBinding.mobileNumLoginPopup.setVisibility(View.GONE);
//                                            newLoginScreenBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//                                            String phoneNumber = newLoginScreenBinding.mobileNumEditText.getText().toString().trim();
//                                            int firstDigit = Integer.parseInt((phoneNumber).substring(0, 1));
//                                            String strTwoDigits = phoneNumber.length() >= 4 ? phoneNumber.substring(phoneNumber.length() - 2) : "";
//                                            newLoginScreenBinding.mobileNumStars.setText(firstDigit + "*******" + strTwoDigits);
//                                            newLoginScreenBinding.timerNewlogin.setText("");
//                                            cancelTimer();
//                                            startTimer();
//                                        } else {
//                                            newLoginScreenBinding.mobileNumLoginPopup.setVisibility(View.GONE);
//                                            newLoginScreenBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//                                            String phoneNumber = newLoginScreenBinding.mobileNumEditText.getText().toString().trim();
//                                            int firstDigit = Integer.parseInt((phoneNumber).substring(0, 1));
//                                            String strTwoDigits = phoneNumber.length() >= 4 ? phoneNumber.substring(phoneNumber.length() - 2) : "";
//                                            newLoginScreenBinding.mobileNumStars.setText(firstDigit + "*******" + strTwoDigits);
//                                            newLoginScreenBinding.timerNewlogin.setText("");
//                                            cancelTimer();
//                                            startTimer();
//
//
//                                            if (newLoginScreenBinding.mobileNumEditText.getText().toString().length() > 0) {
//                                                oldMobileNum = newLoginScreenBinding.mobileNumEditText.getText().toString();
//                                                if (mobileNum.matches(MobilePattern)) {
//                                                    Utils.showDialog(MyCartActivity.this, "Sending OTP…");
//                                                    otp = (int) ((Math.random() * 9000) + 1000);
//                                                    if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
//                                                        Send_Sms_Request sms_req = new Send_Sms_Request();
//                                                        sms_req.setMobileNo(mobileNum);
//                                                        sms_req.setMessage("Dear Apollo Customer, Your one time password is " + String.valueOf(otp) + " and is valid for 3mins.");
//                                                        sms_req.setIsOtp(true);
//                                                        sms_req.setOtp(String.valueOf(otp));
//                                                        sms_req.setApiType("KIOSk");
//                                                        myCartController.handleSendSmsApi(sms_req);
//                                                    } else {
//                                                        Utils.showSnackbar(getApplicationContext(), constraint_Layout, "Internet Connection Not Available");
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                } else {
//                                    AccesskeyDialog accesskeyDialog = new AccesskeyDialog(MyCartActivity.this);
//                                    accesskeyDialog.onClickSubmit(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            accesskeyDialog.listener();
//                                            if (accesskeyDialog.validate()) {
//                                                Intent intent = new Intent(MyCartActivity.this, MposStoreSetupActivity.class);
//                                                startActivity(intent);
//                                                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                                                accesskeyDialog.dismiss();
//                                            }
//                                        }
//                                    });
//
//
//                                    accesskeyDialog.show();
////                Intent intent = new Intent(MainActivity.this, MposStoreSetupActivity.class);
////                startActivity(intent);
////                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                                }
//                            }
////                    dialog.dismiss();
//                        }
//                    });
//
//                    newLoginScreenBinding.otplayoutEditText1.addTextChangedListener(new TextWatcher() {
//                        @Override
//                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                        }
//
//                        @Override
//                        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                        }
//
//                        @Override
//                        public void afterTextChanged(Editable editable) {
//                            if (editable.length() == 1) {
//                                newLoginScreenBinding.otplayoutEditText1.setBackgroundResource(R.drawable.backgroundforotpblack);
//                                newLoginScreenBinding.otplayoutEditText2.requestFocus();
//                            } else {
//                                newLoginScreenBinding.otplayoutEditText1.setBackgroundResource(R.drawable.backgroundforotp);
//                            }
//                        }
//                    });
//                    newLoginScreenBinding.otplayoutEditText2.addTextChangedListener(new TextWatcher() {
//                        @Override
//                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                        }
//
//                        @Override
//                        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                        }
//
//                        @Override
//                        public void afterTextChanged(Editable editable) {
//                            if (editable.length() == 1) {
//                                newLoginScreenBinding.otplayoutEditText2.setBackgroundResource(R.drawable.backgroundforotpblack);
//                                newLoginScreenBinding.otplayoutEditText3.requestFocus();
//                            } else {
//                                newLoginScreenBinding.otplayoutEditText2.setBackgroundResource(R.drawable.backgroundforotp);
//                                newLoginScreenBinding.otplayoutEditText1.requestFocus();
//                            }
//                        }
//                    });
//                    newLoginScreenBinding.otplayoutEditText3.addTextChangedListener(new TextWatcher() {
//                        @Override
//                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                        }
//
//                        @Override
//                        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                        }
//
//                        @Override
//                        public void afterTextChanged(Editable editable) {
//                            if (editable.length() == 1) {
//                                newLoginScreenBinding.otplayoutEditText3.setBackgroundResource(R.drawable.backgroundforotpblack);
//                                newLoginScreenBinding.otplayoutEditText4.requestFocus();
//                            } else {
//                                newLoginScreenBinding.otplayoutEditText3.setBackgroundResource(R.drawable.backgroundforotp);
//                                newLoginScreenBinding.otplayoutEditText2.requestFocus();
//                            }
//                        }
//                    });
//                    newLoginScreenBinding.otplayoutEditText4.addTextChangedListener(new TextWatcher() {
//                        @Override
//                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                        }
//
//                        @Override
//                        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                        }
//
//                        @Override
//                        public void afterTextChanged(Editable editable) {
//                            if (editable.length() == 1) {
//                                newLoginScreenBinding.otplayoutEditText4.setBackgroundResource(R.drawable.backgroundforotpblack);
//                            } else {
//                                newLoginScreenBinding.otplayoutEditText4.setBackgroundResource(R.drawable.backgroundforotp);
//                                newLoginScreenBinding.otplayoutEditText3.requestFocus();
//                            }
//                        }
//                    });
//
//                    newLoginScreenBinding.verifyOtpLoginpopup.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (!TextUtils.isEmpty(newLoginScreenBinding.otplayoutEditText1.getText().toString()) && !TextUtils.isEmpty(newLoginScreenBinding.otplayoutEditText2.getText().toString())
//                                    && !TextUtils.isEmpty(newLoginScreenBinding.otplayoutEditText3.getText().toString()) && !TextUtils.isEmpty(newLoginScreenBinding.otplayoutEditText4.getText().toString())) {
//                                if (String.valueOf(otp).equals(newLoginScreenBinding.otplayoutEditText1.getText().toString() + newLoginScreenBinding.otplayoutEditText2.getText().toString() + newLoginScreenBinding.otplayoutEditText3.getText().toString() + newLoginScreenBinding.otplayoutEditText4.getText().toString())) {
////                            UserLoginController().getGlobalConfigurationApiCall(this, this)
//                                    dialog.dismiss();
//                                    onResumeAfterLogin();
//                                    HomeActivity.isLoggedin = true;
//                                    Intent intent = new Intent(MyCartActivity.this, CheckoutActivity.class);
//                                    startActivity(intent);
//                                    overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//
////                    verify_otp_image.setImageResource(R.drawable.right_selection_green)
////                    SessionManager.setMobilenumber(mobileNum)
////                    startActivity(Intent(applicationContext, HomeActivity::class.java))
////                    finishAffinity()
////                    this.overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
//                                } else {
//                                    Toast.makeText(getApplicationContext(), "Please enter valid OTP.", Toast.LENGTH_SHORT).show();
//                                    newLoginScreenBinding.otplayoutEditText1.setText("");
//                                    newLoginScreenBinding.otplayoutEditText2.setText("");
//                                    newLoginScreenBinding.otplayoutEditText3.setText("");
//                                    newLoginScreenBinding.otplayoutEditText4.setText("");
////                                newLoginScreenBinding.otplayoutLoginpopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
//////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                                newLoginScreenBinding.accesskeyErrorTextOtp.setVisibility( View.VISIBLE);
////                            verify_otp_image.setImageResource(R.drawable.right_selection_green)
////                            Utils.showSnackbar(MyProfileActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_invalid_otp_try_again));
//                                }
//                            } else {
//                                Toast.makeText(getApplicationContext(), "Please enter valid OTP", Toast.LENGTH_SHORT).show();
//                                newLoginScreenBinding.otplayoutEditText1.setText("");
//                                newLoginScreenBinding.otplayoutEditText2.setText("");
//                                newLoginScreenBinding.otplayoutEditText3.setText("");
//                                newLoginScreenBinding.otplayoutEditText4.setText("");
////                            newLoginScreenBinding.otplayoutLoginpopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
//////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                            newLoginScreenBinding.accesskeyErrorTextOtp.setVisibility( View.VISIBLE);
////                        Utils.showSnackbar(MyProfileActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_invalid_otp_try_again));
//                            }
//                        }
//                    });
//                    removeAllExpiryCallbacks();
//                    dialog.show();
                } else {
                    Intent intent = new Intent(MyCartActivity.this, CheckoutActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
                }


//                        if (!HomeActivity.isLoggedin) {
//
//                            dialog = new Dialog(context);
//                            dialogLoginPopupBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_login_popup, null, false);
//                            dialog.setContentView(dialogLoginPopupBinding.getRoot());
//                            if (dialog.getWindow() != null)
//                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                            dialog.setCancelable(true);
////            final Dialog dialog = new Dialog(context);
//                            // if button is clicked, close the custom dialog
//                            dialogLoginPopupBinding.submit.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    if (dialogLoginPopupBinding.mobileNumEditText.getText().toString() != null && dialogLoginPopupBinding.mobileNumEditText.getText().toString() != "") {
//                                        if (SessionManager.INSTANCE.getStoreId() != null && !SessionManager.INSTANCE.getStoreId().isEmpty()
//                                                && SessionManager.INSTANCE.getTerminalId() != null && !SessionManager.INSTANCE.getTerminalId().isEmpty() && SessionManager.INSTANCE.getEposUrl() != null && !SessionManager.INSTANCE.getEposUrl().isEmpty()) {
//                                            String MobilePattern = "[0-9]{10}";
//                                            mobileNum = dialogLoginPopupBinding.mobileNumEditText.getText().toString();
//                                            if (mobileNum.length() < 10) {
////                                di.setImageResource(R.drawable.right_selection_green);
//                                                dialogLoginPopupBinding.mobileNumLoginPopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
//                                                dialogLoginPopupBinding.accesskeyErrorText.setVisibility(View.VISIBLE);
//                                            } else {
//                                                dialogLoginPopupBinding.accesskeyErrorText.setVisibility(View.INVISIBLE);
////                                send_otp_image.setImageResource(R.drawable.right_selection_green)
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_country_code_bg)
////                                edittext_error_text.visibility = View.INVISIBLE
//                                                if (oldMobileNum.equals(dialogLoginPopupBinding.mobileNumEditText.getText().toString()) && dialogLoginPopupBinding.mobileNumEditText.getText().toString().length() > 0 && (mobileNum.matches(MobilePattern))) {
//                                                    dialogLoginPopupBinding.mobileNumLoginPopup.setVisibility(View.GONE);
//                                                    dialogLoginPopupBinding.submitLoginPopup.setVisibility(View.GONE);
//                                                    dialogLoginPopupBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//                                                    dialogLoginPopupBinding.verifyOtpLoginpopup.setVisibility(View.VISIBLE);
//                                                } else {
//                                                    dialogLoginPopupBinding.mobileNumLoginPopup.setVisibility(View.GONE);
//                                                    dialogLoginPopupBinding.submitLoginPopup.setVisibility(View.GONE);
//                                                    dialogLoginPopupBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//                                                    dialogLoginPopupBinding.verifyOtpLoginpopup.setVisibility(View.VISIBLE);
//                                                    if (dialogLoginPopupBinding.mobileNumEditText.getText().toString().length() > 0) {
//                                                        oldMobileNum = dialogLoginPopupBinding.mobileNumEditText.getText().toString();
//                                                        if (mobileNum.matches(MobilePattern)) {
//                                                            Utils.showDialog(MyCartActivity.this, "Sending OTP…");
//                                                            otp = (int) ((Math.random() * 9000) + 1000);
//                                                            if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
//                                                                Send_Sms_Request sms_req = new Send_Sms_Request();
//                                                                sms_req.setMobileNo(mobileNum);
//                                                                sms_req.setMessage("Dear Apollo Customer, Your one time password is " + String.valueOf(otp) + " and is valid for 3mins.");
//                                                                sms_req.setIsOtp(true);
//                                                                sms_req.setOtp(String.valueOf(otp));
//                                                                sms_req.setApiType("KIOSk");
//                                                                myCartController.handleSendSmsApi(sms_req);
//                                                            } else {
//                                                                Utils.showSnackbar(getApplicationContext(), constraint_Layout, "Internet Connection Not Available");
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        } else {
//                                            AccesskeyDialog accesskeyDialog = new AccesskeyDialog(MyCartActivity.this);
//                                            accesskeyDialog.onClickSubmit(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    accesskeyDialog.listener();
//                                                    if (accesskeyDialog.validate()) {
//                                                        Intent intent = new Intent(MyCartActivity.this, CheckoutActivity.class);
//                                                        startActivity(intent);
//                                                        overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                                                        accesskeyDialog.dismiss();
//                                                    }
//                                                }
//                                            });
//
//
//                                            accesskeyDialog.show();
////                Intent intent = new Intent(MainActivity.this, MposStoreSetupActivity.class);
////                startActivity(intent);
////                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                                        }
//                                    }
////                    dialog.dismiss();
//                                }
//                            });
//
//                            dialogLoginPopupBinding.verifyOtpLoginpopup.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    if (!TextUtils.isEmpty(dialogLoginPopupBinding.otplayoutEditText.getText().toString()) && dialogLoginPopupBinding.otplayoutEditText.getText().toString().length() > 0) {
//                                        if (String.valueOf(otp).equals(dialogLoginPopupBinding.otplayoutEditText.getText().toString())) {
////                            UserLoginController().getGlobalConfigurationApiCall(this, this)
//                                            dialog.dismiss();
//                                            HomeActivity.isLoggedin = true;
//                                            Intent intent = new Intent(MyCartActivity.this, CheckoutActivity.class);
//                                            startActivity(intent);
//                                            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
////                    verify_otp_image.setImageResource(R.drawable.right_selection_green)
////                    SessionManager.setMobilenumber(mobileNum)
////                    startActivity(Intent(applicationContext, HomeActivity::class.java))
////                    finishAffinity()
////                    this.overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
//                                        } else {
//                                            dialogLoginPopupBinding.otplayoutLoginpopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
//                                            dialogLoginPopupBinding.accesskeyErrorTextOtp.setVisibility(View.VISIBLE);
////                            verify_otp_image.setImageResource(R.drawable.right_selection_green)
////                            Utils.showSnackbar(MyProfileActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_invalid_otp_try_again));
//                                        }
//                                    } else {
//                                        dialogLoginPopupBinding.otplayoutLoginpopup.setBackgroundResource(R.drawable.phone_error_alert_bg);
////                                edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg);
//                                        dialogLoginPopupBinding.accesskeyErrorTextOtp.setVisibility(View.VISIBLE);
////                        Utils.showSnackbar(MyProfileActivity.this, constraintLayout, getApplicationContext().getResources().getString(R.string.label_invalid_otp_try_again));
//                                    }
//                                }
//                            });
//                            dialog.show();
//                        }
//                        else {
//                            Intent intent = new Intent(MyCartActivity.this, CheckoutActivity.class);
//                            startActivity(intent);
//                            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                        }
//                startActivity(CheckoutActivity.getStartIntent(this, dataList));

            } else {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "No items added to cart", Snackbar.LENGTH_SHORT);
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.color_yellow_button));
                snackbar.show();
            }


//                startActivity(CheckoutActivity.getStartIntent(this, dataList));


//            if (curationViewLayout.getVisibility() == View.GONE) {
//                if (dataList.size() > 0) {
//                    if (curationFlag) {
//                        if (curationDoneFlag) {
//                            checkDeliveryOption();
//                        } else {
//                            final Dialog dialog = new Dialog(MyCartActivity.this);
//                            dialog.setContentView(R.layout.view_curation_alert_dialog);
//                            if (dialog.getWindow() != null)
//                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                            dialog.show();
//                            TextView dialogTitleText = dialog.findViewById(R.id.dialog_info);
//                            Button okButton = dialog.findViewById(R.id.dialog_ok);
//                            Button declineButton = dialog.findViewById(R.id.dialog_cancel);
//                            dialogTitleText.setText(getResources().getString(R.string.label_curation_in_progress_text));
//                            okButton.setText(getResources().getString(R.string.label_ok));
//                            declineButton.setText(getResources().getString(R.string.label_cancel_text));
//                            okButton.setOnClickListener(v -> {
//                                dialog.dismiss();
//                                if (countdown_timer != null)
//                                    countdown_timer.cancel();
//                                checkDeliveryOption();
//                            });
//                            declineButton.setOnClickListener(v -> dialog.dismiss());
//                        }
//                    } else {
//                        checkDeliveryOption();
//                    }
//                } else {
//                    Utils.showSnackbar(MyCartActivity.this, constraint_Layout, getApplicationContext().getResources().getString(R.string.label_min_order_item_alert));
//                }
//            } else {
//                Utils.showSnackbar(MyCartActivity.this, constraint_Layout, getApplicationContext().getResources().getString(R.string.label_curation_progress_wait));
//            }
        });

        promotionViewAll.setOnClickListener(arg0 ->

        {
            SessionManager.INSTANCE.setCartItems(dataList);
            SessionManager.INSTANCE.setDataList(dataList);
            session.setfixedtimeperiod(fixedtime_period_millisec);
            session.settimeperiod1(System.currentTimeMillis());
            Intent intent = new Intent(MyCartActivity.this, MyOffersActivity.class);
            intent.putExtra("categoryname", "Promotions");
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });

        trendingNowViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SessionManager.INSTANCE.setCartItems(dataList);
                SessionManager.INSTANCE.setDataList(dataList);
                session.setfixedtimeperiod(fixedtime_period_millisec);
                session.settimeperiod1(System.currentTimeMillis());
                Intent intent = new Intent(MyCartActivity.this, MyOffersActivity.class);
                intent.putExtra("categoryname", "TrendingNow");
                startActivity(intent);
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            }
        });

        cartItemRecyclerView =

                findViewById(R.id.cartRecyclerView);

        dataList = new ArrayList<>();
        cartListAdapter = new

                MyCartListAdapter(MyCartActivity.this, dataComparingList, this, expandholdedDataList);
        cartItemRecyclerView.setLayoutManager(new

                LinearLayoutManager(this));
        cartItemRecyclerView.setAdapter(cartListAdapter);
        cartListAdapter.notifyDataSetChanged();

        if (SessionManager.INSTANCE.getDataList() != null) {
            if (SessionManager.INSTANCE.getDataList().size() > 0) {
                dataList.clear();
                dataList.addAll(SessionManager.INSTANCE.getDataList());

//                dataComparingList.addAll(SessionManager.INSTANCE.getDataList());
//                for (int i = 0; i < dataComparingList.size(); i++) {
//                    for (int j = 0; j < dataComparingList.size(); j++) {
//                        if (i != j && dataComparingList.get(i).getArtName().equals(dataComparingList.get(j).getArtName())) {
//                            expandholdedDataList.add(dataComparingList.get(j));
//                            dataComparingList.remove(j);
//                            j--;
//                        }
//                    }
//                }

                groupingDuplicates();

                cartListAdapter.notifyDataSetChanged();
                float totalMrpPrice = 0;
                for (int i = 0; i < dataList.size(); i++) {
                    if (!TextUtils.isEmpty(dataList.get(i).getArtprice())) {
                        if (dataList.get(i).getNetAmountInclTax() != null && !dataList.get(i).getNetAmountInclTax().isEmpty()) {
                            totalMrpPrice = Float.parseFloat(dataList.get(i).getNetAmountInclTax()) + totalMrpPrice;
                        } else {
                            totalMrpPrice = Float.parseFloat(dataList.get(i).getArtprice()) * dataList.get(i).getQty() + totalMrpPrice;
                        }
                    }
                }

                DecimalFormat formatter1 = new DecimalFormat("#,###.00");
                String pharmaformatted1 = formatter1.format(totalMrpPrice);

                grandTotalPrice.setText(rupeeSymbol + "" + pharmaformatted1);
                cartCount(dataList.size());
            }
        }

        if (SessionManager.INSTANCE.getDeletedDataList() != null) {
            if (SessionManager.INSTANCE.getDeletedDataList().size() > 0) {
                addAgainLayout.setVisibility(View.VISIBLE);
                removedItemsCount.setText(String.valueOf(SessionManager.INSTANCE.getDeletedDataList().size()));
                deletedataList.addAll(SessionManager.INSTANCE.getDeletedDataList());
            } else {
                addAgainLayout.setVisibility(View.GONE);
            }
        } else {
            addAgainLayout.setVisibility(View.GONE);
        }

        ObjectAnimator textColorAnim = ObjectAnimator.ofInt(curationProcessText, "textColor", Color.WHITE, Color.TRANSPARENT);
        textColorAnim.setDuration(1000);
        textColorAnim.setEvaluator(new

                ArgbEvaluator());
        textColorAnim.setRepeatCount(ValueAnimator.INFINITE);
        textColorAnim.setRepeatMode(ValueAnimator.REVERSE);
        textColorAnim.start();

        ObjectAnimator textCurationAnim = ObjectAnimator.ofInt(curationText, "textColor", Color.WHITE, Color.TRANSPARENT);
        textCurationAnim.setDuration(1000);
        textCurationAnim.setEvaluator(new

                ArgbEvaluator());
        textCurationAnim.setRepeatCount(ValueAnimator.INFINITE);
        textCurationAnim.setRepeatMode(ValueAnimator.REVERSE);
        textCurationAnim.start();

        ArrayList<Product> offerList = SessionManager.INSTANCE.getOfferList();
        ArrayList<Product> trendingList = SessionManager.INSTANCE.getTrendingList();
        if (offerList == null || trendingList == null) {
            if (NetworkUtils.isNetworkConnected(MyCartActivity.this)) {
                myCartController.getProductList("SpecialOffers", this);
            } else {
                Utils.showSnackbar(MyCartActivity.this, constraint_Layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
            }
        }
        if (offerList != null && offerList.size() > 0) {
            product_list_array.clear();
            product_list_array.add(offerList.get(0));
            product_list_array.add(offerList.get(1));
            product_list_array.add(offerList.get(2));
        }

        promotionadaptor = new

                PromotionsAdapter(MyCartActivity.this, product_list_array);
        promotionadaptor.setViewMode(ViewMode.GRID);
        promotionproductrecycleerview.setAdapter(promotionadaptor);
        if (product_list_array.size() == 0) {
            isEmpty = true;
            product_list_array.add(new Product("load"));
        }
        promotionadaptor.notifyDataChanged();
        if (trendingList != null && trendingList.size() > 0) {
            trendingNowAdapter = new TrendingNowAdapter(MyCartActivity.this, trendingList);
        } else {
            trendingNowAdapter = new TrendingNowAdapter(MyCartActivity.this, product_list_array1);
        }
        trendingNowAdapter.setViewMode(ViewMode.GRID);
        trendingnowproductrecycleerview.setAdapter(trendingNowAdapter);
        if (product_list_array1.size() == 0) {
            isEmpty1 = true;
            product_list_array1.add(new Product("load"));
        }
        trendingNowAdapter.notifyDataChanged();
        SessionManager.INSTANCE.setCurrentPage("");

        session = new

                Session(MyCartActivity.this);
        if (SessionManager.INSTANCE.gettimerlogin()) {
            fixedtime_period_millisec = fixedScanTime;
            startcountertimer();
        } else {
            if (SessionManager.INSTANCE.gettimerstart_status()) {
                timeperiod_2_millisec = System.currentTimeMillis();
                timeperiod_1_millisce = session.getimeperiod1();
                Long temptime = timeperiod_2_millisec - timeperiod_1_millisce;
                fixedtime_period_millisec = session.getfixedtimeperiod() - temptime;
                if (countdown_timer != null) {
                    countdown_timer.cancel();
                }
                startcountertimer();
            }
        }
        LocalBroadcastManager.getInstance(this).

                registerReceiver(mMessageReceiverCheckOut, new IntentFilter("MedicineReciver"));
        LocalBroadcastManager.getInstance(this).

                registerReceiver(mPrescriptionMessageReceiver, new IntentFilter("PrescriptionReceived"));
        LocalBroadcastManager.getInstance(this).

                registerReceiver(mMessageReceiver, new IntentFilter("cardReceiverCartItems"));
        LocalBroadcastManager.getInstance(this).

                registerReceiver(mMessageReceivers, new IntentFilter("cardReceiver"));
        LocalBroadcastManager.getInstance(this).

                registerReceiver(mMessageReceiverNew, new IntentFilter("OrderhistoryCardReciver"));
        Constants.getInstance().

                setConnectivityListener(this);

        Constants.getInstance().

                setConnectivityListener(this);

        addAgainLayout.setOnClickListener(v ->

        {
            cartDeleteItemDialog = new CartDeletedItemsDialog(MyCartActivity.this, SessionManager.INSTANCE.getDeletedDataList(), this);
            cartDeleteItemDialog.show();
            Window window = cartDeleteItemDialog.getWindow();
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
            if (window != null) {
                window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });
//        checkOutImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MyCartActivity.this, PaymentOptionsActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//            }
//        });
    }


    private List<OCRToDigitalMedicineResponse> duplicatelabelDataList = new ArrayList<>();

    private void groupingDuplicates() {

        dataComparingList.clear();
        expandholdedDataList.clear();
        labelDataList.clear();
        duplicatelabelDataList.clear();

        dataComparingList.addAll(SessionManager.INSTANCE.getDataList());
        for (int i = 0; i < dataComparingList.size(); i++) {
            for (int j = 0; j < dataComparingList.size(); j++) {
                if (dataComparingList.get(i).getArtName().equals(dataComparingList.get(j).getArtName())) {
                    expandholdedDataList.add(dataComparingList.get(j));
                    labelDataList.add(dataComparingList.get(j));
                    dataComparingList.remove(j);
                    j--;
                }
            }
        }

        for (int i = 0; i < labelDataList.size(); i++) {
            for (int j = 0; j < labelDataList.size(); j++) {
                if (i != j && labelDataList.get(i).getArtName().equals(labelDataList.get(j).getArtName())) {
                    labelDataList.remove(j);
                    j--;
                }
            }
        }

        List<OCRToDigitalMedicineResponse> expandListDummy = new ArrayList<>();
        for (int i = 0; i < labelDataList.size(); i++) {
            for (int j = 0; j < expandholdedDataList.size(); j++) {
                if (labelDataList.get(i).getArtName().equalsIgnoreCase(expandholdedDataList.get(j).getArtName())) {
                    expandListDummy.add(expandholdedDataList.get(j));
                }
            }
        }
        float max = 0;
        for (int i = 0; i < labelDataList.size(); i++) {
            int labelAvgQty = 0;
            boolean maxOnce = false;
            float labelAveragePrice = 0;
            float labelPrice = 0;
            String labelName = "";
            int repeatCount = 0;
            String netAmountinclTax = "";
            for (int j = 0; j < expandListDummy.size(); j++) {
                if (labelDataList.get(i).getArtName().equalsIgnoreCase(expandListDummy.get(j).getArtName())) {
                    if (!maxOnce) {
                        max = Float.parseFloat(expandListDummy.get(j).getArtprice());
                        maxOnce = true;
                    }

                    // loop to find maximum from ArrayList
                    if (Float.parseFloat(expandListDummy.get(j).getArtprice()) > max) {
                        max = Float.parseFloat(expandListDummy.get(j).getArtprice());
                    }

                    labelPrice = labelPrice + Float.parseFloat(expandListDummy.get(j).getArtprice());
                    labelAvgQty = labelAvgQty + expandListDummy.get(j).getQty();
                    repeatCount = repeatCount + 1;
                    labelName = expandListDummy.get(j).getArtName();
                    netAmountinclTax = expandListDummy.get(j).getNetAmountInclTax();
                }
            }

            OCRToDigitalMedicineResponse labelResponse = new OCRToDigitalMedicineResponse();
            labelResponse.setLabelMaxPrice(max);
            labelResponse.setLabelPrice(labelPrice);
            labelAveragePrice = labelResponse.getLabelPrice() / (float) repeatCount;
            labelResponse.setLabelAveragePrice(labelAveragePrice);
            labelResponse.setLabelAvgQty(labelAvgQty);
            labelResponse.setDuplicateCount(repeatCount);
            labelResponse.setLabelName(labelName);
            labelResponse.setNetAmountInclTax(netAmountinclTax);
            duplicatelabelDataList.add(labelResponse);
        }

        for (int i = 0; i < duplicatelabelDataList.size(); i++) {
            OCRToDigitalMedicineResponse labelResponse = new OCRToDigitalMedicineResponse();
            labelResponse.setLabelMaxPrice(duplicatelabelDataList.get(i).getLabelMaxPrice());
            labelResponse.setArtName(duplicatelabelDataList.get(i).getLabelName());
            labelResponse.setArtprice(String.valueOf(duplicatelabelDataList.get(i).getLabelAveragePrice()));
            labelResponse.setQty(duplicatelabelDataList.get(i).getLabelAvgQty());
            labelResponse.setNetAmountInclTax(duplicatelabelDataList.get(i).getNetAmountInclTax());
            dataComparingList.add(labelResponse);
        }

    }

    private void startcountertimer() {
        SessionManager.INSTANCE.settimerlogin(false);
        SessionManager.INSTANCE.settimerstart_status(true);
        countdown_timer = new CountDownTimer(fixedtime_period_millisec, 1000) {
            public void onTick(long millisUntilFinished) {
                fixedtime_period_millisec = millisUntilFinished;
                if (SessionManager.INSTANCE.gettimerstart_status()) {
                    int remainingTime = (int) (fixedScanTime - fixedtime_period_millisec);
                    minVal = (int) (remainingTime) / 60000;
                    secVal = (int) (remainingTime - minVal * 60000) / 1000;
                }
                secVal++;
                if (secVal == 60) {
                    minVal = minVal + 1;
                    secVal = 0;
                }
                int mins = (int) minVal;
                int secs = (int) secVal;
                String asText = mins + "M " + String.format("%02d", secs) + "S";
                timerHeaderText.setText(asText);
                timerChildText.setText(asText);
            }

            public void onFinish() {
                SessionManager.INSTANCE.setCurationStatus(false);
                SessionManager.INSTANCE.settimerlogin(false);
                SessionManager.INSTANCE.settimerstart_status(false);
                if (SessionManager.INSTANCE.getFcmMedicineReceived()) {
                    stopcontdowntimer();
                } else {
                    if (countdown_timer != null)
                        countdown_timer.cancel();
                }
//                stopcontdowntimer();
            }
        }.start();
    }

    private void stopcontdowntimer() {
        Dialog mDialog = Utils.getDialog(MyCartActivity.this, R.layout.dialog_curation_failure);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        ImageView closeImage = mDialog.findViewById(R.id.close_image);
        LinearLayout yesLayout = mDialog.findViewById(R.id.yes_layout);
        LinearLayout noLayout = mDialog.findViewById(R.id.no_layout);
        yesLayout.setOnClickListener(v -> {
            mDialog.dismiss();
            if (countdown_timer != null) {
                countdown_timer.cancel();
            }
            curationViewLayout.setVisibility(View.GONE);
            curationProcessLayout.setVisibility(View.GONE);

            Dialog mobileNotifyDialog = Utils.getDialog(MyCartActivity.this, R.layout.dialog_mobile_number_prescription);
            mobileNotifyDialog.setCancelable(false);
            mobileNotifyDialog.setCanceledOnTouchOutside(false);

            ImageView closeNotifyImage = mobileNotifyDialog.findViewById(R.id.close_image);
            LinearLayout okLayout = mobileNotifyDialog.findViewById(R.id.ok_layout);
            TextView userMobileNumber = mobileNotifyDialog.findViewById(R.id.mobile_no_txt);
            userMobileNumber.setText("" + SessionManager.INSTANCE.getMobilenumber());
            closeNotifyImage.setOnClickListener(view -> {
                mobileNotifyDialog.dismiss();
            });

            okLayout.setOnClickListener(view -> {
                mobileNotifyDialog.dismiss();
                HomeActivity.isLoggedin = false;
                finish();
                Intent intent = new Intent(MyCartActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            });
            mobileNotifyDialog.show();
        });

        noLayout.setOnClickListener(v -> {
            mDialog.dismiss();
            if (countdown_timer != null) {
                countdown_timer.cancel();
            }
            curationViewLayout.setVisibility(View.GONE);
            curationProcessLayout.setVisibility(View.GONE);
            SessionManager.INSTANCE.setCurationStatus(false);
            SessionManager.INSTANCE.setDataList(dataList);
            finish();
            Intent intent1 = new Intent(MyCartActivity.this, MySearchActivity.class);
            intent1.putExtra("activityname", "MyCartActivity");
            startActivity(intent1);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });

        if (!((Activity) MyCartActivity.this).isFinishing()) {
            mDialog.show();
        }
    }

    public void updatecartlist() {
        curationViewLayout.setVisibility(View.GONE);
        if (SessionManager.INSTANCE.getCurationStatus()) {
            curationProcessLayout.setVisibility(View.VISIBLE);
            curationViewLayout.setVisibility(View.GONE);
        } else {
            curationProcessLayout.setVisibility(View.GONE);
            curationViewLayout.setVisibility(View.GONE);
        }
        if (countdown_timer != null) {
            countdown_timer.cancel();
        }

        if (SessionManager.INSTANCE.getDataList() != null) {
            List<OCRToDigitalMedicineResponse> tempdata = SessionManager.INSTANCE.getDataList();
            if (message_string.equalsIgnoreCase("addtocart") || message_string.equalsIgnoreCase("cartupdate")) {
                for (OCRToDigitalMedicineResponse data : tempdata) {
                    String product_sku = data.getArtCode();
                    int count = 0;
                    for (OCRToDigitalMedicineResponse tempdata1 : new ArrayList<OCRToDigitalMedicineResponse>(dataList)) {
                        String product_sku1 = tempdata1.getArtCode();
                        if (product_sku1.equalsIgnoreCase(product_sku)) {
                            dataList.remove(count);
                        }
                        count++;
                    }
                    OCRToDigitalMedicineResponse obj = new OCRToDigitalMedicineResponse();
                    obj.setArtCode(data.getArtCode());
                    obj.setArtName(data.getArtName());
                    obj.setQty(data.getQty());
                    obj.setArtprice(data.getArtprice());
                    obj.setContainer("Strip");
                    dataList.add(obj);
                }
            } else if (message_string.equalsIgnoreCase("cartdelete")) {
                int count1 = 0;
                for (OCRToDigitalMedicineResponse tempdata1 : new ArrayList<OCRToDigitalMedicineResponse>(dataList)) {
                    String product_sku1 = tempdata1.getArtCode();
                    if (product_sku1.equalsIgnoreCase(delete_item_sku)) {
                        dataList.remove(count1);
                    }
                    count1++;
                }
            }
            SessionManager.INSTANCE.setDataList(dataList);
        }
        if (null != dataList && dataList.size() > 0) {
            cartItemCountLayout.setVisibility(View.VISIBLE);
            cartUniqueCount();
            total_itemcount_textview.setText(String.valueOf(countUniques.size()));
            checkOutImage.setImageResource(R.drawable.checkout_cart);
            curationViewLayout.setVisibility(View.GONE);
            if (SessionManager.INSTANCE.getCurationStatus()) {
                curationProcessLayout.setVisibility(View.VISIBLE);
            } else {
                curationProcessLayout.setVisibility(View.GONE);
            }
        } else {
            cartItemCountLayout.setVisibility(View.GONE);
            if (SessionManager.INSTANCE.getCurationStatus()) {
                curationProcessLayout.setVisibility(View.VISIBLE);
            } else {
                curationProcessLayout.setVisibility(View.GONE);
            }
            checkOutImage.setImageResource(R.drawable.checkout_cart_unselect);
        }
        if (dataList.size() > 0) {
            float grandTotalVal = 0;
            for (int i = 0; i < dataList.size(); i++) {
                if (!TextUtils.isEmpty(dataList.get(i).getArtprice())) {
                    if (dataList.get(i).getNetAmountInclTax() != null && !dataList.get(i).getNetAmountInclTax().isEmpty()) {
                        grandTotalVal = grandTotalVal + Float.parseFloat(dataList.get(i).getNetAmountInclTax());
                    } else {
                        float totalPrice = Float.parseFloat(dataList.get(i).getArtprice()) * dataList.get(i).getQty();
                        grandTotalVal = grandTotalVal + totalPrice;
                    }
                }
            }
            String rupeeSymbol = "\u20B9";

            DecimalFormat formatter = new DecimalFormat("#,###.00");
            String pharmaformatted = formatter.format(grandTotalVal);

            grandTotalPrice.setText(rupeeSymbol + "" + pharmaformatted);
            grandTotalPriceValue = grandTotalPrice.getText().toString();

            groupingDuplicates();

            cartListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (countdown_timer != null) {
            countdown_timer.cancel();
        }
        SessionManager.INSTANCE.clearMedicineData();
        startActivity(new Intent(MyCartActivity.this, InsertPrescriptionActivity.class));
        finish();
        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
    }

    public List<OCRToDigitalMedicineResponse> removeDuplicate(List<OCRToDigitalMedicineResponse> dataList) {
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    List<OCRToDigitalMedicineResponse> countUniques;

    private void cartUniqueCount() {
        countUniques = new ArrayList<>();
        countUniques.addAll(dataList);

        for (int i = 0; i < countUniques.size(); i++) {
            for (int j = 0; j < countUniques.size(); j++) {
                if (i != j && countUniques.get(i).getArtName().equals(countUniques.get(j).getArtName())) {
                    countUniques.remove(j);
                    j--;
                }
            }
        }
    }

    @Override
    public void cartCount(int count) {
        if (null == myCartCount)
            return;
        if (count != 0) {
            cartUniqueCount();
            myCartCount.setVisibility(View.VISIBLE);
//            myCartCount.setText(String.valueOf(count));
            myCartCount.setText(String.valueOf(countUniques.size()));
            cartItemCountLayout.setVisibility(View.VISIBLE);
            cartUniqueCount();
            total_itemcount_textview.setText(String.valueOf(countUniques.size()));
            checkOutImage.setImageResource(R.drawable.checkout_cart);
        } else {
            myCartCount.setVisibility(View.GONE);
            myCartCount.setText("0");
            cartItemCountLayout.setVisibility(View.GONE);
            checkOutImage.setImageResource(R.drawable.checkout_cart_unselect);
        }
    }

    @Override
    public void showSnackBAr() {

    }

    @Override
    public void onClick(int position) {

    }

    @Override
    public void onCount(int count, boolean flag) {

    }

    @Override
    public void onPrescriptionClick(int position, boolean flag) {
        if (presSelectedItem.getMedicineList() != null && presSelectedItem.getMedicineList().size() > 0) {
            ScannedMedicine item = presSelectedItem.getMedicineList().get(position);
            if (flag) {
                dialogMedicineList.add(item);
            } else {
                for (int i = 0; i < dialogMedicineList.size(); i++) {
                    if (dialogMedicineList.get(i).getArtCode().equalsIgnoreCase(item.getArtCode())) {
                        dialogMedicineList.remove(item);
                    }
                }
            }
        }
    }

    @Override
    public void onDeletedAddAllClicked() {
        if (SessionManager.INSTANCE.getDeletedDataList().size() > 0) {
            for (OCRToDigitalMedicineResponse data : new ArrayList<OCRToDigitalMedicineResponse>(SessionManager.INSTANCE.getDeletedDataList())) {
                String product_sku = data.getArtCode();
                String product_name = data.getArtName();
                int product_quantyty = data.getQty();
                String product_container = data.getContainer();
                String product_price = data.getArtprice();
                OCRToDigitalMedicineResponse loadobject = new OCRToDigitalMedicineResponse();
                loadobject.setArtCode(product_sku);
                loadobject.setArtName(product_name);
                loadobject.setQty(product_quantyty);
                loadobject.setMedicineType(data.getMedicineType());
                loadobject.setContainer(product_container);
                loadobject.setArtprice(product_price);
                dataList.add(loadobject);
                deletedataList.remove(data);
            }
        }
        SessionManager.INSTANCE.setDataList(dataList);
        SessionManager.INSTANCE.clearDeletedMedicineData();
        addAgainLayout.setVisibility(View.GONE);
        deletedataList.clear();
        if (dataList.size() > 0) {
            float grandTotalVal = 0;
            for (int i = 0; i < dataList.size(); i++) {
                if (!TextUtils.isEmpty(dataList.get(i).getArtprice())) {
                    if (dataList.get(i).getNetAmountInclTax() != null && !dataList.get(i).getNetAmountInclTax().isEmpty()) {
                        grandTotalVal = grandTotalVal + Float.parseFloat(dataList.get(i).getNetAmountInclTax());
                    } else {
                        float totalPrice = Float.parseFloat(dataList.get(i).getArtprice()) * dataList.get(i).getQty();
                        grandTotalVal = grandTotalVal + totalPrice;
                    }
                }
            }
            String rupeeSymbol = "\u20B9";

            DecimalFormat formatter = new DecimalFormat("#,###.00");
            String pharmaformatted = formatter.format(grandTotalVal);

            grandTotalPrice.setText(rupeeSymbol + "" + pharmaformatted);
            grandTotalPriceValue = grandTotalPrice.getText().toString();

            groupingDuplicates();

            cartListAdapter.notifyDataSetChanged();
            cartCount(dataList.size());
        }
    }

    @Override
    public void onSuccessImageService(GetImageRes res) {
        SessionManager.INSTANCE.setScannedImagePath(res.getImageUrl());
        uploadBgImageController.handleUploadImageService(res.getImageUrl());
        handleScannedImageView();
    }

//    List<UpCellCrossCellResponse.Crossselling> crosssellingList = new ArrayList<>();
//    List<UpCellCrossCellResponse.Upselling> upsellingList = new ArrayList<>();

    List<ItemSearchResponse.Item> crosssellingList = new ArrayList<>();
    List<ItemSearchResponse.Item> upsellingList = new ArrayList<>();
    boolean addToCarLayHandel;
    UpCellCrossCellResponse.Crossselling cs;
    private int crosssellcountforadapter = 0;
    private int upssellcountforadapter = 0;

    @Override
    public void onSuccessSearchItemApi(UpCellCrossCellResponse body) {
        crosssellcountforadapter = 0;
        upssellcountforadapter = 0;
        List<UpCellCrossCellResponse.Crossselling> crossselling = null;
        List<UpCellCrossCellResponse.Upselling> upselling = null;
        if (body != null && body.getCrossselling() != null && body.getCrossselling().size() > 0) {
            addToCarLayHandel = false;
            crossselling = new ArrayList<>();
            crossselling.add(body.getCrossselling().get(0));
            crossselling.add(body.getCrossselling().get(1));
            crossselling.add(body.getCrossselling().get(2));
            for (UpCellCrossCellResponse.Crossselling crossSellingList : crossselling) {
                myCartController.searchItemProducts(crossSellingList.getItemid(), 1, 0, 0);
            }
        } else {
            noDataFound.setVisibility(View.VISIBLE);
        }
        if (body != null && body.getUpselling() != null && body.getUpselling().size() > 0) {
            upselling = new ArrayList<>();
            upselling.add(body.getUpselling().get(0));
            upselling.add(body.getUpselling().get(1));
            upselling.add(body.getUpselling().get(2));
            for (UpCellCrossCellResponse.Upselling upSelling : upselling) {
                myCartController.searchItemProducts(upSelling.getItemid(), 2, 0, 0);
            }
        } else {
            noDataFound.setVisibility(View.VISIBLE);
        }
//        if (crossselling != null && crossselling.size() > 0 || upselling != null && upselling.size() > 0)
//            upSellCrosssellApiCall(crossselling, upselling);

    }

    @Override
    public void upSellCrosssellApiCall(List<UpCellCrossCellResponse.Crossselling> crossselling,
                                       List<UpCellCrossCellResponse.Upselling> upselling) {

    }

    int k = 0;

    @Override
    public void setSuccessBatchList(BatchListResponse batchListResponse, int position, ItemSearchResponse.Item iteSearchData) {
        int requiredQty = dataListSphare.get(position).getQty();
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
        calculatePosTransactionRequest.setCreatedonPosTerminal("006");
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
        if (k == dataListSphare.size()) {
            k = 0;
            dummyDataList.clear();
            new MyCartController(this).calculatePosTransaction(calculatePosTransactionRequest, this);
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
//                data.setPack(calculatePosTransactionResponse.getSalesLine().get(i));
                data.setMedicineType(calculatePosTransactionResponse.getSalesLine().get(i).getCategory());
                data.setNetAmountInclTax((calculatePosTransactionResponse.getSalesLine().get(i).getBaseAmount().equals(calculatePosTransactionResponse.getSalesLine().get(i).getNetAmountInclTax())) ? null : String.valueOf(calculatePosTransactionResponse.getSalesLine().get(i).getNetAmountInclTax()));
                data.setQty(calculatePosTransactionResponse.getSalesLine().get(i).getQty());
                dummyDataListdummy.add(data);
            }
        }
//        List<OCRToDigitalMedicineResponse> temporaryDataList = new ArrayList<>();
//        if (SessionManager.INSTANCE.getDataList() != null && SessionManager.INSTANCE.getDataList().size() > 0) {
//            temporaryDataList = SessionManager.INSTANCE.getDataList();
//            for (int i = 0; i < temporaryDataList.size(); i++) {
//                for (int j = 0; j < dummyDataListdummy.size(); j++) {
//                    if (temporaryDataList.get(i).getArtCode().equals(dummyDataListdummy.get(j).getArtCode())) {
//                        if (dummyDataListdummy.get(j).getNetAmountInclTax() != null && temporaryDataList.get(i).getNetAmountInclTax() != null)
//                            temporaryDataList.get(i).setNetAmountInclTax(String.valueOf(Double.parseDouble(temporaryDataList.get(i).getNetAmountInclTax()) + Double.parseDouble(dummyDataListdummy.get(j).getNetAmountInclTax())));
//                        temporaryDataList.get(i).setQty(temporaryDataList.get(i).getQty() + dummyDataListdummy.get(j).getQty());
//                        dummyDataListdummy.remove(j);
//                        j--;
//                    }
//                }
//            }
//            temporaryDataList.addAll(dummyDataListdummy);
        SessionManager.INSTANCE.setDataList(dummyDataListdummy);
        setUp();
//            Intent intent = new Intent("OrderhistoryCardReciver");
//            intent.putExtra("message", "OrderNow");
//            intent.putExtra("MedininesNames", new Gson().toJson(temporaryDataList));
//            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//        } else {
//            temporaryDataList.addAll(dummyDataListdummy);
//            SessionManager.INSTANCE.setDataList(temporaryDataList);
//            setUp();
////            Intent intent = new Intent("OrderhistoryCardReciver");
////            intent.putExtra("message", "OrderNow");
////            intent.putExtra("MedininesNames", new Gson().toJson(temporaryDataList));
////            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//        }
    }

    @Override
    public void onSuccessSearchItemApi(ItemSearchResponse itemSearchResponse, int position) {
        if (itemSearchResponse != null && itemSearchResponse.getItemList() != null && itemSearchResponse.getItemList().size() > 0) {
            for (int i = 0; i < itemSearchResponse.getItemList().size(); i++) {
                dataListSphare.get(position).setMedicineType(String.valueOf(itemSearchResponse.getItemList().get(i).getCategory()));
                dataListSphare.get(position).setArtName(String.valueOf(itemSearchResponse.getItemList().get(i).getDescription()));
            }
        } else {
            dataListSphare.get(position).setOutOfStock(true);
        }
    }

    @Override
    public void onSendSmsSuccess() {
        Utils.dismissDialog();
        if (!isResend) {
            newLoginScreenBinding.mobileNumLoginPopup.setVisibility(View.GONE);
            newLoginScreenBinding.otplayoutLoginpopup.setVisibility(View.VISIBLE);
//        entered_mobile_number.setText(mobileNum)
//            SessionManager.INSTANCE.setMobilenumber(mobileNum);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
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

    @Override
    public void onSearchFailure(String message) {

    }

    List<OCRToDigitalMedicineResponse> dummyDataList = new ArrayList<>();
    private float balanceQty;

    @Override
    public void onSuccessBarcodeItemApi(ItemSearchResponse itemSearchResponse, int serviceType, int qty, int position) {
        if (serviceType == 0) {
            if (itemSearchResponse.getItemList() != null && itemSearchResponse.getItemList().size() > 0) {
                ItemBatchSelectionDilaog itemBatchSelectionDilaog = new ItemBatchSelectionDilaog(MyCartActivity.this, itemSearchResponse.getItemList().get(0).getArtCode());
                itemBatchSelectionDilaog.setItemBatchListDialogListener(this);
                ProductSearch medicine = new ProductSearch();

                medicine.setName(itemSearchResponse.getItemList().get(0).getGenericName());
                itemBatchSelectionDilaog.setTitle(itemSearchResponse.getItemList().get(0).getDescription());
                medicine.setSku(itemSearchResponse.getItemList().get(0).getArtCode());
                medicine.setQty(1);
                medicine.setDescription(itemSearchResponse.getItemList().get(0).getDescription());
                medicine.setCategory(itemSearchResponse.getItemList().get(0).getCategory());
                medicine.setMedicineType(itemSearchResponse.getItemList().get(0).getCategory());
                medicine.setIsInStock(itemSearchResponse.getItemList().get(0).getStockqty() != 0 ? 1 : 0);
                medicine.setIsPrescriptionRequired(0);
                medicine.setPrice(itemSearchResponse.getItemList().get(0).getMrp());

                if (qty != 0) {
                    itemBatchSelectionDilaog.setQtyCount(String.valueOf(qty));
                }

                itemBatchSelectionDilaog.setUnitIncreaseListener(view3 -> {
                    if (itemBatchSelectionDilaog.getQtyCount() != null && !itemBatchSelectionDilaog.getQtyCount().isEmpty()) {
                        if (itemBatchSelectionDilaog.getQtyCount() != null && !itemBatchSelectionDilaog.getQtyCount().isEmpty()) {
                            medicine.setQty(Integer.parseInt(itemBatchSelectionDilaog.getQtyCount()) + 1);
                        } else {
                            medicine.setQty(medicine.getQty() + 1);
                        }
                        itemBatchSelectionDilaog.setQtyCount("" + medicine.getQty());
                    } else {
                        Toast.makeText(MyCartActivity.this, "Please enter product quantity", Toast.LENGTH_SHORT).show();
                    }
                });
                itemBatchSelectionDilaog.setUnitDecreaseListener(view4 -> {
                    if (itemBatchSelectionDilaog.getQtyCount() != null && !itemBatchSelectionDilaog.getQtyCount().isEmpty()) {
                        if (itemBatchSelectionDilaog.getQtyCount() != null && !itemBatchSelectionDilaog.getQtyCount().isEmpty()) {
                            medicine.setQty(Integer.parseInt(itemBatchSelectionDilaog.getQtyCount()));
                        }
                        if (medicine.getQty() > 1) {
                            medicine.setQty(medicine.getQty() - 1);
                            itemBatchSelectionDilaog.setQtyCount("" + medicine.getQty());
                        }
                    }
                });
                itemBatchSelectionDilaog.setPositiveListener(view2 -> {

                    itemBatchSelectionDilaog.globalBatchListHandlings(medicine.getDescription(), medicine.getSku(),
                            balanceQty, dummyDataList, MyCartActivity.this, medicine.getMedicineType());

                    SessionManager.INSTANCE.setBatchId("");
                    if (onItemAddAgainClick) {
                        deletedataList.remove(position);
                        SessionManager.INSTANCE.setDeletedDataList(deletedataList);
                        cartUniqueCount();
                        total_itemcount_textview.setText(String.valueOf(countUniques.size()));
                        cartCount(dataList.size());
                        onItemAddAgainClick = false;

                        if (SessionManager.INSTANCE.getDeletedDataList().size() > 0) {
                            addAgainLayout.setVisibility(View.VISIBLE);
                            removedItemsCount.setText(String.valueOf(SessionManager.INSTANCE.getDeletedDataList().size()));
                        } else {
                            addAgainLayout.setVisibility(View.GONE);
                        }
                        if (SessionManager.INSTANCE.getCurationStatus()) {
                            curationProcessLayout.setVisibility(View.VISIBLE);
                        } else {
                            curationProcessLayout.setVisibility(View.GONE);
                        }
                    }

////                activityHomeBinding.transColorId.setVisibility(View.GONE);
//                    Intent intent = new Intent("cardReceiver");
//                    intent.putExtra("message", "Addtocart");
//                    intent.putExtra("product_sku", medicine.getSku());
//                    intent.putExtra("product_name", medicine.getDescription());
//                    intent.putExtra("product_quantyty", itemBatchSelectionDilaog.getQtyCount().toString());
//                    intent.putExtra("product_price", String.valueOf(itemBatchSelectionDilaog.getItemProice()));//String.valueOf(medicine.getPrice())
//                    // intent.putExtra("product_container", product_container);
//                    intent.putExtra("medicineType", medicine.getMedicineType());
//                    intent.putExtra("product_mou", String.valueOf(medicine.getMou()));
//                    intent.putExtra("product_position", String.valueOf(0));
//                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
////                OCRToDigitalMedicineResponse ocrToDigitalMedicineResponse = new OCRToDigitalMedicineResponse();
////                ocrToDigitalMedicineResponse.setArtCode(medicine.getSku());
////                ocrToDigitalMedicineResponse.setArtName(medicine.getName());
////                ocrToDigitalMedicineResponse.setQty(Integer.parseInt(itemBatchSelectionDilaog.getQtyCount().toString()));
////                ocrToDigitalMedicineResponse.setArtprice(String.valueOf(itemBatchSelectionDilaog.getItemProice()));
////                ocrToDigitalMedicineResponse.setMedicineType(medicine.getMedicineType());
////                ocrToDigitalMedicineResponse.setMou(String.valueOf(medicine.getMou()));
////                if (null != SessionManager.INSTANCE.getDataList()) {
////                    this.dataList = SessionManager.INSTANCE.getDataList();
////                    dataList.add(ocrToDigitalMedicineResponse);
////                    SessionManager.INSTANCE.setDataList(dataList);
////                    SessionManager.INSTANCE.setDataList(dataList);
////                } else {
////                    dataList.add(ocrToDigitalMedicineResponse);
////                    SessionManager.INSTANCE.setDataList(dataList);
////                }
//                    isDialogShow = false;
//                    itemBatchSelectionDilaog.dismiss();
////                Intent intent1 = new Intent(MySearchActivity.this, MyCartActivity.class);
////                intent1.putExtra("activityname", "AddMoreActivity");
////                startActivity(intent1);
////                finish();
////                overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
                });
                itemBatchSelectionDilaog.setNegativeListener(v -> {
//                activityHomeBinding.transColorId.setVisibility(View.GONE);
                    isDialogShow = false;
                    SessionManager.INSTANCE.setBatchId("");
                    itemBatchSelectionDilaog.dismiss();
                });
                isDialogShow = true;
                itemBatchSelectionDilaog.show();
            } else {
                Utils.showSnackbar(MyCartActivity.this, constraint_Layout, "No Item found");
                SessionManager.INSTANCE.setBatchId("");
            }
            Utils.dismissDialog();
        } else if (serviceType == 1) {
            crosssellcountforadapter++;
            if (itemSearchResponse.getItemList() != null && itemSearchResponse.getItemList().size() > 0) {
                crosssellingList.add(itemSearchResponse.getItemList().get(0));
                if (crosssellcountforadapter == 3) {
                    if (crosssellingList != null && crosssellingList.size() > 0) {
                        CrossCellAdapter crossCellAdapter = new CrossCellAdapter(MyCartActivity.this, crosssellingList, addToCarLayHandel);
                        RecyclerView.LayoutManager mLayoutManager4 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                        crossCell_recycle.setLayoutManager(mLayoutManager4);
                        crossCell_recycle.setItemAnimator(new DefaultItemAnimator());
                        crossCell_recycle.setAdapter(crossCellAdapter);
                        noDataFoundCrosssel.setVisibility(View.GONE);
                    } else {
                        noDataFoundCrosssel.setVisibility(View.VISIBLE);
                    }
                }
            }
            if (crosssellcountforadapter == 3 && crosssellingList != null && crosssellingList.size() > 0)
                noDataFoundCrosssel.setVisibility(View.GONE);
            else
                noDataFoundCrosssel.setVisibility(View.VISIBLE);
        } else if (serviceType == 2) {
            upssellcountforadapter++;
            if (itemSearchResponse.getItemList() != null && itemSearchResponse.getItemList().size() > 0) {
                upsellingList.add(itemSearchResponse.getItemList().get(0));
                if (upssellcountforadapter == 3) {
                    if (upsellingList != null && upsellingList.size() > 0) {
                        UpCellAdapter upCellAdapter = new UpCellAdapter(this, upsellingList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                        upcell_recycle.setLayoutManager(mLayoutManager);
                        upcell_recycle.setItemAnimator(new DefaultItemAnimator());
                        upcell_recycle.setAdapter(upCellAdapter);
                        noDataFoundUpsel.setVisibility(View.GONE);
                    } else {
                        noDataFoundUpsel.setVisibility(View.VISIBLE);
                    }
                }
            }
            if (upssellcountforadapter == 3 && upsellingList != null && upsellingList.size() > 0)
                noDataFoundUpsel.setVisibility(View.GONE);
            else
                noDataFoundUpsel.setVisibility(View.VISIBLE);
        }
    }

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
                float grandTotalVal = 0;
                for (int i = 0; i < dataList.size(); i++) {
                    if (!TextUtils.isEmpty(dataList.get(i).getArtprice())) {
                        if (dataList.get(i).getNetAmountInclTax() != null && !dataList.get(i).getNetAmountInclTax().isEmpty()) {
                            grandTotalVal = grandTotalVal + Float.parseFloat(dataList.get(i).getNetAmountInclTax());
                        } else {
                            float totalPrice = Float.parseFloat(dataList.get(i).getArtprice()) * dataList.get(i).getQty();
                            grandTotalVal = grandTotalVal + totalPrice;
                        }
                    }
                }
                String rupeeSymbol = "\u20B9";

                DecimalFormat formatter = new DecimalFormat("#,###.00");
                String pharmaformatted = formatter.format(grandTotalVal);

                grandTotalPrice.setText(rupeeSymbol + "" + pharmaformatted);

                Utils.showSnackbar(MyCartActivity.this, constraint_Layout, getApplicationContext().getResources().getString(R.string.label_item_added_cart));
                cartCount(dataList.size());

                groupingDuplicates();

                cartListAdapter = new MyCartListAdapter(MyCartActivity.this, dataComparingList, MyCartActivity.this, expandholdedDataList);
                cartItemRecyclerView.setLayoutManager(new LinearLayoutManager(MyCartActivity.this));
                cartItemRecyclerView.setAdapter(cartListAdapter);
                cartListAdapter.notifyDataSetChanged();
            }
            if (dataList != null && dataList.size() > 0)
                if (cartListAdapter != null) {

                    groupingDuplicates();

                    cartListAdapter.notifyDataSetChanged();
                }
        }
    };

    private BroadcastReceiver mMessageReceivers = new BroadcastReceiver() {
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
                float grandTotalVal = 0;
                for (int i = 0; i < dataList.size(); i++) {
                    if (!TextUtils.isEmpty(dataList.get(i).getArtprice())) {
                        if (dataList.get(i).getNetAmountInclTax() != null && !dataList.get(i).getNetAmountInclTax().isEmpty()) {
                            grandTotalVal = grandTotalVal + Float.parseFloat(dataList.get(i).getNetAmountInclTax());
                        } else {
                            float totalPrice = Float.parseFloat(dataList.get(i).getArtprice()) * dataList.get(i).getQty();
                            grandTotalVal = grandTotalVal + totalPrice;
                        }
                    }
                }
                String rupeeSymbol = "\u20B9";

                DecimalFormat formatter = new DecimalFormat("#,###.00");
                String pharmaformatted = formatter.format(grandTotalVal);

                grandTotalPrice.setText(rupeeSymbol + "" + pharmaformatted);

                Utils.showSnackbar(MyCartActivity.this, constraint_Layout, getApplicationContext().getResources().getString(R.string.label_item_added_cart));
                cartCount(dataList.size());

                groupingDuplicates();

                cartListAdapter = new MyCartListAdapter(MyCartActivity.this, dataComparingList, MyCartActivity.this, expandholdedDataList);
                cartItemRecyclerView.setLayoutManager(new LinearLayoutManager(MyCartActivity.this));
                cartItemRecyclerView.setAdapter(cartListAdapter);
                cartListAdapter.notifyDataSetChanged();
            }
            if (dataList != null && dataList.size() > 0)
                if (cartListAdapter != null) {

                    groupingDuplicates();

                    cartListAdapter.notifyDataSetChanged();
                }
//            if (null != SessionManager.INSTANCE.getDataList() && SessionManager.INSTANCE.getDataList().size() > 0)
//                checkOutNewBtn.setVisibility(View.VISIBLE);
//            else
//                checkOutNewBtn.setVisibility(View.GONE);
        }
    };

    @Override
    public void onFailureBarcodeItemApi(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickIncrement(int position) {
        int currentVal = dataList.get(position).getQty() + 1;
        dataList.get(position).setQty(currentVal);
        updatePriceInfo(position);
    }

    @Override
    public void onClickDecrement(int position) {
        if (1 < dataList.get(position).getQty()) {
            dataList.get(position).setQty(dataList.get(position).getQty() - 1);
            updatePriceInfo(position);
        }
    }

    int pos;

    @Override
    public void onClickDelete(int position, OCRToDigitalMedicineResponse item) {
        final Dialog dialog = new Dialog(MyCartActivity.this);
        dialog.setContentView(R.layout.dialog_custom_alert);
        dialog.setCancelable(false);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView dialogTitleText = dialog.findViewById(R.id.dialog_info);
        Button okButton = dialog.findViewById(R.id.dialog_ok);
        Button declineButton = dialog.findViewById(R.id.dialog_cancel);
        dialogTitleText.setText(getResources().getString(R.string.label_delete_cart_item));
        okButton.setText(getResources().getString(R.string.label_ok));
        declineButton.setText(getResources().getString(R.string.label_cancel_text));
        okButton.setOnClickListener(v -> {
            OCRToDigitalMedicineResponse loadobject = new OCRToDigitalMedicineResponse();

            for (int i = 0; i < dataList.size(); i++) {
//                if (dataList.get(i).getArtCode().equalsIgnoreCase(item.getArtCode()) ) {
                if (dataList.get(i).getArtCode().equalsIgnoreCase(item.getArtCode())) {
                    pos = i;
                    loadobject.setArtCode(dataList.get(i).getArtCode());
                    loadobject.setArtName(dataList.get(i).getArtName());
                    loadobject.setQty(dataList.get(i).getQty());
                    loadobject.setMedicineType(dataList.get(i).getMedicineType());
                    loadobject.setContainer(dataList.get(i).getContainer());
                    loadobject.setArtprice(dataList.get(i).getArtprice());
                    deletedataList.add(loadobject);
                    SessionManager.INSTANCE.setDeletedDataList(deletedataList);


                }


            }
//            }

//            loadobject.setArtCode(dataList.get(position).getArtCode());
//            loadobject.setArtName(dataList.get(position).getArtName());
//            loadobject.setQty(dataList.get(position).getQty());
//            loadobject.setMedicineType(dataList.get(position).getMedicineType());
//            loadobject.setContainer(dataList.get(position).getContainer());
//            loadobject.setArtprice(dataList.get(position).getArtprice());
//            deletedataList.add(loadobject);
//            SessionManager.INSTANCE.setDeletedDataList(deletedataList);


            dataList.remove(pos);

            String rupeeSymbol = "\u20B9";
            SessionManager.INSTANCE.setDataList(dataList);
            cartCount(dataList.size());
            if (null != dataList && dataList.size() > 0) {
                float totalMrpPrice = 0;
                for (int i = 0; i < dataList.size(); i++) {
                    if (!TextUtils.isEmpty(dataList.get(i).getArtprice())) {
                        if (dataList.get(i).getNetAmountInclTax() != null && !dataList.get(i).getNetAmountInclTax().isEmpty()) {
                            totalMrpPrice = Float.parseFloat(dataList.get(i).getNetAmountInclTax()) + totalMrpPrice;
                        } else {
                            totalMrpPrice = Float.parseFloat(dataList.get(i).getArtprice()) * dataList.get(i).getQty() + totalMrpPrice;
                        }
                    }
                }

                DecimalFormat formatter = new DecimalFormat("#,###.00");
                String pharmaformatted = formatter.format(totalMrpPrice);

                grandTotalPrice.setText(rupeeSymbol + "" + pharmaformatted);
            } else {
                grandTotalPrice.setText(rupeeSymbol + "00.00");
            }

            groupingDuplicates();

            cartListAdapter = new MyCartListAdapter(MyCartActivity.this, dataComparingList, this, expandholdedDataList);
            cartItemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            cartItemRecyclerView.setAdapter(cartListAdapter);
            cartListAdapter.notifyDataSetChanged();
            grandTotalPriceValue = grandTotalPrice.getText().toString();
            dialog.dismiss();
            if (SessionManager.INSTANCE.getDeletedDataList().size() > 0) {
                addAgainLayout.setVisibility(View.VISIBLE);
                removedItemsCount.setText(String.valueOf(SessionManager.INSTANCE.getDeletedDataList().size()));
            } else {
                addAgainLayout.setVisibility(View.GONE);
            }
            if (SessionManager.INSTANCE.getCurationStatus()) {
                curationProcessLayout.setVisibility(View.VISIBLE);
            } else {
                curationProcessLayout.setVisibility(View.GONE);
            }
        });
        declineButton.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public void onClickEdit(int position) {

    }

    @Override
    public void onItemClick(int position) {

    }

    private void updatePriceInfo(int position) {
        String rupeeSymbol = "\u20B9";
        float totalMrpPrice = 0;
        for (int i = 0; i < dataList.size(); i++) {
            if (!TextUtils.isEmpty(dataList.get(i).getArtprice())) {
                if (dataList.get(i).getNetAmountInclTax() != null && !dataList.get(i).getNetAmountInclTax().isEmpty()) {
                    totalMrpPrice = Float.parseFloat(dataList.get(i).getNetAmountInclTax()) + totalMrpPrice;
                } else {
                    totalMrpPrice = Float.parseFloat(dataList.get(i).getArtprice()) * dataList.get(i).getQty() + totalMrpPrice;
                }
            }
        }

        DecimalFormat formatter = new DecimalFormat("#,###.00");
        String pharmaformatted = formatter.format(totalMrpPrice);

        grandTotalPrice.setText(rupeeSymbol + "" + pharmaformatted);
        grandTotalPriceValue = grandTotalPrice.getText().toString();

        SessionManager.INSTANCE.setDataList(dataList);
        cartCount(dataList.size());
        cartListAdapter.notifyItemChanged(position);
    }

    private void checkDeliveryOption() {
        boolean homeDeliveryMode = SessionManager.INSTANCE.getKioskSetupResponse().getDeliveryMode().getHomeDelivery();
        boolean storePickupDeliveryMode = SessionManager.INSTANCE.getKioskSetupResponse().getDeliveryMode().getStorePickup();
        boolean atKioskDeliveryMode = SessionManager.INSTANCE.getKioskSetupResponse().getDeliveryMode().getAtKiosk();
        int deliveryMode = 0;
        if (homeDeliveryMode) {
            deliveryMode++;
        }
        if (storePickupDeliveryMode) {
            deliveryMode++;
        }
        if (atKioskDeliveryMode) {
            deliveryMode++;
        }
        if (deliveryMode >= 2) {
            handleNextActivity();
        } else {
            SessionManager.INSTANCE.setDataList(dataList);
            SessionManager.INSTANCE.setCartItems(dataList);
            if (homeDeliveryMode) {
                SessionManager.INSTANCE.setDeliverytype("DeliveryAtHome");
                if (NetworkUtils.isNetworkConnected(this)) {
                    Utils.showDialog(this, getApplicationContext().getResources().getString(R.string.label_please_wait));
                    myCartController.handelFetchAddressService();
                } else {
                    Utils.showSnackbar(this, constraint_Layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
                }
            } else if (storePickupDeliveryMode) {
                SessionManager.INSTANCE.setDeliverytype("PickfromStore");
                Intent intent1 = new Intent(MyCartActivity.this, StorePickupActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            } else if (atKioskDeliveryMode) {
                SessionManager.INSTANCE.setDeliverytype("AtKiosk");
                UserAddress userAddress = new UserAddress();
                userAddress.setName(SessionManager.INSTANCE.getKioskSetupResponse().getKIOSK_ID());
                userAddress.setAddress1(SessionManager.INSTANCE.getKioskSetupResponse().getADDRESS());
                userAddress.setCity(SessionManager.INSTANCE.getKioskSetupResponse().getCITY());
                userAddress.setState(SessionManager.INSTANCE.getKioskSetupResponse().getSTATE());
                userAddress.setPincode(SessionManager.INSTANCE.getKioskSetupResponse().getPINCODE());
                SessionManager.INSTANCE.setUseraddress(userAddress);

                Intent intent2 = new Intent(MyCartActivity.this, OrderSummaryActivity.class);
                intent2.putExtra("activityname", "DeliverySelectionActivity");
                startActivity(intent2);
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            }
        }
    }

    private void handleNextActivity() {
        SessionManager.INSTANCE.setCartItems(dataList);
        SessionManager.INSTANCE.setDataList(dataList);
        Intent intent = new Intent(MyCartActivity.this, DeliverySelectionActivity.class);
        intent.putExtra("items_count", String.valueOf(dataList.size()));
        intent.putExtra("grand_total_price", grandTotalPriceValue);
        startActivity(intent);
        overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
    }

    @Override
    public void onSuccessGetAddress(ArrayList<UserAddress> response) {
        Utils.dismissDialog();
        if (response != null && response.size() > 0) {
            ArrayList<UserAddress> addressList = new ArrayList<>(response);
            if (addressList.size() == 0 || addressList == null) {
                Intent intent = new Intent(this, AddressAddActivity.class);
                intent.putExtra("addressList", addressList.size());
                startActivity(intent);
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            } else {
                Intent intent = new Intent(this, AddressListActivity.class);
                intent.putExtra("addressList", addressList);
                startActivity(intent);
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            }
        } else {
            Intent intent = new Intent(this, AddressAddActivity.class);
            intent.putExtra("addressList", 0);
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        }
    }

    @Override
    public void onFailure(String error) {
        Utils.showCustomAlertDialog(this, getResources().getString(R.string.label_server_err_message), false, "OK", "");
    }

    private void handleScannedImageView() {
        if (!TextUtils.isEmpty(SessionManager.INSTANCE.getScannedImagePath())) {
            scannedImageHeader.setVisibility(View.VISIBLE);
            scannedImageLayout.setVisibility(View.VISIBLE);
            Glide.with(MyCartActivity.this)
                    .load(SessionManager.INSTANCE.getScannedImagePath())
                    .apply(new RequestOptions().placeholder(R.drawable.thumbnail_image).error(R.drawable.thumbnail_image))
                    .into(scannedImage);
            scannedImage.setOnClickListener(v -> {
                if (SessionManager.INSTANCE.getScannedPrescriptionItems() != null && SessionManager.INSTANCE.getScannedPrescriptionItems().size() > 0) {
                    ScannedPrescriptionViewDialog scannedPrecViewDialog = new ScannedPrescriptionViewDialog(MyCartActivity.this, SessionManager.INSTANCE.getScannedPrescriptionItems());
                    scannedPrecViewDialog.show();
                    Window window = scannedPrecViewDialog.getWindow();
                    int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
                    int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.95);
                    if (window != null) {
                        window.setLayout(width, height);
                    }
                } else {
                    Utils.showSnackbar(MyCartActivity.this, constraint_Layout, getApplicationContext().getResources().getString(R.string.label_curation_progress_wait));
                }
            });
        } else {
            scannedImageHeader.setVisibility(View.INVISIBLE);
            scannedImageLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSuccessUploadBgImage() {
        Utils.printMessage(TAG, "Successfully Image Uploaded");
    }

    private int scannerEvent = 0;

    @Override
    public void scannerBarcodeEvent(byte[] barcodeData, int barcodeType, int scannerID) {
        if (!isDialogShow) {
            if (scannerEvent == 0) {
                scannerEvent = 1;
                barcodeEventHandle();
                String barcode_code = new String(barcodeData);
                if (barcode_code != null) {
//            Toast.makeText(this, barcode_code, Toast.LENGTH_LONG).show();
                    Utils.showDialog(this, "Plaese wait...");
                    myCartController.searchItemProducts(barcode_code, 0, 0, 0);
                } else {
                    Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Utils.showSnackbar(MyCartActivity.this, constraint_Layout, "Please complete present action first.");
        }
    }

    private void barcodeEventHandle() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scannerEvent = 0;
            }
        }, 5000);
    }

    @Override
    public void scannerFirmwareUpdateEvent(FirmwareUpdateEvent firmwareUpdateEvent) {

    }

    @Override
    public void scannerImageEvent(byte[] imageData) {

    }

    @Override
    public void scannerVideoEvent(byte[] videoData) {

    }

    @Override
    public void onDismissDialog() {
        isDialogShow = false;
    }

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