package com.sendkoin.customer.Payment.QRPayment;


import android.util.Log;

import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.sendkoin.api.AcceptTransactionRequest;
import com.sendkoin.api.QrCode;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.PaymentService;

import java.util.UUID;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by warefhaque on 5/23/17.
 */

public class QRScannerPresenter implements QRScannerContract.Presenter {

  private static final String TAG = "QRScannerPresenter";
  private QRScannerContract.View view;
  private LocalPaymentDataStore localPaymentDataStore;
  private PaymentService paymentService;
  public static final String MERCHANT_NAME = "merchant_name";
  public static final String SALE_AMOUNT = "sale_amount";
  private Subscription subscription;

  // TODO: 6/4/17 need to fill in with proper error messages and codes
  public static final String INTERNAL_SERVER_ERROR = "HTTP 500 Internal Server Error";
  public static final String UNAUTHORIZED = "401";

  @Inject
  public QRScannerPresenter(QRScannerContract.View view,
                            LocalPaymentDataStore localPaymentDataStore,
                            PaymentService paymentService) {

    this.view = view;
    this.localPaymentDataStore = localPaymentDataStore;
    this.paymentService = paymentService;
  }

  /**
   * 1. Create the acceptTransactionObject and make call to api
   * to save payment
   * <p>
   * 2. Save the transaction object to realm
   *
   * @param qrCode - provided from the QR
   */
  @Override
  public void acceptTransaction(QrCode qrCode, int saleAmount) {
    // 1. create the transaction object
    AcceptTransactionRequest.Builder acceptTransactionRequest = new AcceptTransactionRequest.Builder();
    switch (qrCode.qr_type){
      case DYNAMIC:
        acceptTransactionRequest
            .idempotence_token(UUID.randomUUID().toString())
            .qr_token(qrCode.qr_token);
        break;
      case STATIC:
        if (saleAmount != -1){
          acceptTransactionRequest
              .idempotence_token(UUID.randomUUID().toString())
              .qr_token(qrCode.qr_token)
              .sale_amount(saleAmount);
        }
        else {
          Log.e(TAG, "Static: Qr sale amount = -1");
        }
        break;
    }

    // 2. save the transaction in the DB
    subscription = paymentService
        .acceptCurrentTransaction(acceptTransactionRequest.build())
        .subscribeOn(Schedulers.io())
        .flatMap(acceptTransactionResponse ->
            localPaymentDataStore.createTransaction(acceptTransactionResponse.transaction))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Transaction>() {
          @Override
          public void onCompleted() {
            Log.d(TAG, "on Completed!");
          }

          @Override
          public void onError(Throwable e) {
            view.showTransactionError(getErrorMessage(e));
            Log.e(TAG, e.getMessage());
          }

          @Override
          public void onNext(Transaction transaction) {
            view.showTransactionComplete(transaction);
          }
        });

  }


  @Override
  public void subscribe() {
    // happens when the scan is captured so dont need here
  }

  @Override
  public void unsubscribe() {
    if (subscription != null) {
      subscription.unsubscribe();
    }

  }

  @Override
  public void closeRealm() {
  }

  public String getErrorMessage(Throwable error) {
    if (error.getMessage().contains(INTERNAL_SERVER_ERROR)) {
      return "Declined. Internal Error.";
    } else {
      return "Declined. Please try again.";
    }
  }
}
