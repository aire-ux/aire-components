package com.aire.ux.annotations;

import com.aire.ux.core.instantiators.BaseAireInstantiator;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.di.InstantiatorFactory;
import com.vaadin.flow.server.VaadinService;

public class AireAnnotationInstantiatorFactory implements InstantiatorFactory {

  @Override
  public Instantiator createInstantitor(VaadinService service) {
    return new BaseAireInstantiator(service.getInstantiator(), new HostDecorator());
  }
}
