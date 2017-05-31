package com.sendkoin.customer.Payment;

import com.sendkoin.customer.Data.Payments.Models.Payment;

import java.util.HashMap;
import java.util.List;

/**
 * Created by warefhaque on 5/20/17.
 */

public interface MainPaymentContract {

     interface View {
       void showPaymentItems(HashMap<String, List<Payment>> payments);
    }

     interface Presenter {
       void loadItems();
       void deleteAll();

    }
}
