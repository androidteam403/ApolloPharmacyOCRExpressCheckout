package com.apollo.pharmacy.ocr.activities;

import static android.graphics.Color.GRAY;
import static com.apollo.pharmacy.ocr.utility.Constants.getContext;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.Utils.EnglishNumberToWords;
import com.apollo.pharmacy.ocr.controller.OrderInProgressController;
import com.apollo.pharmacy.ocr.custompdf.PDFCreatorActivity;
import com.apollo.pharmacy.ocr.custompdf.views.PDFBody;
import com.apollo.pharmacy.ocr.custompdf.views.PDFFooterView;
import com.apollo.pharmacy.ocr.custompdf.views.PDFHeaderView;
import com.apollo.pharmacy.ocr.custompdf.views.PDFTableView;
import com.apollo.pharmacy.ocr.custompdf.views.basic.PDFHorizontalView;
import com.apollo.pharmacy.ocr.custompdf.views.basic.PDFImageView;
import com.apollo.pharmacy.ocr.custompdf.views.basic.PDFLineSeparatorView;
import com.apollo.pharmacy.ocr.custompdf.views.basic.PDFTextView;
import com.apollo.pharmacy.ocr.custompdf.views.basic.PDFVerticalView;
import com.apollo.pharmacy.ocr.databinding.ActivityOrderinProgressBinding;
import com.apollo.pharmacy.ocr.interfaces.OrderinProgressListener;
import com.apollo.pharmacy.ocr.model.OCRToDigitalMedicineResponse;
import com.apollo.pharmacy.ocr.model.PdfModelResponse;
import com.apollo.pharmacy.ocr.utility.SessionManager;
import com.google.android.gms.common.util.IOUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderinProgressActivity extends PDFCreatorActivity implements OrderinProgressListener {

    private ActivityOrderinProgressBinding orderinProgressBinding;
    private List<OCRToDigitalMedicineResponse> dataList;
    private String fmcgOrderId;
    private String pharmaOrderId;
    private boolean onlineAmountPaid;
    private boolean isPharmadeliveryType;
    private boolean isFmcgDeliveryType;
    private OrderInProgressController orderInProgressController;
    private Context primaryBaseActivity;
    private String expressCheckoutTransactionId;
    private Boolean isFmcgQrCodePayment;
    private boolean isBillPrintReady = false;
    private boolean totalRedeemPointsUsed = false;
    private String redeemPointsAfterValidateOtp = "";
    private boolean isFirstPage = false;
    private int pageBreakCount = 0;
    private boolean duplicateCheckboxChecked = false;
    public static final String cambria = "src/main/res/font/cambriab.ttf";
    private final static int ITEXT_FONT_SIZE_EIGHT = 8;
    private final static int ITEXT_FONT_SIZE_TEN = 10;
    private final static int ITEXT_FONT_SIZE_SIX = 6;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderinProgressBinding = DataBindingUtil.setContentView(this, R.layout.activity_orderin_progress);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        orderinProgressBinding.setCallback(this);
//        expressCheckoutTransactionId = "400006876";
//        transactionId = expressCheckoutTransactionId;
//        orderInProgressController = new OrderInProgressController(this, this);
//        orderInProgressController.downloadPdf(expressCheckoutTransactionId);
        setUp();
    }

    @Override
    protected void attachBaseContext(Context base) {
        primaryBaseActivity = base;
        super.attachBaseContext(base);
    }

    private void setUp() {
        if (getIntent() != null) {
            fmcgOrderId = (String) getIntent().getStringExtra("FmcgOrderPlacedData");
            pharmaOrderId = (String) getIntent().getStringExtra("PharmaOrderPlacedData");
            onlineAmountPaid = (boolean) getIntent().getBooleanExtra("OnlineAmountPaid", false);
            isPharmadeliveryType = (boolean) getIntent().getBooleanExtra("pharma_delivery_type", false);
            isFmcgDeliveryType = (boolean) getIntent().getBooleanExtra("fmcg_delivery_type", false);
            isFmcgQrCodePayment = (Boolean) getIntent().getBooleanExtra("IS_FMCG_QR_CODE_PAYMENT", false);
            redeemPointsAfterValidateOtp = (String) getIntent().getStringExtra("redeemPointsAfterValidateOtp");
            totalRedeemPointsUsed = (Boolean) getIntent().getBooleanExtra("totalRedeemPointsUsed", false);
            expressCheckoutTransactionId = (String) getIntent().getStringExtra("EXPRESS_CHECKOUT_TRANSACTION_ID");
            orderinProgressBinding.setIsFmcgQrCodePayment(isFmcgQrCodePayment);
        }

        orderinProgressBinding.printDuplicateCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    duplicateCheckboxChecked = true;
                } else {
                    duplicateCheckboxChecked = false;
                }
            }
        });
        orderinProgressBinding.fmcgRequestId.setText(fmcgOrderId);
        orderinProgressBinding.pharmaRequestId.setText(pharmaOrderId);
        if (onlineAmountPaid) {
            orderinProgressBinding.payTxt.setText("You Paid");
        } else {
            orderinProgressBinding.payTxt.setText("You Pay");
        }
        OrderinProgresssuiModel orderinProgresssuiModel = new OrderinProgresssuiModel();
        if (null != SessionManager.INSTANCE.getDataList())
            this.dataList = SessionManager.INSTANCE.getDataList();
        if (dataList != null && dataList.size() > 0) {

            List<OCRToDigitalMedicineResponse> countUniques = new ArrayList<>();
            countUniques.addAll(dataList);

            for (int i = 0; i < countUniques.size(); i++) {
                for (int j = 0; j < countUniques.size(); j++) {
                    if (i != j && countUniques.get(i).getArtName().equals(countUniques.get(j).getArtName())) {
                        countUniques.remove(j);
                        j--;
                    }
                }
            }

            int pharmaMedicineCount = 0;
            int fmcgMedicineCount = 0;
            double pharmaTotal = 0.0;
            double fmcgTotal = 0.0;
            boolean isFmcg = false;
            boolean isPharma = false;
            for (OCRToDigitalMedicineResponse data : dataList) {
                if (data.getMedicineType() != null) {
                    if (data.getMedicineType().equals("PHARMA")) {
                        isPharma = true;
//                        pharmaMedicineCount++;
                        pharmaTotal = pharmaTotal + (Double.parseDouble(data.getArtprice()) * data.getQty());
                    } else {
                        isFmcg = true;
//                        fmcgMedicineCount++;
                        fmcgTotal = fmcgTotal + (Double.parseDouble(data.getArtprice()) * data.getQty());
                        if (redeemPointsAfterValidateOtp != null && !redeemPointsAfterValidateOtp.isEmpty() && !totalRedeemPointsUsed) {
                            fmcgTotal = fmcgTotal - (Double.parseDouble(redeemPointsAfterValidateOtp));
                        }

                    }
                }
            }

            for (int i = 0; i < dataList.size(); i++) {
                for (int j = 0; j < countUniques.size(); j++) {
                    if (dataList.get(i).getArtName().equalsIgnoreCase(countUniques.get(j).getArtName())) {
                        if (countUniques.get(j).getMedicineType().equals("FMCG")) {
                            fmcgMedicineCount++;
                            countUniques.remove(j);
                            j--;
                        } else {
                            pharmaMedicineCount++;
                            countUniques.remove(j);
                            j--;
                        }
                    }
                }
            }

//            fmcgToatalPass = fmcgTotal;
            orderinProgresssuiModel.setPharmaCount(String.valueOf(pharmaMedicineCount));
            orderinProgresssuiModel.setFmcgCount(String.valueOf(fmcgMedicineCount));
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            String pharmaformatted = formatter.format(pharmaTotal);
            String fmcgFormatted = formatter.format(fmcgTotal);
            orderinProgresssuiModel.setPharmaTotal(getResources().getString(R.string.rupee) + String.valueOf(pharmaformatted));
            orderinProgresssuiModel.setFmcgTotal(getResources().getString(R.string.rupee) + String.valueOf(fmcgFormatted));
            orderinProgresssuiModel.setTotalMedicineCount(String.valueOf(dataList.size()));
            String totalprodAmt = formatter.format(pharmaTotal + fmcgTotal);

            orderinProgresssuiModel.setMedicineTotal(getResources().getString(R.string.rupee) + String.valueOf(totalprodAmt));
            orderinProgresssuiModel.setFmcgPharma(isPharma && isFmcg);
            orderinProgresssuiModel.setFmcg(isFmcg);
            orderinProgresssuiModel.setPharma(isPharma);
            orderinProgresssuiModel.setPharmaHomeDelivery(isPharmadeliveryType);
            orderinProgresssuiModel.setFmcgHomeDelivery(isFmcgDeliveryType);
            orderinProgressBinding.setModel(orderinProgresssuiModel);
            if (!isPharma && isFmcg) {
                orderinProgressBinding.orderisinProgressText.setText("Your order is Completed");
            }
            if (isPharma && isFmcg || isPharma) {
                orderinProgressBinding.orderisinProgressText.setText("Your order is in progress");
            }
            if (isFmcgQrCodePayment) {
//                expressCheckoutTransactionId = "200008578";
//                transactionId = expressCheckoutTransactionId;
//                expressCheckoutTransactionId = (String) getIntent().getStringExtra("EXPRESS_CHECKOUT_TRANSACTION_ID");
//                orderInProgressController = new OrderInProgressController(this, this);
//                orderInProgressController.downloadPdf(expressCheckoutTransactionId);
            }
        }
    }

    Handler continueShopAlertHandler = new Handler();
    Runnable continuShopAlertRunnable = () -> continueShoppingAlert();

    private void continueShoppingAlert() {
        Dialog continueShopAlertDialog = new Dialog(this);
        continueShopAlertDialog.setContentView(R.layout.dialog_alert_for_idle);
        if (continueShopAlertDialog.getWindow() != null)
            continueShopAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        continueShopAlertDialog.setCancelable(false);
        TextView dialogTitleText = continueShopAlertDialog.findViewById(R.id.dialog_info);
        Button okButton = continueShopAlertDialog.findViewById(R.id.dialog_ok);
        Button declineButton = continueShopAlertDialog.findViewById(R.id.dialog_cancel);
        TextView alertTittle = continueShopAlertDialog.findViewById(R.id.session_time_expiry_countdown);

        SpannableStringBuilder alertSpannnable = new SpannableStringBuilder("Alert!");
        alertSpannnable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, alertSpannnable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        alertTittle.setText(alertSpannnable);


        dialogTitleText.setText("Do you want to continue shopping?");
        okButton.setText("Yes, Continue");
        declineButton.setText("Logout");
        okButton.setOnClickListener(v -> {
            if (continueShopAlertDialog != null && continueShopAlertDialog.isShowing()) {
                continueShopAlertDialog.dismiss();
            }
            List<OCRToDigitalMedicineResponse> dataList = new ArrayList<>();
            SessionManager.INSTANCE.setDataList(dataList);
            Intent intent = new Intent(OrderinProgressActivity.this, MySearchActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
            finish();

        });
        declineButton.setOnClickListener(v -> {
            continueShopAlertDialog.dismiss();

//            SessionManager.INSTANCE.logoutUser();
            List<OCRToDigitalMedicineResponse> dataList = new ArrayList<>();
            SessionManager.INSTANCE.setDataList(dataList);
            HomeActivity.isLoggedin = false;

            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finishAffinity();

//            Intent intent = new Intent(OrderinProgressActivity.this, HomeActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//            finishAffinity();

        });
        continueShopAlertDialog.show();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        onClickContinueShopping();
    }

    @Override
    protected void onPause() {
        super.onPause();
        continueShopAlertHandler.removeCallbacks(continuShopAlertRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        continueShopAlertHandler.removeCallbacks(continuShopAlertRunnable);
        continueShopAlertHandler.postDelayed(continuShopAlertRunnable, 30000);
    }

    @Override
    public void onClickContinueShopping() {
        List<OCRToDigitalMedicineResponse> dataList = new ArrayList<>();
        SessionManager.INSTANCE.setDataList(dataList);
        Intent intent = new Intent(this, MySearchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
//        Intent intent = new Intent(OrderinProgressActivity.this, MySearchActivity.class);
//        startActivity(intent);
//        overridePendingTransition(R.animator.trans_left_in, R.animator.trans_left_out);
//        finish();
    }

    @Override
    public void onFailureMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    PdfModelResponse pdfModelResponse;
    String transactionId;


    @Override
    public void onSuccessPdfResponse(PdfModelResponse pdfModelResponse) {
        this.pdfModelResponse = pdfModelResponse;
        if (isStoragePermissionGranted()) {

            transactionId = expressCheckoutTransactionId;

            if (pdfModelResponse != null) {
                try {
                    createPdf();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (orderinProgressBinding.layoutPdfPreview != null) {
                    orderinProgressBinding.layoutPdfPreview.removeAllViews();
                }
//            isStoragePermissionGranted();
//                createPDF(transactionId, orderinProgressBinding.layoutPdfPreview, pdfModelResponse, new PDFUtil.PDFUtilListener() {
//                    @Override
//                    public void pdfGenerationSuccess(File savedPDFFile) {
//                        isBillPrintReady = true;
////                        Toast.makeText(OrderinProgressActivity.this, "PDF Created", Toast.LENGTH_SHORT).show();
////                        openPdf();
//                    }
//
//                    @Override
//                    public void pdfGenerationFailure(Exception exception) {
//                        Toast.makeText(OrderinProgressActivity.this, "PDF NOT Created", Toast.LENGTH_SHORT).show();
//                    }
//                });

//        Toast.makeText(getContext(), "Pdf api is successfull", Toast.LENGTH_SHORT ).show();

//        orderSummaryBinding.postesting.setText(pdfModelResponse.getSalesHeader().get(0).getBranch());

            }
        }

    }

    @Override
    public void onFailurePdfResponse(PdfModelResponse body) {

    }

    @Override
    public void onClickPrintReceipt() {
//        if (isBillPrintReady) {
//        expressCheckoutTransactionId = "400006876";
//        transactionId = expressCheckoutTransactionId;
        expressCheckoutTransactionId = (String) getIntent().getStringExtra("EXPRESS_CHECKOUT_TRANSACTION_ID");
        orderInProgressController = new OrderInProgressController(this, this);
        orderInProgressController.downloadPdf(expressCheckoutTransactionId);

//        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                Log.v(TAG,"Permission is granted");
                return true;
            } else {

//                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
//            Log.v(TAG,"Permission is granted");
            return true;
        }
    }


    private void openPdf() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(), transactionId.concat(".pdf"));
        if (file.exists()) {
            //Button To start print

            PrintAttributes.Builder builder = new PrintAttributes.Builder();
            builder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);
            builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4);
            builder.setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME);

            PrintManager printManager = (PrintManager) primaryBaseActivity.getSystemService(Context.PRINT_SERVICE);
            String jobName = this.getString(R.string.app_name) + " Document";

            printManager.print(jobName, pda, builder.build());

