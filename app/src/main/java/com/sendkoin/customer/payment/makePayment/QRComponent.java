package com.sendkoin.customer.payment.makePayment;

import com.sendkoin.customer.data.dagger.Component.NetComponent;
import com.sendkoin.customer.data.dagger.CustomScope;

import dagger.Component;

/**
 * Created by warefhaque on 5/23/17.
 */
@CustomScope
@Component(dependencies = NetComponent.class, modules = QRPaymentModule.class)
public interface QRComponent {
  void inject (QRCodeScannerActivity activity);
}
