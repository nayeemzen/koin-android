package com.sendkoin.customer.data.dagger.Component;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.sendkoin.customer.data.authentication.SessionManager;
import com.sendkoin.customer.data.dagger.Module.AppModule;
import com.sendkoin.customer.data.dagger.Module.NetModule;
import com.sendkoin.customer.data.payments.Local.LocalOrderDataStore;
import com.sendkoin.customer.data.payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.data.payments.PaymentRepository;

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

    LocalOrderDataStore localOrderDataStore();

    Gson gson();

    SharedPreferences sharedPreferences();

    SessionManager sessionManager();
}