package com.sendkoin.koincustomer.Payment.QRPayment;

import com.sendkoin.koincustomer.Data.Dagger.Component.NetComponent;
import com.sendkoin.koincustomer.Data.Dagger.CustomScope;

import javax.inject.Inject;

import dagger.Component;

/**
 * Created by warefhaque on 5/23/17.
 */
@CustomScope
@Component(dependencies = NetComponent.class, modules = QRPaymentModule.class)
public interface QRComponent {
  void inject (QRCodeScannerActivity activity);
}
