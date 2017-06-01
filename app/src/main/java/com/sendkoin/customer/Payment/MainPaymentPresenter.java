package com.sendkoin.customer.Payment;

import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.sendkoin.api.ListTransactionsResponse;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.Data.Authentication.RealSessionManager;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;
import com.sendkoin.customer.Data.Payments.PaymentRepository;
import com.sendkoin.customer.Data.Payments.PaymentService;
import com.sendkoin.customer.Payment.QRPayment.QRScannerPresenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.realm.RealmResults;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by warefhaque on 5/20/17.
 */

public class MainPaymentPresenter implements MainPaymentContract.Presenter {
  private static final String TAG = "MainPaymentPresenter";
  private MainPaymentContract.View view;
  private LocalPaymentDataStore localPaymentDataStore;
  private PaymentRepository paymentRepository;
  private PaymentService paymentService;
  private RealSessionManager realSessionManager;
  private Subscription subscription;

  // need local and payment repo for calls
  @Inject
  public MainPaymentPresenter(MainPaymentContract.View view,
                              LocalPaymentDataStore localPaymentDataStore,
                              PaymentRepository paymentRepository,
                              PaymentService paymentService,
                              RealSessionManager realSessionManager) {

    this.view = view;
    this.localPaymentDataStore = localPaymentDataStore;
    this.paymentRepository = paymentRepository;
    this.paymentService = paymentService;
    this.realSessionManager = realSessionManager;
  }

  @Override
  public void loadItemsFromDatabase() {
    subscription = paymentRepository
        .getAllPayments(paymentService, "Bearer " + realSessionManager.getSessionToken())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<ListTransactionsResponse>() {
          @Override
          public void onCompleted() {
//                     Log.d(TAG, "Completed DB Call!");
          }

          @Override
          public void onError(Throwable e) {
            Log.e(TAG, "ERROR - " + e.getMessage());
          }

          @Override
          public void onNext(ListTransactionsResponse listTransactionsResponse) {

            List<Transaction> transactions = listTransactionsResponse.transactions;
            view.showPaymentItems(groupPaymentsIntoHashMap(RealmTransaction
                .transactionListToRealmTranscationList(transactions)));

            localPaymentDataStore.saveAllTransactions(RealmTransaction
                .transactionListToRealmTranscationList(transactions))
                .subscribe(realmAsyncTask -> Log.d(TAG, "Saved from DB to realm!"));
          }

        });

  }

  /**
   * Debugging purposes
   */

  @Override
  public void deleteAll() {
    localPaymentDataStore.deleteAllTranscations();
  }


  public HashMap<String, List<RealmTransaction>> groupPaymentsIntoHashMap(List<RealmTransaction> realmTransactions) {
    HashMap<String, List<RealmTransaction>> groupedResult = new HashMap<>();

    for (RealmTransaction realmTransaction : realmTransactions) {
      if (groupedResult.containsKey(realmTransaction.getDate())) {
        groupedResult.get(realmTransaction.getDate()).add(realmTransaction);
      } else {
        List<RealmTransaction> realmTransactionList = new ArrayList<>();
        realmTransactionList.add(realmTransaction);
        groupedResult.put(realmTransaction.getDate(), realmTransactionList);
      }
    }
    return groupedResult;
  }

  private void loadItemsFromRealm() {
    subscription = localPaymentDataStore.getAllTransactions()
        .subscribe(new Subscriber<RealmResults<RealmTransaction>>() {
          @Override
          public void onCompleted() {
            Log.e(TAG, "Completed the load!");
          }

          @Override
          public void onError(Throwable e) {
            Log.e(TAG, e.getMessage());
          }

          @Override
          public void onNext(RealmResults<RealmTransaction> realmTransactions) {
            List<RealmTransaction> items = Stream.of(realmTransactions).collect(Collectors.toList());
            view.showPaymentItems(groupPaymentsIntoHashMap(items));
          }
        });
  }

}
