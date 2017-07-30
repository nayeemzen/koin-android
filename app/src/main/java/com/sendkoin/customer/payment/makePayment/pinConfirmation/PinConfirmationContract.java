package com.sendkoin.customer.payment.makePayment.pinConfirmation;

import com.sendkoin.api.AcceptTransactionRequest;
import com.sendkoin.api.InitiateDynamicTransactionRequest;
import com.sendkoin.api.InitiateStaticTransactionRequest;
import com.sendkoin.api.QrCode;
import com.sendkoin.api.SaleItem;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.BasePresenter;

import java.util.List;

/**
 * Created by warefhaque on 7/29/17.
 */

public interface PinConfirmationContract {
  interface View {
    void showTransactionReciept(Transaction transaction);
    void showTransactionError(String errorMessage);
  }

  interface Presenter extends BasePresenter {
    void processStaticTransactionRequest(InitiateStaticTransactionRequest initiateStaticTransactionRequest);
    void processDynamicTransactionRequest(AcceptTransactionRequest initiateDynamicTransactionRequest);
  }
}
