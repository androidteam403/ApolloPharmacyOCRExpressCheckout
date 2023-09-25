package com.apollo.pharmacy.ocr.Utils.fileupload;

import java.io.File;

public class FileUploadModel {

    private File file;
    private int id;
    private FileUploadResponse fileUploadResponse;
    private boolean isFileUploaded;
    private boolean isFileDownloaded;
    private FileDownloadResponse fileDownloadResponse;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FileUploadResponse getFileUploadResponse() {
        return fileUploadResponse;
    }

    public void setFileUploadResponse(FileUploadResponse fileUploadResponse) {
        this.fileUploadResponse = fileUploadResponse;
    }

    public boolean isFileUploaded() {
        return isFileUploaded;
    }

    public void setFileUploaded(boolean fileUploaded) {
        isFileUploaded = fileUploaded;
    }

    public boolean isFileDownloaded() {
        return isFileDownloaded;
    }

    public void setFileDownloaded(boolean fileDownloaded) {
        isFileDownloaded = fileDownloaded;
    }

    public FileDownloadResponse getFileDownloadResponse() {
        return fileDownloadResponse;
    }

    public void setFileDownloadResponse(FileDownloadResponse fileDownloadResponse) {
        this.fileDownloadResponse = fileDownloadResponse;
    }
}
