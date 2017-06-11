package com.sendkoin.customer.ui;

import com.sendkoin.api.AcceptTransactionRequest;
import com.sendkoin.api.AcceptTransactionResponse;
import com.sendkoin.api.ListTransactionsResponse;
import com.sendkoin.api.Merchant;
import com.sendkoin.api.QueryParameters;
import com.sendkoin.api.Transaction;
import com.sendkoin.api.TransactionDetail;
import com.sendkoin.customer.Data.Payments.PaymentService;
import com.sendkoin.customer.Utility.ByteToken;

import java.util.LinkedHashSet;
import java.util.Set;

import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by warefhaque on 6/9/17.
 */

public class FakeTransactionService implements PaymentService {

  private final Set<String> transactionTokenSet = new LinkedHashSet<>();

  @Override
  public Observable<ListTransactionsResponse> getAllPayments(String token,
                                                             QueryParameters queryParameters,
                                                             int pageNumber) {
    return null;
  }

  @Override
  public Observable<AcceptTransactionResponse> acceptCurrentTransaction(String token,
                                                                        AcceptTransactionRequest acceptTransactionRequest) {
    if (transactionTokenSet.contains(acceptTransactionRequest.transaction_token)) {

      Merchant merchant = new Merchant.Builder()
          .store_name("Pizza Hut")
          .store_type("Restaurant")
          .build();

      Transaction transaction = new Transaction.Builder()
          .amount(567)
          .created_at(acceptTransactionRequest.created_at)
          .merchant(merchant)
          .token(acceptTransactionRequest.transaction_token)
          .build();

      AcceptTransactionResponse acceptTransactionResponse = new AcceptTransactionResponse.Builder()
          .transaction(transaction)
          .build();
      return Observable.just(acceptTransactionResponse);
    }
    else{
      return null;
    }
  }

  @Override
  public Observable<TransactionDetail> getAllItems(String authToken,
                                                   String transactionToken) {
    return null;
  }

  public String initiateTransaction() {
    String transactionToken = ByteToken.generate();
    transactionTokenSet.add(transactionToken);
    return transactionToken;
  }
}
