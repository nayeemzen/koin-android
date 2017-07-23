package com.sendkoin.customer.data.authentication;

import android.content.SharedPreferences;

import javax.inject.Inject;

/**
 * Created by warefhaque on 5/30/17.
 */

public class RealSessionManager implements SessionManager {


  public static final String SESSION_TOKEN_KEY = "session_token";
  public static final String FACEBOOK_ACCESS_TOKEN = "fb_access_token";
  public static final String AUTHORIZATION_ATTEMPTS = "authorization_attempts";
  public static final int MAX_AUTHORIZATION_ATTEMPTS = 3;
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

  @Override
  public void putFbAccessToken(String fbAccessToken) {
    sharedPreferences.edit().putString(FACEBOOK_ACCESS_TOKEN, fbAccessToken).apply();
  }

  @Override
  public String getFbAccessToken() {
    return sharedPreferences.getString(FACEBOOK_ACCESS_TOKEN, null);
  }

  @Override
  public int getAuthAttempts() {
    return sharedPreferences.getInt(AUTHORIZATION_ATTEMPTS, 0);
  }

  @Override
  public void putAuthAttempts(int numAttempts) {
    sharedPreferences.edit().putInt(AUTHORIZATION_ATTEMPTS, numAttempts).apply();
  }
}
