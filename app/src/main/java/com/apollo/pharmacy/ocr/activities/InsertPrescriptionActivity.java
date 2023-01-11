package com.apollo.pharmacy.ocr.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.receiver.MedTrailConnectivityReceiver;
import com.apollo.pharmacy.ocr.utility.Constants;
import com.bumptech.glide.Glide;

public class InsertPrescriptionActivity extends BaseActivity implements MedTrailConnectivityReceiver.MedTrailConnectivityListener {

    public InsertPrescriptionActivity() {
        super();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InsertPrescriptionActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        setContentView(R.layout.activity_insert_prescription);

        LinearLayout faqLayout = findViewById(R.id.help_layout);
        TextView helpText = findViewById(R.id.help_text);
        helpText.setText(getResources().getString(R.string.faq));
        faqLayout.setOnClickListener(view -> startActivity(new Intent(InsertPrescriptionActivity.this, FAQActivity.class)));

        ImageView customerCareImg = findViewById(R.id.customer_care_icon);
        LinearLayout customerHelpLayout = findViewById(R.id.customer_help_layout);
        customerHelpLayout.setVisibility(View.VISIBLE);
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

        ImageView gifImage = findViewById(R.id.gif_scanned_image);
        Glide.with(this).asGif().load(R.drawable.raw_scan_new).into(gifImage);
        Constants.getInstance().setConnectivityListener1(this);

        TextView counterText = findViewById(R.id.counter_text);
        CountDownTimer countDownTimer = new CountDownTimer(6000, 1000) {
            public void onTick(long millisUntilFinished) {
                counterText.setText("Seconds Remaining: 0" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                counterText.setText("Seconds Remaining: 00");
                finish();
                Intent intent = new Intent(InsertPrescriptionActivity.this, PrescriptionPreviewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            }
        }.start();

        registerReceiver(broadcastReceiver, new IntentFilter("Scannedstatus"));
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String scanResult = intent.getStringExtra("scanResult");
            if (Integer.parseInt(scanResult) == 6) {
                finish();
                Intent intent2 = new Intent(context, PrescriptionPreviewActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.animator.trans_right_in, R.animator.trans_right_out);
    }
}
