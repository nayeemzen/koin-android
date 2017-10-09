package com.sendkoin.customer.loadPayment;

import com.sendkoin.customer.KoinTestComponent;
import com.sendkoin.customer.createPayment.QRPaymentTestModule;
import com.sendkoin.customer.data.dagger.CustomScope;

import dagger.Component;

@CustomScope
@Component(
    dependencies = KoinTestComponent.class,
    modules = {LoadPaymentTestModule.class, QRPaymentTestModule.class})
public interface LoadPaymentTestComponent {
  void inject(LoadPaymentTest loadPaymentTest);
}
