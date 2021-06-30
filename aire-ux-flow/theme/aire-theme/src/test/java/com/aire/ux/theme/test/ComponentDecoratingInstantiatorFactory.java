package com.aire.ux.theme.test;

import com.aire.ux.core.instantiators.BaseAireInstantiator;
import com.aire.ux.test.spring.InstantiatorFactory;
import com.vaadin.flow.di.Instantiator;

public class ComponentDecoratingInstantiatorFactory implements InstantiatorFactory {

  @Override
  public Instantiator create(Instantiator delegate) {
    return new BaseAireInstantiator(delegate);
  }
}