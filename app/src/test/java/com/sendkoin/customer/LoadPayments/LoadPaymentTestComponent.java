package com.sendkoin.customer.LoadPayments;

import com.sendkoin.customer.KoinTestComponent;
import com.sendkoin.customer.QRPayment.QRPaymentTestModule;
import com.sendkoin.customer.data.dagger.CustomScope;

import dagger.Component;

@CustomScope
@Component(
    dependencies = KoinTestComponent.class,
    modules = {LoadPaymentTestModule.class, QRPaymentTestModule.class})
public interface LoadPaymentTestComponent {
  void inject(LoadPaymentTest loadPaymentTest);
}
