package com.sendkoin.customer;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sendkoin.customer.Data.Authentication.RealSessionManager;
import com.sendkoin.customer.Data.Authentication.SessionManager;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.PaymentRepository;

import org.robolectric.RuntimeEnvironment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.internal.RealmCore;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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
  Cache providesHttpCache(Application application) {
    int cacheSize = 10 * 1024 * 1024;
    Cache cache = new Cache(application.getCacheDir(), cacheSize);
    return cache;
  }

  @Provides
  @Singleton
  SharedPreferences providesSharedPreferences(Application application) {
    return PreferenceManager.getDefaultSharedPreferences(application);
  }


  @Provides
  LocalPaymentDataStore providesLocalPaymentDataStore(Realm realm) {
    return new LocalPaymentDataStore(realm);
  }

  @Provides
  Realm providesRealm(Application application) {
    RealmConfiguration testConfig =
        new RealmConfiguration.Builder().
            inMemory().
            name("test-realm").build();
    return Realm.getInstance(testConfig);
  }

  @Provides
  PaymentRepository providesPaymentRepository(LocalPaymentDataStore localPaymentDataStore) {
    return new PaymentRepository(localPaymentDataStore);
  }


  @Provides
  @Singleton
  SessionManager providesSessionManager(SharedPreferences sharedPreferences) {
    return new RealSessionManager(sharedPreferences);
  }

}
