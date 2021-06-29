package com.aire.ux.theme.decorators.scenario1;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class MainView extends VerticalLayout {

  public MainView() {
    add(new TestButton());
  }
}
