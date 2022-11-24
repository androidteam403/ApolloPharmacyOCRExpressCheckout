package com.apollo.pharmacy.ocr.activities.userlogin

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.view.animation.TranslateAnimation
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.apollo.pharmacy.ocr.R
import com.apollo.pharmacy.ocr.activities.*
import com.apollo.pharmacy.ocr.activities.checkout.CheckoutActivity
import com.apollo.pharmacy.ocr.activities.mposstoresetup.MposStoreSetupActivity
import com.apollo.pharmacy.ocr.activities.userlogin.model.GetGlobalConfigurationResponse
import com.apollo.pharmacy.ocr.controller.UserLoginController
import com.apollo.pharmacy.ocr.dialog.AccesskeyDialog
import com.apollo.pharmacy.ocr.interfaces.UserLoginListener
import com.apollo.pharmacy.ocr.model.*
import com.apollo.pharmacy.ocr.network.ApiClient
import com.apollo.pharmacy.ocr.network.CallbackWithRetry
import com.apollo.pharmacy.ocr.receiver.ConnectivityReceiver
import com.apollo.pharmacy.ocr.utility.Constants
import com.apollo.pharmacy.ocr.utility.NetworkUtils
import com.apollo.pharmacy.ocr.utility.SessionManager
import com.apollo.pharmacy.ocr.utility.SessionManager.getEposUrl
import com.apollo.pharmacy.ocr.utility.SessionManager.getMobilenumber
import com.apollo.pharmacy.ocr.utility.SessionManager.getStoreId
import com.apollo.pharmacy.ocr.utility.SessionManager.getTerminalId
import com.apollo.pharmacy.ocr.utility.SessionManager.isFcmAdded
import com.apollo.pharmacy.ocr.utility.SessionManager.setDataList
import com.apollo.pharmacy.ocr.utility.SessionManager.setDeletedDataList
import com.apollo.pharmacy.ocr.utility.Utils
import com.apollo.pharmacy.ocr.widget.CustomKeyboard
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_user_login.*
import kotlinx.android.synthetic.main.view_faq_layout.*
import retrofit2.Call
import retrofit2.Response
import java.util.*

