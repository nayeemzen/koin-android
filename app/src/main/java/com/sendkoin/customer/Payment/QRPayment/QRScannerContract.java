package com.sendkoin.customer.Payment.QRPayment;

import android.content.Context;

import com.sendkoin.customer.BasePresenter;
import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by warefhaque on 5/23/17.
 */

public interface QRScannerContract {

  interface View {
    Context getContext();

    void showTransactionConfirmationScreen(RealmTransaction realmTransaction);

    void showTransactionComplete();
  }

  interface Presenter extends BasePresenter {

    void createTransaction(String transactionToken);

    void getTransactionConfirmationDetails(JSONObject payment) throws JSONException;
  }


}
