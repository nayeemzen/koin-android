package com.sendkoin.customer.QRPayment;
import com.sendkoin.customer.KoinTestComponent;
import com.sendkoin.customer.data.dagger.CustomScope;

import dagger.Component;

/**
 * Created by warefhaque on 6/14/17.
 */

@CustomScope
@Component(
    dependencies = KoinTestComponent.class,
    modules = QRPaymentTestModule.class)
public interface QRPaymentTestComponent {
  void inject (QRPaymentTest qrPaymentTest);
}
