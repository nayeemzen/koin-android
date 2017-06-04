package com.sendkoin.customer.Data.Payments;

import com.sendkoin.api.ListTransactionsResponse;
import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;

import io.realm.RealmAsyncTask;
import rx.Observable;

/**
 * Created by warefhaque on 5/20/17.
 * Will call LocalPaymentDataStore and RemotePaymentDataStore
 */

public interface PaymentDataStore {

    Observable<Boolean> createPayment(RealmTransaction realmTransaction);

    Observable<ListTransactionsResponse> getAllPayments(PaymentService paymentService, String authToken, long lastSeen);
}
