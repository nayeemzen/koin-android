package com.sendkoin.customer.payment.paymentDetails;

import com.intrusoft.sectionedrecyclerview.Section;
import com.sendkoin.api.Transaction;

import java.util.List;

/**
 * Created by warefhaque on 6/26/17.
 */

public class DetailedRecieptHeader implements Section<Item> {

  List<Item> childList;
  Transaction transaction;

  public DetailedRecieptHeader() {
  }

  public DetailedRecieptHeader setChildList(List<Item> childList) {
    this.childList = childList;
    return this;
  }

  public DetailedRecieptHeader setTransaction(Transaction transaction) {
    this.transaction = transaction;
    return this;
  }

  @Override
  public List<Item> getChildItems() {
    return childList;
  }
}
