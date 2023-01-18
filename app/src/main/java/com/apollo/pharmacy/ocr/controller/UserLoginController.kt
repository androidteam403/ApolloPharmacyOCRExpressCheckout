package com.apollo.pharmacy.ocr.controller

import android.content.Context
import com.apollo.pharmacy.ocr.activities.userlogin.model.GetGlobalConfigurationResponse
import com.apollo.pharmacy.ocr.interfaces.UserLoginListener
import com.apollo.pharmacy.ocr.model.*
import com.apollo.pharmacy.ocr.network.ApiClient
import com.apollo.pharmacy.ocr.network.CallbackWithRetry
import com.apollo.pharmacy.ocr.utility.ApplicationConstant
import com.apollo.pharmacy.ocr.utility.Constants
import com.apollo.pharmacy.ocr.utility.SessionManager
import com.apollo.pharmacy.ocr.utility.SessionManager.addFcmLog
import com.apollo.pharmacy.ocr.utility.SessionManager.getDataAreaId
import com.apollo.pharmacy.ocr.utility.SessionManager.getKioskSetupResponse
import com.apollo.pharmacy.ocr.utility.SessionManager.getStoreId
import com.apollo.pharmacy.ocr.utility.SessionManager.getTerminalId
import com.apollo.pharmacy.ocr.utility.Utils
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserLoginController {

    fun handleSendSmsApi(request: Send_Sms_Request, userLoginListener: UserLoginListener) {
        var service_call = ApiClient.getApiService(Constants.Send_Sms_Api)
        val call = service_call.send_sms_api(request)
        call.enqueue(object : CallbackWithRetry<Send_Sms_Response>(call) {
            override fun onFailure(call: Call<Send_Sms_Response>, t: Throwable) {
                userLoginListener.onSendSmsFailure()
            }

            override fun onResponse(call: Call<Send_Sms_Response>, response: Response<Send_Sms_Response>) {
                if (response.isSuccessful) {
                    userLoginListener.onSendSmsSuccess()
                } else {
                    userLoginListener.onSendSmsFailure()
                }
            }
        })
    }


    fun handleFCMTokenRegistration(token: String?, userLoginListener: UserLoginListener) {
//        val request: AddFCMTokenRequest = AddFCMTokenRequest(token!!, getKioskSetupResponse().kiosK_ID)
//
//        val apiInterface = ApiClient.getApiService(Constants.Add_FCM_Token)
//        val call = apiInterface.addFcmToken(request)
//        call.enqueue(object : CallbackWithRetry<Meta>(call) {
//            override fun onFailure(call: Call<Meta>, t: Throwable) {
//                userLoginListener.onSendSmsFailure()
//            }
//
//            override fun onResponse(call: Call<Meta>, response: Response<Meta>) {
//                if (response.isSuccessful) {
//                    userLoginListener.onSendSmsSuccess()
//                } else {
//                    userLoginListener.onSendSmsFailure()
//                }
//            }
//        })

        var kioskId = ""
        if(getKioskSetupResponse()!=null && getKioskSetupResponse().kiosK_ID!=null){
            kioskId = getKioskSetupResponse().kiosK_ID
        }
        val request = AddFCMTokenRequest(token!!, kioskId)
        val apiInterface = ApiClient.getApiService(Constants.Add_FCM_Token)
        val call = apiInterface.addFcmToken(request).also {
            it.enqueue(object : CallbackWithRetry<Meta?>(it) {
                override fun onResponse(call: Call<Meta?>, response: Response<Meta?>) {
                    val m = response.body() as Meta?
                    //                assert m != null;
                    if (m != null && m.statusCode == 200) {
                        addFcmLog()
                    }
                }

                override fun onFailure(call: Call<Meta?>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }

    fun getGlobalApiList(context: Context?, userLoginListener: UserLoginListener) {
        val request = Global_api_request()
        request.deviceid = Utils.getDeviceId(context)
        request.key = "2028"
        val apiInterface = ApiClient.getApiService(ApplicationConstant.Global_api_url)
        val call = apiInterface.get_global_apis(request)
        call.enqueue(object : CallbackWithRetry<Global_api_response?>(call) {
            override fun onResponse(call: Call<Global_api_response?>, response: Response<Global_api_response?>) {
                if (response.isSuccessful) {
                    userLoginListener.onSuccessGlobalApiResponse(response.body() as Global_api_response?)
                }
            }

            override fun onFailure(call: Call<Global_api_response?>, t: Throwable) {
                userLoginListener.onFailure(t.message)
            }
        })
    }

    fun getGlobalConfigurationApiCall(mContext: Context?, userLoginListener: UserLoginListener) {
        Utils.showDialog(mContext, "Loading…")
        val api = ApiClient.getApiService(SessionManager.getEposUrl())
        val call = api.GET_GLOBAL_CONFING_API_CALL(getStoreId(), getTerminalId(), getDataAreaId(), Object())
        call.enqueue(object : Callback<GetGlobalConfigurationResponse?> {
            override fun onResponse(call: Call<GetGlobalConfigurationResponse?>, response: Response<GetGlobalConfigurationResponse?>) {
                Utils.dismissDialog()
                if (response.body() != null && response.body()!!.requestStatus == 0) {
                    SessionManager.setDataAreaId(response.body()!!.dataAreaID)
                    val gson = Gson()
                    val json = gson.toJson(response.body())

                    SessionManager.setGlobalConfigurationResponse(json)
                    userLoginListener.onSuccessGlobalConfigurationApiCall(response.body())
                } else if (response.body() != null) {
                    userLoginListener.onFailure(response.body()!!.returnMessage)
                }
            }

            override fun onFailure(call: Call<GetGlobalConfigurationResponse?>, t: Throwable) {
                Utils.dismissDialog()
                userLoginListener.onFailure(t.message)
            }
        })
    }
}