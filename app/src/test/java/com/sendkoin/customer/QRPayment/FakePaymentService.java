package com.sendkoin.customer.QRPayment;

import com.sendkoin.api.AcceptTransactionRequest;
import com.sendkoin.api.AcceptTransactionResponse;
import com.sendkoin.api.ListTransactionsRequest;
import com.sendkoin.api.ListTransactionsResponse;
import com.sendkoin.api.Merchant;
import com.sendkoin.api.Transaction;
import com.sendkoin.api.TransactionDetail;
import com.sendkoin.customer.Data.Payments.PaymentService;

import retrofit2.http.Body;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by warefhaque on 6/14/17.
 */

public class FakePaymentService implements PaymentService {
  @Override
  public Observable<ListTransactionsResponse> getAllPayments(@Body ListTransactionsRequest listTransactionsRequest, @Query("page") int pageNumber) {
    return null;
  }

  @Override
  public Observable<AcceptTransactionResponse> acceptCurrentTransaction(@Body AcceptTransactionRequest acceptTransactionRequest) {
    AcceptTransactionResponse acceptTransactionResponse = new AcceptTransactionResponse.Builder()
        .transaction(new Transaction.Builder()
            .token(acceptTransactionRequest.transaction_token)
            .amount(20)
            .merchant(new Merchant.Builder()
                .store_type("rest")
                .store_name("zen's shop")
                .build())
            .state(Transaction.State.COMPLETE)
            .created_at(1223423523423L)
            .build())
        .build();
    return Observable.just(acceptTransactionResponse);
  }

  @Override
  public Observable<TransactionDetail> getAllItems(@Path("token") String transactionToken) {
    return null;
  }

}
