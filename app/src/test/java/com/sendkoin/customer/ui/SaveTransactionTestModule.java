package com.sendkoin.customer.ui;

import com.sendkoin.customer.Data.Authentication.RealSessionManager;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.PaymentService;
import com.sendkoin.customer.Payment.QRPayment.QRCodeScannerActivity;
import com.sendkoin.customer.Payment.QRPayment.QRScannerContract;
import com.sendkoin.customer.Payment.QRPayment.QRScannerPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

import static org.mockito.Mockito.mock;

/**
 * Created by warefhaque on 6/2/17.
 */
@Module
public class SaveTransactionTestModule {


  @Provides
  @Singleton
  PaymentService providesPaymentService(Retrofit retrofit){
    return retrofit.create(PaymentService.class);
  }

  @Provides
  @Singleton
  QRScannerContract.View providesTransactionScreenView(){
    return mock(QRCodeScannerActivity.class);
  }

  @Provides
  @Singleton
  QRScannerPresenter providesTransactionScreenPresenter(QRScannerContract.View view,
                                                        LocalPaymentDataStore localPaymentDataStore,
                                                        PaymentService paymentService,
                                                        RealSessionManager realSessionManager){

    return new QRScannerPresenter(view, localPaymentDataStore,paymentService, realSessionManager);

  }
}
