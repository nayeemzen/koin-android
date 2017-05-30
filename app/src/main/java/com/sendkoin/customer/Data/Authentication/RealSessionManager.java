package com.sendkoin.customer.Data.Authentication;

import android.content.SharedPreferences;

import javax.inject.Inject;

/**
 * Created by warefhaque on 5/30/17.
 */

public class RealSessionManager implements SessionManager {

  public static final String SESSION_TOKEN_KEY = "session_token";
  private SharedPreferences sharedPreferences;

  @Inject
  public RealSessionManager(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  @Override
  public String getSessionToken() {
    return sharedPreferences.getString(SESSION_TOKEN_KEY, null);
  }

  @Override
  public void putSessionToken(String sessionToken) {
    sharedPreferences.edit().putString(SESSION_TOKEN_KEY, sessionToken).apply();
  }
}
