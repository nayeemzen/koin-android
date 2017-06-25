package com.sendkoin.customer.Payment.QRPayment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sendkoin.api.QrCode;
import com.sendkoin.api.QrType;
import com.sendkoin.customer.KoinApplication;
import com.sendkoin.customer.Payment.TextDrawable;
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
  @BindView(R.id.enter_sales_message)
  TextView enterSalesMessage;
  IImageLoader imageLoader;

  QRScannerFragment qrScannerFragement;
  private Unbinder unbinder;
  private SweetAlertDialog pDialog;
  private QrCode qrCode;
  private UIState currentUiState;

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
    this.qrCode = mGson.fromJson(qrContent, QrCode.class);

    merchantName.setText(qrCode.merchant_name);
    imageLoader.loadImage(merchantLogo, (String) null, qrCode.merchant_name);
    UIState uiState = (qrCode.qr_type == QrType.DYNAMIC) ?
        UIState.DYNAMIC_QR_PAYMENT_CONFIRMATION : UIState.STATIC_QR_GENERATE_PAYMENT;
    setUIState(uiState);

    if (currentUiState == UIState.DYNAMIC_QR_PAYMENT_CONFIRMATION) {
      saleAmount.setText("$" + qrCode.amount.toString());
    }
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
    if (qrCode != null) {
      //loading indicator ON
      showLoadingIndicator();
      if (currentUiState == UIState.STATIC_QR_GENERATE_PAYMENT) {
        if (enterSaleAmount.getText().toString().isEmpty()){
          Toast.makeText(this, "Please enter a valid sale amount", Toast.LENGTH_SHORT).show();
          return;
        }
        int saleAmount = Integer.parseInt(enterSaleAmount.getText().toString());
        mPresenter.acceptTransaction(qrCode, saleAmount);

      } else if (currentUiState == UIState.DYNAMIC_QR_PAYMENT_CONFIRMATION) {
        mPresenter.acceptTransaction(qrCode, -1);
      }
      qrCode = null;
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
    currentUiState = uiState;
    switch (uiState) {
      case SCANNER:
        scannerFrameLayout.setVisibility(View.VISIBLE);
        paymentConfirmationLayout.setVisibility(View.GONE);
        qrScannerFragement.resumeScanning();
        qrCode = null;
        break;
      case DYNAMIC_QR_PAYMENT_CONFIRMATION:
        scannerFrameLayout.setVisibility(View.GONE);
        paymentConfirmationLayout.setVisibility(View.VISIBLE);
        enterSaleAmount.setVisibility(View.GONE);
        saleAmount.setVisibility(View.VISIBLE);
        payButton.setVisibility(View.VISIBLE);
        barCode.setVisibility(View.GONE);
        enterSalesMessage.setVisibility(View.GONE);
        break;
      case STATIC_QR_GENERATE_PAYMENT:
        scannerFrameLayout.setVisibility(View.GONE);
        paymentConfirmationLayout.setVisibility(View.VISIBLE);
        enterSaleAmount.setVisibility(View.VISIBLE);
        enterSaleAmount.setFocusable(true);
        enterSaleAmount.requestFocus();
        // code to try and add the $ sign at the left - not working
//        String code = "$";
//        enterSaleAmount.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(code, this), null, null, null);
//        enterSaleAmount.setCompoundDrawablePadding(code.length()*10);
        saleAmount.setVisibility(View.GONE);
        payButton.setVisibility(View.VISIBLE);
        barCode.setVisibility(View.GONE);
        enterSalesMessage.setVisibility(View.VISIBLE);
        String name = merchantName.getText().toString();
        enterSalesMessage.setText("Please enter the amount in Taka you wish to pay " + name);
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

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
      if (currentUiState != null && currentUiState.equals(UIState.SCANNER)) {
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
