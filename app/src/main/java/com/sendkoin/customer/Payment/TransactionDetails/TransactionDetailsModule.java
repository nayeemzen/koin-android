package com.sendkoin.customer.Payment.TransactionDetails;

import com.sendkoin.customer.Data.Authentication.RealSessionManager;
import com.sendkoin.customer.Data.Dagger.CustomScope;
import com.sendkoin.customer.Data.Payments.PaymentService;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by warefhaque on 6/5/17.
 */

@Module
public class TransactionDetailsModule {

  private TransactionDetailsContract.View view;

  public TransactionDetailsModule(TransactionDetailsContract.View view) {
    this.view = view;
  }

  @Provides
  @CustomScope
  public PaymentService providesPaymentService(Retrofit retrofit){
    return retrofit.create(PaymentService.class);
  }

  @Provides
  @CustomScope
  public TransactionDetailsContract.View providesView(){
    return view;
  }

  @Provides
  @CustomScope
  public TransactionDetailsContract.Presenter providesPresenter(PaymentService paymentService,
                                                                TransactionDetailsContract.View view,
                                                                RealSessionManager realSessionManager){

    return new TransactionDetailsPresenter(paymentService, view, realSessionManager);

  }
}
