package com.sendkoin.customer.Payment.TransactionDetails;

import com.sendkoin.customer.Data.Dagger.Component.NetComponent;
import com.sendkoin.customer.Data.Dagger.CustomScope;
import com.sendkoin.customer.Payment.MainPaymentFragment;
import com.sendkoin.customer.Payment.MainPaymentModule;

import dagger.Component;

/**
 * Created by warefhaque on 6/5/17.
 */

@CustomScope
@Component(dependencies = NetComponent.class, modules = TransactionDetailsModule.class)
public interface TransactionDetailsComponent {
  void inject (TransactionDetailsActivity transactionDetailsActivity);
}
