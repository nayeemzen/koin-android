package com.sendkoin.customer.data.authentication;

/**
 * Created by warefhaque on 5/30/17.
 */

public interface SessionManager {
  String getSessionToken();
  void putSessionToken(String sessionToken);
  void putFbAccessToken(String fbAccessToken);
  String getFbAccessToken();
  int getAuthAttempts();
  void putAuthAttempts(int numAttempts);
}
