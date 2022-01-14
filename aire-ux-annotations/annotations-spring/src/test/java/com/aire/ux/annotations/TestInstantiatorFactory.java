package com.aire.ux.annotations;

import com.aire.ux.core.decorators.ServiceLoaderComponentDecorator;
import com.aire.ux.core.instantiators.BaseAireInstantiator;
import com.aire.ux.test.InstantiatorFactory;
import com.vaadin.flow.di.Instantiator;

public class TestInstantiatorFactory implements InstantiatorFactory {

  public Instantiator create(Instantiator delegate) {
    return new BaseAireInstantiator(
        delegate,
        new ServiceLoaderComponentDecorator(() -> Thread.currentThread().getContextClassLoader()));
  }
}
