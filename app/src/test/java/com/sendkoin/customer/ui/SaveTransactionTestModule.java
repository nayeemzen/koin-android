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
  FakeTransactionService providesFakeTransactionService(){
    return new FakeTransactionService();
  }

  @Provides
  @Singleton
  PaymentService providesPaymentService() {
    return new FakeTransactionService();
  }

  @Provides
  @Singleton
  QRScannerContract.View providesTransactionScreenView() {
    return mock(QRScannerContract.View.class);
  }

  @Provides
  @Singleton
  QRScannerContract.Presenter providesTransactionScreenPresenter(QRScannerContract.View view,
                                                                 FakeTransactionService fakeTransactionService,
                                                                 LocalPaymentDataStore localPaymentDataStore,
                                                                 RealSessionManager realSessionManager) {

    return new QRScannerPresenter(view, localPaymentDataStore, fakeTransactionService, realSessionManager);

  }
}
