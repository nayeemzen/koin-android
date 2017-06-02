package com.sendkoin.customer.Payment.QRPayment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by warefhaque on 6/1/17.
 */

public class QRScannerFragement extends android.app.Fragment implements ZBarScannerView.ResultHandler {
  private ZBarScannerView mScannerView;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    mScannerView = new ZBarScannerView(getActivity());
    return mScannerView;
  }

  @Override
  public void onResume() {
    super.onResume();
    mScannerView.setResultHandler(this);
    mScannerView.startCamera();
  }

  @Override
  public void handleResult(Result rawResult) {

    if (rawResult.getContents().contains(QRScannerPresenter.MERCHANT_NAME)) {
      QRCodeScannerActivity qrCodeScannerActivity = (QRCodeScannerActivity) getActivity();
      try {
        qrCodeScannerActivity.getTransactionConfirmationDetails(new JSONObject(rawResult.getContents()));
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }

  public void resumeScanning(){
    mScannerView.resumeCameraPreview(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    mScannerView.stopCamera();
  }
}
