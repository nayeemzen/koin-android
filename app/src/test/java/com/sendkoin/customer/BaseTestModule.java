package com.sendkoin.customer;

import android.app.Application;

import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.PaymentRepository;

import org.robolectric.RuntimeEnvironment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

/**
 * Created by warefhaque on 5/20/17.
 */

@Module
public class BaseTestModule {
    @Provides
    @Singleton
    Application providesApplication() {
        return RuntimeEnvironment.application;
    }

    @Provides
    @Singleton
    LocalPaymentDataStore providesLocalPaymentDataStore(Realm realm) {
        return new LocalPaymentDataStore(realm);
    }

    @Provides
    @Singleton
    Realm providesRealm() {
        return Realm.getDefaultInstance();
    }

    @Provides
    @Singleton
    PaymentRepository providesPaymentRepository(LocalPaymentDataStore localPaymentDataStore) {
        return new PaymentRepository(localPaymentDataStore);
    }
}
