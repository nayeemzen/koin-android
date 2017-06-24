package com.sendkoin.customer.Payment.QRPayment;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sendkoin.api.QrCode;
import com.sendkoin.customer.KoinApplication;
import com.sendkoin.customer.R;

import javax.inject.Inject;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.loader.PicassoLoader;
import agency.tango.android.avatarview.views.AvatarView;
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
  @BindView(R.id.enter_sales)
  EditText enterSaleAmount;
  @BindView(R.id.confirmation_bar_code)
  ImageView barCode;
  @BindView(R.id.merchant_logo)
  AvatarView merchantLogo;
  IImageLoader imageLoader;

  QRScannerFragment qrScannerFragement;
  private String qrToken;
  private Unbinder unbinder;
  private SweetAlertDialog pDialog;

  private enum UIState {
    SCANNER,
    DYNAMIC_QR_PAYMENT_CONFIRMATION,
    STATIC_QR_GENERATE_PAYMENT,
    STATIC_QR_PAYMENT_CONFIRMATION

  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_qrscanner);
    setUpDagger();
    unbinder = ButterKnife.bind(this);
    qrScannerFragement = new QRScannerFragment();
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.add(R.id.scanner_fragment_layout, qrScannerFragement);
    transaction.commit();
    imageLoader = new PicassoLoader();
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
    payButton.setRadius(20);
    payButton.setPadding(10, 20, 10, 20);
    payButton.setCustomTextFont("Nunito-Regular.ttf");
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
    this.qrToken = qrCode.qr_token;
    setUIState(UIState.PAYMENT_CONFIRMATION);
    merchantName.setText(qrCode.merchant_name);
    saleAmount.setText("$" + qrCode.amount.toString());
    setupPayButton();
  }

  @Override
  public void showTransactionComplete() {
    //loading indicator off and show check mark
    showLoadingComplete();
  }

  @Override
  public void showTransactionError(String error) {
    showLoadingError(error);
  }

  private void showLoadingError(String error) {
    pDialog
        .setTitleText(error)
        .changeAlertType(SweetAlertDialog.ERROR_TYPE);

  }


  @OnClick(R.id.pay_button)
  void processPayment() {
    if (qrToken != null) {
      //loading indicator ON
      showLoadingIndicator();
      mPresenter.acceptTransaction(qrToken);
      qrToken = null;
    }
  }

  private void showLoadingIndicator() {
    pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
    pDialog.getProgressHelper().setBarColor(Color.parseColor("#37B3B8"));
    pDialog.setTitleText("Processing Payment...");
    pDialog.setCancelable(false);
    pDialog.show();
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

  public void setUIState(UIState uiState) {
    switch (uiState) {
      case SCANNER:
        scannerFrameLayout.setVisibility(View.VISIBLE);
        paymentConfirmationLayout.setVisibility(View.GONE);
        qrScannerFragement.resumeScanning();
        qrToken = null;
        break;
      case DYNAMIC_QR_PAYMENT_CONFIRMATION:
        scannerFrameLayout.setVisibility(View.GONE);
        paymentConfirmationLayout.setVisibility(View.VISIBLE);
        enterSaleAmount.setVisibility(View.GONE);
        saleAmount.setVisibility(View.VISIBLE);
        payButton.setVisibility(View.VISIBLE);
        barCode.setVisibility(View.GONE);
        break;
      case STATIC_QR_GENERATE_PAYMENT:
        scannerFrameLayout.setVisibility(View.GONE);
        paymentConfirmationLayout.setVisibility(View.VISIBLE);
        enterSaleAmount.setVisibility(View.VISIBLE);
        enterSaleAmount.setEnabled(true);
        saleAmount.setVisibility(View.GONE);
        payButton.setVisibility(View.VISIBLE);
        barCode.setVisibility(View.GONE);
        break;
      case STATIC_QR_PAYMENT_CONFIRMATION:
        scannerFrameLayout.setVisibility(View.GONE);
        paymentConfirmationLayout.setVisibility(View.VISIBLE);
        enterSaleAmount.setVisibility(View.GONE);
        saleAmount.setVisibility(View.VISIBLE);
        payButton.setVisibility(View.GONE);
        barCode.setVisibility(View.VISIBLE);
    }

  }

  public UIState getUIState() {
    switch (paymentConfirmationLayout.getVisibility()) {
      case View.VISIBLE:
        return UIState.DYNAMIC_QR_PAYMENT_CONFIRMATION;
      default:
        return UIState.SCANNER;
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
      if (getUIState().equals(UIState.SCANNER)) {
        finish();
      } else {
        setUIState(UIState.SCANNER);
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

}
