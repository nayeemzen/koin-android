package com.sendkoin.customer.Payment;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.sendkoin.api.ListTransactionsResponse;
import com.sendkoin.api.QueryParameters;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.Data.Authentication.RealSessionManager;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;
import com.sendkoin.customer.Data.Payments.PaymentRepository;
import com.sendkoin.customer.Data.Payments.PaymentService;

import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;

import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.annimon.stream.Collectors.groupingBy;
import static com.annimon.stream.Collectors.toList;

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
  private CompositeSubscription compositeSubscription = new CompositeSubscription();

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
  }

  /**
   * Fetches latest transactions in ASCENDING order so that new transactions are always appended
   * on top as they come in. (e.g. pull-to-refresh)
   * <p>
   * Fetches the historical transactions in DESCENDING order so that old transactions are always
   * appended to the bottom as they come in. (e.g. load-on-scroll-down)
   * <p>
   * Currently loads all the pages eagerly, in page-sized chunks. This should be sufficient for
   * the customer application and Koin v1.
   *
   * @param fetchLatest If true, fetch transactions after the latest transaction in Realm.
   *                    If false, fetch the transactions before the earliest transaction in Realm.
   */
  @Override
  public void loadTransactionsFromServer(boolean fetchLatest) {
    QueryParameters.Builder queryParametersBuilder = new QueryParameters.Builder();
    long lastSeenTransaction = localPaymentDataStore.getLastSeenTransaction();

    if (fetchLatest && lastSeenTransaction > 0) {
      queryParametersBuilder.updates_after(lastSeenTransaction);
      queryParametersBuilder.order(QueryParameters.Order.ASCENDING);
    } else {
      long earliestSeenTransaction = localPaymentDataStore.getEarliestSeenTransaction();
      if (earliestSeenTransaction > 0) {
        queryParametersBuilder.updates_before(earliestSeenTransaction);
      }
      queryParametersBuilder.order(QueryParameters.Order.DESCENDING);
    }

    QueryParameters queryParameters = queryParametersBuilder.build();
    Subscription subscription = fetchTransactions(queryParameters, 1)
        .flatMap(listTransactionsResponse -> {
          List<Transaction> transactions = listTransactionsResponse.transactions;
          return localPaymentDataStore.saveAllTransactions(transactions);
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<List<Transaction>>() {
          @Override
          public void onCompleted() {
            Log.d(TAG, "Completed DB Call!");
          }

          @Override
          public void onError(Throwable e) {
            if (e != null) {
              // TODO(waref): Move this into a method in the view: e.g void showError();
              Toast.makeText(
                  view.getApplicationContext(),
                  e.getLocalizedMessage(),
                  Toast.LENGTH_SHORT).show();
              Log.e(TAG, "ERROR - " + e.getMessage());
            }
          }

          @Override
          public void onNext(List<Transaction> transactions) {
            Log.d(TAG, String.format("Saved %d transactions to realm", transactions.size()));
          }
        });

    compositeSubscription.add(subscription);
  }

  /**
   * Fetches the transactions from the server and if `has_next_page` is true, recursively is called
   * again until `has_next_page` is false.
   * <p>
   * Using `concatWith` to combine all the results of the
   * recursive calls into one observable.
   * <p>
   * ConcatMap ensures that there is no interleaving between mutliple observables from the multiple
   * recursive calls.
   *
   * @param queryParameters The parameters and predicates to define our query.
   * @param pageNumber      The pageNumber of the query associated with the above queryParameters.
   * @return An observable consisting of the combined transactions from the pages.
   */
  private Observable<ListTransactionsResponse> fetchTransactions(QueryParameters queryParameters,
                                                                 int pageNumber) {
    // TODO(waref): Use authenticator and interceptor in OkHttp. Don't pass authentication header
    // directly in retrofit.
    String authToken = "Bearer " + realSessionManager.getSessionToken();
    return paymentRepository
        .getAllPayments(paymentService, authToken, queryParameters, pageNumber)
        .subscribeOn(Schedulers.io())
        .concatMap(listTransactionsResponse -> {
          if (listTransactionsResponse.has_next_page) {
            return Observable.just(listTransactionsResponse)
                .concatWith(fetchTransactions(queryParameters, pageNumber + 1));
          } else {
            return Observable.just(listTransactionsResponse);
          }
        });
  }

  /**
   * Debugging purposes.
   */
  @Override
  public void deleteAllTransactions() {
    localPaymentDataStore.deleteAllTranscations();
  }

  private LinkedHashMap<String, List<RealmTransaction>> groupTransactionsByCreatedAt(
      List<RealmTransaction> realmTransactions) {
    return Stream.of(realmTransactions).collect(
        groupingBy(RealmTransaction::getCreatedAt, LinkedHashMap::new, toList()));
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
            List<RealmTransaction> items = Stream.of(realmTransactions).collect(toList());
            view.showPaymentItems(groupTransactionsByCreatedAt(items));
          }
        });

    compositeSubscription.add(subscription);
  }

  /**
   * Kick-off the realm loading while the server fetch happens on the background. The server will
   * fetch the new transactions/old missing transactions and insert them to Realm. Since we're using
   * an observable query on Realm, any new insertions should update the UI automatically.
   */
  @Override
  public void subscribe() {
    loadTransactionsFromRealm();
    loadTransactionsFromServer(true);
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
