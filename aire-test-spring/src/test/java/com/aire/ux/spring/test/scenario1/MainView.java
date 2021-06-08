package com.aire.ux.spring.test.scenario1;

import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.Route;
import javax.inject.Inject;
import lombok.Getter;

@Route("main")
public class MainView extends Main {

  @Inject
  @Getter
  private TestService service;

  public MainView() {
    addClassName("main");
  }
}
