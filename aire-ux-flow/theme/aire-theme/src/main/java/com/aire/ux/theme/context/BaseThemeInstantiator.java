package com.aire.ux.theme.context;

import com.vaadin.flow.di.DefaultInstantiator;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.server.VaadinService;

public class BaseThemeInstantiator extends DefaultInstantiator implements Instantiator {

  /**
   * Creates a new instantiator for the given service.
   * @param service the service to use
   */
  public BaseThemeInstantiator(VaadinService service) {
    super(service);
  }



}
