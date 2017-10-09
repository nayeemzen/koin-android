package com.sendkoin.customer.loadPayment;

import com.sendkoin.customer.data.FakePaymentService;
import com.sendkoin.customer.data.dagger.CustomScope;
import com.sendkoin.customer.data.payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.data.payments.PaymentRepository;
import com.sendkoin.customer.data.payments.PaymentService;
import com.sendkoin.customer.payment.paymentList.MainPaymentContract;
import com.sendkoin.customer.payment.paymentList.MainPaymentPresenter;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by warefhaque on 6/15/17.
 */

@Module
public class LoadPaymentTestModule {

  private MainPaymentContract.View view;

  public LoadPaymentTestModule(MainPaymentContract.View view) {
    this.view = view;
  }

  @Provides
  @CustomScope
  public PaymentService providesFakePaymentService () {
    return new FakePaymentService();
  }

  @Provides
  @CustomScope
  public MainPaymentContract.View providesView(){
    return view;
  }

  @Provides
  @CustomScope
  public MainPaymentContract.Presenter providesPresenter(LocalPaymentDataStore localPaymentDataStore,
                                                         PaymentRepository paymentRepository,
                                                         PaymentService paymentService){
    return new MainPaymentPresenter(view, localPaymentDataStore, paymentRepository, paymentService);
  }

}
