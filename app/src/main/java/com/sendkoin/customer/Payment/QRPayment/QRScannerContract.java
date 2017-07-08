package com.sendkoin.customer.Payment.QRPayment;

import android.content.Context;

import com.sendkoin.api.QrCode;
import com.sendkoin.api.SaleItem;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.BasePresenter;

import java.util.List;

/**
 * Created by warefhaque on 5/23/17.
 */

public interface QRScannerContract {

  interface View {
    Context getContext();

    void showTransactionComplete(Transaction transaction);

    void showTransactionError(String errorMessage);
  }

  interface Presenter extends BasePresenter {

    void acceptTransaction(QrCode qrCode, List<SaleItem> saleItemList);

  }


}
