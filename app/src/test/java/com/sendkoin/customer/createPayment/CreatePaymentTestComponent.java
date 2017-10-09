package com.sendkoin.customer.createPayment;
import com.sendkoin.customer.KoinTestComponent;
import com.sendkoin.customer.data.dagger.CustomScope;

import dagger.Component;

/**
 * Created by warefhaque on 6/14/17.
 */

@CustomScope
@Component(
    dependencies = KoinTestComponent.class,
    modules = CreatePaymentTestModule.class)
public interface CreatePaymentTestComponent {
  void inject (CreatePaymentTest createPaymentTest);
}
