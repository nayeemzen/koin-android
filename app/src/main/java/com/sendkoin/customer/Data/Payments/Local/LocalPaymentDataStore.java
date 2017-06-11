package com.sendkoin.customer.Data.Payments.Local;

import com.sendkoin.api.Transaction;
import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;

import static com.sendkoin.customer.Data.Payments.Models.RealmTransaction.toRealmTransactions;

/**
 * Created by warefhaque on 5/20/17.
 */

public class LocalPaymentDataStore {

  private Realm realm;

  @Inject
  public LocalPaymentDataStore(Realm realm) {

    this.realm = realm;
  }

  public Observable<Boolean> createPayment(RealmTransaction realmTransaction) {
    return Observable.fromCallable(() -> {
      realm.executeTransaction(realm1 -> realm1.insert(realmTransaction));
      return true;
    });
  }


  public Observable<RealmResults<RealmTransaction>> getAllTransactions() {
    return realm.where(RealmTransaction.class)
        .findAllSorted("createdAt", Sort.DESCENDING)
        .asObservable();
  }

  public Observable<List<Transaction>> saveAllTransactions(List<Transaction> transactions) {
    return Observable.fromCallable(() -> {
      Realm defaultRealm = Realm.getDefaultInstance();
      defaultRealm.executeTransaction(realm ->
          realm.insertOrUpdate(toRealmTransactions(transactions)));
      defaultRealm.close();
      return transactions;
    });
  }

  public long getLastSeenTransaction() {
    Number lastSeen = realm.where(RealmTransaction.class).max("createdAt");
    return (lastSeen == null) ? 0 : lastSeen.longValue();
  }

  public long getEarliestSeenTransaction(){
    Number earliestSeen = realm.where(RealmTransaction.class).min("createdAt");
    return (earliestSeen == null) ? 0 : earliestSeen.longValue();
  }


  /**
   * Debugging purposes
   */
  public void deleteAllTranscations() {
    realm.executeTransaction(realm1 -> realm1.delete(RealmTransaction.class));
  }

  public void close() {
    realm.close();
  }
}


//      for (RealmTransaction realmTransaction : realmTransactions) {
//        RealmTransaction existingTransaction = realm.where(RealmTransaction.class)
//            .equalTo("transactionToken", realmTransaction.getTransactionToken())
//            .findFirst();
//
//        if (existingTransaction == null) {
//          // no existing transactions
//          realm.executeTransaction(realm1 -> realm1.insert(realmTransaction));
//        }
//      }