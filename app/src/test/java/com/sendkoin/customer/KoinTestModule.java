package com.sendkoin.customer;

import android.app.Application;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.PaymentRepository;
import com.sendkoin.customer.Data.sql.KoinSQLiteOpenHelper;
import com.sendkoin.sql.entities.PaymentEntity;
import com.sendkoin.sql.entities.PaymentEntitySQLiteTypeMapping;


import org.greenrobot.eventbus.EventBus;
import org.robolectric.RuntimeEnvironment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class KoinTestModule {
  @Provides
  @Singleton
  Application provideApplication() {
    return RuntimeEnvironment.application;
  }

  @Provides
  @Singleton
  Gson providesGson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    return gsonBuilder.create();
  }

  @Provides
  PaymentRepository providesPaymentRepository(LocalPaymentDataStore localPaymentDataStore) {
    return new PaymentRepository(localPaymentDataStore);
  }

  @Provides
  @Singleton
  StorIOSQLite provideStorIOSQLite(Application application) {
    return DefaultStorIOSQLite.builder()
        .sqliteOpenHelper(new KoinSQLiteOpenHelper(application))
        .addTypeMapping(PaymentEntity.class, new PaymentEntitySQLiteTypeMapping())
        .build();
  }
}