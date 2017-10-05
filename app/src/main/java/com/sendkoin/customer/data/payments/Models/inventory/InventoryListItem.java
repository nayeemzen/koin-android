package com.sendkoin.customer.data.payments.Models.inventory;

/**
 * Created by warefhaque on 10/5/17.
 */

public abstract class InventoryListItem {
  public static final int CATEGORY = 0;
  public static final int INVENTORY_ITEM = 1;
  public static final int MERCHANT_INFO_ITEM = 2;
  public abstract int getType();
}