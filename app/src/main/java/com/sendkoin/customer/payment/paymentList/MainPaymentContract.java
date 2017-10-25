package com.sendkoin.customer.payment.paymentList;

import android.content.Context;

import com.sendkoin.customer.BasePresenter;
import com.sendkoin.sql.entities.PaymentEntity;

import java.util.List;

public interface MainPaymentContract {

  /**
   * Implemented by MainPaymentFragment
   */
  interface View {
    /**
     * Pass the list of user's past payments after they have been saved locally
     * @param payments list of payments
     */
    void showPaymentItems(List<PaymentEntity> payments);
    Context getApplicationContext();
  }

  interface Presenter extends BasePresenter {
    /**
     * Fetches the latest transactions that the local DB does not have
     * OR the oldest ones depending if the user pulls to refresh or keeps scrolling down
     * @param fetchHistory -
     *                     true : fetch the latest paged transactions
     *                     false : fetch the oldest paged transactions
     */
    void loadTransactionsFromServer(boolean fetchHistory);
  }
}
