package com.sendkoin.customer.payment.paymentCreate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.orangegangsters.lollipin.lib.managers.AppLock;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by warefhaque on 6/1/17.
 */

public class QrScannerFragment extends android.app.Fragment implements ZBarScannerView.ResultHandler {
  private ZBarScannerView mScannerView;
  public static final int PERMISSION_REQUEST_CAMERA = 1;

  @SuppressLint("NewApi")
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

    mScannerView = new ZBarScannerView(getActivity());
    if (!haveCameraPermission())
      requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
    return mScannerView;
  }


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

    if (rawResult.getContents().contains(QrScannerPresenter.MERCHANT_NAME)) {
      ScannerActivity scannerActivity = (ScannerActivity) getActivity();
      Intent intent = new Intent(scannerActivity, QrScannerActivity.class);
      intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
      intent.putExtra("qr_string", rawResult.getContents());
      startActivity(intent);
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
