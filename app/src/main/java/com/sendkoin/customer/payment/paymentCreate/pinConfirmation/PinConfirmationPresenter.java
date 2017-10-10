package com.sendkoin.customer.payment.paymentCreate.pinConfirmation;

import android.util.Log;

import com.sendkoin.api.AcceptTransactionRequest;
import com.sendkoin.api.InitiateStaticTransactionRequest;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.data.payments.Local.LocalOrderDataStore;
import com.sendkoin.customer.data.payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.data.payments.PaymentService;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by warefhaque on 7/29/17.
 */

public class PinConfirmationPresenter implements PinConfirmationContract.Presenter{

  public static final String TAG = PinConfirmationPresenter.class.getSimpleName();
  private static final String INTERNAL_SERVER_ERROR = "HTTP 500 Internal Server Error";
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private PinConfirmationContract.View view;
  private LocalOrderDataStore localOrderDataStore;
  private LocalPaymentDataStore localPaymentDataStore;
  private PaymentService paymentService;

  public PinConfirmationPresenter(PinConfirmationContract.View view,
                                  LocalOrderDataStore localOrderDataStore,
                                  LocalPaymentDataStore localPaymentDataStore,
                                  PaymentService paymentService) {
    this.view = view;
    this.localOrderDataStore = localOrderDataStore;
    this.localPaymentDataStore = localPaymentDataStore;
    this.paymentService = paymentService;
  }

  @Override
  public void subscribe() {
    // taken care of in acceptTransaction
  }

  @Override
  public void unsubscribe() {
    compositeSubscription.clear();
  }


  public String getErrorMessage(Throwable error) {
    if (error.getMessage().contains(INTERNAL_SERVER_ERROR)) {
      return "Declined. Internal Error.";
    } else {
      return "Declined. Please try again.";
    }
  }

  @Override
  public void processStaticTransactionRequest(InitiateStaticTransactionRequest initiateStaticTransactionRequest) {
    Subscription subscription = paymentService
        .initiateCurrentTransaction(initiateStaticTransactionRequest)
        .subscribeOn(Schedulers.io())
        .flatMap(initiateStaticTransactionResponse ->
            localPaymentDataStore.createTransaction(initiateStaticTransactionResponse
                .order
                .transaction))
        .flatMap(transaction -> localOrderDataStore.removeAllOrders(transaction))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Transaction>() {
          @Override
          public void onCompleted() {
            Log.d(TAG, "on Completed!");
          }

          @Override
          public void onError(Throwable e) {
            Log.e(TAG, e.getMessage());
            view.showTransactionError(getErrorMessage(e));
          }

          @Override
          public void onNext(Transaction transaction) {
            view.showTransactionReciept(transaction);
          }
        });

    compositeSubscription.add(subscription);
  }

  @Override
  public void processDynamicTransactionRequest(AcceptTransactionRequest acceptTransactionRequest) {
    Subscription subscription = paymentService
        .acceptCurrentTransaction(acceptTransactionRequest)
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
            view.showTransactionReciept(transaction);
          }
        });

    compositeSubscription.add(subscription);
  }
}
