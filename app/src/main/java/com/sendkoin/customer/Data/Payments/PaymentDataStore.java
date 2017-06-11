package com.sendkoin.customer.Data.Payments;

import com.sendkoin.api.ListTransactionsResponse;
import com.sendkoin.api.QueryParameters;
import com.sendkoin.api.Transaction;

import rx.Observable;

/**
 * Created by warefhaque on 5/20/17.
 * Will call LocalPaymentDataStore and RemotePaymentDataStore
 */

public interface PaymentDataStore {

  Observable<Transaction> createPayment(Transaction transaction);

  Observable<ListTransactionsResponse> getAllPayments(PaymentService paymentService,
                                                      QueryParameters queryParameters,
                                                      int pageNumber);
}
