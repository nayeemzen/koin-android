package com.sendkoin.customer.data.authentication;

import com.sendkoin.api.AuthenticationResponse;
import com.sendkoin.api.FacebookAuthenticationRequest;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by warefhaque on 5/30/17.
 */

public interface AuthenticationService {
  @POST("customer/auth/facebook")
  Observable<AuthenticationResponse> authenticateWithFacebook(
      @Body FacebookAuthenticationRequest facebookAuthenticationRequest);
}