//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
////            Uri uri = Uri.fromFile(file);
//            intent.setDataAndType(photoURI, "application/pdf");
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//
//            try {
//                startActivity(intent);
//            } catch (ActivityNotFoundException e) {
//                Toast.makeText(this, "No Application for pdf view", Toast.LENGTH_SHORT).show();
//            }
        } else {
//            Toast.makeText(this, "File not exist", Toast.LENGTH_SHORT).show();
        }
    }

    PrintDocumentAdapter pda = new PrintDocumentAdapter() {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
            InputStream input = null;
            OutputStream output = null;
            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(), transactionId.concat(".pdf"));

                input = new FileInputStream(file);//"/storage/emulated/0/Documents/my-document-1656940186153.pdf"
                output = new FileOutputStream(destination.getFileDescriptor());
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buf)) > 0) {
                    output.write(buf, 0, bytesRead);
                }
            } catch (Exception e) {

            } finally {
                try {
                    if (input != null) {
                        input.close();
                    } else {
                        Toast.makeText(OrderinProgressActivity.this, "FileInputStream getting null", Toast.LENGTH_SHORT).show();
                    }

                    if (output != null) {
                        output.close();
                    } else {
                        Toast.makeText(OrderinProgressActivity.this, "FileOutStream getting null", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }
            //int pages = computePageCount(newAttributes);
            PrintDocumentInfo pdi = new PrintDocumentInfo.Builder(transactionId + ".pdf").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
            callback.onLayoutFinished(pdi, true);
        }

    };

    @Override
    protected PDFHeaderView getHeaderView(int forPage, PdfModelResponse pdfModelResponse) {
        PDFHeaderView headerView = new PDFHeaderView(getApplicationContext());

        PDFVerticalView verticalViewFirst = new PDFVerticalView(getApplicationContext());


//        PDFLineSeparatorView lineSeparatorView1vertical = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(GRAY);
//        verticalViewFirst.addView(lineSeparatorView1vertical);

        PDFHorizontalView horizontalView = new PDFHorizontalView(getApplicationContext());

//        PDFLineSeparatorViewVertical lineSeparatorView1verticalM = new PDFLineSeparatorViewVertical(getApplicationContext()).setBackgroundColor(GRAY);
//        horizontalView.addView(lineSeparatorView1verticalM);

        PDFVerticalView verticalView = new PDFVerticalView(getApplicationContext());
        LinearLayout.LayoutParams verticalLayoutParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        verticalLayoutParam.setMargins(0, 10, 0, 0);

        PDFVerticalView verticalView2 = new PDFVerticalView(getApplicationContext());
        LinearLayout.LayoutParams headerImageLayoutParam = new LinearLayout.LayoutParams(100, 100, 0);
        PDFImageView imageView = new PDFImageView(getApplicationContext());
        imageView.setImageScale(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.apollo_circle_logo);
        headerImageLayoutParam.setMargins(0, 0, 0, 0);
        verticalView2.addView(imageView);
        verticalView2.setLayout(headerImageLayoutParam);
        verticalView2.getView().setGravity(Gravity.CENTER_HORIZONTAL);
        horizontalView.addView(verticalView2);//1st horizantal view apollo image

        PDFHorizontalView pdfHorizontalView1n = new PDFHorizontalView(getApplicationContext());
        LinearLayout.LayoutParams horizontalLayoutParam1n = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalLayoutParam1n.setMargins(0, 0, 0, 0);

        PDFTextView pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText("Apollo Pharmacy-");
        pdfTextView1.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        pdfHorizontalView1n.addView(pdfTextView1);

        PDFTextView pdfTextView2n = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText(pdfModelResponse.getSalesHeader().get(0).getBranch());
        pdfTextView2n.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        pdfHorizontalView1n.setLayout(horizontalLayoutParam1n);
        pdfHorizontalView1n.getView().setGravity(Gravity.CENTER_VERTICAL);
        pdfHorizontalView1n.addView(pdfTextView2n);

        verticalView.addView(pdfHorizontalView1n);//2nd horizantal view pos testing


        PDFTextView pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText(pdfModelResponse.getSalesHeader().get(0).getAddressOne()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        verticalView.addView(pdfTextView2);
        PDFTextView pdfTextView3 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText(pdfModelResponse.getSalesHeader().get(0).getAddressTwo()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        verticalView.addView(pdfTextView3);

        PDFHorizontalView pdfHorizontalView1nNew1 = new PDFHorizontalView(getApplicationContext());
        LinearLayout.LayoutParams horizontalLayoutParam1nNew1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalLayoutParam1nNew1.setMargins(0, 0, 0, 0);

        PDFTextView pdfTextView1New1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText("PHONE - ");
        pdfTextView1New1.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        pdfHorizontalView1nNew1.addView(pdfTextView1New1);

        PDFTextView pdfTextView2nNew1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText(pdfModelResponse.getSalesHeader().get(0).getTelNo());
        pdfTextView2nNew1.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        pdfHorizontalView1nNew1.setLayout(horizontalLayoutParam1nNew1);
        pdfHorizontalView1nNew1.addView(pdfTextView2nNew1);

        verticalView.addView(pdfHorizontalView1nNew1);

        verticalView.setLayout(verticalLayoutParam);
        verticalView.getView().setGravity(Gravity.CENTER_VERTICAL);
        horizontalView.addView(verticalView);// 2nd horizantal pos testing layout


        PDFVerticalView verticalView3 = new PDFVerticalView(getApplicationContext());
        verticalLayoutParam.setMargins(0, 0, 0, 0);

        PDFHorizontalView pdfHorizontalView1nNew2 = new PDFHorizontalView(getApplicationContext());
        LinearLayout.LayoutParams horizontalLayoutParam1nNew2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalLayoutParam1nNew2.setMargins(0, 0, 0, 0);

        PDFTextView pdfTextView1New2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText("FSSAI NO: ");
        pdfTextView1New2.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        pdfHorizontalView1nNew2.addView(pdfTextView1New2);

        PDFTextView pdfTextView2nNew2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText(pdfModelResponse.getSalesHeader().get(0).getFssaino());
        pdfTextView2nNew2.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        pdfHorizontalView1nNew2.setLayout(horizontalLayoutParam1nNew2);
        pdfHorizontalView1nNew2.addView(pdfTextView2nNew2);

        verticalView3.addView(pdfHorizontalView1nNew2);

        PDFHorizontalView pdfHorizontalView1nNew3 = new PDFHorizontalView(getApplicationContext());
        LinearLayout.LayoutParams horizontalLayoutParam1nNew3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalLayoutParam1nNew3.setMargins(0, 0, 0, 0);

        PDFTextView pdfTextView1New3 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText("D.L.NO: ");
        pdfTextView1New3.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        pdfHorizontalView1nNew3.addView(pdfTextView1New3);

        PDFTextView pdfTextView2nNew3 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText(pdfModelResponse.getSalesHeader().get(0).getDlno());
        pdfTextView2nNew3.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        pdfHorizontalView1nNew3.setLayout(horizontalLayoutParam1nNew3);
        pdfHorizontalView1nNew3.addView(pdfTextView2nNew3);

        verticalView3.addView(pdfHorizontalView1nNew3);

        PDFHorizontalView pdfHorizontalView1nNew4 = new PDFHorizontalView(getApplicationContext());
        LinearLayout.LayoutParams horizontalLayoutParam1nNew4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalLayoutParam1nNew4.setMargins(0, 0, 0, 0);

        PDFTextView pdfTextView1New4 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText("GST NO: ");
        pdfTextView1New4.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        pdfHorizontalView1nNew4.addView(pdfTextView1New4);

        PDFTextView pdfTextView2nNew4 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText(pdfModelResponse.getSalesHeader().get(0).getGstin());
        pdfTextView2nNew4.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        pdfHorizontalView1nNew4.setLayout(horizontalLayoutParam1nNew4);
        pdfHorizontalView1nNew4.addView(pdfTextView2nNew4);

        verticalView3.addView(pdfHorizontalView1nNew4);

        verticalView3.setLayout(verticalLayoutParam);
        verticalView3.getView().setGravity(Gravity.CENTER_VERTICAL);

        PDFHorizontalView pdfHorizontalView1nNew5 = new PDFHorizontalView(getApplicationContext());
        LinearLayout.LayoutParams horizontalLayoutParam1nNew5 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalLayoutParam1nNew5.setMargins(0, 0, 0, 0);

        PDFTextView pdfTextView1New5 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText("C.GSTIN NO: ");
        pdfTextView1New5.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        pdfHorizontalView1nNew5.addView(pdfTextView1New5);

        PDFTextView pdfTextView2nNew5 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText(pdfModelResponse.getSalesHeader().get(0).getCgstin());
        pdfTextView2nNew5.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        pdfHorizontalView1nNew5.setLayout(horizontalLayoutParam1nNew5);
        pdfHorizontalView1nNew5.addView(pdfTextView2nNew5);

        verticalView3.addView(pdfHorizontalView1nNew5);

        verticalView3.setLayout(verticalLayoutParam);
        verticalView3.getView().setGravity(Gravity.CENTER_VERTICAL);


        horizontalView.addView(verticalView3); //// 3rd horizantal fssai layout

        PDFVerticalView verticalView4 = new PDFVerticalView(getApplicationContext());
        LinearLayout.LayoutParams horizontalLayoutParam1nNew2nLi = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        verticalView4.setLayout(horizontalLayoutParam1nNew2nLi);
        verticalLayoutParam.setMargins(5, 0, 0, 0);

        PDFVerticalView pdfHorizontalView1nNew2n = new PDFVerticalView(getApplicationContext());
        LinearLayout.LayoutParams horizontalLayoutParam1nNew2n = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        horizontalLayoutParam1nNew2n.setMargins(0, 0, 0, 0);

        PDFTextView pdfTextView1New25 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);

        LinearLayout.LayoutParams pdfTextView1New25Layouts = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pdfTextView1New25.setLayout(pdfTextView1New25Layouts);
        pdfTextView1New25.setText("Registered Office: ");
        pdfTextView1New25.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        pdfHorizontalView1nNew2n.addView(pdfTextView1New25);

        PDFTextView pdfTextView2nNew25 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        LinearLayout.LayoutParams pdfTextView2nNew25Layouts = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pdfTextView2nNew25.setLayout(pdfTextView2nNew25Layouts);
        pdfTextView2nNew25.setText("No.19 Bishop Garden, Raja Annamalaipuram, Chennai-600028");
        pdfTextView2nNew25.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        pdfHorizontalView1nNew2n.setLayout(horizontalLayoutParam1nNew2n);
        pdfHorizontalView1nNew2n.addView(pdfTextView2nNew25);

        verticalView4.addView(pdfHorizontalView1nNew2n);

        PDFVerticalView pdfHorizontalView1nNew3n = new PDFVerticalView(getApplicationContext());
        LinearLayout.LayoutParams horizontalLayoutParam1nNew3n = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalLayoutParam1nNew3n.setMargins(0, 0, 0, 0);

        PDFTextView pdfTextView1New3n = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText("Admin Office: ");
        pdfTextView1New3n.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        pdfHorizontalView1nNew3n.addView(pdfTextView1New3n);

        PDFTextView pdfTextView2nNew3n = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText("(For all correspondence) Ali Towers,IIIrd Floor,No 55,Greams Road, Chennai-600006.");
        pdfTextView2nNew3n.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        pdfHorizontalView1nNew3n.setLayout(horizontalLayoutParam1nNew3n);
        pdfHorizontalView1nNew3n.addView(pdfTextView2nNew3n);

        verticalView4.addView(pdfHorizontalView1nNew3n);

        PDFVerticalView pdfHorizontalView1nNew4n = new PDFVerticalView(getApplicationContext());
        LinearLayout.LayoutParams horizontalLayoutParam1nNew4n = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalLayoutParam1nNew4n.setMargins(0, 0, 0, 0);

        PDFTextView pdfTextView1New4n = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText("CIN: ");
        pdfTextView1New4n.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        pdfHorizontalView1nNew4n.addView(pdfTextView1New4n);

        PDFTextView pdfTextView2nNew4n = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText("U52500TN2016PLC111328");
        pdfTextView2nNew4n.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        pdfHorizontalView1nNew4n.setLayout(horizontalLayoutParam1nNew4n);
        pdfHorizontalView1nNew4n.addView(pdfTextView2nNew4n);

        verticalView4.addView(pdfHorizontalView1nNew4n);

        verticalView4.setLayout(verticalLayoutParam);
        verticalView4.getView().setGravity(Gravity.CENTER_VERTICAL);
        horizontalView.addView(verticalView4); //// 3rd horizantal fssai layout


//        PDFVerticalView verticalView4 = new PDFVerticalView(getApplicationContext());
//        verticalLayoutParam.setMargins(10, 0, 0, 0);
//
//
//        pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).
//        setText("Registered Office:No.19 Bishop Garden, Raja Annamalaipuram, Chennai-600028").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        verticalView4.addView(pdfTextView1);
//        pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL)
//                .setText("Admin Office : (For all correspondence) Ali Towers,IIIrd Floor,No 55,Greams Road, Chennai-600006.").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        verticalView4.addView(pdfTextView2);
//        pdfTextView3 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL)
//                .setText("CIN : U52500TN2016PLC111328").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        verticalView4.addView(pdfTextView3);
//        PDFTextView pdfTextView4 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
////
////        verticalView4.addView(pdfTextView4);
//        verticalView4.setLayout(verticalLayoutParam);
//        verticalView4.getView().setGravity(Gravity.CENTER_VERTICAL);
//
//        horizontalView.addView(verticalView4);
        // 4th horizantal registered layout
//        PDFLineSeparatorViewVertical lineSeparatorView1verticalNN = new PDFLineSeparatorViewVertical(getApplicationContext()).setBackgroundColor(GRAY);
//        horizontalView.addView(lineSeparatorView1verticalNN);

        verticalViewFirst.addView(horizontalView);


        PDFHorizontalView horizontalViewInvoice = new PDFHorizontalView(getApplicationContext());

//        PDFLineSeparatorViewVertical lineSeparatorView1verticalInvoice = new PDFLineSeparatorViewVertical(getApplicationContext()).setBackgroundColor(GRAY);
//        horizontalViewInvoice.addView(lineSeparatorView1verticalInvoice);

        PDFTextView pdfTextViewNew = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3)

                .setText("INVOICE");
        pdfTextViewNew.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        pdfTextViewNew.getView().setGravity(Gravity.CENTER);
        horizontalViewInvoice.addView(pdfTextViewNew);

//        PDFLineSeparatorViewVertical lineSeparatorView1verticalInvoice2 = new PDFLineSeparatorViewVertical(getApplicationContext()).setBackgroundColor(GRAY);
//        horizontalViewInvoice.addView(lineSeparatorView1verticalInvoice2);


        verticalViewFirst.addView(horizontalViewInvoice);


        PDFLineSeparatorView lineSeparatorView1 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(GRAY);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1, 0);
        layoutParams1.setMargins(0, 2, 0, 2);
        lineSeparatorView1.setLayout(layoutParams1);
        verticalViewFirst.addView(lineSeparatorView1);

        PDFHorizontalView horizontalViewFisrtLine = new PDFHorizontalView(getApplicationContext());
        LinearLayout.LayoutParams horizontalViewFisrtLine1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalViewFisrtLine1.setMargins(0, 0, 0, 0);


        pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        pdfTextView1.setPadding(0, 0, 0, 0);
        pdfTextView1.setText("Name: " + pdfModelResponse.getSalesHeader().get(0).getCustName()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        horizontalViewFisrtLine.addView(pdfTextView1);

        pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        pdfTextView2.setPadding(10, 0, 0, 0);
        pdfTextView2.setText("Mobile No.:" + pdfModelResponse.getSalesHeader().get(0).getCustMobile()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        horizontalViewFisrtLine.addView(pdfTextView2);


        pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        pdfTextView1.setPadding(10, 0, 0, 0);
        pdfTextView1.setText("Bill No.:" + pdfModelResponse.getSalesHeader().get(0).getReceiptId()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        horizontalViewFisrtLine.addView(pdfTextView1);


        pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        pdfTextView1.setPadding(10, 0, 0, 0);
        pdfTextView1.setText(pdfModelResponse.getSalesHeader().get(0).getCorporate()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        horizontalViewFisrtLine.addView(pdfTextView1);


        horizontalViewFisrtLine.setLayout(horizontalViewFisrtLine1);
        verticalViewFirst.addView(horizontalViewFisrtLine);

        PDFLineSeparatorView lineSeparatorViewNew1n = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(GRAY);
        LinearLayout.LayoutParams layoutParams11 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1, 0);
        layoutParams11.setMargins(0, 0, 0, 2);
        lineSeparatorViewNew1n.setLayout(layoutParams11);
        verticalViewFirst.addView(lineSeparatorViewNew1n);

        PDFHorizontalView horizontalViewSecondLine = new PDFHorizontalView(getApplicationContext());
        LinearLayout.LayoutParams horizontalViewSecondLine1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalViewSecondLine1.setMargins(0, 0, 0, 0);


        pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        pdfTextView2.setPadding(0, 0, 0, 0);
        if (pdfModelResponse.getSalesHeader().get(0).getDoctorName().equalsIgnoreCase("")) {
            pdfTextView2.setText("Doctor :" + "--").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        } else {
            pdfTextView2.setText("Doctor :" + pdfModelResponse.getSalesHeader().get(0).getDoctorName()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        }
        horizontalViewSecondLine.addView(pdfTextView2);

        pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        pdfTextView2.setPadding(40, 0, 0, 0);
        pdfTextView2.setText("Ref No: " + pdfModelResponse.getSalesHeader().get(0).getRefNo()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        horizontalViewSecondLine.addView(pdfTextView2);


        pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        pdfTextView2.setPadding(10, 0, 0, 0);
        pdfTextView2.setText("TID : " + pdfModelResponse.getSalesHeader().get(0).getTerminalId()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        horizontalViewSecondLine.addView(pdfTextView2);


        pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        pdfTextView2.setPadding(15, 0, 0, 0);
        pdfTextView2.setText("Bill Date: " + pdfModelResponse.getSalesHeader().get(0).getTransDate()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        horizontalViewSecondLine.addView(pdfTextView2);


        horizontalViewSecondLine.setLayout(horizontalViewSecondLine1);
        verticalViewFirst.addView(horizontalViewSecondLine);
//        verticalViewFirst.setLayout(verticalLayoutParamSample);
        PDFLineSeparatorView lineSeparatorView1s = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(GRAY);
        LinearLayout.LayoutParams layoutParams1s = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1, 0);
        layoutParams1s.setMargins(0, 2, 0, 2);
        lineSeparatorView1s.setLayout(layoutParams1s);
        verticalViewFirst.addView(lineSeparatorView1s);
        headerView.addView(verticalViewFirst);

        return headerView;
    }

    @Override
    protected PDFBody getBodyViews(PdfModelResponse pdfModelResponse) {
        PDFBody pdfBody = new PDFBody();
//        PDFHorizontalView horizontalView = new PDFHorizontalView(getApplicationContext());
//        PDFVerticalView verticalView1 = new PDFVerticalView(getApplicationContext());
//        LinearLayout.LayoutParams verticalLayoutParamSample = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        verticalLayoutParamSample.setMargins(0, 0, 0, 0);
//
//
//        LinearLayout.LayoutParams verticalLayoutParam1 = new LinearLayout.LayoutParams(
//                0,
//                LinearLayout.LayoutParams.WRAP_CONTENT, 1);
//        verticalLayoutParam1.setMargins(0, 10, 0, 0);
//
//
//        PDFTextView pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//        PDFTextView pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//
//        PDFHorizontalView horizontalViewFisrtLine = new PDFHorizontalView(getApplicationContext());
//        LinearLayout.LayoutParams horizontalViewFisrtLine1 = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        horizontalViewFisrtLine1.setMargins(0, 0, 0, 0);
//
//
//        pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//        pdfTextView1.setPadding(0, 0, 0, 0);
//        pdfTextView1.setText("Name: " + pdfModelResponse.getSalesHeader().get(0).getCustName()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
//        horizontalViewFisrtLine.addView(pdfTextView1);
//
//        pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//        pdfTextView2.setPadding(10, 0, 0, 0);
//        pdfTextView2.setText("Mobile No.:" + pdfModelResponse.getSalesHeader().get(0).getCustMobile()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
//        horizontalViewFisrtLine.addView(pdfTextView2);
//
//
//        pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//        pdfTextView1.setPadding(10, 0, 0, 0);
//        pdfTextView1.setText("Bill No.:" + pdfModelResponse.getSalesHeader().get(0).getReceiptId()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
//        horizontalViewFisrtLine.addView(pdfTextView1);
//
//
//        pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//        pdfTextView1.setPadding(10, 0, 0, 0);
//        pdfTextView1.setText(pdfModelResponse.getSalesHeader().get(0).getCorporate()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
//        horizontalViewFisrtLine.addView(pdfTextView1);
//
//
//        horizontalViewFisrtLine.setLayout(horizontalViewFisrtLine1);
//        verticalView1.addView(horizontalViewFisrtLine);
////        verticalView1.setLayout(verticalLayoutParamSample);
////        horizontalView.addView(verticalView1);
////        pdfBody.addView(horizontalView);
//
//
//        PDFLineSeparatorView lineSeparatorViewNew1n = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(GRAY);
//        LinearLayout.LayoutParams layoutParams11 = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                1, 0);
//        layoutParams11.setMargins(0, 0, 0, 2);
//        lineSeparatorViewNew1n.setLayout(layoutParams11);
//        verticalView1.addView(lineSeparatorViewNew1n);
//
//
//
//
//        PDFHorizontalView horizontalViewSecondLine = new PDFHorizontalView(getApplicationContext());
//        LinearLayout.LayoutParams horizontalViewSecondLine1 = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        horizontalViewSecondLine1.setMargins(0, 0, 0, 0);
//
//
//
//        pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//        pdfTextView2.setPadding(0, 0, 0, 0);
//        if (pdfModelResponse.getSalesHeader().get(0).getDoctorName().equalsIgnoreCase("")) {
//            pdfTextView2.setText("Doctor :" + "--").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
//        } else {
//            pdfTextView2.setText("Doctor :" + pdfModelResponse.getSalesHeader().get(0).getDoctorName()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
//        }
//        horizontalViewSecondLine.addView(pdfTextView2);
//
//        pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//        pdfTextView2.setPadding(40, 0, 0, 0);
//        pdfTextView2.setText("Ref No: " + pdfModelResponse.getSalesHeader().get(0).getRefNo()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
//        horizontalViewSecondLine.addView(pdfTextView2);
//
//
//        pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//        pdfTextView2.setPadding(10, 0, 0, 0);
//        pdfTextView2.setText("TID : " + pdfModelResponse.getSalesHeader().get(0).getTerminalId()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
//        horizontalViewSecondLine.addView(pdfTextView2);
//
//
//        pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//        pdfTextView2.setPadding(15, 0, 0, 0);
//        pdfTextView2.setText("Bill Date: " + pdfModelResponse.getSalesHeader().get(0).getTransDate()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
//        horizontalViewSecondLine.addView(pdfTextView2);
//
//
//        horizontalViewSecondLine.setLayout(horizontalViewSecondLine1);
//        verticalView1.addView(horizontalViewSecondLine);
//        verticalView1.setLayout(verticalLayoutParamSample);
////        horizontalView.addView(verticalView1);
//        pdfBody.addView(verticalView1);

//
//        PDFTextView pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//                .setText("Name: " + pdfModelResponse.getSalesHeader().get(0).getCustName()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        verticalView1.addView(pdfTextView1);
//        PDFTextView pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//        if (pdfModelResponse.getSalesHeader().get(0).getDoctorName().equalsIgnoreCase("")) {
//            pdfTextView2.setText("Doctor :" + "--").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        } else {
//            pdfTextView2.setText("Doctor :" + pdfModelResponse.getSalesHeader().get(0).getDoctorName()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        }
//        verticalView1.addView(pdfTextView2);
//        verticalView1.setLayout(verticalLayoutParam1);
//        verticalView1.getView().setGravity(Gravity.CENTER_VERTICAL);
//        horizontalView.addView(verticalView1);
//
//        PDFVerticalView verticalView2 = new PDFVerticalView(getApplicationContext());
//        pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//                .setText("Mobile No.:" + pdfModelResponse.getSalesHeader().get(0).getCustMobile()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        verticalView2.addView(pdfTextView1);
//        pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL)
//                .setText("Ref No: " + pdfModelResponse.getSalesHeader().get(0).getRefNo()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        verticalView2.addView(pdfTextView2);
//        verticalView2.setLayout(verticalLayoutParam1);
//        verticalView2.getView().setGravity(Gravity.CENTER_VERTICAL);
//        horizontalView.addView(verticalView2);
//
//        PDFVerticalView verticalView3 = new PDFVerticalView(getApplicationContext());
//        pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL)
//                .setText("Bill No.:" + pdfModelResponse.getSalesHeader().get(0).getReceiptId()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        verticalView3.addView(pdfTextView1);
//        pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL)
//                .setText("TID : " + pdfModelResponse.getSalesHeader().get(0).getTerminalId()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        verticalView3.addView(pdfTextView2);
//        verticalView3.setLayout(verticalLayoutParam1);
//        verticalView3.getView().setGravity(Gravity.CENTER_VERTICAL);
//        horizontalView.addView(verticalView3);
//
//
//        PDFVerticalView verticalView4 = new PDFVerticalView(getApplicationContext());
//        pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL)
//                .setText(pdfModelResponse.getSalesHeader().get(0).getCorporate()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        verticalView4.addView(pdfTextView1);
//        pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL)
//                .setText("Bill Date: " + pdfModelResponse.getSalesHeader().get(0).getTransDate()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        verticalView4.addView(pdfTextView2);
//
//        verticalView4.setLayout(verticalLayoutParam1);
//        verticalView4.getView().setGravity(Gravity.CENTER_VERTICAL);
//        horizontalView.addView(verticalView4);
//
//        pdfBody.addView(horizontalView);


//        PDFLineSeparatorView lineSeparatorView2 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(GRAY);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                1, 0);
//        layoutParams.setMargins(0, 0, 0, 0);
//        lineSeparatorView2.setLayout(layoutParams);
//        pdfBody.addView(lineSeparatorView2);


        int[] widthPercent = {7, 1, 5, 1, 18, 1, 5, 1, 10, 1, 7, 1, 10, 1, 7, 1, 7, 1, 9, 1, 5};
        //  int[] widthPercent = {18, 4, 5, 2, 10, 2, 6, 1, 10, 1, 8, 2, 6, 8, 2, 8, 7}; // Sum should be equal to 100%
        String[] textInTable = {"Rack", "", "Qty", "", "Product Name", "", "SCH", "", "HSNCODE", "", "Mfg", "", "BATCH", "", "EXPIRY", "", "MRP", "", "AMOUNT", "", "GST%"};


        PDFTableView.PDFTableRowView tableHeader = new PDFTableView.PDFTableRowView(getApplicationContext());
        for (String s : textInTable) {
            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
            pdfTextView.setText(s).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));

            tableHeader.addToRow(pdfTextView);
        }

        // do not modify table first row
        PDFTableView.PDFTableRowView tableRowView1 = new PDFTableView.PDFTableRowView(getApplicationContext());
//        for (String s : textInTable) {
//            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
////            pdfTextView.setText( s);
//            tableRowView1.addToRow(pdfTextView);
//        }

        PDFTableView tableView = new PDFTableView(getApplicationContext(), tableHeader, tableRowView1);

        for (int i = 0; i < pdfModelResponse.getSalesLine().size(); i++) {
            PdfModelResponse.SalesLine salesLine = pdfModelResponse.getSalesLine().get(i);
            if (salesLine.getIsShippingCharge() == 0 && salesLine.getIsCircleCharge() == 0) {
                // Create 10 rows
                PDFTableView.PDFTableRowView tableRowView = new PDFTableView.PDFTableRowView(getApplicationContext());
                for (int j = 0; j < textInTable.length; j++) {
                    PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);

                    if (j == 0) {

//                        pdfTextView.getView().setSingleLine(true);
//                        pdfTextView.getView().setMaxLines(1);
                        String itemName = salesLine.getItemName().replace(" ", "\u00A0");
                        LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
                        pdfTextView.setLayout(childLayoutParams);
                        pdfTextView.setText(salesLine.getRackId()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
                    } else if (j == 1) {
//                        PDFTableView  pdfTableView= new PDFTableView(getApplicationContext(),);
//                        pdfTextView.setText("|");
//                        PDFHorizontalView pdfHorizontalViewf = new PDFHorizontalView(getApplicationContext());
//                        LinearLayout.LayoutParams childLayoutParamsx = new LinearLayout.LayoutParams(
//                                ViewGroup.LayoutParams.MATCH_PARENT,
//                                ViewGroup.LayoutParams.WRAP_CONTENT, 1);
//                        PDFHorizontalView pdfHorizontalViewg = new PDFHorizontalView(getApplicationContext());
//                        LinearLayout.LayoutParams childLayoutParamsxy = new LinearLayout.LayoutParams(
//                              0,
//                                ViewGroup.LayoutParams.WRAP_CONTENT, 0.1F);
//                        pdfTextView.setBackgroundColor(Color.BLACK);
//                        pdfHorizontalViewg.setLayout(childLayoutParamsxy);


//                        LinearLayout.LayoutParams layoutParamstest = new LinearLayout.LayoutParams(
//                                1,
//                                LinearLayout.LayoutParams.MATCH_PARENT);
//                        pdfTextView.setLayout(layoutParamstest);
//                        pdfTextView.setBackgroundColor(Color.BLACK);

                    } else if (j == 2) {
                        LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
                        pdfTextView.setLayout(childLayoutParams);
                        pdfTextView.setText(salesLine.getQty()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
                    } else if (j == 3) {


                    } else if (j == 4) {
                        String itemName = salesLine.getItemName().replace(" ", "\u00A0");
                        LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
                        pdfTextView.setLayout(childLayoutParams);
                        pdfTextView.setText(itemName).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
                    } else if (j == 5) {

                    } else if (j == 6) {
                        LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
                        pdfTextView.setLayout(childLayoutParams);
                        pdfTextView.setText(salesLine.getSch()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
                    } else if (j == 7) {
                    } else if (j == 8) {
                        LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
                        pdfTextView.setLayout(childLayoutParams);
                        pdfTextView.setText(salesLine.getHSNCode()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
                    } else if (j == 9) {
                    } else if (j == 10) {
                        LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
                        pdfTextView.setLayout(childLayoutParams);
                        pdfTextView.setText(salesLine.getManufacturer()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));

                    } else if (j == 11) {
                    } else if (j == 12) {
                        String batchNo = "";
                        if (salesLine.getBatchNo().length() > 12) {
                            batchNo = salesLine.getBatchNo().substring(0, 11);
                            batchNo = batchNo + "\n" + salesLine.getBatchNo().substring(12);
                        } else {
                            batchNo = salesLine.getBatchNo();
                        }
                        pdfTextView.setText(batchNo).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
                    } else if (j == 13) {
                    } else if (j == 14) {
                        if (salesLine.getExpDate() != null && salesLine.getExpDate().length() > 5) {
                            String expDate[] = salesLine.getExpDate().substring(2, 7).split("-");
                            pdfTextView.setText(expDate[1] + "-" + expDate[0]).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
                        } else {
                            pdfTextView.setText("-").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
                        }
                    } else if (j == 15) {
                    } else if (j == 16) {
                        pdfTextView.setText(salesLine.getMrp()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
                    } else if (j == 17) {
                    } else if (j == 18) {
                        pdfTextView.setText(salesLine.getLineTotAmount()).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
                    } else if (j == 19) {

                    } else {
                        Double gst = Double.parseDouble(salesLine.getSGSTPer()) + Double.parseDouble(salesLine.getCGSTPer());

                        pdfTextView.setText(String.format("%.02f", gst)).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
                    }
                    tableRowView.addToRow(pdfTextView);
                }
                tableView.addRow(tableRowView);
            }
        }
        tableView.setColumnWidth(widthPercent);
        pdfBody.addView(tableView);

//for e shipping charge and circle member items
        for (int i = 0; i < 1; i++) {
            PdfModelResponse.SalesLine salesLine = pdfModelResponse.getSalesLine().get(i);
            if (salesLine.getIsCircleCharge() == 1 || salesLine.getIsShippingCharge() == 1) {
                Double gst = Double.parseDouble(salesLine.getSGSTPer()) + Double.parseDouble(salesLine.getCGSTPer());
                String itemDetailsInALine = salesLine.getItemName() + ":" + salesLine.getMrp() + ", SAC:-" + ", GST:" + String.format("%.02f", gst) + "%";

                PDFHorizontalView shippingCharge = new PDFHorizontalView(getApplicationContext());
                PDFTextView shippingChargeText = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText(itemDetailsInALine).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//                shippingChargeText.setLayout(verticalLayoutParam1);
                shippingChargeText.getView().setGravity(Gravity.CENTER);
                shippingCharge.addView(shippingChargeText);
                pdfBody.addView(shippingCharge);
            }
        }
        PDFLineSeparatorView lineSeparatorView4 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(GRAY);
        LinearLayout.LayoutParams layoutParams14 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1, 0);
        layoutParams14.setMargins(0, 0, 0, 2);
        lineSeparatorView4.setLayout(layoutParams14);
        pdfBody.addView(lineSeparatorView4);

//        PDFHorizontalView taxbleView = new PDFHorizontalView(getApplicationContext());
//        double taxbleValue = 0.0;
//        for (int i = 0; i < pdfModelResponse.getSalesLine().size(); i++) {
//            if (pdfModelResponse.getSalesLine().get(i).getTaxable() != null
//                    && !pdfModelResponse.getSalesLine().get(i).getTaxable().isEmpty()) {
//                taxbleValue = taxbleValue + Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getTaxable());
//
//            }
//        }
//        LinearLayout.LayoutParams verticalLayoutParam8 = new LinearLayout.LayoutParams(
//                0,
//                LinearLayout.LayoutParams.WRAP_CONTENT, 1);
//        verticalLayoutParam8.setMargins(0, 0, 0, 0);
//
//
////        PDFTextView taxableValue = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL)
////                .setText("TAXABLE VALUE: " + String.format("%.02f", taxbleValue)).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
////        taxableValue.setLayout(verticalLayoutParam8);
////        taxableValue.getView().setGravity(Gravity.CENTER_VERTICAL);
////        taxbleView.addView(taxableValue);
//
//        PDFHorizontalView pdfHorizontalView1nNew1 = new PDFHorizontalView(getApplicationContext());
//        LinearLayout.LayoutParams horizontalLayoutParam1nNew1 = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        horizontalLayoutParam1nNew1.setMargins(0, 0, 0, 0);
//
//        PDFTextView pdfTextView1New1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL)
//                .setText("TAXABLE VALUE:  ");
//        pdfTextView1New1.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
//        pdfHorizontalView1nNew1.addView(pdfTextView1New1);
//
//        PDFTextView pdfTextView2nNew1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL)
//                .setText(String.format("%.02f", taxbleValue));
//        pdfTextView2nNew1.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        pdfHorizontalView1nNew1.setLayout(horizontalLayoutParam1nNew1);
//        pdfHorizontalView1nNew1.addView(pdfTextView2nNew1);
//        pdfHorizontalView1nNew1.getView().setGravity(Gravity.CENTER_VERTICAL);
//        taxbleView.addView(pdfHorizontalView1nNew1);
//
//        PDFHorizontalView pdfHorizontalView1nNew2 = new PDFHorizontalView(getApplicationContext());
//        LinearLayout.LayoutParams horizontalLayoutParam1nNew2 = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        horizontalLayoutParam1nNew2.setMargins(50, 0, 0, 0);
//
//        PDFTextView pdfTextView1New2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL)
//                .setText("CGstAMT :  ");
//        pdfTextView1New2.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
//        pdfHorizontalView1nNew2.addView(pdfTextView1New2);
//
//        PDFTextView pdfTextView2nNew2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//        double cgstAmount = 0.0;
//        for (int i = 0; i < pdfModelResponse.getSalesLine().size(); i++) {
//            if (pdfModelResponse.getSalesLine().get(i).getTaxable() != null
//                    && !pdfModelResponse.getSalesLine().get(i).getTaxable().isEmpty()
//
//                    && pdfModelResponse.getSalesLine().get(i).getCGSTPer() != null
//                    && !pdfModelResponse.getSalesLine().get(i).getCGSTPer().isEmpty()) {
////                cgstAmount = cgstAmount + ((Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getMrp()) * Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getQty()) * Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getCGSTPer())) / 100);
//                cgstAmount = cgstAmount + ((Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getTaxable()) * Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getCGSTPer())) / 100);
//
//            }
//        }
//        pdfTextView2nNew2.setText( String.format("%.02f", cgstAmount)).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));;
//        pdfTextView2nNew2.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        pdfHorizontalView1nNew2.setLayout(horizontalLayoutParam1nNew2);
//        pdfHorizontalView1nNew2.addView(pdfTextView2nNew2);
//        pdfHorizontalView1nNew2.getView().setGravity(Gravity.CENTER_VERTICAL);
//        taxbleView.addView(pdfHorizontalView1nNew2);
//
//
//        PDFHorizontalView pdfHorizontalView1nNew3 = new PDFHorizontalView(getApplicationContext());
//        LinearLayout.LayoutParams horizontalLayoutParam1nNew3 = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        horizontalLayoutParam1nNew3.setMargins(50, 0, 0, 0);
//
//        PDFTextView pdfTextView1New3 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL)
//                .setText("SGstAMT :  ");
//        pdfTextView1New3.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
//        pdfHorizontalView1nNew3.addView(pdfTextView1New3);
//
//        PDFTextView pdfTextView2nNew3 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//        double sgstAmount = 0.0;
//        for (int i = 0; i < pdfModelResponse.getSalesLine().size(); i++) {
//            if (pdfModelResponse.getSalesLine().get(i).getTaxable() != null
//                    && !pdfModelResponse.getSalesLine().get(i).getTaxable().isEmpty()
//                    && pdfModelResponse.getSalesLine().get(i).getSGSTPer() != null
//                    && !pdfModelResponse.getSalesLine().get(i).getSGSTPer().isEmpty()) {
//                sgstAmount = sgstAmount + ((Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getTaxable()) * Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getSGSTPer())) / 100);
//
//
////                sgstAmount = sgstAmount + ((Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getMrp()) * Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getQty()) * Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getSGSTPer())) / 100);
//
//            }
//        }
//        pdfTextView2nNew3.setText( String.format("%.02f", sgstAmount)).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));;
//        pdfTextView2nNew3.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        pdfHorizontalView1nNew3.setLayout(horizontalLayoutParam1nNew3);
//        pdfHorizontalView1nNew3.addView(pdfTextView2nNew3);
//        pdfHorizontalView1nNew3.getView().setGravity(Gravity.CENTER_VERTICAL);
//        taxbleView.addView(pdfHorizontalView1nNew3);
//
//
//        PDFHorizontalView pdfHorizontalView1nNew4 = new PDFHorizontalView(getApplicationContext());
//        LinearLayout.LayoutParams horizontalLayoutParam1nNew4 = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        horizontalLayoutParam1nNew4.setMargins(50, 0, 0, 0);
//
//        PDFTextView pdfTextView1New4 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL)
//                .setText("Gross :  ");
//        pdfTextView1New4.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
//        pdfHorizontalView1nNew4.addView(pdfTextView1New4);
//
//        PDFTextView pdfTextView2nNew4 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//        double gross = Double.parseDouble(pdfModelResponse.getSalesHeader().get(0).getTotal());
//        pdfTextView2nNew4.setText( String.format("%.02f", gross)).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));;
//        pdfTextView2nNew4.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        pdfHorizontalView1nNew4.setLayout(horizontalLayoutParam1nNew4);
//        pdfHorizontalView1nNew4.addView(pdfTextView2nNew4);
//        pdfHorizontalView1nNew4.getView().setGravity(Gravity.CENTER_VERTICAL);
//        taxbleView.addView(pdfHorizontalView1nNew4);
//        pdfBody.addView(taxbleView);
//        PDFLineSeparatorView lineSeparatorViewNew1nn = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(GRAY);
//        pdfBody.addView(lineSeparatorViewNew1nn);
//
//        PDFHorizontalView taxbleView2 = new PDFHorizontalView(getApplicationContext());
//
//        double discAmt = Double.parseDouble(pdfModelResponse.getSalesHeader().get(0).getDiscount());
//        PDFTextView taxableValue3 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL)
//                .setText("DisAmt :" + String.format("%.02f", discAmt)).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        taxableValue3.setLayout(verticalLayoutParam8);
////        taxableValue3.getView().setGravity(Gravity.CENTER_VERTICAL);
//        taxbleView2.addView(taxableValue3);
//
//
//
//        PDFTextView taxableValue4 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL)
//                .setText("1 HC equal 1 Rupee").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        taxableValue4.setLayout(verticalLayoutParam8);
////        taxableValue4.getView().setGravity(Gravity.CENTER_VERTICAL);
//        taxbleView2.addView(taxableValue4);
//
//        double netAmout = Double.parseDouble(pdfModelResponse.getSalesHeader().get(0).getNetTotal());
//        PDFTextView taxableValue6 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL)
//                .setText("You saved Rs.0.00 & Earned 50.35 HC's").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
//        taxableValue6.setLayout(verticalLayoutParam8);
////        taxableValue6.getView().setGravity(Gravity.CENTER_VERTICAL);
//        taxbleView2.addView(taxableValue6);
//        pdfBody.addView(taxbleView2);
//
//        PDFLineSeparatorView lineSeparatorView5 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(GRAY);
//        LinearLayout.LayoutParams layoutParams5 = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                1, 0);
//        layoutParams5.setMargins(0, 0, 0, 0);
//        lineSeparatorView5.setLayout(layoutParams5);
//        pdfBody.addView(lineSeparatorView5);
//
//
//        PDFHorizontalView footerView = new PDFHorizontalView(getApplicationContext());
//        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams5.setMargins(0, 0, 0, 0);
//        PDFTextView cinValue = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P)
//                .setText(("Rupees " + EnglishNumberToWords.convert(Math.round(Double.parseDouble(pdfModelResponse.getSalesHeader().get(0).getNetTotal()))) + " Only")).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
////        cinValue.setLayout(verticalLayoutParam1);
////        cinValue.getView().setGravity(Gravity.CENTER_VERTICAL);
//        footerView.addView(cinValue);
//
//
////         PDFHorizontalView pdfHorizontalView1nNewPaidAmount = new PDFHorizontalView(getApplicationContext());
////        LinearLayout.LayoutParams horizontalLayoutParam1nNew2PaidAmount = new LinearLayout.LayoutParams(
////                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
////        horizontalLayoutParam1nNew2PaidAmount.setMargins(0, 0, 0, 0);
////
////        PDFTextView pdfTextView1NewPaidAmount = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL)
////                .setText("Paid Amount :  ");
////        pdfTextView1NewPaidAmount.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
////        pdfHorizontalView1nNewPaidAmount.addView(pdfTextView1NewPaidAmount);
////
////        PDFTextView pdfTextView2nNewPaidAmount = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
////        pdfTextView2nNewPaidAmount.setText(" " + String.format("%.02f", netAmout)).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
////        pdfTextView2nNewPaidAmount.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
////        pdfHorizontalView1nNewPaidAmount.setLayout(horizontalLayoutParam1nNew2PaidAmount);
////        pdfHorizontalView1nNewPaidAmount.addView(pdfTextView2nNewPaidAmount);
////        footerView.addView(pdfHorizontalView1nNewPaidAmount);
//
//
//        PDFTextView regsteredOffice = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//        regsteredOffice.setPadding(300, 0, 0, 0);
//        regsteredOffice.setText("Paid Amount: ").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
////        regsteredOffice.setLayout(new LinearLayout.LayoutParams(
////                0,
////                LinearLayout.LayoutParams.WRAP_CONTENT, 2));
////        regsteredOffice.getView().setGravity(Gravity.CENTER_VERTICAL);
//        footerView.addView(regsteredOffice);
//        footerView.setLayout(layout);
//
//        PDFTextView paidAmount = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//        paidAmount.setPadding(0, 1, 0, 0);
//        paidAmount.setText(String.format("%.02f", netAmout)).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
////        regsteredOffice.setLayout(new LinearLayout.LayoutParams(
////                0,
////                LinearLayout.LayoutParams.WRAP_CONTENT, 2));
////        regsteredOffice.getView().setGravity(Gravity.CENTER_VERTICAL);
//        footerView.addView(paidAmount);
//        footerView.setLayout(layout);
//
//        pdfBody.addView(footerView);
//        PDFLineSeparatorView lineSeparatorViewNew1 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(GRAY);
//        LinearLayout.LayoutParams layoutParamsNew1 = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                1, 0);
//        layoutParamsNew1.setMargins(0, 2, 0, 5);
//        lineSeparatorViewNew1.setLayout(layoutParamsNew1);
//        pdfBody.addView(lineSeparatorViewNew1);
//
//        PDFVerticalView verticalViewFirstWishes = new PDFVerticalView(getApplicationContext());
//
//        PDFHorizontalView apolloWishesView = new PDFHorizontalView(getApplicationContext());
//
//
//        PDFTextView wishing = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P)
//                .setText("Wishing you speed recovery\n\nQR Code was digitally\ndisplayed to the customer\nat the time of transaction").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        wishing.setLayout(verticalLayoutParam8);
//        taxableValue3.getView().setGravity(Gravity.CENTER_VERTICAL);
//        apolloWishesView.addView(wishing);
//
//
//
//        PDFTextView taxableValue4Scan = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//        LinearLayout.LayoutParams headerImageLayoutParamScan = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        taxableValue4Scan.setLayout(headerImageLayoutParamScan);
//        taxableValue4Scan.setText("Scan QR Code\nfor Refill/Reorder").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        taxableValue4Scan.setLayout(verticalLayoutParam8);
//        taxableValue4.getView().setGravity(Gravity.CENTER_VERTICAL);
//        apolloWishesView.addView(taxableValue4Scan);
//
//
//
////        PDFVerticalView verticalFooterScanImage = new PDFVerticalView(getApplicationContext());
////        LinearLayout.LayoutParams headerImageLayoutParam = new LinearLayout.LayoutParams(
////                90,
////                90, 0);
//        PDFImageView imageView = new PDFImageView(getApplicationContext());
//        LinearLayout.LayoutParams headerImageLayoutParam = new LinearLayout.LayoutParams(
//                100,
//                100);
//        imageView.setLayout(headerImageLayoutParam);
//        imageView.setImageScale(ImageView.ScaleType.FIT_XY);
//        try {
//            imageView.setImageBitmap(encodeAsBitmap(pdfModelResponse));
//        }catch (Exception e){
//
//        }
//        headerImageLayoutParam.setMargins(0, 0, 50, 0);
////        verticalFooterScanImage.addView(imageView);
////        verticalFooterScanImage.setLayout(headerImageLayoutParam);
//        apolloWishesView.addView(imageView);
//
//
//
//        PDFVerticalView apolloWishesView2 = new PDFVerticalView(getApplicationContext());
//        LinearLayout.LayoutParams verticalFooter2 = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        verticalFooter2.setMargins(0, 0, 0, 0);
//
//        PDFHorizontalView forApolloPahramacyHorzontalView = new PDFHorizontalView(getApplicationContext());
//        LinearLayout.LayoutParams forApolloPahramacyHorzontalViewMargins = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        forApolloPahramacyHorzontalViewMargins.setMargins(0, 0, 0, 0);
//
//
//        pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//        SpannableString word1 = new SpannableString("APOLLO PHARMACY");
//        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
//        word1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, word1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        pdfTextView1.setText("for ").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        forApolloPahramacyHorzontalView.addView(pdfTextView1);
//
//
//        pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
////        SpannableString word2 = new SpannableString("APOLLO PHARMACY");
////        StyleSpan boldSpan1 = new StyleSpan(Typeface.BOLD);
////        word1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, word1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        pdfTextView2.setText("APOLLO PHARMACY").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
//        forApolloPahramacyHorzontalView.addView(pdfTextView2);
//        apolloWishesView2.addView(forApolloPahramacyHorzontalView);
//
//
//        pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL)
//                .setText("Registered Pharmacist").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        apolloWishesView2.addView(pdfTextView2);
//        apolloWishesView2.setLayout(verticalFooter2);
//        apolloWishesView2.getView().setGravity(Gravity.CENTER_VERTICAL);
//        apolloWishesView.addView(apolloWishesView2);
//        verticalViewFirstWishes.addView(apolloWishesView);
//
//        PDFHorizontalView horizontalViewInvoiceSS = new PDFHorizontalView(getApplicationContext());
//        LinearLayout.LayoutParams layoutParamsNew1Text = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT, 0);
//        layoutParamsNew1Text.setMargins(0, 10, 0, 0);
////        PDFLineSeparatorViewVertical lineSeparatorView1verticalInvoice = new PDFLineSeparatorViewVertical(getApplicationContext()).setBackgroundColor(GRAY);
////        horizontalViewInvoice.addView(lineSeparatorView1verticalInvoice);
//
//        PDFTextView pdfTextViewNew = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//
//        pdfTextViewNew.setText("E & O.E Goods Once Sold Cannot Be Taken Back or Exchanges | INSULINS AND VACCINES WILL NOT BE TAKEN BACK | EMERGENCY CALL:1066 | Tollfree No: 1860-500-0101");
//        pdfTextViewNew.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
////        pdfTextViewNew.getView().setGravity(Gravity.CENTER);
//        horizontalViewInvoiceSS.setLayout(layoutParamsNew1Text);
//        horizontalViewInvoiceSS.addView(pdfTextViewNew);
//
////        PDFLineSeparatorViewVertical lineSeparatorView1verticalInvoice2 = new PDFLineSeparatorViewVertical(getApplicationContext()).setBackgroundColor(GRAY);
////        horizontalViewInvoice.addView(lineSeparatorView1verticalInvoice2);
//
//
//
//        verticalViewFirstWishes.addView(horizontalViewInvoiceSS);
//
//
//        pdfBody.addView(verticalViewFirstWishes);
//        PDFLineSeparatorView lineSeparatorView1nf = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(GRAY);
//        LinearLayout.LayoutParams layoutParamsf = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                1, 0);
//        layoutParamsf.setMargins(0, 5, 0, 5);
//        lineSeparatorView1nf.setLayout(layoutParamsf);
//        pdfBody.addView(lineSeparatorView1nf);

//        pdfBody.addView(lineSeparatorView1nf);
//        pdfBody.addView(apolloWishesView);

        return pdfBody;
    }

    @Override
    protected PDFFooterView getFooterView(int forPage, PdfModelResponse pdfModelResponse) {
        PDFFooterView pdffooterVieww = new PDFFooterView(getApplicationContext());
        PDFHorizontalView taxbleView = new PDFHorizontalView(getApplicationContext());
        double taxbleValue = 0.0;
        for (int i = 0; i < pdfModelResponse.getSalesLine().size(); i++) {
            if (pdfModelResponse.getSalesLine().get(i).getTaxable() != null && !pdfModelResponse.getSalesLine().get(i).getTaxable().isEmpty()) {
                taxbleValue = taxbleValue + Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getTaxable());

            }
        }
        LinearLayout.LayoutParams verticalLayoutParam8 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        verticalLayoutParam8.setMargins(0, 0, 0, 0);


//        PDFTextView taxableValue = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL)
//                .setText("TAXABLE VALUE: " + String.format("%.02f", taxbleValue)).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        taxableValue.setLayout(verticalLayoutParam8);
//        taxableValue.getView().setGravity(Gravity.CENTER_VERTICAL);
//        taxbleView.addView(taxableValue);

        PDFHorizontalView pdfHorizontalView1nNew1 = new PDFHorizontalView(getApplicationContext());
        LinearLayout.LayoutParams horizontalLayoutParam1nNew1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalLayoutParam1nNew1.setMargins(0, 0, 0, 0);

        PDFTextView pdfTextView1New1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText("TAXABLE VALUE:  ");
        pdfTextView1New1.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        pdfHorizontalView1nNew1.addView(pdfTextView1New1);

        PDFTextView pdfTextView2nNew1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText(String.format("%.02f", taxbleValue));
        pdfTextView2nNew1.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        pdfHorizontalView1nNew1.setLayout(horizontalLayoutParam1nNew1);
        pdfHorizontalView1nNew1.addView(pdfTextView2nNew1);
        pdfHorizontalView1nNew1.getView().setGravity(Gravity.CENTER_VERTICAL);
        taxbleView.addView(pdfHorizontalView1nNew1);

        PDFHorizontalView pdfHorizontalView1nNew2 = new PDFHorizontalView(getApplicationContext());
        LinearLayout.LayoutParams horizontalLayoutParam1nNew2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalLayoutParam1nNew2.setMargins(50, 0, 0, 0);

        PDFTextView pdfTextView1New2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText("CGstAMT :  ");
        pdfTextView1New2.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        pdfHorizontalView1nNew2.addView(pdfTextView1New2);

        PDFTextView pdfTextView2nNew2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        double cgstAmount = 0.0;
        for (int i = 0; i < pdfModelResponse.getSalesLine().size(); i++) {
            if (pdfModelResponse.getSalesLine().get(i).getTaxable() != null && !pdfModelResponse.getSalesLine().get(i).getTaxable().isEmpty()

                    && pdfModelResponse.getSalesLine().get(i).getCGSTPer() != null && !pdfModelResponse.getSalesLine().get(i).getCGSTPer().isEmpty()) {
//                cgstAmount = cgstAmount + ((Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getMrp()) * Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getQty()) * Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getCGSTPer())) / 100);
                cgstAmount = cgstAmount + ((Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getTaxable()) * Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getCGSTPer())) / 100);

            }
        }
        pdfTextView2nNew2.setText(String.format("%.02f", cgstAmount)).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        ;
        pdfTextView2nNew2.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        pdfHorizontalView1nNew2.setLayout(horizontalLayoutParam1nNew2);
        pdfHorizontalView1nNew2.addView(pdfTextView2nNew2);
        pdfHorizontalView1nNew2.getView().setGravity(Gravity.CENTER_VERTICAL);
        taxbleView.addView(pdfHorizontalView1nNew2);


        PDFHorizontalView pdfHorizontalView1nNew3 = new PDFHorizontalView(getApplicationContext());
        LinearLayout.LayoutParams horizontalLayoutParam1nNew3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalLayoutParam1nNew3.setMargins(50, 0, 0, 0);

        PDFTextView pdfTextView1New3 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText("SGstAMT :  ");
        pdfTextView1New3.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        pdfHorizontalView1nNew3.addView(pdfTextView1New3);

        PDFTextView pdfTextView2nNew3 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        double sgstAmount = 0.0;
        for (int i = 0; i < pdfModelResponse.getSalesLine().size(); i++) {
            if (pdfModelResponse.getSalesLine().get(i).getTaxable() != null && !pdfModelResponse.getSalesLine().get(i).getTaxable().isEmpty() && pdfModelResponse.getSalesLine().get(i).getSGSTPer() != null && !pdfModelResponse.getSalesLine().get(i).getSGSTPer().isEmpty()) {
                sgstAmount = sgstAmount + ((Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getTaxable()) * Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getSGSTPer())) / 100);

//                sgstAmount = sgstAmount + ((Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getMrp()) * Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getQty()) * Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getSGSTPer())) / 100);

            }
        }
        pdfTextView2nNew3.setText(String.format("%.02f", sgstAmount)).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        ;
        pdfTextView2nNew3.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        pdfHorizontalView1nNew3.setLayout(horizontalLayoutParam1nNew3);
        pdfHorizontalView1nNew3.addView(pdfTextView2nNew3);
        pdfHorizontalView1nNew3.getView().setGravity(Gravity.CENTER_VERTICAL);
        taxbleView.addView(pdfHorizontalView1nNew3);


        PDFHorizontalView pdfHorizontalView1nNew4 = new PDFHorizontalView(getApplicationContext());
        LinearLayout.LayoutParams horizontalLayoutParam1nNew4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalLayoutParam1nNew4.setMargins(50, 0, 0, 0);

        PDFTextView pdfTextView1New4 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText("Gross :  ");
        pdfTextView1New4.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        pdfHorizontalView1nNew4.addView(pdfTextView1New4);

        PDFTextView pdfTextView2nNew4 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        double gross = Double.parseDouble(pdfModelResponse.getSalesHeader().get(0).getTotal());
        pdfTextView2nNew4.setText(String.format("%.02f", gross)).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        ;
        pdfTextView2nNew4.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        pdfHorizontalView1nNew4.setLayout(horizontalLayoutParam1nNew4);
        pdfHorizontalView1nNew4.addView(pdfTextView2nNew4);
        pdfHorizontalView1nNew4.getView().setGravity(Gravity.CENTER_VERTICAL);
        taxbleView.addView(pdfHorizontalView1nNew4);
        pdffooterVieww.addView(taxbleView);
        PDFLineSeparatorView lineSeparatorViewNew1nn = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(GRAY);
        pdffooterVieww.addView(lineSeparatorViewNew1nn);

        PDFHorizontalView taxbleView2 = new PDFHorizontalView(getApplicationContext());

        double discAmt = Double.parseDouble(pdfModelResponse.getSalesHeader().get(0).getDiscount());
        PDFTextView taxableValue3 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText("DisAmt :" + String.format("%.02f", discAmt)).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        taxableValue3.setLayout(verticalLayoutParam8);
//        taxableValue3.getView().setGravity(Gravity.CENTER_VERTICAL);
        taxbleView2.addView(taxableValue3);


        PDFTextView taxableValue4 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText("1 HC equal 1 Rupee").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        taxableValue4.setLayout(verticalLayoutParam8);
//        taxableValue4.getView().setGravity(Gravity.CENTER_VERTICAL);
        taxbleView2.addView(taxableValue4);

        double netAmout = Double.parseDouble(pdfModelResponse.getSalesHeader().get(0).getNetTotal());
        PDFTextView taxableValue6 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText("You saved Rs.0.00 & Earned 50.35 HC's").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        taxableValue6.setLayout(verticalLayoutParam8);
//        taxableValue6.getView().setGravity(Gravity.CENTER_VERTICAL);
        taxbleView2.addView(taxableValue6);
        pdffooterVieww.addView(taxbleView2);

        PDFLineSeparatorView lineSeparatorView5 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(GRAY);
        LinearLayout.LayoutParams layoutParams5 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1, 0);
        layoutParams5.setMargins(0, 0, 0, 0);
        lineSeparatorView5.setLayout(layoutParams5);
        pdffooterVieww.addView(lineSeparatorView5);


        PDFHorizontalView footerView = new PDFHorizontalView(getApplicationContext());
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams5.setMargins(0, 0, 0, 0);
        PDFTextView cinValue = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P).setText(("Rupees " + EnglishNumberToWords.convert(Math.round(Double.parseDouble(pdfModelResponse.getSalesHeader().get(0).getNetTotal()))) + " Only")).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
