package com.aire.ux.test;

import com.aire.ux.test.Context.Mode;

public interface ElementResolver {

  Mode getMode();

  default <T> T mock(T value) {
    return value;
  }

  default <T> T spy(T value) {
    return value;
  }

  <T> T resolve();
}
