package com.sendkoin.customer.Data.Payments;

import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.Models.Payment;

import io.realm.RealmAsyncTask;
import io.realm.RealmResults;
import rx.Observable;

/**
 * Created by warefhaque on 5/20/17.
 */

public class PaymentRepository implements PaymentDataStore{


    private LocalPaymentDataStore localPaymentDataStore;

    public PaymentRepository(LocalPaymentDataStore localPaymentDataStore) {
        this.localPaymentDataStore = localPaymentDataStore;
    }

    @Override
    public Observable<RealmAsyncTask> createPayment(Payment payment) {
        return localPaymentDataStore.createPayment(payment);
    }

    @Override
    public Observable<RealmResults<Payment>> getAllPayments() {
        return localPaymentDataStore.getAllTransactions();
    }
}
