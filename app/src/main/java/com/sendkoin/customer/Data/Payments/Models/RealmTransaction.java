package com.sendkoin.customer.Data.Payments.Models;

import android.text.format.DateFormat;

import com.sendkoin.api.Merchant;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.Payment.QRPayment.QRScannerPresenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by warefhaque on 5/20/17.
 */


public class RealmTransaction extends RealmObject {


  public int id;
  @PrimaryKey
  private String transactionToken;
  private Long merchantId;
  private Long createdAt;
  private int amount;
  private String merchantName;
  private String merchantType;
  private String state;

  public String getState() {
    return state;
  }

  public RealmTransaction setState(String state) {
    this.state = state;
    return this;
  }

  public Long getMerchantId() {
    return merchantId;
  }

  public RealmTransaction setMerchantId(Long merchantId) {
    this.merchantId = merchantId;
    return this;
  }

  public String getCreatedAt() {
    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    cal.setTimeInMillis(createdAt);
    String date = DateFormat.format("EEEE, MMMM dd", cal).toString();
    return date;
  }

  public RealmTransaction setCreatedAt(Long createdAt) {
    this.createdAt = createdAt;
    return this;
  }


  public String getMerchantType() {
    return merchantType;
  }

  public RealmTransaction setMerchantType(String merchantType) {
    this.merchantType = merchantType;
    return this;
  }


  public RealmTransaction() {
  }


  public RealmTransaction setTransactionToken(String transactionToken) {
    this.transactionToken = transactionToken;
    return this;
  }

  public RealmTransaction setAmount(int amount) {
    this.amount = amount;
    return this;
  }

  public RealmTransaction setMerchantName(String merchantName) {
    this.merchantName = merchantName;
    return this;
  }


  public Integer getAmount() {
    return amount;
  }


  public String getTransactionToken() {
    return transactionToken;
  }

  public String getMerchantName() {
    return merchantName;
  }


  public Integer getId() {
    return id;
  }

  public RealmTransaction setId(Integer id) {
    this.id = id;
    return this;
  }

  public static RealmTransaction transactionToRealmTransaction(Transaction transaction) {

    Merchant merchant = transaction.merchant;
    return new RealmTransaction()
        .setMerchantType(merchant.store_type)
        .setMerchantName(merchant.store_name)
        .setAmount(transaction.amount)
        .setCreatedAt(transaction.created_at)
        .setTransactionToken(transaction.token)
        .setState(transaction.state);

  }

  public static List<RealmTransaction> transactionListToRealmTranscationList(List<Transaction> transactions) {
    List<RealmTransaction> realmTransactions = new ArrayList<>();
    for (Transaction transaction : transactions) {
      realmTransactions.add(transactionToRealmTransaction(transaction));
    }

    return realmTransactions;
  }

}
