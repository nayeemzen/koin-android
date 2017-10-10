package com.sendkoin.customer.payment.paymentCreate.pinConfirmation;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.orangegangsters.lollipin.lib.managers.AppLockActivity;
import com.sendkoin.api.AcceptTransactionRequest;
import com.sendkoin.api.InitiateStaticTransactionRequest;
import com.sendkoin.api.QrType;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.KoinApplication;
import com.sendkoin.customer.R;
import com.sendkoin.customer.payment.paymentDetails.TransactionDetailsActivity;

import java.io.IOException;

import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.me.lewisdeane.ldialogs.BaseDialog;
import uk.me.lewisdeane.ldialogs.CustomDialog;

/**
 * Created by warefhaque on 7/29/17.
 */

public class PinConfirmationActivity extends AppLockActivity implements PinConfirmationContract.View{

  @Inject PinConfirmationContract.Presenter mPresenter;
  private int pinSuccessCount;
  SweetAlertDialog pDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // link the presenter, View and the modules required
    setUpDagger();
    // this variable is responsible for patching a bug in the pin library that calls the on
    // in success twice and hence allows for duplicate transactions
    pinSuccessCount = 0;
  }

  @Override
  public void showForgotDialog() {
    Resources res = getResources();
    // Create the builder with required paramaters - Context, Title, Positive Text
    CustomDialog.Builder builder = new CustomDialog.Builder(this, res.getString(R.string.activity_dialog_title), res.getString(R.string.activity_dialog_accept));
    builder.content(res.getString(R.string.activity_dialog_content));
    builder.negativeText(res.getString(R.string.activity_dialog_decline));

    //Set theme
    builder.darkTheme(false);
    builder.typeface(Typeface.SANS_SERIF);
    builder.positiveColor(Color.parseColor("#37B3B8")); // int res, or int colorRes parameter versions available as well.
    builder.negativeColor(Color.parseColor("#37B3B8"));
    builder.rightToLeft(false); // Enables right to left positioning for languages that may require so.
    builder.titleAlignment(BaseDialog.Alignment.CENTER);
    builder.buttonAlignment(BaseDialog.Alignment.CENTER);
    builder.setButtonStacking(false);

    //Set text sizes
    builder.titleTextSize((int) res.getDimension(R.dimen.activity_dialog_title_size));
    builder.contentTextSize((int) res.getDimension(R.dimen.activity_dialog_content_size));
    builder.positiveButtonTextSize((int) res.getDimension(R.dimen.activity_dialog_positive_button_size));
    builder.negativeButtonTextSize((int) res.getDimension(R.dimen.activity_dialog_negative_button_size));

    //Build the dialog.
    CustomDialog customDialog = builder.build();
    customDialog.setCanceledOnTouchOutside(false);
    customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    customDialog.setClickListener(new CustomDialog.ClickListener() {
      @Override
      public void onConfirmClick() {
        Toast.makeText(getApplicationContext(), "Yes", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onCancelClick() {
        Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
      }
    });

    // Show the dialog.
    customDialog.show();
  }

  private void setUpDagger() {
    DaggerPinConfirmationComponent.builder()
        .netComponent(((KoinApplication) getApplication()
            .getApplicationContext())
            .getNetComponent())
        .pinConfirmationModule(new PinConfirmationModule(this))
        .build()
        .inject(this);
  }

  @Override
  public void onPinFailure(int attempts) {
    Toast.makeText(this, "Pin incorrect. Please try again", Toast.LENGTH_SHORT).show();
    Log.d(TAG, "pin failure");
  }

  @Override
  public void onPinSuccess(int attempts) {
    //call the presenter
    Log.d(TAG, "pin success");
    pinSuccessCount = pinSuccessCount + 1;

    if (getIntent().hasExtra(getString(R.string.bundle_id_sale_summary)) && pinSuccessCount == 1) {
      Bundle bundle = getIntent().getBundleExtra(getString(R.string.bundle_id_sale_summary));


      try {
        showLoadingDialog();
        byte[] qrTypeByteArray= bundle.getByteArray(QrType.class.getSimpleName());
        if (qrTypeByteArray != null){
          QrType qrType = QrType.ADAPTER.decode(qrTypeByteArray);
          if (qrType == QrType.DYNAMIC) {
            processDynamicTransaction(bundle);
          } else {
            processStaticTransaction(bundle);
          }
        } else {
          showTransactionError("Local error. Please scan again");
        }
      } catch (IOException e) {
        e.printStackTrace();
        showTransactionError("Local error. Please scan again");
      }
    }
  }

  private void processStaticTransaction(Bundle bundle) throws IOException {
    byte[] initiateStaticTransactionByteArray =
        bundle.getByteArray(InitiateStaticTransactionRequest.class.getSimpleName());
    InitiateStaticTransactionRequest initiateStaticTransactionRequest =
        InitiateStaticTransactionRequest.ADAPTER.decode(initiateStaticTransactionByteArray);
    mPresenter.processStaticTransactionRequest(initiateStaticTransactionRequest);
  }

  private void processDynamicTransaction(Bundle bundle) throws IOException {
    byte[] accepTransactionRequestByteArray =
        bundle.getByteArray(AcceptTransactionRequest.class.getSimpleName());
    AcceptTransactionRequest acceptTransactionRequest =
        AcceptTransactionRequest.ADAPTER.decode(accepTransactionRequestByteArray);
    mPresenter.processDynamicTransactionRequest(acceptTransactionRequest);
  }

  private void showLoadingDialog() {
    pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
    pDialog.getProgressHelper().setBarColor(Color.parseColor("#37b3b8"));
    pDialog.setTitleText("Processing Payment");
    pDialog.setCancelable(false);
    pDialog.show();
  }

  @Override
  public int getPinLength() {
    return super.getPinLength();//you can override this method to change the pin length from the default 4
  }

  @Override
  public int getContentView() {
    return R.layout.activity_pin_confirmation;
  }

  @Override
  public void finish() {
      // pinSuccessCount < 1 is so that the user can press back when from this screen
      // as that calls finish as well
      if (!getIntent().hasExtra(getString(R.string.bundle_id_sale_summary))
          || pinSuccessCount < 1) {
        super.finish();
      }
  }

  @Override
  public void showTransactionReciept(Transaction transaction) {

    pDialog
        .setTitleText("Payment Complete")
        .setConfirmText("OK")
        .setConfirmClickListener(sweetAlertDialog -> {
          Intent intent = new Intent(PinConfirmationActivity.this, TransactionDetailsActivity.class);
          intent.putExtra(getString(R.string.trans_token_extra), transaction.token);
          intent.putExtra(getString(R.string.from_payment_extra), true);
          startActivity(intent);
        })
        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
  }

  @Override
  public void onBackPressed() {
    finish();
  }

  @Override
  public void showTransactionError(String errorMessage) {
    pDialog
        .setTitleText("Error")
        .setContentText(errorMessage)
        .setConfirmText("OK")
        .setConfirmClickListener(sweetAlertDialog -> {
          pDialog.dismissWithAnimation();
        })
        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
  }

}
