package com.apollo.pharmacy.ocr.epsonsdk;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FolderUtility {

    Context context;

    public FolderUtility(Context context) {
        this.context = context;
    }

    public String getImageDirRoot() {
        //String extStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        final File filePath = new File(this.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "EpsonScan2SDKSample");// this.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return filePath.getAbsolutePath();
    }

    public String getPDFFileName() {
        File baseDir = new File(getImageDirRoot());
        File pdfDir = new File(baseDir, "SavedPDF");
        if (pdfDir.exists() == false) {
            pdfDir.mkdirs();
        }

        for (int counter = 0; counter < 1000 /*MAX Attempt*/; counter++) {
            File tempDir = new File(pdfDir + "/PDF_" + counter + ".pdf");
            if (tempDir.exists() == false) {
                return tempDir.getAbsolutePath();
            }
        }

        return null;
    }

    public void resetImageStoreDir() {
        File baseDir = new File(getImageDirRoot() + "/ImageStoreDir");
        // delete all contents
        if (baseDir.exists()) {
            if (baseDir.isDirectory()) {
                String[] children = baseDir.list();
                for (int i = 0; i < children.length; i++) {
                    File subfolder = new File(baseDir, children[i]);
                    if (subfolder.isDirectory()) {
                        String[] subchildren = subfolder.list();
                        for (int j = 0; j < subchildren.length; j++) {
                            File subFile = new File(subfolder, subchildren[j]);

                            subFile.delete();

                        }
                    }
                    subfolder.delete();

                }
                baseDir.delete();
            }
        }

        // if folder not exsits create
        if (baseDir.exists() == false) {
            if (baseDir.mkdirs() == false) {
                Log.d("EpsonScan2SDKSample", "fails to create dir");
            }
        }
    }

    public String getTempImageStoreDir() {
        File baseDir = new File(getImageDirRoot() + "/ImageStoreDir");
        // if folder not exsits create
        if (baseDir.exists() == false) {
            if (baseDir.mkdirs() == false) {
                Log.d("EpsonScan2SDKSample", "fails to create dir");
            }
        }

        String baseName = "ImageStore-" + System.currentTimeMillis() + "-";

        for (int counter = 0; counter < 1000 /*MAX Attempt*/; counter++) {
            File tempDir = new File(baseDir, baseName + counter);
            if (tempDir.mkdir()) {
                return tempDir.getAbsolutePath();
            }
        }
        return null;
    }

}