//        cinValue.setLayout(verticalLayoutParam1);
//        cinValue.getView().setGravity(Gravity.CENTER_VERTICAL);
        footerView.addView(cinValue);


//         PDFHorizontalView pdfHorizontalView1nNewPaidAmount = new PDFHorizontalView(getApplicationContext());
//        LinearLayout.LayoutParams horizontalLayoutParam1nNew2PaidAmount = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        horizontalLayoutParam1nNew2PaidAmount.setMargins(0, 0, 0, 0);
//
//        PDFTextView pdfTextView1NewPaidAmount = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL)
//                .setText("Paid Amount :  ");
//        pdfTextView1NewPaidAmount.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
//        pdfHorizontalView1nNewPaidAmount.addView(pdfTextView1NewPaidAmount);
//
//        PDFTextView pdfTextView2nNewPaidAmount = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//        pdfTextView2nNewPaidAmount.setText(" " + String.format("%.02f", netAmout)).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        pdfTextView2nNewPaidAmount.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        pdfHorizontalView1nNewPaidAmount.setLayout(horizontalLayoutParam1nNew2PaidAmount);
//        pdfHorizontalView1nNewPaidAmount.addView(pdfTextView2nNewPaidAmount);
//        footerView.addView(pdfHorizontalView1nNewPaidAmount);


        PDFTextView regsteredOffice = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        regsteredOffice.setPadding(300, 0, 0, 0);
        regsteredOffice.setText("Paid Amount: ").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
