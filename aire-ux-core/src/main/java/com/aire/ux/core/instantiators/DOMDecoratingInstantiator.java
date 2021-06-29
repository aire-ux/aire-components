package com.aire.ux.core.instantiators;

import com.vaadin.flow.di.DefaultInstantiator;
import com.vaadin.flow.server.VaadinService;

public class DOMDecoratingInstantiator extends DefaultInstantiator {

  /**
   * Creates a new instantiator for the given service.
   *
   * @param service the service to use
   */
  public DOMDecoratingInstantiator(VaadinService service) {
    super(service);
  }
}
