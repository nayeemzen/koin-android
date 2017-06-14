package com.sendkoin.customer.QRPayment;

import com.sendkoin.customer.Data.Dagger.Component.NetComponent;
import com.sendkoin.customer.Data.Dagger.CustomScope;
import com.sendkoin.customer.KoinTestComponent;
import com.sendkoin.customer.KoinTestModule;
import com.sendkoin.customer.Payment.QRPayment.QRCodeScannerActivity;
import com.sendkoin.customer.Payment.QRPayment.QRPaymentModule;

import dagger.Component;

/**
 * Created by warefhaque on 6/14/17.
 */

@CustomScope
@Component(dependencies = KoinTestComponent.class, modules = QRPaymentTestModule.class)
public interface QRPaymentTestComponent {
  void inject (QRPaymentTest qrPaymentTest);
}
