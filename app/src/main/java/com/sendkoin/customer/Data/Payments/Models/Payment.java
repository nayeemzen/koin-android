package com.sendkoin.customer.Data.Payments.Models;

import io.realm.RealmObject;

/**
 * Created by warefhaque on 5/20/17.
 */


public class Payment extends RealmObject{

    public Integer id;
    private String transactionId;
    private Integer totalPrice;
    private String merchantName;
    private String merchantType;
    private String date;



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


    public Payment setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public Payment setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    public Payment setMerchantName(String merchantName) {
        this.merchantName = merchantName;
        return this;
    }


    public Integer getTotalPrice() {
        return totalPrice;
    }


    public String getTransactionId() {
        return transactionId;
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
