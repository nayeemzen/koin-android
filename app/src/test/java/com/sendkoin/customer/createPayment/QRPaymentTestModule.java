package com.sendkoin.customer.createPayment;


import com.sendkoin.customer.data.FakeInventoryService;
import com.sendkoin.customer.data.FakePaymentService;
import com.sendkoin.customer.data.dagger.CustomScope;
import com.sendkoin.customer.data.payments.InventoryService;
import com.sendkoin.customer.data.payments.Local.LocalOrderDataStore;
import com.sendkoin.customer.data.payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.data.payments.PaymentService;
import com.sendkoin.customer.payment.paymentCreate.QrScannerContract;
import com.sendkoin.customer.payment.paymentCreate.QrScannerPresenter;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

/**
 * Created by warefhaque on 6/14/17.
 */

@Module
public class QRPaymentTestModule {
  private QrScannerContract.View qrScannerView;

  public QRPaymentTestModule(QrScannerContract.View view) {
    this.qrScannerView = view;
  }

  @Provides
  @CustomScope
  public PaymentService providesPaymentService(){
    return new FakePaymentService();
  }

  @Provides
  @CustomScope
  public InventoryService providesInventoryService(){
    return new FakeInventoryService();
  }

  @Provides
  @CustomScope
  public QrScannerContract.Presenter providesPresenter(LocalPaymentDataStore localPaymentDataStore,
                                                       PaymentService paymentService,
                                                       InventoryService inventoryService,
                                                       LocalOrderDataStore localOrderDataStore){
    return new QrScannerPresenter(
        qrScannerView,
        inventoryService,
        localOrderDataStore);
  }


  @Provides
  @CustomScope
  public QrScannerContract.View providesView(){
    return qrScannerView;
  }

}