//        regsteredOffice.setLayout(new LinearLayout.LayoutParams(
//                0,
//                LinearLayout.LayoutParams.WRAP_CONTENT, 2));
//        regsteredOffice.getView().setGravity(Gravity.CENTER_VERTICAL);
        footerView.addView(regsteredOffice);
        footerView.setLayout(layout);

        PDFTextView paidAmount = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        paidAmount.setPadding(0, 1, 0, 0);
        paidAmount.setText(String.format("%.02f", netAmout)).setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        regsteredOffice.setLayout(new LinearLayout.LayoutParams(
//                0,
//                LinearLayout.LayoutParams.WRAP_CONTENT, 2));
//        regsteredOffice.getView().setGravity(Gravity.CENTER_VERTICAL);
        footerView.addView(paidAmount);
        footerView.setLayout(layout);

        pdffooterVieww.addView(footerView);
        PDFLineSeparatorView lineSeparatorViewNew1 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(GRAY);
        LinearLayout.LayoutParams layoutParamsNew1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1, 0);
        layoutParamsNew1.setMargins(0, 2, 0, 5);
        lineSeparatorViewNew1.setLayout(layoutParamsNew1);
        pdffooterVieww.addView(lineSeparatorViewNew1);

        PDFVerticalView verticalViewFirstWishes = new PDFVerticalView(getApplicationContext());

        PDFHorizontalView apolloWishesView = new PDFHorizontalView(getApplicationContext());


        PDFTextView wishing = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P).setText("Wishing you speed recovery\n\nQR Code was digitally\ndisplayed to the customer\nat the time of transaction").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        wishing.setLayout(verticalLayoutParam8);
        taxableValue3.getView().setGravity(Gravity.CENTER_VERTICAL);
        apolloWishesView.addView(wishing);


        PDFTextView taxableValue4Scan = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        LinearLayout.LayoutParams headerImageLayoutParamScan = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        taxableValue4Scan.setLayout(headerImageLayoutParamScan);
        taxableValue4Scan.setText("Scan QR Code\nfor Refill/Reorder").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        taxableValue4Scan.setLayout(verticalLayoutParam8);
        taxableValue4.getView().setGravity(Gravity.CENTER_VERTICAL);
        apolloWishesView.addView(taxableValue4Scan);


