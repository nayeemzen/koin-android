package com.sendkoin.customer.payment.paymentDetails;

import com.sendkoin.customer.data.dagger.Component.NetComponent;
import com.sendkoin.customer.data.dagger.CustomScope;

import dagger.Component;

/**
 * Created by warefhaque on 6/5/17.
 */

@CustomScope
@Component(dependencies = NetComponent.class, modules = TransactionDetailsModule.class)
public interface TransactionDetailsComponent {
  void inject (DetailedReceiptActivity detailedReceiptActivity);
}
