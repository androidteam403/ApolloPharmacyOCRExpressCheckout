package com.apollo.pharmacy.ocr.utility;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.model.StateCodes;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class Utils {
    static SpotsDialog spotsDialog;
    static SpotsDialog spotsDialogRedeem;
    private static Dialog imageDialog;

    public static void showDialog(Context mContext, String strMessage) {
        try {
            if (spotsDialog != null) {
                if (spotsDialog.isShowing()) {
                    spotsDialog.dismiss();
                }
            }
            spotsDialog = new SpotsDialog(mContext, strMessage, R.style.Custom, false, dialog -> {
            });
            Objects.requireNonNull(spotsDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            spotsDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showDialogRedeem(Context mContext, String strMessage) {
        try {
            if (spotsDialogRedeem != null) {
                if (spotsDialogRedeem.isShowing()) {
                    spotsDialogRedeem.dismiss();
                }
            }
            spotsDialogRedeem = new SpotsDialog(mContext, strMessage, R.style.Custom, false, dialog -> {
            });
            Objects.requireNonNull(spotsDialogRedeem.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            spotsDialogRedeem.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissDialogRedeem() {
        try {
            if (spotsDialogRedeem.isShowing())
                spotsDialogRedeem.dismiss();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static void dismissDialog() {
        try {
            if (spotsDialog.isShowing())
                spotsDialog.dismiss();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void imageViewer(Context context, String imgUrl) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.full_image_viewer, null);
        ViewGroup root = layout.findViewById(R.id.main_activity_view);
        imageDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        imageDialog.setContentView(layout);
        ImageView closeBtn = imageDialog.findViewById(R.id.close_prescription);
        ImageView imageview = imageDialog.findViewById(R.id.imageview);
        Glide.with(context)
                .load(imgUrl)
                .apply(new RequestOptions().placeholder(R.drawable.thumbnail_image).error(R.drawable.thumbnail_image))
                .into(imageview);
        closeBtn.setOnClickListener(v -> imageDialog.dismiss());
        imageDialog.show();
    }

    public static Dialog getDialog(Context mContext, int mLayout) {
        Dialog mDialog = new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(mLayout);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().setGravity(Gravity.CENTER);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        return mDialog;
    }

    public static Dialog getOrderSuccessDialog(Context mContext, int mLayout) {
        Dialog mDialog = new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(mLayout);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().setGravity(Gravity.CENTER);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        return mDialog;
    }

    public static void showCustomAlertDialog(Context mContext, String strMessage, boolean isNoBtnRequired, String yesBtnText, String noBtnText) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_custom_alert);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
        TextView dialogTitleText = dialog.findViewById(R.id.dialog_info);
        Button okButton = dialog.findViewById(R.id.dialog_ok);
        Button declineButton = dialog.findViewById(R.id.dialog_cancel);
        dialogTitleText.setText(strMessage);
        okButton.setText(yesBtnText);
        declineButton.setText(noBtnText);
        okButton.setOnClickListener(v -> dialog.dismiss());
        if (isNoBtnRequired) {
            declineButton.setVisibility(View.VISIBLE);
            declineButton.setOnClickListener(v -> dialog.dismiss());
        } else {
            declineButton.setVisibility(View.GONE);
        }
    }

    public static void showSnackbar(Context context, ConstraintLayout layout, String name) {
        Snackbar mSnackbar = Snackbar.make(layout, name, Snackbar.LENGTH_LONG);
        View snackbarView = mSnackbar.getView();
        snackbarView.setBackgroundColor(context.getResources().getColor(R.color.material_amber_accent_700));
        (mSnackbar.getView()).getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        (mSnackbar.getView()).getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(25);
//        snackbarView.setMinimumHeight(10);
//        snackbarView.setMinimumHeight(40);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        mSnackbar.show();
    }

    public static void showSnackbarDialog(Context context, View layout, String name) {
        Snackbar mSnackbar = Snackbar.make(layout, name, Snackbar.LENGTH_LONG);
        View snackbarView = mSnackbar.getView();
//        FrameLayout.LayoutParams params=(FrameLayout.LayoutParams)snackbarView.getLayoutParams();
//        params.gravity=Gravity.CENTER;
        snackbarView.setBackgroundColor(context.getResources().getColor(R.color.material_amber_accent_700));
        (mSnackbar.getView()).getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
//        (mSnackbar.getView()).getLayoutParams().gravity=Gravity.BOTTOM;
        textView.setTextSize(25);
        snackbarView.setMinimumHeight(60);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            textView.setGravity(Gravity.CENTER_HORIZONTAL);

        }
        mSnackbar.show();
    }

    public static void showSnackbarMessage(Context context, View layout, String name) {
        Snackbar mSnackbar = Snackbar.make(layout, name, Snackbar.LENGTH_LONG);
        View snackbarView = mSnackbar.getView();
        snackbarView.setBackgroundColor(context.getResources().getColor(R.color.material_amber_accent_700));
        (mSnackbar.getView()).getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(25);
        snackbarView.setMinimumHeight(60);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        mSnackbar.show();
    }

    public static void showSnackbarLinear(Context context, LinearLayout layout, String name) {
        Snackbar mSnackbar = Snackbar.make(layout, name, Snackbar.LENGTH_LONG);
        View snackbarView = mSnackbar.getView();
        snackbarView.setBackgroundColor(context.getResources().getColor(R.color.material_amber_accent_700));
        (mSnackbar.getView()).getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        snackbarView.setMinimumHeight(150);
        textView.setTextSize(25);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        mSnackbar.show();
    }

    public static void showCustomSnackbar(ConstraintLayout layout, String message, int color) {
        Snackbar mSnackbar = Snackbar.make(layout, message, Snackbar.LENGTH_LONG);
        View snackbarView = mSnackbar.getView();
        snackbarView.setBackgroundColor(color);
        (mSnackbar.getView()).getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(25);
        snackbarView.setMinimumHeight(60);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        mSnackbar.show();
    }

    public static String getDateFormat(String inputDate) {
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date newDate = null;
        try {
            newDate = spf.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
        return spf.format(newDate);
    }

    public static String getDateFormatSelfOrderHistory(String inputDate) {//7/7/2022 2:20:49 PM
        SimpleDateFormat spf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aaa", Locale.ENGLISH);
        Date newDate = null;
        try {
            newDate = spf.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
        return spf.format(newDate);
    }

    public static String getTimeFormat(String inputDate) {
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date newDate = null;
        try {
            newDate = spf.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        return spf.format(newDate);
    }

    public static String readJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static String removeAllDigit(String str) {
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                result = result + str.charAt(i);
            }
        }
        return result;
    }

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getOrderedID() {
        String dynamicOrderId = "";
        if (SessionManager.INSTANCE.getDynamicOrderId().isEmpty()) {
            String orderedDate = "";
            SimpleDateFormat sdf_neworder = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.ENGLISH);
            String orderdate_new1 = sdf_neworder.format(new Date());
            orderedDate = orderdate_new1.replace("-", "");
            dynamicOrderId = SessionManager.INSTANCE.getKioskSetupResponse().getKIOSK_ID() + "_" + orderedDate;
        } else {
            dynamicOrderId = SessionManager.INSTANCE.getDynamicOrderId();
        }
        return dynamicOrderId;
    }

    public static String getTransactionGeneratedId() {
        DateFormat df = new SimpleDateFormat("yyMMddHHmmss", Locale.ENGLISH);
        Date dateObj = new Date();
        return df.format(dateObj);
    }

    public static ArrayList<StateCodes> getStoreListFromAssets(Context context) {
        ArrayList<StateCodes> statecodelist = new ArrayList<>();
        try {
            String rawData = Utils.readJSONFromAsset(context, "State_codes.json");
            statecodelist = new Gson().fromJson(rawData, new TypeToken<List<StateCodes>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statecodelist;
    }

    public static int convertDpToPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public static void strikeThroughText(TextView price) {
        price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static String getNameFromFilePath(String path) {
        if (path.contains(File.separator)) {
            return path.substring(path.lastIndexOf(File.separator) + 1);
        }
        return path;
    }

    public static void printMessage(String tag, String message) {
        if (Constants.IS_LOG_ENABLED) {
            Log.e(tag, message);
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static String getCurrentDate(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
        return sdf.format(new Date());
    }
}