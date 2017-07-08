package com.sendkoin.customer.Data.Payments;

import com.sendkoin.api.AcceptTransactionRequest;
import com.sendkoin.api.AcceptTransactionResponse;
import com.sendkoin.api.InitiateStaticTransactionRequest;
import com.sendkoin.api.InitiateStaticTransactionResponse;
import com.sendkoin.api.ListTransactionsRequest;
import com.sendkoin.api.ListTransactionsResponse;
import com.sendkoin.api.TransactionDetail;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by warefhaque on 5/30/17.
 */

public interface PaymentService {
  @POST("transactions/customer/list/")
  Observable<ListTransactionsResponse>
  getAllPayments(@Body ListTransactionsRequest listTransactionsRequest,
                 @Query("page") int pageNumber);

  @POST("transactions/customer/accept/")
  Observable<AcceptTransactionResponse>
  acceptCurrentTransaction(@Body AcceptTransactionRequest acceptTransactionRequest);

  @POST("transactions/customer/initiate/")
  Observable<InitiateStaticTransactionResponse>
  initiateCurrentTransaction(@Body InitiateStaticTransactionRequest initiateStaticTransactionRequest);

  @GET("transactions/customer/{token}/")
  Observable<TransactionDetail>
  getAllItems(@Path("token") String transactionToken);
}
