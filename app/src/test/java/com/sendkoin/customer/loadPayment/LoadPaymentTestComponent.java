package com.sendkoin.customer.loadPayment;

import com.sendkoin.customer.KoinTestComponent;
import com.sendkoin.customer.createPayment.CreatePaymentTestModule;
import com.sendkoin.customer.data.dagger.CustomScope;

import dagger.Component;

@CustomScope
@Component(
    dependencies = KoinTestComponent.class,
    modules = LoadPaymentTestModule.class)
public interface LoadPaymentTestComponent {
  void inject(LoadPaymentTest loadPaymentTest);
}
