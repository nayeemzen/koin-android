package com.sendkoin.customer.Payment;

import android.util.Log;
import android.widget.Toast;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.sendkoin.api.ListTransactionsResponse;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.Data.Authentication.RealSessionManager;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;
import com.sendkoin.customer.Data.Payments.PaymentRepository;
import com.sendkoin.customer.Data.Payments.PaymentService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.realm.RealmResults;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

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
  private CompositeSubscription compositeSubscription = new CompositeSubscription();

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
  public void loadTranactionsFromDBAndSave() {
    long lastSeen = localPaymentDataStore.getLastSeenTransaction();
    Subscription subscription = paymentRepository
        .getAllPayments(paymentService, "Bearer " + realSessionManager.getSessionToken(), lastSeen)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<ListTransactionsResponse>() {
          @Override
          public void onCompleted() {
//                     Log.d(TAG, "Completed DB Call!");
          }

          @Override
          public void onError(Throwable e) {
            if (e != null) {
              Toast.makeText(view.getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT)
                  .show();
              Log.e(TAG, "ERROR - " + e.getMessage());
            }
          }

          @Override
          public void onNext(ListTransactionsResponse listTransactionsResponse) {

            List<Transaction> transactions = listTransactionsResponse.transactions;
            // you save the items here, on resume calls the realm db  and updates the view
            localPaymentDataStore.saveAllTransactions(RealmTransaction
                .transactionListToRealmTranscationList(transactions))
                .subscribe(realmAsyncTask -> Log.d(TAG, "Saved from DB to realm!"));
          }

        });

    compositeSubscription.add(subscription);

  }


  /**
   * Debugging purposes
   */

  @Override
  public void deleteAll() {
    localPaymentDataStore.deleteAllTranscations();
  }


  public HashMap<String,
      List<RealmTransaction>> groupTransactionsIntoHashMap(List<RealmTransaction> realmTransactions) {
    HashMap<String, List<RealmTransaction>> groupedResult = new HashMap<>();

    for (RealmTransaction realmTransaction : realmTransactions) {
      if (groupedResult.containsKey(realmTransaction.getCreatedAt())) {
        groupedResult.get(realmTransaction.getCreatedAt()).add(realmTransaction);
      } else {
        List<RealmTransaction> realmTransactionList = new ArrayList<>();
        realmTransactionList.add(realmTransaction);
        groupedResult.put(realmTransaction.getCreatedAt(), realmTransactionList);
      }
    }
    return groupedResult;
  }

  private void loadTransactionsFromRealm() {
   Subscription subscription = localPaymentDataStore.getAllTransactions()
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
            view.showPaymentItems(groupTransactionsIntoHashMap(items));
          }
        });

    compositeSubscription.add(subscription);
  }

  @Override
  public void subscribe() {
    loadTransactionsFromRealm();
    // RX will automatically update the view again when changes are saved in the background
    loadTranactionsFromDBAndSave();
  }

  @Override
  public void unsubscribe() {
    if (compositeSubscription != null) {
      compositeSubscription.clear();
    }
  }

  @Override
  public void closeRealm() {
    if (localPaymentDataStore != null) {
      localPaymentDataStore.close();
    }
  }
}
