package com.sendkoin.customer.Data.Payments.Local;

import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;

import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;
import rx.Observable;

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
    return realm.where(RealmTransaction.class).findAll().asObservable();
  }

  public Observable<Boolean> saveAllTransactions(List<RealmTransaction> realmTransactions) {
    return Observable.fromCallable(() -> {
      realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(realmTransactions));
      return true;
    });
  }

  public long getLastSeenTransaction() {
    Number lastSeen = realm.where(RealmTransaction.class).max("createdAt");
    return (lastSeen == null) ? 0 : lastSeen.longValue();
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