package com.aire.ux.test.vaadin.scenarios.routes;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import lombok.val;

@Route("secondary")
public class SecondaryLayout extends Div {

  public SecondaryLayout() {
    val button = new Button("waddup");
    button.setClassName("aire-button");
    add(button);
  }
}
