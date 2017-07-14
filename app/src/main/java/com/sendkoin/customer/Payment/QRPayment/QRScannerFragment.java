package com.sendkoin.customer.Payment.QRPayment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by warefhaque on 6/1/17.
 */

public class QRScannerFragment extends android.app.Fragment implements ZBarScannerView.ResultHandler {
  private ZBarScannerView mScannerView;
  public static final int PERMISSION_REQUEST_CAMERA = 1;
  QRCodeScannerActivity qrCodeScannerActivity;

  @SuppressLint("NewApi")
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

    mScannerView = new ZBarScannerView(getActivity());
    if (!haveCameraPermission())
      requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
    qrCodeScannerActivity = (QRCodeScannerActivity) getActivity();
    return mScannerView;
  }

  // TODO: 7/14/17 WAREF - Need to fix crash when app is uninstalled and then reinstalled and then
  // the first QR scanned after permission is INVENTORY_QR
  private boolean haveCameraPermission() {
    return Build.VERSION.SDK_INT < 23 || getActivity().checkSelfPermission(Manifest.permission.CAMERA)
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
      getActivity().finish();
    }

  }

  public void startCamera() {
    mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
    mScannerView.startCamera();          // Start camera on resume
  }

  @Override
  public void onResume() {
    super.onResume();
    startCamera();
  }

  @Override
  public void handleResult(Result rawResult) {

    if (rawResult.getContents().contains(QRScannerPresenter.MERCHANT_NAME)) {
      QRCodeScannerActivity qrCodeScannerActivity = (QRCodeScannerActivity) getActivity();
      qrCodeScannerActivity.showTransactionConfirmationScreen(rawResult.getContents());
    } else {
      resumeScanning();
    }
  }

  public void resumeScanning() {
    mScannerView.resumeCameraPreview(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    mScannerView.stopCamera();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mScannerView.stopCamera();
    mScannerView = null;
  }
}
