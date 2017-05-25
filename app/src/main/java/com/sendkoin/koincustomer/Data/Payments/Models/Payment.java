package com.sendkoin.koincustomer.Data.Payments.Models;

import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverColumn;
import com.pushtorefresh.storio.contentresolver.annotations.StorIOContentResolverType;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import io.realm.RealmObject;

/**
 * Created by warefhaque on 5/20/17.
 */


public class Payment extends RealmObject{

    public Integer id;
    public String transactionId;
    public Integer totalPrice;
    public String merchantName;

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
