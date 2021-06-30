package com.aire.ux.core.instantiators.scenario1;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class MainView extends VerticalLayout {

  public MainView() {
    System.out.println("Hello");
  }
}
