package com.sendkoin.customer.payment.paymentCreate;

import com.sendkoin.customer.data.dagger.Component.NetComponent;
import com.sendkoin.customer.data.dagger.CustomScope;

import dagger.Component;


@CustomScope
@Component(dependencies = NetComponent.class, modules = QrScannerModule.class)
public interface QrScannerComponent {
  void inject (QrScannerActivity activity);
}
