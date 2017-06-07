package com.sendkoin.customer.Data.Payments;

import com.sendkoin.api.ListTransactionsResponse;
import com.sendkoin.api.QueryParameters;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;

import io.realm.RealmAsyncTask;
import rx.Observable;

/**
 * Created by warefhaque on 5/20/17.
 */

public class PaymentRepository implements PaymentDataStore {


  private LocalPaymentDataStore localPaymentDataStore;

  public PaymentRepository(LocalPaymentDataStore localPaymentDataStore) {
    this.localPaymentDataStore = localPaymentDataStore;
  }

  @Override
  public Observable<Boolean> createPayment(RealmTransaction realmTransaction) {
    return localPaymentDataStore.createPayment(realmTransaction);
  }

  @Override
  public Observable<ListTransactionsResponse> getAllPayments(PaymentService paymentService,
                                                             String authToken,
                                                             QueryParameters queryParameters,
                                                             int pageNumber) {
    return paymentService.getAllPayments(authToken, queryParameters, pageNumber);
  }
}
