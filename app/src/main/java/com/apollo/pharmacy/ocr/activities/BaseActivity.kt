package com.apollo.pharmacy.ocr.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.apollo.pharmacy.ocr.R
import com.apollo.pharmacy.ocr.model.OCRToDigitalMedicineResponse
import com.apollo.pharmacy.ocr.utility.LanguageManager.Companion.setLocale
import com.apollo.pharmacy.ocr.utility.SessionManager.getSessionTime
import com.apollo.pharmacy.ocr.utility.SessionManager.setDataList
import java.text.DecimalFormat
import java.util.*

open class BaseActivity() : AppCompatActivity() {

    val IDLE_DELAY_MINUTES = getSessionTime() // 15 min
    var isPaymentActivity: Boolean = false;

    // 15 min
    var sessionTimeOutAlert: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_base)
//       if(!HomeActivity.isPaymentSelectionActivity){
        delayedIdle(IDLE_DELAY_MINUTES)
//        }

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(setLocale(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setLocale(this)
    }


    var sessionTimeOutHandler: Handler = Handler()

    var sessionTimeTimeRunnable = Runnable {
        //handle your IDLE state
        isPaymentActivity = HomeActivity.isPaymentSelectionActivity
        if (!isPaymentActivity) {
            logoutConfirmationCallback()
            sessionTimeOutAlert = Dialog(this)
            sessionTimeOutAlert!!.setContentView(R.layout.dialog_alert_for_idle)
            if (sessionTimeOutAlert!!.window != null) sessionTimeOutAlert!!.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            sessionTimeOutAlert!!.setCancelable(false)
            val dialogTitleText = sessionTimeOutAlert!!.findViewById<TextView>(R.id.dialog_info)
            val okButton = sessionTimeOutAlert!!.findViewById<Button>(R.id.dialog_ok)
            val declineButton = sessionTimeOutAlert!!.findViewById<Button>(R.id.dialog_cancel)
            val sessionTimeExpiry = sessionTimeOutAlert!!.findViewById<TextView>(R.id.session_time_expiry_countdown)
            downTimer(sessionTimeExpiry)
            dialogTitleText.text = "Do you want to continue?"
            okButton.text = "Yes, Continue"
            declineButton.text = "Logout"
            okButton.setOnClickListener {
                if (sessionTimeOutAlert!!.isShowing) {
                    sessionTimeOutAlert!!.dismiss()
                }
                delayedIdle(getSessionTime())
            }

            declineButton.setOnClickListener {
                if (sessionTimeOutAlert!!.isShowing) {
                    sessionTimeOutAlert!!.dismiss()
                }

                logoutConfirmationHandler.removeCallbacks(logoutConfirmationRunnable)

                val dataList: List<OCRToDigitalMedicineResponse> = ArrayList()
                setDataList(dataList)

                HomeActivity.isLoggedin = false
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
                finishAffinity()
            }
            if (sessionTimeOutAlert != null) {
                sessionTimeOutAlert!!.show()
            }
        }
        // Logout from app

    }


    var logoutConfirmationHandler = Handler()
    var logoutConfirmationRunnable = Runnable {
        if (sessionTimeOutAlert != null && sessionTimeOutAlert!!.isShowing()) {
            sessionTimeOutAlert!!.dismiss()
        }
        //logoutUser()

        val dataList: List<OCRToDigitalMedicineResponse> = ArrayList()
        setDataList(dataList)
        HomeActivity.isLoggedin = false
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out)
        finishAffinity()
    }


    open fun logoutConfirmationCallback() {
        logoutConfirmationHandler.removeCallbacks(logoutConfirmationRunnable)
        logoutConfirmationHandler.postDelayed(logoutConfirmationRunnable, 30000)
    }

    open fun removeAllExpiryCallbacks() {
        logoutConfirmationHandler.removeCallbacks(logoutConfirmationRunnable)
        sessionTimeOutHandler.removeCallbacks(sessionTimeTimeRunnable)
    }

    open fun delayedIdle(delayMinutes: Int) {
        logoutConfirmationHandler.removeCallbacks(logoutConfirmationRunnable)
        sessionTimeOutHandler.removeCallbacks(sessionTimeTimeRunnable)
        sessionTimeOutHandler.postDelayed(sessionTimeTimeRunnable, ((delayMinutes * 1000 * 60) - 30000).toLong())
    }

    override fun onPause() {
        logoutConfirmationHandler.removeCallbacks(logoutConfirmationRunnable)
        sessionTimeOutHandler.removeCallbacks(sessionTimeTimeRunnable)
        super.onPause()
    }

    override fun onResume() {
        delayedIdle(IDLE_DELAY_MINUTES)
        super.onResume()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        delayedIdle(IDLE_DELAY_MINUTES)
    }

    private fun downTimer(sessionTimeOutCountDown: TextView) {
        object : CountDownTimer(30 * 1000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val second = millisUntilFinished / 1000 % 60
                val minutes = millisUntilFinished / (1000 * 60) % 60
                sessionTimeOutCountDown.text = "Your session will expire in ${DecimalFormat("00").format(minutes)} : ${DecimalFormat("00").format(second)}"
            }

            override fun onFinish() {
                sessionTimeOutCountDown.text = "00:00"
            }
        }.start()
    }
}
