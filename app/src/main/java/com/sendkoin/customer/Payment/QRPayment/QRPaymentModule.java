package com.sendkoin.customer.Payment.QRPayment;

import com.sendkoin.customer.Data.Authentication.SessionManager;
import com.sendkoin.customer.Data.Dagger.CustomScope;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.PaymentService;

import dagger.Module;
import dagger.Provides;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import retrofit2.Retrofit;

/**
 * Created by warefhaque on 5/23/17.
 */

@Module
public class QRPaymentModule {
  private QRScannerContract.View view;

  public QRPaymentModule(QRScannerContract.View view) {
    this.view = view;
  }

  @Provides
  @CustomScope
  public PaymentService providesPaymentService(Retrofit retrofit){
    return  retrofit.create(PaymentService.class);
  }

  @Provides
  @CustomScope
  public QRScannerContract.Presenter providesPresenter(LocalPaymentDataStore localPaymentDataStore,
                                                       PaymentService paymentService,
                                                       SessionManager sessionManager){
    return new QRScannerPresenter(view, localPaymentDataStore, paymentService, sessionManager);
  }

  @Provides
  @CustomScope
  public QRScannerContract.View providesView(){
    return view;
  }

  @Provides
  @CustomScope
  public ZBarScannerView providesZBarScannerView(){
    return new ZBarScannerView(view.getContext());
  }
}
