package com.aire.ux.spring.test.scenario2;

import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.Route;
import javax.inject.Inject;
import lombok.Getter;

@Route("main")
public class MainView extends Main {

  @Getter
  private final TestService service;

  @Inject
  public MainView(final TestService service) {
    addClassName("main");
    this.service = service;
  }
}
