package com.sendkoin.customer.Payment.QRPayment;

import com.sendkoin.customer.Data.Dagger.Component.NetComponent;
import com.sendkoin.customer.Data.Dagger.CustomScope;

import dagger.Component;

/**
 * Created by warefhaque on 5/23/17.
 */
@CustomScope
@Component(dependencies = NetComponent.class, modules = QRPaymentModule.class)
public interface QRComponent {
  void inject (QRCodeScannerActivity activity);
}
