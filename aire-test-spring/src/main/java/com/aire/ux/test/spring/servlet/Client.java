package com.aire.ux.test.spring.servlet;

public interface Client {

  String get(String path);

  void post(String path, String body);
}
