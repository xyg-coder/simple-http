package com.xygcoder.simplehttpexample;

import com.xygcoder.opensource.SimpleHttpServer;

public class TestServer {
  public static void main(String[] args) throws Exception {
    SimpleHttpServer simpleHttpServer = new SimpleHttpServer();
    simpleHttpServer.run(TestServer.class);
  }
}
