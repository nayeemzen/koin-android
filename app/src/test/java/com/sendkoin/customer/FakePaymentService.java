package com.sendkoin.customer;

import com.sendkoin.api.AcceptTransactionRequest;
import com.sendkoin.api.AcceptTransactionResponse;
import com.sendkoin.api.ListTransactionsRequest;
import com.sendkoin.api.ListTransactionsResponse;
import com.sendkoin.api.Transaction;
import com.sendkoin.api.TransactionDetail;
import com.sendkoin.customer.Data.Payments.PaymentService;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import retrofit2.http.Body;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by warefhaque on 6/14/17.
 */

public class FakePaymentService implements PaymentService {
  public static LinkedHashMap<String,Transaction> paymentEntityLinkedHashMap = new LinkedHashMap<>();
  @Override
  public Observable<ListTransactionsResponse> getAllPayments(
      @Body ListTransactionsRequest listTransactionsRequest,
      @Query("page") int pageNumber) {

    ListTransactionsResponse listTransactionsResponse = new ListTransactionsResponse.Builder()
        .transactions(new ArrayList<>())
        .has_next_page(false)
        .build();

    return Observable.just(listTransactionsResponse);
  }

  @Override
  public Observable<AcceptTransactionResponse> acceptCurrentTransaction(
      @Body AcceptTransactionRequest acceptTransactionRequest) {
    AcceptTransactionResponse acceptTransactionResponse = new AcceptTransactionResponse.Builder()
        .transaction(paymentEntityLinkedHashMap.get(acceptTransactionRequest.qr_token))
        .build();
    return Observable.just(acceptTransactionResponse);
  }

  @Override
  public Observable<TransactionDetail> getAllItems(@Path("token") String transactionToken) {
    return null;
  }
}
