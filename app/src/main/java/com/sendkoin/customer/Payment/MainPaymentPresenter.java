package com.sendkoin.customer.Payment;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.sendkoin.api.ListTransactionsResponse;
import com.sendkoin.api.QueryParameters;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.Data.Authentication.RealSessionManager;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;
import com.sendkoin.customer.Data.Payments.PaymentRepository;
import com.sendkoin.customer.Data.Payments.PaymentService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
  public static final String PAGE_NUM = "page_num";
  private MainPaymentContract.View view;
  private LocalPaymentDataStore localPaymentDataStore;
  private PaymentRepository paymentRepository;
  private PaymentService paymentService;
  private RealSessionManager realSessionManager;
  private SharedPreferences sharedPreferences;
  private Subscription subscription;
  private CompositeSubscription compositeSubscription = new CompositeSubscription();
  private Boolean hasNextPage;
  private int pageNumber = 1;

  // need local and payment repo for calls
  @Inject
  public MainPaymentPresenter(MainPaymentContract.View view,
                              LocalPaymentDataStore localPaymentDataStore,
                              PaymentRepository paymentRepository,
                              PaymentService paymentService,
                              RealSessionManager realSessionManager,
                              SharedPreferences sharedPreferences) {

    this.view = view;
    this.localPaymentDataStore = localPaymentDataStore;
    this.paymentRepository = paymentRepository;
    this.paymentService = paymentService;
    this.realSessionManager = realSessionManager;
    this.sharedPreferences = sharedPreferences;
  }

  @Override
  public void loadTransactionsFromDBAndSave(boolean fetchWithLastSeen) {
    // if fetchWithLastSeen then fetch recent ones otherwise everything
    QueryParameters.Builder queryParametersBuilder = new QueryParameters.Builder();

    if (fetchWithLastSeen) {
      queryParametersBuilder.updates_after(localPaymentDataStore.getLastSeenTransaction());
      queryParametersBuilder.order(QueryParameters.Order.ASCENDING);
    }
    else {
      queryParametersBuilder.updates_before(localPaymentDataStore.getEarliestSeenTransaction());
      queryParametersBuilder.order(QueryParameters.Order.DESCENDING);
    }

    Subscription subscription = paymentRepository
        .getAllPayments(paymentService,
            "Bearer " + realSessionManager.getSessionToken(),
            queryParametersBuilder.build(),
            sharedPreferences.getInt(PAGE_NUM,1))
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

            //1. get the transactions
            List<Transaction> transactions = listTransactionsResponse.transactions;

            //2. set the page number for the next query
            setPageNumber(listTransactionsResponse);

            //3. save the items in realm
            localPaymentDataStore.saveAllTransactions(RealmTransaction
                .transactionListToRealmTranscationList(transactions))
                .subscribe();
            //4. wait for RX to update the view in loadTransactionsFromRealm!
          }

        });

    compositeSubscription.add(subscription);

  }

  private void setPageNumber(ListTransactionsResponse listTransactionsResponse) {
    // see if you have more pages and increment the page number
    hasNextPage = listTransactionsResponse.has_next_page;
    pageNumber = sharedPreferences.getInt(PAGE_NUM, 1);
    pageNumber = (hasNextPage) ? pageNumber + 1 : pageNumber;
    sharedPreferences.edit().putInt("page_num", pageNumber).apply();
  }


  /**
   * Debugging purposes
   */

  @Override
  public void deleteAll() {
    localPaymentDataStore.deleteAllTranscations();
  }

  @Override
  public void fetchHistory() {
    loadTransactionsFromDBAndSave(false);
  }

  @Override
  public boolean hasNextPage() {
    return hasNextPage;
  }


  public LinkedHashMap<String,
      List<RealmTransaction>> groupTransactionsIntoHashMap(List<RealmTransaction> realmTransactions) {
    LinkedHashMap<String, List<RealmTransaction>> groupedResult = new LinkedHashMap<>();

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
        .filter(RealmResults::isLoaded)
        .subscribe(new Subscriber<RealmResults<RealmTransaction>>() {
          @Override
          public void onCompleted() {
//            Log.e(TAG, "Completed the load!");
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
    //fetching with last seen = last seen payment
    loadTransactionsFromDBAndSave(true);
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
