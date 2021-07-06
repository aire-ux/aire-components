package com.aire.ux.control.scenario1;

import com.aire.ux.control.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("main")
public class MainView extends VerticalLayout {

  public MainView() {
    var button = new Button();
    button.setText("Button:setText");
    button.setClassName("setter");
    add(button);

    button = new Button("Button::constructorText");
    button.setClassName("ctor");
    add(button);
  }
}
