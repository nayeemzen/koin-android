package com.sendkoin.customer.payment;

import com.sendkoin.customer.data.dagger.CustomScope;
import com.sendkoin.customer.data.payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.data.payments.PaymentRepository;
import com.sendkoin.customer.data.payments.PaymentService;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by warefhaque on 5/20/17.
 */

@Module
public class MainPaymentModule {


  private MainPaymentContract.View view;

  public MainPaymentModule(MainPaymentContract.View view) {
    this.view = view;
  }

  @Provides
  @CustomScope
  PaymentService providesPaymentService(Retrofit retrofit) {
    return retrofit.create(PaymentService.class);
  }

  @Provides
  @CustomScope
  MainPaymentContract.View providesMainPaymentView() {
    return view;
  }

  @Provides
  @CustomScope
  MainPaymentContract.Presenter providesMainPaymentPresenter(MainPaymentContract.View view,
                                                             LocalPaymentDataStore localPaymentDataStore,
                                                             PaymentRepository paymentRepository,
                                                             PaymentService paymentService) {
    return new MainPaymentPresenter(view, localPaymentDataStore, paymentRepository, paymentService);
  }
}

