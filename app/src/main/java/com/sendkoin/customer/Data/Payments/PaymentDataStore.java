package com.sendkoin.customer.Data.Payments;

import com.sendkoin.api.ListTransactionsResponse;
import com.sendkoin.customer.Data.Payments.Models.Payment;

import io.realm.RealmAsyncTask;
import io.realm.RealmResults;
import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by warefhaque on 5/20/17.
 * Will call LocalPaymentDataStore and RemotePaymentDataStore
 */

public interface PaymentDataStore {

    Observable<RealmAsyncTask> createPayment(Payment payment);

    Observable<ListTransactionsResponse> getAllPayments(PaymentService paymentService, String authToken);
}
