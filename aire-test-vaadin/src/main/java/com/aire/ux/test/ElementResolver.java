package com.aire.ux.test;

import com.aire.ux.test.Context.Mode;

public interface ElementResolver {

  default Mode getMode() {
    return Mode.None;
  }


  <T> T resolve();
}
