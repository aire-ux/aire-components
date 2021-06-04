package com.aire.ux.test.vaadin.scenarios.routes;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@Route("main")
public class MainLayout extends Section {

  public MainLayout() {
    add(
        new Span("hello"),
        new Span("World"),
        new Checkbox("Click me, bub!")
    );
  }
}
