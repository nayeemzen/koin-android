package com.sendkoin.customer.QRPayment;


import com.sendkoin.customer.FakeInventoryService;
import com.sendkoin.customer.FakePaymentService;
import com.sendkoin.customer.data.dagger.CustomScope;
import com.sendkoin.customer.data.payments.InventoryService;
import com.sendkoin.customer.data.payments.Local.LocalOrderDataStore;
import com.sendkoin.customer.data.payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.data.payments.PaymentService;
import com.sendkoin.customer.payment.makePayment.QRScannerContract;
import com.sendkoin.customer.payment.makePayment.QRScannerPresenter;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

/**
 * Created by warefhaque on 6/14/17.
 */

@Module
public class QRPaymentTestModule {
  private QRScannerContract.View qrScannerView;

  public QRPaymentTestModule(QRScannerContract.View view) {
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
  public QRScannerContract.Presenter providesPresenter(LocalPaymentDataStore localPaymentDataStore,
                                                       PaymentService paymentService,
                                                       InventoryService inventoryService,
                                                       LocalOrderDataStore localOrderDataStore){
    return new QRScannerPresenter(
        qrScannerView,
        localPaymentDataStore,
        paymentService,
        inventoryService,
        localOrderDataStore);
  }


  @Provides
  @CustomScope
  public QRScannerContract.View providesView(){
    return qrScannerView;
  }

}
