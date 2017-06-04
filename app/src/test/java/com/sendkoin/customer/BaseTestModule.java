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
    private String mBaseUrl = "http://custom-env-1.2tfxydg93p.us-west-2.elasticbeanstalk.com/api/v1/";

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
    Gson providesGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    OkHttpClient providesOkHttpClient(Cache cache) {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.cache(cache);
        return client.build();
    }

    @Provides
    @Singleton
    Retrofit providesRetrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .baseUrl(mBaseUrl)
            .client(okHttpClient)
            .build();
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
    Realm providesRealm() {
        return Realm.getDefaultInstance();
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
