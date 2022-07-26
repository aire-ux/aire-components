package com.aire.ux.test;

import com.aire.ux.core.instantiators.BaseAireInstantiator;
import com.aire.ux.ext.ExtensionComponentDecorator;
import com.aire.ux.ext.ExtensionRegistry;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.di.InstantiatorFactory;
import com.vaadin.flow.server.VaadinService;
import lombok.val;

public class TestExtensionComponentInstantiatorFactory implements InstantiatorFactory {

  @Override
  public Instantiator createInstantitor(VaadinService service) {
    val instantiator = service.getInstantiator();
    return new BaseAireInstantiator(
        instantiator,
        new ExtensionComponentDecorator(instantiator.getOrCreate(ExtensionRegistry.class)));
  }
}
