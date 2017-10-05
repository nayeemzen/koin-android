package com.sendkoin.customer.payment.paymentCreate.pinConfirmation;

import com.sendkoin.api.AcceptTransactionRequest;
import com.sendkoin.api.InitiateStaticTransactionRequest;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.BasePresenter;

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
