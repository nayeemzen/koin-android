package com.sendkoin.customer.data.payments;

import com.sendkoin.api.GetInventoryResponse;

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
