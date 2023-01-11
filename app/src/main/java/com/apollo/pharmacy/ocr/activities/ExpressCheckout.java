package com.apollo.pharmacy.ocr.activities;

import android.app.Application;
import android.content.Context;

public class ExpressCheckout extends Application {
       public static Context context;



    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
    }
}
