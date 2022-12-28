package com.apollo.pharmacy.ocr.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.mposstoresetup.MposStoreSetupActivity;
import com.apollo.pharmacy.ocr.activities.userlogin.UserLoginActivity;
import com.apollo.pharmacy.ocr.controller.MainActivityController;
import com.apollo.pharmacy.ocr.dialog.AccesskeyDialog;
import com.apollo.pharmacy.ocr.interfaces.MainListener;
import com.apollo.pharmacy.ocr.model.API;
import com.apollo.pharmacy.ocr.model.Global_api_response;
import com.apollo.pharmacy.ocr.model.OCRToDigitalMedicineResponse;
import com.apollo.pharmacy.ocr.receiver.ConnectivityReceiver;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.apollo.pharmacy.ocr.utility.LanguageManager;
import com.apollo.pharmacy.ocr.utility.NetworkUtils;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.apollo.pharmacy.ocr.utility.Utils;
import com.apollo.pharmacy.ocr.widget.MarqueeView;
import com.apollo.pharmacy.ocr.widget.MarqueeViewRight;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener, MainListener {

    private LinearLayout langContinueLayout;
    private TextView englishLangText, tamilLangText, teluguLangText, englishLangBtn, hindiLangBtn, teluguLangBtn, kannadaLangBtn,
            marathiLangBtn, malayalamLangBtn, panjabiLangBtn, tamilLangBtn, gujarathiLangBtn, urduLangBtn, letsBeginText;
    private ImageView moreLangImage, settings;
    private RelativeLayout mainLangLayout, moreLangLayout;
    private ConstraintLayout constraint_layout;
    private MainActivityController mainActivityController;
    private static final String TAG = MainActivity.class.getSimpleName();

    public MainActivity() {
        super();
    }

    //Note::
    //Disable logs boolean value at Constants.java
    //Enable Firebase Crashlytics lib at gradle file
    //Remove Static Mobile number at registration page
    //Device reg is currently disable at device registration service "Make In Active"

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
        MainActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        // clear the notification area when the app is opened
        Utils.clearNotifications(getApplicationContext());
        Constants.getInstance().setConnectivityListener(this);
        if (!ConnectivityReceiver.isConnected()) {
            findViewById(R.id.networkErrorLayout).setVisibility(View.VISIBLE);
        }

//        if (SessionManager.INSTANCE.getAccessDialogHandler().equals("Dismiss")) {
//            AccesskeyDialog accesskeyDialog=new AccesskeyDialog(MainActivity.this);
//            accesskeyDialog.dismiss();
//        }
    }

    TextView crash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lang_selection);

       List<OCRToDigitalMedicineResponse> dataList = new ArrayList<>();
        SessionManager.INSTANCE.setDataList(dataList);
        SessionManager.INSTANCE.setDeletedDataList(dataList);

//        if (SessionManager.INSTANCE.getAccessKey() != null && SessionManager.INSTANCE.getAccessKey().equals("AP@11@2021")) {
//            if (SessionManager.INSTANCE.getStoreId() != null && !SessionManager.INSTANCE.getStoreId().isEmpty()
//                    && SessionManager.INSTANCE.getTerminalId() != null && !SessionManager.INSTANCE.getTerminalId().isEmpty() &&
//                    SessionManager.INSTANCE.getEposUrl() != null && !SessionManager.INSTANCE.getEposUrl().isEmpty()) {
////                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
//            } else {
//                Intent intent = new Intent(MainActivity.this, MposStoreSetupActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//            }
//        } else {
//            AccesskeyDialog accesskeyDialog = new AccesskeyDialog(MainActivity.this);
//            accesskeyDialog.onClickSubmit(v1 -> {
//                accesskeyDialog.listener();
//                if (accesskeyDialog.validate()) {
//                    Intent intent = new Intent(MainActivity.this, MposStoreSetupActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//                    accesskeyDialog.dismiss();
//                }
//            });
//            accesskeyDialog.show();
//        }


        initMarque();
