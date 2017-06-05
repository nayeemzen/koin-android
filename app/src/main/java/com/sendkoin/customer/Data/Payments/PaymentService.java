package com.sendkoin.customer.Data.Payments;

import com.sendkoin.api.AcceptTransactionRequest;
import com.sendkoin.api.AcceptTransactionResponse;
import com.sendkoin.api.ListTransactionsResponse;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.Data.Authentication.RealSessionManager;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by warefhaque on 5/30/17.
 */


public interface PaymentService {
  @GET("transactions/customer/list/")
  Observable<ListTransactionsResponse>
  getAllPayments(@Header("Authorization") String token,
                 @Query("updatesSince") long lastSeen,
                 @Query("page") int pageNumber);

  @POST("transactions/customer/accept/")
  Observable<AcceptTransactionResponse>
  acceptCurrentTransaction(@Header("Authorization") String token,
                           @Body AcceptTransactionRequest acceptTransactionRequest);

}
