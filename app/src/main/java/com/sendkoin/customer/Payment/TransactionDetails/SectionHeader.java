package com.sendkoin.customer.Payment.TransactionDetails;

import android.graphics.Bitmap;

import com.intrusoft.sectionedrecyclerview.Section;
import com.sendkoin.api.Transaction;

import java.util.List;

/**
 * Created by warefhaque on 6/26/17.
 */

public class SectionHeader implements Section<Item> {

  List<Item> childList;
  Transaction transaction;

  public SectionHeader() {
  }

  public SectionHeader setChildList(List<Item> childList) {
    this.childList = childList;
    return this;
  }

  public SectionHeader setTransaction(Transaction transaction) {
    this.transaction = transaction;
    return this;
  }

  @Override
  public List<Item> getChildItems() {
    return childList;
  }
}
