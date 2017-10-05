package com.sendkoin.customer.data.payments.Models.inventory;

/**
 * Created by warefhaque on 10/5/17.
 */

public class InventoryItemCategory extends InventoryListItem {

  String categoryName;

  public String getCategoryName() {
    return categoryName;
  }

  public InventoryItemCategory setCategoryName(String categoryName) {
    this.categoryName = categoryName;
    return this;
  }

  @Override
  public int getType() {
    return CATEGORY;
  }
}
