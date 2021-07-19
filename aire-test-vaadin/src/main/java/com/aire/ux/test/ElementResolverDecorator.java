package com.aire.ux.test;

import com.aire.ux.test.Context.Mode;

public interface ElementResolverDecorator {

  <T> T decorate(Mode mode, T value);

}
