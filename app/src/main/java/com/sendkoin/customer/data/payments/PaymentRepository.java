package com.sendkoin.customer.data.payments;

import com.sendkoin.api.ListTransactionsRequest;
import com.sendkoin.api.ListTransactionsResponse;
import com.sendkoin.api.QueryParameters;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.data.payments.Local.LocalPaymentDataStore;

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
  public Observable<Transaction> createPayment(Transaction transaction) {
    return localPaymentDataStore.createTransaction(transaction);
  }

  @Override
  public Observable<ListTransactionsResponse> getAllPayments(PaymentService paymentService,
                                                             QueryParameters queryParameters,
                                                             int pageNumber) {
    ListTransactionsRequest listTransactionsRequest = new ListTransactionsRequest.Builder()
        .query_parameters(queryParameters)
        .build();
    return paymentService.getAllPayments(listTransactionsRequest, pageNumber);
  }
}
