package com.sendkoin.customer.data.payments.Models.inventory;

/**
 * Created by warefhaque on 10/5/17.
 */

public class InventoryItemLocal extends InventoryListItem {

  public String itemName;
  public String itemDescription;
  public String itemImageUrl;
  public int itemPrice;
  public int quantity;
  public String additionalNotes;
  public long inventoryItemId;

  public InventoryItemLocal setInventoryItemId(long inventoryItemId) {
    this.inventoryItemId = inventoryItemId;
    return this;
  }

  public InventoryItemLocal setQuantity(int quantity) {
    this.quantity = quantity;
    return this;
  }

  public InventoryItemLocal setAdditionalNotes(String additionalNotes) {
    this.additionalNotes = additionalNotes;
    return this;
  }

  public InventoryItemLocal setItemName(String itemName) {
    this.itemName = itemName;
    return this;
  }

  public InventoryItemLocal setItemDescription(String itemDescription) {
    this.itemDescription = itemDescription;
    return this;
  }

  public InventoryItemLocal setItemImageUrl(String itemImageUrl) {
    this.itemImageUrl = itemImageUrl;
    return this;
  }

  public InventoryItemLocal setItemPrice(int itemPrice) {
    this.itemPrice = itemPrice;
    return this;
  }

  @Override
  public int getType() {
    return INVENTORY_ITEM;
  }
}