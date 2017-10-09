package com.sendkoin.customer;

import android.app.Application;

import com.google.gson.Gson;
import com.sendkoin.customer.data.payments.Local.LocalOrderDataStore;
import com.sendkoin.customer.data.payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.data.payments.PaymentRepository;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { KoinTestModule.class })
public interface KoinTestComponent {
  PaymentRepository paymentRepository();
  LocalPaymentDataStore localPaymentDataStore();
  LocalOrderDataStore localOrderDataStore();
  MiscGenerator miscGenerator();
  Application application();
}
