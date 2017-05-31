package com.sendkoin.customer.Data.Payments.Local;

import com.sendkoin.customer.Data.Payments.Models.Payment;
import com.sendkoin.customer.Data.Payments.PaymentService;

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

    public Observable<RealmAsyncTask> createPayment(Payment payment) {
        return Observable.fromCallable(() -> realm.executeTransactionAsync(realm1 -> realm1.insert(payment)));
    }


    public Observable<RealmResults<Payment>> getAllTransactions() {
       return realm.where(Payment.class).findAllAsync().asObservable();
    }

    public Observable<RealmAsyncTask> saveAllTransactions(List<Payment> payments){
        return Observable.fromCallable(() -> realm.executeTransactionAsync(realm1 -> realm1.insert(payments)));
    }

    /**
     * Debugging purposes
     */
    public void deleteAllTranscations(){
        realm.executeTransaction(realm1 -> realm1.delete(Payment.class));
    }

    public void close(){
      realm.close();
    }
}
