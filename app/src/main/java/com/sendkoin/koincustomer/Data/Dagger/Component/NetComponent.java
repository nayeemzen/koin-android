package com.sendkoin.koincustomer.Data.Dagger.Component;

import com.google.gson.Gson;
import com.sendkoin.koincustomer.Data.Dagger.Module.AppModule;
import com.sendkoin.koincustomer.Data.Dagger.Module.NetModule;
import com.sendkoin.koincustomer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.koincustomer.Data.Payments.PaymentRepository;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

/**
 * Created by warefhaque on 5/20/17.
 */


@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {
    Retrofit retrofit();

    PaymentRepository paymentRepository();

    LocalPaymentDataStore localPaymentDataStore();

    Gson gson();
}