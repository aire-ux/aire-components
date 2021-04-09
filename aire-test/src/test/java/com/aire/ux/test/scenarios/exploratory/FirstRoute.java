package com.aire.ux.test.scenarios.exploratory;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.router.Route;

@Route("firstRoute")
public class FirstRoute extends Section {
  public FirstRoute() {
    add(new Button("hello"));
  }
}
