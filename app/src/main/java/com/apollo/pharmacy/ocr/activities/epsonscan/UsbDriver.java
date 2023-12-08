package com.apollo.pharmacy.ocr.activities.epsonscan;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import com.epson.epsonscansdk.usb.UsbProfile;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class UsbDriver {
    private static UsbDriver instance;
    private Context context;
    private UsbManager usbManager;
    private UsbDeviceConnection usbConnection;
    private UsbInterface usbInterface;
    private UsbEndpoint inEndpoint;
    private UsbEndpoint outEndpoint;
    private UsbProfile.UsbBulkTransferMode transferMode;
    private boolean connected;
    private final int USB_TIMEOUT = 30000;
    private static final String ACTION_USB_PERMISSION = "com.epson.epsonscansdk.USB_PERMISSION";

    private UsbDriver(Context context) {
        this.usbManager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
        this.usbConnection = null;
        this.usbInterface = null;
        this.inEndpoint = null;
        this.outEndpoint = null;
        this.connected = false;
        this.context = context.getApplicationContext();
    }

    public static synchronized UsbDriver getInstance(Context context) {
        if (instance == null) {
            instance = new UsbDriver(context);
        }

        return instance;
    }

    public List<UsbProfile> getDeviceProfileList() {
        List<UsbProfile> deviceProfileList = new ArrayList();
        HashMap<String, UsbDevice> deviceList = this.usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        while(true) {
            UsbDevice device;
            String deviceName;
            int vendorId;
            int productId;
            do {
                if (!deviceIterator.hasNext()) {
                    return deviceProfileList;
                }

                device = (UsbDevice)deviceIterator.next();
                deviceName = device.getDeviceName();
                vendorId = device.getVendorId();
                productId = device.getProductId();
            } while(vendorId != 1208);

            boolean hasScanner = false;

            for(int i = 0; i < device.getInterfaceCount(); ++i) {
                UsbInterface usbInterface = device.getInterface(i);
                if (usbInterface.getInterfaceClass() == 255 && usbInterface.getInterfaceSubclass() == 255 && usbInterface.getInterfaceProtocol() == 255) {
                    hasScanner = true;
                    break;
                }
            }

            if (hasScanner) {
                boolean hasPermission = this.usbManager.hasPermission(device);
                if (!hasPermission) {
                    Intent intent = new Intent("com.epson.epsonscansdk.USB_PERMISSION");
                    PendingIntent permissionIntent = PendingIntent.getBroadcast(this.context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                    this.usbManager.requestPermission(device, permissionIntent);
                }

                UsbProfile usbProfile = new UsbProfile(deviceName, vendorId, productId);
                deviceProfileList.add(usbProfile);
            }
        }
    }

    public boolean connect(String deviceName) {
        boolean result = false;
        this.transferMode = UsbProfile.UsbBulkTransferMode.BULKTRANSFER_DIV;
        HashMap<String, UsbDevice> deviceList = this.usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        while(deviceIterator.hasNext()) {
            UsbDevice device = (UsbDevice)deviceIterator.next();
            String tmp = device.getDeviceName();
            if (deviceName.equals(device.getDeviceName())) {
                boolean hasPermission = this.usbManager.hasPermission(device);
                if (!hasPermission) {
                    Intent intent = new Intent("com.epson.epsonscansdk.USB_PERMISSION");
                    PendingIntent permissionIntent = PendingIntent.getBroadcast(this.context, 0, intent , PendingIntent.FLAG_IMMUTABLE);
                    this.usbManager.requestPermission(device, permissionIntent);
                }

                this.usbConnection = this.usbManager.openDevice(device);
                if (this.usbConnection != null) {
                    for(int cnt = 0; cnt < device.getInterfaceCount(); ++cnt) {
                        this.usbInterface = device.getInterface(cnt);
                        if (this.usbInterface != null && this.usbInterface.getInterfaceClass() == 255) {
                            result = this.setEndpoint();
                            if (result) {
                                this.connected = true;
                            }

                            return result;
                        }
                    }
                }
                break;
            }
        }

        return result;
    }

    public void disconnect() {
        if (this.usbInterface != null) {
            this.usbConnection.releaseInterface(this.usbInterface);
            this.usbInterface = null;
        }

        if (this.usbConnection != null) {
            this.usbConnection.close();
            this.usbConnection = null;
        }

        this.connected = false;
    }

    public boolean isConnected() {
        return this.connected;
    }

    private boolean setEndpoint() {
        for(int i = 0; i < this.usbInterface.getEndpointCount(); ++i) {
            UsbEndpoint endpoint = this.usbInterface.getEndpoint(i);
            if (endpoint.getType() == 2) {
                switch (endpoint.getDirection()) {
                    case 0:
                        this.outEndpoint = endpoint;
                        break;
                    case 128:
                        this.inEndpoint = endpoint;
                }
            }
        }

        boolean result;
        if (this.inEndpoint != null && this.outEndpoint != null) {
            result = true;
        } else {
            result = false;
        }

        return result;
    }

    public int send(byte[] bytes) {
        int result;
        for(result = 0; result < bytes.length; ++result) {
        }

        result = this.usbConnection.bulkTransfer(this.outEndpoint, bytes, bytes.length, 30000);
        return result;
    }

    public byte[] receive(int length) {
        int totalReceivedLength = 0;
        int bufferSize;
        switch (this.transferMode) {
            case BULKTRANSFER_DIV:
                int maxPacketSize = this.outEndpoint.getMaxPacketSize();
                bufferSize = length > maxPacketSize ? maxPacketSize : length;
                break;
            case BULKTRANSFER_NORMAL:
            default:
                bufferSize = length;
        }

        byte[] chunk = new byte[bufferSize];

        ByteBuffer buff;
        int chunkReceivedLength;
        for(buff = ByteBuffer.allocate(length); totalReceivedLength < length; totalReceivedLength += chunkReceivedLength) {
            chunkReceivedLength = this.usbConnection.bulkTransfer(this.inEndpoint, chunk, chunk.length, 30000);
            if (chunkReceivedLength < 0) {
                break;
            }

            buff.put(Arrays.copyOf(chunk, chunkReceivedLength));
        }

        return buff.array();
    }

    public int usbReset() {
        int writtenByte = -1;
        if (this.usbConnection == null) {
            return writtenByte;
        } else {
            writtenByte = this.usbConnection.controlTransfer(64, 4, 0, 32769, (byte[])null, 0, 30000);
            return writtenByte;
        }
    }
}
