package com.aire.ux.theme.test;

import com.aire.ux.test.InstantiatorFactory;
import com.aire.ux.theme.AireThemeInstantiator;
import com.vaadin.flow.di.Instantiator;

public class ComponentDecoratingInstantiatorFactory implements InstantiatorFactory {

  @Override
  public Instantiator create(Instantiator delegate) {
    return new AireThemeInstantiator(delegate);
  }
}
