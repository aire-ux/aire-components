package com.aire.ux.test;

import com.vaadin.flow.di.Instantiator;

public interface InstantiatorFactory {

  default Instantiator create(Instantiator delegate) {
    return delegate;
  }
}
