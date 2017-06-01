package com.sendkoin.customer.Data.Payments.Models;

import com.sendkoin.api.Transaction;
import com.sendkoin.customer.Payment.QRPayment.QRScannerPresenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by warefhaque on 5/20/17.
 */


public class RealmTransaction extends RealmObject {

  @PrimaryKey
  public Integer id;
  private String transactionToken;
  private Long merchantId;
  private Long createdAt;
  private Integer amount;
  private String merchantName;
  private String merchantType;
  private String date;
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

  public Long getCreatedAt() {
    return createdAt;
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


  public String getDate() {
    return date;
  }


  public RealmTransaction setDate(String date) {
    this.date = date;
    return this;
  }


  public RealmTransaction() {
  }


  public RealmTransaction setTransactionToken(String transactionToken) {
    this.transactionToken = transactionToken;
    return this;
  }

  public RealmTransaction setAmount(Integer amount) {
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

    RealmTransaction realmTransaction = new RealmTransaction()
        // TODO: 5/31/17 (WAREF) ask zen to save Type on the server
        .setMerchantType("Restaurant")
        .setMerchantName(transaction.merchant_name)
        .setAmount(transaction.amount)
        .setCreatedAt(transaction.created_at)
        .setTransactionToken(transaction.token)
        .setMerchantId(transaction.merchant_id)
        .setState(transaction.state)
        // TODO: 5/31/17 (WAREF) ask zen to save date on the server
        .setDate(QRScannerPresenter.getFormattedDate(new Date()));

    return realmTransaction;

  }

  public static List<RealmTransaction> transactionListToRealmTranscationList(List<Transaction> transactions) {
    List<RealmTransaction> realmTransactions = new ArrayList<>();
    for (Transaction transaction : transactions) {
      realmTransactions.add(transactionToRealmTransaction(transaction));
    }

    return realmTransactions;
  }

}
