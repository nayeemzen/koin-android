package com.sendkoin.customer.Payment.QRPayment;


import android.util.Log;

import com.google.gson.Gson;
import com.sendkoin.api.AcceptTransactionRequest;
import com.sendkoin.api.AcceptTransactionResponse;
import com.sendkoin.api.QrCode;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.Data.Authentication.RealSessionManager;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;
import com.sendkoin.customer.Data.Payments.PaymentService;

import org.json.JSONException;
import org.json.JSONObject;

import java.awt.font.TextAttribute;
import java.text.SimpleDateFormat;
import java.util.Date;

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
  private RealSessionManager realSessionManager;
  public static final String MERCHANT_NAME = "merchant_name";
  public static final String SALE_AMOUNT = "sale_amount";
  private Subscription subscription;

  @Inject
  public QRScannerPresenter(QRScannerContract.View view,
                            LocalPaymentDataStore localPaymentDataStore,
                            PaymentService paymentService,
                            RealSessionManager realSessionManager
                            ) {

    this.view = view;
    this.localPaymentDataStore = localPaymentDataStore;
    this.paymentService = paymentService;
    this.realSessionManager = realSessionManager;
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
    //1. create the transaction object
    long timeStamp = System.currentTimeMillis() / 1000L;

    AcceptTransactionRequest acceptTransactionRequest = new AcceptTransactionRequest.Builder()
        .created_at(timeStamp)
        .transaction_token(transactionToken)
        .idempotence_token("waref")
        .build();

    //2. save the transaction in the DB
    subscription = paymentService
        .acceptCurrentTransaction("Bearer " + realSessionManager.getSessionToken(), acceptTransactionRequest)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<AcceptTransactionResponse>() {
          @Override
          public void onCompleted() {
            Log.d(TAG, "on Completed!");
          }

          @Override
          public void onError(Throwable e) {
            view.showTransactionError();
            Log.e(TAG, e.getMessage());
          }

          @Override
          public void onNext(AcceptTransactionResponse acceptTransactionResponse) {
            localPaymentDataStore
                .createPayment(RealmTransaction
                    .transactionToRealmTransaction(acceptTransactionResponse.transaction));


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

}
