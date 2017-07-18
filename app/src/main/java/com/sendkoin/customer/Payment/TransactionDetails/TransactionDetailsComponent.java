package com.sendkoin.customer.Payment.TransactionDetails;

import com.sendkoin.customer.Data.Dagger.Component.NetComponent;
import com.sendkoin.customer.Data.Dagger.CustomScope;

import dagger.Component;

/**
 * Created by warefhaque on 6/5/17.
 */

@CustomScope
@Component(dependencies = NetComponent.class, modules = TransactionDetailsModule.class)
public interface TransactionDetailsComponent {
  void inject (DetailedReceiptActivity detailedReceiptActivity);
}
