package com.aire.ux.core.test;

import com.aire.ux.core.decorators.ServiceLoaderComponentDecorator;
import com.aire.ux.core.instantiators.BaseAireInstantiator;
import com.aire.ux.test.InstantiatorFactory;
import com.vaadin.flow.di.Instantiator;

public class ComponentDecoratingInstantiatorFactory implements InstantiatorFactory {

  @Override
  public Instantiator create(Instantiator delegate) {
    return new BaseAireInstantiator(delegate,
        new ServiceLoaderComponentDecorator(() -> Thread.currentThread()
            .getContextClassLoader()));
  }
}
