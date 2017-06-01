package com.sendkoin.customer.Payment.QRPayment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;
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

    try {
      mPresenter.createPaymentDetails(new JSONObject(result.getContents()));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Context getContext() {
    return getApplicationContext();
  }

  @Override
  public void showPaymentConfirmationScreen(RealmTransaction realmTransaction) {
    AlertDialog alertDialog =  new AlertDialog.Builder(this)
        .setTitle(R.string.qr_scanner_dialog_title)
        .setMessage(realmTransaction.getMerchantName() + " " + realmTransaction.getAmount())
        .setPositiveButton(R.string.alert_dialog_create, (dialogInterface, i) -> {
         mPresenter.createPayment(realmTransaction.getTransactionToken());
        })
        .setNegativeButton(R.string.alert_dialog_cancel, (dialogInterface, i) -> {
          mScannerView.resumeCameraPreview(this);
        })
        .create();

    alertDialog.show();
  }
}
