package com.sendkoin.koincustomer.Payment.QRPayment;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by warefhaque on 5/23/17.
 */

public interface QRScannerContract {

  public interface View{
    Context getContext();
  }
  public interface Presenter{
    void createPayment(JSONObject payment) throws JSONException;
    void unsubscribe();
    void close();
  }


}
