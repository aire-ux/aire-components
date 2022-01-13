package com.aire.ux.annotations.scenario1;

import com.aire.ux.Host;
import com.aire.ux.Slot;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;

@Route("front-page")
@Host("front-page")
@SpringComponent
public class FrontPage extends VerticalLayout {


  @Slot("main")
  void mainContent(Component component) {
    this.add(component);
  }

}
