package com.sendkoin.customer.Payment;

import android.content.Context;

import com.sendkoin.customer.BasePresenter;
import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by warefhaque on 5/20/17.
 */

public interface MainPaymentContract {

  interface View {
    void showPaymentItems(LinkedHashMap<String, List<RealmTransaction>> payments);

    Context getApplicationContext();
  }

  interface Presenter extends BasePresenter {
    void loadTransactionsFromServer(boolean fetchHistory);

    void deleteAllTransactions();
  }
}
