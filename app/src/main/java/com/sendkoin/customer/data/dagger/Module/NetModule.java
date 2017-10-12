package com.sendkoin.customer.data.dagger.Module;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.sendkoin.api.AuthenticationResponse;
import com.sendkoin.api.FacebookAuthenticationRequest;
import com.sendkoin.customer.data.authentication.AuthenticationService;
import com.sendkoin.customer.data.authentication.RealSessionManager;
import com.sendkoin.customer.data.authentication.SessionManager;
import com.sendkoin.customer.data.payments.Local.LocalOrderDataStore;
import com.sendkoin.customer.data.payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.data.payments.PaymentRepository;
import com.sendkoin.customer.data.sql.KoinSQLiteOpenHelper;
import com.sendkoin.customer.login.LogoutEvent;
import com.sendkoin.sql.entities.CurrentOrderEntity;
import com.sendkoin.sql.entities.CurrentOrderEntitySQLiteTypeMapping;
import com.sendkoin.sql.entities.InventoryOrderItemEntity;
import com.sendkoin.sql.entities.InventoryOrderItemEntitySQLiteTypeMapping;
import com.sendkoin.sql.entities.PaymentEntity;
import com.sendkoin.sql.entities.PaymentEntitySQLiteTypeMapping;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


@Module
public class NetModule {
  private static final String AUTHORIZATION = "Authorization";
  String mBaseUrl;

  public NetModule(String mBaseUrl) {
    this.mBaseUrl = mBaseUrl;
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

  /**
   * Provides an OkHTTP client for Retrofit to:
   * 1. Automatically add the Authorization header using an interceptor
   * 2. Automatically attempt to fetch a new session token if a 401 unauthorized is returned.
   * Keeps track of number of failed attempts to not bombard the authentication endpoint.
   * Resets failed attempts to 0 when a session token is received successfully.
   */

  @Provides
  @Singleton
  OkHttpClient providesOkHttpClient(SessionManager sessionManager,
                                    AuthenticationService authenticationService) {
    return new OkHttpClient.Builder()
        .addInterceptor(chain -> chain.proceed(chain.request()
            .newBuilder()
            .addHeader(AUTHORIZATION, "Bearer " + sessionManager.getSessionToken())
            .build()))
        // if there is an authentication challenge the authenticator will follow the actions below
        .authenticator((route, response) -> {

          int numAttempts = sessionManager.getAuthAttempts();

          // cant try to authenticate anymore log the user out
          if (numAttempts > RealSessionManager.MAX_AUTHORIZATION_ATTEMPTS ||
              sessionManager.getFbAccessToken() == null) {
            sessionManager.putSessionToken(null);
            // Event broadcasted and can be received by any activity that is active
            EventBus.getDefault().post(new LogoutEvent());
            return null;
          }

          // try to authenticate until you've run out of attempts
          sessionManager.putAuthAttempts(numAttempts + 1);
          AuthenticationResponse authenticationResponse = authenticationService
              .authenticateWithFacebook(new FacebookAuthenticationRequest.Builder()
                  .access_token(sessionManager.getFbAccessToken())
                  .build())
              .toBlocking()
              .first();

          // successful authorization made and so reset the numAttempts as well
          if (authenticationResponse.session_token != null) {
            sessionManager.putSessionToken(authenticationResponse.session_token);
            sessionManager.putAuthAttempts(0);
          }

          // return the response as specified by the docs for Authenticator i.e. send the proper
          // "credentials" in the response to the authentication challenge
          return response.request()
              .newBuilder()
              .addHeader(AUTHORIZATION, sessionManager.getSessionToken())
              .build();
        }).build();

  }

  /**
   * Provides a Retrofit without the authentication hooks in OkHTTP.
   */
  @Provides
  @Named("authenticator")
  @Singleton
  Retrofit providesAuthenticatorRetrofit(Gson gson) {
    return new Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .baseUrl(mBaseUrl)
        .build();
  }

  @Provides
  @Singleton
  AuthenticationService provideAuthenticationService(@Named("authenticator") Retrofit retrofit) {
    return retrofit.create(AuthenticationService.class);
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
  LocalPaymentDataStore providesLocalPaymentDataStore(StorIOSQLite storIOSQLite) {
    return new LocalPaymentDataStore(storIOSQLite);
  }

  @Provides
  public LocalOrderDataStore providesLocalOrderDataStore (StorIOSQLite storIOSQLite) {
    return new LocalOrderDataStore(storIOSQLite);
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
  StorIOSQLite provideStorIOSQLite(Application application) {
    return DefaultStorIOSQLite.builder()
        .sqliteOpenHelper(new KoinSQLiteOpenHelper(application))
        .addTypeMapping(PaymentEntity.class, new PaymentEntitySQLiteTypeMapping())
        .addTypeMapping(CurrentOrderEntity.class, new CurrentOrderEntitySQLiteTypeMapping())
        .addTypeMapping(InventoryOrderItemEntity.class, new InventoryOrderItemEntitySQLiteTypeMapping())
        .build();
  }

  @Provides
  @Singleton
  SessionManager providesSessionManager(SharedPreferences sharedPreferences) {
    return new RealSessionManager(sharedPreferences);
  }

}
