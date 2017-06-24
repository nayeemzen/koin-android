package com.sendkoin.customer.Payment.QRPayment;

import android.content.Context;

import com.sendkoin.customer.BasePresenter;

/**
 * Created by warefhaque on 5/23/17.
 */

public interface QRScannerContract {

  interface View {
    Context getContext();

    void showTransactionComplete();

    void showTransactionError(String errorMessage);
  }

  interface Presenter extends BasePresenter {

    void acceptTransaction(String qrToken);

  }


}
