package com.apollo.pharmacy.ocr.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.databinding.DataBindingUtil;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.checkout.CheckoutListener;
import com.apollo.pharmacy.ocr.controller.PincodeValidateController;
import com.apollo.pharmacy.ocr.databinding.ActivityMapViewBinding;
import com.apollo.pharmacy.ocr.interfaces.PhonePayQrCodeListener;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

public class DialogForMap implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {
    private Dialog dialog;
    double lating;
    double langing;
    Context context;
    private CheckoutListener checkoutListeners;
    private PhonePayQrCodeListener phonePayQrCodeListeners;
    private ActivityMapViewBinding activityMapViewBinding;


    public DialogForMap(Context context, CheckoutListener checkoutListener, PhonePayQrCodeListener phonePayQrCodeListener) {
        this.checkoutListeners=checkoutListener;
        this.phonePayQrCodeListeners=phonePayQrCodeListener;

        this.context = context;
        dialog = new Dialog(context);

        dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        activityMapViewBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.activity_map_view, null, false);
        dialog.setContentView(activityMapViewBinding.getRoot());

        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);


    }



    public void setMapCloseIconListener(View.OnClickListener okListener){
        activityMapViewBinding.mapDeleteIcon.setOnClickListener(okListener);
    }
    public void resetLocationOnMap(View.OnClickListener okListener){
        activityMapViewBinding.cancel.setOnClickListener(okListener);
    }
    public void selectAndContinue(View.OnClickListener okListener){
        activityMapViewBinding.save.setOnClickListener(okListener);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        activityMapViewBinding.lattitude.setText("" + marker.getPosition().latitude);
        activityMapViewBinding.longitude.setText("" + marker.getPosition().longitude);
    }


    public void selectandContinueFromMap() {
        lating = Double.parseDouble(activityMapViewBinding.lattitude.getText().toString());
        langing = Double.parseDouble(activityMapViewBinding.longitude.getText().toString());
    }

    public void setTextForLatLong(String mapUserLats, String mapUserLangs) {
        activityMapViewBinding.lattitude.setText(mapUserLats);
        activityMapViewBinding.longitude.setText(mapUserLangs);
    }

    public void setTextForLongLangDouble(double latitude, double longitude){
        activityMapViewBinding.lattitude.setText("" + latitude);
        activityMapViewBinding.longitude.setText("" + longitude);

    }


    public double getlating() {
        return lating;
    }

    public double getlanging() {
        return langing;
    }


}
