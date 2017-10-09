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
import com.sendkoin.customer.payment.paymentCreate.pinConfirmation.PinConfirmationContract;
import com.sendkoin.customer.payment.paymentCreate.pinConfirmation.PinConfirmationPresenter;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

/**
 * Created by warefhaque on 6/14/17.
 */

@Module
public class CreatePaymentTestModule {
  private QrScannerContract.View qrScannerView;
  private PinConfirmationContract.View pinConfirmationView;

  public CreatePaymentTestModule(QrScannerContract.View view,
                                 PinConfirmationContract.View pinConfirmationView) {
    this.qrScannerView = view;
    this.pinConfirmationView = pinConfirmationView;
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
  public QrScannerContract.Presenter providesPresenter(InventoryService inventoryService,
                                                       LocalOrderDataStore localOrderDataStore){
    return new QrScannerPresenter(qrScannerView, inventoryService, localOrderDataStore);
  }

  @Provides
  @CustomScope
  public PinConfirmationContract.Presenter providesPinConfirmationPresenter(
      LocalOrderDataStore localOrderDataStore,
      LocalPaymentDataStore localPaymentDataStore,
      PaymentService paymentService){
    return new PinConfirmationPresenter(
        pinConfirmationView,
        localOrderDataStore,
        localPaymentDataStore,
        paymentService);
  }

  @Provides
  @CustomScope
  public QrScannerContract.View providesView(){
    return qrScannerView;
  }

  @Provides
  @CustomScope
  PinConfirmationContract.View providesPinConfirmationView () {
    return pinConfirmationView;
  }

}
