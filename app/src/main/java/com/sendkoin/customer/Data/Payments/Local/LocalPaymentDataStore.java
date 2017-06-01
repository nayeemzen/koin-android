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

public class LocalPaymentDataStore{

    private Realm realm;

    @Inject
    public LocalPaymentDataStore(Realm realm) {

        this.realm = realm;
    }

    public Observable<RealmAsyncTask> createPayment(RealmTransaction realmTransaction) {
        return Observable.fromCallable(() -> realm.executeTransactionAsync(realm1 -> realm1.insert(realmTransaction)));
    }


    public Observable<RealmResults<RealmTransaction>> getAllTransactions() {
       return realm.where(RealmTransaction.class).findAllAsync().asObservable();
    }

    public Observable<RealmAsyncTask> saveAllTransactions(List<RealmTransaction> realmTransactions){
        return Observable.fromCallable(() -> realm.executeTransactionAsync(realm1 -> realm1.insert(realmTransactions)));
    }

    /**
     * Debugging purposes
     */
    public void deleteAllTranscations(){
        realm.executeTransaction(realm1 -> realm1.delete(RealmTransaction.class));
    }

    public void close(){
      realm.close();
    }
}
