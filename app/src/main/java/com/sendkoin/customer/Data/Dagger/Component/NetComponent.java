package com.sendkoin.customer.Data.Dagger.Component;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.sendkoin.customer.Data.Authentication.SessionManager;
import com.sendkoin.customer.Data.Dagger.Module.AppModule;
import com.sendkoin.customer.Data.Dagger.Module.NetModule;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.PaymentRepository;

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

    SharedPreferences sharedPreferences();

    SessionManager sessionManager();
}