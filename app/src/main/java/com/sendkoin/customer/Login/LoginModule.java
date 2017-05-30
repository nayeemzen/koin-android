package com.sendkoin.customer.Login;

import com.sendkoin.customer.Data.Authentication.AuthenticationService;
import com.sendkoin.customer.Data.Authentication.RealSessionManager;
import com.sendkoin.customer.Data.Dagger.CustomScope;
import com.sendkoin.customer.Payment.MainPaymentPresenter;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by warefhaque on 5/30/17.
 */

@Module
public class LoginModule {

  private LoginContract.View view;

  public LoginModule(LoginContract.View view) {
    this.view = view;
  }

  @Provides
  @CustomScope
  AuthenticationService provideAuthenticationService(Retrofit retrofit) {
    return retrofit.create(AuthenticationService.class);
  }

  @Provides
  @CustomScope
  LoginContract.View providesMainPaymentView() {
    return view;
  }

  @Provides
  @CustomScope
  LoginPresenter providesMainPaymentPresenter(LoginContract.View view,
                                              AuthenticationService authenticationService,
                                              RealSessionManager realSessionManager) {
    return new LoginPresenter(view, authenticationService, realSessionManager);
  }

}
