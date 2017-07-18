package com.sendkoin.customer.Payment.TransactionDetails;

/**
 * Created by warefhaque on 6/26/17.
 */

public class Item {
  int price;
  int quantity;
  String itemName;

  public Item() {
  }

  public Item setPrice(int price) {
    this.price = price;
    return this;
  }

  public Item setQuantity(int quantity) {
    this.quantity = quantity;
    return this;
  }

  public Item setItemName(String itemName) {
    this.itemName = itemName;
    return this;
  }
}
