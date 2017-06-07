package com.sendkoin.customer.Payment.TransactionDetails;

import com.sendkoin.api.TransactionDetail;
import com.sendkoin.customer.BasePresenter;

/**
 * Created by warefhaque on 6/5/17.
 */

public interface TransactionDetailsContract {
  interface View{
    void showDetailedTransaction(TransactionDetail transactionDetail);
  }
  interface Presenter extends BasePresenter {
    void fetchTransactionDetails(String transactionToken);
  }
}
