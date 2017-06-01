package com.sendkoin.customer.Payment.QRPayment;

import android.content.Context;

import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by warefhaque on 5/23/17.
 */

public interface QRScannerContract {

  interface View {
    Context getContext();

    void showPaymentConfirmationScreen(RealmTransaction realmTransaction);
  }

  interface Presenter {

    void createPayment(String transactionToken);

    void unsubscribe();

    void close();

    void
    createPaymentDetails(JSONObject payment) throws JSONException;
  }


}
