package com.sendkoin.customer;

import com.sendkoin.api.GetInventoryResponse;
import com.sendkoin.customer.data.payments.InventoryService;

import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by warefhaque on 7/23/17.
 */

public class FakeInventoryService implements InventoryService {
  @Override
  public Observable<GetInventoryResponse> getAllInventoryItems(@Query("qr_token") String qrToken) {
    return null;
  }
}
