package com.sendkoin.customer.Payment.TransactionDetails;

import android.util.Log;

import com.sendkoin.api.SaleItem;
import com.sendkoin.api.Transaction;
import com.sendkoin.api.TransactionDetail;
import com.sendkoin.customer.BasePresenter;
import com.sendkoin.customer.Data.Authentication.RealSessionManager;
import com.sendkoin.customer.Data.Payments.PaymentService;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by warefhaque on 6/5/17.
 */

public class TransactionDetailsPresenter implements TransactionDetailsContract.Presenter{


  private static final String TAG = TransactionDetailsPresenter.class.getSimpleName();
  private PaymentService paymentService;
  private TransactionDetailsContract.View view;
  private RealSessionManager realSessionManager;
  private Subscription subscription;

  public TransactionDetailsPresenter(PaymentService paymentService,
                                     TransactionDetailsContract.View view,
                                     RealSessionManager realSessionManager) {

    this.paymentService = paymentService;
    this.view = view;
    this.realSessionManager = realSessionManager;
  }

  @Override
  public void subscribe() {
    //will be empty
  }

  @Override
  public void unsubscribe() {
    if (subscription != null){
      subscription.unsubscribe();
    }
  }

  @Override
  public void closeRealm() {
    // no applicable here
  }

  @Override
  public void fetchTransactionDetails(String transactionToken) {

    subscription = paymentService
        .getAllItems("Bearer "+realSessionManager.getSessionToken(),transactionToken)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<TransactionDetail>() {
          @Override
          public void onCompleted() {
            Log.d(TAG, "loaded trans details");
          }

          @Override
          public void onError(Throwable e) {
            //show the error on the view
            Log.e(TAG, e.getMessage());
          }

          @Override
          public void onNext(TransactionDetail transactionDetail) {
            view.showDetailedTransaction(transactionDetail);
          }
        });

  }
}