//        PDFVerticalView verticalFooterScanImage = new PDFVerticalView(getApplicationContext());
//        LinearLayout.LayoutParams headerImageLayoutParam = new LinearLayout.LayoutParams(
//                90,
//                90, 0);
        PDFImageView imageView = new PDFImageView(getApplicationContext());
        LinearLayout.LayoutParams headerImageLayoutParam = new LinearLayout.LayoutParams(100, 100);
        imageView.setLayout(headerImageLayoutParam);
        imageView.setImageScale(ImageView.ScaleType.FIT_XY);
        try {
            imageView.setImageBitmap(encodeAsBitmap(pdfModelResponse));
        } catch (Exception e) {

        }
        headerImageLayoutParam.setMargins(0, 0, 50, 0);
//        verticalFooterScanImage.addView(imageView);
//        verticalFooterScanImage.setLayout(headerImageLayoutParam);
        apolloWishesView.addView(imageView);


        PDFVerticalView apolloWishesView2 = new PDFVerticalView(getApplicationContext());
        LinearLayout.LayoutParams verticalFooter2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        verticalFooter2.setMargins(0, 0, 0, 0);

        PDFHorizontalView forApolloPahramacyHorzontalView = new PDFHorizontalView(getApplicationContext());
        LinearLayout.LayoutParams forApolloPahramacyHorzontalViewMargins = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        forApolloPahramacyHorzontalViewMargins.setMargins(0, 0, 0, 0);


        PDFTextView pdfTextView1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        SpannableString word1 = new SpannableString("APOLLO PHARMACY");
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        word1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, word1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        pdfTextView1.setText("for ").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        forApolloPahramacyHorzontalView.addView(pdfTextView1);


        PDFTextView pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
