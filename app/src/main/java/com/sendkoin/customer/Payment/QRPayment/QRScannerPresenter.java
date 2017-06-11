package com.sendkoin.customer.Payment.QRPayment;


import android.util.Log;

import com.sendkoin.api.AcceptTransactionRequest;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.Data.Authentication.SessionManager;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.PaymentService;

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
  private SessionManager sessionManager;
  public static final String MERCHANT_NAME = "merchant_name";
  public static final String SALE_AMOUNT = "sale_amount";
  private Subscription subscription;

  // TODO: 6/4/17 need to fill in with proper error messages and codes
  public static final String INTERNAL_SERVER_ERROR = "HTTP 500 Internal Server Error";
  public static final String UNAUTHORIZED = "401";

  @Inject
  public QRScannerPresenter(QRScannerContract.View view,
                            LocalPaymentDataStore localPaymentDataStore,
                            PaymentService paymentService,
                            SessionManager sessionManager) {

    this.view = view;
    this.localPaymentDataStore = localPaymentDataStore;
    this.paymentService = paymentService;
    this.sessionManager = sessionManager;
  }

  /**
   * 1. Create the acceptTransactionObject and make call to api
   * to save payment
   *
   * 2. Save the transaction object to realm
   *
   * @param transactionToken - provided from the QR
   */
  @Override
  public void createTransaction(String transactionToken) {
    // 1. create the transaction object
    long timeStamp = System.currentTimeMillis() / 1000L;

    AcceptTransactionRequest acceptTransactionRequest = new AcceptTransactionRequest.Builder()
        .created_at(timeStamp)
        .transaction_token(transactionToken)
        .idempotence_token("random_string")
        .build();

    // 2. save the transaction in the DB
    subscription = paymentService
        .acceptCurrentTransaction(acceptTransactionRequest)
        .subscribeOn(Schedulers.io())
        .flatMap(acceptTransactionResponse ->
            localPaymentDataStore.createPayment(acceptTransactionResponse.transaction))
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
            view.showTransactionComplete();
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
    if (localPaymentDataStore != null) {
      localPaymentDataStore.close();
    }
  }

  public String getErrorMessage(Throwable error) {
    if (error.getMessage().contains(INTERNAL_SERVER_ERROR)){
      return "Declined. Internal Error.";
    }else{
      return "Declined. Please try again.";
    }
  }
}
