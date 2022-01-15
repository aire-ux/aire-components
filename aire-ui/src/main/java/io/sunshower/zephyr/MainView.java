package io.sunshower.zephyr;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.ui.Home;

/** The main view contains a button and a click listener. */
@Route("")
@CssImport("./styles/shared-styles.css")
@CssImport("./styles/components/main-layout.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends VerticalLayout {

  public MainView() {
    var button = new Button("go home");
    add(button);
    button.addClassName("hello");
    button.addClickListener(c -> UI.getCurrent().navigate(Test.class));
    button = new Button("sup");
    button.addClickListener(c -> UI.getCurrent().navigate(Home.class));
    add(button);
  }
}
