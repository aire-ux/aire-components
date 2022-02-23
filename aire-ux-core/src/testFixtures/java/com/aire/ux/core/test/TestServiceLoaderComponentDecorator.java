package com.aire.ux.core.test;

import com.aire.ux.core.decorators.ServiceLoaderComponentDecorator;

public class TestServiceLoaderComponentDecorator extends ServiceLoaderComponentDecorator {

  public TestServiceLoaderComponentDecorator() {
    super(() -> Thread.currentThread().getContextClassLoader());
  }
}
