package com.sendkoin.customer.login;

import com.sendkoin.customer.data.authentication.AuthenticationService;
import com.sendkoin.customer.data.authentication.RealSessionManager;
import com.sendkoin.customer.data.dagger.CustomScope;

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
