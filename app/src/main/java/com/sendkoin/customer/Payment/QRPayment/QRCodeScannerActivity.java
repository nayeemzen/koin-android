package com.sendkoin.customer.Payment.QRPayment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.sendkoin.api.QrCode;
import com.sendkoin.api.QrType;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.KoinApplication;
import com.sendkoin.customer.Payment.TextDrawable;
import com.sendkoin.customer.Payment.TransactionDetails.TransactionDetailsActivity;
import com.sendkoin.customer.R;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.scheme.VCard;

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
  @BindView(R.id.done_button)
  FancyButton doneButton;
  @BindView(R.id.payment_complete)
  RelativeLayout paymentCompleteLayout;
  @BindView(R.id.merchant_logo_pay_complete)
  AvatarView merchantLogoPaymentComplete;
  @BindView(R.id.merchant_name_pay_complete)
  TextView merchant_name_pay_complete;
  @BindView(R.id.sale_amount_payment_complete)
  TextView saleAmountPayComplete;
  @BindView(R.id.payment_in_process_layout)
  RelativeLayout paymentInProcessLayout;
  @BindView(R.id.transaction_state_icon)
  ImageView transactionStateIcon;
  @BindView(R.id.transaction_state_text)
  TextView transactionStateText;
  @BindView(R.id.barcode_image)
  ImageView barcodeImageView;


  IImageLoader imageLoader;

  QRScannerFragment qrScannerFragement;
  private Unbinder unbinder;
  private SweetAlertDialog pDialog;
  private QrCode qrCode;
  private UIState currentUiState;
  private UIState paymentUiState;
  private Transaction.State transactionState;

  private enum UIState {
    SCANNER,
    DYNAMIC_QR_PAYMENT_CONFIRMATION,
    STATIC_QR_GENERATE_PAYMENT,
    PAYMENT_COMPLETE
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
    payButton.setBackgroundColor(Color.parseColor("#00FFFFFF"));
    payButton.setFocusBackgroundColor(Color.parseColor("#008489"));
    payButton.setBorderWidth(5);
    payButton.setBorderColor(Color.parseColor("#37B3B8"));
    payButton.setTextSize(20);
    payButton.setTextColor(Color.parseColor("#37B3B8"));
    payButton.setRadius(20);
    payButton.setPadding(10, 20, 10, 20);
    payButton.setCustomTextFont("Nunito-Regular.ttf");
  }

  private void setUpDoneButton() {
    doneButton.setText("Done");
    doneButton.setBackgroundColor(Color.parseColor("#37B3B8"));
    doneButton.setFocusBackgroundColor(Color.parseColor("#008489"));
    doneButton.setTextSize(20);
    doneButton.setTextColor(Color.parseColor("#FFFFFF"));
    doneButton.setRadius(20);
    doneButton.setPadding(10, 20, 10, 20);
    doneButton.setCustomTextFont("Nunito-Regular.ttf");
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
  public void showTransactionComplete(Transaction transaction) {
//    //loading indicator off and show check mark
////    showLoadingComplete();
//    merchant_name_pay_complete.setText(transaction.merchant.store_name);
//    imageLoader.loadImage(merchantLogoPaymentComplete, (String) null, transaction.merchant.store_name);
//    String saleAmount = transaction.amount.toString();
//    saleAmountPayComplete.setText("$" + saleAmount);
//    // need to know to show the sign and on backPresses();
//    transactionState = transaction.state;
//    setTransactionStateUI();
//    setUpBarcode(transaction.token);
//    setUIState(UIState.PAYMENT_COMPLETE);
    Intent intent = new Intent(QRCodeScannerActivity.this, TransactionDetailsActivity.class);
    intent.putExtra("transaction_token",transaction.token);
    intent.putExtra("from_payment",true);
    startActivity(intent);
  }

  private void setUpBarcode(String token) {
    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
    try {
      Bitmap bitmap = barcodeEncoder.encodeBitmap(
          token,
          BarcodeFormat.CODE_128,
          barcodeImageView.getWidth() - 40,
          barcodeImageView.getHeight() - 40);

      barcodeImageView.setImageBitmap(bitmap);
    } catch (WriterException e) {
      e.printStackTrace();
    }
  }

  private void setTransactionStateUI() {
    switch (transactionState) {
      case COMPLETE:
        transactionStateIcon.setImageResource(R.drawable.trans_approved);
        transactionStateText.setText("Transaction Approved");
        transactionStateText.setTextColor(Color.parseColor("#37B3B8"));
        break;
      case FAILED:
        showLoadingError("Error");
        break;
    }
  }

  @Override
  public void showTransactionError(String error) {
    showLoadingError(error);
  }

  private void showLoadingError(String error) {
    transactionStateIcon.setImageResource(R.drawable.trans_declined);
    transactionStateText.setText("Transaction Declined");
    transactionStateText.setTextColor(Color.parseColor("#e74c3c"));
  }


  @OnClick(R.id.pay_button)
  void processPayment() {
    if (qrCode != null) {
      //loading indicator ON
//      showLoadingIndicator();
      // TODO: 6/25/17 WAREF - get loading indicator from Dare!
      if (currentUiState == UIState.STATIC_QR_GENERATE_PAYMENT) {
        if (enterSaleAmount.getText().toString().isEmpty()) {
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

  @OnClick(R.id.done_button)
  void donePayment() {
    if (transactionState != null) {
      // TODO: 6/25/17 WAREF - need to be sure which state it actually is - right now showing processing
      switch (transactionState) {
        case COMPLETE:
          finish();
          break;
        case PROCESSING:
          finish();
          break;
        case FAILED:
          setUIState(paymentUiState);
          break;
      }
      finish();
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
        // reset the state
        paymentUiState = null;
        break;
      case DYNAMIC_QR_PAYMENT_CONFIRMATION:
        scannerFrameLayout.setVisibility(View.GONE);
        paymentConfirmationLayout.setVisibility(View.VISIBLE);
        paymentInProcessLayout.setVisibility(View.VISIBLE);
        paymentCompleteLayout.setVisibility(View.GONE);
        enterSaleAmount.setVisibility(View.GONE);
        saleAmount.setVisibility(View.VISIBLE);
        payButton.setVisibility(View.VISIBLE);
        barCode.setVisibility(View.GONE);
        enterSalesMessage.setVisibility(View.GONE);
        paymentUiState = UIState.DYNAMIC_QR_PAYMENT_CONFIRMATION;
        break;
      case STATIC_QR_GENERATE_PAYMENT:
        scannerFrameLayout.setVisibility(View.GONE);
        paymentConfirmationLayout.setVisibility(View.VISIBLE);
        paymentInProcessLayout.setVisibility(View.VISIBLE);
        paymentCompleteLayout.setVisibility(View.GONE);
        enterSaleAmount.setVisibility(View.VISIBLE);
        enterSaleAmount.setFocusable(true);
        enterSaleAmount.requestFocus();
        saleAmount.setVisibility(View.GONE);
        payButton.setVisibility(View.VISIBLE);
        barCode.setVisibility(View.GONE);
        enterSalesMessage.setVisibility(View.VISIBLE);
        String name = merchantName.getText().toString();
        enterSalesMessage.setText("Please enter the amount in Taka you wish to pay " + name);
        paymentUiState = UIState.STATIC_QR_GENERATE_PAYMENT;
        break;
      case PAYMENT_COMPLETE:
        scannerFrameLayout.setVisibility(View.GONE);
        paymentConfirmationLayout.setVisibility(View.VISIBLE);
        paymentInProcessLayout.setVisibility(View.GONE);
        paymentCompleteLayout.setVisibility(View.VISIBLE);
        setUpDoneButton();
    }

  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
      if (currentUiState != null && currentUiState.equals(UIState.SCANNER)) {
        finish();
      } else {
        if (currentUiState == UIState.PAYMENT_COMPLETE) {
          // TODO: 6/25/17 WAREF - the transaction states were not working need to incorporate
          // transaction state cannot be null here
          if (transactionState == Transaction.State.COMPLETE || transactionState == Transaction.State.PROCESSING) {
            finish();
          } else if (transactionState == Transaction.State.FAILED) {
            setUIState(paymentUiState);
          }
        } else
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
