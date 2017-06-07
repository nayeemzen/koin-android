package com.sendkoin.customer.Payment;

import android.content.SharedPreferences;

import com.sendkoin.customer.Data.Authentication.RealSessionManager;
import com.sendkoin.customer.Data.Dagger.CustomScope;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.PaymentRepository;
import com.sendkoin.customer.Data.Payments.PaymentService;

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
                                                             PaymentService paymentService,
                                                             RealSessionManager realSessionManager,
                                                             SharedPreferences sharedPreferences) {
    return new MainPaymentPresenter(view,
        localPaymentDataStore,
        paymentRepository,
        paymentService,
        realSessionManager,
        sharedPreferences);
  }
}