//        SpannableString word2 = new SpannableString("APOLLO PHARMACY");
//        StyleSpan boldSpan1 = new StyleSpan(Typeface.BOLD);
//        word1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, word1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        pdfTextView2.setText("APOLLO PHARMACY").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambriab));
        forApolloPahramacyHorzontalView.addView(pdfTextView2);
        apolloWishesView2.addView(forApolloPahramacyHorzontalView);


        pdfTextView2 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL).setText("Registered Pharmacist").setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
        apolloWishesView2.addView(pdfTextView2);
        apolloWishesView2.setLayout(verticalFooter2);
        apolloWishesView2.getView().setGravity(Gravity.CENTER_VERTICAL);
        apolloWishesView.addView(apolloWishesView2);
        verticalViewFirstWishes.addView(apolloWishesView);

        PDFHorizontalView horizontalViewInvoiceSS = new PDFHorizontalView(getApplicationContext());
        LinearLayout.LayoutParams layoutParamsNew1Text = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        layoutParamsNew1Text.setMargins(0, 10, 0, 0);
//        PDFLineSeparatorViewVertical lineSeparatorView1verticalInvoice = new PDFLineSeparatorViewVertical(getApplicationContext()).setBackgroundColor(GRAY);
//        horizontalViewInvoice.addView(lineSeparatorView1verticalInvoice);

        PDFTextView pdfTextViewNew = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);

        pdfTextViewNew.setText("E & O.E Goods Once Sold Cannot Be Taken Back or Exchanges | INSULINS AND VACCINES WILL NOT BE TAKEN BACK | EMERGENCY CALL:1066 | Tollfree No: 1860-500-0101");
        pdfTextViewNew.setTextTypeface(ResourcesCompat.getFont(getContext(), R.font.cambria));
