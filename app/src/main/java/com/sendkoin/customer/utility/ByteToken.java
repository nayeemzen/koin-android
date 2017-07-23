package com.sendkoin.customer.utility;

import java.security.SecureRandom;

import okio.ByteString;

/**
 * Created by warefhaque on 5/31/17.
 */

public class ByteToken {
  public static final SecureRandom secureRandom = new SecureRandom();
  private static final int DEFAULT_SIZE = 20;
  private final ByteString token;

  /**
   * Generates a cryptographically random ByteString.
   * @param size The number of bytes to be generated.
   */
  public ByteToken(int size) {
    byte[] bytes = new byte[size];
    secureRandom.nextBytes(bytes);
    token = ByteString.of(bytes);
  }

  /**
   * @return Hex representation of the byte token as a string.
   */
  public String asHex() {
    return token.hex();
  }

  public static String generate() {
    return new ByteToken(DEFAULT_SIZE).asHex();
  }
}
