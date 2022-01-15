package io.sunshower.zephyr;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.router.Route;

@Route("sup")
public class Test extends Section {
  public Test() {
    add(new Button("Test!"));
  }
}
