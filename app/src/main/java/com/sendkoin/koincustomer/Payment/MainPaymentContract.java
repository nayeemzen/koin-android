package com.sendkoin.koincustomer.Payment;

import com.sendkoin.koincustomer.Data.Payments.Models.Payment;

import java.util.HashMap;
import java.util.List;

/**
 * Created by warefhaque on 5/20/17.
 */

public interface MainPaymentContract {

    public interface View {
      public void showPaymentItems(HashMap<String, List<Payment>> payments);
    }

    public interface Presenter {
      public void loadItems();
    }
}
