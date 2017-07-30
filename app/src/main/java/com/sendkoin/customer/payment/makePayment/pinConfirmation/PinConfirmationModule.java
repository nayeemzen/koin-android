package com.sendkoin.customer.payment.makePayment.pinConfirmation;

import com.sendkoin.customer.data.dagger.CustomScope;
import com.sendkoin.customer.data.payments.Local.LocalOrderDataStore;
import com.sendkoin.customer.data.payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.data.payments.PaymentService;
import com.sendkoin.customer.payment.makePayment.QRScannerContract;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by warefhaque on 7/29/17.
 */

@Module
public class PinConfirmationModule {
  PinConfirmationContract.View mView;

  public PinConfirmationModule(PinConfirmationContract.View mView) {
    this.mView = mView;
  }

  @Provides
  @CustomScope
  public PaymentService providesPaymentService(Retrofit retrofit){
    return  retrofit.create(PaymentService.class);
  }

  @Provides
  @CustomScope
  public PinConfirmationContract.Presenter providesPresenter(PaymentService paymentService,
                                                             LocalOrderDataStore localOrderDataStore,
                                                             LocalPaymentDataStore localPaymentDataStore) {
    return new PinConfirmationPresenter(mView,localOrderDataStore,localPaymentDataStore,paymentService);
  }

  @Provides
  @CustomScope
  public PinConfirmationContract.View providesView(){
    return mView;
  }


}
