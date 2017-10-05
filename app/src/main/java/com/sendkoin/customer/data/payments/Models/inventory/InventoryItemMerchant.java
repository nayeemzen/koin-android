package com.sendkoin.customer.data.payments.Models.inventory;

/**
 * Created by warefhaque on 10/5/17.
 */

public class InventoryItemMerchant extends InventoryListItem {

  String merchantName;

  public InventoryItemMerchant setMerchantName(String merchantName) {
    this.merchantName = merchantName;
    return this;
  }

  @Override

  public int getType() {
    return MERCHANT_INFO_ITEM;
  }

  public String getMerchantName() {
    return merchantName;
  }
}