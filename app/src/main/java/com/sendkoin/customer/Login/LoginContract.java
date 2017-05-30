package com.sendkoin.customer.Login;

/**
 * Created by warefhaque on 5/30/17.
 */

public interface LoginContract {
   interface View{
     void loginWithFbFailure(String errorMessage);
     void loginWithFbSuccess(String name);
  }

   interface Presenter{
     void loginWithFacebook(String accessToken);
     void unsubscribe();
  }
}
