package com.sendkoin.customer.Data.Payments.Local;

import com.sendkoin.customer.Data.Payments.Models.Payment;

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

    public Observable<RealmAsyncTask> createPayment(Payment payment) {
        return Observable.fromCallable(() -> realm.executeTransactionAsync(realm1 -> realm1.insert(payment)));
    }


    public Observable<RealmResults<Payment>> getAllTransactions() {
       return realm.where(Payment.class).findAllAsync().asObservable();
    }

    public void close(){
      realm.close();
    }
}
