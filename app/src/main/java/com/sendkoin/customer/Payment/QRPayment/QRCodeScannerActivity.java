package com.sendkoin.customer.Payment.QRPayment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sendkoin.customer.KoinApplication;
import com.sendkoin.customer.R;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by warefhaque on 5/23/17.
 */


public class QRCodeScannerActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler, QRScannerContract.View {

  @Inject QRScannerContract.Presenter mPresenter;
  @Inject ZBarScannerView mScannerView;

  private String TAG = "QRScannerActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setUpDagger();
    setContentView(mScannerView);
  }

  private void setUpDagger() {
    DaggerQRComponent.builder().netComponent(((KoinApplication) getApplication()
        .getApplicationContext())
        .getNetComponent())
        .qRPaymentModule(new QRPaymentModule(this))
        .build()
        .inject(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
    mScannerView.startCamera();          // Start camera on resume
  }


  @Override
  public void onPause() {
    super.onPause();
    mScannerView.stopCamera();// Stop camera on pause
    mPresenter.unsubscribe();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mPresenter.close();
  }

  @Override
  public void handleResult(me.dm7.barcodescanner.zbar.Result result) {
    // Do something with the result here
    Log.v(TAG, result.getContents()); // Prints scan results
    Log.v(TAG, result.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)
    Intent intent = new Intent(QRCodeScannerActivity.this, PaymentConfirmationActivity.class);
    startActivity(intent);
//    AlertDialog dialog = paymentConfirmationDialog(result);
//    dialog.show();

    // If you would like to resume scanning, call this method below:
//        mScannerView.resumeCameraPreview(this);
  }

  private AlertDialog paymentConfirmationDialog(me.dm7.barcodescanner.zbar.Result result) {
    return new AlertDialog.Builder(this)
        .setTitle(R.string.qr_scanner_dialog_title)
        .setMessage(result.getContents())
        .setPositiveButton(R.string.alert_dialog_create, (dialogInterface, i) -> {
          try {
            JSONObject paymentDetails = new JSONObject(result.getContents());
            mPresenter.createPayment(paymentDetails);
          } catch (JSONException e) {
            e.printStackTrace();
          }
        })
        .setNegativeButton(R.string.alert_dialog_cancel, (dialogInterface, i) -> {

        })
        .create();
  }

  @Override
  public Context getContext() {
    return getApplicationContext();
  }
}
