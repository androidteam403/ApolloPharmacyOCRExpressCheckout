package com.apollo.pharmacy.ocr.custompdf;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


import com.apollo.pharmacy.ocr.R;
import com.apollo.pharmacy.ocr.activities.BaseActivity;
import com.apollo.pharmacy.ocr.custompdf.utils.FileManager;
import com.apollo.pharmacy.ocr.custompdf.utils.PDFUtil;
import com.apollo.pharmacy.ocr.custompdf.views.PDFBody;
import com.apollo.pharmacy.ocr.custompdf.views.PDFFooterView;
import com.apollo.pharmacy.ocr.custompdf.views.PDFHeaderView;
import com.apollo.pharmacy.ocr.custompdf.views.basic.PDFImageView;
import com.apollo.pharmacy.ocr.custompdf.views.basic.PDFPageBreakView;
import com.apollo.pharmacy.ocr.custompdf.views.basic.PDFVerticalView;

import com.apollo.pharmacy.ocr.custompdf.views.basic.PDFView;
import com.apollo.pharmacy.ocr.model.PdfModelResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class PDFCreatorActivity extends BaseActivity {
    private static final String TAG = "PDFCreatorActivity";
    ArrayList<Bitmap> pagePreviewBitmapList = new ArrayList<>();
    File savedPDFFile = null;
    private int heightRequiredByHeader = 0;
    private int heightRequiredByFooter = 0;

    public PDFCreatorActivity() {
        super();
    }

    public void createPDF(String fileName, LinearLayout layoutPageParent, PdfModelResponse pdfModelResponse, final PDFUtil.PDFUtilListener pdfUtilListener) {
        ArrayList<View> bodyViewList = new ArrayList<>();
        View header = null;
        if (getHeaderView(0,pdfModelResponse) != null) {
            header = getHeaderView(0,pdfModelResponse).getView();
            header.setTag(PDFHeaderView.class.getSimpleName());
            bodyViewList.add(header);
            addViewToTempLayout(layoutPageParent, header);
        }

        if (getBodyViews(pdfModelResponse) != null) {
            for (PDFView pdfView : getBodyViews(pdfModelResponse).getChildViewList()) {
                View bodyView = pdfView.getView();
                if (pdfView instanceof PDFPageBreakView) {
                    bodyView.setTag(PDFPageBreakView.class.getSimpleName());
                } else {
                    bodyView.setTag(PDFBody.class.getSimpleName());
                }
                bodyViewList.add(bodyView);
                addViewToTempLayout(layoutPageParent, bodyView);
            }
        }

        View footer = null;
        PDFFooterView pdfFooterView = getFooterView(0,pdfModelResponse);
        if (pdfFooterView != null && pdfFooterView.getView().getChildCount() > 1) {
            // pdfFooterView.getView().getChildCount() > 1, because first view is ALWAYS empty space filler.
            footer = pdfFooterView.getView();
            footer.setTag(PDFFooterView.class.getSimpleName());
            addViewToTempLayout(layoutPageParent, footer);
        }

        createPDFFromViewList(header, footer, bodyViewList, fileName,layoutPageParent,pdfModelResponse, new PDFUtil.PDFUtilListener() {
            @Override
            public void pdfGenerationSuccess(File savedPDFFile) {
                try {
                    pagePreviewBitmapList.clear();
                    pagePreviewBitmapList.addAll(PDFUtil.pdfToBitmap(savedPDFFile));
                } catch (Exception e) {
                    e.printStackTrace();
                }
               PDFCreatorActivity.this.savedPDFFile = savedPDFFile;
                pdfUtilListener.pdfGenerationSuccess(savedPDFFile);
            }

            @Override
            public void pdfGenerationFailure(Exception exception) {
                pdfUtilListener.pdfGenerationFailure(exception);
            }
        });
    }

    /**
     * Creates a paginated PDF page views from list of views those are already rendered on screen
     * (Only rendered views can give height)
     *  @param tempViewList list of views to create pdf views from, view should be already rendered to screen
     * @param layoutPageParent
     * @param pdfModelResponse
     */
    private void createPDFFromViewList(final View headerView, final View footerView, @NonNull final ArrayList<View> tempViewList, @NonNull final String filename, LinearLayout layoutPageParent, PdfModelResponse pdfModelResponse, final PDFUtil.PDFUtilListener pdfUtilListener) {
        tempViewList.get(tempViewList.size() - 1).post(new Runnable() {
            @Override
            public void run() {

                // Clean temp folder
                final FileManager fileManager = FileManager.getInstance();
                fileManager.cleanTempFolder(getApplicationContext());

                // get height per page
                final int HEIGHT_ALLOTTED_PER_PAGE = (getResources().getDimensionPixelSize(R.dimen.pdf_height) - (getResources().getDimensionPixelSize(R.dimen.pdf_margin_vertical) * 2));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final List<View> pdfPageViewList = new ArrayList<>();
                        FrameLayout currentPDFLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.item_pdf_page, layoutPageParent, false);
                        currentPDFLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                        pdfPageViewList.add(currentPDFLayout);

                        // Add watermark layout
                        PDFView watermarkPDFView = getWatermarkView(0);
                        if (watermarkPDFView != null && watermarkPDFView.getView() != null) {
                            currentPDFLayout.addView(watermarkPDFView.getView());
                        }

                        LinearLayout currentPDFView = new PDFVerticalView(getApplicationContext()).getView();
                        final LinearLayout.LayoutParams verticalPageLayoutParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT, 0);
                        currentPDFView.setLayoutParams(verticalPageLayoutParams);
                        currentPDFLayout.addView(currentPDFView);

                        int currentPageHeight = 0;

                        if (headerView != null) {
                            // If item is a page header, store its height so we can add it to all pages without waiting to render it every time
                            heightRequiredByHeader = headerView.getHeight();
                        }

                        if (footerView != null) {
                            // If item is a page header, store its height so we can add it to all pages without waiting to render it every time
                            heightRequiredByFooter = footerView.getHeight();
                        }

                        int pageIndex = 1;
                        for (int i = 0; i < tempViewList.size(); i++) {
                            View viewItem = tempViewList.get(i);

                            boolean isPageBreakView = false;
                            if (viewItem.getTag() != null && viewItem.getTag() instanceof String) {
                                isPageBreakView = PDFPageBreakView.class.getSimpleName().equalsIgnoreCase((String) viewItem.getTag());
                            }

                            if (currentPageHeight + viewItem.getHeight() > HEIGHT_ALLOTTED_PER_PAGE) {
                                // this will be exceed current page, create a new page and add this view to that page
                                currentPDFLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.item_pdf_page, layoutPageParent, false);
                                currentPDFLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                pdfPageViewList.add(currentPDFLayout);
                                currentPageHeight = 0;

                                // Add watermark layout
                                watermarkPDFView = getWatermarkView(pageIndex);
                                if (watermarkPDFView != null && watermarkPDFView.getView() != null) {
                                    currentPDFLayout.addView(watermarkPDFView.getView());
                                }

                                currentPDFView = new PDFVerticalView(getApplicationContext()).getView();
                                currentPDFView.setLayoutParams(verticalPageLayoutParams);
                                currentPDFLayout.addView(currentPDFView);

                                // Add page header again
                                if (heightRequiredByHeader > 0) {
                                    // If height is available, only then add header
                                    LinearLayout layoutHeader = getHeaderView(pageIndex,pdfModelResponse).getView();
                                    addViewToTempLayout(layoutPageParent, layoutHeader);
                                    currentPageHeight += heightRequiredByHeader;
                                    layoutPageParent.removeView(layoutHeader);
                                    currentPDFView.addView(layoutHeader);

                                    pageIndex = pageIndex + 1;
                                }
                            }

                            if (!isPageBreakView) {
                                // if not empty view, add
                                currentPageHeight += viewItem.getHeight();

                                layoutPageParent.removeView(viewItem);
                                currentPDFView.addView(viewItem);
                            } else {
                                Log.d(TAG, "run: This is PageBreakView");
                                currentPageHeight = HEIGHT_ALLOTTED_PER_PAGE;
                            }

                            // See if we have enough space to add Next View with Footer
                            // We we don't, add Footer View to current page
                            // Height required to add this view in current page
                            int heightRequiredToAddNextView = 0;
                            boolean shouldAddFooterNow = false;

                            if (tempViewList.size() > i + 1) {
                                // Check if we can add CURRENT_VIEW + NEXT_VIEW + FOOTER in current page
                                View nextViewItem = tempViewList.get(i + 1);
                                heightRequiredToAddNextView = nextViewItem.getHeight();

                                if (currentPageHeight + heightRequiredToAddNextView + heightRequiredByFooter > HEIGHT_ALLOTTED_PER_PAGE) {
                                    shouldAddFooterNow = true;
                                }

                            } else {
                                // Add Views are already added, we should add footer next
                                shouldAddFooterNow = true;
                            }

                            if (isPageBreakView || shouldAddFooterNow) {
                                // Cannot Add Next View with Footer in current Page
                                // Add Footer View to Current Page

                                if (heightRequiredByFooter > 0) {
                                    // Footer is NOT prematurely added like header, so we need to subtract 1 from pageIndex
                                    LinearLayout layoutFooter = getFooterView(pageIndex - 1,pdfModelResponse).getView();
                                    addViewToTempLayout(layoutPageParent, layoutFooter);
                                    layoutPageParent.removeView(layoutFooter);
                                    currentPDFView.addView(layoutFooter);
                                    currentPageHeight = HEIGHT_ALLOTTED_PER_PAGE;
                                }
                            }
                        }

                        PDFUtil.getInstance().generatePDF(pdfPageViewList, fileManager.createTempFileWithName(getApplicationContext(), filename + ".pdf", false).getAbsolutePath(), pdfUtilListener);
                    }
                });
            }
        });
    }

    private void addViewToTempLayout(LinearLayout layoutPageParent, View viewToAdd) {
        layoutPageParent.addView(viewToAdd);
    }


    /**
     * Get header per page, starts with page: 0
     * MAKE SURE HEIGHT OF EVERY HEADER IS SAME FOR EVERY PAGE
     *
     * @param forPage page number
     * @return View for header
     */
    protected abstract PDFHeaderView getHeaderView(int forPage,PdfModelResponse pdfModelResponse);

    /**
     * Content that has to be paginated
     *
     * @return PDFBody, which is a List of Views
     */
    protected abstract PDFBody getBodyViews(PdfModelResponse pdfModelResponse);

    /**
     * Get header per page, starts with page: 0
     * MAKE SURE HEIGHT OF EVERY FOOTER IS SAME FOR EVERY PAGE
     *
     * @param forPage page number
     * @return View for header
     */
    protected abstract PDFFooterView getFooterView(int forPage,PdfModelResponse pdfModelResponse);

    /**
     * Can add watermark images to per page, starts with page: 0
     *
     * @param forPage page number
     * @return PDFImageView or null
     */
    @Nullable
    protected PDFImageView getWatermarkView(int forPage) {
        return null;
    }

}
