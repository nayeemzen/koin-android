package com.sendkoin.customer.Payment.QRPayment;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sendkoin.api.QrCode;
import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;
import com.sendkoin.customer.KoinApplication;
import com.sendkoin.customer.R;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by warefhaque on 5/23/17.
 */


public class QRCodeScannerActivity extends Activity implements QRScannerContract.View {

  @Inject
  QRScannerContract.Presenter mPresenter;

  @Inject
  Gson mGson;

  @BindView(R.id.scanner_fragment_layout)
  FrameLayout scannerFrameLayout;

  @BindView(R.id.payment_confirmation_layout)
  RelativeLayout paymentConfirmationLayout;

  @BindView(R.id.pay_button)
  FancyButton payButton;

  @BindView(R.id.merchant_name)
  TextView merchantName;

  @BindView(R.id.sale_amount)
  TextView saleAmount;

  QRScannerFragement qrScannerFragement;
  private String mTransactionToken;
  private Unbinder unbinder;
  private SweetAlertDialog pDialog;

  private enum UIState {
    SCANNER,
    PAYMENT_CONFIRMATION
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_qrscanner);
    setUpDagger();
    unbinder = ButterKnife.bind(this);
    qrScannerFragement = new QRScannerFragement();
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.add(R.id.scanner_fragment_layout, qrScannerFragement);
    transaction.commit();
  }

  private void setUpDagger() {
    DaggerQRComponent.builder().netComponent(((KoinApplication) getApplication()
        .getApplicationContext())
        .getNetComponent())
        .qRPaymentModule(new QRPaymentModule(this))
        .build()
        .inject(this);
  }

  private void setupPayButton() {
    payButton.setText("Pay");
    payButton.setBackgroundColor(Color.parseColor("#37B3B8"));
    payButton.setFocusBackgroundColor(Color.parseColor("#008489"));
    payButton.setTextSize(20);
    payButton.setRadius(50);
    payButton.setPadding(10, 20, 10, 20);
    payButton.setCustomTextFont("Nunito-Bold.ttf");
  }

  /**
   * For the Calligraphy fonts
   *
   * @param newBase
   */
  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  @Override
  public Context getContext() {
    return getApplicationContext();
  }

  public void showTransactionConfirmationScreen(String qrContent) {
    QrCode qrCode = mGson.fromJson(qrContent, QrCode.class);
    this.mTransactionToken = qrCode.transaction_token;
    setUIState(UIState.PAYMENT_CONFIRMATION);
    merchantName.setText(qrCode.merchant_name);
    saleAmount.setText("$" + qrCode.sale_amount.toString());
    setupPayButton();
  }

  @Override
  public void showTransactionComplete() {
    //loading indicator off and show check mark
    showLoadingComplete();
  }

  @Override
  public void showTransactionError() {
    showLoadingError();
  }

  private void showLoadingError() {
    pDialog
        .setTitleText("Declined. Please try again")
        .changeAlertType(SweetAlertDialog.ERROR_TYPE);

  }

  private void showLoadingComplete() {
    pDialog.setTitleText("Payment Successful!")
        .setConfirmText("OK")
        .setOnDismissListener(dialog -> {
          finish();
          dialog.dismiss();
        });
    pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
  }

  @OnClick(R.id.pay_button)
  void processPayment() {
    if (mTransactionToken != null) {
      //loading indicator ON
      showLoadingIndicator();
      mPresenter.createTransaction(mTransactionToken);
      mTransactionToken = null;
    }
  }

  private void showLoadingIndicator() {
    pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
    pDialog.getProgressHelper().setBarColor(Color.parseColor("#37B3B8"));
    pDialog.setTitleText("Processing Payment...");
    pDialog.setCancelable(false);
    pDialog.show();
  }


  public void setUIState(UIState uiState) {
    switch (uiState) {
      case SCANNER:
        scannerFrameLayout.setVisibility(View.VISIBLE);
        paymentConfirmationLayout.setVisibility(View.GONE);
        qrScannerFragement.resumeScanning();
        mTransactionToken = null;
        break;
      case PAYMENT_CONFIRMATION:
        scannerFrameLayout.setVisibility(View.GONE);
        paymentConfirmationLayout.setVisibility(View.VISIBLE);
        break;
    }

  }

  public UIState getUIState() {
    switch (paymentConfirmationLayout.getVisibility()) {
      case View.VISIBLE:
        return UIState.PAYMENT_CONFIRMATION;
      default:
        return UIState.SCANNER;
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
      if (getUIState().equals(UIState.PAYMENT_CONFIRMATION)) {
        setUIState(UIState.SCANNER);
      } else if (getUIState().equals(UIState.SCANNER)) {
        finish();
      }
    }
    return true;
  }

  /**
   * Not using onResume to subscribe as the Fragment with the scanner does that
   */

  @Override
  protected void onPause() {
    super.onPause();
    mPresenter.unsubscribe();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (unbinder != null) {
      unbinder.unbind();
    }
  }

  //  @Inject ZBarScannerView mScannerView;
//
//  @Inject
//  Gson gson;
//  private String TAG = "QRScannerActivity";
//
//  @Override
//  protected void onCreate(Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//    setUpDagger();
//    setContentView(mScannerView);
//  }
//
//
//
//  @Override
//  protected void onResume() {
//    super.onResume();
//    mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
//    mScannerView.startCamera();          // Start camera on resume
//  }
//
//
//  @Override
//  public void onPause() {
//    super.onPause();
//    mScannerView.stopCamera();// Stop camera on pause
//    mPresenter.unsubscribe();
//  }
//
//  @Override
//  protected void onDestroy() {
//    super.onDestroy();
//    mPresenter.close();
//  }
//
//  @Override
//  public void handleResult(me.dm7.barcodescanner.zbar.Result result) {
//
////    try {
////      mPresenter.getTransactionConfirmationDetails(new JSONObject(result.getContents()));
////    } catch (JSONException e) {
////      e.printStackTrace();
////    }
//
//    // other wise on high def cameras it keeps scanning
//    if (result.getContents().contains(QRScannerPresenter.MERCHANT_NAME)){
//      Log.d(TAG, result.getContents());
//      Intent intent = new Intent(QRCodeScannerActivity.this, PaymentConfirmationActivity.class);
//      startActivity(intent);
//    }
//    else{
//      mScannerView.resumeCameraPreview(this);
//    }
//  }
//
//  @Override
//  public Context getContext() {
//    return getApplicationContext();
//  }
//
//  @Override
//  public void showTransactionConfirmationScreen(RealmTransaction realmTransaction) {
//    AlertDialog alertDialog =  new AlertDialog.Builder(this)
//        .setTitle(R.string.qr_scanner_dialog_title)
//        .setMessage(realmTransaction.getMerchantName() + " " + realmTransaction.getAmount())
//        .setPositiveButton(R.string.alert_dialog_create, (dialogInterface, i) -> {
//         mPresenter.createTransaction(realmTransaction.getTransactionToken());
//        })
//        .setNegativeButton(R.string.alert_dialog_cancel, (dialogInterface, i) -> {
//          mScannerView.resumeCameraPreview(this);
//        })
//        .create();
//
//    alertDialog.show();
//  }
}
