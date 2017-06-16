package com.sendkoin.customer.QRPayment;

import com.sendkoin.customer.Data.Dagger.CustomScope;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.PaymentService;
import com.sendkoin.customer.FakePaymentService;
import com.sendkoin.customer.Payment.QRPayment.QRScannerContract;
import com.sendkoin.customer.Payment.QRPayment.QRScannerPresenter;

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
  public QRScannerContract.Presenter providesPresenter(LocalPaymentDataStore localPaymentDataStore,
                                                       PaymentService paymentService){
    return new QRScannerPresenter(qrScannerView, localPaymentDataStore, paymentService);
  }


  @Provides
  @CustomScope
  public QRScannerContract.View providesView(){
    return qrScannerView;
  }

}
