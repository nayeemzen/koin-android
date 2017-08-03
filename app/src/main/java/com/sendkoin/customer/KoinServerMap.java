package com.sendkoin.customer;

import java.util.LinkedHashMap;
import java.util.Map;

public class KoinServerMap {
  public static Map<KoinServer, String> getServerMap() {
    Map<KoinServer, String> myMap = new LinkedHashMap<>();
    myMap.put(KoinServer.DEVELOPMENT, "http://192.168.2.12:8080/api/v1/");
    myMap.put(KoinServer.PRODUCTION, "http://custom-env-1.2tfxydg93p.us-west-2.elasticbeanstalk.com/api/v1/");
    return myMap;
  }

  public enum KoinServer {
    DEVELOPMENT,
    PRODUCTION
  }
}
