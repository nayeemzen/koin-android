package com.sendkoin.customer.Data.Payments.Models;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by warefhaque on 5/20/17.
 */


public class Payment extends RealmObject {

  public Integer id;
  private String transactionToken;
  private Long merchantId;
  private Long createdAt;
  private Integer amount;
  private String merchantName;
  private String merchantType;
  @Required private String date;
  private String state;

  public String getState() {
    return state;
  }

  public Payment setState(String state) {
    this.state = state;
    return this;
  }

  public Long getMerchantId() {
    return merchantId;
  }

  public Payment setMerchantId(Long merchantId) {
    this.merchantId = merchantId;
    return this;
  }

  public Long getCreatedAt() {
    return createdAt;
  }

  public Payment setCreatedAt(Long createdAt) {
    this.createdAt = createdAt;
    return this;
  }


  public String getMerchantType() {
    return merchantType;
  }

  public Payment setMerchantType(String merchantType) {
    this.merchantType = merchantType;
    return this;
  }


  public String getDate() {
    return date;
  }


  public Payment setDate(String date) {
    this.date = date;
    return this;
  }


  public Payment() {
  }


  public Payment setTransactionToken(String transactionToken) {
    this.transactionToken = transactionToken;
    return this;
  }

  public Payment setAmount(Integer amount) {
    this.amount = amount;
    return this;
  }

  public Payment setMerchantName(String merchantName) {
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

  public Payment setId(Integer id) {
    this.id = id;
    return this;
  }

}