//        FirebaseApp.initializeApp(getApplicationContext());
//        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
//        SessionManager.INSTANCE.setAccessKey("65536");
        zoomAnim();
//        crash.setText("crash");
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        ImageView faqLayout = findViewById(R.id.faq);

        faqLayout.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, FAQActivity.class)));
        mainLangLayout = findViewById(R.id.main_lang_layout);
        moreLangLayout = findViewById(R.id.more_lang_layout);
        englishLangText = findViewById(R.id.english_lang_text);
        tamilLangText = findViewById(R.id.tamil_lang_text);
        teluguLangText = findViewById(R.id.telugu_lang_text);
        moreLangImage = findViewById(R.id.more_language_img);
        settings = findViewById(R.id.settings);
        letsBeginText = findViewById(R.id.lets_begin_text);
        langContinueLayout = findViewById(R.id.lang_continue_layout);
        LinearLayout moreLangContinueLayout = findViewById(R.id.more_lang_continue_layout);
        ImageView backIconImage = findViewById(R.id.back_icon);
        englishLangBtn = findViewById(R.id.english_lang_btn);
        hindiLangBtn = findViewById(R.id.hindi_lang_btn);
        teluguLangBtn = findViewById(R.id.telugu_lang_btn);
        kannadaLangBtn = findViewById(R.id.kannada_lang_btn);
        marathiLangBtn = findViewById(R.id.marathi_lang_btn);
        malayalamLangBtn = findViewById(R.id.malayalam_lang_btn);
        panjabiLangBtn = findViewById(R.id.panjabi_lang_btn);
        tamilLangBtn = findViewById(R.id.tamil_lang_btn);
        gujarathiLangBtn = findViewById(R.id.gujarathi_lang_btn);
        urduLangBtn = findViewById(R.id.urdu_lang_btn);

        ImageView onlineMedicineStoreText = findViewById(R.id.online_medicine_store_log);
        LinearLayout langViewLayout = findViewById(R.id.lang_view_layout);
        Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.trans_slide_right_left);
        onlineMedicineStoreText.setVisibility(View.INVISIBLE);
        langViewLayout.setVisibility(View.GONE);
        moreLangImage.setVisibility(View.GONE);
        langContinueLayout.setVisibility(View.GONE);
        constraint_layout = findViewById(R.id.constraint_layout);
        mainActivityController = new MainActivityController(this);

//        SessionManager.INSTANCE.logoutUser();

        new Handler().postDelayed(() -> onlineMedicineStoreText.setVisibility(View.VISIBLE), 2000);

        new Handler().postDelayed(() -> {
            langViewLayout.setVisibility(View.VISIBLE);
            langViewLayout.startAnimation(animSlide);
        }, 600);

        moreLangImage.setOnClickListener(v -> {
            mainLangLayout.setVisibility(View.GONE);
            moreLangLayout.setVisibility(View.VISIBLE);
        });

        backIconImage.setOnClickListener(v -> {
            moreLangLayout.setVisibility(View.GONE);
            mainLangLayout.setVisibility(View.VISIBLE);
        });

        englishLangText.setOnClickListener(view -> changeLangReCreate("en"));
        tamilLangText.setOnClickListener(view -> changeLangReCreate("ta"));
        teluguLangText.setOnClickListener(view -> changeLangReCreate("te"));

        englishLangBtn.setOnClickListener(view -> changeLangReCreate("en"));
        hindiLangBtn.setOnClickListener(view -> changeLangReCreate("hi"));
        teluguLangBtn.setOnClickListener(view -> changeLangReCreate("te"));
        kannadaLangBtn.setOnClickListener(v -> changeLangReCreate("kn"));
//        marathiLangBtn
        malayalamLangBtn.setOnClickListener(v -> changeLangReCreate("ml"));
//        panjabiLangBtn
        tamilLangBtn.setOnClickListener(v -> changeLangReCreate("ta"));
