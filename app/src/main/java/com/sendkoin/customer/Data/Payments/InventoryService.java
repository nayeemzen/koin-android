package com.sendkoin.customer.Data.Payments;

import com.sendkoin.api.GetInventoryResponse;
import com.sendkoin.api.TransactionDetail;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by warefhaque on 7/9/17.
 */

public interface InventoryService {

  @GET("inventory")
  Observable<GetInventoryResponse>
  getAllInventoryItems(@Query("qr_token") String qrToken);
}
