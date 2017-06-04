package com.sendkoin.customer.Login;

import android.util.Log;

import com.sendkoin.api.AuthenticationResponse;
import com.sendkoin.api.FacebookAuthenticationRequest;
import com.sendkoin.customer.Data.Authentication.AuthenticationService;
import com.sendkoin.customer.Data.Authentication.RealSessionManager;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by warefhaque on 5/30/17.
 */

public class LoginPresenter implements LoginContract.Presenter {


  private static final String TAG = "LoginPresenter";
  private LoginContract.View view;
  private AuthenticationService authenticationService;
  private RealSessionManager realSessionManager;
  private Subscription subscription;

  @Inject
  LoginPresenter(LoginContract.View view, AuthenticationService authenticationService, RealSessionManager realSessionManager) {
    this.view = view;
    this.authenticationService = authenticationService;
    this.realSessionManager = realSessionManager;
  }


  @Override
  public void loginWithFacebook(String accessToken) {
    realSessionManager.putFbAccessToken(accessToken);
    Log.d(TAG, "FB Access Token: " + accessToken);
    subscription = authenticationService.authenticateWithFacebook(
        new FacebookAuthenticationRequest.Builder()
            .access_token(accessToken)
            .build())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Observer<AuthenticationResponse>() {
          @Override
          public void onCompleted() {
//            Log.d(TAG, "Facebook Login Complete!");
          }

          @Override
          public void onError(Throwable e) {
            view.loginWithFbFailure(e.getMessage());
          }

          @Override
          public void onNext(AuthenticationResponse authenticationResponse) {
            realSessionManager.putSessionToken(authenticationResponse.session_token);
            Log.d(TAG, "Authenticated: " + authenticationResponse.customer.full_name + " through facebook!");
            Log.d(TAG, "Session Token: " + authenticationResponse.session_token);
            view.loginWithFbSuccess(formatName(authenticationResponse.customer.full_name));
          }
        });
  }

  @Override
  public void unsubscribe() {
    if (subscription != null) {
      subscription.unsubscribe();
    }
  }


  public String formatName(String name) {
    return name.split(" ")[0];
  }
}