//        gujarathiLangBtn
//        urduLangBtn

        langContinueLayout.setOnClickListener(view -> {
            if (SessionManager.INSTANCE.getStoreId() != null && !SessionManager.INSTANCE.getStoreId().isEmpty()
                    && SessionManager.INSTANCE.getTerminalId() != null && !SessionManager.INSTANCE.getTerminalId().isEmpty() &&
                    SessionManager.INSTANCE.getEposUrl() != null && !SessionManager.INSTANCE.getEposUrl().isEmpty()) {
                Intent intent = new Intent(MainActivity.this, UserLoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            } else {
                AccesskeyDialog accesskeyDialog = new AccesskeyDialog(MainActivity.this);
                accesskeyDialog.onClickSubmit(v1 -> {
                    accesskeyDialog.listener();
                    if (accesskeyDialog.validate()) {
                        Intent intent = new Intent(MainActivity.this, MposStoreSetupActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
                        accesskeyDialog.dismiss();
                    }
                });
                accesskeyDialog.show();
//                Intent intent = new Intent(MainActivity.this, MposStoreSetupActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            }
        });

        moreLangContinueLayout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserLoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        });

        mainActivityController.getGlobalApiList(this);

//        long timeElapsed = 100000; //For example
//        CountDownTimer countdown_timer = new CountDownTimer(timeElapsed, 1000) {
//            public void onTick(long millisUntilFinished) {
//                int mins = (int) countVal / 60;
//                countVal = countVal - mins * 60;
//                int secs = (int) countVal;
//                String asText = mins + "M " + secs + "S";
////                int hours = (int) (timeElapsed / 3600000);
////                int minutes = (int) (timeElapsed - hours * 3600000) / 60000;
////                int seconds = (int) (timeElapsed - hours * 3600000 - minutes * 60000) / 1000;
//                Utils.printMessage(TAG, "Time : "+asText);
//                countVal++;
//            }
//
//            public void onFinish() {
//
//            }
//        }.start();

        settings.setOnClickListener(view -> {
            AccesskeyDialog accesskeyDialog = new AccesskeyDialog(MainActivity.this);
            accesskeyDialog.onClickSubmit(v1 -> {
                accesskeyDialog.listener();
                if (accesskeyDialog.validate()) {
                    Intent intent = new Intent(MainActivity.this, MposStoreSetupActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
                    accesskeyDialog.dismiss();
                }
            });
            accesskeyDialog.show();
        });

    }

    int countVal = 0;

    private void initMarque() {
        MarqueeView first_text = findViewById(R.id.first_text);
        first_text.setText(getApplicationContext().getResources().getString(R.string.label_scroll_welcome));
        first_text.start();
        MarqueeViewRight second_text = findViewById(R.id.second_text);
        second_text.setText(getApplicationContext().getResources().getString(R.string.label_scroll_apollo));
        second_text.start();
        MarqueeView third_text = findViewById(R.id.third_text);
        third_text.setText(getApplicationContext().getResources().getString(R.string.label_scroll_pharmacy));
        third_text.start();
    }

    private void zoomAnim() {
        ImageView app_logo = findViewById(R.id.app_logo);
        Animation animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        app_logo.startAnimation(animZoomOut);
    }

    @Override
    public void onSuccessGlobalApiResponse(Global_api_response list) {
        List<API> apiList = list.getAPIS();
        for (API list1 : apiList) {
            String comparestring = list1.getNAME();
            String url = list1.getURL();
            Utils.printMessage(TAG, "Api list--> " + url + list1.getNAME());
            if (comparestring.equalsIgnoreCase("Add_FCM_Token")) {
                Constants.Add_FCM_Token = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Send_Otp")) {
                Constants.Send_Otp = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Get_Past_Prescription")) {
                Constants.Get_Past_Prescription = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Delete_the_Prescription")) {
                Constants.Delete_the_Prescription = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Get_Portfolio_of_the_User")) {
                Constants.Get_Portfolio_of_the_User = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Get_Scanned_Prescription_Image")) {
                Constants.Get_Scanned_Prescription_Image = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Get_Prescription_Medicine_List")) {
                Constants.Get_Prescription_Medicine_List = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Get_Special_Offers_Products")) {
                Constants.Get_Special_Offers_Products = list1.getURL();
                Constants.Get_Product_List = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Get_Trending_now_Products")) {
                Constants.Get_Trending_now_Products = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Get_The_price_for_Past_Prescription_Medicine_list")) {
                Constants.Get_The_price_for_Past_Prescription_Medicine_list = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Search_Suggestions")) {
                Constants.Search_Suggestions = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Search_Product")) {
                Constants.Search_Product = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Get_Redeem_Points")) {
                Constants.Get_Redeem_Points = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Redeem_points_Send_Otp")) {
                Constants.Redeem_points_Send_Otp = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Redeem_Points_Resend_Otp")) {
                Constants.Redeem_Points_Resend_Otp = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Redeem_Points_Validate_Otp")) {
                Constants.Redeem_Points_Validate_Otp = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Redeem_points_Retry_Validate_Otp")) {
                Constants.Redeem_points_Retry_Validate_Otp = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Redeem_Points_Check_Voucher")) {
                Constants.Redeem_Points_Check_Voucher = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Redeem_Voucher")) {
                Constants.Redeem_Voucher = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Get_Store_Locator")) {
                Constants.Get_Store_Locator = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Get_Bit_List_for_Jiyo")) {
                Constants.Get_Bit_List_for_Jiyo = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Get_Display_Video_For_Jiyo")) {
                Constants.Get_Display_Video_For_Jiyo = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Paytm_Payment_Transaction")) {
                Constants.Paytm_Payment_Transaction = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Pinelab_Upload_Transaction")) {
                Constants.Pinelab_Upload_Transaction = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Pinelab_Get_Cloud_Bases_Transaction")) {
                Constants.Pinelab_Get_Cloud_Bases_Transaction = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Pinelab_Cancel_Transaction")) {
                Constants.Pinelab_Cancel_Transaction = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Get_User_Delivery_Address_List")) {
                Constants.Get_User_Delivery_Address_List = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Edit_User_Delivery_Address")) {
                Constants.Edit_User_Delivery_Address = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Add_User_Delivery_Address")) {
                Constants.Add_User_Delivery_Address = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Delete_User_Delivery_address")) {
                Constants.Delete_User_Delivery_address = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Get_State_city_List")) {
                Constants.Get_State_city_List = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Get_Order_History_For_User")) {
                Constants.Get_Order_History_For_User = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Order_Pushing_Api")) {
                Constants.Order_Pushing_Api = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("Get_Image_link")) {
                Constants.Get_Image_link = list1.getURL();
            } else if (comparestring.equalsIgnoreCase("GET_CATEGORY_LIST")) {
                Constants.Categorylist_Url = list1.getURL();
            }
        }
        handleNextSelectionVisibility();
        handleFcmTokenFunctionality();
    }

    @Override
    public void onFailure(String message) {
        Utils.showSnackbar(MainActivity.this, constraint_layout, message);
    }

    private void handleFcmTokenFunctionality() {
        if (NetworkUtils.isNetworkConnected(MainActivity.this)) {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!SessionManager.INSTANCE.isFcmAdded()) {
                                mainActivityController.handleFCMTokenRegistration(task.getResult().getToken());
                            }
                        }
                    });
        } else {
            Utils.showSnackbar(MainActivity.this, constraint_layout, getApplicationContext().getResources().getString(R.string.label_internet_error_text));
        }
        initialLanguage();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void changeLangReCreate(String selectedLang) {
        LanguageManager.Companion.setNewLocale(this, selectedLang);
        initialLanguage();
    }

    public void initialLanguage() {
        SharedPreferences m = PreferenceManager.getDefaultSharedPreferences(this);
        if (m.getString(LanguageManager.LANGUAGE_KEY, "en").equalsIgnoreCase("en")) {
            letsBeginText.setText(getApplicationContext().getResources().getString(R.string.Lets_Begin));

            englishLangText.setBackgroundResource(R.drawable.lang_curved_selected_bg);
            tamilLangText.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            teluguLangText.setBackgroundResource(R.drawable.lang_curved_unselected_bg);

            englishLangBtn.setBackgroundResource(R.drawable.lang_curved_selected_bg);
            hindiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            teluguLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            kannadaLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            marathiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            malayalamLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            panjabiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            tamilLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            gujarathiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            urduLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
        } else if (Objects.requireNonNull(m.getString(LanguageManager.LANGUAGE_KEY, "en")).equalsIgnoreCase("ta")) {
            letsBeginText.setText(getApplicationContext().getResources().getString(R.string.Lets_Begin));

            englishLangText.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            tamilLangText.setBackgroundResource(R.drawable.lang_curved_selected_bg);
            teluguLangText.setBackgroundResource(R.drawable.lang_curved_unselected_bg);

            englishLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            hindiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            teluguLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            kannadaLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            marathiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            malayalamLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            panjabiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            tamilLangBtn.setBackgroundResource(R.drawable.lang_curved_selected_bg);
            gujarathiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            urduLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
        } else if (Objects.requireNonNull(m.getString(LanguageManager.LANGUAGE_KEY, "en")).equalsIgnoreCase("te")) {
            letsBeginText.setText(getApplicationContext().getResources().getString(R.string.Lets_Begin));

            englishLangText.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            tamilLangText.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            teluguLangText.setBackgroundResource(R.drawable.lang_curved_selected_bg);

            englishLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            hindiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            teluguLangBtn.setBackgroundResource(R.drawable.lang_curved_selected_bg);
            kannadaLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            marathiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            malayalamLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            panjabiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            tamilLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            gujarathiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            urduLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
        } else if (Objects.requireNonNull(m.getString(LanguageManager.LANGUAGE_KEY, "en")).equalsIgnoreCase("hi")) {
            letsBeginText.setText(getApplicationContext().getResources().getString(R.string.Lets_Begin));

            englishLangText.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            tamilLangText.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            teluguLangText.setBackgroundResource(R.drawable.lang_curved_unselected_bg);

            englishLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            hindiLangBtn.setBackgroundResource(R.drawable.lang_curved_selected_bg);
            teluguLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            kannadaLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            marathiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            malayalamLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            panjabiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            tamilLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            gujarathiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            urduLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
        } else if (Objects.requireNonNull(m.getString(LanguageManager.LANGUAGE_KEY, "en")).equalsIgnoreCase("kn")) {
            letsBeginText.setText(getApplicationContext().getResources().getString(R.string.Lets_Begin));

            englishLangText.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            tamilLangText.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            teluguLangText.setBackgroundResource(R.drawable.lang_curved_unselected_bg);

            englishLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            hindiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            teluguLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            kannadaLangBtn.setBackgroundResource(R.drawable.lang_curved_selected_bg);
            marathiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            malayalamLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            panjabiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            tamilLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            gujarathiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            urduLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
        } else if (Objects.requireNonNull(m.getString(LanguageManager.LANGUAGE_KEY, "en")).equalsIgnoreCase("ml")) {
            letsBeginText.setText(getApplicationContext().getResources().getString(R.string.Lets_Begin));

            englishLangText.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            tamilLangText.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            teluguLangText.setBackgroundResource(R.drawable.lang_curved_unselected_bg);

            englishLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            hindiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            teluguLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            kannadaLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            marathiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            malayalamLangBtn.setBackgroundResource(R.drawable.lang_curved_selected_bg);
            panjabiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            tamilLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            gujarathiLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
            urduLangBtn.setBackgroundResource(R.drawable.lang_curved_unselected_bg);
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void handleNextSelectionVisibility() {
        new Handler().postDelayed(() -> {
            moreLangImage.setVisibility(View.VISIBLE);
            langContinueLayout.setVisibility(View.VISIBLE);
        }, 2500);
    }
}