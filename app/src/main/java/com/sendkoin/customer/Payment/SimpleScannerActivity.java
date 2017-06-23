package com.sendkoin.customer.Payment;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by warefhaque on 6/23/17.
 */

public class SimpleScannerActivity extends Activity implements ZBarScannerView.ResultHandler {
  private static final String TAG = SimpleScannerActivity.class.getSimpleName();
  private static final int PERMISSION_REQUEST_CAMERA = 1;
  private ZBarScannerView mScannerView;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
    setContentView(mScannerView);// Set the scanner view as the content view
    if (!haveCameraPermission())
      requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
  }


  private boolean haveCameraPermission() {
    return Build.VERSION.SDK_INT < 23 || checkSelfPermission(Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      switch (requestCode) {
        case PERMISSION_REQUEST_CAMERA:
          startCamera();
          break;
      }
    } else {
      finish();
    }

  }

  public void startCamera() {
    mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
    mScannerView.startCamera();          // Start camera on resume
  }

  @Override
  public void onResume() {
    super.onResume();
    startCamera();         // Start camera on resume
  }

  @Override
  public void onPause() {
    super.onPause();
    mScannerView.stopCamera();           // Stop camera on pause
  }

  @Override
  public void handleResult(Result rawResult) {
    // Do something with the result here
    Log.v(TAG, rawResult.getContents()); // Prints scan results
    Log.v(TAG, rawResult.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)

    // If you would like to resume scanning, call this method below:
    mScannerView.resumeCameraPreview(this);
  }
}