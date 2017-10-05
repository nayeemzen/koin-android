package com.sendkoin.customer.data.payments;

import com.sendkoin.api.GetInventoryResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface InventoryService {

  /**
   * Get all inventory items the merchant has associated with the qr_token
   * Inventory linked with qr_token as different branches of restaurants may have different items
   * @param qrToken - identify the inventory on DB
   * @return GetInventoryResponse
   *
   * @see GetInventoryResponse
   */
  @GET("inventory")
  Observable<GetInventoryResponse>
  getAllInventoryItems(@Query("qr_token") String qrToken);
}