class UserLoginActivity : AppCompatActivity(), UserLoginListener,
    ConnectivityReceiver.ConnectivityReceiverListener {

    val keyboard = null
    var loginActivityName = ""

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            findViewById<View>(R.id.networkErrorLayout).visibility = View.GONE
        } else {
            findViewById<View>(R.id.networkErrorLayout).visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onSendSmsSuccess() {
        Utils.dismissDialog()
        mobile_number_input_layout.visibility = View.GONE
        otp_parent_layout.visibility = View.VISIBLE
        entered_mobile_number.setText(mobileNum)
        SessionManager.setMobilenumber(mobileNum)
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        setOtpNumberKeyboard()
    }

    override fun onSendSmsFailure() {
        Utils.dismissDialog()
        Utils.showCustomAlertDialog(
            this,
            resources.getString(R.string.label_server_err_message),
            false,
            resources.getString(R.string.label_ok),
            ""
        )
    }

    override fun onFailure(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    lateinit var utils: Utils
    lateinit var userLoginController: UserLoginController
    var mobileNum: String = ""
    var otp: Int = 0
    var oldMobileNum: String = ""
    lateinit var countDownTimer: CountDownTimer
    lateinit var constraint_layout: ConstraintLayout;

    override fun onResume() {
        super.onResume()
        val decorView = window.decorView
        decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        Constants.getInstance().setConnectivityListener(this)
        if (!ConnectivityReceiver.isConnected()) {
            findViewById<View>(R.id.networkErrorLayout).visibility = View.VISIBLE
        }

        if (!NetworkUtils.isNetworkConnected(this@UserLoginActivity)) {
            findViewById<View>(R.id.networkErrorLayout).visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)
        if (intent != null) {
            loginActivityName = intent.getStringExtra("userLoginActivity")
        }


//        if (SessionManager.getMobilenumber().isEmpty()) {
//            val dataList: List<OCRToDigitalMedicineResponse> = ArrayList()
//            setDataList(dataList)
//            setDeletedDataList(dataList)
//        }


        val backButton = findViewById<ImageView>(R.id.onClickBack);

        backButton.setOnClickListener { v ->

            if (loginActivityName.equals("mySearchActivityProfileLogin") || loginActivityName.equals(
                    "mySearchActivityOrdersLogin"
                ) || loginActivityName.equals("mySearchActivityCheckoutLogin")
            ) {
                val intent = Intent(this, MySearchActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
                HomeActivity.isLoggedin = false
            } else if (loginActivityName.equals("myCartActivityCheckoutLogin") || loginActivityName.equals(
                    "myCartActivityOrdersLogin"
                ) || loginActivityName.equals("myCartActivityProfileLogin")
            ) {
                val intent = Intent(this, MyCartActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
                HomeActivity.isLoggedin = false
            } else if (loginActivityName.equals("myOffersActivityProfileLogin") || loginActivityName.equals(
                    "myOffersActivityOrdersLogin"
                )
            ) {
                val intent = Intent(this, MyOffersActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
                HomeActivity.isLoggedin = false
            } else if (loginActivityName.equals("homeActivityCheckoutLogin")) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
                HomeActivity.isLoggedin = false
            } else if (loginActivityName.equals("INSERT_PRESCRIPTION_ACTIVITY_NEW")) {
                onBackPressed()
            }
        }


        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        utils = Utils()
        userLoginController = UserLoginController()
        userLoginController.getGlobalApiList(this, this)

        SessionManager.setBatchId("");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        edittext_mobileNum.setRawInputType(InputType.TYPE_CLASS_TEXT)
        edittext_mobileNum.setTextIsSelectable(true)

        setMobileNumberKeyboard()
        val faq_layout = findViewById<ImageView>(R.id.faq);
        val customerCareImg = findViewById<ImageView>(R.id.customer_care_icon)
        val customerHelpLayout = findViewById<LinearLayout>(R.id.customer_help_layout)
        constraint_layout = findViewById(R.id.constraint_layout)
        customerHelpLayout.visibility = View.VISIBLE
        customerCareImg.setOnClickListener { v ->
            if (customerHelpLayout.visibility == View.VISIBLE) {
                customerCareImg.setBackgroundResource(R.drawable.icon_help_circle)
                val animate = TranslateAnimation(0f, customerHelpLayout.width.toFloat(), 0f, 0f)
                animate.duration = 2000
                animate.fillAfter = true
                customerHelpLayout.startAnimation(animate)
                customerHelpLayout.visibility = View.GONE
            } else {
                customerCareImg.setBackgroundResource(R.drawable.icon_help_circle)
                val animate = TranslateAnimation(customerHelpLayout.width.toFloat(), 0f, 0f, 0f)
                animate.duration = 2000
                animate.fillAfter = true
                customerHelpLayout.startAnimation(animate)
                customerHelpLayout.visibility = View.VISIBLE
            }
        }

        val custCareImg = findViewById<ImageView>(R.id.cust_care_icon)
        val custHelpLayout = findViewById<LinearLayout>(R.id.cust_help_layout)
        custHelpLayout.visibility = View.VISIBLE
        custCareImg.setOnClickListener { v ->
            if (custHelpLayout.visibility == View.VISIBLE) {
                custCareImg.setBackgroundResource(R.drawable.icon_help_circle)
                val animate = TranslateAnimation(0f, custHelpLayout.width.toFloat(), 0f, 0f)
                animate.duration = 2000
                animate.fillAfter = true
                custHelpLayout.startAnimation(animate)
                custHelpLayout.visibility = View.GONE
            } else {
                custCareImg.setBackgroundResource(R.drawable.icon_help_circle)
                val animate = TranslateAnimation(custHelpLayout.width.toFloat(), 0f, 0f, 0f)
                animate.duration = 2000
                animate.fillAfter = true
                custHelpLayout.startAnimation(animate)
                custHelpLayout.visibility = View.VISIBLE
            }
        }

        otp_view.setRawInputType(InputType.TYPE_CLASS_TEXT)
        otp_view.setTextIsSelectable(true)

        //Temporary Code
//        SessionManager.setMobilenumber("9440012212")
//        startActivity(Intent(applicationContext, HomeActivity::class.java))
//        finish()
//        this.overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)

//        back_icon.setOnClickListener {
//            startActivity(Intent(this@UserLoginActivity, MainActivity::class.java))
//            finish()
//            overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out)
//        }

        faq_layout.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@UserLoginActivity, FAQActivity::class.java))
        })
        back_otp_icon.setOnClickListener {
            otp_parent_layout.visibility = View.GONE
            mobile_number_input_layout.visibility = View.VISIBLE
            setMobileNumberKeyboard()
        }
        generate_otp_layout.setOnClickListener(View.OnClickListener {
            if (getStoreId() != null && !getStoreId().isEmpty() && getTerminalId() != null && !getTerminalId().isEmpty() && getEposUrl() != null && !getEposUrl().isEmpty()) {
                val MobilePattern = "[0-9]{10}"
                mobileNum = edittext_mobileNum.text.toString()
                if (mobileNum.length < 10) {
                    send_otp_image.setImageResource(R.drawable.right_selection_green)
                    edittext_error_layout.setBackgroundResource(R.drawable.phone_error_alert_bg)
                    edittext_error_text.visibility = View.VISIBLE
                    return@OnClickListener
                } else {
                    send_otp_image.setImageResource(R.drawable.right_selection_green)
                    edittext_error_layout.setBackgroundResource(R.drawable.phone_country_code_bg)
                    edittext_error_text.visibility = View.INVISIBLE
                    if (oldMobileNum.equals(edittext_mobileNum.text.toString()) && edittext_mobileNum.text.toString().length > 0 && (mobileNum.matches(
                            MobilePattern.toRegex()
                        ))
                    ) {
                        mobile_number_input_layout.visibility = View.GONE
                        otp_parent_layout.visibility = View.VISIBLE
                    } else {
                        if (edittext_mobileNum.text.toString().length > 0) {
                            oldMobileNum = edittext_mobileNum.text.toString()
                            if (mobileNum.matches(MobilePattern.toRegex())) {
                                Utils.showDialog(
                                    this, resources.getString(R.string.label_sending_otp)
                                )
                                otp = (Math.random() * 9000).toInt() + 1000
                                if (NetworkUtils.isNetworkConnected(applicationContext)) {
                                    val sms_req = Send_Sms_Request()
                                    sms_req.mobileNo = mobileNum
                                    sms_req.message =
                                        "Dear Apollo Customer, Your one time password is " + otp.toString() + " and is valid for 3mins."
                                    sms_req.isOtp = true
                                    sms_req.otp = otp.toString()
                                    sms_req.apiType = "KIOSk"
                                    userLoginController.handleSendSmsApi(sms_req, this)
                                } else {
                                    Utils.showSnackbar(
                                        applicationContext,
                                        constraint_layout,
                                        resources.getString(R.string.label_internet_error_text)
                                    );
                                }
                            }
                        }
                    }
                }
            } else {
                val accesskeyDialog = AccesskeyDialog(this)
                accesskeyDialog.onClickSubmit { v1: View? ->
                    accesskeyDialog.listener()
                    if (accesskeyDialog.validate()) {
                        val intent = Intent(this, MposStoreSetupActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(
                            R.animator.trans_left_in, R.animator.trans_left_out
                        )
                        accesskeyDialog.dismiss()
                    }
                }
                accesskeyDialog.show()
//                Intent intent = new Intent(MainActivity.this, MposStoreSetupActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            }
        })

        verify_otp_layout.setOnClickListener(View.OnClickListener {
            if (!TextUtils.isEmpty(otp_view.text.toString()) && otp_view.text.toString().length > 0) {
                if (otp == otp_view.text.toString().toInt()) {


                    UserLoginController().getGlobalConfigurationApiCall(this, this)

//                    verify_otp_image.setImageResource(R.drawable.right_selection_green)
//                    SessionManager.setMobilenumber(mobileNum)
//                    startActivity(Intent(applicationContext, HomeActivity::class.java))
//                    finishAffinity()
//                    this.overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
                } else {
                    verify_otp_image.setImageResource(R.drawable.right_selection_green)
                    Utils.showSnackbar(
                        this@UserLoginActivity,
                        constraint_layout,
                        applicationContext.resources.getString(R.string.label_invalid_otp_try_again)
                    )
                }
            } else {
                Utils.showSnackbar(
                    this@UserLoginActivity,
                    constraint_layout,
                    applicationContext.resources.getString(R.string.label_invalid_otp_try_again)
                )
            }
        })

        resend_otp_layout.setOnClickListener(View.OnClickListener {
            if (NetworkUtils.isNetworkConnected(applicationContext)) {
                getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
                resend_button.setTextColor(resources.getColor(R.color.hash_color))

                val sms_req = Send_Sms_Request()
                sms_req.mobileNo = mobileNum
                sms_req.message = "Your OTP is " + otp.toString()
                sms_req.isOtp = true
                sms_req.otp = otp.toString()
                sms_req.apiType = "KIOSk"
                userLoginController.handleSendSmsApi(sms_req, this)
                countDownTimer = object : CountDownTimer(5000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                    }

                    override fun onFinish() {
                        resend_button.setTextColor(Color.parseColor("#000000"))
                    }
                }.start()
            } else {
                Utils.showSnackbar(
                    applicationContext,
                    constraint_layout,
                    resources.getString(R.string.label_internet_error_text)
                );
            }
        })

        change_number_layout.setOnClickListener(View.OnClickListener {
            otp_view.setText("")
            otp_parent_layout.visibility = View.GONE
            mobile_number_input_layout.visibility = View.VISIBLE
            edittext_mobileNum.text?.clear()
            setMobileNumberKeyboard();
        })

        ok_btn_layout.setOnClickListener {
            startActivity(Intent(applicationContext, HomeActivity::class.java))
            finish()
            this.overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
        }

        if (getStoreId() != null && !getStoreId().isEmpty() && getTerminalId() != null && !getTerminalId().isEmpty() && getEposUrl() != null && !getEposUrl().isEmpty()) {

        } else {
            val accesskeyDialog = AccesskeyDialog(this)
            accesskeyDialog.onClickSubmit { v1: View? ->
                accesskeyDialog.listener()
                if (accesskeyDialog.validate()) {
                    val intent = Intent(this, MposStoreSetupActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
                    accesskeyDialog.dismiss()
                }
            }
            accesskeyDialog.show()
//                Intent intent = new Intent(MainActivity.this, MposStoreSetupActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
        }


    }


    override fun onBackPressed() {
        if (loginActivityName.equals("INSERT_PRESCRIPTION_ACTIVITY_NEW")) {
            super.onBackPressed()
        }
//        super.onBackPressed()
//        startActivity(Intent(this@UserLoginActivity, MainActivity::class.java))
//        finish()
//        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out)
    }

    fun setMobileNumberKeyboard() {
        val ic = edittext_mobileNum.onCreateInputConnection(EditorInfo())
        val keyboard = CustomKeyboard(applicationContext)
        keyboard.setInputConnection(ic)
        if (keyboard != null) {
            keyboard_wrapper_layout.removeAllViews()
            keyboard_wrapper_layout.addView(keyboard)
        }
    }

    fun setOtpNumberKeyboard() {
        val ic = otp_view.onCreateInputConnection(EditorInfo())
        val keyboard = CustomKeyboard(applicationContext)
        keyboard.setInputConnection(ic)
        if (keyboard != null) {
            otp_keyboard_wrapper_layout.removeAllViews()
            otp_keyboard_wrapper_layout.addView(keyboard)
        }
    }

    override fun onSuccessGlobalApiResponse(list: Global_api_response) {
        val apiList = list.apis
        for (list1 in apiList) {
            val comparestring = list1.name
            val url = list1.url
            Utils.printMessage("USERLOGINACTIVITY", "Api list--> " + url + list1.name)
            if (comparestring.equals("Add_FCM_Token", ignoreCase = true)) {
                Constants.Add_FCM_Token = list1.url
            } else if (comparestring.equals("Send_Otp", ignoreCase = true)) {
                Constants.Send_Otp = list1.url
            } else if (comparestring.equals("Get_Past_Prescription", ignoreCase = true)) {
                Constants.Get_Past_Prescription = list1.url
            } else if (comparestring.equals("Delete_the_Prescription", ignoreCase = true)) {
                Constants.Delete_the_Prescription = list1.url
            } else if (comparestring.equals("Get_Portfolio_of_the_User", ignoreCase = true)) {
                Constants.Get_Portfolio_of_the_User = list1.url
            } else if (comparestring.equals("Get_Scanned_Prescription_Image", ignoreCase = true)) {
                Constants.Get_Scanned_Prescription_Image = list1.url
            } else if (comparestring.equals("Get_Prescription_Medicine_List", ignoreCase = true)) {
                Constants.Get_Prescription_Medicine_List = list1.url
            } else if (comparestring.equals("Get_Special_Offers_Products", ignoreCase = true)) {
                Constants.Get_Special_Offers_Products = list1.url
                Constants.Get_Product_List = list1.url
            } else if (comparestring.equals("Get_Trending_now_Products", ignoreCase = true)) {
                Constants.Get_Trending_now_Products = list1.url
            } else if (comparestring.equals(
                    "Get_The_price_for_Past_Prescription_Medicine_list", ignoreCase = true
                )
            ) {
                Constants.Get_The_price_for_Past_Prescription_Medicine_list = list1.url
            } else if (comparestring.equals("Search_Suggestions", ignoreCase = true)) {
                Constants.Search_Suggestions = list1.url
            } else if (comparestring.equals("Search_Product", ignoreCase = true)) {
                Constants.Search_Product = list1.url
            } else if (comparestring.equals("Get_Redeem_Points", ignoreCase = true)) {
                Constants.Get_Redeem_Points = list1.url
            } else if (comparestring.equals("Redeem_points_Send_Otp", ignoreCase = true)) {
                Constants.Redeem_points_Send_Otp = list1.url
            } else if (comparestring.equals("Redeem_Points_Resend_Otp", ignoreCase = true)) {
                Constants.Redeem_Points_Resend_Otp = list1.url
            } else if (comparestring.equals("Redeem_Points_Validate_Otp", ignoreCase = true)) {
                Constants.Redeem_Points_Validate_Otp = list1.url
            } else if (comparestring.equals(
                    "Redeem_points_Retry_Validate_Otp", ignoreCase = true
                )
            ) {
                Constants.Redeem_points_Retry_Validate_Otp = list1.url
            } else if (comparestring.equals("Redeem_Points_Check_Voucher", ignoreCase = true)) {
                Constants.Redeem_Points_Check_Voucher = list1.url
            } else if (comparestring.equals("Redeem_Voucher", ignoreCase = true)) {
                Constants.Redeem_Voucher = list1.url
            } else if (comparestring.equals("Get_Store_Locator", ignoreCase = true)) {
                Constants.Get_Store_Locator = list1.url
            } else if (comparestring.equals("Get_Bit_List_for_Jiyo", ignoreCase = true)) {
                Constants.Get_Bit_List_for_Jiyo = list1.url
            } else if (comparestring.equals("Get_Display_Video_For_Jiyo", ignoreCase = true)) {
                Constants.Get_Display_Video_For_Jiyo = list1.url
            } else if (comparestring.equals("Paytm_Payment_Transaction", ignoreCase = true)) {
                Constants.Paytm_Payment_Transaction = list1.url
            } else if (comparestring.equals("Pinelab_Upload_Transaction", ignoreCase = true)) {
                Constants.Pinelab_Upload_Transaction = list1.url
            } else if (comparestring.equals(
                    "Pinelab_Get_Cloud_Bases_Transaction", ignoreCase = true
                )
            ) {
                Constants.Pinelab_Get_Cloud_Bases_Transaction = list1.url
            } else if (comparestring.equals("Pinelab_Cancel_Transaction", ignoreCase = true)) {
                Constants.Pinelab_Cancel_Transaction = list1.url
            } else if (comparestring.equals("Get_User_Delivery_Address_List", ignoreCase = true)) {
                Constants.Get_User_Delivery_Address_List = list1.url
            } else if (comparestring.equals("Edit_User_Delivery_Address", ignoreCase = true)) {
                Constants.Edit_User_Delivery_Address = list1.url
            } else if (comparestring.equals("Add_User_Delivery_Address", ignoreCase = true)) {
                Constants.Add_User_Delivery_Address = list1.url
            } else if (comparestring.equals("Delete_User_Delivery_address", ignoreCase = true)) {
                Constants.Delete_User_Delivery_address = list1.url
            } else if (comparestring.equals("Get_State_city_List", ignoreCase = true)) {
                Constants.Get_State_city_List = list1.url
            } else if (comparestring.equals("Get_Order_History_For_User", ignoreCase = true)) {
                Constants.Get_Order_History_For_User = list1.url
            } else if (comparestring.equals("Order_Pushing_Api", ignoreCase = true)) {
                Constants.Order_Pushing_Api = list1.url
            } else if (comparestring.equals("Get_Image_link", ignoreCase = true)) {
                Constants.Get_Image_link = list1.url
            } else if (comparestring.equals("GET_CATEGORY_LIST", ignoreCase = true)) {
                Constants.Categorylist_Url = list1.url
            }
        }
        handleFcmTokenFunctionality()
    }

    private fun handleFcmTokenFunctionality() {
        if (NetworkUtils.isNetworkConnected(this)) {
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task: Task<InstanceIdResult> ->
                    if (task.isSuccessful) {
                        if (!isFcmAdded()) {
                            userLoginController.handleFCMTokenRegistration(task.result.token, this)
                        }
                    }
                }
        } else {
            Utils.showSnackbar(
                this,
                constraint_layout,
                applicationContext.resources.getString(R.string.label_internet_error_text)
            )
        }
    }

    override fun onSuccessGlobalConfigurationApiCall(getGlobalConfigurationResponse: GetGlobalConfigurationResponse?) {
        verify_otp_image.setImageResource(R.drawable.right_selection_green)
//        mobileNum="9849700117"
        SessionManager.setMobilenumber(mobileNum)
        handleCategoryListService()
        //        entered_mobile_number.setText(mobileNum)

//        startActivity(Intent(applicationContext, HomeActivity::class.java))
//        finishAffinity()
//        loginActivityName= intent.getStringExtra("mySearchActivityLogin")
        if (loginActivityName.equals("mySearchActivityProfileLogin") || loginActivityName.equals("myCartActivityProfileLogin") || loginActivityName.equals(
                "myOffersActivityProfileLogin"
            )
        ) {
            val intent = Intent(this, MyProfileActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
            HomeActivity.isLoggedin = true
        } else if (loginActivityName.equals("mySearchActivityOrdersLogin") || loginActivityName.equals(
                "myCartActivityOrdersLogin"
            ) || loginActivityName.equals("myOffersActivityOrdersLogin")
        ) {
            val intent = Intent(this, MyOrdersActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
            HomeActivity.isLoggedin = true
        } else if (loginActivityName.equals("myCartActivityCheckoutLogin") || loginActivityName.equals(
                "homeActivityCheckoutLogin"
            )
        ) {
            val intent = Intent(this, CheckoutActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
            HomeActivity.isLoggedin = true
        } else if (loginActivityName.equals("mySearchActivityCheckoutLogin")) {
            val intent = Intent(this, MyCartActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
            HomeActivity.isLoggedin = true
        } else if (loginActivityName.equals("INSERT_PRESCRIPTION_ACTIVITY_NEW")) {
            val intent = Intent()
            intent.putExtra("IS_OTP_VERIFIED", true)
            setResult(Activity.RESULT_OK, intent)
            HomeActivity.isLoggedin = true
            finish()


        }


    }

    private fun handleCategoryListService() {
        Utils.showDialog(applicationContext, applicationContext.resources.getString(R.string.label_please_wait))
        val apiInterface = ApiClient.getApiService(Constants.Get_Portfolio_of_the_User)
        val call = apiInterface.getPortFolio(mobileNum, "true", "Apollo pharmacy")


        call.enqueue(object: CallbackWithRetry<PortFolioModel?>(call) {
            override fun onResponse(call: Call<PortFolioModel?>, response: Response<PortFolioModel?>) {
                if (response.isSuccessful) {
                    Utils.dismissDialog()
                    assert(response.body() != null)
                    updateUI(response.body())
                } else {
                    Utils.dismissDialog()
                }
            }

            override fun onFailure(call: Call<PortFolioModel?>, t: Throwable) {
                Utils.dismissDialog()
                Utils.showCustomAlertDialog(applicationContext, t.message, false, applicationContext.resources.getString(R.string.label_ok), "")
            }
        })
    }



    private fun updateUI(portFolioModel: PortFolioModel?) {
        val customerData = portFolioModel!!.customerData
        if (customerData != null) {
           SessionManager.setCustName(customerData.name)
        } else {
            SessionManager.setCustName("")
        }
    }

}