//        pdfTextViewNew.getView().setGravity(Gravity.CENTER);
        horizontalViewInvoiceSS.setLayout(layoutParamsNew1Text);
        horizontalViewInvoiceSS.addView(pdfTextViewNew);

//        PDFLineSeparatorViewVertical lineSeparatorView1verticalInvoice2 = new PDFLineSeparatorViewVertical(getApplicationContext()).setBackgroundColor(GRAY);
//        horizontalViewInvoice.addView(lineSeparatorView1verticalInvoice2);


        verticalViewFirstWishes.addView(horizontalViewInvoiceSS);


        pdffooterVieww.addView(verticalViewFirstWishes);
        PDFLineSeparatorView lineSeparatorView1nf = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(GRAY);
        LinearLayout.LayoutParams layoutParamsf = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1, 0);
        layoutParamsf.setMargins(0, 5, 0, 5);
        lineSeparatorView1nf.setLayout(layoutParamsf);
        pdffooterVieww.addView(lineSeparatorView1nf);
        return pdffooterVieww;
    }

    private Bitmap encodeAsBitmap(PdfModelResponse pdfModelResponse) throws WriterException {

        String str = "CUSTOMERNAME: " + pdfModelResponse.getSalesHeader().get(0).getCustName() + "\nPHONE: " + pdfModelResponse.getSalesHeader().get(0).getCustMobile() + "\nBILL NO: " + pdfModelResponse.getSalesHeader().get(0).getReceiptId();
        for (PdfModelResponse.SalesLine salesLine : pdfModelResponse.getSalesLine()) {
            str = str + "\nITEMID: " + "- " + "QTY: " + salesLine.getQty();
        }
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(str, BarcodeFormat.QR_CODE, 90, 90);

        int w = bitMatrix.getWidth();
        int h = bitMatrix.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                pixels[y * w + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            openPdf();
            onSuccessPdfResponse(pdfModelResponse);
//            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    private void createPdf() throws IOException {
//        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

//        File file = new File(pdfPath, "pdfdoc.pdf");
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(), transactionId.concat(".pdf"));
        OutputStream outputStream = new FileOutputStream(file);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument, PageSize.A4);
        document.setMargins(15, 15, 15, 15);
        pageBreakCount = 0;
        createPdfPageWise(pdfDocument, document, false);
        document.close();
        if (isStoragePermissionGranted()) {
            openPdf();
        }
    }

    private void createPdfPageWise(PdfDocument pdfDocument, Document document, boolean isDuplicate) throws IOException {
        // declaring variables for loading the fonts from asset
        byte[] fontByte, boldByte;
        AssetManager am;
        am = this.getAssets();
//the file name should be same as in your assets folder
        try (InputStream inStream = am.open("font/cambria.ttf")) {
            fontByte = IOUtils.toByteArray(inStream);
        }

        try (InputStream inStream = am.open("font/cambriab.ttf")) {
            boldByte = IOUtils.toByteArray(inStream);

        }
        PdfFont font = PdfFontFactory.createFont(fontByte, PdfEncodings.WINANSI, true);
        PdfFont bold = PdfFontFactory.createFont(boldByte, PdfEncodings.WINANSI, true);
//        PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
//        PdfFont bold = PdfFontFactory.createFont(FontConstants.TIMES_BOLD);
////        PdfFont cam = PdfFontFactory.createFont(font_end, true);
//      PdfFont cam = PdfFontFactory.createFont("src\\main\\res\\font\\cambriab.ttf", true);

        float[] columnWidth1 = {100, 155, 155, 170};//580
//        float columnWidth1[] = {65, 165, 140, 190};
        Table table1 = new Table(columnWidth1);

        //table1.....row1.....
        Drawable apolloLogoDrawable = getDrawable(R.drawable.apollo_1525857827435);
        Bitmap apolloLogoBitMap = ((BitmapDrawable) apolloLogoDrawable).getBitmap();
        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        apolloLogoBitMap.compress(Bitmap.CompressFormat.PNG, 100, stream1);
        byte[] bitMapData1 = stream1.toByteArray();

        ImageData imageData1 = ImageDataFactory.create(bitMapData1);
        Image image1 = new Image(imageData1);
        image1.scaleToFit(50, 50);
        image1.setHeight(50);


        Border border1 = new SolidBorder(new DeviceRgb(199, 199, 199), 1);
        table1.setBorder(border1);
        table1.addCell(new Cell(4, 1).add(image1).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell(4, 1).add(new Paragraph(new Text("Apollo Pharmacy - ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font)).add(new Text(pdfModelResponse.getSalesHeader().get(0).getBranch()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).add(new Paragraph(new Text(pdfModelResponse.getSalesHeader().get(0).getAddressOne()).setFontSize(ITEXT_FONT_SIZE_SIX))).add(new Paragraph(new Text(pdfModelResponse.getSalesHeader().get(0).getAddressTwo()).setFontSize(ITEXT_FONT_SIZE_SIX))).add(new Paragraph(new Text("PHONE: ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text(pdfModelResponse.getSalesHeader().get(0).getTelNo()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell(4, 1).add(new Paragraph(new Text("FSSAI NO: ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text(pdfModelResponse.getSalesHeader().get(0).getFssaino()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).add(new Paragraph(new Text("D.L.NO: ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text(pdfModelResponse.getSalesHeader().get(0).getDlno()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).add(new Paragraph(new Text("GST NO : ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text(pdfModelResponse.getSalesHeader().get(0).getGstin()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(Border.NO_BORDER).add(new Paragraph(new Text("C.GSTIN NO : ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text(pdfModelResponse.getSalesHeader().get(0).getCgstin()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(Border.NO_BORDER));


        table1.addCell(new Cell(4, 1).add(new Paragraph(new Text("Registered Office: ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text("No.19 Bishop Gerden, Raja Annamalaipuram, Chennai-600028").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).add(new Paragraph(new Text("Admin Office: ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text("(For all correspondence) All towers, Floor No 55, Greams Road, Chennai-600006").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).add(new Paragraph(new Text("CIN : ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text("U52500TN2016PLC111328").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(Border.NO_BORDER));
        String duplicate = isDuplicate ? "Duplicate Copy of Invoice" : "";
        table1.addCell(new Cell(1, 2).add(new Paragraph(new Text(duplicate).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font)).setMarginLeft(10)).setBorder(Border.NO_BORDER));

        table1.addCell(new Cell(1, 2).add(new Paragraph(new Text("INVOICE").setFontSize(ITEXT_FONT_SIZE_EIGHT).setFont(bold)).setMarginLeft(10)).setBorder(Border.NO_BORDER));

        float[] columnWidth2 = {170, 120, 120, 170};// 580
//        float columnWidth2[] = {150, 130, 115, 165};
        Table table2 = new Table(columnWidth2);
        Border border2 = new SolidBorder(new DeviceRgb(199, 199, 199), 1);
        table2.setBorder(border2);
//        table2.setBorder(new SolidBorder(1));
        table2.addCell(new Cell().add(new Paragraph(new Text("Name : ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text(pdfModelResponse.getSalesHeader().get(0).getCustName()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(Border.NO_BORDER));
        table2.addCell(new Cell().add(new Paragraph(new Text("Mobile No. : ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text(pdfModelResponse.getSalesHeader().get(0).getCustMobile()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(Border.NO_BORDER));
        table2.addCell(new Cell().add(new Paragraph(new Text("Bill No. : ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text(pdfModelResponse.getSalesHeader().get(0).getReceiptId()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(Border.NO_BORDER));
        table2.addCell(new Cell().add(new Paragraph(new Text("Corp: ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text(pdfModelResponse.getSalesHeader().get(0).getCorporate()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(Border.NO_BORDER));

        float[] columnWidth3 = {190, 133, 102, 155};//580
//        float columnWidth3[] = {180, 130, 100, 150};
        Table table3 = new Table(columnWidth3);
        Border border3 = new SolidBorder(new DeviceRgb(199, 199, 199), 1);
        table3.setBorder(border3);
//        table3.setBorder(new SolidBorder(1));
        if (pdfModelResponse.getSalesHeader().get(0).getDoctorName().equalsIgnoreCase("")) {
            table3.addCell(new Cell().add(new Paragraph(new Text("Doctor : ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text("--").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(Border.NO_BORDER));
        } else {
            table3.addCell(new Cell().add(new Paragraph(new Text("Doctor : ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text(pdfModelResponse.getSalesHeader().get(0).getDoctorName()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(Border.NO_BORDER));
        }

        table3.addCell(new Cell().add(new Paragraph(new Text("Ref. NO : ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text(pdfModelResponse.getSalesHeader().get(0).getRefNo()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(Border.NO_BORDER));
        table3.addCell(new Cell().add(new Paragraph(new Text("TID : ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text(pdfModelResponse.getSalesHeader().get(0).getTerminalId()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(Border.NO_BORDER));
        table3.addCell(new Cell().add(new Paragraph(new Text("Bill Date : ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text(pdfModelResponse.getSalesHeader().get(0).getTransDate()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(Border.NO_BORDER));

        float[] columnWidth4 = {40, 30, 150, 20, 50, 35, 55, 55, 55, 50, 40};//580
//        float columnWidth4[] = {41, 31, 136, 21, 51, 36, 51, 51, 51, 51, 41};
        Table table4 = new Table(columnWidth4);
        Border border4 = new SolidBorder(new DeviceRgb(192, 192, 192), 1);
        table4.setBorder(border4);
        table4.addCell(new Cell().add(new Paragraph(new Text("Rack").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(border4));
        table4.addCell(new Cell().add(new Paragraph(new Text("QTY").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(border4));
        table4.addCell(new Cell().add(new Paragraph(new Text("Product Name").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(border4));
        table4.addCell(new Cell().add(new Paragraph(new Text("Sch").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(border4));
        table4.addCell(new Cell().add(new Paragraph(new Text("HSN Code").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(border4));
        table4.addCell(new Cell().add(new Paragraph(new Text("Mfg").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(border4));
        table4.addCell(new Cell().add(new Paragraph(new Text("Batch").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(border4));
        table4.addCell(new Cell().add(new Paragraph(new Text("Expiry").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(border4));
        table4.addCell(new Cell().add(new Paragraph(new Text("MRP").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(border4));
        table4.addCell(new Cell().add(new Paragraph(new Text("Amount").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(border4));
        table4.addCell(new Cell().add(new Paragraph(new Text("GST%").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(border4));
        for (int i = pageBreakCount; i < pdfModelResponse.getSalesLine().size(); i++) {
            PdfModelResponse.SalesLine salesLine = pdfModelResponse.getSalesLine().get(i);
            pageBreakCount++;
            table4.addCell(new Cell().add(new Paragraph(new Text(pdfModelResponse.getSalesLine().get(i).getRackId()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(border4));
            table4.addCell(new Cell().add(new Paragraph(new Text(salesLine.getQty()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(border4));
            String itemName = salesLine.getItemName().replace(" ", "\u00A0");
            table4.addCell(new Cell().add(new Paragraph(new Text(itemName).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(border4));
            table4.addCell(new Cell().add(new Paragraph(new Text(salesLine.getSch()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(border4));
            table4.addCell(new Cell().add(new Paragraph(new Text(salesLine.getHSNCode()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(border4));
            table4.addCell(new Cell().add(new Paragraph(new Text(salesLine.getManufacturer()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(border4));
            String batchNo = "";
            if (salesLine.getBatchNo().length() > 12) {
                batchNo = salesLine.getBatchNo().substring(0, 11);
                batchNo = batchNo + "\n" + salesLine.getBatchNo().substring(12);
            } else {
                batchNo = salesLine.getBatchNo();
            }
            table4.addCell(new Cell().add(new Paragraph(new Text(batchNo).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(border4));
            if (salesLine.getExpDate() != null && salesLine.getExpDate().length() > 5) {
                String expDate[] = salesLine.getExpDate().substring(2, 7).split("-");
                table4.addCell(new Cell().add(new Paragraph(new Text(expDate[1] + "-" + expDate[0]).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(border4));
            } else {
                table4.addCell(new Cell().add(new Paragraph(new Text("--").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(border4));
            }
            table4.addCell(new Cell().add(new Paragraph(new Text(salesLine.getMrp()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(border4));
            table4.addCell(new Cell().add(new Paragraph(new Text(salesLine.getLineTotAmount()).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(border4));
            Double gst = Double.parseDouble(salesLine.getSGSTPer()) + Double.parseDouble(salesLine.getCGSTPer());
            table4.addCell(new Cell().add(new Paragraph(new Text(String.format("%.02f", gst)).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(border4));
            if (pageBreakCount % 9 == 0) {
                break;
            }

        }

        float[] columnWidth5 = {144, 170, 122, 144};//580
//        float columnWidth5[] = {140, 160, 120, 140};
        Table table5 = new Table(columnWidth5);
        Border border5 = new SolidBorder(new DeviceRgb(192, 192, 192), 1);
        table5.setBorder(border5);
//        table5.setBorder(new SolidBorder(1));
        double taxbleValue = 0.0;
        for (int i = 0; i < pdfModelResponse.getSalesLine().size(); i++) {
            if (pdfModelResponse.getSalesLine().get(i).getTaxable() != null && !pdfModelResponse.getSalesLine().get(i).getTaxable().isEmpty()) {
                taxbleValue = taxbleValue + Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getTaxable());

            }
        }
        table5.addCell(new Cell().add(new Paragraph(new Text("Taxable Value : ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text(String.format("%.02f", taxbleValue)).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(Border.NO_BORDER));
        double cgstAmount = 0.0;
        for (int i = 0; i < pdfModelResponse.getSalesLine().size(); i++) {
            if (pdfModelResponse.getSalesLine().get(i).getTaxable() != null && !pdfModelResponse.getSalesLine().get(i).getTaxable().isEmpty()

                    && pdfModelResponse.getSalesLine().get(i).getCGSTPer() != null && !pdfModelResponse.getSalesLine().get(i).getCGSTPer().isEmpty()) {
//                cgstAmount = cgstAmount + ((Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getMrp()) * Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getQty()) * Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getCGSTPer())) / 100);
                cgstAmount = cgstAmount + ((Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getTaxable()) * Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getCGSTPer())) / 100);

            }
        }
        table5.addCell(new Cell().add(new Paragraph(new Text("CGST Amount: ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text(String.format("%.02f", cgstAmount)).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(Border.NO_BORDER));
        double sgstAmount = 0.0;
        for (int i = 0; i < pdfModelResponse.getSalesLine().size(); i++) {
            if (pdfModelResponse.getSalesLine().get(i).getTaxable() != null && !pdfModelResponse.getSalesLine().get(i).getTaxable().isEmpty() && pdfModelResponse.getSalesLine().get(i).getSGSTPer() != null && !pdfModelResponse.getSalesLine().get(i).getSGSTPer().isEmpty()) {
                sgstAmount = sgstAmount + ((Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getTaxable()) * Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getSGSTPer())) / 100);
                //                sgstAmount = sgstAmount + ((Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getMrp()) * Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getQty()) * Double.parseDouble(pdfModelResponse.getSalesLine().get(i).getSGSTPer())) / 100);

            }
        }
        table5.addCell(new Cell().add(new Paragraph(new Text("SGST Amount : ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text(String.format("%.02f", sgstAmount)).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(Border.NO_BORDER));
        double gross = Double.parseDouble(pdfModelResponse.getSalesHeader().get(0).getTotal());
        table5.addCell(new Cell().add(new Paragraph(new Text("Gross Amount : ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text(String.format("%.02f", gross)).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(Border.NO_BORDER));

        float[] columnWidth6 = {144, 170, 122, 144};//580
//        float columnWidth6[] = {140, 160, 120, 140};
        Table table6 = new Table(columnWidth6);
        Border border6 = new SolidBorder(new DeviceRgb(192, 192, 192), 1);
        table6.setBorder(border6);
//        table6.setBorder(new SolidBorder(1));
        table6.addCell(new Cell().add(new Paragraph(new Text("Donation: ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text("0.00").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font))).setBorder(Border.NO_BORDER));
        table6.addCell(new Cell().add(new Paragraph(new Text("*1 HC equal to 1 Rupee").setFontSize(ITEXT_FONT_SIZE_SIX))).setBorder(Border.NO_BORDER));
        table6.addCell(new Cell(1, 2).add(new Paragraph(new Text("* You Saved Rs.0.00 & Earned 50.35 HC's").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(new DashedBorder(1)));


        float[] columnWidth7 = {290, 290};//580
//        float columnWidth7[] = {280, 280};
        Table table7 = new Table(columnWidth7);
        Border border7 = new SolidBorder(new DeviceRgb(192, 192, 192), 1);
        table7.setBorder(border7);
//        table7.setBorder(new SolidBorder(1));
        double netAmout = Double.parseDouble(pdfModelResponse.getSalesHeader().get(0).getNetTotal());
        table7.addCell(new Cell().add(new Paragraph(new Text(("Rupees " + EnglishNumberToWords.convert(Math.round(Double.parseDouble(pdfModelResponse.getSalesHeader().get(0).getNetTotal()))) + " Only")).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(Border.NO_BORDER).setHorizontalAlignment(HorizontalAlignment.CENTER).setTextAlignment(TextAlignment.CENTER));
        table7.addCell(new Cell().add(new Paragraph(new Text("Paid Amount : ").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold)).add(new Text(String.format("%.02f", netAmout)).setFontSize(ITEXT_FONT_SIZE_SIX).setFont(font)).setMarginLeft(150)).setBorder(Border.NO_BORDER));


        float[] columnWidth8 = {167, 123, 123, 167};//580
//        float columnWidth8[] = {160, 120, 120, 160};
        Table table8 = new Table(columnWidth8);
        Border border8 = new SolidBorder(new DeviceRgb(192, 192, 192), 1);
        table8.setBorder(border8);
//        table8.setBorder(new SolidBorder(1));

        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        try {
            encodeAsBitmap(pdfModelResponse).compress(Bitmap.CompressFormat.PNG, 100, stream2);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        byte[] bitMapData2 = stream2.toByteArray();

        ImageData imageData2 = ImageDataFactory.create(bitMapData2);
        Image image2 = new Image(imageData2);
        image2.scaleToFit(50, 50);
        image2.setHeight(50);


        table8.addCell(new Cell().add(new Paragraph(new Text("Wishing You Speedy Recovery").setFontSize(ITEXT_FONT_SIZE_SIX))).setBorder(Border.NO_BORDER).setHorizontalAlignment(HorizontalAlignment.CENTER).setTextAlignment(TextAlignment.CENTER));
        table8.addCell(new Cell().add(new Paragraph(new Text(""))).setBorder(Border.NO_BORDER));
        table8.addCell(new Cell(4, 1).add(image2).setBorder(Border.NO_BORDER));
        table8.addCell(new Cell().add(new Paragraph(new Text(""))).setBorder(Border.NO_BORDER));

        table8.addCell(new Cell().add(new Paragraph(new Text(""))).setBorder(Border.NO_BORDER));
        table8.addCell(new Cell().add(new Paragraph(new Text(""))).setBorder(Border.NO_BORDER));
        table8.addCell(new Cell().add(new Paragraph(new Text(""))).setBorder(Border.NO_BORDER));

        table8.addCell(new Cell().add(new Paragraph(new Text("QR Code was digitally displayed for the").setFontSize(ITEXT_FONT_SIZE_SIX))).setBorder(Border.NO_BORDER).setHorizontalAlignment(HorizontalAlignment.CENTER).setTextAlignment(TextAlignment.CENTER));
        table8.addCell(new Cell(2, 1).add(new Paragraph(new Text("Scan QR Code For\nRefill/Reorder").setFontSize(ITEXT_FONT_SIZE_SIX))).setBorder(Border.NO_BORDER).setHorizontalAlignment(HorizontalAlignment.CENTER).setTextAlignment(TextAlignment.CENTER));
        table8.addCell(new Cell().add(new Paragraph(new Text("For ").setFontSize(8).setFont(font)).add(new Text("APOLLO PHARMACY").setFontSize(ITEXT_FONT_SIZE_SIX).setFont(bold))).setBorder(Border.NO_BORDER));

        table8.addCell(new Cell().add(new Paragraph(new Text("Customer at the time of the transaction").setFontSize(ITEXT_FONT_SIZE_SIX))).setBorder(Border.NO_BORDER).setHorizontalAlignment(HorizontalAlignment.CENTER).setTextAlignment(TextAlignment.CENTER));
        table8.addCell(new Cell().add(new Paragraph(new Text("Registered pharmacist").setFontSize(ITEXT_FONT_SIZE_SIX))).setBorder(Border.NO_BORDER));

        table8.addCell(new Cell(1, 4).add(new Paragraph(new Text("E & O.E Goods Once Sold Cannot Be Taken Back or Exchanges | INSULINS AND VACCINES WILL NOT BE TAKEN BACK | EMERGENCY CALL:1066 | Tollfree No: 1860-500-0101").setFontSize(ITEXT_FONT_SIZE_SIX))).setBorder(Border.NO_BORDER).setHorizontalAlignment(HorizontalAlignment.CENTER).setTextAlignment(TextAlignment.CENTER));


        document.add(table1);
        document.add(table2);
        document.add(table3);
        document.add(table4);
        document.add(table5);
        document.add(table6);
        document.add(table7);
        document.add(table8);
        if (pageBreakCount != pdfModelResponse.getSalesLine().size()) {
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            createPdfPageWise(pdfDocument, document, isDuplicate);
        } else {
            if (!isDuplicate && duplicateCheckboxChecked) {
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                pageBreakCount = 0;
                createPdfPageWise(pdfDocument, document, true);
            }
        }
    }


    public class OrderinProgresssuiModel {
        private String pharmaCount;
        private String fmcgCount;
        private String fmcgTotal;
        private String pharmaTotal;
        private String totalMedicineCount;
        private String medicineTotal;
        private boolean isFmcgPharma;
        private boolean isFmcg;
        private boolean isPharma;
        private boolean isPharmaHomeDelivery;
        private boolean isFmcgHomeDelivery;

        public String getPharmaCount() {
            return pharmaCount;
        }

        public void setPharmaCount(String pharmaCount) {
            this.pharmaCount = pharmaCount;
        }

        public String getFmcgCount() {
            return fmcgCount;
        }

        public void setFmcgCount(String fmcgCount) {
            this.fmcgCount = fmcgCount;
        }

        public String getFmcgTotal() {
            return fmcgTotal;
        }

        public void setFmcgTotal(String fmcgTotal) {
            this.fmcgTotal = fmcgTotal;
        }

        public String getPharmaTotal() {
            return pharmaTotal;
        }

        public void setPharmaTotal(String pharmaTotal) {
            this.pharmaTotal = pharmaTotal;
        }

        public String getTotalMedicineCount() {
            return totalMedicineCount;
        }

        public void setTotalMedicineCount(String totalMedicineCount) {
            this.totalMedicineCount = totalMedicineCount;
        }

        public String getMedicineTotal() {
            return medicineTotal;
        }

        public void setMedicineTotal(String medicineTotal) {
            this.medicineTotal = medicineTotal;
        }

        public boolean isFmcgPharma() {
            return isFmcgPharma;
        }

        public void setFmcgPharma(boolean fmcgPharma) {
            isFmcgPharma = fmcgPharma;
        }

        public boolean isFmcg() {
            return isFmcg;
        }

        public void setFmcg(boolean fmcg) {
            isFmcg = fmcg;
        }

        public boolean isPharma() {
            return isPharma;
        }

        public void setPharma(boolean pharma) {
            isPharma = pharma;
        }

        public boolean isPharmaHomeDelivery() {
            return isPharmaHomeDelivery;
        }

        public void setPharmaHomeDelivery(boolean pharmaHomeDelivery) {
            isPharmaHomeDelivery = pharmaHomeDelivery;
        }

        public boolean isFmcgHomeDelivery() {
            return isFmcgHomeDelivery;
        }

        public void setFmcgHomeDelivery(boolean fmcgHomeDelivery) {
            isFmcgHomeDelivery = fmcgHomeDelivery;
        }
    }
}
