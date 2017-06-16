package com.sendkoin.customer.LoadPayments;

import com.sendkoin.customer.Data.Dagger.CustomScope;
import com.sendkoin.customer.KoinTestComponent;
import com.sendkoin.customer.QRPayment.QRPaymentTestModule;

import dagger.Component;

/**
 * Created by warefhaque on 6/15/17.
 */

@CustomScope
@Component(
    dependencies = KoinTestComponent.class,
    modules = {LoadPaymentTestModule.class, QRPaymentTestModule.class})
public interface LoadPaymentTestComponent {
  void inject(LoadPaymentTest loadPaymentTest);
}
