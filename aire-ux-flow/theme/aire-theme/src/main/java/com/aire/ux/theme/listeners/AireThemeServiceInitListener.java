package com.aire.ux.theme.listeners;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

public class AireThemeServiceInitListener implements VaadinServiceInitListener {

  @Override
  public void serviceInit(ServiceInitEvent event) {
    event.addBootstrapListener(new ThemeBootstrapListener());
  }
}
