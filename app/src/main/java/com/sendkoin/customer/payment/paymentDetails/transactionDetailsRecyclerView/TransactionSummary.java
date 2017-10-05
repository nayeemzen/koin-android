package com.sendkoin.customer.payment.paymentDetails.transactionDetailsRecyclerView;

import com.intrusoft.sectionedrecyclerview.Section;
import com.sendkoin.api.Transaction;

import java.util.List;

/**
 * Created by warefhaque on 6/26/17.
 */

public class TransactionSummary implements Section<Item> {

  List<Item> childList;
  Transaction transaction;

  public TransactionSummary() {
  }

  public TransactionSummary setChildList(List<Item> childList) {
    this.childList = childList;
    return this;
  }

  public TransactionSummary setTransaction(Transaction transaction) {
    this.transaction = transaction;
    return this;
  }

  @Override
  public List<Item> getChildItems() {
    return childList;
  }
}
