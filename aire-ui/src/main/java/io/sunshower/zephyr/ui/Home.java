package io.sunshower.zephyr.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

@Route("home")
public class Home extends Div {

  public Home() {
    add(new Button("Home"));
  }
}
