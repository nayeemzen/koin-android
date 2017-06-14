package com.sendkoin.customer;

import com.google.gson.Gson;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.PaymentRepository;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { KoinTestModule.class })
public interface KoinTestComponent {

  PaymentRepository paymentRepository();

  LocalPaymentDataStore localPaymentDataStore();

  Gson gson();
}
